package uk.gov.hmcts.probate.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.probate.model.legacy.GrantApplication;

public interface GrantApplicationRepository extends JpaRepository<GrantApplication, Long> {
}
