package uk.gov.hmcts.probate.service;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import uk.gov.hmcts.probate.model.CaseType;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.ReturnedCaveatDetails;
import uk.gov.hmcts.probate.model.ccd.caveat.request.ReturnedCaveats;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.evidencemanagement.header.HttpHeadersFactory;
import uk.gov.hmcts.reform.authorisation.generators.ServiceAuthTokenGenerator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CaveatQueryServiceTest {

    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private HttpHeadersFactory headers;

    @Mock
    private CCDDataStoreAPIConfiguration ccdDataStoreAPIConfiguration;

    @Mock
    private SecurityUtils securityUtils;

    @Mock
    private ServiceAuthTokenGenerator serviceAuthTokenGenerator;

    @Mock
    private BusinessValidationMessageRetriever businessValidationMessageRetrieverMock;

    @InjectMocks
    private CaveatQueryService caveatQueryService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        when(serviceAuthTokenGenerator.generate()).thenReturn("Bearer 321");
        when(securityUtils.getCaseworkerToken()).thenReturn("Bearer 123");
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
    }

    @Test
    void findCaveatWithCaveatIDMatch() {
        CaveatData caveatData = caveatQueryService.findCaveatById(CaseType.CAVEAT,
                "1234567812345678");
        assertEquals("Smith", caveatData.getDeceasedSurname());
    }

    @Test
    void shouldNotFindCaveatWithCaveatIDMatch() {
        assertThrows(BusinessValidationException.class, () -> {
            List<ReturnedCaveatDetails> caveatList = new ImmutableList.Builder<ReturnedCaveatDetails>()
                    .build();
            ReturnedCaveats returnedCaveats = new ReturnedCaveats(caveatList);

            when(restTemplate.postForObject(any(), any(), any())).thenReturn(returnedCaveats);

            caveatQueryService.findCaveatById(CaseType.CAVEAT,
                    "1234567812345678");
            verify(businessValidationMessageRetrieverMock).getMessage(any(), any(), any());
        });
    }

    @Test
    void testHttpExceptionCaughtWithBadPost() {
        when(restTemplate.postForObject(any(), any(), any())).thenThrow(HttpClientErrorException.class);

        assertThrows(CaseMatchingException.class, () ->
                caveatQueryService.findCaveatById(CaseType.CAVEAT, "1234567812345678"));
    }

    @Test
    void testExceptionWithNullFromRestTemplatePost() {
        assertThrows(ClientDataException.class, () -> {
            when(restTemplate.postForObject(any(), any(), any())).thenReturn(null);
            caveatQueryService.findCaveatById(CaseType.CAVEAT, "1234567812345678");
        });
    }
}
