package uk.gov.hmcts.probate.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.OCRMappingException;

import static uk.gov.hmcts.probate.service.exceptionrecord.mapper.OCRFieldDefaultLocalDateFieldMapper.OCR_DATE_FORMAT;

@Slf4j
@Component
public class ExceptedEstateDateOfDeathChecker {

    private LocalDate switchDate;
    private static final String DOD_FIELD_NAME = "deceasedDateOfDeath";

    @Value("${iht-estate.switch-date:2022-01-01}")
    private void setLocalDate(String localDateStr) {
        switchDate = LocalDate.parse(localDateStr);
    }

    public boolean isOnOrAfterSwitchDate(String dateOfDeath) {
        LocalDate dod = null;
        if (null == dateOfDeath) {
            return false;
        }
        try {
            log.info("Parsed LocalDate from OCR date string: {}", dateOfDeath);
            log.info("switchDate {}", switchDate);
            DateTimeFormatter ocrDataDateFormatter = DateTimeFormatter.ofPattern("ddMMyyyy");
            dod = LocalDate.parse(dateOfDeath, ocrDataDateFormatter);
            log.info("Parsed LocalDate from OCR date string: {}", dod);

        } catch (DateTimeParseException dtpe) {
            String errorMessage = DOD_FIELD_NAME
                    + ": Date field '" + dateOfDeath + "' not in expected format " + OCR_DATE_FORMAT;
            log.error(errorMessage);
            throw new OCRMappingException(errorMessage);
        }
        return !dod.isBefore(switchDate);
    }

    public boolean isOnOrAfterSwitchDate(LocalDate dateOfDeath) {
        if (null == dateOfDeath) {
            return false;
        }
        return !dateOfDeath.isBefore(switchDate);
    }
}
