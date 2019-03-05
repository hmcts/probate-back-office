package uk.gov.hmcts.probate.service.probateman.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import uk.gov.hmcts.probate.model.probateman.GrantApplication;
import uk.gov.hmcts.probate.model.probateman.LegacyCaseType;
import uk.gov.hmcts.probate.service.probateman.mapper.qualifiers.ToAdditionalExecutorApplyingMember;
import uk.gov.hmcts.probate.service.probateman.mapper.qualifiers.ToAliasNameMember;
import uk.gov.hmcts.probate.service.probateman.mapper.qualifiers.ToLegacyCaseViewUrl;
import uk.gov.hmcts.reform.probate.model.cases.ApplicationType;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantType;

@Mapper(componentModel = "spring", uses = {AliasNameMapper.class, AdditionalExecutorMapper.class, LegacyCaseViewUrlMapper.class},
        imports = {GrantType.class, ApplicationType.class, LegacyCaseType.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GrantApplicationMapper extends ProbateManMapper<GrantApplication, GrantOfRepresentationData> {

    @Mapping(target = "applicationType", expression = "java(grantApplication.getSolicitorReference() == null ? "
            + "ApplicationType.PERSONAL : ApplicationType.SOLICITORS)")
    @Mapping(target = "grantType", expression = "java(GrantType.GRANT_OF_PROBATE)")
    @Mapping(target = "deceasedForenames", source = "deceasedForenames")
    @Mapping(target = "deceasedSurname", source = "deceasedSurname")
    @Mapping(target = "deceasedDateOfBirth", source = "dateOfBirth")
    @Mapping(target = "deceasedDateOfDeath", source = "dateOfDeath1")
    @Mapping(target = "deceasedAddress.addressLine1", source = "deceasedAddress")
    @Mapping(target = "deceasedAliasNameList", source = "aliasNames", qualifiedBy = {ToAliasNameMember.class})

    //@Mapping(target = "solsSolicitorFirmName", expression = "java(grantApplication.getSolicitorReference() == null ? "
    //       + "null : grantApplication.getApplicantForenames + ' ' + grantApplication.getApplicantSurname)" )
    @Mapping(target = "solsSolicitorAddress.addressLine1", expression = "java(grantApplication.getSolicitorReference() == null ? "
            + "null : grantApplication.getApplicantAddress())")
    @Mapping(target = "solsSolicitorAppReference", expression = "java(grantApplication.getSolicitorReference() == null ? "
            + "null : grantApplication.getSolicitorReference())")

    @Mapping(target = "primaryApplicantForenames", expression = "java(grantApplication.getSolicitorReference() == null ? "
            + "grantApplication.getApplicantForenames() : grantApplication.getGrantee1Forenames())")
    @Mapping(target = "primaryApplicantSurname", expression = "java(grantApplication.getSolicitorReference() == null ? "
            + "grantApplication.getApplicantSurname() : grantApplication.getGrantee1Surname())")
    @Mapping(target = "primaryApplicantAddress.addressLine1",
            expression = "java(grantApplication.getSolicitorReference() == null ? "
                    + "grantApplication.getApplicantAddress() :  grantApplication.getGrantee1Address())")

    @Mapping(target = "additionalExecutorsApplying", source = "grantApplication",
            qualifiedBy = {ToAdditionalExecutorApplyingMember.class})
    @Mapping(target = "ihtNetValue", source = "netEstateValue")
    @Mapping(target = "ihtGrossValue", source = "grossEstateValue")
    @Mapping(target = "recordId", source = "probateNumber")
    @Mapping(target = "legacyId", source = "id")
    @Mapping(target = "legacyType", expression = "java(LegacyCaseType.GRANT_OF_REPRESENTATION.getName())")
    @Mapping(target = "legacyCaseViewUrl", source = "grantApplication", qualifiedBy = {ToLegacyCaseViewUrl.class})
    GrantOfRepresentationData toCcdData(GrantApplication grantApplication);

}