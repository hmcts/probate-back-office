package uk.gov.hmcts.probate.model.ccd.raw.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
public class ReturnedCaseDetails {

    @Valid
    @JsonProperty(value = "case_data")
    private final CaseData data;

    private final String[] lastModified;

    @NotNull
    private final Long id;
}
