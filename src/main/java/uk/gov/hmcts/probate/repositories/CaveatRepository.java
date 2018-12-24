package uk.gov.hmcts.probate.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.probate.model.legacy.Caveat;
import uk.gov.hmcts.probate.model.legacy.GrantApplication;

public interface CaveatRepository extends JpaRepository<Caveat, Long> {
}
