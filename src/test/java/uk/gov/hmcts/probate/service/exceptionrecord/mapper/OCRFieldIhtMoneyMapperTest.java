package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import uk.gov.hmcts.probate.exception.OCRMappingException;
import uk.gov.hmcts.reform.probate.model.IhtFormType;

import static org.junit.Assert.assertEquals;

public class OCRFieldIhtMoneyMapperTest {

    OCRFieldIhtMoneyMapper ocrFieldIhtMoneyMapper = new OCRFieldIhtMoneyMapper();

    private static final String MONETARY_TEST_VALUE_INPUT = "125.50";
    private static final Long MONETARY_TEST_VALUE_PENNIES = 12550L;
    private static final String MONETARY_TEST_UNKNOWN_VALUE = "Twenty two pounds";

    private static final String IHT205_FORM = "IHT205";
    private static final String IHT207_FORM = "IHT207";
    private static final String IHT400421_FORM = "IHT400421";
    private static final String IHT421_FORM = "IHT421";
    private static final String IHT400_FORM = "IHT400";
    private static final String UNKNOWN_FORM = "UNKOWNFORM";

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void testPoundsToPennies() {
        Long response = ocrFieldIhtMoneyMapper.poundsToPennies(MONETARY_TEST_VALUE_INPUT);
        assertEquals(MONETARY_TEST_VALUE_PENNIES, response);
    }

    @Test
    public void testExceptionForToPenniesNotNumeric() throws Exception {
        expectedEx.expect(OCRMappingException.class);
        expectedEx
            .expectMessage("Monetary field '" + MONETARY_TEST_UNKNOWN_VALUE + "' could not be converted to a number");
        Long response = ocrFieldIhtMoneyMapper.poundsToPennies(MONETARY_TEST_UNKNOWN_VALUE);
    }

    @Test
    public void testCorrectFormTypeIHT205() {
        IhtFormType response = ocrFieldIhtMoneyMapper.ihtFormType(IHT205_FORM);
        assertEquals(IhtFormType.optionIHT205, response);
    }

    @Test
    public void testCorrectFormTypeIHT207() {
        IhtFormType response = ocrFieldIhtMoneyMapper.ihtFormType(IHT207_FORM);
        assertEquals(IhtFormType.optionIHT207, response);
    }

    @Test
    public void testCorrectFormTypeIHT400421() {
        IhtFormType response = ocrFieldIhtMoneyMapper.ihtFormType(IHT400421_FORM);
        assertEquals(IhtFormType.optionIHT400421, response);
    }

    @Test
    public void testCorrectFormTypeIHT421() {
        IhtFormType response = ocrFieldIhtMoneyMapper.ihtFormType(IHT421_FORM);
        assertEquals(IhtFormType.optionIHT400421, response);
    }

    @Test
    public void testCorrectFormTypeIHT400() {
        IhtFormType response = ocrFieldIhtMoneyMapper.ihtFormType(IHT400_FORM);
        assertEquals(IhtFormType.optionIHT400421, response);
    }

    @Test(expected = OCRMappingException.class)
    public void testExceptionForUnknownForm5() {
        IhtFormType response = ocrFieldIhtMoneyMapper.ihtFormType(UNKNOWN_FORM);
    }
}
