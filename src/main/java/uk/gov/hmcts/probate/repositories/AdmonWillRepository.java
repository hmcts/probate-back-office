package uk.gov.hmcts.probate.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.probate.model.legacy.AdmonWill;
import uk.gov.hmcts.probate.model.legacy.StandingSearch;

public interface AdmonWillRepository extends JpaRepository<AdmonWill, Long> {
}
