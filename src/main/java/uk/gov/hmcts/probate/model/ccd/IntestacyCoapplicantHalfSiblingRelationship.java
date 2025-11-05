package uk.gov.hmcts.probate.model.ccd;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum IntestacyCoapplicantHalfSiblingRelationship {
    @JsonProperty("sibling")
    SIBLING("sibling"),
    @JsonProperty("nieceOrNephew")
    NIECE_OR_NEPHEW("nieceOrNephew");

    private final String value;

    IntestacyCoapplicantHalfSiblingRelationship(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static IntestacyCoapplicantHalfSiblingRelationship fromValue(String text) {
        for (final var r : IntestacyCoapplicantHalfSiblingRelationship.values()) {
            if (r.value.equals(text)) {
                return r;
            }
        }
        return null; // !?
    }
}
