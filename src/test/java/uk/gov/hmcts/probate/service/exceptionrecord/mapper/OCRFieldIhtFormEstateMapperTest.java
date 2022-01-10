package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import uk.gov.hmcts.probate.exception.OCRMappingException;
import uk.gov.hmcts.reform.probate.model.IhtFormEstate;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

public class OCRFieldIhtFormEstateMapperTest {

    OCRFieldIhtFormEstateMapper ocrFieldIhtFormEstateMapper = new OCRFieldIhtFormEstateMapper();

    private static final String IHT207_FORM = "IHT207";
    private static final String IHT400421_FORM = "IHT400421";
    private static final String UNKNOWN_FORM = "UNKOWNFORM";

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();


    @Test
    public void testCorrectFormTypeIHT207() {
        IhtFormEstate response = ocrFieldIhtFormEstateMapper.ihtFormEstate(IHT207_FORM);
        assertEquals(IhtFormEstate.optionIHT207, response);
    }

    @Test
    public void testCorrectFormTypeIHT400421() {
        IhtFormEstate response = ocrFieldIhtFormEstateMapper.ihtFormEstate(IHT400421_FORM);
        assertEquals(IhtFormEstate.optionIHT400421, response);
    }

    @Test(expected = OCRMappingException.class)
    public void testExceptionForUnknownForm() {
        IhtFormEstate response = ocrFieldIhtFormEstateMapper.ihtFormEstate(UNKNOWN_FORM);
    }
}
