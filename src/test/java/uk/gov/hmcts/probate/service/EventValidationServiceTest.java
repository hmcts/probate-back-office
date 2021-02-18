package uk.gov.hmcts.probate.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicList;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicListItem;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.model.payments.PaymentResponse;
import uk.gov.hmcts.probate.validator.CreditAccountPaymentValidationRule;
import uk.gov.hmcts.probate.validator.ValidationRule;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class EventValidationServiceTest {

    @InjectMocks
    private EventValidationService eventValidationService;

    @Mock
    private CCDData ccdDataMock;
    @Mock
    private CreditAccountPaymentValidationRule creditAccountPaymentValidationRuleMock;
    @Mock
    private CaseDetails caseDetailsMock;
    @Mock
    private CaseData caseDataMock;
    @Mock
    private PaymentResponse paymentResponseMock;

    private SimpleValidationRule validationRule;


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
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

        List<FieldErrorResponse> errors = Arrays.asList(FieldErrorResponse.builder().build(), 
            FieldErrorResponse.builder().build());
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(caseDataMock.getSolsPBANumber()).thenReturn(DynamicList.builder()
            .value(DynamicListItem.builder().code("PBACode").label("PBALabel").build())
            .build());
        when(caseDetailsMock.getId()).thenReturn(1234L);
        when(creditAccountPaymentValidationRuleMock.validate("PBALabel", "1234", paymentResponseMock))
            .thenReturn(errors);
        CallbackResponse fieldErrorResponses = eventValidationService
            .validatePaymentResponse(caseDetailsMock, paymentResponseMock, creditAccountPaymentValidationRuleMock);

        assertEquals(2, fieldErrorResponses.getErrors().size());

    }
    
    private class SimpleValidationRule implements ValidationRule {
        private FieldErrorResponse fieldErrorResponse1Mock;
        
        private FieldErrorResponse fieldErrorResponse2Mock;
        
        @Override
        public List<FieldErrorResponse> validate(CCDData form) {
            return Arrays.asList(fieldErrorResponse1Mock, fieldErrorResponse2Mock);
        }
    }
}