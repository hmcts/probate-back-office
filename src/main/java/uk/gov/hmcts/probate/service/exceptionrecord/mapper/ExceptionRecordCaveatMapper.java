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
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToYesOrNo;
import uk.gov.hmcts.probate.service.exceptionrecord.utils.OCRFieldExtractor;
import uk.gov.hmcts.reform.probate.model.cases.ApplicationType;
import uk.gov.hmcts.reform.probate.model.cases.SolsPaymentMethods;
import uk.gov.hmcts.reform.probate.model.cases.caveat.CaveatData;

import java.util.List;

@Mapper(componentModel = "spring",
        imports = {StringUtils.class, ApplicationType.class},
        uses = {ApplicationTypeMapper.class,
                OCRFieldAddressMapper.class,
                OCRFieldDefaultLocalDateFieldMapper.class,
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
    @Mapping(target = "deceasedDateOfDeath", source = "deceasedDateOfDeath", qualifiedBy = {ToDefaultLocalDate.class})
    @Mapping(target = "deceasedDateOfBirth", source = "deceasedDateOfBirth", qualifiedBy = {ToDefaultLocalDate.class})
    @Mapping(target = "deceasedAnyOtherNames", source = "deceasedAnyOtherNames", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "solsSolicitorFirmName", source = "solsSolicitorFirmName")
    @Mapping(target = "solsSolicitorAppReference", source = "solsSolicitorAppReference")
    @Mapping(target = "solsFeeAccountNumber", source = "solsFeeAccountNumber")
    @Mapping(target = "solsPaymentMethods", ignore = true)
    @Mapping(target = "languagePreferenceWelsh", expression = "java(Boolean.FALSE)")
    @Mapping(target = "solsSolicitorPhoneNumber", source = "solsSolicitorPhoneNumber")
    @Mapping(target = "caveatRaisedEmailNotificationRequested", expression = "java(Boolean.TRUE)")
    @Mapping(target = "paperForm", expression = "java(Boolean.TRUE)")
    @Mapping(target = "applicationType", source = "ocrFields", qualifiedBy = {ToApplicationTypeCaveat.class})
    CaveatData toCcdData(ExceptionRecordOCRFields ocrFields);

    @AfterMapping
    default void setSolsPaymentMethod(
            @MappingTarget CaveatData caseData, ExceptionRecordOCRFields ocrField) {
        if ((caseData.getApplicationType() == ApplicationType.SOLICITORS) &&
                StringUtils.isNotBlank(caseData.getSolsFeeAccountNumber())) {
            caseData.setSolsPaymentMethods(SolsPaymentMethods.FEE_ACCOUNT);
        }
    }

    @AfterMapping
    default void setSolsSolicitorEmail(
            @MappingTarget CaveatData caseData, ExceptionRecordOCRFields ocrField) {
        if ((caseData.getApplicationType() == ApplicationType.SOLICITORS) &&
                StringUtils.isNotBlank(ocrField.getSolsSolicitorEmail())) {
            caseData.setCaveatorEmailAddress(ocrField.getSolsSolicitorEmail());
        }
    }

    @AfterMapping
    default void setSolsSolicitorRepresentativeName(
            @MappingTarget CaveatData caseData, ExceptionRecordOCRFields ocrField) {
        if ((caseData.getApplicationType() == ApplicationType.SOLICITORS) &&
                (StringUtils.isNotBlank(ocrField.getSolsSolicitorRepresentativeName()))) {
            String solicitorFullName = ocrField.getSolsSolicitorRepresentativeName();
            List<String> names = OCRFieldExtractor.splitFullname(solicitorFullName);
            if (names.size() > 2) {
                caseData.setCaveatorSurname(names.get(names.size()-1));
                caseData.setCaveatorForenames(String.join(" ", names.subList(0, names.size()-1)));
            } else if(names.size() == 1) {
                caseData.setCaveatorSurname("");
                caseData.setCaveatorForenames(names.get(0));
            } else {
                caseData.setCaveatorSurname(names.get(1));
                caseData.setCaveatorForenames(names.get(0));
            }
        }
    }
}