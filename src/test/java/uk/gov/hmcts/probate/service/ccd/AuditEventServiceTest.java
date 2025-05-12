package uk.gov.hmcts.probate.service.ccd;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.model.ccd.raw.response.AuditEvent;
import uk.gov.hmcts.probate.model.ccd.raw.response.AuditEventsResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class AuditEventServiceTest {

    private static final String USER_TOKEN = "USER_TOKEN";
    private static final String SERVICE_TOKEN = "SERVICE_TOKEN";
    private static final String CASE_ID = "1111";
    private static final String EVENT_NAME = "updateDraft";
    private static final String STATE_NAME = "Pending";

    @Mock
    private CaseDataApiV2 caseDataApi;


    @Mock
    private AuditEventsResponse auditEventsResponse;

    @InjectMocks
    private AuditEventService auditEventService;

    private static final LocalDateTime LOCAL_DATE_TIME = LocalDateTime.now();

    @BeforeEach
    void setup() {
        when(caseDataApi.getAuditEvents(USER_TOKEN, SERVICE_TOKEN, false, CASE_ID))
                .thenReturn(auditEventsResponse);
    }

    @Test
    void shouldGetAuditEventByName() {
        AuditEvent expectedAuditEvent = buildAuditEvent("updateDraft", STATE_NAME, LOCAL_DATE_TIME);

        List<AuditEvent> auditEventList = List.of(
                expectedAuditEvent,
                buildAuditEvent("CasePrinted",STATE_NAME, LOCAL_DATE_TIME),
                buildAuditEvent("Disposed", STATE_NAME, LOCAL_DATE_TIME));

        when(auditEventsResponse.getAuditEvents()).thenReturn(auditEventList);

        Optional<AuditEvent> actualAuditEvent
                = auditEventService.getLatestAuditEventByName(CASE_ID, List.of(EVENT_NAME), USER_TOKEN, SERVICE_TOKEN);

        assertThat(actualAuditEvent).isPresent().contains(expectedAuditEvent);
    }

    @Test
    void shouldGetLatestInstanceOfAuditEventByName() {
        AuditEvent expectedAuditEvent = buildAuditEvent(EVENT_NAME, STATE_NAME, LOCAL_DATE_TIME);

        List<AuditEvent> auditEventList = List.of(
                buildAuditEvent(EVENT_NAME, STATE_NAME, LOCAL_DATE_TIME.minusMinutes(3)),
                expectedAuditEvent,
                buildAuditEvent(EVENT_NAME, STATE_NAME, LOCAL_DATE_TIME.minusMinutes(2)));

        when(auditEventsResponse.getAuditEvents()).thenReturn(auditEventList);
        Optional<AuditEvent> actualAuditEvent
                = auditEventService.getLatestAuditEventByName(CASE_ID, List.of(EVENT_NAME), USER_TOKEN, SERVICE_TOKEN);

        assertThat(actualAuditEvent).isPresent().contains(expectedAuditEvent);
    }

    @Test
    void shouldReturnEmptyOptionalIfAuditEventWithNameCannotBeFound() {
        List<AuditEvent> auditEventList = List.of(
                buildAuditEvent("keepDraft", STATE_NAME, LOCAL_DATE_TIME),
                buildAuditEvent("disposeCase", STATE_NAME, LOCAL_DATE_TIME));

        when(auditEventsResponse.getAuditEvents()).thenReturn(auditEventList);

        Optional<AuditEvent> actualAuditEvent
                = auditEventService.getLatestAuditEventByName(CASE_ID, List.of(EVENT_NAME), USER_TOKEN, SERVICE_TOKEN);

        assertThat(actualAuditEvent).isEmpty();
    }

    @Test
    void shouldReturnEmptyOptionalIfAuditEventsIsEmpty() {
        when(auditEventsResponse.getAuditEvents()).thenReturn(List.of());

        Optional<AuditEvent> actualAuditEvent
                = auditEventService.getLatestAuditEventByName(CASE_ID, List.of(EVENT_NAME), USER_TOKEN, SERVICE_TOKEN);

        assertThat(actualAuditEvent).isEmpty();
    }

    private AuditEvent buildAuditEvent(String eventId, String stateId, LocalDateTime createdDate) {
        return AuditEvent.builder()
                .id(eventId)
                .stateId(stateId)
                .userFirstName("Tom")
                .userLastName("Jones")
                .createdDate(createdDate)
                .build();
    }

    @Test
    void shouldGetLatestInstanceOfAuditEventByState() {
        AuditEvent expectedAuditEvent = buildAuditEvent(EVENT_NAME, STATE_NAME, LOCAL_DATE_TIME);

        List<AuditEvent> auditEventList = List.of(
                buildAuditEvent(EVENT_NAME, STATE_NAME, LOCAL_DATE_TIME.minusMinutes(3)),
                expectedAuditEvent,
                buildAuditEvent(EVENT_NAME, STATE_NAME, LOCAL_DATE_TIME.minusMinutes(2)));

        when(auditEventsResponse.getAuditEvents()).thenReturn(auditEventList);
        Optional<AuditEvent> actualAuditEvent
                = auditEventService.getLatestAuditEventByState(CASE_ID, List.of(STATE_NAME), USER_TOKEN, SERVICE_TOKEN);

        assertThat(actualAuditEvent).isPresent().contains(expectedAuditEvent);
    }

    @Test
    void shouldReturnEmptyOptionalIfAuditEventsIsEmptyWhenGetEventByState() {
        when(auditEventsResponse.getAuditEvents()).thenReturn(List.of());

        Optional<AuditEvent> actualAuditEvent
                = auditEventService.getLatestAuditEventByState(CASE_ID, List.of(STATE_NAME), USER_TOKEN, SERVICE_TOKEN);

        assertThat(actualAuditEvent).isEmpty();
    }
}