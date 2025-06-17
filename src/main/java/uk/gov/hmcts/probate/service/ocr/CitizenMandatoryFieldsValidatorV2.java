package uk.gov.hmcts.probate.service.ocr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.keyvalue.DefaultKeyValue;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.service.ExceptedEstateDateOfDeathChecker;

import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.BooleanUtils.toBoolean;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static uk.gov.hmcts.probate.model.ccd.ocr.GORCitizenMandatoryFields.IHT_UNUSED_ALLOWANCE;
import static uk.gov.hmcts.probate.model.ccd.ocr.GORCitizenMandatoryFields.IHT_ESTATE_GROSS;
import static uk.gov.hmcts.probate.model.ccd.ocr.GORCitizenMandatoryFields.IHT_ESTATE_NET;
import static uk.gov.hmcts.probate.model.ccd.ocr.GORCitizenMandatoryFields.IHT_ESTATE_NQV;
import static uk.gov.hmcts.probate.model.ccd.ocr.GORCitizenMandatoryFields.IHT_400421_COMPLETED;

@Slf4j
@Service
@RequiredArgsConstructor
public class CitizenMandatoryFieldsValidatorV2 {
    public static final DefaultKeyValue IHT_207_COMPLETED = new DefaultKeyValue("iht207completed",
            "Did you complete an IHT207 form?");
    public static final DefaultKeyValue IHT_205_COMPLETED_ONLINE = new DefaultKeyValue("iht205completedOnline",
            "Did you complete the IHT205 online with HMRC?");
    public static final DefaultKeyValue DIED_AFTER_SWITCH_DATE = new DefaultKeyValue("deceasedDiedOnAfterSwitchDate",
            "Did the deceased die on or after 1 January 2022?");
    private static final String FALSE = "false";

    private final ExceptedEstateDateOfDeathChecker exceptedEstateDateOfDeathChecker;
    private final MandatoryFieldsValidatorUtils mandatoryFieldsValidatorUtils;

    public void addWarnings(Map<String, String> ocrFieldValues, List<String> warnings) {
        if (FALSE.equalsIgnoreCase(ocrFieldValues.get(IHT_400421_COMPLETED.getKey()))) {
            mandatoryFieldsValidatorUtils.addWarningIfEmpty(ocrFieldValues, warnings, IHT_207_COMPLETED);
            if (FALSE.equalsIgnoreCase(ocrFieldValues.get(IHT_207_COMPLETED.getKey()))) {
                mandatoryFieldsValidatorUtils.addWarningIfEmpty(ocrFieldValues, warnings, DIED_AFTER_SWITCH_DATE);

                boolean deceasedDiedOnAfterSwitchDate = toBoolean(ocrFieldValues.get(DIED_AFTER_SWITCH_DATE.getKey()));

                if (isNotBlank(ocrFieldValues.get(DIED_AFTER_SWITCH_DATE.getKey()))
                        && deceasedDiedOnAfterSwitchDate != exceptedEstateDateOfDeathChecker
                        .isOnOrAfterSwitchDate(ocrFieldValues.get("deceasedDateOfDeath"))) {
                    mandatoryFieldsValidatorUtils.addWarning(
                            "Deceased date of death not consistent with the question: "
                                    + "Did the deceased die on or after 1 January "
                                    + "2022? (deceasedDiedOnAfterSwitchDate)", warnings);
                } else {
                    addWarningsForDeceasedDiedOnAfterSwitchDate(deceasedDiedOnAfterSwitchDate, ocrFieldValues,
                            warnings);
                }
            }
        }
    }

    private void addWarningsForDeceasedDiedOnAfterSwitchDate(boolean deceasedDiedOnAfterSwitchDate,
                                                             Map<String, String> ocrFieldValues,
                                                             List<String> warnings) {
        if (deceasedDiedOnAfterSwitchDate) {
            if (mandatoryFieldsValidatorUtils.hasLateSpouseCivilPartner(ocrFieldValues)
                    && mandatoryFieldsValidatorUtils.nqvBetweenThresholds(ocrFieldValues)) {
                mandatoryFieldsValidatorUtils.addWarningsForConditionalFields(ocrFieldValues, warnings,
                        IHT_UNUSED_ALLOWANCE);
            }
            mandatoryFieldsValidatorUtils.addWarningsForConditionalFields(ocrFieldValues, warnings,
                    IHT_ESTATE_GROSS, IHT_ESTATE_NET, IHT_ESTATE_NQV);
        } else if (isNotBlank(ocrFieldValues.get(DIED_AFTER_SWITCH_DATE.getKey()))) {
            mandatoryFieldsValidatorUtils.addWarningIfEmpty(ocrFieldValues, warnings,
                    IHT_205_COMPLETED_ONLINE);
        }
    }
}
