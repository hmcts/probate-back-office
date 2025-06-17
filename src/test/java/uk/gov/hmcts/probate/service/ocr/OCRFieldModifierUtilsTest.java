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
import static uk.gov.hmcts.probate.model.DummyValuesConstants.CAVEATOR_ADDRESS_LINE1;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.CAVEAT_FORENAMES;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.CAVEAT_SURNAME;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.CAVEATOR_POST_CODE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.DECEASED_ANY_OTHER_NAMES;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.DECEASED_DOB;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.DECEASED_DOD;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.DECEASED_DIED_ON_OR_AFTER_SWITCH_DATE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.DECEASED_DOMICILE_IN_ENG_WALES;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.DECEASED_FORENAME;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.DECEASED_SURNAME;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.EXECUTORS_APPLYING_0_ADDRESS_LINE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.EXECUTORS_APPLYING_1_ADDRESS_LINE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.EXECUTORS_APPLYING_2_ADDRESS_LINE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.EXECUTORS_APPLYING_0_ADDRESS_POST_CODE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.EXECUTORS_APPLYING_0_ADDRESS_TOWN;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.EXECUTOR_NOT_APPLYING_0_REASON;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.EXECUTOR_NOT_APPLYING_1_REASON;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.EXECUTOR_NOT_APPLYING_2_REASON;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.IHT_400421;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.IHT_FORM_COMPLETED_ONLINE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.IHT_GROSS_VALUE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.IHT_NET_VALUE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.IHT_REFERENCE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.NOTIFIED_APPLICANTS;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.PRIMARY_APPLICANT_ADDRESS_POST_CODE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.PRIMARY_APPLICANT_FORENAMES;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.SOLICITOR_ADDRESS_LINE1;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.SOLICITOR_ADDRESS_POST_CODE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.SOLICITOR_APP_REFERENCE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.SOLICITOR_FIRM_NAME;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.SOLICITOR_IS_APPLYING;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.SOLICITOR_PHONE_NUMBER;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.SOLICITOR_REPRESENTATIVE_NAME;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.SPOUSE_OR_PARTNER;

class OCRFieldModifierUtilsTest {
    @InjectMocks
    private OCRFieldModifierUtils ocrFieldModifierUtils;
    @Mock
    private BulkScanConfig bulkScanConfig;

    @Mock
    private ExceptedEstateDateOfDeathChecker exceptedEstateDateOfDeathChecker;

    private ExceptionRecordOCRFields ocrFields;

    private Field formVersionField;
    private Field applyingExecutorName0;
    private Field applyingExecutorName1;
    private Field applyingExecutorName2;
    private Field notApplyingExecutorName0;
    private Field notApplyingExecutorName1;
    private Field notApplyingExecutorName2;

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
    private static final String DEFAULT_ALIAS_VALUE = "FALSE";

    //Valid
    private static final String VALID_PRIMARY_APPLICANT_FORENAMES = "First Name";
    private static final String VALID_PRIMARY_APPLICANT_SURNAME = "Surname";
    private static final String VALID_PRIMARY_APPLICANT_ADDRESS_LINE_1 = "123 Fake Street";
    private static final String VALID_PRIMARY_APPLICANT_ADDRESS_POSTCODE = "SW1A 1AA";
    private static final String VALID_PRIMARY_APPLICANT_ALIAS = "Alias Name";
    private static final String VALID_PRIMARY_APPLICANT_HAS_ALIAS = "TRUE";

    private static final String VALID_SOLICITOR_IS_APPLYING = "TRUE";
    private static final String VALID_SOLICITOR_FIRM_NAME = "Firm Name";
    private static final String VALID_SOLICITOR_REPRESENTATIVE_NAME = "Representative Name";
    private static final String VALID_SOLICITOR_APP_REFERENCE = "App Reference";
    private static final String VALID_SOLICITOR_ADDRESS_LINE_1 = "321 Sols Street";
    private static final String VALID_SOLICITOR_ADDRESS_LINE_2 = "321 Fake Place";
    private static final String VALID_SOLICITOR_ADDRESS_TOWN = "MISSING";
    private static final String VALID_SOLICITOR_ADDRESS_POSTCODE = "SW1A 1AA";
    private static final String VALID_SOLICITOR_EMAIL = "rog@gmail.com";
    private static final String VALID_SOLICITOR_PHONE_NUMBER = "07123456789";

    private static final String VALID_DECEASED_FORENAMES = "Deceased Forenames";
    private static final String VALID_DECEASED_SURNAME = "Deceased Surname";
    private static final String VALID_DECEASED_ADDRESS_LINE_1 = "101 Fake Street";
    private static final String VALID_DECEASED_ADDRESS_POSTCODE = "SW1A 1AA";
    private static final String VALID_DECEASED_DATE_OF_BIRTH = "01-01-1990";
    private static final String VALID_DECEASED_ANY_OTHER_NAMES = "TRUE";
    private static final String VALID_DECEASED_DOMICILED_IN_ENG_WALES = "TRUE";

    private static final String VALID_CAVEATOR_FORENAMES = "Bob";
    private static final String VALID_CAVEATOR_SURNAME = "Builder";
    private static final String VALID_CAVEATOR_ADDRESS_LINE_1 = "987 Street";
    private static final String VALID_CAVEATOR_ADDRESS_POSTCODE = "SW1A 1AA";

    private static final String DEFAULT_FORM = "IHT400";


