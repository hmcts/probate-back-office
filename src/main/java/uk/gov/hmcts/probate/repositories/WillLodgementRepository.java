package uk.gov.hmcts.probate.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.probate.model.probateman.WillLodgement;

public interface WillLodgementRepository extends JpaRepository<WillLodgement, Long> {
}
