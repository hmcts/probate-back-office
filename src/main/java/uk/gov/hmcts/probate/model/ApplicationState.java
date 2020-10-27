package uk.gov.hmcts.probate.model;

import lombok.Getter;

@Getter
public enum ApplicationState {

    CASE_STOPPED("BOCaseStopped", "Case stopped"),
    CASE_ESCALATED("BORegistrarEscalation", "Case escalated to registrar"),
    // todo - specify all states
    CASE_NOT_STOPPED_OR_ESCALATED("Other", "Other");

    private final String id;
    private String name;

    ApplicationState(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public static ApplicationState MapCaseState(String caseState) {
        switch (caseState) {
            case "BOCaseStopped":
                return CASE_STOPPED;
            case "BORegistrarEscalation":
                return CASE_ESCALATED;
            default:
                return CASE_NOT_STOPPED_OR_ESCALATED;
        }
    }
}
