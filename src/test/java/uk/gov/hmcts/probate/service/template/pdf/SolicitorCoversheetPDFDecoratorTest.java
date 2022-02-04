package uk.gov.hmcts.probate.service.template.pdf;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import uk.gov.hmcts.probate.businessrule.DispenseNoticeSupportDocsRule;
import uk.gov.hmcts.probate.businessrule.IhtEstate207BusinessRule;
import uk.gov.hmcts.probate.businessrule.PA16FormBusinessRule;
import uk.gov.hmcts.probate.businessrule.PA17FormBusinessRule;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.DispenseNoticeCaseExtra;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.IhtEstate207CaseExtra;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.PA16FormCaseExtra;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.PA17FormCaseExtra;
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
    private PA17FormBusinessRule pa17FormBusinessRuleMock;
    @Mock
    private IhtEstate207BusinessRule ihtEstate207BusinessRuleMock;
    @Mock
    private DispenseNoticeSupportDocsRule dispenseNoticeSupportDocsRule;
    @Mock
    private CaseExtraDecorator caseExtraDecorator;
    @Mock
    private CaseData caseDataMock;

    @Before
    public void setup() {
        initMocks(this);
        setBusinessRuleMocksApplicable(false);
    }

    private void setBusinessRuleMocksApplicable(boolean isApplicable) {
        when(pa16FormBusinessRuleMock.isApplicable(caseDataMock)).thenReturn(isApplicable);
        when(pa17FormBusinessRuleMock.isApplicable(caseDataMock)).thenReturn(isApplicable);
        when(ihtEstate207BusinessRuleMock.isApplicable(caseDataMock)).thenReturn(isApplicable);
        when(dispenseNoticeSupportDocsRule.isApplicable(caseDataMock)).thenReturn(isApplicable);
    }
    
    @Test
    public void shouldNotProvideAdditionalDecoration() {
        String caseJson = "";
        String json = solicitorCoversheetPDFDecorator.decorate(caseDataMock);
        
        assertEquals(caseJson, json);
    }

    @Test
    public void shouldProvidePA16Decoration() {
        when(pa16FormBusinessRuleMock.isApplicable(caseDataMock)).thenReturn(true);
        String extra = "{\"showPa16Form\":\"Yes\",\"pa16FormUrl\":\"PA16FormURL\","
            + "\"pa16FormText\":\"PA16FormTEXT\"}";
        when(caseExtraDecorator.decorate(any()))
            .thenReturn(extra);
        
        String json = solicitorCoversheetPDFDecorator.decorate(caseDataMock);

        assertEquals(extra, json);
    }

    @Test
    public void shouldProvideAdditionalDecorationPA17() {
        when(pa17FormBusinessRuleMock.isApplicable(caseDataMock)).thenReturn(true);
        String extra = "{\"showPa17Form\":\"Yes\",\"pa17FormUrl\":\"PA17FormURL\","
            + "\"pa17FormText\":\"PA17FormTEXT\"}";
        when(caseExtraDecorator.decorate(any()))
            .thenReturn(extra);
        when(caseExtraDecorator.combineDecorations("", caseExtraDecorator.decorate(extra)))
                .thenReturn(extra);
        String json = solicitorCoversheetPDFDecorator.decorate(caseDataMock);

        assertEquals(extra, json);
    }

    @Test
    public void shouldProvideIhtEstate207Decoration() {
        when(ihtEstate207BusinessRuleMock.isApplicable(caseDataMock)).thenReturn(true);
        String extra = "{\"ihtEstate207Text\":\"the inheritance tax form IHT 207\", \"showIhtEstate\":\"Yes\"}";
        when(caseExtraDecorator.decorate(any()))
            .thenReturn(extra);
        when(caseExtraDecorator.combineDecorations("", extra)).thenReturn(extra);

        String json = solicitorCoversheetPDFDecorator.decorate(caseDataMock);

        assertEquals(extra, json);
    }

    @Test
    public void shouldProvideAllDecorations() {
        setBusinessRuleMocksApplicable(true);
        String extraPA16 = "{\"showPa16Form\":\"Yes\",\"pa16FormUrl\":\"PA16FormURL\","
            + "\"pa16FormText\":\"PA16FormTEXT\"}";
        when(caseExtraDecorator.decorate(any(PA16FormCaseExtra.class)))
            .thenReturn(extraPA16);
        String extraPA17 = "{\"showPa17Form\":\"Yes\",\"pa17FormUrl\":\"PA17FormURL\","
                + "\"pa17FormText\":\"PA17FormTEXT\"}";
        when(caseExtraDecorator.decorate(any(PA17FormCaseExtra.class)))
                .thenReturn(extraPA17);
        String extraIht = "{\"ihtEstate207Text\":\"the inheritance tax form IHT 207\"}";
        when(caseExtraDecorator.decorate(any(IhtEstate207CaseExtra.class)))
            .thenReturn(extraIht);
        String extraDispenseNoticeDocs = "{\"dispenseNoticeText\"}";
        when(caseExtraDecorator.decorate(any(DispenseNoticeCaseExtra.class)))
                .thenReturn(extraDispenseNoticeDocs);



        String extraTwo = "{\"showPa16Form\":\"Yes\",\"pa16FormUrl\":\"PA16FormURL\","
                + "\"pa16FormText\":\"PA16FormTEXT\","
                + "\"showPa17Form\":\"Yes\",\"pa17FormUrl\":\"PA17FormURL\","
                + "\"pa17FormText\":\"PA17FormTEXT\"}";
        String extraThree =  extraTwo + ", \"the inheritance tax form IHT 207\"";
        String extraFour = extraThree + ",\"dispenseNoticeText\"}";
        when(caseExtraDecorator.combineDecorations(extraPA16, extraPA17)).thenReturn(extraTwo);
        when(caseExtraDecorator.combineDecorations(extraTwo, extraIht)).thenReturn(extraThree);
        when(caseExtraDecorator.combineDecorations(extraThree, extraDispenseNoticeDocs)).thenReturn(extraFour);
        String json = solicitorCoversheetPDFDecorator.decorate(caseDataMock);

        assertEquals(extraFour, json);
    }
}