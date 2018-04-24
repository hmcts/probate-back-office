package uk.gov.hmcts.probate.validator;

import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.CCDData;

import java.util.List;

public interface ValidationRule {

    String BUSINESS_ERROR = "businessError";

    List<FieldErrorResponse> validate(CCDData form);

}
