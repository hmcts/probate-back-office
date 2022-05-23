package uk.gov.hmcts.probate.util.diagrams.plantuml;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class State {
    private final String id;
    private final String name;
    private final String description;
    protected String crud;

    public String getStateId() {
        return this.getId() + "State";
    }

}
