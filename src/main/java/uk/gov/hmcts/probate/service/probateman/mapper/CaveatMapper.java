package uk.gov.hmcts.probate.service.probateman.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import uk.gov.hmcts.probate.model.probateman.Caveat;
import uk.gov.hmcts.probate.model.probateman.LegacyCaseType;
import uk.gov.hmcts.probate.service.probateman.mapper.qualifiers.ToFullAliasNameMember;
import uk.gov.hmcts.probate.service.probateman.mapper.qualifiers.ToLegacyCaseViewUrl;
import uk.gov.hmcts.reform.probate.model.cases.caveat.CaveatData;

@Mapper(componentModel = "spring",
        uses = {FullAliasNameMapper.class, LegacyCaseiewUrlMapper.class},
        imports = {LegacyCaseType.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface CaveatMapper extends ProbateManMapper<Caveat, CaveatData> {

    @Mapping(target = "deceasedDateOfBirth", source = "dateOfBirth")
    @Mapping(target = "deceasedDateOfDeath", source = "dateOfDeath")
    @Mapping(target = "deceasedForenames", source = "deceasedForenames")
    @Mapping(target = "deceasedSurname", source = "deceasedSurname")
    @Mapping(target = "caveatorForenames", source = "caveatorForenames")
    @Mapping(target = "caveatorSurname", source = "caveatorSurname")
    @Mapping(target = "deceasedFullAliasNameList", source = "aliasNames", qualifiedBy = {ToFullAliasNameMember.class})
    @Mapping(target = "deceasedAddress.addressLine1", source = "cavServiceAddress")
    @Mapping(target = "expiryDate", source = "cavExpiryDate")
    @Mapping(target = "legacyId", source = "id")
    @Mapping(target = "legacyType", expression = "java(LegacyCaseType.CAVEAT.getName())")
    @Mapping(target = "legacyCaseViewUrl", source = "caveat", qualifiedBy = {ToLegacyCaseViewUrl.class})
    CaveatData toCcdData(Caveat caveat);
}
