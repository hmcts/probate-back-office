package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToAdditionalExecutorsApplying;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToDeceasedAddress;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToDefaultLocalDate;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToMartialStatus;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToPrimaryApplicantAddress;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToYesOrNo;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;

@Mapper(componentModel = "spring",
        uses = {OCRFieldAddressMapper.class,
                OCRFieldAdditionalExecutorsApplyingMapper.class,
                OCRFieldDefaultLocalDateFieldMapper.class,
                OCRFieldYesOrNoMapper.class,
                OCRFieldMartialStatusMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ExceptionRecordGrantOfRepresentationMapper {
    @Mapping(target = "extraCopiesOfGrant", source = "extraCopiesOfGrant")
    @Mapping(target = "outsideUkGrantCopies", source = "outsideUKGrantCopies")

    /*
    // Missing from Commons GrantOfRepresentationData.java
    @Mapping(target = "applicationFeePaperForm", source = "applicationFeePaperForm")
    @Mapping(target = "feeForCopiesPaperForm", source = "feeForCopiesPaperForm")
    @Mapping(target = "totalFeePaperForm", source = "totalFeePaperForm")
    @Mapping(target = "paperPaymentMethod", source = "paperPaymentMethod")
    @Mapping(target = "paymentReferenceNumberPaperform", source = "paymentReferenceNumberPaperform")
    */

    @Mapping(target = "primaryApplicantForenames", source = "primaryApplicantForenames")
    @Mapping(target = "primaryApplicantSurname", source = "primaryApplicantSurname")
    @Mapping(target = "primaryApplicantAddress", source = "ocrFields", qualifiedBy = {ToPrimaryApplicantAddress.class})
    @Mapping(target = "primaryApplicantPhoneNumber", source = "primaryApplicantPhoneNumber")
    @Mapping(target = "primaryApplicantEmailAddress", source = "primaryApplicantEmailAddress")

    /*
    // Missing from Commons GrantOfRepresentationData.java
    @Mapping(target = "primaryApplicantSecondPhoneNumber", source = "primaryApplicantSecondPhoneNumber")
    */

    @Mapping(target = "executorsApplying", source = "ocrFields", qualifiedBy = {ToAdditionalExecutorsApplying.class})
    @Mapping(target = "deceasedForenames", source = "deceasedForenames")
    @Mapping(target = "deceasedSurname", source = "deceasedSurname")
    @Mapping(target = "deceasedAddress", source = "ocrFields", qualifiedBy = {ToDeceasedAddress.class})
    @Mapping(target = "deceasedDateOfBirth", source = "deceasedDateOfBirth", qualifiedBy = {ToDefaultLocalDate.class})
    @Mapping(target = "deceasedDateOfDeath", source = "deceasedDateOfDeath", qualifiedBy = {ToDefaultLocalDate.class})
    @Mapping(target = "deceasedAnyOtherNames", source = "deceasedAnyOtherNames", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "deceasedDomicileInEngWales", source = "deceasedDomicileInEngWales", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "deceasedMaritalStatus", source = "deceasedMartialStatus", qualifiedBy = {ToMartialStatus.class})

    /*
    // Missing from Commons GrantOfRepresentationData.java
    @Mapping(target = "dateOfMarriageOrCP", source = "dateOfMarriageOrCP", qualifiedBy = {ToDefaultLocalDate.class})
    @Mapping(target = "dateOfDivorcedCPJudicially", source = "dateOfDivorcedCPJudicially", qualifiedBy = {ToDefaultLocalDate.class})
    @Mapping(target = "courtOfDecree", source = "courtOfDecree")
    */

    @Mapping(target = "deceasedHasAssetsOutsideUK", source = "foreignAsset", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "assetsOutsideNetValue", source = "foreignAssetEstateValue")
    /*
    // Missing from Commons GrantOfRepresentationData.java
    @Mapping(target = "adopted", source = "adopted")
    @Mapping(target = "adoptiveRelatives_1_name", source = "adoptiveRelatives_1_name")
    @Mapping(target = "adoptiveRelatives_1_relationship", source = "adoptiveRelatives_1_relationship")
    @Mapping(target = "adoptiveRelatives_1_adoptedInOrOut", source = "adoptiveRelatives_1_adoptedInOrOut")
    @Mapping(target = "adoptiveRelatives_2_name", source = "adoptiveRelatives_2_name")
    @Mapping(target = "adoptiveRelatives_2_relationship", source = "adoptiveRelatives_2_relationship")
    @Mapping(target = "adoptiveRelatives_2_adoptedInOrOut", source = "adoptiveRelatives_2_adoptedInOrOut")
    @Mapping(target = "adoptiveRelatives_3_name", source = "adoptiveRelatives_3_name")
    @Mapping(target = "adoptiveRelatives_3_relationship", source = "adoptiveRelatives_3_relationship")
    @Mapping(target = "adoptiveRelatives_3_adoptedInOrOut", source = "adoptiveRelatives_3_adoptedInOrOut")
    @Mapping(target = "adoptiveRelatives_4_name", source = "adoptiveRelatives_4_name")
    @Mapping(target = "adoptiveRelatives_4_relationship", source = "adoptiveRelatives_4_relationship")
    @Mapping(target = "adoptiveRelatives_4_adoptedInOrOut", source = "adoptiveRelatives_4_adoptedInOrOut")
    @Mapping(target = "adoptiveRelatives_5_name", source = "adoptiveRelatives_5_name")
    @Mapping(target = "adoptiveRelatives_5_relationship", source = "adoptiveRelatives_5_relationship")
    @Mapping(target = "adoptiveRelatives_5_adoptedInOrOut", source = "adoptiveRelatives_5_adoptedInOrOut")
    @Mapping(target = "spouseOrPartner", source = "spouseOrPartner")
    */

    @Mapping(target = "childrenUnderEighteenSurvived", source = "childrenUnderEighteenSurvived", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "childrenOverEighteenSurvived", source = "childrenOverEighteenSurvived", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "childrenDiedUnderEighteen", source = "childrenDiedUnderEighteen", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "childrenDiedOverEighteen", source = "childrenDiedOverEighteen", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "grandChildrenSurvivedUnderEighteen", source = "grandChildrenSurvivedUnderEighteen", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "grandChildrenSurvivedOverEighteen", source = "grandChildrenSurvivedOverEighteen", qualifiedBy = {ToYesOrNo.class})

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
    @Mapping(target = "willDate", source = "willDate")
    @Mapping(target = "willsOutsideOfUK", source = "willsOutsideOfUK")
    @Mapping(target = "willGiftUnderEighteen", source = "willGiftUnderEighteen")
    @Mapping(target = "executorsNotApplying0notApplyingExecutorName", source = "executorsNotApplying0notApplyingExecutorName")
    @Mapping(target = "executorsNotApplying0notApplyingExecutorReason", source = "executorsNotApplying0notApplyingExecutorReason")
    @Mapping(target = "executorsNotApplying1notApplyingExecutorName", source = "executorsNotApplying1notApplyingExecutorName")
    @Mapping(target = "executorsNotApplying1notApplyingExecutorReason", source = "executorsNotApplying1notApplyingExecutorReason")
    @Mapping(target = "executorsNotApplying2notApplyingExecutorName", source = "executorsNotApplying2notApplyingExecutorName")
    @Mapping(target = "executorsNotApplying2notApplyingExecutorReason", source = "executorsNotApplying2notApplyingExecutorReason")
    @Mapping(target = "notifiedApplicants", source = "notifiedApplicants")
     */

    @Mapping(target = "ihtFormCompletedOnline", source = "ihtFormCompletedOnline", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "ihtReferenceNumber", source = "ihtReferenceNumber")
    @Mapping(target = "ihtFormId", source = "ihtFormId")
    @Mapping(target = "ihtGrossValue", source = "ihtGrossValue")
    @Mapping(target = "ihtNetValue", source = "ihtNetValue")
    GrantOfRepresentationData toCcdData(ExceptionRecordOCRFields ocrFields);
}