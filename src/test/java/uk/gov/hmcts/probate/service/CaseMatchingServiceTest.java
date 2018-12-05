package uk.gov.hmcts.probate.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.probate.config.CCDGatewayConfiguration;
import uk.gov.hmcts.probate.insights.AppInsights;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.casematching.Case;
import uk.gov.hmcts.probate.model.ccd.raw.casematching.MatchedCases;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.evidencemanagement.header.HttpHeadersFactory;

import java.net.URI;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.CaseType.GRANT_OF_REPRESENTATION;

public class CaseMatchingServiceTest {

    @InjectMocks
    private CaseMatchingService caseMatchingService;

    @Mock
    private CaseDetails caseDetails;

    @Mock
    private FileSystemResourceService fileSystemResourceService;

    @Mock
    private CCDGatewayConfiguration ccdGatewayConfiguration;

    @Mock
    private HttpHeadersFactory headers;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private AppInsights appInsights;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        CaseData caseData = CaseData.builder()
                .deceasedForenames("names")
                .deceasedSurname("surname")
                .deceasedDateOfDeath(LocalDate.of(2000, 1, 1))
                .deceasedAddress(SolsAddress.builder().postCode("SW12 0FA").build())
                .build();

        when(caseDetails.getData()).thenReturn(caseData);

        when(ccdGatewayConfiguration.getHost()).thenReturn("http://localhost");
        when(ccdGatewayConfiguration.getCaseMatchingPath()).thenReturn("/path");

        when(headers.getAuthorizationHeaders())
                .thenReturn(new HttpHeaders());

        when(restTemplate.postForObject(Mockito.any(URI.class), Mockito.any(), eq(MatchedCases.class)))
                .thenReturn(new MatchedCases(Collections.singletonList(new Case(caseData, 1L))));

        when(fileSystemResourceService.getFileFromResourceAsString(anyString()))
                .thenReturn("template");

        doNothing().when(appInsights).trackEvent(any(), anyString());
    }

    @Test
    public void findMatches() {
        List<CaseMatch> caseMatches = caseMatchingService.findMatches(GRANT_OF_REPRESENTATION, caseDetails);

        assertEquals(1, caseMatches.size());
        assertEquals("1", caseMatches.get(0).getCaseLink().getCaseReference());
        assertEquals("names surname", caseMatches.get(0).getFullName());
        assertEquals("2000-01-01", caseMatches.get(0).getDod());
        assertEquals("SW12 0FA", caseMatches.get(0).getPostcode());
        assertNull(caseMatches.get(0).getValid());
        assertNull(caseMatches.get(0).getComment());
    }
}