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
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.State;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.service.BulkPrintService;
import uk.gov.hmcts.probate.service.DocumentGeneratorService;
import uk.gov.hmcts.probate.service.DocumentsReceivedNotificationService;
import uk.gov.hmcts.probate.service.EventValidationService;
import uk.gov.hmcts.probate.service.InformationRequestService;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.hmcts.probate.service.RedeclarationNotificationService;
import uk.gov.hmcts.probate.service.docmosis.GrantOfRepresentationDocmosisMapperService;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.hmcts.probate.validator.BulkPrintValidationRule;
import uk.gov.hmcts.probate.validator.EmailAddressNotifyValidationRule;
import uk.gov.hmcts.reform.probate.model.ProbateDocument;
import uk.gov.service.notify.NotificationClientException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.controller.CaseDataTestBuilder.ID;
import static uk.gov.hmcts.probate.controller.CaseDataTestBuilder.LAST_MODIFIED;
import static uk.gov.hmcts.probate.model.State.APPLICATION_RECEIVED;
import static uk.gov.hmcts.probate.model.State.APPLICATION_RECEIVED_NO_DOCS;
import static uk.gov.hmcts.probate.model.State.DOCUMENTS_RECEIVED;

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


    private void setUpMocks(State state, String... errors) throws NotificationClientException {
        CaseDetails caseDetails = new CaseDetails(CaseDataTestBuilder.withDefaults().build(), LAST_MODIFIED, ID);
        callbackRequest = new CallbackRequest(caseDetails);
        document = Document.builder()
            .documentDateAdded(LocalDate.now())
            .documentFileName("fileName")
            .documentGeneratedBy("generatedBy")
            .documentLink(
                DocumentLink.builder().documentUrl("url").documentFilename("file").documentBinaryUrl("binary").build())
            .documentType(DocumentType.DIGITAL_GRANT)
            .build();
        callbackResponse = CallbackResponse.builder().errors(Collections.EMPTY_LIST).build();
        when(eventValidationService.validateEmailRequest(any(), any())).thenReturn(callbackResponse);
        when(notificationService.sendEmail(any(), any())).thenReturn(document);

    }

    private void setUpMocks(State state) throws NotificationClientException {
        this.setUpMocks(state, new String[0]);
    }


}
