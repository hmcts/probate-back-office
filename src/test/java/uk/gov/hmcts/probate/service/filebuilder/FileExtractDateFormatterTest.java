package uk.gov.hmcts.probate.service.filebuilder;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileExtractDateFormatterTest {

    private final FileExtractDateFormatter fileExtractDateFormatter = new FileExtractDateFormatter();

    @Test
    void shouldFormatDataDate() {
        LocalDate date = LocalDate.of(1999, 12, 31);
        assertEquals("31-DEC-1999", fileExtractDateFormatter.formatDataDate(date));
    }

    @Test
    void shouldFormatIronMountainFileDate() {
        LocalDate date = LocalDate.now();
        String fileDate = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(date);
        String expected = DateTimeFormatter.ofPattern("yyyyMMdd").format(date);
        assertEquals(expected, fileExtractDateFormatter.getIronMountainFormattedFileDate(fileDate));
    }

    @Test
    void shouldFormatHmrcFileDate() {
        TemporalAccessor fileDate = (DateTimeFormatter.ofPattern("yyyy-MM-dd").parse("2020-12-31"));
        TemporalAccessor nowDateTime = (DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss").parse("20201231-123456"));
        String fileDateString = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(fileDate);
        String expectedString = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss").format(nowDateTime);
        assertEquals(expectedString,
            fileExtractDateFormatter.getHmrcFormattedFileDate(fileDateString, LocalDateTime.from(nowDateTime)));
    }
}
