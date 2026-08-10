package uk.gov.hmcts.probate.model.ccd.raw;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import uk.gov.hmcts.ccd.sdk.api.CCD;
import uk.gov.hmcts.ccd.sdk.type.FieldType;
import uk.gov.hmcts.probate.model.ccd.raw.request.NotApplyingExecutorReasonFixedList;
import uk.gov.hmcts.ccd.sdk.api.ComplexType;

@ComplexType(name = "ExecutorNotApplying", generate = true)
@Data
@Builder
public class AdditionalExecutorNotApplying {

    @CCD(label = "Executor name")
    private final String notApplyingExecutorName;
    @CCD(label = "Executor name on Will")
    private final String notApplyingExecutorNameOnWill;
    @CCD(label = "Reason for name difference")
    private final String notApplyingExecutorNameDifferenceComment;
    @CCD(
            label = "Reason executor is not applying",
            typeOverride = FieldType.FixedList,
            typeParameterOverride = "notApplyingExecutorReasonFixedList",
            typeParameterClass = NotApplyingExecutorReasonFixedList.class
    )
    private final String notApplyingExecutorReason;
    @CCD(label = "Executor notified", typeOverride = FieldType.YesOrNo)
    private final String notApplyingExecutorNotified;
    @CCD(
            label = "Is a dispense with notice required?",
            showCondition = "notApplyingExecutorReason=\"PowerReserved\"",
            typeOverride = FieldType.YesOrNo
    )
    private final String notApplyingExecutorDispenseWithNotice;
    @CCD(
            label = "Has leave already been given to dispense with notice?",
            showCondition = "notApplyingExecutorDispenseWithNotice=\"Yes\"",
            typeOverride = FieldType.YesOrNo
    )
    private final String notApplyingExecutorDispenseWithNoticeLeaveGiven;
    @CCD(
            label = "On what date was leave given to dispense with notice?",
            showCondition = "notApplyingExecutorDispenseWithNoticeLeaveGiven=\"Yes\""
    )
    private final LocalDate notApplyingExecutorDispenseWithNoticeLeaveGivenDate;

    public boolean hasName(String name) {
        return null != name && (name.equalsIgnoreCase(notApplyingExecutorName)
            || name.equalsIgnoreCase(notApplyingExecutorNameOnWill));
    }

  // ==== ccd-definition-converter: synthesised definition-only fields (retrofit) ====
  @CCD(label = "Executor is dead?")
  private uk.gov.hmcts.ccd.sdk.type.YesOrNo notApplyingExecutorIsDead;
  @CCD(label = "Executor died before deceased?")
  private uk.gov.hmcts.ccd.sdk.type.YesOrNo notApplyingExecutorDiedBefore;
  // ==== end synthesised definition-only fields ====
}
