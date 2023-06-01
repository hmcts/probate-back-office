package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.Organisation;
import uk.gov.hmcts.reform.probate.model.cases.ChangeOfRepresentative;
import uk.gov.hmcts.reform.probate.model.cases.RemovedRepresentative;
import uk.gov.hmcts.reform.probate.model.cases.AddedRepresentative;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class SaveNocService {

    public List<CollectionMember<ChangeOfRepresentative>> getRepresentatives(
            List<uk.gov.hmcts.probate.model.ccd.raw.CollectionMember
                    <uk.gov.hmcts.probate.model.ccd.raw.ChangeOfRepresentative>> reps) {
        List<CollectionMember<ChangeOfRepresentative>> representatives =
                new ArrayList<>();
        for (uk.gov.hmcts.probate.model.ccd.raw.CollectionMember
                <uk.gov.hmcts.probate.model.ccd.raw.ChangeOfRepresentative> repCollectionMember :
                reps) {
            representatives.add(new CollectionMember<>(
                    repCollectionMember.getId(),
                    getRepresentative(repCollectionMember.getValue())));
        }
        return representatives;
    }

    private ChangeOfRepresentative getRepresentative(uk.gov.hmcts.probate.model.ccd.raw
                                                              .ChangeOfRepresentative representative) {
        AddedRepresentative added = AddedRepresentative.builder()
                .organisationID(representative.getAddedRepresentative().getOrganisationID())
                .updatedBy(representative.getAddedRepresentative().getUpdatedBy())
                .updatedVia(representative.getAddedRepresentative().getUpdatedVia())
                .build();

        Organisation organisation = Organisation.builder()
                .organisationID(representative.getRemovedRepresentative().getOrganisation().getOrganisationID())
                .organisationName(representative.getRemovedRepresentative().getOrganisation().getOrganisationName())
                .build();

        RemovedRepresentative removed = RemovedRepresentative.builder()
                        .organisationID(representative.getRemovedRepresentative().getOrganisationID())
                        .solicitorFirstName(representative.getRemovedRepresentative().getSolicitorFirstName())
                        .solicitorLastName(representative.getRemovedRepresentative().getSolicitorLastName())
                        .solicitorEmail(representative.getRemovedRepresentative().getSolicitorEmail())
                        .organisation(organisation)
                        .build();
        return ChangeOfRepresentative.builder()
                .addedDateTime(representative.getAddedDateTime())
                .addedRepresentative(added)
                .removedRepresentative(removed)
                .build();

    }
}
