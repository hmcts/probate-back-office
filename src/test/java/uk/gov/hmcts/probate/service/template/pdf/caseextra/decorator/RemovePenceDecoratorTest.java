package uk.gov.hmcts.probate.service.template.pdf.caseextra.decorator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class RemovePenceDecoratorTest {

    @InjectMocks
    private RemovePenceDecorator removePenceDecorator;

    @Mock
    private CaseExtraDecorator caseExtraDecorator;
    @Mock
    private CaseData caseDataMock;

    @Test
    void shouldDecorateForIhtGrossAndNetValues() {
        caseDataMock = CaseData.builder().ihtGrossValue(BigDecimal.valueOf(126.78))
                .ihtNetValue(BigDecimal.valueOf(125.78)).build();
        String caseExtraJson = "{\"grossValue\" : \"126\",\"netValue\" : \"125\"}";
        when(caseExtraDecorator.decorate(any(IhtGrossNetValueCaseExtra.class))).thenReturn(caseExtraJson);
        String actual = removePenceDecorator.decorate(caseDataMock);
        assertEquals(caseExtraJson, actual);
    }
}
