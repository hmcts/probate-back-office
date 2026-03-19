package uk.gov.hmcts.probate.service;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.CaseType;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.ProbateFullAliasName;
import uk.gov.hmcts.probate.model.ccd.raw.CaseLink;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.casematching.Case;
import uk.gov.hmcts.probate.model.ccd.raw.casematching.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.casematching.MatchedCases;
import uk.gov.hmcts.probate.model.criterion.CaseMatchingCriteria;
import uk.gov.hmcts.probate.query.CaseMatchingJson;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.CaseType.CAVEAT;
import static uk.gov.hmcts.probate.model.CaseType.GRANT_OF_REPRESENTATION;

class CaseMatchingServiceTest {

    private CaseMatchingService caseMatchingService;

    @Mock
    FileSystemResourceService fileSystemResourceService;
    @Mock
    ElasticSearchService elasticSearchService;
    @Mock
    CaseMatchBuilderService caseMatchBuilderService;
    @Mock
    CaseMatchingJsonService caseMatchingJsonService;

    @Mock
    private CaseMatchingCriteria caseMatchingCriteria;
    @Mock
    private Case caseMock;
    @Mock
    CaseMatchingJson caseMatchingJsonMock;
    @Mock
    FeatureToggleService featureToggleServiceMock;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        caseMatchingService = new CaseMatchingService(
                fileSystemResourceService,
                elasticSearchService,
                caseMatchBuilderService,
                caseMatchingJsonService,
                featureToggleServiceMock);

        // common mock behaviours
        when(elasticSearchService.runQuery(any(CaseType.class), anyString()))
                .thenReturn(new MatchedCases(Collections.singletonList(caseMock)));

        when(fileSystemResourceService.getFileFromResourceAsString(anyString()))
                .thenReturn("template");

        caseMatchingJsonMock = mock();
        when(caseMatchingJsonService.getBaseQuery())
                .thenReturn(caseMatchingJsonMock);

        when(caseMatchingJsonMock.withDeceasedForenames(anyString()))
                .thenReturn(caseMatchingJsonMock);
        when(caseMatchingJsonMock.withDeceasedSurname(anyString()))
                .thenReturn(caseMatchingJsonMock);
        when(caseMatchingJsonMock.withDeceasedFullname(anyString()))
                .thenReturn(caseMatchingJsonMock);
        when(caseMatchingJsonMock.withDateOfBirth(any()))
                .thenReturn(caseMatchingJsonMock);
        when(caseMatchingJsonMock.withDateOfDeath(any()))
                .thenReturn(caseMatchingJsonMock);
        when(caseMatchingJsonMock.withAliases(any()))
                .thenReturn(caseMatchingJsonMock);

        when(caseMatchingJsonMock.stealJson())
                .thenReturn(
                        Optional.of(new JSONObject("{}")),
                        Optional.empty());

        when(featureToggleServiceMock.useJsonLibForCaseMatching())
                .thenReturn(false);

        // mocked input data behaviours
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
        when(caseMatchingCriteria.getDeceasedDateOfBirthRaw()).thenReturn(LocalDate.of(1900, 1, 1));
        when(caseMatchingCriteria.getDeceasedDateOfDeath()).thenReturn("2000-01-01");

        when(caseMock.getData()).thenReturn(caseData);
        when(caseMock.getId()).thenReturn(1L);
    }

    @Test
    void findMatches() {
        CaseMatch caseMatch = CaseMatch.builder()
                .caseLink(CaseLink.builder().caseReference("1").build())
                .fullName("names surname")
                .dod("2000-01-01")
                .postcode("SW12 0FA")
                .build();

        when(caseMatchBuilderService.buildCaseMatch(caseMock)).thenReturn(caseMatch);

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
    void findMatchesCaveatWithAlias() {
        CaseData caseData = CaseData.builder()
                .deceasedForenames("names")
                .deceasedSurname("surname")
                .deceasedDateOfDeath(LocalDate.of(2000, 1, 1))
                .build();

        when(caseMatchingCriteria.getDeceasedForenames()).thenReturn("names");
        when(caseMatchingCriteria.getDeceasedSurname()).thenReturn("surname");

        when(caseMock.getData()).thenReturn(caseData);
        CaseMatch caseMatch = CaseMatch.builder()
                .caseLink(CaseLink.builder().caseReference("1").build())
                .aliases("names surname")
                .dod("2000-01-01")
                .build();

        when(caseMatchBuilderService.buildCaseMatch(caseMock)).thenReturn(caseMatch);

        List<CaseMatch> caseMatches = caseMatchingService.findMatches(CAVEAT, caseMatchingCriteria);

        assertEquals(1, caseMatches.size());
        assertEquals("1", caseMatches.get(0).getCaseLink().getCaseReference());
        assertEquals("2000-01-01", caseMatches.get(0).getDod());
        assertNull(caseMatches.get(0).getValid());
        assertNull(caseMatches.get(0).getComment());
    }

    @Test
    void findMatchesCaveatAliasesWithAlias() {
        CaseData caseData = CaseData.builder()
                .deceasedForenames("OtherFirstName")
                .deceasedSurname("OtherLastName")
                .deceasedFullAliasNameList(Collections.singletonList(
                        new CollectionMember<>(null, ProbateFullAliasName.builder()
                                .fullAliasName("name surname").build())))
                .deceasedDateOfDeath(LocalDate.of(2000, 1, 1))
                .build();

        when(caseMatchingCriteria.getDeceasedForenames()).thenReturn("OtherFirstName");
        when(caseMatchingCriteria.getDeceasedSurname()).thenReturn("OtherLastName");
        when(caseMatchingCriteria.getDeceasedFullName()).thenReturn("OtherFirstName OtherLastName");
        when(caseMatchingCriteria.getDeceasedAliases()).thenReturn(Collections.singletonList("name surname"));
        when(caseMock.getData()).thenReturn(caseData);
        CaseMatch caseMatch = CaseMatch.builder()
                .caseLink(CaseLink.builder().caseReference("1").build())
                .aliases("name surname")
                .dod("2000-01-01")
                .build();

        when(caseMatchBuilderService.buildCaseMatch(caseMock)).thenReturn(caseMatch);

        List<CaseMatch> caseMatches = caseMatchingService.findMatches(CAVEAT, caseMatchingCriteria);

        assertEquals(1, caseMatches.size());
        assertEquals("1", caseMatches.get(0).getCaseLink().getCaseReference());
        assertEquals("2000-01-01", caseMatches.get(0).getDod());
        assertNull(caseMatches.get(0).getValid());
        assertNull(caseMatches.get(0).getComment());
    }
}
