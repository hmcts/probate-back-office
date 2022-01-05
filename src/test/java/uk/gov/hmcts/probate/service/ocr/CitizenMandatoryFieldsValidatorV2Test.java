package uk.gov.hmcts.probate.service.ocr;

import org.apache.commons.collections.keyvalue.DefaultKeyValue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.ccd.ocr.GORCitizenMandatoryFields;
import uk.gov.hmcts.probate.model.ocr.OCRField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.service.ocr.CitizenMandatoryFieldsValidatorV2.DIED_AFTER_SWITCH_DATE;
import static uk.gov.hmcts.probate.service.ocr.CitizenMandatoryFieldsValidatorV2.IHT_205_COMPLETED_ONLINE;

public class CitizenMandatoryFieldsValidatorV2Test {
    private OCRFieldTestUtils ocrFieldTestUtils = new OCRFieldTestUtils();
    private ArrayList<String> warnings;

    @Mock
    private MandatoryFieldsValidatorUtils mandatoryFieldsValidatorUtils;

    @InjectMocks
    private CitizenMandatoryFieldsValidatorV2 citizenMandatoryFieldsValidatorV2;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        warnings = new ArrayList<>();
    }

    @Test
    public void testAllMissingIHT207FieldPA1PCitizenV2() {
        List<OCRField> ocrFields = ocrFieldTestUtils.addAllMandatoryGORCitizenFields();
        ocrFieldTestUtils.addAllV2Data(ocrFields);
        ocrFieldTestUtils.removeOCRField(ocrFields, "iht207completed");

        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        when(mandatoryFieldsValidatorUtils.isVersion2(ocrFieldValues)).thenReturn(true);
        warnings.add("IHT 400421 completed (iht400421completed) is mandatory.");
        warnings.add("IHT 207 completed (iht207completed) is mandatory.");

        citizenMandatoryFieldsValidatorV2.addWarnings(ocrFieldValues, warnings);

        ArgumentCaptor<DefaultKeyValue> defaultKeyValueArgumentCaptor1 =
            ArgumentCaptor.forClass(DefaultKeyValue.class);
        verify(mandatoryFieldsValidatorUtils, times(2)).addWarningIfEmpty(any(), any(),
            defaultKeyValueArgumentCaptor1.capture());
        DefaultKeyValue defaultKeyValueArgumentCaptorValue1 = defaultKeyValueArgumentCaptor1.getValue();
        assertEquals(DIED_AFTER_SWITCH_DATE.getValue(), defaultKeyValueArgumentCaptorValue1.getValue());

        assertEquals(2, warnings.size());
        assertEquals("IHT 400421 completed (iht400421completed) is mandatory.", warnings.get(0));
        assertEquals("IHT 207 completed (iht207completed) is mandatory.", warnings.get(1));
    }

    @Test
    public void testAllMissingDiedAfterFieldPA1PCitizenV2() {
        List<OCRField> ocrFields = ocrFieldTestUtils.addAllMandatoryGORCitizenFields();
        ocrFieldTestUtils.addAllV2Data(ocrFields);
        ocrFieldTestUtils.removeOCRField(ocrFields, "deceasedDiedOnAfterSwitchDate");
        warnings.add("IHT 400421 completed (iht400421completed) is mandatory.");
        warnings.add("deceasedDiedOnAfterSwitchDate (deceasedDiedOnAfterSwitchDate) is mandatory.");
        warnings.add("iht205completedOnline (iht205completedOnline) is mandatory.");

        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        when(mandatoryFieldsValidatorUtils.isVersion2(ocrFieldValues)).thenReturn(true);

        citizenMandatoryFieldsValidatorV2.addWarnings(ocrFieldValues, warnings);

        ArgumentCaptor<DefaultKeyValue> defaultKeyValueArgumentCaptor1 =
            ArgumentCaptor.forClass(DefaultKeyValue.class);
        verify(mandatoryFieldsValidatorUtils, times(3)).addWarningIfEmpty(any(), any(),
            defaultKeyValueArgumentCaptor1.capture());
        DefaultKeyValue defaultKeyValueArgumentCaptorValue1 = defaultKeyValueArgumentCaptor1.getValue();
        assertEquals(IHT_205_COMPLETED_ONLINE.getValue(), defaultKeyValueArgumentCaptorValue1.getValue());

        assertEquals(3, warnings.size());
        assertEquals("IHT 400421 completed (iht400421completed) is mandatory.", warnings.get(0));
        assertEquals("deceasedDiedOnAfterSwitchDate (deceasedDiedOnAfterSwitchDate) is mandatory.", warnings.get(1));
        assertEquals("iht205completedOnline (iht205completedOnline) is mandatory.", warnings.get(2));
    }

    @Test
    public void testAllMissingConditionalEstateFieldsPA1PCitizenV2() {
        List<OCRField> ocrFields = ocrFieldTestUtils.addAllMandatoryGORCitizenFields();
        ocrFieldTestUtils.addAllV2Data(ocrFields);
        ocrFieldTestUtils.removeOCRField(ocrFields, "ihtEstateGrossValue");
        ocrFieldTestUtils.removeOCRField(ocrFields, "ihtEstateNetValue");
        ocrFieldTestUtils.removeOCRField(ocrFields, "ihtEstateNetQualifyingValue");
        ocrFieldTestUtils.removeOCRField(ocrFields, "ihtUnusedAllowanceClaimed");
        ocrFieldTestUtils.removeOCRField(ocrFields, "deceasedHadLateSpouseOrCivilPartner");

        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        when(mandatoryFieldsValidatorUtils.isVersion2(ocrFieldValues)).thenReturn(true);

        citizenMandatoryFieldsValidatorV2.addWarnings(ocrFieldValues, warnings);

        ArgumentCaptor<GORCitizenMandatoryFields> argumentCaptor1 =
            ArgumentCaptor.forClass(GORCitizenMandatoryFields.class);
        verify(mandatoryFieldsValidatorUtils, times(1)).addWarningsForConditionalFields(any(), any(),
            argumentCaptor1.capture());
        List<GORCitizenMandatoryFields> argumentCaptorValue1 = argumentCaptor1.getAllValues();
        assertEquals(5, argumentCaptorValue1.size());
        assertEquals("ihtEstateGrossValue", argumentCaptorValue1.get(0).getKey());
        assertEquals("ihtEstateNetValue", argumentCaptorValue1.get(1).getKey());
        assertEquals("ihtEstateNetQualifyingValue", argumentCaptorValue1.get(2).getKey());
        assertEquals("ihtUnusedAllowanceClaimed", argumentCaptorValue1.get(3).getKey());
        assertEquals("deceasedHadLateSpouseOrCivilPartner", argumentCaptorValue1.get(4).getKey());
    }

    @Test
    public void testAllMissingConditionalFieldsIHTReferencePA1PCitizenV2() {
        List<OCRField> ocrFields = ocrFieldTestUtils.addAllMandatoryGORCitizenFields();
        ocrFieldTestUtils.addAllV2Data(ocrFields);
        OCRField iht205completedOnline = OCRField.builder()
            .name("iht205completedOnline")
            .value("true")
            .description("IHT Completed").build();
        ocrFields.add(iht205completedOnline);

        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        when(mandatoryFieldsValidatorUtils.isVersion2(ocrFieldValues)).thenReturn(true);

        citizenMandatoryFieldsValidatorV2.addWarnings(ocrFieldValues, warnings);

        ArgumentCaptor<GORCitizenMandatoryFields> argumentCaptor1 =
            ArgumentCaptor.forClass(GORCitizenMandatoryFields.class);
        verify(mandatoryFieldsValidatorUtils, times(2)).addWarningsForConditionalFields(any(), any(),
            argumentCaptor1.capture());
        List<GORCitizenMandatoryFields> argumentCaptorValue1 = argumentCaptor1.getAllValues();
        assertEquals(6, argumentCaptorValue1.size());
        assertEquals("ihtEstateGrossValue", argumentCaptorValue1.get(0).getKey());
        assertEquals("ihtEstateNetValue", argumentCaptorValue1.get(1).getKey());
        assertEquals("ihtEstateNetQualifyingValue", argumentCaptorValue1.get(2).getKey());
        assertEquals("ihtUnusedAllowanceClaimed", argumentCaptorValue1.get(3).getKey());
        assertEquals("deceasedHadLateSpouseOrCivilPartner", argumentCaptorValue1.get(4).getKey());
        assertEquals("ihtReferenceNumber", argumentCaptorValue1.get(5).getKey());
    }
}