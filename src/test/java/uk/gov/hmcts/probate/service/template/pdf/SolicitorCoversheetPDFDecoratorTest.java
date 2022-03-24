package uk.gov.hmcts.probate.service.template.pdf;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import uk.gov.hmcts.probate.businessrule.AdmonWillRenunicationRule;
import uk.gov.hmcts.probate.businessrule.IhtEstate207BusinessRule;
import uk.gov.hmcts.probate.businessrule.PA14FormBusinessRule;
import uk.gov.hmcts.probate.businessrule.PA15FormBusinessRule;
import uk.gov.hmcts.probate.businessrule.PA16FormBusinessRule;
import uk.gov.hmcts.probate.businessrule.PA17FormBusinessRule;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplying;
import uk.gov.hmcts.probate.businessrule.TCResolutionLodgedWithApplicationRule;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.service.solicitorexecutor.NotApplyingExecutorsMapper;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.IhtEstate207CaseExtra;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.PA14FormCaseExtra;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.PA15FormCaseExtra;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.PA16FormCaseExtra;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.PA17FormCaseExtra;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.decorator.CaseExtraDecorator;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.decorator.SolicitorCoversheetPDFDecorator;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class SolicitorCoversheetPDFDecoratorTest {

    @InjectMocks
    private SolicitorCoversheetPDFDecorator solicitorCoversheetPDFDecorator;

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
    private AdmonWillRenunicationRule admonWillRenunicationRuleMock;
    @Mock
    private TCResolutionLodgedWithApplicationRule tcResolutionLodgedWithApplicationRuleMock;
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
        String caseJson = "";
        String json = solicitorCoversheetPDFDecorator.decorate(caseDataMock);

        assertEquals(caseJson, json);
    }

    @Test
    public void shouldProvidePA14Decoration() {
        when(pa14FormBusinessRuleMock.isApplicable(caseDataMock)).thenReturn(true);
        String extra = "{\"showPa14Form\":\"Yes\",\"pa14FormUrl\":\"PA14FormURL\","
            + "\"pa14FormText\":\"PA14FormTEXT\"}";
        when(caseExtraDecorator.decorate(any(PA14FormCaseExtra.class))).thenReturn(extra);
        when(caseExtraDecorator.combineDecorations("", extra)).thenReturn(extra);
        List<AdditionalExecutorNotApplying> all = new ArrayList<>();
        all.add(AdditionalExecutorNotApplying.builder().build());
        when(notApplyingExecutorsMapper.getAllExecutorsNotApplying(caseDataMock, "MentallyIncapable")).thenReturn(all);

        String json = solicitorCoversheetPDFDecorator.decorate(caseDataMock);

        assertEquals(extra, json);
    }

    @Test
    public void shouldProvidePA15Decoration() {
        when(pa15FormBusinessRuleMock.isApplicable(caseDataMock)).thenReturn(true);
        String extra = "{\"showPa15Form\":\"Yes\",\"pa15FormUrl\":\"PA15FormURL\","
            + "\"pa15FormText\":\"PA15FormTEXT\"}";
        when(caseExtraDecorator.decorate(any(PA15FormCaseExtra.class))).thenReturn(extra);
        when(caseExtraDecorator.combineDecorations("", extra)).thenReturn(extra);
        List<AdditionalExecutorNotApplying> all = new ArrayList<>();
        all.add(AdditionalExecutorNotApplying.builder().build());
        when(notApplyingExecutorsMapper.getAllExecutorsNotApplying(caseDataMock, "Renunciation")).thenReturn(all);

        String json = solicitorCoversheetPDFDecorator.decorate(caseDataMock);

        assertEquals(extra, json);
    }

    @Test
    public void shouldProvidePA16Decoration() {
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
    public void shouldProvideAdditionalDecorationPA17() {
        when(pa17FormBusinessRuleMock.isApplicable(caseDataMock)).thenReturn(true);
        String extra = "{\"showPa17Form\":\"Yes\",\"pa17FormUrl\":\"PA17FormURL\","
            + "\"pa17FormText\":\"PA17FormTEXT\"}";
        when(caseExtraDecorator.decorate(any(PA17FormCaseExtra.class))).thenReturn(extra);
        when(caseExtraDecorator.combineDecorations("", extra)).thenReturn(extra);

        String json = solicitorCoversheetPDFDecorator.decorate(caseDataMock);

        assertEquals(extra, json);
    }

    @Test
    public void shouldProvideIhtEstate207Decoration() {
        when(ihtEstate207BusinessRuleMock.isApplicable(caseDataMock)).thenReturn(true);
        String extra = "{\"ihtEstate207Text\":\"the inheritance tax form IHT 207\", \"showIhtEstate\":\"Yes\"}";
        when(caseExtraDecorator.decorate(any(IhtEstate207CaseExtra.class))).thenReturn(extra);
        when(caseExtraDecorator.combineDecorations("", extra)).thenReturn(extra);

        String json = solicitorCoversheetPDFDecorator.decorate(caseDataMock);

        assertEquals(extra, json);
    }

    @Test
    public void shouldProvideAdmonWillRenunciationDecoration() {
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
    public void shouldProvideTcResolutionLodgedWithApplicationDecoration() {
        when(tcResolutionLodgedWithApplicationRuleMock.isApplicable(caseDataMock)).thenReturn(true);
        String extra = "{\"tcResolutionLodgedWithAppText\":\"a certified copy of the resolution\"}";
        when(caseExtraDecorator.decorate(any())).thenReturn(extra);
        when(caseExtraDecorator.combineDecorations("", extra)).thenReturn(extra);

        String json = solicitorCoversheetPDFDecorator.decorate(caseDataMock);

        assertEquals(extra, json);
    }

    @Test
    public void shouldProvideAllDecorations() {
        when(pa15FormBusinessRuleMock.isApplicable(caseDataMock)).thenReturn(true);
        when(pa16FormBusinessRuleMock.isApplicable(caseDataMock)).thenReturn(true);
        when(pa17FormBusinessRuleMock.isApplicable(caseDataMock)).thenReturn(true);
        when(ihtEstate207BusinessRuleMock.isApplicable(caseDataMock)).thenReturn(true);
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

        String extra1 = "extraPA15";
        String extra2 = "extraPA15, extraPA16";
        String extra3 = "extraPA15, extraPA16, extraPA17";
        when(caseExtraDecorator.combineDecorations(any(), any())).thenReturn(extra1, extra2, extra3);
        String json = solicitorCoversheetPDFDecorator.decorate(caseDataMock);

        assertEquals(extra3, json);
    }
}