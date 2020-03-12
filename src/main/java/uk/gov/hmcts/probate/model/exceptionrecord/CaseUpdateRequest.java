package uk.gov.hmcts.probate.model.exceptionrecord;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;

@Data
public class CaseUpdateRequest {

    private final ExceptionRecordRequest exceptionRecord;
    private final CaseDetails caseDetails;

    public CaseUpdateRequest(
            @JsonProperty("exception_record") ExceptionRecordRequest exceptionRecord,
            @JsonProperty("case_details") CaseDetails caseDetails
    ) {
        this.exceptionRecord = exceptionRecord;
        this.caseDetails = caseDetails;
    }
}
