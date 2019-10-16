package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToAdditionalExecutorsApplying;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToAdoptiveRelatives;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToDeceasedAddress;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToDefaultLocalDate;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToMartialStatus;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToPrimaryApplicantAddress;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToYesOrNo;
import uk.gov.hmcts.reform.probate.model.ProbateType;
import uk.gov.hmcts.reform.probate.model.cases.ApplicationType;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;

@Mapper(componentModel = "spring",
        imports = {ApplicationType.class},
        uses = {OCRFieldAddressMapper.class,
                OCRFieldAdditionalExecutorsApplyingMapper.class,
                OCRFieldDefaultLocalDateFieldMapper.class,
                OCRFieldYesOrNoMapper.class,
                OCRFieldMartialStatusMapper.class,
                OCRFieldAdoptiveRelativesMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ExceptionRecordGrantOfRepresentationMapper {
    @Mapping(target = "extraCopiesOfGrant", source = "extraCopiesOfGrant")
    @Mapping(target = "outsideUkGrantCopies", source = "outsideUKGrantCopies")

    @Mapping(target = "applicationFeePaperForm", source = "applicationFeePaperForm")
    @Mapping(target = "feeForCopiesPaperForm", source = "feeForCopiesPaperForm")
    @Mapping(target = "totalFeePaperForm", source = "totalFeePaperForm")
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

    @Mapping(target = "deceasedHasAssetsOutsideUK", source = "foreignAsset", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "assetsOutsideNetValue", source = "foreignAssetEstateValue")

    @Mapping(target = "adopted", source = "adopted", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "adoptiveRelatives", source = "ocrFields", qualifiedBy = {ToAdoptiveRelatives.class})
    @Mapping(target = "spouseOrPartner", source = "spouseOrPartner", qualifiedBy = {ToYesOrNo.class})

    // These are boolean in commons but are captured as numerical in the forms?
    @Mapping(target = "childrenUnderEighteenSurvived", source = "childrenUnderEighteenSurvived", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "childrenOverEighteenSurvived", source = "childrenOverEighteenSurvived", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "childrenDiedUnderEighteen", source = "childrenDiedUnderEighteen", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "childrenDiedOverEighteen", source = "childrenDiedOverEighteen", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "grandChildrenSurvivedUnderEighteen", source = "grandChildrenSurvivedUnderEighteen", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "grandChildrenSurvivedOverEighteen", source = "grandChildrenSurvivedOverEighteen", qualifiedBy = {ToYesOrNo.class})

    @Mapping(target = "parentsExistUnderEighteenSurvived", source = "grandChildrenSurvivedOverEighteen", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "parentsExistOverEighteenSurvived", source = "grandChildrenSurvivedOverEighteen", qualifiedBy = {ToYesOrNo.class})

    @Mapping(target = "wholeBloodSiblingsSurvived", expression = "java("
            + "Integer.parseInt(ocrFields.getWholeBloodSiblingsSurvivedUnderEighteen()) > 0  || "
            + "Integer.parseInt(ocrFields.getWholeBloodSiblingsSurvivedOverEighteen()) > 0 ? Boolean.TRUE : Boolean.FALSE)")
    @Mapping(target = "wholeBloodSiblingsSurvivedUnderEighteen", source = "wholeBloodSiblingsSurvivedUnderEighteen")
    @Mapping(target = "wholeBloodSiblingsSurvivedOverEighteen", source = "wholeBloodSiblingsSurvivedOverEighteen")

    /*
    // Missing from Commons GrantOfRepresentationData.java
    @Mapping(target = "parentsExistUnderEighteenSurvived", source = "parentsExistUnderEighteenSurvived")
    @Mapping(target = "parentsExistOverEighteenSurvived", source = "parentsExistOverEighteenSurvived")
    @Mapping(target = "wholeBloodSiblingsSurvivedUnderEighteen", source = "wholeBloodSiblingsSurvivedUnderEighteen")
    @Mapping(target = "wholeBloodSiblingsSurvivedOverEighteen", source = "wholeBloodSiblingsSurvivedOverEighteen")
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
    */

    /*
    @Mapping(target = "primaryApplicantRelationshipToDeceased", source = "primaryApplicantRelationshipToDeceased")
    @Mapping(target = "secondApplicantRelationshipToDeceased", source = "secondApplicantRelationshipToDeceased")
    @Mapping(target = "thirdApplicantRelationshipToDeceased", source = "thirdApplicantRelationshipToDeceased")
    @Mapping(target = "fourthApplicantRelationshipToDeceased", source = "fourthApplicantRelationshipToDeceased")
    @Mapping(target = "applyingAsAnAttorney", source = "applyingAsAnAttorney")
    @Mapping(target = "attorneyOnBehalfOfName", source = "attorneyOnBehalfOfName")
    @Mapping(target = "attorneyOnBehalfOfAddressLine1", source = "attorneyOnBehalfOfAddressLine1")
    @Mapping(target = "attorneyOnBehalfOfAddressLine2", source = "attorneyOnBehalfOfAddressLine2")
    @Mapping(target = "attorneyOnBehalfOfAddressTown", source = "attorneyOnBehalfOfAddressTown")
    @Mapping(target = "attorneyOnBehalfOfAddressCounty", source = "attorneyOnBehalfOfAddressCounty")
    @Mapping(target = "attorneyOnBehalfOfAddressPostCode", source = "attorneyOnBehalfOfAddressPostCode")
    @Mapping(target = "mentalCapacity", source = "mentalCapacity")
    @Mapping(target = "courtOfProtection", source = "courtOfProtection")
    @Mapping(target = "epaOrLpa", source = "epaOrLpa")
    @Mapping(target = "epaRegistered", source = "epaRegistered")
    @Mapping(target = "domicilityCountry", source = "domicilityCountry")
    @Mapping(target = "domicilityEntrustingDocument", source = "domicilityEntrustingDocument")
    @Mapping(target = "domicilitySuccessionIHTCert", source = "domicilitySuccessionIHTCert")
    */

    @Mapping(target = "willHasCodicils", source = "willHasCodicils", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "deceasedMarriedAfterWillOrCodicilDate", source = "deceasedMarriedAfterWillOrCodicilDate",
            qualifiedBy = {ToYesOrNo.class})

    /*
    @Mapping(target = "executorsNotApplying0notApplyingExecutorName", source = "executorsNotApplying0notApplyingExecutorName")
    @Mapping(target = "executorsNotApplying0notApplyingExecutorReason", source = "executorsNotApplying0notApplyingExecutorReason")
    @Mapping(target = "executorsNotApplying1notApplyingExecutorName", source = "executorsNotApplying1notApplyingExecutorName")
    @Mapping(target = "executorsNotApplying1notApplyingExecutorReason", source = "executorsNotApplying1notApplyingExecutorReason")
    @Mapping(target = "executorsNotApplying2notApplyingExecutorName", source = "executorsNotApplying2notApplyingExecutorName")
    @Mapping(target = "executorsNotApplying2notApplyingExecutorReason", source = "executorsNotApplying2notApplyingExecutorReason")
    */

    @Mapping(target = "willDate", source = "willDate", qualifiedBy = {ToDefaultLocalDate.class})
    @Mapping(target = "willsOutsideOfUK", source = "willsOutsideOfUK", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "willGiftUnderEighteen", source = "willGiftUnderEighteen", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "notifiedApplicants", source = "notifiedApplicants", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "ihtFormCompletedOnline", source = "ihtFormCompletedOnline", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "ihtReferenceNumber", source = "ihtReferenceNumber")
    @Mapping(target = "ihtFormId", source = "ihtFormId")
    @Mapping(target = "ihtGrossValue", source = "ihtGrossValue")
    @Mapping(target = "ihtNetValue", source = "ihtNetValue")

    @Mapping(target = "paperForm", expression = "java(Boolean.TRUE)")
    @Mapping(target = "applicationType", expression = "java(ApplicationType.PERSONAL)")

    GrantOfRepresentationData toCcdData(ExceptionRecordOCRFields ocrFields);
}