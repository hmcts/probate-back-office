package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

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
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToAttorneyOnBehalfOfAddress;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToDeceasedAddress;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToDefaultLocalDate;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToIHTFormId;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToMartialStatus;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToPennies;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToPrimaryApplicantAddress;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToRelationship;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToRelationshipOther;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToYesOrNo;
import uk.gov.hmcts.reform.probate.model.cases.ApplicationType;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;

@Mapper(componentModel = "spring",
        imports = {ApplicationType.class},
        uses = {OCRFieldAddressMapper.class,
                OCRFieldAdditionalExecutorsApplyingMapper.class,
                OCRFieldAdditionalExecutorsNotApplyingMapper.class,
                OCRFieldDefaultLocalDateFieldMapper.class,
                OCRFieldYesOrNoMapper.class,
                OCRFieldMartialStatusMapper.class,
                OCRFieldAdoptiveRelativesMapper.class,
                OCRFieldIhtMoneyMapper.class,
                OCRFieldRelationshipMapper.class
        },
        unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ExceptionRecordGrantOfRepresentationMapper {
    @Mapping(target = "extraCopiesOfGrant", source = "extraCopiesOfGrant")
    @Mapping(target = "outsideUkGrantCopies", source = "outsideUKGrantCopies")

    @Mapping(target = "applicationFeePaperForm", source = "applicationFeePaperForm", qualifiedBy = {ToPennies.class})
    @Mapping(target = "feeForCopiesPaperForm", source = "feeForCopiesPaperForm", qualifiedBy = {ToPennies.class})
    @Mapping(target = "totalFeePaperForm", source = "totalFeePaperForm", qualifiedBy = {ToPennies.class})
    @Mapping(target = "paperPaymentMethod", expression = "java(ocrFields.getPaperPaymentMethod() == null ? null : \"debitOrCredit\")")
    @Mapping(target = "paymentReferenceNumberPaperform", source = "paymentReferenceNumberPaperform")

    @Mapping(target = "primaryApplicantForenames", source = "primaryApplicantForenames")
    @Mapping(target = "primaryApplicantSurname", source = "primaryApplicantSurname")
    @Mapping(target = "primaryApplicantAddress", source = "ocrFields", qualifiedBy = {ToPrimaryApplicantAddress.class})
    @Mapping(target = "primaryApplicantPhoneNumber", source = "primaryApplicantPhoneNumber")
    @Mapping(target = "primaryApplicantEmailAddress", source = "primaryApplicantEmailAddress")
    @Mapping(target = "primaryApplicantSecondPhoneNumber", source = "primaryApplicantSecondPhoneNumber")

    @Mapping(target = "executorsApplying", source = "ocrFields", qualifiedBy = {ToAdditionalExecutorsApplying.class})
    @Mapping(target = "deceasedForenames", source = "deceasedForenames")
    @Mapping(target = "deceasedSurname", source = "deceasedSurname")
    @Mapping(target = "deceasedAddress", source = "ocrFields", qualifiedBy = {ToDeceasedAddress.class})
    @Mapping(target = "deceasedDateOfBirth", source = "deceasedDateOfBirth", qualifiedBy = {ToDefaultLocalDate.class})
    @Mapping(target = "deceasedDateOfDeath", source = "deceasedDateOfDeath", qualifiedBy = {ToDefaultLocalDate.class})
    @Mapping(target = "deceasedAnyOtherNames", source = "deceasedAnyOtherNames", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "deceasedDomicileInEngWales", source = "deceasedDomicileInEngWales", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "deceasedMaritalStatus", source = "deceasedMartialStatus", qualifiedBy = {ToMartialStatus.class})

    @Mapping(target = "dateOfMarriageOrCP", source = "dateOfMarriageOrCP", qualifiedBy = {ToDefaultLocalDate.class})
    @Mapping(target = "dateOfDivorcedCPJudicially", source = "dateOfDivorcedCPJudicially", qualifiedBy = {ToDefaultLocalDate.class})
    @Mapping(target = "courtOfDecree", source = "courtOfDecree")

    @Mapping(target = "foreignAsset", source = "foreignAsset", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "foreignAssetEstateValue", source = "foreignAssetEstateValue", qualifiedBy = {ToPennies.class})

    @Mapping(target = "adopted", source = "adopted", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "adoptiveRelatives", source = "ocrFields", qualifiedBy = {ToAdoptiveRelatives.class})
    @Mapping(target = "spouseOrPartner", source = "spouseOrPartner", qualifiedBy = {ToYesOrNo.class})

    // Following fields captured as text but used as booleans in orchestrator
    @Mapping(target = "childrenSurvived", ignore = true)
    @Mapping(target = "childrenUnderEighteenSurvived", source = "childrenUnderEighteenSurvived")
    @Mapping(target = "childrenOverEighteenSurvived", source = "childrenOverEighteenSurvived")
    @Mapping(target = "childrenDied", ignore = true)
    @Mapping(target = "childrenDiedUnderEighteen", source = "childrenDiedUnderEighteen")
    @Mapping(target = "childrenDiedOverEighteen", source = "childrenDiedOverEighteen")
    @Mapping(target = "grandChildrenSurvived", ignore = true)
    @Mapping(target = "grandChildrenSurvivedUnderEighteen", source = "grandChildrenSurvivedUnderEighteen")
    @Mapping(target = "grandChildrenSurvivedOverEighteen", source = "grandChildrenSurvivedOverEighteen")

    // Ignored fields defined in after mapping section.
    @Mapping(target = "parentsExistSurvived", ignore = true)
    @Mapping(target = "parentsExistUnderEighteenSurvived", source = "parentsExistUnderEighteenSurvived")
    @Mapping(target = "parentsExistOverEighteenSurvived", source = "parentsExistOverEighteenSurvived")

    @Mapping(target = "wholeBloodSiblingsSurvived", ignore = true)
    @Mapping(target = "wholeBloodSiblingsSurvivedUnderEighteen", source = "wholeBloodSiblingsSurvivedUnderEighteen")
    @Mapping(target = "wholeBloodSiblingsSurvivedOverEighteen", source = "wholeBloodSiblingsSurvivedOverEighteen")

    @Mapping(target = "wholeBloodSiblingsDied", ignore = true)
    @Mapping(target = "wholeBloodSiblingsDiedUnderEighteen", source = "wholeBloodSiblingsDiedUnderEighteen")
    @Mapping(target = "wholeBloodSiblingsDiedOverEighteen", source = "wholeBloodSiblingsDiedOverEighteen")


    @Mapping(target = "wholeBloodNeicesAndNephewsUnderEighteen", source = "wholeBloodNeicesAndNephewsUnderEighteen")
    @Mapping(target = "wholeBloodNeicesAndNephewsOverEighteen", source = "wholeBloodNeicesAndNephewsOverEighteen")


    @Mapping(target = "halfBloodSiblingsSurvivedUnderEighteen", source = "halfBloodSiblingsSurvivedUnderEighteen")
    @Mapping(target = "halfBloodSiblingsSurvivedOverEighteen", source = "halfBloodSiblingsSurvivedOverEighteen")


    @Mapping(target = "halfBloodSiblingsDiedUnderEighteen", source = "halfBloodSiblingsDiedUnderEighteen")
    @Mapping(target = "halfBloodSiblingsDiedOverEighteen", source = "halfBloodSiblingsDiedOverEighteen")


    @Mapping(target = "halfBloodNeicesAndNephewsUnderEighteen", source = "halfBloodNeicesAndNephewsUnderEighteen")
    @Mapping(target = "halfBloodNeicesAndNephewsOverEighteen", source = "halfBloodNeicesAndNephewsOverEighteen")


    @Mapping(target = "grandparentsDiedUnderEighteen", source = "grandparentsDiedUnderEighteen")
    @Mapping(target = "grandparentsDiedOverEighteen", source = "grandparentsDiedOverEighteen")


    @Mapping(target = "wholeBloodUnclesAndAuntsSurvivedUnderEighteen", source = "wholeBloodUnclesAndAuntsSurvivedUnderEighteen")
    @Mapping(target = "wholeBloodUnclesAndAuntsSurvivedOverEighteen", source = "wholeBloodUnclesAndAuntsSurvivedOverEighteen")


    @Mapping(target = "wholeBloodUnclesAndAuntsDiedUnderEighteen", source = "wholeBloodUnclesAndAuntsDiedUnderEighteen")
    @Mapping(target = "wholeBloodUnclesAndAuntsDiedOverEighteen", source = "wholeBloodUnclesAndAuntsDiedOverEighteen")


    @Mapping(target = "wholeBloodCousinsSurvivedUnderEighteen", source = "wholeBloodCousinsSurvivedUnderEighteen")
    @Mapping(target = "wholeBloodCousinsSurvivedOverEighteen", source = "wholeBloodCousinsSurvivedOverEighteen")


    @Mapping(target = "halfBloodUnclesAndAuntsSurvivedUnderEighteen", source = "halfBloodUnclesAndAuntsSurvivedUnderEighteen")
    @Mapping(target = "halfBloodUnclesAndAuntsSurvivedOverEighteen", source = "halfBloodUnclesAndAuntsSurvivedOverEighteen")


    @Mapping(target = "halfBloodUnclesAndAuntsDiedUnderEighteen", source = "halfBloodUnclesAndAuntsDiedUnderEighteen")
    @Mapping(target = "halfBloodUnclesAndAuntsDiedOverEighteen", source = "halfBloodUnclesAndAuntsDiedOverEighteen")


    @Mapping(target = "halfBloodCousinsSurvivedUnderEighteen", source = "halfBloodCousinsSurvivedUnderEighteen")
    @Mapping(target = "halfBloodCousinsSurvivedOverEighteen", source = "halfBloodCousinsSurvivedOverEighteen")

    @Mapping(target = "primaryApplicantRelationshipToDeceased",
            source = "primaryApplicantRelationshipToDeceased", qualifiedBy = {ToRelationship.class})
    @Mapping(target = "paRelationshipToDeceasedOther",
            source = "primaryApplicantRelationshipToDeceased", qualifiedBy = {ToRelationshipOther.class})

    @Mapping(target = "applyingAsAnAttorney", source = "applyingAsAnAttorney", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "attorneyNamesAndAddress", source = "ocrFields", qualifiedBy = {ToAttorneyOnBehalfOfAddress.class})
    @Mapping(target = "mentalCapacity", source = "mentalCapacity", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "courtOfProtection", source = "courtOfProtection", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "epaOrLpa", source = "epaOrLpa", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "epaRegistered", source = "epaRegistered", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "domicilityCountry", source = "domicilityCountry")

    // domicilityIHTCert defined in after mapping section If domicilityEntrustingDocument or domicilitySuccessionIHTCert is true
    @Mapping(target = "domicilityIHTCert", ignore = true)

    @Mapping(target = "executorsNotApplying", source = "ocrFields", qualifiedBy = {ToAdditionalExecutorsNotApplying.class})
    @Mapping(target = "willHasCodicils", source = "willHasCodicils", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "deceasedMarriedAfterWillOrCodicilDate", source = "deceasedMarriedAfterWillOrCodicilDate",
            qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "willDate", source = "willDate", qualifiedBy = {ToDefaultLocalDate.class})
    @Mapping(target = "willsOutsideOfUK", source = "willsOutsideOfUK", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "willGiftUnderEighteen", source = "willGiftUnderEighteen", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "notifiedApplicants", source = "notifiedApplicants", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "ihtFormCompletedOnline", source = "ihtFormCompletedOnline", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "ihtReferenceNumber", source = "ihtReferenceNumber")
    @Mapping(target = "ihtFormId", source = "ihtFormId", qualifiedBy = {ToIHTFormId.class})
    @Mapping(target = "ihtGrossValue", source = "ihtGrossValue", qualifiedBy = {ToPennies.class})
    @Mapping(target = "ihtNetValue", source = "ihtNetValue", qualifiedBy = {ToPennies.class})

    @Mapping(target = "paperForm", expression = "java(Boolean.TRUE)")
    @Mapping(target = "applicationType", expression = "java(ApplicationType.PERSONAL)")
    GrantOfRepresentationData toCcdData(ExceptionRecordOCRFields ocrFields);

    @AfterMapping
    default void setDomicilityIHTCert(@MappingTarget GrantOfRepresentationData caseData, ExceptionRecordOCRFields ocrField) {
        if (BooleanUtils.toBoolean(ocrField.getDomicilityEntrustingDocument())
                || BooleanUtils.toBoolean(ocrField.getDomicilitySuccessionIHTCert())) {
            caseData.setDomicilityIHTCert(Boolean.TRUE);
        }
    }

    @AfterMapping
    default void setDiedOrSurvivedBoolean(@MappingTarget GrantOfRepresentationData caseData, ExceptionRecordOCRFields ocrField) {
        if (BooleanUtils.toBoolean(caseData.getChildrenUnderEighteenSurvived().toString())
                || BooleanUtils.toBoolean(caseData.getChildrenOverEighteenSurvived().toString())) {
            caseData.setChildrenSurvived(Boolean.TRUE);
        } else {
            caseData.setChildrenSurvived(Boolean.FALSE);
        }
        if (BooleanUtils.toBoolean(caseData.getChildrenDiedUnderEighteen().toString())
                || BooleanUtils.toBoolean(caseData.getChildrenDiedOverEighteen().toString())) {
            caseData.setChildrenDied(Boolean.TRUE);
        } else {
            caseData.setChildrenDied(Boolean.FALSE);
        }
        if (BooleanUtils.toBoolean(caseData.getGrandChildrenSurvivedUnderEighteen().toString())
                || BooleanUtils.toBoolean(caseData.getGrandChildrenSurvivedOverEighteen().toString())) {
            caseData.setGrandChildrenSurvived(Boolean.TRUE);
        } else {
            caseData.setGrandChildrenSurvived(Boolean.FALSE);
        }
    }
}