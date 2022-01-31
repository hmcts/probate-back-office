package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToIHTFormEstateValuesCompleted;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OCRFieldIhtFormEstateValuesCompletedMapper {

    @Value("${iht-estate.switch-date:2022-01-01}")
    String ihtEstateSwitchDate;

    @ToIHTFormEstateValuesCompleted
    public Boolean toIhtFormEstateValuesCompleted(ExceptionRecordOCRFields ocrFields) {
        log.info("Beginning mapping for ihtFormEstateValuesCompleted");

        if (ocrFields.getIhtEstateGrossValue() != null
            && ocrFields.getIhtEstateNetValue() != null
            && ocrFields.getIhtEstateNetQualifyingValue() != null) {
            return Boolean.FALSE;
        } else if (
            "IHT207".equalsIgnoreCase(ocrFields.getIhtFormEstate())
                || "IHT400421".equalsIgnoreCase(ocrFields.getIhtFormEstate())
        ) {

            LocalDate switchDate = LocalDate.parse(ihtEstateSwitchDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            if (null == ocrFields.getDeceasedDateOfDeath()) {
                return null;
            }
            LocalDate dod = LocalDate.parse(ocrFields.getDeceasedDateOfDeath(), 
                DateTimeFormatter.ofPattern("ddMMyyyy"));

            if (!dod.isBefore(switchDate)) {
                return Boolean.TRUE;
            }
        }
        return null;
    }
}