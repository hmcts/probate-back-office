package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.OCRMappingException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Component
class OCRFieldPaymentMethodMapperTest {

    private static final String PAYMENT_METHOD_DEBTORCREDIT = "debitOrCredit";
    private static final String PAYMENT_METHOD_CHEQUE = "cheque";
    private static final String PAYMENT_METHOD_CASH = "cash";
    private static final String PAYMENT_METHOD_FEEACCOUNT = "feeAccount";

    private OCRFieldPaymentMethodMapper paymentMethodMapper = new OCRFieldPaymentMethodMapper();

    @Test
    void testPaymentMethodDebitOrCredit() {
        String response = paymentMethodMapper.validateKnownPaymentMethod(PAYMENT_METHOD_DEBTORCREDIT);
        assertEquals(PAYMENT_METHOD_DEBTORCREDIT, response);
    }

    @Test
    void testPaymentMethodCheque() {
        String response = paymentMethodMapper.validateKnownPaymentMethod(PAYMENT_METHOD_CHEQUE);
        assertEquals(PAYMENT_METHOD_CHEQUE, response);
    }

    @Test
    void testPaymentMethodCash() {
        String response = paymentMethodMapper.validateKnownPaymentMethod(PAYMENT_METHOD_CASH);
        assertEquals(PAYMENT_METHOD_CASH, response);
    }

    @Test
    void testPaymentMethodFeeAccount() {
        String response = paymentMethodMapper.validateKnownPaymentMethod(PAYMENT_METHOD_FEEACCOUNT);
        assertEquals(PAYMENT_METHOD_FEEACCOUNT, response);
    }

    @Test
    void testPaymentMethodNull() {
        String response = paymentMethodMapper.validateKnownPaymentMethod(null);
        assertNull(response);
    }

    @Test
    void testPaymentMethodEmpty() {
        String response = paymentMethodMapper.validateKnownPaymentMethod("");
        assertNull(response);
    }

    @Test
    void testPaymentMethodError() {
        assertThrows(OCRMappingException.class, () -> {
            String response = paymentMethodMapper.validateKnownPaymentMethod("notfound");
            assertTrue(false);
        });
    }
}
