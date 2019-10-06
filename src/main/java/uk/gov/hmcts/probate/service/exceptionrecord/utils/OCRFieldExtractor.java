package uk.gov.hmcts.probate.service.exceptionrecord.utils;

import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordRequest;
import uk.gov.hmcts.probate.model.ocr.OCRField;

import java.util.List;

public class OCRFieldExtractor {

    public static String get(List<OCRField> ocrFields, String name) {
        return ocrFields
                .stream()
                .filter(it -> it.getName().equals(name))
                .map(it -> it.getValue().trim())
                .findFirst()
                .orElse(null);
    }

    public static String get(List<OCRField> ocrFields, String name1, String name2) {
        return (get(ocrFields, name1) + " "
                + get(ocrFields, name2)).trim().replaceAll("\\s{2,}", " ");
    }

    public static String get(List<OCRField> ocrFields, String name1, String name2, String name3) {
        return (get(ocrFields, name1) + " "
                + get(ocrFields, name2) + " "
                + get(ocrFields, name3)).trim().replaceAll("\\s{2,}", " ");
    }

    private OCRFieldExtractor() {
        // util class
    }
}