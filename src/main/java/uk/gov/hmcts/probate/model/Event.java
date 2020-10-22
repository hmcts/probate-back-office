package uk.gov.hmcts.probate.model;

import lombok.Getter;

@Getter
public enum Event {
    SOLICITOR_CREATE_APPLICATION("solicitorCreateApplication", "Apply for probate");

    private final String id;
    private String name;

    Event(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
