package uk.gov.hmcts.probate.service.ocr;

import org.apache.commons.collections.keyvalue.DefaultKeyValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.bulkscan.type.OcrDataField;
import uk.gov.hmcts.probate.model.ccd.ocr.GORCitizenMandatoryFields;
import uk.gov.hmcts.probate.service.ExceptedEstateDateOfDeathChecker;
import uk.gov.hmcts.probate.validator.IhtEstateValidationRule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.service.ocr.CitizenMandatoryFieldsValidatorV2.DIED_AFTER_SWITCH_DATE;
import static uk.gov.hmcts.probate.service.ocr.CitizenMandatoryFieldsValidatorV2.IHT_205_COMPLETED_ONLINE;
import static uk.gov.hmcts.probate.service.ocr.CitizenMandatoryFieldsValidatorV2.IHT_207_COMPLETED;

class CitizenMandatoryFieldsValidatorV2Test {
    private OCRFieldTestUtils ocrFieldTestUtils = new OCRFieldTestUtils();
    private ArrayList<String> warnings;

    @Mock
    private ExceptedEstateDateOfDeathChecker exceptedEstateDateOfDeathChecker;
    @Mock
    private MandatoryFieldsValidatorUtils mandatoryFieldsValidatorUtils;
    @Mock
    private IhtEstateValidationRule ihtEstateValidationRule;

