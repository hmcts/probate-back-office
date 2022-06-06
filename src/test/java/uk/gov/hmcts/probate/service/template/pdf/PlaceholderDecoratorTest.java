package uk.gov.hmcts.probate.service.template.pdf;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.insights.AppInsights;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class PlaceholderDecoratorTest {

    private static final String DECEASED_DATE_OF_DEATH = "deceasedDateOfDeath";
    private static final String DECEASED_DATE_OF_DEATH_IN_WELSH = "deceasedDateOfDeathInWelsh";
    private static final String DECEASED_DATE_OF_BIRTH = "deceasedDateOfBirth";
    private static final String DECEASED_DATE_OF_BIRTH_IN_WELSH = "deceasedDateOfBirthInWelsh";
    private static final String GRANT_ISSUED_DATE = "grantIssuedDate";
    private static final String GRANT_ISSUED_DATE_IN_WELSH = "grantIssuedDateInWelsh";
    private static final String GRANT_REISSUED_DATE = "reissueDate";
    private static final String GRANT_REISSUED_DATE_IN_WELSH = "grantReissuedDateInWelsh";

    @MockBean
    private AppInsights appInsights;

    @Autowired
    private PlaceholderDecorator placeholderDecorator;

    @Test
    public void decorate_when_both_date_provided() {
        Map<String, Object> placeholders = new HashMap<>();
        placeholders.put(DECEASED_DATE_OF_DEATH, String.valueOf(LocalDate.of(2018,10,19)));
        placeholders.put(GRANT_ISSUED_DATE, String.valueOf(LocalDate.of(2019,12,23)));
        placeholderDecorator.decorate(placeholders);
        assertEquals("23 Rhagfyr 2019", placeholders.get(GRANT_ISSUED_DATE_IN_WELSH));
        assertEquals("19 Hydref 2018", placeholders.get(DECEASED_DATE_OF_DEATH_IN_WELSH));
        assertEquals(null, placeholders.get(GRANT_REISSUED_DATE_IN_WELSH));
    }

    @Test
    public void decorate_when_reissue() {
        Map<String, Object> placeholders = new HashMap<>();
        placeholders.put(GRANT_REISSUED_DATE, String.valueOf(LocalDate.of(2019,12,23)));
        placeholderDecorator.decorate(placeholders);
        assertEquals("23 Rhagfyr 2019", placeholders.get(GRANT_REISSUED_DATE_IN_WELSH));
    }

    @Test
    public void decorate_date_of_birth() {
        Map<String, Object> placeholders = new HashMap<>();
        placeholders.put(DECEASED_DATE_OF_BIRTH, String.valueOf(LocalDate.of(2018,10,19)));
        placeholderDecorator.decorate(placeholders);
        assertEquals("19 Hydref 2018", placeholders.get(DECEASED_DATE_OF_BIRTH_IN_WELSH));
    }

    @Test
    public void decorate_when_grant_issued_not_provided() {
        Map<String, Object> placeholders = new HashMap<>();
        placeholders.put(DECEASED_DATE_OF_DEATH, String.valueOf(LocalDate.of(2018,10,19)));
        placeholderDecorator.decorate(placeholders);
        assertNotNull(placeholders.get(GRANT_ISSUED_DATE_IN_WELSH));
        assertEquals(null, placeholders.get(GRANT_REISSUED_DATE_IN_WELSH));
    }
}
