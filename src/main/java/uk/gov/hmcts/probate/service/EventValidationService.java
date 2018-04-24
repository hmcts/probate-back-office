package uk.gov.hmcts.probate.service;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.validator.ValidationRule;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EventValidationService {

    public List<FieldErrorResponse> validate(CCDData form, List<? extends ValidationRule> rules) {
        return rules.stream()
            .map(rule -> rule.validate(form))
            .flatMap(List::stream)
            .collect(Collectors.toList());
    }
}
