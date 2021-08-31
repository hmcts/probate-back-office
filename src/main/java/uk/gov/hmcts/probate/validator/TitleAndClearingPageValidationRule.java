package uk.gov.hmcts.probate.validator;

import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;

public interface TitleAndClearingPageValidationRule {
    void validate(CaseDetails caseDetails);
}
