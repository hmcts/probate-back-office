package uk.gov.hmcts.probate.model.ccd;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.ccd.sdk.api.CCD;
import uk.gov.hmcts.ccd.sdk.type.FieldType;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReissueReasonFixedList;
import uk.gov.hmcts.ccd.sdk.api.ComplexType;

@ComplexType(name = "ReissueExamineCase", generate = true)
@Data
@Builder
public class Reissue {

    @CCD(
            label = "Select a reason for reissuing the case",
            typeOverride = FieldType.FixedList,
            typeParameterOverride = "reissueReasonFixedList",
            typeParameterClass = ReissueReasonFixedList.class
    )
    private final String reissueReason;
    @CCD(label = "Describe the details of the reason", typeOverride = FieldType.TextArea)
    private final String reissueReasonDetails;
}
