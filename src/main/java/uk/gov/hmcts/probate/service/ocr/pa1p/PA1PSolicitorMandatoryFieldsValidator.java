package uk.gov.hmcts.probate.service.ocr.pa1p;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.keyvalue.DefaultKeyValue;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.ocr.GORSolicitorMandatoryFields;
import uk.gov.hmcts.probate.service.ocr.MandatoryFieldsValidatorUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.lang.String.format;
import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.DEPENDANT_DESC_SOLSFEEACCOUNTNUMBER;
import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.DEPENDANT_KEY_PAPERPAYMENTMETHOD;
import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.DEPENDANT_KEY_SOLSFEEACCOUNTNUMBER;
import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.MANDATORY_FIELD_NOT_FOUND_LOG;
import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.MANDATORY_FIELD_WARNING_STRING;

@Slf4j
@Service
@RequiredArgsConstructor
public class PA1PSolicitorMandatoryFieldsValidator {
    private final MandatoryFieldsValidatorUtils mandatoryFieldsValidatorUtils;
    public static final DefaultKeyValue F = new DefaultKeyValue("iht207completed",
            "Did you complete an IHT207 form?");

    public void addWarnings(Map<String, String> ocrFieldValues, List<String> warnings) {
        if (!mandatoryFieldsValidatorUtils.addWarningForNoFormVersion(ocrFieldValues, warnings)) {
            if (mandatoryFieldsValidatorUtils.isVersion3(ocrFieldValues)) {
                addWarningsFormVersion3(ocrFieldValues, warnings);
            } else {
                addWarningsFormVersion1And2(ocrFieldValues, warnings);
            }
        }
        addWarningsAllFormVersion(ocrFieldValues, warnings);

    }

    private void addWarningsFormVersion1And2(Map<String, String> ocrFieldValues, List<String> warnings) {
        Stream.of(GORSolicitorMandatoryFields.values())
                .filter(GORSolicitorMandatoryFields -> GORSolicitorMandatoryFields.isVersion1()
                    || GORSolicitorMandatoryFields.isVersion2())
                .forEach(field -> {
                    if (!ocrFieldValues.containsKey(field.getKey())) {
                        log.warn(MANDATORY_FIELD_NOT_FOUND_LOG, field.getKey());
                        warnings.add(String.format(MANDATORY_FIELD_WARNING_STRING, field.getValue(), field.getKey()));
                    }
                });
    }


    private void addWarningsFormVersion3(Map<String, String> ocrFieldValues, List<String> warnings) {
        Stream.of(GORSolicitorMandatoryFields.values()).filter(GORSolicitorMandatoryFields::isVersion3)
                .forEach(field -> {
                    if (!ocrFieldValues.containsKey(field.getKey())) {
                        log.warn("v3 " + MANDATORY_FIELD_NOT_FOUND_LOG, field.getKey());
                        warnings.add(format(MANDATORY_FIELD_WARNING_STRING, field.getValue(), field.getKey()));
                    }
                });
    }

    private void addWarningsAllFormVersion(Map<String, String> ocrFieldValues, List<String> warnings) {
        if (ocrFieldValues.containsKey(DEPENDANT_KEY_PAPERPAYMENTMETHOD)
                && ocrFieldValues.get(DEPENDANT_KEY_PAPERPAYMENTMETHOD).equalsIgnoreCase("PBA")
                && StringUtils.isBlank(ocrFieldValues.get(DEPENDANT_KEY_SOLSFEEACCOUNTNUMBER))
        ) {
            log.warn(MANDATORY_FIELD_NOT_FOUND_LOG, DEPENDANT_KEY_SOLSFEEACCOUNTNUMBER);
            warnings.add(String.format(MANDATORY_FIELD_WARNING_STRING,
                    DEPENDANT_DESC_SOLSFEEACCOUNTNUMBER, DEPENDANT_KEY_SOLSFEEACCOUNTNUMBER));
        }
    }
}
