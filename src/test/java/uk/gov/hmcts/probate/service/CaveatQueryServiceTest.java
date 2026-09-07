package uk.gov.hmcts.probate.service;

import com.google.common.collect.ImmutableList;
import org.hamcrest.Matchers;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONPointer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
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
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.evidencemanagement.header.HttpHeadersFactory;
import uk.gov.hmcts.reform.authorisation.generators.ServiceAuthTokenGenerator;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.service.CaveatQueryService.EXPIRABLE_STATES;
import static uk.gov.hmcts.reform.probate.model.cases.CaseState.DRAFT;

class CaveatQueryServiceTest {

    private static final LocalDateTime LAST_MODIFIED = LocalDateTime.now();
    private static final String EXPIRY_DATE = "2020-12-31";

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

    private SecurityDTO securityDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        securityDTO = SecurityDTO.builder().build();
        when(securityUtils.getSecurityDTO()).thenReturn(securityDTO);

        when(serviceAuthTokenGenerator.generate()).thenReturn("Bearer 321");
        when(securityUtils.getCaseworkerToken()).thenReturn("Bearer 123");
        when(headers.getAuthorizationHeaders()).thenReturn(new HttpHeaders());

        when(ccdDataStoreAPIConfiguration.getHost()).thenReturn("http://localhost");
        when(ccdDataStoreAPIConfiguration.getCaseMatchingPath()).thenReturn("/path");

        CaveatData caveatData = CaveatData.builder().deceasedSurname("Smith").build();
        List<ReturnedCaveatDetails> caveatList = new ImmutableList.Builder<ReturnedCaveatDetails>().add(
                new ReturnedCaveatDetails(caveatData, LAST_MODIFIED, DRAFT, 1L))
                .build();
        ReturnedCaveats returnedCaveats = new ReturnedCaveats(caveatList, 1);

        when(restTemplate.postForObject(any(), any(), any())).thenReturn(returnedCaveats);
        caveatQueryService.dataExtractPaginationSize = 100;
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
            ReturnedCaveats returnedCaveats = new ReturnedCaveats(caveatList, 1);

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

    @Test
    void shouldReturnExpiredCaveats() {
        CaveatData caveatData = CaveatData.builder().deceasedSurname("Expired").build();
        List<ReturnedCaveatDetails> caveatList = new ImmutableList.Builder<ReturnedCaveatDetails>().add(
                new ReturnedCaveatDetails(caveatData, LAST_MODIFIED, DRAFT, 1L))
                .build();
        ReturnedCaveats returnedCaveats = new ReturnedCaveats(caveatList, 1);
        when(restTemplate.postForObject(any(), any(), any())).thenReturn(returnedCaveats);
        List<ReturnedCaveatDetails> result = caveatQueryService.fetchExpiredCaveatsPage(EXPIRY_DATE, null);
        assertEquals(1, result.size());
        assertEquals("Expired", result.get(0).getData().getDeceasedSurname());
    }

    @Test
    void shouldHandleSearchAfterValuesForPagination() {
        CaveatData caveatData = CaveatData.builder().deceasedSurname("Pagination").build();
        List<ReturnedCaveatDetails> caveatList = new ImmutableList.Builder<ReturnedCaveatDetails>().add(
                new ReturnedCaveatDetails(caveatData, LAST_MODIFIED, DRAFT, 1L))
                .build();
        ReturnedCaveats returnedCaveats = new ReturnedCaveats(caveatList, 1);
        when(restTemplate.postForObject(any(), any(), any())).thenReturn(returnedCaveats);
        List<ReturnedCaveatDetails> result = caveatQueryService.fetchExpiredCaveatsPage(EXPIRY_DATE, new Long[]{1L});
        assertEquals(1, result.size());
        assertEquals("Pagination", result.get(0).getData().getDeceasedSurname());
    }

    @Test
    void shouldThrowClientDataExceptionWhenRestTemplateReturnsNull() {
        when(restTemplate.postForObject(any(), any(), any())).thenReturn(null);
        assertThrows(ClientDataException.class,
                () -> caveatQueryService.fetchExpiredCaveatsPage(EXPIRY_DATE, null));
    }

    @Test
    void shouldThrowCaseMatchingExceptionOnHttpClientError() {
        when(restTemplate.postForObject(any(), any(), any()))
                .thenThrow(new HttpClientErrorException(org.springframework.http.HttpStatus.BAD_REQUEST));
        assertThrows(CaseMatchingException.class,
                () -> caveatQueryService.fetchExpiredCaveatsPage(EXPIRY_DATE, null));
    }

    @Test
    void checkExpectedStatesForExpiryInQuery() {
        caveatQueryService.fetchExpiredCaveatsPage(EXPIRY_DATE, null);

        final ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(restTemplate).postForObject(any(), captor.capture(), eq(ReturnedCaveats.class));

        final Object capturedObject = captor.getValue();
        if (!(capturedObject instanceof HttpEntity)) {
            fail("captured object is not a HttpEntity");
        }

        final HttpEntity httpEntity = (HttpEntity) capturedObject;
        final Object bodyObject = httpEntity.getBody();
        if (!(bodyObject instanceof String)) {
            fail("body object is not a String");
        }

        final String body = (String) bodyObject;
        final JSONObject bodyJson = new JSONObject(body);

        final JsonObjectUtils jsonObjectUtils = new JsonObjectUtils();
        final JSONArray jsonArray = jsonObjectUtils.findArrayInQuery(
                bodyJson,
                new JSONPointer("/query/bool/filter/1/terms/state.keyword"));
        final List<Object> jsonList = jsonArray.toList();

        assertAll(
                () -> assertThat(jsonList, Matchers.containsInAnyOrder(EXPIRABLE_STATES)),
                () -> assertThat(jsonList, Matchers.hasSize(6)));
    }
}
