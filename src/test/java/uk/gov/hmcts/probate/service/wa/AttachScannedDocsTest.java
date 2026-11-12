package uk.gov.hmcts.probate.service.wa;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.probate.model.Constants;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.model.wa.TaskTypes;
import uk.gov.hmcts.probate.service.user.UserInfoService;
import uk.gov.hmcts.reform.probate.model.idam.UserInfo;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.StateConstants.STATE_BO_CASE_STOPPED;
import static uk.gov.hmcts.probate.model.StateConstants.STATE_BO_REDEC_NOTIFICATION_SENT;

@ExtendWith(MockitoExtension.class)
class AttachScannedDocsTest {

    @Mock
    private UserInfoService userInfoService;

    @Mock
    private CallbackRequest callbackRequest;

    @Mock
    private WaTaskService waTaskService;

    @Mock
    private CaseDetails caseDetails;

    @Mock
    private CaseData caseData;

    @InjectMocks
    private AttachScannedDocs processor;

    private static final String AUTH_TOKEN = "authToken";

    @Test
    void shouldReturnCorrectEventId() {
        assertThat(processor.getEventId()).isEqualTo("attachScannedDocs");
    }

    @Test
    void shouldSetCreateTaskToYesWhenUserHasSystemUpdateRoleOtherState() {
        when(userInfoService.getCaseworkerInfoIncludingSystemUsers()).thenReturn(Optional.of(
                UserInfo.builder().roles(List.of("caseworker-probate-systemupdate")).build()));
        when(callbackRequest.getCaseDetails()).thenReturn(caseDetails);
        when(caseDetails.getState()).thenReturn("OtherState");

        ResponseCaseData responseCaseData = ResponseCaseData.builder().build();
        processor.process(AUTH_TOKEN, callbackRequest, responseCaseData, null);

        assertThat(responseCaseData.getCreateTask()).isEqualTo(Constants.YES);
        verify(waTaskService, never()).setCreateTask(any(), any(), any());
    }

    @Test
    void shouldNotSetCreateTaskToYesWhenUserHasSystemUpdateRoleCaseStopped() {
        when(userInfoService.getCaseworkerInfoIncludingSystemUsers()).thenReturn(Optional.of(
                UserInfo.builder().roles(List.of("caseworker-probate-systemupdate")).build()));
        when(callbackRequest.getCaseDetails()).thenReturn(caseDetails);
        when(caseDetails.getState()).thenReturn(STATE_BO_CASE_STOPPED);

        ResponseCaseData responseCaseData = ResponseCaseData.builder().build();
        processor.process(AUTH_TOKEN, callbackRequest, responseCaseData, null);

        verify(waTaskService).setCreateTask(callbackRequest, responseCaseData, TaskTypes.RESOLVE_STOPPED_CASE);
    }

    @Test
    void shouldNotSetCreateTaskToYesWhenUserHasSystemUpdateRoleBORedecNotificationSent() {
        when(userInfoService.getCaseworkerInfoIncludingSystemUsers()).thenReturn(Optional.of(
                UserInfo.builder().roles(List.of("caseworker-probate-systemupdate")).build()));
        when(callbackRequest.getCaseDetails()).thenReturn(caseDetails);
        when(caseDetails.getState()).thenReturn(STATE_BO_REDEC_NOTIFICATION_SENT);

        ResponseCaseData responseCaseData = ResponseCaseData.builder().build();
        processor.process(AUTH_TOKEN, callbackRequest, responseCaseData, null);

        verify(waTaskService).setCreateTask(callbackRequest, responseCaseData, TaskTypes.RESOLVE_REDECLARATION);
    }

    @Test
    void shouldSetCreateTaskToYesWhenUserHasIdamServiceAccountRoleOtherState() {
        when(userInfoService.getCaseworkerInfoIncludingSystemUsers()).thenReturn(Optional.of(
                UserInfo.builder().roles(List.of("idam-service-account")).build()));
        when(callbackRequest.getCaseDetails()).thenReturn(caseDetails);
        when(caseDetails.getState()).thenReturn("OtherState");

        ResponseCaseData responseCaseData = ResponseCaseData.builder().build();
        processor.process(AUTH_TOKEN, callbackRequest, responseCaseData, null);

        assertThat(responseCaseData.getCreateTask()).isEqualTo(Constants.YES);
        verify(waTaskService, never()).setCreateTask(any(), any(), any());
    }

    @Test
    void shouldSetCreateTaskToYesWhenUserHasIdamServiceAccountRoleCaseStopped() {
        when(userInfoService.getCaseworkerInfoIncludingSystemUsers()).thenReturn(Optional.of(
                UserInfo.builder().roles(List.of("idam-service-account")).build()));
        when(callbackRequest.getCaseDetails()).thenReturn(caseDetails);
        when(caseDetails.getState()).thenReturn(STATE_BO_CASE_STOPPED);

        ResponseCaseData responseCaseData = ResponseCaseData.builder().build();
        processor.process(AUTH_TOKEN, callbackRequest, responseCaseData, null);

        verify(waTaskService).setCreateTask(callbackRequest, responseCaseData, TaskTypes.RESOLVE_STOPPED_CASE);
    }

    @Test
    void shouldSetCreateTaskToYesWhenUserHasIdamServiceAccountRoleBORedecNotificationSent() {
        when(userInfoService.getCaseworkerInfoIncludingSystemUsers()).thenReturn(Optional.of(
                UserInfo.builder().roles(List.of("idam-service-account")).build()));
        when(callbackRequest.getCaseDetails()).thenReturn(caseDetails);
        when(caseDetails.getState()).thenReturn(STATE_BO_REDEC_NOTIFICATION_SENT);

        ResponseCaseData responseCaseData = ResponseCaseData.builder().build();
        processor.process(AUTH_TOKEN, callbackRequest, responseCaseData, null);

        verify(waTaskService).setCreateTask(callbackRequest, responseCaseData, TaskTypes.RESOLVE_REDECLARATION);
    }

    @Test
    void shouldSetCreateTaskToNoWhenUserHasOtherRole() {
        when(userInfoService.getCaseworkerInfoIncludingSystemUsers()).thenReturn(Optional.of(
                UserInfo.builder().roles(List.of("caseworker-probate-caseadmin")).build()));

        ResponseCaseData responseCaseData = ResponseCaseData.builder().build();
        processor.process(AUTH_TOKEN, callbackRequest, responseCaseData, null);

        assertThat(responseCaseData.getCreateTask()).isEqualTo(Constants.NO);
    }

    @Test
    void shouldSetCreateTaskToNoWhenUserInfoIsEmpty() {
        when(userInfoService.getCaseworkerInfoIncludingSystemUsers()).thenReturn(Optional.empty());

        ResponseCaseData responseCaseData = ResponseCaseData.builder().build();
        processor.process(AUTH_TOKEN, callbackRequest, responseCaseData, null);

        assertThat(responseCaseData.getCreateTask()).isEqualTo(Constants.NO);
    }
}
