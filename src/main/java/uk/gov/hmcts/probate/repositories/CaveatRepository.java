package uk.gov.hmcts.probate.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.probate.model.probateman.Caveat;

public interface CaveatRepository extends JpaRepository<Caveat, Long> {
}
