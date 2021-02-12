package uk.gov.hmcts.probate.service.probateman;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.raw.CaseLink;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.probateman.GrantApplication;
import uk.gov.hmcts.probate.model.probateman.LegacyCaseType;
import uk.gov.hmcts.probate.model.probateman.ProbateManType;
import uk.gov.hmcts.probate.repositories.GrantApplicationRepository;
import uk.gov.hmcts.probate.service.LegacyImportService;
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
public class LegacyImportServiceImplTest {
    private static final String DO_IMPORT_YES = "Yes";
    private static final Long LEGACY_ID = 20001L;
    private static final String LEGACY_CASE_URL =
        "http://localhost:3453/print/probateManTypes/STANDING_SEARCH/cases/20001";
    private static final Long CCD_CASE_ID = 1111222233334444L;

    private LegacyImportService legacyImportService;

    @Mock
    private ProbateManService probateManService;

    @Mock
    private Map<ProbateManType, JpaRepository> repositories;

    @Mock
    private CaseData caseDataMock;

    @Before
    public void setUp() {
        legacyImportService = new LegacyImportServiceImpl(probateManService, repositories);
    }

    @Test
    public void shouldImportLegacyCasesWhenLegacyCaseFound() {
        CaseMatch caseMatch = Mockito.mock(CaseMatch.class);
        when(caseMatch.getType()).thenReturn(LegacyCaseType.GRANT_OF_REPRESENTATION.getName());
        when(caseMatch.getDoImport()).thenReturn(DO_IMPORT_YES);
        when(caseMatch.getLegacyCaseViewUrl()).thenReturn(LEGACY_CASE_URL);
        CollectionMember<CaseMatch> memberRow = new CollectionMember<>(caseMatch);
        List<CollectionMember<CaseMatch>> legacyRows = new ArrayList<>();
        legacyRows.add(memberRow);

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

        List<CaseMatch> legacyCaseMatches = legacyImportService.importLegacyRows(legacyRows);


        assertThat(legacyCaseMatches.size(), equalTo(1));
        verify(grantApplicationMock).setDnmInd("Y");
        verify(grantApplicationMock).setCcdCaseNo("1111222233334444");
        verify(grantApplicationRepositoryMock).save(grantApplicationMock);
        verify(caseMatch).setCaseLink(ArgumentMatchers.any(CaseLink.class));
    }

