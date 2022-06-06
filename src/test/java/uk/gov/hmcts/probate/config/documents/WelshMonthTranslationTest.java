package uk.gov.hmcts.probate.config.documents;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.insights.AppInsights;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class WelshMonthTranslationTest {

    @MockBean
    private AppInsights appInsights;

    @Autowired
    private WelshMonthTranslation welshMonthTranslation;

    @Test
    public void convertDateInWelsh() {
        final String dateInWelsh = welshMonthTranslation.getMonths().get(5);
        Assert.assertEquals("Mai", dateInWelsh);
    }
}
