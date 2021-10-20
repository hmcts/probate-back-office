package uk.gov.hmcts.probate.service.template.pdf;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import uk.gov.hmcts.probate.businessrule.PA16FormBusinessRule;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.service.FileSystemResourceService;

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
    private FileSystemResourceService fileSystemResourceServiceMock;
    @Mock
    private CaseData caseDataMock;

    @Before
    public void setup() {
        initMocks(this);
    }
    
    
    @Test
    public void shouldNotProvideAdditionalDecoration() {
        String json = solicitorCoversheetPDFDecorator.decorate(caseDataMock);
        
        assertEquals("", json);
    }

    @Test
    public void shouldProvideAdditionalDecoration() {
        when(pa16FormBusinessRuleMock.isApplicable(caseDataMock)).thenReturn(true);
        when(fileSystemResourceServiceMock.getFileFromResourceAsString(any()))
            .thenReturn("\"case_extras\": {\"showPa16Form\":\"Yes\",\"pa16FormUrl\":\"<PA16FormURL>\"," 
                + "\"pa16FormText\":\"<PA16FormTEXT>\"}");
        
        String json = solicitorCoversheetPDFDecorator.decorate(caseDataMock);

        String expected = "\"case_extras\": {\"showPa16Form\":\"Yes\",\"pa16FormUrl\":\"https://www.gov" 
            + ".uk/government/publications/form-pa16-give-up-probate-administrator-rights\",\"pa16FormText\":\"Give " 
            + "up probate administrator rights paper form (PA16)\"}";
        assertEquals(expected, json);
    }
}