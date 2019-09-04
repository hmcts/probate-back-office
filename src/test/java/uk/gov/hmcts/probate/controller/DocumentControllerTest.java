package uk.gov.hmcts.probate.controller;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.hmcts.probate.insights.AppInsights;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.State;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.probate.model.DocumentType.ADMON_WILL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.ADMON_WILL_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.EDGE_CASE;
import static uk.gov.hmcts.probate.model.DocumentType.GRANT_COVER;
import static uk.gov.hmcts.probate.model.DocumentType.INTESTACY_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.INTESTACY_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.SENT_EMAIL;
import static uk.gov.hmcts.probate.model.DocumentType.WILL_LODGEMENT_DEPOSIT_RECEIPT;

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
        when(pdfManagementService.generateAndUpload(any(CallbackRequest.class), eq(GRANT_COVER)))
                .thenReturn(Document.builder().documentType(GRANT_COVER).build());

        when(pdfManagementService.generateAndUpload(any(WillLodgementCallbackRequest.class), eq(WILL_LODGEMENT_DEPOSIT_RECEIPT)))
                .thenReturn(Document.builder().documentType(WILL_LODGEMENT_DEPOSIT_RECEIPT).build());

        when(notificationService.sendEmail(any(State.class), any(CaseDetails.class))).thenReturn(document);

        when(documentGeneratorService.generateGrantReissue(any(), any())).thenReturn(document);
        when(documentGeneratorService.generateCoversheet(any(CallbackRequest.class)))
                .thenReturn(Document.builder().documentType(DocumentType.GRANT_COVERSHEET).build());

        when(bulkPrintService.sendToBulkPrint(any(), any(),
                any(), anyBoolean())).thenReturn(LETTER_UUID);

        when(notificationService.generateGrantReissue(any(CallbackRequest.class)))
                .thenReturn(Document.builder().documentType(SENT_EMAIL).build());
    }

    @Test
    public void generateGrantDraftGrantOfRepresentation() throws Exception {

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotifications.json");

        mockMvc.perform(post("/document/generate-grant-draft")
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")));

        doNothing().when(documentService).expire(any(CallbackRequest.class), eq(DIGITAL_GRANT_DRAFT));

        verify(documentService).expire(any(CallbackRequest.class), eq(DIGITAL_GRANT_DRAFT));
    }

    @Test
    public void generateDigitalGrant() throws Exception {

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotifications.json");

        mockMvc.perform(post("/document/generate-grant")
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")));

        doNothing().when(documentService).expire(any(CallbackRequest.class), eq(DIGITAL_GRANT_DRAFT));
        verify(documentService).expire(any(CallbackRequest.class), eq(DIGITAL_GRANT_DRAFT));
    }

    @Test
    public void generateDigitalGrantBulkPrint() throws Exception {

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotificationsBulkPrint.json");

        mockMvc.perform(post("/document/generate-grant")
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")));

        SendLetterResponse sendLetterResponse = new SendLetterResponse(UUID.randomUUID());
        when(bulkPrintService.sendToBulkPrint(any(CallbackRequest.class), any(Document.class),
                any(Document.class))).thenReturn(sendLetterResponse);
        verify(bulkPrintService).sendToBulkPrint(any(CallbackRequest.class), any(Document.class), any(Document.class));
    }

    @Test
    public void shouldNotPrintDigitalGrantIfLocalPrint() throws Exception {

        String solicitorPayload = testUtils.getStringFromFile("payloadWithLocalPrint.json");

        mockMvc.perform(post("/document/generate-grant")
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")));

        doNothing().when(documentService).expire(any(CallbackRequest.class), eq(DIGITAL_GRANT_DRAFT));
        verify(documentService).expire(any(CallbackRequest.class), eq(DIGITAL_GRANT_DRAFT));
    }

    @Test
    public void shouldNotPrintDigitalGrantIfBulkPrint() throws Exception {

        String solicitorPayload = testUtils.getStringFromFile("payloadWithEdgeCase.json");

        mockMvc.perform(post("/document/generate-grant")
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")));

        doNothing().when(documentService).expire(any(CallbackRequest.class), eq(DIGITAL_GRANT_DRAFT));
        verify(documentService).expire(any(CallbackRequest.class), eq(DIGITAL_GRANT_DRAFT));
    }

    @Test
    public void generateGrantDraftIntestacy() throws Exception {

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotificationsIntestacy.json");

        mockMvc.perform(post("/document/generate-grant-draft")
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")));

        doNothing().when(documentService).expire(ArgumentMatchers.any(CallbackRequest.class), eq(INTESTACY_GRANT_DRAFT));

        verify(documentService).expire(ArgumentMatchers.any(CallbackRequest.class), eq(ADMON_WILL_GRANT_DRAFT));
        verify(documentService).expire(ArgumentMatchers.any(CallbackRequest.class), eq(DIGITAL_GRANT_DRAFT));
        verify(documentService).expire(ArgumentMatchers.any(CallbackRequest.class), eq(INTESTACY_GRANT_DRAFT));
    }

    @Test
    public void generateGrantDraftAdmonWill() throws Exception {

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotificationsAdmonWill.json");

        mockMvc.perform(post("/document/generate-grant-draft")
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")));

        doNothing().when(documentService).expire(ArgumentMatchers.any(CallbackRequest.class), eq(ADMON_WILL_GRANT_DRAFT));

        verify(documentService).expire(ArgumentMatchers.any(CallbackRequest.class), eq(ADMON_WILL_GRANT_DRAFT));
    }

    @Test
    public void generateGrantAdmonWill() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotificationsAdmonWill.json");

        mockMvc.perform(post("/document/generate-grant")
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")));

        verify(pdfManagementService).generateAndUpload(any(CallbackRequest.class), eq(ADMON_WILL_GRANT));

        doNothing().when(documentService).expire(any(CallbackRequest.class), eq(ADMON_WILL_GRANT));
    }

    @Test
    public void shouldNotGenerateGrantEdgeCase() throws Exception {

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotificationsEdgeCase.json");

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

    private Matcher<String> doesNotContainString(String s) {
        return CoreMatchers.not(containsString(s));
    }
}