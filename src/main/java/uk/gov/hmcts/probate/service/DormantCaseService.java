package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.CcdCaseType;
import uk.gov.hmcts.probate.model.ccd.EventId;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.ccd.CcdClientApi;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;

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
    private final CaseQueryService caseQueryService;
    private final CcdClientApi ccdClientApi;
    private final SecurityUtils securityUtils;
    @Value("${make_dormant.add_time_minutes}")
    private int makeDormantAddTimeMinutes;
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public void makeCasesDormant(String dormancyStartDate, String endDate) {
        log.info("Make Dormant upto date: {}", endDate);
        List<ReturnedCaseDetails> cases = caseQueryService.findCaseToBeMadeDormant(dormancyStartDate, endDate);
        log.info("Found {} cases with dated document for Make Dormant", cases.size());
        for (ReturnedCaseDetails returnedCaseDetails : cases) {
            GrantOfRepresentationData grantOfRepresentationData = GrantOfRepresentationData.builder()
                        .moveToDormantDateTime(LocalDateTime.now(ZoneOffset.UTC)
                        .plusMinutes(makeDormantAddTimeMinutes))
                        .lastModifiedDateForDormant(LocalDateTime.now(ZoneOffset.UTC))
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
            List<ReturnedCaseDetails> cases = caseQueryService.findCaseToBeReactivatedFromDormant(date);
            log.info("Found {} cases with dated document for Reactivate Dormant", cases.size());
            for (ReturnedCaseDetails returnedCaseDetails : cases) {
                log.info("MoveToDormantDateTime before {} ", returnedCaseDetails.getData().getMoveToDormantDateTime());
                if (StringUtils.isNotBlank(returnedCaseDetails.getData().getMoveToDormantDateTime())) {
                    LocalDateTime moveToDormantDateTime = LocalDateTime.parse(returnedCaseDetails.getData()
                            .getMoveToDormantDateTime(), DATE_FORMAT);
                    if (returnedCaseDetails.getData().getLastModifiedDateForDormant().isAfter(moveToDormantDateTime)) {
                        GrantOfRepresentationData grantOfRepresentationData = GrantOfRepresentationData.builder()
                                .evidenceHandled(false)
                                .lastModifiedDateForDormant(LocalDateTime.now(ZoneOffset.UTC))
                                .build();
                        log.info("Updating case to Stopped from Dormant in CCD by scheduler for case id : {}",
                                returnedCaseDetails.getId());
                        updateCaseAsCaseworker(returnedCaseDetails.getId().toString(), grantOfRepresentationData,
                            returnedCaseDetails.getLastModified());
                    }
                }
            }
            log.info("End of the reactivateDormantCases method");
        } catch (Exception e) {
            log.error("Reactivate Dormant method error {}", e.getMessage());
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
}
