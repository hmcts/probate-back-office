package uk.gov.hmcts.probate.service.ocr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ocr.OCRField;
import uk.gov.hmcts.probate.service.ocr.pa1a.PA1ACitizenMandatoryFieldsValidator;
import uk.gov.hmcts.probate.service.ocr.pa1a.PA1ASolicitorMandatoryFieldsValidator;
import uk.gov.hmcts.probate.service.ocr.pa1p.PA1PCitizenMandatoryFieldsValidator;
import uk.gov.hmcts.probate.service.ocr.pa1p.PA1PSolicitorMandatoryFieldsValidator;
import uk.gov.hmcts.probate.service.ocr.pa8a.PA8ACitizenMandatoryFieldsValidator;
import uk.gov.hmcts.probate.service.ocr.pa8a.PA8ASolicitorMandatoryFieldsValidator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.LEGAL_REPRESENTATIVE;
import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.SOLICTOR_KEY_IS_APPLYING;

@Slf4j
@Service
@RequiredArgsConstructor
public class OCRToCCDMandatoryField {
    private final PA1PCitizenMandatoryFieldsValidator pa1PCitizenMandatoryFieldsValidator;
    private final PA1PSolicitorMandatoryFieldsValidator pa1PSolicitorMandatoryFieldsValidator;
    private final PA1ACitizenMandatoryFieldsValidator pa1ACitizenMandatoryFieldsValidator;
    private final PA1ASolicitorMandatoryFieldsValidator pa1ASolicitorMandatoryFieldsValidator;
    private final PA8ACitizenMandatoryFieldsValidator pa8ACitizenMandatoryFieldsValidator;
    private final PA8ASolicitorMandatoryFieldsValidator pa8ASolicitorMandatoryFieldsValidator;

    public List<String> ocrToCCDMandatoryFields(List<OCRField> ocrFields, FormType formType) {
        List<String> warnings = new ArrayList<>();
        Map<String, String> ocrFieldValues = new HashMap<>();

        ocrFields.forEach(ocrField -> ocrFieldValues.put(ocrField.getName(), ocrField.getValue()));

        switch (formType) {
            case PA8A:
                warnings.addAll(getWarningsForPA8ACase(ocrFieldValues));
                break;
            case PA1A:
                warnings.addAll(getWarningsForPA1ACase(ocrFieldValues));
                break;
            case PA1P:
                warnings.addAll(getWarningsForPA1PCase(ocrFieldValues));
                break;
            default:
                log.error("Error '{}' does not match a known form-type.", formType);
        }

        return warnings;
    }

    private Collection<? extends String> getWarningsForPA1PCase(Map<String, String> ocrFieldValues) {
        List<String> warnings = new ArrayList<>();
        boolean isSolicitorForm = isSolicitorForm(ocrFieldValues);

        if (isSolicitorForm) {
            pa1PSolicitorMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);
        } else {
            pa1PCitizenMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);
        }
        return warnings;
    }

    private ArrayList<String> getWarningsForPA1ACase(Map<String, String> ocrFieldValues) {
        ArrayList<String> warnings = new ArrayList<>();
        boolean isSolicitorForm = isSolicitorForm(ocrFieldValues);

        if (isSolicitorForm) {
            pa1ASolicitorMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);
        } else {
            pa1ACitizenMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);
        }
        return warnings;
    }

    private ArrayList<String> getWarningsForPA8ACase(Map<String, String> ocrFieldValues) {
        ArrayList<String> warnings = new ArrayList<>();
        boolean isSolicitorForm =  BooleanUtils.toBoolean(ocrFieldValues.get(LEGAL_REPRESENTATIVE));

        if (isSolicitorForm) {
            pa8ASolicitorMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);
        } else {
            pa8ACitizenMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);
        }
        return warnings;
    }

    private boolean isSolicitorForm(Map<String, String> ocrFieldValues) {
        if (ocrFieldValues.containsKey(SOLICTOR_KEY_IS_APPLYING)) {
            return BooleanUtils.toBoolean(ocrFieldValues.get(SOLICTOR_KEY_IS_APPLYING));
        }
        return false;
    }

}
