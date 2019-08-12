package uk.gov.hmcts.probate.service.ocr.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ocr.OCRField;
import uk.gov.hmcts.probate.service.ocr.mapper.qualifiers.ToStraightCcdFieldMember;

import java.util.List;

@Slf4j
@Component
public class StraightCcdFieldMapper {


    @ToStraightCcdFieldMember
    public String toStraightCcdFieldMember(List<OCRField> fields, String targetField) {
        log.info("Beginning mapping for {}", targetField);
        String targetValue= null;
        for (OCRField field : fields) {
            if (field.getName().equals(targetField)) {
                targetValue = field.getValue();
            }
        }
        return targetValue;
    }
}
