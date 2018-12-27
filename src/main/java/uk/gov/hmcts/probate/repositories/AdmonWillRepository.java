package uk.gov.hmcts.probate.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.probate.model.probateman.AdmonWill;

public interface AdmonWillRepository extends JpaRepository<AdmonWill, Long> {
}
