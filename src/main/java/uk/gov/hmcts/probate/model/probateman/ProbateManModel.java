package uk.gov.hmcts.probate.model.probateman;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = GrantApplication.class, name = "Grant Application"),
    @JsonSubTypes.Type(value = Caveat.class, name = "Caveat"),
    @JsonSubTypes.Type(value = StandingSearch.class, name = "Standing Search"),
    @JsonSubTypes.Type(value = WillLodgement.class, name = "Will Lodgement")
})
public abstract class ProbateManModel {

    @JsonIgnore
    protected static final String DATE_FORMAT = "yyyy-MM-dd";

    @Column(name = "DNM_IND")
    private String dnmInd; //varchar(1), DO NOT MATCH flag

    @Column(name = "CCD_CASE_NO")
    private String ccdCaseNo; //varchar(20),
}
