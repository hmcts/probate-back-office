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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.Constants.FALSE;
import static uk.gov.hmcts.probate.model.Constants.TRUE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.DECEASED_ANY_OTHER_NAMES;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.DECEASED_DIED_ON_OR_AFTER_SWITCH_DATE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.DECEASED_DOMICILE_IN_ENG_WALES;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.DECEASED_DOB;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.DECEASED_DOD;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.DEFAULT_VALUE;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.DEFAULT_DECEASED_SURNAME_VALUE;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.DEFAULT_POSTCODE_VALUE;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.DEFAULT_DOB_VALUE;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.DEFAULT_EMAIL_VALUE;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.DEFAULT_PHONE_VALUE;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.DEFAULT_DATE_OF_BIRTH;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.DEFAULT_DECEASED_ANY_OTHER_NAMES_VALUE;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.DEFAULT_DECEASED_DOMICILE_IN_ENG_WALES_VALUE;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_PRIMARY_APPLICANT_FORENAMES;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_PRIMARY_APPLICANT_SURNAME;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_PRIMARY_APPLICANT_ADDRESS_LINE_1;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_PRIMARY_APPLICANT_ADDRESS_POSTCODE;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_PRIMARY_APPLICANT_ALIAS;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_PRIMARY_APPLICANT_HAS_ALIAS;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_DECEASED_FORENAMES;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_DECEASED_SURNAME;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_DECEASED_ADDRESS_LINE_1;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_DECEASED_ADDRESS_POSTCODE;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_DECEASED_DATE_OF_BIRTH;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_DECEASED_ANY_OTHER_NAMES;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_DECEASED_DOMICILED_IN_ENG_WALES;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.MARRIED_CIVIL_PARTNERSHIP;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.DIVORCED_CIVIL_PARTNERSHIP;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.DATE_OF_MARRIAGE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.DATE_OF_DIVORCED_CP_JUDICIALLY;

class DeceasedFieldsHandlerTest {
    @InjectMocks
    private DeceasedFieldsHandler deceasedFieldsHandler;
    @Mock
    private BulkScanConfig bulkScanConfig;

    @Mock
    private ExceptedEstateDateOfDeathChecker exceptedEstateDateOfDeathChecker;

    private ExceptionRecordOCRFields ocrFields;
    private Field maritalStatusField;
    private Field foreignAssestField;

    @BeforeEach
    void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        deceasedFieldsHandler = new DeceasedFieldsHandler(bulkScanConfig, exceptedEstateDateOfDeathChecker);

        when(bulkScanConfig.getName()).thenReturn(DEFAULT_DECEASED_SURNAME_VALUE);
        when(bulkScanConfig.getPostcode()).thenReturn(DEFAULT_POSTCODE_VALUE);
        when(bulkScanConfig.getDob()).thenReturn(DEFAULT_DOB_VALUE);
        when(bulkScanConfig.getEmail()).thenReturn(DEFAULT_EMAIL_VALUE);
        when(bulkScanConfig.getPhone()).thenReturn(DEFAULT_PHONE_VALUE);
        when(bulkScanConfig.getDob()).thenReturn(DEFAULT_DATE_OF_BIRTH);
        when(bulkScanConfig.getDeceasedAnyOtherNames()).thenReturn(DEFAULT_DECEASED_ANY_OTHER_NAMES_VALUE);
        when(bulkScanConfig.getDeceasedDomicileInEngWales()).thenReturn(DEFAULT_DECEASED_DOMICILE_IN_ENG_WALES_VALUE);
        when(bulkScanConfig.getDeceasedDiedOnOrAfterSwitchDateTrue()).thenReturn(TRUE);
        when(bulkScanConfig.getDeceasedDiedOnOrAfterSwitchDateFalse()).thenReturn(FALSE);
        when(bulkScanConfig.getDateOfDeathForDiedOnOrAfterSwitchDateTrue()).thenReturn("01012022");
        when(bulkScanConfig.getDateOfDeathForDiedOnOrAfterSwitchDateFalse()).thenReturn("01011990");

        Field bulkScanConfigField = DeceasedFieldsHandler.class.getDeclaredField("bulkScanConfig");
        bulkScanConfigField.setAccessible(true);
        bulkScanConfigField.set(deceasedFieldsHandler, bulkScanConfig);

