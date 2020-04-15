package uk.gov.hmcts.probate.service;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.GrantScheduleResponse;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.ccd.CcdClientApi;
import uk.gov.hmcts.probate.validator.EmailAddressNotifyApplicantValidationRule;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;
import uk.gov.hmcts.reform.sendletter.api.SendLetterResponse;
import uk.gov.service.notify.NotificationClientException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.lang.Boolean.TRUE;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.DocumentType.GRANT_COVER;
import static uk.gov.hmcts.probate.model.ccd.EventId.SCHEDULED_UPDATE_GRANT_AWAITING_DOCUMENTATION_NOTIFICATION_SENT;
import static uk.gov.hmcts.probate.model.ccd.EventId.SCHEDULED_UPDATE_GRANT_DELAY_NOTIFICATION_SENT;

public class GrantNotificationServiceTest {

    @InjectMocks
    private GrantNotificationService grantNotificationService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private EmailAddressNotifyApplicantValidationRule emailAddressNotifyApplicantValidationRule;

    @Mock
    private CaseQueryService caseQueryService;

    @Mock
    private CcdClientApi ccdClientApi;

    @Mock
    private SecurityUtils securityUtils;

    @Mock
    private BulkPrintService bulkPrintService;

    @Mock
    private DocumentGeneratorService documentGeneratorService;

    private Document sentEmail;

    private CaseData caseData1;
    private CaseData caseData2;
    private CaseData caseData3;
    private CaseData caseData4;
    private CaseData caseData5;
    private CaseData caseData6;

    private List<Document> documents = new ArrayList<>();
    private List<ReturnedCaseDetails> returnedCases = new ArrayList<>();
    private List<ReturnedCaseDetails> returnedCasesBulkPrint = new ArrayList();

    private ReturnedCaseDetails returnedCaseDetails1;
    private ReturnedCaseDetails returnedCaseDetails2;
    private ReturnedCaseDetails returnedCaseDetails3;

    private ReturnedCaseDetails returnedCaseDetails4;
    private ReturnedCaseDetails returnedCaseDetails5;
    private ReturnedCaseDetails returnedCaseDetails6;

    private static final String SENT_EMAIL_FILE_NAME = "sentEmail.pdf";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        caseData1 = CaseData.builder()
            .registryLocation("Registry1")
            .primaryApplicantEmailAddress("test1@test1.com")
            .primaryApplicantForenames("Forename1")
            .primaryApplicantSurname("Surname1")
            .applicationType(ApplicationType.PERSONAL)
            .build();

        caseData2 = CaseData.builder()
            .registryLocation("Registry2")
            .primaryApplicantEmailAddress("test2@test2.com")
            .primaryApplicantForenames("Forename2")
            .primaryApplicantSurname("Surname2")
            .applicationType(ApplicationType.PERSONAL)
            .build();
        caseData2.getProbateDocumentsGenerated().add(new CollectionMember("100", buildDocument()));

        caseData3 = CaseData.builder()
            .registryLocation("Registry3")
            .solsSolicitorEmail("test3@test3.com")
            .primaryApplicantForenames("Forename3")
            .primaryApplicantSurname("Surname3")
            .applicationType(ApplicationType.SOLICITOR)
            .build();

        caseData4 = CaseData.builder()
                .registryLocation("Registry1")
                .primaryApplicantEmailAddress("test1@test1.com")
                .primaryApplicantForenames("Forename1")
                .primaryApplicantSurname("Surname1")
                .applicationType(ApplicationType.PERSONAL)
                .boSendToBulkPrintRequested("Yes")
                .build();

        caseData5 = CaseData.builder()
                .registryLocation("Registry2")
                .primaryApplicantEmailAddress("test2@test2.com")
                .primaryApplicantForenames("Forename2")
                .primaryApplicantSurname("Surname2")
                .applicationType(ApplicationType.PERSONAL)
                .boSendToBulkPrintRequested("Yes")
                .build();
        caseData5.getProbateDocumentsGenerated().add(new CollectionMember("100", buildDocument()));

        caseData6 = CaseData.builder()
                .registryLocation("Registry3")
                .solsSolicitorEmail("test3@test3.com")
                .primaryApplicantForenames("Forename3")
                .primaryApplicantSurname("Surname3")
                .applicationType(ApplicationType.SOLICITOR)
                .boSendToBulkPrintRequested("Yes")
                .build();

