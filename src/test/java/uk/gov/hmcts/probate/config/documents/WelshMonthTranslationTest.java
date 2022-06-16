package uk.gov.hmcts.probate.config.documents;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.insights.AppInsights;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class WelshMonthTranslationTest {

    @MockBean
    private AppInsights appInsights;

    @Autowired
    private WelshMonthTranslation welshMonthTranslation;

    @Test
    void convertDateInWelsh() {
        final String dateInWelsh = welshMonthTranslation.getMonths().get(5);
        assertEquals("Mai", dateInWelsh);
    }
}
