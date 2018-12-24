package uk.gov.hmcts.probate.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.probate.model.legacy.GrantApplication;
import uk.gov.hmcts.probate.model.legacy.StandingSearch;

public interface StandingSearchRepository extends JpaRepository<StandingSearch, Long> {
}
