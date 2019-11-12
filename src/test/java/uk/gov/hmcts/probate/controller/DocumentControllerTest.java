package uk.gov.hmcts.probate.controller;

import org.hamcrest.Matchers;
import org.hamcrest.*;
import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.context.*;
import org.springframework.boot.test.mock.mockito.*;
import org.springframework.http.*;
import org.springframework.test.context.junit4.*;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.setup.*;
import org.springframework.web.context.*;
import uk.gov.hmcts.probate.insights.*;
import uk.gov.hmcts.probate.model.*;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.*;
import uk.gov.hmcts.probate.model.ccd.raw.request.*;
import uk.gov.hmcts.probate.model.ccd.raw.response.*;
import uk.gov.hmcts.probate.model.ccd.willlodgement.request.*;
import uk.gov.hmcts.probate.service.*;
import uk.gov.hmcts.probate.service.template.pdf.*;
import uk.gov.hmcts.probate.util.*;
import uk.gov.hmcts.reform.sendletter.api.SendLetterResponse;
import uk.gov.service.notify.*;

import java.time.*;
import java.util.*;

import static org.hamcrest.CoreMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
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

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        when(pdfManagementService.generateAndUpload(any(CallbackRequest.class), eq(DIGITAL_GRANT_DRAFT)))
                .thenReturn(Document.builder().documentType(DIGITAL_GRANT_DRAFT).build());
        when(pdfManagementService.generateAndUpload(any(CallbackRequest.class), eq(DIGITAL_GRANT)))
                .thenReturn(Document.builder().documentType(DIGITAL_GRANT).build());
        when(pdfManagementService.generateAndUpload(any(CallbackRequest.class), eq(INTESTACY_GRANT_DRAFT)))
                .thenReturn(Document.builder().documentType(INTESTACY_GRANT_DRAFT).build());
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

        when(documentGeneratorService.generateGrantReissue(any(), any())).thenReturn(document);
        when(documentGeneratorService.generateCoversheet(any(CallbackRequest.class)))
                .thenReturn(Document.builder().documentType(DocumentType.GRANT_COVERSHEET).build());
        when(documentGeneratorService.generateSoT(any()))
                .thenReturn(Document.builder().documentType(DocumentType.STATEMENT_OF_TRUTH).build());

        when(documentGeneratorService.generateLetter(any(CallbackRequest.class), eq(true))).thenReturn(letter);
        when(documentGeneratorService.generateLetter(any(CallbackRequest.class), eq(false))).thenReturn(letter);
        byte[] bytes = "string".getBytes();
        when (documentGeneratorService.generateSealedWillDocument(any())).thenReturn(bytes);
        when(pdfManagementService.generateDocumentAndUpload(bytes))
                .thenReturn(Document.builder().documentType(SEALED_WILL).build());

        SendLetterResponse sendLetterResponse = new SendLetterResponse(UUID.randomUUID());
        when(bulkPrintService.sendToBulkPrint(any(CallbackRequest.class), any(Document.class),
                any(Document.class))).thenReturn(sendLetterResponse);

        when(bulkPrintService.sendToBulkPrint(any(CallbackRequest.class), any(Document.class),
                any(Document.class), eq(true))).thenReturn(LETTER_UUID);

        when(notificationService.generateGrantReissue(any(CallbackRequest.class)))
                .thenReturn(Document.builder().documentType(SENT_EMAIL).build());
        doNothing().when(documentService).expire(any(CallbackRequest.class), any(DocumentType.class));

    }

    @Test
    public void generateGrantDraftGrantOfRepresentation() throws Exception {

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotifications.json");

        MvcResult result = mockMvc.perform(post("/document/generate-grant-draft")
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.probateDocumentsGenerated[0].value.DocumentType", is(DIGITAL_GRANT_DRAFT.getTemplateName())))
                .andReturn();

        verify(documentService).expire(any(CallbackRequest.class), eq(DIGITAL_GRANT_DRAFT));
    }

    @Test
    public void generateDigitalGrant() throws Exception {

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotifications.json");

        MvcResult result = mockMvc.perform(post("/document/generate-grant")
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.probateDocumentsGenerated[1].value.DocumentType", is(DIGITAL_GRANT.getTemplateName())))
                .andReturn();

        verify(documentService).expire(any(CallbackRequest.class), eq(DIGITAL_GRANT_DRAFT));
    }

    @Test
    public void generateDigitalGrantWithBulkPrint() throws Exception {

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

        String solicitorPayload = testUtils.getStringFromFile("payloadWithLocalPrint.json");

        MvcResult result = mockMvc.perform(post("/document/generate-grant")
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.probateDocumentsGenerated[1].value.DocumentType", is(DIGITAL_GRANT.getTemplateName())))
                .andReturn();

        verify(documentService).expire(any(CallbackRequest.class), eq(DIGITAL_GRANT_DRAFT));
    }

    @Test
    public void generateGrantDraftIntestacy() throws Exception {

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotificationsIntestacy.json");

        MvcResult result = mockMvc.perform(post("/document/generate-grant-draft")
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.probateDocumentsGenerated[1].value.DocumentType", is(INTESTACY_GRANT_DRAFT.getTemplateName())))
                .andReturn();

        doNothing().when(documentService).expire(ArgumentMatchers.any(CallbackRequest.class), eq(INTESTACY_GRANT_DRAFT));

        verify(documentService).expire(ArgumentMatchers.any(CallbackRequest.class), eq(ADMON_WILL_GRANT_DRAFT));
        verify(documentService).expire(ArgumentMatchers.any(CallbackRequest.class), eq(DIGITAL_GRANT_DRAFT));
        verify(documentService).expire(ArgumentMatchers.any(CallbackRequest.class), eq(INTESTACY_GRANT_DRAFT));
    }

    @Test
    public void generateGrantDraftAdmonWill() throws Exception {

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotificationsAdmonWill.json");

        MvcResult result = mockMvc.perform(post("/document/generate-grant-draft")
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.probateDocumentsGenerated[1].value.DocumentType", is(ADMON_WILL_GRANT_DRAFT.getTemplateName())))
                .andReturn();

        doNothing().when(documentService).expire(ArgumentMatchers.any(CallbackRequest.class), eq(ADMON_WILL_GRANT_DRAFT));

        verify(documentService).expire(ArgumentMatchers.any(CallbackRequest.class), eq(ADMON_WILL_GRANT_DRAFT));
        verify(documentService).expire(ArgumentMatchers.any(CallbackRequest.class), eq(DIGITAL_GRANT_DRAFT));
        verify(documentService).expire(ArgumentMatchers.any(CallbackRequest.class), eq(INTESTACY_GRANT_DRAFT));
    }

    @Test
    public void generateGrantAdmonWill() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotificationsAdmonWill.json");

        MvcResult result = mockMvc.perform(post("/document/generate-grant")
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.probateDocumentsGenerated[1].value.DocumentType", is(ADMON_WILL_GRANT.getTemplateName())))
                .andReturn();

        verify(pdfManagementService).generateAndUpload(any(CallbackRequest.class), eq(ADMON_WILL_GRANT));

        doNothing().when(documentService).expire(any(CallbackRequest.class), eq(ADMON_WILL_GRANT));
    }

    @Test
    public void generateGrantIntestacy() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotificationsIntestacy.json");

        MvcResult result = mockMvc.perform(post("/document/generate-grant")
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.probateDocumentsGenerated[1].value.DocumentType", is(INTESTACY_GRANT.getTemplateName())))
                .andReturn();

        verify(pdfManagementService).generateAndUpload(any(CallbackRequest.class), eq(INTESTACY_GRANT));

        doNothing().when(documentService).expire(any(CallbackRequest.class), eq(INTESTACY_GRANT));
    }

    @Test
    public void shouldNotGenerateGrantDraftEdgeCase() throws Exception {

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotificationsEdgeCase.json");

        MvcResult result = mockMvc.perform(post("/document/generate-grant-draft")
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.probateDocumentsGenerated", Matchers.hasSize(1)))
                .andReturn();

        doNothing().when(documentService).expire(ArgumentMatchers.any(CallbackRequest.class), eq(EDGE_CASE));

        verify(documentService).expire(ArgumentMatchers.any(CallbackRequest.class), eq(ADMON_WILL_GRANT_DRAFT));
        verify(documentService).expire(ArgumentMatchers.any(CallbackRequest.class), eq(DIGITAL_GRANT_DRAFT));
        verify(documentService).expire(ArgumentMatchers.any(CallbackRequest.class), eq(INTESTACY_GRANT_DRAFT));
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

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotificationsDefaultCase.json");

        mockMvc.perform(post("/document/generate-grant-draft")
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")));

        doNothing().when(documentService).expire(ArgumentMatchers.any(CallbackRequest.class), eq(EDGE_CASE));

        verify(documentService).expire(ArgumentMatchers.any(CallbackRequest.class), eq(ADMON_WILL_GRANT_DRAFT));
        verify(documentService).expire(ArgumentMatchers.any(CallbackRequest.class), eq(DIGITAL_GRANT_DRAFT));
        verify(documentService).expire(ArgumentMatchers.any(CallbackRequest.class), eq(INTESTACY_GRANT_DRAFT));
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
        String personalPayload = testUtils.getStringFromFile("solicitorAdditionalExecutors.json");

        mockMvc.perform(post("/document/generate-grant")
                .content(personalPayload)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")));

        doNothing().when(documentService).expire(any(CallbackRequest.class), eq(DIGITAL_GRANT_DRAFT));
        verify(documentService).expire(any(CallbackRequest.class), eq(DIGITAL_GRANT_DRAFT));
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