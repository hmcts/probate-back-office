package uk.gov.hmcts.probate.service.probateman;

import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.probateman.AdmonWill;
import uk.gov.hmcts.probate.model.probateman.Caveat;
import uk.gov.hmcts.probate.model.probateman.GrantApplication;
import uk.gov.hmcts.probate.model.probateman.ProbateManModel;
import uk.gov.hmcts.probate.model.probateman.StandingSearch;
import uk.gov.hmcts.probate.repositories.AdmonWillRepository;
import uk.gov.hmcts.probate.repositories.CaveatRepository;
import uk.gov.hmcts.probate.repositories.GrantApplicationRepository;
import uk.gov.hmcts.probate.repositories.StandingSearchRepository;

import java.util.Map;
import java.util.Optional;

@Component
public class ProbateManServiceImpl {

    private Map<Class<? extends ProbateManModel>, JpaRepository> repositoryMap;

    @Autowired
    public ProbateManServiceImpl(
        AdmonWillRepository admonWillRepository, CaveatRepository caveatRepository,
        GrantApplicationRepository grantApplicationRepository,
        StandingSearchRepository standingSearchRepository
    ) {
        repositoryMap = ImmutableMap.<Class<? extends ProbateManModel>, JpaRepository>builder()
            .put(AdmonWill.class, admonWillRepository)
            .put(Caveat.class, caveatRepository)
            .put(GrantApplication.class, grantApplicationRepository)
            .put(StandingSearch.class, standingSearchRepository)
            .build();
    }


    public CaseDetails saveToCcd(Long id, Class<? extends ProbateManModel> clazz) {

        JpaRepository repository = Optional.ofNullable(repositoryMap.get(clazz))
            .orElseThrow(() ->
                new IllegalArgumentException("Cannot find repository for class: " + clazz.getSimpleName()));

        Optional<ProbateManModel> probateManModelOptional = repository.findById(id);

        ProbateManModel probateManModel = probateManModelOptional
            .orElseThrow(() -> new IllegalArgumentException("Cannot find " + clazz + " with id: " + id));


        return null;
    }
}
