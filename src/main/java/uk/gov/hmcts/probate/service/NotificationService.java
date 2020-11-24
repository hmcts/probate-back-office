package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.config.notifications.EmailAddresses;
import uk.gov.hmcts.probate.config.notifications.NotificationTemplates;
import uk.gov.hmcts.probate.config.properties.registries.RegistriesProperties;
import uk.gov.hmcts.probate.config.properties.registries.Registry;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.exception.InvalidEmailException;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.CaseOrigin;
import uk.gov.hmcts.probate.model.Constants;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ExecutorsApplyingNotification;
import uk.gov.hmcts.probate.model.LanguagePreference;
import uk.gov.hmcts.probate.model.SentEmail;
import uk.gov.hmcts.probate.model.State;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.service.client.DocumentStoreClient;
import uk.gov.hmcts.probate.service.notification.CaveatPersonalisationService;
import uk.gov.hmcts.probate.service.notification.GrantOfRepresentationPersonalisationService;
import uk.gov.hmcts.probate.service.notification.SentEmailPersonalisationService;
import uk.gov.hmcts.probate.service.notification.TemplateService;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.probate.validator.EmailAddressNotificationValidationRule;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.probate.model.cases.RegistryLocation;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.SendEmailResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.validation.Valid;

import static uk.gov.hmcts.probate.model.Constants.BUSINESS_ERROR;
import static uk.gov.hmcts.probate.model.DocumentType.SENT_EMAIL;
import static uk.gov.hmcts.probate.model.State.GRANT_REISSUED;
import static uk.gov.service.notify.NotificationClient.prepareUpload;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationService {

    @Autowired
    private final EmailAddresses emailAddresses;

    @Autowired
    private BusinessValidationMessageService businessValidationMessageService;
    private final NotificationTemplates notificationTemplates;
    private final RegistriesProperties registriesProperties;
    private final NotificationClient notificationClient;
    private final MarkdownTransformationService markdownTransformationService;
    private final PDFManagementService pdfManagementService;
    private final EventValidationService eventValidationService;
    private final List<EmailAddressNotificationValidationRule> emailAddressNotificationValidationRules;
    private final GrantOfRepresentationPersonalisationService grantOfRepresentationPersonalisationService;
    private final CaveatPersonalisationService caveatPersonalisationService;
    private final SentEmailPersonalisationService sentEmailPersonalisationService;
    private final TemplateService templateService;
    private final AuthTokenGenerator serviceAuthTokenGenerator;
    private final DocumentStoreClient documentStoreClient;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM Y HH:mm");
    private static final DateTimeFormatter EXCELA_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");

    private static final String PERSONALISATION_APPLICANT_NAME = "applicant_name";
    private static final String PERSONALISATION_SOT_LINK = "sot_link";

    @Value("${notifications.grantDelayedNotificationPeriodDays}")
    private Long grantDelayedNotificationPeriodDays;

    @Value("${notifications.grantAwaitingDocumentationNotificationPeriodDays}")
    private Long grantAwaitingDocumentationNotificationPeriodDays;

    private static final DateTimeFormatter RELEASE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    @Value("${notifications.grantDelayedNotificationReleaseDate}")
    private String grantDelayedNotificationReleaseDate;

    public Document sendEmail(State state, CaseDetails caseDetails)
        throws NotificationClientException {
        return sendEmail(state, caseDetails, Optional.empty());
    }
    
    public Document sendEmail(State state, CaseDetails caseDetails, Optional<CaseOrigin> caseOriginOptional)
        throws NotificationClientException {

        CaseData caseData = caseDetails.getData();
        log.info("sendEmail for case: {}", caseDetails.getId());
        Registry registry = getRegistry(caseData.getRegistryLocation(), caseData.getLanguagePreference());
        log.info("template params, state={}, applicationType()={}, regLocation={}, language={}, paperForm={}, for case: {}, origin: {}", 
            state, caseData.getApplicationType(), caseData.getRegistryLocation(), caseData.getLanguagePreference(), caseData.getPaperForm(), caseDetails.getId(), caseOriginOptional.isEmpty() ? "none" : caseOriginOptional.get());
        String templateId = templateService.getTemplateId(state, caseData.getApplicationType(),
            caseData.getRegistryLocation(), caseData.getLanguagePreference(), caseData.getPaperForm(), caseOriginOptional.orElse(null));
        log.info("Got templateId: {}", templateId);
        String emailReplyToId = registry.getEmailReplyToId();
        String emailAddress = getEmail(caseData);
        Map<String, Object> personalisation = grantOfRepresentationPersonalisationService.getPersonalisation(caseDetails,
            registry);
        String reference = caseData.getSolsSolicitorAppReference();

        if (state == state.CASE_STOPPED_CAVEAT) {
            personalisation = caveatPersonalisationService.getCaveatStopPersonalisation(personalisation, caseData);
        }

        if (caseData.getApplicationType().equals(ApplicationType.SOLICITOR) && !StringUtils.isEmpty(caseData.getSolsSOTName())) {
            personalisation.replace(PERSONALISATION_APPLICANT_NAME, caseData.getSolsSOTName());
        }
        log.info("Personlisation complete now get the email repsonse");
        SendEmailResponse response =
            getSendEmailResponse(state, templateId, emailReplyToId, emailAddress, personalisation, reference);

        return getSentEmailDocument(state, emailAddress, response);
    }


    public Document sendEmail(State state, CaseDetails caseDetails, ExecutorsApplyingNotification executor)
        throws NotificationClientException {
        CaseData caseData = caseDetails.getData();
        Registry registry = registriesProperties.getRegistries().get(caseData.getRegistryLocation().toLowerCase());

        String templateId = templateService.getTemplateId(state, caseData.getApplicationType()
            , caseData.getRegistryLocation(), caseData.getLanguagePreference());
        String emailAddress = executor.getEmail();
        Map<String, Object> personalisation = grantOfRepresentationPersonalisationService.getPersonalisation(caseDetails,
            registry);
        String reference = caseData.getSolsSolicitorAppReference();
        String emailReplyToId = registry.getEmailReplyToId();

        personalisation.replace(PERSONALISATION_APPLICANT_NAME, executor.getName());

        SendEmailResponse response =
            getSendEmailResponse(state, templateId, emailReplyToId, emailAddress, personalisation, reference);

        return getSentEmailDocument(state, emailAddress, response);
    }

    public Document sendCaveatEmail(State state, CaveatDetails caveatDetails)
        throws NotificationClientException {

        CaveatData caveatData = caveatDetails.getData();
        Registry registry = registriesProperties.getRegistries().get(caveatData.getRegistryLocation().toLowerCase());

        String templateId = templateService.getTemplateId(state, caveatData.getApplicationType()
            , caveatData.getRegistryLocation(), caveatData.getLanguagePreference());
        String emailAddress = caveatData.getCaveatorEmailAddress();
        Map<String, String> personalisation;

        if (caveatData.getSolsSolicitorAppReference() != null) {
            personalisation = caveatPersonalisationService.getSolsCaveatPersonalisation(caveatDetails, registry);
        } else {
            personalisation = caveatPersonalisationService.getCaveatPersonalisation(caveatDetails, registry);
        }

        String reference = caveatDetails.getId().toString();

        SendEmailResponse response;

        response = notificationClient.sendEmail(templateId, emailAddress, personalisation, reference);
        log.info("Sent email with template {} for case ", templateId, caveatDetails.getId());

        DocumentType documentType;
        switch (state) {
            case GENERAL_CAVEAT_MESSAGE:
            case CAVEAT_RAISED:
            case CAVEAT_RAISED_SOLS:
            case CAVEAT_EXTEND:
            case CAVEAT_WITHDRAW:
                documentType = SENT_EMAIL;
                break;
            default:
                throw new BadRequestException("Unsupported State");
        }

        return getGeneratedSentEmailDocument(response, emailAddress, documentType);
    }

    public Document sendExcelaEmail(List<ReturnedCaseDetails> caseDetails) throws
        NotificationClientException {
        String templateId = notificationTemplates.getEmail().get(LanguagePreference.ENGLISH).get(caseDetails.get(0).getData().getApplicationType())
            .getExcelaData();
        Map<String, String> personalisation = grantOfRepresentationPersonalisationService.getExcelaPersonalisation(caseDetails);
        String reference = LocalDateTime.now().format(EXCELA_DATE);

        SendEmailResponse response;

        response = notificationClient.sendEmail(templateId, emailAddresses.getExcelaEmail(), personalisation, reference);
        log.info("Excela email reference response: {}", response.getReference());

        return getGeneratedSentEmailDocument(response, emailAddresses.getExcelaEmail(), SENT_EMAIL);
    }

    public Document sendEmailWithDocumentAttached(CaseDetails caseDetails, ExecutorsApplyingNotification executor,
                                                  State state) throws NotificationClientException, IOException {
        String authHeader = serviceAuthTokenGenerator.generate();
        byte[] sotDocument = documentStoreClient.retrieveDocument(caseDetails.getData()
            .getProbateSotDocumentsGenerated()
            .get(caseDetails.getData().getProbateSotDocumentsGenerated().size() - 1).getValue(), authHeader);

        Registry registry = registriesProperties.getRegistries().get(caseDetails.getData().getRegistryLocation().toLowerCase());

        String templateId = templateService.getTemplateId(state, caseDetails.getData().getApplicationType()
            , caseDetails.getData().getRegistryLocation()
            , caseDetails.getData().getLanguagePreference());
        String emailReplyToId = registry.getEmailReplyToId();

        Map<String, Object> personalisation =
            grantOfRepresentationPersonalisationService.getPersonalisation(caseDetails, registry);
        grantOfRepresentationPersonalisationService.addSingleAddressee(personalisation, executor.getName());

        personalisation.put(PERSONALISATION_SOT_LINK, prepareUpload(sotDocument));

        String reference = caseDetails.getData().getSolsSolicitorAppReference();

        SendEmailResponse response =
            getSendEmailResponse(state, templateId, emailReplyToId, executor.getEmail(), personalisation, reference);

        return getSentEmailDocument(state, executor.getEmail(), response);
    }

    public Document generateGrantReissue(CallbackRequest callbackRequest) throws NotificationClientException {
        CaseDetails caseDetails = callbackRequest.getCaseDetails();
        @Valid CaseData caseData = caseDetails.getData();
        CallbackResponse callbackResponse;
        Document sentEmail;
        callbackResponse = eventValidationService.validateEmailRequest(callbackRequest, emailAddressNotificationValidationRules);

        if (callbackResponse.getErrors().isEmpty()) {
            sentEmail = sendEmail(GRANT_REISSUED, caseDetails);
        } else if (caseData.getApplicationType().equals(ApplicationType.SOLICITOR)) {
            throw new InvalidEmailException(businessValidationMessageService.generateError(BUSINESS_ERROR,
                "emailNotProvidedSOLS").getMessage(),
                "Invalid email exception: No email address provided for application type SOLS: " + caseDetails.getId());
        } else {
            throw new InvalidEmailException(businessValidationMessageService.generateError(BUSINESS_ERROR,
                "emailNotProvidedPA").getMessage(),
                "Invalid email exception: No email address provided for application type PA: " + caseDetails.getId());
        }

        return sentEmail;
    }

    public Document sendGrantDelayedEmail(ReturnedCaseDetails caseDetails) throws NotificationClientException {
        String templateId = notificationTemplates.getEmail().get(caseDetails.getData().getLanguagePreference())
            .get(caseDetails.getData().getApplicationType())
            .getGrantDelayed();
        return sendGrantNotificationEmail(caseDetails, templateId);
    }

    public Document sendGrantAwaitingDocumentationEmail(ReturnedCaseDetails caseDetails) throws NotificationClientException {
        String templateId = notificationTemplates.getEmail().get(caseDetails.getData().getLanguagePreference())
            .get(caseDetails.getData().getApplicationType())
            .getGrantAwaitingDocumentation();
        return sendGrantNotificationEmail(caseDetails, templateId);
    }

    private Document sendGrantNotificationEmail(ReturnedCaseDetails caseDetails, String templateId) throws NotificationClientException {
 
        Registry registry = registriesProperties.getRegistries().get(caseDetails.getData().getRegistryLocation().toLowerCase());
        Map<String, Object> personalisation = grantOfRepresentationPersonalisationService.getPersonalisation(caseDetails, registry);
        String reference = caseDetails.getData().getSolsSolicitorAppReference();
        String emailAddress = caseDetails.getData().getPrimaryApplicantEmailAddress();
        SendEmailResponse response = notificationClient.sendEmail(templateId, emailAddress, personalisation, reference);
        log.info("Grant notification email reference response: {}", response.getReference());

        return getGeneratedSentEmailDocument(response, emailAddress, SENT_EMAIL);
    }

    protected Registry getRegistry(String registryLocation, LanguagePreference languagePreference) {
        String defaultRegistryLocation = (languagePreference == null || LanguagePreference.ENGLISH.equals(languagePreference)) ? RegistryLocation.CTSC.getName() : RegistryLocation.CARDIFF.getName();
        return registriesProperties.getRegistries().get((Optional.ofNullable(registryLocation).orElse(defaultRegistryLocation)).toLowerCase());
    }

    private Document getGeneratedSentEmailDocument(SendEmailResponse response, String emailAddress, DocumentType docType) {
        SentEmail sentEmail = SentEmail.builder()
            .sentOn(LocalDateTime.now().format(formatter))
            .from(response.getFromEmail().orElse(""))
            .to(emailAddress)
            .subject(response.getSubject())
            .body(markdownTransformationService.toHtml(response.getBody()))
            .build();

        return pdfManagementService.generateAndUpload(sentEmail, docType);
    }

    public void startGrantDelayNotificationPeriod(CaseDetails caseDetails) {

        CaseData caseData = caseDetails.getData();
        LocalDate grantDelayedNotificationReleaseLocalDate = LocalDate.parse(grantDelayedNotificationReleaseDate, RELEASE_DATE_FORMAT);
        String evidenceHandled = caseData.getEvidenceHandled();
        if (!StringUtils.isEmpty(evidenceHandled)) {
            log.info("Evidence Handled flag {} ", evidenceHandled);
            if (evidenceHandled.equals(Constants.NO)
                && caseData.getGrantDelayedNotificationDate() == null
                && !LocalDate.now().isBefore(grantDelayedNotificationReleaseLocalDate)) {
                log.info("Grant delay notification {} ", caseData.getGrantDelayedNotificationDate());
                caseData.setGrantDelayedNotificationDate(LocalDate.now().plusDays(grantDelayedNotificationPeriodDays));
            } else {
                log.info("Grant delay notification date not set for case: {}", caseDetails.getId());
            }
        }
    }

    public void startAwaitingDocumentationNotificationPeriod(CaseDetails caseDetails) {

        CaseData caseData = caseDetails.getData();
        LocalDate grantDelayedNotificationReleaseLocalDate = LocalDate.parse(grantDelayedNotificationReleaseDate, RELEASE_DATE_FORMAT);
        if (!LocalDate.now().isBefore(grantDelayedNotificationReleaseLocalDate)
            && (caseData.getScannedDocuments() == null || caseData.getScannedDocuments().isEmpty())) {
            LocalDate notificationDate = LocalDate.now().plusDays(grantAwaitingDocumentationNotificationPeriodDays);
            log.info("Setting grantAwaitingDocumentationNotificationDate {} for case {}", notificationDate.toString(), caseDetails.getId());
            caseData.setGrantAwaitingDocumentationNotificationDate(notificationDate);
        }
    }

    public void resetAwaitingDocumentationNotificationDate(CaseDetails caseDetails) {

        CaseData caseData = caseDetails.getData();
        LocalDate grantDelayedNotificationReleaseLocalDate = LocalDate.parse(grantDelayedNotificationReleaseDate, RELEASE_DATE_FORMAT);
        if (!LocalDate.now().isBefore(grantDelayedNotificationReleaseLocalDate)) {
            log.info("Resetting grantAwaitingDocumentationNotificationDate to null for case {}", caseDetails.getId());
            caseData.setGrantAwaitingDocumentationNotificationDate(null);
        }
    }

    private Document getGeneratedSentEmailDocmosisDocument(SendEmailResponse response,
                                                           String emailAddress, DocumentType docType) {
        SentEmail sentEmail = SentEmail.builder()
            .sentOn(LocalDateTime.now().format(formatter))
            .from(response.getFromEmail().orElse(""))
            .to(emailAddress)
            .subject(response.getSubject())
            .body(response.getBody())
            .build();
        Map<String, Object> placeholders = sentEmailPersonalisationService.getPersonalisation(sentEmail);
        return pdfManagementService.generateDocmosisDocumentAndUpload(placeholders, docType);
    }

    private Document getSentEmailDocument(State state, String emailAddress, SendEmailResponse response) {
        if (state == State.CASE_STOPPED_REQUEST_INFORMATION) {
            return getGeneratedSentEmailDocmosisDocument(response, emailAddress, SENT_EMAIL);
        } else {
            return getGeneratedSentEmailDocument(response, emailAddress, SENT_EMAIL);
        }
    }

    private SendEmailResponse getSendEmailResponse(State state, String templateId, String emailReplyToId,
                                                   String emailAddress, Map<String, Object> personalisation,
                                                   String reference)
        throws NotificationClientException {
        SendEmailResponse response;
        switch (state) {
            case CASE_STOPPED:
            case CASE_STOPPED_CAVEAT:
                response = notificationClient.sendEmail(templateId, emailAddress, personalisation, reference, emailReplyToId);
                break;
            case CASE_STOPPED_REQUEST_INFORMATION:
            case REDECLARATION_SOT:
            default:
                response = notificationClient.sendEmail(templateId, emailAddress, personalisation, reference);
        }
        log.info("Return the SendEmailResponse");
        return response;
    }

    private String getEmail(CaseData caseData) {
        switch (caseData.getApplicationType()) {
            case SOLICITOR:
                return caseData.getSolsSolicitorEmail().toLowerCase();
            case PERSONAL:
                return caseData.getPrimaryApplicantEmailAddress().toLowerCase();
            default:
                throw new BadRequestException("Unsupported application type");
        }
    }

}
