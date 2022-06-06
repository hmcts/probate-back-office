package uk.gov.hmcts.probate.service.template.pdf;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.insights.AppInsights;

import java.time.LocalDate;

@ExtendWith(SpringExtension.class)
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
