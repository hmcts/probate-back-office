package uk.gov.hmcts.probate.service.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.config.notifications.EmailTemplates;
import uk.gov.hmcts.probate.config.notifications.NotificationTemplates;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.LanguagePreference;
import uk.gov.hmcts.probate.model.State;

import static uk.gov.hmcts.probate.model.Constants.CTSC;

@Slf4j
@RequiredArgsConstructor
@Service
public class TemplateService {

    private final NotificationTemplates notificationTemplates;

    public String getTemplateId(State state, ApplicationType applicationType, String registryLocation,
                                LanguagePreference languagePreference) {

        EmailTemplates emailTemplates = notificationTemplates.getEmail().get(languagePreference).get(applicationType);
        switch (state) {
            case DOCUMENTS_RECEIVED:
                return emailTemplates.getDocumentReceived();
            case CASE_STOPPED:
                return emailTemplates.getCaseStopped();
            case CASE_STOPPED_CAVEAT:
                return emailTemplates.getCaseStoppedCaveat();
            case GRANT_ISSUED:
                return emailTemplates.getGrantIssued();
            case GRANT_REISSUED:
                return emailTemplates.getGrantReissued();
            case GENERAL_CAVEAT_MESSAGE:
                return emailTemplates.getGeneralCaveatMessage();
            case CASE_STOPPED_REQUEST_INFORMATION:
                return emailTemplates.getRequestInformation();
            case REDECLARATION_SOT:
                return emailTemplates.getRedeclarationSot();
            case CAVEAT_RAISED:
                if (registryLocation.equalsIgnoreCase(CTSC)) {
                    return emailTemplates.getCaveatRaisedCtsc();
                } else {
                    return emailTemplates.getCaveatRaised();
                }
            case CAVEAT_EXTEND:
                return emailTemplates.getCaveatExtend();
            case CAVEAT_RAISED_SOLS:
                return notificationTemplates.getEmail().get(languagePreference).get(applicationType).getCaveatRaisedSols();
            case CAVEAT_WITHDRAW:
                return emailTemplates.getCaveatWithdrawn();
             default:
                throw new BadRequestException("Unsupported state");
        }
    }
}




