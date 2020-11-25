package uk.gov.hmcts.probate.service;

import ch.qos.logback.core.db.dialect.SybaseSqlAnywhereDialect;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.State;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.caveat.response.CaveatCallbackResponse;
import uk.gov.hmcts.probate.model.ccd.caveat.response.ResponseCaveatData;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.service.docmosis.CaveatDocmosisService;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.probate.transformer.CaveatCallbackResponseTransformer;
import uk.gov.hmcts.probate.validator.BulkPrintValidationRule;
import uk.gov.hmcts.probate.validator.CaveatsEmailValidationRule;
import uk.gov.hmcts.reform.sendletter.api.SendLetterResponse;
import uk.gov.service.notify.NotificationClientException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.Constants.CAVEAT_LIFESPAN;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.DocumentType.CAVEAT_COVERSHEET;
import static uk.gov.hmcts.probate.model.DocumentType.CAVEAT_EXTENDED;
import static uk.gov.hmcts.probate.model.DocumentType.CAVEAT_RAISED;


public class CaveatNotificationServiceTest {

    @InjectMocks
    private CaveatNotificationService caveatNotificationService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private EventValidationService eventValidationService;

    @Mock
    private List<CaveatsEmailValidationRule> emailAddressNotificationValidationRules;

    @Mock
    private List<BulkPrintValidationRule> bulkPrintValidationRules;

    @Mock
    private CaveatDocmosisService caveatDocmosisService;

    @Mock
    private CaveatCallbackResponseTransformer caveatCallbackResponseTransformer;

    @Mock
    Map<String, Object> placeholders;

    @Mock
    private PDFManagementService pdfManagementService;

    @Mock
    BulkPrintService bulkPrintService;

    @Mock
    private CaveatCallbackResponse caveatCallbackResponse;

    private Document coversheet;
    private Document caveatRaised;
    private Document caveatExtended;
    private Document sentEmail;
    private CaveatData caveatData;
    private CaveatData solsCaveatData;
    private CaveatDetails caveatDetails;
    private CaveatCallbackRequest caveatCallbackRequest;
    private ResponseCaveatData responseCaveatData;
    private List<Document> documents = new ArrayList<>();

    private static final long ID = 1234567891234567L;
    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private static final String SENT_EMAIL_FILE_NAME = "sentEmail.pdf";
    private static final String COVERSHEET_FILE_NAME = "sentEmail.pdf";
    private static final String CAVEAT_RAISED_FILE_NAME = "sentEmail.pdf";
    private static final String CAVEAT_EXTENDED_FILE_NAME = "sentEmail.pdf";
    private static final List<CollectionMember<Document>> DOCUMENTS_LIST = Arrays.asList(
            new CollectionMember("id",
                    Document.builder()
                            .documentFileName(SENT_EMAIL_FILE_NAME)
                            .build()));

    private static final List<CollectionMember<Document>> DOCUMENTS_LIST_CAVEAT_RAISED = Arrays.asList(
            new CollectionMember("id",
                    Document.builder()
                            .documentFileName(COVERSHEET_FILE_NAME)
                            .build()),
            new CollectionMember("id",
                    Document.builder()
                            .documentFileName(CAVEAT_RAISED_FILE_NAME)
                            .build()));

    private static final List<CollectionMember<Document>> DOCUMENTS_LIST_CAVEAT_EXTENDED = Arrays.asList(
        new CollectionMember("id",
            Document.builder()
                .documentFileName(COVERSHEET_FILE_NAME)
                .build()),
        new CollectionMember("id",
            Document.builder()
                .documentFileName(CAVEAT_EXTENDED_FILE_NAME)
                .build()));

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        caveatData = CaveatData.builder()
                .registryLocation("leeds")
                .caveatorEmailAddress("test@test.com")
                .deceasedForenames("name")
                .deceasedSurname("name")
                .build();

        responseCaveatData = ResponseCaveatData.builder()
                .registryLocation("leeds")
                .caveatorEmailAddress("test@test.com")
                .deceasedForenames("name")
                .deceasedSurname("name")
                .caveatRaisedEmailNotificationRequested("Yes")
                .build();

        solsCaveatData = CaveatData.builder()
                .applicationType(ApplicationType.SOLICITOR)
                .registryLocation("ctsc")
                .caveatorEmailAddress("solicitor@test.com")
                .deceasedForenames("forename")
                .deceasedSurname("surname")
                .build();

