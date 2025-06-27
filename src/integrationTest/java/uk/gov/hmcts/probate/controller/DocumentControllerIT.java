package uk.gov.hmcts.probate.controller;

import org.hamcrest.BaseMatcher;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

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
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.FeatureToggleService;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.hmcts.probate.service.DocumentService;
import uk.gov.hmcts.probate.service.BulkPrintService;
import uk.gov.hmcts.probate.service.DocumentGeneratorService;
import uk.gov.hmcts.probate.service.EvidenceUploadService;
import uk.gov.hmcts.probate.service.IdamApi;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.probate.service.user.UserInfoService;
import uk.gov.hmcts.probate.util.TestUtils;
import uk.gov.hmcts.reform.ccd.document.am.feign.CaseDocumentClient;
import uk.gov.hmcts.reform.probate.model.idam.UserInfo;
import uk.gov.hmcts.reform.sendletter.api.SendLetterResponse;
import uk.gov.service.notify.NotificationClientException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.probate.model.DocumentType.AD_COLLIGENDA_BONA_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.AD_COLLIGENDA_BONA_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.ADMON_WILL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.ADMON_WILL_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.EDGE_CASE;
import static uk.gov.hmcts.probate.model.DocumentType.GRANT_COVER;
import static uk.gov.hmcts.probate.model.DocumentType.INTESTACY_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.INTESTACY_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.SENT_EMAIL;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_DIGITAL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_DIGITAL_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.WILL_LODGEMENT_DEPOSIT_RECEIPT;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class DocumentControllerIT {

    private static final String LETTER_UUID = "c387262a-c8a6-44eb-9aea-a740460f9302";
    private static final String AUTH_HEADER = "Authorization";
    private static final String AUTH_TOKEN = "Bearer someAuthorizationToken";
    private static final Optional<UserInfo> CASEWORKER_USERINFO = Optional.ofNullable(UserInfo.builder()
            .familyName("familyName")
            .givenName("givenname")
            .roles(Arrays.asList("caseworker-probate"))
            .build());

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private TestUtils testUtils;

    @MockitoBean
    private PDFManagementService pdfManagementService;

    @MockitoBean
    private NotificationService notificationService;

    @MockitoSpyBean
    private DocumentService documentService;

    @MockitoBean
    private BulkPrintService bulkPrintService;

    @MockitoBean
    private DocumentGeneratorService documentGeneratorService;

    @MockitoBean
    private EvidenceUploadService evidenceUploadService;

    @MockitoBean
    private IdamApi idamApi;

    @Mock
    private SendLetterResponse sendLetterResponseMock;

    @Mock
    private ResponseCaseData.ResponseCaseDataBuilder responseCaseDataBuilder;

    @MockitoBean
    private UserInfoService userInfoService;

    @MockitoSpyBean
    private SecurityUtils securityUtils;

    @MockitoBean
    private CaseDocumentClient caseDocumentClient;

    @MockitoBean
    private FeatureToggleService featureToggleService;

    @BeforeEach
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

        when(pdfManagementService.generateAndUpload(any(CallbackRequest.class), eq(AD_COLLIGENDA_BONA_GRANT)))
                .thenReturn(Document.builder().documentType(AD_COLLIGENDA_BONA_GRANT).build());


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


        when(pdfManagementService
            .generateAndUpload(any(WillLodgementCallbackRequest.class), eq(WILL_LODGEMENT_DEPOSIT_RECEIPT)))
            .thenReturn(Document.builder().documentType(WILL_LODGEMENT_DEPOSIT_RECEIPT).build());

        when(notificationService.sendEmail(any(State.class), any(CaseDetails.class))).thenReturn(document);

        when(notificationService.sendSealedAndCertifiedEmail(any(CaseDetails.class))).thenReturn(document);

        when(documentGeneratorService.generateGrantReissue(any(), any(), any())).thenReturn(document);
        when(documentGeneratorService.generateCoversheet(any(CallbackRequest.class)))
            .thenReturn(Document.builder().documentType(DocumentType.GRANT_COVERSHEET).build());
        when(documentGeneratorService.generateSoT(any()))
            .thenReturn(Document.builder().documentType(DocumentType.STATEMENT_OF_TRUTH).build());

        when(documentGeneratorService.generateLetter(any(CallbackRequest.class), eq(true))).thenReturn(letter);
        when(documentGeneratorService.generateLetter(any(CallbackRequest.class), eq(false))).thenReturn(letter);

        SendLetterResponse sendLetterResponse = new SendLetterResponse(UUID.randomUUID());
        when(bulkPrintService.sendToBulkPrintForGrant(any(CallbackRequest.class), any(Document.class),
            any(Document.class))).thenReturn(sendLetterResponse);

        when(bulkPrintService.optionallySendToBulkPrint(any(CallbackRequest.class), any(Document.class),
            any(Document.class), eq(true))).thenReturn(LETTER_UUID);

        when(notificationService.generateGrantReissue(any(CallbackRequest.class)))
            .thenReturn(Document.builder().documentType(SENT_EMAIL).build());
        doNothing().when(documentService).expire(any(CallbackRequest.class), any(DocumentType.class));
        when(documentGeneratorService
            .getDocument(any(CallbackRequest.class), eq(DocumentStatus.FINAL), eq(DocumentIssueType.GRANT)))
            .thenReturn(Document.builder().documentType(WELSH_DIGITAL_GRANT).build());
        when(documentGeneratorService
            .getDocument(any(CallbackRequest.class), eq(DocumentStatus.PREVIEW), eq(DocumentIssueType.GRANT)))
            .thenReturn(welshDocumentDraft);

        when(documentGeneratorService.getSolicitorSoTDocType(any()))
                .thenCallRealMethod();

        doReturn(CASEWORKER_USERINFO).when(userInfoService).getCaseworkerInfo();

        when(featureToggleService.enableAmendLegalStatementFiletypeCheck()).thenReturn(true);
    }

    @Test
    void generateGrantDraftGrantOfRepresentation() throws Exception {

        when(documentGeneratorService
            .getDocument(any(CallbackRequest.class), eq(DocumentStatus.PREVIEW), eq(DocumentIssueType.GRANT)))
            .thenReturn(Document.builder().documentType(DIGITAL_GRANT_DRAFT).build());

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotifications.json");

        MvcResult result = mockMvc.perform(post("/document/generate-grant-draft")
            .header(AUTH_HEADER, AUTH_TOKEN)
            .content(solicitorPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.probateDocumentsGenerated[0].value.DocumentType",
                is(DIGITAL_GRANT_DRAFT.getTemplateName())))
            .andReturn();

        verify(documentGeneratorService)
            .getDocument(any(CallbackRequest.class), eq(DocumentStatus.PREVIEW), eq(DocumentIssueType.GRANT));
    }

    @Test
    void generateDigitalGrant() throws Exception {
        when(documentGeneratorService
            .getDocument(any(CallbackRequest.class), eq(DocumentStatus.FINAL), eq(DocumentIssueType.GRANT)))
            .thenReturn(Document.builder().documentType(DIGITAL_GRANT).build());

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotifications.json");

        MvcResult result = mockMvc.perform(post("/document/generate-grant")
            .header(AUTH_HEADER, AUTH_TOKEN)
            .content(solicitorPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(
                jsonPath("$.data.probateDocumentsGenerated[1].value.DocumentType", is(DIGITAL_GRANT.getTemplateName())))
            .andReturn();

        verify(documentGeneratorService)
            .getDocument(any(CallbackRequest.class), eq(DocumentStatus.FINAL), eq(DocumentIssueType.GRANT));
    }

    @Test
    void generateDigitalGrantWithBulkPrint() throws Exception {
        when(documentGeneratorService
            .getDocument(any(CallbackRequest.class), eq(DocumentStatus.FINAL), eq(DocumentIssueType.GRANT)))
            .thenReturn(Document.builder().documentType(DIGITAL_GRANT).build());

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotificationsBulkPrint.json");

        MvcResult result = mockMvc.perform(post("/document/generate-grant")
            .header(AUTH_HEADER, AUTH_TOKEN)
            .content(solicitorPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(
                jsonPath("$.data.probateDocumentsGenerated[1].value.DocumentType", is(DIGITAL_GRANT.getTemplateName())))
            .andReturn();

        verify(bulkPrintService)
            .sendToBulkPrintForGrant(any(CallbackRequest.class), any(Document.class), any(Document.class));
    }

    @Test
    void generateDigitalGrantReissueWithBulkPrint() throws Exception {

        String solicitorPayload = testUtils.getStringFromFile("payloadWithBulkPrint.json");

        MvcResult result = mockMvc.perform(post("/document/generate-grant-reissue")
            .header(AUTH_HEADER, AUTH_TOKEN)
            .content(solicitorPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.probateDocumentsGenerated[1].value.DocumentType",
                is(DIGITAL_GRANT_REISSUE.getTemplateName())))
            .andReturn();

        verify(notificationService).sendSealedAndCertifiedEmail(any(CaseDetails.class));
        verify(bulkPrintService)
            .optionallySendToBulkPrint(any(CallbackRequest.class), any(Document.class), any(Document.class), eq(true));
    }

    @Test
    void generateDigitalGrantReissueWithBulkPrintWillLeftAnnexed() throws Exception {

        String solicitorPayload = testUtils.getStringFromFile("payloadWithBulkPrintWillLeftAnnexed.json");

        MvcResult result = mockMvc.perform(post("/document/generate-grant-reissue")
                        .header(AUTH_HEADER, AUTH_TOKEN)
                        .content(solicitorPayload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.probateDocumentsGenerated[1].value.DocumentType",
                        is(DIGITAL_GRANT_REISSUE.getTemplateName())))
                .andReturn();

        verify(bulkPrintService)
                .optionallySendToBulkPrint(any(CallbackRequest.class),
                        any(Document.class), any(Document.class), eq(true));
    }



    @Test
    void generateDigitalGrantIfLocalPrint() throws Exception {
        when(documentGeneratorService
            .getDocument(any(CallbackRequest.class), eq(DocumentStatus.FINAL), eq(DocumentIssueType.GRANT)))
            .thenReturn(Document.builder().documentType(DIGITAL_GRANT).build());
        String solicitorPayload = testUtils.getStringFromFile("payloadWithLocalPrint.json");

        MvcResult result = mockMvc.perform(post("/document/generate-grant")
            .header(AUTH_HEADER, AUTH_TOKEN)
            .content(solicitorPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(
                jsonPath("$.data.probateDocumentsGenerated[1].value.DocumentType", is(DIGITAL_GRANT.getTemplateName())))
            .andReturn();
        verify(notificationService).sendEmail(eq(State.GRANT_ISSUED), any(CaseDetails.class));
        verify(notificationService).sendSealedAndCertifiedEmail(any(CaseDetails.class));
        verify(documentGeneratorService)
            .getDocument(any(CallbackRequest.class), eq(DocumentStatus.FINAL), eq(DocumentIssueType.GRANT));
    }

    @Test
    void generateDigitalGrantIntestacy() throws Exception {
        when(documentGeneratorService
            .getDocument(any(CallbackRequest.class), eq(DocumentStatus.FINAL), eq(DocumentIssueType.GRANT)))
            .thenReturn(Document.builder().documentType(DIGITAL_GRANT).build());
        String solicitorPayload = testUtils.getStringFromFile("personalPayloadNotificationsGrantIntestacy.json");

        MvcResult result = mockMvc.perform(post("/document/generate-grant")
            .header(AUTH_HEADER, AUTH_TOKEN)
            .content(solicitorPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
        verify(notificationService).sendEmail(eq(State.GRANT_ISSUED_INTESTACY), any(CaseDetails.class));
        verify(notificationService).sendSealedAndCertifiedEmail(any(CaseDetails.class));
        verify(documentGeneratorService)
            .getDocument(any(CallbackRequest.class), eq(DocumentStatus.FINAL), eq(DocumentIssueType.GRANT));
    }

    @Test
    void generateWelshDigitalGrant() throws Exception {

        String payload = testUtils.getStringFromFile("welshGrantOfProbatPayload.json");

        MvcResult result = mockMvc.perform(post("/document/generate-grant")
            .header(AUTH_HEADER, AUTH_TOKEN)
            .content(payload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.probateDocumentsGenerated[0].value.DocumentType",
                is(WELSH_DIGITAL_GRANT.getTemplateName())))
            .andReturn();

        verify(documentGeneratorService)
            .getDocument(any(CallbackRequest.class), eq(DocumentStatus.FINAL), eq(DocumentIssueType.GRANT));
    }

    @Test
    void generateWelshDigitalGrantDraft() throws Exception {
        String payload = testUtils.getStringFromFile("welshGrantOfProbatPayloadDraft.json");

        MvcResult result = mockMvc.perform(post("/document/generate-grant-draft")
            .header(AUTH_HEADER, AUTH_TOKEN)
            .content(payload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.probateDocumentsGenerated[0].value.DocumentType",
                is(WELSH_DIGITAL_GRANT_DRAFT.getTemplateName())))
            .andReturn();

        verify(documentGeneratorService)
            .getDocument(any(CallbackRequest.class), eq(DocumentStatus.PREVIEW), eq(DocumentIssueType.GRANT));
    }

    @Test
    void generateGrantDraftIntestacy() throws Exception {
        when(documentGeneratorService
            .getDocument(any(CallbackRequest.class), eq(DocumentStatus.PREVIEW), eq(DocumentIssueType.GRANT)))
            .thenReturn(Document.builder().documentType(INTESTACY_GRANT_DRAFT).build());

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotificationsIntestacy.json");

        MvcResult result = mockMvc.perform(post("/document/generate-grant-draft")
            .header(AUTH_HEADER, AUTH_TOKEN)
            .content(solicitorPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.probateDocumentsGenerated[1].value.DocumentType",
                is(INTESTACY_GRANT_DRAFT.getTemplateName())))
            .andReturn();

        verify(documentGeneratorService)
            .getDocument(any(CallbackRequest.class), eq(DocumentStatus.PREVIEW), eq(DocumentIssueType.GRANT));
    }

    @Test
    void generateGrantDraftAdmonWill() throws Exception {
        when(documentGeneratorService
            .getDocument(any(CallbackRequest.class), eq(DocumentStatus.PREVIEW), eq(DocumentIssueType.GRANT)))
            .thenReturn(Document.builder().documentType(ADMON_WILL_GRANT_DRAFT).build());

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotificationsAdmonWill.json");

        MvcResult result = mockMvc.perform(post("/document/generate-grant-draft")
            .header(AUTH_HEADER, AUTH_TOKEN)
            .content(solicitorPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.probateDocumentsGenerated[1].value.DocumentType",
                is(ADMON_WILL_GRANT_DRAFT.getTemplateName())))
            .andReturn();

        verify(documentGeneratorService)
            .getDocument(any(CallbackRequest.class), eq(DocumentStatus.PREVIEW), eq(DocumentIssueType.GRANT));
    }

    @Test
    void generateGrantDraftAdColligendaBona() throws Exception {
        when(documentGeneratorService
                .getDocument(any(CallbackRequest.class), eq(DocumentStatus.PREVIEW), eq(DocumentIssueType.GRANT)))
                .thenReturn(Document.builder().documentType(AD_COLLIGENDA_BONA_GRANT_DRAFT).build());

        String solicitorPayload = testUtils
                .getStringFromFile("solicitorPayloadNotificationsAdColligendaBona.json");

        mockMvc.perform(post("/document/generate-grant-draft")
                        .content(solicitorPayload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.probateDocumentsGenerated[1].value.DocumentType",
                        is(AD_COLLIGENDA_BONA_GRANT_DRAFT.getTemplateName())))
                .andReturn();

        verify(documentGeneratorService)
                .getDocument(any(CallbackRequest.class), eq(DocumentStatus.PREVIEW), eq(DocumentIssueType.GRANT));
    }

    @Test
    void generateGrantAdmonWill() throws Exception {
        when(documentGeneratorService
            .getDocument(any(CallbackRequest.class), eq(DocumentStatus.FINAL), eq(DocumentIssueType.GRANT)))
            .thenReturn(Document.builder().documentType(ADMON_WILL_GRANT).build());

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotificationsAdmonWill.json");

        MvcResult result = mockMvc.perform(post("/document/generate-grant")
            .header(AUTH_HEADER, AUTH_TOKEN)
            .content(solicitorPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.probateDocumentsGenerated[1].value.DocumentType",
                is(ADMON_WILL_GRANT.getTemplateName())))
            .andReturn();

        verify(documentGeneratorService)
            .getDocument(any(CallbackRequest.class), eq(DocumentStatus.FINAL), eq(DocumentIssueType.GRANT));
    }

    @Test
    void generateGrantIntestacy() throws Exception {
        when(documentGeneratorService
            .getDocument(any(CallbackRequest.class), eq(DocumentStatus.FINAL), eq(DocumentIssueType.GRANT)))
            .thenReturn(Document.builder().documentType(INTESTACY_GRANT).build());

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotificationsIntestacy.json");

        MvcResult result = mockMvc.perform(post("/document/generate-grant")
            .header(AUTH_HEADER, AUTH_TOKEN)
            .content(solicitorPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.probateDocumentsGenerated[1].value.DocumentType",
                is(INTESTACY_GRANT.getTemplateName())))
            .andReturn();

        verify(documentGeneratorService)
            .getDocument(any(CallbackRequest.class), eq(DocumentStatus.FINAL), eq(DocumentIssueType.GRANT));
    }

    @Test
    void generateGrantAdColligendaBona() throws Exception {
        when(documentGeneratorService
                .getDocument(any(CallbackRequest.class), eq(DocumentStatus.FINAL), eq(DocumentIssueType.GRANT)))
                .thenReturn(Document.builder().documentType(AD_COLLIGENDA_BONA_GRANT).build());

        String solicitorPayload = testUtils
                .getStringFromFile("solicitorPayloadNotificationsAdColligendaBona.json");

        mockMvc.perform(post("/document/generate-grant")
                        .content(solicitorPayload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.probateDocumentsGenerated[1].value.DocumentType",
                        is(AD_COLLIGENDA_BONA_GRANT.getTemplateName())))
                .andReturn();

        verify(documentGeneratorService)
                .getDocument(any(CallbackRequest.class), eq(DocumentStatus.FINAL), eq(DocumentIssueType.GRANT));
    }

    @Test
    void shouldNotGenerateGrantDraftEdgeCase() throws Exception {
        when(documentGeneratorService
            .getDocument(any(CallbackRequest.class), eq(DocumentStatus.PREVIEW), eq(DocumentIssueType.GRANT)))
            .thenReturn(Document.builder().documentType(EDGE_CASE).build());

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotificationsEdgeCase.json");

        MvcResult result = mockMvc.perform(post("/document/generate-grant-draft")
            .header(AUTH_HEADER, AUTH_TOKEN)
            .content(solicitorPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.probateDocumentsGenerated", Matchers.empty()))
            .andReturn();

        verify(documentGeneratorService)
            .getDocument(any(CallbackRequest.class), eq(DocumentStatus.PREVIEW), eq(DocumentIssueType.GRANT));
    }

    @Test
    void shouldNotGenerateGrantEdgeCaseBulkPrint() throws Exception {

        String solicitorPayload = testUtils.getStringFromFile("payloadWithEdgeCaseBulkPrint.json");

        MvcResult result = mockMvc.perform(post("/document/generate-grant")
            .header(AUTH_HEADER, AUTH_TOKEN)
            .content(solicitorPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("data")))
            .andReturn();

    }

    @Test
    void shouldNotGenerateGrantEdgeCaseReissue() throws Exception {

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotificationsEdgeCase.json");

        mockMvc.perform(post("/document/generate-grant-reissue")
            .header(AUTH_HEADER, AUTH_TOKEN)
            .content(solicitorPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("data")));


    }

    @Test
    void shouldNotSendToBulkPrintIfEdgeCaseReissue() throws Exception {

        String solicitorPayload = testUtils.getStringFromFile("payloadWithEdgeCaseBulkPrint.json");

        mockMvc.perform(post("/document/generate-grant-reissue")
            .header(AUTH_HEADER, AUTH_TOKEN)
            .content(solicitorPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("data")));

    }

    @Test
    void shouldGenerateGrantDefaultCaseType() throws Exception {
        when(documentGeneratorService
            .getDocument(any(CallbackRequest.class), eq(DocumentStatus.PREVIEW), eq(DocumentIssueType.GRANT)))
            .thenReturn(Document.builder().documentType(DIGITAL_GRANT_DRAFT).build());

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotificationsDefaultCase.json");

        mockMvc.perform(post("/document/generate-grant-draft")
            .header(AUTH_HEADER, AUTH_TOKEN)
            .content(solicitorPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("data")));

        verify(documentGeneratorService)
            .getDocument(any(CallbackRequest.class), eq(DocumentStatus.PREVIEW), eq(DocumentIssueType.GRANT));
    }

    @Test
    void generateWillLodgementReceipt() throws Exception {

        String solicitorPayload = testUtils.getStringFromFile("willLodgementPayloadNotifications.json");

        mockMvc.perform(post("/document/generate-deposit-receipt")
            .content(solicitorPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("data")));

        verify(pdfManagementService)
            .generateAndUpload(any(WillLodgementCallbackRequest.class), eq(WILL_LODGEMENT_DEPOSIT_RECEIPT));
    }

    @Test
    void shouldReturnGrantPAValidateUnSuccessful() throws Exception {
        String personalPayload = testUtils.getStringFromFile("personalPayloadNotificationsNoEmail.json");

        mockMvc.perform(post("/document/generate-grant")
            .header(AUTH_HEADER, AUTH_TOKEN)
            .content(personalPayload)
            .contentType(MediaType.APPLICATION_JSON))
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
    void shouldReturnGrantSolsValidateSuccessfulEmailNotRequested() throws Exception {
        when(documentGeneratorService
            .getDocument(any(CallbackRequest.class), eq(DocumentStatus.FINAL), eq(DocumentIssueType.GRANT)))
            .thenReturn(Document.builder().documentType(DIGITAL_GRANT).build());

        String personalPayload = testUtils.getStringFromFile("solicitorAdditionalExecutors.json");

        mockMvc.perform(post("/document/generate-grant")
            .header(AUTH_HEADER, AUTH_TOKEN)
            .content(personalPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("data")));

        verify(documentGeneratorService)
            .getDocument(any(CallbackRequest.class), eq(DocumentStatus.FINAL), eq(DocumentIssueType.GRANT));
    }

    @Test
    void generateGrantDraftReissueGrantOfRepresentation() throws Exception {

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotifications.json");

        mockMvc.perform(post("/document/generate-grant-draft-reissue")
            .header(AUTH_HEADER, AUTH_TOKEN)
            .content(solicitorPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("data")));

    }

    @Test
    void generateGrantReissueGrantOfRepresentation() throws Exception {

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotifications.json");

        mockMvc.perform(post("/document/generate-grant-reissue")
            .header(AUTH_HEADER, AUTH_TOKEN)
            .content(solicitorPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("data")))
            .andExpect(content().string(containsString("sentEmail")));
    }

    @Test
    void generateGrantReissueGrantOfRepresentationWithNoEmail() throws Exception {

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotificationsNoReissueEmail.json");

        mockMvc.perform(post("/document/generate-grant-reissue")
            .header(AUTH_HEADER, AUTH_TOKEN)
            .content(solicitorPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("data")))
            .andExpect(content().string(doesNotContainString("sentEmail")));
    }

    @Test
    void generateGrantReissueGrantOfRepresentationWithNoEmailPdfSize3() throws Exception {

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotificationspdfSizeThree.json");

        mockMvc.perform(post("/document/generate-grant-reissue")
            .header(AUTH_HEADER, AUTH_TOKEN)
            .content(solicitorPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("data")))
            .andExpect(content().string(doesNotContainString("sentEmail")));
    }

    @Test
    void testGenerateStatementOfTruthReturnsOk() throws Exception {
        String personalPayload = testUtils.getStringFromFile("personalPayloadNotifications.json");

        mockMvc.perform(post("/document/generate-sot")
            .header(AUTH_HEADER, AUTH_TOKEN)
            .content(personalPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("data")))
            .andExpect(content().string(containsString("statementOfTruth")));
    }

    @Test
    void shouldValidateWithPaperCase() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile("paperForm.json");

        mockMvc
                .perform(post("/document/generate-sot").header(AUTH_HEADER, AUTH_TOKEN)
                        .content(solicitorPayload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors[0]").value("You can only use this event for digital cases."))
                .andExpect(jsonPath("$.errors[1]").value("Dim ond ar gyfer achosion digidol y "
                        + "gallwch ddefnyddio'r adnodd hwn."))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldValidateWithDigitalCase() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile("digitalCase.json");

        mockMvc
            .perform(post("/document/generate-sot").header(AUTH_HEADER, AUTH_TOKEN)
                    .content(solicitorPayload).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldValidateAssembleLetter() throws Exception {
        String payload = testUtils.getStringFromFile("generateLetter.json");

        mockMvc.perform(post("/document/assembleLetter").header(AUTH_HEADER, AUTH_TOKEN)
                        .content(payload).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldValidatePreviewLetter() throws Exception {
        String payload = testUtils.getStringFromFile("generateLetter.json");

        mockMvc.perform(post("/document/previewLetter").header(AUTH_HEADER, AUTH_TOKEN)
                        .content(payload).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldValidateGenerateLetter() throws Exception {
        String payload = testUtils.getStringFromFile("generateLetter.json");

        mockMvc.perform(post("/document/generateLetter").header(AUTH_HEADER, AUTH_TOKEN)
                        .content(payload).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldDefaultReprintValues() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile("welshGrantOfProbatPayload.json");

        mockMvc.perform(post("/document/default-reprint-values")
            .content(solicitorPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.reprintDocument.list_items[0].label", is("Grant")))
            .andExpect(jsonPath("$.data.reprintDocument.list_items[0].code", is("WelshGrantFileName")))
            .andReturn();
    }

    @Test
    void shouldSendForReprint() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile("welshGrantOfProbatPayloadReprintGrant.json");

        mockMvc.perform(post("/document/reprint")
            .header(AUTH_HEADER, AUTH_TOKEN)
            .content(solicitorPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.reprintDocument.list_items[0].label", is("Grant")))
            .andExpect(jsonPath("$.data.reprintDocument.list_items[0].code", is("WelshGrantFileName")))
            .andExpect(jsonPath("$.data.reprintDocument.value.label", is("Grant")))
            .andExpect(jsonPath("$.data.reprintDocument.value.code", is("WelshGrantFileName")))
            .andReturn();
    }

    @Test
    void shouldHandleEvidenceToYesFromNull() throws Exception {
        String payload = testUtils.getStringFromFile("evidenceHandledYesFromNull.json");

        mockMvc.perform(post("/document/generate-grant").header(AUTH_HEADER, AUTH_TOKEN)
                        .content(payload).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"evidenceHandled\":\"Yes\"")));
    }

    @Test
    void shouldHandleEvidenceToYes() throws Exception {
        String payload = testUtils.getStringFromFile("evidenceHandledYes.json");

        mockMvc.perform(post("/document/generate-grant").header(AUTH_HEADER, AUTH_TOKEN)
                        .content(payload).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"evidenceHandled\":\"Yes\"")));
    }

    @Test
    void shouldHandleEvidenceToNo() throws Exception {
        String payload = testUtils.getStringFromFile("evidenceHandledNo.json");

        mockMvc.perform(post("/document/generate-grant").header(AUTH_HEADER, AUTH_TOKEN)
                        .content(payload).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"evidenceHandled\":\"Yes\"")));
    }

    @Test
    void shouldUpdateLastEvidenceAddedDateCaseworker() throws Exception {
        String payload = testUtils.getStringFromFile("digitalCase.json");
        mockMvc.perform(post("/document/evidenceAdded")
                        .header(AUTH_HEADER, AUTH_TOKEN)
                        .content(payload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        verify(evidenceUploadService)
                .updateLastEvidenceAddedDate(any(CaseDetails.class));
    }

    @Test
    void shouldUpdateLastEvidenceAddedDateRobotOngoing() throws Exception {
        String payload = testUtils.getStringFromFile("digitalCase.json");
        mockMvc.perform(post("/document/evidenceAddedRPARobot")
                        .content(payload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        verify(evidenceUploadService)
                .updateLastEvidenceAddedDate(any(CaseDetails.class));
    }

    @Test
    void shouldSetupForPermanentRemovalGrant() throws Exception {
        String caveatPayload = testUtils.getStringFromFile("digitalCase.json");
        mockMvc.perform(post("/document/setup-for-permanent-removal")
                        .content(caveatPayload).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteRemovedDocumentsGrant() throws Exception {
        String caveatPayload = testUtils.getStringFromFile("digitalCase.json");
        mockMvc.perform(post("/document/permanently-delete-removed")
                        .header(AUTH_HEADER, AUTH_TOKEN)
                        .content(caveatPayload).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldSetupForPermanentRemovalWillLodgement() throws Exception {
        String willPayload = testUtils.getStringFromFile("willLodgementPayloadNotifications.json");
        mockMvc.perform(post("/document/setup-for-permanent-removal-will")
                        .content(willPayload).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteRemovedDocumentsWillLodgement() throws Exception {
        String willPayload = testUtils.getStringFromFile("willLodgementDocumentsPayloadNotifications.json");
        mockMvc.perform(post("/document/permanently-delete-removed-will")
                        .content(willPayload).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldHandleEvidenceToYesForCitizenHubResponse() throws Exception {
        String payload = testUtils.getStringFromFile("citizenHubResponseevidenceHandledYes.json");

        mockMvc.perform(post("/document/citizenHubResponse")
                        .content(payload).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"evidenceHandled\":\"Yes\"")));
    }

    @Test
    void shouldHandleEvidenceToNoForCitizenHubResponse() throws Exception {
        String payload = testUtils.getStringFromFile("citizenHubResponseevidenceHandledNo.json");

        mockMvc.perform(post("/document/citizenHubResponse")
                        .content(payload).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"evidenceHandled\":\"No\"")));
    }

    private Matcher<String> doesNotContainString(String s) {
        return CoreMatchers.not(containsString(s));
    }

    @Test
    void shouldAttachAmendedLegalStatement_PA() throws Exception {
        String payload = testUtils.getStringFromFile("uploadAmendedLegalStatement_PA.json");

        final var request = post("/document/amendLegalStatement")
                .header("authorization", "authToken")
                .content(payload)
                .contentType(MediaType.APPLICATION_JSON);

        final String expectedDate = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
        final String expectedFilename = new StringBuilder()
                .append("amendedLegalStatement_")
                .append(expectedDate)
                .append(".pdf")
                .toString();

        final AmendedFilenameMatcher contentMatcher = new AmendedFilenameMatcher(expectedFilename);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().string(contentMatcher));
    }

    @Test
    void shouldAttachAmendedLegalStatement_PP_probate() throws Exception {
        String payload = testUtils.getStringFromFile("uploadAmendedLegalStatement_PP_probate.json");

        final var request = post("/document/amendLegalStatement")
                .header("authorization", "authToken")
                .content(payload)
                .contentType(MediaType.APPLICATION_JSON);

        final String expectedDate = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
        final String expectedFilename = new StringBuilder()
                .append("amendedLegalStatementGrantOfProbate_")
                .append(expectedDate)
                .append(".pdf")
                .toString();

        final AmendedFilenameMatcher contentMatcher = new AmendedFilenameMatcher(expectedFilename);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().string(contentMatcher));
    }

    @Test
    void shouldAttachAmendedLegalStatement_PP_intestacy() throws Exception {
        String payload = testUtils.getStringFromFile("uploadAmendedLegalStatement_PP_intestacy.json");

        final var request = post("/document/amendLegalStatement")
                .header("authorization", "authToken")
                .content(payload)
                .contentType(MediaType.APPLICATION_JSON);

        final String expectedDate = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
        final String expectedFilename = new StringBuilder()
                .append("amendedLegalStatementIntestacy_")
                .append(expectedDate)
                .append(".pdf")
                .toString();

        final AmendedFilenameMatcher contentMatcher = new AmendedFilenameMatcher(expectedFilename);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().string(contentMatcher));
    }

    @Test
    void shouldAttachAmendedLegalStatement_PP_admon() throws Exception {
        String payload = testUtils.getStringFromFile("uploadAmendedLegalStatement_PP_admon.json");

        final var request = post("/document/amendLegalStatement")
                .header("authorization", "authToken")
                .content(payload)
                .contentType(MediaType.APPLICATION_JSON);

        final String expectedDate = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
        final String expectedFilename = new StringBuilder()
                .append("amendedLegalStatementAdmon_")
                .append(expectedDate)
                .append(".pdf")
                .toString();

        final AmendedFilenameMatcher contentMatcher = new AmendedFilenameMatcher(expectedFilename);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().string(contentMatcher));
    }

    @Test
    void shouldAttachAmendedLegalStatement_PP_edgeCase() throws Exception {
        String payload = testUtils.getStringFromFile("uploadAmendedLegalStatement_PP_edgeCase.json");

        final var request = post("/document/amendLegalStatement")
                .header("authorization", "authToken")
                .content(payload)
                .contentType(MediaType.APPLICATION_JSON);

        final String expectedDate = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
        final String expectedFilename = new StringBuilder()
                .append("amendedLegalStatementGrantOfProbate_")
                .append(expectedDate)
                .append(".pdf")
                .toString();

        final AmendedFilenameMatcher contentMatcher = new AmendedFilenameMatcher(expectedFilename);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().string(contentMatcher));
    }

    private final class AmendedFilenameMatcher extends BaseMatcher<String> {

        private final String expectedFilename;

        AmendedFilenameMatcher(String expectedFilename) {
            this.expectedFilename = expectedFilename;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("a json string which has .data.amendedLegalStatement.document_filename matching ");
            description.appendValue(expectedFilename);
        }

        @Override
        public boolean matches(Object actual) {
            if (!(actual instanceof String)) {
                return false;
            }
            final String body = (String) actual;
            final JSONObject bodyJson = new JSONObject(body);

            final JSONObject data = bodyJson.optJSONObject("data");
            if (data == null) {
                return false;
            }

            final JSONObject amendedLegalStatement = data.optJSONObject("amendedLegalStatement");
            if (amendedLegalStatement == null) {
                return false;
            }

            final String amendedDocName = amendedLegalStatement.optString("document_filename");

            return expectedFilename.equals(amendedDocName);
        }
    }

    /*
     * There is no distinction between PP and PA at the point this error occurs. We use PP only because it exists.
     */
    @Test
    void shouldAcceptPdfValidateAmendLegalStatement() throws Exception {
        String payload = testUtils.getStringFromFile("uploadAmendedLegalStatement_PP_probate.json");

        // unclear why when(sU.gST()).thenReturn("sA"); doesn't work, but this does.
        doReturn("serviceAuth").when(securityUtils).generateServiceToken();

        final uk.gov.hmcts.reform.ccd.document.am.model.Document mockDocument =
                uk.gov.hmcts.reform.ccd.document.am.model.Document.builder()
                        .mimeType(MediaType.APPLICATION_PDF_VALUE)
                        .build();
        when(caseDocumentClient.getMetadataForDocument(any(), any(), anyString())).thenReturn(mockDocument);

        final var request = post("/document/validateAmendLegalStatement")
                .content(payload)
                .contentType(MediaType.APPLICATION_JSON);

        final NoErrorReturnedMatcher contentMatcher = new NoErrorReturnedMatcher();

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().string(contentMatcher));
    }

    private final class NoErrorReturnedMatcher extends BaseMatcher<String> {
        @Override
        public void describeTo(Description description) {
            description.appendText("a json string which does not have .errors");
        }

        @Override
        public boolean matches(Object actual) {
            if (!(actual instanceof String)) {
                return false;
            }
            final String body = (String) actual;
            final JSONObject bodyJson = new JSONObject(body);

            final JSONArray errors = bodyJson.optJSONArray("errors");

            if (errors != null && errors.length() != 0) {
                return false;
            }
            return true;
        }
    }

    /*
     * There is no distinction between PP and PA at the point this error occurs. We use PP only because it exists.
     */
    @Test
    void shouldRejectNonPdfValidateAmendLegalStatement() throws Exception {
        String payload = testUtils.getStringFromFile("uploadAmendedLegalStatement_PP_probate.json");

        // unclear why when(sU.gST()).thenReturn("sA"); doesn't work, but this does.
        doReturn("serviceAuth").when(securityUtils).generateServiceToken();

        final uk.gov.hmcts.reform.ccd.document.am.model.Document mockDocument =
                uk.gov.hmcts.reform.ccd.document.am.model.Document.builder()
                        .mimeType(MediaType.IMAGE_PNG_VALUE)
                        .build();
        when(caseDocumentClient.getMetadataForDocument(any(), any(), anyString())).thenReturn(mockDocument);

        final var request = post("/document/validateAmendLegalStatement")
                .content(payload)
                .contentType(MediaType.APPLICATION_JSON);

        final ErrorReturnedMatcher contentMatcher = new ErrorReturnedMatcher();

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().string(contentMatcher));
    }

    private final class ErrorReturnedMatcher extends BaseMatcher<String> {
        @Override
        public void describeTo(Description description) {
            description.appendText("a json string which has .errors containing at least one string");
        }

        @Override
        public boolean matches(Object actual) {
            if (!(actual instanceof String)) {
                return false;
            }
            final String body = (String) actual;
            final JSONObject bodyJson = new JSONObject(body);

            final JSONArray errors = bodyJson.optJSONArray("errors");
            if (errors == null || errors.length() == 0) {
                return false;
            }

            final int maxIdx = errors.length();
            for (int idx = 0; idx < maxIdx; idx++) {
                final String errStr = errors.optString(idx);
                if (errStr != null) {
                    return true;
                }
            }
            return false;
        }
    }
}
