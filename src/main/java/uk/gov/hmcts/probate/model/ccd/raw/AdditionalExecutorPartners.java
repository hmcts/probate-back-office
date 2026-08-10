package uk.gov.hmcts.probate.model.ccd.raw;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.ccd.sdk.api.CCD;
import uk.gov.hmcts.ccd.sdk.type.FieldType;
import uk.gov.hmcts.ccd.sdk.api.ComplexType;

@ComplexType(name = "OtherPartnerExecutorApplying", generate = true)
@Data
@Builder
@AllArgsConstructor
public class AdditionalExecutorPartners {

    @CCD(label = "First name(s)")
    private final String additionalExecForenames;
    @CCD(label = "Last name(s)")
    private final String additionalExecLastname;
    @CCD(label = "What is their address?", typeOverride = FieldType.AddressUK)
    private final SolsAddress additionalExecAddress;

}
