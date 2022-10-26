package uk.gov.hmcts.probate.service.ocr.pa1a;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.DEPENDANT_DESC_IHTFORMID;
import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.DEPENDANT_DESC_IHTREFERENCENUMBER;
import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.DEPENDANT_KEY_IHTFORMID;
import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.DEPENDANT_KEY_IHTREFERENCENUMBER;
import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.MANDATORY_FIELD_NOT_FOUND_LOG;
import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.MANDATORY_FIELD_WARNING_STRING;
import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.MANDATORY_KEY_IHTFORMCOMPLETEDONLINE;

@Slf4j
@Service
@RequiredArgsConstructor
public class PA1ACommonMandatoryFieldsValidator {

    public void addWarnings(Map<String, String> ocrFieldValues, List<String> warnings) {
        if (ocrFieldValues.containsKey(MANDATORY_KEY_IHTFORMCOMPLETEDONLINE)) {
            boolean result = BooleanUtils.toBoolean(ocrFieldValues.get(MANDATORY_KEY_IHTFORMCOMPLETEDONLINE));
            if (result && !ocrFieldValues.containsKey(DEPENDANT_KEY_IHTREFERENCENUMBER)) {
                log.warn(MANDATORY_FIELD_NOT_FOUND_LOG, DEPENDANT_KEY_IHTREFERENCENUMBER);
                warnings.add(String.format(MANDATORY_FIELD_WARNING_STRING,
                    DEPENDANT_DESC_IHTREFERENCENUMBER, DEPENDANT_KEY_IHTREFERENCENUMBER));
            } else if (!result && !ocrFieldValues.containsKey(DEPENDANT_KEY_IHTFORMID)) {
                log.warn(MANDATORY_FIELD_NOT_FOUND_LOG, DEPENDANT_KEY_IHTFORMID);
                warnings.add(
                    String.format(MANDATORY_FIELD_WARNING_STRING, DEPENDANT_DESC_IHTFORMID, DEPENDANT_KEY_IHTFORMID));
            }
        }
    }
}
