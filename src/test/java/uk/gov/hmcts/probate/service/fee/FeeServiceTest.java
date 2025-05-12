package uk.gov.hmcts.probate.service.fee;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.probate.config.FeeServiceConfiguration;
import uk.gov.hmcts.probate.exception.ClientDataException;
import uk.gov.hmcts.probate.exception.SocketException;
import uk.gov.hmcts.probate.model.fee.FeeResponse;
import uk.gov.hmcts.probate.model.fee.FeesResponse;
import uk.gov.hmcts.probate.service.FeatureToggleService;

import java.math.BigDecimal;
import java.net.SocketTimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FeeServiceTest {

    @Mock
    FeatureToggleService featureToggleService;

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

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        when(responseEntity.getBody()).thenReturn(feeResponse);
        when(restTemplate.getForEntity(any(), eq(FeeResponse.class))).thenReturn(responseEntity);
        when(feeServiceConfiguration.getUrl()).thenReturn("http://test.test/lookup");
    }

    @Test
    void issueFeeShouldReturnPositiveValue() throws SocketTimeoutException {
        when(feeResponse.getFeeAmount()).thenReturn(BigDecimal.ONE);
        when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);

        FeeResponse issueFee = feeService.getApplicationFeeResponse(BigDecimal.valueOf(5000));

        assertEquals(BigDecimal.ONE, issueFee.getFeeAmount());
    }

    @Test
    void issueFeeShouldReturnZeroValue() throws SocketTimeoutException {
        when(responseEntity.getStatusCode()).thenReturn(HttpStatus.NO_CONTENT);

        FeeResponse issueFee = feeService.getApplicationFeeResponse(BigDecimal.valueOf(1000));

        assertEquals(BigDecimal.ZERO, issueFee.getFeeAmount());
    }

    @Test
    void copiesFeeShouldReturnZeroValue() throws SocketTimeoutException {
        when(feeResponse.getFeeAmount()).thenReturn(BigDecimal.ZERO);
        when(responseEntity.getStatusCode()).thenReturn(HttpStatus.NO_CONTENT);
        FeeResponse copiesFee = feeService.getCopiesFeeResponse(5L);

        assertEquals(BigDecimal.ZERO, copiesFee.getFeeAmount());
    }

    @Test
    void copiesFeeEqualsZero() throws SocketTimeoutException {
        when(feeResponse.getFeeAmount()).thenReturn(BigDecimal.ZERO);
        when(responseEntity.getStatusCode()).thenReturn(HttpStatus.NO_CONTENT);
        FeeResponse copiesFee = feeService.getCopiesFeeResponse(null);

        assertEquals(BigDecimal.ZERO, copiesFee.getFeeAmount());
    }

    @Test
    void copiesFeeEqualsNonZeroNewFeeToggledOff() throws SocketTimeoutException {

        when(feeServiceConfiguration.getUrl()).thenReturn("http://test.test/lookupWithKeyword");
        when(feeServiceConfiguration.getKeyword()).thenReturn("FeeKey");
        when(restTemplate.getForEntity(eq("http://test.test/lookupWithKeywordnull?service&jurisdiction1&"
                + "jurisdiction2&channel&applicant_type&event=copies&amount_or_volume=1&keyword=KeyFee"),
            eq(FeeResponse.class))).thenReturn(responseEntity);
        when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
        when(feeResponse.getFeeAmount()).thenReturn(BigDecimal.ONE);
        when(featureToggleService.isNewFeeRegisterCodeEnabled()).thenReturn(false);

        FeeResponse copiesFee = feeService.getCopiesFeeResponse(1L);

        assertEquals(BigDecimal.ONE, copiesFee.getFeeAmount());
        verify(feeServiceConfiguration, times(1)).getKeyword();
    }

    @Test
    void copiesFeeEqualsNonZeroNewFeeToggledOn() throws SocketTimeoutException {

        when(feeServiceConfiguration.getUrl()).thenReturn("http://test.test/lookupWithKeyword");
        when(feeServiceConfiguration.getKeyword()).thenReturn("FeeKey");
        when(restTemplate.getForEntity("http://test.test/lookupWithKeywordnull?service&jurisdiction1&"
                + "jurisdiction2&channel&applicant_type&event=copies&amount_or_volume=1&keyword=KeyFee",
            FeeResponse.class)).thenReturn(responseEntity);
        when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
        when(feeResponse.getFeeAmount()).thenReturn(BigDecimal.ONE);
        when(featureToggleService.isNewFeeRegisterCodeEnabled()).thenReturn(true);

        FeeResponse copiesFee = feeService.getCopiesFeeResponse(1L);

        assertEquals(BigDecimal.ONE, copiesFee.getFeeAmount());
        verify(feeServiceConfiguration, times(1)).getNewCopiesFeeKeyword();
    }

    @Test
    void getTotalFee() {
        when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
        when(feeResponse.getFeeAmount()).thenReturn(BigDecimal.ONE);

        FeesResponse feesResponse = feeService.getAllFeesData(BigDecimal.valueOf(5001), 1L, 1L);
        assertEquals(BigDecimal.ONE, feesResponse.getApplicationFeeResponse().getFeeAmount());
        assertEquals(BigDecimal.ONE, feesResponse.getUkCopiesFeeResponse().getFeeAmount());
        assertEquals(BigDecimal.ONE, feesResponse.getOverseasCopiesFeeResponse().getFeeAmount());
    }

    @Test
    void getApplicationFeeWithOldKeywordGreaterThan5000() {
        when(feeServiceConfiguration.getUrl()).thenReturn("http://test.test/lookupWithKeyword");
        when(feeServiceConfiguration.getIhtMinAmt()).thenReturn(Double.valueOf(5000));
        when(featureToggleService.isNewFeeRegisterCodeEnabled()).thenReturn(true);
        when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
        BigDecimal expectedFee = BigDecimal.valueOf(5001);
        when(feeResponse.getFeeAmount()).thenReturn(expectedFee);

        FeesResponse feesResponse = feeService.getAllFeesData(BigDecimal.valueOf(5001), 0L, 0L);
        assertEquals(expectedFee, feesResponse.getApplicationFeeResponse().getFeeAmount());

        verify(feeServiceConfiguration, times(1)).getNewIssuesFeeKeyword();
        verify(feeServiceConfiguration, times(0)).getNewIssuesFee5kKeyword();
    }

    @Test
    void getApplicationFeeWithOldKeywordNotGreaterThan5000() {
        when(feeServiceConfiguration.getUrl()).thenReturn("http://test.test/lookupWithKeyword");
        when(feeServiceConfiguration.getIhtMinAmt()).thenReturn(Double.valueOf(5000));
        when(featureToggleService.isNewFeeRegisterCodeEnabled()).thenReturn(true);
        when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
        BigDecimal mockedFeeAmtForAllFees = BigDecimal.valueOf(5000);
        when(feeResponse.getFeeAmount()).thenReturn(mockedFeeAmtForAllFees);

        FeesResponse feesResponse = feeService.getAllFeesData(BigDecimal.valueOf(5000), 1L, 1L);
        assertEquals(mockedFeeAmtForAllFees, feesResponse.getApplicationFeeResponse().getFeeAmount());

        verify(feeServiceConfiguration, times(1)).getNewIssuesFee5kKeyword();
        verify(feeServiceConfiguration, times(0)).getNewIssuesFeeKeyword();
    }

    @Test
    void testExceptionIfRestTemplateReturnsNull() {
        assertThrows(ClientDataException.class, () -> {
            when(restTemplate.getForEntity(any(), eq(FeeResponse.class))).thenReturn(null);
            feeService.getApplicationFeeResponse(BigDecimal.valueOf(5000));
        });
    }

    @Test
    void testExceptionIfRestTemplateReturnTimeout() {
        Exception exception = assertThrows(SocketException.class, () -> {
            when(restTemplate.getForEntity(any(), eq(FeeResponse.class)))
                    .thenThrow(new SocketException("Timeout occurred while calling "
                            + "Fee register service.Please try again later"));
            feeService.getAllFeesData(BigDecimal.valueOf(5001), 1L, 1L);
        });
        assertEquals("Timeout occurred while calling Fee register service.Please try again later",
                exception.getMessage());
    }

    @Test
    void testExceptionIfResponseEntityGetBodyReturnsNull() {
        assertThrows(ClientDataException.class, () -> {
            when(feeResponse.getFeeAmount()).thenReturn(BigDecimal.ONE);
            when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);

            when(responseEntity.getBody()).thenReturn(null);
            feeService.getApplicationFeeResponse(BigDecimal.valueOf(5000));
        });
    }

    @Test
    void copiesFeeExceptionIfResponseEntityGetBodyReturnsNull() {
        assertThrows(ClientDataException.class, () -> {
            when(feeServiceConfiguration.getUrl()).thenReturn("http://test.test/lookupWithKeyword");
            when(feeServiceConfiguration.getKeyword()).thenReturn("FeeKey");
            when(restTemplate.getForEntity(eq("http://test.test/lookupWithKeywordnull?service&jurisdiction1&"
                            + "jurisdiction2&channel&applicant_type&event=copies&amount_or_volume=1&keyword=KeyFee"),
                    eq(FeeResponse.class))).thenReturn(responseEntity);
            when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
            when(feeResponse.getFeeAmount()).thenReturn(BigDecimal.ONE);
            when(responseEntity.getBody()).thenReturn(null);
            feeService.getCopiesFeeResponse(1L);
        });
    }

    @Test
    void caveatFeeShouldReturnPositiveValue() {
        when(feeResponse.getFeeAmount()).thenReturn(BigDecimal.ONE);
        when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);

        FeeResponse issueFee = feeService.getCaveatFeesData();

        assertEquals(BigDecimal.ONE, issueFee.getFeeAmount());
    }

    @Test
    void caveatFeeShouldReturnZeroValue() {
        when(responseEntity.getStatusCode()).thenReturn(HttpStatus.NO_CONTENT);

        FeeResponse issueFee = feeService.getCaveatFeesData();

        assertEquals(BigDecimal.ZERO, issueFee.getFeeAmount());
    }

    @Test
    void testExceptionIfCaveatResponseEntityGetBodyReturnsNull() {
        assertThrows(ClientDataException.class, () -> {
            when(feeResponse.getFeeAmount()).thenReturn(BigDecimal.ONE);
            when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
            when(responseEntity.getBody()).thenReturn(null);

            feeService.getCaveatFeesData();
        });
    }


}
