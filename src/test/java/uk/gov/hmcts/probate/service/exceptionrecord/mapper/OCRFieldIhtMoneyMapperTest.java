package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import uk.gov.hmcts.probate.exception.OCRMappingException;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OCRFieldIhtMoneyMapperTest {

    OCRFieldIhtMoneyMapper ocrFieldIhtMoneyMapper = new OCRFieldIhtMoneyMapper();

    private static final String MONETARY_TEST_VALUE_INPUT = "125.50";
    private static final Long MONETARY_TEST_VALUE_PENNIES = 12550L;
    private static final String MONETARY_TEST_UNKNOWN_VALUE = "Twenty two pounds";

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void testPoundsToPennies() {
        Long response = ocrFieldIhtMoneyMapper.poundsToPennies(MONETARY_TEST_VALUE_INPUT);
        assertEquals(MONETARY_TEST_VALUE_PENNIES, response);
    }

    @Test
    public void testExceptionForToPenniesNotNumeric() throws Exception {
        assertThrows(OCRMappingException.class, () -> {
            expectedEx.expect(OCRMappingException.class);
            expectedEx
                .expectMessage("Monetary field '" + MONETARY_TEST_UNKNOWN_VALUE
                        + "' could not be converted to a number");
            Long response = ocrFieldIhtMoneyMapper.poundsToPennies(MONETARY_TEST_UNKNOWN_VALUE);
        });
    }
}
