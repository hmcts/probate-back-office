package uk.gov.hmcts.probate.service.ocr.pa1a;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.service.ocr.CommonMandatoryFieldsValidatorV3;
import uk.gov.hmcts.probate.service.ocr.MandatoryFieldsValidatorUtils;

import java.util.List;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class PA1ACommonMandatoryFieldsValidator {
    private final MandatoryFieldsValidatorUtils mandatoryFieldsValidatorUtils;
    private final CommonMandatoryFieldsValidatorV3 commonMandatoryFieldsValidatorV3;

    public void addWarnings(Map<String, String> ocrFieldValues, List<String> warnings) {
        if (mandatoryFieldsValidatorUtils.isVersion3(ocrFieldValues)) {
            commonMandatoryFieldsValidatorV3.addWarnings(ocrFieldValues, warnings);
        }
    }
}
