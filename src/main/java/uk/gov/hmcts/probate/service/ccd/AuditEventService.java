package uk.gov.hmcts.probate.service.ccd;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.raw.response.AuditEvent;
import uk.gov.hmcts.probate.model.ccd.raw.response.AuditEventsResponse;

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
        log.info("Getting latest audit event for caseId: {}", caseId);
        AuditEventsResponse auditEventsResponse
                = caseDataApi.getAuditEvents(userToken, authToken, false, caseId);
        log.info("auditEventsResponse AuditEvents().size(): {}", auditEventsResponse.getAuditEvents().size());
        return auditEventsResponse.getAuditEvents().stream()
                .filter(auditEvent -> eventName.contains(auditEvent.getId()))
                .max(Comparator.comparing(AuditEvent::getCreatedDate));
    }

    public Optional<AuditEvent> getLatestAuditEventByState(String caseId, List<String> stateName,
                                                          String userToken, String authToken) {
        log.info("Getting latest audit event for caseId: {}", caseId);
        AuditEventsResponse auditEventsResponse
                = caseDataApi.getAuditEvents(userToken, authToken, false, caseId);
        log.info("auditEventsResponse AuditEvents().size(): {}", auditEventsResponse.getAuditEvents().size());
        return auditEventsResponse.getAuditEvents().stream()
                .filter(auditEvent -> stateName.contains(auditEvent.getStateId()))
                .max(Comparator.comparing(AuditEvent::getCreatedDate));
    }

    public Optional<AuditEvent> getLatestAuditEventExcludingDormantState(String caseId, List<String> stateNames,
                                                                         String userToken, String authToken) {
        log.info("Getting latest audit event for caseId: {}", caseId);
        AuditEventsResponse auditEventsResponse
                = caseDataApi.getAuditEvents(userToken, authToken, false, caseId);
        log.info("auditEventsResponse AuditEvents().size(): {}", auditEventsResponse.getAuditEvents().size());
        return auditEventsResponse.getAuditEvents().stream()
                .filter(auditEvent -> stateNames.contains(auditEvent.getStateId()))
                .max(Comparator.comparing(AuditEvent::getCreatedDate));
    }
}
