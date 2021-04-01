package uk.gov.hmcts.probate.validator;

import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;

public interface CaveatEmailValidationRule {
    void validate(CaveatDetails caveatDetails);
}
