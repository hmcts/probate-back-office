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

    private final Pattern MARKDOWN_LINK_PATTERN =
            Pattern.compile("^\\[(.*?)]\\((https?:\\/\\/.*?)\\)$", Pattern.CASE_INSENSITIVE);

    public <T> List<String> validatePersonalisation(Map<String, T> personalisation) {
        List<String> invalidFields = new ArrayList<>();
        for (var entry : personalisation.entrySet()) {
            if (entry.getValue() != null) {
                String entryValue = entry.getValue().toString();
                int firstIndex = entryValue.indexOf('[');
                int secondIndex = entryValue.indexOf(')');
                if (firstIndex != -1 && secondIndex != -1 && firstIndex < secondIndex) {
                    String valueToValidate = entryValue.substring(firstIndex, secondIndex + 1);
                    if (!valueToValidate.isEmpty() && MARKDOWN_LINK_PATTERN.matcher(valueToValidate).matches()) {
                        invalidFields.add(entry.getKey());
                    }
                }
            }
        }
        return invalidFields;
    }
}
