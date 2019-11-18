package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import org.junit.Test;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.OCRMappingException;
import uk.gov.hmcts.reform.probate.model.cases.MaritalStatus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@Component
public class OCRFieldPaymentMapperTest {

    private static final String PAYMENT_METHOD_DEBTORCREDIT = "debitOrCredit";
    private static final String PAYMENT_METHOD_CHEQUE = "cheque";
    private static final String PAYMENT_METHOD_CASH = "cash";
    private static final String PAYMENT_METHOD_FEEACCOUNT = "feeAccount";

    private OCRFieldPaymentMapper paymentMethodMapper = new OCRFieldPaymentMapper();

    @Test
    public void testPaymentMethodDebitOrCredit() {
        String response = paymentMethodMapper.validateKnownPaymentMethod(PAYMENT_METHOD_DEBTORCREDIT);
        assertEquals(PAYMENT_METHOD_DEBTORCREDIT, response);
    }

    @Test
    public void testPaymentMethodCheque() {
        String response = paymentMethodMapper.validateKnownPaymentMethod(PAYMENT_METHOD_CHEQUE);
        assertEquals(PAYMENT_METHOD_CHEQUE, response);
    }

    @Test
    public void testPaymentMethodCash() {
        String response = paymentMethodMapper.validateKnownPaymentMethod(PAYMENT_METHOD_CASH);
        assertEquals(PAYMENT_METHOD_CASH, response);
    }

    @Test
    public void testPaymentMethodFeeAccount() {
        String response = paymentMethodMapper.validateKnownPaymentMethod(PAYMENT_METHOD_FEEACCOUNT);
        assertEquals(PAYMENT_METHOD_FEEACCOUNT, response);
    }

    @Test
    public void testPaymentMethodNull() {
        String response = paymentMethodMapper.validateKnownPaymentMethod(null);
        assertNull(response);
    }

    @Test
    public void testPaymentMethodEmpty() {
        String response = paymentMethodMapper.validateKnownPaymentMethod("");
        assertNull(response);
    }

    @Test(expected = OCRMappingException.class)
    public void testPaymentMethodError() {
        String response = paymentMethodMapper.validateKnownPaymentMethod("notfound");
        assertTrue(false);
    }
}