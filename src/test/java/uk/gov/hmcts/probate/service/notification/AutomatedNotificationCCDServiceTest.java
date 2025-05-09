package uk.gov.hmcts.probate.service.notification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.ccd.CcdCaseType.GRANT_OF_REPRESENTATION;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.probate.model.ccd.CcdCaseType;
import uk.gov.hmcts.probate.model.ccd.EventId;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.service.ccd.CcdClientApi;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class AutomatedNotificationCCDServiceTest {
    @Mock
    private CcdClientApi ccdClientApi;

    @Mock
    private CaseData caseDataMock;

    @InjectMocks
    private AutomatedNotificationCCDService automatedNotificationCCDService;

    private static final String CASE_ID = "1234567890123456";
    private static final EventId EVENT_ID = EventId.AUTOMATED_NOTIFICATION;
    private static final CcdCaseType CASE_TYPE_GOP = GRANT_OF_REPRESENTATION;
    private static final String DESCRIPTION = AutomatedNotificationCCDService.EVENT_DESCRIPTION;
    private static final String SUMMARY = AutomatedNotificationCCDService.EVENT_SUMMARY;
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
    void shouldAddNotificationAndUpdateCaseWhenNoExisting_notifications() {
        when(caseDetails.getData()).thenReturn(new HashMap<>());

        Document sentEmail = mock(Document.class);

        automatedNotificationCCDService.saveNotification(caseDetails, CASE_ID, securityDTO, sentEmail);

        ArgumentCaptor<CaseData> captor = ArgumentCaptor.forClass(CaseData.class);
        verify(ccdClientApi).updateCaseAsCaseworker(
                eq(CASE_TYPE_GOP), eq(CASE_ID), eq(LAST_MODIFIED),
                captor.capture(),
                eq(EVENT_ID), eq(securityDTO),
                eq(DESCRIPTION),
                eq(SUMMARY)
        );

        CaseData cd = captor.getValue();
        assertNotNull(cd.getProbateNotificationsGenerated());
        assertEquals(1, cd.getProbateNotificationsGenerated().size());
        assertSame(sentEmail, cd.getProbateNotificationsGenerated().getFirst().getValue());
        assertEquals(LocalDate.now(), cd.getFirstStopReminderSentDate());
    }

    @Test
    void shouldAppendToExistingNotifications() {
        Document existingEmail = mock(Document.class);
        CollectionMember<Document> existingMember = new CollectionMember<>("id1", existingEmail);
        List<CollectionMember<Document>> existingList = new ArrayList<>();
        existingList.add(existingMember);
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("probateNotificationsGenerated", existingList);
        when(caseDetails.getData()).thenReturn(dataMap);

        Document sentEmail = mock(Document.class);

        automatedNotificationCCDService.saveNotification(caseDetails, CASE_ID, securityDTO, sentEmail);

        ArgumentCaptor<CaseData> captor = ArgumentCaptor.forClass(CaseData.class);
        verify(ccdClientApi).updateCaseAsCaseworker(
                eq(GRANT_OF_REPRESENTATION), eq(CASE_ID), eq(LAST_MODIFIED),
                captor.capture(),
                eq(EVENT_ID), eq(securityDTO),
                eq(DESCRIPTION),
                eq(SUMMARY)
        );

        CaseData cd = captor.getValue();
        List<CollectionMember<Document>> notifications = cd.getProbateNotificationsGenerated();
        assertNotNull(notifications);
        assertEquals(2, notifications.size());
        assertSame(existingEmail, notifications.get(0).getValue());
        assertSame(sentEmail, notifications.get(1).getValue());
    }
}
