package uk.gov.hmcts.probate.model.probateman;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

public class RegularCaseNamingStrategy extends PropertyNamingStrategy.PropertyNamingStrategyBase {

    @Override
    public String translate(String input) {
        return Arrays.stream(StringUtils.splitByCharacterTypeCamelCase(input))
            .map(StringUtils::capitalize)
            .collect(Collectors.joining(" "));
    }
}
