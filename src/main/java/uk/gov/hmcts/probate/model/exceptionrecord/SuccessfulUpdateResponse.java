package uk.gov.hmcts.probate.model.exceptionrecord;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;

import java.util.List;

@Data
@Builder
public class SuccessfulUpdateResponse {

    @JsonProperty("case_update_details")
    public final ResponseCaseData caseUpdateDetails;

    @JsonProperty("warnings")
    public final List<String> warnings;

    // region constructor
    public SuccessfulUpdateResponse(
            ResponseCaseData caseUpdateDetails,
            List<String> warnings,
            List<String> errors
    ) {
        this.caseUpdateDetails = caseUpdateDetails;
        this.warnings = warnings;
    }
    // endregion
}