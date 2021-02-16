package uk.gov.hmcts.probate.service.payments;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.config.FeeServiceConfiguration;
import uk.gov.hmcts.probate.model.fee.FeeResponse;
import uk.gov.hmcts.probate.model.payments.PaymentFee;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class PaymentFeeBuilderTest {
    @Mock
    private FeeServiceConfiguration feeServiceConfiguration;

    @InjectMocks
    private PaymentFeeBuilder paymentFeeBuilder;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(feeServiceConfiguration.getJurisdiction1()).thenReturn("feeJurisdiction1");
        when(feeServiceConfiguration.getJurisdiction2()).thenReturn("feeJurisdiction2");
    }

    @Test
    public void shouldBuildPaymentFeeFromFeeResponse() {
        FeeResponse feeResponse = FeeResponse.builder().feeAmount(BigDecimal.TEN).code("feeCode").description(
            "feeDescription").version(
            "feeVersion").build();
        BigDecimal volume = BigDecimal.valueOf(2);
        PaymentFee paymentFee = paymentFeeBuilder.buildPaymentFee(feeResponse, volume);
        assertEquals("feeCode", paymentFee.getCode());
        assertEquals("feeDescription", paymentFee.getDescription());
        assertEquals("feeJurisdiction1", paymentFee.getJurisdiction1());
        assertEquals("feeJurisdiction2", paymentFee.getJurisdiction2());
        assertEquals("feeVersion", paymentFee.getVersion());
        assertEquals(20, paymentFee.getCalculatedAmount().longValue());
        assertEquals(10, paymentFee.getFeeAmount().longValue());
        assertEquals(2, paymentFee.getVolume().longValue());

    }

}