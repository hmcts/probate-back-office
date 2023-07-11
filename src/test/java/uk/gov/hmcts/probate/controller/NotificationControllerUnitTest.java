package uk.gov.hmcts.probate.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.BindingResult;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.State;
import uk.gov.hmcts.probate.model.ccd.raw.ChangeOfRepresentative;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.RemovedRepresentative;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.service.BulkPrintService;
import uk.gov.hmcts.probate.service.DocumentGeneratorService;
import uk.gov.hmcts.probate.service.ScannedDocumentOrderingService;
import uk.gov.hmcts.probate.service.DocumentsReceivedNotificationService;
import uk.gov.hmcts.probate.service.EventValidationService;
import uk.gov.hmcts.probate.service.EvidenceUploadService;
import uk.gov.hmcts.probate.service.InformationRequestService;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.hmcts.probate.service.RaiseGrantOfRepresentationNotificationService;
import uk.gov.hmcts.probate.service.RedeclarationNotificationService;
import uk.gov.hmcts.probate.service.docmosis.GrantOfRepresentationDocmosisMapperService;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.hmcts.probate.transformer.CaseDataTransformer;
import uk.gov.hmcts.probate.transformer.HandOffLegacyTransformer;
import uk.gov.hmcts.probate.validator.BulkPrintValidationRule;
import uk.gov.hmcts.probate.validator.EmailAddressNotifyValidationRule;
import uk.gov.hmcts.reform.probate.model.ProbateDocument;
import uk.gov.service.notify.NotificationClientException;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.controller.CaseDataTestBuilder.ID;
import static uk.gov.hmcts.probate.controller.CaseDataTestBuilder.LAST_MODIFIED;
import static uk.gov.hmcts.probate.model.ApplicationState.CASE_PRINTED;
import static uk.gov.hmcts.probate.model.ApplicationType.SOLICITOR;
import static uk.gov.hmcts.probate.model.State.APPLICATION_RECEIVED;
import static uk.gov.hmcts.probate.model.State.APPLICATION_RECEIVED_NO_DOCS;
import static uk.gov.hmcts.probate.model.State.DOCUMENTS_RECEIVED;
import static uk.gov.hmcts.probate.model.State.GRANT_RAISED;
import static uk.gov.hmcts.probate.model.State.NOC;

@ExtendWith(SpringExtension.class)
class NotificationControllerUnitTest {

    @Mock
    DocumentGeneratorService documentGeneratorService;
    @Mock
    NotificationService notificationService;
    @Mock
    CallbackResponseTransformer callbackResponseTransformer;
    @Mock
    EventValidationService eventValidationService;
    @Mock
    List<EmailAddressNotifyValidationRule> emailAddressNotifyValidationRules;
    @Mock
    PDFManagementService pdfManagementService;
    @Mock
    BulkPrintService bulkPrintService;
    @Mock
    List<BulkPrintValidationRule> bulkPrintValidationRules;
    @Mock
    GrantOfRepresentationDocmosisMapperService gorDocmosisService;
    @Mock
    InformationRequestService informationRequestService;
    @Mock
    RedeclarationNotificationService redeclarationNotificationService;
    @Mock
    DocumentsReceivedNotificationService documentsReceivedNotificationService;
    @Mock
    CaseDataTransformer caseDataTransformer;
    @Mock
    ScannedDocumentOrderingService scannedDocumentOrderingService;
    @Mock
    RaiseGrantOfRepresentationNotificationService raiseGrantOfRepresentationNotificationService;
    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private BindingResult bindingResultMock;
    @Mock
    private HandOffLegacyTransformer handOffLegacyTransformerMock;
    @Mock
    EvidenceUploadService evidenceUploadService;

    @InjectMocks
    NotificationController notificationController;

    @Captor
    private ArgumentCaptor<List<Document>> documents;


    private CallbackRequest callbackRequest;
    private CallbackResponse callbackResponse;
    private Document document;

