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

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
    private static final String DECEASED_DATE_OF_DEATH = "deceasedDateOfDeath";
    private static final String DECEASED_DATE_OF_DEATH_IN_WELSH = "deceasedDateOfDeathInWelsh";
    private static final String DECEASED_DATE_OF_BIRTH = "deceasedDateOfBirth";
    private static final String DECEASED_DATE_OF_BIRTH_IN_WELSH = "deceasedDateOfBirthInWelsh";
    private static final String GRANT_ISSUED_DATE = "grantIssuedDate";
    private static final String GRANT_ISSUED_DATE_IN_WELSH = "grantIssuedDateInWelsh";
    private static final String GRANT_REISSUED_DATE = "reissueDate";
    private static final String GRANT_REISSUED_DATE_IN_WELSH = "grantReissuedDateInWelsh";

    private final LocalDateToWelshStringConverter localDateToWelshStringConverter;

    public void decorate(Map<String, Object> placeholders, String grantIssuedDate) {
        putWelshDateIfPresent(placeholders, DECEASED_DATE_OF_DEATH, DECEASED_DATE_OF_DEATH_IN_WELSH);
        putWelshDateIfPresent(placeholders, DECEASED_DATE_OF_BIRTH, DECEASED_DATE_OF_BIRTH_IN_WELSH);

        String issuedDate = (String) placeholders.computeIfAbsent(GRANT_ISSUED_DATE, k -> grantIssuedDate);
        placeholders.put(GRANT_ISSUED_DATE_IN_WELSH,
            localDateToWelshStringConverter.convert(LocalDate.parse(issuedDate)));

        putWelshDateIfPresent(placeholders, GRANT_REISSUED_DATE, GRANT_REISSUED_DATE_IN_WELSH);
    }

    public void decorate(Map<String, Object> placeholders) {
        decorate(placeholders, LocalDate.now().format(dateTimeFormatter));
    }

    private void putWelshDateIfPresent(Map<String, Object> placeholders, String sourceKey, String targetKey) {
        Object dateValue = placeholders.get(sourceKey);
        if (dateValue != null) {
            placeholders.put(targetKey, localDateToWelshStringConverter.convert(LocalDate.parse((String) dateValue)));
        }
    }
}
