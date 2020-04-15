package uk.gov.hmcts.probate.model.exceptionrecord;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.caveat.request.ExceptionRecordCaveatDetails;

@Data
public class CaveatCaseUpdateRequest {

    private final ExceptionRecordRequest exceptionRecord;
    private final ExceptionRecordCaveatDetails caveatDetails;

    public CaveatCaseUpdateRequest(
            @JsonProperty("exception_record") ExceptionRecordRequest exceptionRecord,
            @JsonProperty("case_details") ExceptionRecordCaveatDetails caveatDetails
    ) {
        this.exceptionRecord = exceptionRecord;
        this.caveatDetails = caveatDetails;
    }
}
