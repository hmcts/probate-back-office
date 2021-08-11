package uk.gov.hmcts.probate.service.template.pdf;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.probate.insights.AppInsights;

import java.time.LocalDate;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LocalDateToWelshStringConverterTest {

    @MockBean
    private AppInsights appInsights;

    @Autowired
    private LocalDateToWelshStringConverter localDateToWelshStringConverter;

    @Test
    public void convertDateInWelsh() {
        LocalDate localDate = LocalDate.of(2019, 12, 23);
        final String dateInWelsh = localDateToWelshStringConverter.convert(localDate);
        Assert.assertEquals("23 Rhagfyr 2019", dateInWelsh);
    }
}