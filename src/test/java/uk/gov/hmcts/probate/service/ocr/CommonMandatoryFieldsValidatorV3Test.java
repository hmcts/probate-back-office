package uk.gov.hmcts.probate.service.ocr;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.ccd.ocr.GORCitizenMandatoryFields;
import uk.gov.hmcts.probate.model.ocr.OCRField;
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

class CommonMandatoryFieldsValidatorV3Test {
    private OCRFieldTestUtils ocrFieldTestUtils = new OCRFieldTestUtils();
    private ArrayList<String> warnings;

    @Mock
    private ExceptedEstateDateOfDeathChecker exceptedEstateDateOfDeathChecker;
    @Mock
    private MandatoryFieldsValidatorUtils mandatoryFieldsValidatorUtils;
    @Mock
    private IhtEstateValidationRule ihtEstateValidationRule;

    @InjectMocks
    private CommonMandatoryFieldsValidatorV3 commonMandatoryFieldsValidatorV3;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        warnings = new ArrayList<>();
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate("01012022")).thenReturn(true);
    }

    @Test
    void shouldCheckDateOfDeathAgainstSwitchDateAndOneFormSubmitted() {
        List<OCRField> ocrFields = new ArrayList<>();
        OCRField deceasedDiedOnAfterSwitchDate = OCRField.builder()
                .name("deceasedDiedOnAfterSwitchDate")
                .value("true")
                .description("deceasedDiedOnAfterSwitchDate").build();
        OCRField deceasedDateOfDeath = OCRField.builder()
                .name("deceasedDateOfDeath")
                .value("01012021")
                .description("deceasedDateOfDeath").build();
        ocrFields.add(deceasedDiedOnAfterSwitchDate);
        ocrFields.add(deceasedDateOfDeath);
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        commonMandatoryFieldsValidatorV3.addWarnings(ocrFieldValues, warnings);
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(mandatoryFieldsValidatorUtils, times(2)).addWarning(argumentCaptor.capture(), any());
        List<String> argumentCaptorValues = argumentCaptor.getAllValues();
        assertEquals(2, argumentCaptorValues.size());
        assertEquals("Deceased date of death not consistent with the question: "
                        + "Did the deceased die on or after 1 January 2022? (deceasedDiedOnAfterSwitchDate)",
                argumentCaptorValues.get(0));
        assertEquals("Applicant must submit one and only one iht form, "
                        + "submitted form:[]",
                argumentCaptorValues.get(1));
    }

    @Test
    void shouldCheckBeforeSwitchDateIHT400Completed() {
        List<OCRField> ocrFields = new ArrayList<>();
        OCRField deceasedDiedOnAfterSwitchDate = OCRField.builder()
                .name("deceasedDiedOnAfterSwitchDate")
                .value("false")
                .description("deceasedDiedOnAfterSwitchDate").build();
        OCRField deceasedDateOfDeath = OCRField.builder()
                .name("deceasedDateOfDeath")
                .value("01012021")
                .description("deceasedDateOfDeath").build();
        OCRField iht400 = OCRField.builder()
                .name("iht400completed")
                .value("true")
                .description("iht400completed").build();
        OCRField iht400Process = OCRField.builder()
                .name("iht400process")
                .value("true")
                .description("iht400process").build();
        OCRField ihtCode = OCRField.builder()
                .name("ihtcode")
                .value("true")
                .description("ihtcode").build();
        OCRField iht400Gross = OCRField.builder()
                .name("probategrossvalueiht400")
                .value("100000")
                .description("probategrossvalueiht400").build();
        OCRField iht400Net = OCRField.builder()
                .name("probatenetvalueiht400")
                .value("90000")
                .description("probatenetvalueiht400").build();
        ocrFields.add(deceasedDiedOnAfterSwitchDate);
        ocrFields.add(deceasedDateOfDeath);
        ocrFields.add(iht400);
        ocrFields.add(iht400Process);
        ocrFields.add(ihtCode);
        ocrFields.add(iht400Gross);
        ocrFields.add(iht400Net);
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        commonMandatoryFieldsValidatorV3.addWarnings(ocrFieldValues, warnings);

        ArgumentCaptor<GORCitizenMandatoryFields[]> gorCitizenMandatoryFieldsArgumentCaptor =
                ArgumentCaptor.forClass(GORCitizenMandatoryFields[].class);
        verify(mandatoryFieldsValidatorUtils, times(2))
                .addWarningsForConditionalFields(any(), any(),
                        gorCitizenMandatoryFieldsArgumentCaptor.capture());
        List<GORCitizenMandatoryFields[]> capturedArgs = gorCitizenMandatoryFieldsArgumentCaptor.getAllValues();
        assertEquals(2, capturedArgs.size());
        assertEquals(GORCitizenMandatoryFields.IHT_400_PROCESS.getValue(), capturedArgs.get(0)[0].getValue());
        assertEquals(GORCitizenMandatoryFields.IHT_CODE.getValue(), capturedArgs.get(1)[0].getValue());
        assertEquals(GORCitizenMandatoryFields.PROBATE_GROSS_VALUE_IHT_400.getValue(),
                capturedArgs.get(1)[1].getValue());
        assertEquals(GORCitizenMandatoryFields.PROBATE_NET_VALUE_IHT_400.getValue(),
                capturedArgs.get(1)[2].getValue());
    }

    @Test
    void shouldCheckBeforeSwitchDateIHT400hmrcLetterNotReceived() {
        List<OCRField> ocrFields = new ArrayList<>();
        OCRField deceasedDiedOnAfterSwitchDate = OCRField.builder()
                .name("deceasedDiedOnAfterSwitchDate")
                .value("false")
                .description("deceasedDiedOnAfterSwitchDate").build();
        OCRField deceasedDateOfDeath = OCRField.builder()
                .name("deceasedDateOfDeath")
                .value("01012021")
                .description("deceasedDateOfDeath").build();
        OCRField iht400 = OCRField.builder()
                .name("iht400completed")
                .value("true")
                .description("iht400completed").build();
        OCRField iht400Process = OCRField.builder()
                .name("iht400process")
                .value("false")
                .description("iht400process").build();
        OCRField iht421GrossValue = OCRField.builder()
                .name("iht421GrossValue")
                .value("100000")
                .description("iht421GrossValue").build();
        OCRField iht421NetValue = OCRField.builder()
                .name("iht421NetValue")
                .value("90000")
                .description("iht421NetValue").build();
        ocrFields.add(deceasedDiedOnAfterSwitchDate);
        ocrFields.add(deceasedDateOfDeath);
        ocrFields.add(iht400);
        ocrFields.add(iht400Process);
        ocrFields.add(iht421GrossValue);
        ocrFields.add(iht421NetValue);
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        commonMandatoryFieldsValidatorV3.addWarnings(ocrFieldValues, warnings);

        ArgumentCaptor<GORCitizenMandatoryFields[]> gorCitizenMandatoryFieldsArgumentCaptor =
                ArgumentCaptor.forClass(GORCitizenMandatoryFields[].class);
        verify(mandatoryFieldsValidatorUtils, times(2))
                .addWarningsForConditionalFields(any(), any(),
                        gorCitizenMandatoryFieldsArgumentCaptor.capture());
        List<GORCitizenMandatoryFields[]> capturedArgs = gorCitizenMandatoryFieldsArgumentCaptor.getAllValues();
        assertEquals(2, capturedArgs.size());
        assertEquals(GORCitizenMandatoryFields.IHT_400_PROCESS.getValue(), capturedArgs.get(0)[0].getValue());
        assertEquals(GORCitizenMandatoryFields.IHT_421_GROSS_VALUE.getValue(), capturedArgs.get(1)[0].getValue());
        assertEquals(GORCitizenMandatoryFields.IHT_421_NET_VALUE.getValue(), capturedArgs.get(1)[1].getValue());
    }

    @Test
    void shouldCheckBeforeSwitchDateIHT400421() {
        List<OCRField> ocrFields = new ArrayList<>();
        OCRField deceasedDiedOnAfterSwitchDate = OCRField.builder()
                .name("deceasedDiedOnAfterSwitchDate")
                .value("false")
                .description("deceasedDiedOnAfterSwitchDate").build();
        OCRField deceasedDateOfDeath = OCRField.builder()
                .name("deceasedDateOfDeath")
                .value("01012021")
                .description("deceasedDateOfDeath").build();
        OCRField iht400421 = OCRField.builder()
                .name("iht400421completed")
                .value("true")
                .description("iht400421completed").build();
        OCRField iht421GrossValue = OCRField.builder()
                .name("iht421GrossValue")
                .value("100000")
                .description("iht421GrossValue").build();
        OCRField iht421NetValue = OCRField.builder()
                .name("iht421NetValue")
                .value("90000")
                .description("iht421NetValue").build();
        ocrFields.add(deceasedDiedOnAfterSwitchDate);
        ocrFields.add(deceasedDateOfDeath);
        ocrFields.add(iht400421);
        ocrFields.add(iht421GrossValue);
        ocrFields.add(iht421NetValue);
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        commonMandatoryFieldsValidatorV3.addWarnings(ocrFieldValues, warnings);

        ArgumentCaptor<GORCitizenMandatoryFields[]> gorCitizenMandatoryFieldsArgumentCaptor =
                ArgumentCaptor.forClass(GORCitizenMandatoryFields[].class);
        verify(mandatoryFieldsValidatorUtils, times(1))
                .addWarningsForConditionalFields(any(), any(),
                        gorCitizenMandatoryFieldsArgumentCaptor.capture());
        GORCitizenMandatoryFields[] capturedArgs = gorCitizenMandatoryFieldsArgumentCaptor.getValue();
        assertEquals(2, capturedArgs.length);
        assertEquals(GORCitizenMandatoryFields.IHT_421_GROSS_VALUE, capturedArgs[0]);
        assertEquals(GORCitizenMandatoryFields.IHT_421_NET_VALUE, capturedArgs[1]);
    }

    @Test
    void shouldCheckBeforeSwitchDateIHT205() {
        List<OCRField> ocrFields = new ArrayList<>();
        OCRField deceasedDiedOnAfterSwitchDate = OCRField.builder()
                .name("deceasedDiedOnAfterSwitchDate")
                .value("false")
                .description("deceasedDiedOnAfterSwitchDate").build();
        OCRField deceasedDateOfDeath = OCRField.builder()
                .name("deceasedDateOfDeath")
                .value("01012021")
                .description("deceasedDateOfDeath").build();
        OCRField iht205 = OCRField.builder()
                .name("iht205completed")
                .value("true")
                .description("iht205completed").build();
        OCRField iht205GrossValue = OCRField.builder()
                .name("ihtGrossValue205")
                .value("100000")
                .description("ihtGrossValue205").build();
        OCRField iht205NetValue = OCRField.builder()
                .name("ihtNetValue205")
                .value("90000")
                .description("ihtNetValue205").build();
        ocrFields.add(deceasedDiedOnAfterSwitchDate);
        ocrFields.add(deceasedDateOfDeath);
        ocrFields.add(iht205);
        ocrFields.add(iht205GrossValue);
        ocrFields.add(iht205NetValue);
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        commonMandatoryFieldsValidatorV3.addWarnings(ocrFieldValues, warnings);

        ArgumentCaptor<GORCitizenMandatoryFields[]> gorCitizenMandatoryFieldsArgumentCaptor =
                ArgumentCaptor.forClass(GORCitizenMandatoryFields[].class);
        verify(mandatoryFieldsValidatorUtils, times(1))
                .addWarningsForConditionalFields(any(), any(),
                        gorCitizenMandatoryFieldsArgumentCaptor.capture());
        GORCitizenMandatoryFields[] capturedArgs = gorCitizenMandatoryFieldsArgumentCaptor.getValue();
        assertEquals(2, capturedArgs.length);
        assertEquals(GORCitizenMandatoryFields.IHT_GROSS_VALUE_205, capturedArgs[0]);
        assertEquals(GORCitizenMandatoryFields.IHT_NET_VALUE_205, capturedArgs[1]);
    }

    @Test
    void shouldCheckBeforeSwitchDateIHT207() {
        List<OCRField> ocrFields = new ArrayList<>();
        OCRField deceasedDiedOnAfterSwitchDate = OCRField.builder()
                .name("deceasedDiedOnAfterSwitchDate")
                .value("false")
                .description("deceasedDiedOnAfterSwitchDate").build();
        OCRField deceasedDateOfDeath = OCRField.builder()
                .name("deceasedDateOfDeath")
                .value("01012021")
                .description("deceasedDateOfDeath").build();
        OCRField iht207 = OCRField.builder()
                .name("iht207completed")
                .value("true")
                .description("iht207completed").build();
        OCRField iht207GrossValue = OCRField.builder()
                .name("iht207GrossValue")
                .value("100000")
                .description("iht207GrossValue").build();
        OCRField iht207NetValue = OCRField.builder()
                .name("iht207NetValue")
                .value("90000")
                .description("iht207NetValue").build();
        ocrFields.add(deceasedDiedOnAfterSwitchDate);
        ocrFields.add(deceasedDateOfDeath);
        ocrFields.add(iht207);
        ocrFields.add(iht207GrossValue);
        ocrFields.add(iht207NetValue);
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        commonMandatoryFieldsValidatorV3.addWarnings(ocrFieldValues, warnings);

        ArgumentCaptor<GORCitizenMandatoryFields[]> gorCitizenMandatoryFieldsArgumentCaptor =
                ArgumentCaptor.forClass(GORCitizenMandatoryFields[].class);
        verify(mandatoryFieldsValidatorUtils, times(1))
                .addWarningsForConditionalFields(any(), any(),
                        gorCitizenMandatoryFieldsArgumentCaptor.capture());
        GORCitizenMandatoryFields[] capturedArgs = gorCitizenMandatoryFieldsArgumentCaptor.getValue();
        assertEquals(2, capturedArgs.length);
        assertEquals(GORCitizenMandatoryFields.IHT_207_GROSS_VALUE, capturedArgs[0]);
        assertEquals(GORCitizenMandatoryFields.IHT_207_NET_VALUE, capturedArgs[1]);
    }

    @Test
    void shouldCheckAfterSwitchDateExceptedEstateWithUnusedAllowanceForWidowedNvq() {
        List<OCRField> ocrFields = new ArrayList<>();
        OCRField deceasedDiedOnAfterSwitchDate = OCRField.builder()
                .name("deceasedDiedOnAfterSwitchDate")
                .value("true")
                .description("deceasedDiedOnAfterSwitchDate").build();
        OCRField deceasedDateOfDeath = OCRField.builder()
                .name("deceasedDateOfDeath")
                .value("01012022")
                .description("deceasedDateOfDeath").build();
        OCRField exceptedEstate = OCRField.builder()
                .name("exceptedEstate")
                .value("true")
                .description("exceptedEstate").build();
        OCRField deceasedMaritalStatus = OCRField.builder()
                .name("deceasedMartialStatus")
                .value("widowed")
                .description("deceasedMartialStatus").build();
        OCRField ihtEstateNetQualifyingValue = OCRField.builder()
                .name("ihtEstateNetQualifyingValue")
                .value("50000000")
                .description("ihtEstateNetQualifyingValue").build();
        ocrFields.add(deceasedDiedOnAfterSwitchDate);
        ocrFields.add(deceasedDateOfDeath);
        ocrFields.add(exceptedEstate);
        ocrFields.add(deceasedMaritalStatus);
        ocrFields.add(ihtEstateNetQualifyingValue);
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        when(mandatoryFieldsValidatorUtils.nqvBetweenThresholds(any())).thenReturn(true);
        when(mandatoryFieldsValidatorUtils.hasLateSpouseCivilPartner(any())).thenReturn(true);
        commonMandatoryFieldsValidatorV3.addWarnings(ocrFieldValues, warnings);

        ArgumentCaptor<GORCitizenMandatoryFields[]> gorCitizenMandatoryFieldsArgumentCaptor =
                ArgumentCaptor.forClass(GORCitizenMandatoryFields[].class);
        verify(mandatoryFieldsValidatorUtils, times(2))
                .addWarningsForConditionalFields(any(), any(),
                        gorCitizenMandatoryFieldsArgumentCaptor.capture());
        List<GORCitizenMandatoryFields[]> capturedArgs = gorCitizenMandatoryFieldsArgumentCaptor.getAllValues();
        assertEquals(2, capturedArgs.size());
        assertEquals(GORCitizenMandatoryFields.IHT_ESTATE_GROSS.getValue(), capturedArgs.get(0)[0].getValue());
        assertEquals(GORCitizenMandatoryFields.IHT_ESTATE_NET.getValue(), capturedArgs.get(0)[1].getValue());
        assertEquals(GORCitizenMandatoryFields.IHT_ESTATE_NQV.getValue(), capturedArgs.get(0)[2].getValue());
        assertEquals(GORCitizenMandatoryFields.IHT_GROSS_VALUE_EXCEPTED_ESTATE.getValue(),
                capturedArgs.get(0)[3].getValue());
        assertEquals(GORCitizenMandatoryFields.IHT_NET_VALUE_EXCEPTED_ESTATE.getValue(),
                capturedArgs.get(0)[4].getValue());
        assertEquals(GORCitizenMandatoryFields.IHT_UNUSED_ALLOWANCE.getValue(), capturedArgs.get(1)[0].getValue());
    }


    @Test
    void shouldCheckAfterSwitchDateExceptedEstateWithNoUnusedAllowanceForWidowedNvq() {
        List<OCRField> ocrFields = new ArrayList<>();
        OCRField deceasedDiedOnAfterSwitchDate = OCRField.builder()
                .name("deceasedDiedOnAfterSwitchDate")
                .value("true")
                .description("deceasedDiedOnAfterSwitchDate").build();
        OCRField deceasedDateOfDeath = OCRField.builder()
                .name("deceasedDateOfDeath")
                .value("01012022")
                .description("deceasedDateOfDeath").build();
        OCRField exceptedEstate = OCRField.builder()
                .name("exceptedEstate")
                .value("true")
                .description("exceptedEstate").build();
        OCRField deceasedMaritalStatus = OCRField.builder()
                .name("deceasedMartialStatus")
                .value("widowed")
                .description("deceasedMartialStatus").build();
        OCRField ihtEstateNetQualifyingValue = OCRField.builder()
                .name("ihtEstateNetQualifyingValue")
                .value("50000000")
                .description("ihtEstateNetQualifyingValue").build();
        ocrFields.add(deceasedDiedOnAfterSwitchDate);
        ocrFields.add(deceasedDateOfDeath);
        ocrFields.add(exceptedEstate);
        ocrFields.add(deceasedMaritalStatus);
        ocrFields.add(ihtEstateNetQualifyingValue);
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        when(ihtEstateValidationRule.isNqvBetweenValues(any())).thenReturn(false);
        commonMandatoryFieldsValidatorV3.addWarnings(ocrFieldValues, warnings);

        ArgumentCaptor<GORCitizenMandatoryFields[]> gorCitizenMandatoryFieldsArgumentCaptor =
                ArgumentCaptor.forClass(GORCitizenMandatoryFields[].class);
        verify(mandatoryFieldsValidatorUtils, times(1))
                .addWarningsForConditionalFields(any(), any(),
                        gorCitizenMandatoryFieldsArgumentCaptor.capture());
        GORCitizenMandatoryFields[] capturedArgs = gorCitizenMandatoryFieldsArgumentCaptor.getValue();
        assertEquals(5, capturedArgs.length);
        assertEquals(GORCitizenMandatoryFields.IHT_ESTATE_GROSS, capturedArgs[0]);
        assertEquals(GORCitizenMandatoryFields.IHT_ESTATE_NET, capturedArgs[1]);
        assertEquals(GORCitizenMandatoryFields.IHT_ESTATE_NQV, capturedArgs[2]);
        assertEquals(GORCitizenMandatoryFields.IHT_GROSS_VALUE_EXCEPTED_ESTATE, capturedArgs[3]);
        assertEquals(GORCitizenMandatoryFields.IHT_NET_VALUE_EXCEPTED_ESTATE, capturedArgs[4]);
    }

    @Test
    void shouldCheckBeforeSwitchDateExceptedEstate() {
        List<OCRField> ocrFields = new ArrayList<>();
        OCRField deceasedDiedOnAfterSwitchDate = OCRField.builder()
                .name("deceasedDiedOnAfterSwitchDate")
                .value("false")
                .description("deceasedDiedOnAfterSwitchDate").build();
        OCRField deceasedDateOfDeath = OCRField.builder()
                .name("deceasedDateOfDeath")
                .value("01012021")
                .description("deceasedDateOfDeath").build();
        OCRField exceptedEstate = OCRField.builder()
                .name("exceptedEstate")
                .value("true")
                .description("exceptedEstate").build();
        ocrFields.add(deceasedDiedOnAfterSwitchDate);
        ocrFields.add(deceasedDateOfDeath);
        ocrFields.add(exceptedEstate);
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        commonMandatoryFieldsValidatorV3.addWarnings(ocrFieldValues, warnings);

        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(mandatoryFieldsValidatorUtils, times(1)).addWarning(argumentCaptor.capture(), any());
        List<String> argumentCaptorValues = argumentCaptor.getAllValues();
        assertEquals(1, argumentCaptorValues.size());
        assertEquals("Option \"I did not have to submit any forms to HMRC.\" (exceptedEstate) is not applicable"
                    + " to deceased died before 1 January 2022 (deceasedDateOfDeath)(deceasedDiedOnAfterSwitchDate)",
                argumentCaptorValues.get(0));
    }

    @Test
    void shouldCheckAfterSwitchDateIht205() {
        List<OCRField> ocrFields = new ArrayList<>();
        OCRField deceasedDiedOnAfterSwitchDate = OCRField.builder()
                .name("deceasedDiedOnAfterSwitchDate")
                .value("true")
                .description("deceasedDiedOnAfterSwitchDate").build();
        OCRField deceasedDateOfDeath = OCRField.builder()
                .name("deceasedDateOfDeath")
                .value("01012022")
                .description("deceasedDateOfDeath").build();
        OCRField iht205completed = OCRField.builder()
                .name("iht205completed")
                .value("true")
                .description("iht205completed").build();
        ocrFields.add(deceasedDiedOnAfterSwitchDate);
        ocrFields.add(deceasedDateOfDeath);
        ocrFields.add(iht205completed);
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        commonMandatoryFieldsValidatorV3.addWarnings(ocrFieldValues, warnings);

        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(mandatoryFieldsValidatorUtils, times(1)).addWarning(argumentCaptor.capture(), any());
        List<String> argumentCaptorValues = argumentCaptor.getAllValues();
        assertEquals(1, argumentCaptorValues.size());
        assertEquals("Option \"IHT205\" (iht205completed) is not applicable"
                + " to deceased died on or after 1 January 2022 (deceasedDateOfDeath)(deceasedDiedOnAfterSwitchDate)",
                argumentCaptorValues.get(0));
    }

    @Test
    void shouldCheckAfterSwitchDateIHT400Completed() {
        List<OCRField> ocrFields = new ArrayList<>();
        OCRField deceasedDiedOnAfterSwitchDate = OCRField.builder()
                .name("deceasedDiedOnAfterSwitchDate")
                .value("true")
                .description("deceasedDiedOnAfterSwitchDate").build();
        OCRField deceasedDateOfDeath = OCRField.builder()
                .name("deceasedDateOfDeath")
                .value("01012021")
                .description("deceasedDateOfDeath").build();
        OCRField iht400 = OCRField.builder()
                .name("iht400completed")
                .value("true")
                .description("iht400completed").build();
        OCRField iht400Process = OCRField.builder()
                .name("iht400process")
                .value("true")
                .description("iht400process").build();
        OCRField ihtCode = OCRField.builder()
                .name("ihtcode")
                .value("true")
                .description("ihtcode").build();
        ocrFields.add(deceasedDiedOnAfterSwitchDate);
        ocrFields.add(deceasedDateOfDeath);
        ocrFields.add(iht400);
        ocrFields.add(iht400Process);
        ocrFields.add(ihtCode);
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        commonMandatoryFieldsValidatorV3.addWarnings(ocrFieldValues, warnings);
        ArgumentCaptor<GORCitizenMandatoryFields[]> gorCitizenMandatoryFieldsArgumentCaptor =
                ArgumentCaptor.forClass(GORCitizenMandatoryFields[].class);
        verify(mandatoryFieldsValidatorUtils, times(2))
                .addWarningsForConditionalFields(any(), any(),
                        gorCitizenMandatoryFieldsArgumentCaptor.capture());
        List<GORCitizenMandatoryFields[]> capturedArgs =
                gorCitizenMandatoryFieldsArgumentCaptor.getAllValues();
        assertEquals(2, capturedArgs.size());
        assertEquals(GORCitizenMandatoryFields.IHT_400_PROCESS.getValue(), capturedArgs.get(0)[0].getValue());
        assertEquals(GORCitizenMandatoryFields.IHT_CODE.getValue(), capturedArgs.get(1)[0].getValue());
        assertEquals(GORCitizenMandatoryFields.PROBATE_GROSS_VALUE_IHT_400.getValue(),
                capturedArgs.get(1)[1].getValue());
        assertEquals(GORCitizenMandatoryFields.PROBATE_NET_VALUE_IHT_400.getValue(),
                capturedArgs.get(1)[2].getValue());
    }

    @Test
    void shouldNotAddDuplicateWarningsForIht421Values() {
        List<OCRField> ocrFields = new ArrayList<>();
        OCRField deceasedDiedOnAfterSwitchDate = OCRField.builder()
                .name("deceasedDiedOnAfterSwitchDate")
                .value("true")
                .description("deceasedDiedOnAfterSwitchDate").build();
        OCRField deceasedDateOfDeath = OCRField.builder()
                .name("deceasedDateOfDeath")
                .value("01012022")
                .description("deceasedDateOfDeath").build();
        OCRField iht400 = OCRField.builder()
                .name("iht400completed")
                .value("true")
                .description("iht400completed").build();
        OCRField iht400421 = OCRField.builder()
                .name("iht400421completed")
                .value("true")
                .description("iht400421completed").build();
        OCRField iht400Process = OCRField.builder()
                .name("iht400process")
                .value("false")
                .description("iht400process").build();
        ocrFields.add(deceasedDiedOnAfterSwitchDate);
        ocrFields.add(deceasedDateOfDeath);
        ocrFields.add(iht400);
        ocrFields.add(iht400421);
        ocrFields.add(iht400Process);
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        commonMandatoryFieldsValidatorV3.addWarnings(ocrFieldValues, warnings);

        ArgumentCaptor<GORCitizenMandatoryFields[]> gorCitizenMandatoryFieldsArgumentCaptor =
                ArgumentCaptor.forClass(GORCitizenMandatoryFields[].class);

        verify(mandatoryFieldsValidatorUtils, times(2))
                .addWarningsForConditionalFields(any(), any(),
                        gorCitizenMandatoryFieldsArgumentCaptor.capture());


        List<GORCitizenMandatoryFields[]> capturedArgs =
                gorCitizenMandatoryFieldsArgumentCaptor.getAllValues();
        assertEquals(2, capturedArgs.size());
        assertEquals(GORCitizenMandatoryFields.IHT_400_PROCESS.getValue(), capturedArgs.get(0)[0].getValue());
        assertEquals(GORCitizenMandatoryFields.IHT_421_GROSS_VALUE.getValue(), capturedArgs.get(1)[0].getValue());
        assertEquals(GORCitizenMandatoryFields.IHT_421_NET_VALUE.getValue(), capturedArgs.get(1)[1].getValue());
    }
}
