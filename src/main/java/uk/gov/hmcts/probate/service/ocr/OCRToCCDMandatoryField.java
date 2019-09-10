package uk.gov.hmcts.probate.service.ocr;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.ocr.IntestacyMandatoryFields;
import uk.gov.hmcts.probate.model.ccd.ocr.GORMandatoryFields;
import uk.gov.hmcts.probate.model.ccd.ocr.CaveatMandatoryFields;
import uk.gov.hmcts.probate.model.ocr.OCRField;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Stream;

@Slf4j
@Service
public class OCRToCCDMandatoryField {

    private static final String MANDATORY_FIELD_WARNING_STIRNG = "Key '%s' is mandatory.";
    private static final String DEPENDANT_KEY_PRIMARYAPPLICANTALIAS = "primaryApplicantAlias";
    private static final String DEPENDANT_KEY_IHTREFERENCENUMBER = "ihtReferenceNumber";
    private static final String DEPENDANT_KEY_IHTFORMID = "ihtFormId";
    private static final String MANDATORY_KEY_EXECUTORSNOTAPPLYING_EXECUTORNAME =
            "executorsNotApplying_%s_notApplyingExecutorName";
    private static final String DEPENDANT_KEY_EXECUTORSNOTAPPLYING_EXECUTORREASON =
            "executorsNotApplying_%s_notApplyingExecutorReason";
    private static final String MANDATORY_KEY_PRIMARYAPPLICANTHASALIAS = GORMandatoryFields.PRIMARY_APPLICANT_HAS_ALIAS.getKey();
    private static final String MANDATORY_KEY_IHTFORMCOMPLETEDONLINE = GORMandatoryFields.IHT_FORM_COMPLETED_ONLINE.getKey();

    public List<String> ocrToCCDMandatoryFields(List<OCRField> ocrFields, FormType formType) {
        List<String> descriptions = new ArrayList<String>();
        HashMap<String, String> ocrFieldValues = new HashMap<String, String>();

        ocrFields.forEach(ocrField -> {
            ocrFieldValues.put(ocrField.getName(), ocrField.getValue());
        });

        switch (formType) {
            case PA8A:
                Stream.of(CaveatMandatoryFields.values()).forEach(field -> {
                    log.info("Checking {} against ocr fields", field.getKey());
                    if (!ocrFieldValues.containsKey(field.getKey())) {
                        log.warn("{} was not found in ocr fields", field.getKey());
                        descriptions.add(String.format(MANDATORY_FIELD_WARNING_STIRNG, field.getKey()));
                    }
                });
                break;
            case PA1A:
                Stream.of(IntestacyMandatoryFields.values()).forEach(field -> {
                    log.info("Checking {} against ocr fields", field.getKey());
                    if (!ocrFieldValues.containsKey(field.getKey())) {
                        log.warn("{} was not found in ocr fields", field.getKey());
                        descriptions.add(String.format(MANDATORY_FIELD_WARNING_STIRNG, field.getKey()));
                    }
                });

                if (ocrFieldValues.containsKey(MANDATORY_KEY_IHTFORMCOMPLETEDONLINE)) {
                    boolean result = BooleanUtils.toBoolean(ocrFieldValues.get(MANDATORY_KEY_IHTFORMCOMPLETEDONLINE));
                    if (result && !ocrFieldValues.containsKey(DEPENDANT_KEY_IHTREFERENCENUMBER)) {
                        log.warn("{} was not found in ocr fields", DEPENDANT_KEY_IHTREFERENCENUMBER);
                        descriptions.add(String.format(MANDATORY_FIELD_WARNING_STIRNG, DEPENDANT_KEY_IHTREFERENCENUMBER));
                    } else if (!result && !ocrFieldValues.containsKey(DEPENDANT_KEY_IHTFORMID)) {
                        log.warn("{} was not found in ocr fields", DEPENDANT_KEY_IHTFORMID);
                        descriptions.add(String.format(MANDATORY_FIELD_WARNING_STIRNG, DEPENDANT_KEY_IHTFORMID));
                    }
                }
                break;
            case PA1P:
                Stream.of(GORMandatoryFields.values()).forEach(field -> {
                    log.info("Checking {} against ocr fields", field.getKey());
                    if (!ocrFieldValues.containsKey(field.getKey())) {
                        log.warn("{} was not found in ocr fields", field.getKey());
                        descriptions.add(String.format(MANDATORY_FIELD_WARNING_STIRNG, field.getKey()));
                    }
                });

                if (ocrFieldValues.containsKey(MANDATORY_KEY_PRIMARYAPPLICANTHASALIAS)) {
                    boolean result = BooleanUtils.toBoolean(ocrFieldValues.get(MANDATORY_KEY_PRIMARYAPPLICANTHASALIAS));
                    if (result && !ocrFieldValues.containsKey(DEPENDANT_KEY_PRIMARYAPPLICANTALIAS)) {
                        log.warn("{} was not found in ocr fields", DEPENDANT_KEY_PRIMARYAPPLICANTALIAS);
                        descriptions.add(String.format(MANDATORY_FIELD_WARNING_STIRNG, DEPENDANT_KEY_PRIMARYAPPLICANTALIAS));
                    }
                }

                for (int i = 0; i < 3; i++) {
                    String executorNotApplyingNameKey =
                            String.format(MANDATORY_KEY_EXECUTORSNOTAPPLYING_EXECUTORNAME, i);
                    String executorNotApplyingReasonKey =
                            String.format(DEPENDANT_KEY_EXECUTORSNOTAPPLYING_EXECUTORREASON, i);
                    if (ocrFieldValues.containsKey(executorNotApplyingNameKey)) {
                        boolean resultPopulated = !ocrFieldValues.get(executorNotApplyingNameKey).isEmpty();
                        if (resultPopulated && !ocrFieldValues.containsKey(executorNotApplyingReasonKey)) {
                            log.warn("{} was not found in ocr fields", executorNotApplyingReasonKey);
                            descriptions.add(String.format(MANDATORY_FIELD_WARNING_STIRNG, executorNotApplyingReasonKey));
                        }
                    }
                }

                if (ocrFieldValues.containsKey(MANDATORY_KEY_IHTFORMCOMPLETEDONLINE)) {
                    boolean result = BooleanUtils.toBoolean(ocrFieldValues.get(MANDATORY_KEY_IHTFORMCOMPLETEDONLINE));
                    if (result && !ocrFieldValues.containsKey(DEPENDANT_KEY_IHTREFERENCENUMBER)) {
                        log.warn("{} was not found in ocr fields", DEPENDANT_KEY_IHTREFERENCENUMBER);
                        descriptions.add(String.format(MANDATORY_FIELD_WARNING_STIRNG, DEPENDANT_KEY_IHTREFERENCENUMBER));
                    } else if (!result && !ocrFieldValues.containsKey(DEPENDANT_KEY_IHTFORMID)) {
                        log.warn("{} was not found in ocr fields", DEPENDANT_KEY_IHTFORMID);
                        descriptions.add(String.format(MANDATORY_FIELD_WARNING_STIRNG, DEPENDANT_KEY_IHTFORMID));
                    }
                }
                break;
            default:
                log.error("Error '{}' does not match a known form-type.", formType);
                break;
        }

        return descriptions;
    }
}
