package uk.gov.hmcts.probate.util.diagrams.plantuml;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlantUmlEvent {
    private final String id;
    private final String name;
    private final String description;
    private final PlantUmlState pre;
    private final PlantUmlState post;
    private final String start;
    private final String about;
    private final String submitted;
    private final boolean showSummary;

    public String getEventId() {
        return this.getId() +"Event";
    }

}
