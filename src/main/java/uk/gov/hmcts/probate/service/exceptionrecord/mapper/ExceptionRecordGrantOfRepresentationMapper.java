package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import io.micrometer.core.instrument.util.StringUtils;
import org.apache.commons.lang.BooleanUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToAdditionalExecutorsApplying;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToAdditionalExecutorsNotApplying;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToAdoptiveRelatives;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToApplicationTypeGrantOfRepresentation;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToAttorneyOnBehalfOfAddress;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToDeceasedAddress;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToDefaultLocalDate;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToIHTFormId;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToLong;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToMartialStatus;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToPaperPaymentMethod;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToPennies;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToPrimaryApplicantAddress;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToRelationship;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToRelationshipOther;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToSolicitorAddress;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToYesOrNo;
import uk.gov.hmcts.probate.service.exceptionrecord.utils.OCRFieldExtractor;
import uk.gov.hmcts.reform.probate.model.cases.ApplicationType;
import uk.gov.hmcts.reform.probate.model.cases.SolsPaymentMethods;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantType;

import java.util.List;

@Mapper(componentModel = "spring",
        imports = {StringUtils.class, ApplicationType.class},
        uses = {ApplicationTypeMapper.class,
                OCRFieldAddressMapper.class,
                OCRFieldAdditionalExecutorsApplyingMapper.class,
                OCRFieldAdditionalExecutorsNotApplyingMapper.class,
                OCRFieldDefaultLocalDateFieldMapper.class,
                OCRFieldYesOrNoMapper.class,
                OCRFieldMartialStatusMapper.class,
                OCRFieldAdoptiveRelativesMapper.class,
                OCRFieldIhtMoneyMapper.class,
                OCRFieldRelationshipMapper.class,
                OCRFieldPaymentMethodMapper.class,
                OCRFieldNumberMapper.class
        },
        unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ExceptionRecordGrantOfRepresentationMapper {
    @Mapping(target = "extraCopiesOfGrant", source = "ocrFields.extraCopiesOfGrant", qualifiedBy = {ToLong.class})
    @Mapping(target = "outsideUkGrantCopies", source = "ocrFields.outsideUKGrantCopies", qualifiedBy = {ToLong.class})

    @Mapping(target = "applicationFeePaperForm", source = "ocrFields.applicationFeePaperForm", qualifiedBy = {ToPennies.class})
    @Mapping(target = "feeForCopiesPaperForm", source = "ocrFields.feeForCopiesPaperForm", qualifiedBy = {ToPennies.class})
    @Mapping(target = "totalFeePaperForm", source = "ocrFields.totalFeePaperForm", qualifiedBy = {ToPennies.class})
    @Mapping(target = "paperPaymentMethod", source = "ocrFields.paperPaymentMethod", qualifiedBy = {ToPaperPaymentMethod.class})
    @Mapping(target = "paymentReferenceNumberPaperform", source = "ocrFields.paymentReferenceNumberPaperform")

    @Mapping(target = "primaryApplicantForenames", source = "ocrFields.primaryApplicantForenames")
    @Mapping(target = "primaryApplicantSurname", source = "ocrFields.primaryApplicantSurname")
    @Mapping(target = "primaryApplicantAddress", source = "ocrFields", qualifiedBy = {ToPrimaryApplicantAddress.class})
    @Mapping(target = "primaryApplicantPhoneNumber", source = "ocrFields.primaryApplicantPhoneNumber")
    @Mapping(target = "primaryApplicantEmailAddress", source = "ocrFields.primaryApplicantEmailAddress")
    @Mapping(target = "primaryApplicantSecondPhoneNumber", source = "ocrFields.primaryApplicantSecondPhoneNumber")
    @Mapping(target = "primaryApplicantHasAlias", source = "ocrFields.primaryApplicantHasAlias", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "primaryApplicantAlias", source = "ocrFields.primaryApplicantAlias")

    @Mapping(target = "executorsApplying", source = "ocrFields", qualifiedBy = {ToAdditionalExecutorsApplying.class})

    @Mapping(target = "solsSolicitorIsApplying", source = "ocrFields.solsSolicitorIsApplying", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "solsSolicitorAddress", source = "ocrFields", qualifiedBy = {ToSolicitorAddress.class})
    @Mapping(target = "solsSolicitorFirmName", source = "ocrFields.solsSolicitorFirmName")
    @Mapping(target = "solsSolicitorAppReference", source = "ocrFields.solsSolicitorAppReference")
    @Mapping(target = "solsFeeAccountNumber", source = "ocrFields.solsFeeAccountNumber")
    @Mapping(target = "solsSolicitorEmail", source = "ocrFields.solsSolicitorEmail")
    @Mapping(target = "solsSolicitorPhoneNumber", source = "ocrFields.solsSolicitorPhoneNumber")

    @Mapping(target = "deceasedForenames", source = "ocrFields.deceasedForenames")
    @Mapping(target = "deceasedSurname", source = "ocrFields.deceasedSurname")
    @Mapping(target = "deceasedAddress", source = "ocrFields", qualifiedBy = {ToDeceasedAddress.class})
    @Mapping(target = "deceasedDateOfBirth", source = "ocrFields.deceasedDateOfBirth", qualifiedBy = {ToDefaultLocalDate.class})
    @Mapping(target = "deceasedDateOfDeath", source = "ocrFields.deceasedDateOfDeath", qualifiedBy = {ToDefaultLocalDate.class})
    @Mapping(target = "deceasedAnyOtherNames", source = "ocrFields.deceasedAnyOtherNames", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "deceasedDomicileInEngWales", source = "ocrFields.deceasedDomicileInEngWales", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "deceasedMaritalStatus", source = "ocrFields.deceasedMartialStatus", qualifiedBy = {ToMartialStatus.class})

    @Mapping(target = "dateOfMarriageOrCP", source = "ocrFields.dateOfMarriageOrCP", qualifiedBy = {ToDefaultLocalDate.class})
    @Mapping(target = "dateOfDivorcedCPJudicially", source = "ocrFields.dateOfDivorcedCPJudicially", qualifiedBy = {ToDefaultLocalDate.class})
    @Mapping(target = "courtOfDecree", source = "ocrFields.courtOfDecree")

    @Mapping(target = "foreignAsset", source = "ocrFields.foreignAsset", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "foreignAssetEstateValue", source = "ocrFields.foreignAssetEstateValue", qualifiedBy = {ToPennies.class})

    @Mapping(target = "adopted", source = "ocrFields.adopted", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "adoptiveRelatives", source = "ocrFields", qualifiedBy = {ToAdoptiveRelatives.class})
    @Mapping(target = "spouseOrPartner", source = "ocrFields.spouseOrPartner", qualifiedBy = {ToYesOrNo.class})

    // Following fields captured as text but used as booleans in orchestrator
    @Mapping(target = "childrenSurvived", ignore = true)
    @Mapping(target = "childrenUnderEighteenSurvivedText", expression = "java(ocrFields.getChildrenUnderEighteenSurvived())")
    @Mapping(target = "childrenOverEighteenSurvivedText", expression = "java(ocrFields.getChildrenOverEighteenSurvived())")
    @Mapping(target = "childrenDied", ignore = true)
    @Mapping(target = "childrenDiedUnderEighteenText", expression = "java(ocrFields.getChildrenDiedUnderEighteen())")
    @Mapping(target = "childrenDiedOverEighteenText", expression = "java(ocrFields.getChildrenDiedOverEighteen())")
    @Mapping(target = "grandChildrenSurvived", ignore = true)
    @Mapping(target = "grandChildrenSurvivedUnderEighteenText", expression = "java(ocrFields.getGrandChildrenSurvivedUnderEighteen())")
    @Mapping(target = "grandChildrenSurvivedOverEighteenText", expression = "java(ocrFields.getGrandChildrenSurvivedOverEighteen())")

    // Ignored fields defined in after mapping section.
    @Mapping(target = "parentsExistUnderEighteenSurvived", source = "ocrFields.parentsExistUnderEighteenSurvived")
    @Mapping(target = "parentsExistOverEighteenSurvived", source = "ocrFields.parentsExistOverEighteenSurvived")
    @Mapping(target = "wholeBloodSiblingsSurvivedUnderEighteen", source = "ocrFields.wholeBloodSiblingsSurvivedUnderEighteen")
    @Mapping(target = "wholeBloodSiblingsSurvivedOverEighteen", source = "ocrFields.wholeBloodSiblingsSurvivedOverEighteen")
    @Mapping(target = "wholeBloodSiblingsDiedUnderEighteen", source = "ocrFields.wholeBloodSiblingsDiedUnderEighteen")
    @Mapping(target = "wholeBloodSiblingsDiedOverEighteen", source = "ocrFields.wholeBloodSiblingsDiedOverEighteen")
    @Mapping(target = "wholeBloodNeicesAndNephewsUnderEighteen", source = "ocrFields.wholeBloodNeicesAndNephewsUnderEighteen")
    @Mapping(target = "wholeBloodNeicesAndNephewsOverEighteen", source = "ocrFields.wholeBloodNeicesAndNephewsOverEighteen")
    @Mapping(target = "halfBloodSiblingsSurvivedUnderEighteen", source = "ocrFields.halfBloodSiblingsSurvivedUnderEighteen")
    @Mapping(target = "halfBloodSiblingsSurvivedOverEighteen", source = "ocrFields.halfBloodSiblingsSurvivedOverEighteen")
    @Mapping(target = "halfBloodSiblingsDiedUnderEighteen", source = "ocrFields.halfBloodSiblingsDiedUnderEighteen")
    @Mapping(target = "halfBloodSiblingsDiedOverEighteen", source = "ocrFields.halfBloodSiblingsDiedOverEighteen")
    @Mapping(target = "halfBloodNeicesAndNephewsUnderEighteen", source = "ocrFields.halfBloodNeicesAndNephewsUnderEighteen")
    @Mapping(target = "halfBloodNeicesAndNephewsOverEighteen", source = "ocrFields.halfBloodNeicesAndNephewsOverEighteen")
    @Mapping(target = "grandparentsDiedUnderEighteen", source = "ocrFields.grandparentsDiedUnderEighteen")
    @Mapping(target = "grandparentsDiedOverEighteen", source = "ocrFields.grandparentsDiedOverEighteen")
    @Mapping(target = "wholeBloodUnclesAndAuntsSurvivedUnderEighteen", source = "ocrFields.wholeBloodUnclesAndAuntsSurvivedUnderEighteen")
    @Mapping(target = "wholeBloodUnclesAndAuntsSurvivedOverEighteen", source = "ocrFields.wholeBloodUnclesAndAuntsSurvivedOverEighteen")
    @Mapping(target = "wholeBloodUnclesAndAuntsDiedUnderEighteen", source = "ocrFields.wholeBloodUnclesAndAuntsDiedUnderEighteen")
    @Mapping(target = "wholeBloodUnclesAndAuntsDiedOverEighteen", source = "ocrFields.wholeBloodUnclesAndAuntsDiedOverEighteen")
    @Mapping(target = "wholeBloodCousinsSurvivedUnderEighteen", source = "ocrFields.wholeBloodCousinsSurvivedUnderEighteen")
    @Mapping(target = "wholeBloodCousinsSurvivedOverEighteen", source = "ocrFields.wholeBloodCousinsSurvivedOverEighteen")
    @Mapping(target = "halfBloodUnclesAndAuntsSurvivedUnderEighteen", source = "ocrFields.halfBloodUnclesAndAuntsSurvivedUnderEighteen")
    @Mapping(target = "halfBloodUnclesAndAuntsSurvivedOverEighteen", source = "ocrFields.halfBloodUnclesAndAuntsSurvivedOverEighteen")
    @Mapping(target = "halfBloodUnclesAndAuntsDiedUnderEighteen", source = "ocrFields.halfBloodUnclesAndAuntsDiedUnderEighteen")
    @Mapping(target = "halfBloodUnclesAndAuntsDiedOverEighteen", source = "ocrFields.halfBloodUnclesAndAuntsDiedOverEighteen")
    @Mapping(target = "halfBloodCousinsSurvivedUnderEighteen", source = "ocrFields.halfBloodCousinsSurvivedUnderEighteen")
    @Mapping(target = "halfBloodCousinsSurvivedOverEighteen", source = "ocrFields.halfBloodCousinsSurvivedOverEighteen")
    @Mapping(target = "primaryApplicantRelationshipToDeceased",
            source = "ocrFields.primaryApplicantRelationshipToDeceased", qualifiedBy = {ToRelationship.class})
    @Mapping(target = "paRelationshipToDeceasedOther",
            source = "ocrFields.primaryApplicantRelationshipToDeceased", qualifiedBy = {ToRelationshipOther.class})

    @Mapping(target = "solsSOTName", source = "ocrFields.solsSolicitorRepresentativeName")
    @Mapping(target = "applyingAsAnAttorney", source = "ocrFields.applyingAsAnAttorney", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "attorneyOnBehalfOfNameAndAddress", source = "ocrFields", qualifiedBy = {ToAttorneyOnBehalfOfAddress.class})
    @Mapping(target = "mentalCapacity", source = "ocrFields.mentalCapacity", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "courtOfProtection", source = "ocrFields.courtOfProtection", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "epaOrLpa", source = "ocrFields.epaOrLpa", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "epaRegistered", source = "ocrFields.epaRegistered", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "domicilityCountry", source = "ocrFields.domicilityCountry")

    // domicilityIHTCert defined in after mapping section If domicilityEntrustingDocument or domicilitySuccessionIHTCert is true
    @Mapping(target = "domicilityIHTCert", ignore = true)

    @Mapping(target = "executorsNotApplying", source = "ocrFields", qualifiedBy = {ToAdditionalExecutorsNotApplying.class})
    @Mapping(target = "willHasCodicils", source = "ocrFields.willHasCodicils", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "deceasedMarriedAfterWillOrCodicilDate", source = "ocrFields.deceasedMarriedAfterWillOrCodicilDate",
            qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "languagePreferenceWelsh", source = "ocrFields.bilingualGrantRequested", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "willDate", source = "ocrFields.willDate", qualifiedBy = {ToDefaultLocalDate.class})
    @Mapping(target = "willsOutsideOfUK", source = "ocrFields.willsOutsideOfUK", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "willGiftUnderEighteen", source = "ocrFields.willGiftUnderEighteen", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "notifiedApplicants", source = "ocrFields.notifiedApplicants", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "ihtFormCompletedOnline", source = "ocrFields.ihtFormCompletedOnline", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "ihtReferenceNumber", source = "ocrFields.ihtReferenceNumber")
    @Mapping(target = "ihtFormId", source = "ocrFields.ihtFormId", qualifiedBy = {ToIHTFormId.class})
    @Mapping(target = "ihtGrossValue", source = "ocrFields.ihtGrossValue", qualifiedBy = {ToPennies.class})
    @Mapping(target = "ihtNetValue", source = "ocrFields.ihtNetValue", qualifiedBy = {ToPennies.class})

    @Mapping(target = "paperForm", expression = "java(Boolean.TRUE)")
    @Mapping(target = "applicationType", source = "ocrFields", qualifiedBy = {ToApplicationTypeGrantOfRepresentation.class})
    GrantOfRepresentationData toCcdData(ExceptionRecordOCRFields ocrFields, GrantType grantType);



    @AfterMapping
    default void setDomicilityIHTCert(@MappingTarget GrantOfRepresentationData caseData, ExceptionRecordOCRFields ocrField) {
        if (BooleanUtils.toBoolean(ocrField.getDomicilityEntrustingDocument())
                || BooleanUtils.toBoolean(ocrField.getDomicilitySuccessionIHTCert())) {
            caseData.setDomicilityIHTCert(Boolean.TRUE);
        }
    }

    @AfterMapping
    default void setSolsPaymentMethod(
            @MappingTarget GrantOfRepresentationData caseData, ExceptionRecordOCRFields ocrField) {
        if ((caseData.getApplicationType() == ApplicationType.SOLICITORS) &&
                StringUtils.isNotBlank(caseData.getSolsFeeAccountNumber())) {
            caseData.setSolsPaymentMethods(SolsPaymentMethods.FEE_ACCOUNT);
        }
    }

    @AfterMapping
    default void setSolsSolicitorRepresentativeName(
            @MappingTarget GrantOfRepresentationData caseData, ExceptionRecordOCRFields ocrField) {
        if ((caseData.getApplicationType() == ApplicationType.SOLICITORS) &&
                (StringUtils.isNotBlank(ocrField.getSolsSolicitorRepresentativeName()))) {
            String solicitorFullName = ocrField.getSolsSolicitorRepresentativeName();
            List<String> names = OCRFieldExtractor.splitFullname(solicitorFullName);
            if (names.size() > 2) {
                caseData.setSolsSOTSurname(names.get(names.size()-1));
                caseData.setSolsSOTForenames(String.join(" ", names.subList(0, names.size()-1)));
            } else if(names.size() == 1) {
                caseData.setSolsSOTSurname("");
                caseData.setSolsSOTForenames(names.get(0));
            } else {
                caseData.setSolsSOTSurname(names.get(1));
                caseData.setSolsSOTForenames(names.get(0));
            }
        }
    }

    @AfterMapping
    default void clearIhtFormOrReferenceIfNotSelected(
            @MappingTarget GrantOfRepresentationData caseData, ExceptionRecordOCRFields ocrField) {
        if (caseData.getIhtFormCompletedOnline() != null && caseData.getIhtFormCompletedOnline()) {
            caseData.setIhtFormId(null);
        }
        if (caseData.getIhtFormCompletedOnline() != null && !caseData.getIhtFormCompletedOnline()) {
            caseData.setIhtReferenceNumber(null);
        }
    }

    @AfterMapping
    @SuppressWarnings("Duplicates")
    default void setDerivedFamilyBooleans(
            @MappingTarget GrantOfRepresentationData caseData, ExceptionRecordOCRFields ocrField, GrantType grantType) {
        if (greaterThenZero(ocrField.getChildrenUnderEighteenSurvived())
                || greaterThenZero(ocrField.getChildrenOverEighteenSurvived())) {
            caseData.setChildrenSurvived(Boolean.TRUE);
        } else {
            caseData.setChildrenOverEighteenSurvivedText(null);
            caseData.setChildrenUnderEighteenSurvivedText(null);
        }
        if (greaterThenZero(ocrField.getChildrenDiedUnderEighteen())
                || greaterThenZero(ocrField.getChildrenDiedOverEighteen())) {
            caseData.setChildrenDied(Boolean.TRUE);
        } else {
            caseData.setChildrenDiedOverEighteenText(null);
            caseData.setChildrenDiedUnderEighteenText(null);
        }
        if (greaterThenZero(ocrField.getGrandChildrenSurvivedUnderEighteen())
                || greaterThenZero(ocrField.getGrandChildrenSurvivedOverEighteen())) {
            caseData.setGrandChildrenSurvived(Boolean.TRUE);
        } else {
            caseData.setGrandChildrenSurvivedOverEighteenText(null);
            caseData.setGrandChildrenSurvivedUnderEighteenText(null);
        }
        if (grantType.equals(GrantType.INTESTACY)) {
            if (greaterThenZero(ocrField.getParentsExistUnderEighteenSurvived())
                    || greaterThenZero(ocrField.getParentsExistOverEighteenSurvived())) {
                caseData.setParentsExistSurvived(Boolean.TRUE);
            } else {
                caseData.setParentsExistOverEighteenSurvived(null);
                caseData.setParentsExistUnderEighteenSurvived(null);
            }
            if (greaterThenZero(ocrField.getWholeBloodSiblingsSurvivedUnderEighteen())
                    || greaterThenZero(ocrField.getWholeBloodSiblingsSurvivedOverEighteen())) {
                caseData.setWholeBloodSiblingsSurvived(Boolean.TRUE);
            } else {
                caseData.setWholeBloodSiblingsSurvivedOverEighteen(null);
                caseData.setWholeBloodSiblingsSurvivedUnderEighteen(null);
            }
            if (greaterThenZero(ocrField.getWholeBloodSiblingsDiedUnderEighteen())
                    || greaterThenZero(ocrField.getWholeBloodSiblingsDiedOverEighteen())) {
                caseData.setWholeBloodSiblingsDied(Boolean.TRUE);
            } else {
                caseData.setWholeBloodSiblingsDiedOverEighteen(null);
                caseData.setWholeBloodSiblingsDiedUnderEighteen(null);
            }
            if (greaterThenZero(ocrField.getWholeBloodNeicesAndNephewsUnderEighteen())
                    || greaterThenZero(ocrField.getWholeBloodNeicesAndNephewsOverEighteen())) {
                caseData.setWholeBloodNeicesAndNephews(Boolean.TRUE);
            } else {
                caseData.setWholeBloodNeicesAndNephewsOverEighteen(null);
                caseData.setWholeBloodNeicesAndNephewsUnderEighteen(null);
            }
            if (greaterThenZero(ocrField.getHalfBloodSiblingsSurvivedUnderEighteen())
                    || greaterThenZero(ocrField.getHalfBloodSiblingsSurvivedOverEighteen())) {
                caseData.setHalfBloodSiblingsSurvived(Boolean.TRUE);
            } else {
                caseData.setHalfBloodSiblingsSurvivedOverEighteen(null);
                caseData.setHalfBloodSiblingsSurvivedUnderEighteen(null);
            }
            if (greaterThenZero(ocrField.getHalfBloodSiblingsDiedUnderEighteen())
                    || greaterThenZero(ocrField.getHalfBloodSiblingsDiedOverEighteen())) {
                caseData.setHalfBloodSiblingsDied(Boolean.TRUE);
            } else {
                caseData.setHalfBloodSiblingsDiedOverEighteen(null);
                caseData.setHalfBloodSiblingsDiedUnderEighteen(null);
            }
            if (greaterThenZero(ocrField.getHalfBloodNeicesAndNephewsUnderEighteen())
                    || greaterThenZero(ocrField.getHalfBloodNeicesAndNephewsOverEighteen())) {
                caseData.setHalfBloodNeicesAndNephews(Boolean.TRUE);
            } else {
                caseData.setHalfBloodNeicesAndNephewsOverEighteen(null);
                caseData.setHalfBloodNeicesAndNephewsUnderEighteen(null);
            }
            if (greaterThenZero(ocrField.getGrandparentsDiedUnderEighteen())
                    || greaterThenZero(ocrField.getGrandparentsDiedOverEighteen())) {
                caseData.setGrandparentsDied(Boolean.TRUE);
            } else {
                caseData.setGrandparentsDiedOverEighteen(null);
                caseData.setGrandparentsDiedUnderEighteen(null);
            }
            if (greaterThenZero(ocrField.getWholeBloodUnclesAndAuntsSurvivedUnderEighteen())
                    || greaterThenZero(ocrField.getWholeBloodUnclesAndAuntsSurvivedOverEighteen())) {
                caseData.setWholeBloodUnclesAndAuntsSurvived(Boolean.TRUE);
            } else {
                caseData.setWholeBloodUnclesAndAuntsSurvivedOverEighteen(null);
                caseData.setWholeBloodUnclesAndAuntsSurvivedUnderEighteen(null);
            }
            if (greaterThenZero(ocrField.getWholeBloodUnclesAndAuntsDiedUnderEighteen())
                    || greaterThenZero(ocrField.getWholeBloodUnclesAndAuntsDiedOverEighteen())) {
                caseData.setWholeBloodUnclesAndAuntsDied(Boolean.TRUE);
            } else {
                caseData.setWholeBloodUnclesAndAuntsDiedOverEighteen(null);
                caseData.setWholeBloodUnclesAndAuntsDiedUnderEighteen(null);
            }
            if (greaterThenZero(ocrField.getWholeBloodCousinsSurvivedUnderEighteen())
                    || greaterThenZero(ocrField.getWholeBloodCousinsSurvivedOverEighteen())) {
                caseData.setWholeBloodCousinsSurvived(Boolean.TRUE);
            } else {
                caseData.setWholeBloodCousinsSurvivedOverEighteen(null);
                caseData.setWholeBloodCousinsSurvivedUnderEighteen(null);
            }
            if (greaterThenZero(ocrField.getHalfBloodUnclesAndAuntsSurvivedUnderEighteen())
                    || greaterThenZero(ocrField.getHalfBloodUnclesAndAuntsSurvivedOverEighteen())) {
                caseData.setHalfBloodUnclesAndAuntsSurvived(Boolean.TRUE);
            } else {
                caseData.setHalfBloodUnclesAndAuntsSurvivedOverEighteen(null);
                caseData.setHalfBloodUnclesAndAuntsSurvivedUnderEighteen(null);
            }
            if (greaterThenZero(ocrField.getHalfBloodUnclesAndAuntsDiedUnderEighteen())
                    || greaterThenZero(ocrField.getHalfBloodUnclesAndAuntsDiedOverEighteen())) {
                caseData.setHalfBloodUnclesAndAuntsDied(Boolean.TRUE);
            } else {
                caseData.setHalfBloodUnclesAndAuntsDiedOverEighteen(null);
                caseData.setHalfBloodUnclesAndAuntsDiedUnderEighteen(null);
            }
            if (greaterThenZero(ocrField.getHalfBloodCousinsSurvivedUnderEighteen())
                    || greaterThenZero(ocrField.getHalfBloodCousinsSurvivedOverEighteen())) {
                caseData.setHalfBloodCousinsSurvived(Boolean.TRUE);
            } else {
                caseData.setHalfBloodCousinsSurvivedOverEighteen(null);
                caseData.setHalfBloodCousinsSurvivedUnderEighteen(null);
            }
        }
    }

    @AfterMapping
    default void setApplyingAsAnAttorneyBoolean(@MappingTarget GrantOfRepresentationData caseData, ExceptionRecordOCRFields ocrField) {
        if (caseData.getAttorneyOnBehalfOfNameAndAddress().size() > 0
                && StringUtils.isNotBlank(caseData.getAttorneyOnBehalfOfNameAndAddress().get(0).getValue().getName())) {
            caseData.setApplyingAsAnAttorney(Boolean.TRUE);
        } else {
            caseData.setApplyingAsAnAttorney(Boolean.FALSE);
        }
    }

    default boolean greaterThenZero(String field) {
        Integer integerValue = 0;
        if (field != null) {
            try {
                integerValue = Integer.valueOf(field);
            } catch (Exception e) {
                // do nothing
            }
        }
        return BooleanUtils.toBoolean(integerValue);
    }
}
