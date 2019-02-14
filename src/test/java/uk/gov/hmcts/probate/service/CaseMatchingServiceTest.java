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
import uk.gov.hmcts.probate.exception.CaseMatchingException;
import uk.gov.hmcts.probate.insights.AppInsights;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.raw.CaseLink;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.casematching.Case;
import uk.gov.hmcts.probate.model.ccd.raw.casematching.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.casematching.MatchedCases;
import uk.gov.hmcts.probate.model.criterion.CaseMatchingCriteria;
import uk.gov.hmcts.probate.service.evidencemanagement.header.HttpHeadersFactory;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.CaseType.GRANT_OF_REPRESENTATION;

public class CaseMatchingServiceTest {

    @InjectMocks
    private CaseMatchingService caseMatchingService;

    @Mock
    private CaseMatchBuilderService caseMatchBuilderService;

    @Mock
    private CaseMatchingCriteria caseMatchingCriteria;

    @Mock
    private FileSystemResourceService fileSystemResourceService;

    @Mock
    private CCDDataStoreAPIConfiguration ccdDataStoreAPIConfiguration;

    @Mock
    private HttpHeadersFactory headers;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private AppInsights appInsights;

    @Mock
    private Case caseMock;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        CaseData caseData = CaseData.builder()
                .deceasedForenames("names")
                .deceasedSurname("surname")
                .deceasedDateOfDeath(LocalDate.of(2000, 1, 1))
                .deceasedAddress(SolsAddress.builder().postCode("SW12 0FA").build())
                .build();

        when(caseMatchingCriteria.getDeceasedForenames()).thenReturn("names");
        when(caseMatchingCriteria.getDeceasedSurname()).thenReturn("surname");
        when(caseMatchingCriteria.getDeceasedFullName()).thenReturn("name surname");
        when(caseMatchingCriteria.getDeceasedAliases()).thenReturn(Collections.singletonList("name surname"));
        when(caseMatchingCriteria.getDeceasedDateOfBirth()).thenReturn("1900-01-01");
        when(caseMatchingCriteria.getDeceasedDateOfDeath()).thenReturn("2000-01-01");

        when(ccdDataStoreAPIConfiguration.getHost()).thenReturn("http://localhost");
        when(ccdDataStoreAPIConfiguration.getCaseMatchingPath()).thenReturn("/path");

        when(headers.getAuthorizationHeaders())
                .thenReturn(new HttpHeaders());

        when(caseMock.getData()).thenReturn(caseData);
        when(caseMock.getId()).thenReturn(1L);
        when(restTemplate.postForObject(any(URI.class), any(), eq(MatchedCases.class)))
                .thenReturn(new MatchedCases(Collections.singletonList(caseMock)));

        when(fileSystemResourceService.getFileFromResourceAsString(anyString()))
                .thenReturn("template");

        doNothing().when(appInsights).trackEvent(any(), anyString());
    }

    @Test
    public void findMatches() {
        CaseMatch caseMatch = CaseMatch.builder()
                .caseLink(CaseLink.builder().caseReference("1").build())
                .fullName("names surname")
                .dod("2000-01-01")
                .postcode("SW12 0FA")
                .build();

        when(caseMatchBuilderService.buildCaseMatch(caseMock, GRANT_OF_REPRESENTATION)).thenReturn(caseMatch);

        List<CaseMatch> caseMatches = caseMatchingService.findMatches(GRANT_OF_REPRESENTATION, caseMatchingCriteria);

        assertEquals(1, caseMatches.size());
        assertEquals("1", caseMatches.get(0).getCaseLink().getCaseReference());
        assertEquals("names surname", caseMatches.get(0).getFullName());
        assertEquals("2000-01-01", caseMatches.get(0).getDod());
        assertEquals("SW12 0FA", caseMatches.get(0).getPostcode());
        assertNull(caseMatches.get(0).getValid());
        assertNull(caseMatches.get(0).getComment());
    }

    @Test
    public void findCases() {
        CaseMatch caseMatch = CaseMatch.builder()
                .caseLink(CaseLink.builder().caseReference("1").build())
                .fullName("names surname")
                .dod("2000-01-01")
                .postcode("SW12 0FA")
                .build();

        when(caseMatchBuilderService.buildCaseMatch(caseMock, GRANT_OF_REPRESENTATION)).thenReturn(caseMatch);

        List<CaseMatch> cases = caseMatchingService.findCases(GRANT_OF_REPRESENTATION, caseMatchingCriteria);

        assertEquals(1, cases.size());
        assertEquals("1", cases.get(0).getCaseLink().getCaseReference());
        assertEquals("names surname", cases.get(0).getFullName());
        assertEquals("2000-01-01", cases.get(0).getDod());
        assertEquals("SW12 0FA", cases.get(0).getPostcode());
        assertNull(cases.get(0).getValid());
        assertNull(cases.get(0).getComment());
    }

    @Test
    public void findCasesWithDatedDocument() throws IOException {
        CaseData caseData = CaseData.builder()
                .deceasedSurname("Smith")
                .build();
        List<Case> caseList = new ImmutableList.Builder<Case>().add(new Case(caseData, 1L)).build();
        MatchedCases matchedCases = new MatchedCases(caseList);

        when(restTemplate.postForObject(any(), any(), any())).thenReturn(matchedCases);

        List<Case> cases = caseMatchingService.findCasesWithDatedDocument(GRANT_OF_REPRESENTATION, "Test",
                "testDate");

        assertEquals(1, cases.size());
        assertThat(cases.get(0).getId(), is(1L));
        assertEquals("Smith", cases.get(0).getData().getDeceasedSurname());
    }

    @Test
    public void testHttpExceptionCaughtWithBadPost() {
        when(restTemplate.postForObject(any(), any(), any())).thenThrow(HttpClientErrorException.class);

        Assertions.assertThatThrownBy(() -> caseMatchingService.findCasesWithDatedDocument(GRANT_OF_REPRESENTATION,
                "test", "testDate"))
                .isInstanceOf(CaseMatchingException.class);
    }
}