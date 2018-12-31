package uk.gov.hmcts.probate.service.probateman.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import uk.gov.hmcts.probate.model.ccd.grantapplication.request.GrantApplicationData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.probateman.GrantApplication;

@Mapper(componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GrantApplicationMapper extends ProbateManMapper<GrantApplication, GrantApplicationData> {

    @Mappings({
        @Mapping(target = "gaDeceasedForenames", source = "deceasedForenames"),
        @Mapping(target = "gaDeceasedSurname", source = "deceasedSurname"),
        @Mapping(target = "gaPrimaryApplicantForenames", source = "applicantForenames"),
        @Mapping(target = "gaPrimaryApplicantSurname", source = "applicantSurname"),
        @Mapping(target = "gaDateOfBirth", source = "dateOfBirth"),
        @Mapping(target = "gaDateOfDeath", source = "dateOfDeath1"),
        @Mapping(target = "gaDeceasedAddress", ignore = true)
    })

    GrantApplicationData toCcdData(GrantApplication grantApplication);
}
