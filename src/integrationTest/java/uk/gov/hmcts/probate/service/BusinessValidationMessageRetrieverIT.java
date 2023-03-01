package uk.gov.hmcts.probate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.insights.AppInsights;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

import static java.util.Locale.UK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class BusinessValidationMessageRetrieverIT {

    @Autowired
    private BusinessValidationMessageRetriever businessValidationMessageRetriever;

    @MockBean
    AppInsights appInsights;

    @Test
    void shouldGetMessage() {
        String message = businessValidationMessageRetriever.getMessage("dodIsBeforeDob", null, UK);
        assertThat(message, not(is(emptyOrNullString())));
    }

    @Test
    void testEmailForPaymentError() {
        String[] empty = {};
        String message = businessValidationMessageRetriever.getMessage(
            "creditAccountPaymentErrorMessageDuplicatePayment2", empty, UK);
        assertThat(message, containsString("probatefeedback@justice.gov.uk"));
    }
}