        caseData1 = Mockito.spy(caseData1);
        when(caseData1.getBoSendToBulkPrint()).thenReturn("No");

        caseData2 = Mockito.spy(caseData2);
        when(caseData2.getBoSendToBulkPrint()).thenReturn("No");

        caseData3 = Mockito.spy(caseData3);
        when(caseData3.getBoSendToBulkPrint()).thenReturn("No");

        returnedCaseDetails1 = new ReturnedCaseDetails(caseData1, null, Long.valueOf(1));
        returnedCaseDetails2 = new ReturnedCaseDetails(caseData2, null, Long.valueOf(2));
        returnedCaseDetails3 = new ReturnedCaseDetails(caseData3, null, Long.valueOf(3));

        returnedCaseDetails4 = new ReturnedCaseDetails(caseData4, null, Long.valueOf(4));
        returnedCaseDetails5 = new ReturnedCaseDetails(caseData5, null, Long.valueOf(5));
        returnedCaseDetails6 = new ReturnedCaseDetails(caseData6, null, Long.valueOf(6));

        returnedCases.add(returnedCaseDetails1);
        returnedCases.add(returnedCaseDetails2);
        returnedCases.add(returnedCaseDetails3);

        returnedCasesBulkPrint.add(returnedCaseDetails4);
        returnedCasesBulkPrint.add(returnedCaseDetails5);
        returnedCasesBulkPrint.add(returnedCaseDetails6);


