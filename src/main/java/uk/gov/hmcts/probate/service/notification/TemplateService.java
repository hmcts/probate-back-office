package uk.gov.hmcts.probate.service.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.config.notifications.EmailTemplates;
import uk.gov.hmcts.probate.config.notifications.NotificationTemplates;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.CaseOrigin;
import uk.gov.hmcts.probate.model.LanguagePreference;
import uk.gov.hmcts.probate.model.State;

import static uk.gov.hmcts.probate.model.CaseOrigin.CASEWORKER;
import static uk.gov.hmcts.probate.model.Constants.CTSC;
import static uk.gov.hmcts.probate.model.Constants.YES;

@Slf4j
@RequiredArgsConstructor
@Service
public class TemplateService {

    private final NotificationTemplates notificationTemplates;

    public String getTemplateId(State state, ApplicationType applicationType, String registryLocation,
                                LanguagePreference languagePreference) {
        return getTemplateId(state, applicationType, registryLocation, languagePreference, null, null);

    }

    public String getTemplateId(State state, ApplicationType applicationType, String registryLocation,
                                LanguagePreference languagePreference, String paperForm) {
        return getTemplateId(state, applicationType, registryLocation, languagePreference, paperForm, null);

    }

    public String getTemplateId(State state, ApplicationType applicationType, String registryLocation,
                                LanguagePreference languagePreference, String paperForm, CaseOrigin caseOrigin) {

        EmailTemplates emailTemplates = notificationTemplates.getEmail().get(languagePreference).get(applicationType);
        switch (state) {
            case APPLICATION_RECEIVED:
                if (YES.equalsIgnoreCase(paperForm) && caseOrigin.equals(CASEWORKER)) {
                    return emailTemplates.getApplicationReceivedPaperFormCaseworker();
                } else {
                    return emailTemplates.getApplicationReceived();
                }
            case DOCUMENTS_RECEIVED:
                return emailTemplates.getDocumentReceived();
            case CASE_STOPPED:
                return emailTemplates.getCaseStopped();
            case CASE_STOPPED_CAVEAT:
                return emailTemplates.getCaseStoppedCaveat();
            case GRANT_ISSUED:
                return emailTemplates.getGrantIssued();
            case GRANT_ISSUED_INTESTACY:
                return emailTemplates.getGrantIssuedIntestacy();
            case GRANT_REISSUED:
                return emailTemplates.getGrantReissued();
            case GENERAL_CAVEAT_MESSAGE:
                return emailTemplates.getGeneralCaveatMessage();
            case CASE_STOPPED_REQUEST_INFORMATION:
                return emailTemplates.getRequestInformation();
            case REDECLARATION_SOT:
                return emailTemplates.getRedeclarationSot();
            case GRANT_RAISED:
                if (YES.equalsIgnoreCase(paperForm)) {
                    return emailTemplates.getGrantRaisedPaperFormBulkScan();
                } else {
                    return emailTemplates.getGrantRaised();
                }
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




