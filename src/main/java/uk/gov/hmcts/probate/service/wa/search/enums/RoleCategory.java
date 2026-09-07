package uk.gov.hmcts.probate.service.wa.search.enums;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum RoleCategory {
    JUDICIAL("J"),
    LEGAL_OPERATIONS("L"),
    ADMIN("A"),
    CTSC("C"),
    ENFORCEMENT("E"),
    @JsonEnumDefaultValue UNKNOWN(null);

    private final String abbreviation;

    RoleCategory(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public static Set<String> getAbbreviations(List<RoleCategory> roleCategories) {
        return Stream.ofNullable(roleCategories)
            .flatMap(Collection::stream)
            .map(r -> r.abbreviation)
            .collect(Collectors.toSet());
    }

    public static boolean isAllowed(String value) {
        return Stream.of(RoleCategory.values())
            .anyMatch(category -> category.name().equals(value));
    }
}
