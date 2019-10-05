package uk.gov.hmcts.probate.model.exceptionrecord;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import uk.gov.hmcts.probate.model.ocr.OCRField;

import java.time.LocalDateTime;
import java.util.List;

import static uk.gov.hmcts.probate.service.exceptionrecord.utils.OCRFieldExtractor.get;

@Data
public class ExceptionRecordRequest {

    private final String id;
    private final String caseTypeId;
    private final String poBox;
    private final String formType;
    private final String jurisdiction;
    private final JourneyClassification journeyClassification;
    private final LocalDateTime deliveryDate;
    private final LocalDateTime openingDate;
    private final List<InputScannedDoc> scannedDocuments;
    private final List<OCRField> ocrFields;

    public ExceptionRecordRequest(
            @JsonProperty("id") String id,
            @JsonProperty("case_type_id") String caseTypeId,
            @JsonProperty("po_box") String poBox,
            @JsonProperty("po_box_jurisdiction") String jurisdiction,
            @JsonProperty("form_type") String formType,
            @JsonProperty("journey_classification") JourneyClassification journeyClassification,
            @JsonProperty("delivery_date") LocalDateTime deliveryDate,
            @JsonProperty("opening_date") LocalDateTime openingDate,
            @JsonProperty("scanned_documents") List<InputScannedDoc> scannedDocuments,
            @JsonProperty("ocr_data_fields") List<OCRField> ocrFields
    ) {
        this.id = id;
        this.caseTypeId = caseTypeId;
        this.poBox = poBox;
        this.formType = formType;
        this.jurisdiction = jurisdiction;
        this.journeyClassification = journeyClassification;
        this.deliveryDate = deliveryDate;
        this.openingDate = openingDate;
        this.scannedDocuments = scannedDocuments;
        this.ocrFields = ocrFields;
    }

