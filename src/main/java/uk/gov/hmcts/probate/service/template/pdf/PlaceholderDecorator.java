package uk.gov.hmcts.probate.service.template.pdf;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlaceholderDecorator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final String DECEASED_DATE_OF_DEATH = "deceasedDateOfDeath";
    private static final String DECEASED_DATE_OF_DEATH_IN_WELSH = "deceasedDateOfDeathInWelsh";
    private static final String DECEASED_DATE_OF_BIRTH = "deceasedDateOfBirth";
    private static final String DECEASED_DATE_OF_BIRTH_IN_WELSH = "deceasedDateOfBirthInWelsh";
    private static final String GRANT_ISSUED_DATE = "grantIssuedDate";
    private static final String GRANT_ISSUED_DATE_IN_WELSH = "grantIssuedDateInWelsh";
    private static final String GRANT_REISSUED_DATE = "reissueDate";
    private static final String GRANT_REISSUED_DATE_IN_WELSH = "grantReissuedDateInWelsh";

    private final LocalDateToWelshStringConverter localDateToWelshStringConverter;
    private final Clock clock;

    public void decorate(Map<String, Object> placeholders, String grantIssuedDate) {
        putWelshDateIfPresent(placeholders, DECEASED_DATE_OF_DEATH, DECEASED_DATE_OF_DEATH_IN_WELSH);
        putWelshDateIfPresent(placeholders, DECEASED_DATE_OF_BIRTH, DECEASED_DATE_OF_BIRTH_IN_WELSH);
        putWelshDateIfPresent(placeholders, GRANT_REISSUED_DATE, GRANT_REISSUED_DATE_IN_WELSH);
        String dateForGrantIssuedDate = StringUtils.defaultIfBlank(
                grantIssuedDate,
                LocalDate.now(clock).format(DATE_FORMATTER)
        );
        placeholders.putIfAbsent(GRANT_ISSUED_DATE, dateForGrantIssuedDate);
        putWelshDateIfPresent(placeholders, GRANT_ISSUED_DATE, GRANT_ISSUED_DATE_IN_WELSH);
    }

    public void decorate(Map<String, Object> placeholders) {
        decorate(placeholders, LocalDate.now(clock).format(DATE_FORMATTER));
    }

    private void putWelshDateIfPresent(Map<String, Object> placeholders, String sourceKey, String targetKey) {
        Object value = placeholders.get(sourceKey);
        if (value instanceof String date) {
            placeholders.put(targetKey, localDateToWelshStringConverter.convert(LocalDate.parse(date)));
        }
    }
}
