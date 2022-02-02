package uk.gov.hmcts.probate.service.exceptionrecord.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ExceptedEstateDateOfDeathChecker {
    
    @Value("${iht-estate.switch-date:2022-01-01}")
    String ihtEstateSwitchDate;
    
    public boolean isOnOrAfterSwitchDate(String dateOfDeath) {
        if (null == dateOfDeath) {
            return false;
        }
        LocalDate switchDate = LocalDate.parse(ihtEstateSwitchDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate dod = LocalDate.parse(dateOfDeath, DateTimeFormatter.ofPattern("ddMMyyyy"));
        return !dod.isBefore(switchDate);
    }
}