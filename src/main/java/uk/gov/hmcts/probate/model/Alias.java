package uk.gov.hmcts.probate.model;

import lombok.Getter;

import static org.apache.commons.lang3.StringUtils.substringAfterLast;
import static org.apache.commons.lang3.StringUtils.substringBeforeLast;

@Getter
public class Alias {

    private String firstName;
    private String lastName;

    private static final String SPACE = " ";

    public Alias(String combined) {

        String name = combined.trim();
        firstName = substringBeforeLast(name, SPACE).trim();
        lastName = substringAfterLast(name, SPACE).trim();
    }
}
