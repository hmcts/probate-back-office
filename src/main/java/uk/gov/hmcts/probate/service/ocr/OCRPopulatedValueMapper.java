package uk.gov.hmcts.probate.service.ocr;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ocr.OCRField;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class OCRPopulatedValueMapper {

    public List<OCRField> ocrPopulatedValueMapper(List<OCRField> fields) {
        List<OCRField> populatedValues = new ArrayList<>();
        for (OCRField field : fields) {
            if (field.getValue() != null && !field.getValue().isEmpty()) {
                populatedValues.add(field);
            }
        }
        return populatedValues;
    }
}