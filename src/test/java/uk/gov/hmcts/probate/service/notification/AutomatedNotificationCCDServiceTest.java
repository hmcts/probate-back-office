package uk.gov.hmcts.probate.service.notification;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.NotificationType;
import uk.gov.hmcts.probate.model.ccd.CcdCaseType;
import uk.gov.hmcts.probate.model.ccd.EventId;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.service.ccd.CcdClientApi;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.probate.model.ProbateDocument;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.ccd.CcdCaseType.GRANT_OF_REPRESENTATION;

@ExtendWith(SpringExtension.class)
class AutomatedNotificationCCDServiceTest {
    @Mock
    private CcdClientApi ccdClientApi;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private FirstStopReminderNotification firstStopReminderNotificationStrategy;

    @Mock
    private SecondStopReminderNotification secondStopReminderNotificationStrategy;

    @InjectMocks
    private AutomatedNotificationCCDService automatedNotificationCCDService;

    private static final String CASE_ID = "1234567890123456";
    private static final EventId EVENT_ID = EventId.AUTOMATED_NOTIFICATION;
    private static final CcdCaseType CASE_TYPE_GOP = GRANT_OF_REPRESENTATION;
    private static final String DESCRIPTION = "description";
    private static final String SUMMARY = "summary";
    private static final LocalDateTime LAST_MODIFIED = LocalDateTime.of(2025, 5, 6, 10, 0);

    private CaseDetails caseDetails;
    private SecurityDTO securityDTO;

    @BeforeEach
    void setUp() {
        caseDetails = mock(CaseDetails.class);
        securityDTO = mock(SecurityDTO.class);
        when(caseDetails.getLastModified()).thenReturn(LAST_MODIFIED);
    }

    @Test
    void shouldAddNotificationAndUpdateCaseWhenNoExistingNotifications() {
        when(caseDetails.getData()).thenReturn(new HashMap<>());
        stubNotificationStrategy(
                firstStopReminderNotificationStrategy,
                NotificationType.FIRST_STOP_REMINDER,
                DESCRIPTION,
                SUMMARY
        );

        Document sentEmail = createMockDocument("newEmail.pdf");

        automatedNotificationCCDService
                .saveNotification(caseDetails, CASE_ID, securityDTO, sentEmail, firstStopReminderNotificationStrategy);

        GrantOfRepresentationData cd = captureUpdatedCaseData(CASE_ID, securityDTO, EVENT_ID, DESCRIPTION, SUMMARY);

        assertNotNull(cd.getProbateNotificationsGenerated());
        assertEquals(1, cd.getProbateNotificationsGenerated().size());
        assertEquals(sentEmail.getDocumentFileName(),
                cd.getProbateNotificationsGenerated().getFirst().getValue().getDocumentFileName());
        assertEquals(LocalDate.now(), cd.getFirstStopReminderSentDate());
    }

    @Test
    void shouldFirstStopReminderSentDateNullSecondStopReminder() {
        when(caseDetails.getData()).thenReturn(new HashMap<>());

        stubNotificationStrategy(
                secondStopReminderNotificationStrategy,
                NotificationType.SECOND_STOP_REMINDER,
                DESCRIPTION,
                SUMMARY
        );

        Document sentEmail = createMockDocument("newEmail.pdf");

        automatedNotificationCCDService
                .saveNotification(caseDetails, CASE_ID, securityDTO, sentEmail, secondStopReminderNotificationStrategy);

        GrantOfRepresentationData cd = captureUpdatedCaseData(CASE_ID, securityDTO, EVENT_ID, DESCRIPTION, SUMMARY);

        assertNotNull(cd.getProbateNotificationsGenerated());
        assertEquals(1, cd.getProbateNotificationsGenerated().size());
        assertEquals(sentEmail.getDocumentFileName(),
                cd.getProbateNotificationsGenerated().getFirst().getValue().getDocumentFileName());
        assertNull(cd.getFirstStopReminderSentDate());
    }

