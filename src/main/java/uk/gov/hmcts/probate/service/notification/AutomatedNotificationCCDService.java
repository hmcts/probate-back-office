package uk.gov.hmcts.probate.service.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.EventId;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.service.ccd.CcdClientApi;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static uk.gov.hmcts.probate.model.ccd.CcdCaseType.GRANT_OF_REPRESENTATION;

@Slf4j
@Service
@RequiredArgsConstructor
public class AutomatedNotificationCCDService {
    private CcdClientApi ccdClientApi;
    public static final String EVENT_DESCRIPTION = "Send Stop Reminder";
    public static final String EVENT_SUMMARY = "Send Stop Reminder";

    @Autowired
    public AutomatedNotificationCCDService(final CcdClientApi ccdClientApi) {
        this.ccdClientApi = ccdClientApi;
    }

    public void saveNotification(final CaseDetails caseDetails,
                                 final String caseId,
                                 final SecurityDTO securityDTO,
                                 final Document sentEmail) {

        log.info("AutomatedNotificationCCDService saveNotification to Case: " + caseId);
        ccdClientApi.updateCaseAsCaseworker(GRANT_OF_REPRESENTATION, caseId,
                caseDetails.getLastModified(), buildCaseData(caseDetails, sentEmail),
                EventId.AUTOMATED_NOTIFICATION, securityDTO, EVENT_DESCRIPTION, EVENT_SUMMARY);
    }

    private CaseData buildCaseData(CaseDetails caseDetails, Document sentEmail) {
        List<CollectionMember<Document>> notifications = getNotifications(caseDetails);
        notifications.add(new CollectionMember<>(null, sentEmail));
        return CaseData.builder()
                .probateNotificationsGenerated(notifications)
                .firstStopReminderSentDate(LocalDate.now())
                .build();
    }

    @SuppressWarnings("unchecked")
    private List<CollectionMember<Document>> getNotifications(CaseDetails caseDetails) {
        List<CollectionMember<Document>> notifications =
                (List<CollectionMember<Document>>) caseDetails.getData().get("probateNotificationsGenerated");
        return notifications != null
                ? notifications
                : new ArrayList<>();
    }
}
