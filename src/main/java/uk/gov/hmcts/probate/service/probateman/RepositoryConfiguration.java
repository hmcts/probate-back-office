package uk.gov.hmcts.probate.service.probateman;

import com.google.common.collect.ImmutableMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.probate.model.probateman.ProbateManType;
import uk.gov.hmcts.probate.repositories.WillLodgementRepository;
import uk.gov.hmcts.probate.repositories.CaveatRepository;
import uk.gov.hmcts.probate.repositories.GrantApplicationRepository;
import uk.gov.hmcts.probate.repositories.StandingSearchRepository;

import java.util.Map;

@Configuration
public class RepositoryConfiguration {

    @Bean
    public Map<ProbateManType, JpaRepository> repositories(WillLodgementRepository willLodgementRepository,
                                                           CaveatRepository caveatRepository,
                                                           GrantApplicationRepository grantApplicationRepository,
                                                           StandingSearchRepository standingSearchRepository) {
        return ImmutableMap.<ProbateManType, JpaRepository>builder()
            .put(ProbateManType.GRANT_APPLICATION, grantApplicationRepository)
            .put(ProbateManType.CAVEAT, caveatRepository)
            .put(ProbateManType.STANDING_SEARCH, standingSearchRepository)
            .put(ProbateManType.WILL_LODGEMENT, willLodgementRepository)
            .build();
    }
}
