package uk.gov.hmcts.probate.service.ocr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.collections.keyvalue.DefaultKeyValue;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.ccd.ocr.GORCitizenMandatoryFields;
import uk.gov.hmcts.probate.model.ocr.OCRField;
import uk.gov.hmcts.probate.service.ExceptedEstateDateOfDeathChecker;
import static uk.gov.hmcts.probate.service.ocr.CitizenMandatoryFieldsValidatorV2.DIED_AFTER_SWITCH_DATE;
import static uk.gov.hmcts.probate.service.ocr.CitizenMandatoryFieldsValidatorV2.IHT_205_COMPLETED_ONLINE;
import static uk.gov.hmcts.probate.service.ocr.CitizenMandatoryFieldsValidatorV2.IHT_207_COMPLETED;

public class CitizenMandatoryFieldsValidatorV2Test {
    private OCRFieldTestUtils ocrFieldTestUtils = new OCRFieldTestUtils();
    private ArrayList<String> warnings;

    @Mock
    private ExceptedEstateDateOfDeathChecker exceptedEstateDateOfDeathChecker;

    @Mock
    private MandatoryFieldsValidatorUtils mandatoryFieldsValidatorUtils;

    @InjectMocks
    private CitizenMandatoryFieldsValidatorV2 citizenMandatoryFieldsValidatorV2;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        warnings = new ArrayList<>();
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate("01012022")).thenReturn(true);
    }
    
    @Test
    public void shouldCheckIht207CompletedIfIht400421completedFalse() {
        List<OCRField> ocrFields = new ArrayList<>();
        OCRField iht400421completed = OCRField.builder()
            .name("iht400421completed")
            .value("false")
            .description("IHT Completed online?").build();
        OCRField iht207completed = OCRField.builder()
            .name("iht207completed")
            .value("true")
            .description("IHT Completed").build();
        ocrFields.add(iht400421completed);
        ocrFields.add(iht207completed);
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
    public void shouldCheckDateOfDeathAgainstSwitchDate() {
        List<OCRField> ocrFields = new ArrayList<>();
        OCRField iht400421completed = OCRField.builder()
            .name("iht400421completed")
            .value("false")
            .description("IHT Completed online?").build();
        OCRField iht207completed = OCRField.builder()
            .name("iht207completed")
            .value("false")
            .description("IHT Completed").build();
        OCRField deceasedDiedOnAfterSwitchDate = OCRField.builder()
            .name("deceasedDiedOnAfterSwitchDate")
            .value("true")
            .description("deceasedDiedOnAfterSwitchDate").build();
        OCRField deceasedDateOfDeath = OCRField.builder()
            .name("deceasedDateOfDeath")
            .value("01012021")
            .description("deceasedDateOfDeath").build();
        ocrFields.add(iht400421completed);
        ocrFields.add(iht207completed);
        ocrFields.add(deceasedDiedOnAfterSwitchDate);
        ocrFields.add(deceasedDateOfDeath);
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
    public void shouldNotCheckDateOfDeathAgainstSwitchDate() {
        List<OCRField> ocrFields = new ArrayList<>();
        OCRField iht400421completed = OCRField.builder()
            .name("iht400421completed")
            .value("false")
            .description("IHT Completed online?").build();
        OCRField iht207completed = OCRField.builder()
            .name("iht207completed")
            .value("false")
            .description("IHT Completed").build();
        OCRField deceasedDateOfDeath = OCRField.builder()
            .name("deceasedDateOfDeath")
            .value("01012021")
            .description("deceasedDateOfDeath").build();
        ocrFields.add(iht400421completed);
        ocrFields.add(iht207completed);
        ocrFields.add(deceasedDateOfDeath);
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        citizenMandatoryFieldsValidatorV2.addWarnings(ocrFieldValues, warnings);
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(mandatoryFieldsValidatorUtils, times(0)).addWarning(any(), any());
    }

    @Test
    public void shouldCheckSwitchDateAndIHTEstateFields() {
        List<OCRField> ocrFields = new ArrayList<>();
        OCRField iht400421completed = OCRField.builder()
            .name("iht400421completed")
            .value("false")
            .description("IHT Completed online?").build();
        OCRField iht207completed = OCRField.builder()
            .name("iht207completed")
            .value("false")
            .description("IHT Completed").build();
        OCRField deceasedDiedOnAfterSwitchDate = OCRField.builder()
            .name("deceasedDiedOnAfterSwitchDate")
            .value("true")
            .description("deceasedDiedOnAfterSwitchDate").build();
        OCRField deceasedDateOfDeath = OCRField.builder()
            .name("deceasedDateOfDeath")
            .value("01012022")
            .description("deceasedDateOfDeath").build();
        ocrFields.add(iht400421completed);
        ocrFields.add(iht207completed);
        ocrFields.add(deceasedDiedOnAfterSwitchDate);
        ocrFields.add(deceasedDateOfDeath);
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
        assertEquals(4, citizenMandatoryFieldsArgumentCaptorAllValues.size());
        assertEquals(GORCitizenMandatoryFields.IHT_ESTATE_GROSS.getValue(),
            citizenMandatoryFieldsArgumentCaptorAllValues.get(0).getValue());
        assertEquals(GORCitizenMandatoryFields.IHT_ESTATE_NET.getValue(),
            citizenMandatoryFieldsArgumentCaptorAllValues.get(1).getValue());
        assertEquals(GORCitizenMandatoryFields.IHT_ESTATE_NQV.getValue(),
            citizenMandatoryFieldsArgumentCaptorAllValues.get(2).getValue());
        assertEquals(GORCitizenMandatoryFields.IHT_UNUSED_ALLOWANCE.getValue(),
            citizenMandatoryFieldsArgumentCaptorAllValues.get(3).getValue());
    }

    @Test
    public void shouldCheckSwitchDateAndIHT205CompletedOnline() {
        List<OCRField> ocrFields = new ArrayList<>();
        OCRField iht400421completed = OCRField.builder()
            .name("iht400421completed")
            .value("false")
            .description("IHT Completed online?").build();
        OCRField iht207completed = OCRField.builder()
            .name("iht207completed")
            .value("false")
            .description("IHT Completed").build();
        OCRField deceasedDiedOnAfterSwitchDate = OCRField.builder()
            .name("deceasedDiedOnAfterSwitchDate")
            .value("false")
            .description("deceasedDiedOnAfterSwitchDate").build();
        ocrFields.add(iht400421completed);
        ocrFields.add(iht207completed);
        ocrFields.add(deceasedDiedOnAfterSwitchDate);
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
    public void shouldNotCheckIHT205CompletedOnline() {
        List<OCRField> ocrFields = new ArrayList<>();
        OCRField iht400421completed = OCRField.builder()
            .name("iht400421completed")
            .value("false")
            .description("IHT Completed online?").build();
        OCRField iht207completed = OCRField.builder()
            .name("iht207completed")
            .value("false")
            .description("IHT Completed").build();
        ocrFields.add(iht400421completed);
        ocrFields.add(iht207completed);
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
    public void shouldCheckIHTCompletedOnline() {
        List<OCRField> ocrFields = new ArrayList<>();
        OCRField iht400421completed = OCRField.builder()
            .name("iht400421completed")
            .value("false")
            .description("IHT Completed online?").build();
        OCRField iht207completed = OCRField.builder()
            .name("iht207completed")
            .value("false")
            .description("IHT Completed").build();
        OCRField deceasedDiedOnAfterSwitchDate = OCRField.builder()
            .name("deceasedDiedOnAfterSwitchDate")
            .value("false")
            .description("deceasedDiedOnAfterSwitchDate").build();
        OCRField iht205completedOnline = OCRField.builder()
            .name("iht205completedOnline")
            .value("true")
            .description("Did you complete the IHT205 online with HMRC?").build();
        ocrFields.add(iht400421completed);
        ocrFields.add(iht207completed);
        ocrFields.add(deceasedDiedOnAfterSwitchDate);
        ocrFields.add(iht205completedOnline);
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
    public void shouldCheckIHTFormIdSetCorrectly() {
        List<OCRField> ocrFields = new ArrayList<>();
        OCRField iht400421completed = OCRField.builder()
            .name("iht400421completed")
            .value("false")
            .description("IHT Completed online?").build();
        OCRField iht207completed = OCRField.builder()
            .name("iht207completed")
            .value("false")
            .description("IHT Completed").build();
        OCRField deceasedDiedOnAfterSwitchDate = OCRField.builder()
            .name("deceasedDiedOnAfterSwitchDate")
            .value("false")
            .description("deceasedDiedOnAfterSwitchDate").build();
        OCRField iht205completedOnline = OCRField.builder()
            .name("iht205completedOnline")
            .value("false")
            .description("Did you complete the IHT205 online with HMRC?").build();
        OCRField ihtFormId = OCRField.builder()
            .name("ihtFormId")
            .value("IHT205")
            .description("ihtFormId").build();
        ocrFields.add(iht400421completed);
        ocrFields.add(iht207completed);
        ocrFields.add(deceasedDiedOnAfterSwitchDate);
        ocrFields.add(iht205completedOnline);
        ocrFields.add(ihtFormId);
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