package uk.gov.hmcts.probate.service.ocr.pa1p;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.keyvalue.DefaultKeyValue;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.ocr.GORCitizenMandatoryFields;
import uk.gov.hmcts.probate.service.ocr.MandatoryFieldsValidatorUtils;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import static java.lang.String.format;
import static org.apache.commons.lang3.BooleanUtils.toBoolean;
import static uk.gov.hmcts.probate.model.ccd.ocr.GORCitizenMandatoryFields.DECEASED_LATE_SPOUSE;
import static uk.gov.hmcts.probate.model.ccd.ocr.GORCitizenMandatoryFields.IHT_400421_COMPLETED;
import static uk.gov.hmcts.probate.model.ccd.ocr.GORCitizenMandatoryFields.IHT_ESTATE_GROSS;
import static uk.gov.hmcts.probate.model.ccd.ocr.GORCitizenMandatoryFields.IHT_ESTATE_NET;
import static uk.gov.hmcts.probate.model.ccd.ocr.GORCitizenMandatoryFields.IHT_ESTATE_NQV;
import static uk.gov.hmcts.probate.model.ccd.ocr.GORCitizenMandatoryFields.IHT_IDENTIFIER;
import static uk.gov.hmcts.probate.model.ccd.ocr.GORCitizenMandatoryFields.IHT_UNUSED_ALLOWANCE;
import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.MANDATORY_FIELD_NOT_FOUND_LOG;
import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.MANDATORY_FIELD_WARNING_STIRNG;

@Slf4j
@Service
@RequiredArgsConstructor
public class PA1PCitizenMandatoryFieldsValidator {
    private final MandatoryFieldsValidatorUtils mandatoryFieldsValidatorUtils;

    private static final DefaultKeyValue IHT_207_COMPLETED = new DefaultKeyValue("iht207completed",
        "Did you complete an IHT400 and IHT421 form?");
    private static final DefaultKeyValue IHT_205_COMPLETED_ONLINE = new DefaultKeyValue("iht205completedOnline",
        "Did you complete the IHT205 online with HMRC?");
    private static final DefaultKeyValue DIED_AFTER_SWITCH_DATE = new DefaultKeyValue("deceasedDiedOnAfterSwitchDate",
        "Did the deceased die on or after 1 January 2022?");

    public void addWarnings(HashMap<String, String> ocrFieldValues, List<String> warnings) {
        if (mandatoryFieldsValidatorUtils.isVersion2(ocrFieldValues)) {
            addWarningsFormVersion2(ocrFieldValues, warnings);
        } else {
            addWarningsFormVersion1(ocrFieldValues, warnings);
        }

    }

    private void addWarningsFormVersion1(HashMap<String, String> ocrFieldValues, List<String> warnings) {
        Stream.of(GORCitizenMandatoryFields.values()).filter(field -> field.isVersion1()).forEach(field -> {
            log.info("Checking {} against ocr fields", field.getKey());
            if (!ocrFieldValues.containsKey(field.getKey())) {
                log.warn(MANDATORY_FIELD_NOT_FOUND_LOG, field.getKey());
                warnings.add(format(MANDATORY_FIELD_WARNING_STIRNG, field.getValue(), field.getKey()));
            }
        });
    }

    private void addWarningsFormVersion2(HashMap<String, String> ocrFieldValues, List<String> warnings) {
        Stream.of(GORCitizenMandatoryFields.values()).filter(field -> field.isVersion2()).forEach(field -> {
            log.info("Checking v2 {} against ocr fields", field.getKey());
            if (!ocrFieldValues.containsKey(field.getKey())) {
                log.warn("v2 " + MANDATORY_FIELD_NOT_FOUND_LOG, field.getKey());
                warnings.add(format(MANDATORY_FIELD_WARNING_STIRNG, field.getValue(), field.getKey()));
            }
        });

        boolean iht400421completed = toBoolean(ocrFieldValues.get(IHT_400421_COMPLETED.getKey()));

        if (!iht400421completed) {
            mandatoryFieldsValidatorUtils.addWarningIfEmpty(ocrFieldValues, warnings, IHT_207_COMPLETED);
        }

        boolean iht207completed = toBoolean(ocrFieldValues.get(IHT_207_COMPLETED.getKey()));
        if (!iht207completed) {
            mandatoryFieldsValidatorUtils.addWarningIfEmpty(ocrFieldValues, warnings, DIED_AFTER_SWITCH_DATE);
        }

        boolean deceasedDiedOnAfterSwitchDate = toBoolean(ocrFieldValues.get(DIED_AFTER_SWITCH_DATE.getKey()));
        if (deceasedDiedOnAfterSwitchDate) {
            mandatoryFieldsValidatorUtils.addWarningsForConditionalFields(ocrFieldValues, warnings, IHT_ESTATE_GROSS,
                IHT_ESTATE_NET, IHT_ESTATE_NQV, IHT_UNUSED_ALLOWANCE, DECEASED_LATE_SPOUSE);
        } else {
            mandatoryFieldsValidatorUtils.addWarningIfEmpty(ocrFieldValues, warnings, IHT_205_COMPLETED_ONLINE);
        }

        boolean iht205completedOnline = toBoolean(ocrFieldValues.get(IHT_205_COMPLETED_ONLINE.getKey()));
        if (iht205completedOnline) {
            mandatoryFieldsValidatorUtils.addWarningsForConditionalFields(ocrFieldValues, warnings, IHT_IDENTIFIER);
        }
    }

}
