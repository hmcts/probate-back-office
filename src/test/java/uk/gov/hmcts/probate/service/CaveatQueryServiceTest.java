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
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.ccd.CcdClientApi;
import uk.gov.hmcts.probate.service.evidencemanagement.header.HttpHeadersFactory;
import uk.gov.hmcts.reform.authorisation.generators.ServiceAuthTokenGenerator;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.probate.model.cases.CaseState.CAVEAT_NOT_MATCHED;
import static uk.gov.hmcts.reform.probate.model.cases.CaseState.DRAFT;

class CaveatQueryServiceTest {

    private static final String EXPIRY_DATE = "2020-12-31";
    private static final LocalDateTime LAST_MODIFIED = LocalDateTime.now();

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

    @Mock
    private CcdClientApi ccdClientApi;

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
    void findCaveatWithExpiryDate() {
        CaveatData reliantData = CaveatData.builder().deceasedSurname("Reliant").build();
        List<ReturnedCaveatDetails> firstPage = new ImmutableList.Builder<ReturnedCaveatDetails>().add(
                new ReturnedCaveatDetails(reliantData, LAST_MODIFIED, CAVEAT_NOT_MATCHED, 1L)).build();
        CaveatData robinData = CaveatData.builder().deceasedSurname("Robin").build();
        List<ReturnedCaveatDetails> secondPage = new ImmutableList.Builder<ReturnedCaveatDetails>().add(
                new ReturnedCaveatDetails(robinData, LAST_MODIFIED, CAVEAT_NOT_MATCHED, 1L)).build();

        ReturnedCaveats page1 = new ReturnedCaveats(firstPage, 2);
        ReturnedCaveats page2 = new ReturnedCaveats(secondPage, 2);
        ReturnedCaveats emptyPage = new ReturnedCaveats(ImmutableList.of(), 2);

        when(restTemplate.postForObject(any(), any(), any()))
                .thenReturn(page1)
                .thenReturn(page2)
                .thenReturn(emptyPage);

        caveatQueryService.findAndExpireCaveatExpiredCases("2023-10-01");
        verify(ccdClientApi).updateCaseAsCaseworker(
                any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void shouldSkipWhenNoExpiredCaveats() {
        ReturnedCaveats emptyPage = new ReturnedCaveats(ImmutableList.of(), 0);
        when(restTemplate.postForObject(any(), any(), any()))
                .thenReturn(emptyPage);
        caveatQueryService.findAndExpireCaveatExpiredCases(EXPIRY_DATE);
        verify(ccdClientApi, never()).updateCaseAsCaseworker(any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void shouldUseAwaitingResolutionEventIdWhenStateIsAwaitingResolution() {
        CaveatData caveatData = CaveatData.builder().deceasedSurname("Test").build();
        List<ReturnedCaveatDetails> page = ImmutableList.of(new ReturnedCaveatDetails(caveatData, LAST_MODIFIED,
                uk.gov.hmcts.reform.probate.model.cases.CaseState.CAVEAT_AWAITING_RESOLUTION, 1L));
        ReturnedCaveats returned = new ReturnedCaveats(page, 1);
        ReturnedCaveats empty = new ReturnedCaveats(ImmutableList.of(), 0);
        when(restTemplate.postForObject(any(), any(), any()))
                .thenReturn(returned)
                .thenReturn(empty);
        caveatQueryService.findAndExpireCaveatExpiredCases(EXPIRY_DATE);
        verify(ccdClientApi).updateCaseAsCaseworker(any(), any(), any(), any(),
                eq(uk.gov.hmcts.probate.model.ccd.EventId.CAVEAT_EXPIRED_FOR_AWAITING_RESOLUTION),
                any(), any(), any()
        );
    }

    @Test
    void shouldUseExpiredForAwaitingResponseEventIdWhenStateIsAwaitingWarningResponse() {
        CaveatData caveatData = CaveatData.builder().deceasedSurname("Test").build();
        List<ReturnedCaveatDetails> page = ImmutableList.of(new ReturnedCaveatDetails(caveatData, LAST_MODIFIED,
                uk.gov.hmcts.reform.probate.model.cases.CaseState.CAVEAT_AWAITING_WARNING_RESPONSE, 1L));
        ReturnedCaveats returned = new ReturnedCaveats(page, 1);
        ReturnedCaveats empty = new ReturnedCaveats(ImmutableList.of(), 0);
        when(restTemplate.postForObject(any(), any(), any()))
                .thenReturn(returned)
                .thenReturn(empty);
        caveatQueryService.findAndExpireCaveatExpiredCases(EXPIRY_DATE);
        verify(ccdClientApi).updateCaseAsCaseworker(any(), any(), any(), any(),
                eq(uk.gov.hmcts.probate.model.ccd.EventId.CAVEAT_EXPIRED_FOR_AWAITING_WARNING_RESPONSE),
                any(), any(), any()
        );
    }

    @Test
    void shouldUseExpiredForWarningValidationEventIdWhenStateIsWarningResponse() {
        CaveatData caveatData = CaveatData.builder().deceasedSurname("Test").build();
        List<ReturnedCaveatDetails> page = ImmutableList.of(new ReturnedCaveatDetails(caveatData, LAST_MODIFIED,
                uk.gov.hmcts.reform.probate.model.cases.CaseState.CAVEAT_WARNING_VALIDATION, 1L));
        ReturnedCaveats returned = new ReturnedCaveats(page, 1);
        ReturnedCaveats empty = new ReturnedCaveats(ImmutableList.of(), 0);
        when(restTemplate.postForObject(any(), any(), any()))
                .thenReturn(returned)
                .thenReturn(empty);
        caveatQueryService.findAndExpireCaveatExpiredCases(EXPIRY_DATE);
        verify(ccdClientApi).updateCaseAsCaseworker(any(), any(), any(), any(),
                eq(uk.gov.hmcts.probate.model.ccd.EventId.CAVEAT_EXPIRED_FOR_WARNNG_VALIDATION),
                any(), any(), any()
        );
    }

}
