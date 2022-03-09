package uk.gov.hmcts.probate.service.ocr.pa1a;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.ocr.IntestacyCitizenMandatoryFields;
import uk.gov.hmcts.probate.service.ocr.CitizenMandatoryFieldsValidatorV2;
import uk.gov.hmcts.probate.service.ocr.MandatoryFieldsValidatorUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.lang.String.format;
import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.MANDATORY_FIELD_NOT_FOUND_LOG;
import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.MANDATORY_FIELD_WARNING_STIRNG;

@Slf4j
@Service
@RequiredArgsConstructor
public class PA1ACitizenMandatoryFieldsValidator {
    private final MandatoryFieldsValidatorUtils mandatoryFieldsValidatorUtils;
    private final CitizenMandatoryFieldsValidatorV2 citizenMandatoryFieldsValidatorV2;

    public void addWarnings(Map<String, String> ocrFieldValues, List<String> warnings) {
        if (mandatoryFieldsValidatorUtils.isVersion2(ocrFieldValues)) {
            addWarningsFormVersion2(ocrFieldValues, warnings);
        } else {
            addWarningsFormVersion1(ocrFieldValues, warnings);
        }

    }

    private void addWarningsFormVersion1(Map<String, String> ocrFieldValues, List<String> warnings) {
        Stream.of(IntestacyCitizenMandatoryFields.values()).filter(IntestacyCitizenMandatoryFields::isVersion1)
            .forEach(field -> {
                log.info("Checking {} against ocr fields", field.getKey());
                if (!ocrFieldValues.containsKey(field.getKey())) {
                    log.warn(MANDATORY_FIELD_NOT_FOUND_LOG, field.getKey());
                    warnings.add(String.format(MANDATORY_FIELD_WARNING_STIRNG, field.getValue(), field.getKey()));
                }
            });
    }

    private void addWarningsFormVersion2(Map<String, String> ocrFieldValues, List<String> warnings) {
        Stream.of(IntestacyCitizenMandatoryFields.values()).filter(IntestacyCitizenMandatoryFields::isVersion2)
            .forEach(field -> {
                log.info("Checking v2 {} against ocr fields", field.getKey());
                if (!ocrFieldValues.containsKey(field.getKey())) {
                    log.warn("v2 " + MANDATORY_FIELD_NOT_FOUND_LOG, field.getKey());
                    warnings.add(format(MANDATORY_FIELD_WARNING_STIRNG, field.getValue(), field.getKey()));
                }
            });

        citizenMandatoryFieldsValidatorV2.addWarnings(ocrFieldValues, warnings);
    }
}
