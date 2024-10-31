package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.service.FeatureToggleService;
import uk.gov.hmcts.probate.service.MarkdownValidatorService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@Slf4j
public class PersonalisationValidationRule {
    private final Parser markdownParser;

    private final MarkdownValidatorService markdownValidatorService;

    private final FeatureToggleService featureToggleService;

    private final Pattern markdownLinkPattern =
            Pattern.compile("^\\[(.*?)]\\((https?:\\/\\/.*?)\\)$", Pattern.CASE_INSENSITIVE);

    public PersonalisationValidationResult validatePersonalisation(final Map<String, ?> personalisation) {
        final boolean enableMarkdownFiltering = featureToggleService.enableNewMarkdownFiltering();

        final Map<String, String> invalidFields = new HashMap<>();
        final List<String> htmlFields = new ArrayList<>();
        for (final var entry : personalisation.entrySet()) {
            if (entry.getValue() != null) {
                if (enableMarkdownFiltering) {
                    final String key = entry.getKey();
                    final String entryValue = entry.getValue().toString();
                    final Node parsed = markdownParser.parse(entryValue);

                    MarkdownValidatorService.NontextVisitor nontextVisit = markdownValidatorService
                            .getNontextVisitor(key);
                    parsed.accept(nontextVisit);
                    if (nontextVisit.isInvalid()) {
                        invalidFields.put(key, nontextVisit.getWhyInvalid());
                    }
                    if (nontextVisit.isHasHtml()) {
                        htmlFields.add(key);
                    }
                } else {
                    doOldMarkdownFiltering(entry, invalidFields);
                }
            }
        }

        return new PersonalisationValidationResult(
                Collections.unmodifiableMap(invalidFields),
                Collections.unmodifiableList(htmlFields));
    }

    void doOldMarkdownFiltering(Map.Entry<String, ?> entry, Map<String, String> invalidFields) {
        log.debug("Using old markdown filtering");
        String entryValue = entry.getValue().toString();
        int firstIndex = entryValue.indexOf('[');
        int secondIndex = entryValue.indexOf(')');
        if (firstIndex != -1 && secondIndex != -1 && firstIndex < secondIndex) {
            String valueToValidate = entryValue.substring(firstIndex, secondIndex + 1);
            if (!valueToValidate.isEmpty() && markdownLinkPattern.matcher(valueToValidate).find()) {
                invalidFields.put(entry.getKey(), "OLD_LINK");
            }
        }
    }

    public record PersonalisationValidationResult(
        Map<String, String> invalidFields,
        List<String> htmlFields) {
    }
}
