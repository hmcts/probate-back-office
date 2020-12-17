package uk.gov.hmcts.probate.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.model.payments.PaymentResponse;
import uk.gov.hmcts.probate.transformer.CCDDataTransformer;
import uk.gov.hmcts.probate.transformer.CaveatDataTransformer;
import uk.gov.hmcts.probate.validator.CreditAccountPaymentValidationRule;
import uk.gov.hmcts.probate.validator.ValidationRule;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class EventValidationServiceTest {

    private EventValidationService eventValidationService;

    @Mock
    private CCDData ccdDataMock;
    @Mock
    private FieldErrorResponse fieldErrorResponse1Mock;
    @Mock
    private FieldErrorResponse fieldErrorResponse2Mock;
    @Mock
    private CCDDataTransformer ccdBeanTransformer;
    @Mock
    private CaveatDataTransformer caveatDataTransformer;

    private SimpleValidationRule validationRule;


    @Before
    public void setup() {
        eventValidationService = new EventValidationService(ccdBeanTransformer, caveatDataTransformer);
        validationRule = new SimpleValidationRule();
    }


    @Test
    public void shouldGatherValidationErrors() {

        List<FieldErrorResponse> fieldErrorResponses = eventValidationService
            .validate(ccdDataMock, Collections.singletonList(validationRule));

        assertEquals(2, fieldErrorResponses.size());

    }

    @Test
    public void shouldGatherPaymentValidationErrors() {

        CreditAccountPaymentValidationRule creditAccountPaymentValidationRuleMock = Mockito.mock(CreditAccountPaymentValidationRule.class);
        CaseDetails caseDetailsMock = Mockito.mock(CaseDetails.class);
        PaymentResponse paymentResponseMock = Mockito.mock(PaymentResponse.class);

        List<FieldErrorResponse> errors = Arrays.asList(FieldErrorResponse.builder().build(), FieldErrorResponse.builder().build());
        when(creditAccountPaymentValidationRuleMock.validate(caseDetailsMock, paymentResponseMock)).thenReturn(errors);
        CallbackResponse fieldErrorResponses = eventValidationService
            .validatePaymentresponse(caseDetailsMock, paymentResponseMock, creditAccountPaymentValidationRuleMock);

        assertEquals(2, fieldErrorResponses.getErrors().size());

    }

    private class SimpleValidationRule implements ValidationRule {
        @Override
        public List<FieldErrorResponse> validate(CCDData form) {
            return Arrays.asList(fieldErrorResponse1Mock, fieldErrorResponse2Mock);
        }
    }
}