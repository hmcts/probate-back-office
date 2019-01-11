package uk.gov.hmcts.probate.service.probateman.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import uk.gov.hmcts.probate.model.probateman.GrantApplication;
import uk.gov.hmcts.probate.service.probateman.mapper.qualifiers.ToAdditionalExecutorApplyingMember;
import uk.gov.hmcts.probate.service.probateman.mapper.qualifiers.ToAliasNameMember;
import uk.gov.hmcts.reform.probate.model.cases.ApplicationType;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentation;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantType;

@Mapper(componentModel = "spring", uses = {AliasNameMapper.class, AdditionalExecutorMapper.class},
        imports = {GrantType.class, ApplicationType.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GrantApplicationMapper extends ProbateManMapper<GrantApplication, GrantOfRepresentation> {

    @Mappings({
            @Mapping(target = "applicationType", expression = "java(grantApplication.getSolicitorReference() == null ? "
                    + "ApplicationType.PERSONAL : ApplicationType.SOLICITORS)"),
            @Mapping(target = "caseType", expression = "java(GrantType.GRANT_OF_PROBATE)"),
            @Mapping(target = "deceasedForenames", source = "deceasedForenames"),
            @Mapping(target = "deceasedSurname", source = "deceasedSurname"),
            @Mapping(target = "primaryApplicantForenames", source = "applicantForenames"),
            @Mapping(target = "primaryApplicantSurname", source = "applicantSurname"),
            @Mapping(target = "deceasedDateOfBirth", source = "dateOfBirth"),
            @Mapping(target = "deceasedDateOfDeath", source = "dateOfDeath1"),
            @Mapping(target = "deceasedAddress.addressLine1", source = "deceasedAddress"),
            @Mapping(target = "deceasedAliasNameList", source = "aliasNames", qualifiedBy = {ToAliasNameMember.class}),
            @Mapping(target = "primaryApplicantAddress.addressLine1", source = "applicantAddress"),
            @Mapping(target = "additionalExecutorsApplying", source = "grantApplication",
                    qualifiedBy = {ToAdditionalExecutorApplyingMember.class}),
            @Mapping(target = "ihtNetValue", source = "netEstateValue"),
            @Mapping(target = "ihtGrossValue", source = "grossEstateValue")
            //@Mapping(target = "solicitorReference", source = ""),
    })
    GrantOfRepresentation toCcdData(GrantApplication grantApplication);

}