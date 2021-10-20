package uk.gov.hmcts.probate.service.template.pdf;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import uk.gov.hmcts.probate.businessrule.PA16FormBusinessRule;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.service.FileSystemResourceService;

import static org.junit.jupiter.api.Assertions.*;
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
        when(fileSystemResourceServiceMock.getFileFromResourceAsString(any())).thenReturn("{json file contents <PA16FormText>}");
        
        String json = solicitorCoversheetPDFDecorator.decorate(caseDataMock);

        String expected = "{json file contents <a href='https://www.gov" 
            + ".uk/government/publications/form-pa16-give-up-probate-administrator-rights'" 
            + " target='blank'>Give up probate administrator rights paper form (PA16)<a/>}";
        assertEquals(expected, json);
    }
}