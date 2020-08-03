package uk.gov.hmcts.probate.model.exceptionrecord;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CaseCreationDetails<T> {

    @JsonProperty("exception_record_case_type_id")
    public final String exceptionRecordCaseTypeId;

    @JsonProperty("event_id")
    public final String eventId;

    @JsonProperty("case_data")
    public final T caseData;

    // region constructor
    public CaseCreationDetails(
            String exceptionRecordCaseTypeId,
            String eventId,
            T caseData
    ) {
        this.exceptionRecordCaseTypeId = exceptionRecordCaseTypeId;
        this.eventId = eventId;
        this.caseData = caseData;
    }
    // endregion
}
