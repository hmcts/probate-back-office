package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.OCRMappingException;
import uk.gov.hmcts.probate.model.Constants;
import uk.gov.hmcts.reform.probate.model.cases.MaritalStatus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Component
public class OCRFieldYesOrNoMapperTest {

    private OCRFieldYesOrNoMapper yesOrNoMapper = new OCRFieldYesOrNoMapper();

    @Test
    public void testTrueAsYesState() {
        Boolean response = yesOrNoMapper.toYesOrNo("true");
        assertEquals(true, response);
    }

    @Test
    public void testYesAsYesState() {
        Boolean response = yesOrNoMapper.toYesOrNo("yes");
        assertEquals(true, response);
    }

    @Test
    public void testFalseAsNoState() {
        Boolean response = yesOrNoMapper.toYesOrNo("false");
        assertEquals(false, response);
    }

    @Test
    public void testNoAsNoState() {
        Boolean response = yesOrNoMapper.toYesOrNo("no");
        assertEquals(false, response);
    }

    @Test
    public void testEmptyStringAsNullState() {
        Boolean response = yesOrNoMapper.toYesOrNo("");
        assertEquals(null, response);
    }

    @Test
    public void testNullAsNullState() {
        Boolean response = yesOrNoMapper.toYesOrNo(null);
        assertEquals(null, response);
    }

    @Test(expected = OCRMappingException.class)
    public void testYesNoError() {
        Boolean response = yesOrNoMapper.toYesOrNo("notfound");
        assertTrue(false);
    }
}