    public ExceptionRecordOCRFields getOCRFieldsObject() {

        return ExceptionRecordOCRFields.builder()
                .caveatorForenames(get(ocrFields, "caveatorForenames", "caveatorMiddleNames"))
                .caveatorSurnames(get(ocrFields, "caveatorSurnames"))
                .caveatorEmailAddress(get(ocrFields, "caveatorEmailAddress"))
                .caveatorAddressLine1(get(ocrFields, "caveatorAddressLine1"))
                .caveatorAddressLine2(get(ocrFields, "caveatorAddressLine2"))
                .caveatorAddressTown(get(ocrFields, "caveatorAddressTown"))
                .caveatorAddressCounty(get(ocrFields, "caveatorAddressCounty"))
                .caveatorAddressPostCode(get(ocrFields, "caveatorAddressPostCode"))
                .deceasedForenames(get(ocrFields, "deceasedForenames", "deceasedMiddleNames"))
                .deceasedSurname(get(ocrFields, "deceasedSurname"))
                .deceasedAnyOtherNames(get(ocrFields, "deceasedAnyOtherNames"))
                .deceasedAddressLine1(get(ocrFields, "deceasedAddressLine1"))
                .deceasedAddressLine2(get(ocrFields, "deceasedAddressLine2"))
                .deceasedAddressTown(get(ocrFields, "deceasedAddressTown"))
                .deceasedAddressCounty(get(ocrFields, "deceasedAddressCounty"))
                .deceasedAddressPostCode(get(ocrFields, "deceasedAddressPostCode"))
                .deceasedDateOfBirth(get(ocrFields, "deceasedDateOfBirth"))
                .deceasedDateOfDeath(get(ocrFields, "deceasedDateOfDeath"))
                .extraCopiesOfGrant(get(ocrFields, "extraCopiesOfGrant"))
                .outsideUKGrantCopies(get(ocrFields, "outsideUKGrantCopies"))
                .applicationFeePaperForm(get(ocrFields, "applicationFeePaperForm"))
                .feeForCopiesPaperForm(get(ocrFields, "feeForCopiesPaperForm"))
                .totalFeePaperForm(get(ocrFields, "totalFeePaperForm"))
                .paperPaymentMethod(get(ocrFields, "paperPaymentMethod"))
                .paymentReferenceNumberPaperform(get(ocrFields, "paymentReferenceNumberPaperform"))
                .primaryApplicantForenames(get(ocrFields, "primaryApplicantForenames"))
                .primaryApplicantMiddleNames(get(ocrFields, "primaryApplicantMiddleNames"))
                .primaryApplicantSurname(get(ocrFields, "primaryApplicantSurname"))
                .primaryApplicantAddressLine1(get(ocrFields, "primaryApplicantAddressLine1"))
                .primaryApplicantAddressLine2(get(ocrFields, "primaryApplicantAddressLine2"))
                .primaryApplicantAddressTown(get(ocrFields, "primaryApplicantAddressTown"))
                .primaryApplicantAddressCounty(get(ocrFields, "primaryApplicantAddressCounty"))
                .primaryApplicantAddressPostCode(get(ocrFields, "primaryApplicantAddressPostCode"))
                .primaryApplicantPhoneNumber(get(ocrFields, "primaryApplicantPhoneNumber"))
                .primaryApplicantSecondPhoneNumber(get(ocrFields, "primaryApplicantSecondPhoneNumber"))
                .primaryApplicantEmailAddress(get(ocrFields, "primaryApplicantEmailAddress"))
                .primaryApplicantHasAlias(get(ocrFields, "primaryApplicantHasAlias"))
                .primaryApplicantAlias(get(ocrFields, "primaryApplicantAlias"))
                .executorsApplying_0_applyingExecutorName(get(ocrFields, "executorsApplying_0_applyingExecutorForenames",
                        "executorsApplying_0_applyingExecutorMiddleNames", "executorsApplying_0_applyingExecutorSurname"))
                .executorsApplying_0_applyingExecutorAddressLine1(get(ocrFields, "executorsApplying_0_applyingExecutorAddressLine1"))
                .executorsApplying_0_applyingExecutorAddressLine2(get(ocrFields, "executorsApplying_0_applyingExecutorAddressLine2"))
                .executorsApplying_0_applyingExecutorAddressTown(get(ocrFields, "executorsApplying_0_applyingExecutorAddressTown"))
                .executorsApplying_0_applyingExecutorAddressCounty(get(ocrFields, "executorsApplying_0_applyingExecutorAddressCounty"))
                .executorsApplying_0_applyingExecutorAddressPostCode(get(ocrFields, "executorsApplying_0_applyingExecutorAddressPostCode"))
                .executorsApplying_0_applyingExecutorEmail(get(ocrFields, "executorsApplying_0_applyingExecutorEmail"))
                .executorsApplying_0_applyingExecutorDifferentNameToWill(get(ocrFields,
                        "executorsApplying_0_applyingExecutorDifferentNameToWill"))
                .executorsApplying_0_applyingExecutorOtherNames(get(ocrFields, "executorsApplying_0_applyingExecutorOtherNames"))
                .executorsApplying_1_applyingExecutorName(get(ocrFields, "executorsApplying_1_applyingExecutorForenames",
                        "executorsApplying_1_applyingExecutorMiddleNames", "executorsApplying_1_applyingExecutorSurname"))
                .executorsApplying_1_applyingExecutorAddressLine1(get(ocrFields, "executorsApplying_1_applyingExecutorAddressLine1"))
                .executorsApplying_1_applyingExecutorAddressLine2(get(ocrFields, "executorsApplying_1_applyingExecutorAddressLine2"))
                .executorsApplying_1_applyingExecutorAddressTown(get(ocrFields, "executorsApplying_1_applyingExecutorAddressTown"))
                .executorsApplying_1_applyingExecutorAddressCounty(get(ocrFields, "executorsApplying_1_applyingExecutorAddressCounty"))
                .executorsApplying_1_applyingExecutorAddressPostCode(get(ocrFields, "executorsApplying_1_applyingExecutorAddressPostCode"))
                .executorsApplying_1_applyingExecutorEmail(get(ocrFields, "executorsApplying_1_applyingExecutorEmail"))
                .executorsApplying_1_applyingExecutorDifferentNameToWill(get(ocrFields,
                        "executorsApplying_1_applyingExecutorDifferentNameToWill"))
                .executorsApplying_1_applyingExecutorOtherNames(get(ocrFields, "executorsApplying_1_applyingExecutorOtherNames"))
                .executorsApplying_2_applyingExecutorName(get(ocrFields, "executorsApplying_2_applyingExecutorForenames",
                        "executorsApplying_2_applyingExecutorMiddleNames", "executorsApplying_2_applyingExecutorSurname"))
                .executorsApplying_2_applyingExecutorAddressLine1(get(ocrFields, "executorsApplying_2_applyingExecutorAddressLine1"))
                .executorsApplying_2_applyingExecutorAddressLine2(get(ocrFields, "executorsApplying_2_applyingExecutorAddressLine2"))
                .executorsApplying_2_applyingExecutorAddressTown(get(ocrFields, "executorsApplying_2_applyingExecutorAddressTown"))
                .executorsApplying_2_applyingExecutorAddressCounty(get(ocrFields, "executorsApplying_2_applyingExecutorAddressCounty"))
                .executorsApplying_2_applyingExecutorAddressPostCode(get(ocrFields, "executorsApplying_2_applyingExecutorAddressPostCode"))
                .executorsApplying_2_applyingExecutorEmail(get(ocrFields, "executorsApplying_2_applyingExecutorEmail"))
                .executorsApplying_2_applyingExecutorDifferentNameToWill(get(ocrFields,
                        "executorsApplying_2_applyingExecutorDifferentNameToWill"))
                .executorsApplying_2_applyingExecutorOtherNames(get(ocrFields, "executorsApplying_2_applyingExecutorOtherNames"))
                .deceasedDomicileInEngWales(get(ocrFields, "deceasedDomicileInEngWales"))
                .deceasedMartialStatus(get(ocrFields, "deceasedMartialStatus"))
                .dateOfMarriageOrCP(get(ocrFields, "dateOfMarriageOrCP"))
                .dateOfDivorcedCPJudicially(get(ocrFields, "dateOfDivorcedCPJudicially"))
                .courtOfDecree(get(ocrFields, "courtOfDecree"))
                .foreignAsset(get(ocrFields, "foreignAsset"))
                .foreignAssetEstateValue(get(ocrFields, "foreignAssetEstateValue"))
                .adopted(get(ocrFields, "adopted"))
                .adoptiveRelatives_1_name(get(ocrFields, "adoptiveRelatives_1_name"))
                .adoptiveRelatives_1_relationship(get(ocrFields, "adoptiveRelatives_1_relationship"))
                .adoptiveRelatives_1_adoptedInOrOut(get(ocrFields, "adoptiveRelatives_1_adoptedInOrOut"))
                .adoptiveRelatives_2_name(get(ocrFields, "adoptiveRelatives_2_name"))
                .adoptiveRelatives_2_relationship(get(ocrFields, "adoptiveRelatives_2_relationship"))
                .adoptiveRelatives_2_adoptedInOrOut(get(ocrFields, "adoptiveRelatives_2_adoptedInOrOut"))
                .adoptiveRelatives_3_name(get(ocrFields, "adoptiveRelatives_3_name"))
                .adoptiveRelatives_3_relationship(get(ocrFields, "adoptiveRelatives_3_relationship"))
                .adoptiveRelatives_3_adoptedInOrOut(get(ocrFields, "adoptiveRelatives_3_adoptedInOrOut"))
                .adoptiveRelatives_4_name(get(ocrFields, "adoptiveRelatives_4_name"))
                .adoptiveRelatives_4_relationship(get(ocrFields, "adoptiveRelatives_4_relationship"))
                .adoptiveRelatives_4_adoptedInOrOut(get(ocrFields, "adoptiveRelatives_4_adoptedInOrOut"))
                .adoptiveRelatives_5_name(get(ocrFields, "adoptiveRelatives_5_name"))
                .adoptiveRelatives_5_relationship(get(ocrFields, "adoptiveRelatives_5_relationship"))
                .adoptiveRelatives_5_adoptedInOrOut(get(ocrFields, "adoptiveRelatives_5_adoptedInOrOut"))
                .spouseOrPartner(get(ocrFields, "spouseOrPartner"))
                .childrenUnderEighteenSurvived(get(ocrFields, "childrenUnderEighteenSurvived"))
                .childrenOverEighteenSurvived(get(ocrFields, "childrenOverEighteenSurvived"))
                .childrenDiedUnderEighteen(get(ocrFields, "childrenDiedUnderEighteen"))
                .childrenDiedOverEighteen(get(ocrFields, "childrenDiedOverEighteen"))
                .grandChildrenSurvivedUnderEighteen(get(ocrFields, "grandChildrenSurvivedUnderEighteen"))
                .grandChildrenSurvivedOverEighteen(get(ocrFields, "grandChildrenSurvivedOverEighteen"))
                .parentsExistUnderEighteenSurvived(get(ocrFields, "parentsExistUnderEighteenSurvived"))
                .parentsExistOverEighteenSurvived(get(ocrFields, "parentsExistOverEighteenSurvived"))
                .wholeBloodSiblingsSurvivedUnderEighteen(get(ocrFields, "wholeBloodSiblingsSurvivedUnderEighteen"))
                .wholeBloodSiblingsSurvivedOverEighteen(get(ocrFields, "wholeBloodSiblingsSurvivedOverEighteen"))
                .wholeBloodSiblingsDiedUnderEighteen(get(ocrFields, "wholeBloodSiblingsDiedUnderEighteen"))
                .wholeBloodSiblingsDiedOverEighteen(get(ocrFields, "wholeBloodSiblingsDiedOverEighteen"))
                .wholeBloodNeicesAndNephewsUnderEighteen(get(ocrFields, "wholeBloodNeicesAndNephewsUnderEighteen"))
                .wholeBloodNeicesAndNephewsOverEighteen(get(ocrFields, "wholeBloodNeicesAndNephewsOverEighteen"))
                .halfBloodSiblingsSurvivedUnderEighteen(get(ocrFields, "halfBloodSiblingsSurvivedUnderEighteen"))
                .halfBloodSiblingsSurvivedOverEighteen(get(ocrFields, "halfBloodSiblingsSurvivedOverEighteen"))
                .halfBloodSiblingsDiedUnderEighteen(get(ocrFields, "halfBloodSiblingsDiedUnderEighteen"))
                .halfBloodSiblingsDiedOverEighteen(get(ocrFields, "halfBloodSiblingsDiedOverEighteen"))
                .halfBloodNeicesAndNephewsUnderEighteen(get(ocrFields, "halfBloodNeicesAndNephewsUnderEighteen"))
                .halfBloodNeicesAndNephewsOverEighteen(get(ocrFields, "halfBloodNeicesAndNephewsOverEighteen"))
                .grandparentsDiedUnderEighteen(get(ocrFields, "grandparentsDiedUnderEighteen"))
                .grandparentsDiedOverEighteen(get(ocrFields, "grandparentsDiedOverEighteen"))
                .wholeBloodUnclesAndAuntsSurvivedUnderEighteen(get(ocrFields, "wholeBloodUnclesAndAuntsSurvivedUnderEighteen"))
                .wholeBloodUnclesAndAuntsSurvivedOverEighteen(get(ocrFields, "wholeBloodUnclesAndAuntsSurvivedOverEighteen"))
                .wholeBloodUnclesAndAuntsDiedUnderEighteen(get(ocrFields, "wholeBloodUnclesAndAuntsDiedUnderEighteen"))
                .wholeBloodUnclesAndAuntsDiedOverEighteen(get(ocrFields, "wholeBloodUnclesAndAuntsDiedOverEighteen"))
                .wholeBloodCousinsSurvivedUnderEighteen(get(ocrFields, "wholeBloodCousinsSurvivedUnderEighteen"))
                .wholeBloodCousinsSurvivedOverEighteen(get(ocrFields, "wholeBloodCousinsSurvivedOverEighteen"))
                .halfBloodUnclesAndAuntsSurvivedUnderEighteen(get(ocrFields, "halfBloodUnclesAndAuntsSurvivedUnderEighteen"))
                .halfBloodUnclesAndAuntsSurvivedOverEighteen(get(ocrFields, "halfBloodUnclesAndAuntsSurvivedOverEighteen"))
                .halfBloodUnclesAndAuntsDiedUnderEighteen(get(ocrFields, "halfBloodUnclesAndAuntsDiedUnderEighteen"))
                .halfBloodUnclesAndAuntsDiedOverEighteen(get(ocrFields, "halfBloodUnclesAndAuntsDiedOverEighteen"))
                .halfBloodCousinsSurvivedUnderEighteen(get(ocrFields, "halfBloodCousinsSurvivedUnderEighteen"))
                .halfBloodCousinsSurvivedOverEighteen(get(ocrFields, "halfBloodCousinsSurvivedOverEighteen"))
                .primaryApplicantRelationshipToDeceased(get(ocrFields, "primaryApplicantRelationshipToDeceased"))
                .secondApplicantRelationshipToDeceased(get(ocrFields, "secondApplicantRelationshipToDeceased"))
                .thirdApplicantRelationshipToDeceased(get(ocrFields, "thirdApplicantRelationshipToDeceased"))
                .fourthApplicantRelationshipToDeceased(get(ocrFields, "fourthApplicantRelationshipToDeceased"))
                .applyingAsAnAttorney(get(ocrFields, "applyingAsAnAttorney"))
                .attorneyOnBehalfOfName(get(ocrFields, "attorneyOnBehalfOfName"))
                .attorneyOnBehalfOfAddressLine1(get(ocrFields, "attorneyOnBehalfOfAddressLine1"))
                .attorneyOnBehalfOfAddressLine2(get(ocrFields, "attorneyOnBehalfOfAddressLine2"))
                .attorneyOnBehalfOfAddressTown(get(ocrFields, "attorneyOnBehalfOfAddressTown"))
                .attorneyOnBehalfOfAddressCounty(get(ocrFields, "attorneyOnBehalfOfAddressCounty"))
                .attorneyOnBehalfOfAddressPostCode(get(ocrFields, "attorneyOnBehalfOfAddressPostCode"))
                .mentalCapacity(get(ocrFields, "mentalCapacity"))
                .courtOfProtection(get(ocrFields, "courtOfProtection"))
                .epaOrLpa(get(ocrFields, "epaOrLpa"))
                .epaRegistered(get(ocrFields, "epaRegistered"))
                .domicilityCountry(get(ocrFields, "domicilityCountry"))
                .domicilityEntrustingDocument(get(ocrFields, "domicilityEntrustingDocument"))
                .domicilitySuccessionIHTCert(get(ocrFields, "domicilitySuccessionIHTCert"))
                .ihtFormCompletedOnline(get(ocrFields, "ihtFormCompletedOnline"))
                .ihtReferenceNumber(get(ocrFields, "ihtReferenceNumber"))
                .ihtFormId(get(ocrFields, "ihtFormId"))
                .ihtGrossValue(get(ocrFields, "ihtGrossValue"))
                .ihtNetValue(get(ocrFields, "ihtNetValue"))
                .build();
    }
}