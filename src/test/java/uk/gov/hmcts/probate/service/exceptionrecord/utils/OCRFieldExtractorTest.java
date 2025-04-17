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
    private static final String VALID_POSTCODE_KEY = "deceasedAddressPostCode";
    private static final String LAST_NAME_VALUE = "Monkhouse";
    private static final String MIDDLE_NAME_VALUE = "Marley";
    private static final String FIRST_NAME_VALUE = "Bob";
    private static final String DEFAULT_POSTCODE_VALUE = "MI55 1NG";
    private static final String VALID_POSTCODE_VALUE = "SW1A 1AA";
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

        when(bulkScanConfig.getPostcode()).thenReturn(DEFAULT_POSTCODE_VALUE);
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
}
