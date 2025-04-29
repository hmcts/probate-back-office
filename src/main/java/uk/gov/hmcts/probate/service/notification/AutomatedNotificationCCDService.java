package uk.gov.hmcts.probate.service.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.EventId;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.service.ccd.CcdClientApi;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;

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
                               final SecurityDTO securityDTO) {
        log.info("AutomatedNotificationCCDService saveNotification to Case: " + caseId);
        ccdClientApi.updateCaseAsCaseworker(GRANT_OF_REPRESENTATION, caseId,
                caseDetails.getLastModified(), GrantOfRepresentationData.builder().build(),
                EventId.DISPOSE_CASE, securityDTO, EVENT_DESCRIPTION, EVENT_SUMMARY);
    }
}
