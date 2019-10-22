package uk.gov.hmcts.probate.service.exceptionrecord.utils;

import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordRequest;
import uk.gov.hmcts.probate.model.ocr.OCRField;

import java.util.List;

@Slf4j
public class OCRFieldExtractor {

    public static String get(List<OCRField> ocrFields, String name) {
        String response = null;

        try {
            response = ocrFields
                    .stream()
                    .filter(it -> it.getName().equals(name))
                    .map(it -> it.getValue().trim())
                    .findFirst()
                    .orElse(null);
        } catch (NullPointerException npe) {
            log.warn("Unable to extract value for field '{}'", name);
        }

        return response;
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