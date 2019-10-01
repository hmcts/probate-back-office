package uk.gov.hmcts.probate.model.exceptionrecord;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.probate.model.ocr.OCRField;

import java.util.List;

@Data
@Builder
public class OCRFieldsList {

    public final List<OCRField> ocrFields;

    //public final OCRFieldsNames fieldNames;

    public final String deceasedForenames = "deceasedForenames";



}