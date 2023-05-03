package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.caseaccess.Organisation;
import uk.gov.hmcts.probate.model.caseaccess.OrganisationPolicy;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.RemovedRepresentative;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class PrepareNocService {

    public void addNocDate(CaseData caseData) {
        caseData.setNocPreparedDate(LocalDate.now());
    }

    public void addRemovedRepresentatives(CaseData caseData) {
        List<CollectionMember<RemovedRepresentative>> representatives = caseData.getRemovedRepresentatives();
        RemovedRepresentative representative = setRemovedRepresentative(caseData);
        representatives.add(new CollectionMember<>(null, representative));
        representatives.sort((m1, m2) -> {
            LocalDateTime dt1 = m1.getValue().getAddedDateTime();
            LocalDateTime dt2 = m2.getValue().getAddedDateTime();
            return dt1.compareTo(dt2);
        });
        Collections.reverse(representatives);
    }

    private RemovedRepresentative setRemovedRepresentative(CaseData caseData) {
        OrganisationPolicy organisationPolicy = caseData.getApplicantOrganisationPolicy();
        Organisation organisation = organisationPolicy != null ? organisationPolicy.getOrganisation() : null;
        return RemovedRepresentative.builder()
                .addedDateTime(LocalDateTime.now())
                .organisationID(organisation.getOrganisationID())
                .organisationName(organisation.getOrganisationName())
                .solicitorFirstName(caseData.getSolsSOTForenames())
                .solicitorLastName(caseData.getSolsSOTSurname())
                .solicitorEmail(caseData.getSolsSolicitorEmail())
                .solsAddress(caseData.getSolsSolicitorAddress())
                .build();
    }
}
