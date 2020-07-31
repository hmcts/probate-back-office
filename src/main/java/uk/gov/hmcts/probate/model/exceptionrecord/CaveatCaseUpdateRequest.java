package uk.gov.hmcts.probate.model.exceptionrecord;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.caveat.request.ExceptionRecordCaveatDetails;

@Data
public class CaveatCaseUpdateRequest {

    private final ExceptionRecordRequest exceptionRecord;
    private final ExceptionRecordRequest caseUpdateDetails;
    private final ExceptionRecordCaveatDetails caveatDetails;
    private final Boolean isAutomatedProcess;

    public CaveatCaseUpdateRequest(
            @JsonProperty("exception_record") ExceptionRecordRequest exceptionRecord,
            @JsonProperty("case_update_details") ExceptionRecordRequest caseUpdateDetails,
            @JsonProperty("case_details") ExceptionRecordCaveatDetails caveatDetails,
            @JsonProperty("is_automated_process") Boolean isAutomatedProcess
    ) {
        this.exceptionRecord = exceptionRecord;
        this.caseUpdateDetails = caseUpdateDetails;
        this.caveatDetails = caveatDetails;
        this.isAutomatedProcess = isAutomatedProcess;
    }
}
