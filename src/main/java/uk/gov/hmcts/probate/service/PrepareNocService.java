package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.caseaccess.Organisation;
import uk.gov.hmcts.probate.model.caseaccess.OrganisationPolicy;
import uk.gov.hmcts.probate.model.ccd.raw.AddedRepresentative;
import uk.gov.hmcts.probate.model.ccd.raw.ChangeOfRepresentative;
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

    public void addRepresentatives(CaseData caseData) {
        List<CollectionMember<ChangeOfRepresentative>> representatives = caseData.getChangeOfRepresentatives();
        ChangeOfRepresentative representative = buildRepresentative(caseData);
        representatives.add(new CollectionMember<>(null, representative));
        log.info("Change of Representatives - " + representatives);
        representatives.sort((m1, m2) -> {
            LocalDateTime dt1 = m1.getValue().getAddedDateTime();
            LocalDateTime dt2 = m2.getValue().getAddedDateTime();
            return dt1.compareTo(dt2);
        });
        Collections.reverse(representatives);
    }

    private ChangeOfRepresentative buildRepresentative(CaseData caseData) {
        RemovedRepresentative removeRepresentative = caseData.getRemovedRepresentative();
        AddedRepresentative addRepresentative = setAddedRepresentative(caseData);
        log.info("Removed Representative - " + removeRepresentative);
        log.info("Added Representative - " + addRepresentative);
        return ChangeOfRepresentative.builder()
                .addedDateTime(LocalDateTime.now())
                .addedRepresentative(addRepresentative)
                .removedRepresentative(removeRepresentative)
                .build();
    }

    public RemovedRepresentative setRemovedRepresentative(CaseData caseData) {
        OrganisationPolicy organisationPolicy = caseData.getApplicantOrganisationPolicy();

        if (organisationPolicy != null) {
            Organisation organisation = organisationPolicy.getOrganisation();

            RemovedRepresentative removed = RemovedRepresentative.builder()
                    .organisationID(organisation.getOrganisationID())
                    .solicitorFirstName(caseData.getSolsSOTForenames())
                    .solicitorLastName(caseData.getSolsSOTSurname())
                    .solicitorEmail(caseData.getSolsSolicitorEmail())
                    .organisation(organisation)
                    .build();
            caseData.setRemovedRepresentative(removed);
            return removed;
        }
        return null;
    }

    private AddedRepresentative setAddedRepresentative(CaseData caseData) {
        OrganisationPolicy organisationPolicy = caseData.getApplicantOrganisationPolicy();
        Organisation organisation = organisationPolicy.getOrganisation();
        return AddedRepresentative.builder()
                .organisationID(organisation.getOrganisationID())
                .updatedBy("ABC")
                .updatedVia("NOC")
                .build();
    }
}
