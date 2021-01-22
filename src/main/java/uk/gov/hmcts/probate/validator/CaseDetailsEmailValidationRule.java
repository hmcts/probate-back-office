package uk.gov.hmcts.probate.validator;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;

public interface CaseDetailsEmailValidationRule{
    void validate(CaseDetails caseDetails);
}
