package uk.gov.hmcts.probate.service.template.pdf.caseextra.decorator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.probate.businessrule.IhtEstate207BusinessRule;
import uk.gov.hmcts.probate.businessrule.IhtEstate400421BusinessRule;
import uk.gov.hmcts.probate.businessrule.IhtEstateNotCompletedBusinessRule;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.IhtEstateConfirmCaseExtra;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SolicitorLegalStatementPDFDecoratorTest {

    @InjectMocks
    private SolicitorLegalStatementPDFDecorator solicitorLegalStatementPDFDecorator;

    @Mock
    private CaseExtraDecorator caseExtraDecorator;
    @Mock
    private IhtEstateNotCompletedBusinessRule ihtEstateNotCompletedBusinessRule;
    @Mock
    private IhtEstate400421BusinessRule ihtEstate400421BusinessRule;
    @Mock
    private IhtEstate207BusinessRule ihtEstate207BusinessRule;
    @Mock
    private CaseData caseDataMock;

    @Test
    public void shouldDecorateForIhtEstateNotCompleted() {
        when(ihtEstateNotCompletedBusinessRule.isApplicable(caseDataMock)).thenReturn(true);
        when(caseExtraDecorator.decorate(any(IhtEstateConfirmCaseExtra.class))).thenReturn("someJson");
        String actual = solicitorLegalStatementPDFDecorator.decorate(caseDataMock);
        assertEquals("someJson", actual);
    }

    @Test
    public void shouldDecorateForIhtEstate400421() {
        when(ihtEstate400421BusinessRule.isApplicable(caseDataMock)).thenReturn(true);
        when(caseExtraDecorator.decorate(any(IhtEstateConfirmCaseExtra.class))).thenReturn("someJson");
        String actual = solicitorLegalStatementPDFDecorator.decorate(caseDataMock);
        assertEquals("someJson", actual);
    }

    @Test
    public void shouldDecorateForIhtEstate207() {
        when(ihtEstate207BusinessRule.isApplicable(caseDataMock)).thenReturn(true);
        when(caseExtraDecorator.decorate(any(IhtEstateConfirmCaseExtra.class))).thenReturn("someJson");
        String actual = solicitorLegalStatementPDFDecorator.decorate(caseDataMock);
        assertEquals("someJson", actual);
    }
    
    @Test
    public void shouldNotDecorate() {
        when(ihtEstate400421BusinessRule.isApplicable(caseDataMock)).thenReturn(false);
        when(ihtEstate207BusinessRule.isApplicable(caseDataMock)).thenReturn(false);
        when(ihtEstate207BusinessRule.isApplicable(caseDataMock)).thenReturn(false);
        String actual = solicitorLegalStatementPDFDecorator.decorate(caseDataMock);
        assertEquals("", actual);
    }
}