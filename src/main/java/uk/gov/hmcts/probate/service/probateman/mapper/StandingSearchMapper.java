package uk.gov.hmcts.probate.service.probateman.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import uk.gov.hmcts.probate.model.probateman.LegacyCaseType;
import uk.gov.hmcts.probate.model.probateman.StandingSearch;
import uk.gov.hmcts.probate.service.probateman.mapper.qualifiers.ToFullAliasNameMember;
import uk.gov.hmcts.probate.service.probateman.mapper.qualifiers.ToLegacyCaseViewUrl;
import uk.gov.hmcts.reform.probate.model.cases.standingsearch.StandingSearchData;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {FullAliasNameMapper.class, LegacyCaseViewUrlMapper.class},
        imports = {LegacyCaseType.class},
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface StandingSearchMapper extends ProbateManMapper<StandingSearch, StandingSearchData> {

    @Mapping(target = "deceasedForenames", source = "deceasedForenames")
    @Mapping(target = "deceasedSurname", source = "deceasedSurname")
    @Mapping(target = "deceasedDateOfBirth", source = "dateOfBirth")
    @Mapping(target = "deceasedDateOfDeath", source = "dateOfDeath1")
    @Mapping(target = "deceasedAnyOtherNames", expression = "java(standingSearch.getAliasNames() == null ? false : true )")
    @Mapping(target = "deceasedFullAliasNameList", source = "aliasNames", qualifiedBy = {ToFullAliasNameMember.class})
    @Mapping(target = "deceasedAddress.addressLine1", source = "deceasedAddress")
    @Mapping(target = "applicantForenames", source = "ssApplicantForename")
    @Mapping(target = "applicantSurname", source = "ssApplicantSurname")
    @Mapping(target = "applicantAddress.addressLine1", source = "applicantAddress")
    @Mapping(target = "expiryDate", source = "ssDateOfExpiry")
    @Mapping(target = "recordId", source = "ssNumber")
    @Mapping(target = "legacyId", source = "id")
    @Mapping(target = "applicationSubmittedDate", source = "ssDateOfEntry")
    @Mapping(target = "legacyType", expression = "java(LegacyCaseType.STANDING_SEARCH.getName())")
    @Mapping(target = "legacyCaseViewUrl", source = "standingSearch", qualifiedBy = {ToLegacyCaseViewUrl.class})
    StandingSearchData toCcdData(StandingSearch standingSearch);
}
