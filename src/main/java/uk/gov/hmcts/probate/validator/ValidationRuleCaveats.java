package uk.gov.hmcts.probate.validator;

import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;

import java.util.List;

public interface ValidationRuleCaveats {

    List<FieldErrorResponse> validate(CaveatData form);
}
