package uk.gov.hmcts.probate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.model.DocumentIssueType;
import uk.gov.hmcts.probate.model.DocumentStatus;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.GrantScheduleResponse;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.service.BulkPrintService;
import uk.gov.hmcts.probate.service.DocumentGeneratorService;
import uk.gov.hmcts.probate.service.DocumentService;
import uk.gov.hmcts.probate.service.EventValidationService;
import uk.gov.hmcts.probate.service.EvidenceUploadService;
import uk.gov.hmcts.probate.service.FeatureToggleService;
import uk.gov.hmcts.probate.service.GrantNotificationService;
import uk.gov.hmcts.probate.service.InformationRequestService;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.hmcts.probate.service.RedeclarationNotificationService;
import uk.gov.hmcts.probate.service.docmosis.GrantOfRepresentationDocmosisMapperService;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.probate.service.user.UserInfoService;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.hmcts.probate.transformer.CaseDataTransformer;
import uk.gov.hmcts.probate.util.TestUtils;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;
import uk.gov.hmcts.reform.probate.model.idam.UserInfo;
import uk.gov.hmcts.reform.sendletter.api.SendLetterResponse;
import uk.gov.service.notify.NotificationClientException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.probate.model.DocumentType.ADMON_WILL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.ADMON_WILL_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.CAVEAT_STOPPED;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.EDGE_CASE;
import static uk.gov.hmcts.probate.model.DocumentType.GRANT_COVER;
import static uk.gov.hmcts.probate.model.DocumentType.GRANT_COVERSHEET;
import static uk.gov.hmcts.probate.model.DocumentType.GRANT_RAISED;
import static uk.gov.hmcts.probate.model.DocumentType.INTESTACY_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.INTESTACY_GRANT_DRAFT;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class NotificationControllerIT {
    private static final String DOC_RECEIVED_URL = "/notify/documents-received";
    private static final String CASE_STOPPED_URL = "/notify/case-stopped";
    private static final String REDECLARATION_SOT_DEFAULT_URL = "/notify/redeclaration-sot-default-values";
    private static final String REQUEST_INFO_DEFAULT_URL = "/notify/request-information-default-values";
    private static final String REQUEST_INFO_URL = "/notify/stopped-information-request";
    private static final String REDECLARATION_SOT = "/notify/redeclaration-sot";
    private static final String RAISE_GRANT = "/notify/grant-received";
    private static final String APPLICATION_RECEIVED_URL = "/notify/application-received";
    private static final String GRANT_DELAYED = "/notify/grant-delayed-scheduled?date=aDate";
    private static final String GRANT_AWAITING_DOCS = "/notify/grant-awaiting-documents-scheduled?date=aDate";
    private static final String START_GRANT_DELAYED_NOTIFICATION_DATE = "/notify/start-grant-delayed-notify-period";
    private static final String REQUEST_INFO_EMAIL_PREVIEW_URL = "/notify/information-request-email-preview";
    private static final Map<String, Object> EMPTY_MAP = new HashMap();
    private static final Document EMPTY_DOC = Document.builder().documentType(CAVEAT_STOPPED).build();
    private static final String AUTH_HEADER = "Authorization";
    private static final String AUTH_TOKEN = "Bearer someAuthorizationToken";
    private static final Optional<UserInfo> CASEWORKER_USERINFO = Optional.ofNullable(UserInfo.builder()
            .familyName("familyName")
            .givenName("givenname")
            .roles(Arrays.asList("caseworker-probate"))
            .build());
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestUtils testUtils;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private PDFManagementService pdfManagementService;

    @MockBean
    private GrantOfRepresentationDocmosisMapperService grantOfRepresentationDocmosisMapperService;

    @MockBean
    private BulkPrintService bulkPrintService;

    private EventValidationService eventValidationService;

    @MockBean
    private CoreCaseDataApi coreCaseDataApi;

    @MockBean
    private CallbackResponseTransformer callbackResponseTransformer;

    @MockBean
    private DocumentGeneratorService documentGeneratorService;

    @MockBean
    private InformationRequestService informationRequestService;

    @MockBean
    private RedeclarationNotificationService redeclarationNotificationService;

    @MockBean
    private GrantNotificationService grantNotificationService;

    @MockBean
    private FeatureToggleService featureToggleService;
    @MockBean
    CaseDataTransformer caseDataTransformer;

    @MockBean
    private EvidenceUploadService evidenceUploadService;

    @MockBean
    private UserInfoService userInfoService;

    @SpyBean
    private DocumentService documentService;
    private List<String> errors = new ArrayList<>();
    private CallbackResponse errorResponse;
    private CallbackResponse successfulResponse;

    @BeforeEach
    public void setUp() throws NotificationClientException, BadRequestException {
        errors.add("Bulk Print is currently unavailable please contact support desk.");
        errorResponse = CallbackResponse.builder().errors(errors).build();
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        successfulResponse =
            CallbackResponse.builder().data(ResponseCaseData.builder().deceasedForenames("Bob").build()).build();
        List<Document> docList = new ArrayList<>();
        docList.add(EMPTY_DOC);

        Document document = Document.builder()
            .documentDateAdded(LocalDate.now())
            .documentFileName("fileName")
            .documentGeneratedBy("generatedBy")
            .documentLink(
                DocumentLink.builder().documentUrl("url").documentFilename("file").documentBinaryUrl("binary").build())
            .documentType(DocumentType.DIGITAL_GRANT)
            .build();

        doReturn(document).when(notificationService).sendEmail(any(), any());

        when(pdfManagementService.generateAndUpload(any(CallbackRequest.class), eq(DIGITAL_GRANT_DRAFT)))
            .thenReturn(Document.builder().documentType(DIGITAL_GRANT_DRAFT).build());
        when(pdfManagementService.generateAndUpload(any(CallbackRequest.class), eq(DIGITAL_GRANT)))
            .thenReturn(Document.builder().documentType(DIGITAL_GRANT).build());

        when(pdfManagementService.generateAndUpload(any(CallbackRequest.class), eq(ADMON_WILL_GRANT_DRAFT)))
            .thenReturn(Document.builder().documentType(ADMON_WILL_GRANT_DRAFT).build());
        when(pdfManagementService.generateAndUpload(any(CallbackRequest.class), eq(ADMON_WILL_GRANT)))
            .thenReturn(Document.builder().documentType(ADMON_WILL_GRANT).build());

        when(pdfManagementService.generateAndUpload(any(CallbackRequest.class), eq(INTESTACY_GRANT_DRAFT)))
            .thenReturn(Document.builder().documentType(INTESTACY_GRANT_DRAFT).build());
        when(pdfManagementService.generateAndUpload(any(CallbackRequest.class), eq(INTESTACY_GRANT)))
            .thenReturn(Document.builder().documentType(INTESTACY_GRANT).build());

        when(pdfManagementService.generateAndUpload(any(CallbackRequest.class), eq(GRANT_COVER)))
            .thenReturn(Document.builder().documentType(GRANT_COVER).build());

        when(pdfManagementService.generateDocmosisDocumentAndUpload(any(Map.class), eq(GRANT_COVERSHEET)))
            .thenReturn(Document.builder().documentType(GRANT_COVERSHEET).build());

        when(pdfManagementService.generateAndUpload(any(CallbackRequest.class), eq(EDGE_CASE)))
            .thenReturn(Document.builder().documentType(EDGE_CASE).build());

        when(grantOfRepresentationDocmosisMapperService.caseDataForStoppedMatchedCaveat(any())).thenReturn(EMPTY_MAP);

        when(pdfManagementService.generateDocmosisDocumentAndUpload(any(), eq(CAVEAT_STOPPED))).thenReturn(EMPTY_DOC);

        when(documentGeneratorService.generateCoversheet(any())).thenReturn(EMPTY_DOC);

        when(callbackResponseTransformer.addDocuments(any(), any(), any(), any(), any()))
            .thenReturn(successfulResponse);
        when(callbackResponseTransformer.addNocDocuments(any(), any(), any())).thenReturn(successfulResponse);
        when(callbackResponseTransformer.caseStopped(any(), any(), any(), any())).thenReturn(successfulResponse);
        when(callbackResponseTransformer.defaultRedeclarationSOTValues(any())).thenReturn(successfulResponse);
        when(callbackResponseTransformer.defaultRequestInformationValues(any())).thenReturn(successfulResponse);
        when(callbackResponseTransformer.addInformationRequestDocuments(any(), eq(docList), any()))
            .thenReturn(successfulResponse);
        when(callbackResponseTransformer.addInformationRequestDocuments(any(), eq(new ArrayList<>()), any()))
            .thenReturn(successfulResponse);
        when(callbackResponseTransformer.addDocumentPreview(any(), any())).thenReturn(successfulResponse);
        when(callbackResponseTransformer.grantRaised(any(), any(), any(), any())).thenReturn(successfulResponse);
        when(informationRequestService.handleInformationRequest(any(), any())).thenReturn(successfulResponse);
        when(informationRequestService.emailPreview(any())).thenReturn(document);

        when(informationRequestService.handleInformationRequest(any(), any())).thenReturn(successfulResponse);

        when(redeclarationNotificationService.handleRedeclarationNotification(any(), any()))
            .thenReturn(successfulResponse);

        when(featureToggleService.isFeatureToggleOn("probate-documents-received-notification", false))
            .thenReturn(true);
        doReturn(CASEWORKER_USERINFO).when(userInfoService).getCaseworkerInfo();
    }

    @Test
    void solicitorDocumentsReceivedShouldReturnDataPayloadOkResponseCode() throws Exception {

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotifications.json");

        mockMvc.perform(post("/notify/documents-received")
            .header(AUTH_HEADER, AUTH_TOKEN)
            .content(solicitorPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("data")));
    }

    @Test
    void personalDocumentsReceivedShouldReturnDataPayloadOkResponseCode() throws Exception {

        String solicitorPayload = testUtils.getStringFromFile("personalPayloadNotifications.json");

        mockMvc.perform(post("/notify/documents-received")
            .header(AUTH_HEADER, AUTH_TOKEN)
            .content(solicitorPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("data")));
    }

    @Test
    void personalApplicationReceivedShouldReturnDataPayloadOkResponseCode() throws Exception {

        String solicitorPayload = testUtils.getStringFromFile("personalPayloadNotifications.json");

        mockMvc.perform(post(APPLICATION_RECEIVED_URL)
            .content(solicitorPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("generatedBy")));
    }

    @Test
    void personalApplicationReceivedForPaperShouldReturnDataEmptyResponse() throws Exception {

        String solicitorPayload = testUtils.getStringFromFile("personalPayloadNotificationsPaper.json");

        mockMvc.perform(post(APPLICATION_RECEIVED_URL)
            .content(solicitorPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(""));
    }

    @Test
    void solicitorGrantIssuedShouldReturnDataPayloadOkResponseCode() throws Exception {
        when(documentGeneratorService
            .getDocument(any(CallbackRequest.class), eq(DocumentStatus.FINAL), eq(DocumentIssueType.GRANT)))
            .thenReturn(Document.builder().documentType(DIGITAL_GRANT).build());

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotifications.json");

        mockMvc.perform(post("/document/generate-grant")
            .header(AUTH_HEADER, AUTH_TOKEN)
            .content(solicitorPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("data")));

        verify(documentGeneratorService)
            .getDocument(any(CallbackRequest.class), eq(DocumentStatus.FINAL), eq(DocumentIssueType.GRANT));
    }

    @Test
    void solicitorAdmonWillGrantIssuedShouldReturnDataPayloadOkResponseCode() throws Exception {
        when(documentGeneratorService
            .getDocument(any(CallbackRequest.class), eq(DocumentStatus.FINAL), eq(DocumentIssueType.GRANT)))
            .thenReturn(Document.builder().documentType(DIGITAL_GRANT).build());

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotificationsAdmonWill.json");

        mockMvc.perform(post("/document/generate-grant")
            .header(AUTH_HEADER, AUTH_TOKEN)
            .content(solicitorPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("data")));

        verify(documentGeneratorService)
            .getDocument(any(CallbackRequest.class), eq(DocumentStatus.FINAL), eq(DocumentIssueType.GRANT));
    }

    @Test
    void solicitorIntestacyGrantIssuedShouldReturnDataPayloadOkResponseCode() throws Exception {
        when(documentGeneratorService
            .getDocument(any(CallbackRequest.class), eq(DocumentStatus.FINAL), eq(DocumentIssueType.GRANT)))
            .thenReturn(Document.builder().documentType(DIGITAL_GRANT).build());

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotificationsIntestacy.json");

        mockMvc.perform(post("/document/generate-grant")
            .header(AUTH_HEADER, AUTH_TOKEN)
            .content(solicitorPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("data")));

        verify(documentGeneratorService)
            .getDocument(any(CallbackRequest.class), eq(DocumentStatus.FINAL), eq(DocumentIssueType.GRANT));
    }

    @Test
    void solicitorEdgeCaseGrantIssuedShouldReturnDataPayloadOkResponseCode() throws Exception {
        when(documentGeneratorService
            .getDocument(any(CallbackRequest.class), eq(DocumentStatus.FINAL), eq(DocumentIssueType.GRANT)))
            .thenReturn(Document.builder().documentType(DIGITAL_GRANT).build());

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotificationsEdgeCase.json");

        mockMvc.perform(post("/document/generate-grant")
            .header(AUTH_HEADER, AUTH_TOKEN)
            .content(solicitorPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("data")));

        verify(documentGeneratorService)
            .getDocument(any(CallbackRequest.class), eq(DocumentStatus.FINAL), eq(DocumentIssueType.GRANT));
    }

    @Test
    void personalGrantIssuedShouldReturnDataPayloadOkResponseCode() throws Exception {
        when(documentGeneratorService
            .getDocument(any(CallbackRequest.class), eq(DocumentStatus.FINAL), eq(DocumentIssueType.GRANT)))
            .thenReturn(Document.builder().documentType(DIGITAL_GRANT).build());

        String solicitorPayload = testUtils.getStringFromFile("personalPayloadNotifications.json");

        mockMvc.perform(post("/document/generate-grant")
            .header(AUTH_HEADER, AUTH_TOKEN)
            .content(solicitorPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("data")));

        verify(documentGeneratorService)
            .getDocument(any(CallbackRequest.class), eq(DocumentStatus.FINAL), eq(DocumentIssueType.GRANT));
    }

    @Test
    void solicitorCaseStoppedShouldReturnDataPayloadOkResponseCode() throws Exception {

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotifications.json");

        mockMvc.perform(post(CASE_STOPPED_URL)
            .header(AUTH_HEADER, AUTH_TOKEN)
            .content(solicitorPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("data")));
    }

    @Test
    void personalCaseStoppedShouldReturnDataPayloadOkResponseCode() throws Exception {

        String solicitorPayload = testUtils.getStringFromFile("personalPayloadNotifications.json");

        mockMvc.perform(post(CASE_STOPPED_URL)
            .header(AUTH_HEADER, AUTH_TOKEN)
            .content(solicitorPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("data")));
    }

    @Test
    void caseStoppedWithNotificationsRequestedShouldReturnOk() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile("stopNotificationsRequestedPayload.json");

        mockMvc.perform(post(CASE_STOPPED_URL)
            .header(AUTH_HEADER, AUTH_TOKEN)
            .content(solicitorPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("data")));
    }

    @Test
    void caseStoppedWithNoEmailNotificationAndNoBulkPrintRequestedShouldReturnOk() throws
        Exception {
        String solicitorPayload =
            testUtils.getStringFromFile("stopNotificationNoEmailRequestedAndNoBulkPrintPayload.json");

        mockMvc.perform(post(CASE_STOPPED_URL)
            .header(AUTH_HEADER, AUTH_TOKEN)
            .content(solicitorPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("data")));
    }

    @Test
    void caseStoppedWithNoEmailNotificationRequestedShouldReturnBulkPrintError() throws Exception {
        when(bulkPrintService
            .sendToBulkPrintForGrant(any(CallbackRequest.class), any(Document.class),
                    any(Document.class))).thenReturn(null);
        String solicitorPayload = testUtils.getStringFromFile("stopNotificationNoEmailRequested.json");

        mockMvc.perform(post(CASE_STOPPED_URL)
            .header(AUTH_HEADER, AUTH_TOKEN)
            .content(solicitorPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.errors[0]")
                .value("Bulk Print is currently unavailable please contact support desk."))
            .andExpect(jsonPath("$.errors[1]")
                    .value("Nid yw Argraffu Swmp ar gael ar hyn o bryd, cysylltwch â'r ddesg gymorth."));
    }

    @Test
    void shouldReturnEmailSolsValidateSuccessful() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile("solicitorAdditionalExecutors.json");

        mockMvc.perform(post(DOC_RECEIVED_URL).header(AUTH_HEADER, AUTH_TOKEN)
                        .content(solicitorPayload).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldReturnEmailSolsValidateUnSuccessful() throws Exception {
        doReturn(true).when(featureToggleService)
            .isFeatureToggleOn("probate-documents-received-notification", false);

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNoEmail.json");

        mockMvc.perform(post(DOC_RECEIVED_URL).header(AUTH_HEADER, AUTH_TOKEN)
                        .content(solicitorPayload).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.errors[0]")
                .value("There is no email address for this solicitor. Add an email address or contact them by post."))
            .andExpect(jsonPath("$.errors[1]")
                        .value("Nid oes cyfeiriad e-bost ar gyfer y cyfreithiwr hwn. Ychwanegwch gyfeiriad "
                                + "e-bost neu cysylltwch â nhw drwy'r post."))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    }

    @Test
    void shouldReturnEmailPAValidateSuccessful() throws Exception {
        String personalPayload = testUtils.getStringFromFile("personalPayloadNotifications.json");

        mockMvc.perform(post(DOC_RECEIVED_URL).header(AUTH_HEADER, AUTH_TOKEN)
                        .content(personalPayload).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldReturnEmailPAValidateFromBulkScanSuccessful() throws Exception {
        String personalPayload = testUtils.getStringFromFile("personalPayloadNotificationsFromBulkScan.json");

        mockMvc.perform(post(DOC_RECEIVED_URL).header(AUTH_HEADER, AUTH_TOKEN)
                        .content(personalPayload).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldReturnEmailPAValidateUnSuccessful() throws Exception {

        doReturn(true).when(featureToggleService)
            .isFeatureToggleOn("probate-documents-received-notification", false);

        String personalPayload = testUtils.getStringFromFile("personalPayloadNotificationsNoEmail.json");

        mockMvc.perform(post(DOC_RECEIVED_URL).header(AUTH_HEADER, AUTH_TOKEN)
                        .content(personalPayload).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.errors[0]")
                .value("There is no email address for this applicant. Add an email address or contact "
                        + "them by post."))
            .andExpect(jsonPath("$.errors[1]")
                    .value("Nid oes cyfeiriad e-bost ar gyfer y ceisydd hwn. Ychwanegwch gyfeiriad "
                                + "e-bost neu cysylltwch â nhw drwy'r post."))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    }

    @Test
    void shouldReturnPAApplicantReceivedValidateUnSuccessfulCaseStopped() throws Exception {
        String personalPayload = testUtils.getStringFromFile("personalPayloadNotificationsNoEmail.json");

        mockMvc.perform(post(APPLICATION_RECEIVED_URL)
            .content(personalPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void shouldReturnEmailSolsValidateSuccessfulCaseStopped() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile("solicitorAdditionalExecutors.json");

        mockMvc.perform(post(CASE_STOPPED_URL).header(AUTH_HEADER, AUTH_TOKEN)
                        .content(solicitorPayload).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldReturnEmailSolsValidateUnSuccessfulCaseStopped() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNoEmail.json");
        SendLetterResponse sendLetterResponse = new SendLetterResponse(UUID.randomUUID());
        when(bulkPrintService
                .sendToBulkPrintForGrant(any(CallbackRequest.class), any(Document.class),
                        any(Document.class))).thenReturn(sendLetterResponse);

        mockMvc.perform(post(CASE_STOPPED_URL).header(AUTH_HEADER, AUTH_TOKEN)
                        .content(solicitorPayload).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(containsString("data")));
    }

    @Test
    void shouldReturnEmailPAValidateSuccessfulCaseStopped() throws Exception {
        String personalPayload = testUtils.getStringFromFile("personalPayloadNotifications.json");

        mockMvc.perform(post(CASE_STOPPED_URL).header(AUTH_HEADER, AUTH_TOKEN)
                        .content(personalPayload).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldReturnEmailPAValidateUnSuccessfulCaseStopped() throws Exception {
        String personalPayload = testUtils.getStringFromFile("personalPayloadNotificationsNoEmail.json");
        SendLetterResponse sendLetterResponse = new SendLetterResponse(UUID.randomUUID());
        when(bulkPrintService
                .sendToBulkPrintForGrant(any(CallbackRequest.class), any(Document.class),
                        any(Document.class))).thenReturn(sendLetterResponse);

        mockMvc.perform(post(CASE_STOPPED_URL).header(AUTH_HEADER, AUTH_TOKEN)
                        .content(personalPayload).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(containsString("data")));

    }

    @Test
    void shouldReturnSuccessfulForRequestInformationDefaultValues() throws Exception {
        String personalPayload = testUtils.getStringFromFile("personalPayloadNotifications.json");

        mockMvc.perform(post(REQUEST_INFO_DEFAULT_URL).content(personalPayload).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    }

    @Test
    void shouldReturnSuccessfulForRedeclarationSOTDefaultValues() throws Exception {
        String personalPayload = testUtils.getStringFromFile("personalPayloadNotifications.json");

        mockMvc.perform(post(REDECLARATION_SOT_DEFAULT_URL)
                .content(personalPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    }

    @Test
    void shouldReturnSuccessfulResponseForInformationRequest() throws Exception {
        String personalPayload = testUtils.getStringFromFile("personalPayloadNotifications.json");

        mockMvc.perform(post(REQUEST_INFO_URL).header(AUTH_HEADER, AUTH_TOKEN)
                        .content(personalPayload).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(containsString("data")));
    }

    @Test
    void shouldReturnSuccessfulResponseForInformationRequestEmailPreview() throws Exception {
        String personalPayload = testUtils.getStringFromFile("personalPayloadNotifications.json");

        mockMvc.perform(post(REQUEST_INFO_EMAIL_PREVIEW_URL).content(personalPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("data")));
    }

    @Test
    void shouldReturnSuccessfulResponseForRedeclarationSot() throws Exception {
        String personalPayload = testUtils.getStringFromFile("personalPayloadNotifications.json");

        mockMvc.perform(post(REDECLARATION_SOT).header(AUTH_HEADER, AUTH_TOKEN)
                        .content(personalPayload).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(containsString("data")));
    }

    @Test
    void shouldReturnSuccessfulResponseFoRaiseGrant() throws Exception {
        String personalPayload = testUtils.getStringFromFile("personalPayloadNotifications.json");
        Document raiseGrantDoc = Document.builder().documentType(GRANT_RAISED).build();
        doReturn(raiseGrantDoc).when(notificationService).sendEmail(any(), any());

        mockMvc.perform(post(RAISE_GRANT).header(AUTH_HEADER, AUTH_TOKEN)
                        .content(personalPayload).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(containsString("data")));
    }

    @Test
    void shouldReturnSuccessfulResponseFoRaiseGrantWithoutEmail() throws Exception {
        String personalPayload = testUtils.getStringFromFile("personalPayloadNotificationsNoEmail.json");
        Document raiseGrantDoc = Document.builder().documentType(GRANT_RAISED).build();
        doReturn(raiseGrantDoc).when(notificationService).sendEmail(any(), any());

        mockMvc.perform(post(RAISE_GRANT).header(AUTH_HEADER, AUTH_TOKEN)
                        .content(personalPayload).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(containsString("data")));
    }

    @Test
    void shouldReturnSuccessfulResponseForStartGrantDelayNotification() throws Exception {
        String personalPayload = testUtils.getStringFromFile("personalPayloadNotifications.json");
        when(callbackResponseTransformer
                .transformCaseForAttachScannedDocs(any(), any(), any())).thenReturn(successfulResponse);
        mockMvc.perform(post(START_GRANT_DELAYED_NOTIFICATION_DATE).header(AUTH_HEADER, AUTH_TOKEN)
            .content(personalPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(containsString("data")));
        verify(notificationService).startGrantDelayNotificationPeriod(any());
        verify(notificationService).resetAwaitingDocumentationNotificationDate(any());
        verify(caseDataTransformer).transformCaseDataForAttachDocuments(any());
        verify(caseDataTransformer).transformCaseDataForDocsReceivedNotificationSent(any());
        verify(evidenceUploadService).updateLastEvidenceAddedDate(any());
        verify(notificationService).sendEmail(any(), any());
    }

    @Test
    void shouldReturnSuccessfulResponseForGrantDelayed() throws Exception {
        GrantScheduleResponse response =
            GrantScheduleResponse.builder().scheduleResponseData(Arrays.asList("returnString")).build();
        when(grantNotificationService.handleGrantDelayedNotification("aDate")).thenReturn(response);
        mockMvc.perform(post(GRANT_DELAYED).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void shouldReturnSuccessfulResponseForGrantAwaitingDocs() throws Exception {
        GrantScheduleResponse response =
            GrantScheduleResponse.builder().scheduleResponseData(Arrays.asList("returnString")).build();
        when(grantNotificationService.handleAwaitingDocumentationNotification("aDate")).thenReturn(response);
        mockMvc.perform(post(GRANT_AWAITING_DOCS).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void shouldReturnSuccessfulResponseForNoc() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile("solicitorNocPayloadNotifications.json");

        mockMvc.perform(post("/notify/noc-notification")
                        .header(AUTH_HEADER, AUTH_TOKEN)
                        .content(solicitorPayload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")));
    }

    @Test
    void shouldReturnUnSuccessfulForNocEmail() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNoEmail.json");

        mockMvc.perform(post("/notify/noc-notification").header(AUTH_HEADER, AUTH_TOKEN)
                        .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors[0]")
                        .value("There is no email address for this solicitor. Add an email address or "
                                + "contact them by post."))
                .andExpect(jsonPath("$.errors[1]")
                        .value("Nid oes cyfeiriad e-bost ar gyfer y cyfreithiwr hwn. "
                                + "Ychwanegwch gyfeiriad e-bost neu cysylltwch â nhw drwy'r post."))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    }
}
