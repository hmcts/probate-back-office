package uk.gov.hmcts.probate.model.ocr;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;

import java.util.List;
import javax.validation.Valid;

@Data
public class OCRRequest {
    @Valid
    @JsonProperty(value = "ocr_data_fields", required = true)
    private final List<OCRField> ocrFields;

    @JsonProperty(value = "scanned_documents")
    private final List<ScannedDocument> scannedDocuments;

    @JsonProperty(value = "case_type_id")
    private final String caseTypeId;

    @JsonProperty(value = "po_box")
    private final String poBox;

    @JsonProperty(value = "po_box_jurisdiction")
    private final String poBoxJurisdiction;

    @JsonProperty(value = "delivery_date")
    private final String deliveryDate;

    @JsonProperty(value = "opening_date")
    private final String openingDate;
}
