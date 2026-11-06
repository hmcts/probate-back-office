package uk.gov.hmcts.probate.service.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.service.EvidenceUploadService;

import static uk.gov.hmcts.probate.model.Constants.CASE_TYPE_INTESTACY;

@Component
@RequiredArgsConstructor
public class FinalWillValidationRule {

    private final EvidenceUploadService evidenceUploadService;

    public void validate(CallbackRequest request) {
        evidenceUploadService.validateIssueGrantDocuments(request);
        CaseData caseData = request.getCaseDetails().getData();
        if (!CASE_TYPE_INTESTACY.equalsIgnoreCase(caseData.getCaseType())) {
            evidenceUploadService.validateFinalWillIndicator(request);
        }
    }
}
