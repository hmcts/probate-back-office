package uk.gov.hmcts.probate.service.probateman;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.probate.model.CaseType;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.criterion.CaseMatchingCriteria;
import uk.gov.hmcts.probate.service.CaseSearchService;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LegacySearchServiceImplTest {

    @InjectMocks
    private LegacySearchServiceImpl legacySearchService;

    @Mock
    private CaseSearchService caseSearchService;

    @Mock
    private CaseDetails caseDetailsMock;

    @Mock
    private CaseData caseDataMock;

    @BeforeEach
    public void setUp() {
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
    }

    @Test
    void shouldFindLegacyCaseMatches() {
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
