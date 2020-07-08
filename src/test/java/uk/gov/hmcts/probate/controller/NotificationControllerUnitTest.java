package uk.gov.hmcts.probate.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.probate.exception.ClientException;
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
import uk.gov.hmcts.probate.validator.EmailAddressNotificationValidationRule;
import uk.gov.hmcts.probate.validator.EmailAddressNotifyValidationRule;
import uk.gov.hmcts.reform.probate.model.ProbateDocument;
import uk.gov.service.notify.NotificationClientException;
import static org.hamcrest.Matchers.empty;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.controller.CaseDataTestBuilder.ID;
import static uk.gov.hmcts.probate.controller.CaseDataTestBuilder.LAST_MODIFIED;
import static uk.gov.hmcts.probate.model.State.APPLICATION_RECEIVED;
import static uk.gov.hmcts.probate.model.State.DOCUMENTS_RECEIVED;

@RunWith(MockitoJUnitRunner.class)
public class NotificationControllerUnitTest {

    @Mock
    DocumentGeneratorService documentGeneratorService;
    @Mock
    NotificationService notificationService;
    @Mock
    CallbackResponseTransformer callbackResponseTransformer;
    @Mock
    EventValidationService eventValidationService;
    @Mock
    List<EmailAddressNotificationValidationRule> emailAddressNotificationValidationRules;
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
    public void shouldSendApplicationReceived() throws NotificationClientException {
        setUpMocks(APPLICATION_RECEIVED);
        ResponseEntity<ProbateDocument> stringResponseEntity = notificationController.sendApplicationReceivedNotification(callbackRequest);
        assertThat(stringResponseEntity.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void shouldNotSendApplicationReceivedForPaper() throws NotificationClientException {
        CaseDetails caseDetails = new CaseDetails(CaseData.builder().paperForm("Yes").build(), LAST_MODIFIED, ID);
        callbackRequest = new CallbackRequest(caseDetails);
        
        ResponseEntity<ProbateDocument> stringResponseEntity = notificationController.sendApplicationReceivedNotification(callbackRequest);
        assertThat(stringResponseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(stringResponseEntity.getBody(), equalTo(null));
        verify(eventValidationService, times(0)).validateEmailRequest(any(), any());
    }

    @Test
    public void shouldNotSendApplicationReceivedForPCitizenPaperAsNull() throws NotificationClientException {
        CaseDetails caseDetails = new CaseDetails(CaseData.builder().paperForm(null).build(), LAST_MODIFIED, ID);
        callbackRequest = new CallbackRequest(caseDetails);

        ResponseEntity<ProbateDocument> stringResponseEntity = notificationController.sendApplicationReceivedNotification(callbackRequest);
        assertThat(stringResponseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(stringResponseEntity.getBody(), equalTo(null));
        verify(eventValidationService, times(0)).validateEmailRequest(any(), any());
    }

    @Test
    public void shouldAddDocumentEvenIfNoEmailAddressPresent() throws NotificationClientException {
        setUpMocks(APPLICATION_RECEIVED);
        CaseDetails caseDetails = new CaseDetails(CaseDataTestBuilder.withDefaultsAndNoPrimaryApplicantEmailAddress().build(), LAST_MODIFIED, ID);
        callbackRequest = new CallbackRequest(caseDetails);
        ResponseEntity<ProbateDocument> stringResponseEntity = notificationController.sendApplicationReceivedNotification(callbackRequest);
        assertThat(stringResponseEntity.getStatusCode(), is(HttpStatus.OK));
        verifyNoMoreInteractions(notificationService);
    }

    @Test
    public void shouldHandleErrorsFromSendApplicationReceived() throws NotificationClientException {
        setUpMocks(APPLICATION_RECEIVED, "This is an error", "This is another error");

        ResponseEntity<ProbateDocument> stringResponseEntity = notificationController.sendApplicationReceivedNotification(callbackRequest);
        assertThat(stringResponseEntity.getStatusCode(), is(HttpStatus.OK));
        verifyNoMoreInteractions(callbackResponseTransformer);
    }

    @Test
    public void shouldSendDocumentsReceived() throws NotificationClientException {
        setUpMocks(DOCUMENTS_RECEIVED);
        notificationController.sendDocumentReceivedNotification(callbackRequest);
        verify(documentsReceivedNotificationService).handleDocumentReceivedNotification(callbackRequest);
    }


    private void setUpMocks(State state, String ...errors) throws NotificationClientException {
        CaseDetails caseDetails = new CaseDetails(CaseDataTestBuilder.withDefaults().build(), LAST_MODIFIED, ID);
        callbackRequest = new CallbackRequest(caseDetails);
        document = Document.builder()
            .documentDateAdded(LocalDate.now())
            .documentFileName("fileName")
            .documentGeneratedBy("generatedBy")
            .documentLink(DocumentLink.builder().documentUrl("url").documentFilename("file").documentBinaryUrl("binary").build())
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
