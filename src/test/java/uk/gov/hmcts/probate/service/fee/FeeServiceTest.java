package uk.gov.hmcts.probate.service.fee;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.probate.config.FeeServiceConfiguration;
import uk.gov.hmcts.probate.insights.AppInsights;
import uk.gov.hmcts.probate.model.fee.Fee;
import uk.gov.hmcts.probate.model.fee.FeeServiceResponse;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

public class FeeServiceTest {

    @InjectMocks
    private FeeService feeService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ResponseEntity<Fee> responseEntity;

    @Mock
    private Fee fee;

    @Mock
    private FeeServiceConfiguration feeServiceConfiguration;

    @Mock
    AppInsights appInsights;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(responseEntity.getBody()).thenReturn(fee);
        when(restTemplate.getForEntity(any(), eq(Fee.class))).thenReturn(responseEntity);
        when(feeServiceConfiguration.getUrl()).thenReturn("http://test.test/lookup");
    }

    @Test
    public void issueFeeShouldReturnPositiveValue() {
        when(fee.getFeeAmount()).thenReturn(BigDecimal.ONE);
        when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);

        BigDecimal issueFee = feeService.getApplicationFee(BigDecimal.valueOf(5000));

        assertEquals(BigDecimal.ONE, issueFee);
    }

    @Test
    public void issueFeeShouldReturnZeroValue() {
        when(responseEntity.getStatusCode()).thenReturn(HttpStatus.NO_CONTENT);

        BigDecimal issueFee = feeService.getApplicationFee(BigDecimal.valueOf(1000));

        assertEquals(BigDecimal.ZERO, issueFee);
    }

    @Test
    public void copiesFeeShouldReturnZeroValue() {
        when(fee.getFeeAmount()).thenReturn(BigDecimal.ZERO);
        when(responseEntity.getStatusCode()).thenReturn(HttpStatus.NO_CONTENT);
        BigDecimal copiesFee = feeService.getCopiesFee(5L);

        assertEquals(BigDecimal.ZERO, copiesFee);
    }

    @Test
    public void copiesFeeEqualsZero() {
        when(fee.getFeeAmount()).thenReturn(BigDecimal.ZERO);
        when(responseEntity.getStatusCode()).thenReturn(HttpStatus.NO_CONTENT);
        BigDecimal copiesFee = feeService.getCopiesFee(null);

        assertEquals(BigDecimal.ZERO, copiesFee);
    }

    @Test
    public void getTotalFee() {
        when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
        when(fee.getFeeAmount()).thenReturn(BigDecimal.ONE);

        FeeServiceResponse feeServiceResponse = feeService.getTotalFee(BigDecimal.valueOf(5001), 1L, 1L);
        assertEquals(BigDecimal.ONE, feeServiceResponse.getApplicationFee());
        assertEquals(BigDecimal.ONE, feeServiceResponse.getFeeForUkCopies());
        assertEquals(BigDecimal.ONE, feeServiceResponse.getFeeForNonUkCopies());
    }
}
