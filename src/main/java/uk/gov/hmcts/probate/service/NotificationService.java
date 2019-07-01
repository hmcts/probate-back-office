package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.config.notifications.EmailAddresses;
import uk.gov.hmcts.probate.config.notifications.NotificationTemplates;
import uk.gov.hmcts.probate.config.properties.registries.RegistriesProperties;
import uk.gov.hmcts.probate.config.properties.registries.Registry;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.CaseType;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.SentEmail;
import uk.gov.hmcts.probate.model.State;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.SendEmailResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static uk.gov.hmcts.probate.model.Constants.CTSC;
import static uk.gov.hmcts.probate.model.Constants.DOC_SUBTYPE_WILL;
import static uk.gov.hmcts.probate.model.DocumentType.SENT_EMAIL;

@RequiredArgsConstructor
@Component
public class NotificationService {
    @Autowired
    private final EmailAddresses emailAddresses;
    private final NotificationTemplates notificationTemplates;
    private final RegistriesProperties registriesProperties;
    private final NotificationClient notificationClient;
    private final MarkdownTransformationService markdownTransformationService;
    private final PDFManagementService pdfManagementService;
    private final CaveatQueryService caveatQueryService;
    private final FormatterService formatterService;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM Y HH:mm");
    private static final DateTimeFormatter DATETIME_FORMATTER_CAVEAT_EXPIRY = DateTimeFormatter.ofPattern("dd MMMM yyyy");
    private static final DateTimeFormatter EXCELA_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter EXCELA_CONTENT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final String PERSONALISATION_APPLICANT_NAME = "applicant_name";
    private static final String PERSONALISATION_DECEASED_NAME = "deceased_name";
    private static final String PERSONALISATION_SOLICITOR_NAME = "solicitor_name";
    private static final String PERSONALISATION_SOLICITOR_REFERENCE = "solicitor_reference";
    private static final String PERSONALISATION_REGISTRY_NAME = "registry_name";
    private static final String PERSONALISATION_REGISTRY_PHONE = "registry_phone";
    private static final String PERSONALISATION_CASE_STOP_DETAILS = "case-stop-details";
    private static final String PERSONALISATION_CAVEAT_CASE_ID = "caveat_case_id";
    private static final String PERSONALISATION_DECEASED_DOD = "deceased_dod";
    private static final String PERSONALISATION_CCD_REFERENCE = "ccd_reference";
    private static final String PERSONALISATION_CAVEAT_EXPIRY_DATE = "caveat_expiry_date";
    private static final String PERSONALISATION_MESSAGE_CONTENT = "message_content";
    private static final String PERSONALISATION_EXCELA_NAME = "excelaName";
    private static final String PERSONALISATION_CASE_DATA = "caseData";
    private static final String PERSONALISATION_DATE_CAVEAT_ENTERED = "date_caveat_entered";
    private static final String PERSONALISATION_CAVEATOR_NAME = "caveator_name";
    private static final String PERSONALISATION_CAVEATOR_ADDRESS = "caveator_address";

    public Document sendEmail(State state, CaseDetails caseDetails)
            throws NotificationClientException {

        CaseData caseData = caseDetails.getData();
        Registry registry = registriesProperties.getRegistries().get(caseData.getRegistryLocation().toLowerCase());

        String templateId = getTemplateId(state, caseData.getApplicationType(), caseData.getRegistryLocation());
        String emailReplyToId = registry.getEmailReplyToId();
        String emailAddress = getEmail(caseData);
        Map<String, String> personalisation = getPersonalisation(caseDetails, registry);
        String reference = caseData.getSolsSolicitorAppReference();

        if (state == state.CASE_STOPPED_CAVEAT) {
            personalisation = getCaveatStopPersonalisation(personalisation, caseData);
        }

        SendEmailResponse response;

        if (state == State.CASE_STOPPED || state == State.CASE_STOPPED_CAVEAT) {
            response = notificationClient.sendEmail(templateId, emailAddress, personalisation, reference, emailReplyToId);
        } else {
            response = notificationClient.sendEmail(templateId, emailAddress, personalisation, reference);
        }

        return getGeneratedSentEmailDocument(response, emailAddress, SENT_EMAIL);
    }

