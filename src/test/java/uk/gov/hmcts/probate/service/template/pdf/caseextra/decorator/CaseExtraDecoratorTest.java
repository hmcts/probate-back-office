
package uk.gov.hmcts.probate.service.template.pdf.caseextra.decorator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CaseExtraDecoratorTest {

    @InjectMocks
    private CaseExtraDecorator caseExtraDecorator;

    @Mock
    private ObjectMapper objectMapperMock;

    private final CaseData caseData = CaseData.builder().build();

    @Test
    void shouldDecorateCaseData() throws JsonProcessingException {
        when(objectMapperMock.writeValueAsString(any())).thenReturn("someJson");
        String actual = caseExtraDecorator.decorate(caseData);

        assertEquals("someJson", actual);
    }

    @Test
    void shouldThrowExceptionWhenDecorateCaseData() throws JsonProcessingException {
        when(objectMapperMock.writeValueAsString(any()))
                .thenThrow(new JsonProcessingException("Serialization failed") {});

        assertThrows(BadRequestException.class,
                () -> caseExtraDecorator.decorate(caseData));
    }

    @Test
    void shouldCombineJsons() {
        String actual = caseExtraDecorator.combineDecorations(
            "{\"first\":\"firstValue\"}",
            "{\"second\":\"secondValue\"}");

        assertEquals("{\"first\":\"firstValue\",\"second\":\"secondValue\"}", actual);
    }
}
