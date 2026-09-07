package uk.gov.hmcts.probate.service.wa.search.enums;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.stream;

public enum CFTTaskState {

    UNCONFIGURED("UNCONFIGURED", "UCNF"),
    PENDING_AUTO_ASSIGN("PENDING_AUTO_ASSIGN", "PA"),
    ASSIGNED("ASSIGNED", "A"),
    CONFIGURED("CONFIGURED", "CNF"),
    UNASSIGNED("UNASSIGNED", "U"),
    COMPLETED("COMPLETED", "C"),
    CANCELLED("CANCELLED", "CAN"),
    TERMINATED("TERMINATED", "T"),
    PENDING_RECONFIGURATION("PENDING_RECONFIGURATION", "PR");

    private String value;

    private String abbreviation;


    CFTTaskState(String value, String abbreviation) {
        this.value = value;
        this.abbreviation = abbreviation;
    }

    public static Optional<CFTTaskState> from(
        String value
    ) {
        return stream(values())
            .filter(v -> v.getValue().equalsIgnoreCase(value))
            .findFirst();
    }

    public String getValue() {
        return value;
    }

    public static Set<String> getAbbreviations(List<CFTTaskState> states) {
        return Stream.ofNullable(states)
            .flatMap(Collection::stream)
            .map(s -> s.abbreviation)
            .collect(Collectors.toSet());
    }

}
