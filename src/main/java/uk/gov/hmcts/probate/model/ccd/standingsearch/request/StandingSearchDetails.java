package uk.gov.hmcts.probate.model.ccd.standingsearch.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
public class StandingSearchDetails {

    @Valid
    @JsonProperty(value = "case_data")
    private final StandingSearchData standingSearchData;

    @JsonProperty(value = "last_modified")
    private final String[] lastModified;

    @NotNull
    private final Long id;
}
