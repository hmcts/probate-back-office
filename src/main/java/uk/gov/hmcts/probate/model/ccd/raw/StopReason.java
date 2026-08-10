package uk.gov.hmcts.probate.model.ccd.raw;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.ccd.sdk.api.CCD;
import uk.gov.hmcts.ccd.sdk.type.FieldType;
import uk.gov.hmcts.probate.model.ccd.raw.request.StopReasonFixedList;
import uk.gov.hmcts.probate.model.ccd.raw.request.DocumentsRequiredSubList;
import uk.gov.hmcts.ccd.sdk.api.ComplexType;


@ComplexType(name = "boCaseStopReason", generate = true)
@Data
@Builder
public class StopReason {

    @CCD(
            label = "Select a reason for stopping the case",
            typeOverride = FieldType.FixedList,
            typeParameterOverride = "stopReasonFixedList",
            typeParameterClass = StopReasonFixedList.class
    )
    @JsonProperty(value = "caseStopReason")
    private final String caseStopReason;

    @CCD(
            label = "Select the document that is required",
            showCondition = "caseStopReason=\"DocumentsRequired\"",
            typeOverride = FieldType.FixedList,
            typeParameterOverride = "DocumentsRequiredSubList",
            typeParameterClass = DocumentsRequiredSubList.class
    )
    @JsonProperty(value = "caseStopSubReasonDocRequired")
    private final String caseStopSubReasonDocRequired;
}