        sentEmail = Document.builder().documentFileName(SENT_EMAIL_FILE_NAME).build();
    }

    @Test
    public void shouldNotifyForGrantDelayed() throws NotificationClientException {
        documents.add(sentEmail);

        String dateString = "31-12-2020";
        when(caseQueryService.findCasesForGrantDelayed(dateString)).thenReturn(returnedCases);
        when(notificationService.sendGrantDelayedEmail(returnedCaseDetails1)).thenReturn(buildDocument());
        when(notificationService.sendGrantDelayedEmail(returnedCaseDetails2)).thenReturn(buildDocument());
        when(notificationService.sendGrantDelayedEmail(returnedCaseDetails3)).thenReturn(buildDocument());

        GrantScheduleResponse response = grantNotificationService.handleGrantDelayedNotification(dateString);
        assertThat(response.getScheduleResponseData().size(), equalTo(3));

        assertThat(response.getScheduleResponseData().size(), equalTo(3));
        assertThat(response.getScheduleResponseData().get(0), equalTo("1"));
        assertThat(response.getScheduleResponseData().get(1), equalTo("2"));
        assertThat(response.getScheduleResponseData().get(2), equalTo("3"));
    }

    @Test
    public void shouldNotifyForGrantDelayedWithNotificationException() throws NotificationClientException {
        documents.add(sentEmail);

        String dateString = "31-12-2020";
        when(caseQueryService.findCasesForGrantDelayed(dateString)).thenReturn(returnedCases);
        when(notificationService.sendGrantDelayedEmail(returnedCaseDetails1)).thenReturn(buildDocument());
        when(notificationService.sendGrantDelayedEmail(returnedCaseDetails2)).thenReturn(buildDocument());
        when(notificationService.sendGrantDelayedEmail(returnedCaseDetails3)).thenThrow(new NotificationClientException("notificationError"));

        GrantScheduleResponse response = grantNotificationService.handleGrantDelayedNotification(dateString);

        ArgumentCaptor<GrantOfRepresentationData> grantOfRepresentationDataArgumentCaptor = ArgumentCaptor.forClass(GrantOfRepresentationData.class);
        verify(ccdClientApi, times(2)).updateCaseAsCaseworker(any(), any(), grantOfRepresentationDataArgumentCaptor.capture(),
            eq(SCHEDULED_UPDATE_GRANT_DELAY_NOTIFICATION_SENT), any());
        GrantOfRepresentationData grantOfRepresentationData = grantOfRepresentationDataArgumentCaptor.getValue();
        assertThat(grantOfRepresentationData.getGrantDelayedNotificationSent(), equalTo(TRUE));

        assertThat(response.getScheduleResponseData().size(), equalTo(3));
        assertThat(response.getScheduleResponseData().get(0), equalTo("1"));
        assertThat(response.getScheduleResponseData().get(1), equalTo("2"));
        assertThat(response.getScheduleResponseData().get(2), equalTo("<3:notificationError>"));
    }

    @Test
    public void shouldNotifyForGrantDelayedWithCCDException() throws NotificationClientException {
        documents.add(sentEmail);

        String dateString = "31-12-2020";
        when(caseQueryService.findCasesForGrantDelayed(dateString)).thenReturn(returnedCases);
        when(notificationService.sendGrantDelayedEmail(returnedCaseDetails1)).thenReturn(buildDocument());
        when(notificationService.sendGrantDelayedEmail(returnedCaseDetails2)).thenReturn(buildDocument());
        when(notificationService.sendGrantDelayedEmail(returnedCaseDetails3)).thenThrow(RuntimeException.class);

        GrantScheduleResponse response = grantNotificationService.handleGrantDelayedNotification(dateString);

        ArgumentCaptor<GrantOfRepresentationData> grantOfRepresentationDataArgumentCaptor = ArgumentCaptor.forClass(GrantOfRepresentationData.class);
        verify(ccdClientApi, times(2)).updateCaseAsCaseworker(any(), any(), grantOfRepresentationDataArgumentCaptor.capture(),
            eq(SCHEDULED_UPDATE_GRANT_DELAY_NOTIFICATION_SENT), any());
        GrantOfRepresentationData grantOfRepresentationData = grantOfRepresentationDataArgumentCaptor.getValue();
        assertThat(grantOfRepresentationData.getGrantDelayedNotificationSent(), equalTo(TRUE));

        assertThat(response.getScheduleResponseData().size(), equalTo(3));
        assertThat(response.getScheduleResponseData().get(0), equalTo("1"));
        assertThat(response.getScheduleResponseData().get(1), equalTo("2"));
        assertThat(response.getScheduleResponseData().get(2), equalTo("<3:null>"));
    }

    @Test
    public void shouldNotifyForGrantDelayedWithCCDUpdateException() throws NotificationClientException {
        documents.add(sentEmail);

        String dateString = "31-12-2020";
        when(caseQueryService.findCasesForGrantDelayed(dateString)).thenReturn(returnedCases);
        when(notificationService.sendGrantDelayedEmail(returnedCaseDetails1)).thenReturn(buildDocument());
        when(notificationService.sendGrantDelayedEmail(returnedCaseDetails2)).thenReturn(buildDocument());
        when(notificationService.sendGrantDelayedEmail(returnedCaseDetails3)).thenReturn(buildDocument());

        when(ccdClientApi.updateCaseAsCaseworker(any(), any(), any(), any(), any())).thenThrow(new RuntimeException());
        GrantScheduleResponse response = grantNotificationService.handleGrantDelayedNotification(dateString);

        assertThat(response.getScheduleResponseData().size(), equalTo(3));
        assertThat(response.getScheduleResponseData().get(0), equalTo("<1:null>"));
        assertThat(response.getScheduleResponseData().get(1), equalTo("<2:null>"));
        assertThat(response.getScheduleResponseData().get(2), equalTo("<3:null>"));
    }

    @Test
    public void shouldSendToBulkPrintForMissingEmailAddressForGrantDelayed() throws NotificationClientException {
        documents.add(sentEmail);
        final Document letter = Document.builder()
                .documentType(DocumentType.LETTER_OF_GRANT_DELAY)
                .documentDateAdded(LocalDate.now())
                .documentFileName("test")
                .documentGeneratedBy("test")
                .documentLink(DocumentLink.builder().build())
                .build();

        final Document cover = Document.builder()
                .documentType(GRANT_COVER)
                .documentDateAdded(LocalDate.now())
                .documentFileName("test")
                .documentGeneratedBy("test")
                .documentLink(DocumentLink.builder().build())
                .build();

        when(documentGeneratorService.generateLetterOfGrantDelay(any(ReturnedCaseDetails.class),
                eq(GRANT_COVER)))
                .thenReturn(cover);

        when(documentGeneratorService.generateLetterOfGrantDelay(any(ReturnedCaseDetails.class),
                eq(DocumentType.LETTER_OF_GRANT_DELAY)))
                .thenReturn(letter);

        String dateString = "31-12-2020";
        when(caseQueryService.findCasesForGrantDelayed(dateString)).thenReturn(returnedCasesBulkPrint);
        UUID letterID= UUID.randomUUID();
        List<FieldErrorResponse> errorsList = new ArrayList<>();
        errorsList.add(FieldErrorResponse.builder().message("emailError").build());
        when(emailAddressNotifyApplicantValidationRule.validate(any())).thenReturn(errorsList);
        when(bulkPrintService.sendToBulkPrintForGrantDelay(any(ReturnedCaseDetails.class), any(Document.class),
                any(Document.class))).thenReturn(new SendLetterResponse(letterID));
        GrantScheduleResponse response = grantNotificationService.handleGrantDelayedNotification(dateString);

        verify(bulkPrintService, times(3)).sendToBulkPrintForGrantDelay(any(ReturnedCaseDetails.class),
                any(Document.class),
                any(Document.class));
    }

    @Test
    public void shouldNotifyForGrantAwaitingDocs() throws NotificationClientException {
        documents.add(sentEmail);

        String dateString = "31-12-2020";
        when(caseQueryService.findCasesForGrantAwaitingDocumentation(dateString)).thenReturn(returnedCases);
        when(notificationService.sendGrantAwaitingDocumentationEmail(returnedCaseDetails1)).thenReturn(buildDocument());
        when(notificationService.sendGrantAwaitingDocumentationEmail(returnedCaseDetails2)).thenReturn(buildDocument());
        when(notificationService.sendGrantAwaitingDocumentationEmail(returnedCaseDetails3)).thenReturn(buildDocument());

        GrantScheduleResponse response = grantNotificationService.handleAwaitingDocumentationNotification(dateString);

        ArgumentCaptor<GrantOfRepresentationData> grantOfRepresentationDataArgumentCaptor = ArgumentCaptor.forClass(GrantOfRepresentationData.class);
        verify(ccdClientApi, times(3)).updateCaseAsCaseworker(any(), any(), grantOfRepresentationDataArgumentCaptor.capture(),
            eq(SCHEDULED_UPDATE_GRANT_AWAITING_DOCUMENTATION_NOTIFICATION_SENT), any());
        GrantOfRepresentationData grantOfRepresentationData = grantOfRepresentationDataArgumentCaptor.getValue();
        assertThat(grantOfRepresentationData.getGrantAwaitingDocumentatioNotificationSent(), equalTo(TRUE));

        assertThat(response.getScheduleResponseData().size(), equalTo(3));
        assertThat(response.getScheduleResponseData().size(), equalTo(3));
        assertThat(response.getScheduleResponseData().get(0), equalTo("1"));
        assertThat(response.getScheduleResponseData().get(1), equalTo("2"));
        assertThat(response.getScheduleResponseData().get(2), equalTo("3"));
    }

    @Test
    public void shouldNotifyForGrantAwaitingDocsWithCCDUpdateException() throws NotificationClientException {
        documents.add(sentEmail);

        String dateString = "31-12-2020";
        when(caseQueryService.findCasesForGrantAwaitingDocumentation(dateString)).thenReturn(returnedCases);
        when(notificationService.sendGrantAwaitingDocumentationEmail(returnedCaseDetails1)).thenReturn(buildDocument());
        when(notificationService.sendGrantAwaitingDocumentationEmail(returnedCaseDetails2)).thenReturn(buildDocument());
        when(notificationService.sendGrantAwaitingDocumentationEmail(returnedCaseDetails3)).thenReturn(buildDocument());

        when(ccdClientApi.updateCaseAsCaseworker(any(), any(), any(), any(), any())).thenThrow(new RuntimeException());
        GrantScheduleResponse response = grantNotificationService.handleAwaitingDocumentationNotification(dateString);

        assertThat(response.getScheduleResponseData().size(), equalTo(3));
        assertThat(response.getScheduleResponseData().get(0), equalTo("<1:null>"));
        assertThat(response.getScheduleResponseData().get(1), equalTo("<2:null>"));
        assertThat(response.getScheduleResponseData().get(2), equalTo("<3:null>"));
    }

    @Test
    public void shouldSendToBulkPrintForMissingEmailAddressForGrantAwaitingDocs() throws NotificationClientException {
        documents.add(sentEmail);
         final Document letter = Document.builder()
                .documentType(DocumentType.LETTER_OF_GRANT_DELAY)
                .documentDateAdded(LocalDate.now())
                .documentFileName("test")
                .documentGeneratedBy("test")
                .documentLink(DocumentLink.builder().build())
                .build();

        final Document cover = Document.builder()
                .documentType(GRANT_COVER)
                .documentDateAdded(LocalDate.now())
                .documentFileName("test")
                .documentGeneratedBy("test")
                .documentLink(DocumentLink.builder().build())
                .build();

        when(documentGeneratorService.generateLetterOfGrantDelay(any(ReturnedCaseDetails.class),
                eq(GRANT_COVER)))
                .thenReturn(cover);

        when(documentGeneratorService.generateLetterOfGrantDelay(any(ReturnedCaseDetails.class),
                eq(DocumentType.LETTER_OF_GRANT_DELAY)))
                .thenReturn(letter);


        UUID letterID= UUID.randomUUID();
        String dateString = "31-12-2020";
        when(caseQueryService.findCasesForGrantAwaitingDocumentation(dateString)).thenReturn(returnedCasesBulkPrint);

        List<FieldErrorResponse> errorsList = new ArrayList<>();
        errorsList.add(FieldErrorResponse.builder().message("emailError").build());
        when(emailAddressNotifyApplicantValidationRule.validate(any())).thenReturn(errorsList);
        when(bulkPrintService.sendToBulkPrintForGrantDelay(any(ReturnedCaseDetails.class), any(Document.class),
                any(Document.class))).thenReturn(new SendLetterResponse(letterID));
        GrantScheduleResponse response = grantNotificationService.handleAwaitingDocumentationNotification(dateString);

        verify(bulkPrintService, times(3)).sendToBulkPrintForGrantDelay(any(ReturnedCaseDetails.class),
                any(Document.class),
                any(Document.class));
    }

    @Test
    public void shouldThrowExceptionForMissingEmailAddressForGrantDelayed() throws NotificationClientException {
        documents.add(sentEmail);

        String dateString = "31-12-2020";
        when(caseQueryService.findCasesForGrantDelayed(dateString)).thenReturn(returnedCases);

        List<FieldErrorResponse> errorsList = new ArrayList<>();
        errorsList.add(FieldErrorResponse.builder().message("emailError").build());
        when(emailAddressNotifyApplicantValidationRule.validate(any())).thenReturn(errorsList);

        GrantScheduleResponse response = grantNotificationService.handleGrantDelayedNotification(dateString);

        assertThat(response.getScheduleResponseData().size(), equalTo(3));
        assertThat(response.getScheduleResponseData().get(0), equalTo("<1:emailError>"));
        assertThat(response.getScheduleResponseData().get(1), equalTo("<2:emailError>"));
        assertThat(response.getScheduleResponseData().get(2), equalTo("<3:emailError>"));
    }

    @Test
    public void shouldThrowExceptionForMissingEmailAddressForGrantAwaitingDocs() throws NotificationClientException {
        documents.add(sentEmail);
        String dateString = "31-12-2020";

        when(caseQueryService.findCasesForGrantAwaitingDocumentation(dateString)).thenReturn(returnedCases);

        List<FieldErrorResponse> errorsList = new ArrayList<>();
        errorsList.add(FieldErrorResponse.builder().message("emailError").build());
        when(emailAddressNotifyApplicantValidationRule.validate(any())).thenReturn(errorsList);

        GrantScheduleResponse response = grantNotificationService.handleAwaitingDocumentationNotification(dateString);

        assertThat(response.getScheduleResponseData().size(), equalTo(3));
        assertThat(response.getScheduleResponseData().get(0), equalTo("<1:emailError>"));
        assertThat(response.getScheduleResponseData().get(1), equalTo("<2:emailError>"));
        assertThat(response.getScheduleResponseData().get(2), equalTo("<3:emailError>"));
    }
    private Document buildDocument() {
        Document document = Document.builder()
            .documentFileName(SENT_EMAIL_FILE_NAME)
            .documentDateAdded(LocalDate.now())
            .documentGeneratedBy("GenBy")
            .documentLink(DocumentLink.builder().documentBinaryUrl("binUrl").documentFilename("fileName").documentUrl("url").build())
            .documentType(DocumentType.DIGITAL_GRANT)
            .build();

        return document;
    }
}