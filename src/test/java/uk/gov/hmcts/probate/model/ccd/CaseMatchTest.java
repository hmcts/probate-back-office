package uk.gov.hmcts.probate.model.ccd;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.CaseType;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.casematching.Case;
import uk.gov.hmcts.probate.model.ccd.raw.casematching.CaseData;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class CaseMatchTest {

    @Mock
    private Case caseMock;

    @Mock
    private CaseData caseData;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(caseData.getDeceasedFullName()).thenReturn("Name");

        when(caseMock.getData()).thenReturn(caseData);
    }

    @Test
    public void buildCaseMatchWithDoD() {
        when(caseData.getDeceasedDateOfDeath()).thenReturn(LocalDate.of(2000, 1, 1));
        CaseMatch caseMatch = CaseMatch.buildCaseMatch(caseMock, CaseType.LEGACY);

        assertEquals("2000-01-01", caseMatch.getDod());
    }

    @Test
    public void buildCaseMatchWithoutDoD() {
        CaseMatch caseMatch = CaseMatch.buildCaseMatch(caseMock, CaseType.LEGACY);

        assertNull(caseMatch.getDod());
    }

    @Test
    public void buildCaseMatchWithAddress() {
        when(caseData.getDeceasedAddress()).thenReturn(SolsAddress.builder().postCode("SW1 0ZZ").build());
        CaseMatch caseMatch = CaseMatch.buildCaseMatch(caseMock, CaseType.LEGACY);

        assertEquals("SW1 0ZZ", caseMatch.getPostcode());
    }

    @Test
    public void buildCaseMatchWithoutAddress() {
        CaseMatch caseMatch = CaseMatch.buildCaseMatch(caseMock, CaseType.LEGACY);

        assertNull(caseMatch.getPostcode());
    }

    @Test
    public void legacyCaseDoesNotHaveCaseLink() {
        when(caseMock.getId()).thenReturn(1234L);

        CaseMatch caseMatch = CaseMatch.buildCaseMatch(caseMock, CaseType.LEGACY);

        assertNull(caseMatch.getCaseLink());
    }

    @Test
    public void notLegacyCaseHasCaseLink() {
        when(caseMock.getId()).thenReturn(1234L);

        CaseMatch caseMatch = CaseMatch.buildCaseMatch(caseMock, CaseType.GRANT_OF_REPRESENTATION);

        assertNotNull(caseMatch.getCaseLink());
    }

    @Test
    public void legacyCaseContainsLegacyCaseType() {
        when(caseData.getLegacyCaseType()).thenReturn("CAVEAT");

        CaseMatch caseMatch = CaseMatch.buildCaseMatch(caseMock, CaseType.LEGACY);

        assertTrue(caseMatch.getType().contains("CAVEAT"));
    }
}