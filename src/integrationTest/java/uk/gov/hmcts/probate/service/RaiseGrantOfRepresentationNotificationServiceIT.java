package uk.gov.hmcts.probate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.SentEmail;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.service.docmosis.GrantOfRepresentationDocmosisMapperService;
import uk.gov.hmcts.probate.service.documentmanagement.DocumentManagementService;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.probate.validator.EmailAddressNotifyValidationRule;
import uk.gov.hmcts.reform.authorisation.generators.ServiceAuthTokenGenerator;
import uk.gov.hmcts.reform.probate.model.idam.UserInfo;
import uk.gov.hmcts.reform.sendletter.api.SendLetterResponse;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.SendEmailResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.DocumentType.GRANT_COVERSHEET;
import static uk.gov.hmcts.probate.model.DocumentType.GRANT_RAISED;
import static uk.gov.hmcts.probate.model.DocumentType.SENT_EMAIL;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class RaiseGrantOfRepresentationNotificationServiceIT {

    private static final Long ID = 1L;
    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private static final Long CASE_ID = 12345678987654321L;
    private static final String SENT_EMAIL_FILE_NAME = "sentEmail.pdf";
    private static final String COVERSHEET_FILE_NAME = "coversheet.pdf";
    private static final byte[] DOC_BYTES = {(byte) 23};
    private static final Optional<UserInfo> CASEWORKER_USERINFO = Optional.ofNullable(UserInfo.builder()
            .familyName("familyName")
            .givenName("givenname")
            .roles(Arrays.asList("caseworker-probate"))
            .build());

    @MockBean
    BulkPrintService bulkPrintService;
    @Autowired
    private RaiseGrantOfRepresentationNotificationService handleGrantReceivedNotification;
    @MockBean
    private SendEmailResponse sendEmailResponse;
    @MockBean
    private PDFManagementService pdfManagementService;
    @MockBean
    private GrantOfRepresentationDocmosisMapperService grantOfRepresentationDocmosisMapperService;
    @Mock
    private EventValidationService eventValidationService;
    @Mock
    private List<EmailAddressNotifyValidationRule> emailAddressNotifyValidationRules;
    @Mock
    private CallbackResponse callbackResponse;
    @Mock
    private DateFormatterService dateFormatterService;
    @MockBean
    private ServiceAuthTokenGenerator tokenGenerator;
    @MockBean
    private DocumentManagementService documentManagementService;
    @SpyBean
    private NotificationClient notificationClient;
    private CallbackRequest callbackRequest;

    @BeforeEach
    public void setUp() throws NotificationClientException, IOException {
        when(sendEmailResponse.getFromEmail()).thenReturn(Optional.of("emailResponseFrom@probate-test.com"));
        when(sendEmailResponse.getBody()).thenReturn("test-body");
        when(documentManagementService.getDocument(any())).thenReturn(DOC_BYTES);
        when(tokenGenerator.generate()).thenReturn("123");

        doReturn(sendEmailResponse).when(notificationClient).sendEmail(any(), any(), any(), any(), any());

        SendLetterResponse letterResponse = new SendLetterResponse(UUID.randomUUID());
        when(bulkPrintService.sendToBulkPrintForGrant(any(CallbackRequest.class), any(), any()))
            .thenReturn(letterResponse);
    }

    @Disabled
    @Test
    void shouldHandleGrantReceivedNotificationPersonalWithEmail()
        throws NotificationClientException, BadRequestException {

        CaseDetails caseDetails =
            new CaseDetails(CaseData.builder()
                .caseType("gop")
                .applicationType(ApplicationType.PERSONAL)
                .primaryApplicantEmailAddress("primary@probate-test.com")
                .registryLocation("Bristol")
                .build(),
                LAST_MODIFIED, CASE_ID);
        callbackRequest = new CallbackRequest(caseDetails);

        when(eventValidationService.validateEmailRequest(callbackRequest, emailAddressNotifyValidationRules))
            .thenReturn(callbackResponse);
        when(pdfManagementService.generateAndUpload(any(SentEmail.class), eq(SENT_EMAIL))).thenReturn(Document.builder()
            .documentFileName(SENT_EMAIL_FILE_NAME).documentType(GRANT_RAISED).build());

        CallbackResponse response = handleGrantReceivedNotification.handleGrantReceivedNotification(callbackRequest,
            CASEWORKER_USERINFO);
        assertEquals(1, response.getData().getProbateNotificationsGenerated().size());
        assertEquals(SENT_EMAIL_FILE_NAME,
            response.getData().getProbateNotificationsGenerated().get(0).getValue().getDocumentFileName());
        assertEquals(GRANT_RAISED,
            response.getData().getProbateNotificationsGenerated().get(0).getValue().getDocumentType());
    }

    @Disabled
    @Test
    void shouldHandleGrantReceivedNotificationPersonalWithoutEmail()
        throws NotificationClientException, BadRequestException {

        CaseDetails caseDetails =
            new CaseDetails(CaseData.builder()
                .caseType("gop")
                .applicationType(ApplicationType.PERSONAL)
                .registryLocation("Bristol")
                .build(),
                LAST_MODIFIED, CASE_ID);
        callbackRequest = new CallbackRequest(caseDetails);

        when(eventValidationService.validateEmailRequest(callbackRequest, emailAddressNotifyValidationRules))
            .thenReturn(callbackResponse);
        when(pdfManagementService.generateDocmosisDocumentAndUpload(any(Map.class), eq(GRANT_RAISED)))
            .thenReturn(Document.builder()
                .documentFileName(SENT_EMAIL_FILE_NAME).documentType(GRANT_RAISED).build());
        when(pdfManagementService.generateDocmosisDocumentAndUpload(any(Map.class), eq(GRANT_COVERSHEET)))
            .thenReturn(Document.builder()
                .documentFileName(COVERSHEET_FILE_NAME).documentType(GRANT_COVERSHEET).build());

        CallbackResponse response = handleGrantReceivedNotification.handleGrantReceivedNotification(callbackRequest,
            CASEWORKER_USERINFO);
        assertEquals(2, response.getData().getProbateNotificationsGenerated().size());
        assertEquals(COVERSHEET_FILE_NAME,
            response.getData().getProbateNotificationsGenerated().get(0).getValue().getDocumentFileName());
        assertEquals(SENT_EMAIL_FILE_NAME,
            response.getData().getProbateNotificationsGenerated().get(1).getValue().getDocumentFileName());
        assertEquals(GRANT_COVERSHEET,
            response.getData().getProbateNotificationsGenerated().get(0).getValue().getDocumentType());
        assertEquals(GRANT_RAISED,
            response.getData().getProbateNotificationsGenerated().get(1).getValue().getDocumentType());
    }

}
