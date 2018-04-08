package uk.gov.hmcts.probate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import javax.validation.Valid;
import java.io.Serializable;

@JsonRootName(value = "ccddata")
public class CCDData implements Serializable {

    @Valid
    private final Deceased deceased;

    @Valid
    private final InheritanceTax iht;

    @JsonCreator
    public CCDData(@JsonProperty("deceased") Deceased deceased,
                   @JsonProperty("iht") InheritanceTax iht) {
        this.deceased = deceased;
        this.iht = iht;
    }

    public Deceased getDeceased() {
        return deceased;
    }

    public InheritanceTax getIht() {
        return iht;
    }
}
