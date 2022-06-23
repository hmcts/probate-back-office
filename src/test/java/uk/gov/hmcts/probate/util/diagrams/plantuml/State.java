package uk.gov.hmcts.probate.util.diagrams.plantuml;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
class State implements Cell {
    protected static final String COLOR_STATE = "#4040ff";

    private final String id;
    private final String name;
    private final String description;
    protected String crud;

    public String getStateId() {
        return this.getId() + "State";
    }

    public String getColor() {
        return COLOR_STATE;
    }

}
