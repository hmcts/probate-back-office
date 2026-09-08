package uk.gov.hmcts.probate.model.probateman;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

public class RegularCaseNamingStrategy extends PropertyNamingStrategies.NamingBase {

    @Override
    public String translate(String input) {
        return Arrays.stream(StringUtils.splitByCharacterTypeCamelCase(input))
            .map(StringUtils::capitalize)
            .collect(Collectors.joining(" "));
    }
}
