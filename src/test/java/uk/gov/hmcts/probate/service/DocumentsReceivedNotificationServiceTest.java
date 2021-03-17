package uk.gov.hmcts.probate.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.probate.insights.AppInsights;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.hmcts.probate.validator.EmailAddressNotificationValidationRule;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static uk.gov.hmcts.probate.model.ApplicationType.PERSONAL;
import static uk.gov.hmcts.probate.model.ApplicationType.SOLICITOR;
import static uk.gov.hmcts.probate.model.DocumentType.SENT_EMAIL;
import static uk.gov.hmcts.probate.model.State.DOCUMENTS_RECEIVED;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DocumentsReceivedNotificationServiceTest {

    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private static final Long CASE_ID = 12345678987654321L;
    private static final String BULK_SCAN_CASE_REFERENCE = "9876654312345678";
    private static final String DOCUMENTS_RECEIVED_FILE_NAME = "documentReceived.pdf";

    private Document emailDocument;
    private CallbackRequest callbackRequest;
    private CallbackResponse callbackResponse;
    private CallbackResponse callbackResponseWithData;
    private CallbackResponse callbackResponseWithDataNoDocuments;
    private CaseDetails personalCaseDataBirmingham;
    private CaseDetails solicitorCaseDataBirmingham;
    private CaseDetails personalCaseDataBirminghamFromBulkScan;
    private CaseDetails solicitorCaseDataBirminghamFromBulkScan;
    private Map<String, Object> expectedMap;
    private List<String> errors = new ArrayList<String>();
    private List<Document> expectedOneDocument = new ArrayList<Document>();
    private List<Document> expectedNoDocuments = new ArrayList<Document>();
    private List<CollectionMember<Document>> documents = new ArrayList<>();
    private List<CollectionMember<Document>> noDocuments = new ArrayList<>();
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM Y HH:mm");

    @InjectMocks
    private DocumentsReceivedNotificationService documentsReceivedNotificationService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private PDFManagementService pdfManagementService;

    @MockBean
    private AppInsights appInsights;

    @Mock
    private EventValidationService eventValidationService;

    @Mock
    private CallbackResponseTransformer callbackResponseTransformer;

    @Mock
    private List<EmailAddressNotificationValidationRule> emailAddressNotificationValidationRules;

    @Before
    public void setup() throws IOException, NotificationClientException {
        personalCaseDataBirmingham = new CaseDetails(CaseData.builder()
                .applicationType(PERSONAL)
                .registryLocation("Birmingham")
                .primaryApplicantEmailAddress("primary@probate-test.com")
                .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
                .bulkScanCaseReference("")
                .build(), LAST_MODIFIED, CASE_ID);

        solicitorCaseDataBirmingham = new CaseDetails(CaseData.builder()
                .applicationType(SOLICITOR)
                .registryLocation("Birmingham")
                .solsSolicitorEmail("solicitor@probate-test.com")
                .solsSolicitorAppReference("1234-5678-9012")
                .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
                .bulkScanCaseReference(null)
                .build(), LAST_MODIFIED, CASE_ID);

        personalCaseDataBirminghamFromBulkScan = new CaseDetails(CaseData.builder()
                .applicationType(PERSONAL)
                .registryLocation("Birmingham")
                .primaryApplicantEmailAddress("primary@probate-test.com")
                .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
                .bulkScanCaseReference(BULK_SCAN_CASE_REFERENCE)
                .build(), LAST_MODIFIED, CASE_ID);

        solicitorCaseDataBirminghamFromBulkScan = new CaseDetails(CaseData.builder()
                .applicationType(SOLICITOR)
                .registryLocation("Birmingham")
                .solsSolicitorEmail("solicitor@probate-test.com")
                .solsSolicitorAppReference("1234-5678-9012")
                .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
                .bulkScanCaseReference(BULK_SCAN_CASE_REFERENCE)
                .build(), LAST_MODIFIED, CASE_ID);

        errors = new ArrayList<String>();
        emailDocument = Document.builder().documentFileName(DOCUMENTS_RECEIVED_FILE_NAME).documentType(SENT_EMAIL).build();
        CollectionMember<Document> collectionOfDocuments = new CollectionMember(null, emailDocument);
        documents.add(collectionOfDocuments);
        expectedOneDocument.add(emailDocument);

        ResponseCaseData reponseCaseData = ResponseCaseData.builder().probateNotificationsGenerated(documents).build();
        ResponseCaseData reponseCaseDataNoDocuments = ResponseCaseData.builder().probateNotificationsGenerated(noDocuments).build();
        callbackResponse = CallbackResponse.builder().errors(errors).build();
        callbackResponseWithData = CallbackResponse.builder().data(reponseCaseData).errors(errors).build();
        callbackResponseWithDataNoDocuments = CallbackResponse.builder().data(reponseCaseDataNoDocuments).errors(errors).build();
    }

    @Test
    public void handleDocumentReceivedPersonalNotification() throws NotificationClientException {
        callbackRequest = new CallbackRequest(personalCaseDataBirmingham);
        doReturn(callbackResponse).when(eventValidationService).validateEmailRequest(callbackRequest, emailAddressNotificationValidationRules);
        doReturn(emailDocument).when(notificationService).sendEmail(eq(DOCUMENTS_RECEIVED), any());
        doReturn(callbackResponseWithData).when(callbackResponseTransformer).addDocuments(any(), eq(expectedOneDocument), any(), any());

        CallbackResponse callbackResponse = documentsReceivedNotificationService.handleDocumentReceivedNotification(callbackRequest);

        assertEquals(1, callbackResponse.getData().getProbateNotificationsGenerated().size());
    }

    @Test
    public void handleDocumentReceivedSolicitorNotification() throws NotificationClientException {
        callbackRequest = new CallbackRequest(solicitorCaseDataBirmingham);
        doReturn(callbackResponse).when(eventValidationService).validateEmailRequest(callbackRequest, emailAddressNotificationValidationRules);
        doReturn(emailDocument).when(notificationService).sendEmail(eq(DOCUMENTS_RECEIVED), any());
        doReturn(callbackResponseWithData).when(callbackResponseTransformer).addDocuments(any(), eq(expectedOneDocument), any(), any());

        CallbackResponse callbackResponse = documentsReceivedNotificationService.handleDocumentReceivedNotification(callbackRequest);

        assertEquals(1, callbackResponse.getData().getProbateNotificationsGenerated().size());
    }

    @Test
    public void handleDisableDocumentReceivedPersonalNotificationFromBulkScan() throws NotificationClientException {
        callbackRequest = new CallbackRequest(personalCaseDataBirminghamFromBulkScan);
        doReturn(callbackResponse).when(eventValidationService).validateEmailRequest(callbackRequest, emailAddressNotificationValidationRules);
        doReturn(callbackResponseWithDataNoDocuments).when(callbackResponseTransformer).addDocuments(any(), eq(expectedNoDocuments), any(), any());

        CallbackResponse callbackResponse = documentsReceivedNotificationService.handleDocumentReceivedNotification(callbackRequest);

        assertEquals(0, callbackResponse.getData().getProbateNotificationsGenerated().size());
    }

    @Test
    public void handleDisableDocumentReceivedSolicitorNotificationFromBulkScan() throws NotificationClientException {
        callbackRequest = new CallbackRequest(solicitorCaseDataBirminghamFromBulkScan);
        doReturn(callbackResponse).when(eventValidationService).validateEmailRequest(callbackRequest, emailAddressNotificationValidationRules);
        doReturn(callbackResponseWithDataNoDocuments).when(callbackResponseTransformer).addDocuments(any(), eq(expectedNoDocuments), any(), any());

        CallbackResponse callbackResponse = documentsReceivedNotificationService.handleDocumentReceivedNotification(callbackRequest);

        assertEquals(0, callbackResponse.getData().getProbateNotificationsGenerated().size());
    }
}