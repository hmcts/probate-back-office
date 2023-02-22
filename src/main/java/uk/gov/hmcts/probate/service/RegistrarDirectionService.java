package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.RegistrarDirection;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static uk.gov.hmcts.probate.model.Constants.YES;

@Slf4j
@RequiredArgsConstructor
@Component
public class RegistrarDirectionService {
    public final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public void addAndOrderDirections(CaseData caseData) {
        RegistrarDirection registrarDirectionToAdd = buildWithTime(caseData.getRegistrarDirectionToAdd());
        List<CollectionMember<RegistrarDirection>>
                caseDirections = caseData.getRegistrarDirections();
        caseDirections.add(new CollectionMember<>(null, registrarDirectionToAdd));
        caseDirections.sort((m1, m2) -> {
            LocalDateTime dt1 = m1.getValue().getAddedDateTime();
            LocalDateTime dt2 = m2.getValue().getAddedDateTime();
            return dt1.compareTo(dt2);
        });
        Collections.reverse(caseDirections);

        caseData.setRegistrarDirectionToAdd(null);
        caseData.setEvidenceHandled(YES);
    }

    private RegistrarDirection buildWithTime(RegistrarDirection registrarDirectionToAdd) {
        return RegistrarDirection.builder()
                .addedDateTime(LocalDateTime.now())
                .decision(registrarDirectionToAdd.getDecision())
                .furtherInformation(registrarDirectionToAdd.getFurtherInformation())
                .build();
    }
}
