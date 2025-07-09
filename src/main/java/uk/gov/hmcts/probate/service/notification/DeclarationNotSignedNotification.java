package uk.gov.hmcts.probate.service.notification;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.NotificationType;
import uk.gov.hmcts.probate.model.ccd.EventId;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.ExecutorApplying;
import uk.gov.service.notify.NotificationClientException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

import static uk.gov.hmcts.probate.model.NotificationType.DECLARATION_NOT_SIGNED;
import static uk.gov.hmcts.probate.model.StateConstants.STATE_PENDING;

@Slf4j
@Service
public class DeclarationNotSignedNotification implements NotificationStrategy {
    private static final String DECLARATION_NOT_SIGNED_EVENT_DESCRIPTION = "Send Declaration Not Signed Notification";
    private static final String DECLARATION_NOT_SIGNED_EVENT_SUMMARY = "Send Declaration Not Signed Notification";
    private static final String DECLARATION_NOT_SIGNED_FAILURE_EVENT_DESCRIPTION
            = "Failed to send Declaration Not Signed Notification";
    private static final String DECLARATION_NOT_SIGNED_FAILURE_EVENT_SUMMARY
            = "Failed to send Declaration Not Signed Notification";
    private static final String EXECUTORS_APPLYING = "executorsApplying";
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    @Setter
    private LocalDate referenceDate;

    public DeclarationNotSignedNotification(NotificationService notificationService,
                                            ObjectMapper objectMapper) {
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }

    @Override
    public String getQueryTemplate() {
        return "templates/elasticsearch/caseMatching/declaration_not_signed_query.json";
    }

    @Override
    public boolean matchesType(NotificationType type) {
        return type == DECLARATION_NOT_SIGNED;
    }

    @Override
    public Document sendEmail(CaseDetails caseDetails) throws NotificationClientException {
        notificationService.sendDeclarationNotSignedEmail(caseDetails);
        return new Document(); //Dummy return, as the actual document is not used in this context.
    }

    @Override
    public String getEventSummary() {
        return DECLARATION_NOT_SIGNED_EVENT_SUMMARY;
    }

    @Override
    public String getEventDescription() {
        return DECLARATION_NOT_SIGNED_EVENT_DESCRIPTION;
    }

    @Override
    public String getFailureEventDescription() {
        return DECLARATION_NOT_SIGNED_FAILURE_EVENT_DESCRIPTION;
    }

    @Override
    public String getFailureEventSummary() {
        return DECLARATION_NOT_SIGNED_FAILURE_EVENT_SUMMARY;
    }

    @Override
    public EventId getEventId() {
        return EventId.AUTO_NOTIFICATION_DECLARATION_NOT_SIGNED;
    }

    @Override
    public NotificationType getType() {
        return DECLARATION_NOT_SIGNED;
    }

    @Override
    public Predicate<CaseDetails> accepts() {
        return cd -> cd != null
                && cd.getData() != null
                && referenceDate != null
                && STATE_PENDING.equals(cd.getState())
                && !cd.getLastModified().isAfter(referenceDate.plusDays(1).atStartOfDay())
                && anyExecutorNotSigned(cd);
    }

    @Override
    public boolean skipSaveNotification() {
        return true;
    }

    private boolean anyExecutorNotSigned(CaseDetails cd) {
        return getExecutorsApplyingList(cd.getData()).stream()
                .map(CollectionMember::getValue)
                .filter(Objects::nonNull)
                .anyMatch(executor -> isEmailSent(executor) && !isAgreed(executor));
    }

    private List<CollectionMember<ExecutorApplying>> getExecutorsApplyingList(Map<String, Object> data) {
        Object raw = data.get(EXECUTORS_APPLYING);
        if (!(raw instanceof List<?>)) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.convertValue(raw, new TypeReference<List<CollectionMember<ExecutorApplying>>>() {});
        } catch (Exception e) {
            log.warn("Failed to parse executorsApplying. Reason: {}", e.getMessage());
            throw e;
        }
    }

    private boolean isEmailSent(ExecutorApplying executor) {
        log.info("isEmailSent {}",Boolean.TRUE.equals(executor.getApplyingExecutorEmailSent()));
        return Boolean.TRUE.equals(executor.getApplyingExecutorEmailSent());
    }

    private boolean isAgreed(ExecutorApplying executor) {
        log.info("isAgreed {}",Boolean.TRUE.equals(executor.getApplyingExecutorAgreed()));
        return Boolean.TRUE.equals(executor.getApplyingExecutorAgreed());
    }
}