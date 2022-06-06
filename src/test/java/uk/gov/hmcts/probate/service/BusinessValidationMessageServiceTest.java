package uk.gov.hmcts.probate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;

import java.util.Locale;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class BusinessValidationMessageServiceTest {

    @Mock
    private BusinessValidationMessageRetriever businessValidationMessageRetrieverMock;

    private BusinessValidationMessageService underTest;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        underTest = new BusinessValidationMessageService(businessValidationMessageRetrieverMock);
    }

    @Test
    void shouldValidateFormWithNoErrors() {
        String code = "dobIsNull";
        String param = "someParam";
        String message = "Date of birth cannot be empty";

        Mockito.when(
            businessValidationMessageRetrieverMock.getMessage(code, null, Locale.UK)
        ).thenReturn(message);

        FieldErrorResponse error = underTest.generateError(param, code);

        assertThat(error.getParam(), is(param));
        assertThat(error.getCode(), is(code));
        assertThat(error.getMessage(), is(message));
    }

    @Test
    void shouldValidateFormWithArgsWithNoErrors() {
        String code = "dobIsNull";
        String param = "someParam";
        String message = "Date of birth cannot be empty";
        String[] args = {"arg1", "arg2"};

        Mockito.when(
            businessValidationMessageRetrieverMock.getMessage(code, args, Locale.UK)
        ).thenReturn(message);

        FieldErrorResponse error = underTest.generateError(param, code, args);

        assertThat(error.getParam(), is(param));
        assertThat(error.getCode(), is(code));
        assertThat(error.getMessage(), is(message));
    }
}
