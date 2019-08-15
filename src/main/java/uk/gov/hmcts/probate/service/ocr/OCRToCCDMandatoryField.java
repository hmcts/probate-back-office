package uk.gov.hmcts.probate.service.ocr;

import lombok.extern.slf4j.Slf4j;
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
                        descriptions.add(field.getValue());
                    }
                });
                break;
            case PA1A:
            case PA1P:
            default:
                Stream.of(GORMandatoryFields.values()).forEach(field -> {
                    log.info("Checking {} against ocr fields", field.getKey());
                    if (!ocrFieldNames.contains(field.getKey())) {
                        log.warn("{} was not found in ocr fields", field.getKey());
                        descriptions.add(field.getValue());
                    }
                });
                break;
        }
        return descriptions;
    }
}
