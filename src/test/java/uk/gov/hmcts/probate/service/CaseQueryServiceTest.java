package uk.gov.hmcts.probate.service;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.probate.config.CCDDataStoreAPIConfiguration;
import uk.gov.hmcts.probate.exception.CaseMatchingException;
import uk.gov.hmcts.probate.exception.ClientDataException;
import uk.gov.hmcts.probate.model.CaseType;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCases;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.evidencemanagement.header.HttpHeadersFactory;
import uk.gov.hmcts.reform.authorisation.generators.ServiceAuthTokenGenerator;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class CaseQueryServiceTest {

    private static final LocalDateTime LAST_MODIFIED = LocalDateTime.now(ZoneOffset.UTC).minusYears(2);

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
    private FileSystemResourceService fileSystemResourceService;

    @Mock
    private DemoInstanceToggleService demoInstanceToggleService;

    @Captor
    private ArgumentCaptor<HttpEntity<String>> entityCaptor;

    @InjectMocks
    private CaseQueryService caseQueryService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        when(serviceAuthTokenGenerator.generate()).thenReturn("Bearer 321");
        when(securityUtils.getCaseworkerToken()).thenReturn("Bearer 123");
        when(headers.getAuthorizationHeaders()).thenReturn(new HttpHeaders());

        when(ccdDataStoreAPIConfiguration.getHost()).thenReturn("http://localhost");
        when(ccdDataStoreAPIConfiguration.getCaseMatchingPath()).thenReturn("/path");

        CaseData caseData = CaseData.builder()
            .deceasedSurname("Smith")
            .build();
        List<ReturnedCaseDetails> caseList =
            new ImmutableList.Builder<ReturnedCaseDetails>().add(new ReturnedCaseDetails(caseData,
                LAST_MODIFIED, 1L))
                .build();
        ReturnedCases returnedCases = new ReturnedCases(caseList, 1);

        when(restTemplate.postForObject(any(), any(), any())).thenReturn(returnedCases);
        when(demoInstanceToggleService.getCaseType()).thenReturn(CaseType.GRANT_OF_REPRESENTATION);
    }

    @Test
    void findCasesWithDatedDocumentReturnsCaseList() {
        List<ReturnedCaseDetails> cases =
            caseQueryService.findGrantIssuedCasesWithGrantIssuedDate("invokingService", "2021-01-01");

        assertEquals(1, cases.size());
        assertThat(cases.get(0).getId(), is(1L));
        assertEquals("Smith", cases.get(0).getData().getDeceasedSurname());
    }

    @Test
    void findAllCasesWithDatedDocumentReturnsCaseList() {
        caseQueryService.dataExtractPaginationSize = 1;
        List<ReturnedCaseDetails> cases = caseQueryService.findAllCasesWithGrantIssuedDate("invokingService",
            "2021-01-01");

        assertEquals(1, cases.size());
        assertThat(cases.get(0).getId(), is(1L));
        assertEquals("Smith", cases.get(0).getData().getDeceasedSurname());
    }

    @Test
    void findAllCasesWithDatedDocumentReturnsCaseListForMultiplePages() {
        caseQueryService.dataExtractPaginationSize = 3;
        ReturnedCases returnedCases1 = getReturnedCases(3, 0, 5);
        ReturnedCases returnedCases2 = getReturnedCases(2, 3, 5);
        when(restTemplate.postForObject(any(), any(), any())).thenReturn(returnedCases1, returnedCases2);
        List<ReturnedCaseDetails> cases = caseQueryService.findAllCasesWithGrantIssuedDate("invokingService",
            "2021-01-01");

        assertEquals(5, cases.size());
        assertThat(cases.get(0).getId(), is(0L));
        assertThat(cases.get(1).getId(), is(1L));
        assertThat(cases.get(2).getId(), is(2L));
        assertThat(cases.get(3).getId(), is(3L));
        assertThat(cases.get(4).getId(), is(4L));
        assertEquals("Smith0", cases.get(0).getData().getDeceasedSurname());
        assertEquals("Smith1", cases.get(1).getData().getDeceasedSurname());
        assertEquals("Smith2", cases.get(2).getData().getDeceasedSurname());
        assertEquals("Smith3", cases.get(3).getData().getDeceasedSurname());
        assertEquals("Smith4", cases.get(4).getData().getDeceasedSurname());
    }

    @Test
    void findAllCasesWithDatedDocumentReturnsCaseListForMultiplePagesExact() {
        caseQueryService.dataExtractPaginationSize = 3;
        ReturnedCases returnedCases1 = getReturnedCases(3, 0, 6);
        ReturnedCases returnedCases2 = getReturnedCases(3, 3, 6);
        when(restTemplate.postForObject(any(), any(), any())).thenReturn(returnedCases1, returnedCases2);
        List<ReturnedCaseDetails> cases = caseQueryService.findAllCasesWithGrantIssuedDate("invokingService",
            "2021-01-01");

        assertEquals(6, cases.size());
        assertThat(cases.get(0).getId(), is(0L));
        assertThat(cases.get(1).getId(), is(1L));
        assertThat(cases.get(2).getId(), is(2L));
        assertThat(cases.get(3).getId(), is(3L));
        assertThat(cases.get(4).getId(), is(4L));
        assertThat(cases.get(5).getId(), is(5L));
        assertEquals("Smith0", cases.get(0).getData().getDeceasedSurname());
        assertEquals("Smith1", cases.get(1).getData().getDeceasedSurname());
        assertEquals("Smith2", cases.get(2).getData().getDeceasedSurname());
        assertEquals("Smith3", cases.get(3).getData().getDeceasedSurname());
        assertEquals("Smith4", cases.get(4).getData().getDeceasedSurname());
        assertEquals("Smith5", cases.get(5).getData().getDeceasedSurname());
    }

    @Test
    void findAllCasesWithDatedDocumentReturnsCaseListForMultiplePagesPlus() {
        caseQueryService.dataExtractPaginationSize = 3;
        ReturnedCases returnedCases1 = getReturnedCases(3, 0, 7);
        ReturnedCases returnedCases2 = getReturnedCases(3, 3, 7);
        ReturnedCases returnedCases3 = getReturnedCases(1, 6, 7);
        when(restTemplate.postForObject(any(), any(), any())).thenReturn(returnedCases1, returnedCases2,
            returnedCases3);
        List<ReturnedCaseDetails> cases = caseQueryService.findAllCasesWithGrantIssuedDate("invokingService",
            "2021-01-01");

        assertEquals(7, cases.size());
        assertThat(cases.get(0).getId(), is(0L));
        assertThat(cases.get(1).getId(), is(1L));
        assertThat(cases.get(2).getId(), is(2L));
        assertThat(cases.get(3).getId(), is(3L));
        assertThat(cases.get(4).getId(), is(4L));
        assertThat(cases.get(5).getId(), is(5L));
        assertThat(cases.get(6).getId(), is(6L));
        assertEquals("Smith0", cases.get(0).getData().getDeceasedSurname());
        assertEquals("Smith1", cases.get(1).getData().getDeceasedSurname());
        assertEquals("Smith2", cases.get(2).getData().getDeceasedSurname());
        assertEquals("Smith3", cases.get(3).getData().getDeceasedSurname());
        assertEquals("Smith4", cases.get(4).getData().getDeceasedSurname());
        assertEquals("Smith5", cases.get(5).getData().getDeceasedSurname());
        assertEquals("Smith6", cases.get(6).getData().getDeceasedSurname());
    }

    private ReturnedCases getReturnedCases(int numCases, int caseIndex, int total) {
        ArrayList<ReturnedCaseDetails> allReturnedCases = new ArrayList<>();
        for (int i = 0; i < numCases; i++) {
            CaseData caseData = CaseData.builder()
                .deceasedSurname("Smith" + (caseIndex + i))
                .build();
            allReturnedCases.add(new ReturnedCaseDetails(caseData,
                LAST_MODIFIED, Long.valueOf(caseIndex + i)));
        }
        List<ReturnedCaseDetails> caseList =
            new ImmutableList.Builder<ReturnedCaseDetails>()
                .addAll(allReturnedCases)
                .build();
        return new ReturnedCases(caseList, total);
    }

    @Test
    void findCasesInitiatedBySchedulerReturnsCaseList() {
        when(headers.getAuthorizationHeaders()).thenThrow(NullPointerException.class);
        List<ReturnedCaseDetails> cases = caseQueryService.findGrantIssuedCasesWithGrantIssuedDate("invokingService",
            "2021-01-01");

        assertEquals(1, cases.size());
        assertThat(cases.get(0).getId(), is(1L));
        assertEquals("Smith", cases.get(0).getData().getDeceasedSurname());
    }

    @Test
    void findCasesWithDateRangeReturnsCaseListExela() {
        when(fileSystemResourceService.getFileFromResourceAsString(anyString())).thenReturn("qry");
        ReturnedCases returnedCases1 = getReturnedCases(1, 0, 3);
        ReturnedCases returnedCases2 = getReturnedCases(1, 1, 3);
        ReturnedCases returnedCases3 = getReturnedCases(1, 2, 3);
        when(restTemplate.postForObject(any(), any(), any())).thenReturn(returnedCases1, returnedCases2,
                returnedCases3);
        List<ReturnedCaseDetails> cases = caseQueryService
            .findCaseStateWithinDateRangeExela("2019-01-01", "2019-02-05");

        assertEquals(3, cases.size());
        assertEquals(0, cases.get(0).getId().intValue());
        assertEquals("Smith0", cases.get(0).getData().getDeceasedSurname());
    }

    @Test
    void findCasesWithDateRangeThrowsError() {
        assertThrows(ClientDataException.class, () -> {
            CaseData caseData = CaseData.builder()
                    .deceasedSurname("Smith")
                    .build();
            List<ReturnedCaseDetails> caseList =
                    new ImmutableList.Builder<ReturnedCaseDetails>()
                            .add(new ReturnedCaseDetails(caseData, LAST_MODIFIED, 1L))
                            .add(new ReturnedCaseDetails(caseData, LAST_MODIFIED, 2L))
                            .add(new ReturnedCaseDetails(caseData, LAST_MODIFIED, 3L))
                            .build();
            ReturnedCases returnedCases = new ReturnedCases(caseList, 3);
            when(restTemplate.postForObject(any(), any(), any())).thenReturn(null);

            when(fileSystemResourceService.getFileFromResourceAsString(anyString())).thenReturn("qry");
            caseQueryService.findCaseStateWithinDateRangeExela("2019-01-01", "2019-02-05");
        });
    }

    @Test
    void findCasesWithDateRangeReturnsCaseListHMRC() {
        when(fileSystemResourceService.getFileFromResourceAsString(anyString())).thenReturn("qry");
        ReturnedCases returnedCases1 = getReturnedCases(1, 0, 3);
        ReturnedCases returnedCases2 = getReturnedCases(1, 1, 3);
        ReturnedCases returnedCases3 = getReturnedCases(1, 2, 3);
        when(restTemplate.postForObject(any(), any(), any())).thenReturn(returnedCases1, returnedCases2,
                returnedCases3);
        List<ReturnedCaseDetails> cases = caseQueryService
            .findCaseStateWithinDateRangeHMRC("2019-01-01", "2019-02-05");

        assertEquals(3, cases.size());
        assertEquals(0, cases.get(0).getId().intValue());
        assertEquals("Smith0", cases.get(0).getData().getDeceasedSurname());
    }

    @Test
    void findCasesWithDateRangeReturnsCaseListSmeeAndFord() {
        when(fileSystemResourceService.getFileFromResourceAsString(anyString())).thenReturn("qry");
        ReturnedCases returnedCases1 = getReturnedCases(1, 0, 3);
        ReturnedCases returnedCases2 = getReturnedCases(1, 1, 3);
        ReturnedCases returnedCases3 = getReturnedCases(1, 2, 3);
        when(restTemplate.postForObject(any(), any(), any())).thenReturn(returnedCases1, returnedCases2,
                returnedCases3);

        List<ReturnedCaseDetails> cases = caseQueryService
            .findCaseStateWithinDateRangeSmeeAndFord("2019-01-01", "2019-02-05");

        assertEquals(3, cases.size());
        assertEquals(0, cases.get(0).getId().intValue());
        assertEquals("Smith0", cases.get(0).getData().getDeceasedSurname());
    }

    @Test
    void findCasesWithDateRangeReturnsCaseListMakeDormant() {
        when(fileSystemResourceService.getFileFromResourceAsString(anyString())).thenReturn("qry");
        ReturnedCases returnedCases1 = getReturnedCases(1, 0, 3);
        ReturnedCases returnedCases2 = getReturnedCases(1, 1, 3);
        ReturnedCases returnedCases3 = getReturnedCases(1, 2, 3);
        when(restTemplate.postForObject(any(), any(), any())).thenReturn(returnedCases1, returnedCases2,
                returnedCases3);

        List<ReturnedCaseDetails> cases = caseQueryService
                .findCaseToBeMadeDormant("2022-01-01", "2022-01-10");

        assertEquals(3, cases.size());
        assertEquals(0, cases.get(0).getId().intValue());
        assertEquals("Smith0", cases.get(0).getData().getDeceasedSurname());
    }

    @Test
    void findCasesWithDateRangeReturnsCaseReactivateDormant() {
        when(fileSystemResourceService.getFileFromResourceAsString(anyString())).thenReturn("qry");
        ReturnedCases returnedCases1 = getReturnedCases(1, 0, 3);
        ReturnedCases returnedCases2 = getReturnedCases(1, 1, 3);
        ReturnedCases returnedCases3 = getReturnedCases(1, 2, 3);
        when(restTemplate.postForObject(any(), any(), any())).thenReturn(returnedCases1, returnedCases2,
                returnedCases3);

        List<ReturnedCaseDetails> cases = caseQueryService
                .findCaseToBeReactivatedFromDormant("2022-01-01");

        assertEquals(3, cases.size());
        assertEquals(0, cases.get(0).getId().intValue());
        assertEquals("Smith0", cases.get(0).getData().getDeceasedSurname());
    }

    @Test
    void testHttpExceptionCaughtWithBadPost() {
        when(restTemplate.postForObject(any(), any(), any())).thenThrow(HttpClientErrorException.class);

        assertThrows(CaseMatchingException.class, () ->
                caseQueryService.findGrantIssuedCasesWithGrantIssuedDate("invokingService",
            "2021-01-01"));
    }

    @Test
    void findCasesForGrantDelayed() {
        List<ReturnedCaseDetails> cases = caseQueryService.findCasesForGrantDelayed("2019-02-05");

        assertEquals(1, cases.size());
        assertEquals(1, cases.get(0).getId().intValue());
        assertEquals("Smith", cases.get(0).getData().getDeceasedSurname());
    }

    @Test
    void findCasesForGrantAwaitingDocs() {
        CaseData caseData = CaseData.builder()
            .deceasedSurname("Smith")
            .build();
        List<ReturnedCaseDetails> caseList =
            new ImmutableList.Builder<ReturnedCaseDetails>().add(new ReturnedCaseDetails(caseData,
                LAST_MODIFIED, 1L))
                .build();
        ReturnedCases returnedCases = new ReturnedCases(caseList, 1);
        when(restTemplate.postForObject(any(), entityCaptor.capture(), any())).thenReturn(returnedCases);

        List<ReturnedCaseDetails> cases = caseQueryService.findCasesForGrantAwaitingDocumentation("2019-02-05");

        String expected = "{\"from\":0,\"size\":0,\"query\":{\"bool\":{\"must\":[{\"bool\":{\"should\":[{\"match\":"
                + "{\"state\":{\"query\":\"CasePrinted\",\"operator\":\"OR\",\"prefix_length\":0,\"max_expansions\":50,"
                + "\"fuzzy_transpositions\":true,\"lenient\":false,\"zero_terms_query\":\"NONE\","
                + "\"auto_generate_synonyms_phrase_query\":true,\"boost\":1.0}}}],\"adjust_pure_negative\":true,"
                + "\"minimum_should_match\":\"1\",\"boost\":1.0}},{\"match\":"
                + "{\"data.grantAwaitingDocumentationNotificationDate\":{\"query\":\"2019-02-05\",\"operator\":\"OR\","
                + "\"prefix_length\":0,\"max_expansions\":50,\"fuzzy_transpositions\":true,\"lenient\":false,"
                + "\"zero_terms_query\":\"NONE\",\"auto_generate_synonyms_phrase_query\":true,\"boost\":1.0}}},"
                + "{\"match\":{\"data.paperForm\":{\"query\":\"No\",\"operator\":\"OR\",\"prefix_length\":0,"
                + "\"max_expansions\":50,\"fuzzy_transpositions\":true,\"lenient\":false,\"zero_terms_query\":\"NONE\","
                + "\"auto_generate_synonyms_phrase_query\":true,\"boost\":1.0}}}],\"must_not\":[{\"exists\":{\"field\":"
                + "\"data.grantAwaitingDocumentatioNotificationSent\",\"boost\":1.0}},{\"exists\":{\"field\":"
                + "\"data.evidenceHandled\",\"boost\":1.0}}],\"adjust_pure_negative\":true,\"boost\":1.0}},"
                + "\"sort\":[{\"id\":{\"order\":\"asc\"}}]}";
        assertEquals(expected, entityCaptor.getValue().getBody());
        assertEquals(1, cases.size());
        assertEquals(1, cases.get(0).getId().intValue());
        assertEquals("Smith", cases.get(0).getData().getDeceasedSurname());
    }

    @Test
    void testExceptionWithNullFromRestTemplatePost() {
        assertThrows(ClientDataException.class, () -> {
            when(restTemplate.postForObject(any(), any(), any())).thenReturn(null);
            caseQueryService.findGrantIssuedCasesWithGrantIssuedDate("invokingService", "2021-01-01");
        });
    }
}
