package uk.gov.hmcts.probate.service.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.exception.CcdUpdateNotificationException;
import uk.gov.hmcts.probate.model.NotificationType;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;
import uk.gov.hmcts.reform.ccd.client.model.CaseDataContent;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.ccd.client.model.Event;
import uk.gov.hmcts.reform.ccd.client.model.StartEventResponse;
import uk.gov.hmcts.reform.probate.model.ProbateDocument;
import uk.gov.hmcts.reform.probate.model.ProbateDocumentLink;
import uk.gov.hmcts.reform.probate.model.ProbateDocumentType;
import uk.gov.hmcts.reform.probate.model.cases.BulkPrint;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static uk.gov.hmcts.reform.probate.model.cases.JurisdictionId.PROBATE;

@Slf4j
@Service
@RequiredArgsConstructor
public class AutomatedNotificationCCDService {
    private final CoreCaseDataApi coreCaseDataApi;
    private final ObjectMapper objectMapper;
    private static final String PROBATE_NOTIFICATIONS_GENERATED = "probateNotificationsGenerated";
    private static final String BULK_PRINT_ID = "bulkPrintId";

    public StartEventResponse startEvent(final String caseId,
                                         final SecurityDTO securityDTO,
                                         final NotificationStrategy notificationStrategy) {
        StartEventResponse startEventResponse = coreCaseDataApi.startEventForCaseWorker(
                securityDTO.getAuthorisation(),
                securityDTO.getServiceAuthorisation(),
                securityDTO.getUserId(),
                PROBATE.name(),
                notificationStrategy.getCaseTypeName(),
                caseId,
                notificationStrategy.getEventId().getName());

        if (startEventResponse == null || startEventResponse.getCaseDetails() == null) {
            final String errorMsg = String.format(
                    "Failed to start event for case id: %s and event id: %s",
                    caseId, notificationStrategy.getEventId());
            throw new CcdUpdateNotificationException(errorMsg);
        }
        return startEventResponse;
    }

    public void saveNotification(final CaseDetails caseDetails,
                                 final String caseId,
                                 final SecurityDTO securityDTO,
                                 final Document sentEmail,
                                 final NotificationStrategy notificationStrategy,
                                 final StartEventResponse startEventResponse) {
        if (notificationStrategy.skipSaveNotification()) {
            log.info("Skip save notification for case id: {} type: {}", caseId, notificationStrategy.getType());
            return;
        }
        try {
            log.info("AutomatedNotificationCCDService buildCaseData for case id: {} type: {}",
                    caseId, notificationStrategy.getType());
            final GrantOfRepresentationData updatedData =
                    buildCaseData(caseDetails.getData(), sentEmail, notificationStrategy.getType());

            final Event event = Event.builder()
                    .id(startEventResponse.getEventId())
                    .summary(notificationStrategy.getEventSummary())
                    .description(notificationStrategy.getEventDescription())
                    .build();

            final CaseDataContent caseDataContent = CaseDataContent.builder()
                    .eventToken(startEventResponse.getToken())
                    .event(event)
                    .data(updatedData)
                    .build();

            log.info("AutomatedNotificationCCDService saveNotification to Case: {}", caseId);
            coreCaseDataApi.submitEventForCaseWorker(
                    securityDTO.getAuthorisation(),
                    securityDTO.getServiceAuthorisation(),
                    securityDTO.getUserId(),
                    PROBATE.name(),
                    notificationStrategy.getCaseTypeName(),
                    caseId,
                    true,
                    caseDataContent);
        } catch (Exception e) {
            final String errorMsg = String.format("Error saving notification to CCD for case: %s", caseId);
            log.error(errorMsg, e);
            throw new CcdUpdateNotificationException(errorMsg, e);
        }
    }

    public void saveFailedNotification(final String caseId,
                                       final SecurityDTO securityDTO,
                                       final NotificationStrategy notificationStrategy,
                                       final StartEventResponse startEventResponse) {
        if (notificationStrategy.skipSaveNotification()) {
            log.info("Skip save failed notification for case id: {} type: {}", caseId, notificationStrategy.getType());
            return;
        }
        try {
            log.info("AutomatedNotificationCCDService saveFailedNotification to Case: {}", caseId);
            final GrantOfRepresentationData data = GrantOfRepresentationData.builder().build();

            final Event event = Event.builder()
                    .id(startEventResponse.getEventId())
                    .summary(notificationStrategy.getFailureEventSummary())
                    .description(notificationStrategy.getFailureEventDescription())
                    .build();

            final CaseDataContent caseDataContent = CaseDataContent.builder()
                    .eventToken(startEventResponse.getToken())
                    .event(event)
                    .data(data)
                    .build();

            coreCaseDataApi.submitEventForCaseWorker(
                    securityDTO.getAuthorisation(),
                    securityDTO.getServiceAuthorisation(),
                    securityDTO.getUserId(),
                    PROBATE.name(),
                    notificationStrategy.getCaseTypeName(),
                    caseId,
                    true,
                    caseDataContent);
        } catch (Exception e) {
            final String errorMsg = String.format("Error saving failed notification to CCD for case: %s", caseId);
            log.error(errorMsg, e);
            throw new CcdUpdateNotificationException(errorMsg, e);
        }
    }

