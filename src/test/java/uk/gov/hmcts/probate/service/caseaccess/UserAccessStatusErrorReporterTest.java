package uk.gov.hmcts.probate.service.caseaccess;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.model.ccd.raw.response.AfterSubmitCallbackResponse;
import uk.gov.hmcts.probate.service.ConfirmationResponseService;
import uk.gov.hmcts.probate.service.IdamApi;
import uk.gov.hmcts.probate.service.organisations.OrganisationsRetrievalService;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.service.caseaccess.UserAccessStatusErrorReporter.MESSAGE_PENDING;
import static uk.gov.hmcts.probate.service.caseaccess.UserAccessStatusErrorReporter.MESSAGE_SUSPENDED;
import static uk.gov.hmcts.probate.service.caseaccess.UserAccessStatusErrorReporter.SAC_ERROR_USER_ORG;

@ContextConfiguration(classes = {UserAccessStatusErrorReporter.class})
@ExtendWith(SpringExtension.class)
class UserAccessStatusErrorReporterTest {
    @MockBean
    private IdamApi idamApi;
    @MockBean
    private OrganisationsRetrievalService organisationsRetrievalService;
    @MockBean
    private ConfirmationResponseService confirmationResponseService;

    @Autowired
    private UserAccessStatusErrorReporter userAccessStatusErrorReporter;

    @Mock
    ResponseEntity<Map<String, Object>> userResponseMock;
    @Mock
    AfterSubmitCallbackResponse afterSubmitCallbackResponseMock;

    @Test
    void shouldReturnErrorForPendingStatus() {
        HashMap hm = new HashMap();
        hm.put("id", "someUserId");
        hm.put("email", "someUserEmail");
        when(userResponseMock.getBody()).thenReturn(hm);
        when(idamApi.getUserDetails("authorisationToken")).thenReturn(userResponseMock);
        when(organisationsRetrievalService.getUserAccountStatus("someUserEmail", "authorisationToken",
                "1234567890123456")).thenReturn("PENDING");

        when(confirmationResponseService
                .getCaseAccessErrorConfirmation("1234567890123456", "PENDING", MESSAGE_PENDING))
                .thenReturn(afterSubmitCallbackResponseMock);
        AfterSubmitCallbackResponse actualResponse = userAccessStatusErrorReporter
                .getAccessError(400, SAC_ERROR_USER_ORG, "authorisationToken", "1234567890123456", "caseType");

        assertEquals(afterSubmitCallbackResponseMock, actualResponse);
    }

    @Test
    void shouldReturnErrorForSuspendedStatus() {
        HashMap hm = new HashMap();
        hm.put("id", "someUserId");
        hm.put("email", "someUserEmail");
        when(userResponseMock.getBody()).thenReturn(hm);
        when(idamApi.getUserDetails("authorisationToken")).thenReturn(userResponseMock);
        when(organisationsRetrievalService.getUserAccountStatus("someUserEmail", "authorisationToken",
                "1234567890123456")).thenReturn("SUSPENDED");

        when(confirmationResponseService
                .getCaseAccessErrorConfirmation("1234567890123456", "SUSPENDED", MESSAGE_SUSPENDED))
                .thenReturn(afterSubmitCallbackResponseMock);
        AfterSubmitCallbackResponse actualResponse = userAccessStatusErrorReporter
                .getAccessError(400, SAC_ERROR_USER_ORG, "authorisationToken", "1234567890123456", "caseType");

        assertEquals(afterSubmitCallbackResponseMock, actualResponse);
    }

    @Test
    void shouldReturnNullErrorForNon400() {
        HashMap hm = new HashMap();
        hm.put("id", "someUserId");
        hm.put("email", "someUserEmail");
        when(userResponseMock.getBody()).thenReturn(hm);
        when(idamApi.getUserDetails("authorisationToken")).thenReturn(userResponseMock);
        when(organisationsRetrievalService.getUserAccountStatus("someUserEmail", "authorisationToken",
                "1234567890123456")).thenReturn("PENDING");

        when(confirmationResponseService
                .getCaseAccessErrorConfirmation("1234567890123456", "PENDING", MESSAGE_PENDING))
                .thenReturn(afterSubmitCallbackResponseMock);
        AfterSubmitCallbackResponse actualResponse = userAccessStatusErrorReporter
                .getAccessError(200, SAC_ERROR_USER_ORG, "authorisationToken", "1234567890123456", "caseType");

        assertNull(actualResponse);
    }

    @Test
    void shouldReturnNullError() {
        HashMap hm = new HashMap();
        hm.put("id", "someUserId");
        hm.put("email", "someUserEmail");
        when(userResponseMock.getBody()).thenReturn(hm);
        when(idamApi.getUserDetails("authorisationToken")).thenReturn(userResponseMock);
        when(organisationsRetrievalService.getUserAccountStatus("someUserEmail", "authorisationToken",
                "1234567890123456")).thenReturn("OTHER");

        when(confirmationResponseService
                .getCaseAccessErrorConfirmation("1234567890123456", "OTHER", MESSAGE_SUSPENDED))
                .thenReturn(afterSubmitCallbackResponseMock);
        AfterSubmitCallbackResponse actualResponse = userAccessStatusErrorReporter
                .getAccessError(400, SAC_ERROR_USER_ORG, "authorisationToken", "1234567890123456", "caseType");

        assertNull(actualResponse);
    }
}
