package uk.gov.hmcts.probate.service.ocr.pa1p;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.ocr.GORCitizenMandatoryFields;
import uk.gov.hmcts.probate.service.ocr.CitizenMandatoryFieldsValidatorV2;
import uk.gov.hmcts.probate.service.ocr.MandatoryFieldsValidatorUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.lang.String.format;
import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.DEPENDANT_DESC_IHTFORMID;
import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.DEPENDANT_DESC_IHTREFERENCENUMBER;
import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.DEPENDANT_KEY_IHTFORMID;
import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.DEPENDANT_KEY_IHTREFERENCENUMBER;
import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.MANDATORY_FIELD_NOT_FOUND_LOG;
import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.MANDATORY_FIELD_WARNING_STRING;
import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.MANDATORY_KEY_FORM_VERSION;
import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.DEPENDANT_KEY_IHTFORMCOMPLETEDONLINE;
import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.DEPENDANT_DESC_IHTFORMCOMPLETEDONLINE;

@Slf4j
@Service
@RequiredArgsConstructor
public class PA1PCitizenMandatoryFieldsValidator {
    private final MandatoryFieldsValidatorUtils mandatoryFieldsValidatorUtils;
    private final CitizenMandatoryFieldsValidatorV2 citizenMandatoryFieldsValidatorV2;

    public void addWarnings(Map<String, String> ocrFieldValues, List<String> warnings) {
        if (!mandatoryFieldsValidatorUtils.addWarningForNoFormVersion(ocrFieldValues, warnings)) {
            if (mandatoryFieldsValidatorUtils.isVersion3(ocrFieldValues)) {
                addWarningsFormVersion3(ocrFieldValues, warnings);
            } else if (mandatoryFieldsValidatorUtils.isVersion2(ocrFieldValues)) {
                addWarningsFormVersion2(ocrFieldValues, warnings);
            } else {
                addWarningsFormVersion1(ocrFieldValues, warnings);
            }
        }
    }

    private void addWarningsFormVersion1(Map<String, String> ocrFieldValues, List<String> warnings) {
        Stream.of(GORCitizenMandatoryFields.values()).filter(GORCitizenMandatoryFields::isVersion1)
            .forEach(field -> {
                if (!ocrFieldValues.containsKey(field.getKey())) {
                    log.warn(MANDATORY_FIELD_NOT_FOUND_LOG, field.getKey());
                    warnings.add(format(MANDATORY_FIELD_WARNING_STRING, field.getValue(), field.getKey()));
                }
            });
        if (ocrFieldValues.containsKey(MANDATORY_KEY_FORM_VERSION)
                && "1".equals(ocrFieldValues.get(MANDATORY_KEY_FORM_VERSION))) {
            if (!ocrFieldValues.containsKey(DEPENDANT_KEY_IHTFORMCOMPLETEDONLINE)) {
                log.warn(MANDATORY_FIELD_NOT_FOUND_LOG, DEPENDANT_KEY_IHTFORMCOMPLETEDONLINE);
                warnings.add(String.format(MANDATORY_FIELD_WARNING_STRING,
                        DEPENDANT_DESC_IHTFORMCOMPLETEDONLINE, DEPENDANT_KEY_IHTFORMCOMPLETEDONLINE));
            } else {
                boolean formCompletedOnlineResult =
                        BooleanUtils.toBoolean(ocrFieldValues.get(DEPENDANT_KEY_IHTFORMCOMPLETEDONLINE));
                if (formCompletedOnlineResult && !ocrFieldValues.containsKey(DEPENDANT_KEY_IHTREFERENCENUMBER)) {
                    log.warn(MANDATORY_FIELD_NOT_FOUND_LOG, DEPENDANT_KEY_IHTREFERENCENUMBER);
                    warnings.add(String.format(MANDATORY_FIELD_WARNING_STRING,
                            DEPENDANT_DESC_IHTREFERENCENUMBER, DEPENDANT_KEY_IHTREFERENCENUMBER));
                } else if (!formCompletedOnlineResult && !ocrFieldValues.containsKey(DEPENDANT_KEY_IHTFORMID)) {
                    log.warn(MANDATORY_FIELD_NOT_FOUND_LOG, DEPENDANT_KEY_IHTFORMID);
                    warnings.add(
                            String.format(MANDATORY_FIELD_WARNING_STRING,
                                    DEPENDANT_DESC_IHTFORMID, DEPENDANT_KEY_IHTFORMID));
                }
            }
        }
    }

    private void addWarningsFormVersion2(Map<String, String> ocrFieldValues, List<String> warnings) {
        Stream.of(GORCitizenMandatoryFields.values()).filter(GORCitizenMandatoryFields::isVersion2)
            .forEach(field -> {
                if (!ocrFieldValues.containsKey(field.getKey())) {
                    log.warn("v2 " + MANDATORY_FIELD_NOT_FOUND_LOG, field.getKey());
                    warnings.add(format(MANDATORY_FIELD_WARNING_STRING, field.getValue(), field.getKey()));
                }
            });

        citizenMandatoryFieldsValidatorV2.addWarnings(ocrFieldValues, warnings);
    }

    private void addWarningsFormVersion3(Map<String, String> ocrFieldValues, List<String> warnings) {
        Stream.of(GORCitizenMandatoryFields.values()).filter(GORCitizenMandatoryFields::isVersion3)
                .forEach(field -> {
                    log.info("Checking v3 {} against ocr fields", field.getKey());
                    if (!ocrFieldValues.containsKey(field.getKey())) {
                        log.warn("v3 " + MANDATORY_FIELD_NOT_FOUND_LOG, field.getKey());
                        warnings.add(format(MANDATORY_FIELD_WARNING_STRING, field.getValue(), field.getKey()));
                    }
                });
    }
}
