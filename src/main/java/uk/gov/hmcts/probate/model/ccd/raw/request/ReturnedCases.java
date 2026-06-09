package uk.gov.hmcts.probate.model.ccd.raw.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReturnedCases {

    @JsonProperty(value = "cases")
    private final List<ReturnedCaseDetails> cases;

    @JsonProperty(value = "total")
    private final int total;
}