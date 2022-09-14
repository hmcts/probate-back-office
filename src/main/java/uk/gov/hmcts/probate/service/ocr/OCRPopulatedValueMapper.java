package uk.gov.hmcts.probate.service.ocr;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.bulkscan.type.OcrDataField;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class OCRPopulatedValueMapper {

    public List<OcrDataField> ocrPopulatedValueMapper(List<OcrDataField> fields) {
        List<OcrDataField> populatedValues = new ArrayList<>();
        for (OcrDataField field : fields) {
            log.info("Checking ocr field: {} for null or empty", field.name());
            if (field.value() != null) {
                if (!field.value().isEmpty()) {
                    populatedValues.add(field);
                    log.info("{} passed empty check validation", field.name());
                }
            }
        }
        return populatedValues;
    }
}
