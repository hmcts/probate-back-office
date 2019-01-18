package uk.gov.hmcts.probate.model.probateman;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = GrantApplication.class, name = "Grant Application"),
    @JsonSubTypes.Type(value = Caveat.class, name = "Caveat"),
    @JsonSubTypes.Type(value = StandingSearch.class, name = "Standing Search"),
    @JsonSubTypes.Type(value = WillLodgement.class, name = "Will Lodgement")
})
public abstract class ProbateManModel {

    protected static final String DATE_FORMAT = "yyyy-MM-dd";
}
