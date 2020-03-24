package uk.gov.hmcts.probate.model.exceptionrecord;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;

@Data
public class CaveatCaseUpdateRequest {

    private final ExceptionRecordRequest exceptionRecord;
    private final CaveatDetails caveatDetails;

    public CaveatCaseUpdateRequest(
            @JsonProperty("exception_record") ExceptionRecordRequest exceptionRecord,
            @JsonProperty("case_details") CaveatDetails caveatDetails
    ) {
        this.exceptionRecord = exceptionRecord;
        this.caveatDetails = caveatDetails;
    }
}
