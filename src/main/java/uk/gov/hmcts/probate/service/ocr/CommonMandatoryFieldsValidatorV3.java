package uk.gov.hmcts.probate.service.ocr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.keyvalue.DefaultKeyValue;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.ocr.GORCitizenMandatoryFields;
import uk.gov.hmcts.probate.service.ExceptedEstateDateOfDeathChecker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.BooleanUtils.toBoolean;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static uk.gov.hmcts.probate.model.ccd.ocr.GORCitizenMandatoryFields.IHT_207_GROSS_VALUE;
import static uk.gov.hmcts.probate.model.ccd.ocr.GORCitizenMandatoryFields.IHT_207_NET_VALUE;
import static uk.gov.hmcts.probate.model.ccd.ocr.GORCitizenMandatoryFields.IHT_421_GROSS_VALUE;
import static uk.gov.hmcts.probate.model.ccd.ocr.GORCitizenMandatoryFields.IHT_421_NET_VALUE;
import static uk.gov.hmcts.probate.model.ccd.ocr.GORCitizenMandatoryFields.IHT_CODE;
import static uk.gov.hmcts.probate.model.ccd.ocr.GORCitizenMandatoryFields.IHT_ESTATE_GROSS;
import static uk.gov.hmcts.probate.model.ccd.ocr.GORCitizenMandatoryFields.IHT_ESTATE_NET;
import static uk.gov.hmcts.probate.model.ccd.ocr.GORCitizenMandatoryFields.IHT_ESTATE_NQV;
import static uk.gov.hmcts.probate.model.ccd.ocr.GORCitizenMandatoryFields.IHT_GROSS_VALUE_EXCEPTED_ESTATE;
import static uk.gov.hmcts.probate.model.ccd.ocr.GORCitizenMandatoryFields.IHT_GROSS_VALUE_205;
import static uk.gov.hmcts.probate.model.ccd.ocr.GORCitizenMandatoryFields.IHT_NET_VALUE_EXCEPTED_ESTATE;
import static uk.gov.hmcts.probate.model.ccd.ocr.GORCitizenMandatoryFields.IHT_NET_VALUE_205;
import static uk.gov.hmcts.probate.model.ccd.ocr.GORCitizenMandatoryFields.IHT_UNUSED_ALLOWANCE;
import static uk.gov.hmcts.probate.model.ccd.ocr.GORCitizenMandatoryFields.PROBATE_GROSS_VALUE_IHT_400;
import static uk.gov.hmcts.probate.model.ccd.ocr.GORCitizenMandatoryFields.PROBATE_NET_VALUE_IHT_400;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommonMandatoryFieldsValidatorV3 {
    public static final DefaultKeyValue DIED_AFTER_SWITCH_DATE = new DefaultKeyValue("deceasedDiedOnAfterSwitchDate",
        "Did the deceased die on or after 1 January 2022?");
    private static final String TRUE = "true";
    public static final String DECEASED_DATE_OF_DEATH = "deceasedDateOfDeath";
    public static final String IHT_FORM_NOT_REQUIRED = "exceptedEstate";
    public static final String IHT400_COMPLETED = "iht400completed";
    public static final String IHT_400_PROCESS = "iht400process";
    public static final String IHT400421_COMPLETED = "iht400421completed";
    public static final String IHT207_COMPLETED = "iht207completed";
    public static final String IHT205_COMPLETED = "iht205completed";


    private final ExceptedEstateDateOfDeathChecker exceptedEstateDateOfDeathChecker;
    private final MandatoryFieldsValidatorUtils mandatoryFieldsValidatorUtils;

    public void addWarnings(Map<String, String> ocrFieldValues, List<String> warnings) {
        boolean deceasedDiedOnAfterSwitchDate = toBoolean(ocrFieldValues.get(DIED_AFTER_SWITCH_DATE.getKey()));

        if (isNotBlank(ocrFieldValues.get(DIED_AFTER_SWITCH_DATE.getKey()))
            && deceasedDiedOnAfterSwitchDate != exceptedEstateDateOfDeathChecker
            .isOnOrAfterSwitchDate(ocrFieldValues.get(DECEASED_DATE_OF_DEATH))) {
            mandatoryFieldsValidatorUtils.addWarning(
                "Deceased date of death not consistent with the question: "
                    + "Did the deceased die on or after 1 January 2022? (deceasedDiedOnAfterSwitchDate)",
                warnings);
        }
        if (deceasedDiedOnAfterSwitchDate) {
            addWarningsAfterSwitchDate(ocrFieldValues, warnings);
        } else {
            addWarningsBeforeSwitchDate(ocrFieldValues, warnings);
        }
    }

    private void addWarningsAfterSwitchDate(Map<String, String> ocrFieldValues, List<String> warnings) {
        addWarningsForSubmittedIHTForm(ocrFieldValues, warnings, true);


    }

    private void addWarningsBeforeSwitchDate(Map<String, String> ocrFieldValues, List<String> warnings) {
        addWarningsForSubmittedIHTForm(ocrFieldValues, warnings, false);

    }

    private void addWarningsForSubmittedIHTForm(Map<String, String> ocrFieldValues, List<String> warnings,
                                                boolean diedOnAfterSwitchDate) {
        List<String> submittedForm = new ArrayList<>();
        boolean iht421WarningsAdded = false;
        if (TRUE.equalsIgnoreCase(ocrFieldValues.get(IHT_FORM_NOT_REQUIRED))) {
            if (diedOnAfterSwitchDate) {
                addWarningsForIHTForms(IHT_FORM_NOT_REQUIRED, ocrFieldValues, warnings, iht421WarningsAdded);
            } else {
                mandatoryFieldsValidatorUtils.addWarning(
                        "Option \"I did not have to submit any forms to HMRC.\" (exceptedEstate) is not applicable"
                    + " to deceased died before 1 January 2022 (deceasedDateOfDeath)(deceasedDiedOnAfterSwitchDate)",
                        warnings);
            }
            submittedForm.add(IHT_FORM_NOT_REQUIRED);
        }
        if (TRUE.equalsIgnoreCase(ocrFieldValues.get(IHT400_COMPLETED))) {
            iht421WarningsAdded =
                    addWarningsForIHTForms(IHT400_COMPLETED, ocrFieldValues, warnings, iht421WarningsAdded);
            submittedForm.add(IHT400_COMPLETED);
        }
        if (TRUE.equalsIgnoreCase(ocrFieldValues.get(IHT400421_COMPLETED))) {
            iht421WarningsAdded =
                    addWarningsForIHTForms(IHT400421_COMPLETED, ocrFieldValues, warnings, iht421WarningsAdded);
            submittedForm.add(IHT400421_COMPLETED);
        }
        if (TRUE.equalsIgnoreCase(ocrFieldValues.get(IHT207_COMPLETED))) {
            addWarningsForIHTForms(IHT207_COMPLETED, ocrFieldValues, warnings, iht421WarningsAdded);
            submittedForm.add(IHT207_COMPLETED);
        }
        if (TRUE.equalsIgnoreCase(ocrFieldValues.get(IHT205_COMPLETED))) {
            if (!diedOnAfterSwitchDate) {
                addWarningsForIHTForms(IHT205_COMPLETED, ocrFieldValues, warnings, iht421WarningsAdded);
            } else {
                mandatoryFieldsValidatorUtils.addWarning(
                        "Option \"IHT205\" (iht205completed) is not applicable "
                    + "to deceased died on or after 1 January 2022 "
                    + "(deceasedDateOfDeath)(deceasedDiedOnAfterSwitchDate)",
                        warnings);
            }
            submittedForm.add(IHT205_COMPLETED);
        }
        if (submittedForm.size() != 1) {
            mandatoryFieldsValidatorUtils.addWarning("Applicant must submit one and only one iht form, "
                    + "submitted form:" + submittedForm, warnings);
        }
    }

    private boolean addWarningsForIHTForms(String ihtForm, Map<String, String> ocrFieldValues, List<String> warnings,
                                        boolean iht421WarningsAdded) {
        switch (ihtForm) {
            case IHT_FORM_NOT_REQUIRED:
                mandatoryFieldsValidatorUtils.addWarningsForConditionalFields(ocrFieldValues, warnings,
                        IHT_ESTATE_GROSS, IHT_ESTATE_NET, IHT_ESTATE_NQV, IHT_GROSS_VALUE_EXCEPTED_ESTATE,
                        IHT_NET_VALUE_EXCEPTED_ESTATE);
                if (mandatoryFieldsValidatorUtils.hasLateSpouseCivilPartner(ocrFieldValues)
                        && mandatoryFieldsValidatorUtils.nqvBetweenThresholds(ocrFieldValues)) {
                    mandatoryFieldsValidatorUtils.addWarningsForConditionalFields(ocrFieldValues, warnings,
                            IHT_UNUSED_ALLOWANCE);
                }
                break;
            case IHT400_COMPLETED:
                mandatoryFieldsValidatorUtils.addWarningsForConditionalFields(ocrFieldValues, warnings,
                        GORCitizenMandatoryFields.IHT_400_PROCESS);
                if (TRUE.equalsIgnoreCase(ocrFieldValues.get(IHT_400_PROCESS))) {
                    mandatoryFieldsValidatorUtils.addWarningsForConditionalFields(ocrFieldValues, warnings,
                            IHT_CODE, PROBATE_GROSS_VALUE_IHT_400, PROBATE_NET_VALUE_IHT_400);
                } else if (!iht421WarningsAdded) {
                    mandatoryFieldsValidatorUtils.addWarningsForConditionalFields(ocrFieldValues, warnings,
                            IHT_421_GROSS_VALUE, IHT_421_NET_VALUE);
                    iht421WarningsAdded = true;
                }
                break;
            case IHT400421_COMPLETED:
                if (!iht421WarningsAdded) {
                    mandatoryFieldsValidatorUtils.addWarningsForConditionalFields(ocrFieldValues, warnings,
                            IHT_421_GROSS_VALUE, IHT_421_NET_VALUE);
                    iht421WarningsAdded = true;
                }
                break;
            case IHT207_COMPLETED:
                mandatoryFieldsValidatorUtils.addWarningsForConditionalFields(ocrFieldValues, warnings,
                        IHT_207_GROSS_VALUE, IHT_207_NET_VALUE);
                break;
            case IHT205_COMPLETED:
                mandatoryFieldsValidatorUtils.addWarningsForConditionalFields(ocrFieldValues, warnings,
                        IHT_GROSS_VALUE_205, IHT_NET_VALUE_205);
                break;
            default:
        }
        return iht421WarningsAdded;
    }


}
