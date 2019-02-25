package uk.gov.hmcts.probate.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.CaseType;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.raw.CaseLink;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.casematching.Case;
import uk.gov.hmcts.probate.model.ccd.raw.casematching.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.casematching.MatchedCases;
import uk.gov.hmcts.probate.model.criterion.CaseMatchingCriteria;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.CaseType.GRANT_OF_REPRESENTATION;

public class CaseSearchServiceTest {

    @InjectMocks
    private CaseSearchService caseSearchService;

    @Mock
    private ElasticSearchService elasticSearchService;

    @Mock
    private CaseMatchBuilderService caseMatchBuilderService;

    @Mock
    private CaseMatchingCriteria caseMatchingCriteria;

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

        when(caseMock.getData()).thenReturn(caseData);
        when(caseMock.getId()).thenReturn(1L);
        when(elasticSearchService.runQuery(any(CaseType.class), anyString()))
                .thenReturn(new MatchedCases(Collections.singletonList(caseMock)));
    }

    @Test
    public void findCases() {
        CaseMatch caseMatch = CaseMatch.builder()
                .caseLink(CaseLink.builder().caseReference("1").build())
                .fullName("names surname")
                .dod("2000-01-01")
                .postcode("SW12 0FA")
                .build();

        when(caseMatchBuilderService.buildCaseMatch(caseMock)).thenReturn(caseMatch);

        List<CaseMatch> cases = caseSearchService.findCases(GRANT_OF_REPRESENTATION, caseMatchingCriteria);

        assertEquals(1, cases.size());
        assertEquals("1", cases.get(0).getCaseLink().getCaseReference());
        assertEquals("names surname", cases.get(0).getFullName());
        assertEquals("2000-01-01", cases.get(0).getDod());
        assertEquals("SW12 0FA", cases.get(0).getPostcode());
        assertNull(cases.get(0).getValid());
        assertNull(cases.get(0).getComment());
    }

    @Test
    public void findCaseByLegacyId() {
        when(caseMatchingCriteria.getLegacyId()).thenReturn("1234");

        CaseMatch caseMatch = CaseMatch.builder()
                .caseLink(CaseLink.builder().caseReference("1").build())
                .fullName("names surname")
                .dod("2000-01-01")
                .postcode("SW12 0FA")
                .build();

        when(caseMatchBuilderService.buildCaseMatch(caseMock)).thenReturn(caseMatch);

        List<CaseMatch> cases = caseSearchService.findCases(GRANT_OF_REPRESENTATION, caseMatchingCriteria);

        assertEquals(1, cases.size());
        assertEquals("1", cases.get(0).getCaseLink().getCaseReference());
        assertEquals("names surname", cases.get(0).getFullName());
        assertEquals("2000-01-01", cases.get(0).getDod());
        assertEquals("SW12 0FA", cases.get(0).getPostcode());
        assertNull(cases.get(0).getValid());
        assertNull(cases.get(0).getComment());
    }
}