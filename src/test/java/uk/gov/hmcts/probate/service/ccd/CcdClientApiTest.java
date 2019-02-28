package uk.gov.hmcts.probate.service.ccd;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.probate.model.ccd.CcdCaseType;
import uk.gov.hmcts.probate.model.ccd.EventId;
import uk.gov.hmcts.probate.model.ccd.JurisdictionId;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;
import uk.gov.hmcts.reform.ccd.client.model.CaseDataContent;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.ccd.client.model.StartEventResponse;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CcdClientApiTest {

    private static final String AUTHORISATION = "XXJDHFHF";
    private static final String SERVICE_AUTHORISATION = "uifhuhfsd";
    private static final String USER_ID = "33";
    private static final String EVENT_ID = "EVENTID324234";

    @Mock
    private CoreCaseDataApi coreCaseDataApi;

    @InjectMocks
    private CcdClientApi ccdClientApi;

    @Test
    public void shouldCreateCase() {
        CcdCaseType ccdCaseType = CcdCaseType.GRANT_OF_REPRESENTATION;
        CaseData caseData = CaseData.builder().build();
        EventId eventId = EventId.IMPORT_GOR_CASE;
        SecurityDTO securityDTO = SecurityDTO.builder()
            .authorisation(AUTHORISATION)
            .serviceAuthorisation(SERVICE_AUTHORISATION)
            .userId(USER_ID)
            .build();
        StartEventResponse startEventResponse = StartEventResponse.builder()
            .eventId(EVENT_ID)
            .build();
        CaseDetails caseDetails = CaseDetails.builder().build();

        when(coreCaseDataApi.startForCaseworker(
            AUTHORISATION,
            SERVICE_AUTHORISATION,
            USER_ID,
            JurisdictionId.PROBATE.name(),
            ccdCaseType.getName(),
            eventId.getName())).thenReturn(startEventResponse);

        when(coreCaseDataApi.submitForCaseworker(
            eq(AUTHORISATION),
            eq(SERVICE_AUTHORISATION),
            eq(USER_ID),
            eq(JurisdictionId.PROBATE.name()),
            eq(ccdCaseType.getName()),
            eq(false),
            any(CaseDataContent.class))).thenReturn(caseDetails);

        CaseDetails actualCaseDetails = ccdClientApi.createCase(caseData, ccdCaseType, eventId, securityDTO);

        assertThat(actualCaseDetails, equalTo(caseDetails));


        verify(coreCaseDataApi, times(1)).startForCaseworker(
            AUTHORISATION,
            SERVICE_AUTHORISATION,
            USER_ID,
            JurisdictionId.PROBATE.name(),
            ccdCaseType.getName(),
            eventId.getName());

        verify(coreCaseDataApi, times(1)).submitForCaseworker(
            eq(AUTHORISATION),
            eq(SERVICE_AUTHORISATION),
            eq(USER_ID),
            eq(JurisdictionId.PROBATE.name()),
            eq(ccdCaseType.getName()),
            eq(false),
            any(CaseDataContent.class));
    }
}