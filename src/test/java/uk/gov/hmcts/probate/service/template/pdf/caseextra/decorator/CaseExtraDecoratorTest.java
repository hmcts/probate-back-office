package uk.gov.hmcts.probate.service.template.pdf.caseextra.decorator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
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

    @Test(expected = BadRequestException.class)
    public void shouldThrowExceptionWhenDecorateCaseData() throws JsonProcessingException {
        when(objectMapperMock.writeValueAsString(any())).thenThrow(JsonProcessingException.class);
        caseExtraDecorator.decorate(caseDataMock);
    }

    @Test
    public void shouldCombineJsons() {
        String actual = caseExtraDecorator.combineDecorations("{\"first\":\"firstValue\"}",
            "{\"second\":\"secondValue\"}");
        assertEquals("{\"first\":\"firstValue\",\"second\":\"secondValue\"}", actual);
    }

}