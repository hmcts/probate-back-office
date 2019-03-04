package uk.gov.hmcts.probate.service.probateman.mapper;

import com.google.common.collect.ImmutableMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.hmcts.probate.model.probateman.ProbateManType;

import java.util.Map;

@Configuration
public class MapperConfiguration {

    @Bean
    public Map<ProbateManType, ProbateManMapper> mappers(CaveatMapper caveatMapper,
                                                         GrantApplicationMapper grantApplicationMapper,
                                                         StandingSearchMapper standingSearchMapper,
                                                         WillLodgementMapper willLodgementMapper) {
        return ImmutableMap.<ProbateManType, ProbateManMapper>builder()
            .put(ProbateManType.GRANT_APPLICATION, grantApplicationMapper)
            .put(ProbateManType.CAVEAT, caveatMapper)
            .put(ProbateManType.STANDING_SEARCH, standingSearchMapper)
            .put(ProbateManType.WILL_LODGEMENT, willLodgementMapper)
            .build();
    }
}
