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

    private ExceptionRecordOCRFields ocrFields;

    private Field formVersionField;

    //Defaults
    private static final String DEFAULT_IHT_FORM = "FALSE";
    private static final String DEFAULT_VALUE = "1.11";
    private static final String DEFAULT_DECEASED_SURNAME_VALUE = "MISSING";
    private static final String DEFAULT_POSTCODE_VALUE = "MI55 1NG";
    private static final String DEFAULT_DOB_VALUE = "01011990";
    private static final String DEFAULT_EMAIL_VALUE = "contactprobate@justice.gov.uk";
    private static final String DEFAULT_PHONE_VALUE = "1234";
    private static final String DEFAULT_DATE_OF_BIRTH = "01011900";
    private static final String DEFAULT_DECEASED_ANY_OTHER_NAMES_VALUE = "FALSE";
    private static final String DEFAULT_DECEASED_DOMICILE_IN_ENG_WALES_VALUE = "TRUE";

    //Valid
    private static final String VALID_PRIMARY_APPLICANT_FORENAMES = "First Name";
    private static final String VALID_PRIMARY_APPLICANT_SURNAME = "Surname";
    private static final String VALID_PRIMARY_APPLICANT_ADDRESS_LINE_1 = "123 Fake Street";
    private static final String VALID_PRIMARY_APPLICANT_ADDRESS_POSTCODE = "SW1A 1AA";

    private static final String VALID_SOLICITOR_IS_APPLYING = "TRUE";
    private static final String VALID_SOLICITOR_FIRM_NAME = "Firm Name";
    private static final String VALID_SOLICITOR_REPRESENTATIVE_NAME = "Representative Name";
    private static final String VALID_SOLICITOR_APP_REFERENCE = "App Reference";
    private static final String VALID_SOLICITOR_ADDRESS_LINE_1 = "321 Sols Street";
    private static final String VALID_SOLICITOR_ADDRESS_LINE_2 = "321 Fake Place";
    private static final String VALID_SOLICITOR_ADDRESS_TOWN = "Fakeville";
    private static final String VALID_SOLICITOR_ADDRESS_POSTCODE = "SW1A 1AA";
    private static final String VALID_SOLICITOR_EMAIL = "rog@gmail.com";
    private static final String VALID_SOLICITOR_PHONE_NUMBER = "07123456789";

    private static final String VALID_DECEASED_SURNAME = "Deceased Surname";
    private static final String VALID_DECEASED_ADDRESS_LINE_1 = "101 Fake Street";
    private static final String VALID_DECEASED_ADDRESS_POSTCODE = "SW1A 1AA";
    private static final String VALID_DECEASED_DATE_OF_BIRTH = "01-01-1990";
    private static final String VALID_DECEASED_ANY_OTHER_NAMES = "TRUE";
    private static final String VALID_DECEASED_DOMICILED_IN_ENG_WALES = "TRUE";

    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        ocrFieldModifierUtils = new OCRFieldModifierUtils(bulkScanConfig, exceptedEstateDateOfDeathChecker);

        when(bulkScanConfig.getIhtForm()).thenReturn(DEFAULT_IHT_FORM);
        when(bulkScanConfig.getGrossNetValue()).thenReturn(DEFAULT_VALUE);
        when(bulkScanConfig.getName()).thenReturn(DEFAULT_DECEASED_SURNAME_VALUE);
        when(bulkScanConfig.getPostcode()).thenReturn(DEFAULT_POSTCODE_VALUE);
        when(bulkScanConfig.getDob()).thenReturn(DEFAULT_DOB_VALUE);
        when(bulkScanConfig.getEmail()).thenReturn(DEFAULT_EMAIL_VALUE);
        when(bulkScanConfig.getPhone()).thenReturn(DEFAULT_PHONE_VALUE);
        when(bulkScanConfig.getDob()).thenReturn(DEFAULT_DATE_OF_BIRTH);
        when(bulkScanConfig.getDeceasedAnyOtherNames()).thenReturn(DEFAULT_DECEASED_ANY_OTHER_NAMES_VALUE);
        when(bulkScanConfig.getDeceasedDomicileInEngWales()).thenReturn(DEFAULT_DECEASED_DOMICILE_IN_ENG_WALES_VALUE);

        Field bulkScanConfigField = OCRFieldModifierUtils.class.getDeclaredField("bulkScanConfig");
        bulkScanConfigField.setAccessible(true);
        bulkScanConfigField.set(ocrFieldModifierUtils, bulkScanConfig);

        formVersionField = ExceptionRecordOCRFields.class.getDeclaredField("formVersion");
        formVersionField.setAccessible(true);

        ocrFields = ExceptionRecordOCRFields.builder()
                .primaryApplicantForenames(VALID_PRIMARY_APPLICANT_FORENAMES)
                .primaryApplicantSurname(VALID_PRIMARY_APPLICANT_SURNAME)
                .primaryApplicantAddressLine1(VALID_PRIMARY_APPLICANT_ADDRESS_LINE_1)
                .primaryApplicantAddressPostCode(VALID_PRIMARY_APPLICANT_ADDRESS_POSTCODE)
                .solsSolicitorIsApplying(VALID_SOLICITOR_IS_APPLYING)
                .solsSolicitorFirmName(VALID_SOLICITOR_FIRM_NAME)
                .solsSolicitorRepresentativeName(VALID_SOLICITOR_REPRESENTATIVE_NAME)
                .solsSolicitorAppReference(VALID_SOLICITOR_APP_REFERENCE)
                .solsSolicitorAddressLine1(VALID_SOLICITOR_ADDRESS_LINE_1)
                .solsSolicitorAddressLine2(VALID_SOLICITOR_ADDRESS_LINE_2)
                .solsSolicitorAddressTown(VALID_SOLICITOR_ADDRESS_TOWN)
                .solsSolicitorAddressPostCode(VALID_SOLICITOR_ADDRESS_POSTCODE)
                .solsSolicitorEmail(VALID_SOLICITOR_EMAIL)
                .solsSolicitorPhoneNumber(VALID_SOLICITOR_PHONE_NUMBER)
                .deceasedSurname(VALID_DECEASED_SURNAME)
                .deceasedAddressLine1(VALID_DECEASED_ADDRESS_LINE_1)
                .deceasedAddressPostCode(VALID_DECEASED_ADDRESS_POSTCODE)
                .deceasedDateOfBirth(VALID_DECEASED_DATE_OF_BIRTH)
                .deceasedAnyOtherNames(VALID_DECEASED_ANY_OTHER_NAMES)
                .deceasedDomicileInEngWales(VALID_DECEASED_DOMICILED_IN_ENG_WALES)
                .build();
    }

    @Test
    void shouldSetApplicantForenameToMissingWhenEmpty() {
        ocrFields.setPrimaryApplicantForenames("");

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultValues(ocrFields);

        assertEquals(1, modifiedFields.size());
        assertEquals("primaryApplicantForenames", modifiedFields.get(0).getValue().getFieldName());
        assertEquals("MISSING", ocrFields.getPrimaryApplicantForenames());
    }

    @Test
    void shouldSetPostcodeToM1551NGWhenEmpty() {
        ocrFields.setPrimaryApplicantAddressPostCode("");
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultValues(ocrFields);

        for (CollectionMember<ModifiedOCRField> modifiedField : modifiedFields) {
            System.out.println(modifiedField.getValue().getFieldName());
        }

        assertEquals(1, modifiedFields.size());
        assertEquals("primaryApplicantAddressPostCode", modifiedFields.get(0).getValue().getFieldName());
        assertEquals("MI55 1NG", ocrFields.getPrimaryApplicantAddressPostCode());
    }

    //TODO - Not sure on the default value
    /*
    @Test
    void shouldSetSolsSolicitorIsApplyingToTrueWhenEmpty() {
        ocrFields.setSolsSolicitorIsApplying("");
        when(bulkScanConfig.getName()).thenReturn(DEFAULT_DECEASED_SURNAME_VALUE);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultValues(ocrFields);

        assertEquals(1, modifiedFields.size());
        assertEquals("solsSolicitorIsApplying", modifiedFields.get(0).getValue().getFieldName());
        assertEquals("TRUE", ocrFields.getSolsSolicitorIsApplying());
    }
     */

    @Test
    void shouldSetSolsSolicitorRepresentativeNameToFirmNameWhenEmpty() {
        ocrFields.setSolsSolicitorRepresentativeName("");
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultValues(ocrFields);

        assertEquals(1, modifiedFields.size());
        assertEquals("solsSolicitorRepresentativeName", modifiedFields.get(0).getValue().getFieldName());
        assertEquals(VALID_SOLICITOR_FIRM_NAME, ocrFields.getSolsSolicitorRepresentativeName());
    }

    @Test
    void shouldSetSolsSolicitorAppReferenceToDeceasedSurnameWhenEmpty() {
        ocrFields.setSolsSolicitorAppReference("");
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultValues(ocrFields);

        assertEquals(1, modifiedFields.size());
        assertEquals("solsSolicitorAppReference", modifiedFields.get(0).getValue().getFieldName());
        assertEquals(VALID_DECEASED_SURNAME, ocrFields.getSolsSolicitorAppReference());
    }

    //Tests setting to existing postcode - can also set MISSING, see next test
    //TODO - Concrete impl
    /*
    @Test
    void should_AutoFill_SolsSolicitorAddressLine1_With_Postcode_When_Empty() {
        ocrFields.setSolsSolicitorAddressLine1("");
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultValues(ocrFields);

        assertEquals(1, modifiedFields.size());
        assertEquals("solsSolicitorAddressLine1", modifiedFields.get(0).getValue().getFieldName());
        assertEquals(DEFAULT_POSTCODE_VALUE, ocrFields.getSolsSolicitorAddressLine1());
    }
     */

    //Test setting to MISSING to when empty and no fillable data
    //TODO - Concrete impl
    /*
    @Test
    void should_AutoFill_SolsSolicitorAddressLine1_With_Missing_When_Empty_And_No_AutoFillable_Data_Exists() {
        ocrFields.setSolsSolicitorAddressLine1("");
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultValues(ocrFields);

        assertEquals(1, modifiedFields.size());
        assertEquals("solsSolicitorAddressLine1", modifiedFields.get(0).getValue().getFieldName());
        assertEquals("MISSING", ocrFields.getSolsSolicitorAddressLine1());
    }
     */

    //TODO - Find out N/A value?
    @Test
    void should_Not_AutoFill_SolsSolicitorAddressLine2_With_X_When_Empty_And_Address_Street_And_Postcode_Exist() {
        ocrFields.setSolsSolicitorAddressLine2("");
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultValues(ocrFields);

        assertEquals(1, modifiedFields.size());
        assertEquals("solsSolicitorAddressLine2", modifiedFields.get(0).getValue().getFieldName());
        assertEquals("", ocrFields.getSolsSolicitorAddressLine2());
    }

    //Expect to fail until get response from ops on what missing value should be
    //TODO
    /*
    @Test
    void should_AutoFill_SolsSolicitorAddressLine2_With_X_When_Empty_And_Address_Street_And_Postcode_Do_Not_Exist() {
        ocrFields.setSolsSolicitorAddressLine1("");
        ocrFields.setSolsSolicitorAddressLine2("");
        ocrFields.setSolsSolicitorAddressTown("");
        ocrFields.setSolsSolicitorAddressPostCode("");
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultValues(ocrFields);

        assertEquals(1, modifiedFields.size());
        assertEquals("solsSolicitorAddressLine2", modifiedFields.get(0).getValue().getFieldName());
        assertEquals("MISSING", ocrFields.getSolsSolicitorAddressLine2());
    }
    */

    //TODO
    /*
    @Test
    void should_Not_AutoFill_SolsSolicitorAddressTown_With_X_When_Empty_And_Address_Street_And_Postcode_Exist() {
        ocrFields.setSolsSolicitorAddressTown("");
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultValues(ocrFields);

        assertEquals(1, modifiedFields.size());
        assertEquals("solsSolicitorAddressTown", modifiedFields.get(0).getValue().getFieldName());
        assertEquals("", ocrFields.getSolsSolicitorAddressTown());
    }
     */

    //Expect to fail until get response from ops on what missing value should be
    //TODO
    /*
    @Test
    void should_AutoFill_SolsSolicitorAddressTown_With_X_When_Empty_And_Address_Street_And_Postcode_Do_Not_Exist() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .solsSolicitorAddressLine1("")
                .solsSolicitorAddressLine2("")
                .solsSolicitorAddressPostCode("")
                .build();
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultValues(ocrFields);

        assertEquals(1, modifiedFields.size());
        assertEquals("solsSolicitorAddressTown", modifiedFields.get(0).getValue().getFieldName());
        assertEquals("MISSING", ocrFields.getSolsSolicitorAddressTown());
    }
    */

    @Test
    void shouldSetPhoneTo1234WhenEmpty() {
        ocrFields.setSolsSolicitorPhoneNumber("");
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultValues(ocrFields);

        assertEquals(1, modifiedFields.size());
        assertEquals("solsSolicitorPhoneNumber", modifiedFields.get(0).getValue().getFieldName());
        assertEquals(DEFAULT_PHONE_VALUE, ocrFields.getSolsSolicitorPhoneNumber());
    }

    @Test
    void shouldSetDateTo01011900WhenEmpty() {
        ocrFields.setDeceasedDateOfBirth("");
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultValues(ocrFields);

        assertEquals(1, modifiedFields.size());
        assertEquals("deceasedDateOfBirth", modifiedFields.get(0).getValue().getFieldName());
        assertEquals(DEFAULT_DATE_OF_BIRTH, ocrFields.getDeceasedDateOfBirth());
    }

    @Test
    void shouldSetDeceasedAnyOtherNamesNoWhenEmpty() {
        ocrFields.setDeceasedAnyOtherNames("");
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultValues(ocrFields);

        assertEquals(1, modifiedFields.size());
        assertEquals("deceasedAnyOtherNames", modifiedFields.get(0).getValue().getFieldName());
        assertEquals(DEFAULT_DECEASED_ANY_OTHER_NAMES_VALUE, ocrFields.getDeceasedAnyOtherNames());
    }

    @Test
    void shouldSetDeceasedDomicileInEngWalesTrueWhenEmpty() {
        ocrFields.setDeceasedDomicileInEngWales("");
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultValues(ocrFields);

        assertEquals(1, modifiedFields.size());
        assertEquals("deceasedDomicileInEngWales", modifiedFields.get(0).getValue().getFieldName());
        assertEquals(DEFAULT_DECEASED_DOMICILE_IN_ENG_WALES_VALUE, ocrFields.getDeceasedDomicileInEngWales());
    }

    //IHT
    @Test
    void shouldSetDeceasedDiedOnAfterSwitchDateTrueWhenDeceasedDateOfDeathIsAfter() {
        ocrFields.setDeceasedDateOfDeath("01012022");
        ocrFields.setDeceasedDiedOnAfterSwitchDate("");

        ocrFields.setIhtEstateNetValue("1000");
        ocrFields.setIhtEstateNetQualifyingValue("1000");
        ocrFields.setIht400421Completed("FALSE");
        ocrFields.setIht207Completed("FALSE");
        ocrFields.setIht205Completed("FALSE");
        ocrFields.setIht400Completed("TRUE");
        ocrFields.setIht400process("TRUE");
        ocrFields.setProbateGrossValueIht400("1000");
        ocrFields.setProbateNetValueIht400("1000");

        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate("01012022")).thenReturn(true);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultValues(ocrFields);

        assertEquals(1, modifiedFields.size());
        assertEquals("deceasedDiedOnAfterSwitchDate", modifiedFields.get(0).getValue().getFieldName());
        assertEquals("TRUE", ocrFields.getDeceasedDiedOnAfterSwitchDate());
    }

    @Test
    void shouldSetDeceasedDiedOnAfterSwitchDateFalseWhenDeceasedDateOfDeathIsBefore() {
        ocrFields.setDeceasedDateOfDeath("01012020");
        ocrFields.setDeceasedDiedOnAfterSwitchDate("");

        ocrFields.setIht400421Completed("FALSE");
        ocrFields.setIht207Completed("FALSE");
        ocrFields.setIht205Completed("FALSE");
        ocrFields.setIht400Completed("FALSE");
        ocrFields.setIht400process("TRUE");
        ocrFields.setProbateGrossValueIht400("1000");
        ocrFields.setProbateNetValueIht400("1000");

        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate("01012020")).thenReturn(false);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultValues(ocrFields);

        assertEquals(1, modifiedFields.size());
        assertEquals("deceasedDiedOnAfterSwitchDate", modifiedFields.get(0).getValue().getFieldName());
        assertEquals("FALSE", ocrFields.getDeceasedDiedOnAfterSwitchDate());
    }

    @Test
    void shouldSetDefaultIHTFormWhenDeceasedDateOfDeathIsAfterAndFormVersionIsThree() throws IllegalAccessException {
        formVersionField.set(ocrFields, "3");

        ocrFields.setDeceasedDateOfDeath("01012022");

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
        assertEquals("3", ocrFields.getFormVersion());
    }

    @Test
    void shouldSetDefaultIHTFormWhenDeceasedDateOfDeathIsBeforeAndFormVersionIsThree() throws IllegalAccessException {
        formVersionField.set(ocrFields, "3");

        ocrFields.setDeceasedDateOfDeath("01012020");

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
        assertEquals("3", ocrFields.getFormVersion());
    }

    @Test
    void shouldSetDefaultIHTFormWhenDeceasedDateOfDeathIsAfterAndFormVersionIsTwo() throws IllegalAccessException {
        formVersionField.set(ocrFields, "3");

        ocrFields.setDeceasedDateOfDeath("01012022");

        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate("01012022")).thenReturn(true);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultValues(ocrFields);

        assertEquals("deceasedDiedOnAfterSwitchDate", modifiedFields.get(0).getValue().getFieldName());
        assertEquals("TRUE", ocrFields.getDeceasedDiedOnAfterSwitchDate());
        assertEquals("iht400421Completed", modifiedFields.get(1).getValue().getFieldName());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht400421Completed());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht207Completed());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht205Completed());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht205completedOnline());
        assertEquals("3", ocrFields.getFormVersion());
    }

    @Test
    void shouldSetDefaultIHTFormWhenDeceasedDateOfDeathIsBeforeAndFormVersionIsTwo() throws IllegalAccessException {
        formVersionField.set(ocrFields, "2");

        ocrFields.setDeceasedDateOfDeath("01012020");

        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate("01012020")).thenReturn(false);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultValues(ocrFields);

        assertEquals("deceasedDiedOnAfterSwitchDate", modifiedFields.get(0).getValue().getFieldName());
        assertEquals("FALSE", ocrFields.getDeceasedDiedOnAfterSwitchDate());
        assertEquals("iht400421Completed", modifiedFields.get(1).getValue().getFieldName());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht400421Completed());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht207Completed());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht205Completed());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht205completedOnline());
        assertEquals("2", ocrFields.getFormVersion());
    }

    @Test
    void shouldSetDefaultGrossNetValueWhenDeceasedDateOfDeathIsAfterAndFormVersionIsThreeAndIhtIs400421()
            throws IllegalAccessException {
        formVersionField.set(ocrFields, "3");

        ocrFields.setDeceasedDateOfDeath("01012022");
        ocrFields.setIht400421Completed("TRUE");

        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate("01012022")).thenReturn(true);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultValues(ocrFields);

        assertEquals("deceasedDiedOnAfterSwitchDate", modifiedFields.get(0).getValue().getFieldName());
        assertEquals("TRUE", ocrFields.getDeceasedDiedOnAfterSwitchDate());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht207Completed());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht205Completed());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht205completedOnline());
        assertEquals(DEFAULT_VALUE, ocrFields.getIht421grossValue());
        assertEquals(DEFAULT_VALUE, ocrFields.getIht421netValue());
        assertEquals("3", ocrFields.getFormVersion());
    }

    @Test
    void shouldSetDefaultGrossNetValueWhenDeceasedDateOfDeathIsAfterAndFormVersionIsTwoAndIhtIs400421()
            throws IllegalAccessException {
        formVersionField.set(ocrFields, "2");


        ocrFields.setDeceasedDateOfDeath("01012022");
        ocrFields.setIht400421Completed("TRUE");

        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate("01012022")).thenReturn(true);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultValues(ocrFields);

        assertEquals("deceasedDiedOnAfterSwitchDate", modifiedFields.get(0).getValue().getFieldName());
        assertEquals("TRUE", ocrFields.getDeceasedDiedOnAfterSwitchDate());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht207Completed());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht205Completed());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht205completedOnline());
        assertEquals(DEFAULT_VALUE, ocrFields.getIht421grossValue());
        assertEquals(DEFAULT_VALUE, ocrFields.getIht421netValue());
        assertEquals("2", ocrFields.getFormVersion());
    }

    @Test
    void shouldSetDefaultGrossNetValueWhenDeceasedDateOfDeathIsAfterAndFormVersionIsThreeAndIhtIs207()
            throws IllegalAccessException {
        formVersionField.set(ocrFields, "3");

        ocrFields.setDeceasedDateOfDeath("01012022");
        ocrFields.setIht207Completed("TRUE");

        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate("01012022")).thenReturn(true);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultValues(ocrFields);

        assertEquals("deceasedDiedOnAfterSwitchDate", modifiedFields.get(0).getValue().getFieldName());
        assertEquals("TRUE", ocrFields.getDeceasedDiedOnAfterSwitchDate());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht400421Completed());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht205Completed());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht205completedOnline());
        assertEquals(DEFAULT_VALUE, ocrFields.getIht207grossValue());
        assertEquals(DEFAULT_VALUE, ocrFields.getIht207netValue());
        assertEquals("3", ocrFields.getFormVersion());
    }

    @Test
    void shouldSetDefaultGrossNetValueWhenDeceasedDateOfDeathIsAfterAndFormVersionIsTwoAndIhtIs207()
            throws IllegalAccessException {
        formVersionField.set(ocrFields, "2");

        ocrFields.setDeceasedDateOfDeath("01012022");
        ocrFields.setIht207Completed("TRUE");

        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate("01012022")).thenReturn(true);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultValues(ocrFields);

        assertEquals("deceasedDiedOnAfterSwitchDate", modifiedFields.get(0).getValue().getFieldName());
        assertEquals("TRUE", ocrFields.getDeceasedDiedOnAfterSwitchDate());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht400421Completed());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht205Completed());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht205completedOnline());
        assertEquals(DEFAULT_VALUE, ocrFields.getIht207grossValue());
        assertEquals(DEFAULT_VALUE, ocrFields.getIht207netValue());
        assertEquals("2", ocrFields.getFormVersion());
    }

    @Test
    void shouldSetDefaultGrossNetValueWhenDeceasedDateOfDeathIsBeforeAndFormVersionIsThreeAndIhtIs205()
            throws IllegalAccessException {
        formVersionField.set(ocrFields, "3");

        ocrFields.setDeceasedDateOfDeath("01012020");
        ocrFields.setIht205Completed("TRUE");

        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate("01012020")).thenReturn(false);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultValues(ocrFields);

        assertEquals("deceasedDiedOnAfterSwitchDate", modifiedFields.get(0).getValue().getFieldName());
        assertEquals("FALSE", ocrFields.getDeceasedDiedOnAfterSwitchDate());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht400421Completed());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht207Completed());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht205completedOnline());
        assertEquals(DEFAULT_VALUE, ocrFields.getIhtGrossValue205());
        assertEquals(DEFAULT_VALUE, ocrFields.getIhtNetValue205());
        assertEquals("3", ocrFields.getFormVersion());
    }

    @Test
    void shouldSetDefaultGrossNetValueWhenDeceasedDateOfDeathIsBeforeAndFormVersionIsTwoAndIhtIs205Online()
            throws IllegalAccessException {
        formVersionField.set(ocrFields, "2");

        ocrFields.setDeceasedDateOfDeath("01012020");
        ocrFields.setIht205completedOnline("TRUE");

        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate("01012020")).thenReturn(false);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultValues(ocrFields);

        assertEquals("deceasedDiedOnAfterSwitchDate", modifiedFields.get(0).getValue().getFieldName());
        assertEquals("FALSE", ocrFields.getDeceasedDiedOnAfterSwitchDate());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht400421Completed());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht207Completed());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht205Completed());
        assertEquals(DEFAULT_VALUE, ocrFields.getIhtGrossValue205());
        assertEquals(DEFAULT_VALUE, ocrFields.getIhtNetValue205());
        assertEquals("1234", ocrFields.getIhtReferenceNumber());
        assertEquals("2", ocrFields.getFormVersion());
    }

    @Test
    void shouldSetDefaultEstateGrossNetValueWhenDeceasedDateOfDeathIsBeforeAndFormVersionIsThree()
            throws IllegalAccessException, NoSuchFieldException {
        formVersionField.set(ocrFields, "3");

        Field exceptedEstateField = ExceptionRecordOCRFields.class.getDeclaredField("exceptedEstate");
        exceptedEstateField.setAccessible(true);
        exceptedEstateField.set(ocrFields, "TRUE");

        ocrFields.setDeceasedDateOfDeath("01012020");
        ocrFields.setDeceasedDiedOnAfterSwitchDate("");

        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate("01012020")).thenReturn(false);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultValues(ocrFields);

        assertEquals("deceasedDiedOnAfterSwitchDate", modifiedFields.get(0).getValue().getFieldName());
        assertEquals("FALSE", ocrFields.getDeceasedDiedOnAfterSwitchDate());
        assertEquals(DEFAULT_VALUE, ocrFields.getIhtEstateGrossValue());
        assertEquals(DEFAULT_VALUE, ocrFields.getIhtEstateNetValue());
        assertEquals(DEFAULT_VALUE, ocrFields.getIhtEstateNetQualifyingValue());
        assertEquals("3", ocrFields.getFormVersion());
    }

    @Test
    void shouldSetDefaultEstateGrossNetValueWhenDeceasedDateOfDeathIsAfterAndFormVersionIsTwo()
            throws IllegalAccessException {
        formVersionField.set(ocrFields, "2");
        ocrFields.setDeceasedDateOfDeath("01012022");

        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate("01012022")).thenReturn(true);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultValues(ocrFields);

        assertEquals("deceasedDiedOnAfterSwitchDate", modifiedFields.get(0).getValue().getFieldName());
        assertEquals("TRUE", ocrFields.getDeceasedDiedOnAfterSwitchDate());
        assertEquals(DEFAULT_VALUE, ocrFields.getIhtEstateGrossValue());
        assertEquals(DEFAULT_VALUE, ocrFields.getIhtEstateNetValue());
        assertEquals(DEFAULT_VALUE, ocrFields.getIhtEstateNetQualifyingValue());
        assertEquals("2", ocrFields.getFormVersion());
    }

    @Test
    void shouldReturnWarningWhenMoreThanOneIHTFormIsTrue() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .exceptedEstate("TRUE")
                .iht400Completed("TRUE")
                .iht400process("FALSE")
                .iht400421Completed("FALSE")
                .iht207Completed("FALSE")
                .iht205Completed("FALSE")
                .build();

        List<CollectionMember<String>> warnings = ocrFieldModifierUtils.checkWarnings(ocrFields);

        assertEquals(1, warnings.size());
        assertEquals("More than one IHT form is marked as TRUE. Only one form should be selected as TRUE.",
                warnings.get(0).getValue());
    }

    @Test
    void shouldReturnNoWarningWhenOnlyOneIHTFormIsTrue() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .exceptedEstate("TRUE")
                .iht400Completed("FALSE")
                .iht400process("FALSE")
                .iht400421Completed("FALSE")
                .iht207Completed("FALSE")
                .iht205Completed("FALSE")
                .build();

        List<CollectionMember<String>> warnings = ocrFieldModifierUtils.checkWarnings(ocrFields);

        assertEquals(0, warnings.size());
    }
}
