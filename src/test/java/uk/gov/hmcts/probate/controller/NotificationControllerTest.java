package uk.gov.hmcts.probate.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.insights.AppInsights;
import uk.gov.hmcts.probate.model.DocumentIssueType;
import uk.gov.hmcts.probate.model.DocumentStatus;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.service.BulkPrintService;
import uk.gov.hmcts.probate.service.DocumentGeneratorService;
import uk.gov.hmcts.probate.service.DocumentService;
import uk.gov.hmcts.probate.service.EventValidationService;
import uk.gov.hmcts.probate.service.InformationRequestService;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.hmcts.probate.service.RedeclarationNotificationService;
import uk.gov.hmcts.probate.service.docmosis.GrantOfRepresentationDocmosisMapperService;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.hmcts.probate.util.TestUtils;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;
import uk.gov.service.notify.NotificationClientException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class NotificationControllerTest {
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
    private AppInsights appInsights;

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

    @SpyBean
    private DocumentService documentService;

    private static final String DOC_RECEIVED_URL = "/notify/documents-received";
    private static final String CASE_STOPPED_URL = "/notify/case-stopped";
    private static final String REQUEST_INFO_DEFAULT_URL = "/notify/request-information-default-values";
    private static final String REQUEST_INFO_URL = "/notify/stopped-information-request";
    private static final String REDECLARATION_SOT = "/notify/redeclaration-sot";
    private static final String RAISE_GRANT = "/notify/grant-received";

    private static final Map<String, Object> EMPTY_MAP = new HashMap();
    private static final Document EMPTY_DOC = Document.builder().documentType(CAVEAT_STOPPED).build();

    private List<String> errors = new ArrayList<>();
    private CallbackResponse errorResponse;
    private CallbackResponse successfulResponse;

    @Before
    public void setUp() throws NotificationClientException, BadRequestException {
        errors.add("Bulk Print is currently unavailable please contact support desk.");
        errorResponse = CallbackResponse.builder().errors(errors).build();
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        successfulResponse =
                CallbackResponse.builder().data(ResponseCaseData.builder().deceasedForenames("Bob").build()).build();
        List<Document> docList = new ArrayList<>();
        docList.add(EMPTY_DOC);

        Document document = Document.builder().documentType(DIGITAL_GRANT).build();

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

        when(callbackResponseTransformer.addDocuments(any(), any(), any(), any())).thenReturn(successfulResponse);
        when(callbackResponseTransformer.caseStopped(any(), any(), any())).thenReturn(successfulResponse);
        when(callbackResponseTransformer.defaultRequestInformationValues(any())).thenReturn(successfulResponse);
        when(callbackResponseTransformer.addInformationRequestDocuments(any(), eq(docList), any())).thenReturn(successfulResponse);
        when(callbackResponseTransformer.addInformationRequestDocuments(any(), eq(new ArrayList<>()), any())).thenReturn(successfulResponse);
        when(callbackResponseTransformer.grantRaised(any(), any(), any())).thenReturn(successfulResponse);

        when(informationRequestService.handleInformationRequest(any())).thenReturn(successfulResponse);

        when(redeclarationNotificationService.handleRedeclarationNotification(any())).thenReturn(successfulResponse);

    }

    @Test
    public void solicitorDocumentsReceivedShouldReturnDataPayloadOkResponseCode() throws Exception {

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotifications.json");

        mockMvc.perform(post("/notify/documents-received")
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")));
    }

    @Test
    public void personalDocumentsReceivedShouldReturnDataPayloadOkResponseCode() throws Exception {

        String solicitorPayload = testUtils.getStringFromFile("personalPayloadNotifications.json");

        mockMvc.perform(post("/notify/documents-received")
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")));
    }

    @Test
    public void solicitorGrantIssuedShouldReturnDataPayloadOkResponseCode() throws Exception {
        when(documentGeneratorService.getDocument(any(CallbackRequest.class),  eq(DocumentStatus.FINAL), eq(DocumentIssueType.GRANT)))
                .thenReturn(Document.builder().documentType(DIGITAL_GRANT).build());

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotifications.json");

        mockMvc.perform(post("/document/generate-grant")
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")));

        verify(documentGeneratorService).getDocument(any(CallbackRequest.class), eq(DocumentStatus.FINAL), eq(DocumentIssueType.GRANT));
    }

    @Test
    public void solicitorAdmonWillGrantIssuedShouldReturnDataPayloadOkResponseCode() throws Exception {
        when(documentGeneratorService.getDocument(any(CallbackRequest.class),  eq(DocumentStatus.FINAL), eq(DocumentIssueType.GRANT)))
                .thenReturn(Document.builder().documentType(DIGITAL_GRANT).build());

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotificationsAdmonWill.json");

        mockMvc.perform(post("/document/generate-grant")
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")));

        verify(documentGeneratorService).getDocument(any(CallbackRequest.class), eq(DocumentStatus.FINAL), eq(DocumentIssueType.GRANT));
    }

    @Test
    public void solicitorIntestacyGrantIssuedShouldReturnDataPayloadOkResponseCode() throws Exception {
        when(documentGeneratorService.getDocument(any(CallbackRequest.class),  eq(DocumentStatus.FINAL), eq(DocumentIssueType.GRANT)))
                .thenReturn(Document.builder().documentType(DIGITAL_GRANT).build());

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotificationsIntestacy.json");

        mockMvc.perform(post("/document/generate-grant")
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")));

        verify(documentGeneratorService).getDocument(any(CallbackRequest.class), eq(DocumentStatus.FINAL), eq(DocumentIssueType.GRANT));
    }

    @Test
    public void solicitorEdgeCaseGrantIssuedShouldReturnDataPayloadOkResponseCode() throws Exception {
        when(documentGeneratorService.getDocument(any(CallbackRequest.class),  eq(DocumentStatus.FINAL), eq(DocumentIssueType.GRANT)))
                .thenReturn(Document.builder().documentType(DIGITAL_GRANT).build());

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotificationsEdgeCase.json");

        mockMvc.perform(post("/document/generate-grant")
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")));

        verify(documentGeneratorService).getDocument(any(CallbackRequest.class), eq(DocumentStatus.FINAL), eq(DocumentIssueType.GRANT));
    }

    @Test
    public void personalGrantIssuedShouldReturnDataPayloadOkResponseCode() throws Exception {
        when(documentGeneratorService.getDocument(any(CallbackRequest.class),  eq(DocumentStatus.FINAL), eq(DocumentIssueType.GRANT)))
                .thenReturn(Document.builder().documentType(DIGITAL_GRANT).build());

        String solicitorPayload = testUtils.getStringFromFile("personalPayloadNotifications.json");

        mockMvc.perform(post("/document/generate-grant")
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")));

        verify(documentGeneratorService).getDocument(any(CallbackRequest.class), eq(DocumentStatus.FINAL), eq(DocumentIssueType.GRANT));
    }

    @Test
    public void solicitorCaseStoppedShouldReturnDataPayloadOkResponseCode() throws Exception {

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotifications.json");

        mockMvc.perform(post(CASE_STOPPED_URL)
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")));
    }

    @Test
    public void personalCaseStoppedShouldReturnDataPayloadOkResponseCode() throws Exception {

        String solicitorPayload = testUtils.getStringFromFile("personalPayloadNotifications.json");

        mockMvc.perform(post(CASE_STOPPED_URL)
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")));
    }

    @Test
    public void caseStoppedWithNotificationsRequestedShouldReturnOk() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile("stopNotificationsRequestedPayload.json");

        mockMvc.perform(post(CASE_STOPPED_URL)
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")));
    }

    @Test
    public void caseStoppedWithNoEmailNotificationAndNoBulkPrintRequestedShouldReturnOk() throws
            Exception {
        String solicitorPayload = testUtils.getStringFromFile("stopNotificationNoEmailRequestedAndNoBulkPrintPayload.json");

        mockMvc.perform(post(CASE_STOPPED_URL)
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")));
    }

    @Test
    public void caseStoppedWithNoEmailNotificationRequestedShouldReturnBulkPrintError() throws Exception {
        when(bulkPrintService.sendToBulkPrint(any(CallbackRequest.class), eq(Document.builder().build()), eq(Document
                .builder().build()))).thenReturn(null);
        String solicitorPayload = testUtils.getStringFromFile("stopNotificationNoEmailRequested.json");

        mockMvc.perform(post(CASE_STOPPED_URL)
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors[0]")
                        .value("Bulk Print is currently unavailable please contact support desk."));
    }

    @Test
    public void shouldReturnEmailSolsValidateSuccessful() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile("solicitorAdditionalExecutors.json");

        mockMvc.perform(post(DOC_RECEIVED_URL).content(solicitorPayload).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
    }

    @Test
    public void shouldReturnEmailSolsValidateUnSuccessful() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNoEmail.json");

        mockMvc.perform(post(DOC_RECEIVED_URL).content(solicitorPayload).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors[0]")
                        .value("There is no email address for this solicitor. "
                                + "To continue the application, go back and select no to sending an email."))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));

    }

    @Test
    public void shouldReturnEmailPAValidateSuccessful() throws Exception {
        String personalPayload = testUtils.getStringFromFile("personalPayloadNotifications.json");

        mockMvc.perform(post(DOC_RECEIVED_URL).content(personalPayload).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
    }

    @Test
    public void shouldReturnEmailPAValidateFromBulkScanSuccessful() throws Exception {
        String personalPayload = testUtils.getStringFromFile("personalPayloadNotificationsFromBulkScan.json");

        mockMvc.perform(post(DOC_RECEIVED_URL).content(personalPayload).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
    }

    @Test
    public void shouldReturnEmailPAValidateUnSuccessful() throws Exception {
        String personalPayload = testUtils.getStringFromFile("personalPayloadNotificationsNoEmail.json");

        mockMvc.perform(post(DOC_RECEIVED_URL).content(personalPayload).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors[0]")
                        .value("There is no email address for this applicant. "
                                + "To continue the application, go back and select no to sending an email."))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));

    }

    @Test
    public void shouldReturnGrantPAValidateUnSuccessfulCaseStopped() throws Exception {
        String personalPayload = testUtils.getStringFromFile("personalPayloadNotificationsNoEmail.json");

        mockMvc.perform(post("/notify/documents-received")
                .content(personalPayload)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors[0]")
                        .value("There is no email address for this applicant. "
                                + "To continue the application, go back and select no to sending an email."))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));

    }

    @Test
    public void shouldReturnEmailSolsValidateSuccessfulCaseStopped() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile("solicitorAdditionalExecutors.json");

        mockMvc.perform(post(CASE_STOPPED_URL).content(solicitorPayload).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
    }

    @Test
    public void shouldReturnEmailSolsValidateUnSuccessfulCaseStopped() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNoEmail.json");

        mockMvc.perform(post(CASE_STOPPED_URL).content(solicitorPayload).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors[0]")
                        .value("There is no email address for this solicitor. Add an email address or contact them by post."))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));

    }

    @Test
    public void shouldReturnEmailPAValidateSuccessfulCaseStopped() throws Exception {
        String personalPayload = testUtils.getStringFromFile("personalPayloadNotifications.json");

        mockMvc.perform(post(CASE_STOPPED_URL).content(personalPayload).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
    }

    @Test
    public void shouldReturnEmailPAValidateUnSuccessfulCaseStopped() throws Exception {
        String personalPayload = testUtils.getStringFromFile("personalPayloadNotificationsNoEmail.json");

        mockMvc.perform(post(CASE_STOPPED_URL).content(personalPayload).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors[0]")
                        .value("There is no email address for this applicant. Add an email address or contact them by post."))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));

    }

    @Test
    public void shouldReturnSuccessfulForRequestInformationDefaultValues() throws Exception {
        String personalPayload = testUtils.getStringFromFile("personalPayloadNotifications.json");

        mockMvc.perform(post(REQUEST_INFO_DEFAULT_URL).content(personalPayload).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));

    }

    @Test
    public void shouldReturnSuccessfulResponseForInformationRequest() throws Exception {
        String personalPayload = testUtils.getStringFromFile("personalPayloadNotifications.json");

        mockMvc.perform(post(REQUEST_INFO_URL).content(personalPayload).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().string(containsString("data")));
    }

    @Test
    public void shouldReturnSuccessfulResponseForRedeclarationSot() throws Exception {
        String personalPayload = testUtils.getStringFromFile("personalPayloadNotifications.json");

        mockMvc.perform(post(REDECLARATION_SOT).content(personalPayload).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().string(containsString("data")));
    }

    @Test
    public void shouldReturnSuccessfulResponseFoRaiseGrant() throws Exception {
        String personalPayload = testUtils.getStringFromFile("personalPayloadNotifications.json");
        Document raiseGrantDoc = Document.builder().documentType(GRANT_RAISED).build();
        doReturn(raiseGrantDoc).when(notificationService).sendEmail(any(), any());

        mockMvc.perform(post(RAISE_GRANT).content(personalPayload).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().string(containsString("data")));
    }

    @Test
    public void shouldReturnSuccessfulResponseFoRaiseGrantWithoutEmail() throws Exception {
        String personalPayload = testUtils.getStringFromFile("personalPayloadNotificationsNoEmail.json");
        Document raiseGrantDoc = Document.builder().documentType(GRANT_RAISED).build();
        doReturn(raiseGrantDoc).when(notificationService).sendEmail(any(), any());

        mockMvc.perform(post(RAISE_GRANT).content(personalPayload).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().string(containsString("data")));
    }
}
