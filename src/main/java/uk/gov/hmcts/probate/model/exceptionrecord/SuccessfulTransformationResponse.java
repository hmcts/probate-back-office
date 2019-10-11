package uk.gov.hmcts.probate.model.exceptionrecord;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SuccessfulTransformationResponse {

    @JsonProperty("case_creation_details")
    public final CaseCreationDetails caseCreationDetails;

    @JsonProperty("warnings")
    public final List<String> warnings;

    @JsonProperty("errors")
    public final List<String> errors;

    // region constructor
    public SuccessfulTransformationResponse(
            CaseCreationDetails caseCreationDetails,
            List<String> warnings,
            List<String> errors
    ) {
        this.caseCreationDetails = caseCreationDetails;
        this.warnings = warnings;
        this.errors = errors;
    }
    // endregion
}