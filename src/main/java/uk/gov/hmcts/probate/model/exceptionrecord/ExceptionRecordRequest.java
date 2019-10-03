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

                .ihtFormCompletedOnline(get(ocrFields, "ihtFormCompletedOnline"))
                .ihtReferenceNumber(get(ocrFields, "ihtReferenceNumber"))
                .ihtGrossValue(get(ocrFields, "ihtGrossValue"))
                .ihtNetValue(get(ocrFields, "ihtNetValue"))
                .ihtFormId(get(ocrFields, "ihtFormId"))

                .build();
    }
}