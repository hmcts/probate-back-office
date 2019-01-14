package uk.gov.hmcts.probate.service.probateman.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import uk.gov.hmcts.probate.model.probateman.StandingSearch;
import uk.gov.hmcts.probate.service.probateman.mapper.qualifiers.ToFullAliasNameMember;
import uk.gov.hmcts.reform.probate.model.cases.standingsearch.StandingSearchData;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {FullAliasNameMapper.class},
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface StandingSearchMapper extends ProbateManMapper<StandingSearch, StandingSearchData> {

    @Mappings({
            @Mapping(target = "deceasedForenames", source = "deceasedForenames"),
            @Mapping(target = "deceasedSurname", source = "deceasedSurname"),
            @Mapping(target = "deceasedDateOfBirth", source = "dateOfBirth"),
            @Mapping(target = "deceasedDateOfDeath", source = "dateOfDeath1"),
            @Mapping(target = "deceasedFullAliasNameList", source = "aliasNames", qualifiedBy = {ToFullAliasNameMember.class}),
            @Mapping(target = "deceasedAddress.addressLine1", source = "deceasedAddress"),
            @Mapping(target = "applicantForenames", source = "ssApplicantForename"),
            @Mapping(target = "applicantSurname", source = "ssApplicantSurname"),
            @Mapping(target = "applicantAddress.addressLine1", source = "applicantAddress"),
            @Mapping(target = "expiryDate", source = "ssDateOfExpiry")
    })
    StandingSearchData toCcdData(StandingSearch standingSearch);
}
