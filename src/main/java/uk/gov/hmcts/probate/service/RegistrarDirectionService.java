package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.RegistrarDirection;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class RegistrarDirectionService {
    public final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public void addAndOrderDirections(CaseData caseData) {
        List<CollectionMember<RegistrarDirection>>
                caseDirections = caseData.getRegistrarDirections();
        caseDirections.add(new CollectionMember<>(null, caseData.getRegistrarDirectionToAdd()));
        if (caseDirections != null) {
            caseDirections.sort((m1, m2) -> {
                LocalDateTime dt1 = m1.getValue().getAddedDateTime();
                LocalDateTime dt2 = m2.getValue().getAddedDateTime();
                return -dt1.compareTo(dt2);
            });
        }

        caseData.setRegistrarDirectionToAdd(null);
    }
}
