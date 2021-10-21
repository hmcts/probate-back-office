package uk.gov.hmcts.probate.service.template.pdf;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import uk.gov.hmcts.probate.businessrule.PA16FormBusinessRule;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.decorator.CaseExtraDecorator;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.decorator.SolicitorCoversheetPDFDecorator;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class SolicitorCoversheetPDFDecoratorTest {

    @InjectMocks
    private SolicitorCoversheetPDFDecorator solicitorCoversheetPDFDecorator;

    @Mock
    private PA16FormBusinessRule pa16FormBusinessRuleMock;
    @Mock
    private CaseExtraDecorator caseExtraDecorator;
    @Mock
    private CaseData caseDataMock;

    @Before
    public void setup() {
        initMocks(this);
    }
    
    
    @Test
    public void shouldNotProvideAdditionalDecoration() {
        String caseJson = "{\"caseData\":{\"someAttr\": \"someValue\"}";
        String json = solicitorCoversheetPDFDecorator.decorate(caseJson, caseDataMock);
        
        assertEquals(caseJson, json);
    }

    @Test
    public void shouldProvideAdditionalDecoration() {
        String caseJson = "{\"caseData\":{\"someAttr\": \"someValue\"}";
        when(pa16FormBusinessRuleMock.isApplicable(caseDataMock)).thenReturn(true);
        String extra = "{\"showPa16Form\":\"Yes\",\"pa16FormUrl\":\"PA16FormURL\","
            + "\"pa16FormText\":\"PA16FormTEXT\"}";
        when(caseExtraDecorator.decorate(any()))
            .thenReturn(extra);
        
        String expected = "{\"caseData\":{\"someAttr\": \"someValue\",\"case_extras\":{\"showPa16Form\":\"Yes\"," 
            + "\"pa16FormUrl\":\"PA16FormURL\",\"pa16FormText\":\"PA16FormTEXT\"}}";
        String json = solicitorCoversheetPDFDecorator.decorate(caseJson, caseDataMock);

        assertEquals(expected, json);
    }
}