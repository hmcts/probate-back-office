package uk.gov.hmcts.probate.service.ocr.pa1p;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.ocr.GORSolicitorMandatoryFields;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.DEPENDANT_DESC_SOLSFEEACCOUNTNUMBER;
import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.DEPENDANT_KEY_PAPERPAYMENTMETHOD;
import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.DEPENDANT_KEY_SOLSFEEACCOUNTNUMBER;
import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.MANDATORY_FIELD_NOT_FOUND_LOG;
import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.MANDATORY_FIELD_WARNING_STIRNG;

@Slf4j
@Service
@RequiredArgsConstructor
public class PA1PSolicitorMandatoryFieldsValidator {

    public void addWarnings(Map<String, String> ocrFieldValues, List<String> warnings) {
        addWarningsFormVersion1(ocrFieldValues, warnings);
    }

    private void addWarningsFormVersion1(Map<String, String> ocrFieldValues, List<String> warnings) {
        Stream.of(GORSolicitorMandatoryFields.values()).forEach(field -> {
            log.info("Checking {} against ocr fields", field.getKey());
            if (!ocrFieldValues.containsKey(field.getKey())) {
                log.warn(MANDATORY_FIELD_NOT_FOUND_LOG, field.getKey());
                warnings.add(String.format(MANDATORY_FIELD_WARNING_STIRNG, field.getValue(), field.getKey()));
            }
        });

        if (ocrFieldValues.containsKey(DEPENDANT_KEY_PAPERPAYMENTMETHOD)
            && ocrFieldValues.get(DEPENDANT_KEY_PAPERPAYMENTMETHOD).equalsIgnoreCase("PBA")
            && StringUtils.isBlank(ocrFieldValues.get(DEPENDANT_KEY_SOLSFEEACCOUNTNUMBER))
        ) {
            log.warn(MANDATORY_FIELD_NOT_FOUND_LOG, DEPENDANT_KEY_SOLSFEEACCOUNTNUMBER);
            warnings.add(String.format(MANDATORY_FIELD_WARNING_STIRNG,
                DEPENDANT_DESC_SOLSFEEACCOUNTNUMBER, DEPENDANT_KEY_SOLSFEEACCOUNTNUMBER));
        }
    }
}
