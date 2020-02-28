package uk.gov.hmcts.probate.service.filebuilder;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class FileExtractDateFormatter {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
    private static final DateTimeFormatter DATE_FORMAT_REQUEST = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_FORMAT_FOOTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    public String formatDataDate(LocalDate date) {
        return DATE_FORMAT.format(date).toUpperCase();
    }

    public String getFormattedFileDate(String date) {
        LocalDate dateUsed = LocalDate.from(DATE_FORMAT_REQUEST.parse(date));
        return DATE_FORMAT_FOOTER.format(dateUsed).toUpperCase();
    }
}
