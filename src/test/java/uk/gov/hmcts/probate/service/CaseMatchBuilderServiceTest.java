package uk.gov.hmcts.probate.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.CaseType;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.casematching.Case;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.probateman.LegacyCaseType;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class CaseMatchBuilderServiceTest {

    @InjectMocks
    private CaseMatchBuilderService caseMatchBuilderService;

    @Mock
    private Case caseMock;

    @Mock
    private CaseData caseDataMock;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(caseDataMock.getDeceasedFullName()).thenReturn("Name");

        when(caseMock.getData()).thenReturn(caseDataMock);
        when(caseDataMock.getLegacyCaseType()).thenReturn(LegacyCaseType.CAVEAT.name());
    }


    @Test
    public void buildCaseMatchWithDoD() {
        when(caseDataMock.getDeceasedDateOfDeath()).thenReturn(LocalDate.of(2000, 1, 1));
        CaseMatch caseMatch = caseMatchBuilderService.buildCaseMatch(caseMock, CaseType.LEGACY);

        assertEquals("2000-01-01", caseMatch.getDod());
    }

    @Test
    public void buildCaseMatchWithoutDoD() {
        CaseMatch caseMatch = caseMatchBuilderService.buildCaseMatch(caseMock, CaseType.LEGACY);

        assertNull(caseMatch.getDod());
    }

    @Test
    public void buildCaseMatchWithAddress() {
        when(caseDataMock.getDeceasedAddress()).thenReturn(SolsAddress.builder().postCode("SW1 0ZZ").build());
        CaseMatch caseMatch = caseMatchBuilderService.buildCaseMatch(caseMock, CaseType.LEGACY);

        assertEquals("SW1 0ZZ", caseMatch.getPostcode());
    }

    @Test
    public void buildCaseMatchWithoutAddress() {
        CaseMatch caseMatch = caseMatchBuilderService.buildCaseMatch(caseMock, CaseType.LEGACY);

        assertNull(caseMatch.getPostcode());
    }

    @Test
    public void shouldNotHaveCaseLinkForLegacyCase() {
        when(caseMock.getId()).thenReturn(1234L);

        CaseMatch caseMatch = caseMatchBuilderService.buildCaseMatch(caseMock, CaseType.LEGACY);

        assertNull(caseMatch.getCaseLink());
    }

    @Test
    public void shouldHaveCaseLinkForCCDCase() {
        when(caseMock.getId()).thenReturn(1234L);

        CaseMatch caseMatch = caseMatchBuilderService.buildCaseMatch(caseMock, CaseType.GRANT_OF_REPRESENTATION);

        assertNotNull(caseMatch.getCaseLink());
    }

    @Test
    public void shouldContainLegacyCaseType() {
        when(caseDataMock.getLegacyCaseType()).thenReturn("CAVEAT");

        CaseMatch caseMatch = caseMatchBuilderService.buildCaseMatch(caseMock, CaseType.LEGACY);

        assertTrue(caseMatch.getType().contains("CAVEAT"));
    }

}