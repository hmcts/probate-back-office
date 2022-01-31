package uk.gov.hmcts.probate.service.template.markdown;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import uk.gov.hmcts.probate.businessrule.PA15FormBusinessRule;
import uk.gov.hmcts.probate.businessrule.PA16FormBusinessRule;
import uk.gov.hmcts.probate.businessrule.PA17FormBusinessRule;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplying;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.service.LinkFormatterService;
import uk.gov.hmcts.probate.service.solicitorexecutor.RenouncingExecutorsMapper;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class MarkdownDecoratorServiceTest {

    @InjectMocks
    private MarkdownDecoratorService markdownDecoratorService;

    @Mock
    private PA15FormBusinessRule pa15FormBusinessRule;

    @Mock
    private PA16FormBusinessRule pa16FormBusinessRule;

    @Mock
    private PA17FormBusinessRule pa17FormBusinessRule;

    @Mock
    private RenouncingExecutorsMapper renouncingExecutorsMapper;

    @Mock
    private LinkFormatterService linkFormatterService;

    @Mock
    private CaseData caseDataMock;

    @Before
    public void  setup() {
        initMocks(this);
    }

    @Test
    public void shouldGetPA15FormLabel() {
        when(pa15FormBusinessRule.isApplicable(caseDataMock)).thenReturn(true);
        List<AdditionalExecutorNotApplying> allRenounced = new ArrayList<>();
        allRenounced.add(AdditionalExecutorNotApplying.builder().notApplyingExecutorName("name1").build());
        allRenounced.add(AdditionalExecutorNotApplying.builder().notApplyingExecutorName("name2").build());
        when(renouncingExecutorsMapper.getAllRenouncingExecutors(caseDataMock)).thenReturn(allRenounced);
        String md = markdownDecoratorService.getPA15FormLabel(caseDataMock);
        assertEquals("\n*   null\n*   null", md);
    }

    @Test
    public void shouldNotGetPA15FormLabel() {
        when(pa15FormBusinessRule.isApplicable(caseDataMock)).thenReturn(false);

        String md = markdownDecoratorService.getPA15FormLabel(caseDataMock);
        assertEquals("", md);
    }
    
    @Test
    public void shouldGetPA16FormLabel() {
        when(pa16FormBusinessRule.isApplicable(caseDataMock)).thenReturn(true);
        when(linkFormatterService.formatLink(any(), any(), any(), any())).thenReturn("formattedLink");

        String md = markdownDecoratorService.getPA16FormLabel(caseDataMock);
        assertEquals("\n*   formattedLink", md);
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
        when(linkFormatterService.formatLink(any(), any(), any(), any())).thenReturn("formattedLink");

        String md = markdownDecoratorService.getPA17FormLabel(caseDataMock);
        assertEquals("\n*   formattedLink", md);
    }

    @Test
    public void shouldNotGetPA17FormLabel() {
        when(pa17FormBusinessRule.isApplicable(caseDataMock)).thenReturn(false);

        String md = markdownDecoratorService.getPA17FormLabel(caseDataMock);
        assertEquals("", md);
    }
}