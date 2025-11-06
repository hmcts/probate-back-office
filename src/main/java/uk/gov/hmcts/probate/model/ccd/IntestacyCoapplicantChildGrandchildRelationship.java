package uk.gov.hmcts.probate.model.ccd;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum IntestacyCoapplicantChildGrandchildRelationship {
    @JsonProperty("child")
    CHILD("child"),
    @JsonProperty("grandchild")
    GRANDCHILD("grandchild");

    private final String value;

    IntestacyCoapplicantChildGrandchildRelationship(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static IntestacyCoapplicantChildGrandchildRelationship fromValue(String text) {
        for (final var r : IntestacyCoapplicantChildGrandchildRelationship.values()) {
            if (r.value.equals(text)) {
                return r;
            }
        }
        return null; // !?
    }
}
