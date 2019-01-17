package uk.gov.hmcts.probate.service.probateman;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.probateman.ProbateManType;
import uk.gov.hmcts.probate.repositories.GrantApplicationRepository;
import uk.gov.hmcts.probate.service.CaseMatchingService;
import uk.gov.hmcts.probate.service.ProbateManService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LegacySearchServiceImplTest {

    @Mock
    private CaseMatchingService caseMatchingService;

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
        legacySearchService = new LegacySearchServiceImpl(caseMatchingService, probateManService, repositories);

        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
    }

    @Test
    public void shouldFindLegacyCaseMatches() {
        List<CollectionMember<CaseMatch>> expectedCaseMatches = new ArrayList<>();
        List<CollectionMember<CaseMatch>> legacyCaseMatches = legacySearchService.findLegacyCaseMatches(caseDetailsMock);

        assertThat(legacyCaseMatches, equalTo(expectedCaseMatches));
    }

}