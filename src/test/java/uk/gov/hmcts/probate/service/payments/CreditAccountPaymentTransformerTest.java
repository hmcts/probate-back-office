package uk.gov.hmcts.probate.service.payments;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicList;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicListItem;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.fee.FeeResponse;
import uk.gov.hmcts.probate.model.fee.FeesResponse;
import uk.gov.hmcts.probate.model.payments.CreditAccountPayment;
import uk.gov.hmcts.probate.model.payments.PaymentFee;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CreditAccountPaymentTransformerTest {

    @Mock
    private CaseDetails caseDetails;

    @Mock
    private CaseData caseData;

    @Mock
    private FeesResponse feesResponse;

    @Mock
    private FeeResponse feeResponseApplication;

    @Mock
    private FeeResponse feeResponseUK;

    @Mock
    private FeeResponse feeResponseOverseas;

    @Mock
    private PaymentFee paymentFeeApplication;

    @Mock
    private PaymentFee paymentFeeUK;

    @Mock
    private PaymentFee paymentFeeOverseas;

    @MockBean
    private PaymentFeeBuilder paymentFeeBuilder;

    @Autowired
    private CreditAccountPaymentTransformer creditAccountPaymentTransformer;

    @Before
    public void setup() {
        when(caseDetails.getId()).thenReturn(1234L);
        when(caseDetails.getData()).thenReturn(caseData);
        when(caseData.getSolsPBANumber()).thenReturn(DynamicList.builder().value(DynamicListItem.builder().build()).build());
        when(caseData.getSolsSolicitorAppReference()).thenReturn("solsAppRef");
        when(caseData.getSolsSolicitorFirmName()).thenReturn("SolicitorFirmName");
        DynamicListItem item1 = DynamicListItem.builder().code("PBA1111").label("PBA1111Label").build();
        DynamicListItem item2 = DynamicListItem.builder().code("PBA2222").label("PBA2222Label").build();
        DynamicList pbaList = DynamicList.builder().listItems(Arrays.asList(item1, item2)).value(item1).build();
        when(caseData.getSolsPBANumber()).thenReturn(pbaList);
        when(feesResponse.getApplicationFeeResponse()).thenReturn(feeResponseApplication);
        when(feesResponse.getUkCopiesFeeResponse()).thenReturn(feeResponseUK);
        when(feesResponse.getOverseasCopiesFeeResponse()).thenReturn(feeResponseOverseas);
        when(paymentFeeBuilder.buildPaymentFee(feeResponseApplication, BigDecimal.ONE)).thenReturn(paymentFeeApplication);
        when(paymentFeeBuilder.buildPaymentFee(feeResponseUK, BigDecimal.valueOf(1L))).thenReturn(paymentFeeUK);
        when(paymentFeeBuilder.buildPaymentFee(feeResponseOverseas, BigDecimal.valueOf(2L))).thenReturn(paymentFeeOverseas);
    }


    @Test
    public void shouldTransformAll() {
        when(caseData.getExtraCopiesOfGrant()).thenReturn(1L);
        when(caseData.getOutsideUKGrantCopies()).thenReturn(2L);
        when(feesResponse.getTotalAmount()).thenReturn(BigDecimal.valueOf(216.20));

        CreditAccountPayment creditAccountPayment = creditAccountPaymentTransformer.transform(caseDetails, feesResponse);
        assertStandardCreditAccountPayment(creditAccountPayment);
        assertEquals(BigDecimal.valueOf(216.20), creditAccountPayment.getAmount());
        assertEquals(3, creditAccountPayment.getFees().size());
        assertEquals(paymentFeeApplication, creditAccountPayment.getFees().get(0));
        assertEquals(paymentFeeUK, creditAccountPayment.getFees().get(1));
        assertEquals(paymentFeeOverseas, creditAccountPayment.getFees().get(2));
    }

    @Test
    public void shouldTransformApplicationOnly() {
        when(caseData.getExtraCopiesOfGrant()).thenReturn(0L);
        when(caseData.getOutsideUKGrantCopies()).thenReturn(0L);
        when(feesResponse.getTotalAmount()).thenReturn(BigDecimal.valueOf(215.00));

        CreditAccountPayment creditAccountPayment = creditAccountPaymentTransformer.transform(caseDetails, feesResponse);
        assertStandardCreditAccountPayment(creditAccountPayment);
        assertEquals(BigDecimal.valueOf(215.00), creditAccountPayment.getAmount());
        assertEquals(1, creditAccountPayment.getFees().size());
        assertEquals(paymentFeeApplication, creditAccountPayment.getFees().get(0));
    }

    @Test
    public void shouldTransformApplicationAndUKCopiesOnly() {
        when(caseData.getExtraCopiesOfGrant()).thenReturn(1L);
        when(caseData.getOutsideUKGrantCopies()).thenReturn(0L);
        when(feesResponse.getTotalAmount()).thenReturn(BigDecimal.valueOf(215.00));

        CreditAccountPayment creditAccountPayment = creditAccountPaymentTransformer.transform(caseDetails, feesResponse);
        assertStandardCreditAccountPayment(creditAccountPayment);
        assertEquals(BigDecimal.valueOf(215.00), creditAccountPayment.getAmount());
        assertEquals(2, creditAccountPayment.getFees().size());
        assertEquals(paymentFeeApplication, creditAccountPayment.getFees().get(0));
        assertEquals(paymentFeeUK, creditAccountPayment.getFees().get(1));
    }

    @Test
    public void shouldTransformApplicationAndOverseasCopiesOnly() {
        when(caseData.getExtraCopiesOfGrant()).thenReturn(0L);
        when(caseData.getOutsideUKGrantCopies()).thenReturn(2L);
        when(feesResponse.getTotalAmount()).thenReturn(BigDecimal.valueOf(215.00));

        CreditAccountPayment creditAccountPayment = creditAccountPaymentTransformer.transform(caseDetails, feesResponse);
        assertStandardCreditAccountPayment(creditAccountPayment);
        assertEquals(BigDecimal.valueOf(215.00), creditAccountPayment.getAmount());
        assertEquals(2, creditAccountPayment.getFees().size());
        assertEquals(paymentFeeApplication, creditAccountPayment.getFees().get(0));
        assertEquals(paymentFeeOverseas, creditAccountPayment.getFees().get(1));
    }

    private void assertStandardCreditAccountPayment(CreditAccountPayment creditAccountPayment) {
        assertEquals("PBA1111", creditAccountPayment.getAccountNumber());
        assertEquals("solsAppRef", creditAccountPayment.getCaseReference());
        assertEquals("1234", creditAccountPayment.getCcdCaseNumber());
        assertEquals("solsAppRef", creditAccountPayment.getCustomerReference());
        assertEquals("Probate Solicitor payment", creditAccountPayment.getDescription());
        assertEquals("SolicitorFirmName", creditAccountPayment.getOrganisationName());
        assertEquals("GBP", creditAccountPayment.getCurrency());
        assertEquals("PROBATE", creditAccountPayment.getService());
        assertEquals("ABA6", creditAccountPayment.getSiteId());
    }
}