    @BeforeEach
     void setup() throws Exception {
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
        when(bulkScanConfig.getPrimaryApplicantHasAlias()).thenReturn(DEFAULT_ALIAS_VALUE);
        when(bulkScanConfig.getDeceasedDiedOnOrAfterSwitchDateTrue()).thenReturn(TRUE);
        when(bulkScanConfig.getDeceasedDiedOnOrAfterSwitchDateFalse()).thenReturn(FALSE);
        when(bulkScanConfig.getDateOfDeathForDiedOnOrAfterSwitchDateTrue()).thenReturn("01012022");
        when(bulkScanConfig.getDateOfDeathForDiedOnOrAfterSwitchDateFalse()).thenReturn("01011990");
        when(bulkScanConfig.getExecutorsNotApplyingReason()).thenReturn("A");
        when(bulkScanConfig.getSolicitorApplying()).thenReturn(FALSE);
        when(bulkScanConfig.getDefaultForm()).thenReturn(DEFAULT_FORM);

        Field bulkScanConfigField = OCRFieldModifierUtils.class.getDeclaredField("bulkScanConfig");
        bulkScanConfigField.setAccessible(true);
        bulkScanConfigField.set(ocrFieldModifierUtils, bulkScanConfig);

        formVersionField = ExceptionRecordOCRFields.class.getDeclaredField("formVersion");
        formVersionField.setAccessible(true);

        applyingExecutorName0 = ExceptionRecordOCRFields.class
                .getDeclaredField("executorsApplying0applyingExecutorName");
        applyingExecutorName0.setAccessible(true);

        applyingExecutorName1 = ExceptionRecordOCRFields.class
                .getDeclaredField("executorsApplying1applyingExecutorName");
        applyingExecutorName1.setAccessible(true);

        applyingExecutorName2 = ExceptionRecordOCRFields.class
                .getDeclaredField("executorsApplying2applyingExecutorName");
        applyingExecutorName2.setAccessible(true);

        notApplyingExecutorName0 = ExceptionRecordOCRFields.class
                .getDeclaredField("executorsNotApplying0notApplyingExecutorName");
        notApplyingExecutorName0.setAccessible(true);

        notApplyingExecutorName1 = ExceptionRecordOCRFields.class
                .getDeclaredField("executorsNotApplying1notApplyingExecutorName");
        notApplyingExecutorName1.setAccessible(true);

        notApplyingExecutorName2 = ExceptionRecordOCRFields.class
                .getDeclaredField("executorsNotApplying2notApplyingExecutorName");
        notApplyingExecutorName2.setAccessible(true);

        ocrFields = ExceptionRecordOCRFields.builder()
                .primaryApplicantForenames(VALID_PRIMARY_APPLICANT_FORENAMES)
                .primaryApplicantSurname(VALID_PRIMARY_APPLICANT_SURNAME)
                .primaryApplicantAddressLine1(VALID_PRIMARY_APPLICANT_ADDRESS_LINE_1)
                .primaryApplicantAddressPostCode(VALID_PRIMARY_APPLICANT_ADDRESS_POSTCODE)
                .primaryApplicantAlias(VALID_PRIMARY_APPLICANT_ALIAS)
                .primaryApplicantHasAlias(VALID_PRIMARY_APPLICANT_HAS_ALIAS)
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
                .deceasedForenames(VALID_DECEASED_FORENAMES)
                .deceasedSurname(VALID_DECEASED_SURNAME)
                .deceasedAddressLine1(VALID_DECEASED_ADDRESS_LINE_1)
                .deceasedAddressPostCode(VALID_DECEASED_ADDRESS_POSTCODE)
                .deceasedDateOfBirth(VALID_DECEASED_DATE_OF_BIRTH)
                .deceasedAnyOtherNames(VALID_DECEASED_ANY_OTHER_NAMES)
                .deceasedDomicileInEngWales(VALID_DECEASED_DOMICILED_IN_ENG_WALES)
                .caveatorForenames(VALID_CAVEATOR_FORENAMES)
                .caveatorSurnames(VALID_CAVEATOR_SURNAME)
                .caveatorAddressLine1(VALID_CAVEATOR_ADDRESS_LINE_1)
                .caveatorAddressPostCode(VALID_CAVEATOR_ADDRESS_POSTCODE)
                .deceasedDateOfDeath("01012022")
                .deceasedDiedOnAfterSwitchDate(TRUE)
                .spouseOrPartner(TRUE)
                .notifiedApplicants(TRUE)
                .build();
    }

    @Test
    void shouldSetApplicantForenameToMissingWhenEmpty() {
        ocrFields.setSolsSolicitorIsApplying(FALSE);
        ocrFields.setPrimaryApplicantForenames("");

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultGorValues(ocrFields);

        assertEquals(1, modifiedFields.size());
        assertEquals(PRIMARY_APPLICANT_FORENAMES, modifiedFields.getFirst().getValue().getFieldName());
        assertEquals(bulkScanConfig.getName(), ocrFields.getPrimaryApplicantForenames());
    }

    @Test
    void shouldSetApplicantForenameToMissingWhenSolicitorIsApplying() {
        ocrFields.setSolsSolicitorIsApplying(TRUE);
        ocrFields.setPrimaryApplicantForenames("");

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultGorValues(ocrFields);

        assertEquals(1, modifiedFields.size());
    }

    @Test
    void shouldSetPostcodeToM1551NGWhenEmpty() {
        ocrFields.setSolsSolicitorIsApplying(FALSE);
        ocrFields.setPrimaryApplicantAddressPostCode("");
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultGorValues(ocrFields);

        for (CollectionMember<ModifiedOCRField> modifiedField : modifiedFields) {
            System.out.println(modifiedField.getValue().getFieldName());
        }
        assertEquals(1, modifiedFields.size());
        assertEquals(PRIMARY_APPLICANT_ADDRESS_POST_CODE, modifiedFields.getFirst().getValue().getFieldName());
        assertEquals(DEFAULT_POSTCODE_VALUE, ocrFields.getPrimaryApplicantAddressPostCode());
    }

    @Test
    void shouldSetPostcodeToM1551NGWhenSolicitorIsApplying() {
        ocrFields.setSolsSolicitorIsApplying(TRUE);
        ocrFields.setPrimaryApplicantAddressPostCode("");
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultGorValues(ocrFields);

        for (CollectionMember<ModifiedOCRField> modifiedField : modifiedFields) {
            System.out.println(modifiedField.getValue().getFieldName());
        }
        assertEquals(1, modifiedFields.size());
    }

