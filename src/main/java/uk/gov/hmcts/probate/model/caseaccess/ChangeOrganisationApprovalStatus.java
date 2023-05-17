package uk.gov.hmcts.probate.model.caseaccess;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ChangeOrganisationApprovalStatus {

    PENDING("0"),
    APPROVED("1"),
    REJECTED("2");

    String value;

    ChangeOrganisationApprovalStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
