package uk.gov.hmcts.probate.service.template.pdf.caseextra.decorator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.businessrule.IhtEstateNotCompletedBusinessRule;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.IhtEstateConfirmCaseExtra;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class SolicitorLegalStatementPDFDecoratorTest {

    @InjectMocks
    private SolicitorLegalStatementPDFDecorator solicitorLegalStatementPDFDecorator;

    @Mock
    private CaseExtraDecorator caseExtraDecorator;
    @Mock
    private IhtEstateNotCompletedBusinessRule ihtEstateNotCompletedBusinessRule;
    @Mock
    private CaseData caseDataMock;

    @Test
    void shouldDecorateForIhtEstateNotCompleted() {
        when(ihtEstateNotCompletedBusinessRule.isApplicable(caseDataMock)).thenReturn(true);
        when(caseExtraDecorator.decorate(any(IhtEstateConfirmCaseExtra.class))).thenReturn("someJson");
        String actual = solicitorLegalStatementPDFDecorator.decorate(caseDataMock);
        assertEquals("someJson", actual);
    }

    @Test
    void shouldNotDecorate() {
        when(ihtEstateNotCompletedBusinessRule.isApplicable(caseDataMock)).thenReturn(false);
        String actual = solicitorLegalStatementPDFDecorator.decorate(caseDataMock);
        assertEquals("", actual);
    }
}
