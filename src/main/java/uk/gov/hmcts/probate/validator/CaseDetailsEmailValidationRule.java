package uk.gov.hmcts.probate.validator;

import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;

import java.util.List;

public interface CaseDetailsEmailValidationRule {

    List<FieldErrorResponse> validate(CaseDetails caseDetails);
}
