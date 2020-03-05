package uk.gov.hmcts.probate.service.ocr;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.ocr.CaveatMandatoryFields;
import uk.gov.hmcts.probate.model.ccd.ocr.GORCitizenMandatoryFields;
import uk.gov.hmcts.probate.model.ccd.ocr.GORSolicitorMandatoryFields;
import uk.gov.hmcts.probate.model.ccd.ocr.IntestacyCitizenMandatoryFields;
import uk.gov.hmcts.probate.model.ccd.ocr.IntestacySolicitorMandatoryFields;
import uk.gov.hmcts.probate.model.ocr.OCRField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
public class OCRToCCDMandatoryField {

    private static final String MANDATORY_FIELD_WARNING_STIRNG = "%s (%s) is mandatory.";

    private static final String DEPENDANT_KEY_PRIMARYAPPLICANTALIAS = "primaryApplicantAlias";
    private static final String DEPENDANT_DESC_PRIMARYAPPLICANTALIAS = "Primary applicant alias";
    private static final String DEPENDANT_KEY_IHTREFERENCENUMBER = "ihtReferenceNumber";
    private static final String DEPENDANT_DESC_IHTREFERENCENUMBER = "IHT reference number";
    private static final String DEPENDANT_KEY_IHTFORMID = "ihtFormId";
    private static final String DEPENDANT_DESC_IHTFORMID = "IHT form id";
    private static final String DEPENDANT_KEY_PAPERPAYMENTMETHOD = "paperPaymentMethod";
    private static final String DEPENDANT_KEY_SOLSFEEACCOUNTNUMBER = "solsFeeAccountNumber";
    private static final String DEPENDANT_DESC_SOLSFEEACCOUNTNUMBER = "Solicitors fee account number";

    private static final String MANDATORY_KEY_EXECUTORSNOTAPPLYING_EXECUTORNAME =
            "executorsNotApplying_%s_notApplyingExecutorName";
    private static final String DEPENDANT_KEY_EXECUTORSNOTAPPLYING_EXECUTORREASON =
            "executorsNotApplying_%s_notApplyingExecutorReason";
    private static final String DEPENDANT_DESC_EXECUTORSNOTAPPLYING_EXECUTORREASON =
            "Executor %s not applying reason";
    private static final String MANDATORY_KEY_PRIMARYAPPLICANTHASALIAS = GORSolicitorMandatoryFields.PRIMARY_APPLICANT_HAS_ALIAS.getKey();
    private static final String MANDATORY_KEY_IHTFORMCOMPLETEDONLINE = GORSolicitorMandatoryFields.IHT_FORM_COMPLETED_ONLINE.getKey();

    private static final String SOLICTOR_KEY_IS_APPLYING = "solsSolicitorIsApplying";