    @Test
    void shouldSetSolsSolicitorRepresentativeNameToFirmNameWhenEmpty() {
        ocrFields.setSolsSolicitorRepresentativeName("");
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultGorValues(ocrFields);

        assertEquals(1, modifiedFields.size());
        assertEquals(SOLICITOR_REPRESENTATIVE_NAME, modifiedFields.getFirst().getValue().getFieldName());
        assertEquals(VALID_SOLICITOR_FIRM_NAME, ocrFields.getSolsSolicitorRepresentativeName());
    }

    @Test
    void shouldSetSolsSolicitorAppReferenceToDeceasedSurnameWhenEmpty() {
        ocrFields.setSolsSolicitorAppReference("");
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultGorValues(ocrFields);

        assertEquals(1, modifiedFields.size());
        assertEquals(SOLICITOR_APP_REFERENCE, modifiedFields.getFirst().getValue().getFieldName());
        assertEquals(VALID_DECEASED_SURNAME, ocrFields.getSolsSolicitorAppReference());
    }

    @Test
    void should_AutoFill_SolsSolicitorAddressLine1_With_Missing_When_Empty() {
        ocrFields.setSolsSolicitorAddressLine1("");
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultGorValues(ocrFields);

        assertEquals(1, modifiedFields.size());
        assertEquals(SOLICITOR_ADDRESS_LINE1, modifiedFields.getFirst().getValue().getFieldName());
        assertEquals(bulkScanConfig.getName(), ocrFields.getSolsSolicitorAddressLine1());
    }

    @Test
    void should_Not_AutoFill_SolsSolicitorAddressLine2_When_Empty_And_Address_Street_And_Postcode_Exist() {
        ocrFields.setSolsSolicitorAddressLine2("");
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultGorValues(ocrFields);

        assertEquals(0, modifiedFields.size());
        assertEquals("", ocrFields.getSolsSolicitorAddressLine2());
    }

    @Test
    void should_Not_AutoFill_SolsSolicitorAddressTown_When_Empty_And_Address_Street_And_Postcode_Exist() {
        ocrFields.setSolsSolicitorAddressTown("");
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultGorValues(ocrFields);

        assertEquals(0, modifiedFields.size());
        assertEquals("", ocrFields.getSolsSolicitorAddressTown());
    }

    @Test
    void shouldSetPhoneTo1234WhenEmpty() {
        ocrFields.setSolsSolicitorPhoneNumber("");
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultGorValues(ocrFields);

        assertEquals(1, modifiedFields.size());
        assertEquals(SOLICITOR_PHONE_NUMBER, modifiedFields.getFirst().getValue().getFieldName());
        assertEquals(DEFAULT_PHONE_VALUE, ocrFields.getSolsSolicitorPhoneNumber());
    }

    @Test
    void shouldSetDateTo01011900WhenEmpty() {
        ocrFields.setDeceasedDateOfBirth("");
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultGorValues(ocrFields);

        assertEquals(1, modifiedFields.size());
        assertEquals(DECEASED_DOB, modifiedFields.getFirst().getValue().getFieldName());
        assertEquals(DEFAULT_DATE_OF_BIRTH, ocrFields.getDeceasedDateOfBirth());
    }

    @Test
    void shouldSetDeceasedAnyOtherNamesNoWhenEmpty() {
        ocrFields.setDeceasedAnyOtherNames("");
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultGorValues(ocrFields);

        assertEquals(1, modifiedFields.size());
        assertEquals(DECEASED_ANY_OTHER_NAMES, modifiedFields.getFirst().getValue().getFieldName());
        assertEquals(DEFAULT_DECEASED_ANY_OTHER_NAMES_VALUE, ocrFields.getDeceasedAnyOtherNames());
    }

    @Test
    void shouldSetDeceasedDomicileInEngWalesTrueWhenEmpty() {
        ocrFields.setDeceasedDomicileInEngWales("");
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultGorValues(ocrFields);

        assertEquals(1, modifiedFields.size());
        assertEquals(DECEASED_DOMICILE_IN_ENG_WALES, modifiedFields.getFirst().getValue().getFieldName());
        assertEquals(DEFAULT_DECEASED_DOMICILE_IN_ENG_WALES_VALUE, ocrFields.getDeceasedDomicileInEngWales());
    }

    //IHT
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

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultGorValues(ocrFields);

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

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultGorValues(ocrFields);

