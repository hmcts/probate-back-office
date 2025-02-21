package uk.gov.hmcts.probate.service.ocr;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.config.BulkScanConfig;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.probate.service.ExceptedEstateDateOfDeathChecker;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.ModifiedOCRField;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class OCRFieldModifierUtilsTest {
    @InjectMocks
    private OCRFieldModifierUtils ocrFieldModifierUtils;
    @Mock
    private BulkScanConfig bulkScanConfig;

    @Mock
    private ExceptedEstateDateOfDeathChecker exceptedEstateDateOfDeathChecker;
    private static final String DEFAULT_IHT_FORM = "FALSE";
    private static final String DEFAULT_VALUE = "1.11";

    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        ocrFieldModifierUtils = new OCRFieldModifierUtils(bulkScanConfig, exceptedEstateDateOfDeathChecker);
        when(bulkScanConfig.getIhtForm()).thenReturn(DEFAULT_IHT_FORM);
        when(bulkScanConfig.getGrossNetValue()).thenReturn(DEFAULT_VALUE);
        Field bulkScanConfigField = OCRFieldModifierUtils.class.getDeclaredField("bulkScanConfig");
        bulkScanConfigField.setAccessible(true);
        bulkScanConfigField.set(ocrFieldModifierUtils, bulkScanConfig);
    }

    @Test
    void shouldSetDeceasedDiedOnAfterSwitchDateTrueWhenDeceasedDateOfDeathIsAfter() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder().deceasedDateOfDeath("01012022")
                .deceasedDiedOnAfterSwitchDate("").build();

        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate("01012022")).thenReturn(true);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultValues(ocrFields);

        assertEquals(1, modifiedFields.size());
        assertEquals("deceasedDiedOnAfterSwitchDate", modifiedFields.get(0).getValue().getFieldName());
        assertEquals("TRUE", ocrFields.getDeceasedDiedOnAfterSwitchDate());
    }

    @Test
    void shouldSetDeceasedDiedOnAfterSwitchDateFalseWhenDeceasedDateOfDeathIsBefore() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder().deceasedDateOfDeath("01012020")
                .deceasedDiedOnAfterSwitchDate("").build();

        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate("01012020")).thenReturn(false);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultValues(ocrFields);

        assertEquals(1, modifiedFields.size());
        assertEquals("deceasedDiedOnAfterSwitchDate", modifiedFields.get(0).getValue().getFieldName());
        assertEquals("FALSE", ocrFields.getDeceasedDiedOnAfterSwitchDate());
    }

    @Test
    void shouldSetDefaultIHTFormWhenDeceasedDateOfDeathIsAfterAndFormVersionIsThree() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder().deceasedDateOfDeath("01012022")
                .formVersion("3").build();

        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate("01012022")).thenReturn(true);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultValues(ocrFields);

        assertEquals("deceasedDiedOnAfterSwitchDate", modifiedFields.get(0).getValue().getFieldName());
        assertEquals("TRUE", ocrFields.getDeceasedDiedOnAfterSwitchDate());
        assertEquals("iht400421Completed", modifiedFields.get(1).getValue().getFieldName());
        assertEquals("TRUE", ocrFields.getIht400Completed());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht400421Completed());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht207Completed());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht205Completed());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht205completedOnline());
    }

    @Test
    void shouldSetDefaultIHTFormWhenDeceasedDateOfDeathIsBeforeAndFormVersionIsThree() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder().deceasedDateOfDeath("01012020")
                .formVersion("3").build();

        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate("01012020")).thenReturn(false);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultValues(ocrFields);

        assertEquals("deceasedDiedOnAfterSwitchDate", modifiedFields.get(0).getValue().getFieldName());
        assertEquals("FALSE", ocrFields.getDeceasedDiedOnAfterSwitchDate());
        assertEquals("iht400421Completed", modifiedFields.get(1).getValue().getFieldName());
        assertEquals("TRUE", ocrFields.getIht400Completed());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht400421Completed());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht207Completed());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht205Completed());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht205completedOnline());
    }

    @Test
    void shouldSetDefaultIHTFormWhenDeceasedDateOfDeathIsAfterAndFormVersionIsTwo() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder().deceasedDateOfDeath("01012022")
                .formVersion("2").build();

        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate("01012022")).thenReturn(true);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultValues(ocrFields);

        assertEquals("deceasedDiedOnAfterSwitchDate", modifiedFields.get(0).getValue().getFieldName());
        assertEquals("TRUE", ocrFields.getDeceasedDiedOnAfterSwitchDate());
        assertEquals("iht400421Completed", modifiedFields.get(1).getValue().getFieldName());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht400421Completed());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht207Completed());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht205Completed());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht205completedOnline());
    }

    @Test
    void shouldSetDefaultIHTFormWhenDeceasedDateOfDeathIsBeforeAndFormVersionIsTwo() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder().deceasedDateOfDeath("01012020")
                .formVersion("2").build();

        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate("01012020")).thenReturn(false);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultValues(ocrFields);

        assertEquals("deceasedDiedOnAfterSwitchDate", modifiedFields.get(0).getValue().getFieldName());
        assertEquals("FALSE", ocrFields.getDeceasedDiedOnAfterSwitchDate());
        assertEquals("iht400421Completed", modifiedFields.get(1).getValue().getFieldName());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht400421Completed());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht207Completed());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht205Completed());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht205completedOnline());
    }

    @Test
    void shouldSetDefaultGrossNetValueWhenDeceasedDateOfDeathIsAfterAndFormVersionIsThreeAndIhtIs400421() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder().deceasedDateOfDeath("01012022")
                .formVersion("3").iht400421Completed("TRUE").build();

        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate("01012022")).thenReturn(true);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultValues(ocrFields);

        assertEquals("deceasedDiedOnAfterSwitchDate", modifiedFields.get(0).getValue().getFieldName());
        assertEquals("TRUE", ocrFields.getDeceasedDiedOnAfterSwitchDate());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht207Completed());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht205Completed());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht205completedOnline());
        assertEquals(DEFAULT_VALUE, ocrFields.getIht421grossValue());
        assertEquals(DEFAULT_VALUE, ocrFields.getIht421netValue());
    }

    @Test
    void shouldSetDefaultGrossNetValueWhenDeceasedDateOfDeathIsAfterAndFormVersionIsTwoAndIhtIs400421() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder().deceasedDateOfDeath("01012022")
                .formVersion("2").iht400421Completed("TRUE").build();

        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate("01012022")).thenReturn(true);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultValues(ocrFields);

        assertEquals("deceasedDiedOnAfterSwitchDate", modifiedFields.get(0).getValue().getFieldName());
        assertEquals("TRUE", ocrFields.getDeceasedDiedOnAfterSwitchDate());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht207Completed());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht205Completed());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht205completedOnline());
        assertEquals(DEFAULT_VALUE, ocrFields.getIht421grossValue());
        assertEquals(DEFAULT_VALUE, ocrFields.getIht421netValue());
    }

    @Test
    void shouldSetDefaultGrossNetValueWhenDeceasedDateOfDeathIsAfterAndFormVersionIsThreeAndIhtIs207() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder().deceasedDateOfDeath("01012022")
                .formVersion("3").iht207Completed("TRUE").build();

        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate("01012022")).thenReturn(true);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultValues(ocrFields);

        assertEquals("deceasedDiedOnAfterSwitchDate", modifiedFields.get(0).getValue().getFieldName());
        assertEquals("TRUE", ocrFields.getDeceasedDiedOnAfterSwitchDate());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht400421Completed());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht205Completed());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht205completedOnline());
        assertEquals(DEFAULT_VALUE, ocrFields.getIht207grossValue());
        assertEquals(DEFAULT_VALUE, ocrFields.getIht207netValue());
    }

    @Test
    void shouldSetDefaultGrossNetValueWhenDeceasedDateOfDeathIsAfterAndFormVersionIsTwoAndIhtIs207() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder().deceasedDateOfDeath("01012022")
                .formVersion("2").iht207Completed("TRUE").build();

        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate("01012022")).thenReturn(true);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = oCRFieldModifierUtils.setDefaultValues(ocrFields);

        assertEquals("deceasedDiedOnAfterSwitchDate", modifiedFields.get(0).getValue().getFieldName());
        assertEquals("TRUE", ocrFields.getDeceasedDiedOnAfterSwitchDate());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht400421Completed());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht205Completed());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht205completedOnline());
        assertEquals(DEFAULT_VALUE, ocrFields.getIht207grossValue());
        assertEquals(DEFAULT_VALUE, ocrFields.getIht207netValue());
    }

    @Test
    void shouldSetDefaultGrossNetValueWhenDeceasedDateOfDeathIsBeforeAndFormVersionIsThreeAndIhtIs205() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder().deceasedDateOfDeath("01012020")
                .formVersion("3").iht205Completed("TRUE").build();

        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate("01012020")).thenReturn(false);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = oCRFieldModifierUtils.setDefaultValues(ocrFields);

        assertEquals("deceasedDiedOnAfterSwitchDate", modifiedFields.get(0).getValue().getFieldName());
        assertEquals("FALSE", ocrFields.getDeceasedDiedOnAfterSwitchDate());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht400421Completed());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht207Completed());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht205completedOnline());
        assertEquals(DEFAULT_VALUE, ocrFields.getIhtGrossValue205());
        assertEquals(DEFAULT_VALUE, ocrFields.getIhtNetValue205());
    }

    @Test
    void shouldSetDefaultGrossNetValueWhenDeceasedDateOfDeathIsBeforeAndFormVersionIsTwoAndIhtIs205Online() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder().deceasedDateOfDeath("01012020")
                .formVersion("2").iht205completedOnline("TRUE").build();

        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate("01012020")).thenReturn(false);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = oCRFieldModifierUtils.setDefaultValues(ocrFields);

        assertEquals("deceasedDiedOnAfterSwitchDate", modifiedFields.get(0).getValue().getFieldName());
        assertEquals("FALSE", ocrFields.getDeceasedDiedOnAfterSwitchDate());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht400421Completed());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht207Completed());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht205Completed());
        assertEquals(DEFAULT_VALUE, ocrFields.getIhtGrossValue205());
        assertEquals(DEFAULT_VALUE, ocrFields.getIhtNetValue205());
        assertEquals("1234", ocrFields.getIhtReferenceNumber());
    }

    @Test
    void shouldSetDefaultEstateGrossNetValueWhenDeceasedDateOfDeathIsBeforeAndFormVersionIsThree() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder().deceasedDateOfDeath("01012020")
                .formVersion("3").exceptedEstate("true").build();

        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate("01012020")).thenReturn(false);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = oCRFieldModifierUtils.setDefaultValues(ocrFields);

        assertEquals("deceasedDiedOnAfterSwitchDate", modifiedFields.get(0).getValue().getFieldName());
        assertEquals("FALSE", ocrFields.getDeceasedDiedOnAfterSwitchDate());
        assertEquals(DEFAULT_VALUE, ocrFields.getIhtEstateGrossValue());
        assertEquals(DEFAULT_VALUE, ocrFields.getIhtEstateNetValue());
        assertEquals(DEFAULT_VALUE, ocrFields.getIhtEstateNetQualifyingValue());
    }

    @Test
    void shouldSetDefaultEstateGrossNetValueWhenDeceasedDateOfDeathIsAfterAndFormVersionIsTwo() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder().deceasedDateOfDeath("01012022")
                .formVersion("2").build();

        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate("01012022")).thenReturn(true);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = oCRFieldModifierUtils.setDefaultValues(ocrFields);

        assertEquals("deceasedDiedOnAfterSwitchDate", modifiedFields.get(0).getValue().getFieldName());
        assertEquals("TRUE", ocrFields.getDeceasedDiedOnAfterSwitchDate());
        assertEquals(DEFAULT_VALUE, ocrFields.getIhtEstateGrossValue());
        assertEquals(DEFAULT_VALUE, ocrFields.getIhtEstateNetValue());
        assertEquals(DEFAULT_VALUE, ocrFields.getIhtEstateNetQualifyingValue());
    }
}
