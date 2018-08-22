package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.config.notifications.NotificationTemplates;
import uk.gov.hmcts.probate.config.properties.registries.RegistriesProperties;
import uk.gov.hmcts.probate.config.properties.registries.Registry;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.State;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class NotificationService {
    private final NotificationTemplates notificationTemplates;
    private final RegistriesProperties registriesProperties;
    private final NotificationClient notificationClient;

    public void sendEmail(State state, CaseData caseData)
            throws NotificationClientException {

        Registry registry = registriesProperties.getRegistries().get(caseData.getRegistryLocation().toLowerCase());

        String templateId = getTemplateId(state, caseData.getApplicationType());
        String emailReplyToId = registry.getEmailReplyToId();
        String emailAddress = getEmail(caseData);
        Map<String, String> personalisation = getPersonalisation(caseData, registry);
        String reference = caseData.getSolsSolicitorAppReference();

        if (state == State.CASE_STOPPED) {
            notificationClient.sendEmail(templateId, emailAddress, personalisation, reference, emailReplyToId);

        } else {
            notificationClient.sendEmail(templateId, emailAddress, personalisation, reference);
        }
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
