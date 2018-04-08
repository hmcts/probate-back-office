package uk.gov.hmcts.probate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.BusinessValidationError;
import uk.gov.hmcts.probate.model.CCDData;
import uk.gov.hmcts.probate.validator.ValidationRule;

import java.util.LinkedList;
import java.util.List;

@Component
public class BusinessValidationService {

    private final List<ValidationRule> validationRules;

    @Autowired
    public BusinessValidationService(List<ValidationRule> validationRules) {
        this.validationRules = validationRules;
    }

    public List<BusinessValidationError> validateForm(CCDData form) {
        List<BusinessValidationError> errorList = new LinkedList<>();

        for (ValidationRule rule : validationRules) {
            errorList.addAll(rule.validate(form));
        }

        return errorList;
    }
}
