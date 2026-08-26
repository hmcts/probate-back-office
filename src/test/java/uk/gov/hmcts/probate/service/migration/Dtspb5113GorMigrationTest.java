package uk.gov.hmcts.probate.service.migration;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.probate.exception.DataMigrationException;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Dtspb5113GorMigrationTest {

    private Dtspb5113GorMigration underTest;

    @BeforeEach
    void setUp() {
        underTest = new Dtspb5113GorMigration();
    }

    @Test
    void shouldSetStateWhenMigrateToStateIsValid() {
        final CallbackRequest callbackRequest = mock(CallbackRequest.class);
        final CaseDetails caseDetails = mock(CaseDetails.class);
        final JSONObject migrationData = mock(JSONObject.class);

        final Long caseId = 1L;
        final String migrateToState = "BOPostGrantIssued";

        when(callbackRequest.getCaseDetails()).thenReturn(caseDetails);
        when(caseDetails.getId()).thenReturn(caseId);
        when(migrationData.getString("migrateToState")).thenReturn(migrateToState);

        final CallbackRequest result = underTest.migrate(callbackRequest, migrationData);

        assertSame(callbackRequest, result);
        verify(caseDetails).setState(migrateToState);
    }

    @Test
    void shouldThrowExceptionWhenMigrateToStateIsInvalid() {
        final CallbackRequest callbackRequest = mock(CallbackRequest.class);
        final CaseDetails caseDetails = mock(CaseDetails.class);
        final JSONObject migrationData = mock(JSONObject.class);

        final Long caseId = 1L;
        final String migrateToState = "InvalidState";

        when(callbackRequest.getCaseDetails()).thenReturn(caseDetails);
        when(caseDetails.getId()).thenReturn(caseId);
        when(migrationData.getString("migrateToState")).thenReturn(migrateToState);

        final DataMigrationException exception = assertThrows(
                DataMigrationException.class,
                () -> underTest.migrate(callbackRequest, migrationData)
        );

        assertEquals(
                "Invalid state for migration: InvalidState for case: 1",
                exception.getMessage()
        );

        verify(caseDetails, never()).setState(anyString());
    }
}
