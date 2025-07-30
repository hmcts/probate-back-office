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
import static uk.gov.hmcts.probate.model.Constants.CHANNEL_CHOICE_BULKSCAN;
import static uk.gov.hmcts.probate.model.Constants.CHANNEL_CHOICE_DIGITAL;
import static uk.gov.hmcts.probate.model.Constants.CHANNEL_CHOICE_PAPERFORM;
import static uk.gov.hmcts.probate.model.Constants.NO;

@Slf4j
@RequiredArgsConstructor
@Service
public class TemplateService {

    private final NotificationTemplates notificationTemplates;

    public String getTemplateId(State state, ApplicationType applicationType, String registryLocation,
                                LanguagePreference languagePreference) {
        return getTemplateId(state, applicationType, registryLocation, languagePreference, null, null, null);
    }

    public String getTemplateId(State state, ApplicationType applicationType, String registryLocation,
                                LanguagePreference languagePreference, CaseOrigin caseOrigin,
                                String channelChoice, String informationNeededByPost) {

        EmailTemplates emailTemplates = notificationTemplates.getEmail().get(languagePreference).get(applicationType);
        switch (state) {
            case APPLICATION_RECEIVED:
                if (CHANNEL_CHOICE_PAPERFORM.equalsIgnoreCase(channelChoice) && caseOrigin.equals(CASEWORKER)) {
                    return emailTemplates.getApplicationReceivedPaperFormCaseworker();
                } else {
                    return emailTemplates.getApplicationReceived();
                }
            case APPLICATION_RECEIVED_NO_DOCS:
                return emailTemplates.getApplicationReceivedNoDocs();
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
                if (requestInfoByPostForPersonalApplication(channelChoice, applicationType, informationNeededByPost)) {
                    return emailTemplates.getRequestInformationByPost();
                } else {
                    return emailTemplates.getRequestInformation();
                }
            case REDECLARATION_SOT:
                return emailTemplates.getRedeclarationSot();
            case GRANT_RAISED:
                if (CHANNEL_CHOICE_PAPERFORM.equalsIgnoreCase(channelChoice)
                        || CHANNEL_CHOICE_BULKSCAN.equalsIgnoreCase(channelChoice)) {
                    return emailTemplates.getGrantRaisedPaperFormBulkScan();
                } else {
                    return emailTemplates.getGrantRaised();
                }
            case NOC:
                return emailTemplates.getNoticeOfChangeReceived();
            case CAVEAT_RAISED:
                return emailTemplates.getCaveatRaised();
            case CAVEAT_EXTEND:
                return emailTemplates.getCaveatExtend();
            case CAVEAT_RAISED_SOLS:
                return notificationTemplates.getEmail().get(languagePreference).get(applicationType)
                    .getCaveatRaisedSols();
            case CAVEAT_WITHDRAW:
                return emailTemplates.getCaveatWithdrawn();
            default:
                throw new BadRequestException("Unsupported state");
        }
    }

    private boolean requestInfoByPostForPersonalApplication(String channelChoice,
                                                            ApplicationType applicationType,
                                                            String informationNeededByPost) {
        return ApplicationType.PERSONAL.equals(applicationType)
            && ((CHANNEL_CHOICE_DIGITAL.equalsIgnoreCase(channelChoice)
                && !NO.equalsIgnoreCase(informationNeededByPost))
                || CHANNEL_CHOICE_BULKSCAN.equalsIgnoreCase(channelChoice)
                || CHANNEL_CHOICE_PAPERFORM.equalsIgnoreCase(channelChoice));
    }

    public String getStopReminderTemplateId(ApplicationType applicationType,
                                            LanguagePreference languagePreference,
                                            String channelChoice,
                                            String informationNeededByPost,
                                            boolean isFirstStopReminder) {
        EmailTemplates emailTemplates = notificationTemplates.getEmail().get(languagePreference).get(applicationType);

        boolean isSolicitor = ApplicationType.SOLICITOR.equals(applicationType);
        boolean isPostalRequest = requestInfoByPostForPersonalApplication(channelChoice,
                applicationType, informationNeededByPost);
        boolean useHubTemplate = !(isSolicitor || isPostalRequest);

        if (isFirstStopReminder) {
            return useHubTemplate ? emailTemplates.getFirstStopReminderForHub()
                    : emailTemplates.getFirstStopReminder();
        } else {
            return useHubTemplate ? emailTemplates.getSecondStopReminderForHub()
                    : emailTemplates.getSecondStopReminder();
        }
    }

    public String getHseReminderTemplateId(ApplicationType applicationType,
                                            LanguagePreference languagePreference,
                                            String channelChoice,
                                            String informationNeededByPost) {
        EmailTemplates emailTemplates = notificationTemplates.getEmail().get(languagePreference).get(applicationType);

        boolean isSolicitor = ApplicationType.SOLICITOR.equals(applicationType);
        boolean isPostalRequest = requestInfoByPostForPersonalApplication(channelChoice,
                applicationType, informationNeededByPost);
        boolean useHubTemplate = !(isSolicitor || isPostalRequest);

        return useHubTemplate ? emailTemplates.getHseReminderForHub()
                : emailTemplates.getHseReminder();
    }

    public String getDormantWarningTemplateId(ApplicationType applicationType,
                                              LanguagePreference languagePreference) {
        EmailTemplates emailTemplates = notificationTemplates.getEmail().get(languagePreference).get(applicationType);
        return emailTemplates.getDormantWarning();
    }

    public String getUnsubmittedApplicationTemplateId(ApplicationType applicationType,
                                                      LanguagePreference languagePreference) {
        EmailTemplates emailTemplates = notificationTemplates.getEmail().get(languagePreference).get(applicationType);
        return emailTemplates.getUnsubmittedApplication();
    }

    public String getDeclarationNotSignedTemplateId(LanguagePreference languagePreference,
                                                    boolean isPrimaryApplicant) {
        EmailTemplates emailTemplates =
                notificationTemplates.getEmail().get(languagePreference).get(ApplicationType.PERSONAL);
        if (isPrimaryApplicant) {
            return emailTemplates.getDeclarationNotSignedPrimaryApplicant();
        } else {
            return emailTemplates.getDeclarationNotSignedExecutors();
        }
    }
}




