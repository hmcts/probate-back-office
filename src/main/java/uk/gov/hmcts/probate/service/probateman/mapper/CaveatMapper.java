package uk.gov.hmcts.probate.service.probateman.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.probateman.Caveat;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface CaveatMapper extends ProbateManMapper<Caveat, CaveatData> {

    @Mappings({
        @Mapping(target = "deceasedForenames", source = "deceasedForenames"),
        @Mapping(target = "deceasedSurname", source = "deceasedSurname")
    })
    CaveatData toCcdData(Caveat caveat);
}
