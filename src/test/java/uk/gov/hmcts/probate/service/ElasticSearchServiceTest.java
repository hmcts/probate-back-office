package uk.gov.hmcts.probate.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.probate.config.CCDDataStoreAPIConfiguration;
import uk.gov.hmcts.probate.exception.CaseMatchingException;
import uk.gov.hmcts.probate.insights.AppInsights;
import uk.gov.hmcts.probate.model.CaseType;
import uk.gov.hmcts.probate.model.ccd.raw.casematching.Case;
import uk.gov.hmcts.probate.model.ccd.raw.casematching.MatchedCases;
import uk.gov.hmcts.probate.service.evidencemanagement.header.HttpHeadersFactory;

import java.net.URI;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ElasticSearchServiceTest {

    @InjectMocks
    private ElasticSearchService elasticSearchService;

    @Mock
    private CCDDataStoreAPIConfiguration ccdDataStoreAPIConfiguration;

    @Mock
    private HttpHeadersFactory headers;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private Case caseMock;

    @Mock
    private AppInsights appInsights;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(ccdDataStoreAPIConfiguration.getHost()).thenReturn("http://localhost");
        when(ccdDataStoreAPIConfiguration.getCaseMatchingPath()).thenReturn("/path");
        when(headers.getAuthorizationHeaders()).thenReturn(new HttpHeaders());
        when(restTemplate.postForObject(any(URI.class), any(), eq(MatchedCases.class)))
                .thenReturn(new MatchedCases(Collections.singletonList(caseMock)));
    }

    @Test
    public void runQuery() {
        MatchedCases matchedCases = elasticSearchService.runQuery(CaseType.LEGACY, "{}");

        assertEquals(1, matchedCases.getCases().size());

        verify(restTemplate).postForObject(any(), any(), eq(MatchedCases.class));
    }

    @Test(expected = CaseMatchingException.class)
    public void shouldThrowExceptionRunningQuery() {
        when(restTemplate.postForObject(any(URI.class), any(), eq(MatchedCases.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        MatchedCases matchedCases = elasticSearchService.runQuery(CaseType.LEGACY, "{}");
    }
}