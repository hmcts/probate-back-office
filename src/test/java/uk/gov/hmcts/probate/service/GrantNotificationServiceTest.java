package uk.gov.hmcts.probate.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.GrantScheduleResponse;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.ccd.CcdClientApi;
import uk.gov.hmcts.probate.validator.EmailAddressNotifyApplicantValidationRule;
import uk.gov.service.notify.NotificationClientException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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


    private Document sentEmail;
    private CaseData caseData1;
    private CaseData caseData2;
    private CaseData caseData3;
    private List<Document> documents = new ArrayList<>();
    private List<ReturnedCaseDetails> returnedCases = new ArrayList();
    private ReturnedCaseDetails returnedCaseDetails1;
    private ReturnedCaseDetails returnedCaseDetails2;
    private ReturnedCaseDetails returnedCaseDetails3;

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

        returnedCaseDetails1 = new ReturnedCaseDetails(caseData1, null, Long.valueOf(1));
        returnedCaseDetails2 = new ReturnedCaseDetails(caseData2, null, Long.valueOf(2));
        returnedCaseDetails3 = new ReturnedCaseDetails(caseData3, null, Long.valueOf(3));
        returnedCases.add(returnedCaseDetails1);
        returnedCases.add(returnedCaseDetails2);
        returnedCases.add(returnedCaseDetails3);

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

        when(ccdClientApi.updateCaseAsCaseworker(any(), any(),any(), any(), any())).thenThrow(new RuntimeException());
        GrantScheduleResponse response = grantNotificationService.handleGrantDelayedNotification(dateString);

        assertThat(response.getScheduleResponseData().size(), equalTo(3));
        assertThat(response.getScheduleResponseData().get(0), equalTo("<1:null>"));
        assertThat(response.getScheduleResponseData().get(1), equalTo("<2:null>"));
        assertThat(response.getScheduleResponseData().get(2), equalTo("<3:null>"));
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
    public void shouldNotifyForGrantAwaitingDocs() throws NotificationClientException {
        documents.add(sentEmail);

        String dateString = "31-12-2020";
        when(caseQueryService.findCasesForGrantAwaitingDocumentation(dateString)).thenReturn(returnedCases);
        when(notificationService.sendGrantAwaitingDocumentationEmail(returnedCaseDetails1)).thenReturn(buildDocument());
        when(notificationService.sendGrantAwaitingDocumentationEmail(returnedCaseDetails2)).thenReturn(buildDocument());
        when(notificationService.sendGrantAwaitingDocumentationEmail(returnedCaseDetails3)).thenReturn(buildDocument());

        GrantScheduleResponse response = grantNotificationService.handleAwaitingDocumentationNotification(dateString);
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

        when(ccdClientApi.updateCaseAsCaseworker(any(), any(),any(), any(), any())).thenThrow(new RuntimeException());
        GrantScheduleResponse response = grantNotificationService.handleAwaitingDocumentationNotification(dateString);

        assertThat(response.getScheduleResponseData().size(), equalTo(3));
        assertThat(response.getScheduleResponseData().get(0), equalTo("<1:null>"));
        assertThat(response.getScheduleResponseData().get(1), equalTo("<2:null>"));
        assertThat(response.getScheduleResponseData().get(2), equalTo("<3:null>"));
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