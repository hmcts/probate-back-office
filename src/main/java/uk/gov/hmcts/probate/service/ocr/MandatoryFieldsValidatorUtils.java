package uk.gov.hmcts.probate.service.ocr;

import static java.lang.String.format;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.keyvalue.DefaultKeyValue;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.ocr.GORCitizenMandatoryFields;
import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.MANDATORY_FIELD_WARNING_STIRNG;

@Slf4j
@Service
@RequiredArgsConstructor
public class MandatoryFieldsValidatorUtils {
    private static final String VERSION2_KEY = "formVersion";

    public void addWarningIfEmpty(Map<String, String> ocrFieldValues, List<String> warnings,
                                  DefaultKeyValue keyValue) {
        if (isEmpty(ocrFieldValues.get(keyValue.getKey()))) {
            log.warn("{} was not found in ocr fields when expected", keyValue.getKey());
            warnings.add(format(MANDATORY_FIELD_WARNING_STIRNG, keyValue.getValue(),
                keyValue.getKey()));
        }
    }

    public void addWarningsForConditionalFields(Map<String, String> ocrFieldValues, List<String> warnings,
                                                GORCitizenMandatoryFields... toCheck) {
        Stream.of(toCheck).forEach(field -> {
            if (!ocrFieldValues.containsKey(field.getKey())) {
                log.warn("{} was not found in ocr fields when expected", field.getKey());
                warnings.add(format(MANDATORY_FIELD_WARNING_STIRNG, field.getValue(), field.getKey()));
            }
        });
    }

    public void addWarning(String warning, List<String> warnings) {
        log.warn(warning);
        warnings.add(warning);
    }

    public boolean isVersion2(Map<String, String> ocrFieldValues) {
        return "2".equals(ocrFieldValues.get(VERSION2_KEY));
    }
}
