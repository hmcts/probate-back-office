package uk.gov.hmcts.probate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public enum DocumentIssueType {

    @JsonProperty("reissue")
    REISSUE("reissue"),

    @JsonProperty("grant")
    GRANT("grant");


    private final String issueType;

    DocumentIssueType(String issueName) {
        this.issueType = issueName;
    }
}
