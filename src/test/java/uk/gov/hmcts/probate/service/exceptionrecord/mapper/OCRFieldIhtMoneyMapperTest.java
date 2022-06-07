package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.probate.exception.OCRMappingException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OCRFieldIhtMoneyMapperTest {

    OCRFieldIhtMoneyMapper ocrFieldIhtMoneyMapper = new OCRFieldIhtMoneyMapper();

    private static final String MONETARY_TEST_VALUE_INPUT = "125.50";
    private static final Long MONETARY_TEST_VALUE_PENNIES = 12550L;
    private static final String MONETARY_TEST_UNKNOWN_VALUE = "Twenty two pounds";

    @Test
    void testPoundsToPennies() {
        Long response = ocrFieldIhtMoneyMapper.poundsToPennies(MONETARY_TEST_VALUE_INPUT);
        assertEquals(MONETARY_TEST_VALUE_PENNIES, response);
    }

    @Test
    void testExceptionForToPenniesNotNumeric() throws Exception {
        OCRMappingException expectedEx = assertThrows(OCRMappingException.class, () -> {
            ocrFieldIhtMoneyMapper.poundsToPennies(MONETARY_TEST_UNKNOWN_VALUE);
        });
        assertEquals("Monetary field '" + MONETARY_TEST_UNKNOWN_VALUE
                + "' could not be converted to a number", expectedEx.getMessage());

    }
}
