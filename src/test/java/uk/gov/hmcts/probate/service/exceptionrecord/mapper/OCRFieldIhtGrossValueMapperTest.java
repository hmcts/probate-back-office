package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.exception.OCRMappingException;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
class OCRFieldIhtGrossValueMapperTest {

    OCRFieldIhtGrossValueMapper ocrFieldIhtGrossValueMapper = new OCRFieldIhtGrossValueMapper();

    private static final String MONETARY_TEST_VALUE_INPUT = "125.50";
    private static final Long MONETARY_TEST_VALUE_PENNIES = 12550L;
    private static final String MONETARY_TEST_UNKNOWN_VALUE = "Twenty two pounds";
    private static final String VERSION_3 = "3";
    private static final String TRUE = "true";
    private static final String FALSE = "false";

    @Test
    void shouldConvertIHTGrossValueNoFormVersion() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .ihtGrossValue(MONETARY_TEST_VALUE_INPUT)
                .build();
        Long response = ocrFieldIhtGrossValueMapper.toIHTGrossValue(ocrFields);
        assertEquals(MONETARY_TEST_VALUE_PENNIES, response);
    }

    @Test
    void testExceptionForToPenniesNotNumeric() throws Exception {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .ihtGrossValue(MONETARY_TEST_UNKNOWN_VALUE)
                .build();
        OCRMappingException expectedEx = assertThrows(OCRMappingException.class, () -> {
            ocrFieldIhtGrossValueMapper.toIHTGrossValue(ocrFields);
        });
        assertEquals("IhtGrossValue: Monetary field '" + MONETARY_TEST_UNKNOWN_VALUE
                + "' could not be converted to a number", expectedEx.getMessage());

    }

    @Test
    void shouldConvertIHT421GrossDiedAfterSwitchDateVersion3() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .deceasedDiedOnAfterSwitchDate(TRUE)
                .iht400421Completed(TRUE)
                .iht421grossValue(MONETARY_TEST_VALUE_INPUT)
                .formVersion(VERSION_3)
                .build();
        Long response = ocrFieldIhtGrossValueMapper.toIHTGrossValue(ocrFields);
        assertEquals(MONETARY_TEST_VALUE_PENNIES, response);
    }

    @Test
    void shouldConvertIHT400GrossDiedAfterSwitchDateVersion3() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .deceasedDiedOnAfterSwitchDate(TRUE)
                .iht400Completed(TRUE)
                .probateGrossValueIht400(MONETARY_TEST_VALUE_INPUT)
                .formVersion(VERSION_3)
                .build();
        Long response = ocrFieldIhtGrossValueMapper.toIHTGrossValue(ocrFields);
        assertEquals(MONETARY_TEST_VALUE_PENNIES, response);
    }

    @Test
    void shouldConvertIHT207GrossDiedAfterSwitchDateVersion3() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .deceasedDiedOnAfterSwitchDate(TRUE)
                .iht207Completed(TRUE)
                .iht207grossValue(MONETARY_TEST_VALUE_INPUT)
                .formVersion(VERSION_3)
                .build();
        Long response = ocrFieldIhtGrossValueMapper.toIHTGrossValue(ocrFields);
        assertEquals(MONETARY_TEST_VALUE_PENNIES, response);
    }

    @Test
    void shouldConvertIHTGrossExceptedEstateDiedAfterSwitchDateVersion3() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .deceasedDiedOnAfterSwitchDate(TRUE)
                .exceptedEstate(TRUE)
                .ihtGrossValueExceptedEstate(MONETARY_TEST_VALUE_INPUT)
                .formVersion(VERSION_3)
                .build();
        Long response = ocrFieldIhtGrossValueMapper.toIHTGrossValue(ocrFields);
        assertEquals(MONETARY_TEST_VALUE_PENNIES, response);
    }

    @Test
    void shouldConvertIHT205GrossDiedBeforeSwitchDateVersion3() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .deceasedDiedOnAfterSwitchDate(FALSE)
                .iht205Completed(TRUE)
                .ihtGrossValue205(MONETARY_TEST_VALUE_INPUT)
                .formVersion(VERSION_3)
                .build();
        Long response = ocrFieldIhtGrossValueMapper.toIHTGrossValue(ocrFields);
        assertEquals(MONETARY_TEST_VALUE_PENNIES, response);
    }

    @Test
    void shouldConvertIHT207GrossDiedBeforeSwitchDateVersion3() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .deceasedDiedOnAfterSwitchDate(FALSE)
                .iht207Completed(TRUE)
                .iht207grossValue(MONETARY_TEST_VALUE_INPUT)
                .formVersion(VERSION_3)
                .build();
        Long response = ocrFieldIhtGrossValueMapper.toIHTGrossValue(ocrFields);
        assertEquals(MONETARY_TEST_VALUE_PENNIES, response);
    }

    @Test
    void shouldConvertIHT400GrossDiedBeforeSwitchDateVersion3() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .deceasedDiedOnAfterSwitchDate(FALSE)
                .iht400Completed(TRUE)
                .probateGrossValueIht400(MONETARY_TEST_VALUE_INPUT)
                .formVersion(VERSION_3)
                .build();
        Long response = ocrFieldIhtGrossValueMapper.toIHTGrossValue(ocrFields);
        assertEquals(MONETARY_TEST_VALUE_PENNIES, response);
    }

    @Test
    void shouldConvertIHT421GrossDiedBeforeSwitchDateVersion3() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .deceasedDiedOnAfterSwitchDate(FALSE)
                .iht400421Completed(TRUE)
                .iht421grossValue(MONETARY_TEST_VALUE_INPUT)
                .formVersion(VERSION_3)
                .build();
        Long response = ocrFieldIhtGrossValueMapper.toIHTGrossValue(ocrFields);
        assertEquals(MONETARY_TEST_VALUE_PENNIES, response);
    }

    @Test
    void shouldThrowOCRMappingException() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .formVersion(VERSION_3)
                .build();

        Exception exception = assertThrows(OCRMappingException.class, () -> {
            ocrFieldIhtGrossValueMapper.toIHTGrossValue(ocrFields);
        });
        assertEquals("No ihtGrossValue mapped", exception.getMessage());
    }
}
