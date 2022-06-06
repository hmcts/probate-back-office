package uk.gov.hmcts.probate.service.template.pdf.caseextra.decorator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class CaseExtraDecoratorTest {

    @InjectMocks
    private CaseExtraDecorator caseExtraDecorator;

    @Mock
    private ObjectMapper objectMapperMock;
    @Mock
    private CaseData caseDataMock;

    @Test
    public void shouldDecorateCaseData() throws JsonProcessingException {
        when(objectMapperMock.writeValueAsString(any())).thenReturn("someJson");
        String actual = caseExtraDecorator.decorate(caseDataMock);
        assertEquals("someJson", actual);
    }

    @Test
    public void shouldThrowExceptionWhenDecorateCaseData() throws JsonProcessingException {
        assertThrows(BadRequestException.class, () -> {
            when(objectMapperMock.writeValueAsString(any())).thenThrow(JsonProcessingException.class);
            caseExtraDecorator.decorate(caseDataMock);
        });
    }

    @Test
    public void shouldCombineJsons() {
        String actual = caseExtraDecorator.combineDecorations("{\"first\":\"firstValue\"}",
            "{\"second\":\"secondValue\"}");
        assertEquals("{\"first\":\"firstValue\",\"second\":\"secondValue\"}", actual);
    }

}
