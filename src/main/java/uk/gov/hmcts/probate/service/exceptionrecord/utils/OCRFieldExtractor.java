package uk.gov.hmcts.probate.service.exceptionrecord.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ocr.OCRField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class OCRFieldExtractor {

    private OCRFieldExtractor() {
        throw new IllegalStateException("Utility Class");
    }

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
                + get(ocrFields, name2)).replace("null", "").replaceAll("\\s{2,}", " ").trim();
    }

    public static String get(List<OCRField> ocrFields, String name1, String name2, String name3) {
        return (get(ocrFields, name1) + " "
                + get(ocrFields, name2) + " "
                + get(ocrFields, name3)).replace("null", "").replaceAll("\\s{2,}", " ").trim();
    }

    public static List<String> splitFullname(String fullName) {
        return new ArrayList<>(Arrays.asList(fullName.split(" ")));
    }
}