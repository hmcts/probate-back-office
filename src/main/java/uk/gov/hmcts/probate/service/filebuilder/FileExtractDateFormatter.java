package uk.gov.hmcts.probate.service.filebuilder;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class FileExtractDateFormatter {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
    private static final DateTimeFormatter DATE_FORMAT_REQUEST = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_FORMAT_IRON_MOUNTAIN = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter DATE_FORMAT_HMRC = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    public String formatDataDate(LocalDate date) {
        return DATE_FORMAT.format(date).toUpperCase();
    }

    public String getHMRCFormattedFileDate(String date, LocalDateTime now) {
        LocalDate dateUsed = LocalDate.from(DATE_FORMAT_REQUEST.parse(date));
        LocalDateTime dateUsedWithNowTime = dateUsed.atTime(now.getHour(), now.getMinute(), now.getSecond());
        return DATE_FORMAT_HMRC.format(dateUsedWithNowTime).toUpperCase();
    }
    
    public String getIronMountainFormattedFileDate(String date) {
        LocalDate dateUsed = LocalDate.from(DATE_FORMAT_REQUEST.parse(date));
        return DATE_FORMAT_IRON_MOUNTAIN.format(dateUsed).toUpperCase();
    }
}
