package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class PersonalisationValidationRule {

    private final Pattern regexPattern =
            Pattern.compile("'\\[(.*?)]\\((https?://.*?)\\)$", Pattern.CASE_INSENSITIVE);

    public List<String> validatePersonalisation(Map<String, Object> personalisation) {
        List<String> invalidFields = new ArrayList<>();
        for (var entry : personalisation.entrySet()) {
            if (entry.getValue() != null && regexPattern.matcher(entry.getValue().toString()).matches()) {
                invalidFields.add(entry.getKey());
            }
        }
        return invalidFields;
    }

    public List<String> validateCaveatPersonalisation(Map<String, String> personalisation) {
        List<String> invalidFields = new ArrayList<>();
        for (var entry : personalisation.entrySet()) {
            if (entry.getValue() != null && regexPattern.matcher(entry.getValue()).matches()) {
                invalidFields.add(entry.getKey());
            }
        }
        return invalidFields;
    }
}
