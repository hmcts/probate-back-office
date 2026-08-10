package uk.gov.hmcts.probate.model.ccd.raw;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.ccd.sdk.api.CCD;
import uk.gov.hmcts.ccd.sdk.type.FieldType;
import uk.gov.hmcts.probate.model.ccd.raw.request.ExecutorTypeList;
import uk.gov.hmcts.probate.model.ccd.raw.request.AliasReasonList;
import uk.gov.hmcts.ccd.sdk.api.ComplexType;

@ComplexType(name = "ExecutorApplying", generate = true)
@Data
@Builder
@AllArgsConstructor
public class AdditionalExecutorApplying {

    @CCD(label = "First name(s) of executor")
    private final String applyingExecutorFirstName;
    @CCD(label = "Last name(s) of executor")
    private final String applyingExecutorLastName;
    @CCD(
            label = "Name of their position within the trust corporation as per the resolution",
            showCondition = "applyingExecutorType=\"TrustCorporation\""
    )
    private final String applyingExecutorTrustCorpPosition;
    @CCD(
            label = "Executor type",
            typeOverride = FieldType.FixedList,
            typeParameterOverride = "executorTypeList",
            typeParameterClass = ExecutorTypeList.class
    )
    private final String applyingExecutorType;
    @CCD(label = "Executor phone number")
    private final String applyingExecutorPhoneNumber;
    @CCD(
            label = "Executor email",
            regex = "[a-zA-Z0-9#$%'+=?^_`{|}~-]{1,30}(?:\\.[^.\\n]{1,30}){0,30}@[a-zA-Z0-9](?:[a-zA-Z0-9-.]{0,30}[a-zA-Z0-9])?\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,10}[a-zA-Z0-9])?"
    )
    private final String applyingExecutorEmail;
    @CCD(label = "Executor address", typeOverride = FieldType.AddressUK)
    private SolsAddress applyingExecutorAddress;
    @CCD(label = "Executor name")
    private String applyingExecutorName;
    @CCD(label = "Has other name?", typeOverride = FieldType.YesOrNo)
    private String applyingExecutorHasOtherName;
    @CCD(label = "Executor other names")
    private String applyingExecutorOtherNames;
    @CCD(
            label = "Reason for executor other names",
            typeOverride = FieldType.FixedList,
            typeParameterOverride = "aliasReasonList",
            typeParameterClass = AliasReasonList.class
    )
    private String applyingExecutorOtherNamesReason;
    @CCD(
            label = "Other reason for executor name difference",
            showCondition = "applyingExecutorOtherNamesReason=\"other\""
    )
    private String applyingExecutorOtherReason;
    @CCD(label = "Has agreed?", typeOverride = FieldType.YesOrNo)
    private final String applyingExecutorAgreed;
    @CCD(label = "Executor invitation id")
    private final String applyingExecutorInvitationId;

  // ==== ccd-definition-converter: synthesised definition-only fields (retrofit) ====
  @CCD(label = "Executor postcode")
  private String applyingExecutorPostCode;
  @CCD(label = "Executor lead name")
  private String applyingExecutorLeadName;
  @CCD(label = "Is applicant?")
  private uk.gov.hmcts.ccd.sdk.type.YesOrNo applyingExecutorApplicant;
  @CCD(label = "Has email changed?")
  private uk.gov.hmcts.ccd.sdk.type.YesOrNo applyingExecutorEmailChanged;
  @CCD(label = "Has email sent?")
  private uk.gov.hmcts.ccd.sdk.type.YesOrNo applyingExecutorEmailSent;
  // ==== end synthesised definition-only fields ====
}
