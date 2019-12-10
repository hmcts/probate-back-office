package uk.gov.hmcts.probate.model.exceptionrecord;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ExceptionRecordErrorResponse {

    @JsonProperty("warnings")
    public final List<String> warnings;

    @JsonProperty("errors")
    public final List<String> errors;

    public ExceptionRecordErrorResponse(List<String> errors, List<String> warnings) {
        this.errors = errors;
        this.warnings = warnings;
    }
}