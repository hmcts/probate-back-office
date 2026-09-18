package uk.gov.hmcts.probate.service.migration;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.StateConstants.STATE_DORMANT;

@ExtendWith(MockitoExtension.class)
class Dtspb5113GorRollbackTest {

    private Dtspb5113GorRollback underTest;

    @BeforeEach
    void setUp() {
        underTest = new Dtspb5113GorRollback();
    }

    @Test
    void shouldSetCaseStateToDormantAndReturnCallbackRequest() {
        final CallbackRequest callbackRequest = mock();
        final CaseDetails caseDetails = mock();
        final JSONObject migrationData = mock();

        when(callbackRequest.getCaseDetails()).thenReturn(caseDetails);

        final CallbackRequest result = underTest.migrate(callbackRequest, migrationData);

        assertSame(callbackRequest, result);
        verify(caseDetails).setState(STATE_DORMANT);
    }
}
