package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.OCRMappingException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Component
class OCRFieldYesOrNoMapperTest {

    private OCRFieldYesOrNoMapper yesOrNoMapper = new OCRFieldYesOrNoMapper();

    @Test
    void testTrueAsYesState() {
        Boolean response = yesOrNoMapper.toYesOrNo("true");
        assertEquals(true, response);
    }

    @Test
    void testYesAsYesState() {
        Boolean response = yesOrNoMapper.toYesOrNo("yes");
        assertEquals(true, response);
    }

    @Test
    void testFalseAsNoState() {
        Boolean response = yesOrNoMapper.toYesOrNo("false");
        assertEquals(false, response);
    }

    @Test
    void testNoAsNoState() {
        Boolean response = yesOrNoMapper.toYesOrNo("no");
        assertEquals(false, response);
    }

    @Test
    void testEmptyStringAsNullState() {
        Boolean response = yesOrNoMapper.toYesOrNo("");
        assertEquals(null, response);
    }

    @Test
    void testNullAsNullState() {
        Boolean response = yesOrNoMapper.toYesOrNo(null);
        assertEquals(null, response);
    }

    @Test
    void testYesNoError() {
        assertThrows(OCRMappingException.class, () -> {
            Boolean response = yesOrNoMapper.toYesOrNo("notfound");
            assertTrue(false);
        });
    }
}
