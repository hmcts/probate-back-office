package uk.gov.hmcts.probate.service.template.markdown;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import uk.gov.hmcts.probate.businessrule.AuthenticatedTranslationBusinessRule;
import uk.gov.hmcts.probate.businessrule.DispenseNoticeSupportDocsRule;
import uk.gov.hmcts.probate.businessrule.PA16FormBusinessRule;
import uk.gov.hmcts.probate.businessrule.PA17FormBusinessRule;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static uk.gov.hmcts.probate.model.Constants.YES;

public class MarkdownDecoratorServiceTest {

    @InjectMocks
    private MarkdownDecoratorService markdownDecoratorService;
    
    @Mock
    private PA16FormBusinessRule pa16FormBusinessRule;

    @Mock
    private PA17FormBusinessRule pa17FormBusinessRule;

    @Mock
    private AuthenticatedTranslationBusinessRule authenticatedTranslationBusinessRule;
    
    @Mock
    private DispenseNoticeSupportDocsRule dispenseNoticeSupportDocsRule;

    @Mock
    private CaseData caseDataMock;

    @Before
    public void  setup() {
        initMocks(this);
    }
    
    @Test
    public void shouldGetPA16FormLabel() {
        when(pa16FormBusinessRule.isApplicable(caseDataMock)).thenReturn(true);
        
        String md = markdownDecoratorService.getPA16FormLabel(caseDataMock);
        assertEquals("\n*   <a href=\"https://www.gov.uk/government/publications/form-pa16-give-up-probate" 
            + "-administrator-rights\" target=\"_blank\">Give up probate administrator rights paper form (PA16)</a>", 
            md);
    }

    @Test
    public void shouldNotGetPA16FormLabel() {
        when(pa16FormBusinessRule.isApplicable(caseDataMock)).thenReturn(false);

        String md = markdownDecoratorService.getPA16FormLabel(caseDataMock);
        assertEquals("", md);
    }


    @Test
    public void shouldGetPA17FormLabel() {
        when(pa17FormBusinessRule.isApplicable(caseDataMock)).thenReturn(true);

        String md = markdownDecoratorService.getPA17FormLabel(caseDataMock);
        assertEquals("\n*   <a href=\"https://www.gov.uk/government/publications/form-pa17-give-up-probate-executor" 
                + "-rights-for-legal-professionals\" target=\"_blank\">Give up probate executor rights for probate " 
                + "practitioners paper form (PA17)</a>",
            md);
    }

    @Test
    public void shouldNotGetPA17FormLabel() {
        when(pa17FormBusinessRule.isApplicable(caseDataMock)).thenReturn(false);

        String md = markdownDecoratorService.getPA17FormLabel(caseDataMock);
        assertEquals("", md);
    }

    @Test
    public void shouldGetAuthenticatedTranslationFormLabel() {
        when(authenticatedTranslationBusinessRule.isApplicable(caseDataMock)).thenReturn(true);

        String md = markdownDecoratorService.getAuthenticatedTranslationLabel(caseDataMock);
        assertEquals("\n*   an authenticated translation of the will",
                md);
    }

    @Test
    public void shouldNotGetAuthenticatedTranslationFormLabel() {
        when(authenticatedTranslationBusinessRule.isApplicable(caseDataMock)).thenReturn(false);

        String md = markdownDecoratorService.getAuthenticatedTranslationLabel(caseDataMock);
        assertEquals("", md);
    }

    @Test
    public void shouldGetDispenseWithNoticeSupportDocsLabel() {
        when(dispenseNoticeSupportDocsRule.isApplicable(caseDataMock)).thenReturn(true);

        String supportDocsText =
                "the documents you listed to support your request to dispense with notice to non-applying executor(s):";
        String supportDocsEntry = "document1 document2";
        String expectedText = "\n   " + supportDocsText + supportDocsEntry;
        when(caseDataMock.getDispenseWithNotice()).thenReturn(YES);
        when(caseDataMock.getDispenseWithNoticeSupportingDocs()).thenReturn("document1 document2");
        String md = markdownDecoratorService.getDispenseWithNoticeSupportDocsLabelAndList(caseDataMock);
        assertEquals(expectedText, md);
    }

    @Test
    public void shouldNotGetDispenseWithNoticeSupportDocsLabel() {
        when(dispenseNoticeSupportDocsRule.isApplicable(caseDataMock)).thenReturn(false);

        String md = markdownDecoratorService.getAuthenticatedTranslationLabel(caseDataMock);
        assertEquals("", md);
    }
}