    public List<String> ocrToCCDMandatoryFields(List<OCRField> ocrFields, FormType formType) {
        List<String> warnings = new ArrayList<String>();
        HashMap<String, String> ocrFieldValues = new HashMap<String, String>();
        boolean isSolicitorForm = false;

        ocrFields.forEach(ocrField -> {
            ocrFieldValues.put(ocrField.getName(), ocrField.getValue());
        });

        switch (formType) {
            case PA8A:
                Stream.of(CaveatMandatoryFields.values()).forEach(field -> {
                    log.info("Checking {} against ocr fields", field.getKey());
                    if (!ocrFieldValues.containsKey(field.getKey())) {
                        log.warn("{} was not found in ocr fields", field.getKey());
                        warnings.add(String.format(MANDATORY_FIELD_WARNING_STIRNG, field.getValue(), field.getKey()));
                    }
                });
                break;
            case PA1A:
                if (ocrFieldValues.containsKey(SOLICTOR_KEY_IS_APPLYING)) {
                    isSolicitorForm = BooleanUtils.toBoolean(ocrFieldValues.get(SOLICTOR_KEY_IS_APPLYING));
                }

                if (isSolicitorForm) {
                    Stream.of(IntestacySolicitorMandatoryFields.values()).forEach(field -> {
                        log.info("Checking {} against ocr fields", field.getKey());
                        if (!ocrFieldValues.containsKey(field.getKey())) {
                            log.warn("{} was not found in ocr fields", field.getKey());
                            warnings.add(String.format(MANDATORY_FIELD_WARNING_STIRNG, field.getValue(), field.getKey()));
                        }
                    });

                    if (ocrFieldValues.containsKey(DEPENDANT_KEY_PAPERPAYMENTMETHOD)
                            && ocrFieldValues.get(DEPENDANT_KEY_PAPERPAYMENTMETHOD).equalsIgnoreCase("PBA")
                            && StringUtils.isBlank(ocrFieldValues.get(DEPENDANT_KEY_SOLSFEEACCOUNTNUMBER))
                    ) {
                        log.warn("{} was not found in ocr fields", DEPENDANT_KEY_SOLSFEEACCOUNTNUMBER);
                        warnings.add(String.format(MANDATORY_FIELD_WARNING_STIRNG,
                                DEPENDANT_DESC_SOLSFEEACCOUNTNUMBER, DEPENDANT_KEY_SOLSFEEACCOUNTNUMBER));
                    }
                } else {
                    Stream.of(IntestacyCitizenMandatoryFields.values()).forEach(field -> {
                        log.info("Checking {} against ocr fields", field.getKey());
                        if (!ocrFieldValues.containsKey(field.getKey())) {
                            log.warn("{} was not found in ocr fields", field.getKey());
                            warnings.add(String.format(MANDATORY_FIELD_WARNING_STIRNG, field.getValue(), field.getKey()));
                        }
                    });
                }

                if (ocrFieldValues.containsKey(MANDATORY_KEY_IHTFORMCOMPLETEDONLINE)) {
                    boolean result = BooleanUtils.toBoolean(ocrFieldValues.get(MANDATORY_KEY_IHTFORMCOMPLETEDONLINE));
                    if (result && !ocrFieldValues.containsKey(DEPENDANT_KEY_IHTREFERENCENUMBER)) {
                        log.warn("{} was not found in ocr fields", DEPENDANT_KEY_IHTREFERENCENUMBER);
                        warnings.add(String.format(MANDATORY_FIELD_WARNING_STIRNG,
                                DEPENDANT_DESC_IHTREFERENCENUMBER, DEPENDANT_KEY_IHTREFERENCENUMBER));
                    } else if (!result && !ocrFieldValues.containsKey(DEPENDANT_KEY_IHTFORMID)) {
                        log.warn("{} was not found in ocr fields", DEPENDANT_KEY_IHTFORMID);
                        warnings.add(String.format(MANDATORY_FIELD_WARNING_STIRNG, DEPENDANT_DESC_IHTFORMID, DEPENDANT_KEY_IHTFORMID));
                    }
                }
                break;
            case PA1P:
                if (ocrFieldValues.containsKey(SOLICTOR_KEY_IS_APPLYING)) {
                    isSolicitorForm = BooleanUtils.toBoolean(ocrFieldValues.get(SOLICTOR_KEY_IS_APPLYING));
                }

                if (isSolicitorForm) {
                    Stream.of(GORSolicitorMandatoryFields.values()).forEach(field -> {
                        log.info("Checking {} against ocr fields", field.getKey());
                        if (!ocrFieldValues.containsKey(field.getKey())) {
                            log.warn("{} was not found in ocr fields", field.getKey());
                            warnings.add(String.format(MANDATORY_FIELD_WARNING_STIRNG, field.getValue(), field.getKey()));
                        }
                    });

                    if (ocrFieldValues.containsKey(DEPENDANT_KEY_PAPERPAYMENTMETHOD)
                            && ocrFieldValues.get(DEPENDANT_KEY_PAPERPAYMENTMETHOD).equalsIgnoreCase("PBA")
                            && StringUtils.isBlank(ocrFieldValues.get(DEPENDANT_KEY_SOLSFEEACCOUNTNUMBER))
                    ) {
                        log.warn("{} was not found in ocr fields", DEPENDANT_KEY_SOLSFEEACCOUNTNUMBER);
                        warnings.add(String.format(MANDATORY_FIELD_WARNING_STIRNG,
                                DEPENDANT_DESC_SOLSFEEACCOUNTNUMBER, DEPENDANT_KEY_SOLSFEEACCOUNTNUMBER));
                    }
                } else {
                    Stream.of(GORCitizenMandatoryFields.values()).forEach(field -> {
                        log.info("Checking {} against ocr fields", field.getKey());
                        if (!ocrFieldValues.containsKey(field.getKey())) {
                            log.warn("{} was not found in ocr fields", field.getKey());
                            warnings.add(String.format(MANDATORY_FIELD_WARNING_STIRNG, field.getValue(), field.getKey()));
                        }
                    });
                }

                if (ocrFieldValues.containsKey(MANDATORY_KEY_PRIMARYAPPLICANTHASALIAS)) {
                    boolean result = BooleanUtils.toBoolean(ocrFieldValues.get(MANDATORY_KEY_PRIMARYAPPLICANTHASALIAS));
                    if (result && !ocrFieldValues.containsKey(DEPENDANT_KEY_PRIMARYAPPLICANTALIAS)) {
                        log.warn("{} was not found in ocr fields", DEPENDANT_KEY_PRIMARYAPPLICANTALIAS);
                        warnings.add(String.format(MANDATORY_FIELD_WARNING_STIRNG,
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
                            log.warn("{} was not found in ocr fields", executorNotApplyingReasonKey);
                            warnings.add(String.format(MANDATORY_FIELD_WARNING_STIRNG,
                                    executorNotApplyingReasonDesc, executorNotApplyingReasonKey));
                        }
                    }
                }

                if (ocrFieldValues.containsKey(MANDATORY_KEY_IHTFORMCOMPLETEDONLINE)) {
                    boolean result = BooleanUtils.toBoolean(ocrFieldValues.get(MANDATORY_KEY_IHTFORMCOMPLETEDONLINE));
                    if (result && !ocrFieldValues.containsKey(DEPENDANT_KEY_IHTREFERENCENUMBER)) {
                        log.warn("{} was not found in ocr fields", DEPENDANT_KEY_IHTREFERENCENUMBER);
                        warnings.add(String.format(MANDATORY_FIELD_WARNING_STIRNG,
                                DEPENDANT_DESC_IHTREFERENCENUMBER, DEPENDANT_KEY_IHTREFERENCENUMBER));
                    } else if (!result && !ocrFieldValues.containsKey(DEPENDANT_KEY_IHTFORMID)) {
                        log.warn("{} was not found in ocr fields", DEPENDANT_KEY_IHTFORMID);
                        warnings.add(String.format(MANDATORY_FIELD_WARNING_STIRNG, DEPENDANT_DESC_IHTFORMID, DEPENDANT_KEY_IHTFORMID));
                    }
                }
                break;
            default:
                log.error("Error '{}' does not match a known form-type.", formType);
        }

        return warnings;
    }
}
