package uk.gov.hmcts.probate.service.migration;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.probate.exception.DataMigrationException;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.AuditEvent;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.ccd.AuditEventService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Dtspb5113GorMigrationTest {

    private Dtspb5113GorMigration underTest;

    @Mock
    SecurityUtils securityUtils;

    @Mock
    AuditEventService auditEventService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        underTest = new Dtspb5113GorMigration(auditEventService, securityUtils);
    }

    @Test
    void shouldFetchFromAuditEventAndSetState() {
        final CallbackRequest callbackRequest = mock();
        final CaseDetails caseDetails = mock();
        final Long caseId = 1L;
        final JSONObject migrationData = mock();

        final SecurityDTO securityDTO = mock();
        final String authorisation = "Bearer user-token";
        final String serviceAuthorisation = "Bearer s2s-token";

        final AuditEvent auditEvent = mock();
        final String auditState = "BOPostGrantIssued";

        when(callbackRequest.getCaseDetails()).thenReturn(caseDetails);
        when(caseDetails.getId()).thenReturn(caseId);

        when(securityUtils.getSecurityDTO()).thenReturn(securityDTO);
        when(securityDTO.getAuthorisation()).thenReturn(authorisation);
        when(securityDTO.getServiceAuthorisation()).thenReturn(serviceAuthorisation);

        when(auditEvent.getStateId()).thenReturn(auditState);
        when(auditEventService.getLatestAuditEventExcludingDormantState(
                eq(String.valueOf(caseId)),
                eq(List.of(
                        "BOPostGrantIssued",
                        "BOExaminingReissue",
                        "BOCaseMatchingReissue",
                        "BOCaseStoppedReissue",
                        "BOGrantIssuedRegistrarEscalation",
                        "BOPostGrantIssuedRegistrarEscalation")),
                eq(authorisation),
                eq(serviceAuthorisation)))
                .thenReturn(Optional.of(auditEvent));

        final CallbackRequest result = underTest.migrate(callbackRequest, migrationData);

        assertSame(callbackRequest, result);

        verify(caseDetails).setState(auditState);
    }

    @Test
    void shouldThrowExceptionWhenNoAuditEventFound() {
        final CallbackRequest callbackRequest = mock();
        final CaseDetails caseDetails = mock();
        final Long caseId = 1L;
        final JSONObject migrationData = mock();

        final SecurityDTO securityDTO = mock();
        final String authorisation = "Bearer user-token";
        final String serviceAuthorisation = "Bearer s2s-token";

        when(callbackRequest.getCaseDetails()).thenReturn(caseDetails);
        when(caseDetails.getId()).thenReturn(caseId);

        when(securityUtils.getSecurityDTO()).thenReturn(securityDTO);
        when(securityDTO.getAuthorisation()).thenReturn(authorisation);
        when(securityDTO.getServiceAuthorisation()).thenReturn(serviceAuthorisation);

        when(auditEventService.getLatestAuditEventExcludingDormantState(
                anyString(),
                anyList(),
                anyString(),
                anyString()))
                .thenReturn(Optional.empty());

        final DataMigrationException exception = assertThrows(
                DataMigrationException.class,
                () -> underTest.migrate(callbackRequest, migrationData)
        );

        assertEquals("No audit event found for case ID: 1", exception.getMessage());

        verify(caseDetails, never()).setState(anyString());
    }
}
