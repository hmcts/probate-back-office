package uk.gov.hmcts.probate.exception;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BadRequestExceptionTest {

    @Mock
    private Errors errors;

    @Test
    public void shouldCreateBadRequestException() {
        final String message = "MESSAGE";
        FieldError fieldError = new FieldError("", "field", "defaultMessage");
        when(errors.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));
        BadRequestException badRequestException = new BadRequestException(message, errors);

        assertThat(badRequestException, notNullValue());
        assertThat(badRequestException.getErrors(), hasSize(1));
    }
}
