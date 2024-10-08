package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.probate.exception.OCRMappingException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OCRFieldDefaultLocalDateFieldMapperTest {

    private static final String OCR_DATE_FORMAT = "ddMMyyyy";

    private OCRFieldDefaultLocalDateFieldMapper ocrFieldDefaultLocalDateFieldMapper =
        new OCRFieldDefaultLocalDateFieldMapper();

    @Test
    void testOcrDateFormatCorrect() {
        LocalDate response = ocrFieldDefaultLocalDateFieldMapper.toDefaultDateFieldMember(
                "deceasedDateOfBirth","25122018");
        assertEquals(LocalDate.parse("2018-12-25", DateTimeFormatter.ofPattern("yyyy-MM-dd")), response);
    }

    @Test
    void testOcrDateFormatCorrectWithSlashes() {
        LocalDate response = ocrFieldDefaultLocalDateFieldMapper.toDefaultDateFieldMember(
                "deceasedDateOfBirth","25/12/2018");
        assertEquals(LocalDate.parse("2018-12-25", DateTimeFormatter.ofPattern("yyyy-MM-dd")), response);
    }

    @Test
    void testOcrDateFormatError() {
        assertThrows(OCRMappingException.class, () -> {
            LocalDate response = ocrFieldDefaultLocalDateFieldMapper.toDefaultDateFieldMember(
                    "deceasedDateOfBirth","Garbage");
        });
    }

    @Test
    void testInvalidDOBExceptionMessage() {
        OCRMappingException expectedEx = assertThrows(OCRMappingException.class, () -> {
            ocrFieldDefaultLocalDateFieldMapper.toDefaultDateFieldMember("deceasedDateOfBirth",
                    "2511");
        });
        assertEquals("deceasedDateOfBirth"
                + ": Date field '" + "2511" + "' not in expected format " + OCR_DATE_FORMAT, expectedEx.getMessage());
    }
}