        sentEmail = Document.builder().documentFileName(SENT_EMAIL_FILE_NAME).build();
        coversheet = Document.builder().documentFileName(COVERSHEET_FILE_NAME).build();
        caveatRaised = Document.builder().documentFileName(CAVEAT_RAISED_FILE_NAME).build();

    }

    @Test
    public void testSolsCaveatRaise() throws NotificationClientException {
        documents.add(sentEmail);

        responseCaveatData = ResponseCaveatData.builder()
                .notificationsGenerated(DOCUMENTS_LIST)
                .paperForm(NO)
                .build();

        caveatDetails = new CaveatDetails(solsCaveatData, LAST_MODIFIED, ID);
        caveatCallbackRequest = new CaveatCallbackRequest(caveatDetails);

        when(notificationService.sendCaveatEmail(State.CAVEAT_RAISED_SOLS, caveatDetails)).thenReturn(Document.builder()
                .documentFileName(SENT_EMAIL_FILE_NAME).build());

        caveatCallbackResponse = CaveatCallbackResponse.builder().caveatData(responseCaveatData).build();
        when(caveatCallbackResponseTransformer.caveatRaised(caveatCallbackRequest, documents, null)).thenReturn(caveatCallbackResponse);

        caveatNotificationService.solsCaveatRaise(caveatCallbackRequest);

        assertEquals(1, caveatCallbackResponse.getCaveatData().getNotificationsGenerated().size());
    }
    
    @Test
    public void testCaveatRaiseWithEmail() throws NotificationClientException {
        caveatData = CaveatData.builder()
                .caveatRaisedEmailNotificationRequested("Yes")
                .build();

        documents.add(sentEmail);

        responseCaveatData = ResponseCaveatData.builder()
                .notificationsGenerated(DOCUMENTS_LIST)
                .build();

        caveatDetails = new CaveatDetails(caveatData, LAST_MODIFIED, ID);
        caveatCallbackRequest = new CaveatCallbackRequest(caveatDetails);

        when(eventValidationService.validateCaveatRequest(any(CaveatCallbackRequest.class), any(List.class)))
                .thenReturn(caveatCallbackResponse.builder().errors(new ArrayList<>()).build());
        when(notificationService.sendCaveatEmail(State.CAVEAT_RAISED, caveatDetails)).thenReturn(Document.builder()
                .documentFileName(SENT_EMAIL_FILE_NAME).build());

        caveatCallbackResponse = CaveatCallbackResponse.builder().caveatData(responseCaveatData).build();
        when(caveatCallbackResponseTransformer.caveatRaised(caveatCallbackRequest, documents, null)).thenReturn(caveatCallbackResponse);

        caveatNotificationService.caveatRaise(caveatCallbackRequest);

        assertEquals(1, caveatCallbackResponse.getCaveatData().getNotificationsGenerated().size());
    }

    @Test
    public void testCaveatRaiseWithNoEmailNoBulkPrint() throws NotificationClientException {
        caveatData = CaveatData.builder()
                .caveatRaisedEmailNotificationRequested("No")
                .sendToBulkPrintRequested("No")
                .build();

        responseCaveatData = ResponseCaveatData.builder()
                .notificationsGenerated(DOCUMENTS_LIST_CAVEAT_RAISED)
                .build();

        documents.add(coversheet);
        documents.add(caveatRaised);

        caveatDetails = new CaveatDetails(caveatData, LAST_MODIFIED, ID);
        caveatCallbackRequest = new CaveatCallbackRequest(caveatDetails);

        when(caveatDocmosisService.caseDataAsPlaceholders(caveatDetails)).thenReturn(placeholders);
        when(pdfManagementService.generateDocmosisDocumentAndUpload(placeholders, CAVEAT_COVERSHEET)).thenReturn(coversheet);
        when(pdfManagementService.generateDocmosisDocumentAndUpload(placeholders, CAVEAT_RAISED)).thenReturn(caveatRaised);
        caveatCallbackResponse = CaveatCallbackResponse.builder().caveatData(responseCaveatData).build();
        when(caveatCallbackResponseTransformer.caveatRaised(caveatCallbackRequest, documents, null)).thenReturn(caveatCallbackResponse);

        caveatNotificationService.caveatRaise(caveatCallbackRequest);

        assertEquals(2, caveatCallbackResponse.getCaveatData().getNotificationsGenerated().size());
    }

    @Test
    public void testCaveatRaiseWithNoEmailBulkPrintValidSendLetter() throws NotificationClientException {
        caveatData = CaveatData.builder()
                .caveatRaisedEmailNotificationRequested("No")
                .sendToBulkPrintRequested("Yes")
                .build();

        responseCaveatData = ResponseCaveatData.builder()
                .registryLocation("leeds")
                .caveatorEmailAddress("test@test.com")
                .deceasedForenames("name")
                .deceasedSurname("name")
                .caveatRaisedEmailNotificationRequested("Yes")
                .notificationsGenerated(DOCUMENTS_LIST_CAVEAT_RAISED)
                .build();


        documents.add(coversheet);
        documents.add(caveatRaised);

        caveatCallbackResponse = CaveatCallbackResponse.builder().caveatData(responseCaveatData).build();
        caveatDetails = new CaveatDetails(caveatData, LAST_MODIFIED, ID);
        caveatCallbackRequest = new CaveatCallbackRequest(caveatDetails);
        SendLetterResponse sendLetterResponse = new SendLetterResponse(UUID.randomUUID());
        when(caveatDocmosisService.caseDataAsPlaceholders(caveatDetails)).thenReturn(placeholders);
        when(pdfManagementService.generateDocmosisDocumentAndUpload(placeholders, CAVEAT_COVERSHEET)).thenReturn(coversheet);
        when(pdfManagementService.generateDocmosisDocumentAndUpload(placeholders, CAVEAT_RAISED)).thenReturn(caveatRaised);
        when(bulkPrintService.sendToBulkPrintForCaveat(caveatCallbackRequest, caveatRaised, coversheet)).thenReturn(sendLetterResponse);
        when(eventValidationService.validateCaveatBulkPrintResponse(eq(sendLetterResponse.letterId.toString()), any(List.class)))
                .thenReturn(caveatCallbackResponse.builder().errors(new ArrayList<>()).build());

        when(caveatCallbackResponseTransformer.caveatRaised(caveatCallbackRequest, documents, null)).thenReturn(caveatCallbackResponse);

        caveatNotificationService.caveatRaise(caveatCallbackRequest);

        assertEquals(2, caveatCallbackResponse.getCaveatData().getNotificationsGenerated().size());
    }

    @Test
    public void testSolicitorCaveatRaiseWithEmail() throws NotificationClientException {
        caveatData = CaveatData.builder()
            .caveatRaisedEmailNotificationRequested("Yes")
            .caveatorEmailAddress("caveator@email.com")
            .applicationType(ApplicationType.SOLICITOR)
            .build();

        documents.add(sentEmail);

        responseCaveatData = ResponseCaveatData.builder()
            .notificationsGenerated(DOCUMENTS_LIST)
            .build();

        caveatDetails = new CaveatDetails(caveatData, LAST_MODIFIED, ID);
        caveatCallbackRequest = new CaveatCallbackRequest(caveatDetails);

        when(eventValidationService.validateCaveatRequest(any(CaveatCallbackRequest.class), any(List.class)))
            .thenReturn(caveatCallbackResponse.builder().errors(new ArrayList<>()).build());
        when(notificationService.sendCaveatEmail(State.CAVEAT_RAISED, caveatDetails)).thenReturn(Document.builder()
            .documentFileName(SENT_EMAIL_FILE_NAME).build());

        caveatCallbackResponse = CaveatCallbackResponse.builder().caveatData(responseCaveatData).build();
        when(caveatCallbackResponseTransformer.caveatRaised(caveatCallbackRequest, documents, null)).thenReturn(caveatCallbackResponse);

        caveatNotificationService.caveatRaise(caveatCallbackRequest);

        assertEquals(1, caveatCallbackResponse.getCaveatData().getNotificationsGenerated().size());
    }

    @Test
    public void testSolicitorCaveatRaiseWithNoEmail() throws NotificationClientException {
        caveatData = CaveatData.builder()
            .caveatRaisedEmailNotificationRequested("Yes")
            .applicationType(ApplicationType.SOLICITOR)
            .build();

        documents.add(sentEmail);

        responseCaveatData = ResponseCaveatData.builder()
            .notificationsGenerated(DOCUMENTS_LIST)
            .build();

        caveatDetails = new CaveatDetails(caveatData, LAST_MODIFIED, ID);
        caveatCallbackRequest = new CaveatCallbackRequest(caveatDetails);

        when(eventValidationService.validateCaveatRequest(any(CaveatCallbackRequest.class), any(List.class)))
            .thenReturn(caveatCallbackResponse.builder().errors(new ArrayList<>()).build());
        when(notificationService.sendCaveatEmail(State.CAVEAT_RAISED, caveatDetails)).thenReturn(Document.builder()
            .documentFileName(SENT_EMAIL_FILE_NAME).build());

        caveatCallbackResponse = CaveatCallbackResponse.builder().caveatData(responseCaveatData).build();
        when(caveatCallbackResponseTransformer.caveatRaised(caveatCallbackRequest, documents, null)).thenReturn(caveatCallbackResponse);

        caveatNotificationService.caveatRaise(caveatCallbackRequest);

        assertEquals(1, caveatCallbackResponse.getCaveatData().getNotificationsGenerated().size());
    }

    @Test
    public void testGenerateExpiryDateWithCaveatorEmailAddress() throws NotificationClientException {
        caveatData = CaveatData.builder()
                .caveatRaisedEmailNotificationRequested("Yes")
                .caveatorEmailAddress("caveator@email.com")
                .applicationType(ApplicationType.SOLICITOR)
                .build();

        documents.add(sentEmail);

        responseCaveatData = ResponseCaveatData.builder()
                .notificationsGenerated(DOCUMENTS_LIST)
                .expiryDate(LocalDate.now().plusMonths(CAVEAT_LIFESPAN).toString())
                .build();

        caveatDetails = new CaveatDetails(caveatData, LAST_MODIFIED, ID);
        caveatCallbackRequest = new CaveatCallbackRequest(caveatDetails);

        when(eventValidationService.validateCaveatRequest(any(CaveatCallbackRequest.class), any(List.class)))
                .thenReturn(caveatCallbackResponse.builder().errors(new ArrayList<>()).build());
        when(notificationService.sendCaveatEmail(State.CAVEAT_RAISED, caveatDetails)).thenReturn(Document.builder()
                .documentFileName(SENT_EMAIL_FILE_NAME).build());

        caveatCallbackResponse = CaveatCallbackResponse.builder().caveatData(responseCaveatData).build();
        when(caveatCallbackResponseTransformer.caveatRaised(caveatCallbackRequest, documents, null)).thenReturn(caveatCallbackResponse);

        caveatNotificationService.caveatRaise(caveatCallbackRequest);

        assertEquals(LocalDate.now().plusMonths(CAVEAT_LIFESPAN).toString(), caveatCallbackResponse.getCaveatData().getExpiryDate());
    }

    @Test
    public void testGenerateExpiryDateWithoutCaveatorEmailAddress() throws NotificationClientException {
        caveatData = CaveatData.builder()
                .caveatRaisedEmailNotificationRequested("Yes")
                .applicationType(ApplicationType.SOLICITOR)
                .build();

        documents.add(sentEmail);

        responseCaveatData = ResponseCaveatData.builder()
                .notificationsGenerated(DOCUMENTS_LIST)
                .expiryDate(LocalDate.now().plusMonths(CAVEAT_LIFESPAN).toString())
                .build();

        caveatDetails = new CaveatDetails(caveatData, LAST_MODIFIED, ID);
        caveatCallbackRequest = new CaveatCallbackRequest(caveatDetails);

        when(eventValidationService.validateCaveatRequest(any(CaveatCallbackRequest.class), any(List.class)))
                .thenReturn(caveatCallbackResponse.builder().errors(new ArrayList<>()).build());
        when(notificationService.sendCaveatEmail(State.CAVEAT_RAISED, caveatDetails)).thenReturn(Document.builder()
                .documentFileName(SENT_EMAIL_FILE_NAME).build());

        caveatCallbackResponse = CaveatCallbackResponse.builder().caveatData(responseCaveatData).build();
        when(caveatCallbackResponseTransformer.caveatRaised(caveatCallbackRequest, documents, null)).thenReturn(caveatCallbackResponse);

        caveatNotificationService.caveatRaise(caveatCallbackRequest);

        assertEquals(LocalDate.now().plusMonths(CAVEAT_LIFESPAN).toString(), caveatCallbackResponse.getCaveatData().getExpiryDate());
    }

    @Test
    public void testCaveatExtendWithError() throws NotificationClientException {
        caveatData = CaveatData.builder()
            .caveatRaisedEmailNotificationRequested("Yes")
            .build();

        documents.add(sentEmail);

        responseCaveatData = ResponseCaveatData.builder()
            .notificationsGenerated(DOCUMENTS_LIST)
            .build();

        caveatDetails = new CaveatDetails(caveatData, LAST_MODIFIED, ID);
        caveatCallbackRequest = new CaveatCallbackRequest(caveatDetails);

        caveatCallbackResponse = caveatCallbackResponse.builder().errors(new ArrayList<>(Arrays.asList("error1"))).build();
        when(eventValidationService.validateCaveatRequest(any(CaveatCallbackRequest.class), any(List.class)))
            .thenReturn(caveatCallbackResponse);

        caveatNotificationService.caveatExtend(caveatCallbackRequest);

        assertEquals(1, caveatCallbackResponse.getErrors().size());
    }

    @Test
    public void testCaveatExtendWithEmail() throws NotificationClientException {
        caveatData = CaveatData.builder()
            .caveatRaisedEmailNotificationRequested("Yes")
            .build();

        documents.add(sentEmail);

        responseCaveatData = ResponseCaveatData.builder()
            .notificationsGenerated(DOCUMENTS_LIST)
            .build();

        caveatDetails = new CaveatDetails(caveatData, LAST_MODIFIED, ID);
        caveatCallbackRequest = new CaveatCallbackRequest(caveatDetails);

        when(eventValidationService.validateCaveatRequest(any(CaveatCallbackRequest.class), any(List.class)))
            .thenReturn(caveatCallbackResponse.builder().errors(new ArrayList<>()).build());
        when(notificationService.sendCaveatEmail(State.CAVEAT_EXTEND, caveatDetails)).thenReturn(Document.builder()
            .documentFileName(SENT_EMAIL_FILE_NAME).build());

        caveatCallbackResponse = CaveatCallbackResponse.builder().caveatData(responseCaveatData).build();
        when(caveatCallbackResponseTransformer.caveatExtendExpiry(caveatCallbackRequest, documents, null))
                .thenReturn(caveatCallbackResponse);

        caveatNotificationService.caveatExtend(caveatCallbackRequest);

        assertEquals(1, caveatCallbackResponse.getCaveatData().getNotificationsGenerated().size());
    }

    @Test
    public void testCaveatExtendWithNoEmail() throws NotificationClientException {
        caveatData = CaveatData.builder()
            .caveatRaisedEmailNotificationRequested("No")
            .build();

        documents.add(null);
        documents.add(null);

        responseCaveatData = ResponseCaveatData.builder()
            .notificationsGenerated(DOCUMENTS_LIST)
            .build();

        caveatDetails = new CaveatDetails(caveatData, LAST_MODIFIED, ID);
        caveatCallbackRequest = new CaveatCallbackRequest(caveatDetails);
        when(caveatCallbackResponseTransformer.transformResponseWithNoChanges(caveatCallbackRequest)).thenReturn(caveatCallbackResponse);
        when(caveatCallbackResponseTransformer.caveatExtendExpiry(caveatCallbackRequest, documents, null)).thenReturn(caveatCallbackResponse);

        CaveatCallbackResponse response = caveatNotificationService.caveatExtend(caveatCallbackRequest);

        assertEquals(caveatCallbackResponse, response);
    }

    @Test
    public void testCaveatExtendWithNoEmailNoBP() throws NotificationClientException {
        caveatData = CaveatData.builder()
            .caveatRaisedEmailNotificationRequested("No")
            .sendToBulkPrintRequested("No")
            .build();

        responseCaveatData = ResponseCaveatData.builder()
            .notificationsGenerated(DOCUMENTS_LIST_CAVEAT_EXTENDED)
            .build();

        documents.add(coversheet);
        documents.add(caveatExtended);

        caveatDetails = new CaveatDetails(caveatData, LAST_MODIFIED, ID);
        caveatCallbackRequest = new CaveatCallbackRequest(caveatDetails);

        when(caveatDocmosisService.caseDataAsPlaceholders(caveatDetails)).thenReturn(placeholders);
        when(pdfManagementService.generateDocmosisDocumentAndUpload(placeholders, CAVEAT_COVERSHEET)).thenReturn(coversheet);
        when(pdfManagementService.generateDocmosisDocumentAndUpload(placeholders, CAVEAT_EXTENDED)).thenReturn(caveatExtended);
        caveatCallbackResponse = CaveatCallbackResponse.builder().caveatData(responseCaveatData).build();
        when(caveatCallbackResponseTransformer.caveatExtendExpiry(caveatCallbackRequest, documents, null))
                .thenReturn(caveatCallbackResponse);

        caveatNotificationService.caveatExtend(caveatCallbackRequest);

        assertEquals(2, caveatCallbackResponse.getCaveatData().getNotificationsGenerated().size());
    }

    @Test
    public void testCaveatExtendWithNoEmailBulkPrintValidSendLetter() throws NotificationClientException {
        caveatData = CaveatData.builder()
            .caveatRaisedEmailNotificationRequested("No")
            .sendToBulkPrintRequested("Yes")
            .build();

        responseCaveatData = ResponseCaveatData.builder()
            .registryLocation("leeds")
            .caveatorEmailAddress("test@test.com")
            .deceasedForenames("name")
            .deceasedSurname("name")
            .caveatRaisedEmailNotificationRequested("Yes")
            .notificationsGenerated(DOCUMENTS_LIST_CAVEAT_EXTENDED)
            .build();


        documents.add(coversheet);
        documents.add(caveatExtended);

        caveatCallbackResponse = CaveatCallbackResponse.builder().caveatData(responseCaveatData).build();
        caveatDetails = new CaveatDetails(caveatData, LAST_MODIFIED, ID);
        caveatCallbackRequest = new CaveatCallbackRequest(caveatDetails);
        SendLetterResponse sendLetterResponse = new SendLetterResponse(UUID.randomUUID());
        when(caveatDocmosisService.caseDataAsPlaceholders(caveatDetails)).thenReturn(placeholders);
        when(pdfManagementService.generateDocmosisDocumentAndUpload(placeholders, CAVEAT_COVERSHEET)).thenReturn(coversheet);
        when(pdfManagementService.generateDocmosisDocumentAndUpload(placeholders, CAVEAT_EXTENDED)).thenReturn(caveatExtended);
        when(bulkPrintService.sendToBulkPrintForCaveat(caveatCallbackRequest, caveatExtended, coversheet)).thenReturn(sendLetterResponse);
        when(eventValidationService.validateCaveatBulkPrintResponse(eq(sendLetterResponse.letterId.toString()), any(List.class)))
            .thenReturn(caveatCallbackResponse.builder().errors(new ArrayList<>()).build());

        when(caveatCallbackResponseTransformer.caveatExtendExpiry(caveatCallbackRequest, documents, sendLetterResponse.letterId.toString()))
            .thenReturn(caveatCallbackResponse);

        caveatNotificationService.caveatExtend(caveatCallbackRequest);

        assertEquals(2, caveatCallbackResponse.getCaveatData().getNotificationsGenerated().size());
    }

    @Test
    public void testCaveatExtendWithNoEmailBulkPrintNullSendLetter() throws NotificationClientException {
        caveatData = CaveatData.builder()
            .caveatRaisedEmailNotificationRequested("No")
            .sendToBulkPrintRequested("Yes")
            .build();

        responseCaveatData = ResponseCaveatData.builder()
            .registryLocation("leeds")
            .caveatorEmailAddress("test@test.com")
            .deceasedForenames("name")
            .deceasedSurname("name")
            .caveatRaisedEmailNotificationRequested("Yes")
            .notificationsGenerated(DOCUMENTS_LIST_CAVEAT_EXTENDED)
            .build();


        documents.add(coversheet);
        documents.add(caveatExtended);

        caveatCallbackResponse = CaveatCallbackResponse.builder().caveatData(responseCaveatData).build();
        caveatDetails = new CaveatDetails(caveatData, LAST_MODIFIED, ID);
        caveatCallbackRequest = new CaveatCallbackRequest(caveatDetails);
        when(caveatDocmosisService.caseDataAsPlaceholders(caveatDetails)).thenReturn(placeholders);
        when(pdfManagementService.generateDocmosisDocumentAndUpload(placeholders, CAVEAT_COVERSHEET)).thenReturn(coversheet);
        when(pdfManagementService.generateDocmosisDocumentAndUpload(placeholders, CAVEAT_EXTENDED)).thenReturn(caveatExtended);
        when(eventValidationService.validateCaveatBulkPrintResponse(eq(null), any(List.class)))
            .thenReturn(caveatCallbackResponse.builder().errors(new ArrayList<>()).build());

        when(caveatCallbackResponseTransformer.caveatExtendExpiry(caveatCallbackRequest, documents, null))
            .thenReturn(caveatCallbackResponse);

        caveatNotificationService.caveatExtend(caveatCallbackRequest);

        assertEquals(2, caveatCallbackResponse.getCaveatData().getNotificationsGenerated().size());
    }

    @Test
    public void testWithDrawEmail() throws NotificationClientException {
        CaveatData caveatData = CaveatData.builder()
                .caveatRaisedEmailNotificationRequested("Yes")
                .caveatorEmailAddress("test@test.com").build();

        caveatDetails = new CaveatDetails(caveatData, LAST_MODIFIED, ID);
        caveatCallbackRequest = new CaveatCallbackRequest(caveatDetails);
        CaveatCallbackResponse caveatCallbackResponse = CaveatCallbackResponse.builder().errors(new ArrayList<>()).build();
        Document document = Document.builder().build();
        when(notificationService.sendCaveatEmail(eq(State.CAVEAT_WITHDRAW), eq(caveatDetails))).thenReturn(document);
        when(eventValidationService.validateCaveatRequest(eq(caveatCallbackRequest), isA(List.class))).thenReturn(caveatCallbackResponse);


        caveatNotificationService.withdraw(caveatCallbackRequest);

        ArgumentCaptor<List<Document>> listArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(caveatCallbackResponseTransformer).withdrawn(eq(caveatCallbackRequest), listArgumentCaptor.capture(), isNull());
        List<Document> passedDocument = listArgumentCaptor.getValue();

        assertEquals("Document matched", passedDocument.get(0), document);
        verify(notificationService).sendCaveatEmail(eq(State.CAVEAT_WITHDRAW), eq(caveatDetails));
        verify(eventValidationService).validateCaveatRequest(eq(caveatCallbackRequest), isA(List.class));
    }

    @Test
    public void testWithdrawnBluckPrint() throws NotificationClientException {
        CaveatData caveatData = CaveatData.builder()
                .caveatRaisedEmailNotificationRequested("No")
                .caveatorEmailAddress("test@test.com")
                .sendToBulkPrintRequested("Yes")
                .build();

        caveatDetails = new CaveatDetails(caveatData, LAST_MODIFIED, ID);
        caveatCallbackRequest = new CaveatCallbackRequest(caveatDetails);
        CaveatCallbackResponse caveatCallbackResponse = CaveatCallbackResponse.builder().errors(new ArrayList<>()).build();
        Document document = Document.builder().documentFileName("withdrawn.doc").build();

        when(caveatDocmosisService.caseDataAsPlaceholders(eq(caveatCallbackRequest.getCaseDetails()))).thenReturn(placeholders);
        when(pdfManagementService.generateDocmosisDocumentAndUpload(eq(placeholders),  eq(DocumentType.CAVEAT_COVERSHEET)))
                .thenReturn(coversheet);
        when(pdfManagementService.generateDocmosisDocumentAndUpload(eq(placeholders),  eq(DocumentType.CAVEAT_WITHDRAWN)))
                .thenReturn(document);

        SendLetterResponse sendLetterResponse = new SendLetterResponse(UUID.randomUUID());
        when(bulkPrintService.sendToBulkPrintForCaveat(eq(caveatCallbackRequest), eq(document), eq(coversheet))).thenReturn(sendLetterResponse);
        when(eventValidationService.validateCaveatBulkPrintResponse(eq(sendLetterResponse.letterId.toString()), any()))
                .thenReturn(caveatCallbackResponse);

        caveatNotificationService.withdraw(caveatCallbackRequest);

        ArgumentCaptor<List<Document>> listArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(caveatCallbackResponseTransformer).withdrawn(eq(caveatCallbackRequest),
                listArgumentCaptor.capture(), eq(sendLetterResponse.letterId.toString()));
        List<Document> passedDocument = listArgumentCaptor.getValue();

        assertEquals("Document size", 2, passedDocument.size());
        verify(notificationService, never()).sendCaveatEmail(eq(State.CAVEAT_WITHDRAW), eq(caveatDetails));
        verify(eventValidationService, never()).validateCaveatRequest(eq(caveatCallbackRequest), isA(List.class));
        verify(bulkPrintService).sendToBulkPrintForCaveat(eq(caveatCallbackRequest), eq(document), eq(coversheet));
        verify(eventValidationService).validateCaveatBulkPrintResponse(eq(sendLetterResponse.letterId.toString()),
                any());
    }

    @Test
    public void testWithdrawnBluckPrintValidationFailure() throws NotificationClientException {
        CaveatData caveatData = CaveatData.builder()
                .caveatRaisedEmailNotificationRequested("No")
                .caveatorEmailAddress("test@test.com")
                .sendToBulkPrintRequested("Yes")
                .build();

        caveatDetails = new CaveatDetails(caveatData, LAST_MODIFIED, ID);
        caveatCallbackRequest = new CaveatCallbackRequest(caveatDetails);
        CaveatCallbackResponse caveatCallbackResponse = CaveatCallbackResponse.builder()
                .errors(Arrays.asList("bulkPrintResponseNull")).build();
        Document document = Document.builder().documentFileName("withdrawn.doc").build();

        when(caveatDocmosisService.caseDataAsPlaceholders(eq(caveatCallbackRequest.getCaseDetails()))).thenReturn(placeholders);
        when(pdfManagementService.generateDocmosisDocumentAndUpload(eq(placeholders),  eq(DocumentType.CAVEAT_COVERSHEET)))
                .thenReturn(coversheet);
        when(pdfManagementService.generateDocmosisDocumentAndUpload(eq(placeholders),  eq(DocumentType.CAVEAT_WITHDRAWN)))
                .thenReturn(document);

        SendLetterResponse sendLetterResponse = new SendLetterResponse(UUID.randomUUID());
        when(bulkPrintService.sendToBulkPrintForCaveat(eq(caveatCallbackRequest), eq(document), eq(coversheet))).thenReturn(sendLetterResponse);
        when(eventValidationService.validateCaveatBulkPrintResponse(eq(sendLetterResponse.letterId.toString()), any()))
                .thenReturn(caveatCallbackResponse);

        caveatNotificationService.withdraw(caveatCallbackRequest);

        verify(caveatCallbackResponseTransformer, never()).withdrawn(eq(caveatCallbackRequest), anyList(), isNull());
        verify(notificationService, never()).sendCaveatEmail(eq(State.CAVEAT_WITHDRAW), eq(caveatDetails));
        verify(eventValidationService, never()).validateCaveatRequest(eq(caveatCallbackRequest), isA(List.class));
        verify(bulkPrintService).sendToBulkPrintForCaveat(eq(caveatCallbackRequest), eq(document), eq(coversheet));
        verify(eventValidationService).validateCaveatBulkPrintResponse(eq(sendLetterResponse.letterId.toString()),
                any());
    }

    @Test
    public void testWithdrawnWithoutEmailOrBulkPrint() throws NotificationClientException {
        CaveatData caveatData = CaveatData.builder()
                .caveatRaisedEmailNotificationRequested("No")
                .caveatorEmailAddress("test@test.com")
                .sendToBulkPrintRequested("No")
                .build();

        caveatDetails = new CaveatDetails(caveatData, LAST_MODIFIED, ID);
        caveatCallbackRequest = new CaveatCallbackRequest(caveatDetails);
        CaveatCallbackResponse caveatCallbackResponse = CaveatCallbackResponse.builder().errors(new ArrayList<>()).build();
        Document document = Document.builder().documentFileName("withdrawn.doc").build();

        when(caveatDocmosisService.caseDataAsPlaceholders(eq(caveatCallbackRequest.getCaseDetails()))).thenReturn(placeholders);
        when(pdfManagementService.generateDocmosisDocumentAndUpload(eq(placeholders),  eq(DocumentType.CAVEAT_COVERSHEET)))
                .thenReturn(coversheet);
        when(pdfManagementService.generateDocmosisDocumentAndUpload(eq(placeholders),  eq(DocumentType.CAVEAT_WITHDRAWN)))
                .thenReturn(document);


        caveatNotificationService.withdraw(caveatCallbackRequest);

        ArgumentCaptor<List<Document>> listArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(caveatCallbackResponseTransformer).withdrawn(eq(caveatCallbackRequest), listArgumentCaptor.capture(), isNull());
        List<Document> passedDocument = listArgumentCaptor.getValue();

        assertEquals("Document size", 2, passedDocument.size());
        verify(notificationService, never()).sendCaveatEmail(eq(State.CAVEAT_WITHDRAW), eq(caveatDetails));
        verify(eventValidationService, never()).validateCaveatRequest(eq(caveatCallbackRequest), isA(List.class));
        verify(bulkPrintService, never()).sendToBulkPrintForCaveat(eq(caveatCallbackRequest), eq(document), eq(coversheet));
        verify(eventValidationService, never()).validateCaveatBulkPrintResponse(any(), any());

    }

    @Test
    public void testWithDrawNotificationValidationFailure() throws NotificationClientException {
        CaveatData caveatData = CaveatData.builder()
                .caveatRaisedEmailNotificationRequested("Yes")
                .build();

        caveatDetails = new CaveatDetails(caveatData, LAST_MODIFIED, ID);
        caveatCallbackRequest = new CaveatCallbackRequest(caveatDetails);
        CaveatCallbackResponse caveatCallbackResponse = CaveatCallbackResponse.builder()
                .errors(Arrays.asList("notifyApplicantNoEmailPA")).build();
        Document document = Document.builder().build();
        when(notificationService.sendCaveatEmail(eq(State.CAVEAT_WITHDRAW), eq(caveatDetails))).thenReturn(document);
        when(eventValidationService.validateCaveatRequest(eq(caveatCallbackRequest), isA(List.class))).thenReturn(caveatCallbackResponse);


        caveatNotificationService.withdraw(caveatCallbackRequest);


        verify(caveatCallbackResponseTransformer, never()).withdrawn(eq(caveatCallbackRequest), any(), isNull());

        verify(notificationService, never()).sendCaveatEmail(eq(State.CAVEAT_WITHDRAW), eq(caveatDetails));
        verify(eventValidationService).validateCaveatRequest(eq(caveatCallbackRequest), isA(List.class));
    }
}

