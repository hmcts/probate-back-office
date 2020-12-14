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
import uk.gov.hmcts.probate.exception.ClientDataException;
import uk.gov.hmcts.probate.insights.AppInsights;
import uk.gov.hmcts.probate.model.fee.FeeResponse;
import uk.gov.hmcts.probate.model.fee.FeeServiceResponse;
import uk.gov.hmcts.probate.model.fee.FeesResponse;

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
    private ResponseEntity<FeeResponse> responseEntity;

    @Mock
    private FeeResponse feeResponse;

    @Mock
    private FeeServiceConfiguration feeServiceConfiguration;

    @Mock
    AppInsights appInsights;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(responseEntity.getBody()).thenReturn(feeResponse);
        when(restTemplate.getForEntity(any(), eq(FeeResponse.class))).thenReturn(responseEntity);
        when(feeServiceConfiguration.getUrl()).thenReturn("http://test.test/lookup");
    }

    @Test
    public void issueFeeShouldReturnPositiveValue() {
        when(feeResponse.getFeeAmount()).thenReturn(BigDecimal.ONE);
        when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);

        FeeResponse issueFee = feeService.getApplicationFeeResponse(BigDecimal.valueOf(5000));

        assertEquals(BigDecimal.ONE, issueFee.getFeeAmount());
    }

    @Test
    public void issueFeeShouldReturnZeroValue() {
        when(responseEntity.getStatusCode()).thenReturn(HttpStatus.NO_CONTENT);

        FeeResponse issueFee = feeService.getApplicationFeeResponse(BigDecimal.valueOf(1000));

        assertEquals(BigDecimal.ZERO, issueFee.getFeeAmount());
    }

    @Test
    public void copiesFeeShouldReturnZeroValue() {
        when(feeResponse.getFeeAmount()).thenReturn(BigDecimal.ZERO);
        when(responseEntity.getStatusCode()).thenReturn(HttpStatus.NO_CONTENT);
        FeeResponse copiesFee = feeService.getCopiesFeeResponse(5L);

        assertEquals(BigDecimal.ZERO, copiesFee.getFeeAmount());
    }

    @Test
    public void copiesFeeEqualsZero() {
        when(feeResponse.getFeeAmount()).thenReturn(BigDecimal.ZERO);
        when(responseEntity.getStatusCode()).thenReturn(HttpStatus.NO_CONTENT);
        FeeResponse copiesFee = feeService.getCopiesFeeResponse(null);

        assertEquals(BigDecimal.ZERO, copiesFee.getFeeAmount());
    }

    @Test
    public void copiesFeeEqualsNonZero() {

        when(feeServiceConfiguration.getUrl()).thenReturn("http://test.test/lookupWithKeyword");
        when(feeServiceConfiguration.getKeyword()).thenReturn("FeeKey");
        when(restTemplate.getForEntity(eq("http://test.test/lookupWithKeywordnull?service&jurisdiction1&"
                + "jurisdiction2&channel&applicant_type&event=copies&amount_or_volume=1&keyword=KeyFee"),
                eq(FeeResponse.class))).thenReturn(responseEntity);
        when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
        when(feeResponse.getFeeAmount()).thenReturn(BigDecimal.ONE);
        FeeResponse copiesFee = feeService.getCopiesFeeResponse(1L);

        assertEquals(BigDecimal.ONE, copiesFee.getFeeAmount());
    }

    @Test
    public void getTotalFee() {
        when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
        when(feeResponse.getFeeAmount()).thenReturn(BigDecimal.ONE);

        FeesResponse feesResponse = feeService.getAllFeesData(BigDecimal.valueOf(5001), 1L, 1L);
        assertEquals(BigDecimal.ONE, feesResponse.getApplicationFeeResponse().getFeeAmount());
        assertEquals(BigDecimal.ONE, feesResponse.getUkCopiesFeeResponse().getFeeAmount());
        assertEquals(BigDecimal.ONE, feesResponse.getOverseasCopiesFeeResponse().getFeeAmount());
    }

    @Test(expected = ClientDataException.class)
    public void testExceptionIfRestTemplateReturnsNull() {
        when(restTemplate.getForEntity(any(), eq(FeeResponse.class))).thenReturn(null);
        feeService.getApplicationFeeResponse(BigDecimal.valueOf(5000));
    }

    @Test(expected = ClientDataException.class)
    public void testExceptionIfResponseEntityGetBodyReturnsNull() {
        when(feeResponse.getFeeAmount()).thenReturn(BigDecimal.ONE);
        when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);

        when(responseEntity.getBody()).thenReturn(null);
        feeService.getApplicationFeeResponse(BigDecimal.valueOf(5000));
    }

    @Test(expected = ClientDataException.class)
    public void copiesFeeExceptionIfResponseEntityGetBodyReturnsNull() {

        when(feeServiceConfiguration.getUrl()).thenReturn("http://test.test/lookupWithKeyword");
        when(feeServiceConfiguration.getKeyword()).thenReturn("FeeKey");
        when(restTemplate.getForEntity(eq("http://test.test/lookupWithKeywordnull?service&jurisdiction1&"
                + "jurisdiction2&channel&applicant_type&event=copies&amount_or_volume=1&keyword=KeyFee"),
            eq(FeeResponse.class))).thenReturn(responseEntity);
        when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
        when(feeResponse.getFeeAmount()).thenReturn(BigDecimal.ONE);
        when(responseEntity.getBody()).thenReturn(null);
        feeService.getCopiesFeeResponse(1L);
    }
}