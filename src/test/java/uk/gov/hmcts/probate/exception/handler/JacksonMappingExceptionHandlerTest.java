package uk.gov.hmcts.probate.exception.handler;

import com.fasterxml.jackson.databind.JsonMappingException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;

import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class JacksonMappingExceptionHandlerTest {

    @InjectMocks
    private JacksonMappingExceptionHandler jacksonMappingExceptionHandler;

    @Mock
    private JsonMappingException jsonMappingException;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldHandleMessageNotReadableException() {
        JsonMappingException.Reference reference1 = Mockito.mock(JsonMappingException.Reference.class);
        when(reference1.getFieldName()).thenReturn("field1");

        JsonMappingException.Reference reference2 = Mockito.mock(JsonMappingException.Reference.class);
        when(reference2.getFieldName()).thenReturn("field2");

        when(jsonMappingException.getPath()).thenReturn(Arrays.asList(reference1, reference2));

        ResponseEntity responseEntity = jacksonMappingExceptionHandler
            .handleMessageNotReadableException(jsonMappingException);

        Assert.assertNotNull(responseEntity);
        Assert.assertEquals("JsonParseError", ((FieldErrorResponse) responseEntity.getBody()).getCode());
        Assert.assertEquals("field1.field2", ((FieldErrorResponse) responseEntity.getBody()).getField());
    }
}
