package uk.gov.hmcts.probate.service.probateman.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import uk.gov.hmcts.probate.model.probateman.LegacyCaseType;
import uk.gov.hmcts.probate.model.probateman.WillLodgement;
import uk.gov.hmcts.probate.service.probateman.mapper.qualifiers.ToFullAliasNameMember;
import uk.gov.hmcts.probate.service.probateman.mapper.qualifiers.ToLegacyCaseViewUrl;
import uk.gov.hmcts.reform.probate.model.cases.willlodgement.WillLodgementData;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {FullAliasNameMapper.class, LegacyCaseViewUrlMapper.class},
        imports = {LegacyCaseType.class},
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface WillLodgementMapper extends ProbateManMapper<WillLodgement, WillLodgementData> {

    @Mapping(target = "deceasedForenames", source = "deceasedForenames")
    @Mapping(target = "deceasedSurname", source = "deceasedSurname")
    @Mapping(target = "deceasedDateOfBirth", source = "dateOfBirth")
    @Mapping(target = "deceasedDateOfDeath", source = "dateOfDeath1")
    @Mapping(target = "deceasedFullAliasNameList", source = "aliasNames", qualifiedBy = {ToFullAliasNameMember.class})
    @Mapping(target = "recordId", source = "rkNumber")
    @Mapping(target = "legacyId", source = "id")
    @Mapping(target = "legacyType", expression = "java(LegacyCaseType.WILL_LODGEMENT.getName())")
    @Mapping(target = "legacyCaseViewUrl", source = "willLodgement", qualifiedBy = {ToLegacyCaseViewUrl.class})
    WillLodgementData toCcdData(WillLodgement willLodgement);
}
