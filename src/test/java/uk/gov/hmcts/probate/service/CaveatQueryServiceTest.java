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

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static uk.gov.hmcts.reform.probate.model.cases.CaseState.DRAFT;
import static uk.gov.hmcts.reform.probate.model.cases.CaseState.PA_APP_CREATED;

class CaveatQueryServiceTest {

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

    @BeforeEach
    void setUp() {
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
                new ReturnedCaveatDetails(caveatData, LAST_MODIFIED, DRAFT, 1L))
                .build();
        ReturnedCaveats returnedCaveats = new ReturnedCaveats(caveatList, 1);

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
        List<ReturnedCaveatDetails> result = caveatQueryService.findCaveatExpiredCases("2023-10-01");
        assertEquals("Smith", result.getFirst().getData().getDeceasedSurname());
    }

    @Test
    void findCaveatCasesWithPayment() {
        CaveatData caseData = CaveatData.builder()
                .deceasedSurname("Smith")
                .build();
        List<ReturnedCaveatDetails> caseList =
                new ImmutableList.Builder<ReturnedCaveatDetails>().add(new ReturnedCaveatDetails(caseData,
                                LAST_MODIFIED, PA_APP_CREATED,1L))
                        .build();
        ReturnedCaveats returnedCases = new ReturnedCaveats(caseList, 1);
        when(restTemplate.postForObject(any(), any(), any())).thenReturn(returnedCases);

        List<ReturnedCaveatDetails> cases = caveatQueryService.findCaveatDraftCases("2023-10-01",
                "2023-10-10", CaseType.CAVEAT);

        assertEquals(1, cases.size());
        assertEquals(1, cases.getFirst().getId().intValue());
        assertEquals("Smith", cases.getFirst().getData().getDeceasedSurname());
    }

    @Test
    void findCaveatCasesWithPaymentAndMoreThanDefaultSize() {
        int caseCount = 15;
        List<ReturnedCaveatDetails> caseList = new ImmutableList.Builder<ReturnedCaveatDetails>().build();
        for (int i = 1; i <= caseCount; i++) {
            CaveatData caseData = CaveatData.builder()
                    .deceasedSurname("Smith" + i)
                    .build();
            caseList = new ImmutableList.Builder<ReturnedCaveatDetails>()
                    .addAll(caseList)
                    .add(new ReturnedCaveatDetails(caseData, LAST_MODIFIED, PA_APP_CREATED, (long) i))
                    .build();
        }
        ReturnedCaveats returnedCases = new ReturnedCaveats(caseList, caseCount);
        when(restTemplate.postForObject(any(), any(), any())).thenReturn(returnedCases);

        List<ReturnedCaveatDetails> cases = caveatQueryService.findCaveatDraftCases("2023-10-01",
                "2023-10-10", CaseType.CAVEAT);

        assertEquals(caseCount, cases.size());
        assertEquals("Smith1", cases.getFirst().getData().getDeceasedSurname());
        assertEquals("Smith15", cases.getLast().getData().getDeceasedSurname());
    }

    @Test
    void findCaveatCasesWithPaymentWhenTotalIsGreaterThanPageSize() {
        int caseCount = 15;
        CaveatData caseData = CaveatData.builder()
                .deceasedSurname("Smith")
                .build();
        List<ReturnedCaveatDetails> caseList = new ImmutableList.Builder<ReturnedCaveatDetails>()
                .add(new ReturnedCaveatDetails(caseData, LAST_MODIFIED, PA_APP_CREATED, 1L))
                .build();
        ReturnedCaveats returnedCases = new ReturnedCaveats(caseList, caseCount);
        when(restTemplate.postForObject(any(), any(), any())).thenReturn(returnedCases);

        List<ReturnedCaveatDetails> cases = caveatQueryService.findCaveatDraftCases("2023-10-01",
                "2023-10-10", CaseType.CAVEAT);

        assertEquals(caseCount, cases.size());
        assertEquals("Smith", cases.getFirst().getData().getDeceasedSurname());
        verify(restTemplate, times(15)).postForObject(any(), any(), any());
    }
}
