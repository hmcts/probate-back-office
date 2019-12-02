package uk.gov.hmcts.probate.service.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.config.notifications.NotificationTemplates;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.State;

import static uk.gov.hmcts.probate.model.Constants.CTSC;


@Slf4j
@RequiredArgsConstructor
@Component
public class WelshTemplateIdDeterminer  implements TemplateIdDeterminer{

    private final NotificationTemplates notificationTemplates;

    @Override
    public String determineTemplateId(State state, ApplicationType applicationType, String registryLocation) {
        switch (state) {
            case DOCUMENTS_RECEIVED:
                return notificationTemplates.getEmail().get(applicationType).getDocumentReceivedWelsh();
            case CASE_STOPPED:
                return notificationTemplates.getEmail().get(applicationType).getCaseStoppedWelsh();
            case CASE_STOPPED_CAVEAT:
                return notificationTemplates.getEmail().get(applicationType).getCaseStoppedCaveatWelsh();
            case GRANT_ISSUED:
                return notificationTemplates.getEmail().get(applicationType).getGrantIssuedWelsh();
            case GRANT_REISSUED:
                return notificationTemplates.getEmail().get(applicationType).getGrantReissuedWelsh();
            case GENERAL_CAVEAT_MESSAGE:
                return notificationTemplates.getEmail().get(applicationType).getGeneralCaveatMessageWelsh();
            case CASE_STOPPED_REQUEST_INFORMATION:
                return notificationTemplates.getEmail().get(applicationType).getRequestInformationWelsh();
            case REDECLARATION_SOT:
                return notificationTemplates.getEmail().get(applicationType).getRedeclarationSotWelsh();
            case CAVEAT_RAISED:
                if (registryLocation.equalsIgnoreCase(CTSC)) {
                    return notificationTemplates.getEmail().get(applicationType).getCaveatRaisedCtscWelsh();
                } else {
                    return notificationTemplates.getEmail().get(applicationType).getCaveatRaisedWelsh();
                }
            default:
                throw new BadRequestException("Unsupported state");
        }
    }
}
