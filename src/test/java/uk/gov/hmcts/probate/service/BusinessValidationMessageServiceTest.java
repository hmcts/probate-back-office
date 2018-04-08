package uk.gov.hmcts.probate.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.hmcts.probate.model.BusinessValidationError;

import java.util.Locale;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(MockitoJUnitRunner.class)
public class BusinessValidationMessageServiceTest {
    @InjectMocks
    private BusinessValidationMessageService businessValidationMessageService;

    @Mock
    private BusinessValidationMessageRetriever businessValidationMessageRetrieverMock;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldValidateFormWithNoErrors() {
        Mockito.when(
            businessValidationMessageRetrieverMock.getMessage("dobIsNull", null, Locale.UK)
        ).thenReturn("Date of birth cannot be empty");

        BusinessValidationError error = businessValidationMessageService.generateError("someParam", "dobIsNull");

        assertThat(error.getParam(), is("someParam"));
        assertThat(error.getCode(), is("dobIsNull"));
        assertThat(error.getMsg(), is("Date of birth cannot be empty"));
    }
}
