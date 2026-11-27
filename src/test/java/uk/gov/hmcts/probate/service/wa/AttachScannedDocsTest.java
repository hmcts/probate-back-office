package uk.gov.hmcts.probate.service.wa;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.service.user.UserInfoService;
import uk.gov.hmcts.reform.probate.model.idam.UserInfo;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;

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

    @InjectMocks
    private AttachScannedDocs processor;

    private static final String AUTH_TOKEN = "authToken";

    @Test
    void shouldReturnCorrectEventId() {
        assertThat(processor.getEventId()).isEqualTo("attachScannedDocs");
    }

    @Test
    void shouldSetCreateTaskToYesWhenUserHasSystemUpdateRole() {
        when(caseDetails.getId())
                .thenReturn(1234L);

        when(callbackRequest.getCaseDetails())
                .thenReturn(caseDetails);
        when(caseDetails.getId())
                .thenReturn(1234L);

        when(callbackRequest.getCaseDetails())
                .thenReturn(caseDetails);
        when(caseDetails.getData())
                .thenReturn(CaseData.builder().build());
        when(userInfoService.getCaseworkerInfoIncludingSystemUsers()).thenReturn(Optional.of(
                UserInfo.builder().roles(List.of("caseworker-probate-systemupdate")).build()));

        ResponseCaseData responseCaseData = ResponseCaseData.builder().build();

        doAnswer(invocation -> {
            ResponseCaseData response = invocation.getArgument(2);
            response.setCreateTask(YES);
            return null;
        })
                .when(waTaskService).createTaskIfRequired(processor.getEventId(), callbackRequest, responseCaseData);

        processor.process(AUTH_TOKEN, callbackRequest, responseCaseData, null);

        assertThat(responseCaseData.getCreateTask()).isEqualTo(YES);
        verify(waTaskService).createTaskIfRequired(processor.getEventId(),
                callbackRequest,
                responseCaseData);
    }

    @Test
    void shouldSetCreateTaskToYesWhenUserHasIdamServiceAccountRole() {
        when(caseDetails.getId())
                .thenReturn(1234L);

        when(callbackRequest.getCaseDetails())
                .thenReturn(caseDetails);
        when(caseDetails.getData())
                .thenReturn(CaseData.builder().build());

        when(userInfoService.getCaseworkerInfoIncludingSystemUsers()).thenReturn(Optional.of(
                UserInfo.builder().roles(List.of("idam-service-account")).build()));

        ResponseCaseData responseCaseData = ResponseCaseData.builder().build();
        doAnswer(invocation -> {
            ResponseCaseData response = invocation.getArgument(2);
            response.setCreateTask(YES);
            return null;
        })
                .when(waTaskService).createTaskIfRequired(processor.getEventId(), callbackRequest, responseCaseData);

        processor.process(AUTH_TOKEN, callbackRequest, responseCaseData, null);

        assertThat(responseCaseData.getCreateTask()).isEqualTo(YES);
        verify(waTaskService).createTaskIfRequired(processor.getEventId(),
                callbackRequest,
                responseCaseData);
    }

    @Test
    void shouldSetCreateTaskToNoWhenTaskIsPresent() {
        when(caseDetails.getId())
                .thenReturn(1234L);

        when(callbackRequest.getCaseDetails())
                .thenReturn(caseDetails);
        when(caseDetails.getData())
                .thenReturn(CaseData.builder().build());

        when(userInfoService.getCaseworkerInfoIncludingSystemUsers()).thenReturn(Optional.of(
                UserInfo.builder().roles(List.of("idam-service-account")).build()));

        ResponseCaseData responseCaseData = ResponseCaseData.builder().build();
        doAnswer(invocation -> {
            ResponseCaseData response = invocation.getArgument(2);
            response.setCreateTask(NO);
            return null;
        })
                .when(waTaskService).createTaskIfRequired(processor.getEventId(), callbackRequest, responseCaseData);

        processor.process(AUTH_TOKEN, callbackRequest, responseCaseData, null);

        assertThat(responseCaseData.getCreateTask()).isEqualTo(NO);
        verify(waTaskService).createTaskIfRequired(processor.getEventId(),
                callbackRequest,
                responseCaseData);
    }

    @Test
    void shouldSetCreateTaskToNoWhenUserHasOtherRole() {
        when(caseDetails.getId())
                .thenReturn(1234L);
        when(callbackRequest.getCaseDetails())
                .thenReturn(caseDetails);
        when(userInfoService.getCaseworkerInfoIncludingSystemUsers()).thenReturn(Optional.of(
                UserInfo.builder().roles(List.of("caseworker-probate-caseadmin")).build()));

        ResponseCaseData responseCaseData = ResponseCaseData.builder().build();
        processor.process(AUTH_TOKEN, callbackRequest, responseCaseData, null);

        assertThat(responseCaseData.getCreateTask()).isEqualTo(NO);
        verifyNoInteractions(waTaskService);
    }

    @Test
    void shouldSetCreateTaskToNoWhenUserInfoIsEmpty() {
        when(userInfoService.getCaseworkerInfoIncludingSystemUsers()).thenReturn(Optional.empty());

        ResponseCaseData responseCaseData = ResponseCaseData.builder().build();
        processor.process(AUTH_TOKEN, callbackRequest, responseCaseData, null);

        assertThat(responseCaseData.getCreateTask()).isEqualTo(NO);
    }
}
