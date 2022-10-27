package uk.gov.hmcts.probate.service.ocr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ocr.OCRField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.DEPENDANT_KEY_SOLSWILLTYPE;
import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.DEPENDANT_KEY_SOLSWILLTYPEREASON;
import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.SOLICTOR_KEY_IS_APPLYING;

@Slf4j
@Service
@RequiredArgsConstructor
public class NonMandatoryFieldsValidator {
    private final OcrEmailValidator ocrEmailValidator;

    public List<String> ocrToCCDNonMandatoryWarnings(List<OCRField> ocrFields, FormType formType) {
        List<String> warnings = new ArrayList<>();
        warnings.addAll(ocrEmailValidator.validateField(ocrFields));
        Map<String, String> ocrFieldValues = new HashMap<>();
        boolean isSolicitorForm = false;

        ocrFields.forEach(ocrField -> ocrFieldValues.put(ocrField.getName(), ocrField.getValue()));

        switch (formType) {
            case PA1A:
            case PA1P:
                if (ocrFieldValues.containsKey(SOLICTOR_KEY_IS_APPLYING)) {
                    isSolicitorForm = BooleanUtils.toBoolean(ocrFieldValues.get(SOLICTOR_KEY_IS_APPLYING));
                }

                if (isSolicitorForm) {
                    log.warn("Solictor details have been provided this will be flagged as a solicitor case.");
                    warnings.add("The form has been flagged as a Solictor case.");
                }

                if ((ocrFieldValues.containsKey(DEPENDANT_KEY_SOLSWILLTYPE)
                    && StringUtils.isNotBlank(ocrFieldValues.get(DEPENDANT_KEY_SOLSWILLTYPE)))
                    || (ocrFieldValues.containsKey(DEPENDANT_KEY_SOLSWILLTYPEREASON)
                    && StringUtils.isNotBlank(ocrFieldValues.get(DEPENDANT_KEY_SOLSWILLTYPEREASON)))) {
                    log.warn("Solictor details include a will type or reason to be flagged.");
                    warnings.add(
                        "An application type and/or reason has been provided, this will need to be reviewed as it "
                            + "will not be "
                            +
                            "mapped to the case.");
                }
                break;

            default:
                log.error("Error '{}' does not match a known form-type.", formType);
        }

        return warnings;
    }
}
