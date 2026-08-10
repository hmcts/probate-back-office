package uk.gov.hmcts.probate.model.ccd.raw;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.ccd.sdk.api.CCD;
import uk.gov.hmcts.ccd.sdk.type.FieldType;
import uk.gov.hmcts.probate.model.ccd.raw.request.NotApplyingExecutorReasonFixedList;
import uk.gov.hmcts.ccd.sdk.api.ComplexType;

@ComplexType(name = "solAdditionalExecutor", generate = true)
@Data
@Builder
public class AdditionalExecutor {

    @CCD(label = "First name(s)")
    private final String additionalExecForenames;
    @CCD(label = "Last name(s)")
    private final String additionalExecLastname;
    @CCD(label = "Is this name different to how they are named in the will?", typeOverride = FieldType.YesOrNo)
    private final String additionalExecNameOnWill;
    @CCD(label = "Enter their full name as it appears in the will", showCondition = "additionalExecNameOnWill=\"Yes\"")
    private final String additionalExecAliasNameOnWill;
    @CCD(label = "Are they applying?", typeOverride = FieldType.YesOrNo)
    private final String additionalApplying;
    @CCD(label = "Executor address", showCondition = "additionalApplying=\"Yes\"", typeOverride = FieldType.AddressUK)
    private final SolsAddress additionalExecAddress;
    @CCD(
            label = "Why are they not applying?",
            showCondition = "additionalApplying=\"No\"",
            typeOverride = FieldType.FixedRadioList,
            typeParameterOverride = "notApplyingExecutorReasonFixedList",
            typeParameterClass = NotApplyingExecutorReasonFixedList.class
    )
    private final String additionalExecReasonNotApplying;

}
