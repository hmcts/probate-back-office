package uk.gov.hmcts.probate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

public enum DocumentCaseType {

    @JsonProperty("intestacy")
    INTESTACY("intestacy"),
    @JsonProperty("admonWill")
    ADMON_WILL("admonWill"),
    @JsonProperty("gop")
    GOP("gop");

    public String getCaseType() {
        return caseType;
    }

    private final String caseType;

    DocumentCaseType(String caseType) {
        this.caseType = caseType;
    }

    public static DocumentCaseType getCaseType(String name) {
        return Arrays.stream(DocumentCaseType.values()).filter(caseState -> caseState.getCaseType().equals(name)).findFirst().orElse(GOP);
    }
}
