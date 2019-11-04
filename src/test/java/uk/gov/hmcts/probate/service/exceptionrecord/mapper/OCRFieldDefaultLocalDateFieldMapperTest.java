package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import org.junit.Test;
import uk.gov.hmcts.probate.exception.OCRMappingException;
import uk.gov.hmcts.reform.probate.model.Relationship;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;

public class OCRFieldDefaultLocalDateFieldMapperTest {

    private static final String OCR_DATE_FORMAT = "ddMMyyyy";

    private OCRFieldDefaultLocalDateFieldMapper ocrFieldDefaultLocalDateFieldMapper = new OCRFieldDefaultLocalDateFieldMapper();

    @Test
    public void testOcrDateFormatCorrect() {
        LocalDate response = ocrFieldDefaultLocalDateFieldMapper.toDefaultDateFieldMember("25122018");
        assertEquals(LocalDate.parse("2018-12-25", DateTimeFormatter.ofPattern("yyyy-MM-dd")), response);
    }

    @Test
    public void testOcrDateFormatCorrectWithSlashes() {
        LocalDate response = ocrFieldDefaultLocalDateFieldMapper.toDefaultDateFieldMember("25/12/2018");
        assertEquals(LocalDate.parse("2018-12-25", DateTimeFormatter.ofPattern("yyyy-MM-dd")), response);
    }

    @Test(expected = OCRMappingException.class)
    public void testOcrDateFormatError() {
        LocalDate response = ocrFieldDefaultLocalDateFieldMapper.toDefaultDateFieldMember("Garbage");
    }
}
