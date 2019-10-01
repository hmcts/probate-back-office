package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CaveatsExceptionRecordMapper {
    /*
    @Mapping(target = "outsideUKGrantCopies", source = "ocrFields, outsideUKGrantCopies", qualifiedBy = {ToStraightCcdFieldMember.class})
    @Mapping(target = "applicationFeePaperForm", source = "ocrFields, applicationFeePaperForm",
    qualifiedBy = {ToStraightCcdFieldMember.class})
    @Mapping(target = "feeForCopiesPaperForm", source = "ocrFields, feeForCopiesPaperForm", qualifiedBy = {ToStraightCcdFieldMember.class})
    //@Mapping(target = "totalFeePaperForm", source = "ocrFields, totalFeePaperForm", qualifiedBy = {ToStraightCcdFieldMember.class})
    @Mapping(target = "paperPaymentMethod", source = "ocrFields, paperPaymentMethod", qualifiedBy = {ToStraightCcdFieldMember.class})
    //@Mapping(target = "paymentReferenceNumberPaperform", source = "ocrFields, paymentReferenceNumberPaperform",
    qualifiedBy = {ToStraightCcdFieldMember.class})
    @Mapping(target = "primaryApplicantForenames", source = "ocrFields, primaryApplicantForenames",
    qualifiedBy = {ToStraightCcdFieldMember.class})
    @Mapping(target = "primaryApplicantSurname", source = "ocrFields, primaryApplicantSurname",
    qualifiedBy = {ToStraightCcdFieldMember.class})
    @Mapping(target = "primaryApplicantHasAlias", source = "ocrFields, primaryApplicantHasAlias",
    qualifiedBy = {ToStraightCcdFieldMember.class})
    @Mapping(target = "primaryApplicantAlias", source = "ocrFields, primaryApplicantAlias",
    qualifiedBy = {ToStraightCcdFieldMember.class})
    @Mapping(target = "primaryApplicantPhoneNumber", source = "ocrFields, primaryApplicantPhoneNumber",
    qualifiedBy = {ToStraightCcdFieldMember.class})
    @Mapping(target = "primaryApplicantSecondPhoneNumber", source = "ocrFields, primaryApplicantSecondPhoneNumber",
    qualifiedBy = {ToStraightCcdFieldMember.class})
    @Mapping(target = "primaryApplicantEmailAddress", source = "ocrFields, primaryApplicantEmailAddress",
    qualifiedBy = {ToStraightCcdFieldMember.class})
    @Mapping(target = "primaryApplicantAddress.addressLine1", source = "ocrFields, primaryApplicantAddress_AddressLine1",
    qualifiedBy = {ToStraightCcdFieldMember.class})
    @Mapping(target = "primaryApplicantAddress.addressLine2", source = "ocrFields, primaryApplicantAddress_AddressLine2",
    qualifiedBy = {ToStraightCcdFieldMember.class})
    @Mapping(target = "primaryApplicantAddress.postTown", source = "ocrFields, primaryApplicantAddress_PostTown",
    qualifiedBy = {ToStraightCcdFieldMember.class})
    @Mapping(target = "primaryApplicantAddress.county", source = "ocrFields, primaryApplicantAddress_County",
    qualifiedBy = {ToStraightCcdFieldMember.class})
    @Mapping(target = "primaryApplicantAddress.postCode", source = "ocrFields, primaryApplicantAddress_PostCode",
    qualifiedBy = {ToStraightCcdFieldMember.class})
    @Mapping(target = "deceasedForenames", source = "ocrFields, deceasedForenames", qualifiedBy = {ToStraightCcdFieldMember.class})
    @Mapping(target = "deceasedSurname", source = "ocrFields, deceasedSurname", qualifiedBy = {ToStraightCcdFieldMember.class})
    @Mapping(target = "deceasedAddress.addressLine1", source = "ocrFields, deceasedAddress_AddressLine1",
    qualifiedBy = {ToStraightCcdFieldMember.class})
    @Mapping(target = "deceasedAddress.addressLine2", source = "ocrFields, deceasedAddress_AddressLine2",
    qualifiedBy = {ToStraightCcdFieldMember.class})
    @Mapping(target = "deceasedAddress.postTown", source = "ocrFields, deceasedAddress_PostTown",
    qualifiedBy = {ToStraightCcdFieldMember.class})
    @Mapping(target = "deceasedAddress.county", source = "ocrFields, deceasedAddress_County",
    qualifiedBy = {ToStraightCcdFieldMember.class})
    @Mapping(target = "deceasedAddress.postCode", source = "ocrFields, deceasedAddress_PostCode",
    qualifiedBy = {ToStraightCcdFieldMember.class})
    @Mapping(target = "deceasedAnyOtherNames", source = "ocrFields, deceasedAnyOtherNames", qualifiedBy = {ToStraightCcdFieldMember.class})
    @Mapping(target = "deceasedDomicileInEngWales", source = "ocrFields, deceasedDomicileInEngWales",
    qualifiedBy = {ToStraightCcdFieldMember.class})
    @Mapping(target = "deceasedMartialStatus", source = "ocrFields, deceasedMartialStatus", qualifiedBy = {ToStraightCcdFieldMember.class})
    @Mapping(target = "dateOfMarriageOrCP", source = "ocrFields, dateOfMarriageOrCP", qualifiedBy = {ToStraightCcdFieldMember.class})
    @Mapping(target = "dateOfDivorcedCPJudicially", source = "ocrFields, dateOfDivorcedCPJudicially",
    qualifiedBy = {ToStraightCcdFieldMember.class})
    @Mapping(target = "courtOfDecree", source = "ocrFields, courtOfDecree", qualifiedBy = {ToStraightCcdFieldMember.class})
    @Mapping(target = "foreignAsset", source = "ocrFields, foreignAsset", qualifiedBy = {ToStraightCcdFieldMember.class})
    @Mapping(target = "foreignAssetEstateValue", source = "ocrFields, foreignAssetEstateValue",
    qualifiedBy = {ToStraightCcdFieldMember.class})
    @Mapping(target = "adopted", source = "ocrFields, adopted", qualifiedBy = {ToStraightCcdFieldMember.class})
    //@Mapping(target = "willHasCodicils", source = "ocrFields, willHasCodicils", qualifiedBy = {ToStraightCcdFieldMember.class})
    @Mapping(target = "willsOutsideOfUK", source = "ocrFields, willsOutsideOfUK", qualifiedBy = {ToStraightCcdFieldMember.class})
    @Mapping(target = "deceasedMarriedAfterWillOrCodicilDate", source = "ocrFields, deceasedMarriedAfterWillOrCodicilDate",
    qualifiedBy = {ToStraightCcdFieldMember.class})
    @Mapping(target = "willGiftUnderEighteen", source = "ocrFields, willGiftUnderEighteen", qualifiedBy = {ToStraightCcdFieldMember.class})
    @Mapping(target = "notifiedApplicants", source = "ocrFields, notifiedApplicants", qualifiedBy = {ToStraightCcdFieldMember.class})
    @Mapping(target = "spouseOrPartner", source = "ocrFields, spouseOrPartner", qualifiedBy = {ToStraightCcdFieldMember.class})
    @Mapping(target = "childrenUnderEighteenSurvived", source = "ocrFields, childrenUnderEighteenSurvived",
    qualifiedBy = {ToStraightCcdFieldMember.class})
    @Mapping(target = "childrenOverEighteenSurvived", source = "ocrFields, childrenOverEighteenSurvived",
    qualifiedBy = {ToStraightCcdFieldMember.class})
    @Mapping(target = "childrenDiedUnderEighteen", source = "ocrFields, childrenDiedUnderEighteen",
    qualifiedBy = {ToStraightCcdFieldMember.class})
    @Mapping(target = "childrenDiedOverEighteen", source = "ocrFields, childrenDiedOverEighteen",
    qualifiedBy = {ToStraightCcdFieldMember.class})
    @Mapping(target = "grandChildrenSurvivedUnderEighteen", source = "ocrFields, grandChildrenSurvivedUnderEighteen",
    qualifiedBy = {ToStraightCcdFieldMember.class})
    @Mapping(target = "grandChildrenSurvivedOverEighteen", source = "ocrFields, grandChildrenSurvivedOverEighteen",
    qualifiedBy = {ToStraightCcdFieldMember.class})
    @Mapping(target = "primaryApplicantRelationshipToDeceased", source = "ocrFields, primaryApplicantRelationshipToDeceased",
    qualifiedBy = {ToStraightCcdFieldMember.class})
    @Mapping(target = "applyingAsAnAttorney", source = "ocrFields, applyingAsAnAttorney", qualifiedBy = {ToStraightCcdFieldMember.class})
    @Mapping(target = "mentalCapacity", source = "ocrFields, mentalCapacity", qualifiedBy = {ToStraightCcdFieldMember.class})
    @Mapping(target = "courtOfProtection", source = "ocrFields, courtOfProtection", qualifiedBy = {ToStraightCcdFieldMember.class})
    @Mapping(target = "epaOrLpa", source = "ocrFields, epaOrLpa", qualifiedBy = {ToStraightCcdFieldMember.class})
    @Mapping(target = "epaRegistered", source = "ocrFields, epaRegistered", qualifiedBy = {ToStraightCcdFieldMember.class})
    @Mapping(target = "domicilityCountry", source = "ocrFields, domicilityCountry", qualifiedBy = {ToStraightCcdFieldMember.class})
    @Mapping(target = "ihtFormCompletedOnline", source = "ocrFields, ihtFormCompletedOnline",
    qualifiedBy = {ToStraightCcdFieldMember.class})
    @Mapping(target = "ihtReferenceNumber", source = "ocrFields, ihtReferenceNumber", qualifiedBy = {ToStraightCcdFieldMember.class})
    @Mapping(target = "ihtGrossValue", source = "ocrFields, ihtGrossValue", qualifiedBy = {ToStraightCcdFieldMember.class})
    @Mapping(target = "ihtNetValue", source = "ocrFields, ihtNetValue", qualifiedBy = {ToStraightCcdFieldMember.class})
    @Mapping(target = "ihtFormId", source = "ocrFields, ihtFormId", qualifiedBy = {ToStraightCcdFieldMember.class})
    @Mapping(target = "executorsApplying", source = "ocrFields", qualifiedBy = {ToAdditionalExecutorApplying.class})
    //@Mapping(target = "deceasedDateOfBirth", source = "ocrFields, deceasedDateOfBirth",
    qualifiedBy = {ToStraightCcdFieldMember.class})//should be a date one
     */
    //CaseData toCcdData(ExceptionRecordOCRFields caveatsER);
    //CaseData toCcdData(ExceptionRecordOCRFields caveatsER);
}