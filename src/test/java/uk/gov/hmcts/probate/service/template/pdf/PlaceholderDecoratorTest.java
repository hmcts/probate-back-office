package uk.gov.hmcts.probate.service.template.pdf;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import uk.gov.hmcts.probate.insights.AppInsights;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PlaceholderDecoratorTest {

    private static final String DECEASED_DATE_OF_DEATH = "deceasedDateOfDeath";
    private static final String DECEASED_DATE_OF_DEATH_IN_WELSH = "deceasedDateOfDeathInWelsh";
    private static final String GRANT_ISSUED_DATE = "grantIssuedDate";
    private static final String GRANT_ISSUED_DATE_IN_WELSH = "grantIssuedDateInWelsh";

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
    }

    @Test
    public void decorate_when_grant_issued_not_provided() {
        Map<String, Object> placeholders = new HashMap<>();
        placeholders.put(DECEASED_DATE_OF_DEATH, String.valueOf(LocalDate.of(2018,10,19)));
        placeholderDecorator.decorate(placeholders);
        assertNotNull(placeholders.get(GRANT_ISSUED_DATE_IN_WELSH));
    }
}
