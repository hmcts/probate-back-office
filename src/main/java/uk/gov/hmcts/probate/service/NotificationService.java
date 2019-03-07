package uk.gov.hmcts.probate.service;

import joptsimple.internal.Strings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.config.notifications.NotificationTemplates;
import uk.gov.hmcts.probate.config.properties.registries.RegistriesProperties;
import uk.gov.hmcts.probate.config.properties.registries.Registry;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.SentEmail;
import uk.gov.hmcts.probate.model.State;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.SendEmailResponse;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static uk.gov.hmcts.probate.model.Constants.DOC_SUBTYPE_WILL;
import static uk.gov.hmcts.probate.model.Constants.EXCELA_EMAIL;
import static uk.gov.hmcts.probate.model.DocumentType.SENT_EMAIL;

@RequiredArgsConstructor
@Component
public class NotificationService {
    private final NotificationTemplates notificationTemplates;
    private final RegistriesProperties registriesProperties;
    private final NotificationClient notificationClient;
    private final MarkdownTransformationService markdownTransformationService;
    private final PDFManagementService pdfManagementService;

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM Y HH:mm");
    private static final DateTimeFormatter EXCELA_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");

    public Document sendEmail(State state, CaseDetails caseDetails)
            throws NotificationClientException {

        CaseData caseData = caseDetails.getData();
        Registry registry = registriesProperties.getRegistries().get(caseData.getRegistryLocation().toLowerCase());

        String templateId = getTemplateId(state, caseData.getApplicationType());
        String emailReplyToId = registry.getEmailReplyToId();
        String emailAddress = getEmail(caseData);
        Map<String, String> personalisation = getPersonalisation(caseDetails, registry);
        String reference = caseData.getSolsSolicitorAppReference();

        SendEmailResponse response;

        if (state == State.CASE_STOPPED) {
            response = notificationClient.sendEmail(templateId, emailAddress, personalisation, reference, emailReplyToId);
        } else {
            response = notificationClient.sendEmail(templateId, emailAddress, personalisation, reference);
        }

        return getGeneratedSentEmailDocument(response, emailAddress);
    }

    public Document sendCaveatEmail(State state, CaveatDetails caveatDetails)
        throws NotificationClientException {

        CaveatData caveatData = caveatDetails.getData();
        Registry registry = registriesProperties.getRegistries().get(caveatData.getRegistryLocation().toLowerCase());

        String templateId = getTemplateId(state, caveatData.getApplicationType());
        String emailAddress = caveatData.getCaveatorEmailAddress();
        Map<String, String> personalisation = getCaveatPersonalisation(caveatDetails, registry);
        String reference = caveatDetails.getId().toString();

        SendEmailResponse response;

        response = notificationClient.sendEmail(templateId, emailAddress, personalisation, reference);

        return getGeneratedSentEmailDocument(response, emailAddress);
    }

    public Document sendExcelaEmail(CaseDetails caseDetails) throws
            NotificationClientException {
        String templateId = notificationTemplates.getEmail().get(caseDetails.getData().getApplicationType())
                .getExcelaData();
        Map<String, String> personalisation = getExcelaPersonalisation(caseDetails.getId(), caseDetails.getData());
        String reference = caseDetails.getId().toString();

        SendEmailResponse response;

        response = notificationClient.sendEmail(templateId, EXCELA_EMAIL, personalisation, reference);

        return getGeneratedSentEmailDocument(response, EXCELA_EMAIL);
    }

    private Document getGeneratedSentEmailDocument(SendEmailResponse response, String emailAddress) {
        SentEmail sentEmail = SentEmail.builder()
                .sentOn(LocalDateTime.now().format(formatter))
                .from(response.getFromEmail().orElse(""))
                .to(emailAddress)
                .subject(response.getSubject())
                .body(markdownTransformationService.toHtml(response.getBody()))
                .build();

        return pdfManagementService.generateAndUpload(sentEmail, SENT_EMAIL);
    }

    private Map<String, String> getPersonalisation(CaseDetails caseDetails, Registry registry) {
        CaseData caseData = caseDetails.getData();
        HashMap<String, String> personalisation = new HashMap<>();
        personalisation.put("applicant_name", caseData.getPrimaryApplicantFullName());
        personalisation.put("deceased_name", caseData.getDeceasedFullName());
        personalisation.put("solicitor_name", caseData.getSolsSOTName());
        personalisation.put("solicitor_reference", caseData.getSolsSolicitorAppReference());
        personalisation.put("registry_name", registry.getName());
        personalisation.put("registry_phone", registry.getPhone());
        personalisation.put("case-stop-details", caseData.getBoStopDetails());
        personalisation.put("deceased_dod", caseData.getDeceasedDateOfDeathFormatted());
        personalisation.put("ccd_reference", caseDetails.getId().toString());

        return personalisation;
    }

    private Map<String, String> getCaveatPersonalisation(CaveatDetails caveatDetails, Registry registry) {
        CaveatData caveatData = caveatDetails.getData();

        HashMap<String, String> personalisation = new HashMap<>();

        personalisation.put("applicant_name", caveatData.getCaveatorFullName());
        personalisation.put("deceased_name", caveatData.getDeceasedFullName());
        personalisation.put("ccd_reference", caveatDetails.getId().toString());
        personalisation.put("message_content", caveatData.getMessageContent());
        personalisation.put("registry_name", registry.getName());
        personalisation.put("registry_phone", registry.getPhone());

        return personalisation;
    }

    private Map<String, String> getExcelaPersonalisation(Long id, CaseData caseData) {
        HashMap<String, String> personalisation = new HashMap<>();

        personalisation.put("excelaName", LocalDateTime.now().format(EXCELA_DATE) + "will");
        personalisation.put("ccdId", id.toString());
        personalisation.put("deceasedSurname", caseData.getDeceasedSurname());
        personalisation.put("willReferenceNumber", getWillReferenceNumber(caseData));

        return personalisation;
    }

    private String getTemplateId(State state, ApplicationType applicationType) {
        switch (state) {
            case DOCUMENTS_RECEIVED:
                return notificationTemplates.getEmail().get(applicationType).getDocumentReceived();
            case CASE_STOPPED:
                return notificationTemplates.getEmail().get(applicationType).getCaseStopped();
            case GRANT_ISSUED:
                return notificationTemplates.getEmail().get(applicationType).getGrantIssued();
            case GENERAL_CAVEAT_MESSAGE:
                return notificationTemplates.getEmail().get(applicationType).getGeneralCaveatMessage();
            default:
                throw new BadRequestException("Unsupported state", null);
        }
    }

    private String getEmail(CaseData caseData) {
        switch (caseData.getApplicationType()) {
            case SOLICITOR:
                return caseData.getSolsSolicitorEmail().toLowerCase();
            case PERSONAL:
                return caseData.getPrimaryApplicantEmailAddress().toLowerCase();
            default:
                throw new BadRequestException("Unsupported application type", null);
        }
    }

    private String getWillReferenceNumber(CaseData data) {
        for (CollectionMember<ScannedDocument> document : data.getScannedDocuments()) {
            if (document.getValue().getSubtype().equals(DOC_SUBTYPE_WILL)) {
                return document.getValue().getControlNumber();
            }
        }
        return Strings.EMPTY;
    }
}
