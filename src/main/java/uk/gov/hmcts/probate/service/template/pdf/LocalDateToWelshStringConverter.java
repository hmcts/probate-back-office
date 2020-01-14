package uk.gov.hmcts.probate.service.template.pdf;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.config.documents.WelshMonthTranslation;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class LocalDateToWelshStringConverter {

    private final WelshMonthTranslation welshMonthTranslation;

    public String convert(LocalDate dateToConvert) {
        if (dateToConvert == null) {
            return null;
        }
        int day = dateToConvert.getDayOfMonth();
        int year = dateToConvert.getYear();
        int month = dateToConvert.getMonth().getValue();
        return String.join(" ", Integer.toString(day),  welshMonthTranslation.getMonths().get(month),
                Integer.toString(year));
    }

}
