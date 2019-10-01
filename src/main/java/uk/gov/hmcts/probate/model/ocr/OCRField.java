package uk.gov.hmcts.probate.model.ocr;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OCRField {
    public String name;
    public String value;
    private String description;
}
