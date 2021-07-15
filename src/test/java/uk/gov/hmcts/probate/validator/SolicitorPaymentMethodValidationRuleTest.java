package uk.gov.hmcts.probate.validator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.ApplicationType.PERSONAL;

@RunWith(MockitoJUnitRunner.class)
public class SolicitorPaymentMethodValidationRuleTest {

    @Mock
    private BusinessValidationMessageRetriever businessValidationMessageRetriever;

    @Mock
    private CaseDetails caseDetailsMock;
    @Mock
    private CaseData caseDataMock;
    @Mock
    private CaveatDetails caveatDetailsMock;
    @Mock
    private CaveatData caveatDataMock;


    private SolicitorPaymentMethodValidationRule underTest;

    @Before
    public void setUp() {
        underTest = new SolicitorPaymentMethodValidationRule(businessValidationMessageRetriever);

        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(caseDataMock.getApplicationType()).thenReturn(ApplicationType.SOLICITOR);
        when(caveatDetailsMock.getData()).thenReturn(caveatDataMock);
        when(caveatDataMock.getApplicationType()).thenReturn(ApplicationType.SOLICITOR);
        when(businessValidationMessageRetriever.getMessage(any(), any(), any()))
            .thenReturn("The solicitor payment method selected is not 'fee account'");
    }

    @Test(expected = BusinessValidationException.class)
    public void shouldThrowExceptionForChequePaymentMethodChosen() {
        when(caseDataMock.getSolsPaymentMethods()).thenReturn("cheque");

        underTest.validate(caseDetailsMock);
    }

    @Test()
    public void shouldNotThrowExceptionForFeeAccountPaymentMethodChosen() {
        when(caseDataMock.getSolsPaymentMethods()).thenReturn("fee account");
        underTest.validate(caseDetailsMock);

        verify(businessValidationMessageRetriever, never()).getMessage(any(), any(), any());
    }

    @Test(expected = BusinessValidationException.class)
    public void shouldThrowExceptionForChequePaymentMethodChosenCaveat() {
        when(caveatDataMock.getSolsPaymentMethods()).thenReturn("cheque");

        underTest.validate(caveatDetailsMock);
    }

    @Test()
    public void shouldNotThrowExceptionForFeeAccountPaymentMethodChosenCaveat() {
        when(caveatDataMock.getSolsPaymentMethods()).thenReturn("fee account");
        underTest.validate(caveatDetailsMock);

        verify(businessValidationMessageRetriever, never()).getMessage(any(), any(), any());
    }

    @Test()
    public void shouldNotThrowExceptionForNotSolicitor() {
        when(caveatDataMock.getApplicationType()).thenReturn(PERSONAL);
        when(caveatDataMock.getSolsPaymentMethods()).thenReturn("fee account");
        underTest.validate(caveatDetailsMock);

        verify(businessValidationMessageRetriever, never()).getMessage(any(), any(), any());
    }

}