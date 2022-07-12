package uk.gov.hmcts.probate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.casematching.Case;
import uk.gov.hmcts.probate.model.ccd.raw.casematching.CaseData;
import uk.gov.hmcts.probate.model.probateman.LegacyCaseType;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class CaseMatchBuilderServiceTest {

    @InjectMocks
    private CaseMatchBuilderService caseMatchBuilderService;

    @Mock
    private Case caseMock;

    @Mock
    private CaseData caseDataMock;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(caseDataMock.getDeceasedFullName()).thenReturn("Name");

        when(caseMock.getData()).thenReturn(caseDataMock);
        when(caseDataMock.getLegacyCaseType()).thenReturn(LegacyCaseType.CAVEAT.name());
    }


    @Test
    void buildCaseMatchWithDoD() {
        when(caseDataMock.getDeceasedDateOfDeath()).thenReturn(LocalDate.of(2000, 1, 1));
        CaseMatch caseMatch = caseMatchBuilderService.buildCaseMatch(caseMock);

        assertEquals("2000-01-01", caseMatch.getDod());
    }

    @Test
    void buildCaseMatchWithoutDoD() {
        CaseMatch caseMatch = caseMatchBuilderService.buildCaseMatch(caseMock);

        assertNull(caseMatch.getDod());
    }

    @Test
    void buildCaseMatchWithAddress() {
        when(caseDataMock.getDeceasedAddress()).thenReturn(SolsAddress.builder().postCode("SW1 0ZZ").build());
        CaseMatch caseMatch = caseMatchBuilderService.buildCaseMatch(caseMock);

        assertEquals("SW1 0ZZ", caseMatch.getPostcode());
    }

    @Test
    void buildCaseMatchWithoutAddress() {
        CaseMatch caseMatch = caseMatchBuilderService.buildCaseMatch(caseMock);

        assertNull(caseMatch.getPostcode());
    }

    @Test
    void shouldNotHaveCaseLinkForLegacyCase() {
        when(caseMock.getData().getLegacyId()).thenReturn("1234");
        when(caseMock.getId()).thenReturn(1234L);

        CaseMatch caseMatch = caseMatchBuilderService.buildCaseMatch(caseMock);

        assertNull(caseMatch.getCaseLink());
    }

    @Test
    void shouldHaveCaseLinkForCCDCase() {
        when(caseMock.getId()).thenReturn(1234L);

        CaseMatch caseMatch = caseMatchBuilderService.buildCaseMatch(caseMock);

        assertNotNull(caseMatch.getCaseLink());
    }

    @Test
    void shouldContainLegacyCaseType() {
        when(caseDataMock.getLegacyId()).thenReturn("1234");
        when(caseDataMock.getLegacyCaseType()).thenReturn("CAVEAT");
        when(caseDataMock.getRecordId()).thenReturn("9876");

        CaseMatch caseMatch = caseMatchBuilderService.buildCaseMatch(caseMock);

        assertTrue(caseMatch.getType().contains("CAVEAT"));
        assertEquals("9876", caseMatch.getRecordId());
    }
}
