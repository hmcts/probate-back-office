package uk.gov.hmcts.probate.service.probateman;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.probateman.GrantApplication;
import uk.gov.hmcts.probate.model.probateman.LegacyCaseType;
import uk.gov.hmcts.probate.model.probateman.ProbateManType;
import uk.gov.hmcts.probate.repositories.GrantApplicationRepository;
import uk.gov.hmcts.probate.service.LegacyCaseMatchingService;
import uk.gov.hmcts.probate.service.ProbateManService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LegacySearchServiceImplTest {

    private static final String DO_IMPORT_YES = "Yes";
    private static final Long LEGACY_ID = 1L;
    private static final Long CCD_CASE_ID = 1111222233334444L;

    @Mock
    private LegacyCaseMatchingService legacyCaseMatchingService;

    @Mock
    private ProbateManService probateManService;

    @Mock
    private Map<ProbateManType, JpaRepository> repositories;

    private LegacySearchServiceImpl legacySearchService;

    @Mock
    private CaseDetails caseDetailsMock;

    @Mock
    private CaseData caseDataMock;

    @Before
    public void setUp() {
        legacySearchService = new LegacySearchServiceImpl(legacyCaseMatchingService, probateManService, repositories);

        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
    }

    @Test
    public void shouldFindLegacyCaseMatches() {
        List<CollectionMember<CaseMatch>> expectedCaseMatches = new ArrayList<>();
        List<CollectionMember<CaseMatch>> legacyCaseMatches = legacySearchService.findLegacyCaseMatches(caseDetailsMock);

        assertThat(legacyCaseMatches, equalTo(expectedCaseMatches));
    }

    @Test
    public void shouldImportLegacyCasesWhenLegacyCaseFound() {
        CaseMatch caseMatch = Mockito.mock(CaseMatch.class);
        when(caseMatch.getType()).thenReturn(LegacyCaseType.GRANT_OF_REPRESENTATION.getName());
        when(caseMatch.getDoImport()).thenReturn(DO_IMPORT_YES);
        when(caseMatch.getId()).thenReturn(LEGACY_ID.toString());
        CollectionMember<CaseMatch> memberRow = new CollectionMember<>(caseMatch);
        List<CollectionMember<CaseMatch>> legacyRows = new ArrayList<>();
        legacyRows.add(memberRow);
        when(caseDataMock.getLegacySearchResultRows()).thenReturn(legacyRows);

        uk.gov.hmcts.reform.ccd.client.model.CaseDetails caseDetailsSaved =
                Mockito.mock(uk.gov.hmcts.reform.ccd.client.model.CaseDetails.class);
        when(caseDetailsSaved.getId()).thenReturn(CCD_CASE_ID);
        when(probateManService.saveToCcd(LEGACY_ID, ProbateManType.GRANT_APPLICATION)).thenReturn(caseDetailsSaved);

        GrantApplicationRepository grantApplicationRepositoryMock = Mockito.mock(GrantApplicationRepository.class);
        when(repositories.get(ProbateManType.GRANT_APPLICATION)).thenReturn(grantApplicationRepositoryMock);
        GrantApplication grantApplicationMock = Mockito.mock(GrantApplication.class);
        Optional<GrantApplication> grantApplicationOptional = Optional.of(grantApplicationMock);
        when(grantApplicationRepositoryMock.findById(LEGACY_ID)).thenReturn(grantApplicationOptional);

        List<CollectionMember<CaseMatch>> expectedCaseMatches = new ArrayList<>();
        CaseMatch expectedCaseMatch = CaseMatch.builder().build();
        expectedCaseMatches.add(new CollectionMember<CaseMatch>(null, expectedCaseMatch));

        List<CollectionMember<CaseMatch>> legacyCaseMatches = legacySearchService.importLegacyRows(caseDataMock);


        assertThat(legacyCaseMatches.size(), equalTo(1));
        verify(grantApplicationMock).setDnmInd("Y");
        verify(grantApplicationMock).setCcdCaseNo("1111222233334444");
    }

    @Test
    public void shouldImportLegacyCasesWhenLegacyNotCaseFound() {
        CaseMatch caseMatch = Mockito.mock(CaseMatch.class);
        when(caseMatch.getType()).thenReturn(LegacyCaseType.GRANT_OF_REPRESENTATION.getName());
        when(caseMatch.getDoImport()).thenReturn(DO_IMPORT_YES);
        when(caseMatch.getId()).thenReturn(LEGACY_ID.toString());
        CollectionMember<CaseMatch> memberRow = new CollectionMember<>(caseMatch);
        List<CollectionMember<CaseMatch>> legacyRows = new ArrayList<>();
        legacyRows.add(memberRow);
        when(caseDataMock.getLegacySearchResultRows()).thenReturn(legacyRows);

        uk.gov.hmcts.reform.ccd.client.model.CaseDetails caseDetailsSaved =
                Mockito.mock(uk.gov.hmcts.reform.ccd.client.model.CaseDetails.class);
        when(caseDetailsSaved.getId()).thenReturn(CCD_CASE_ID);
        when(probateManService.saveToCcd(LEGACY_ID, ProbateManType.GRANT_APPLICATION)).thenReturn(caseDetailsSaved);

        GrantApplicationRepository grantApplicationRepositoryMock = Mockito.mock(GrantApplicationRepository.class);
        when(repositories.get(ProbateManType.GRANT_APPLICATION)).thenReturn(grantApplicationRepositoryMock);
        GrantApplication grantApplicationMock = Mockito.mock(GrantApplication.class);
        Optional<GrantApplication> grantApplicationOptional = Optional.of(grantApplicationMock);
        when(grantApplicationRepositoryMock.findById(LEGACY_ID)).thenReturn(Optional.empty());

        List<CollectionMember<CaseMatch>> expectedCaseMatches = new ArrayList<>();

        List<CollectionMember<CaseMatch>> legacyCaseMatches = legacySearchService.importLegacyRows(caseDataMock);


        assertThat(legacyCaseMatches.size(), equalTo(1));
    }
}