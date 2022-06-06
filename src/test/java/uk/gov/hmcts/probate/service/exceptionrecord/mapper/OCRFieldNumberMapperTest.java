package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.OCRMappingException;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Component
public class OCRFieldNumberMapperTest {

    private OCRFieldNumberMapper ocrFieldNumberMapper = new OCRFieldNumberMapper();

    @Test
    public void testStringReturnedAsLong() {
        Long response = ocrFieldNumberMapper.stringToLong("3423");
        assertEquals(Long.valueOf(3423L), response);
    }

    @Test
    public void testEmptyStringRetunsNull() {
        Long response = ocrFieldNumberMapper.stringToLong("");
        assertEquals(null, response);
    }

    @Test
    public void testInvalidStringThrowsException() {
        assertThrows(OCRMappingException.class, () -> {
            long response = ocrFieldNumberMapper.stringToLong("Not a number");
        });
    }
}