    @Test
    void shouldSendApplicationReceived() throws NotificationClientException {
        setUpMocks(APPLICATION_RECEIVED);
        ResponseEntity<ProbateDocument> stringResponseEntity =
            notificationController.sendApplicationReceivedNotification(callbackRequest);
        assertThat(stringResponseEntity.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    void shouldSendApplicationReceivedNoDocsRequired() throws NotificationClientException {
        setUpMocks(APPLICATION_RECEIVED_NO_DOCS);
        CaseDetails caseDetails = new CaseDetails(CaseData.builder()
            .paperForm("No")
            .primaryApplicantNotRequiredToSendDocuments("Yes")
            .primaryApplicantEmailAddress("1@1.com")
            .caseType("intestacy")
            .build(), LAST_MODIFIED, ID);
        callbackRequest = new CallbackRequest(caseDetails);
        ResponseEntity<ProbateDocument> stringResponseEntity =
            notificationController.sendApplicationReceivedNotification(callbackRequest);
        assertThat(stringResponseEntity.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    void shouldNotSendApplicationReceivedForPaper() throws NotificationClientException {
        CaseDetails caseDetails = new CaseDetails(CaseData.builder().paperForm("Yes").build(), LAST_MODIFIED, ID);
        callbackRequest = new CallbackRequest(caseDetails);

        ResponseEntity<ProbateDocument> stringResponseEntity =
            notificationController.sendApplicationReceivedNotification(callbackRequest);
        assertThat(stringResponseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(stringResponseEntity.getBody(), equalTo(null));
        verify(eventValidationService, times(0)).validateEmailRequest(any(), any());
    }

    @Test
    void shouldNotSendApplicationReceivedForPCitizenPaperAsNull() throws NotificationClientException {
        CaseDetails caseDetails = new CaseDetails(CaseData.builder().paperForm(null).build(), LAST_MODIFIED, ID);
        callbackRequest = new CallbackRequest(caseDetails);

        ResponseEntity<ProbateDocument> stringResponseEntity =
            notificationController.sendApplicationReceivedNotification(callbackRequest);
        assertThat(stringResponseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(stringResponseEntity.getBody(), equalTo(null));
        verify(eventValidationService, times(0)).validateEmailRequest(any(), any());
    }

    @Test
    void shouldAddDocumentEvenIfNoEmailAddressPresent() throws NotificationClientException {
        setUpMocks(APPLICATION_RECEIVED);
        CaseDetails caseDetails =
            new CaseDetails(CaseDataTestBuilder.withDefaultsAndNoPrimaryApplicantEmailAddress().build(), LAST_MODIFIED,
                ID);
        callbackRequest = new CallbackRequest(caseDetails);
        ResponseEntity<ProbateDocument> stringResponseEntity =
            notificationController.sendApplicationReceivedNotification(callbackRequest);
        assertThat(stringResponseEntity.getStatusCode(), is(HttpStatus.OK));
        verifyNoMoreInteractions(notificationService);
    }

    @Test
    void shouldHandleErrorsFromSendApplicationReceived() throws NotificationClientException {
        setUpMocks(APPLICATION_RECEIVED, "This is an error", "This is another error");

        ResponseEntity<ProbateDocument> stringResponseEntity =
            notificationController.sendApplicationReceivedNotification(callbackRequest);
        assertThat(stringResponseEntity.getStatusCode(), is(HttpStatus.OK));
        verifyNoMoreInteractions(callbackResponseTransformer);
    }

    @Test
    void shouldSendDocumentsReceived() throws NotificationClientException {
        setUpMocks(DOCUMENTS_RECEIVED);
        notificationController.sendDocumentReceivedNotification(callbackRequest);
        verify(documentsReceivedNotificationService).handleDocumentReceivedNotification(callbackRequest);
    }


    @Test
    void shouldUpdateEvidenceAddedDate() throws NotificationClientException {
        setUpMocks(APPLICATION_RECEIVED);
        HttpServletRequest requestMock = mock(HttpServletRequest.class);
        BindingResult bindingResultMock = mock(BindingResult.class);
        ResponseEntity<CallbackResponse> responseEntity =
                notificationController.startDelayedNotificationPeriod(callbackRequest, bindingResultMock, requestMock);
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
    }

    private void setUpMocks(State state, String... errors) throws NotificationClientException {
        CaseDetails caseDetails = new CaseDetails(CaseDataTestBuilder.withDefaults().build(), LAST_MODIFIED, ID);
        callbackRequest = new CallbackRequest(caseDetails);
        document = Document.builder()
                .documentDateAdded(LocalDate.now())
                .documentFileName("fileName")
                .documentGeneratedBy("generatedBy")
                .documentLink(
                        DocumentLink.builder().documentUrl("url").documentFilename("file")
                                .documentBinaryUrl("binary").build())
                .documentType(DocumentType.DIGITAL_GRANT)
                .build();
        callbackResponse = CallbackResponse.builder().errors(Collections.EMPTY_LIST).build();
        when(eventValidationService.validateEmailRequest(any(), any())).thenReturn(callbackResponse);
        when(notificationService.sendEmail(any(), any())).thenReturn(document);
        when(raiseGrantOfRepresentationNotificationService.handleGrantReceivedNotification(any()))
                .thenReturn(callbackResponse);

    }

    private void setUpMocks(State state) throws NotificationClientException {
        this.setUpMocks(state, new String[0]);
    }

    @Test
    void shouldTransformEvidenceHandledGrantReceived() throws NotificationClientException {
        setUpMocks(GRANT_RAISED);
        ResponseEntity<CallbackResponse> callbackResponse =
                notificationController.sendGrantReceivedNotification(callbackRequest);
        verify(caseDataTransformer).transformCaseDataForEvidenceHandledForCreateBulkscan(callbackRequest);
        assertThat(callbackResponse.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    void shouldTransformForAttachDocs() throws NotificationClientException {
        setUpMocks(APPLICATION_RECEIVED);
        ResponseEntity<CallbackResponse> callbackResponse =
                notificationController.startDelayedNotificationPeriod(callbackRequest, bindingResultMock,
                httpServletRequest);
        verify(caseDataTransformer).transformCaseDataForAttachDocuments(callbackRequest);
        assertThat(callbackResponse.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    void shouldOrderScannedDocumentsForGrantReceived() throws NotificationClientException {
        List scannedDocMock = mock(List.class);
        CaseData caseData = CaseData.builder()
                .scannedDocuments(scannedDocMock)
                .build();
        CallbackRequest callbackRequest = new CallbackRequest(new CaseDetails(caseData, null, 0L));
        ResponseEntity<CallbackResponse> callbackResponse =
                notificationController.sendGrantReceivedNotification(callbackRequest);
        verify(scannedDocumentOrderingService)
                .orderScannedDocuments(callbackRequest.getCaseDetails().getData());
        assertThat(callbackResponse.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    void shouldOrderScannedDocumentsForAttachDocs() throws NotificationClientException {
        List scannedDocMock = mock(List.class);
        CaseData caseData = CaseData.builder()
                .scannedDocuments(scannedDocMock)
                .build();
        CallbackRequest callbackRequest = new CallbackRequest(new CaseDetails(caseData, null, 0L));
        ResponseEntity<CallbackResponse> callbackResponse =
                notificationController.startDelayedNotificationPeriod(callbackRequest, bindingResultMock,
                        httpServletRequest);
        verify(scannedDocumentOrderingService)
                .orderScannedDocuments(callbackRequest.getCaseDetails().getData());
        assertThat(callbackResponse.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    void shouldSendNotificationForAttachDocsCasePrinted() throws NotificationClientException {
        setUpMocks(APPLICATION_RECEIVED);
        CaseDetails caseDetails = new CaseDetails(CaseData.builder().primaryApplicantEmailAddress("pa@probate-test.com")
                .build(), LAST_MODIFIED, ID);
        caseDetails.setState(CASE_PRINTED.getId());
        callbackRequest = new CallbackRequest(caseDetails);
        ResponseEntity<CallbackResponse> callbackResponse =
                notificationController.startDelayedNotificationPeriod(callbackRequest, bindingResultMock,
                        httpServletRequest);
        verify(notificationService).sendEmail(DOCUMENTS_RECEIVED, callbackRequest.getCaseDetails());
        verify(caseDataTransformer).transformCaseDataForDocsReceivedNotificationSent(callbackRequest);
        assertThat(callbackResponse.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    void shouldNotSendNotificationForAttachDocsNotCasePrinted() throws NotificationClientException {
        setUpMocks(APPLICATION_RECEIVED);
        CaseDetails caseDetails = new CaseDetails(CaseData.builder().build(), LAST_MODIFIED, ID);
        callbackRequest = new CallbackRequest(caseDetails);
        ResponseEntity<CallbackResponse> callbackResponse =
                notificationController.startDelayedNotificationPeriod(callbackRequest, bindingResultMock,
                        httpServletRequest);
        verify(notificationService, times(0)).sendEmail(DOCUMENTS_RECEIVED, callbackRequest.getCaseDetails());
        verify(caseDataTransformer, times(0)).transformCaseDataForDocsReceivedNotificationSent(callbackRequest);
        assertThat(callbackResponse.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    void shouldNotSendNotificationForAttachDocsNoEmailAddressPresent() throws NotificationClientException {
        setUpMocks(APPLICATION_RECEIVED);
        CaseDetails caseDetails =
            new CaseDetails(CaseDataTestBuilder.withDefaultsAndNoPrimaryApplicantEmailAddress().build(), LAST_MODIFIED,
                    ID);
        callbackRequest = new CallbackRequest(caseDetails);
        ResponseEntity<CallbackResponse> callbackResponse =
                notificationController.startDelayedNotificationPeriod(callbackRequest, bindingResultMock,
                        httpServletRequest);
        verify(notificationService, times(0)).sendEmail(DOCUMENTS_RECEIVED, callbackRequest.getCaseDetails());
        assertThat(callbackResponse.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    void shouldStartAwaitingDocumentationNotificationPeriodNoEvidenceHandled() throws NotificationClientException {
        setUpMocks(APPLICATION_RECEIVED_NO_DOCS);
        CaseDetails caseDetails = new CaseDetails(CaseData.builder()
                .paperForm("No")
                .primaryApplicantNotRequiredToSendDocuments("Yes")
                .primaryApplicantEmailAddress("1@1.com")
                .primaryApplicantNotRequiredToSendDocuments("Yes")
                .caseType("intestacy")
                .build(), LAST_MODIFIED, ID);
        callbackRequest = new CallbackRequest(caseDetails);
        ResponseEntity<ProbateDocument> stringResponseEntity =
                notificationController.sendApplicationReceivedNotification(callbackRequest);
        verify(notificationService, times(0)).startAwaitingDocumentationNotificationPeriod(any());
        assertThat(stringResponseEntity.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    void shouldNotStartAwaitingDocumentationNotificationPeriodNullEvidenceHandled() throws NotificationClientException {
        setUpMocks(APPLICATION_RECEIVED);
        ResponseEntity<ProbateDocument> stringResponseEntity =
                notificationController.sendApplicationReceivedNotification(callbackRequest);
        verify(notificationService).startAwaitingDocumentationNotificationPeriod(any());
        assertThat(stringResponseEntity.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    void shouldSendNocEmail() throws NotificationClientException {
        setUpMocks(NOC);
        CollectionMember<ChangeOfRepresentative> representative =
                new CollectionMember<>(null, ChangeOfRepresentative
                        .builder().removedRepresentative(RemovedRepresentative.builder()
                                .solicitorEmail("solicitor@gmail.com").build())
                        .build());
        CaseDetails caseDetails = new CaseDetails(CaseData.builder()
                .applicationType(SOLICITOR)
                .registryLocation("Manchester")
                .solsSolicitorEmail("solicitor@probate-test.com")
                .solsSolicitorAppReference("1234-5678-9012")
                .languagePreferenceWelsh("No")
                .changeOfRepresentatives(Arrays.asList(representative))
                .build(), LAST_MODIFIED, ID);
        callbackRequest = new CallbackRequest(caseDetails);
        document = Document.builder()
                .documentDateAdded(LocalDate.now())
                .documentFileName("fileName")
                .documentGeneratedBy("generatedBy")
                .documentLink(
                        DocumentLink.builder().documentUrl("url").documentFilename("file")
                                .documentBinaryUrl("binary").build())
                .documentType(DocumentType.SENT_EMAIL)
                .build();
        callbackResponse = CallbackResponse.builder().errors(Collections.EMPTY_LIST).build();
        when(eventValidationService.validateNocEmail(any(), any())).thenReturn(callbackResponse);
        when(notificationService.sendNocEmail(any(), any())).thenReturn(document);
        ResponseEntity<CallbackResponse> stringResponseEntity =
                notificationController.sendNOCEmailNotification(callbackRequest);
        assertThat(stringResponseEntity.getStatusCode(), is(HttpStatus.OK));
    }
}
