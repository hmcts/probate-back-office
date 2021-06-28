package uk.gov.hmcts.probate.validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicList;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicListItem;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.payments.PaymentResponse;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class CreditAccountPaymentValidationRuleTest {

    @InjectMocks
    private CreditAccountPaymentValidationRule creditAccountPaymentValidationRule;

    @Mock
    private BusinessValidationMessageService businessValidationMessageService;
    @Mock
    private CaseDetails caseDetails;
    @Mock
    private CaseData caseData;
    @Mock
    private PaymentResponse paymentResponse;

    private static final Long CASE_ID = 1234L;
    private static final String PBA_CODE = "PBACode";
    private static final String PBA_LABEL = "PBALabel";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(caseDetails.getData()).thenReturn(caseData);
        when(caseDetails.getId()).thenReturn(CASE_ID);
        when(caseData.getSolsPBANumber()).thenReturn(DynamicList.builder()
            .value(DynamicListItem.builder().code(PBA_CODE).label(PBA_LABEL).build())
            .build());
    }

    @Test
    public void shouldReturnNoErrors() {
        when(paymentResponse.getStatus()).thenReturn("Success");
        List<FieldErrorResponse> errors = creditAccountPaymentValidationRule.validate(PBA_CODE,
            CASE_ID.toString(), paymentResponse);
        assertEquals(true, errors.isEmpty());
    }

    @Test
    public void shouldReturnErrors() {
        when(paymentResponse.getStatus()).thenReturn("Failure");
        when(caseData.getSolsPBANumber()).thenReturn(DynamicList.builder().value(DynamicListItem.builder().code(
            "PBAError").build()).build());
        FieldErrorResponse error = FieldErrorResponse.builder().code("PBAError").build();
        when(businessValidationMessageService.generateError(any(String.class), any(String.class),
            any(String[].class))).thenReturn(error);

        List<FieldErrorResponse> errors = creditAccountPaymentValidationRule.validate(PBA_CODE,
            CASE_ID.toString(), paymentResponse);
        assertEquals(1, errors.size());
        assertEquals("PBAError", errors.get(0).getCode());
    }
}