package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.Constants;
import uk.gov.hmcts.probate.model.ccd.CcdCaseType;
import uk.gov.hmcts.probate.model.ccd.EventId;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.ccd.CcdClientApi;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class DormantCaseService {

    public static final String DORMANT_SUMMARY = "This case has been moved to "
            + "the dormant state due to no action or event on the case for 6 months";
    public static final String REACTIVATE_DORMANT_SUMMARY = "Case-reactivated due to new evidence received";
    public static final String DORMANT_NOTIFICATION_SUMMARY = "Warning notification sent as no action is performed " +
            "even after 3 months of case being in Dormant";
    public static final String WITHDRAW_DORMANT_SUMMARY = "Case withdrawn as no action after six months of case in Dormant";
    private final CaseQueryService caseQueryService;
    private final CcdClientApi ccdClientApi;
    private final SecurityUtils securityUtils;
    @Value("${make_dormant.add_time_minutes}")
    private int makeDormantAddTimeMinutes;
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    public static final DateTimeFormatter LOCAL_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void makeCasesDormant(String dormancyStartDate, String endDate) {
        log.info("Make Dormant upto date: {}", endDate);
        List<ReturnedCaseDetails> cases = caseQueryService.findCaseToBeMadeDormant(dormancyStartDate, endDate);
        log.info("Found {} cases with dated document for Make Dormant", cases.size());
        for (ReturnedCaseDetails returnedCaseDetails : cases) {
            GrantOfRepresentationData grantOfRepresentationData = GrantOfRepresentationData.builder()
                        .moveToDormantDateTime(LocalDateTime.now(ZoneOffset.UTC)
                        .plusMinutes(makeDormantAddTimeMinutes))
                        .build();
            log.info("Updating case to Dormant in CCD by scheduler for case id : {}",returnedCaseDetails.getId());
            try {
                ccdClientApi.updateCaseAsCaseworker(CcdCaseType.GRANT_OF_REPRESENTATION,
                            returnedCaseDetails.getId().toString(),
                            returnedCaseDetails.getLastModified(),
                            grantOfRepresentationData, EventId.MAKE_CASE_DORMANT,
                            securityUtils.getUserBySchedulerTokenAndServiceSecurityDTO(), DORMANT_SUMMARY,
                        DORMANT_SUMMARY);
                log.info("Updated case to Dormant in CCD by scheduler for case id : {}", returnedCaseDetails.getId());
            } catch (Exception e) {
                log.error("Dormant case error: Case:{}, cannot be moved in Dormant state {}",
                        returnedCaseDetails.getId(),e.getMessage());
            }
        }
    }

    public void reactivateDormantCases(String date) {
        try {
            log.info("Reactivate Dormant cases for date: {}", date);
            LocalDate reactivateDate = LocalDate.parse(date, LOCAL_DATE_FORMAT);
            log.info("reactivateDate {}",reactivateDate);
            List<ReturnedCaseDetails> cases = caseQueryService.findCaseToBeReactivatedFromDormant(date);
            log.info("Found {} cases with dated document for Reactivate Dormant", cases.size());
            for (ReturnedCaseDetails returnedCaseDetails : cases) {
                log.info("MoveToDormantDateTime before {} ", returnedCaseDetails.getData().getMoveToDormantDateTime());
                if (StringUtils.isNotBlank(returnedCaseDetails.getData().getMoveToDormantDateTime())) {
                    LocalDateTime moveToDormantDateTime = LocalDateTime.parse(returnedCaseDetails.getData()
                            .getMoveToDormantDateTime(), DATE_FORMAT);
                    if (returnedCaseDetails.getLastModified().isAfter(moveToDormantDateTime) &&
                            Constants.NO.equals(returnedCaseDetails.getData().getDormantNotificationSent())) {
                        GrantOfRepresentationData grantOfRepresentationData = GrantOfRepresentationData.builder()
                                .evidenceHandled(false)
                                .build();
                        log.info("Updating case to Stopped from Dormant in CCD by scheduler for case id : {}",
                                returnedCaseDetails.getId());
                        updateCaseAsCaseworker(returnedCaseDetails.getId().toString(), grantOfRepresentationData,
                            returnedCaseDetails.getLastModified());
                    }
                    else {
                        log.info("Cannot be moved state to CaseStopped for case id : {}",
                                returnedCaseDetails.getId());
                    }
                }
                else {
                    log.info("No MoveToDormantDateTime present for case id : {}", returnedCaseDetails.getId());
                }
            }
            log.info("End of the reactivateDormantCases method");
        } catch (Exception e) {
            log.error("Reactivate Dormant method error {}", e.getMessage());
        }
    }

    public void dormantNotificationCases(String date) {
        try {
            log.info("send Dormant notification for cases dated: {}", date);
            LocalDate notificationDate = LocalDate.parse(date, LOCAL_DATE_FORMAT);
            log.info("reactivateDate {}",notificationDate);
            List<ReturnedCaseDetails> cases = caseQueryService.findCaseToBeReactivatedFromDormant(date);
            log.info("Found {} cases with dated document for sending Dormant notification", cases.size());
            for (ReturnedCaseDetails returnedCaseDetails : cases) {
                log.info("MoveToDormantDateTime before {} ", returnedCaseDetails.getData().getMoveToDormantDateTime());
                if (StringUtils.isNotBlank(returnedCaseDetails.getData().getMoveToDormantDateTime())) {
                    LocalDateTime moveToDormantDateTime = LocalDateTime.parse(returnedCaseDetails.getData()
                            .getMoveToDormantDateTime(), DATE_FORMAT);
                    if (notificationDate.equals(moveToDormantDateTime.toLocalDate())) {
                        log.info("second Notification 3+ months");
                        GrantOfRepresentationData grantOfRepresentationData = GrantOfRepresentationData.builder()
                                .dormantNotificationSent(true)
                                .build();
                        updateCaseDormantAsCaseworker(returnedCaseDetails.getId().toString(), grantOfRepresentationData,
                                returnedCaseDetails.getLastModified());
                    }
                    else {
                        log.info("Cannot send notification for case id : {}", returnedCaseDetails.getId());
                    }
                }
                else {
                    log.info("No MoveToDormantDateTime present for case id : {}", returnedCaseDetails.getId());
                }
            }
            log.info("End of the dormantNotificationCases method");
        } catch (Exception e) {
            log.error("Dormant Notification method error {}", e.getMessage());
        }
    }

    public void withdrawDormantNotificationCases(String date) {
        try {
            log.info("Withdraw Dormant cases for date: {}", date);
            LocalDate withdrawDate = LocalDate.parse(date, LOCAL_DATE_FORMAT);
            log.info("WithdrawDormant date {}",withdrawDate);
            List<ReturnedCaseDetails> cases = caseQueryService.findCaseToBeReactivatedFromDormant(date);
            log.info("Found {} cases with dated document for withdraw case", cases.size());
            for (ReturnedCaseDetails returnedCaseDetails : cases) {
                log.info("MoveToDormantDateTime before {} ", returnedCaseDetails.getData().getMoveToDormantDateTime());
                if (StringUtils.isNotBlank(returnedCaseDetails.getData().getMoveToDormantDateTime())) {
                    LocalDateTime moveToDormantDateTime = LocalDateTime.parse(returnedCaseDetails.getData()
                            .getMoveToDormantDateTime(), DATE_FORMAT);
                    if (withdrawDate.equals(moveToDormantDateTime.toLocalDate())) {
                        GrantOfRepresentationData grantOfRepresentationData = GrantOfRepresentationData.builder()
                                .build();
                        updateCaseWithdrawnAsCaseworker(returnedCaseDetails.getId().toString(), grantOfRepresentationData,
                                returnedCaseDetails.getLastModified());
                    }
                    else {
                        log.info("Cannot withdraw Dormant for case id : {}", returnedCaseDetails.getId());
                    }
                }
                else {
                    log.info("No MoveToDormantDateTime present for case id : {}", returnedCaseDetails.getId());
                }
            }
            log.info("End of the withdrawDormantNotificationCases method");
        } catch (Exception e) {
            log.error("Dormant withdrawn method error {}", e.getMessage());
        }
    }

    private void updateCaseAsCaseworker(String caseId, GrantOfRepresentationData grantOfRepresentationData,
                                        LocalDateTime lastModifiedDate) {
        try {
            ccdClientApi.updateCaseAsCaseworker(CcdCaseType.GRANT_OF_REPRESENTATION, caseId,
                lastModifiedDate, grantOfRepresentationData, EventId.REACTIVATE_DORMANT_CASE,
                    securityUtils.getUserBySchedulerTokenAndServiceSecurityDTO(),
                    REACTIVATE_DORMANT_SUMMARY, REACTIVATE_DORMANT_SUMMARY);
            log.info("Updated case to Stopped from Dormant in CCD by scheduler for case id : {}", caseId);
        } catch (Exception e) {
            log.error("Dormant case error: Case:{} ,cannot be reactivated from Dormant state {}", caseId,
                    e.getMessage());
        }
    }

    private void updateCaseDormantAsCaseworker(String caseId, GrantOfRepresentationData grantOfRepresentationData,
                                        LocalDateTime lastModifiedDate) {
        try {
            ccdClientApi.updateCaseAsCaseworker(CcdCaseType.GRANT_OF_REPRESENTATION, caseId,
                    lastModifiedDate, grantOfRepresentationData, EventId.STILL_DORMANT,
                    securityUtils.getUserBySchedulerTokenAndServiceSecurityDTO(),
                    DORMANT_NOTIFICATION_SUMMARY, DORMANT_NOTIFICATION_SUMMARY);
            log.info("sent follow up notification for Dormant in CCD by scheduler for case id : {}", caseId);
        } catch (Exception e) {
            log.error("Dormant case error: Case:{} ,cannot be reactivated from Dormant state {}", caseId,
                    e.getMessage());
        }
    }

    private void updateCaseWithdrawnAsCaseworker(String caseId, GrantOfRepresentationData grantOfRepresentationData,
                                        LocalDateTime lastModifiedDate) {
        try {
            ccdClientApi.updateCaseAsCaseworker(CcdCaseType.GRANT_OF_REPRESENTATION, caseId,
                    lastModifiedDate, grantOfRepresentationData, EventId.WITHDRAWN,
                    securityUtils.getUserBySchedulerTokenAndServiceSecurityDTO(),
                    WITHDRAW_DORMANT_SUMMARY, WITHDRAW_DORMANT_SUMMARY);
            log.info("Updated case to withdraw from Dormant in CCD by scheduler for case id : {}", caseId);
        } catch (Exception e) {
            log.error("Dormant case error: Case:{} ,cannot be withdrawn from Dormant state {}", caseId,
                    e.getMessage());
        }
    }
}
