package uk.gov.hmcts.probate.service.probateman;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.probate.model.probateman.GrantApplication;
import uk.gov.hmcts.probate.model.probateman.ProbateManType;
import uk.gov.hmcts.probate.repositories.AdmonWillRepository;
import uk.gov.hmcts.probate.repositories.CaveatRepository;
import uk.gov.hmcts.probate.repositories.GrantApplicationRepository;
import uk.gov.hmcts.probate.repositories.StandingSearchRepository;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;

import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class ProbateManServiceImplTest {

    @Mock
    private AdmonWillRepository admonWillRepository;

    @Mock
    private CaveatRepository caveatRepository;

    @Mock
    private GrantApplicationRepository grantApplicationRepository;

    @Mock
    private StandingSearchRepository standingSearchRepository;

    @InjectMocks
    private ProbateManServiceImpl probateManService;

    @Test
    public void shouldSaveToCcd() {

        GrantApplication grantApplication = new GrantApplication();

        Mockito.when(grantApplicationRepository.findById(1L)).thenReturn(Optional.of(grantApplication));

        CaseDetails caseDetails = probateManService.saveToCcd(1L, ProbateManType.GRANT_APPLICATION);

        Assert.assertTrue(true);
    }
}