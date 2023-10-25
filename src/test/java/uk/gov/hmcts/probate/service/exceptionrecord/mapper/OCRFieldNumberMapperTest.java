package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.OCRMappingException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Component
class OCRFieldNumberMapperTest {

    private OCRFieldNumberMapper ocrFieldNumberMapper = new OCRFieldNumberMapper();

    @Test
    void testStringReturnedAsLong() {
        Long response = ocrFieldNumberMapper.stringToLong("extraCopiesOfGrant", "3423");
        assertEquals(Long.valueOf(3423L), response);
    }

    @Test
    void testEmptyStringRetunsNull() {
        Long response = ocrFieldNumberMapper.stringToLong("extraCopiesOfGrant", "");
        assertEquals(null, response);
    }

    @Test
    void testInvalidStringThrowsException() {
        assertThrows(OCRMappingException.class, () -> {
            long response = ocrFieldNumberMapper.stringToLong("extraCopiesOfGrant", "Not a number");
        });
    }

    @Test
    void testInvalidStringExceptionMessage() {
        OCRMappingException expectedEx = assertThrows(OCRMappingException.class, () -> {
            ocrFieldNumberMapper.stringToLong("extraCopiesOfGrant", "Not a number");
        });
        assertEquals("extraCopiesOfGrant"
                        + ": Numerical field 'Not a number' could not be converted to a Long number: "
                + "For input string: \"Not a number\"", expectedEx.getMessage());
    }
}
