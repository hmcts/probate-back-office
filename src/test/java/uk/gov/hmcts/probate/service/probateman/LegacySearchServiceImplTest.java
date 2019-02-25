package uk.gov.hmcts.probate.service.probateman;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.CaseMatchingService;
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
        List<CollectionMember<CaseMatch>> legacyCaseMatches = legacySearchService.findLegacyCaseMatches(caseDetailsMock);

        assertThat(legacyCaseMatches, equalTo(expectedCaseMatches));
    }

}