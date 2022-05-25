package uk.gov.hmcts.probate.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.probate.insights.AppInsights;

import java.util.Locale;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.emptyOrNullString;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BusinessValidationMessageRetrieverTest {

    @Autowired
    private BusinessValidationMessageRetriever businessValidationMessageRetriever;

    @MockBean
    AppInsights appInsights;

    @Test
    public void shouldGetMessage() {
        String message = businessValidationMessageRetriever.getMessage("dodIsBeforeDob", null, Locale.UK);
        assertThat(message, not(is(emptyOrNullString())));
    }
}