    @InjectMocks
    private CitizenMandatoryFieldsValidatorV2 citizenMandatoryFieldsValidatorV2;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        warnings = new ArrayList<>();
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate("01012022")).thenReturn(true);
    }

    @Test
    void shouldCheckIht207CompletedIfIht400421completedFalse() {
        List<OcrDataField> ocrFields = new ArrayList<>() {
            {
                add(new OcrDataField("iht400421completed", "false"));
                add(new OcrDataField("iht207completed", "true"));
            }
        };

        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        citizenMandatoryFieldsValidatorV2.addWarnings(ocrFieldValues, warnings);
        ArgumentCaptor<DefaultKeyValue> defaultKeyValueArgumentCaptor =
            ArgumentCaptor.forClass(DefaultKeyValue.class);
        verify(mandatoryFieldsValidatorUtils, times(1)).addWarningIfEmpty(any(), any(),
            defaultKeyValueArgumentCaptor.capture());
        List<DefaultKeyValue> defaultKeyValueArgumentCaptorValues = defaultKeyValueArgumentCaptor.getAllValues();
        assertEquals(1, defaultKeyValueArgumentCaptorValues.size());
        assertEquals(IHT_207_COMPLETED.getValue(), defaultKeyValueArgumentCaptorValues.get(0).getValue());
    }

    @Test
    void shouldCheckDateOfDeathAgainstSwitchDate() {
        List<OcrDataField> ocrFields = new ArrayList<>() {
            {
                add(new OcrDataField("iht400421completed", "false"));
                add(new OcrDataField("iht207completed", "false"));
                add(new OcrDataField("deceasedDiedOnAfterSwitchDate", "true"));
                add(new OcrDataField("deceasedDateOfDeath", "01012021"));
            }
        };

        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        citizenMandatoryFieldsValidatorV2.addWarnings(ocrFieldValues, warnings);
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(mandatoryFieldsValidatorUtils, times(1)).addWarning(argumentCaptor.capture(), any());
        List<String> argumentCaptorValues = argumentCaptor.getAllValues();
        assertEquals(1, argumentCaptorValues.size());
        assertEquals("Deceased date of death not consistent with the question: "
            + "Did the deceased die on or after 1 January 2022? (deceasedDiedOnAfterSwitchDate)",
            argumentCaptorValues.get(0));
    }

    @Test
    void shouldNotCheckDateOfDeathAgainstSwitchDate() {
        List<OcrDataField> ocrFields = new ArrayList<>() {
            {
                add(new OcrDataField("iht400421completed", "false"));
                add(new OcrDataField("iht207completed", "false"));
                add(new OcrDataField("deceasedDateOfDeath", "01012021"));
            }
        };
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        citizenMandatoryFieldsValidatorV2.addWarnings(ocrFieldValues, warnings);
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(mandatoryFieldsValidatorUtils, times(0)).addWarning(any(), any());
    }

    @Test
    void shouldCheckSwitchDateAndIHTEstateFields() {
        List<OcrDataField> ocrFields = new ArrayList<>() {
            {
                add(new OcrDataField("iht400421completed", "false"));
                add(new OcrDataField("iht207completed", "false"));
                add(new OcrDataField("deceasedDiedOnAfterSwitchDate", "true"));
                add(new OcrDataField("deceasedDateOfDeath", "01012022"));
            }
        };
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        citizenMandatoryFieldsValidatorV2.addWarnings(ocrFieldValues, warnings);
        ArgumentCaptor<DefaultKeyValue> defaultKeyValueArgumentCaptor =
            ArgumentCaptor.forClass(DefaultKeyValue.class);
        verify(mandatoryFieldsValidatorUtils, times(2)).addWarningIfEmpty(any(), any(),
            defaultKeyValueArgumentCaptor.capture());
        List<DefaultKeyValue> defaultKeyValueArgumentCaptorValues = defaultKeyValueArgumentCaptor.getAllValues();
        assertEquals(2, defaultKeyValueArgumentCaptorValues.size());
        assertEquals(IHT_207_COMPLETED.getValue(), defaultKeyValueArgumentCaptorValues.get(0).getValue());
        assertEquals(DIED_AFTER_SWITCH_DATE.getValue(), defaultKeyValueArgumentCaptorValues.get(1).getValue());

        ArgumentCaptor<GORCitizenMandatoryFields> gorCitizenMandatoryFieldsArgumentCaptor =
            ArgumentCaptor.forClass(GORCitizenMandatoryFields.class);
        verify(mandatoryFieldsValidatorUtils, times(1))
            .addWarningsForConditionalFields(any(), any(),
                gorCitizenMandatoryFieldsArgumentCaptor.capture());
        List<GORCitizenMandatoryFields> citizenMandatoryFieldsArgumentCaptorAllValues =
            gorCitizenMandatoryFieldsArgumentCaptor.getAllValues();
        assertEquals(3, citizenMandatoryFieldsArgumentCaptorAllValues.size());
        assertEquals(GORCitizenMandatoryFields.IHT_ESTATE_GROSS.getValue(),
            citizenMandatoryFieldsArgumentCaptorAllValues.get(0).getValue());
        assertEquals(GORCitizenMandatoryFields.IHT_ESTATE_NET.getValue(),
            citizenMandatoryFieldsArgumentCaptorAllValues.get(1).getValue());
        assertEquals(GORCitizenMandatoryFields.IHT_ESTATE_NQV.getValue(),
            citizenMandatoryFieldsArgumentCaptorAllValues.get(2).getValue());
    }

    @Test
    public void shouldCheckSwitchDateAndIHTEstateFieldsWithUnusedAllowanceForWidowedNvq() {
        List<OcrDataField> ocrFields = new ArrayList<>() {
            {
                add(new OcrDataField("iht400421completed", "false"));
                add(new OcrDataField("iht207completed", "false"));
                add(new OcrDataField("deceasedDiedOnAfterSwitchDate", "true"));
                add(new OcrDataField("deceasedDateOfDeath", "01012022"));
                add(new OcrDataField("deceasedMartialStatus", "widowed"));
                add(new OcrDataField("ihtEstateNetQualifyingValue", "50000000"));
            }
        };

        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        when(ihtEstateValidationRule.isNqvBetweenValues(any())).thenReturn(true);

        citizenMandatoryFieldsValidatorV2.addWarnings(ocrFieldValues, warnings);
        ArgumentCaptor<DefaultKeyValue> defaultKeyValueArgumentCaptor =
                ArgumentCaptor.forClass(DefaultKeyValue.class);
        verify(mandatoryFieldsValidatorUtils, times(2)).addWarningIfEmpty(any(), any(),
                defaultKeyValueArgumentCaptor.capture());
        List<DefaultKeyValue> defaultKeyValueArgumentCaptorValues = defaultKeyValueArgumentCaptor.getAllValues();
        assertEquals(2, defaultKeyValueArgumentCaptorValues.size());
        assertEquals(IHT_207_COMPLETED.getValue(), defaultKeyValueArgumentCaptorValues.get(0).getValue());
        assertEquals(DIED_AFTER_SWITCH_DATE.getValue(), defaultKeyValueArgumentCaptorValues.get(1).getValue());

        ArgumentCaptor<GORCitizenMandatoryFields> gorCitizenMandatoryFieldsArgumentCaptor =
                ArgumentCaptor.forClass(GORCitizenMandatoryFields.class);
        verify(mandatoryFieldsValidatorUtils, times(2))
                .addWarningsForConditionalFields(any(), any(),
                        gorCitizenMandatoryFieldsArgumentCaptor.capture());
        List<GORCitizenMandatoryFields> citizenMandatoryFieldsArgumentCaptorAllValues =
                gorCitizenMandatoryFieldsArgumentCaptor.getAllValues();
        assertEquals(4, citizenMandatoryFieldsArgumentCaptorAllValues.size());
        assertEquals(GORCitizenMandatoryFields.IHT_UNUSED_ALLOWANCE.getValue(),
                citizenMandatoryFieldsArgumentCaptorAllValues.get(0).getValue());
        assertEquals(GORCitizenMandatoryFields.IHT_ESTATE_GROSS.getValue(),
                citizenMandatoryFieldsArgumentCaptorAllValues.get(1).getValue());
        assertEquals(GORCitizenMandatoryFields.IHT_ESTATE_NET.getValue(),
                citizenMandatoryFieldsArgumentCaptorAllValues.get(2).getValue());
        assertEquals(GORCitizenMandatoryFields.IHT_ESTATE_NQV.getValue(),
                citizenMandatoryFieldsArgumentCaptorAllValues.get(3).getValue());
    }

    @Test
    public void shouldCheckSwitchDateAndIHTEstateFieldsWithNoUnusedAllowanceForWidowedNvq() {
        List<OcrDataField> ocrFields = new ArrayList<>() {
            {
                add(new OcrDataField("iht400421completed", "false"));
                add(new OcrDataField("iht207completed", "false"));
                add(new OcrDataField("deceasedDiedOnAfterSwitchDate", "true"));
                add(new OcrDataField("deceasedDateOfDeath", "01012022"));
                add(new OcrDataField("deceasedMartialStatus", "widowed"));
                add(new OcrDataField("ihtEstateNetQualifyingValue", "10000000"));
            }
        };

        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        when(ihtEstateValidationRule.isNqvBetweenValues(any())).thenReturn(false);

        citizenMandatoryFieldsValidatorV2.addWarnings(ocrFieldValues, warnings);
        ArgumentCaptor<DefaultKeyValue> defaultKeyValueArgumentCaptor =
                ArgumentCaptor.forClass(DefaultKeyValue.class);
        verify(mandatoryFieldsValidatorUtils, times(2)).addWarningIfEmpty(any(), any(),
                defaultKeyValueArgumentCaptor.capture());
        List<DefaultKeyValue> defaultKeyValueArgumentCaptorValues = defaultKeyValueArgumentCaptor.getAllValues();
        assertEquals(2, defaultKeyValueArgumentCaptorValues.size());
        assertEquals(IHT_207_COMPLETED.getValue(), defaultKeyValueArgumentCaptorValues.get(0).getValue());
        assertEquals(DIED_AFTER_SWITCH_DATE.getValue(), defaultKeyValueArgumentCaptorValues.get(1).getValue());

        ArgumentCaptor<GORCitizenMandatoryFields> gorCitizenMandatoryFieldsArgumentCaptor =
                ArgumentCaptor.forClass(GORCitizenMandatoryFields.class);
        verify(mandatoryFieldsValidatorUtils, times(1))
                .addWarningsForConditionalFields(any(), any(),
                        gorCitizenMandatoryFieldsArgumentCaptor.capture());
        List<GORCitizenMandatoryFields> citizenMandatoryFieldsArgumentCaptorAllValues =
                gorCitizenMandatoryFieldsArgumentCaptor.getAllValues();
        assertEquals(3, citizenMandatoryFieldsArgumentCaptorAllValues.size());
        assertEquals(GORCitizenMandatoryFields.IHT_ESTATE_GROSS.getValue(),
                citizenMandatoryFieldsArgumentCaptorAllValues.get(0).getValue());
        assertEquals(GORCitizenMandatoryFields.IHT_ESTATE_NET.getValue(),
                citizenMandatoryFieldsArgumentCaptorAllValues.get(1).getValue());
        assertEquals(GORCitizenMandatoryFields.IHT_ESTATE_NQV.getValue(),
                citizenMandatoryFieldsArgumentCaptorAllValues.get(2).getValue());
    }

    @Test
    public void shouldCheckSwitchDateAndIHTEstateFieldsWithUnusedAllowanceForNvq() {
        List<OcrDataField> ocrFields = new ArrayList<>() {
            {
                add(new OcrDataField("iht400421completed", "false"));
                add(new OcrDataField("iht207completed", "false"));
                add(new OcrDataField("deceasedDiedOnAfterSwitchDate", "true"));
                add(new OcrDataField("deceasedDateOfDeath", "01012022"));
                add(new OcrDataField("deceasedMartialStatus", "neverMarried"));
                add(new OcrDataField("ihtEstateNetQualifyingValue", "10000000"));
            }
        };

        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        when(ihtEstateValidationRule.isNqvBetweenValues(any())).thenReturn(true);
        citizenMandatoryFieldsValidatorV2.addWarnings(ocrFieldValues, warnings);
        ArgumentCaptor<DefaultKeyValue> defaultKeyValueArgumentCaptor =
                ArgumentCaptor.forClass(DefaultKeyValue.class);
        verify(mandatoryFieldsValidatorUtils, times(2)).addWarningIfEmpty(any(), any(),
                defaultKeyValueArgumentCaptor.capture());
        List<DefaultKeyValue> defaultKeyValueArgumentCaptorValues = defaultKeyValueArgumentCaptor.getAllValues();
        assertEquals(2, defaultKeyValueArgumentCaptorValues.size());
        assertEquals(IHT_207_COMPLETED.getValue(), defaultKeyValueArgumentCaptorValues.get(0).getValue());
        assertEquals(DIED_AFTER_SWITCH_DATE.getValue(), defaultKeyValueArgumentCaptorValues.get(1).getValue());

        ArgumentCaptor<GORCitizenMandatoryFields> gorCitizenMandatoryFieldsArgumentCaptor =
                ArgumentCaptor.forClass(GORCitizenMandatoryFields.class);
        verify(mandatoryFieldsValidatorUtils, times(1))
                .addWarningsForConditionalFields(any(), any(),
                        gorCitizenMandatoryFieldsArgumentCaptor.capture());
        List<GORCitizenMandatoryFields> citizenMandatoryFieldsArgumentCaptorAllValues =
                gorCitizenMandatoryFieldsArgumentCaptor.getAllValues();
        assertEquals(3, citizenMandatoryFieldsArgumentCaptorAllValues.size());
        assertEquals(GORCitizenMandatoryFields.IHT_ESTATE_GROSS.getValue(),
                citizenMandatoryFieldsArgumentCaptorAllValues.get(0).getValue());
        assertEquals(GORCitizenMandatoryFields.IHT_ESTATE_NET.getValue(),
                citizenMandatoryFieldsArgumentCaptorAllValues.get(1).getValue());
        assertEquals(GORCitizenMandatoryFields.IHT_ESTATE_NQV.getValue(),
                citizenMandatoryFieldsArgumentCaptorAllValues.get(2).getValue());
    }

    @Test
    public void shouldCheckSwitchDateAndIHTEstateFieldsWithUnusedAllowanceForNotWidowedNvq() {
        List<OcrDataField> ocrFields = new ArrayList<>() {
            {
                add(new OcrDataField("iht400421completed", "false"));
                add(new OcrDataField("iht207completed", "false"));
                add(new OcrDataField("deceasedDiedOnAfterSwitchDate", "true"));
                add(new OcrDataField("deceasedDateOfDeath", "01012022"));
                add(new OcrDataField("deceasedMartialStatus", "neverMarried"));
                add(new OcrDataField("ihtEstateNetQualifyingValue", "50000000"));
            }
        };

        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        when(ihtEstateValidationRule.isNqvBetweenValues(any())).thenReturn(true);
        citizenMandatoryFieldsValidatorV2.addWarnings(ocrFieldValues, warnings);
        ArgumentCaptor<DefaultKeyValue> defaultKeyValueArgumentCaptor =
                ArgumentCaptor.forClass(DefaultKeyValue.class);
        verify(mandatoryFieldsValidatorUtils, times(2)).addWarningIfEmpty(any(), any(),
                defaultKeyValueArgumentCaptor.capture());
        List<DefaultKeyValue> defaultKeyValueArgumentCaptorValues = defaultKeyValueArgumentCaptor.getAllValues();
        assertEquals(2, defaultKeyValueArgumentCaptorValues.size());
        assertEquals(IHT_207_COMPLETED.getValue(), defaultKeyValueArgumentCaptorValues.get(0).getValue());
        assertEquals(DIED_AFTER_SWITCH_DATE.getValue(), defaultKeyValueArgumentCaptorValues.get(1).getValue());

        ArgumentCaptor<GORCitizenMandatoryFields> gorCitizenMandatoryFieldsArgumentCaptor =
                ArgumentCaptor.forClass(GORCitizenMandatoryFields.class);
        verify(mandatoryFieldsValidatorUtils, times(1))
                .addWarningsForConditionalFields(any(), any(),
                        gorCitizenMandatoryFieldsArgumentCaptor.capture());
        List<GORCitizenMandatoryFields> citizenMandatoryFieldsArgumentCaptorAllValues =
                gorCitizenMandatoryFieldsArgumentCaptor.getAllValues();
        assertEquals(3, citizenMandatoryFieldsArgumentCaptorAllValues.size());
        assertEquals(GORCitizenMandatoryFields.IHT_ESTATE_GROSS.getValue(),
                citizenMandatoryFieldsArgumentCaptorAllValues.get(0).getValue());
        assertEquals(GORCitizenMandatoryFields.IHT_ESTATE_NET.getValue(),
                citizenMandatoryFieldsArgumentCaptorAllValues.get(1).getValue());
        assertEquals(GORCitizenMandatoryFields.IHT_ESTATE_NQV.getValue(),
                citizenMandatoryFieldsArgumentCaptorAllValues.get(2).getValue());
    }

    @Test
    void shouldCheckSwitchDateAndIHT205CompletedOnline() {
        List<OcrDataField> ocrFields = new ArrayList<>() {
            {
                add(new OcrDataField("iht400421completed", "false"));
                add(new OcrDataField("iht207completed", "false"));
                add(new OcrDataField("deceasedDiedOnAfterSwitchDate", "false"));
            }
        };

        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        citizenMandatoryFieldsValidatorV2.addWarnings(ocrFieldValues, warnings);
        ArgumentCaptor<DefaultKeyValue> defaultKeyValueArgumentCaptor =
            ArgumentCaptor.forClass(DefaultKeyValue.class);
        verify(mandatoryFieldsValidatorUtils, times(3)).addWarningIfEmpty(any(), any(),
            defaultKeyValueArgumentCaptor.capture());
        List<DefaultKeyValue> defaultKeyValueArgumentCaptorValues = defaultKeyValueArgumentCaptor.getAllValues();
        assertEquals(3, defaultKeyValueArgumentCaptorValues.size());
        assertEquals(IHT_207_COMPLETED.getValue(), defaultKeyValueArgumentCaptorValues.get(0).getValue());
        assertEquals(DIED_AFTER_SWITCH_DATE.getValue(), defaultKeyValueArgumentCaptorValues.get(1).getValue());
        assertEquals(IHT_205_COMPLETED_ONLINE.getValue(), defaultKeyValueArgumentCaptorValues.get(2).getValue());

        verify(mandatoryFieldsValidatorUtils, times(0))
            .addWarningsForConditionalFields(any(), any(), any());
    }

    @Test
    void shouldNotCheckIHT205CompletedOnline() {
        List<OcrDataField> ocrFields = new ArrayList<>() {
            {
                add(new OcrDataField("iht400421completed", "false"));
                add(new OcrDataField("iht207completed", "false"));
            }
        };
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        citizenMandatoryFieldsValidatorV2.addWarnings(ocrFieldValues, warnings);
        ArgumentCaptor<DefaultKeyValue> defaultKeyValueArgumentCaptor =
            ArgumentCaptor.forClass(DefaultKeyValue.class);
        verify(mandatoryFieldsValidatorUtils, times(2)).addWarningIfEmpty(any(), any(),
            defaultKeyValueArgumentCaptor.capture());
        List<DefaultKeyValue> defaultKeyValueArgumentCaptorValues = defaultKeyValueArgumentCaptor.getAllValues();
        assertEquals(2, defaultKeyValueArgumentCaptorValues.size());
        assertEquals(IHT_207_COMPLETED.getValue(), defaultKeyValueArgumentCaptorValues.get(0).getValue());
        assertEquals(DIED_AFTER_SWITCH_DATE.getValue(), defaultKeyValueArgumentCaptorValues.get(1).getValue());

        verify(mandatoryFieldsValidatorUtils, times(0))
            .addWarningsForConditionalFields(any(), any(), any());
    }

    @Test
    void shouldCheckIHTCompletedOnline() {
        List<OcrDataField> ocrFields = new ArrayList<>() {
            {
                add(new OcrDataField("iht400421completed", "false"));
                add(new OcrDataField("iht207completed", "false"));
                add(new OcrDataField("deceasedDiedOnAfterSwitchDate", "false"));
                add(new OcrDataField("iht205completedOnline", "true"));
            }
        };
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        citizenMandatoryFieldsValidatorV2.addWarnings(ocrFieldValues, warnings);
        ArgumentCaptor<DefaultKeyValue> defaultKeyValueArgumentCaptor =
            ArgumentCaptor.forClass(DefaultKeyValue.class);
        verify(mandatoryFieldsValidatorUtils, times(3)).addWarningIfEmpty(any(), any(),
            defaultKeyValueArgumentCaptor.capture());
        List<DefaultKeyValue> defaultKeyValueArgumentCaptorValues = defaultKeyValueArgumentCaptor.getAllValues();
        assertEquals(3, defaultKeyValueArgumentCaptorValues.size());
        assertEquals(IHT_207_COMPLETED.getValue(), defaultKeyValueArgumentCaptorValues.get(0).getValue());
        assertEquals(DIED_AFTER_SWITCH_DATE.getValue(), defaultKeyValueArgumentCaptorValues.get(1).getValue());
        assertEquals(IHT_205_COMPLETED_ONLINE.getValue(), defaultKeyValueArgumentCaptorValues.get(2).getValue());

    }

    @Test
    void shouldCheckIHTFormIdSetCorrectly() {
        List<OcrDataField> ocrFields = new ArrayList<>() {
            {
                add(new OcrDataField("iht400421completed", "false"));
                add(new OcrDataField("iht207completed", "false"));
                add(new OcrDataField("deceasedDiedOnAfterSwitchDate", "false"));
                add(new OcrDataField("iht205completedOnline", "false"));
                add(new OcrDataField("ihtFormId", "IHT205"));
            }
        };

        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        citizenMandatoryFieldsValidatorV2.addWarnings(ocrFieldValues, warnings);
        ArgumentCaptor<DefaultKeyValue> defaultKeyValueArgumentCaptor =
            ArgumentCaptor.forClass(DefaultKeyValue.class);
        verify(mandatoryFieldsValidatorUtils, times(3)).addWarningIfEmpty(any(), any(),
            defaultKeyValueArgumentCaptor.capture());
        List<DefaultKeyValue> defaultKeyValueArgumentCaptorValues = defaultKeyValueArgumentCaptor.getAllValues();
        assertEquals(3, defaultKeyValueArgumentCaptorValues.size());
        assertEquals(IHT_207_COMPLETED.getValue(), defaultKeyValueArgumentCaptorValues.get(0).getValue());
        assertEquals(DIED_AFTER_SWITCH_DATE.getValue(), defaultKeyValueArgumentCaptorValues.get(1).getValue());
        assertEquals(IHT_205_COMPLETED_ONLINE.getValue(), defaultKeyValueArgumentCaptorValues.get(2).getValue());

        verify(mandatoryFieldsValidatorUtils, times(0)).addWarning(any(), any());
    }
}