    @Test
    public void shouldImportLegacyCasesWhenLegacyCaseFoundButNotFoundWhenUpdating() {
        CaseMatch caseMatch = Mockito.mock(CaseMatch.class);
        when(caseMatch.getType()).thenReturn(LegacyCaseType.GRANT_OF_REPRESENTATION.getName());
        when(caseMatch.getDoImport()).thenReturn(DO_IMPORT_YES);
        when(caseMatch.getLegacyCaseViewUrl()).thenReturn(LEGACY_CASE_URL);
        CollectionMember<CaseMatch> memberRow = new CollectionMember<>(caseMatch);
        List<CollectionMember<CaseMatch>> legacyRows = new ArrayList<>();
        legacyRows.add(memberRow);

        uk.gov.hmcts.reform.ccd.client.model.CaseDetails caseDetailsSaved =
            Mockito.mock(uk.gov.hmcts.reform.ccd.client.model.CaseDetails.class);
        when(caseDetailsSaved.getId()).thenReturn(CCD_CASE_ID);
        when(probateManService.saveToCcd(LEGACY_ID, ProbateManType.GRANT_APPLICATION)).thenReturn(caseDetailsSaved);

        GrantApplicationRepository grantApplicationRepositoryMock = Mockito.mock(GrantApplicationRepository.class);
        when(repositories.get(ProbateManType.GRANT_APPLICATION)).thenReturn(grantApplicationRepositoryMock);
        GrantApplication grantApplicationMock = Mockito.mock(GrantApplication.class);
        Optional<GrantApplication> grantApplicationOptional = Optional.empty();
        when(grantApplicationRepositoryMock.findById(LEGACY_ID)).thenReturn(grantApplicationOptional);

        List<CollectionMember<CaseMatch>> expectedCaseMatches = new ArrayList<>();
        CaseMatch expectedCaseMatch = CaseMatch.builder().build();
        expectedCaseMatches.add(new CollectionMember<CaseMatch>(null, expectedCaseMatch));

        List<CaseMatch> legacyCaseMatches = legacyImportService.importLegacyRows(legacyRows);


        assertThat(legacyCaseMatches.size(), equalTo(1));
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionImportingLegacyCasesAndNoneFound() {
        CaseMatch caseMatch = Mockito.mock(CaseMatch.class);
        when(caseMatch.getType()).thenReturn(LegacyCaseType.GRANT_OF_REPRESENTATION.getName());
        when(caseMatch.getDoImport()).thenReturn(DO_IMPORT_YES);
        when(caseMatch.getLegacyCaseViewUrl()).thenReturn(LEGACY_CASE_URL);
        CollectionMember<CaseMatch> memberRow = new CollectionMember<>(caseMatch);
        List<CollectionMember<CaseMatch>> legacyRows = new ArrayList<>();
        legacyRows.add(memberRow);

        RuntimeException ste = Mockito.mock(RuntimeException.class);
        when(probateManService.saveToCcd(LEGACY_ID, ProbateManType.GRANT_APPLICATION)).thenThrow(ste);

        legacyImportService.importLegacyRows(legacyRows);
    }

    @Test
    public void shouldFindCaseWhenImportingLegacyCasesForException() {
        CaseMatch caseMatch = Mockito.mock(CaseMatch.class);
        when(caseMatch.getType()).thenReturn(LegacyCaseType.GRANT_OF_REPRESENTATION.getName());
        when(caseMatch.getDoImport()).thenReturn(DO_IMPORT_YES);
        when(caseMatch.getLegacyCaseViewUrl()).thenReturn(LEGACY_CASE_URL);
        CollectionMember<CaseMatch> memberRow = new CollectionMember<>(caseMatch);
        List<CollectionMember<CaseMatch>> legacyRows = new ArrayList<>();
        legacyRows.add(memberRow);

        uk.gov.hmcts.reform.ccd.client.model.CaseDetails caseDetailsSaved =
            Mockito.mock(uk.gov.hmcts.reform.ccd.client.model.CaseDetails.class);
        when(caseDetailsSaved.getId()).thenReturn(CCD_CASE_ID);
        RuntimeException ste = Mockito.mock(RuntimeException.class);
        when(probateManService.saveToCcd(LEGACY_ID, ProbateManType.GRANT_APPLICATION)).thenThrow(ste);

        when(probateManService.retrieveCCDCase(ProbateManType.GRANT_APPLICATION.getCcdCaseType().getName(), LEGACY_ID))
            .thenReturn(Optional.of(caseDetailsSaved));

        GrantApplicationRepository grantApplicationRepositoryMock = Mockito.mock(GrantApplicationRepository.class);
        when(repositories.get(ProbateManType.GRANT_APPLICATION)).thenReturn(grantApplicationRepositoryMock);
        GrantApplication grantApplicationMock = Mockito.mock(GrantApplication.class);
        Optional<GrantApplication> grantApplicationOptional = Optional.of(grantApplicationMock);
        when(grantApplicationRepositoryMock.findById(LEGACY_ID)).thenReturn(grantApplicationOptional);

        List<CollectionMember<CaseMatch>> expectedCaseMatches = new ArrayList<>();
        CaseMatch expectedCaseMatch = CaseMatch.builder().build();
        expectedCaseMatches.add(new CollectionMember<CaseMatch>(null, expectedCaseMatch));

        List<CaseMatch> legacyCaseMatches = legacyImportService.importLegacyRows(legacyRows);
        assertThat(legacyCaseMatches.size(), equalTo(1));
        verify(grantApplicationMock).setDnmInd("Y");
        verify(grantApplicationMock).setCcdCaseNo("1111222233334444");
        verify(grantApplicationRepositoryMock).save(grantApplicationMock);
        verify(caseMatch).setCaseLink(ArgumentMatchers.any(CaseLink.class));
    }

    @Test
    public void shouldBeValidRowsToImport() {
        CaseMatch caseMatch = Mockito.mock(CaseMatch.class);
        when(caseMatch.getDoImport()).thenReturn(DO_IMPORT_YES);
        CaseLink caseLink = CaseLink.builder().caseReference("someCaseReference").build();
        when(caseMatch.getCaseLink()).thenReturn(caseLink);
        CollectionMember<CaseMatch> memberRow = new CollectionMember<>(caseMatch);

        CaseMatch caseMatch2 = Mockito.mock(CaseMatch.class);
        CollectionMember<CaseMatch> memberRow2 = new CollectionMember<>(caseMatch2);

        List<CollectionMember<CaseMatch>> legacyRows = new ArrayList<>();
        legacyRows.add(memberRow);
        legacyRows.add(memberRow2);

        boolean actual = legacyImportService.areLegacyRowsValidToImport(legacyRows);
        assertThat(actual, equalTo(true));
    }

    @Test
    public void shouldNotBeValidRowsToImport() {
        CaseMatch caseMatch1 = Mockito.mock(CaseMatch.class);
        when(caseMatch1.getDoImport()).thenReturn(DO_IMPORT_YES);
        when(caseMatch1.getLegacyCaseViewUrl()).thenReturn(LEGACY_CASE_URL);
        CaseLink caseLink = CaseLink.builder().build();
        when(caseMatch1.getCaseLink()).thenReturn(caseLink);
        CollectionMember<CaseMatch> memberRow1 = new CollectionMember<>(caseMatch1);

        CaseMatch caseMatch2 = Mockito.mock(CaseMatch.class);
        when(caseMatch2.getDoImport()).thenReturn(DO_IMPORT_YES);
        when(caseMatch2.getLegacyCaseViewUrl()).thenReturn(LEGACY_CASE_URL);
        CollectionMember<CaseMatch> memberRow2 = new CollectionMember<>(caseMatch2);

        List<CollectionMember<CaseMatch>> legacyRows = new ArrayList<>();
        legacyRows.add(memberRow1);
        legacyRows.add(memberRow2);

        boolean actual = legacyImportService.areLegacyRowsValidToImport(legacyRows);
        assertThat(actual, equalTo(false));
    }
}