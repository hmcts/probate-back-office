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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class Dtspb5005GorRollbackTest {

    Dtspb5005GorRollback underTest;

    @BeforeEach
    void setUp() {
        underTest = new Dtspb5005GorRollback();
    }

    @Test
    void shouldNullApplicantOrganisationPolicy() {
        final CallbackRequest callbackRequest = mock();
        final CaseDetails caseDetails = mock();
        final CaseData caseData = mock();
        final Long caseId = 1L;

        final JSONObject migrationData = mock();

        when(callbackRequest.getCaseDetails())
                .thenReturn(caseDetails);

        when(caseDetails.getData())
                .thenReturn(caseData);
        when(caseDetails.getId())
                .thenReturn(caseId);

        final CallbackRequest result = underTest.migrate(callbackRequest, migrationData);

        assertAll(
                () -> verify(caseData).clearApplicantOrganisationPolicy(caseId),
                () -> assertThat(result, sameInstance(callbackRequest)));
    }
}
