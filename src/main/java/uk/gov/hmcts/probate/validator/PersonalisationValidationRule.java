package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.service.MarkdownValidatorService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PersonalisationValidationRule {

    private final Parser markdownParser;

    private final MarkdownValidatorService markdownValidatorService;

    public <T> PersonalisationValidationResult validatePersonalisation(final Map<String, T> personalisation) {
        final Map<String, String> invalidFields = new HashMap<>();
        final List<String> htmlFields = new ArrayList<>();
        for (final var entry : personalisation.entrySet()) {
            if (entry.getValue() != null) {
                final String key = entry.getKey();
                final String entryValue = entry.getValue().toString();
                final Node parsed = markdownParser.parse(entryValue);

                MarkdownValidatorService.NontextVisitor nontextVisit = markdownValidatorService.getNontextVisitor(key);
                parsed.accept(nontextVisit);
                if (nontextVisit.isInvalid()) {
                    invalidFields.put(key, nontextVisit.getWhyInvalid());
                }
                if (nontextVisit.isHasHtml()) {
                    htmlFields.add(key);
                }
            }
        }

        return new PersonalisationValidationResult(
                Collections.unmodifiableMap(invalidFields),
                Collections.unmodifiableList(htmlFields));
    }

    public record PersonalisationValidationResult(
        Map<String, String> invalidFields,
        List<String> htmlFields) {
    }
}
