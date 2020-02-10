package uk.gov.hmcts.probate.service.template.pdf;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlaceholderDecorator {

    private final LocalDateToWelshStringConverter localDateToWelshStringConverter;

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
    private static final String DECEASED_DATE_OF_DEATH = "deceasedDateOfDeath";
    private static final String DECEASED_DATE_OF_DEATH_IN_WELSH = "deceasedDateOfDeathInWelsh";
    private static final String GRANT_ISSUED_DATE = "grantIssuedDate";
    private static final String GRANT_ISSUED_DATE_IN_WELSH = "grantIssuedDateInWelsh";

    public void decorate(Map<String, Object> placeholders) {
        if (placeholders.get(DECEASED_DATE_OF_DEATH) != null) {
            String deceasedDate = (String) placeholders.get(DECEASED_DATE_OF_DEATH);
            placeholders.put(DECEASED_DATE_OF_DEATH_IN_WELSH, localDateToWelshStringConverter.convert(LocalDate.parse(deceasedDate)));
        }
        placeholders.computeIfAbsent(GRANT_ISSUED_DATE, k -> dateTimeFormatter.format(LocalDate.now()));
        placeholders.put(GRANT_ISSUED_DATE_IN_WELSH, localDateToWelshStringConverter.convert(LocalDate.parse((String) placeholders.get(GRANT_ISSUED_DATE))));

    }
}
