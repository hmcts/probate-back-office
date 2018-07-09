package uk.gov.hmcts.probate.transformer;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;


@Component
public class NameParser {

    private static final String SPACE = " ";
    public static final String FIRST_NAMES = "aliasFirstName";
    public static final String SURNAME = "aliasSurname";

    public Map<String, String> parse(String aliasName) {

        if (StringUtils.isBlank(aliasName)) {
            return Collections.emptyMap();
        }
        String name = aliasName.trim();
        String surname = substringAfterLast(name, SPACE);
        String firstNames = substringBeforeLast(name, SPACE);
        return createMapWithFirstNamesAndSurname(firstNames, surname);
    }


    private String substringAfterLast(String str, String separator) {
        return StringUtils.substringAfterLast(str, separator).trim();
    }

    private String substringBeforeLast(String str, String separator) {
        return StringUtils.substringBeforeLast(str, separator).trim();
    }

    private Map<String, String> createMapWithFirstNamesAndSurname(String firstNames,
                                                         String surname) {
        return ImmutableMap.<String, String>builder()
                .put(FIRST_NAMES, firstNames)
                .put(SURNAME, surname).build();

    }

}
