package uk.gov.hmcts.probate.model.ocr;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import uk.gov.hmcts.bulkscan.type.OcrDataField;

import java.util.List;
import javax.validation.Valid;

@Data
public class OCRRequest {
    @Valid
    @JsonProperty(value = "ocr_data_fields", required = true)
    private final List<OcrDataField> ocrFields;
}
