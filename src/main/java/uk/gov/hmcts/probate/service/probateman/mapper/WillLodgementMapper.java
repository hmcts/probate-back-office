package uk.gov.hmcts.probate.service.probateman.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import uk.gov.hmcts.probate.model.probateman.WillLodgement;
import uk.gov.hmcts.probate.service.probateman.mapper.qualifiers.ToFullAliasNameMember;
import uk.gov.hmcts.reform.probate.model.cases.willlodgement.WillLodgementData;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {FullAliasNameMapper.class},
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface WillLodgementMapper extends ProbateManMapper<WillLodgement, WillLodgementData> {

    @Mappings({
            @Mapping(target = "deceasedForenames", source = "deceasedForenames"),
            @Mapping(target = "deceasedSurname", source = "deceasedSurname"),
            @Mapping(target = "deceasedDateOfBirth", source = "dateOfBirth"),
            @Mapping(target = "deceasedDateOfDeath", source = "dateOfDeath1"),
            @Mapping(target = "deceasedFullAliasNameList", source = "aliasNames", qualifiedBy = {ToFullAliasNameMember.class})
    })
    WillLodgementData toCcdData(WillLodgement willLodgement);
}
