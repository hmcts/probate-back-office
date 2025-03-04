package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.EventId;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.service.ccd.CcdClientApi;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.probate.model.cases.caveat.CaveatData;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;

import static uk.gov.hmcts.probate.model.ccd.CcdCaseType.CAVEAT;
import static uk.gov.hmcts.probate.model.ccd.CcdCaseType.GRANT_OF_REPRESENTATION;

@Slf4j
@Service
@RequiredArgsConstructor
public class DisposalCCDService {
    private CcdClientApi ccdClientApi;
    public static final String DISPOSE_DRAFT_DESCRIPTION = "Dispose inactive draft";
    public static final String DISPOSE_DRAFT_SUMMARY = "Move inactive draft to Disposed Draft state";


    @Autowired
    public DisposalCCDService(final CcdClientApi ccdClientApi) {
        this.ccdClientApi = ccdClientApi;
    }

    public void disposeGOPCase(final CaseDetails caseDetails,
                               final String caseId,
                               final SecurityDTO securityDTO) {
        log.info("DisposalCCDService dispose GOP Case: " + caseId);
        ccdClientApi.updateCaseAsCaseworker(GRANT_OF_REPRESENTATION, caseId,
                caseDetails.getLastModified(), GrantOfRepresentationData.builder().build(),
                EventId.DISPOSE_CASE, securityDTO, DISPOSE_DRAFT_DESCRIPTION, DISPOSE_DRAFT_SUMMARY);
    }

    public void disposeCaveatCase(final CaseDetails caseDetails,
                                  final String caseId,
                                  final SecurityDTO securityDTO) {
        log.info("DisposalCCDService dispose Caveat Case: " + caseId);
        ccdClientApi.updateCaseAsCaseworker(CAVEAT, caseId,
                caseDetails.getLastModified(), CaveatData.builder().build(), EventId.DISPOSE_CASE, securityDTO,
                DISPOSE_DRAFT_DESCRIPTION, DISPOSE_DRAFT_SUMMARY);
    }
}
