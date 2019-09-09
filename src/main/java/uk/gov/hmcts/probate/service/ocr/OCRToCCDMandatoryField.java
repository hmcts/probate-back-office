package uk.gov.hmcts.probate.service.ocr;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.ocr.CaveatMandatoryFields;
import uk.gov.hmcts.probate.model.ccd.ocr.GORMandatoryFields;
import uk.gov.hmcts.probate.model.ocr.OCRField;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
public class OCRToCCDMandatoryField {

    private static final String MANDATORY_FIELD_WARNING_STIRNG = "Key '%s' is mandatory.";
    private static final String IHTFORMCOMPLETEDONLINE_KEY = GORMandatoryFields.IHT_FORM_COMPLETED_ONLINE.getKey();
    private static final String DEPENDANT_KEY_IHTREFERENCENUMBER = "ihtReferenceNumber";
    private static final String DEPENDANT_KEY_IHTFORMID = "ihtFormId";

    public List<String> ocrToCCDMandatoryFields(List<OCRField> ocrFields, FormType formType) {
        List<String> descriptions = new ArrayList<>();
        List<String> ocrFieldNames = new ArrayList<>();
        ocrFields.forEach(ocrField -> {
            ocrFieldNames.add(ocrField.getName());
        });
        switch (formType) {
            case PA8A:
                Stream.of(CaveatMandatoryFields.values()).forEach(field -> {
                    log.info("Checking {} against ocr fields", field.getKey());
                    if (!ocrFieldNames.contains(field.getKey())) {
                        log.warn("{} was not found in ocr fields", field.getKey());
                        descriptions.add(String.format(MANDATORY_FIELD_WARNING_STIRNG, field.getKey()));
                    }
                });;
                break;
            case PA1A:
            case PA1P:
            default:
                Stream.of(GORMandatoryFields.values()).forEach(field -> {
                    log.info("Checking {} against ocr fields", field.getKey());
                    if (!ocrFieldNames.contains(field.getKey())) {
                        log.warn("{} was not found in ocr fields", field.getKey());
                        descriptions.add(String.format(MANDATORY_FIELD_WARNING_STIRNG, field.getKey()));
                    }
                });
                log.error("1 DEBUG HAS {}", IHTFORMCOMPLETEDONLINE_KEY);
                if (ocrFieldNames.contains(IHTFORMCOMPLETEDONLINE_KEY)) {
                    log.error("2 DEBUG HAS {}", IHTFORMCOMPLETEDONLINE_KEY);
                    boolean result = BooleanUtils.toBoolean(ocrFieldNames.get(ocrFieldNames
                            .indexOf(IHTFORMCOMPLETEDONLINE_KEY)));

                    log.error("{} is {}", IHTFORMCOMPLETEDONLINE_KEY, Boolean.toString(result));
                    if (result && !ocrFieldNames.contains(DEPENDANT_KEY_IHTREFERENCENUMBER)) {
                        log.warn("{} was not found in ocr fields", DEPENDANT_KEY_IHTREFERENCENUMBER);
                        descriptions.add(String.format(MANDATORY_FIELD_WARNING_STIRNG, DEPENDANT_KEY_IHTREFERENCENUMBER));
                    } else if (!result && !ocrFieldNames.contains(DEPENDANT_KEY_IHTFORMID)) {
                        log.warn("{} was not found in ocr fields", DEPENDANT_KEY_IHTFORMID);
                        descriptions.add(String.format(MANDATORY_FIELD_WARNING_STIRNG, DEPENDANT_KEY_IHTFORMID));
                    }
                }
                break;
        }

        return descriptions;
    }
}
