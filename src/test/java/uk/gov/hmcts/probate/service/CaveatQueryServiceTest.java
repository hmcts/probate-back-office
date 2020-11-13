package uk.gov.hmcts.probate.service;

import com.google.common.collect.ImmutableList;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.probate.config.CCDDataStoreAPIConfiguration;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.exception.CaseMatchingException;
import uk.gov.hmcts.probate.exception.ClientDataException;
import uk.gov.hmcts.probate.insights.AppInsights;
import uk.gov.hmcts.probate.model.CaseType;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.ReturnedCaveatDetails;
import uk.gov.hmcts.probate.model.ccd.caveat.request.ReturnedCaveats;
import uk.gov.hmcts.probate.service.evidencemanagement.header.HttpHeadersFactory;
import uk.gov.hmcts.reform.authorisation.generators.ServiceAuthTokenGenerator;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CaveatQueryServiceTest {

    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private HttpHeadersFactory headers;

    @Mock
    private AppInsights appInsights;

    @Mock
    private CCDDataStoreAPIConfiguration ccdDataStoreAPIConfiguration;

    @Mock
    private IdamAuthenticateUserService idamAuthenticateUserService;

    @Mock
    private ServiceAuthTokenGenerator serviceAuthTokenGenerator;

    @Mock
    private BusinessValidationMessageRetriever businessValidationMessageRetrieverMock;

    @InjectMocks
    private CaveatQueryService caveatQueryService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(serviceAuthTokenGenerator.generate()).thenReturn("Bearer 321");
        when(idamAuthenticateUserService.getIdamOauth2Token()).thenReturn("Bearer 123");
        when(headers.getAuthorizationHeaders()).thenReturn(new HttpHeaders());

        when(ccdDataStoreAPIConfiguration.getHost()).thenReturn("http://localhost");
        when(ccdDataStoreAPIConfiguration.getCaseMatchingPath()).thenReturn("/path");

        CaveatData caveatData = CaveatData.builder()
                .deceasedSurname("Smith")
                .build();
        List<ReturnedCaveatDetails> caveatList = new ImmutableList.Builder<ReturnedCaveatDetails>().add(
                new ReturnedCaveatDetails(caveatData, LAST_MODIFIED, 1L))
                .build();
        ReturnedCaveats returnedCaveats = new ReturnedCaveats(caveatList);

        when(restTemplate.postForObject(any(), any(), any())).thenReturn(returnedCaveats);

        doNothing().when(appInsights).trackEvent(any(), anyString());
    }

    @Test
    public void findCaveatWithCaveatIDMatch() {
        CaveatData caveatData = caveatQueryService.findCaveatById(CaseType.CAVEAT,
                "1234567812345678");
        assertEquals("Smith", caveatData.getDeceasedSurname());
    }

    @Test(expected = BusinessValidationException.class)
    public void shouldNotFindCaveatWithCaveatIDMatch() {
        List<ReturnedCaveatDetails> caveatList = new ImmutableList.Builder<ReturnedCaveatDetails>()
                .build();
        ReturnedCaveats returnedCaveats = new ReturnedCaveats(caveatList);

        when(restTemplate.postForObject(any(), any(), any())).thenReturn(returnedCaveats);

        caveatQueryService.findCaveatById(CaseType.CAVEAT,
                "1234567812345678");
        verify(businessValidationMessageRetrieverMock).getMessage(any(), any(), any());
    }

    @Test
    public void testHttpExceptionCaughtWithBadPost() {
        when(restTemplate.postForObject(any(), any(), any())).thenThrow(HttpClientErrorException.class);

        Assertions.assertThatThrownBy(() -> caveatQueryService.findCaveatById(CaseType.CAVEAT, "1234567812345678"))
                .isInstanceOf(CaseMatchingException.class);
    }

    @Test(expected = ClientDataException.class)
    public void testExceptionWithNullFromRestTemplatePost() {
        when(restTemplate.postForObject(any(), any(), any())).thenReturn(null);
        caveatQueryService.findCaveatById(CaseType.CAVEAT, "1234567812345678");
    }
}
