package uk.gov.hmcts.probate.service.ocr.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ocr.OCRField;
import uk.gov.hmcts.probate.service.ocr.mapper.qualifiers.ToAdditionalExecutorApplying;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ExceptionRecordMapper {
    @Mapping(target = "extraCopiesOfGrant", source = "extraCopiesOfGrant")
    @Mapping(target = "outsideUKGrantCopies", source = "outsideUKGrantCopies")
    @Mapping(target = "applicationFeePaperForm", source = "applicationFeePaperForm")
    @Mapping(target = "feeForCopiesPaperForm", source = "feeForCopiesPaperForm")
    @Mapping(target = "totalFeePaperForm", source = "totalFeePaperForm")
    @Mapping(target = "paperPaymentMethod", source = "paperPaymentMethod")
    @Mapping(target = "paymentReferenceNumberPaperform", source = "paymentReferenceNumberPaperform")
    @Mapping(target = "primaryApplicantForenames", source = "primaryApplicantForenames")
    @Mapping(target = "primaryApplicantSurname", source = "primaryApplicantSurname")
    @Mapping(target = "primaryApplicantHasAlias", source = "primaryApplicantHasAlias")
    @Mapping(target = "primaryApplicantAlias", source = "primaryApplicantAlias")
    @Mapping(target = "primaryApplicantPhoneNumber", source = "primaryApplicantPhoneNumber")
    @Mapping(target = "primaryApplicantSecondPhoneNumber", source = "primaryApplicantSecondPhoneNumber")
    @Mapping(target = "primaryApplicantEmailAddress", source = "primaryApplicantEmailAddress")
    @Mapping(target = "primaryApplicantAddress.addressLine1", source = "primaryApplicantAddress_AddressLine1")
    @Mapping(target = "primaryApplicantAddress.addressLine2", source = "primaryApplicantAddress_AddressLine2")
    @Mapping(target = "primaryApplicantAddress.postTown", source = "primaryApplicantAddress_PostTown")
    @Mapping(target = "primaryApplicantAddress.county", source = "primaryApplicantAddress_County")
    @Mapping(target = "primaryApplicantAddress.postCode", source = "primaryApplicantAddress_PostCode")
    @Mapping(target = "deceasedForenames", source = "deceasedForenames")
    @Mapping(target = "deceasedSurname", source = "deceasedSurname")
    @Mapping(target = "deceasedAddress.addressLine1", source = "deceasedAddress_AddressLine1")
    @Mapping(target = "deceasedAddress.addressLine2", source = "deceasedAddress_AddressLine2")
    @Mapping(target = "deceasedAddress.postTown", source = "deceasedAddress_PostTown")
    @Mapping(target = "deceasedAddress.county", source = "deceasedAddress_County")
    @Mapping(target = "deceasedAddress.postCode", source = "deceasedAddress_PostCode")
    @Mapping(target = "deceasedAnyOtherNames", source = "deceasedAnyOtherNames")
    @Mapping(target = "deceasedDomicileInEngWales", source = "deceasedDomicileInEngWales")
    @Mapping(target = "deceasedMartialStatus", source = "deceasedMartialStatus")
    @Mapping(target = "dateOfMarriageOrCP", source = "dateOfMarriageOrCP")
    @Mapping(target = "dateOfDivorcedCPJudicially", source = "dateOfDivorcedCPJudicially")
    @Mapping(target = "courtOfDecree", source = "courtOfDecree")
    @Mapping(target = "foreignAsset", source = "foreignAsset")
    @Mapping(target = "foreignAssetEstateValue", source = "foreignAssetEstateValue")
    @Mapping(target = "adopted", source = "adopted")
    @Mapping(target = "willHasCodicils", source = "willHasCodicils")
    @Mapping(target = "willsOutsideOfUK", source = "willsOutsideOfUK")
    @Mapping(target = "deceasedMarriedAfterWillOrCodicilDate", source = "deceasedMarriedAfterWillOrCodicilDate")
    @Mapping(target = "willGiftUnderEighteen", source = "willGiftUnderEighteen")
    @Mapping(target = "notifiedApplicants", source = "notifiedApplicants")
    @Mapping(target = "spouseOrPartner", source = "spouseOrPartner")
    @Mapping(target = "childrenUnderEighteenSurvived", source = "childrenUnderEighteenSurvived")
    @Mapping(target = "childrenOverEighteenSurvived", source = "childrenOverEighteenSurvived")
    @Mapping(target = "childrenDiedUnderEighteen", source = "childrenDiedUnderEighteen")
    @Mapping(target = "childrenDiedOverEighteen", source = "childrenDiedOverEighteen")
    @Mapping(target = "grandChildrenSurvivedUnderEighteen", source = "grandChildrenSurvivedUnderEighteen")
    @Mapping(target = "grandChildrenSurvivedOverEighteen", source = "grandChildrenSurvivedOverEighteen")
    @Mapping(target = "primaryApplicantRelationshipToDeceased", source = "primaryApplicantRelationshipToDeceased")
    @Mapping(target = "applyingAsAnAttorney", source = "applyingAsAnAttorney")
    @Mapping(target = "mentalCapacity", source = "mentalCapacity")
    @Mapping(target = "courtOfProtection", source = "courtOfProtection")
    @Mapping(target = "epaOrLpa", source = "epaOrLpa")
    @Mapping(target = "epaRegistered", source = "epaRegistered")
    @Mapping(target = "domicilityCountry", source = "domicilityCountry")
    @Mapping(target = "ihtFormCompletedOnline", source = "ihtFormCompletedOnline")
    @Mapping(target = "ihtReferenceNumber", source = "ihtReferenceNumber")
    @Mapping(target = "ihtGrossValue", source = "ihtGrossValue")
    @Mapping(target = "ihtNetValue", source = "ihtNetValue")
    @Mapping(target = "ihtFormId", source = "ihtFormId")
    @Mapping(target = "executorsApplying", source = "ocrFields", qualifiedBy = {ToAdditionalExecutorApplying.class})
    @Mapping(target = "deceasedDateOfBirth", source = "deceasedDateOfBirth", dateFormat = "dd-MM-yyyy")







    CaseData toCcdData(List<OCRField> ocrFields);
}
