package uk.gov.hmcts.probate.service.migration;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class Dtspb5005CaveatRollbackTest {

    Dtspb5005CaveatRollback underTest;

    @BeforeEach
    void setUp() {
        underTest = new Dtspb5005CaveatRollback();
    }

    @Test
    void shouldNullApplicantOrganisationPolicy() {
        final CaveatCallbackRequest callbackRequest = mock();
        final CaveatDetails caveatDetails = mock();
        final CaveatData caveatData = mock();

        final JSONObject migrationData = mock();

        when(callbackRequest.getCaseDetails())
                .thenReturn(caveatDetails);

        when(caveatDetails.getData())
                .thenReturn(caveatData);

        final CaveatCallbackRequest result = underTest.migrate(callbackRequest, migrationData);

        assertAll(
                () -> verify(caveatData).setApplicantOrganisationPolicy(null),
                () -> assertThat(result, sameInstance(callbackRequest)));
    }
}
