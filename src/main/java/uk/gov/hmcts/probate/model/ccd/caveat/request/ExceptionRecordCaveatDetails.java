package uk.gov.hmcts.probate.model.ccd.caveat.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
public class ExceptionRecordCaveatDetails {

    @Valid
    @JsonProperty(value = "case_data")
    private final CaveatData data;

    @JsonProperty(value = "exception_record_case_type_id")
    private final String exceptionRecordCaseTypeId;

    @NotNull
    @JsonProperty(value = "exception_record_id")
    private final Long exceptionRecordId;
}
