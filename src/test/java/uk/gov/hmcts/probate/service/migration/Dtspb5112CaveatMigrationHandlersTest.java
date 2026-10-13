package uk.gov.hmcts.probate.service.migration;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.service.migration.dtspb5112.Dtspb5112CaveatStates;
import uk.gov.hmcts.probate.service.migration.dtspb5112.Dtspb5112CloseExpiredCaveatMigration;
import uk.gov.hmcts.probate.service.migration.dtspb5112.Dtspb5112CloseExpiredCaveatRollback;
import uk.gov.hmcts.probate.service.migration.dtspb5112.Dtspb5112PaAppCreatedCaveatMigration;
import uk.gov.hmcts.probate.service.migration.dtspb5112.Dtspb5112PaAppCreatedCaveatRollback;
import uk.gov.hmcts.probate.service.migration.dtspb5112.Dtspb5112SetExpiryCaveatRollback;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.service.migration.dtspb5112.Dtspb5112CaveatStates.CAVEAT_CLOSED;
import static uk.gov.hmcts.probate.service.migration.dtspb5112.Dtspb5112CaveatStates.CAVEAT_RAISED;
import static uk.gov.hmcts.probate.service.migration.dtspb5112.Dtspb5112CaveatStates.PA_APP_CREATED;

class Dtspb5112CaveatMigrationHandlersTest {

    @Test
    void paAppCreatedMigrationShouldSetCaveatRaisedState() {
        CaveatCallbackRequest request = mock(CaveatCallbackRequest.class);
        CaveatDetails details = mock(CaveatDetails.class);
        when(request.getCaseDetails()).thenReturn(details);

        CaveatCallbackRequest result = new Dtspb5112PaAppCreatedCaveatMigration()
            .migrate(request, new JSONObject());

        verify(details).setState(CAVEAT_RAISED);
        assertThat(result).isSameAs(request);
    }

    @Test
    void paAppCreatedRollbackShouldSetPaAppCreatedState() {
        CaveatCallbackRequest request = mock(CaveatCallbackRequest.class);
        CaveatDetails details = mock(CaveatDetails.class);
        when(request.getCaseDetails()).thenReturn(details);

        CaveatCallbackRequest result = new Dtspb5112PaAppCreatedCaveatRollback()
            .migrate(request, new JSONObject());

        verify(details).setState(PA_APP_CREATED);
        assertThat(result).isSameAs(request);
    }

    @Test
    void setExpiryRollbackShouldClearExpiryDate() {
        CaveatCallbackRequest request = mock(CaveatCallbackRequest.class);
        CaveatDetails details = mock(CaveatDetails.class);
        CaveatData data = mock(CaveatData.class);
        when(request.getCaseDetails()).thenReturn(details);
        when(details.getData()).thenReturn(data);

        CaveatCallbackRequest result = new Dtspb5112SetExpiryCaveatRollback()
            .migrate(request, new JSONObject());

        verify(data).setExpiryDate(null);
        assertThat(result).isSameAs(request);
    }

    @Test
    void closeExpiredMigrationShouldSetCaveatClosedState() {
        CaveatCallbackRequest request = mock(CaveatCallbackRequest.class);
        CaveatDetails details = mock(CaveatDetails.class);
        when(request.getCaseDetails()).thenReturn(details);

        CaveatCallbackRequest result = new Dtspb5112CloseExpiredCaveatMigration()
            .migrate(request, new JSONObject());

        verify(details).setState(CAVEAT_CLOSED);
        assertThat(result).isSameAs(request);
    }

    @Test
    void closeExpiredRollbackShouldRestoreOriginalState() {
        CaveatCallbackRequest request = mock(CaveatCallbackRequest.class);
        CaveatDetails details = mock(CaveatDetails.class);
        when(request.getCaseDetails()).thenReturn(details);
        JSONObject migrationData = new JSONObject()
            .put("originalState", Dtspb5112CaveatStates.AWAITING_WARNING_RESPONSE);

        CaveatCallbackRequest result = new Dtspb5112CloseExpiredCaveatRollback()
            .migrate(request, migrationData);

        verify(details).setState(Dtspb5112CaveatStates.AWAITING_WARNING_RESPONSE);
        assertThat(result).isSameAs(request);
    }

    @Test
    void closeRollbackShouldRejectMissingOriginalState() {
        CaveatCallbackRequest request = mock(CaveatCallbackRequest.class);
        CaveatDetails details = mock(CaveatDetails.class);
        when(request.getCaseDetails()).thenReturn(details);
        Dtspb5112CloseExpiredCaveatRollback handler = new Dtspb5112CloseExpiredCaveatRollback();
        JSONObject metadata = new JSONObject();

        assertThatThrownBy(() -> handler.migrate(request, metadata))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid DTSPB-5112 originalState: null");
    }

    @Test
    void closeRollbackShouldRejectInvalidOriginalState() {
        CaveatCallbackRequest request = mock(CaveatCallbackRequest.class);
        CaveatDetails details = mock(CaveatDetails.class);
        when(request.getCaseDetails()).thenReturn(details);

        JSONObject migrationData = new JSONObject()
                .put("originalState", PA_APP_CREATED);

        Dtspb5112CloseExpiredCaveatRollback handler =
                new Dtspb5112CloseExpiredCaveatRollback();

        assertThatThrownBy(() -> handler.migrate(request, migrationData))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid DTSPB-5112 originalState: PAAppCreated");
    }
}
