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

    public void makeCasesDormant(String date) {
        log.info("Make Dormant for date: {}", date);
        List<ReturnedCaseDetails> cases = caseQueryService.findCaseToBeMadeDormant(date);
        log.info("Found {} cases with dated document for Make Dormant", cases.size());
        for (ReturnedCaseDetails returnedCaseDetails : cases) {
            GrantOfRepresentationData grantOfRepresentationData = GrantOfRepresentationData.builder()
                    .build();
            ccdClientApi.updateCaseAsCaseworker(CcdCaseType.GRANT_OF_REPRESENTATION,
                    returnedCaseDetails.getId().toString(),
                    grantOfRepresentationData, EventId.MAKE_CASE_DORMANT,
                    securityUtils.getUserByAuthTokenAndServiceSecurityDTO(), "", DORMANT_SUMMARY);

        }
    }

    public void reactivateDormantCases(String fromDate, String toDate) {
        log.info("Reactivate Dormant cases for date: {}", fromDate);
        List<ReturnedCaseDetails> cases = caseQueryService.findCaseToBeReactivatedFromDormant(fromDate, fromDate);
        log.info("Found {} cases with dated document for Reactivate Dormant", cases.size());
        for (ReturnedCaseDetails returnedCaseDetails : cases) {
            GrantOfRepresentationData grantOfRepresentationData = GrantOfRepresentationData.builder().evidenceHandled(false)
                    .build();
            ccdClientApi.updateCaseAsCaseworker(CcdCaseType.GRANT_OF_REPRESENTATION,
                    returnedCaseDetails.getId().toString(),
                    grantOfRepresentationData, EventId.REACTIVATE_DORMANT_CASE,
                    securityUtils.getUserByAuthTokenAndServiceSecurityDTO(), "", REACTIVATE_DORMANT_SUMMARY);

        }
    }
}
