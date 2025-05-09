package uk.gov.hmcts.probate.service.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.EventId;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.service.ccd.CcdClientApi;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.probate.model.ProbateDocument;
import uk.gov.hmcts.reform.probate.model.ProbateDocumentLink;
import uk.gov.hmcts.reform.probate.model.ProbateDocumentType;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static uk.gov.hmcts.probate.model.ccd.CcdCaseType.GRANT_OF_REPRESENTATION;

@Slf4j
@Service
@RequiredArgsConstructor
public class AutomatedNotificationCCDService {
    private final CcdClientApi ccdClientApi;
    private final ObjectMapper objectMapper;
    public static final String EVENT_DESCRIPTION = "Send Stop Reminder";
    public static final String EVENT_SUMMARY = "Send Stop Reminder";


    public void saveNotification(final CaseDetails caseDetails,
                                 final String caseId,
                                 final SecurityDTO securityDTO,
                                 final Document sentEmail,
                                 final boolean isFirstStopReminder) {
        log.info("AutomatedNotificationCCDService buildCaseData for case id: {}", caseId);
        GrantOfRepresentationData data = buildCaseData(caseDetails.getData(), sentEmail, isFirstStopReminder);
        log.info("AutomatedNotificationCCDService saveNotification to Case: " + caseId);
        try {
            ccdClientApi.updateCaseAsCaseworker(GRANT_OF_REPRESENTATION, caseId, caseDetails.getLastModified(), data,
                    EventId.AUTOMATED_NOTIFICATION, securityDTO, EVENT_DESCRIPTION, EVENT_SUMMARY);
        } catch (Exception e) {
            log.error("Error saving notification to CCD for case id: {}, Error: {}", caseId, e.getMessage());
        }
    }

    private GrantOfRepresentationData buildCaseData(Map<String, Object> data,
                                                    Document sentEmail,
                                                    boolean isFirstStopReminder) {
        List<CollectionMember<ProbateDocument>> notifications =
                getProbateDocuments(sentEmail, data);
        if (isFirstStopReminder) {
            return GrantOfRepresentationData.builder()
                .probateNotificationsGenerated(notifications)
                .firstStopReminderSentDate(LocalDate.now())
                .build();
        } else {
            return GrantOfRepresentationData.builder()
                .probateNotificationsGenerated(notifications)
                .build();
        }
    }

    private List<CollectionMember<ProbateDocument>> getProbateDocuments(
            Document sentEmail,
            Map<String, Object> data) {
        List<CollectionMember<ProbateDocument>> existingDocuments = new ArrayList<>();
        Object rawData = data.get("probateNotificationsGenerated");

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
                log.warn("Failed to parse probateNotificationsGenerated. Reason: {}",
                        e.getMessage());
                throw e;
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
