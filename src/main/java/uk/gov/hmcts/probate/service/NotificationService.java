package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.config.notifications.NotificationTemplates;
import uk.gov.hmcts.probate.config.properties.registries.RegistriesProperties;
import uk.gov.hmcts.probate.config.properties.registries.Registry;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.SentEmail;
import uk.gov.hmcts.probate.model.State;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.SendEmailResponse;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

    public Optional<Document> sendEmail(State state, CaseData caseData)
            throws NotificationClientException {

        Registry registry = registriesProperties.getRegistries().get(caseData.getRegistryLocation().toLowerCase());

        String templateId = getTemplateId(state, caseData.getApplicationType());
        String emailReplyToId = registry.getEmailReplyToId();
        String emailAddress = getEmail(caseData);
        Map<String, String> personalisation = getPersonalisation(caseData, registry);
        String reference = caseData.getSolsSolicitorAppReference();

        Document document = null;

        if (state == State.CASE_STOPPED) {
            SendEmailResponse response = notificationClient.sendEmail(templateId, emailAddress, personalisation, reference, emailReplyToId);
            SentEmail sentEmail = SentEmail.builder()
                    .sentOn(LocalDateTime.now().format(formatter))
                    .from(response.getFromEmail().orElse(""))
                    .to(emailAddress)
                    .subject(response.getSubject())
                    .body(markdownTransformationService.toHtml(response.getBody()))
                    .build();

            document = pdfManagementService.generateAndUpload(sentEmail, SENT_EMAIL);
        } else {
            notificationClient.sendEmail(templateId, emailAddress, personalisation, reference);
        }

        return Optional.ofNullable(document);
    }

    private Map<String, String> getPersonalisation(CaseData caseData, Registry registry) {
        HashMap<String, String> personalisation = new HashMap<>();
        personalisation.put("applicant_name", caseData.getPrimaryApplicantFullName());
        personalisation.put("deceased_name", caseData.getDeceasedFullName());
        personalisation.put("solicitor_name", caseData.getSolsSOTName());
        personalisation.put("solicitor_reference", caseData.getSolsSolicitorAppReference());
        personalisation.put("registry_name", registry.getName());
        personalisation.put("registry_phone", registry.getPhone());
        personalisation.put("case-stop-details", caseData.getBoStopDetails());

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
            default:
                throw new BadRequestException("Unsupported state", null);
        }
    }

    private String getEmail(CaseData caseData) {
        switch (caseData.getApplicationType()) {
            case SOLICITOR:
                return caseData.getSolsSolicitorEmail();
            case PERSONAL:
                return caseData.getPrimaryApplicantEmailAddress();
            default:
                throw new BadRequestException("Unsupported application type", null);
        }
    }
}
