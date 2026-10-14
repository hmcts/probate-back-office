package uk.gov.hmcts.probate.service.wa;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.probate.model.Constants;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.service.user.UserInfoService;
import uk.gov.hmcts.reform.probate.model.idam.UserInfo;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AttachScannedDocsTest {

    @Mock
    private UserInfoService userInfoService;

    @Mock
    private CallbackRequest callbackRequest;

    @InjectMocks
    private AttachScannedDocs processor;

    private static final String AUTH_TOKEN = "authToken";

    @Test
    void shouldReturnCorrectEventId() {
        assertThat(processor.getEventId()).isEqualTo("attachScannedDocs");
    }

    @Test
    void shouldSetCreateTaskToYesWhenUserHasSystemUpdateRole() {
        when(userInfoService.getCaseworkerInfo()).thenReturn(Optional.of(
                UserInfo.builder().roles(List.of("caseworker-probate-systemupdate")).build()));

        ResponseCaseData responseCaseData = ResponseCaseData.builder().build();
        processor.process(AUTH_TOKEN, callbackRequest, responseCaseData);

        assertThat(responseCaseData.getCreateTask()).isEqualTo(Constants.YES);
    }

    @Test
    void shouldSetCreateTaskToYesWhenUserHasIdamServiceAccountRole() {
        when(userInfoService.getCaseworkerInfo()).thenReturn(Optional.of(
                UserInfo.builder().roles(List.of("idam-service-account")).build()));

        ResponseCaseData responseCaseData = ResponseCaseData.builder().build();
        processor.process(AUTH_TOKEN, callbackRequest, responseCaseData);

        assertThat(responseCaseData.getCreateTask()).isEqualTo(Constants.YES);
    }

    @Test
    void shouldSetCreateTaskToNoWhenUserHasOtherRole() {
        when(userInfoService.getCaseworkerInfo()).thenReturn(Optional.of(
                UserInfo.builder().roles(List.of("caseworker-probate-caseadmin")).build()));

        ResponseCaseData responseCaseData = ResponseCaseData.builder().build();
        processor.process(AUTH_TOKEN, callbackRequest, responseCaseData);

        assertThat(responseCaseData.getCreateTask()).isEqualTo(Constants.NO);
    }

    @Test
    void shouldSetCreateTaskToNoWhenUserInfoIsEmpty() {
        when(userInfoService.getCaseworkerInfo()).thenReturn(Optional.empty());

        ResponseCaseData responseCaseData = ResponseCaseData.builder().build();
        processor.process(AUTH_TOKEN, callbackRequest, responseCaseData);

        assertThat(responseCaseData.getCreateTask()).isEqualTo(Constants.NO);
    }
}
