package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.exception.OCRMappingException;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
class OCRFieldIhtNetValueMapperTest {

    OCRFieldIhtNetValueMapper ocrFieldIhtNetValueMapper = new OCRFieldIhtNetValueMapper();

    private static final String MONETARY_TEST_VALUE_INPUT = "125.50";
    private static final Long MONETARY_TEST_VALUE_PENNIES = 12550L;
    private static final String MONETARY_TEST_UNKNOWN_VALUE = "Twenty two pounds";
    private static final String VERSION_3 = "3";
    private static final String TRUE = "true";
    private static final String FALSE = "false";

    @Test
    void shouldConvertIHTNetValueNoFormVersion() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .ihtNetValue(MONETARY_TEST_VALUE_INPUT)
                .build();
        Long response = ocrFieldIhtNetValueMapper.toIHTNetValue(ocrFields);
        assertEquals(MONETARY_TEST_VALUE_PENNIES, response);
    }

    @Test
    void testExceptionForToPenniesNotNumeric() throws Exception {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .ihtNetValue(MONETARY_TEST_UNKNOWN_VALUE)
                .build();
        OCRMappingException expectedEx = assertThrows(OCRMappingException.class, () -> {
            ocrFieldIhtNetValueMapper.toIHTNetValue(ocrFields);
        });
        assertEquals("IhtNetValue: Monetary field '" + MONETARY_TEST_UNKNOWN_VALUE
                + "' could not be converted to a number", expectedEx.getMessage());

    }

    @Test
    void shouldConvertIHT421NetDiedAfterSwitchDateVersion3() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .deceasedDiedOnAfterSwitchDate(TRUE)
                .iht400421Completed(TRUE)
                .iht421netValue(MONETARY_TEST_VALUE_INPUT)
                .formVersion(VERSION_3)
                .build();
        Long response = ocrFieldIhtNetValueMapper.toIHTNetValue(ocrFields);
        assertEquals(MONETARY_TEST_VALUE_PENNIES, response);
    }

    @Test
    void shouldConvertIHT400NetDiedAfterSwitchDateNotReceivedHmrcLetterVersion3() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .deceasedDiedOnAfterSwitchDate(TRUE)
                .iht400Completed(TRUE)
                .iht400process(FALSE)
                .iht421netValue(MONETARY_TEST_VALUE_INPUT)
                .formVersion(VERSION_3)
                .build();
        Long response = ocrFieldIhtNetValueMapper.toIHTNetValue(ocrFields);
        assertEquals(MONETARY_TEST_VALUE_PENNIES, response);
    }

    @Test
    void shouldConvertIHT400NetDiedAfterSwitchDateReceivedHmrcLetterVersion3() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .deceasedDiedOnAfterSwitchDate(TRUE)
                .iht400Completed(TRUE)
                .iht400process(TRUE)
                .probateNetValueIht400(MONETARY_TEST_VALUE_INPUT)
                .formVersion(VERSION_3)
                .build();
        Long response = ocrFieldIhtNetValueMapper.toIHTNetValue(ocrFields);
        assertEquals(MONETARY_TEST_VALUE_PENNIES, response);
    }

    @Test
    void shouldConvertIHT207NetDiedAfterSwitchDateVersion3() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .deceasedDiedOnAfterSwitchDate(TRUE)
                .iht207Completed(TRUE)
                .iht207netValue(MONETARY_TEST_VALUE_INPUT)
                .formVersion(VERSION_3)
                .build();
        Long response = ocrFieldIhtNetValueMapper.toIHTNetValue(ocrFields);
        assertEquals(MONETARY_TEST_VALUE_PENNIES, response);
    }

    @Test
    void shouldConvertIHTNetExceptedEstateDiedAfterSwitchDateVersion3() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .deceasedDiedOnAfterSwitchDate(TRUE)
                .exceptedEstate(TRUE)
                .ihtNetValueExceptedEstate(MONETARY_TEST_VALUE_INPUT)
                .formVersion(VERSION_3)
                .build();
        Long response = ocrFieldIhtNetValueMapper.toIHTNetValue(ocrFields);
        assertEquals(MONETARY_TEST_VALUE_PENNIES, response);
    }

    @Test
    void shouldConvertIHT205NetDiedBeforeSwitchDateVersion3() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .deceasedDiedOnAfterSwitchDate(FALSE)
                .iht205Completed(TRUE)
                .ihtNetValue205(MONETARY_TEST_VALUE_INPUT)
                .formVersion(VERSION_3)
                .build();
        Long response = ocrFieldIhtNetValueMapper.toIHTNetValue(ocrFields);
        assertEquals(MONETARY_TEST_VALUE_PENNIES, response);
    }

    @Test
    void shouldConvertIHT207NetDiedBeforeSwitchDateVersion3() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .deceasedDiedOnAfterSwitchDate(FALSE)
                .iht207Completed(TRUE)
                .iht207netValue(MONETARY_TEST_VALUE_INPUT)
                .formVersion(VERSION_3)
                .build();
        Long response = ocrFieldIhtNetValueMapper.toIHTNetValue(ocrFields);
        assertEquals(MONETARY_TEST_VALUE_PENNIES, response);
    }

    @Test
    void shouldConvertIHT400NetDiedBeforeSwitchDateReceivedHmrcLetterVersion3() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .deceasedDiedOnAfterSwitchDate(FALSE)
                .iht400Completed(TRUE)
                .iht400process(TRUE)
                .probateNetValueIht400(MONETARY_TEST_VALUE_INPUT)
                .formVersion(VERSION_3)
                .build();
        Long response = ocrFieldIhtNetValueMapper.toIHTNetValue(ocrFields);
        assertEquals(MONETARY_TEST_VALUE_PENNIES, response);
    }

    @Test
    void shouldConvertIHT400NetDiedBeforeSwitchDateNotReceivedHmrcLetterVersion3() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .deceasedDiedOnAfterSwitchDate(FALSE)
                .iht400Completed(TRUE)
                .iht400process(FALSE)
                .iht421netValue(MONETARY_TEST_VALUE_INPUT)
                .formVersion(VERSION_3)
                .build();
        Long response = ocrFieldIhtNetValueMapper.toIHTNetValue(ocrFields);
        assertEquals(MONETARY_TEST_VALUE_PENNIES, response);
    }

    @Test
    void shouldConvertIHT421NetDiedBeforeSwitchDateVersion3() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .deceasedDiedOnAfterSwitchDate(FALSE)
                .iht400421Completed(TRUE)
                .iht421netValue(MONETARY_TEST_VALUE_INPUT)
                .formVersion(VERSION_3)
                .build();
        Long response = ocrFieldIhtNetValueMapper.toIHTNetValue(ocrFields);
        assertEquals(MONETARY_TEST_VALUE_PENNIES, response);
    }

    @Test
    void shouldThrowOCRMappingException() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .formVersion(VERSION_3)
                .build();

        Exception exception = assertThrows(OCRMappingException.class, () -> {
            ocrFieldIhtNetValueMapper.toIHTNetValue(ocrFields);
        });
        assertEquals("No ihtNetValue mapped", exception.getMessage());
    }
}
