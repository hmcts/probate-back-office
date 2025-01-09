package uk.gov.hmcts.probate.service.template.markdown;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import uk.gov.hmcts.probate.businessrule.AuthenticatedTranslationBusinessRule;
import uk.gov.hmcts.probate.businessrule.DispenseNoticeSupportDocsRule;
import uk.gov.hmcts.probate.businessrule.NotarialWillBusinessRule;
import uk.gov.hmcts.probate.businessrule.PA14FormBusinessRule;
import uk.gov.hmcts.probate.businessrule.PA15FormBusinessRule;
import uk.gov.hmcts.probate.businessrule.PA16FormBusinessRule;
import uk.gov.hmcts.probate.businessrule.PA17FormBusinessRule;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplying;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.service.SendDocumentsRenderer;
import uk.gov.hmcts.probate.service.solicitorexecutor.NotApplyingExecutorsMapper;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static uk.gov.hmcts.probate.model.Constants.GRANT_TYPE_INTESTACY;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.service.template.markdown.MarkdownDecoratorService.BULLET;

class MarkdownDecoratorServiceTest {

    @InjectMocks
    private MarkdownDecoratorService markdownDecoratorService;

    @Mock
    private PA14FormBusinessRule pa14FormBusinessRule;

    @Mock
    private PA15FormBusinessRule pa15FormBusinessRule;

    @Mock
    private PA16FormBusinessRule pa16FormBusinessRule;

    @Mock
    private PA17FormBusinessRule pa17FormBusinessRule;

    @Mock
    private AuthenticatedTranslationBusinessRule authenticatedTranslationBusinessRule;

    @Mock
    private NotApplyingExecutorsMapper notApplyingExecutorsMapper;

    @Mock
    private SendDocumentsRenderer sendDocumentsRenderer;

    @Mock
    private DispenseNoticeSupportDocsRule dispenseNoticeSupportDocsRule;

    @Mock
    private NotarialWillBusinessRule notarialWillBusinessRule;

    @Mock
    private CaseData caseDataMock;

    @BeforeEach
    public void  setup() {
        openMocks(this);
    }

    @Test
    void shouldGetPA14FormLabel() {
        when(pa14FormBusinessRule.isApplicable(caseDataMock)).thenReturn(true);
        List<AdditionalExecutorNotApplying> allIncapable = new ArrayList<>();
        allIncapable.add(AdditionalExecutorNotApplying.builder().notApplyingExecutorName("name1").build());
        allIncapable.add(AdditionalExecutorNotApplying.builder().notApplyingExecutorName("name2").build());
        when(notApplyingExecutorsMapper.getAllExecutorsNotApplying(caseDataMock, "MentallyIncapable"))
            .thenReturn(allIncapable);
        when(sendDocumentsRenderer.getPA14NotApplyingExecutorText("name1")).thenReturn("formattedLink1");
        when(sendDocumentsRenderer.getPA14NotApplyingExecutorText("name2")).thenReturn("formattedLink2");

        String md = markdownDecoratorService.getPA14FormLabel(caseDataMock,false);
        assertEquals("\n*   formattedLink1\n*   formattedLink2", md);
    }

    @Test
    void shouldNotGetPA14FormLabel() {
        when(pa14FormBusinessRule.isApplicable(caseDataMock)).thenReturn(false);

        String md = markdownDecoratorService.getPA14FormLabel(caseDataMock,false);
        assertEquals("", md);
    }

    @Test
    void shouldGetPA15FormLabel() {
        when(pa15FormBusinessRule.isApplicable(caseDataMock)).thenReturn(true);
        List<AdditionalExecutorNotApplying> allRenounced = new ArrayList<>();
        allRenounced.add(AdditionalExecutorNotApplying.builder().notApplyingExecutorName("name1").build());
        allRenounced.add(AdditionalExecutorNotApplying.builder().notApplyingExecutorName("name2").build());
        when(notApplyingExecutorsMapper.getAllExecutorsNotApplying(caseDataMock, "Renunciation"))
            .thenReturn(allRenounced);
        when(sendDocumentsRenderer.getPA15NotApplyingExecutorText("name1")).thenReturn("formattedLink1");
        when(sendDocumentsRenderer.getPA15NotApplyingExecutorText("name2")).thenReturn("formattedLink2");

        String md = markdownDecoratorService.getPA15FormLabel(caseDataMock, false);
        assertEquals("\n*   formattedLink1\n*   formattedLink2", md);
    }

    @Test
    void shouldNotGetPA15FormLabel() {
        when(pa15FormBusinessRule.isApplicable(caseDataMock)).thenReturn(false);

        String md = markdownDecoratorService.getPA15FormLabel(caseDataMock, false);
        assertEquals("", md);
    }

