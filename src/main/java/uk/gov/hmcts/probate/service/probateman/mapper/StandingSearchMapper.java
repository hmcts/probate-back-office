package uk.gov.hmcts.probate.service.probateman.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import uk.gov.hmcts.probate.model.ccd.standingsearch.request.StandingSearchData;
import uk.gov.hmcts.probate.model.probateman.StandingSearch;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface StandingSearchMapper extends ProbateManMapper<StandingSearch, StandingSearchData> {

    @Mappings({
        @Mapping(target = "deceasedForenames", source = "deceasedForenames"),
        @Mapping(target = "deceasedSurname", source = "deceasedSurname"),
        @Mapping(target = "deceasedAddress", ignore = true),
        @Mapping(target = "applicantAddress", ignore = true)
    })
    StandingSearchData toCcdData(StandingSearch standingSearch);
}
