package uk.gov.hmcts.probate.service.probateman.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import uk.gov.hmcts.probate.model.ccd.willlodgement.request.WillLodgementData;
import uk.gov.hmcts.probate.model.probateman.WillLodgement;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface WillLodgementMapper extends ProbateManMapper<WillLodgement, WillLodgementData> {

    @Mappings({
            @Mapping(target = "wlApplicantForenames", source = "deceasedForenames"),
            @Mapping(target = "wlApplicantSurname", source = "deceasedSurname"),
            @Mapping(target = "wlApplicantReferenceNumber", source = "rkNumber"),
    })
    WillLodgementData toCcdData(WillLodgement willLodgement);
}
