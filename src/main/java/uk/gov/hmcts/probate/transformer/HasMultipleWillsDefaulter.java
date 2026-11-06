package uk.gov.hmcts.probate.transformer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.service.EvidenceUploadService;

import static uk.gov.hmcts.probate.model.Constants.CASE_TYPE_INTESTACY;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;

@Component
@RequiredArgsConstructor
public class HasMultipleWillsDefaulter {
    private final EvidenceUploadService evidenceUploadService;

    public String defaultHasMultipleWills(CaseData caseData) {
        return CASE_TYPE_INTESTACY.equalsIgnoreCase(caseData.getCaseType())
            ? NO
            : (evidenceUploadService.hasMultipleWills(caseData) ? YES : NO);
        }
}
