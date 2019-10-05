package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToDefaultLocalDate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class OCRFieldDefaultLocalDateFieldMapper {

    public static final String OCR_DATE_FORMAT = "ddMMyyyy";

    @SuppressWarnings("squid:S1168")
    @ToDefaultLocalDate
    public LocalDate toDefaultDateFieldMember(String dateValue) {
        log.info("Beginning mapping for Date value: {}", dateValue);

        if (dateValue == null || dateValue.isEmpty()) {
            return null;
        }

        dateValue = dateValue.replaceAll("[^\\d.]","");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(OCR_DATE_FORMAT);
        LocalDate localDate = LocalDate.parse(dateValue, formatter);
        log.info("LocalDate ISO_LOCAL_DATE string {}", localDate);
        return localDate;
    }

}