package uk.gov.hmcts.probate.service.filebuilder;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class FileExtractDateFormatter {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
    private static final DateTimeFormatter DATE_FORMAT_FOOTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    public String formatDataDate(LocalDate date) {
        return DATE_FORMAT.format(date).toUpperCase();
    }

    public String formatFileDate() {
        return DATE_FORMAT_FOOTER.format(LocalDate.now()).toUpperCase();
    }
}
