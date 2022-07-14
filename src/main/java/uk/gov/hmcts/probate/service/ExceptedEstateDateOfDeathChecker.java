package uk.gov.hmcts.probate.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ExceptedEstateDateOfDeathChecker {

    private LocalDate switchDate;

    @Value("${iht-estate.switch-date:2022-01-01}")
    private void setLocalDate(String localDateStr) {
        switchDate = LocalDate.parse(localDateStr);
    }

    public boolean isOnOrAfterSwitchDate(String dateOfDeath) {
        if (null == dateOfDeath) {
            return false;
        }
        DateTimeFormatter ocrDataDateFormatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        LocalDate dod = LocalDate.parse(dateOfDeath, ocrDataDateFormatter);
        return !dod.isBefore(switchDate);
    }

    public boolean isOnOrAfterSwitchDate(LocalDate dateOfDeath) {
        if (null == dateOfDeath) {
            return false;
        }
        return !dateOfDeath.isBefore(switchDate);
    }
}
