package uk.gov.hmcts.probate.service.exceptionrecord.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.config.BulkScanConfig;
import uk.gov.hmcts.probate.model.ocr.OCRField;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.ModifiedOCRField;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class OCRFieldExtractorTest {

    @Mock
    private BulkScanConfig bulkScanConfig;

    private static final String SOME_KEY_WITH_NO_VALUE = "keyWithNoValue";
    private static final String SOME_KEY_WITH_NULL_VALUE = "keyWithNullValue";
    private static final String SOME_KEY_WITH_EMPTY_VALUE = "keyWithEmptyValue";

    private static final String LAST_NAME_KEY = "surname";
    private static final String MIDDLE_NAME_KEY = "middleName";
    private static final String FIRST_NAME_KEY = "firstName";
    private static final String LAST_NAME_VALUE = "Monkhouse";
    private static final String MIDDLE_NAME_VALUE = "Marley";
    private static final String FIRST_NAME_VALUE = "Bob";

    private static final String DECEASED_SURNAME_KEY = "deceasedSurname";
    private static final String VALID_DECEASED_SURNAME_VALUE = "VALID SURNAME";
    private static final String DEFAULT_DECEASED_SURNAME_VALUE = "MISSING";

    private static final String VALID_POSTCODE_KEY = "deceasedAddressPostCode";
    private static final String VALID_POSTCODE_VALUE = "SW1A 1AA";
    private static final String DEFAULT_POSTCODE_VALUE = "MI55 1NG";

    private static final String VALID_DOB_KEY = "deceasedDateOfBirth";
    private static final String VALID_DOB_VALUE = "01-01-2025";
    private static final String DEFAULT_DOB_VALUE = "01011990";

    //Below here need doing
    private static final String VALID_SOLS_APP_REFERENCE_DOB_KEY = "solsSolicitorAppReference";
    private static final String VALID_SOLS_APP_REFERENCE_DOB_VALUE = "Bob Jefferson";
    private static final String DEFAULT_SOLS_APP_REFERENCE_DOB_VALUE = VALID_DECEASED_SURNAME_VALUE;

    private static final String VALID_EMAIL_KEY = "solsSolicitorEmail";
    private static final String VALID_EMAIL_VALUE = "fake@gmail.com";
    private static final String DEFAULT_EMAIL_VALUE = "contactprobate@justice.gov.uk";

    private static final String VALID_PHONE_KEY = "solsSolicitorPhoneNumber";
    private static final String VALID_PHONE_VALUE = "07123456789";
    private static final String DEFAULT_PHONE_VALUE = "1234";

    private static final String VALID_DECEASED_ANY_OTHER_NAMES_KEY = "deceasedAnyOtherNames";
    private static final String VALID_DECEASED_ANY_OTHER_NAMES_VALUE = "";
    private static final String DEFAULT_DECEASED_ANY_OTHER_NAMES_VALUE = "FALSE";

    private static final String VALID_DOMICILE_IN_ENG_WALES_KEY = "deceasedDomicileInEngWales";
    private static final String VALID_DOMICILE_IN_ENG_WALES_VALUE = "FALSE";
    private static final String DEFAULT_DECEASED_DOMICILE_IN_ENG_WALES_VALUE = "TRUE";

    private static final String VALID_SOLS_SOLICITOR_IS_APPLYING_KEY = "solsSolicitorIsApplying";
    private static final String VALID_SOLS_SOLICITOR_IS_APPLYING_VALUE = "FALSE";
    private static final String DEFAULT_SOLS_SOLICITOR_IS_APPLYING_VALUE = "TRUE";

    private static final String VALID_SOLS_SOLICITOR_REPRESENTATIVE_NAME_KEY = "solsSolicitorRepresentativeName";
    private static final String VALID_SOLS_SOLICITOR_REPRESENTATIVE_NAME_VALUE = "Bork Bork Associates";
    private static final String INVALID_SOLS_SOLICITOR_REPRESENTATIVE_NAME_VALUE = "";
    private static final String DEFAULT_SOLS_SOLICITOR_REPRESENTATIVE_NAME_VALUE = "Firm Name";


    private List<OCRField> ocrFields = new ArrayList<>();

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        ocrFields.add(OCRField.builder().name(FIRST_NAME_KEY).value(FIRST_NAME_VALUE).build());
        ocrFields.add(OCRField.builder().name(MIDDLE_NAME_KEY).value(MIDDLE_NAME_VALUE).build());
        ocrFields.add(OCRField.builder().name(LAST_NAME_KEY).value(LAST_NAME_VALUE).build());
        ocrFields.add(OCRField.builder().name(SOME_KEY_WITH_NO_VALUE).build());
        ocrFields.add(OCRField.builder().name(SOME_KEY_WITH_NULL_VALUE).value(null).build());
        ocrFields.add(OCRField.builder().name(SOME_KEY_WITH_EMPTY_VALUE).value("").build());

        ocrFields.add(OCRField.builder().name(VALID_POSTCODE_KEY).value(VALID_POSTCODE_VALUE).build());
        ocrFields.add(OCRField.builder().name(DECEASED_SURNAME_KEY).value(VALID_DECEASED_SURNAME_VALUE).build());
        ocrFields.add(OCRField.builder().name(VALID_DOB_KEY).value(VALID_DOB_VALUE).build());
        ocrFields.add(OCRField.builder().name(VALID_SOLS_APP_REFERENCE_DOB_KEY).value(VALID_SOLS_APP_REFERENCE_DOB_VALUE).build());
        ocrFields.add(OCRField.builder().name(VALID_EMAIL_KEY).value(VALID_EMAIL_VALUE).build());
        ocrFields.add(OCRField.builder().name(VALID_PHONE_KEY).value(VALID_PHONE_VALUE).build());
        ocrFields.add(OCRField.builder().name(VALID_DECEASED_ANY_OTHER_NAMES_KEY).value(VALID_DECEASED_ANY_OTHER_NAMES_VALUE).build());
        ocrFields.add(OCRField.builder().name(VALID_DOMICILE_IN_ENG_WALES_KEY).value(VALID_DOMICILE_IN_ENG_WALES_VALUE).build());
        ocrFields.add(OCRField.builder().name(VALID_SOLS_SOLICITOR_REPRESENTATIVE_NAME_KEY).value(VALID_SOLS_SOLICITOR_REPRESENTATIVE_NAME_VALUE).build());

        when(bulkScanConfig.getPostcode()).thenReturn(DEFAULT_POSTCODE_VALUE);
        when(bulkScanConfig.getName()).thenReturn(DEFAULT_DECEASED_SURNAME_VALUE);
        when(bulkScanConfig.getDob()).thenReturn(DEFAULT_DOB_VALUE);
        when(bulkScanConfig.getSolsSolicitorAppReference()).thenReturn(DEFAULT_SOLS_APP_REFERENCE_DOB_VALUE);
        when(bulkScanConfig.getEmail()).thenReturn(DEFAULT_EMAIL_VALUE);
        when(bulkScanConfig.getPhone()).thenReturn(DEFAULT_PHONE_VALUE);
        when(bulkScanConfig.getDeceasedAnyOtherNames()).thenReturn(DEFAULT_DECEASED_ANY_OTHER_NAMES_VALUE);
        when(bulkScanConfig.getDeceasedDomicileInEngWales()).thenReturn(DEFAULT_DECEASED_DOMICILE_IN_ENG_WALES_VALUE);

        Field bulkScanConfigField = OCRFieldExtractor.class.getDeclaredField("bulkScanConfig");
        bulkScanConfigField.setAccessible(true);
        bulkScanConfigField.set(null, bulkScanConfig);
    }

    @Test
    void getValidResponse() {
        String response = OCRFieldExtractor.get(ocrFields, LAST_NAME_KEY);
        assertEquals(LAST_NAME_VALUE, response);
    }

    @Test
    void getValidTwoParamResponse() {
        String response = OCRFieldExtractor.get(ocrFields, FIRST_NAME_KEY, LAST_NAME_KEY);
        assertEquals(FIRST_NAME_VALUE + " " + LAST_NAME_VALUE, response);
    }

    @Test
    void getValidThreeParamResponse() {
        String response = OCRFieldExtractor.get(ocrFields, FIRST_NAME_KEY, MIDDLE_NAME_KEY, LAST_NAME_KEY);
        assertEquals(FIRST_NAME_VALUE + " " + MIDDLE_NAME_VALUE + " " + LAST_NAME_VALUE, response);
    }

    @Test
    void getValidThreeParamResponseWithNoMiddleName() {
        String response = OCRFieldExtractor.get(ocrFields, FIRST_NAME_KEY, null, LAST_NAME_KEY);
        assertEquals(FIRST_NAME_VALUE + " " + LAST_NAME_VALUE, response);
    }

    @Test
    void getValidThreeParamResponseWithNoMiddleNameOrFirstName() {
        String response = OCRFieldExtractor.get(ocrFields, null, null, LAST_NAME_KEY);
        assertEquals(LAST_NAME_VALUE, response);
    }

    @Test
    void getNullResponseForMissingValue() {
        String response = OCRFieldExtractor.get(ocrFields, SOME_KEY_WITH_NO_VALUE);
        assertNull(response);
    }

    @Test
    void getEmptyResponseForEmptyValue() {
        String response = OCRFieldExtractor.get(ocrFields, SOME_KEY_WITH_EMPTY_VALUE);
        assertEquals(response, "");
    }

    @Test
    void getNullResponseForNullValue() {
        String response = OCRFieldExtractor.get(ocrFields, SOME_KEY_WITH_NULL_VALUE);
        assertNull(response);
    }

    //Name - "MISSING" related values
    @Test
    void getDefaultNameIfNull() {
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        String response = OCRFieldExtractor.getDefaultNameIfInvalid(ocrFields, SOME_KEY_WITH_NULL_VALUE,
                modifiedFields);
        assertEquals(DEFAULT_DECEASED_SURNAME_VALUE, response);
        assertEquals(SOME_KEY_WITH_NULL_VALUE, modifiedFields.get(0).getValue().getFieldName());
        assertNull(modifiedFields.get(0).getValue().getOriginalValue());
    }

    @Test
    void getDefaultNameIfNoValue() {
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        String response = OCRFieldExtractor.getDefaultNameIfInvalid(ocrFields, SOME_KEY_WITH_NO_VALUE,
                modifiedFields);
        assertEquals(DEFAULT_DECEASED_SURNAME_VALUE, response);
        assertEquals(SOME_KEY_WITH_NO_VALUE, modifiedFields.get(0).getValue().getFieldName());
        assertNull(modifiedFields.get(0).getValue().getOriginalValue());
    }

    //Needs further validation check inside concrete class
    @Test
    void getDefaultNameIfEmptyValue() {
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        String response = OCRFieldExtractor.getDefaultNameIfInvalid(ocrFields, SOME_KEY_WITH_EMPTY_VALUE,
                modifiedFields);
        assertEquals(DEFAULT_DECEASED_SURNAME_VALUE, response);
        assertEquals(SOME_KEY_WITH_EMPTY_VALUE, modifiedFields.get(0).getValue().getFieldName());
        assertEquals("", modifiedFields.get(0).getValue().getOriginalValue());
    }

    //Needs further validation check inside concrete class
    @Test
    void getDefaultNameIfInvalid() {
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        String response = OCRFieldExtractor.getDefaultNameIfInvalid(ocrFields, FIRST_NAME_KEY,
                modifiedFields);
        assertEquals(DEFAULT_DECEASED_SURNAME_VALUE, response);
        assertEquals(FIRST_NAME_KEY, modifiedFields.get(0).getValue().getFieldName());
        assertEquals(FIRST_NAME_VALUE.toUpperCase(), modifiedFields.get(0).getValue().getOriginalValue());
    }

    // Should surnames be capitalised? If so this is fine, if not change both the concrete code and
    // VALID_DECEASED_SURNAME_VALUE to be lower cases
    @Test
    void getNameIfNotNull() {
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        String response = OCRFieldExtractor.getDefaultNameIfInvalid(ocrFields, DECEASED_SURNAME_KEY,
                modifiedFields);
        assertEquals(VALID_DECEASED_SURNAME_VALUE, response);
        assertTrue(modifiedFields.isEmpty());
    }

    // Postcodes
    @Test
    void getDefaultPostcodeIfNull() {
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        String response = OCRFieldExtractor.getDefaultPostcodeIfInvalid(ocrFields, SOME_KEY_WITH_NULL_VALUE,
                modifiedFields);
        assertEquals(DEFAULT_POSTCODE_VALUE, response);
        assertEquals(SOME_KEY_WITH_NULL_VALUE, modifiedFields.get(0).getValue().getFieldName());
        assertNull(modifiedFields.get(0).getValue().getOriginalValue());
    }

    @Test
    void getDefaultPostcodeIfNoValue() {
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        String response = OCRFieldExtractor.getDefaultPostcodeIfInvalid(ocrFields, SOME_KEY_WITH_NO_VALUE,
                modifiedFields);
        assertEquals(DEFAULT_POSTCODE_VALUE, response);
        assertEquals(SOME_KEY_WITH_NO_VALUE, modifiedFields.get(0).getValue().getFieldName());
        assertNull(modifiedFields.get(0).getValue().getOriginalValue());
    }

    @Test
    void getDefaultPostcodeIfEmptyValue() {
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        String response = OCRFieldExtractor.getDefaultPostcodeIfInvalid(ocrFields, SOME_KEY_WITH_EMPTY_VALUE,
                modifiedFields);
        assertEquals(DEFAULT_POSTCODE_VALUE, response);
        assertEquals(SOME_KEY_WITH_EMPTY_VALUE, modifiedFields.get(0).getValue().getFieldName());
        assertEquals("", modifiedFields.get(0).getValue().getOriginalValue());
    }

    @Test
    void getDefaultPostcodeIfInvalid() {
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        String response = OCRFieldExtractor.getDefaultPostcodeIfInvalid(ocrFields, FIRST_NAME_KEY,
                modifiedFields);
        assertEquals(DEFAULT_POSTCODE_VALUE, response);
        assertEquals(FIRST_NAME_KEY, modifiedFields.get(0).getValue().getFieldName());
        assertEquals(FIRST_NAME_VALUE.toUpperCase(), modifiedFields.get(0).getValue().getOriginalValue());
    }

    @Test
    void getPostcodeIfNotNull() {
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        String response = OCRFieldExtractor.getDefaultPostcodeIfInvalid(ocrFields, VALID_POSTCODE_KEY,
                modifiedFields);
        assertEquals(VALID_POSTCODE_VALUE, response);
        assertTrue(modifiedFields.isEmpty());
    }

    // Date of Birth
    @Test
    void getDefaultDobIfNull() {
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        String response = OCRFieldExtractor.getDefaultDateOfBirthIfInvalid(ocrFields, SOME_KEY_WITH_NULL_VALUE,
                modifiedFields);
        assertEquals(DEFAULT_DOB_VALUE, response);
        assertEquals(SOME_KEY_WITH_NULL_VALUE, modifiedFields.get(0).getValue().getFieldName());
        assertNull(modifiedFields.get(0).getValue().getOriginalValue());
    }

    @Test
    void getDefaultDobIfNoValue() {
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        String response = OCRFieldExtractor.getDefaultDateOfBirthIfInvalid(ocrFields, SOME_KEY_WITH_NO_VALUE,
                modifiedFields);
        assertEquals(DEFAULT_DOB_VALUE, response);
        assertEquals(SOME_KEY_WITH_NO_VALUE, modifiedFields.get(0).getValue().getFieldName());
        assertNull(modifiedFields.get(0).getValue().getOriginalValue());
    }

    //Needs further validation check inside concrete class
    @Test
    void getDefaultDobIfEmptyValue() {
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        String response = OCRFieldExtractor.getDefaultDateOfBirthIfInvalid(ocrFields, SOME_KEY_WITH_EMPTY_VALUE,
                modifiedFields);
        assertEquals(DEFAULT_DOB_VALUE, response);
        assertEquals(SOME_KEY_WITH_EMPTY_VALUE, modifiedFields.get(0).getValue().getFieldName());
        assertEquals("", modifiedFields.get(0).getValue().getOriginalValue());
    }

    //Needs further validation check inside concrete class
    @Test
    void getDefaultDobIfInvalid() {
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        String response = OCRFieldExtractor.getDefaultDateOfBirthIfInvalid(ocrFields, FIRST_NAME_KEY,
                modifiedFields);
        assertEquals(DEFAULT_DOB_VALUE, response);
        assertEquals(FIRST_NAME_KEY, modifiedFields.get(0).getValue().getFieldName());
        assertEquals(FIRST_NAME_VALUE.toUpperCase(), modifiedFields.get(0).getValue().getOriginalValue());
    }

    @Test
    void getDobIfNotNull() {
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        String response = OCRFieldExtractor.getDefaultDateOfBirthIfInvalid(ocrFields, VALID_POSTCODE_KEY,
                modifiedFields);
        assertEquals(VALID_POSTCODE_VALUE, response);
        assertTrue(modifiedFields.isEmpty());
    }

    // Application Reference Value
    @Test
    void getDefaultSolsSolicitorAppReferenceIfNull() {
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        String response = OCRFieldExtractor.getDefaultSolsAppReferenceIfInvalid(ocrFields, SOME_KEY_WITH_NULL_VALUE,
                modifiedFields);
        assertEquals(DEFAULT_SOLS_APP_REFERENCE_DOB_VALUE, response);
        assertEquals(SOME_KEY_WITH_NULL_VALUE, modifiedFields.get(0).getValue().getFieldName());
        assertNull(modifiedFields.get(0).getValue().getOriginalValue());
    }

    @Test
    void getDefaultSolsSolicitorAppReferenceIfNoValue() {
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        String response = OCRFieldExtractor.getDefaultSolsAppReferenceIfInvalid(ocrFields, SOME_KEY_WITH_NO_VALUE,
                modifiedFields);
        assertEquals(DEFAULT_SOLS_APP_REFERENCE_DOB_VALUE, response);
        assertEquals(SOME_KEY_WITH_NO_VALUE, modifiedFields.get(0).getValue().getFieldName());
        assertNull(modifiedFields.get(0).getValue().getOriginalValue());
    }

    //Needs further validation check inside concrete class
    @Test
    void getDefaultSolsSolicitorAppReferenceIfEmptyValue() {
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        String response = OCRFieldExtractor.getDefaultSolsAppReferenceIfInvalid(ocrFields, SOME_KEY_WITH_EMPTY_VALUE,
                modifiedFields);
        assertEquals(DEFAULT_SOLS_APP_REFERENCE_DOB_VALUE, response);
        assertEquals(SOME_KEY_WITH_EMPTY_VALUE, modifiedFields.get(0).getValue().getFieldName());
        assertEquals("", modifiedFields.get(0).getValue().getOriginalValue());
    }

    //Needs further validation check inside concrete class
    @Test
    void getDefaultSolsSolicitorAppReferenceIfInvalid() {
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        String response = OCRFieldExtractor.getDefaultSolsAppReferenceIfInvalid(ocrFields, FIRST_NAME_KEY,
                modifiedFields);
        assertEquals(DEFAULT_SOLS_APP_REFERENCE_DOB_VALUE, response);
        assertEquals(FIRST_NAME_KEY, modifiedFields.get(0).getValue().getFieldName());
        assertEquals(FIRST_NAME_VALUE.toUpperCase(), modifiedFields.get(0).getValue().getOriginalValue());
    }

    @Test
    void getDefaultSolicitorAppReferenceIfNotNull() {
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        String response = OCRFieldExtractor.getDefaultSolsAppReferenceIfInvalid(ocrFields, VALID_POSTCODE_KEY,
                modifiedFields);
        assertEquals(VALID_POSTCODE_VALUE, response);
        assertTrue(modifiedFields.isEmpty());
    }

    // Email Value
    @Test
    void getDefaultSolsSolicitorEmailIfNull() {
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        String response = OCRFieldExtractor.getDefaultEmailIfInvalid(ocrFields, SOME_KEY_WITH_NULL_VALUE,
                modifiedFields);
        assertEquals(DEFAULT_EMAIL_VALUE, response);
        assertEquals(SOME_KEY_WITH_NULL_VALUE, modifiedFields.get(0).getValue().getFieldName());
        assertNull(modifiedFields.get(0).getValue().getOriginalValue());
    }

    @Test
    void getDefaultSolsSolicitorEmailIfNoValue() {
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        String response = OCRFieldExtractor.getDefaultEmailIfInvalid(ocrFields, SOME_KEY_WITH_NO_VALUE,
                modifiedFields);
        assertEquals(DEFAULT_EMAIL_VALUE, response);
        assertEquals(SOME_KEY_WITH_NO_VALUE, modifiedFields.get(0).getValue().getFieldName());
        assertNull(modifiedFields.get(0).getValue().getOriginalValue());
    }

    //Needs further validation check inside concrete class
    @Test
    void getDefaultSolsSolicitorEmailIfEmptyValue() {
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        String response = OCRFieldExtractor.getDefaultEmailIfInvalid(ocrFields, SOME_KEY_WITH_EMPTY_VALUE,
                modifiedFields);
        assertEquals(DEFAULT_EMAIL_VALUE, response);
        assertEquals(SOME_KEY_WITH_EMPTY_VALUE, modifiedFields.get(0).getValue().getFieldName());
        assertEquals("", modifiedFields.get(0).getValue().getOriginalValue());
    }

    //Needs further validation check inside concrete class
    @Test
    void getDefaultSolsSolicitorEmailIfInvalid() {
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        String response = OCRFieldExtractor.getDefaultEmailIfInvalid(ocrFields, FIRST_NAME_KEY,
                modifiedFields);
        assertEquals(DEFAULT_EMAIL_VALUE, response);
        assertEquals(FIRST_NAME_KEY, modifiedFields.get(0).getValue().getFieldName());
        assertEquals(FIRST_NAME_VALUE.toUpperCase(), modifiedFields.get(0).getValue().getOriginalValue());
    }

    //Do we want emails to be capitalised?
    @Test
    void getDefaultSolsSolicitorEmailIfNotNull() {
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        String response = OCRFieldExtractor.getDefaultEmailIfInvalid(ocrFields, VALID_EMAIL_KEY,
                modifiedFields);
        assertEquals(VALID_EMAIL_VALUE, response);
        assertTrue(modifiedFields.isEmpty());
    }

    // Phone Value
    @Test
    void getDefaultSolsSolicitorPhoneNumberIfNull() {
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        String response = OCRFieldExtractor.getDefaultPhoneNumberIfInvalid(ocrFields, SOME_KEY_WITH_NULL_VALUE,
                modifiedFields);
        assertEquals(DEFAULT_PHONE_VALUE, response);
        assertEquals(SOME_KEY_WITH_NULL_VALUE, modifiedFields.get(0).getValue().getFieldName());
        assertNull(modifiedFields.get(0).getValue().getOriginalValue());
    }

    @Test
    void getDefaultSolsSolicitorPhoneNumberIfNoValue() {
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        String response = OCRFieldExtractor.getDefaultPhoneNumberIfInvalid(ocrFields, SOME_KEY_WITH_NO_VALUE,
                modifiedFields);
        assertEquals(DEFAULT_PHONE_VALUE, response);
        assertEquals(SOME_KEY_WITH_NO_VALUE, modifiedFields.get(0).getValue().getFieldName());
        assertNull(modifiedFields.get(0).getValue().getOriginalValue());
    }

    //Needs further validation check inside concrete class
    @Test
    void getDefaultSolsSolicitorPhoneNumberIfEmptyValue() {
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        String response = OCRFieldExtractor.getDefaultPhoneNumberIfInvalid(ocrFields, SOME_KEY_WITH_EMPTY_VALUE,
                modifiedFields);
        assertEquals(DEFAULT_PHONE_VALUE, response);
        assertEquals(SOME_KEY_WITH_EMPTY_VALUE, modifiedFields.get(0).getValue().getFieldName());
        assertEquals("", modifiedFields.get(0).getValue().getOriginalValue());
    }

    //Needs further validation check inside concrete class
    @Test
    void getDefaultSolsSolicitorPhoneNumberIfInvalid() {
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        String response = OCRFieldExtractor.getDefaultPhoneNumberIfInvalid(ocrFields, FIRST_NAME_KEY,
                modifiedFields);
        assertEquals(DEFAULT_PHONE_VALUE, response);
        assertEquals(FIRST_NAME_KEY, modifiedFields.get(0).getValue().getFieldName());
        assertEquals(FIRST_NAME_VALUE.toUpperCase(), modifiedFields.get(0).getValue().getOriginalValue());
    }

    @Test
    void getDefaultSolsSolicitorPhoneNumberIfNotNull() {
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        String response = OCRFieldExtractor.getDefaultPhoneNumberIfInvalid(ocrFields, VALID_PHONE_KEY,
                modifiedFields);
        assertEquals(VALID_PHONE_VALUE, response);
        assertTrue(modifiedFields.isEmpty());
    }

    // Deceased any other names Value
    @Test
    void getDefaultDeceasedAnyOtherNamesIfNull() {
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        String response = OCRFieldExtractor.getDefaultDeceasedAnyOtherNamesIfInvalid(ocrFields, SOME_KEY_WITH_NULL_VALUE,
                modifiedFields);
        assertEquals(DEFAULT_DECEASED_ANY_OTHER_NAMES_VALUE, response);
        assertEquals(SOME_KEY_WITH_NULL_VALUE, modifiedFields.get(0).getValue().getFieldName());
        assertNull(modifiedFields.get(0).getValue().getOriginalValue());
    }

    @Test
    void getDefaultDeceasedAnyOtherNamesIfNoValue() {
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        String response = OCRFieldExtractor.getDefaultDeceasedAnyOtherNamesIfInvalid(ocrFields, SOME_KEY_WITH_NO_VALUE,
                modifiedFields);
        assertEquals(DEFAULT_DECEASED_ANY_OTHER_NAMES_VALUE, response);
        assertEquals(SOME_KEY_WITH_NO_VALUE, modifiedFields.get(0).getValue().getFieldName());
        assertNull(modifiedFields.get(0).getValue().getOriginalValue());
    }

    //Needs further validation check inside concrete class
    @Test
    void getDefaultDeceasedAnyOtherNamesIfEmptyValue() {
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        String response = OCRFieldExtractor.getDefaultDeceasedAnyOtherNamesIfInvalid(ocrFields, SOME_KEY_WITH_EMPTY_VALUE,
                modifiedFields);
        assertEquals(DEFAULT_DECEASED_ANY_OTHER_NAMES_VALUE, response);
        assertEquals(SOME_KEY_WITH_EMPTY_VALUE, modifiedFields.get(0).getValue().getFieldName());
        assertEquals("", modifiedFields.get(0).getValue().getOriginalValue());
    }

    //Needs further validation check inside concrete class
    @Test
    void getDefaultDeceasedAnyOtherNamesIfInvalid() {
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        String response = OCRFieldExtractor.getDefaultDeceasedAnyOtherNamesIfInvalid(ocrFields, FIRST_NAME_KEY,
                modifiedFields);
        assertEquals(DEFAULT_DECEASED_ANY_OTHER_NAMES_VALUE, response);
        assertEquals(FIRST_NAME_KEY, modifiedFields.get(0).getValue().getFieldName());
        assertEquals(FIRST_NAME_VALUE.toUpperCase(), modifiedFields.get(0).getValue().getOriginalValue());
    }

    @Test
    void getDeceasedAnyOtherNamesIfNotNull() {
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        String response = OCRFieldExtractor.getDefaultDeceasedAnyOtherNamesIfInvalid(ocrFields, VALID_PHONE_KEY,
                modifiedFields);
        assertEquals(VALID_PHONE_VALUE, response);
        assertTrue(modifiedFields.isEmpty());
    }

    // Deceased Domicile in Eng/Wales Value
    @Test
    void getDefaultDeceasedDomicileInEngWalesIfNull() {
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        String response = OCRFieldExtractor.getDefaultDomiciledInEngWalesIfInvalid(ocrFields, SOME_KEY_WITH_NULL_VALUE,
                modifiedFields);
        assertEquals(DEFAULT_DECEASED_DOMICILE_IN_ENG_WALES_VALUE, response);
        assertEquals(SOME_KEY_WITH_NULL_VALUE, modifiedFields.get(0).getValue().getFieldName());
        assertNull(modifiedFields.get(0).getValue().getOriginalValue());
    }

    @Test
    void getDefaultDeceasedDomicileInEngWalesIfNoValue() {
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        String response = OCRFieldExtractor.getDefaultDomiciledInEngWalesIfInvalid(ocrFields, SOME_KEY_WITH_NO_VALUE,
                modifiedFields);
        assertEquals(DEFAULT_DECEASED_DOMICILE_IN_ENG_WALES_VALUE, response);
        assertEquals(SOME_KEY_WITH_NO_VALUE, modifiedFields.get(0).getValue().getFieldName());
        assertNull(modifiedFields.get(0).getValue().getOriginalValue());
    }

    //Needs further validation check inside concrete class
    @Test
    void getDefaultDeceasedDomicileInEngWalesIfEmptyValue() {
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        String response = OCRFieldExtractor.getDefaultDomiciledInEngWalesIfInvalid(ocrFields, SOME_KEY_WITH_EMPTY_VALUE,
                modifiedFields);
        assertEquals(DEFAULT_DECEASED_DOMICILE_IN_ENG_WALES_VALUE, response);
        assertEquals(SOME_KEY_WITH_EMPTY_VALUE, modifiedFields.get(0).getValue().getFieldName());
        assertEquals("", modifiedFields.get(0).getValue().getOriginalValue());
    }

    //Needs further validation check inside concrete class
    @Test
    void getDefaultDeceasedDomicileInEngWalesIfInvalid() {
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        String response = OCRFieldExtractor.getDefaultDomiciledInEngWalesIfInvalid(ocrFields, FIRST_NAME_KEY,
                modifiedFields);
        assertEquals(DEFAULT_DECEASED_DOMICILE_IN_ENG_WALES_VALUE, response);
        assertEquals(FIRST_NAME_KEY, modifiedFields.get(0).getValue().getFieldName());
        assertEquals(FIRST_NAME_VALUE.toUpperCase(), modifiedFields.get(0).getValue().getOriginalValue());
    }

    @Test
    void getDeceasedDomicileInEngWalesIfNotNull() {
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        String response = OCRFieldExtractor.getDefaultDomiciledInEngWalesIfInvalid(ocrFields, VALID_DOMICILE_IN_ENG_WALES_KEY,
                modifiedFields);
        assertEquals(VALID_DOMICILE_IN_ENG_WALES_VALUE, response);
        assertTrue(modifiedFields.isEmpty());
    }

    // SolsSolicitorIsApplying Value
    @Test
    void getDefaultSolsSolicitorIsApplyingIfNull() {
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        String response = OCRFieldExtractor.getDefaultDomiciledInEngWalesIfInvalid(ocrFields, SOME_KEY_WITH_NULL_VALUE,
                modifiedFields);
        assertEquals(DEFAULT_SOLS_SOLICITOR_IS_APPLYING_VALUE, response);
        assertEquals(SOME_KEY_WITH_NULL_VALUE, modifiedFields.get(0).getValue().getFieldName());
        assertNull(modifiedFields.get(0).getValue().getOriginalValue());
    }

    @Test
    void getDefaultSolsSolicitorIsApplyingIfNoValue() {
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        String response = OCRFieldExtractor.getDefaultDomiciledInEngWalesIfInvalid(ocrFields, SOME_KEY_WITH_NO_VALUE,
                modifiedFields);
        assertEquals(DEFAULT_SOLS_SOLICITOR_IS_APPLYING_VALUE, response);
        assertEquals(SOME_KEY_WITH_NO_VALUE, modifiedFields.get(0).getValue().getFieldName());
        assertNull(modifiedFields.get(0).getValue().getOriginalValue());
    }

    //Needs further validation check inside concrete class
    @Test
    void getDefaultSolsSolicitorIsApplyingIfEmptyValue() {
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        String response = OCRFieldExtractor.getDefaultDomiciledInEngWalesIfInvalid(ocrFields, SOME_KEY_WITH_EMPTY_VALUE,
                modifiedFields);
        assertEquals(DEFAULT_SOLS_SOLICITOR_IS_APPLYING_VALUE, response);
        assertEquals(SOME_KEY_WITH_EMPTY_VALUE, modifiedFields.get(0).getValue().getFieldName());
        assertEquals("", modifiedFields.get(0).getValue().getOriginalValue());
    }

    //Needs further validation check inside concrete class
    @Test
    void getDefaultSolsSolicitorIsApplyingIfInvalid() {
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        String response = OCRFieldExtractor.getDefaultDomiciledInEngWalesIfInvalid(ocrFields, FIRST_NAME_KEY,
                modifiedFields);
        assertEquals(DEFAULT_SOLS_SOLICITOR_IS_APPLYING_VALUE, response);
        assertEquals(FIRST_NAME_KEY, modifiedFields.get(0).getValue().getFieldName());
        assertEquals(FIRST_NAME_VALUE.toUpperCase(), modifiedFields.get(0).getValue().getOriginalValue());
    }

    @Test
    void getSolsSolicitorIsApplyingIfNotNull() {
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        String response = OCRFieldExtractor.getDefaultDomiciledInEngWalesIfInvalid(ocrFields, VALID_SOLS_SOLICITOR_IS_APPLYING_KEY,
                modifiedFields);
        assertEquals(VALID_SOLS_SOLICITOR_IS_APPLYING_VALUE, response);
        assertTrue(modifiedFields.isEmpty());
    }

    // solsSolicitorRepresentativeName Value
    @Test
    void getDefaultSolsSolicitorRepresentativeNameIfNull() {
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        String response = OCRFieldExtractor.getDefaultDomiciledInEngWalesIfInvalid(ocrFields, SOME_KEY_WITH_NULL_VALUE,
                modifiedFields);
        assertEquals(DEFAULT_SOLS_SOLICITOR_REPRESENTATIVE_NAME_VALUE, response);
        assertEquals(SOME_KEY_WITH_NULL_VALUE, modifiedFields.get(0).getValue().getFieldName());
        assertNull(modifiedFields.get(0).getValue().getOriginalValue());
    }

    @Test
    void getDefaultSolsSolicitorRepresentativeNameIfNoValue() {
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        String response = OCRFieldExtractor.getDefaultDomiciledInEngWalesIfInvalid(ocrFields, SOME_KEY_WITH_NO_VALUE,
                modifiedFields);
        assertEquals(DEFAULT_SOLS_SOLICITOR_REPRESENTATIVE_NAME_VALUE, response);
        assertEquals(SOME_KEY_WITH_NO_VALUE, modifiedFields.get(0).getValue().getFieldName());
        assertNull(modifiedFields.get(0).getValue().getOriginalValue());
    }

    //Needs further validation check inside concrete class
    @Test
    void getDefaultSolsSolicitorRepresentativeNameIfEmptyValue() {
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        String response = OCRFieldExtractor.getDefaultDomiciledInEngWalesIfInvalid(ocrFields, SOME_KEY_WITH_EMPTY_VALUE,
                modifiedFields);
        assertEquals(DEFAULT_SOLS_SOLICITOR_REPRESENTATIVE_NAME_VALUE, response);
        assertEquals(SOME_KEY_WITH_EMPTY_VALUE, modifiedFields.get(0).getValue().getFieldName());
        assertEquals("", modifiedFields.get(0).getValue().getOriginalValue());
    }

    //Needs further validation check inside concrete class
    @Test
    void getDefaultSolsSolicitorRepresentativeNameIfInvalid() {
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        String response = OCRFieldExtractor.getDefaultDomiciledInEngWalesIfInvalid(ocrFields, FIRST_NAME_KEY,
                modifiedFields);
        assertEquals(DEFAULT_SOLS_SOLICITOR_REPRESENTATIVE_NAME_VALUE, response);
        assertEquals(FIRST_NAME_KEY, modifiedFields.get(0).getValue().getFieldName());
        assertEquals(FIRST_NAME_VALUE.toUpperCase(), modifiedFields.get(0).getValue().getOriginalValue());
    }

    //Caps?
    @Test
    void getSolsSolicitorRepresentativeNameIfNotNull() {
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        String response = OCRFieldExtractor.getDefaultDomiciledInEngWalesIfInvalid(ocrFields, VALID_SOLS_SOLICITOR_REPRESENTATIVE_NAME_KEY,
                modifiedFields);
        assertEquals(VALID_SOLS_SOLICITOR_REPRESENTATIVE_NAME_VALUE.toUpperCase(), response);
        assertTrue(modifiedFields.isEmpty());
    }
}
