package uk.gov.hmcts.probate.service.exceptionrecord.utils;

import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordRequest;
import uk.gov.hmcts.probate.model.ocr.OCRField;

import java.util.List;

public class OCRFieldExtractor {

    public static String get(List<OCRField> ocrFields, String name) {
        return ocrFields
                .stream()
                .filter(it -> it.name.equals(name))
                .map(it -> it.value)
                .findFirst()
                .orElse(null);
    }

    public static String get(List<OCRField> ocrFields, String name1, String name2) {
        return (get(ocrFields, name1) + " " + get(ocrFields, name2)).trim();
    }

    private OCRFieldExtractor() {
        // util class
    }
}