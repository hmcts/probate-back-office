package uk.gov.hmcts.probate.service.payments;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.config.FeeServiceConfiguration;
import uk.gov.hmcts.probate.model.fee.FeeResponse;
import uk.gov.hmcts.probate.model.payments.PaymentFee;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class PaymentFeeBuilderTest {
    @Mock
    private FeeServiceConfiguration feeServiceConfiguration;

    @InjectMocks
    private PaymentFeeBuilder paymentFeeBuilder;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        when(feeServiceConfiguration.getJurisdiction1()).thenReturn("feeJurisdiction1");
        when(feeServiceConfiguration.getJurisdiction2()).thenReturn("feeJurisdiction2");
    }

    @Test
    void shouldBuildPaymentFeeFromFeeResponse() {
        FeeResponse feeResponse = FeeResponse.builder().feeAmount(BigDecimal.valueOf(1.50)).code("feeCode").description(
            "feeDescription").version(
            "feeVersion").build();
        BigDecimal volume = BigDecimal.valueOf(1);
        PaymentFee paymentFee = paymentFeeBuilder.buildPaymentFee(feeResponse, volume);
        assertEquals("feeCode", paymentFee.getCode());
        assertEquals("feeDescription", paymentFee.getDescription());
        assertEquals("feeJurisdiction1", paymentFee.getJurisdiction1());
        assertEquals("feeJurisdiction2", paymentFee.getJurisdiction2());
        assertEquals("feeVersion", paymentFee.getVersion());
        assertEquals("1.50", "" + paymentFee.getCalculatedAmount());
        assertEquals("1.50", "" + paymentFee.getFeeAmount());
        assertEquals("1", "" + paymentFee.getVolume());

    }

    @Test
    void shouldBuildCopiesPaymentFee() {
        BigDecimal feeAmount = new BigDecimal(1.50).setScale(2);
        String code = "code";
        String description = "description";
        String version = "version";
        BigDecimal volume = BigDecimal.valueOf(1);
        PaymentFee paymentFee = paymentFeeBuilder.buildCopiesPaymentFee(feeAmount, code, description, version, volume);
        assertEquals("code", paymentFee.getCode());
        assertEquals("description", paymentFee.getDescription());
        assertEquals("feeJurisdiction1", paymentFee.getJurisdiction1());
        assertEquals("feeJurisdiction2", paymentFee.getJurisdiction2());
        assertEquals("version", paymentFee.getVersion());
        assertEquals("1.50", "" + paymentFee.getCalculatedAmount());
        assertEquals("1.50", "" + paymentFee.getFeeAmount());
        assertEquals("1", "" + paymentFee.getVolume());

    }
}
