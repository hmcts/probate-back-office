package uk.gov.hmcts.probate.validator;

import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;

public interface CaseDetailsValidationRule {

    void validate(CaseDetails caseDetails);
}
