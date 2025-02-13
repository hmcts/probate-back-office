package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.CcdCaseType;
import uk.gov.hmcts.probate.model.ccd.EventId;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.service.ccd.CcdClientApi;

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

    public void disposeGOPCase(final ReturnedCaseDetails caseDetails,
                               final String caseId,
                               final SecurityDTO securityDTO) {
        log.info("DisposalCCDService dispose GOP Case: " + caseId);
        ccdClientApi.updateCaseAsCaseworker(CcdCaseType.GRANT_OF_REPRESENTATION, caseId,
                caseDetails.getLastModified(), caseDetails.getData(), EventId.DISPOSE_CASE, securityDTO,
                DISPOSE_DRAFT_DESCRIPTION, DISPOSE_DRAFT_SUMMARY);
    }

    public void disposeCaveatCase(final ReturnedCaseDetails caseDetails,
                                  final String caseId,
                                  final SecurityDTO securityDTO) {
        log.info("DisposalCCDService dispose Caveat Case: " + caseId);
        ccdClientApi.updateCaseAsCaseworker(CcdCaseType.CAVEAT, caseId,
                caseDetails.getLastModified(), caseDetails.getData(), EventId.DISPOSE_CASE, securityDTO,
                DISPOSE_DRAFT_DESCRIPTION, DISPOSE_DRAFT_SUMMARY);
    }
}