        maritalStatusField = ExceptionRecordOCRFields.class.getDeclaredField("deceasedMartialStatus");
        maritalStatusField.setAccessible(true);

        foreignAssestField = ExceptionRecordOCRFields.class.getDeclaredField("foreignAsset");
        foreignAssestField.setAccessible(true);

        ocrFields = ExceptionRecordOCRFields.builder()
                .primaryApplicantForenames(VALID_PRIMARY_APPLICANT_FORENAMES)
                .primaryApplicantSurname(VALID_PRIMARY_APPLICANT_SURNAME)
                .primaryApplicantAddressLine1(VALID_PRIMARY_APPLICANT_ADDRESS_LINE_1)
                .primaryApplicantAddressPostCode(VALID_PRIMARY_APPLICANT_ADDRESS_POSTCODE)
                .primaryApplicantAlias(VALID_PRIMARY_APPLICANT_ALIAS)
                .primaryApplicantHasAlias(VALID_PRIMARY_APPLICANT_HAS_ALIAS)
                .deceasedForenames(VALID_DECEASED_FORENAMES)
                .deceasedSurname(VALID_DECEASED_SURNAME)
                .deceasedAddressLine1(VALID_DECEASED_ADDRESS_LINE_1)
                .deceasedAddressPostCode(VALID_DECEASED_ADDRESS_POSTCODE)
                .deceasedDateOfBirth(VALID_DECEASED_DATE_OF_BIRTH)
                .deceasedAnyOtherNames(VALID_DECEASED_ANY_OTHER_NAMES)
                .deceasedDomicileInEngWales(VALID_DECEASED_DOMICILED_IN_ENG_WALES)
                .deceasedDiedOnAfterSwitchDate(TRUE)
                .deceasedDateOfDeath("01012022")
                .build();
    }

    @Test
    void shouldSetDateTo01011990WhenEmpty() {
        ocrFields.setDeceasedDateOfBirth("");

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        deceasedFieldsHandler.handleDeceasedFields(ocrFields, modifiedFields);

        assertEquals(1, modifiedFields.size());
        assertEquals(DECEASED_DOB, modifiedFields.getFirst().getValue().getFieldName());
        assertEquals(DEFAULT_DATE_OF_BIRTH, ocrFields.getDeceasedDateOfBirth());
    }

    @Test
    void shouldSetDeceasedAnyOtherNamesNoWhenEmpty() {
        ocrFields.setDeceasedAnyOtherNames("");

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        deceasedFieldsHandler.handleDeceasedFields(ocrFields, modifiedFields);

        assertEquals(1, modifiedFields.size());
        assertEquals(DECEASED_ANY_OTHER_NAMES, modifiedFields.getFirst().getValue().getFieldName());
        assertEquals(DEFAULT_DECEASED_ANY_OTHER_NAMES_VALUE, ocrFields.getDeceasedAnyOtherNames());
    }

    @Test
    void shouldSetDeceasedDomicileInEngWalesTrueWhenEmpty() {
        ocrFields.setDeceasedDomicileInEngWales("");

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        deceasedFieldsHandler.handleDeceasedFields(ocrFields, modifiedFields);

        assertEquals(1, modifiedFields.size());
        assertEquals(DECEASED_DOMICILE_IN_ENG_WALES, modifiedFields.getFirst().getValue().getFieldName());
        assertEquals(DEFAULT_DECEASED_DOMICILE_IN_ENG_WALES_VALUE, ocrFields.getDeceasedDomicileInEngWales());
    }

    @Test
    void shouldSetDeceasedDiedOnAfterSwitchDateTrueWhenDeceasedDateOfDeathIsAfter() {
        ocrFields.setDeceasedDateOfDeath("01012022");
        ocrFields.setDeceasedDiedOnAfterSwitchDate("");

        ocrFields.setIhtEstateNetValue("1000");
        ocrFields.setIhtEstateNetQualifyingValue("1000");
        ocrFields.setIht400421Completed(FALSE);
        ocrFields.setIht207Completed(FALSE);
        ocrFields.setIht205Completed(FALSE);
        ocrFields.setIht400Completed(TRUE);
        ocrFields.setIht400process(TRUE);
        ocrFields.setProbateGrossValueIht400("1000");
        ocrFields.setProbateNetValueIht400("1000");

        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate("01012022")).thenReturn(true);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        deceasedFieldsHandler.handleDeceasedFields(ocrFields, modifiedFields);

        assertEquals(1, modifiedFields.size());
        assertEquals(DECEASED_DIED_ON_OR_AFTER_SWITCH_DATE, modifiedFields.getFirst().getValue().getFieldName());
        assertEquals(TRUE, ocrFields.getDeceasedDiedOnAfterSwitchDate());
    }

    @Test
    void shouldSetDeceasedDiedOnAfterSwitchDateFalseWhenDeceasedDateOfDeathIsBefore() {
        ocrFields.setDeceasedDateOfDeath("01012020");
        ocrFields.setDeceasedDiedOnAfterSwitchDate("");

        ocrFields.setIht400421Completed(FALSE);
        ocrFields.setIht207Completed(FALSE);
        ocrFields.setIht205Completed(FALSE);
        ocrFields.setIht400Completed(FALSE);
        ocrFields.setIht400process(TRUE);
        ocrFields.setProbateGrossValueIht400("1000");
        ocrFields.setProbateNetValueIht400("1000");

        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate("01012020")).thenReturn(false);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        deceasedFieldsHandler.handleDeceasedFields(ocrFields, modifiedFields);

        assertEquals(1, modifiedFields.size());
        assertEquals(DECEASED_DIED_ON_OR_AFTER_SWITCH_DATE, modifiedFields.getFirst().getValue().getFieldName());
        assertEquals(FALSE, ocrFields.getDeceasedDiedOnAfterSwitchDate());
    }

    @Test
    void shouldHandleDeceasedDateOfDeathPresent() {
        ocrFields.setDeceasedDateOfDeath("01012022");
        ocrFields.setDeceasedDiedOnAfterSwitchDate("");

        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate("01012022")).thenReturn(true);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        deceasedFieldsHandler.handleDeceasedFields(ocrFields, modifiedFields);

        assertEquals(1, modifiedFields.size());
        assertEquals(DECEASED_DIED_ON_OR_AFTER_SWITCH_DATE, modifiedFields.getFirst().getValue().getFieldName());
        assertEquals(TRUE, ocrFields.getDeceasedDiedOnAfterSwitchDate());
    }

    @Test
    void shouldHandleDeceasedDateOfDeathBeforePresent() {
        ocrFields.setDeceasedDateOfDeath("01012020");
        ocrFields.setDeceasedDiedOnAfterSwitchDate("");

        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate("01012020")).thenReturn(false);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        deceasedFieldsHandler.handleDeceasedFields(ocrFields, modifiedFields);

        assertEquals(1, modifiedFields.size());
        assertEquals(DECEASED_DIED_ON_OR_AFTER_SWITCH_DATE, modifiedFields.getFirst().getValue().getFieldName());
        assertEquals(FALSE, ocrFields.getDeceasedDiedOnAfterSwitchDate());
    }

    @Test
    void shouldHandleDeceasedDateOfDeathMissing() {
        ocrFields.setDeceasedDateOfDeath("");
        ocrFields.setDeceasedDiedOnAfterSwitchDate(TRUE);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        deceasedFieldsHandler.handleDeceasedFields(ocrFields, modifiedFields);

        assertEquals(1, modifiedFields.size());
        assertEquals(DECEASED_DOD, modifiedFields.getFirst().getValue().getFieldName());
        assertEquals(bulkScanConfig.getDateOfDeathForDiedOnOrAfterSwitchDateTrue(), ocrFields.getDeceasedDateOfDeath());
    }

    @Test
    void shouldHandleDeceasedDateOfDeathMissingWhenSwitchDateIsFalse() {
        ocrFields.setDeceasedDateOfDeath("");
        ocrFields.setDeceasedDiedOnAfterSwitchDate(FALSE);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        deceasedFieldsHandler.handleDeceasedFields(ocrFields, modifiedFields);

        assertEquals(1, modifiedFields.size());
        assertEquals(DECEASED_DOD, modifiedFields.getFirst().getValue().getFieldName());
        assertEquals(bulkScanConfig.getDateOfDeathForDiedOnOrAfterSwitchDateFalse(),
                ocrFields.getDeceasedDateOfDeath());
    }

    @Test
    void shouldHandleBothDeceasedFieldsMissing() {
        ocrFields.setDeceasedDateOfDeath("");
        ocrFields.setDeceasedDiedOnAfterSwitchDate("");
        ocrFields.setIhtEstateGrossValue(DEFAULT_VALUE);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        deceasedFieldsHandler.handleDeceasedFields(ocrFields, modifiedFields);

        assertEquals(2, modifiedFields.size());
        assertEquals(DECEASED_DIED_ON_OR_AFTER_SWITCH_DATE, modifiedFields.getFirst().getValue().getFieldName());
        assertEquals(TRUE, ocrFields.getDeceasedDiedOnAfterSwitchDate());
        assertEquals(DECEASED_DOD, modifiedFields.getLast().getValue().getFieldName());
        assertEquals("01012022", ocrFields.getDeceasedDateOfDeath());
    }

    @Test
    void shouldSetDateOfMarriageOrCPWhenMaritalStatusIsMarriedOrCivilPartnership() throws IllegalAccessException {
        maritalStatusField.set(ocrFields, MARRIED_CIVIL_PARTNERSHIP);
        ocrFields.setDateOfMarriageOrCP("");

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        deceasedFieldsHandler.handleDeceasedFields(ocrFields, modifiedFields);

        assertEquals(1, modifiedFields.size());
        assertEquals(DATE_OF_MARRIAGE, modifiedFields.get(0).getValue().getFieldName());
        assertEquals(bulkScanConfig.getDob(), ocrFields.getDateOfMarriageOrCP());
    }

    @Test
    void shouldSetDateOfDivorcedCPJudiciallyWhenMaritalStatusIsDivorcedOrJudicially() throws IllegalAccessException {
        maritalStatusField.set(ocrFields, DIVORCED_CIVIL_PARTNERSHIP);
        ocrFields.setDateOfDivorcedCPJudicially("");

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        deceasedFieldsHandler.handleDeceasedFields(ocrFields, modifiedFields);

        assertEquals(1, modifiedFields.size());
        assertEquals(DATE_OF_DIVORCED_CP_JUDICIALLY, modifiedFields.get(0).getValue().getFieldName());
        assertEquals(bulkScanConfig.getDob(), ocrFields.getDateOfDivorcedCPJudicially());
    }

    @Test
    void shouldNotSetDateOfDivorcedCPJudiciallyWhenMaritalStatusIsUnknown() throws IllegalAccessException {
        maritalStatusField.set(ocrFields, "UNKNOWN");
        ocrFields.setDateOfDivorcedCPJudicially("");

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        deceasedFieldsHandler.handleDeceasedFields(ocrFields, modifiedFields);

        assertEquals(0, modifiedFields.size());
    }

    @Test
    void shouldSetForeignAssetEstateValueWhenForeignAssetIsTrueAndValueIsEmpty() throws IllegalAccessException {
        foreignAssestField.set(ocrFields, TRUE);
        ocrFields.setForeignAssetEstateValue("");

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        deceasedFieldsHandler.handleDeceasedFields(ocrFields, modifiedFields);

        assertEquals(1, modifiedFields.size());
        assertEquals("foreignAssetEstateValue", modifiedFields.getFirst().getValue().getFieldName());
        assertEquals(bulkScanConfig.getGrossNetValue(), ocrFields.getForeignAssetEstateValue());
    }

    @Test
    void shouldNotModifyForeignAssetEstateValueWhenValueIsPresent() throws IllegalAccessException {
        foreignAssestField.set(ocrFields, TRUE);
        ocrFields.setForeignAssetEstateValue("1000");

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        deceasedFieldsHandler.handleDeceasedFields(ocrFields, modifiedFields);

        assertEquals(0, modifiedFields.size());
        assertEquals("1000", ocrFields.getForeignAssetEstateValue());
    }

    @org.junit.jupiter.api.Test
    void shouldNotModifyForeignAssetEstateValueWhenForeignAssetIsFalse() throws IllegalAccessException {
        foreignAssestField.set(ocrFields, FALSE);
        ocrFields.setForeignAssetEstateValue("");

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        deceasedFieldsHandler.handleDeceasedFields(ocrFields, modifiedFields);

        assertEquals(0, modifiedFields.size());
        assertEquals("", ocrFields.getForeignAssetEstateValue());
    }
}
