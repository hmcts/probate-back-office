package uk.gov.hmcts.probate.model.exceptionrecord;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.json.JSONArray;
import uk.gov.hmcts.probate.model.ocr.OCRField;

import java.time.LocalDateTime;
import java.util.List;

import static uk.gov.hmcts.probate.service.exceptionrecord.utils.OCRFieldExtractor.get;

@Data
public class ExceptionRecordRequest {

    private final String exceptionRecordId;
    private final String exceptionRecordCaseTypeId;
    private final String poBox;
    private final String formType;
    private final String jurisdiction;
    private final JourneyClassification journeyClassification;
    private final LocalDateTime deliveryDate;
    private final LocalDateTime openingDate;
    private final List<InputScannedDoc> scannedDocuments;
    private final List<OCRField> ocrFields;
    private final String envelopeId;
    private final Boolean isAutomatedProcess;

    public ExceptionRecordRequest(
            @JsonProperty("exception_record_id") String exceptionRecordId,
            @JsonProperty("exception_record_case_type_id") String exceptionRecordCaseTypeId,
            @JsonProperty("po_box") String poBox,
            @JsonProperty("po_box_jurisdiction") String jurisdiction,
            @JsonProperty("form_type") String formType,
            @JsonProperty("journey_classification") JourneyClassification journeyClassification,
            @JsonProperty("delivery_date") LocalDateTime deliveryDate,
            @JsonProperty("opening_date") LocalDateTime openingDate,
            @JsonProperty("scanned_documents") List<InputScannedDoc> scannedDocuments,
            @JsonProperty("ocr_data_fields") List<OCRField> ocrFields,
            @JsonProperty("envelope_id") String envelopeId,
            @JsonProperty("is_automated_process") Boolean isAutomatedProcess
    ) {
        this.exceptionRecordId = exceptionRecordId;
        this.exceptionRecordCaseTypeId = exceptionRecordCaseTypeId;
        this.poBox = poBox;
        this.formType = formType;
        this.jurisdiction = jurisdiction;
        this.journeyClassification = journeyClassification;
        this.deliveryDate = deliveryDate;
        this.openingDate = openingDate;
        this.scannedDocuments = scannedDocuments;
        this.ocrFields = ocrFields;
        this.envelopeId = envelopeId;
        this.isAutomatedProcess = isAutomatedProcess;
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
                .solsSolicitorRepresentativeName(get(ocrFields, "solsSolicitorRepresentativeName"))
                .solsSolicitorFirmName(get(ocrFields, "solsSolicitorFirmName"))
                .solsSolicitorAppReference(get(ocrFields, "solsSolicitorAppReference"))
                .solsFeeAccountNumber(get(ocrFields, "solsFeeAccountNumber"))
                .solsSolicitorAddressLine1(get(ocrFields, "solsSolicitorAddressLine1"))
                .solsSolicitorAddressLine2(get(ocrFields, "solsSolicitorAddressLine2"))
                .solsSolicitorAddressTown(get(ocrFields, "solsSolicitorAddressTown"))
                .solsSolicitorAddressCounty(get(ocrFields, "solsSolicitorAddressCounty"))
                .solsSolicitorAddressPostCode(get(ocrFields, "solsSolicitorAddressPostCode"))
                .solsSolicitorEmail(get(ocrFields, "solsSolicitorEmail"))
                .solsSolicitorPhoneNumber(get(ocrFields, "solsSolicitorPhoneNumber"))
                .caseReference(get(ocrFields, "caseReference"))
                .extraCopiesOfGrant(get(ocrFields, "extraCopiesOfGrant"))
                .outsideUKGrantCopies(get(ocrFields, "outsideUKGrantCopies"))
                .applicationFeePaperForm(get(ocrFields, "applicationFeePaperForm"))
                .feeForCopiesPaperForm(get(ocrFields, "feeForCopiesPaperForm"))
                .totalFeePaperForm(get(ocrFields, "totalFeePaperForm"))
                .paperPaymentMethod(get(ocrFields, "paperPaymentMethod"))
                .paymentReferenceNumberPaperform(get(ocrFields, "paymentReferenceNumberPaperform"))
                .solsFeeAccountNumber(get(ocrFields, "solsFeeAccountNumber"))
                .primaryApplicantForenames(get(ocrFields, "primaryApplicantForenames", "primaryApplicantMiddleNames"))
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
                .executorsApplying0applyingExecutorName(get(ocrFields, "executorsApplying_0_applyingExecutorForenames",
                        "executorsApplying_0_applyingExecutorMiddleNames", "executorsApplying_0_applyingExecutorSurname"))
                .executorsApplying0applyingExecutorAddressLine1(get(ocrFields, "executorsApplying_0_applyingExecutorAddressLine1"))
                .executorsApplying0applyingExecutorAddressLine2(get(ocrFields, "executorsApplying_0_applyingExecutorAddressLine2"))
                .executorsApplying0applyingExecutorAddressTown(get(ocrFields, "executorsApplying_0_applyingExecutorAddressTown"))
                .executorsApplying0applyingExecutorAddressCounty(get(ocrFields, "executorsApplying_0_applyingExecutorAddressCounty"))
                .executorsApplying0applyingExecutorAddressPostCode(get(ocrFields, "executorsApplying_0_applyingExecutorAddressPostCode"))
                .executorsApplying0applyingExecutorEmail(get(ocrFields, "executorsApplying_0_applyingExecutorEmail"))
                .executorsApplying0applyingExecutorDifferentNameToWill(get(ocrFields,
                        "executorsApplying_0_applyingExecutorDifferentNameToWill"))
                .executorsApplying0applyingExecutorOtherNames(get(ocrFields, "executorsApplying_0_applyingExecutorOtherNames"))
                .executorsApplying1applyingExecutorName(get(ocrFields, "executorsApplying_1_applyingExecutorForenames",
                        "executorsApplying_1_applyingExecutorMiddleNames", "executorsApplying_1_applyingExecutorSurname"))
                .executorsApplying1applyingExecutorAddressLine1(get(ocrFields, "executorsApplying_1_applyingExecutorAddressLine1"))
                .executorsApplying1applyingExecutorAddressLine2(get(ocrFields, "executorsApplying_1_applyingExecutorAddressLine2"))
                .executorsApplying1applyingExecutorAddressTown(get(ocrFields, "executorsApplying_1_applyingExecutorAddressTown"))
                .executorsApplying1applyingExecutorAddressCounty(get(ocrFields, "executorsApplying_1_applyingExecutorAddressCounty"))
                .executorsApplying1applyingExecutorAddressPostCode(get(ocrFields, "executorsApplying_1_applyingExecutorAddressPostCode"))
                .executorsApplying1applyingExecutorEmail(get(ocrFields, "executorsApplying_1_applyingExecutorEmail"))
                .executorsApplying1applyingExecutorDifferentNameToWill(get(ocrFields,
                        "executorsApplying_1_applyingExecutorDifferentNameToWill"))
                .executorsApplying1applyingExecutorOtherNames(get(ocrFields, "executorsApplying_1_applyingExecutorOtherNames"))
                .executorsApplying2applyingExecutorName(get(ocrFields, "executorsApplying_2_applyingExecutorForenames",
                        "executorsApplying_2_applyingExecutorMiddleNames", "executorsApplying_2_applyingExecutorSurname"))
                .executorsApplying2applyingExecutorAddressLine1(get(ocrFields, "executorsApplying_2_applyingExecutorAddressLine1"))
                .executorsApplying2applyingExecutorAddressLine2(get(ocrFields, "executorsApplying_2_applyingExecutorAddressLine2"))
                .executorsApplying2applyingExecutorAddressTown(get(ocrFields, "executorsApplying_2_applyingExecutorAddressTown"))
                .executorsApplying2applyingExecutorAddressCounty(get(ocrFields, "executorsApplying_2_applyingExecutorAddressCounty"))
                .executorsApplying2applyingExecutorAddressPostCode(get(ocrFields, "executorsApplying_2_applyingExecutorAddressPostCode"))
                .executorsApplying2applyingExecutorEmail(get(ocrFields, "executorsApplying_2_applyingExecutorEmail"))
                .executorsApplying2applyingExecutorDifferentNameToWill(get(ocrFields,
                        "executorsApplying_2_applyingExecutorDifferentNameToWill"))
                .executorsApplying2applyingExecutorOtherNames(get(ocrFields, "executorsApplying_2_applyingExecutorOtherNames"))
                .solsSolicitorIsApplying(get(ocrFields,"solsSolicitorIsApplying"))
                .solsSolicitorRepresentativeName(get(ocrFields,"solsSolicitorRepresentativeName"))
                .solsSolicitorFirmName(get(ocrFields,"solsSolicitorFirmName"))
                .solsSolicitorAppReference(get(ocrFields,"solsSolicitorAppReference"))
                .solsSolicitorAddressLine1(get(ocrFields,"solsSolicitorAddressLine1"))
                .solsSolicitorAddressLine2(get(ocrFields,"solsSolicitorAddressLine2"))
                .solsSolicitorAddressTown(get(ocrFields,"solsSolicitorAddressTown"))
                .solsSolicitorAddressCounty(get(ocrFields,"solsSolicitorAddressCounty"))
                .solsSolicitorAddressPostCode(get(ocrFields,"solsSolicitorAddressPostCode"))
                .solsSolicitorEmail(get(ocrFields,"solsSolicitorEmail"))
                .solsSolicitorPhoneNumber(get(ocrFields,"solsSolicitorPhoneNumber"))
                .deceasedDomicileInEngWales(get(ocrFields, "deceasedDomicileInEngWales"))
                .deceasedMartialStatus(get(ocrFields, "deceasedMartialStatus"))
                .dateOfMarriageOrCP(get(ocrFields, "dateOfMarriageOrCP"))
                .dateOfDivorcedCPJudicially(get(ocrFields, "dateOfDivorcedCPJudicially"))
                .courtOfDecree(get(ocrFields, "courtOfDecree"))
                .foreignAsset(get(ocrFields, "foreignAsset"))
                .foreignAssetEstateValue(get(ocrFields, "foreignAssetEstateValue"))
                .adopted(get(ocrFields, "adopted"))
                .adoptiveRelatives0name(get(ocrFields, "adoptiveRelatives_0_name"))
                .adoptiveRelatives0relationship(get(ocrFields, "adoptiveRelatives_0_relationship"))
                .adoptiveRelatives0adoptedInOrOut(get(ocrFields, "adoptiveRelatives_0_adoptedInOrOut"))
                .adoptiveRelatives1name(get(ocrFields, "adoptiveRelatives_1_name"))
                .adoptiveRelatives1relationship(get(ocrFields, "adoptiveRelatives_1_relationship"))
                .adoptiveRelatives1adoptedInOrOut(get(ocrFields, "adoptiveRelatives_1_adoptedInOrOut"))
                .adoptiveRelatives2name(get(ocrFields, "adoptiveRelatives_2_name"))
                .adoptiveRelatives2relationship(get(ocrFields, "adoptiveRelatives_2_relationship"))
                .adoptiveRelatives2adoptedInOrOut(get(ocrFields, "adoptiveRelatives_2_adoptedInOrOut"))
                .adoptiveRelatives3name(get(ocrFields, "adoptiveRelatives_3_name"))
                .adoptiveRelatives3relationship(get(ocrFields, "adoptiveRelatives_3_relationship"))
                .adoptiveRelatives3adoptedInOrOut(get(ocrFields, "adoptiveRelatives_3_adoptedInOrOut"))
                .adoptiveRelatives4name(get(ocrFields, "adoptiveRelatives_4_name"))
                .adoptiveRelatives4relationship(get(ocrFields, "adoptiveRelatives_4_relationship"))
                .adoptiveRelatives4adoptedInOrOut(get(ocrFields, "adoptiveRelatives_4_adoptedInOrOut"))
                .adoptiveRelatives5name(get(ocrFields, "adoptiveRelatives_5_name"))
                .adoptiveRelatives5relationship(get(ocrFields, "adoptiveRelatives_5_relationship"))
                .adoptiveRelatives5adoptedInOrOut(get(ocrFields, "adoptiveRelatives_5_adoptedInOrOut"))
                .solsWillType(get(ocrFields, "solsWillType"))
                .solsWillTypeReason(get(ocrFields, "solsWillTypeReason"))
                .bilingualGrantRequested(get(ocrFields, "bilingualGrantRequested"))
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
                .willDate(get(ocrFields, "willDate"))
                .willHasCodicils(get(ocrFields, "willHasCodicils"))
                .willsOutsideOfUK(get(ocrFields, "willsOutsideOfUK"))
                .deceasedMarriedAfterWillOrCodicilDate(get(ocrFields, "deceasedMarriedAfterWillOrCodicilDate"))
                .willGiftUnderEighteen(get(ocrFields, "willGiftUnderEighteen"))
                .executorsNotApplying0notApplyingExecutorName(get(ocrFields, "executorsNotApplying_0_notApplyingExecutorName"))
                .executorsNotApplying0notApplyingExecutorReason(get(ocrFields, "executorsNotApplying_0_notApplyingExecutorReason"))
                .executorsNotApplying1notApplyingExecutorName(get(ocrFields, "executorsNotApplying_1_notApplyingExecutorName"))
                .executorsNotApplying1notApplyingExecutorReason(get(ocrFields, "executorsNotApplying_1_notApplyingExecutorReason"))
                .executorsNotApplying2notApplyingExecutorName(get(ocrFields, "executorsNotApplying_2_notApplyingExecutorName"))
                .executorsNotApplying2notApplyingExecutorReason(get(ocrFields, "executorsNotApplying_2_notApplyingExecutorReason"))
                .notifiedApplicants(get(ocrFields, "notifiedApplicants"))
                .ihtFormCompletedOnline(get(ocrFields, "ihtFormCompletedOnline"))
                .ihtReferenceNumber(get(ocrFields, "ihtReferenceNumber"))
                .ihtFormId(get(ocrFields, "ihtFormId"))
                .ihtGrossValue(get(ocrFields, "ihtGrossValue"))
                .ihtNetValue(get(ocrFields, "ihtNetValue"))
                .build();
    }
}