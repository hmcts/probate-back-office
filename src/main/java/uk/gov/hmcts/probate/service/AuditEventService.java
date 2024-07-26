package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.raw.AuditEvent;
import uk.gov.hmcts.probate.model.ccd.raw.AuditEventsResponse;
import uk.gov.hmcts.probate.service.client.CaseDataApiV2;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuditEventService {
    private final CaseDataApiV2 caseDataApi;

    public Optional<AuditEvent> getLatestAuditEventByName(String caseId, List<String> eventName,
                                                          String userToken, String authToken) {
        AuditEventsResponse auditEventsResponse = caseDataApi.getAuditEvents(userToken, authToken, false,
                caseId);

        return auditEventsResponse.getAuditEvents().stream()
            .filter(auditEvent -> !eventName.contains(auditEvent.getId()))
            .max(Comparator.comparing(AuditEvent::getCreatedDate));
    }
}
