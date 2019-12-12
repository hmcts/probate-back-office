package uk.gov.hmcts.probate.service.filebuilder;

import org.junit.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;

public class FileExtractDateFormatterTest {

    private final FileExtractDateFormatter fileExtractDateFormatter = new FileExtractDateFormatter();

    @Test
    public void shouldFormatDataDate() {
        LocalDate data = LocalDate.of(1999, 12, 31);
        assertEquals("31-DEC-1999", fileExtractDateFormatter.formatDataDate(data));
    }

    @Test
    public void shouldFormatFooterDate() {
        LocalDate data = LocalDate.now();
        String expected = DateTimeFormatter.ofPattern("yyyyMMdd").format(data);
        assertEquals(expected, fileExtractDateFormatter.formatFileDate());
    }
}