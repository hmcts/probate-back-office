package uk.gov.hmcts.probate.service.ocr;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.config.BulkScanConfig;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.probate.service.EmailValidationService;
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

    @Mock
    private EmailValidationService emailValidationService;

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

    //Dynamic
    private static final String DEFAULT_SOLS_SOLICITOR_IS_APPLYING_VALUE = "TRUE"; //if sols details exist
    private static final String DEFAULT_SOLS_SOLICITOR_REPRESENTATIVE_NAME_VALUE = "Firm Name"; //if sols name empty
    private static final String DEFAULT_SOLS_APP_REFERENCE_DOB_VALUE = ""; //VALID_DECEASED_SURNAME_VALUE

    //Potential Dynamic Fields
    private static final String VALID_FIRM_NAME = "Bob Firm";
    private static final String VALID_DECEASED_SURNAME = "Mak Gee";
    private static final String VALID_POSTCODE = "SW1A 1AA";


    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        ocrFieldModifierUtils = new OCRFieldModifierUtils(bulkScanConfig, exceptedEstateDateOfDeathChecker, emailValidationService);

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
    }

    //Simple dummy value tests
    @Test
    void shouldSetApplicantForenameToMissingWhenEmpty() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder().primaryApplicantForenames("").build();
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultValues(ocrFields);

        //assertEquals(1, modifiedFields.size());
        assertEquals("primaryApplicantForenames", modifiedFields.get(0).getValue().getFieldName());
        assertEquals("MISSING", ocrFields.getPrimaryApplicantForenames());
    }

    @Test
    void shouldSetPostcodeToM1551NGWhenEmpty() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder().primaryApplicantAddressPostCode("").build();
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultValues(ocrFields);

        //assertEquals(1, modifiedFields.size());
        assertEquals("primaryApplicantAddressPostCode", modifiedFields.get(3).getValue().getFieldName());
        assertEquals("MI55 1NG", ocrFields.getPrimaryApplicantAddressPostCode());
    }

    //Dynamic assignment
    @Test
    void shouldSetSolsSolicitorIsApplyingToFalseWhenFalse() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder().solsSolicitorIsApplying("FALSE").build();
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultValues(ocrFields);

        //assertEquals(1, modifiedFields.size());
        //assertEquals("solsSolicitorIsApplying", modifiedFields.get(3).getValue().getFieldName());
        assertEquals("FALSE", ocrFields.getSolsSolicitorIsApplying());
    }

    @Test
    void shouldSetSolsSolicitorIsApplyingToTrueWhenEmpty() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .solsSolicitorIsApplying("")
                .solsSolicitorRepresentativeName("Bob")
                .solsSolicitorEmail("boblawfirm@gmail.com")
                .solsSolicitorFirmName("Bob Law Partners Thing")
                .build();
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultValues(ocrFields);

        //assertEquals(1, modifiedFields.size());
        //assertEquals("solsSolicitorIsApplying", modifiedFields.get(3).getValue().getFieldName());
        assertEquals("TRUE", ocrFields.getSolsSolicitorIsApplying());
    }

    @Test
    void shouldSetSolsSolicitorRepresentativeNameToFirmNameWhenEmpty() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .solsSolicitorFirmName(VALID_FIRM_NAME)
                .solsSolicitorRepresentativeName("")
                .build();
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultValues(ocrFields);

        //assertEquals(1, modifiedFields.size());
        //assertEquals("solsSolicitorIsApplying", modifiedFields.get(3).getValue().getFieldName());
        assertEquals(VALID_FIRM_NAME, ocrFields.getSolsSolicitorRepresentativeName());
    }

    @Test
    void shouldSetSolsSolicitorAppReferenceToDeceasedSurnameWhenEmpty() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .solsSolicitorAppReference("")
                .deceasedSurname(VALID_DECEASED_SURNAME)
                .build();
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultValues(ocrFields);

        //assertEquals(1, modifiedFields.size());
        //assertEquals("solsSolicitorIsApplying", modifiedFields.get(3).getValue().getFieldName());
        assertEquals(VALID_DECEASED_SURNAME, ocrFields.getSolsSolicitorAppReference());
    }

    //Tests setting to existing postcode - can also set MISSING, see next test
    @Test
    void should_AutoFill_SolsSolicitorAddressLine1_With_Postcode_When_Empty() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .solsSolicitorAddressLine1("")
                .solsSolicitorAddressPostCode(VALID_POSTCODE)
                .build();
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultValues(ocrFields);

        //assertEquals(1, modifiedFields.size());
        //assertEquals("solsSolicitorIsApplying", modifiedFields.get(3).getValue().getFieldName());
        assertEquals(VALID_POSTCODE, ocrFields.getSolsSolicitorAddressLine1());
    }

    //Test setting to MISSING to when empty and no fillable data
    @Test
    void should_AutoFill_SolsSolicitorAddressLine1_With_MISSING_When_Empty_And_No_AutoFillable_Data_Exists() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .solsSolicitorAddressLine1("")
                .deceasedSurname("MISSING")
                .build();
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultValues(ocrFields);

        //assertEquals(1, modifiedFields.size());
        //assertEquals("solsSolicitorIsApplying", modifiedFields.get(3).getValue().getFieldName());
        assertEquals("MISSING", ocrFields.getSolsSolicitorAppReference());
    }

    @Test
    void should_Not_AutoFill_SolsSolicitorAddressLine2_With_SOMETHING_When_Empty_And_Address_Street_And_Postcode_Exist() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .solsSolicitorAddressLine1("123 Fake Street")
                .solsSolicitorAddressLine2("")
                .solsSolicitorAddressPostCode(VALID_POSTCODE)
                .build();
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultValues(ocrFields);

        //assertEquals(1, modifiedFields.size());
        //assertEquals("solsSolicitorIsApplying", modifiedFields.get(3).getValue().getFieldName());
        assertEquals("", ocrFields.getSolsSolicitorAddressLine2());
    }

    //Expect to fail until get response from ops on what missing value should be
    @Test
    void should_AutoFill_SolsSolicitorAddressLine2_With_SOMETHING_When_Empty_And_Address_Street_And_Postcode_Do_Not_Exist() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .solsSolicitorAddressLine1("")
                .solsSolicitorAddressLine2("")
                .solsSolicitorAddressTown("")
                .solsSolicitorAddressPostCode("")
                .build();
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultValues(ocrFields);

        //assertEquals(1, modifiedFields.size());
        //assertEquals("solsSolicitorIsApplying", modifiedFields.get(3).getValue().getFieldName());
        assertEquals("MISSING", ocrFields.getSolsSolicitorAddressLine2());
    }

    @Test
    void should_Not_AutoFill_SolsSolicitorAddressTown_With_SOMETHING_When_Empty_And_Address_Street_And_Postcode_Exist() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .solsSolicitorAddressLine1("123 Fake Street")
                .solsSolicitorAddressLine2("123 Fake Place")
                .solsSolicitorAddressTown("Fakesville")
                .solsSolicitorAddressPostCode(VALID_POSTCODE)
                .build();
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultValues(ocrFields);

        //assertEquals(1, modifiedFields.size());
        //assertEquals("solsSolicitorIsApplying", modifiedFields.get(3).getValue().getFieldName());
        assertEquals("", ocrFields.getSolsSolicitorAddressTown());
    }

    //Expect to fail until get response from ops on what missing value should be
    @Test
    void should_AutoFill_SolsSolicitorAddressTown_With_SOMETHING_When_Empty_And_Address_Street_And_Postcode_Do_Not_Exist() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .solsSolicitorAddressLine1("")
                .solsSolicitorAddressLine2("")
                .solsSolicitorAddressPostCode("")
                .build();
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultValues(ocrFields);

        //assertEquals(1, modifiedFields.size());
        //assertEquals("solsSolicitorIsApplying", modifiedFields.get(3).getValue().getFieldName());
        assertEquals("MISSING", ocrFields.getSolsSolicitorAddressTown());
    }

    @Test
    void shouldSetPhoneTo1234WhenEmpty() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .solsSolicitorPhoneNumber("")
                .build();
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultValues(ocrFields);

        //assertEquals(1, modifiedFields.size());
        //assertEquals("solsSolicitorIsApplying", modifiedFields.get(3).getValue().getFieldName());
        assertEquals(DEFAULT_PHONE_VALUE, ocrFields.getSolsSolicitorPhoneNumber());
    }

    @Test
    void shouldSetDateTo01011900WhenEmpty() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .deceasedDateOfBirth("")
                .build();
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultValues(ocrFields);

        //assertEquals(1, modifiedFields.size());
        //assertEquals("solsSolicitorIsApplying", modifiedFields.get(3).getValue().getFieldName());
        assertEquals(DEFAULT_DATE_OF_BIRTH, ocrFields.getDeceasedDateOfBirth());
    }

    @Test
    void shouldSetDeceasedAnyOtherNamesNoWhenEmpty() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .deceasedAnyOtherNames("")
                .build();
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultValues(ocrFields);

        //assertEquals(1, modifiedFields.size());
        //assertEquals("solsSolicitorIsApplying", modifiedFields.get(3).getValue().getFieldName());
        assertEquals(DEFAULT_DECEASED_ANY_OTHER_NAMES_VALUE, ocrFields.getDeceasedAnyOtherNames());
    }


    @Test
    void shouldSetDeceasedDomicileInEngWalesTrueWhenEmpty() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .deceasedDomicileInEngWales("")
                .build();
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultValues(ocrFields);

        //assertEquals(1, modifiedFields.size());
        //assertEquals("solsSolicitorIsApplying", modifiedFields.get(3).getValue().getFieldName());
        assertEquals(DEFAULT_DECEASED_DOMICILE_IN_ENG_WALES_VALUE, ocrFields.getDeceasedDomicileInEngWales());
    }

    //IHT
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
    void shouldSetDefaultGrossNetValueWhenDeceasedDateOfDeathIsBeforeAndFormVersionIsThreeAndIhtIs205() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder().deceasedDateOfDeath("01012020")
                .formVersion("3").iht205Completed("TRUE").build();

        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate("01012020")).thenReturn(false);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultValues(ocrFields);

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

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultValues(ocrFields);

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

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultValues(ocrFields);

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

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultValues(ocrFields);

        assertEquals("deceasedDiedOnAfterSwitchDate", modifiedFields.get(0).getValue().getFieldName());
        assertEquals("TRUE", ocrFields.getDeceasedDiedOnAfterSwitchDate());
        assertEquals(DEFAULT_VALUE, ocrFields.getIhtEstateGrossValue());
        assertEquals(DEFAULT_VALUE, ocrFields.getIhtEstateNetValue());
        assertEquals(DEFAULT_VALUE, ocrFields.getIhtEstateNetQualifyingValue());
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
