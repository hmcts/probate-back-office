package uk.gov.hmcts.probate.model;

import lombok.Getter;

@Getter
public enum ApplicationState {
    CASE_STOPPED("BOCaseStopped", "Case stopped");

    private final String id;
    private String name;

    ApplicationState(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
