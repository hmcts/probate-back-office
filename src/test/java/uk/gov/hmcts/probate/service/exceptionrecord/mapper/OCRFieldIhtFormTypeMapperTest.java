package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import uk.gov.hmcts.probate.exception.OCRMappingException;
import uk.gov.hmcts.reform.probate.model.IhtFormType;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

public class OCRFieldIhtFormTypeMapperTest {

    OCRFieldIhtFormTypeMapper ocrFieldIhtFormTypeMapper = new OCRFieldIhtFormTypeMapper();

    private static final String IHT205_FORM = "IHT205";
    private static final String IHT207_FORM = "IHT207";
    private static final String IHT400421_FORM = "IHT400421";
    private static final String IHT421_FORM = "IHT421";
    private static final String IHT400_FORM = "IHT400";
    private static final String UNKNOWN_FORM = "UNKOWNFORM";

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void testCorrectFormTypeIHT205() {
        IhtFormType response = ocrFieldIhtFormTypeMapper.ihtFormType(IHT205_FORM);
        assertEquals(IhtFormType.optionIHT205, response);
    }

    @Test
    public void testCorrectFormTypeIHT207() {
        IhtFormType response = ocrFieldIhtFormTypeMapper.ihtFormType(IHT207_FORM);
        assertEquals(IhtFormType.optionIHT207, response);
    }

    @Test
    public void testCorrectFormTypeIHT400421() {
        IhtFormType response = ocrFieldIhtFormTypeMapper.ihtFormType(IHT400421_FORM);
        assertEquals(IhtFormType.optionIHT400421, response);
    }

    @Test
    public void testCorrectFormTypeIHT421() {
        IhtFormType response = ocrFieldIhtFormTypeMapper.ihtFormType(IHT421_FORM);
        assertEquals(IhtFormType.optionIHT400421, response);
    }

    @Test
    public void testCorrectFormTypeIHT400() {
        IhtFormType response = ocrFieldIhtFormTypeMapper.ihtFormType(IHT400_FORM);
        assertEquals(IhtFormType.optionIHT400421, response);
    }

    @Test(expected = OCRMappingException.class)
    public void testExceptionForUnknownForm5() {
        IhtFormType response = ocrFieldIhtFormTypeMapper.ihtFormType(UNKNOWN_FORM);
    }
}
