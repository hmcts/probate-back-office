package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import uk.gov.hmcts.reform.probate.model.cases.caveat.CaveatData;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToCaveatorAddress;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToDeceasedAddress;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToDefaultLocalDate;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToYesOrNo;

@Mapper(componentModel = "spring",
        uses = {AddressMapper.class, DefaultLocalDateFieldMapper.class, YesOrNoMapper.class},
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
    CaveatData toCcdData(ExceptionRecordOCRFields ocrFields);
}