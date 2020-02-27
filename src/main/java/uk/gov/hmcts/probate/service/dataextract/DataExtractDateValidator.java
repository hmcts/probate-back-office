package uk.gov.hmcts.probate.service.dataextract;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.exception.ClientException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataExtractDateValidator {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void dateValidator(String date) {
        dateValidator(null, date);
    }

    public void dateValidator(String fromDate, String toDate) {
        if (StringUtils.isBlank(toDate)) {
            throw new ClientException(HttpStatus.BAD_REQUEST.value(),
                "Error on extract dates, toDate is null or empty");
        }
        try {
            LocalDate to = LocalDate.parse(toDate, DATE_FORMAT);
            if (!StringUtils.isBlank(fromDate)) {
                LocalDate from = LocalDate.parse(fromDate, DATE_FORMAT);
                if (!from.isEqual(to) && !from.isBefore(to)) {
                    throw new ClientException(HttpStatus.BAD_REQUEST.value(),
                        "Error on extract dates, fromDate is not before toDate: " + fromDate + "," + toDate);
                }
            }
        } catch (DateTimeParseException e) {
            log.error("Error parsing date, use the format of 'yyyy-MM-dd': ");
            throw new ClientException(HttpStatus.BAD_REQUEST.value(),
                "Error parsing date, use the format of 'yyyy-MM-dd': " + e.getMessage());
        }
    }

}