    @Test
    void shouldGetPA16FormLabel() {
        when(pa16FormBusinessRule.isApplicable(caseDataMock)).thenReturn(true);
        when(sendDocumentsRenderer.getPA16FormText()).thenReturn("formattedLink");

        String md = markdownDecoratorService.getPA16FormLabel(caseDataMock, false);
        assertEquals("\n*   formattedLink", md);
    }

    @Test
    void shouldNotGetPA16FormLabel() {
        when(pa16FormBusinessRule.isApplicable(caseDataMock)).thenReturn(false);

        String md = markdownDecoratorService.getPA16FormLabel(caseDataMock, false);
        assertEquals("", md);
    }


    @Test
    void shouldGetPA17FormLabel() {
        when(pa17FormBusinessRule.isApplicable(caseDataMock)).thenReturn(true);
        when(sendDocumentsRenderer.getPA17FormText()).thenReturn("formattedLink");

        String md = markdownDecoratorService.getPA17FormLabel(caseDataMock, false);
        assertEquals("\n*   formattedLink", md);
    }

    @Test
    void shouldNotGetPA17FormLabel() {
        when(pa17FormBusinessRule.isApplicable(caseDataMock)).thenReturn(false);

        String md = markdownDecoratorService.getPA17FormLabel(caseDataMock, false);
        assertEquals("", md);
    }

    @Test
    void shouldGetAuthenticatedTranslationFormLabel() {
        when(authenticatedTranslationBusinessRule.isApplicable(caseDataMock)).thenReturn(true);

        String md = markdownDecoratorService.getAuthenticatedTranslationLabel(caseDataMock, false);
        assertEquals("\n*   an authenticated translation of the will in English or Welsh",
            md);
    }

    @Test
    void shouldNotGetAuthenticatedTranslationFormLabel() {
        when(authenticatedTranslationBusinessRule.isApplicable(caseDataMock)).thenReturn(false);

        String md = markdownDecoratorService.getAuthenticatedTranslationLabel(caseDataMock, false);
        assertEquals("", md);
    }

    @Test
    void shouldGetDispenseWithNoticeSupportDocsLabel() {
        when(dispenseNoticeSupportDocsRule.isApplicable(caseDataMock)).thenReturn(true);

        String supportDocsText =
            "the documents you listed to support your request to dispense with notice to non-applying executor(s): ";
        String supportDocsEntry = "document1 document2";
        String expectedText = BULLET + supportDocsText + supportDocsEntry;
        when(caseDataMock.getDispenseWithNotice()).thenReturn(YES);
        when(caseDataMock.getDispenseWithNoticeSupportingDocs()).thenReturn("document1 document2");
        String md = markdownDecoratorService.getDispenseWithNoticeSupportDocsLabelAndList(caseDataMock, false);
        assertEquals(expectedText, md);
    }

    @Test
    void shouldNotGetDispenseWithNoticeSupportDocsLabel() {
        when(dispenseNoticeSupportDocsRule.isApplicable(caseDataMock)).thenReturn(false);

        String md = markdownDecoratorService.getDispenseWithNoticeSupportDocsLabelAndList(caseDataMock, false);
        assertEquals("", md);
    }

    @Test
    void shouldGetNotarialWillLabel() {
        when(notarialWillBusinessRule.isApplicable(caseDataMock)).thenReturn(true);
        String willLabel = markdownDecoratorService.getWillLabel(caseDataMock);
        String notarialWill = "the notarial or court sealed copy of the will";
        String statementAndExhibits
            = "statement of truth and Exhibits that lead to a R54 Order NCPR 1987 to prove the will is lost, "
            + "and that it has not been revoked";
        assertTrue(willLabel.contains(notarialWill));
        assertTrue(willLabel.contains(statementAndExhibits));
    }

    @Test
    void shouldGetOriginalWillLabelNoCodicils() {
        when(notarialWillBusinessRule.isApplicable(caseDataMock)).thenReturn(false);
        when(caseDataMock.getWillHasCodicils()).thenReturn(NO);
        assertEquals(BULLET + "the original will",
            markdownDecoratorService.getWillLabel(caseDataMock));
    }

    @Test
    void shouldGetOriginalWillLabelWithCodicils() {
        when(notarialWillBusinessRule.isApplicable(caseDataMock)).thenReturn(false);
        when(caseDataMock.getWillHasCodicils()).thenReturn(YES);
        assertEquals(BULLET + "the original will and any codicils",
            markdownDecoratorService.getWillLabel(caseDataMock));
    }

    @Test
    void shouldGetEmptyWillLabelAsIntestacy() {
        when(caseDataMock.getSolsWillType()).thenReturn(GRANT_TYPE_INTESTACY);
        assertTrue(markdownDecoratorService.getWillLabel(caseDataMock).isEmpty());
    }
}
