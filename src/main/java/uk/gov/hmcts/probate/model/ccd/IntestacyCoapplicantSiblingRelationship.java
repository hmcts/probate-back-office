package uk.gov.hmcts.probate.model.ccd;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum IntestacyCoapplicantSiblingRelationship {
    @JsonProperty("sibling")
    SIBLING("sibling"),
    @JsonProperty("nieceOrNephew")
    NIECE_OR_NEPHEW("nieceOrNephew");

    private final String value;

    IntestacyCoapplicantSiblingRelationship(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static IntestacyCoapplicantSiblingRelationship fromValue(String text) {
        for (final var r : IntestacyCoapplicantSiblingRelationship.values()) {
            if (r.value.equals(text)) {
                return r;
            }
        }
        return null; // !?
    }
}
