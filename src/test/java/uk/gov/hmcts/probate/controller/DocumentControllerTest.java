package uk.gov.hmcts.probate.controller;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.hmcts.probate.insights.AppInsights;
import uk.gov.hmcts.probate.model.DocumentIssueType;
import uk.gov.hmcts.probate.model.DocumentStatus;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.State;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.model.ccd.willlodgement.request.WillLodgementCallbackRequest;
import uk.gov.hmcts.probate.service.BulkPrintService;
import uk.gov.hmcts.probate.service.DocumentGeneratorService;
import uk.gov.hmcts.probate.service.DocumentService;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.probate.util.TestUtils;
import uk.gov.hmcts.reform.sendletter.api.SendLetterResponse;
import uk.gov.service.notify.NotificationClientException;

import java.time.LocalDate;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.probate.model.DocumentType.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private TestUtils testUtils;

    @MockBean
    private PDFManagementService pdfManagementService;

    @MockBean
    private NotificationService notificationService;

    @SpyBean
    private DocumentService documentService;

    @MockBean
    private BulkPrintService bulkPrintService;

    @MockBean
    private DocumentGeneratorService documentGeneratorService;

    @MockBean
    private AppInsights appInsights;

    @Mock
    private SendLetterResponse sendLetterResponseMock;

    @Mock
    private ResponseCaseData.ResponseCaseDataBuilder responseCaseDataBuilder;

    private static final String LETTER_UUID = "c387262a-c8a6-44eb-9aea-a740460f9302";

    @Before
    public void setUp() throws NotificationClientException {
        final Document document = Document.builder()
                .documentType(DocumentType.DIGITAL_GRANT_REISSUE)
                .documentDateAdded(LocalDate.now())
                .documentFileName("test")
                .documentGeneratedBy("test")
                .documentLink(DocumentLink.builder().build())
                .build();

        final Document letter = Document.builder()
                .documentType(DocumentType.ASSEMBLED_LETTER)
                .documentDateAdded(LocalDate.now())
                .documentFileName("test")
                .documentGeneratedBy("test")
                .documentLink(DocumentLink.builder().build())
                .build();


        final Document welshDocumentDraft = Document.builder()
                .documentType(DocumentType.WELSH_DIGITAL_GRANT_DRAFT)
                .documentDateAdded(LocalDate.now())
                .documentFileName("DRAFT")
                .documentGeneratedBy("test")
                .documentLink(DocumentLink.builder().build())
                .build();

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();


        when(pdfManagementService.generateAndUpload(any(CallbackRequest.class), eq(DIGITAL_GRANT_DRAFT)))
                .thenReturn(Document.builder().documentType(DIGITAL_GRANT_DRAFT).build());

        when(pdfManagementService.generateAndUpload(any(CallbackRequest.class), eq(DIGITAL_GRANT)))
                .thenReturn(Document.builder().documentType(DIGITAL_GRANT).build());

        when(pdfManagementService.generateAndUpload(any(CallbackRequest.class), eq(INTESTACY_GRANT)))
                .thenReturn(Document.builder().documentType(INTESTACY_GRANT).build());




        when(pdfManagementService.generateAndUpload(any(CallbackRequest.class), eq(ADMON_WILL_GRANT_DRAFT)))
                .thenReturn(Document.builder().documentType(ADMON_WILL_GRANT_DRAFT).build());
        when(pdfManagementService.generateAndUpload(any(CallbackRequest.class), eq(ADMON_WILL_GRANT)))
                .thenReturn(Document.builder().documentType(ADMON_WILL_GRANT).build());
        when(pdfManagementService.generateAndUpload(any(CallbackRequest.class), eq(EDGE_CASE)))
                .thenReturn(Document.builder().documentType(EDGE_CASE).build());
        when(pdfManagementService.generateAndUpload(any(CallbackRequest.class), eq(EDGE_CASE)))
                .thenReturn(Document.builder().documentType(EDGE_CASE).build());
        when(pdfManagementService.generateAndUpload(any(CallbackRequest.class), eq(GRANT_COVER)))
                .thenReturn(Document.builder().documentType(GRANT_COVER).build());




        when(pdfManagementService.generateAndUpload(any(WillLodgementCallbackRequest.class), eq(WILL_LODGEMENT_DEPOSIT_RECEIPT)))
                .thenReturn(Document.builder().documentType(WILL_LODGEMENT_DEPOSIT_RECEIPT).build());

        when(notificationService.sendEmail(any(State.class), any(CaseDetails.class))).thenReturn(document);

        when(documentGeneratorService.generateGrantReissue(any(), any(), any())).thenReturn(document);
        when(documentGeneratorService.generateCoversheet(any(CallbackRequest.class)))
                .thenReturn(Document.builder().documentType(DocumentType.GRANT_COVERSHEET).build());
        when(documentGeneratorService.generateSoT(any()))
                .thenReturn(Document.builder().documentType(DocumentType.STATEMENT_OF_TRUTH).build());

        when(documentGeneratorService.generateLetter(any(CallbackRequest.class), eq(true))).thenReturn(letter);
        when(documentGeneratorService.generateLetter(any(CallbackRequest.class), eq(false))).thenReturn(letter);

        SendLetterResponse sendLetterResponse = new SendLetterResponse(UUID.randomUUID());
        when(bulkPrintService.sendToBulkPrint(any(CallbackRequest.class), any(Document.class),
                any(Document.class))).thenReturn(sendLetterResponse);

        when(bulkPrintService.sendToBulkPrint(any(CallbackRequest.class), any(Document.class),
                any(Document.class), eq(true))).thenReturn(LETTER_UUID);

        when(notificationService.generateGrantReissue(any(CallbackRequest.class)))
                .thenReturn(Document.builder().documentType(SENT_EMAIL).build());
        doNothing().when(documentService).expire(any(CallbackRequest.class), any(DocumentType.class));
        when(documentGeneratorService.getDocument(any(CallbackRequest.class), eq(DocumentStatus.FINAL), eq(DocumentIssueType.GRANT)))
               .thenReturn(Document.builder().documentType(WELSH_DIGITAL_GRANT).build());
        when(documentGeneratorService.getDocument(any(CallbackRequest.class), eq(DocumentStatus.PREVIEW), eq(DocumentIssueType.GRANT)))
                .thenReturn(welshDocumentDraft);
    }

    @Test
    public void generateGrantDraftGrantOfRepresentation() throws Exception {

        when(documentGeneratorService.getDocument(any(CallbackRequest.class),  eq(DocumentStatus.PREVIEW), eq(DocumentIssueType.GRANT)))
                .thenReturn(Document.builder().documentType(DIGITAL_GRANT_DRAFT).build());

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotifications.json");

        MvcResult result = mockMvc.perform(post("/document/generate-grant-draft")
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.probateDocumentsGenerated[0].value.DocumentType", is(DIGITAL_GRANT_DRAFT.getTemplateName())))
                .andReturn();

        verify(documentGeneratorService).getDocument(any(CallbackRequest.class), eq(DocumentStatus.PREVIEW), eq(DocumentIssueType.GRANT));
    }

    @Test
    public void generateDigitalGrant() throws Exception {
        when(documentGeneratorService.getDocument(any(CallbackRequest.class),  eq(DocumentStatus.FINAL), eq(DocumentIssueType.GRANT)))
                .thenReturn(Document.builder().documentType(DIGITAL_GRANT).build());

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotifications.json");

        MvcResult result = mockMvc.perform(post("/document/generate-grant")
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.probateDocumentsGenerated[1].value.DocumentType", is(DIGITAL_GRANT.getTemplateName())))
                .andReturn();

        verify(documentGeneratorService).getDocument(any(CallbackRequest.class), eq(DocumentStatus.FINAL), eq(DocumentIssueType.GRANT));
    }

    @Test
    public void generateDigitalGrantWithBulkPrint() throws Exception {
        when(documentGeneratorService.getDocument(any(CallbackRequest.class),  eq(DocumentStatus.FINAL), eq(DocumentIssueType.GRANT)))
                .thenReturn(Document.builder().documentType(DIGITAL_GRANT).build());

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotificationsBulkPrint.json");

        MvcResult result = mockMvc.perform(post("/document/generate-grant")
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.probateDocumentsGenerated[1].value.DocumentType", is(DIGITAL_GRANT.getTemplateName())))
                .andReturn();

        verify(bulkPrintService).sendToBulkPrint(any(CallbackRequest.class), any(Document.class), any(Document.class));
    }

    @Test
    public void generateDigitalGrantReissueWithBulkPrint() throws Exception {

        String solicitorPayload = testUtils.getStringFromFile("payloadWithBulkPrint.json");

        MvcResult result = mockMvc.perform(post("/document/generate-grant-reissue")
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.probateDocumentsGenerated[1].value.DocumentType", is(DIGITAL_GRANT_REISSUE.getTemplateName())))
                .andReturn();

        verify(bulkPrintService).sendToBulkPrint(any(CallbackRequest.class), any(Document.class), any(Document.class), eq(true));
    }

    @Test
    public void generateDigitalGrantIfLocalPrint() throws Exception {
        when(documentGeneratorService.getDocument(any(CallbackRequest.class),  eq(DocumentStatus.FINAL), eq(DocumentIssueType.GRANT)))
                .thenReturn(Document.builder().documentType(DIGITAL_GRANT).build());
        String solicitorPayload = testUtils.getStringFromFile("payloadWithLocalPrint.json");

        MvcResult result = mockMvc.perform(post("/document/generate-grant")
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.probateDocumentsGenerated[1].value.DocumentType", is(DIGITAL_GRANT.getTemplateName())))
                .andReturn();
        verify(notificationService).sendEmail(eq(State.GRANT_ISSUED), any(CaseDetails.class));
        verify(documentGeneratorService).getDocument(any(CallbackRequest.class), eq(DocumentStatus.FINAL), eq(DocumentIssueType.GRANT));
    }

    @Test
    public void generateDigitalGrantIntestacy() throws Exception {
        when(documentGeneratorService.getDocument(any(CallbackRequest.class),  eq(DocumentStatus.FINAL), eq(DocumentIssueType.GRANT)))
                .thenReturn(Document.builder().documentType(DIGITAL_GRANT).build());
        String solicitorPayload = testUtils.getStringFromFile("personalPayloadNotificationsGrantIntestacy.json");

        MvcResult result = mockMvc.perform(post("/document/generate-grant")
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        verify(notificationService).sendEmail(eq(State.GRANT_ISSUED_INTESTACY), any(CaseDetails.class));
        verify(documentGeneratorService).getDocument(any(CallbackRequest.class), eq(DocumentStatus.FINAL), eq(DocumentIssueType.GRANT));
    }

    @Test
    public void generateWelshDigitalGrant() throws Exception {

        String payload = testUtils.getStringFromFile("welshGrantOfProbatPayload.json");

        MvcResult result = mockMvc.perform(post("/document/generate-grant")
                .content(payload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.probateDocumentsGenerated[0].value.DocumentType", is(WELSH_DIGITAL_GRANT.getTemplateName())))
                .andReturn();

        verify(documentGeneratorService).getDocument(any(CallbackRequest.class), eq(DocumentStatus.FINAL), eq(DocumentIssueType.GRANT));
    }

    @Test
    public void generateWelshDigitalGrantDraft() throws Exception {

        String payload = testUtils.getStringFromFile("welshGrantOfProbatPayloadDraft.json");

         MvcResult result = mockMvc.perform(post("/document/generate-grant-draft")
            .content(payload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.probateDocumentsGenerated[0].value.DocumentType", is(WELSH_DIGITAL_GRANT_DRAFT.getTemplateName())))
            .andReturn();

        verify(documentGeneratorService).getDocument(any(CallbackRequest.class), eq(DocumentStatus.PREVIEW), eq(DocumentIssueType.GRANT));
    }

    @Test
    public void generateGrantDraftIntestacy() throws Exception {
        when(documentGeneratorService.getDocument(any(CallbackRequest.class),  eq(DocumentStatus.PREVIEW), eq(DocumentIssueType.GRANT)))
                .thenReturn(Document.builder().documentType(INTESTACY_GRANT_DRAFT).build());

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotificationsIntestacy.json");

        MvcResult result = mockMvc.perform(post("/document/generate-grant-draft")
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.probateDocumentsGenerated[1].value.DocumentType", is(INTESTACY_GRANT_DRAFT.getTemplateName())))
                .andReturn();

        verify(documentGeneratorService).getDocument(any(CallbackRequest.class), eq(DocumentStatus.PREVIEW), eq(DocumentIssueType.GRANT));
    }

    @Test
    public void generateGrantDraftAdmonWill() throws Exception {
        when(documentGeneratorService.getDocument(any(CallbackRequest.class),  eq(DocumentStatus.PREVIEW), eq(DocumentIssueType.GRANT)))
                .thenReturn(Document.builder().documentType(ADMON_WILL_GRANT_DRAFT).build());

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotificationsAdmonWill.json");

        MvcResult result = mockMvc.perform(post("/document/generate-grant-draft")
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.probateDocumentsGenerated[1].value.DocumentType", is(ADMON_WILL_GRANT_DRAFT.getTemplateName())))
                .andReturn();

        verify(documentGeneratorService).getDocument(any(CallbackRequest.class), eq(DocumentStatus.PREVIEW), eq(DocumentIssueType.GRANT));
    }

    @Test
    public void generateGrantAdmonWill() throws Exception {
        when(documentGeneratorService.getDocument(any(CallbackRequest.class),  eq(DocumentStatus.FINAL), eq(DocumentIssueType.GRANT)))
                .thenReturn(Document.builder().documentType(ADMON_WILL_GRANT).build());

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotificationsAdmonWill.json");

        MvcResult result = mockMvc.perform(post("/document/generate-grant")
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.probateDocumentsGenerated[1].value.DocumentType", is(ADMON_WILL_GRANT.getTemplateName())))
                .andReturn();

        verify(documentGeneratorService).getDocument(any(CallbackRequest.class), eq(DocumentStatus.FINAL), eq(DocumentIssueType.GRANT));
    }

    @Test
    public void generateGrantIntestacy() throws Exception {
        when(documentGeneratorService.getDocument(any(CallbackRequest.class),  eq(DocumentStatus.FINAL), eq(DocumentIssueType.GRANT)))
                .thenReturn(Document.builder().documentType(INTESTACY_GRANT).build());

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotificationsIntestacy.json");

        MvcResult result = mockMvc.perform(post("/document/generate-grant")
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.probateDocumentsGenerated[1].value.DocumentType", is(INTESTACY_GRANT.getTemplateName())))
                .andReturn();

        verify(documentGeneratorService).getDocument(any(CallbackRequest.class), eq(DocumentStatus.FINAL), eq(DocumentIssueType.GRANT));
    }

    @Test
    public void shouldNotGenerateGrantDraftEdgeCase() throws Exception {
        when(documentGeneratorService.getDocument(any(CallbackRequest.class),  eq(DocumentStatus.PREVIEW), eq(DocumentIssueType.GRANT)))
                .thenReturn(Document.builder().documentType(EDGE_CASE).build());

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotificationsEdgeCase.json");

        MvcResult result = mockMvc.perform(post("/document/generate-grant-draft")
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.probateDocumentsGenerated", Matchers.empty()))
                .andReturn();

        verify(documentGeneratorService).getDocument(any(CallbackRequest.class), eq(DocumentStatus.PREVIEW), eq(DocumentIssueType.GRANT));
    }

    @Test
    public void shouldNotGenerateGrantEdgeCaseBulkPrint() throws Exception {

        String solicitorPayload = testUtils.getStringFromFile("payloadWithEdgeCaseBulkPrint.json");

        MvcResult result = mockMvc.perform(post("/document/generate-grant")
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")))
                .andReturn();

    }

    @Test
    public void shouldNotGenerateGrantEdgeCaseReissue() throws Exception {

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotificationsEdgeCase.json");

        mockMvc.perform(post("/document/generate-grant-reissue")
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")));


    }

    @Test
    public void shouldNotSendToBulkPrintIfEdgeCaseReissue() throws Exception {

        String solicitorPayload = testUtils.getStringFromFile("payloadWithEdgeCaseBulkPrint.json");

        mockMvc.perform(post("/document/generate-grant-reissue")
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")));

    }

    @Test
    public void shouldGenerateGrantDefaultCaseType() throws Exception {
        when(documentGeneratorService.getDocument(any(CallbackRequest.class),  eq(DocumentStatus.PREVIEW), eq(DocumentIssueType.GRANT)))
                .thenReturn(Document.builder().documentType(DIGITAL_GRANT_DRAFT).build());

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotificationsDefaultCase.json");

        mockMvc.perform(post("/document/generate-grant-draft")
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")));

        verify(documentGeneratorService).getDocument(any(CallbackRequest.class), eq(DocumentStatus.PREVIEW), eq(DocumentIssueType.GRANT));
    }

    @Test
    public void generateWillLodgementReceipt() throws Exception {

        String solicitorPayload = testUtils.getStringFromFile("willLodgementPayloadNotifications.json");

        mockMvc.perform(post("/document/generate-deposit-receipt")
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")));

        verify(pdfManagementService).generateAndUpload(any(WillLodgementCallbackRequest.class), eq(WILL_LODGEMENT_DEPOSIT_RECEIPT));
    }

    @Test
    public void shouldReturnGrantPAValidateUnSuccessful() throws Exception {
        String personalPayload = testUtils.getStringFromFile("personalPayloadNotificationsNoEmail.json");

        mockMvc.perform(post("/document/generate-grant")
                .content(personalPayload)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors[0]")
                        .value("There is no email address for this applicant. "
                                + "To continue the application, go back and select no to sending an email."))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));

    }

    @Test
    public void shouldReturnGrantSolsValidateSuccessfulEmailNotRequested() throws Exception {
        when(documentGeneratorService.getDocument(any(CallbackRequest.class),  eq(DocumentStatus.FINAL), eq(DocumentIssueType.GRANT)))
                .thenReturn(Document.builder().documentType(DIGITAL_GRANT).build());

        String personalPayload = testUtils.getStringFromFile("solicitorAdditionalExecutors.json");

        mockMvc.perform(post("/document/generate-grant")
                .content(personalPayload)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")));

        verify(documentGeneratorService).getDocument(any(CallbackRequest.class), eq(DocumentStatus.FINAL), eq(DocumentIssueType.GRANT));
    }

    @Test
    public void generateGrantDraftReissueGrantOfRepresentation() throws Exception {

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotifications.json");

        mockMvc.perform(post("/document/generate-grant-draft-reissue")
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")));

    }

    @Test
    public void generateGrantReissueGrantOfRepresentation() throws Exception {

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotifications.json");

        mockMvc.perform(post("/document/generate-grant-reissue")
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")))
                .andExpect(content().string(containsString("sentEmail")));
    }

    @Test
    public void generateGrantReissueGrantOfRepresentationWithNoEmail() throws Exception {

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotificationsNoReissueEmail.json");

        mockMvc.perform(post("/document/generate-grant-reissue")
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")))
                .andExpect(content().string(doesNotContainString("sentEmail")));
    }

    @Test
    public void generateGrantReissueGrantOfRepresentationWithNoEmailPdfSize3() throws Exception {

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotificationspdfSizeThree.json");

        mockMvc.perform(post("/document/generate-grant-reissue")
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")))
                .andExpect(content().string(doesNotContainString("sentEmail")));
    }

    @Test
    public void testGenerateStatementOfTruthReturnsOk() throws Exception {
        String personalPayload = testUtils.getStringFromFile("personalPayloadNotifications.json");

        mockMvc.perform(post("/document/generate-sot")
                .content(personalPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")))
                .andExpect(content().string(containsString("statementOfTruth")));
    }

    @Test
    public void shouldValidateWithPaperCase() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile("paperForm.json");

        mockMvc.perform(post("/document/generate-sot").content(solicitorPayload).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors[0]").value("You can only use this event for digital cases."))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
    }

    @Test
    public void shouldValidateWithDigitalCase() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile("digitalCase.json");

        mockMvc.perform(post("/document/generate-sot").content(solicitorPayload).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
    }

    @Test
    public void shouldValidateAssembleLetter() throws Exception {
        String payload = testUtils.getStringFromFile("generateLetter.json");

        mockMvc.perform(post("/document/assembleLetter").content(payload).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
    }

    @Test
    public void shouldValidatePreviewLetter() throws Exception {
        String payload = testUtils.getStringFromFile("generateLetter.json");

        mockMvc.perform(post("/document/previewLetter").content(payload).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
    }

    @Test
    public void shouldValidateGenerateLetter() throws Exception {
        String payload = testUtils.getStringFromFile("generateLetter.json");

        mockMvc.perform(post("/document/generateLetter").content(payload).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
    }

    private Matcher<String> doesNotContainString(String s) {
        return CoreMatchers.not(containsString(s));
    }
}