    public Document sendCaveatEmail(State state, CaveatDetails caveatDetails)
            throws NotificationClientException {

        CaveatData caveatData = caveatDetails.getData();
        Registry registry = registriesProperties.getRegistries().get(caveatData.getRegistryLocation().toLowerCase());

        String templateId = getTemplateId(state, caveatData.getApplicationType(), caveatData.getRegistryLocation());
        String emailAddress = caveatData.getCaveatorEmailAddress();
        Map<String, String> personalisation = getCaveatPersonalisation(caveatDetails, registry);
        String reference = caveatDetails.getId().toString();

        SendEmailResponse response;

        response = notificationClient.sendEmail(templateId, emailAddress, personalisation, reference);

        DocumentType documentType;
        switch (state) {
            case GENERAL_CAVEAT_MESSAGE:
                documentType = SENT_EMAIL;
                break;
            case CAVEAT_RAISED:
                documentType = SENT_EMAIL;
                break;
            default:
                throw new BadRequestException("Unsupported State");
        }

        return getGeneratedSentEmailDocument(response, emailAddress, documentType);
    }

    public Document sendExcelaEmail(List<ReturnedCaseDetails> caseDetails) throws
            NotificationClientException {
        String templateId = notificationTemplates.getEmail().get(caseDetails.get(0).getData().getApplicationType())
                .getExcelaData();
        Map<String, String> personalisation = getExcelaPersonalisation(caseDetails);
        String reference = LocalDateTime.now().format(EXCELA_DATE);

        SendEmailResponse response;

        response = notificationClient.sendEmail(templateId, emailAddresses.getExcelaEmail(), personalisation, reference);

        return getGeneratedSentEmailDocument(response, emailAddresses.getExcelaEmail(), SENT_EMAIL);
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

    private Map<String, String> getPersonalisation(CaseDetails caseDetails, Registry registry) {
        CaseData caseData = caseDetails.getData();
        HashMap<String, String> personalisation = new HashMap<>();
        personalisation.put(PERSONALISATION_APPLICANT_NAME, caseData.getPrimaryApplicantFullName());
        personalisation.put(PERSONALISATION_DECEASED_NAME, caseData.getDeceasedFullName());
        personalisation.put(PERSONALISATION_SOLICITOR_NAME, caseData.getSolsSOTName());
        personalisation.put(PERSONALISATION_SOLICITOR_REFERENCE, caseData.getSolsSolicitorAppReference());
        personalisation.put(PERSONALISATION_REGISTRY_NAME, registry.getName());
        personalisation.put(PERSONALISATION_REGISTRY_PHONE, registry.getPhone());
        personalisation.put(PERSONALISATION_CASE_STOP_DETAILS, caseData.getBoStopDetails());
        personalisation.put(PERSONALISATION_CAVEAT_CASE_ID, caseData.getBoCaseStopCaveatId());
        personalisation.put(PERSONALISATION_DECEASED_DOD, caseData.getDeceasedDateOfDeathFormatted());
        personalisation.put(PERSONALISATION_CCD_REFERENCE, caseDetails.getId().toString());

        return personalisation;
    }

    private Map<String, String> getCaveatStopPersonalisation(Map<String, String> personalisation, CaseData caseData) {

        CaveatData caveatData = caveatQueryService.findCaveatById(CaseType.CAVEAT, caseData.getBoCaseStopCaveatId());

        if (caveatData != null) {
            personalisation.put(PERSONALISATION_CAVEATOR_NAME, caveatData.getCaveatorFullName());
            personalisation.put(PERSONALISATION_CAVEATOR_ADDRESS, formatterService.formatAddress(caveatData.getCaveatorAddress()));
            personalisation.put(PERSONALISATION_CAVEAT_EXPIRY_DATE, caveatData.getExpiryDate().format(DATETIME_FORMATTER_CAVEAT_EXPIRY));
        }

        if (caseData.getApplicationType().equals(ApplicationType.SOLICITOR)) {
            personalisation.replace(PERSONALISATION_APPLICANT_NAME, caseData.getSolsSOTName());
        }
        return personalisation;
    }

    private Map<String, String> getCaveatPersonalisation(CaveatDetails caveatDetails, Registry registry) {
        CaveatData caveatData = caveatDetails.getData();

        HashMap<String, String> personalisation = new HashMap<>();

        personalisation.put(PERSONALISATION_APPLICANT_NAME, caveatData.getCaveatorFullName());
        personalisation.put(PERSONALISATION_DECEASED_NAME, caveatData.getDeceasedFullName());
        personalisation.put(PERSONALISATION_CCD_REFERENCE, caveatDetails.getId().toString());
        personalisation.put(PERSONALISATION_MESSAGE_CONTENT, caveatData.getMessageContent());
        personalisation.put(PERSONALISATION_REGISTRY_NAME, registry.getName());
        personalisation.put(PERSONALISATION_REGISTRY_PHONE, registry.getPhone());
        if (caveatData.getExpiryDate() == null) {
            caveatData.setExpiryDate(LocalDate.now());
        }
        personalisation.put(PERSONALISATION_CAVEAT_EXPIRY_DATE, caveatData.getExpiryDate().format(DATETIME_FORMATTER_CAVEAT_EXPIRY));

        return personalisation;
    }

    private Map<String, String> getExcelaPersonalisation(List<ReturnedCaseDetails> cases) {
        HashMap<String, String> personalisation = new HashMap<>();

        StringBuilder data = getBuiltData(cases);

        personalisation.put(PERSONALISATION_EXCELA_NAME, LocalDateTime.now().format(EXCELA_DATE) + "will");
        personalisation.put(PERSONALISATION_CASE_DATA, data.toString());

        return personalisation;
    }

    private String getTemplateId(State state, ApplicationType applicationType, String registryLocation) {
        switch (state) {
            case DOCUMENTS_RECEIVED:
                return notificationTemplates.getEmail().get(applicationType).getDocumentReceived();
            case CASE_STOPPED:
                return notificationTemplates.getEmail().get(applicationType).getCaseStopped();
            case CASE_STOPPED_CAVEAT:
                return notificationTemplates.getEmail().get(applicationType).getCaseStoppedCaveat();
            case GRANT_ISSUED:
                return notificationTemplates.getEmail().get(applicationType).getGrantIssued();
            case GENERAL_CAVEAT_MESSAGE:
                return notificationTemplates.getEmail().get(applicationType).getGeneralCaveatMessage();
            case CAVEAT_RAISED:
                if (registryLocation.equalsIgnoreCase(CTSC)) {
                    return notificationTemplates.getEmail().get(applicationType).getCaveatRaisedCtsc();
                } else {
                    return notificationTemplates.getEmail().get(applicationType).getCaveatRaised();
                }
            default:
                throw new BadRequestException("Unsupported state");
        }
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

    private String getWillReferenceNumber(CaseData data) {
        for (CollectionMember<ScannedDocument> document : data.getScannedDocuments()) {
            if (document.getValue().getSubtype() != null && document.getValue().getSubtype().equals(DOC_SUBTYPE_WILL)) {
                return document.getValue().getControlNumber();
            }
        }
        return "";
    }

    private StringBuilder getBuiltData(List<ReturnedCaseDetails> cases) {
        StringBuilder data = new StringBuilder();

        for (ReturnedCaseDetails currentCase : cases) {
            data.append(getWillReferenceNumber(currentCase.getData()));
            data.append(", ");
            data.append(currentCase.getData().getDeceasedForenames());
            data.append(" ");
            data.append(currentCase.getData().getDeceasedSurname());
            data.append(", ");
            data.append(EXCELA_CONTENT_DATE.format(currentCase.getData().getDeceasedDateOfBirth()));
            data.append(", ");
            data.append(EXCELA_CONTENT_DATE.format(LocalDate.parse(currentCase.getData().getGrantIssuedDate())));
            data.append(", ");
            data.append(currentCase.getId().toString());
            data.append("\n");
        }
        return data;
    }
}
