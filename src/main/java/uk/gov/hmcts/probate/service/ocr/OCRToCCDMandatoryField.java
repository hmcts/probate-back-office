package uk.gov.hmcts.probate.service.ocr;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.ocr.MandatoryFields;
import uk.gov.hmcts.probate.model.ocr.OCRField;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
public class OCRToCCDMandatoryField {

    public List<String> ocrToCCDMandatoryFields(List<OCRField> ocrFields) {
        List<String> descriptions = new ArrayList<>();
        List<String> ocrFieldNames = new ArrayList<>();
        ocrFields.forEach(ocrField -> {
            ocrFieldNames.add(ocrField.getName());
        });
        Stream.of(MandatoryFields.values()).forEach(field -> {
            log.info("Checking {} against ocr fields", field.getKey());
            if (!ocrFieldNames.contains(field.getKey())) {
                log.warn("{} was not found in ocr fields", field.getKey());
                descriptions.add(field.getValue());
            }
        });
        return descriptions;
    }
}
