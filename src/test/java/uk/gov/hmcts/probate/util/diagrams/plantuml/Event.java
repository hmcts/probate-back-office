package uk.gov.hmcts.probate.util.diagrams.plantuml;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Event {
    private final String id;
    private final String name;
    private final String description;
    private final State pre;
    private final State post;
    private final String start;
    private final String about;
    private final String submitted;
    private final boolean showSummary;
    protected String crud;

    public String getEventId() {
        return this.getId() +"Event";
    }

}
