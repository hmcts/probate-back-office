package uk.gov.hmcts.probate.model.ocr;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OCRField {
    private String name;
    private String value;
    private String description;
}
