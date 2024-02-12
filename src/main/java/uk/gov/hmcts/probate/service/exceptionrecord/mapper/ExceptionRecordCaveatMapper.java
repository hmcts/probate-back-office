package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToApplicationTypeCaveat;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToCaveatorAddress;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToDeceasedAddress;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToDefaultLocalDate;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToProbateFee;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToProbateFeeNotIncludedReason;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToYesOrNo;
import uk.gov.hmcts.reform.probate.model.cases.ApplicationType;
import uk.gov.hmcts.reform.probate.model.cases.SolsPaymentMethods;
import uk.gov.hmcts.reform.probate.model.cases.caveat.CaveatData;

@Mapper(componentModel = "spring",
    imports = {StringUtils.class, ApplicationType.class},
    uses = {ApplicationTypeMapper.class,
        OCRFieldAddressMapper.class,
        OCRFieldDefaultLocalDateFieldMapper.class,
        OCRFieldProbateFeeMapper.class,
        OCRFieldProbateFeeNotIncludedReasonMapper.class,
        OCRFieldYesOrNoMapper.class},
    unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ExceptionRecordCaveatMapper {
    @Mapping(target = "caveatorForenames", source = "caveatorForenames")
    @Mapping(target = "caveatorSurname", source = "caveatorSurnames")
    @Mapping(target = "caveatorEmailAddress", source = "caveatorEmailAddress")
    @Mapping(target = "caveatorAddress", source = "ocrFields", qualifiedBy = {ToCaveatorAddress.class})
    @Mapping(target = "deceasedForenames", source = "deceasedForenames")
    @Mapping(target = "deceasedSurname", source = "deceasedSurname")
    @Mapping(target = "deceasedAddress", source = "ocrFields", qualifiedBy = {ToDeceasedAddress.class})
    @Mapping(target = "deceasedDateOfDeath", expression = "java(OCRFieldDefaultLocalDateFieldMapper"
            + ".toDefaultDateFieldMember(new String(\"deceasedDateOfDeath\"), ocrFields.getDeceasedDateOfDeath()))",
            qualifiedBy = {ToDefaultLocalDate.class})
    @Mapping(target = "deceasedAnyOtherNames", expression = "java(OCRFieldYesOrNoMapper.toYesOrNo(new String("
            + "\"deceasedAnyOtherNames\"), ocrFields.getDeceasedAnyOtherNames()))", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "solsSolicitorRepresentativeName", source = "solsSolicitorRepresentativeName")
    @Mapping(target = "dxNumber", source = "dxNumber")
    @Mapping(target = "practitionerAcceptsServiceByEmail", expression = "java(OCRFieldYesOrNoMapper.toYesOrNo("
            + "new String(\"practitionerAcceptsServiceByEmail\"), "
            + "ocrFields.getPractitionerAcceptsServiceByEmail()))", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "solsSolicitorFirmName", source = "solsSolicitorFirmName")
    @Mapping(target = "solsSolicitorAppReference", source = "solsSolicitorAppReference")
    @Mapping(target = "solsFeeAccountNumber", source = "solsFeeAccountNumber")
    @Mapping(target = "solsPaymentMethods", ignore = true)
    @Mapping(target = "languagePreferenceWelsh", expression = "java(OCRFieldYesOrNoMapper.toYesOrNo(new String("
            + "\"bilingualCorrespondenceRequested\"), ocrFields.getBilingualCorrespondenceRequested()))",
            qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "solsSolicitorPhoneNumber", source = "solsSolicitorPhoneNumber")
    @Mapping(target = "caveatorPhoneNumber", source = "caveatorPhoneNumber")
    @Mapping(target = "probateFee", source = "probateFee", qualifiedBy = {ToProbateFee.class})
    @Mapping(target = "probateFeeNotIncludedReason", source = "probateFeeNotIncludedReason",
            qualifiedBy = {ToProbateFeeNotIncludedReason.class})
    @Mapping(target = "helpWithFeesReference", source = "helpWithFeesReference")
    @Mapping(target = "probateFeeNotIncludedExplanation", source = "probateFeeNotIncludedExplanation")
    @Mapping(target = "probateFeeAccountNumber", source = "probateFeeAccountNumber")
    @Mapping(target = "probateFeeAccountReference", source = "probateFeeAccountReference")
    @Mapping(target = "caveatRaisedEmailNotificationRequested", expression = "java(Boolean.TRUE)")
    @Mapping(target = "paperForm", expression = "java(Boolean.TRUE)")
    @Mapping(target = "applicationType", source = "ocrFields", qualifiedBy = {ToApplicationTypeCaveat.class})
    CaveatData toCcdData(ExceptionRecordOCRFields ocrFields);

    @AfterMapping
    default void setLanguagePreferenceWelsh(
            @MappingTarget CaveatData caseData, ExceptionRecordOCRFields ocrFields) {
        if (null == ocrFields.getBilingualCorrespondenceRequested()) {
            caseData.setLanguagePreferenceWelsh(Boolean.FALSE);
        }
    }

    @AfterMapping
    default void setSolsPaymentMethod(
        @MappingTarget CaveatData caseData, ExceptionRecordOCRFields ocrField) {
        if ((caseData.getApplicationType() == ApplicationType.SOLICITORS)
            && StringUtils.isNotBlank(caseData.getSolsFeeAccountNumber())) {
            caseData.setSolsPaymentMethods(SolsPaymentMethods.FEE_ACCOUNT);
        }
    }

    @AfterMapping
    default void setSolsSolicitorEmail(
        @MappingTarget CaveatData caseData, ExceptionRecordOCRFields ocrField) {
        if ((caseData.getApplicationType() == ApplicationType.SOLICITORS)
            && StringUtils.isNotBlank(ocrField.getSolsSolicitorEmail())) {
            caseData.setCaveatorEmailAddress(ocrField.getSolsSolicitorEmail());
        }
    }
}
