package uk.gov.hmcts.probate.service.template.pdf.caseextra.decorator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.CaseExtra;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CaseExtraDecoratorTest {

    @InjectMocks
    private CaseExtraDecorator caseExtraDecorator;
    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private CaseExtra caseExtraMock;
    
    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldDeserailiseCaseExtra() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(caseExtraMock)).thenReturn("somejson");
        String json = caseExtraDecorator.decorate(caseExtraMock);

        assertEquals("somejson", json);
    }

    @Test(expected = BadRequestException.class)
    public void shouldThrowBadRequest() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(caseExtraMock)).thenThrow(JsonProcessingException.class);
        caseExtraDecorator.decorate(caseExtraMock);
    }
    
    @Test
    public void shouldCombineDecorations() {
        String extraPA16 = "{\"showPa16Form\":\"Yes\",\"pa16FormUrl\":\"PA16FormURL\","
            + "\"pa16FormText\":\"PA16FormTEXT\"}";
        String extraIht = "{\"ihtEstate207Text\":\"the inheritance tax form IHT 207\"}";

        String extraAll = "{\"showPa16Form\":\"Yes\",\"pa16FormUrl\":\"PA16FormURL\","
            + "\"pa16FormText\":\"PA16FormTEXT\",\"ihtEstate207Text\":\"the inheritance tax form IHT 207\"}";

        String combined = caseExtraDecorator.combineDecorations(extraPA16, extraIht);
        assertEquals(extraAll, combined);
    }
}