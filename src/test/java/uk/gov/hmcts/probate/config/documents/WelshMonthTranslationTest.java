package uk.gov.hmcts.probate.config.documents;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import uk.gov.hmcts.probate.insights.AppInsights;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WelshMonthTranslationTest {

    @MockBean
    private AppInsights appInsights;

    @Autowired
    private WelshMonthTranslation welshMonthTranslation;

    @Test
    public void convertDateInWelsh(){
        final String dateInWelsh =  welshMonthTranslation.getMonths().get(5);
        Assert.assertEquals("Mai",dateInWelsh);
    }
}
