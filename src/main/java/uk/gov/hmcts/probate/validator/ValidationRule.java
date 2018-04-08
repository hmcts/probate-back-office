package uk.gov.hmcts.probate.validator;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.BusinessValidationError;
import uk.gov.hmcts.probate.model.CCDData;

import java.util.List;
import java.util.Optional;

@Component
public interface ValidationRule {

    String BUSINESS_ERROR = "businessError";

    List<BusinessValidationError> validate(CCDData form);

}