    @Test
    void shouldAppendToExistingNotifications() {
        TypeFactory typeFactory = mock(TypeFactory.class);
        JavaType javaType = mock(JavaType.class);
        when(objectMapper.getTypeFactory()).thenReturn(typeFactory);
        when(typeFactory.constructParametricType(
                uk.gov.hmcts.reform.probate.model.cases.CollectionMember.class,
                ProbateDocument.class)
        ).thenReturn(javaType);

        Document existingEmail = createMockDocument("existingEmail.pdf");
        CollectionMember<Document> existingMember = new CollectionMember<>("id1", existingEmail);
        List<CollectionMember<Document>> existingList = new ArrayList<>();
        existingList.add(existingMember);

        Map<String, Object> dataMap = new HashMap<>();
        List<Object> rawList = new ArrayList<>();
        rawList.add(existingMember);
        dataMap.put("probateNotificationsGenerated", rawList);
        when(caseDetails.getData()).thenReturn(dataMap);

        ProbateDocument mappedExisting = ProbateDocument.builder()
                .documentFileName("existingEmail.pdf")
                .build();
        uk.gov.hmcts.reform.probate.model.cases.CollectionMember<ProbateDocument> mappedExistingMember =
                new uk.gov.hmcts.reform.probate.model.cases.CollectionMember<>("id1", mappedExisting);

        when(objectMapper.convertValue(
                eq(existingMember),
                any(JavaType.class))
        ).thenReturn(mappedExistingMember);

        stubNotificationStrategy(
                firstStopReminderNotificationStrategy,
                NotificationType.FIRST_STOP_REMINDER,
                DESCRIPTION,
                SUMMARY
        );

        Document sentEmail = createMockDocument("newEmail.pdf");

        automatedNotificationCCDService
                .saveNotification(caseDetails, CASE_ID, securityDTO, sentEmail, firstStopReminderNotificationStrategy);

        GrantOfRepresentationData cd = captureUpdatedCaseData(CASE_ID, securityDTO, EVENT_ID, DESCRIPTION, SUMMARY);

        List<uk.gov.hmcts.reform.probate.model.cases.CollectionMember<ProbateDocument>> notifications =
                cd.getProbateNotificationsGenerated();

        assertNotNull(notifications);
        assertEquals(2, notifications.size());
        assertEquals("existingEmail.pdf", notifications.get(0).getValue().getDocumentFileName());
        assertEquals("newEmail.pdf", notifications.get(1).getValue().getDocumentFileName());
    }

    private Document createMockDocument(String fileName) {
        DocumentLink link = DocumentLink.builder()
                .documentBinaryUrl("binaryUrl")
                .documentFilename(fileName)
                .documentUrl("documentUrl")
                .build();

        return Document.builder()
                .documentLink(link)
                .documentType(DocumentType.SENT_EMAIL)
                .documentFileName(fileName)
                .documentGeneratedBy("system")
                .documentDateAdded(LocalDate.now())
                .build();
    }

    private GrantOfRepresentationData captureUpdatedCaseData(String caseId, SecurityDTO securityDTO, EventId eventId,
                                                             String description, String summary) {
        ArgumentCaptor<GrantOfRepresentationData> captor = ArgumentCaptor.forClass(GrantOfRepresentationData.class);
        verify(ccdClientApi).updateCaseAsCaseworker(
                eq(GRANT_OF_REPRESENTATION), eq(caseId), eq(LAST_MODIFIED),
                captor.capture(),
                eq(eventId), eq(securityDTO),
                eq(description),
                eq(summary)
        );
        return captor.getValue();
    }

    private void stubNotificationStrategy(NotificationStrategy strategy, NotificationType type,
                                          String description, String summary) {
        when(strategy.getType()).thenReturn(type);
        when(strategy.getEventDescription()).thenReturn(description);
        when(strategy.getEventSummary()).thenReturn(summary);
    }
}