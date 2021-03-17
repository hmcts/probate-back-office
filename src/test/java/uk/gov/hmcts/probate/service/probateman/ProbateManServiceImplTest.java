package uk.gov.hmcts.probate.service.probateman;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.probate.model.ccd.CcdCaseType;
import uk.gov.hmcts.probate.model.probateman.Caveat;
import uk.gov.hmcts.probate.model.probateman.GrantApplication;
import uk.gov.hmcts.probate.model.probateman.ProbateManModel;
import uk.gov.hmcts.probate.model.probateman.ProbateManType;
import uk.gov.hmcts.probate.repositories.CaveatRepository;
import uk.gov.hmcts.probate.repositories.GrantApplicationRepository;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.CoreCaseDataService;
import uk.gov.hmcts.probate.service.probateman.mapper.GrantApplicationMapper;
import uk.gov.hmcts.probate.service.probateman.mapper.ProbateManMapper;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;

import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.ccd.CcdCaseType.GRANT_OF_REPRESENTATION;
import static uk.gov.hmcts.probate.model.ccd.EventId.IMPORT_GOR_CASE;

@RunWith(MockitoJUnitRunner.class)
public class ProbateManServiceImplTest {

    private static final Long ID = 1L;

    @Mock
    private GrantApplicationRepository grantApplicationRepository;

    @Mock
    private GrantApplicationMapper grantApplicationMapper;

    @Mock
    private CaveatRepository caveatRepository;

    @Mock
    private SecurityUtils securityUtils;

    @Mock
    private CoreCaseDataService coreCaseDataService;

    private Map<ProbateManType, JpaRepository> repositories;

    private Map<ProbateManType, ProbateManMapper> mappers;

    private ProbateManServiceImpl probateManService;

    @Before
    public void setUp() {
        mappers = ImmutableMap.<ProbateManType, ProbateManMapper>builder()
            .put(ProbateManType.GRANT_APPLICATION, grantApplicationMapper)
            .build();

        repositories = ImmutableMap.<ProbateManType, JpaRepository>builder()
            .put(ProbateManType.GRANT_APPLICATION, grantApplicationRepository)
            .put(ProbateManType.CAVEAT, caveatRepository)
            .build();

        probateManService = new ProbateManServiceImpl(repositories, mappers, securityUtils, coreCaseDataService);
    }

    @Test
    public void shouldSaveToCcd() {
        SecurityDTO securityDTO = SecurityDTO.builder().build();
        GrantApplication grantApplication = new GrantApplication();
        GrantOfRepresentationData grantOfRepresentationData = GrantOfRepresentationData.builder().build();
        CaseDetails caseDetails = CaseDetails.builder().build();

        when(securityUtils.getSecurityDTO()).thenReturn(securityDTO);
        when(grantApplicationRepository.findById(ID)).thenReturn(Optional.of(grantApplication));
        when(grantApplicationMapper.toCcdData(grantApplication)).thenReturn(grantOfRepresentationData);

        when(coreCaseDataService.createCase(
            grantOfRepresentationData,
            GRANT_OF_REPRESENTATION,
            IMPORT_GOR_CASE,
            securityDTO
        )).thenReturn(caseDetails);

        CaseDetails actualCaseDetails = probateManService.saveToCcd(ID, ProbateManType.GRANT_APPLICATION);

        assertThat(actualCaseDetails, equalTo(caseDetails));

        verify(securityUtils, times(1)).getSecurityDTO();
        verify(grantApplicationRepository, times(1)).findById(ID);
        verify(grantApplicationMapper, times(1)).toCcdData(grantApplication);
        verify(coreCaseDataService, times(1)).createCase(grantOfRepresentationData,
                GRANT_OF_REPRESENTATION, IMPORT_GOR_CASE, securityDTO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenRepositoryNotPresentInRepositoryConfig() {
        probateManService.saveToCcd(ID, ProbateManType.WILL_LODGEMENT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenCannotFindEntityInProbateManDB() {
        when(caveatRepository.findById(ID)).thenReturn(Optional.empty());

        probateManService.saveToCcd(ID, ProbateManType.CAVEAT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenMapperNotPresentInMapperConfig() {
        Caveat caveat = new Caveat();
        when(caveatRepository.findById(ID)).thenReturn(Optional.of(caveat));

        probateManService.saveToCcd(ID, ProbateManType.CAVEAT);
    }

    @Test
    public void shouldGetProbateManModel() {
        GrantApplication grantApplication = new GrantApplication();
        when(grantApplicationRepository.findById(ID)).thenReturn(Optional.of(grantApplication));

        ProbateManModel probateManModel = probateManService.getProbateManModel(ID, ProbateManType.GRANT_APPLICATION);

        assertThat(probateManModel, equalTo(grantApplication));
        verify(grantApplicationRepository, times(1)).findById(ID);
    }

    @Test
    public void shouldRetrieveCCdCase() {
        String caseType = GRANT_OF_REPRESENTATION.name();
        Long legacyId = 1L;
        Optional<CaseDetails> caseDetails = Optional.empty();
        when(coreCaseDataService.retrieveCaseByLegacyId(caseType, legacyId, securityUtils.getSecurityDTO()))
                .thenReturn(caseDetails);
        assertThat(caseDetails, equalTo(probateManService.retrieveCCDCase(caseType, legacyId)));

    }
}
