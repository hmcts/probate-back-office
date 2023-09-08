package uk.gov.hmcts.probate.service.ocr.pa1p;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.DEPENDANT_DESC_EXECUTORSNOTAPPLYING_EXECUTORREASON;
import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.DEPENDANT_DESC_IHTFORMID;
import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.DEPENDANT_DESC_IHTREFERENCENUMBER;
import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.DEPENDANT_DESC_PRIMARYAPPLICANTALIAS;
import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.DEPENDANT_KEY_EXECUTORSNOTAPPLYING_EXECUTORREASON;
import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.DEPENDANT_KEY_IHTFORMID;
import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.DEPENDANT_KEY_IHTREFERENCENUMBER;
import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.DEPENDANT_KEY_PRIMARYAPPLICANTALIAS;
import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.MANDATORY_FIELD_NOT_FOUND_LOG;
import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.MANDATORY_FIELD_WARNING_STRING;
import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.MANDATORY_KEY_EXECUTORSNOTAPPLYING_EXECUTORNAME;
import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.MANDATORY_KEY_IHTFORMCOMPLETEDONLINE;
import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.MANDATORY_KEY_FORM_VERSION;
import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.MANDATORY_KEY_PRIMARYAPPLICANTHASALIAS;

@Slf4j
@Service
@RequiredArgsConstructor
public class PA1PCommonMandatoryFieldsValidator {

    public void addWarnings(Map<String, String> ocrFieldValues, List<String> warnings) {
        if (ocrFieldValues.containsKey(MANDATORY_KEY_PRIMARYAPPLICANTHASALIAS)) {
            boolean result = BooleanUtils.toBoolean(ocrFieldValues.get(MANDATORY_KEY_PRIMARYAPPLICANTHASALIAS));
            if (result && !ocrFieldValues.containsKey(DEPENDANT_KEY_PRIMARYAPPLICANTALIAS)) {
                log.warn(MANDATORY_FIELD_NOT_FOUND_LOG, DEPENDANT_KEY_PRIMARYAPPLICANTALIAS);
                warnings.add(String.format(MANDATORY_FIELD_WARNING_STRING,
                    DEPENDANT_DESC_PRIMARYAPPLICANTALIAS, DEPENDANT_KEY_PRIMARYAPPLICANTALIAS));
            }
        }

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

        if (ocrFieldValues.containsKey(MANDATORY_KEY_IHTFORMCOMPLETEDONLINE)) {
            boolean result = BooleanUtils.toBoolean(ocrFieldValues.get(MANDATORY_KEY_IHTFORMCOMPLETEDONLINE));
            if (ocrFieldValues.containsKey(MANDATORY_KEY_FORM_VERSION)
                    && "1" == ocrFieldValues.get(MANDATORY_KEY_FORM_VERSION)) { //Need to change after 3640 is pushed
                if (result && !ocrFieldValues.containsKey(DEPENDANT_KEY_IHTREFERENCENUMBER)) {
                    log.warn(MANDATORY_FIELD_NOT_FOUND_LOG, DEPENDANT_KEY_IHTREFERENCENUMBER);
                    warnings.add(String.format(MANDATORY_FIELD_WARNING_STRING,
                            DEPENDANT_DESC_IHTREFERENCENUMBER, DEPENDANT_KEY_IHTREFERENCENUMBER));
                } else if (!result && !ocrFieldValues.containsKey(DEPENDANT_KEY_IHTFORMID)) {
                    log.warn(MANDATORY_FIELD_NOT_FOUND_LOG, DEPENDANT_KEY_IHTFORMID);
                    warnings.add(
                            String.format(MANDATORY_FIELD_WARNING_STRING,
                                    DEPENDANT_DESC_IHTFORMID, DEPENDANT_KEY_IHTFORMID));
                }
            }
        }
    }
}
