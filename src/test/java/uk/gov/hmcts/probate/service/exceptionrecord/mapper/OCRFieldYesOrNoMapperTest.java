package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.OCRMappingException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Component
class OCRFieldYesOrNoMapperTest {

    private OCRFieldYesOrNoMapper yesOrNoMapper = new OCRFieldYesOrNoMapper();

    @Test
    void testTrueAsYesState() {
        Boolean response = yesOrNoMapper.toYesOrNo("willHasCodicils","true");
        assertEquals(true, response);
    }

    @Test
    void testYesAsYesState() {
        Boolean response = yesOrNoMapper.toYesOrNo("willHasCodicils","yes");
        assertEquals(true, response);
    }

    @Test
    void testFalseAsNoState() {
        Boolean response = yesOrNoMapper.toYesOrNo("willHasCodicils","false");
        assertEquals(false, response);
    }

    @Test
    void testNoAsNoState() {
        Boolean response = yesOrNoMapper.toYesOrNo("willHasCodicils","no");
        assertEquals(false, response);
    }

    @Test
    void testEmptyStringAsNullState() {
        Boolean response = yesOrNoMapper.toYesOrNo("willHasCodicils","");
        assertEquals(null, response);
    }

    @Test
    void testNullAsNullState() {
        Boolean response = yesOrNoMapper.toYesOrNo("willHasCodicils",null);
        assertEquals(null, response);
    }

    @Test
    void testYesNoError() {
        assertThrows(OCRMappingException.class, () -> {
            Boolean response = yesOrNoMapper.toYesOrNo("willHasCodicils","notfound");
            assertTrue(false);
        });
    }
}
