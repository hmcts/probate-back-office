package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.CcdCaseType;
import uk.gov.hmcts.probate.model.ccd.EventId;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.ccd.CcdClientApi;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DormantCaseService {
    private final CaseQueryService caseQueryService;
    private final CcdClientApi ccdClientApi;
    private final SecurityUtils securityUtils;

    public void makeCasesDormant(String fromDate, String toDate) {
        log.info("Make Dormant for date: {}", fromDate);
        //TODO: this qry is actually wrong - it should NOT include already dormant cases
        List<ReturnedCaseDetails> cases = caseQueryService.findCaseToBeMadeDormant(fromDate, fromDate);
        log.info("Found {} cases with dated document for Make Dormant", cases.size());
        for (ReturnedCaseDetails returnedCaseDetails : cases) {
            //TODO: we dont need to set the SEH flag here
            GrantOfRepresentationData grantOfRepresentationData = GrantOfRepresentationData.builder()
                    .build();
            ccdClientApi.updateCaseAsCaseworker(CcdCaseType.GRANT_OF_REPRESENTATION,
                    returnedCaseDetails.getId().toString(),
                    grantOfRepresentationData, EventId.MAKE_CASE_DORMANT,
                    securityUtils.getUserAndServiceSecurityDTO());

        }
    }

    public void reactivateDormantCases(String fromDate, String toDate) {
        log.info("Reactivate Dormant cases for date: {}", fromDate);
        List<ReturnedCaseDetails> cases = caseQueryService.findCaseToBeReactivatedFromDormant(fromDate, fromDate);
        log.info("Found {} cases with dated document for Reactivate Dormant", cases.size());
        for (ReturnedCaseDetails returnedCaseDetails : cases) {
            GrantOfRepresentationData grantOfRepresentationData = GrantOfRepresentationData.builder()
                    //TODO: this will actually be handled by the callback of the reactivate event
                    //.evidenceHandled(Boolean.FALSE)
                    .build();
            ccdClientApi.updateCaseAsCaseworker(CcdCaseType.GRANT_OF_REPRESENTATION,
                    returnedCaseDetails.getId().toString(),
                    grantOfRepresentationData, EventId.REACTIVATE_DORMANT_CASE,
                    securityUtils.getUserAndServiceSecurityDTO());

        }
    }
}
