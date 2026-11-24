package uk.gov.hmcts.probate.service.migration;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ClearDeceasedDobMigrationHandlerTest {

    ClearDeceasedDobMigrationHandler clearDeceasedDobMigrationHandler;

    @BeforeEach
    void setUp() {
        clearDeceasedDobMigrationHandler = new ClearDeceasedDobMigrationHandler();
    }

    @Test
    void shouldClearDeceasedDob() {
        final CallbackRequest callbackRequest = mock();
        final CaseDetails caseDetails = mock();
        final CaseData caseData = mock();
        final Long caseId = 1L;

        when(callbackRequest.getCaseDetails())
                .thenReturn(caseDetails);
        when(caseDetails.getData())
                .thenReturn(caseData);
        when(caseDetails.getId())
                .thenReturn(caseId);

        final JSONObject migrationData = null;

        final CallbackRequest actual = clearDeceasedDobMigrationHandler.migrate(
                callbackRequest,
                migrationData);

        assertAll(
                () -> assertThat(actual, sameInstance(callbackRequest)),
                () -> verify(caseData).clearDeceasedDateOfBirth(eq(caseId)));
    }
}