        assertEquals(1, modifiedFields.size());
        assertEquals(DECEASED_DIED_ON_OR_AFTER_SWITCH_DATE, modifiedFields.getFirst().getValue().getFieldName());
        assertEquals(FALSE, ocrFields.getDeceasedDiedOnAfterSwitchDate());
    }

    @Test
    void shouldSetDefaultIHTFormWhenDeceasedDateOfDeathIsAfterAndFormVersionIsThree() throws IllegalAccessException {
        formVersionField.set(ocrFields, "3");

        ocrFields.setDeceasedDateOfDeath("01012022");
        ocrFields.setDeceasedDiedOnAfterSwitchDate("");

        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate("01012022")).thenReturn(true);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultGorValues(ocrFields);

        assertEquals(DECEASED_DIED_ON_OR_AFTER_SWITCH_DATE, modifiedFields.get(0).getValue().getFieldName());
        assertEquals(TRUE, ocrFields.getDeceasedDiedOnAfterSwitchDate());
        assertEquals(IHT_400421, modifiedFields.get(1).getValue().getFieldName());
        assertEquals(TRUE, ocrFields.getIht400Completed());
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
        ocrFields.setDeceasedDiedOnAfterSwitchDate("");

        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate("01012020")).thenReturn(false);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultGorValues(ocrFields);

        assertEquals(DECEASED_DIED_ON_OR_AFTER_SWITCH_DATE, modifiedFields.get(0).getValue().getFieldName());
        assertEquals(FALSE, ocrFields.getDeceasedDiedOnAfterSwitchDate());
        assertEquals(IHT_400421, modifiedFields.get(1).getValue().getFieldName());
        assertEquals(TRUE, ocrFields.getIht400Completed());
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
        ocrFields.setDeceasedDiedOnAfterSwitchDate("");

        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate("01012022")).thenReturn(true);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultGorValues(ocrFields);

        assertEquals(DECEASED_DIED_ON_OR_AFTER_SWITCH_DATE, modifiedFields.get(0).getValue().getFieldName());
        assertEquals(TRUE, ocrFields.getDeceasedDiedOnAfterSwitchDate());
        assertEquals(IHT_400421, modifiedFields.get(1).getValue().getFieldName());
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
        ocrFields.setDeceasedDiedOnAfterSwitchDate("");

        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate("01012020")).thenReturn(false);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultGorValues(ocrFields);

        assertEquals(DECEASED_DIED_ON_OR_AFTER_SWITCH_DATE, modifiedFields.get(0).getValue().getFieldName());
        assertEquals(FALSE, ocrFields.getDeceasedDiedOnAfterSwitchDate());
        assertEquals(IHT_400421, modifiedFields.get(1).getValue().getFieldName());
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
        ocrFields.setDeceasedDiedOnAfterSwitchDate("");
        ocrFields.setIht400421Completed(TRUE);

        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate("01012022")).thenReturn(true);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultGorValues(ocrFields);

        assertEquals(DECEASED_DIED_ON_OR_AFTER_SWITCH_DATE, modifiedFields.getFirst().getValue().getFieldName());
        assertEquals(TRUE, ocrFields.getDeceasedDiedOnAfterSwitchDate());
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
        ocrFields.setDeceasedDiedOnAfterSwitchDate("");
        ocrFields.setIht400421Completed(TRUE);

        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate("01012022")).thenReturn(true);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultGorValues(ocrFields);

        assertEquals(DECEASED_DIED_ON_OR_AFTER_SWITCH_DATE, modifiedFields.getFirst().getValue().getFieldName());
        assertEquals(TRUE, ocrFields.getDeceasedDiedOnAfterSwitchDate());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht207Completed());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht205Completed());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht205completedOnline());
        assertEquals(DEFAULT_VALUE, ocrFields.getIhtGrossValue());
        assertEquals(DEFAULT_VALUE, ocrFields.getIhtNetValue());
        assertEquals("2", ocrFields.getFormVersion());
    }

    @Test
    void shouldSetDefaultGrossNetValueWhenDeceasedDateOfDeathIsAfterAndFormVersionIsThreeAndIhtIs207()
            throws IllegalAccessException {
        formVersionField.set(ocrFields, "3");

        ocrFields.setDeceasedDateOfDeath("01012022");
        ocrFields.setDeceasedDiedOnAfterSwitchDate("");
        ocrFields.setIht207Completed(TRUE);

        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate("01012022")).thenReturn(true);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultGorValues(ocrFields);

        assertEquals(DECEASED_DIED_ON_OR_AFTER_SWITCH_DATE, modifiedFields.getFirst().getValue().getFieldName());
        assertEquals(TRUE, ocrFields.getDeceasedDiedOnAfterSwitchDate());
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
        ocrFields.setDeceasedDiedOnAfterSwitchDate("");
        ocrFields.setIht207Completed(TRUE);

        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate("01012022")).thenReturn(true);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultGorValues(ocrFields);

        assertEquals(DECEASED_DIED_ON_OR_AFTER_SWITCH_DATE, modifiedFields.getFirst().getValue().getFieldName());
        assertEquals(TRUE, ocrFields.getDeceasedDiedOnAfterSwitchDate());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht400421Completed());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht205Completed());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht205completedOnline());
        assertEquals(DEFAULT_VALUE, ocrFields.getIhtGrossValue());
        assertEquals(DEFAULT_VALUE, ocrFields.getIhtNetValue());
        assertEquals("2", ocrFields.getFormVersion());
    }

    @Test
    void shouldSetDefaultGrossNetValueWhenDeceasedDateOfDeathIsBeforeAndFormVersionIsThreeAndIhtIs205()
            throws IllegalAccessException {
        formVersionField.set(ocrFields, "3");

        ocrFields.setDeceasedDateOfDeath("01012020");
        ocrFields.setDeceasedDiedOnAfterSwitchDate("");
        ocrFields.setIht205Completed(TRUE);

        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate("01012020")).thenReturn(false);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultGorValues(ocrFields);

        assertEquals(DECEASED_DIED_ON_OR_AFTER_SWITCH_DATE, modifiedFields.getFirst().getValue().getFieldName());
        assertEquals(FALSE, ocrFields.getDeceasedDiedOnAfterSwitchDate());
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
        formVersionField.set(ocrFields, "3");

        ocrFields.setDeceasedDateOfDeath("01012020");
        ocrFields.setDeceasedDiedOnAfterSwitchDate("");
        ocrFields.setIht205completedOnline(TRUE);

        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate("01012020")).thenReturn(false);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultGorValues(ocrFields);

        assertEquals(DECEASED_DIED_ON_OR_AFTER_SWITCH_DATE, modifiedFields.getFirst().getValue().getFieldName());
        assertEquals(FALSE, ocrFields.getDeceasedDiedOnAfterSwitchDate());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht400421Completed());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht207Completed());
        assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht205Completed());
        assertEquals(DEFAULT_VALUE, ocrFields.getIhtGrossValue205());
        assertEquals(DEFAULT_VALUE, ocrFields.getIhtNetValue205());
        assertEquals("1234", ocrFields.getIhtReferenceNumber());
        assertEquals("3", ocrFields.getFormVersion());
    }

    @Test
    void shouldSetDefaultEstateGrossNetValueWhenDeceasedDateOfDeathIsBeforeAndFormVersionIsThree()
            throws IllegalAccessException, NoSuchFieldException {
        formVersionField.set(ocrFields, "3");

        Field exceptedEstateField = ExceptionRecordOCRFields.class.getDeclaredField("exceptedEstate");
        exceptedEstateField.setAccessible(true);
        exceptedEstateField.set(ocrFields, TRUE);

        ocrFields.setDeceasedDateOfDeath("01012020");
        ocrFields.setDeceasedDiedOnAfterSwitchDate("");

        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate("01012020")).thenReturn(false);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultGorValues(ocrFields);

        assertEquals(DECEASED_DIED_ON_OR_AFTER_SWITCH_DATE, modifiedFields.getFirst().getValue().getFieldName());
        assertEquals(FALSE, ocrFields.getDeceasedDiedOnAfterSwitchDate());
        assertEquals(DEFAULT_VALUE, ocrFields.getIhtEstateGrossValue());
        assertEquals(DEFAULT_VALUE, ocrFields.getIhtEstateNetValue());
        assertEquals(DEFAULT_VALUE, ocrFields.getIhtEstateNetQualifyingValue());
        assertEquals(DEFAULT_VALUE, ocrFields.getIhtEstateGrossValue());

        assertEquals("3", ocrFields.getFormVersion());
    }

    @Test
    void shouldSetDefaultEstateGrossNetValueWhenDeceasedDateOfDeathIsAfterAndFormVersionIsTwo()
            throws IllegalAccessException {
        formVersionField.set(ocrFields, "2");
        ocrFields.setDeceasedDateOfDeath("01012022");
        ocrFields.setDeceasedDiedOnAfterSwitchDate("");

        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate("01012022")).thenReturn(true);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultGorValues(ocrFields);

        assertEquals(DECEASED_DIED_ON_OR_AFTER_SWITCH_DATE, modifiedFields.getFirst().getValue().getFieldName());
        assertEquals(TRUE, ocrFields.getDeceasedDiedOnAfterSwitchDate());
        assertEquals(DEFAULT_VALUE, ocrFields.getIhtEstateGrossValue());
        assertEquals(DEFAULT_VALUE, ocrFields.getIhtEstateNetValue());
        assertEquals(DEFAULT_VALUE, ocrFields.getIhtEstateNetQualifyingValue());
        assertEquals(DEFAULT_VALUE, ocrFields.getIhtGrossValue());
        assertEquals(DEFAULT_VALUE, ocrFields.getIhtNetValue());
        assertEquals("2", ocrFields.getFormVersion());
    }

    @Test
    void shouldReturnWarningWhenMoreThanOneIHTFormIsTrue() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .exceptedEstate(TRUE)
                .iht400Completed(TRUE)
                .iht400process(FALSE)
                .iht400421Completed(FALSE)
                .iht207Completed(FALSE)
                .iht205Completed(FALSE)
                .build();

        List<CollectionMember<String>> warnings = ocrFieldModifierUtils.checkWarnings(ocrFields);

        assertEquals(1, warnings.size());
        assertEquals("More than one IHT form is marked as TRUE. Only one form should be selected as TRUE.",
                warnings.getFirst().getValue());
    }

    @Test
    void shouldReturnNoWarningWhenOnlyOneIHTFormIsTrue() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .exceptedEstate(TRUE)
                .iht400Completed(FALSE)
                .iht400process(FALSE)
                .iht400421Completed(FALSE)
                .iht207Completed(FALSE)
                .iht205Completed(FALSE)
                .build();

        List<CollectionMember<String>> warnings = ocrFieldModifierUtils.checkWarnings(ocrFields);

        assertEquals(0, warnings.size());
    }

    @Test
    void shouldSetCaveatorForenamesToDefaultWhenEmpty() {
        ocrFields.setDeceasedDateOfDeath(VALID_DECEASED_DATE_OF_BIRTH);
        ocrFields.setCaveatorForenames("");
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils
                .setDefaultCaveatValues(ocrFields);

        assertEquals(2, modifiedFields.size());
        assertEquals(CAVEAT_FORENAMES, modifiedFields.getFirst().getValue().getFieldName());
    }

    @Test
    void shouldSetCaveatorSurnamesToDefaultWhenEmpty() {
        ocrFields.setDeceasedDateOfDeath(VALID_DECEASED_DATE_OF_BIRTH);
        ocrFields.setCaveatorSurnames("");
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils
                .setDefaultCaveatValues(ocrFields);

        assertEquals(2, modifiedFields.size());
        assertEquals(CAVEAT_SURNAME, modifiedFields.getFirst().getValue().getFieldName());
    }

    @Test
    void shouldSetDeceasedForenamesToDefaultWhenEmpty() {
        ocrFields.setDeceasedDateOfDeath(VALID_DECEASED_DATE_OF_BIRTH);
        ocrFields.setDeceasedForenames("");
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils
                .setDefaultCaveatValues(ocrFields);

        assertEquals(2, modifiedFields.size());
        assertEquals(DECEASED_FORENAME, modifiedFields.getFirst().getValue().getFieldName());
    }

    @Test
    void shouldSetDeceasedSurnameToDefaultWhenEmpty() {
        ocrFields.setDeceasedDateOfDeath(VALID_DECEASED_DATE_OF_BIRTH);
        ocrFields.setDeceasedSurname("");
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils
                .setDefaultCaveatValues(ocrFields);

        assertEquals(2, modifiedFields.size());
        assertEquals(DECEASED_SURNAME, modifiedFields.getFirst().getValue().getFieldName());
    }

    @Test
    void shouldSetDeceasedDateOfDeathToDefaultWhenEmpty() {
        ocrFields.setDeceasedDateOfDeath("");
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils
                .setDefaultCaveatValues(ocrFields);

        assertEquals(2, modifiedFields.size());
        assertEquals(DECEASED_DOD, modifiedFields.getFirst().getValue().getFieldName());
    }

    @Test
    void shouldSetSolicitorAddressFieldsWhenLegalRepresentativeIsTrue() {
        ocrFields.setLegalRepresentative(TRUE);
        ocrFields.setSolsSolicitorAddressLine1("");
        ocrFields.setSolsSolicitorAddressPostCode("");
        ocrFields.setSolsSolicitorFirmName("");
        ocrFields.setDeceasedDateOfDeath(VALID_DECEASED_DATE_OF_BIRTH);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils
                .setDefaultCaveatValues(ocrFields);

        assertEquals(3, modifiedFields.size());
        assertEquals(SOLICITOR_ADDRESS_LINE1, modifiedFields.get(0).getValue().getFieldName());
        assertEquals(SOLICITOR_ADDRESS_POST_CODE, modifiedFields.get(1).getValue().getFieldName());
        assertEquals(SOLICITOR_FIRM_NAME, modifiedFields.get(2).getValue().getFieldName());
    }

    @Test
    void shouldSetCitizenAddressFieldsWhenLegalRepresentativeIsFalse() {
        ocrFields.setLegalRepresentative(FALSE);
        ocrFields.setCaveatorAddressLine1("");
        ocrFields.setCaveatorAddressPostCode("");
        ocrFields.setDeceasedDateOfDeath(VALID_DECEASED_DATE_OF_BIRTH);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils
                .setDefaultCaveatValues(ocrFields);

        assertEquals(2, modifiedFields.size());
        assertEquals(CAVEATOR_ADDRESS_LINE1, modifiedFields.get(0).getValue().getFieldName());
        assertEquals(CAVEATOR_POST_CODE, modifiedFields.get(1).getValue().getFieldName());
    }

    @Test
    void shouldNotModifyFieldsWhenAllValuesArePresent() throws NoSuchFieldException, IllegalAccessException {
        ocrFields.setCaveatorForenames("John");
        ocrFields.setCaveatorSurnames("Doe");
        ocrFields.setDeceasedForenames("Jane");
        ocrFields.setDeceasedSurname("Smith");
        ocrFields.setDeceasedDateOfDeath("01012020");
        Field legalRepresentative = ExceptionRecordOCRFields.class.getDeclaredField("legalRepresentative");
        legalRepresentative.setAccessible(true);
        legalRepresentative.set(ocrFields, "false");
        ocrFields.setCaveatorAddressLine1("123 Street");
        ocrFields.setCaveatorAddressPostCode("SW1A 1AA");

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils
                .setDefaultCaveatValues(ocrFields);

        assertEquals(0, modifiedFields.size());
    }

    @Test
    void shouldHandleDeceasedDateOfDeathPresent() {
        ocrFields.setDeceasedDateOfDeath("01012022");
        ocrFields.setDeceasedDiedOnAfterSwitchDate("");

        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate("01012022")).thenReturn(true);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultGorValues(ocrFields);

        assertEquals(1, modifiedFields.size());
        assertEquals(DECEASED_DIED_ON_OR_AFTER_SWITCH_DATE, modifiedFields.getFirst().getValue().getFieldName());
        assertEquals(TRUE, ocrFields.getDeceasedDiedOnAfterSwitchDate());
    }

    @Test
    void shouldHandleDeceasedDateOfDeathBeforePresent() {
        ocrFields.setDeceasedDateOfDeath("01012020");
        ocrFields.setDeceasedDiedOnAfterSwitchDate("");

        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate("01012020")).thenReturn(false);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultGorValues(ocrFields);

        assertEquals(1, modifiedFields.size());
        assertEquals(DECEASED_DIED_ON_OR_AFTER_SWITCH_DATE, modifiedFields.getFirst().getValue().getFieldName());
        assertEquals(FALSE, ocrFields.getDeceasedDiedOnAfterSwitchDate());
    }

    @Test
    void shouldHandleDeceasedDateOfDeathMissing() {
        ocrFields.setDeceasedDateOfDeath("");
        ocrFields.setDeceasedDiedOnAfterSwitchDate(TRUE);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultGorValues(ocrFields);

        assertEquals(1, modifiedFields.size());
        assertEquals(DECEASED_DOD, modifiedFields.getFirst().getValue().getFieldName());
        assertEquals(bulkScanConfig.getDateOfDeathForDiedOnOrAfterSwitchDateTrue(), ocrFields.getDeceasedDateOfDeath());
    }

    @Test
    void shouldHandleDeceasedDateOfDeathMissingWhenSwitchDateIsFalse() {
        ocrFields.setDeceasedDateOfDeath("");
        ocrFields.setDeceasedDiedOnAfterSwitchDate(FALSE);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultGorValues(ocrFields);

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

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultGorValues(ocrFields);

        assertEquals(2, modifiedFields.size());
        assertEquals(DECEASED_DIED_ON_OR_AFTER_SWITCH_DATE, modifiedFields.getFirst().getValue().getFieldName());
        assertEquals(TRUE, ocrFields.getDeceasedDiedOnAfterSwitchDate());
        assertEquals(DECEASED_DOD, modifiedFields.getLast().getValue().getFieldName());
        assertEquals("01012022", ocrFields.getDeceasedDateOfDeath());
    }

    @Test
    void shouldSetExecutorAddressFieldsWhenExecutorNameIsPresent() throws IllegalAccessException {
        applyingExecutorName0.set(ocrFields, "Executor Name");
        ocrFields.setExecutorsApplying0applyingExecutorAddressLine1("");
        ocrFields.setExecutorsApplying0applyingExecutorAddressTown("");
        ocrFields.setExecutorsApplying0applyingExecutorAddressPostCode("");

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultGorValues(ocrFields);

        assertEquals(3, modifiedFields.size());
        assertEquals(EXECUTORS_APPLYING_0_ADDRESS_LINE, modifiedFields.get(0).getValue()
                .getFieldName());
        assertEquals(EXECUTORS_APPLYING_0_ADDRESS_TOWN,
                modifiedFields.get(1).getValue().getFieldName());
        assertEquals(EXECUTORS_APPLYING_0_ADDRESS_POST_CODE,
                modifiedFields.get(2).getValue().getFieldName());
        assertEquals(bulkScanConfig.getName(), ocrFields.getExecutorsApplying0applyingExecutorAddressLine1());
        assertEquals(bulkScanConfig.getName(), ocrFields.getExecutorsApplying0applyingExecutorAddressTown());
        assertEquals(bulkScanConfig.getPostcode(), ocrFields.getExecutorsApplying0applyingExecutorAddressPostCode());
    }

    @Test
    void shouldNotModifyExecutorAddressFieldsWhenExecutorNameIsBlank() throws IllegalAccessException {
        applyingExecutorName0.set(ocrFields, "");
        ocrFields.setExecutorsApplying0applyingExecutorAddressLine1("");
        ocrFields.setExecutorsApplying0applyingExecutorAddressTown("");
        ocrFields.setExecutorsApplying0applyingExecutorAddressPostCode("");

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultGorValues(ocrFields);

        assertEquals(0, modifiedFields.size());
        assertEquals("", ocrFields.getExecutorsApplying0applyingExecutorAddressLine1());
        assertEquals("", ocrFields.getExecutorsApplying0applyingExecutorAddressTown());
        assertEquals("", ocrFields.getExecutorsApplying0applyingExecutorAddressPostCode());
    }

    @Test
    void shouldHandleMultipleExecutorsApplyingFields() throws IllegalAccessException {
        applyingExecutorName0.set(ocrFields, "Executor Name0");
        applyingExecutorName1.set(ocrFields, "Executor Name1");
        applyingExecutorName2.set(ocrFields, "Executor Name2");
        ocrFields.setExecutorsApplying0applyingExecutorAddressLine1("");
        ocrFields.setExecutorsApplying1applyingExecutorAddressLine1("");
        ocrFields.setExecutorsApplying2applyingExecutorAddressLine1("");

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultGorValues(ocrFields);

        assertEquals(9, modifiedFields.size());
        assertEquals(EXECUTORS_APPLYING_0_ADDRESS_LINE,
                modifiedFields.get(0).getValue().getFieldName());
        assertEquals(EXECUTORS_APPLYING_1_ADDRESS_LINE,
                modifiedFields.get(3).getValue().getFieldName());
        assertEquals(EXECUTORS_APPLYING_2_ADDRESS_LINE,
                modifiedFields.get(6).getValue().getFieldName());
        assertEquals(bulkScanConfig.getName(), ocrFields.getExecutorsApplying0applyingExecutorAddressLine1());
        assertEquals(bulkScanConfig.getName(), ocrFields.getExecutorsApplying1applyingExecutorAddressLine1());
        assertEquals(bulkScanConfig.getName(), ocrFields.getExecutorsApplying2applyingExecutorAddressLine1());
    }

    @Test
    void shouldSetExecutorReasonWhenExecutorNameIsPresentAndReasonIsBlank() throws IllegalAccessException {
        notApplyingExecutorName0.set(ocrFields, "Executor Name");
        ocrFields.setExecutorsNotApplying0notApplyingExecutorReason("");

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        ocrFieldModifierUtils.handleExecutorsNotApplyingFields(ocrFields, modifiedFields);

        assertEquals(1, modifiedFields.size());
        assertEquals(EXECUTOR_NOT_APPLYING_0_REASON,
                modifiedFields.getFirst().getValue().getFieldName());
        assertEquals(bulkScanConfig.getExecutorsNotApplyingReason(),
                ocrFields.getExecutorsNotApplying0notApplyingExecutorReason());
    }

    @Test
    void shouldNotModifyExecutorReasonWhenExecutorNameIsBlank() throws IllegalAccessException {
        notApplyingExecutorName0.set(ocrFields, "");
        ocrFields.setExecutorsNotApplying0notApplyingExecutorReason("");

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        ocrFieldModifierUtils.handleExecutorsNotApplyingFields(ocrFields, modifiedFields);

        assertEquals(0, modifiedFields.size());
        assertEquals("", ocrFields.getExecutorsNotApplying0notApplyingExecutorReason());
    }

    @Test
    void shouldHandleMultipleExecutorsNotApplyingFields() throws IllegalAccessException {
        notApplyingExecutorName0.set(ocrFields, "Executor Name0");
        notApplyingExecutorName1.set(ocrFields, "Executor Name1");
        notApplyingExecutorName2.set(ocrFields, "Executor Name2");
        ocrFields.setExecutorsNotApplying0notApplyingExecutorReason("");
        ocrFields.setExecutorsNotApplying1notApplyingExecutorReason("");
        ocrFields.setExecutorsNotApplying2notApplyingExecutorReason("");

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        ocrFieldModifierUtils.handleExecutorsNotApplyingFields(ocrFields, modifiedFields);

        assertEquals(3, modifiedFields.size());
        assertEquals(EXECUTOR_NOT_APPLYING_0_REASON,
                modifiedFields.get(0).getValue().getFieldName());
        assertEquals(EXECUTOR_NOT_APPLYING_1_REASON,
                modifiedFields.get(1).getValue().getFieldName());
        assertEquals(EXECUTOR_NOT_APPLYING_2_REASON,
                modifiedFields.get(2).getValue().getFieldName());
        assertEquals(bulkScanConfig.getExecutorsNotApplyingReason(),
                ocrFields.getExecutorsNotApplying0notApplyingExecutorReason());
        assertEquals(bulkScanConfig.getExecutorsNotApplyingReason(),
                ocrFields.getExecutorsNotApplying1notApplyingExecutorReason());
        assertEquals(bulkScanConfig.getExecutorsNotApplyingReason(),
                ocrFields.getExecutorsNotApplying2notApplyingExecutorReason());
    }

    @Test
    void shouldSetSolicitorIsApplyingToDefaultWhenEmpty() {
        ocrFields.setSolsSolicitorIsApplying("");
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultGorValues(ocrFields);

        assertEquals(1, modifiedFields.size());
        assertEquals(SOLICITOR_IS_APPLYING, modifiedFields.getFirst().getValue().getFieldName());
        assertEquals(bulkScanConfig.getSolicitorApplying(), ocrFields.getSolsSolicitorIsApplying());
    }

    @Test
    void shouldSetSolicitorIsApplyingToDefaultWhenValueIsPresent() {
        ocrFields.setSolsSolicitorIsApplying(TRUE);
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultGorValues(ocrFields);

        assertEquals(0, modifiedFields.size());
    }

    @Test
    void shouldSetSpouseOrPartnerToDefaultWhenEmpty() {
        ocrFields.setSpouseOrPartner("");
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultGorValues(ocrFields);

        assertEquals(1, modifiedFields.size());
        assertEquals(SPOUSE_OR_PARTNER, modifiedFields.get(0).getValue().getFieldName());
        assertEquals(bulkScanConfig.getFieldsNotCompleted(), ocrFields.getSpouseOrPartner());
    }

    @Test
    void shouldNotModifySpouseOrPartnerWhenValueIsPresent() {
        ocrFields.setSpouseOrPartner(TRUE);
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultGorValues(ocrFields);

        assertEquals(0, modifiedFields.size());
        assertEquals(TRUE, ocrFields.getSpouseOrPartner());
    }

    @Test
    void shouldSetNotifiedApplicantsToDefaultWhenEmpty() {
        ocrFields.setNotifiedApplicants("");
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultGorValues(ocrFields);

        assertEquals(1, modifiedFields.size());
        assertEquals(NOTIFIED_APPLICANTS, modifiedFields.getFirst().getValue().getFieldName());
        assertEquals(bulkScanConfig.getFieldsNotCompleted(), ocrFields.getNotifiedApplicants());
    }

    @Test
    void shouldNotModifyNotifiedApplicantsWhenValueIsPresent() {
        ocrFields.setNotifiedApplicants(TRUE);
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultGorValues(ocrFields);

        assertEquals(0, modifiedFields.size());
        assertEquals(TRUE, ocrFields.getNotifiedApplicants());
    }

    @Test
    void shouldSetIhtFormCompletedOnlineToDefaultWhenBlank() throws IllegalAccessException {
        formVersionField.set(ocrFields, "1");

        ocrFields.setIhtFormCompletedOnline("");

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultGorValues(ocrFields);

        assertEquals(2, modifiedFields.size());
        assertEquals(IHT_FORM_COMPLETED_ONLINE, modifiedFields.getFirst().getValue().getFieldName());
        assertEquals(bulkScanConfig.getFieldsNotCompleted(), ocrFields.getIhtFormCompletedOnline());
        assertEquals(bulkScanConfig.getDefaultForm(), ocrFields.getIhtFormId());
    }

    @Test
    void shouldSetIhtReferenceNumberWhenIhtFormCompletedOnlineIsTrueAndBlank() throws IllegalAccessException {
        formVersionField.set(ocrFields, "1");
        ocrFields.setIhtFormCompletedOnline(TRUE);
        ocrFields.setIhtReferenceNumber("");

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultGorValues(ocrFields);

        assertEquals(1, modifiedFields.size());
        assertEquals(IHT_REFERENCE, modifiedFields.getFirst().getValue().getFieldName());
        assertEquals(bulkScanConfig.getName(), ocrFields.getIhtReferenceNumber());
    }

    @Test
    void shouldSetGrossAndNetValuesWhenIhtFormIdIsValidAndIhtFormCompletedOnlineIsFalse()
            throws IllegalAccessException {
        formVersionField.set(ocrFields, "1");
        ocrFields.setIhtFormCompletedOnline(FALSE);
        ocrFields.setIhtFormId(DEFAULT_FORM);
        ocrFields.setIhtGrossValue("");
        ocrFields.setIhtNetValue("");

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultGorValues(ocrFields);

        assertEquals(2, modifiedFields.size());
        assertEquals(IHT_GROSS_VALUE, modifiedFields.get(0).getValue().getFieldName());
        assertEquals(bulkScanConfig.getGrossNetValue(), ocrFields.getIhtGrossValue());
        assertEquals(IHT_NET_VALUE, modifiedFields.get(1).getValue().getFieldName());
        assertEquals(bulkScanConfig.getGrossNetValue(), ocrFields.getIhtNetValue());
    }

    @Test
    void shouldNotModifyFieldsWhenIhtFormCompletedOnlineIsTrueAndValuesArePresent() throws IllegalAccessException {
        formVersionField.set(ocrFields, "1");
        ocrFields.setIhtFormCompletedOnline(TRUE);
        ocrFields.setIhtReferenceNumber("12345");

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultGorValues(ocrFields);

        assertEquals(0, modifiedFields.size());
    }

    @Test
    void shouldModifyFieldsWhenIhtFormIdIsValidAndValuesAreNotPresent() throws IllegalAccessException {
        formVersionField.set(ocrFields, "1");
        ocrFields.setIhtFormCompletedOnline(FALSE);
        ocrFields.setIhtFormId(DEFAULT_FORM);
        ocrFields.setIhtGrossValue("");
        ocrFields.setIhtNetValue("");

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultGorValues(ocrFields);

        assertEquals(2, modifiedFields.size());
        assertEquals("1.11", ocrFields.getIhtGrossValue());
        assertEquals("1.11", ocrFields.getIhtNetValue());
    }

    @Test
    void shouldNotModifyFieldsWhenIhtFormIdIsValidAndValuesArePresent() throws IllegalAccessException {
        formVersionField.set(ocrFields, "1");
        ocrFields.setIhtFormCompletedOnline(FALSE);
        ocrFields.setIhtFormId(DEFAULT_FORM);
        ocrFields.setIhtGrossValue("1.11");
        ocrFields.setIhtNetValue("1.11");

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultGorValues(ocrFields);

        assertEquals(0, modifiedFields.size());
        assertEquals("1.11", ocrFields.getIhtGrossValue());
        assertEquals("1.11", ocrFields.getIhtNetValue());
    }

}
