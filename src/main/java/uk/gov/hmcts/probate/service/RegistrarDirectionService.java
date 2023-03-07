package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.RegistrarDirection;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static uk.gov.hmcts.probate.model.Constants.NO;

@Slf4j
@RequiredArgsConstructor
@Component
public class RegistrarDirectionService {

    public void addAndOrderDirectionsToGrant(CaseData caseData) {
        addAndOrderDirections(caseData.getRegistrarDirections(), caseData.getRegistrarDirectionToAdd());

        caseData.setRegistrarDirectionToAdd(null);
        caseData.setEvidenceHandled(NO);
    }

    public void addAndOrderDirectionsToCaveat(CaveatData caseData) {
        addAndOrderDirections(caseData.getRegistrarDirections(), caseData.getRegistrarDirectionToAdd());

        caseData.setRegistrarDirectionToAdd(null);
    }

    private void addAndOrderDirections(List<CollectionMember<RegistrarDirection>>
                                               caseDirections, RegistrarDirection registrarDirectionToAdd) {
        RegistrarDirection registrarDirectionToAddWithTime = buildWithTime(registrarDirectionToAdd);
        caseDirections.add(new CollectionMember<>(null, registrarDirectionToAddWithTime));
        caseDirections.sort((m1, m2) -> {
            LocalDateTime dt1 = m1.getValue().getAddedDateTime();
            LocalDateTime dt2 = m2.getValue().getAddedDateTime();
            return dt1.compareTo(dt2);
        });
        Collections.reverse(caseDirections);
    }

    private RegistrarDirection buildWithTime(RegistrarDirection registrarDirectionToAdd) {
        return RegistrarDirection.builder()
                .addedDateTime(LocalDateTime.now())
                .decision(registrarDirectionToAdd.getDecision())
                .furtherInformation(registrarDirectionToAdd.getFurtherInformation())
                .build();
    }
}
