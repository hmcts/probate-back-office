package uk.gov.hmcts.probate.service.fee;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.probate.config.FeeServiceConfiguration;
import uk.gov.hmcts.probate.exception.ClientDataException;
import uk.gov.hmcts.probate.insights.AppInsights;
import uk.gov.hmcts.probate.model.fee.Fee;
import uk.gov.hmcts.probate.model.fee.FeeServiceResponse;
import uk.gov.hmcts.probate.service.FeatureToggleService;

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

    @Mock
    FeatureToggleService featureToggleService;

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
    public void copiesFeeEqualsNonZero() {

        when(feeServiceConfiguration.getUrl()).thenReturn("http://test.test/lookupWithKeyword");
        when(feeServiceConfiguration.getKeyword()).thenReturn("FeeKey");
        when(restTemplate.getForEntity(eq("http://test.test/lookupWithKeywordnull?service&jurisdiction1&"
                + "jurisdiction2&channel&applicant_type&event=copies&amount_or_volume=1&keyword=KeyFee"),
                eq(Fee.class))).thenReturn(responseEntity);
        when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
        when(fee.getFeeAmount()).thenReturn(BigDecimal.ONE);
        BigDecimal copiesFee = feeService.getCopiesFee(1L);

        assertEquals(BigDecimal.ONE, copiesFee);
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

    @Test
    public void getTotalFeeWithNewKeyword() {
        when(feeServiceConfiguration.getUrl()).thenReturn("http://test.test/lookupWithKeyword");
        when(feeServiceConfiguration.getKeyword()).thenReturn("GrantWill");
        when (featureToggleService.isNewFeeRegisterCodeEnabled()).thenReturn(true);
        when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
        when(fee.getFeeAmount()).thenReturn(BigDecimal.ONE);

        FeeServiceResponse feeServiceResponse = feeService.getTotalFee(BigDecimal.valueOf(5001), 1L, 1L);
        assertEquals(BigDecimal.ONE, feeServiceResponse.getApplicationFee());
        assertEquals(BigDecimal.ONE, feeServiceResponse.getFeeForUkCopies());
        assertEquals(BigDecimal.ONE, feeServiceResponse.getFeeForNonUkCopies());
    }

    @Test
    public void getTotalFeeWithOldKeyword() {
        when(feeServiceConfiguration.getUrl()).thenReturn("http://test.test/lookupWithKeyword");
        when(feeServiceConfiguration.getKeyword()).thenReturn("NewFee");
        when (featureToggleService.isNewFeeRegisterCodeEnabled()).thenReturn(true);
        when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
        when(fee.getFeeAmount()).thenReturn(BigDecimal.ONE);

        FeeServiceResponse feeServiceResponse = feeService.getTotalFee(BigDecimal.valueOf(5001), 1L, 1L);
        assertEquals(BigDecimal.ONE, feeServiceResponse.getApplicationFee());
        assertEquals(BigDecimal.ONE, feeServiceResponse.getFeeForUkCopies());
        assertEquals(BigDecimal.ONE, feeServiceResponse.getFeeForNonUkCopies());
    }

    @Test(expected = ClientDataException.class)
    public void testExceptionIfRestTemplateReturnsNull() {
        when(restTemplate.getForEntity(any(), eq(Fee.class))).thenReturn(null);
        feeService.getApplicationFee(BigDecimal.valueOf(5000));
    }

    @Test(expected = ClientDataException.class)
    public void testExceptionIfResponseEntityGetBodyReturnsNull() {
        when(fee.getFeeAmount()).thenReturn(BigDecimal.ONE);
        when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);

        when(responseEntity.getBody()).thenReturn(null);
        feeService.getApplicationFee(BigDecimal.valueOf(5000));
    }

    @Test(expected = ClientDataException.class)
    public void copiesFeeExceptionIfResponseEntityGetBodyReturnsNull() {

        when(feeServiceConfiguration.getUrl()).thenReturn("http://test.test/lookupWithKeyword");
        when(feeServiceConfiguration.getKeyword()).thenReturn("FeeKey");
        when(restTemplate.getForEntity(eq("http://test.test/lookupWithKeywordnull?service&jurisdiction1&"
                + "jurisdiction2&channel&applicant_type&event=copies&amount_or_volume=1&keyword=KeyFee"),
            eq(Fee.class))).thenReturn(responseEntity);
        when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
        when(fee.getFeeAmount()).thenReturn(BigDecimal.ONE);
        when(responseEntity.getBody()).thenReturn(null);
        feeService.getCopiesFee(1L);
    }
}
