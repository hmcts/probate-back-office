package uk.gov.hmcts.probate.service.exceptionrecord.utils;

import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.probate.model.ocr.OCRField;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class OCRFieldExtractorTest {

    private List<OCRField> ocrFields = new ArrayList<>();

    private static final String SOME_KEY_WITH_NO_VALUE = "keyWithNoValue";
    private static final String SOME_KEY_WITH_NULL_VALUE = "keyWithNullValue";
    private static final String SOME_KEY_WITH_EMPTY_VALUE = "keyWithEmptyValue";

    private static final String LAST_NAME_KEY = "surname";
    private static final String MIDDLE_NAME_KEY = "middleName";
    private static final String FIRST_NAME_KEY = "firstName";
    private static final String LAST_NAME_VALUE = "Monkhouse";
    private static final String MIDDLE_NAME_VALUE = "Marley";
    private static final String FIRST_NAME_VALUE = "Bob";

    @Before
    public void setUp() {
        ocrFields.add(OCRField.builder().name(FIRST_NAME_KEY).value(FIRST_NAME_VALUE).build());
        ocrFields.add(OCRField.builder().name(MIDDLE_NAME_KEY).value(MIDDLE_NAME_VALUE).build());
        ocrFields.add(OCRField.builder().name(LAST_NAME_KEY).value(LAST_NAME_VALUE).build());
        ocrFields.add(OCRField.builder().name(SOME_KEY_WITH_NO_VALUE).build());
        ocrFields.add(OCRField.builder().name(SOME_KEY_WITH_NULL_VALUE).value(null).build());
        ocrFields.add(OCRField.builder().name(SOME_KEY_WITH_EMPTY_VALUE).value("").build());
    }

    @Test
    public void getValidResponse() {
        String response = OCRFieldExtractor.get(ocrFields, LAST_NAME_KEY);
        assertEquals(LAST_NAME_VALUE, response);
    }

    @Test
    public void getValidTwoParamResponse() {
        String response = OCRFieldExtractor.get(ocrFields, FIRST_NAME_KEY, LAST_NAME_KEY);
        assertEquals(FIRST_NAME_VALUE + " " + LAST_NAME_VALUE, response);
    }

    @Test
    public void getValidThreeParamResponse() {
        String response = OCRFieldExtractor.get(ocrFields, FIRST_NAME_KEY, MIDDLE_NAME_KEY, LAST_NAME_KEY);
        assertEquals(FIRST_NAME_VALUE + " " + MIDDLE_NAME_VALUE + " " + LAST_NAME_VALUE, response);
    }

    @Test
    public void getValidThreeParamResponseWithNoMiddleName() {
        String response = OCRFieldExtractor.get(ocrFields, FIRST_NAME_KEY, null, LAST_NAME_KEY);
        assertEquals(FIRST_NAME_VALUE + " " + LAST_NAME_VALUE, response);
    }

    @Test
    public void getValidThreeParamResponseWithNoMiddleNameOrFirstName() {
        String response = OCRFieldExtractor.get(ocrFields, null, null, LAST_NAME_KEY);
        assertEquals(LAST_NAME_VALUE, response);
    }

    @Test
    public void getNullResponseForMissingValue() {
        String response = OCRFieldExtractor.get(ocrFields, SOME_KEY_WITH_NO_VALUE);
        assertNull(response);
    }

    @Test
    public void getEmptyResponseForEmptyValue() {
        String response = OCRFieldExtractor.get(ocrFields, SOME_KEY_WITH_EMPTY_VALUE);
        assertEquals(response, "");
    }

    @Test
    public void getNullResponseForNullValue() {
        String response = OCRFieldExtractor.get(ocrFields, SOME_KEY_WITH_NULL_VALUE);
        assertNull(response);
    }
}
