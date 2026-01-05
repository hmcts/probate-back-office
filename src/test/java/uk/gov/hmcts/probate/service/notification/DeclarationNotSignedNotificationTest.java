package uk.gov.hmcts.probate.service.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.ExecutorApplying;
import uk.gov.service.notify.NotificationClientException;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static uk.gov.hmcts.probate.model.StateConstants.STATE_DORMANT;
import static uk.gov.hmcts.probate.model.StateConstants.STATE_PENDING;

class DeclarationNotSignedNotificationTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private CaseDetails caseDetails;

    private DeclarationNotSignedNotification underTest;

    private static final String EXECUTORS_APPLYING = "executorsApplying";

    AutoCloseable closeableMocks;
    private Clock fixedClock;

    @BeforeEach
    void setUp() {
        closeableMocks = MockitoAnnotations.openMocks(this);

        fixedClock = Clock.fixed(
                LocalDateTime.of(2025,5,27,12,0).toInstant(ZoneOffset.UTC),
                ZoneId.of("UTC")
        );
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        underTest = new DeclarationNotSignedNotification(notificationService, objectMapper);
        underTest.setReferenceDate(LocalDate.now(fixedClock));
    }

    @AfterEach
    void tearDown() throws Exception {
        closeableMocks.close();
    }


    @Test
    void shouldSendDeclarationNotSigned() throws NotificationClientException {
        underTest.sendNotification(caseDetails);

        verify(notificationService, times(1)).sendDeclarationNotSignedEmail(caseDetails);
    }

    @Test
    void acceptsNullOrNoDataReturnsFalse() {
        assertAll(
            () -> assertFalse(underTest.accepts().test(null)),
            () -> {
                CaseDetails noData = CaseDetails.builder()
                        .state(STATE_PENDING)
                        .data(null)
                        .lastModified(LocalDateTime.now(fixedClock))
                        .build();
                assertFalse(underTest.accepts().test(noData));
            }
        );
    }

    @Test
    void acceptsWrongStateReturnsFalse() {
        CaseDetails wrong = CaseDetails.builder()
                .state(STATE_DORMANT)
                .lastModified(LocalDateTime.of(2025,5,24,11,0))
                .build();
        assertFalse(underTest.accepts().test(wrong));
    }

    @Test
    void acceptsInvalidLastModifiedReturnsFalse() {
        LocalDateTime after = LocalDateTime.of(2025,5,28,12,1);
        CaseDetails cd = CaseDetails.builder()
                .state(STATE_PENDING)
                .lastModified(after)
                .build();
        assertFalse(underTest.accepts().test(cd));
    }

    @Test
    void acceptsEmptyExecutorApplyingReturnsFalse() {
        LocalDateTime before = LocalDateTime.of(2025,5,23,23,59);
        Map<String,Object> data = Map.of(EXECUTORS_APPLYING, Collections.emptyList());
        CaseDetails beforeCase = CaseDetails.builder()
                .state(STATE_PENDING)
                .lastModified(before)
                .data(data)
                .build();
        assertFalse(underTest.accepts().test(beforeCase));
    }

    @Test
    void acceptsInvalidExecutorApplyingReturnsFalse() {
        LocalDateTime before = LocalDateTime.of(2025,5,23,23,59);
        List<CollectionMember<ExecutorApplying>> list = List.of(
                buildExecutor("Executor one", "executor-one@probate-test.com",true, true),
                buildExecutor("Executor two", "executor-two@probate-test.com",false, false),
                buildExecutor("Executor three", "executor-three@probate-test.com",null, null),
                buildExecutor("Executor four", "executor-four@probate-test.com",true, false)
        );
        Map<String,Object> data = Map.of(EXECUTORS_APPLYING, list);
        CaseDetails beforeCase = CaseDetails.builder()
                .state(STATE_PENDING)
                .lastModified(before)
                .data(data)
                .build();
        assertFalse(underTest.accepts().test(beforeCase));
    }

    @Test
    void acceptsReturnsTrue() {
        LocalDateTime before = LocalDateTime.of(2025,5,26,23,59);
        List<CollectionMember<ExecutorApplying>> executors = List.of(
                buildExecutor("Executor one", "executor-one@probate-test.com",true, true),
                buildExecutor("Executor two", "executor-two@probate-test.com",false, false),
                buildExecutor("Executor three", "executor-three@probate-test.com",null, null),
                buildExecutor("Executor four", "executor-four@probate-test.com",true, false),
                buildExecutor("Executor five", "executor-five@probate-test.com",false, true),
                buildExecutor("Executor six", "executor-six@probate-test.com",true, null),
                buildExecutor("Executor seven", "executor-seven@probate-test.com",null, true)
        );
        Map<String,Object> data = Map.of(EXECUTORS_APPLYING, executors);
        CaseDetails beforeCase = CaseDetails.builder()
                .state(STATE_PENDING)
                .data(data)
                .lastModified(before)
                .build();
        assertTrue(underTest.accepts().test(beforeCase));
    }

    private CollectionMember<ExecutorApplying> buildExecutor(String name,
                                                             String email,
                                                             Boolean isAgreed,
                                                             Boolean emailSent) {
        ExecutorApplying applying = ExecutorApplying.builder()
                .applyingExecutorName(name)
                .applyingExecutorEmail(email)
                .applyingExecutorAgreed(isAgreed)
                .applyingExecutorEmailSent(emailSent)
                .build();
        return new CollectionMember<>(null, applying);
    }
}
