package uk.gov.hmcts.probate.service.probateman.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import uk.gov.hmcts.probate.model.ccd.grantapplication.request.GrantApplicationData;
import uk.gov.hmcts.probate.model.probateman.GrantApplication;

@Mapper(componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GrantApplicationMapper extends ProbateManMapper<GrantApplication, GrantApplicationData> {

    @Mappings({
        @Mapping(target = "deceasedForenames", source = "deceasedForenames"),
        @Mapping(target = "deceasedSurname", source = "deceasedSurname"),
        @Mapping(target = "primaryApplicantForenames", source = "applicantForenames"),
        @Mapping(target = "primaryApplicantSurname", source = "applicantSurname"),
        @Mapping(target = "deceasedDateOfBirth", source = "dateOfBirth"),
        @Mapping(target = "deceasedDateOfDeath", source = "dateOfDeath1")
    })
    GrantApplicationData toCcdData(GrantApplication grantApplication);
}
