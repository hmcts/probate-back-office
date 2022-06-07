package uk.gov.hmcts.probate.exception.handler;

import com.fasterxml.jackson.databind.JsonMappingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class JacksonMappingExceptionHandlerTest {

    @InjectMocks
    private JacksonMappingExceptionHandler jacksonMappingExceptionHandler;

    @Mock
    private JsonMappingException jsonMappingException;

    @BeforeEach
    public void setup() {
        openMocks(this);
    }

    @Test
    void shouldHandleMessageNotReadableException() {
        JsonMappingException.Reference reference1 = Mockito.mock(JsonMappingException.Reference.class);
        when(reference1.getFieldName()).thenReturn("field1");

        JsonMappingException.Reference reference2 = Mockito.mock(JsonMappingException.Reference.class);
        when(reference2.getFieldName()).thenReturn("field2");

        when(jsonMappingException.getPath()).thenReturn(Arrays.asList(reference1, reference2));

        ResponseEntity responseEntity = jacksonMappingExceptionHandler
            .handleMessageNotReadableException(jsonMappingException);

        assertNotNull(responseEntity);
        assertEquals("JsonParseError", ((FieldErrorResponse) responseEntity.getBody()).getCode());
        assertEquals("field1.field2", ((FieldErrorResponse) responseEntity.getBody()).getField());
    }
}
