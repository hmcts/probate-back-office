package uk.gov.hmcts.probate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.CaseType.GRANT_OF_REPRESENTATION;

@ExtendWith(MockitoExtension.class)
class CaseSearchServiceTest {

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

    @BeforeEach
    void setUp() {

        CaseData caseData = CaseData.builder()
                .deceasedForenames("names")
                .deceasedSurname("surname")
                .deceasedDateOfDeath(LocalDate.of(2000, 1, 1))
                .deceasedAddress(SolsAddress.builder().postCode("SW12 0FA").build())
                .build();

        when(elasticSearchService.runQuery(any(CaseType.class), anyString()))
                .thenReturn(new MatchedCases(Collections.singletonList(caseMock)));
    }

    @Test
    void findCases() {
        CaseMatch caseMatch = CaseMatch.builder()
                .caseLink(CaseLink.builder().caseReference("1").build())
                .fullName("names surname")
                .dod("2000-01-01")
                .postcode("SW12 0FA")
                .build();

        when(caseMatchBuilderService.buildCaseMatch(caseMock)).thenReturn(caseMatch);

        List<CaseMatch> cases = caseSearchService.findCases(GRANT_OF_REPRESENTATION, caseMatchingCriteria);

        assertEquals(1, cases.size());
        assertEquals("1", cases.getFirst().getCaseLink().getCaseReference());
        assertEquals("names surname", cases.getFirst().getFullName());
        assertEquals("2000-01-01", cases.getFirst().getDod());
        assertEquals("SW12 0FA", cases.getFirst().getPostcode());
        assertNull(cases.getFirst().getValid());
        assertNull(cases.getFirst().getComment());
    }

    @Test
    void findCasesWithMissingNameData() {
        when(caseMatchingCriteria.getDeceasedForenames()).thenReturn(null);
        when(caseMatchingCriteria.getDeceasedSurname()).thenReturn(null);
        when(caseMatchingCriteria.getDeceasedFullName()).thenReturn(null);

        CaseMatch caseMatch = CaseMatch.builder()
                .caseLink(CaseLink.builder().caseReference("1").build())
                .fullName("names surname")
                .dod("2000-01-01")
                .postcode("SW12 0FA")
                .build();

        when(caseMatchBuilderService.buildCaseMatch(caseMock)).thenReturn(caseMatch);

        List<CaseMatch> cases = caseSearchService.findCases(GRANT_OF_REPRESENTATION, caseMatchingCriteria);

        assertEquals(1, cases.size());
        assertEquals("1", cases.getFirst().getCaseLink().getCaseReference());
        assertEquals("names surname", cases.getFirst().getFullName());
        assertEquals("2000-01-01", cases.getFirst().getDod());
        assertEquals("SW12 0FA", cases.getFirst().getPostcode());
        assertNull(cases.getFirst().getValid());
        assertNull(cases.getFirst().getComment());
    }

    @Test
    void findCaseByRecordId() {
        when(caseMatchingCriteria.getRecordId()).thenReturn("1234");

        CaseMatch caseMatch = CaseMatch.builder()
                .recordId("record1")
                .caseLink(CaseLink.builder().caseReference("1").build())
                .fullName("names surname")
                .dod("2000-01-01")
                .postcode("SW12 0FA")
                .build();

        when(caseMatchBuilderService.buildCaseMatch(caseMock)).thenReturn(caseMatch);

        List<CaseMatch> cases = caseSearchService.findCases(GRANT_OF_REPRESENTATION, caseMatchingCriteria);

        assertEquals(1, cases.size());
        assertEquals("1", cases.getFirst().getCaseLink().getCaseReference());
        assertEquals("names surname", cases.getFirst().getFullName());
        assertEquals("2000-01-01", cases.getFirst().getDod());
        assertEquals("SW12 0FA", cases.getFirst().getPostcode());
        assertEquals("record1", cases.getFirst().getRecordId());
        assertNull(cases.getFirst().getValid());
        assertNull(cases.getFirst().getComment());
    }

    @Test
    void findCasesBuildsFuzzyAndStrictNameQueries() {
        when(caseMatchingCriteria.getDeceasedForenames()).thenReturn("John");
        when(caseMatchingCriteria.getDeceasedSurname()).thenReturn("Smith");
        when(caseMatchingCriteria.getDeceasedFullName()).thenReturn("John Smith");

        CaseMatch caseMatch = CaseMatch.builder()
                .caseLink(CaseLink.builder().caseReference("1").build())
                .build();

        when(caseMatchBuilderService.buildCaseMatch(caseMock)).thenReturn(caseMatch);

        caseSearchService.findCases(GRANT_OF_REPRESENTATION, caseMatchingCriteria);

        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);

        verify(elasticSearchService).runQuery(
                eq(GRANT_OF_REPRESENTATION),
                queryCaptor.capture()
        );

        String query = queryCaptor.getValue();

        assertAll(
            () -> assertThat(query, containsString("\"multi_match\"")),
            () -> assertThat(query, containsString("\"query\":\"John\"")),
            () -> assertThat(query, containsString("\"data.deceasedForenames")),
            () -> assertThat(query, containsString("\"query\":\"Smith\"")),
            () -> assertThat(query, containsString("\"data.deceasedSurname")),
            () -> assertThat(query, containsString("\"query\":\"John Smith\"")),
            () -> assertThat(query, containsString("\"data.solsDeceasedAliasNamesList.*")),
            () -> assertThat(query, containsString("\"operator\":\"AND\"")),
            () -> assertThat(query, containsString("\"fuzziness\":\"2\"")),
            () -> assertThat(query, containsString("\"fuzziness\":\"0\"")),
            () -> assertThat(query, containsString("\"boost\":2.0")),
            () -> assertThat(query, containsString("\"minimum_should_match\":\"1\"")),
            () -> assertThat(query, containsString("\"must_not\"")),
            () -> assertThat(query, containsString("\"data.imported_to_ccd\"")),
            () -> assertThat(query, containsString("\"Y\""))
        );
    }
}
