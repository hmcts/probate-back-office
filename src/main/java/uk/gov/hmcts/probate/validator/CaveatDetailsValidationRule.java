package uk.gov.hmcts.probate.validator;

import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;

public interface CaveatDetailsValidationRule {

    void validate(CaveatDetails caveatDetails);
}
