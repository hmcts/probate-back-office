package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.CcdCaseType;
import uk.gov.hmcts.probate.model.ccd.EventId;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.ccd.CcdClientApi;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DormantCaseReminderService {

    private final CaseQueryService caseQueryService;
    private final SecurityUtils securityUtils;
    private final CcdClientApi ccdClientApi;
    public static final String REMINDER_SUMMARY = "Bulk scan dormant case reminder letter";

    public void sendReminderLetter(String startDate, String endDate) {
        log.info("Fetch cases upto date: {}", endDate);
        List<ReturnedCaseDetails> cases = caseQueryService.findCasesToSendReminder(startDate, endDate);
        log.info("Found {} cases to send reminder letter", cases.size());

        for (ReturnedCaseDetails returnedCaseDetails : cases) {

            GrantOfRepresentationData grantOfRepresentationData = GrantOfRepresentationData.builder()
                    .build();
            log.info("Updating case to set flag in CCD by scheduler for case id : {}", returnedCaseDetails.getId());
            try {
                ccdClientApi.updateCaseAsCaseworker(CcdCaseType.GRANT_OF_REPRESENTATION,
                        returnedCaseDetails.getId().toString(),
                        returnedCaseDetails.getLastModified(),
                        grantOfRepresentationData, EventId.DORMANT_REMINDER,
                        securityUtils.getUserBySchedulerTokenAndServiceSecurityDTO(), REMINDER_SUMMARY,
                        REMINDER_SUMMARY);
                log.info("Updated case to set flag in CCD by scheduler for case id : {}", returnedCaseDetails.getId());
            } catch (Exception e) {
                log.error("BulkScan Dormant reminder case error: Case:{}, cannot be saved to CCD {}",
                        returnedCaseDetails.getId(), e.getMessage());
            }
        }
    }
}