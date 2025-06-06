package uk.gov.hmcts.probate.service.notification;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.exception.CcdUpdateNotificationException;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.NotificationType;
import uk.gov.hmcts.probate.model.ccd.EventId;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;
import uk.gov.hmcts.reform.ccd.client.model.CaseDataContent;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.ccd.client.model.StartEventResponse;
import uk.gov.hmcts.reform.probate.model.ProbateDocument;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.ccd.CcdCaseType.GRANT_OF_REPRESENTATION;
import static uk.gov.hmcts.reform.probate.model.cases.JurisdictionId.PROBATE;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AutomatedNotificationCCDServiceTest {
    @Mock
    private CoreCaseDataApi coreCaseDataApi;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private FirstStopReminderNotification firstStopReminderNotificationStrategy;

    @Mock
    private SecondStopReminderNotification secondStopReminderNotificationStrategy;

    @Mock
    private DormantWarningNotification dormantWarningNotificationStrategy;

    private AutomatedNotificationCCDService underTest;

    AutoCloseable closeableMocks;

    private static final String CASE_ID = "1234567890123456";
    private static final EventId EVENT_ID = EventId.AUTO_NOTIFICATION_FIRST_STOP_REMINDER;
    private static final String DESCRIPTION = "description";
    private static final String SUMMARY = "summary";
    private static final String FAILURE_DESCRIPTION = "failure description";
    private static final String FAILURE_SUMMARY = "failure summary";
    private static final LocalDateTime LAST_MODIFIED = LocalDateTime.of(2025, 5, 6, 10, 0);

    private CaseDetails caseDetails;
    private SecurityDTO securityDTO;
    private StartEventResponse startEvent;

    @BeforeEach
    void setUp() {
        closeableMocks = MockitoAnnotations.openMocks(this);

        securityDTO = SecurityDTO.builder()
                .authorisation("authToken")
                .serviceAuthorisation("svcToken")
                .userId("user1")
                .build();

        caseDetails = CaseDetails.builder()
                .data(new HashMap<>())
                .lastModified(LAST_MODIFIED)
                .build();

        startEvent = StartEventResponse.builder()
                .token("evtToken")
                .eventId(EVENT_ID.getName())
                .caseDetails(caseDetails)
                .build();
        underTest = new AutomatedNotificationCCDService(coreCaseDataApi, objectMapper);
    }

    @Test
    void shouldAddNotificationAndUpdateCaseWhenNoExistingNotifications() {
        stubNotificationStrategy(
                firstStopReminderNotificationStrategy,
                NotificationType.FIRST_STOP_REMINDER
        );

        Document sentEmail = createMockDocument("newEmail.pdf");

        underTest
                .saveNotification(caseDetails, CASE_ID, securityDTO, sentEmail,
                        firstStopReminderNotificationStrategy, startEvent);

        CaseDataContent caseDataContent = captureUpdatedCaseData(CASE_ID, securityDTO);
        GrantOfRepresentationData cd = (GrantOfRepresentationData) caseDataContent.getData();

        assertNotNull(cd.getProbateNotificationsGenerated());
        assertEquals(1, cd.getProbateNotificationsGenerated().size());
        assertEquals(sentEmail.getDocumentFileName(),
                cd.getProbateNotificationsGenerated().getFirst().getValue().getDocumentFileName());
        assertEquals(LocalDate.now(), cd.getFirstStopReminderSentDate());
    }

    @Test
    void startEventReturnsResponseOrThrows() {
        stubNotificationStrategy(
                firstStopReminderNotificationStrategy,
                NotificationType.FIRST_STOP_REMINDER
        );
        when(coreCaseDataApi.startEventForCaseWorker(
                anyString(), anyString(), anyString(),
                anyString(),
                eq(GRANT_OF_REPRESENTATION.getName()),
                eq(CASE_ID),
                eq(EventId.AUTO_NOTIFICATION_FIRST_STOP_REMINDER.getName())))
                .thenReturn(startEvent);

        StartEventResponse response = underTest
                .startEvent(CASE_ID, securityDTO, firstStopReminderNotificationStrategy);
        assertEquals(startEvent, response);

        when(coreCaseDataApi.startEventForCaseWorker(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(null);
        assertThrows(CcdUpdateNotificationException.class, () ->
                underTest
                        .startEvent(CASE_ID, securityDTO, firstStopReminderNotificationStrategy));
    }

    Stream<NotificationStrategy> notificationStrategiesExceptFirstStopReminder() {
        return Stream.of(
                secondStopReminderNotificationStrategy,
                dormantWarningNotificationStrategy
        );
    }

    @ParameterizedTest
    @MethodSource("notificationStrategiesExceptFirstStopReminder")
    void shouldNotSetFirstStopReminderSentDateIfNotFirstStopReminder(NotificationStrategy notificationStrategy) {
        stubNotificationStrategy(
                notificationStrategy,
                NotificationType.SECOND_STOP_REMINDER
        );

        Document sentEmail = createMockDocument("newEmail.pdf");

        underTest
                .saveNotification(caseDetails, CASE_ID, securityDTO, sentEmail,
                        notificationStrategy, startEvent);

        CaseDataContent caseDataContent = captureUpdatedCaseData(CASE_ID, securityDTO);
        GrantOfRepresentationData cd = (GrantOfRepresentationData) caseDataContent.getData();

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
        caseDetails.setData(dataMap);

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
                NotificationType.FIRST_STOP_REMINDER
        );

        Document sentEmail = createMockDocument("newEmail.pdf");

        underTest
                .saveNotification(caseDetails, CASE_ID, securityDTO, sentEmail,
                        firstStopReminderNotificationStrategy, startEvent);

        CaseDataContent caseDataContent = captureUpdatedCaseData(CASE_ID, securityDTO);
        GrantOfRepresentationData cd = (GrantOfRepresentationData) caseDataContent.getData();

        List<uk.gov.hmcts.reform.probate.model.cases.CollectionMember<ProbateDocument>> notifications =
                cd.getProbateNotificationsGenerated();

        assertNotNull(notifications);
        assertEquals(2, notifications.size());
        assertEquals("existingEmail.pdf", notifications.get(0).getValue().getDocumentFileName());
        assertEquals("newEmail.pdf", notifications.get(1).getValue().getDocumentFileName());
    }

    @Test
    void shouldSaveFailedNotification() {
        stubNotificationStrategy(
                firstStopReminderNotificationStrategy,
                NotificationType.FIRST_STOP_REMINDER
        );

        underTest
                .saveFailedNotification(CASE_ID, securityDTO,
                        firstStopReminderNotificationStrategy, startEvent);

        CaseDataContent caseDataContent = captureUpdatedCaseData(CASE_ID, securityDTO);
        GrantOfRepresentationData data = (GrantOfRepresentationData) caseDataContent.getData();

        assertNotNull(data);
        assertNull(data.getFirstStopReminderSentDate());
        assertNull(data.getProbateNotificationsGenerated());
    }

    @Test
    void saveNotificationShouldWrapClientExceptions() {
        stubNotificationStrategy(
                firstStopReminderNotificationStrategy,
                NotificationType.FIRST_STOP_REMINDER
        );
        Document sentEmail = createMockDocument("newEmail.pdf");

        doThrow(new RuntimeException("ccd-fail"))
                .when(coreCaseDataApi)
                .submitEventForCaseWorker(
                        anyString(),
                        anyString(),
                        anyString(),
                        anyString(),
                        anyString(),
                        anyString(),
                        anyBoolean(),
                        any(CaseDataContent.class));

        CcdUpdateNotificationException ex = assertThrows(CcdUpdateNotificationException.class, () ->
                underTest.saveNotification(
                        caseDetails, CASE_ID, securityDTO, sentEmail,
                        firstStopReminderNotificationStrategy, startEvent));
        assertNotNull(ex.getCause());
        assertEquals("ccd-fail", ex.getCause().getMessage());
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

    private CaseDataContent captureUpdatedCaseData(String caseId, SecurityDTO securityDTO) {
        ArgumentCaptor<CaseDataContent> captor = ArgumentCaptor.forClass(CaseDataContent.class);
        verify(coreCaseDataApi).submitEventForCaseWorker(
                eq(securityDTO.getAuthorisation()),
                eq(securityDTO.getServiceAuthorisation()),
                eq(securityDTO.getUserId()),
                eq(PROBATE.name()),
                eq(GRANT_OF_REPRESENTATION.getName()),
                eq(caseId),
                eq(true),
                captor.capture()
        );
        return captor.getValue();
    }

    private void stubNotificationStrategy(NotificationStrategy strategy, NotificationType type) {
        when(strategy.getType()).thenReturn(type);
        when(strategy.getEventDescription()).thenReturn(AutomatedNotificationCCDServiceTest.DESCRIPTION);
        when(strategy.getEventSummary()).thenReturn(AutomatedNotificationCCDServiceTest.SUMMARY);
        when(strategy.getFailureEventDescription()).thenReturn(AutomatedNotificationCCDServiceTest.FAILURE_DESCRIPTION);
        when(strategy.getFailureEventSummary()).thenReturn(AutomatedNotificationCCDServiceTest.FAILURE_SUMMARY);
        when(strategy.getEventId()).thenReturn(EventId.AUTO_NOTIFICATION_FIRST_STOP_REMINDER);
        when(strategy.getCaseTypeName()).thenReturn(GRANT_OF_REPRESENTATION.getName());
    }
}