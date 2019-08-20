package uk.gov.hmcts.probate.service.ocr;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ocr.OCRField;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class OCRMapper {

    public List<OCRField> ocrMapper(List<OCRField> fields) {
        List<OCRField> populatedValues = new ArrayList<>();
        for (OCRField field : fields) {
            log.info("Checking ocr field: {} for null or empty", field.getName());
            if (field.getValue() != null) {
                if (!field.getValue().isEmpty()) {
                    populatedValues.add(field);
                    log.info("{} passed empty check validation", field.getName());
                }
            }
        }
        return populatedValues;
    }
}
