package uk.gov.hmcts.probate.service.ocr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.ocr.CaveatCitizenMandatoryFields;
import uk.gov.hmcts.probate.model.ccd.ocr.CaveatSolicitorMandatoryFields;
import uk.gov.hmcts.probate.model.ocr.OCRField;
import uk.gov.hmcts.probate.service.ocr.pa1a.PA1ACitizenMandatoryFieldsValidator;
import uk.gov.hmcts.probate.service.ocr.pa1a.PA1ACommonMandatoryFieldsValidator;
import uk.gov.hmcts.probate.service.ocr.pa1a.PA1ASolicitorMandatoryFieldsValidator;
import uk.gov.hmcts.probate.service.ocr.pa1p.PA1PCitizenMandatoryFieldsValidator;
import uk.gov.hmcts.probate.service.ocr.pa1p.PA1PCommonMandatoryFieldsValidator;
import uk.gov.hmcts.probate.service.ocr.pa1p.PA1PSolicitorMandatoryFieldsValidator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.MANDATORY_FIELD_NOT_FOUND_LOG;
import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.MANDATORY_FIELD_WARNING_STIRNG;
import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.SOLICTOR_KEY_FIRM_NAME;
import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.SOLICTOR_KEY_IS_APPLYING;
import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.SOLICTOR_KEY_REPRESENTATIVE_NAME;

@Slf4j
@Service
@RequiredArgsConstructor
public class OCRToCCDMandatoryField {

    private final PA1PCommonMandatoryFieldsValidator pa1PCommonMandatoryFieldsValidator;
    private final PA1PCitizenMandatoryFieldsValidator pa1PCitizenMandatoryFieldsValidator;
    private final PA1PSolicitorMandatoryFieldsValidator pa1PSolicitorMandatoryFieldsValidator;
    private final PA1ACitizenMandatoryFieldsValidator pa1ACitizenMandatoryFieldsValidator;
    private final PA1ASolicitorMandatoryFieldsValidator pa1ASolicitorMandatoryFieldsValidator;
    private final PA1ACommonMandatoryFieldsValidator pa1ACommonMandatoryFieldsValidator;

    public List<String> ocrToCCDMandatoryFields(List<OCRField> ocrFields, FormType formType) {
        List<String> warnings = new ArrayList<>();
        Map<String, String> ocrFieldValues = new HashMap<String, String>();

        ocrFields.forEach(ocrField -> {
            ocrFieldValues.put(ocrField.getName(), ocrField.getValue());
        });

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
        boolean isSolicitorForm = false;

        if (ocrFieldValues.containsKey(SOLICTOR_KEY_IS_APPLYING)) {
            isSolicitorForm = BooleanUtils.toBoolean(ocrFieldValues.get(SOLICTOR_KEY_IS_APPLYING));
        }

        if (isSolicitorForm) {
            pa1PSolicitorMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);
        } else {
            pa1PCitizenMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);
        }

        pa1PCommonMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);
        return warnings;
    }

    private ArrayList<String> getWarningsForPA1ACase(Map<String, String> ocrFieldValues) {
        ArrayList<String> warnings = new ArrayList<>();
        boolean isSolicitorForm = false;
        if (ocrFieldValues.containsKey(SOLICTOR_KEY_IS_APPLYING)) {
            isSolicitorForm = BooleanUtils.toBoolean(ocrFieldValues.get(SOLICTOR_KEY_IS_APPLYING));
        }

        if (isSolicitorForm) {
            pa1ASolicitorMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);
        } else {
            pa1ACitizenMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);
        }
        pa1ACitizenMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);

        return warnings;
    }

    private ArrayList<String> getWarningsForPA8ACase(Map<String, String> ocrFieldValues) {
        ArrayList<String> warnings = new ArrayList<>();
        boolean isSolicitorForm = false;
        if (StringUtils.isNotBlank(ocrFieldValues.get(SOLICTOR_KEY_REPRESENTATIVE_NAME))
            || (StringUtils.isNotBlank(ocrFieldValues.get(SOLICTOR_KEY_FIRM_NAME)))) {
            isSolicitorForm = true;
        }

        if (isSolicitorForm) {
            Stream.of(CaveatSolicitorMandatoryFields.values()).forEach(field -> {
                log.info("Checking {} against ocr fields", field.getKey());
                if (!ocrFieldValues.containsKey(field.getKey())) {
                    log.warn(MANDATORY_FIELD_NOT_FOUND_LOG, field.getKey());
                    warnings.add(String.format(MANDATORY_FIELD_WARNING_STIRNG, field.getValue(), field.getKey()));
                }
            });

        } else {
            Stream.of(CaveatCitizenMandatoryFields.values()).forEach(field -> {
                log.info("Checking {} against ocr fields", field.getKey());
                if (!ocrFieldValues.containsKey(field.getKey())) {
                    log.warn(MANDATORY_FIELD_NOT_FOUND_LOG, field.getKey());
                    warnings.add(String.format(MANDATORY_FIELD_WARNING_STIRNG, field.getValue(), field.getKey()));
                }
            });
        }
        return warnings;
    }

}
