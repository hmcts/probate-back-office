package uk.gov.hmcts.probate.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Locale;

import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BusinessValidationMessageRetrieverTest {

    @Autowired
    private BusinessValidationMessageRetriever businessValidationMessageRetriever;

    @Test
    public void shouldGetMessage() {
        String message = businessValidationMessageRetriever.getMessage("dodIsBeforeDob", null, Locale.UK);
        assertThat(message, not(isEmptyOrNullString()));
    }
}
