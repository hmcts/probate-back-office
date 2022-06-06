package uk.gov.hmcts.probate.model.criterion;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.standingsearch.request.StandingSearchData;
import uk.gov.hmcts.probate.model.ccd.standingsearch.request.StandingSearchDetails;
import uk.gov.hmcts.probate.model.ccd.willlodgement.request.WillLodgementData;
import uk.gov.hmcts.probate.model.ccd.willlodgement.request.WillLodgementDetails;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CaseMatchingCriteriaTest {

    @Mock
    private CaseData caseData;

    @Mock
    private CaveatData caveatData;

    @Mock
    private StandingSearchData standingSearchData;

    @Mock
    private WillLodgementData willLodgementData;

    @Mock
    private CaseDetails caseDetails;

    @Mock
    private CaveatDetails caveatDetails;

    @Mock
    private StandingSearchDetails standingSearchDetails;

    @Mock
    private WillLodgementDetails willLodgementDetails;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(caseDetails.getData()).thenReturn(caseData);
        when(caveatDetails.getData()).thenReturn(caveatData);
        when(standingSearchDetails.getData()).thenReturn(standingSearchData);
        when(willLodgementDetails.getData()).thenReturn(willLodgementData);
    }

    @Test
    void shouldCreateCriteriaOfCaseDetails() {
        CaseMatchingCriteria caseMatchingCriteria = CaseMatchingCriteria.of(caseDetails);

        Assert.assertNotNull(caseMatchingCriteria);
        verify(caseDetails, times(1)).getData();
    }

    @Test
    void shouldCreateCriteriaOfCaveatDetails() {
        CaseMatchingCriteria caseMatchingCriteria = CaseMatchingCriteria.of(caveatDetails);

        Assert.assertNotNull(caseMatchingCriteria);
        verify(caveatDetails, times(1)).getData();
    }

    @Test
    void shouldCreateCriteriaOfStandingSearchDetails() {
        CaseMatchingCriteria caseMatchingCriteria = CaseMatchingCriteria.of(standingSearchDetails);

        Assert.assertNotNull(caseMatchingCriteria);
        verify(standingSearchDetails, times(1)).getData();
    }


    @Test
    void shouldCreateCriteriaOfWillLodgementDetails() {
        CaseMatchingCriteria caseMatchingCriteria = CaseMatchingCriteria.of(willLodgementDetails);

        Assert.assertNotNull(caseMatchingCriteria);
        verify(willLodgementDetails, times(1)).getData();
    }
}
