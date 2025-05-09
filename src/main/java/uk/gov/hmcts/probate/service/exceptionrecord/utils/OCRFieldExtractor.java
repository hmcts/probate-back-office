package uk.gov.hmcts.probate.service.exceptionrecord.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.config.BulkScanConfig;
import uk.gov.hmcts.probate.model.ocr.OCRField;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.ModifiedOCRField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class OCRFieldExtractor {

    private static final String POSTCODE_REGEX_PATTERN = "^([A-Z]{1,2}\\d[A-Z\\d]? ?\\d[A-Z]{2}|GIR ?0A{2})$";

    private static BulkScanConfig bulkScanConfig;

    @Autowired
    private OCRFieldExtractor(BulkScanConfig bulkScanConfig) {
        OCRFieldExtractor.bulkScanConfig = bulkScanConfig;
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

    public static String getDefaultPostcodeIfInvalid(List<OCRField> ocrFields, String name,
                                                List<CollectionMember<ModifiedOCRField>> modifiedFields) {
        String response = ocrFields
                .stream()
                .filter(it -> it.getName().equals(name) && it.getValue() != null)
                .map(it -> it.getValue().trim().toUpperCase())
                .findFirst()
                .orElse(null);

        if (null == response || isInValidPostCode(response)) {
            log.warn("Invalid or missing postcode for field '{}'", name);
            ModifiedOCRField modifiedOCRField = ModifiedOCRField.builder()
                    .fieldName(name)
                    .originalValue(response)
                    .build();
            modifiedFields.add(new CollectionMember(null, modifiedOCRField));
            return bulkScanConfig.getPostcode();
        }

        return response;
    }

    private static boolean isInValidPostCode(final String postCode) {
        return !postCode.matches(POSTCODE_REGEX_PATTERN);
    }
}