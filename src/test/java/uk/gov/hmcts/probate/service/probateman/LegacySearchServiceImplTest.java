package uk.gov.hmcts.probate.service.probateman;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.probate.model.CaseType;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.criterion.CaseMatchingCriteria;
import uk.gov.hmcts.probate.service.CaseSearchService;
import uk.gov.hmcts.probate.service.LegacySearchService;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LegacySearchServiceImplTest {

    private LegacySearchService legacySearchService;

    @Mock
    private CaseSearchService caseSearchService;

    @Mock
    private CaseDetails caseDetailsMock;

    @Mock
    private CaseData caseDataMock;

    @Before
    public void setUp() {
        legacySearchService = new LegacySearchServiceImpl(caseSearchService);

        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
    }

    @Test
    public void shouldFindLegacyCaseMatches() {
        List<CaseMatch> expectedCaseMatches = new ArrayList<>();
        CaseMatch caseMatch1 = CaseMatch.builder().build();
        expectedCaseMatches.add(caseMatch1);

        List<CollectionMember> expectedCollection = new ArrayList<>();
        CollectionMember collectionMember = new CollectionMember(null, caseMatch1);
        expectedCollection.add(collectionMember);

        when(caseSearchService.findCases(ArgumentMatchers.any(CaseType.class),
            ArgumentMatchers.any(CaseMatchingCriteria.class))).thenReturn(expectedCaseMatches);
        List<CollectionMember<CaseMatch>> legacyCaseMatches =
            legacySearchService.findLegacyCaseMatches(caseDetailsMock);

        assertThat(legacyCaseMatches, equalTo(expectedCollection));
    }

}