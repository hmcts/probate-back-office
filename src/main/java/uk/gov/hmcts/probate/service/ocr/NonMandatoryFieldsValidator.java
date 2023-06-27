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
            case PA1A, PA1P:
                if (ocrFieldValues.containsKey(SOLICTOR_KEY_IS_APPLYING)) {
                    isSolicitorForm = BooleanUtils.toBoolean(ocrFieldValues.get(SOLICTOR_KEY_IS_APPLYING));
                }

                if (isSolicitorForm) {
                    log.info("Solicitor Form scanned type {} has {} fields", formType, ocrFields.stream().count());
                }

                break;

            default:
                log.error("Error '{}' does not match a known form-type.", formType);
        }

        return warnings;
    }
}
