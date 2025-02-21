package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.service.documentmanagement.DocumentManagementService;
import uk.gov.hmcts.probate.service.notification.CaveatPersonalisationService;
import uk.gov.hmcts.probate.service.notification.DisposalReminderPersonalisationService;
import uk.gov.hmcts.probate.service.notification.GrantOfRepresentationPersonalisationService;
import uk.gov.hmcts.probate.service.notification.SentEmailPersonalisationService;
import uk.gov.hmcts.probate.service.notification.SmeeAndFordPersonalisationService;
import uk.gov.hmcts.probate.service.notification.TemplateService;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.probate.service.user.UserInfoService;
import uk.gov.hmcts.probate.validator.EmailAddressNotifyValidationRule;
import uk.gov.hmcts.probate.validator.PersonalisationValidationRule;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.probate.model.cases.RegistryLocation;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.SendEmailResponse;

import jakarta.validation.Valid;
import uk.gov.service.notify.TemplatePreview;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static uk.gov.hmcts.probate.model.Constants.BUSINESS_ERROR;
import static uk.gov.hmcts.probate.model.Constants.CAVEAT_SOLICITOR_NAME;
import static uk.gov.hmcts.probate.model.DocumentType.SENT_EMAIL;
import static uk.gov.hmcts.probate.model.State.CASE_STOPPED_REQUEST_INFORMATION;
import static uk.gov.hmcts.probate.model.State.GRANT_REISSUED;
import static uk.gov.hmcts.probate.model.StateConstants.STATE_CASE_PAYMENT_FAILED;
import static uk.gov.hmcts.probate.model.StateConstants.STATE_PENDING;
import static uk.gov.service.notify.NotificationClient.prepareUpload;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationService {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM Y HH:mm");
    private static final DateTimeFormatter EXELA_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final String PERSONALISATION_APPLICANT_NAME = "applicant_name";
    private static final String PERSONALISATION_SOT_LINK = "sot_link";
    private static final DateTimeFormatter RELEASE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String INVALID_PERSONALISATION_ERROR_MESSAGE =
            "Markdown Link detected in case data, stop sending notification email.";
    private static final List<String> PA_DRAFT_STATE_LIST = List.of(STATE_PENDING, STATE_CASE_PAYMENT_FAILED);

    private final EmailAddresses emailAddresses;
    private final NotificationTemplates notificationTemplates;
    private final RegistriesProperties registriesProperties;
    private final NotificationClient notificationClient;
    private final MarkdownTransformationService markdownTransformationService;
    private final PDFManagementService pdfManagementService;
    private final EventValidationService eventValidationService;
    private final List<EmailAddressNotifyValidationRule> emailAddressNotifyValidationRules;
    private final GrantOfRepresentationPersonalisationService grantOfRepresentationPersonalisationService;
    private final SmeeAndFordPersonalisationService smeeAndFordPersonalisationService;
    private final CaveatPersonalisationService caveatPersonalisationService;
    private final SentEmailPersonalisationService sentEmailPersonalisationService;
    private final TemplateService templateService;
    private final AuthTokenGenerator serviceAuthTokenGenerator;
    private final NotificationClientService notificationClientService;
    private final DocumentManagementService documentManagementService;
    private final PersonalisationValidationRule personalisationValidationRule;
    private final BusinessValidationMessageService businessValidationMessageService;
    private final DisposalReminderPersonalisationService disposalReminderPersonalisationService;
    private final UserInfoService userInfoService;


    @Value("${notifications.grantDelayedNotificationPeriodDays}")
    private Long grantDelayedNotificationPeriodDays;
    @Value("${notifications.grantAwaitingDocumentationNotificationPeriodDays}")
    private Long grantAwaitingDocumentationNotificationPeriodDays;
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
        log.info(
            "template params, state={}, applicationType()={}, regLocation={}, language={}, for case: "
                + "{}, origin: {}, channelChoice: {}, informationNeededByPost: {}",
            state, caseData.getApplicationType(), caseData.getRegistryLocation(), caseData.getLanguagePreference(),
                caseDetails.getId(), caseOriginOptional.isEmpty() ? "none" : caseOriginOptional.get(),
                caseData.getChannelChoice(), caseData.getInformationNeededByPost());
        String templateId = templateService.getTemplateId(state, caseData.getApplicationType(),
            caseData.getRegistryLocation(), caseData.getLanguagePreference(),
            caseOriginOptional.orElse(null), caseData.getChannelChoice(), caseData.getInformationNeededByPost());
        log.info("Got templateId: {}", templateId);
        Map<String, Object> personalisation =
            grantOfRepresentationPersonalisationService.getPersonalisation(caseDetails,
                registry);

        if (state == state.CASE_STOPPED_CAVEAT) {
            personalisation = caveatPersonalisationService.getCaveatStopPersonalisation(personalisation, caseData);
        }

        if (caseData.getApplicationType().equals(ApplicationType.SOLICITOR)) {
            if (!StringUtils.isEmpty(caseData.getSolsSOTName())) {
                personalisation.replace(PERSONALISATION_APPLICANT_NAME, caseData.getSolsSOTName());
            } else if (!StringUtils.isEmpty(caseData.getSolsSOTForenames()) && !StringUtils
                    .isEmpty(caseData.getSolsSOTSurname())) {
                personalisation.replace(PERSONALISATION_APPLICANT_NAME,
                        String.join(" ", caseData.getSolsSOTForenames(), caseData.getSolsSOTSurname()));
            }
        }

        String emailReplyToId = registry.getEmailReplyToId();
        String emailAddress = getEmail(caseData);
        String reference = caseData.getSolsSolicitorAppReference();

        doCommonNotificationServiceHandling(personalisation, caseDetails.getId());

        log.info("Personlisation complete now get the email repsonse");
        SendEmailResponse response =
            getSendEmailResponse(state, templateId, emailReplyToId, emailAddress, personalisation, reference,
                caseDetails.getId());

        return getSentEmailDocument(state, emailAddress, response);
    }

    public Document emailPreview(CaseDetails caseDetails) throws NotificationClientException {
        CaseData caseData = caseDetails.getData();
        Registry registry = registriesProperties.getRegistries().get(caseData.getRegistryLocation().toLowerCase());

        String templateId = templateService.getTemplateId(CASE_STOPPED_REQUEST_INFORMATION,
            caseData.getApplicationType(), caseData.getRegistryLocation(), caseData.getLanguagePreference(),
                null, caseData.getChannelChoice(), caseData.getInformationNeededByPost());
        Map<String, Object> personalisation =
                grantOfRepresentationPersonalisationService.getPersonalisation(caseDetails,
                        registry);

        doCommonNotificationServiceHandling(personalisation, caseDetails.getId());

        TemplatePreview previewResponse =
                notificationClientService.emailPreview(caseDetails.getId(), templateId, personalisation);
        return getGeneratedDocument(previewResponse, getEmail(caseData), SENT_EMAIL);
    }

    public Document sendSealedAndCertifiedEmail(CaseDetails caseDetails) throws NotificationClientException {
        CaseData caseData = caseDetails.getData();
        String reference = caseDetails.getId().toString();
        String deceasedName = caseData.getDeceasedFullName();

        String templateId = notificationTemplates.getEmail().get(LanguagePreference.ENGLISH)
                .get(caseData.getApplicationType()).getSealedAndCertified();
        Map<String, Object> personalisation =
                grantOfRepresentationPersonalisationService.getSealedAndCertifiedPersonalisation(caseDetails.getId(),
                         deceasedName);
        doCommonNotificationServiceHandling(personalisation, caseDetails.getId());

        log.info("Sealed And Certified get the email response for case {}", caseDetails.getId());

        SendEmailResponse response = notificationClientService.sendEmail(templateId,
                emailAddresses.getSealedAndCertifiedEmail(), personalisation, reference);

        log.info("Send Sealed And Certified completed for case {}", caseDetails.getId());
        return getGeneratedSentEmailDocument(response, emailAddresses.getSealedAndCertifiedEmail(), SENT_EMAIL);
    }

    public Document sendNocEmail(State state, CaseDetails caseDetails) throws NotificationClientException {
        CaseData caseData = caseDetails.getData();
        Registry registry = registriesProperties.getRegistries().get(caseData.getRegistryLocation().toLowerCase());
        String emailAddress = caseData.getRemovedRepresentative() != null
                ? caseData.getRemovedRepresentative().getSolicitorEmail() : null;
        String solicitorName = removedSolicitorNameForPersonalisation(caseData);
        String reference = caseData.getSolsSolicitorAppReference();
        String deceasedName = caseData.getDeceasedFullName();

        String templateId = templateService.getTemplateId(state, caseData.getApplicationType(),
                caseData.getRegistryLocation(), caseData.getLanguagePreference());
        Map<String, Object> personalisation =
                grantOfRepresentationPersonalisationService.getNocPersonalisation(caseDetails.getId(),
                        solicitorName, deceasedName);
        String emailReplyToId = registry.getEmailReplyToId();

        doCommonNotificationServiceHandling(personalisation, caseDetails.getId());

        log.info("Personlisation complete now get the email response");

        SendEmailResponse response =
                getSendEmailResponse(state, templateId, emailReplyToId, emailAddress, personalisation, reference,
                        caseDetails.getId());

        return getSentEmailDocument(state, emailAddress, response);
    }

    public Document sendCaveatNocEmail(State state, CaveatDetails caveatDetails) throws
            NotificationClientException {
        CaveatData caveatData = caveatDetails.getData();
        Registry registry = registriesProperties.getRegistries().get(caveatData.getRegistryLocation().toLowerCase());
        String emailAddress = caveatData.getRemovedRepresentative() != null
                ? caveatData.getRemovedRepresentative().getSolicitorEmail() : null;
        String deceasedName = caveatData.getDeceasedFullName();

        String templateId = templateService.getTemplateId(state, caveatData.getApplicationType(),
                caveatData.getRegistryLocation(), caveatData.getLanguagePreference());
        Map<String, Object> personalisation =
                grantOfRepresentationPersonalisationService.getNocPersonalisation(caveatDetails.getId(),
                        CAVEAT_SOLICITOR_NAME, deceasedName);
        String emailReplyToId = registry.getEmailReplyToId();
        String reference = caveatData.getSolsSolicitorAppReference();

        doCommonNotificationServiceHandling(personalisation, caveatDetails.getId());

        log.info("Personlisation complete now get the email response");

        SendEmailResponse response =
                getSendEmailResponse(state, templateId, emailReplyToId, emailAddress, personalisation, reference,
                        caveatDetails.getId());

        return getSentEmailDocument(state, emailAddress, response);
    }

    public Document sendCaveatEmail(State state, CaveatDetails caveatDetails)
        throws NotificationClientException {

        CaveatData caveatData = caveatDetails.getData();
        Registry registry = registriesProperties.getRegistries().get(caveatData.getRegistryLocation().toLowerCase());

        String templateId = templateService.getTemplateId(state, caveatData.getApplicationType(),
            caveatData.getRegistryLocation(), caveatData.getLanguagePreference());
        String emailAddress = caveatData.getCaveatorEmailAddress();
        Map<String, String> personalisation;

        if (caveatData.getSolsSolicitorAppReference() != null) {
            personalisation = caveatPersonalisationService.getSolsCaveatPersonalisation(caveatDetails, registry);
        } else {
            personalisation = caveatPersonalisationService.getCaveatPersonalisation(caveatDetails, registry);
        }

        String reference = caveatDetails.getId().toString();

        doCommonNotificationServiceHandling(personalisation, caveatDetails.getId());

        SendEmailResponse response = notificationClientService.sendEmail(
                caveatDetails.getId(), templateId, emailAddress, personalisation, reference);
        log.info("Sent email with template {} for caveat number {}", templateId, caveatDetails.getId());

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

    public Document sendExelaEmail(List<ReturnedCaseDetails> caseDetails) throws
        NotificationClientException {
        String templateId = notificationTemplates.getEmail().get(LanguagePreference.ENGLISH)
            .get(caseDetails.get(0).getData().getApplicationType())
            .getExelaData();
        Map<String, String> personalisation =
            grantOfRepresentationPersonalisationService.getExelaPersonalisation(caseDetails);
        String reference = LocalDateTime.now().format(EXELA_DATE);

        SendEmailResponse response;
        response = notificationClientService.sendEmail(templateId, emailAddresses.getExcelaEmail(),
            personalisation, reference);
        log.info("Exela email reference response: {}", response.getReference());

        return getGeneratedSentEmailDocument(response, emailAddresses.getExcelaEmail(), SENT_EMAIL);
    }

    public SendEmailResponse sendSmeeAndFordEmail(List<ReturnedCaseDetails> caseDetails, String fromDate,
                                                  String toDate) throws NotificationClientException {
        log.info("sending Smee And Ford email");
        String templateId = notificationTemplates.getEmail().get(LanguagePreference.ENGLISH)
            .get(caseDetails.get(0).getData().getApplicationType())
            .getSmeeAndFordData();
        Map<String, String> personalisation =
            smeeAndFordPersonalisationService.getSmeeAndFordPersonalisation(caseDetails, fromDate, toDate);
        String reference = LocalDateTime.now().format(EXELA_DATE);

        SendEmailResponse response =
            notificationClientService.sendEmail(templateId, emailAddresses.getSmeeAndFordEmail(),
                personalisation, reference);
        log.info("Smee And Ford email reference response: {}", response.getReference());

        return response;
    }

    public void sendDisposalReminderEmail(uk.gov.hmcts.reform.ccd.client.model.CaseDetails caseDetails)
            throws NotificationClientException {
        log.info("Sending Disposal Reminder email");
        Map<String, Object> data = caseDetails.getData();
        if (data == null) {
            log.error("Case data is null for case ID: {}", caseDetails.getId());
            return;
        }
        String emailAddress = Optional.of(data)
                .flatMap(caseData -> {
                    try {
                        return Optional.ofNullable(getEmail(caseData));
                    } catch (BadRequestException e) {
                        return Optional.empty();
                    }
                })
                .orElseGet(() -> getUserEmail(caseDetails.getId()));
        if (emailAddress == null) {
            throw new NotificationClientException("Email address not found for case ID: " + caseDetails.getId());
        }
        ApplicationType applicationType = getApplicationType(caseDetails);

        LanguagePreference languagePreference = Optional.ofNullable(
                caseDetails.getData().get("languagePreferenceWelsh"))
                .map(Object::toString)
                .filter("Yes"::equalsIgnoreCase)
                .map(yes -> LanguagePreference.WELSH)
                .orElse(LanguagePreference.ENGLISH);
        log.info("ApplicationType: {}, LanguagePreference: {}", applicationType, languagePreference);
        String templateId = notificationTemplates.getEmail()
                .get(languagePreference)
                .get(applicationType)
                .getDisposalReminder();
        log.info("templateId: {}", templateId);
        Map<String, String> personalisation =
                disposalReminderPersonalisationService.getDisposalReminderPersonalisation(caseDetails, applicationType);
        log.info("start sendEmail");
        SendEmailResponse response =
                notificationClientService.sendEmail(templateId, emailAddress,
                        personalisation, caseDetails.getId().toString());
        log.info("Disposal Reminder email reference response: {}", response.getReference());
    }

    public Document sendEmailWithDocumentAttached(CaseDetails caseDetails, ExecutorsApplyingNotification executor,
                                                  State state) throws NotificationClientException, IOException {
        List<CollectionMember<Document>> probateSotDocumentsGenerated = caseDetails.getData()
            .getProbateSotDocumentsGenerated();
        Document document = probateSotDocumentsGenerated.get(probateSotDocumentsGenerated.size() - 1).getValue();
        byte[] sotDocument = documentManagementService.getDocument(document);

        Registry registry =
            registriesProperties.getRegistries().get(caseDetails.getData().getRegistryLocation().toLowerCase());

        Map<String, Object> personalisation =
            grantOfRepresentationPersonalisationService.getPersonalisation(caseDetails, registry);
        grantOfRepresentationPersonalisationService.addSingleAddressee(personalisation, executor.getName());

        personalisation.put(PERSONALISATION_SOT_LINK, prepareUpload(sotDocument));

        String reference = caseDetails.getData().getSolsSolicitorAppReference();
        String templateId = templateService.getTemplateId(state, caseDetails.getData().getApplicationType(),
                caseDetails.getData().getRegistryLocation(),
                caseDetails.getData().getLanguagePreference());
        String emailReplyToId = registry.getEmailReplyToId();

        doCommonNotificationServiceHandling(personalisation, caseDetails.getId());

        SendEmailResponse response =
            getSendEmailResponse(state, templateId, emailReplyToId, executor.getEmail(), personalisation, reference,
                caseDetails.getId());

        return getSentEmailDocument(state, executor.getEmail(), response);
    }

    public Document generateGrantReissue(CallbackRequest callbackRequest) throws NotificationClientException {
        CaseDetails caseDetails = callbackRequest.getCaseDetails();
        @Valid CaseData caseData = caseDetails.getData();
        CallbackResponse callbackResponse;
        Document sentEmail;
        callbackResponse =
            eventValidationService.validateEmailRequest(callbackRequest, emailAddressNotifyValidationRules);

        if (callbackResponse.getErrors().isEmpty()) {
            sentEmail = sendEmail(GRANT_REISSUED, caseDetails);
        } else if (caseData.getApplicationType().equals(ApplicationType.SOLICITOR)) {
            throw new InvalidEmailException(businessValidationMessageService.generateError(BUSINESS_ERROR,
                "emailNotProvidedSOLS").getMessage(),
                "Invalid email exception: No email address provided for application type SOLS: " + caseDetails.getId(),
                    businessValidationMessageService.generateError(BUSINESS_ERROR,
                            "emailNotProvidedSOLSWelsh").getMessage());
        } else {
            throw new InvalidEmailException(businessValidationMessageService.generateError(BUSINESS_ERROR,
                "emailNotProvidedPA").getMessage(),
                "Invalid email exception: No email address provided for application type PA: " + caseDetails.getId(),
                    businessValidationMessageService.generateError(BUSINESS_ERROR,
                            "emailNotProvidedPAWelsh").getMessage());
        }

        return sentEmail;
    }

    public Document sendGrantDelayedEmail(ReturnedCaseDetails caseDetails) throws NotificationClientException {
        String templateId = notificationTemplates.getEmail().get(caseDetails.getData().getLanguagePreference())
            .get(caseDetails.getData().getApplicationType())
            .getGrantDelayed();
        return sendGrantNotificationEmail(caseDetails, templateId);
    }

    public Document sendGrantAwaitingDocumentationEmail(ReturnedCaseDetails caseDetails)
        throws NotificationClientException {
        String templateId = notificationTemplates.getEmail().get(caseDetails.getData().getLanguagePreference())
            .get(caseDetails.getData().getApplicationType())
            .getGrantAwaitingDocumentation();
        return sendGrantNotificationEmail(caseDetails, templateId);
    }

    private Document sendGrantNotificationEmail(ReturnedCaseDetails caseDetails, String templateId)
        throws NotificationClientException {

        Registry registry =
            registriesProperties.getRegistries().get(caseDetails.getData().getRegistryLocation().toLowerCase());
        Map<String, Object> personalisation =
            grantOfRepresentationPersonalisationService.getPersonalisation(caseDetails, registry);

        String reference = caseDetails.getData().getSolsSolicitorAppReference();
        String emailAddress = caseDetails.getData().getApplicationType().equals(ApplicationType.PERSONAL)
            ? caseDetails.getData().getPrimaryApplicantEmailAddress() : caseDetails.getData().getSolsSolicitorEmail();

        doCommonNotificationServiceHandling(personalisation, caseDetails.getId());

        SendEmailResponse response = notificationClientService.sendEmail(caseDetails.getId(), templateId, emailAddress,
            personalisation, reference);
        log.info("Grant notification email reference response: {}", response.getReference());

        return getGeneratedSentEmailDocument(response, emailAddress, SENT_EMAIL);
    }

    protected Registry getRegistry(String registryLocation, LanguagePreference languagePreference) {
        String defaultRegistryLocation =
            (languagePreference == null || LanguagePreference.ENGLISH.equals(languagePreference))
                ? RegistryLocation.CTSC.getName() : RegistryLocation.CARDIFF.getName();
        return registriesProperties.getRegistries()
            .get((Optional.ofNullable(registryLocation).orElse(defaultRegistryLocation)).toLowerCase());
    }

    private Document getGeneratedSentEmailDocument(SendEmailResponse response, String emailAddress,
                                                   DocumentType docType) {
        SentEmail sentEmail = SentEmail.builder()
            .sentOn(LocalDateTime.now().format(formatter))
            .from(response.getFromEmail().orElse(""))
            .to(emailAddress)
            .subject(response.getSubject())
            .body(markdownTransformationService.toHtml(response.getBody()))
            .build();

        return pdfManagementService.generateAndUpload(sentEmail, docType);
    }

    private Document getGeneratedDocument(TemplatePreview response, String emailAddress,
                                          DocumentType docType) {
        SentEmail sentEmail = SentEmail.builder()
                .sentOn(LocalDateTime.now().format(formatter))
                .to(emailAddress)
                .subject(response.getSubject().orElse(""))
                .body(response.getBody())
                .build();
        Map<String, Object> placeholders = sentEmailPersonalisationService.getPersonalisation(sentEmail);
        return pdfManagementService.generateDocmosisDocumentAndUpload(placeholders, docType);
    }

    public void startGrantDelayNotificationPeriod(CaseDetails caseDetails) {

        CaseData caseData = caseDetails.getData();
        LocalDate grantDelayedNotificationReleaseLocalDate =
            LocalDate.parse(grantDelayedNotificationReleaseDate, RELEASE_DATE_FORMAT);
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
        LocalDate grantDelayedNotificationReleaseLocalDate =
            LocalDate.parse(grantDelayedNotificationReleaseDate, RELEASE_DATE_FORMAT);
        if (!LocalDate.now().isBefore(grantDelayedNotificationReleaseLocalDate)
            && (caseData.getScannedDocuments() == null || caseData.getScannedDocuments().isEmpty())) {
            LocalDate notificationDate = LocalDate.now().plusDays(grantAwaitingDocumentationNotificationPeriodDays);
            log.info("Setting grantAwaitingDocumentationNotificationDate {} for case {}", notificationDate.toString(),
                caseDetails.getId());
            caseData.setGrantAwaitingDocumentationNotificationDate(notificationDate);
        }
    }

    public void resetAwaitingDocumentationNotificationDate(CaseDetails caseDetails) {

        CaseData caseData = caseDetails.getData();
        LocalDate grantDelayedNotificationReleaseLocalDate =
            LocalDate.parse(grantDelayedNotificationReleaseDate, RELEASE_DATE_FORMAT);
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
                                                   String reference, Long caseId)
        throws NotificationClientException {
        SendEmailResponse response;
        switch (state) {
            case CASE_STOPPED:
            case CASE_STOPPED_CAVEAT:
                response =
                    notificationClientService.sendEmail(caseId, templateId, emailAddress,
                        personalisation, reference, emailReplyToId);
                break;
            case CASE_STOPPED_REQUEST_INFORMATION:
            case REDECLARATION_SOT:
            default:
                response = notificationClientService.sendEmail(caseId, templateId, emailAddress, personalisation,
                    reference);
        }
        log.info("Return the SendEmailResponse");
        return response;
    }

    private String getEmail(CaseData caseData) {
        if (caseData == null || caseData.getApplicationType() == null) {
            throw new BadRequestException("Casedata or ApplicationType is null");
        }
        log.info("getEmail for caseType: {}", caseData.getApplicationType());
        return switch (caseData.getApplicationType()) {
            case SOLICITOR -> Optional.ofNullable(caseData.getSolsSolicitorEmail())
                    .map(String::toLowerCase)
                    .orElse(null);
            case PERSONAL -> Optional.ofNullable(caseData.getPrimaryApplicantEmailAddress())
                    .map(String::toLowerCase)
                    .orElse(null);
            default -> throw new BadRequestException("Unsupported application type");
        };
    }

    private String getEmail(Map<String, Object> caseData) {
        String applicationType = Optional.ofNullable(caseData.get("applicationType"))
                .map(Object::toString)
                .orElseThrow(() -> new BadRequestException("ApplicationType is missing in case data"));

        log.info("getEmail for caseType: {}", applicationType);

        return switch (applicationType.toUpperCase()) {
            case "SOLICITOR" -> Optional.ofNullable(caseData.get("solsSolicitorEmail"))
                    .map(Object::toString)
                    .map(String::toLowerCase)
                    .orElse(null);
            case "PERSONAL" -> Optional.ofNullable(caseData.get("primaryApplicantEmailAddress"))
                    .map(Object::toString)
                    .map(String::toLowerCase)
                    .orElse(null);
            default -> throw new BadRequestException("Unsupported application type: " + applicationType);
        };
    }

    private String getUserEmail(Long caseReference) {
        log.info("getUserEmail for caseReference: {}", caseReference);
        return userInfoService.getUserEmailByCaseId(caseReference).orElse(null);
    }

    private ApplicationType getApplicationType(uk.gov.hmcts.reform.ccd.client.model.CaseDetails caseDetails) {
        if (caseDetails == null || caseDetails.getData() == null) {
            return ApplicationType.PERSONAL;
        }
        return Optional.ofNullable(caseDetails.getData().get("applicationType"))
                .map(Object::toString)
                .map(ApplicationType::fromString)
                .orElseGet(() -> PA_DRAFT_STATE_LIST.contains(caseDetails.getState())
                        ? ApplicationType.PERSONAL
                        : ApplicationType.SOLICITOR);
    }

    private String removedSolicitorNameForPersonalisation(CaseData caseData) {
        return caseData.getRemovedRepresentative() != null
                ? String.join(" ", caseData.getRemovedRepresentative().getSolicitorFirstName(),
                caseData.getRemovedRepresentative().getSolicitorLastName()) : null;
    }

    CommonNotificationResult doCommonNotificationServiceHandling(
            final Map<String, ?> personalisation,
            final Long caseId) throws NotificationClientException {
        final PersonalisationValidationRule.PersonalisationValidationResult validationResult =
                personalisationValidationRule.validatePersonalisation(personalisation);
        final Map<String, String> invalidFields = validationResult.invalidFields();
        final List<String> htmlFields = validationResult.htmlFields();

        if (!invalidFields.isEmpty()) {
            log.error("Personalisation validation failed for case: {} fields: {}",
                    caseId, invalidFields);
            throw new NotificationClientException(INVALID_PERSONALISATION_ERROR_MESSAGE);
        } else if (!htmlFields.isEmpty()) {
            log.info("Personalisation validation found HTML for case: {} fields: {}",
                    caseId, validationResult.htmlFields());
            return CommonNotificationResult.FOUND_HTML;
        }
        return CommonNotificationResult.ALL_OK;
    }

    enum CommonNotificationResult {
        ALL_OK,
        FOUND_HTML;
    }
}
