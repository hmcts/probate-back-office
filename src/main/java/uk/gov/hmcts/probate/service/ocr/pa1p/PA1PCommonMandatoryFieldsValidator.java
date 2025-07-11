package uk.gov.hmcts.probate.service.ocr.pa1p;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.service.ocr.CommonMandatoryFieldsValidatorV3;
import uk.gov.hmcts.probate.service.ocr.MandatoryFieldsValidatorUtils;

import java.util.List;
import java.util.Map;

import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.DEPENDANT_DESC_EXECUTORSNOTAPPLYING_EXECUTORREASON;
import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.DEPENDANT_KEY_EXECUTORSNOTAPPLYING_EXECUTORREASON;
import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.MANDATORY_FIELD_NOT_FOUND_LOG;
import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.MANDATORY_FIELD_WARNING_STRING;
import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.MANDATORY_KEY_EXECUTORSNOTAPPLYING_EXECUTORNAME;

@Slf4j
@Service
@RequiredArgsConstructor
public class PA1PCommonMandatoryFieldsValidator {
    private final MandatoryFieldsValidatorUtils mandatoryFieldsValidatorUtils;
    private final CommonMandatoryFieldsValidatorV3 commonMandatoryFieldsValidatorV3;

    public void addWarnings(Map<String, String> ocrFieldValues, List<String> warnings) {

        for (int i = 0; i < 3; i++) {
            String executorNotApplyingNameKey =
                String.format(MANDATORY_KEY_EXECUTORSNOTAPPLYING_EXECUTORNAME, i);
            String executorNotApplyingReasonKey =
                String.format(DEPENDANT_KEY_EXECUTORSNOTAPPLYING_EXECUTORREASON, i);
            String executorNotApplyingReasonDesc =
                String.format(DEPENDANT_DESC_EXECUTORSNOTAPPLYING_EXECUTORREASON, i);
            if (ocrFieldValues.containsKey(executorNotApplyingNameKey)) {
                boolean resultPopulated = !ocrFieldValues.get(executorNotApplyingNameKey).isEmpty();
                if (resultPopulated && !ocrFieldValues.containsKey(executorNotApplyingReasonKey)) {
                    log.warn(MANDATORY_FIELD_NOT_FOUND_LOG, executorNotApplyingReasonKey);
                    warnings.add(String.format(MANDATORY_FIELD_WARNING_STRING,
                        executorNotApplyingReasonDesc, executorNotApplyingReasonKey));
                }
            }
        }
    }
}