    private GrantOfRepresentationData buildCaseData(Map<String, Object> data,
                                                    Document sentEmail,
                                                    NotificationType notificationType) {
        List<CollectionMember<ProbateDocument>> notifications =
                getProbateDocuments(sentEmail, data);
        return switch (notificationType) {
            case FIRST_STOP_REMINDER -> GrantOfRepresentationData.builder()
                    .probateNotificationsGenerated(notifications)
                    .firstStopReminderSentDate(LocalDate.now())
                    .build();
            case SECOND_STOP_REMINDER,
                 HSE_REMINDER,
                 DORMANT_WARNING,
                 UNSUBMITTED_APPLICATION,
                 DECLARATION_NOT_SIGNED
                    -> GrantOfRepresentationData.builder()
                    .probateNotificationsGenerated(notifications)
                    .build();
            case DORMANT_REMINDER -> GrantOfRepresentationData.builder()
                    .bulkPrintId(getBulkPrintCollection((BulkPrint)data.get("bulkPrint"),data))
                    .probateNotificationsGenerated(notifications)
                    .build();
        };
    }

    private List<CollectionMember<BulkPrint>> getBulkPrintCollection(BulkPrint bulkPrint, Map<String, Object> data) {
        List<CollectionMember<BulkPrint>> existingBulkPrints = new ArrayList<>();
        Object rawData = data.get(BULK_PRINT_ID);

        if (rawData != null) {
            try {
                List<?> rawList = (List<?>) rawData;
                for (Object item : rawList) {
                    CollectionMember<BulkPrint> member = objectMapper.convertValue(
                            item,
                            objectMapper.getTypeFactory()
                                    .constructParametricType(CollectionMember.class, BulkPrint.class)
                    );
                    existingBulkPrints.add(member);
                }
            } catch (Exception e) {
                throw new CcdUpdateNotificationException("Failed to deserialize existing notifications", e);
            }
        }
        existingBulkPrints.add(new CollectionMember<>(null, bulkPrint));

        return existingBulkPrints;
    }

    private List<CollectionMember<ProbateDocument>> getProbateDocuments(
            Document sentEmail,
            Map<String, Object> data) {
        List<CollectionMember<ProbateDocument>> existingDocuments = new ArrayList<>();
        Object rawData = data.get(PROBATE_NOTIFICATIONS_GENERATED);

        if (rawData != null) {
            try {
                List<?> rawList = (List<?>) rawData;
                for (Object item : rawList) {
                    CollectionMember<ProbateDocument> member = objectMapper.convertValue(
                            item,
                            objectMapper.getTypeFactory()
                                    .constructParametricType(CollectionMember.class, ProbateDocument.class)
                    );
                    existingDocuments.add(member);
                }
            } catch (Exception e) {
                throw new CcdUpdateNotificationException("Failed to deserialize existing notifications", e);
            }
        }
        existingDocuments.add(new CollectionMember<>(null, getProbateDocument(sentEmail)));

        return existingDocuments;
    }

    private ProbateDocument getProbateDocument(Document boDocument) {
        ProbateDocumentLink probateDocumentLink = ProbateDocumentLink.builder()
                .documentBinaryUrl(boDocument.getDocumentLink().getDocumentBinaryUrl())
                .documentFilename(boDocument.getDocumentLink().getDocumentFilename())
                .documentUrl(boDocument.getDocumentLink().getDocumentUrl())
                .build();
        ProbateDocumentType probateDocumentType = ProbateDocumentType.valueOf(boDocument.getDocumentType().name());
        return ProbateDocument.builder()
                .documentDateAdded(boDocument.getDocumentDateAdded())
                .documentFileName(boDocument.getDocumentFileName())
                .documentGeneratedBy(boDocument.getDocumentGeneratedBy())
                .documentLink(probateDocumentLink)
                .documentType(probateDocumentType)
                .build();
    }
}
