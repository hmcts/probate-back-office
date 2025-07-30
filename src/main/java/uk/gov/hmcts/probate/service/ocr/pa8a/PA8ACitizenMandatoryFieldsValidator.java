package uk.gov.hmcts.probate.service.ocr.pa8a;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.ocr.CaveatCitizenMandatoryFields;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.MANDATORY_FIELD_NOT_FOUND_LOG;
import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.MANDATORY_FIELD_WARNING_STRING;

@Slf4j
@Service
@RequiredArgsConstructor
public class PA8ACitizenMandatoryFieldsValidator {

    public void addWarnings(Map<String, String> ocrFieldValues, List<String> warnings) {
        Stream.of(CaveatCitizenMandatoryFields.values()).forEach(field -> {
            log.info("Checking {} against ocr fields", field.getKey());
            if (!ocrFieldValues.containsKey(field.getKey())) {
                log.warn(MANDATORY_FIELD_NOT_FOUND_LOG, field.getKey());
                warnings.add(String.format(MANDATORY_FIELD_WARNING_STRING, field.getValue(), field.getKey()));
            }
        });
    }

}