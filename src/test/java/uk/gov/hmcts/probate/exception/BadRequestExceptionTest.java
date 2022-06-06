package uk.gov.hmcts.probate.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
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
