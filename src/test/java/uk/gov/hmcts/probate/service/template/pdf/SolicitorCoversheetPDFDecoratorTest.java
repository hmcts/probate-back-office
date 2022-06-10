package uk.gov.hmcts.probate.service.template.pdf;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import uk.gov.hmcts.probate.businessrule.AdmonWillRenunicationRule;
import uk.gov.hmcts.probate.businessrule.AuthenticatedTranslationBusinessRule;
import uk.gov.hmcts.probate.businessrule.DispenseNoticeSupportDocsRule;
import uk.gov.hmcts.probate.businessrule.IhtEstate207BusinessRule;
import uk.gov.hmcts.probate.businessrule.NoDocumentsRequiredBusinessRule;
import uk.gov.hmcts.probate.businessrule.PA14FormBusinessRule;
import uk.gov.hmcts.probate.businessrule.PA15FormBusinessRule;
import uk.gov.hmcts.probate.businessrule.PA16FormBusinessRule;
import uk.gov.hmcts.probate.businessrule.PA17FormBusinessRule;
import uk.gov.hmcts.probate.businessrule.TCResolutionLodgedWithApplicationRule;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplying;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.service.solicitorexecutor.NotApplyingExecutorsMapper;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.AuthenticatedTranslationCaseExtra;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.DispenseNoticeCaseExtra;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.IhtEstate207CaseExtra;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.NoDocumentsRequiredCaseExtra;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.PA14FormCaseExtra;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.PA15FormCaseExtra;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.PA16FormCaseExtra;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.PA17FormCaseExtra;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.decorator.CaseExtraDecorator;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.decorator.SolicitorCoversheetPDFDecorator;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class SolicitorCoversheetPDFDecoratorTest {

    @InjectMocks
    private SolicitorCoversheetPDFDecorator solicitorCoversheetPDFDecorator;

    @Mock
    private NoDocumentsRequiredBusinessRule noDocumentsRequiredBusinessRule;
    @Mock
    private PA14FormBusinessRule pa14FormBusinessRuleMock;
    @Mock
    private PA15FormBusinessRule pa15FormBusinessRuleMock;
    @Mock
    private NotApplyingExecutorsMapper notApplyingExecutorsMapper;
    @Mock
    private PA16FormBusinessRule pa16FormBusinessRuleMock;
    @Mock
    private PA17FormBusinessRule pa17FormBusinessRuleMock;
    @Mock
    private IhtEstate207BusinessRule ihtEstate207BusinessRuleMock;
    @Mock
    private AuthenticatedTranslationBusinessRule authenticatedTranslationBusinessRuleMock;
    @Mock
    private AdmonWillRenunicationRule admonWillRenunicationRuleMock;
    @Mock
    private TCResolutionLodgedWithApplicationRule tcResolutionLodgedWithApplicationRuleMock;
    @Mock
    private DispenseNoticeSupportDocsRule dispenseNoticeSupportDocsRuleMock;
    @Mock
    private CaseExtraDecorator caseExtraDecorator;
    @Mock
    private CaseData caseDataMock;

    @BeforeEach
    public void setup() {
        openMocks(this);
        setBusinessRuleMocksApplicable(false);
    }

    private void setBusinessRuleMocksApplicable(boolean isApplicable) {
        when(pa14FormBusinessRuleMock.isApplicable(caseDataMock)).thenReturn(isApplicable);
        when(pa15FormBusinessRuleMock.isApplicable(caseDataMock)).thenReturn(isApplicable);
        when(pa16FormBusinessRuleMock.isApplicable(caseDataMock)).thenReturn(isApplicable);
        when(pa17FormBusinessRuleMock.isApplicable(caseDataMock)).thenReturn(isApplicable);
        when(ihtEstate207BusinessRuleMock.isApplicable(caseDataMock)).thenReturn(isApplicable);
        when(authenticatedTranslationBusinessRuleMock.isApplicable(caseDataMock)).thenReturn(isApplicable);
        when(dispenseNoticeSupportDocsRuleMock.isApplicable(caseDataMock)).thenReturn(isApplicable);
    }

    @Test
    void shouldNotProvideAdditionalDecoration() {
        String caseJson = "";
        String json = solicitorCoversheetPDFDecorator.decorate(caseDataMock);

        assertEquals(caseJson, json);
    }

    @Test
    void shouldProvideNoDocumentsRequiredDecoration() {
        when(noDocumentsRequiredBusinessRule.isApplicable(caseDataMock)).thenReturn(true);
        String extra = "{\"documentsNotRequired\": \"Yes\"}";
        final NoDocumentsRequiredCaseExtra noDocumentsRequiredCaseExtra = NoDocumentsRequiredCaseExtra.builder().documentsNotRequired("Yes").build();
        when(caseExtraDecorator.decorate(any(NoDocumentsRequiredCaseExtra.class)))
            .thenReturn(extra);
        when(caseExtraDecorator.combineDecorations("", extra)).thenReturn(extra);

        String json = solicitorCoversheetPDFDecorator.decorate(caseDataMock);

        assertEquals(extra, json);
        verify(noDocumentsRequiredBusinessRule).isApplicable(caseDataMock);
        verify(caseExtraDecorator).decorate(eq(noDocumentsRequiredCaseExtra));
        verify(caseExtraDecorator).combineDecorations(any(),eq(extra));
    }

    @Test
    void shouldProvidePA14Decoration() {
        when(pa14FormBusinessRuleMock.isApplicable(caseDataMock)).thenReturn(true);
        String extra = "{\"showPa14Form\":\"Yes\",\"pa14FormUrl\":\"PA14FormURL\","
            + "\"pa14FormText\":\"PA14FormTEXT\"}";
        when(caseExtraDecorator.decorate(any(PA14FormCaseExtra.class))).thenReturn(extra);
        when(caseExtraDecorator.combineDecorations("", extra)).thenReturn(extra);
        List<AdditionalExecutorNotApplying> all = new ArrayList<>();
        all.add(AdditionalExecutorNotApplying.builder().build());
        when(notApplyingExecutorsMapper
            .getAllExecutorsNotApplying(caseDataMock, "MentallyIncapable"))
            .thenReturn(all);

        String json = solicitorCoversheetPDFDecorator.decorate(caseDataMock);

        assertEquals(extra, json);
    }

    @Test
    void shouldProvidePA15Decoration() {
        when(pa15FormBusinessRuleMock.isApplicable(caseDataMock)).thenReturn(true);
        String extra = "{\"showPa15Form\":\"Yes\",\"pa15FormUrl\":\"PA15FormURL\","
            + "\"pa15FormText\":\"PA15FormTEXT\"}";
        when(caseExtraDecorator.decorate(any(PA15FormCaseExtra.class))).thenReturn(extra);
        when(caseExtraDecorator.combineDecorations("", extra)).thenReturn(extra);
        List<AdditionalExecutorNotApplying> all = new ArrayList<>();
        all.add(AdditionalExecutorNotApplying.builder().build());
        when(notApplyingExecutorsMapper.getAllExecutorsNotApplying(caseDataMock, "Renunciation"))
            .thenReturn(all);

        String json = solicitorCoversheetPDFDecorator.decorate(caseDataMock);

        assertEquals(extra, json);
    }

    @Test
    void shouldProvidePA16Decoration() {
        when(pa16FormBusinessRuleMock.isApplicable(caseDataMock)).thenReturn(true);
        String extra = "{\"showPa16Form\":\"Yes\",\"pa16FormUrl\":\"PA16FormURL\","
            + "\"pa16FormText\":\"PA16FormTEXT\"}";
        when(caseExtraDecorator.decorate(any(PA16FormCaseExtra.class))).thenReturn(extra);
        when(caseExtraDecorator.combineDecorations("", extra)).thenReturn(extra);

        String json = solicitorCoversheetPDFDecorator.decorate(caseDataMock);

        assertEquals(extra, json);
        verify(pa16FormBusinessRuleMock).isApplicable(caseDataMock);
    }

    @Test
    void shouldProvideAdditionalDecorationPA17() {
        when(pa17FormBusinessRuleMock.isApplicable(caseDataMock)).thenReturn(true);
        String extra = "{\"showPa17Form\":\"Yes\",\"pa17FormUrl\":\"PA17FormURL\","
            + "\"pa17FormText\":\"PA17FormTEXT\"}";
        when(caseExtraDecorator.decorate(any(PA17FormCaseExtra.class))).thenReturn(extra);
        when(caseExtraDecorator.combineDecorations("", extra)).thenReturn(extra);

        String json = solicitorCoversheetPDFDecorator.decorate(caseDataMock);

        assertEquals(extra, json);
    }

    @Test
    void shouldProvideIhtEstate207Decoration() {
        when(ihtEstate207BusinessRuleMock.isApplicable(caseDataMock)).thenReturn(true);
        String extra = "{\"ihtEstate207Text\":\"the inheritance tax form IHT 207\", \"showIhtEstate\":\"Yes\"}";
        when(caseExtraDecorator.decorate(any(IhtEstate207CaseExtra.class))).thenReturn(extra);
        when(caseExtraDecorator.combineDecorations("", extra)).thenReturn(extra);

        String json = solicitorCoversheetPDFDecorator.decorate(caseDataMock);

        assertEquals(extra, json);
    }

    @Test
    void shouldProvideAdmonWillRenunciationDecoration() {
        when(admonWillRenunicationRuleMock.isApplicable(caseDataMock)).thenReturn(true);
        String extra = "{\"showAdmonWillRenunciation\": \"Yes\","
            + "\"pa15FormUrl\":\"PA15FormURL\", \"admonWillRenunciationText\":\"admonWillRenunciationText\""
            + "\"pa17FormUrl\":\"PA17FormURL\", \"pa15FormText\":\"PA15\", \"pa17FormText\":\"PA17\"}";
        when(caseExtraDecorator.decorate(any()))
            .thenReturn(extra);
        when(caseExtraDecorator.combineDecorations("", extra)).thenReturn(extra);

        String json = solicitorCoversheetPDFDecorator.decorate(caseDataMock);

        assertEquals(extra, json);
    }

    @Test
    void shouldProvideTcResolutionLodgedWithApplicationDecoration() {
        when(tcResolutionLodgedWithApplicationRuleMock.isApplicable(caseDataMock)).thenReturn(true);
        String extra = "{\"tcResolutionLodgedWithAppText\":\"a certified copy of the resolution\"}";
        when(caseExtraDecorator.decorate(any())).thenReturn(extra);
        when(caseExtraDecorator.combineDecorations("", extra)).thenReturn(extra);

        String json = solicitorCoversheetPDFDecorator.decorate(caseDataMock);

        assertEquals(extra, json);
    }

    @Test
    void shouldProvideAllDecorations() {
        when(pa15FormBusinessRuleMock.isApplicable(caseDataMock)).thenReturn(true);
        when(pa16FormBusinessRuleMock.isApplicable(caseDataMock)).thenReturn(true);
        when(pa17FormBusinessRuleMock.isApplicable(caseDataMock)).thenReturn(true);
        when(ihtEstate207BusinessRuleMock.isApplicable(caseDataMock)).thenReturn(true);
        when(authenticatedTranslationBusinessRuleMock.isApplicable(caseDataMock)).thenReturn(true);
        when(dispenseNoticeSupportDocsRuleMock.isApplicable(caseDataMock)).thenReturn(true);
        String extraPA15 = "extraPA15";
        String extraPA16 = "extraPA16";
        String extraPA17 = "extraPA17";
        when(caseExtraDecorator.decorate(any(PA15FormCaseExtra.class)))
            .thenReturn(extraPA15);
        when(caseExtraDecorator.decorate(any(PA16FormCaseExtra.class)))
            .thenReturn(extraPA16);
        when(caseExtraDecorator.decorate(any(PA17FormCaseExtra.class)))
            .thenReturn(extraPA17);
        String extraIht = "extraIht";
        when(caseExtraDecorator.decorate(any(IhtEstate207CaseExtra.class)))
            .thenReturn(extraIht);
        when(caseExtraDecorator.combineDecorations("", extraIht)).thenReturn(extraIht);
        String extraAuthTrans = "extraAuthTrans";
        when(caseExtraDecorator.decorate(any(AuthenticatedTranslationCaseExtra.class)))
            .thenReturn(extraAuthTrans);
        String extraDispenseNoticeDocs = "extraDispenseNotice";
        when(caseExtraDecorator.decorate(any(DispenseNoticeCaseExtra.class)))
            .thenReturn(extraDispenseNoticeDocs);
        String extra1 = "extraPA15";
        String extra2 = "extraPA15, extraPA16";
        String extra3 = "extraPA15, extraPA16, extraPA17";
        String extra4 = "extraPA15, extraPA16, extraPA17, extraIht";
        String extra5 = "extraPA15, extraPA16, extraPA17, extraIht, extraAuthTrans";
        String extra6 = "extraPA15, extraPA16, extraPA17, extraIht, extraDispenseNotice";
        when(caseExtraDecorator.combineDecorations(any(), any()))
            .thenReturn(extra1, extra2, extra3, extra4, extra5, extra6);
        String json = solicitorCoversheetPDFDecorator.decorate(caseDataMock);

        assertEquals(extra6, json);
    }
}
