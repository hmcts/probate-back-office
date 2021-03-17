package uk.gov.hmcts.probate.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
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
import uk.gov.service.notify.NotificationClientException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.Boolean.TRUE;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
            .primaryApplicantEmailAddress("primary@probate-test.com")
            .primaryApplicantForenames("Forename1")
            .primaryApplicantSurname("Surname1")
            .applicationType(ApplicationType.PERSONAL)
            .build();

        caseData2 = CaseData.builder()
            .registryLocation("Registry2")
            .primaryApplicantEmailAddress("primary@probate-test.com")
            .primaryApplicantForenames("Forename2")
            .primaryApplicantSurname("Surname2")
            .applicationType(ApplicationType.PERSONAL)
            .build();
        caseData2.getProbateNotificationsGenerated().add(new CollectionMember("100", buildDocument()));

        caseData3 = CaseData.builder()
            .registryLocation("Registry3")
            .solsSolicitorEmail("solicitor@probate-test.com")
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

        uk.gov.hmcts.reform.ccd.client.model.CaseDetails caseDetails = Mockito.mock(uk.gov.hmcts.reform.ccd.client.model.CaseDetails.class);
        Map caseData = Mockito.mock(Map.class);
        when(caseDetails.getData()).thenReturn(caseData);
//        when(caseData.get("grantDelayedNotificationIdentified")).thenReturn("Yes");
//        when(caseData.get("grantDelayedNotificationSent")).thenReturn("Yes");
        when(ccdClientApi.readForCaseWorker(any(), any(), any())).thenReturn(caseDetails);

        GrantScheduleResponse response = grantNotificationService.handleGrantDelayedNotification(dateString);
        assertThat(response.getScheduleResponseData().size(), equalTo(3));

        assertThat(response.getScheduleResponseData().contains("1"), equalTo(true));
        assertThat(response.getScheduleResponseData().contains("2"), equalTo(true));
        assertThat(response.getScheduleResponseData().contains("3"), equalTo(true));
    }

    @Test
    public void shouldNotifyForGrantDelayedForIdentifiedOrSent() throws NotificationClientException {
        documents.add(sentEmail);

        String dateString = "31-12-2020";
        when(caseQueryService.findCasesForGrantDelayed(dateString)).thenReturn(returnedCases);
        when(notificationService.sendGrantDelayedEmail(returnedCaseDetails1)).thenReturn(buildDocument());
        when(notificationService.sendGrantDelayedEmail(returnedCaseDetails2)).thenReturn(buildDocument());
        when(notificationService.sendGrantDelayedEmail(returnedCaseDetails3)).thenReturn(buildDocument());

        uk.gov.hmcts.reform.ccd.client.model.CaseDetails caseDetails = Mockito.mock(uk.gov.hmcts.reform.ccd.client.model.CaseDetails.class);
        Map caseData = Mockito.mock(Map.class);
        when(caseDetails.getData()).thenReturn(caseData);
        when(caseData.get("grantDelayedNotificationIdentified")).thenReturn("No");
        when(caseData.get("grantDelayedNotificationSent")).thenReturn("NO");
        when(ccdClientApi.readForCaseWorker(any(), any(), any())).thenReturn(caseDetails);

        GrantScheduleResponse response = grantNotificationService.handleGrantDelayedNotification(dateString);
        assertThat(response.getScheduleResponseData().size(), equalTo(3));

        assertThat(response.getScheduleResponseData().contains("1"), equalTo(true));
        assertThat(response.getScheduleResponseData().contains("2"), equalTo(true));
        assertThat(response.getScheduleResponseData().contains("3"), equalTo(true));
    }

    @Test
    public void shouldNotNotifyForGrantDelayedForNoIdentifiedOrSent() throws NotificationClientException {
        documents.add(sentEmail);

        String dateString = "31-12-2020";
        when(caseQueryService.findCasesForGrantDelayed(dateString)).thenReturn(returnedCases);
        when(notificationService.sendGrantDelayedEmail(returnedCaseDetails1)).thenReturn(buildDocument());
        when(notificationService.sendGrantDelayedEmail(returnedCaseDetails2)).thenReturn(buildDocument());
        when(notificationService.sendGrantDelayedEmail(returnedCaseDetails3)).thenReturn(buildDocument());

        uk.gov.hmcts.reform.ccd.client.model.CaseDetails caseDetails = Mockito.mock(uk.gov.hmcts.reform.ccd.client.model.CaseDetails.class);
        Map caseData = Mockito.mock(Map.class);
        when(caseDetails.getData()).thenReturn(caseData);
        when(caseData.get("grantDelayedNotificationIdentified")).thenReturn("Yes");
        when(caseData.get("grantDelayedNotificationSent")).thenReturn("Yes");
        when(ccdClientApi.readForCaseWorker(any(), any(), any())).thenReturn(caseDetails);

        GrantScheduleResponse response = grantNotificationService.handleGrantDelayedNotification(dateString);

        verify(ccdClientApi, times(0)).updateCaseAsCaseworker(any(), any(), any(),
            any(), any());

        assertThat(response.getScheduleResponseData().size(), equalTo(3));
        assertThat(response.getScheduleResponseData().contains("<1:Case has already been updated>"), equalTo(true));
        assertThat(response.getScheduleResponseData().contains("<2:Case has already been updated>"), equalTo(true));
        assertThat(response.getScheduleResponseData().contains("<3:Case has already been updated>"), equalTo(true));
    }

    @Test
    public void shouldNotNotifyForGrantDelayedForNoIdentified() throws NotificationClientException {
        documents.add(sentEmail);

        String dateString = "31-12-2020";
        when(caseQueryService.findCasesForGrantDelayed(dateString)).thenReturn(returnedCases);
        when(notificationService.sendGrantDelayedEmail(returnedCaseDetails1)).thenReturn(buildDocument());
        when(notificationService.sendGrantDelayedEmail(returnedCaseDetails2)).thenReturn(buildDocument());
        when(notificationService.sendGrantDelayedEmail(returnedCaseDetails3)).thenReturn(buildDocument());

        uk.gov.hmcts.reform.ccd.client.model.CaseDetails caseDetails = Mockito.mock(uk.gov.hmcts.reform.ccd.client.model.CaseDetails.class);
        Map caseData = Mockito.mock(Map.class);
        when(caseDetails.getData()).thenReturn(caseData);
        when(caseData.get("grantDelayedNotificationIdentified")).thenReturn("Yes");
        when(ccdClientApi.readForCaseWorker(any(), any(), any())).thenReturn(caseDetails);

        GrantScheduleResponse response = grantNotificationService.handleGrantDelayedNotification(dateString);

        verify(ccdClientApi, times(0)).updateCaseAsCaseworker(any(), any(), any(),
            any(), any());

        assertThat(response.getScheduleResponseData().size(), equalTo(3));
        assertThat(response.getScheduleResponseData().contains("<1:Case has already been updated>"), equalTo(true));
        assertThat(response.getScheduleResponseData().contains("<2:Case has already been updated>"), equalTo(true));
        assertThat(response.getScheduleResponseData().contains("<3:Case has already been updated>"), equalTo(true));
    }

    @Test
    public void shouldNotNotifyForGrantDelayedForNoSent() throws NotificationClientException {
        documents.add(sentEmail);

        String dateString = "31-12-2020";
        when(caseQueryService.findCasesForGrantDelayed(dateString)).thenReturn(returnedCases);
        when(notificationService.sendGrantDelayedEmail(returnedCaseDetails1)).thenReturn(buildDocument());
        when(notificationService.sendGrantDelayedEmail(returnedCaseDetails2)).thenReturn(buildDocument());
        when(notificationService.sendGrantDelayedEmail(returnedCaseDetails3)).thenReturn(buildDocument());

        uk.gov.hmcts.reform.ccd.client.model.CaseDetails caseDetails = Mockito.mock(uk.gov.hmcts.reform.ccd.client.model.CaseDetails.class);
        Map caseData = Mockito.mock(Map.class);
        when(caseDetails.getData()).thenReturn(caseData);
        when(caseData.get("grantDelayedNotificationSent")).thenReturn("Yes");
        when(ccdClientApi.readForCaseWorker(any(), any(), any())).thenReturn(caseDetails);

        GrantScheduleResponse response = grantNotificationService.handleGrantDelayedNotification(dateString);

        verify(ccdClientApi, times(0)).updateCaseAsCaseworker(any(), any(), any(),
            any(), any());

        assertThat(response.getScheduleResponseData().size(), equalTo(3));
        assertThat(response.getScheduleResponseData().contains("<1:Case has already been updated>"), equalTo(true));
        assertThat(response.getScheduleResponseData().contains("<2:Case has already been updated>"), equalTo(true));
        assertThat(response.getScheduleResponseData().contains("<3:Case has already been updated>"), equalTo(true));
    }

    @Test
    public void shouldNotifyForGrantDelayedWithNotificationException() throws NotificationClientException {
        documents.add(sentEmail);

        String dateString = "31-12-2020";
        when(caseQueryService.findCasesForGrantDelayed(dateString)).thenReturn(returnedCases);
        when(notificationService.sendGrantDelayedEmail(returnedCaseDetails1)).thenReturn(buildDocument());
        when(notificationService.sendGrantDelayedEmail(returnedCaseDetails2)).thenReturn(buildDocument());
        when(notificationService.sendGrantDelayedEmail(returnedCaseDetails3)).thenThrow(new NotificationClientException("notificationError"));

        uk.gov.hmcts.reform.ccd.client.model.CaseDetails caseDetails = Mockito.mock(uk.gov.hmcts.reform.ccd.client.model.CaseDetails.class);
        Map caseData = Mockito.mock(Map.class);
        when(caseDetails.getData()).thenReturn(caseData);
        when(ccdClientApi.readForCaseWorker(any(), any(), any())).thenReturn(caseDetails);

        GrantScheduleResponse response = grantNotificationService.handleGrantDelayedNotification(dateString);

        ArgumentCaptor<GrantOfRepresentationData> grantOfRepresentationDataArgumentCaptor = ArgumentCaptor.forClass(GrantOfRepresentationData.class);
        verify(ccdClientApi, times(2)).updateCaseAsCaseworker(any(), any(), grantOfRepresentationDataArgumentCaptor.capture(),
            eq(SCHEDULED_UPDATE_GRANT_DELAY_NOTIFICATION_SENT), any());
        GrantOfRepresentationData grantOfRepresentationData = grantOfRepresentationDataArgumentCaptor.getValue();
        assertThat(grantOfRepresentationData.getGrantDelayedNotificationSent(), equalTo(TRUE));

        assertThat(response.getScheduleResponseData().size(), equalTo(3));
        assertThat(response.getScheduleResponseData().contains("1"), equalTo(true));
        assertThat(response.getScheduleResponseData().contains("2"), equalTo(true));
        assertThat(response.getScheduleResponseData().contains("<3:notificationError>"), equalTo(true));
    }

    @Test
    public void shouldNotifyForGrantDelayedWithCCDException() throws NotificationClientException {
        documents.add(sentEmail);

        String dateString = "31-12-2020";
        when(caseQueryService.findCasesForGrantDelayed(dateString)).thenReturn(returnedCases);
        when(notificationService.sendGrantDelayedEmail(returnedCaseDetails1)).thenReturn(buildDocument());
        when(notificationService.sendGrantDelayedEmail(returnedCaseDetails2)).thenReturn(buildDocument());
        when(notificationService.sendGrantDelayedEmail(returnedCaseDetails3)).thenThrow(RuntimeException.class);

        uk.gov.hmcts.reform.ccd.client.model.CaseDetails caseDetails = Mockito.mock(uk.gov.hmcts.reform.ccd.client.model.CaseDetails.class);
        Map caseData = Mockito.mock(Map.class);
        when(caseDetails.getData()).thenReturn(caseData);
        when(ccdClientApi.readForCaseWorker(any(), any(), any())).thenReturn(caseDetails);

        GrantScheduleResponse response = grantNotificationService.handleGrantDelayedNotification(dateString);

        ArgumentCaptor<GrantOfRepresentationData> grantOfRepresentationDataArgumentCaptor = ArgumentCaptor.forClass(GrantOfRepresentationData.class);
        verify(ccdClientApi, times(2)).updateCaseAsCaseworker(any(), any(), grantOfRepresentationDataArgumentCaptor.capture(),
            eq(SCHEDULED_UPDATE_GRANT_DELAY_NOTIFICATION_SENT), any());
        GrantOfRepresentationData grantOfRepresentationData = grantOfRepresentationDataArgumentCaptor.getValue();
        assertThat(grantOfRepresentationData.getGrantDelayedNotificationSent(), equalTo(TRUE));

        assertThat(response.getScheduleResponseData().size(), equalTo(3));
        assertThat(response.getScheduleResponseData().contains("1"), equalTo(true));
        assertThat(response.getScheduleResponseData().contains("2"), equalTo(true));
        assertThat(response.getScheduleResponseData().contains("<3:null>"), equalTo(true));
    }

    @Test
    public void shouldNotifyForGrantDelayedWithCCDUpdateException() throws NotificationClientException {
        documents.add(sentEmail);

        String dateString = "31-12-2020";
        when(caseQueryService.findCasesForGrantDelayed(dateString)).thenReturn(returnedCases);
        when(notificationService.sendGrantDelayedEmail(returnedCaseDetails1)).thenReturn(buildDocument());
        when(notificationService.sendGrantDelayedEmail(returnedCaseDetails2)).thenReturn(buildDocument());
        when(notificationService.sendGrantDelayedEmail(returnedCaseDetails3)).thenReturn(buildDocument());

        uk.gov.hmcts.reform.ccd.client.model.CaseDetails caseDetails = Mockito.mock(uk.gov.hmcts.reform.ccd.client.model.CaseDetails.class);
        Map caseData = Mockito.mock(Map.class);
        when(caseDetails.getData()).thenReturn(caseData);
        when(ccdClientApi.readForCaseWorker(any(), any(), any())).thenReturn(caseDetails);

        when(ccdClientApi.updateCaseAsCaseworker(any(), any(), any(), any(), any())).thenThrow(new RuntimeException());
        GrantScheduleResponse response = grantNotificationService.handleGrantDelayedNotification(dateString);

        assertThat(response.getScheduleResponseData().size(), equalTo(3));
        assertThat(response.getScheduleResponseData().contains("<1:null>"), equalTo(true));
        assertThat(response.getScheduleResponseData().contains("<2:null>"), equalTo(true));
        assertThat(response.getScheduleResponseData().contains("<3:null>"), equalTo(true));
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
        assertThat(response.getScheduleResponseData().contains("<1:emailError>"), equalTo(true));
        assertThat(response.getScheduleResponseData().contains("<2:emailError>"), equalTo(true));
        assertThat(response.getScheduleResponseData().contains("<3:emailError>"), equalTo(true));
    }

    @Test
    public void shouldNotifyForGrantAwaitingDocs() throws NotificationClientException {
        documents.add(sentEmail);

        String dateString = "31-12-2020";
        when(caseQueryService.findCasesForGrantAwaitingDocumentation(dateString)).thenReturn(returnedCases);
        when(notificationService.sendGrantAwaitingDocumentationEmail(returnedCaseDetails1)).thenReturn(buildDocument());
        when(notificationService.sendGrantAwaitingDocumentationEmail(returnedCaseDetails2)).thenReturn(buildDocument());
        when(notificationService.sendGrantAwaitingDocumentationEmail(returnedCaseDetails3)).thenReturn(buildDocument());

        uk.gov.hmcts.reform.ccd.client.model.CaseDetails caseDetails = Mockito.mock(uk.gov.hmcts.reform.ccd.client.model.CaseDetails.class);
        Map caseData = Mockito.mock(Map.class);
        when(caseDetails.getData()).thenReturn(caseData);
        when(ccdClientApi.readForCaseWorker(any(), any(), any())).thenReturn(caseDetails);

        GrantScheduleResponse response = grantNotificationService.handleAwaitingDocumentationNotification(dateString);

        ArgumentCaptor<GrantOfRepresentationData> grantOfRepresentationDataArgumentCaptor = ArgumentCaptor.forClass(GrantOfRepresentationData.class);
        verify(ccdClientApi, times(3)).updateCaseAsCaseworker(any(), any(), grantOfRepresentationDataArgumentCaptor.capture(),
            eq(SCHEDULED_UPDATE_GRANT_AWAITING_DOCUMENTATION_NOTIFICATION_SENT), any());
        GrantOfRepresentationData grantOfRepresentationData = grantOfRepresentationDataArgumentCaptor.getValue();
        assertThat(grantOfRepresentationData.getGrantAwaitingDocumentatioNotificationSent(), equalTo(TRUE));

        assertThat(response.getScheduleResponseData().size(), equalTo(3));
        assertThat(response.getScheduleResponseData().contains("1"), equalTo(true));
        assertThat(response.getScheduleResponseData().contains("2"), equalTo(true));
        assertThat(response.getScheduleResponseData().contains("3"), equalTo(true));
    }

    @Test
    public void shouldNotifyForGrantAwaitingDocsForNotIdentifiedOrNotified() throws NotificationClientException {
        documents.add(sentEmail);

        String dateString = "31-12-2020";
        when(caseQueryService.findCasesForGrantAwaitingDocumentation(dateString)).thenReturn(returnedCases);
        when(notificationService.sendGrantAwaitingDocumentationEmail(returnedCaseDetails1)).thenReturn(buildDocument());
        when(notificationService.sendGrantAwaitingDocumentationEmail(returnedCaseDetails2)).thenReturn(buildDocument());
        when(notificationService.sendGrantAwaitingDocumentationEmail(returnedCaseDetails3)).thenReturn(buildDocument());

        uk.gov.hmcts.reform.ccd.client.model.CaseDetails caseDetails = Mockito.mock(uk.gov.hmcts.reform.ccd.client.model.CaseDetails.class);
        Map caseData = Mockito.mock(Map.class);
        when(caseDetails.getData()).thenReturn(caseData);
        when(caseData.get("grantDelayedNotificationIdentified")).thenReturn("No");
        when(caseData.get("grantAwaitingDocumentatioNotificationSent")).thenReturn("NO");
        when(ccdClientApi.readForCaseWorker(any(), any(), any())).thenReturn(caseDetails);

        GrantScheduleResponse response = grantNotificationService.handleAwaitingDocumentationNotification(dateString);

        ArgumentCaptor<GrantOfRepresentationData> grantOfRepresentationDataArgumentCaptor = ArgumentCaptor.forClass(GrantOfRepresentationData.class);
        verify(ccdClientApi, times(3)).updateCaseAsCaseworker(any(), any(), grantOfRepresentationDataArgumentCaptor.capture(),
            eq(SCHEDULED_UPDATE_GRANT_AWAITING_DOCUMENTATION_NOTIFICATION_SENT), any());
        GrantOfRepresentationData grantOfRepresentationData = grantOfRepresentationDataArgumentCaptor.getValue();
        assertThat(grantOfRepresentationData.getGrantAwaitingDocumentatioNotificationSent(), equalTo(TRUE));

        assertThat(response.getScheduleResponseData().size(), equalTo(3));
        assertThat(response.getScheduleResponseData().contains("1"), equalTo(true));
        assertThat(response.getScheduleResponseData().contains("2"), equalTo(true));
        assertThat(response.getScheduleResponseData().contains("3"), equalTo(true));
    }

    @Test
    public void shouldNotNotifyForGrantAwaitingDocsForNoIdentificationNoNotification() throws NotificationClientException {
        documents.add(sentEmail);

        String dateString = "31-12-2020";
        when(caseQueryService.findCasesForGrantAwaitingDocumentation(dateString)).thenReturn(returnedCases);
        when(notificationService.sendGrantAwaitingDocumentationEmail(returnedCaseDetails1)).thenReturn(buildDocument());
        when(notificationService.sendGrantAwaitingDocumentationEmail(returnedCaseDetails2)).thenReturn(buildDocument());
        when(notificationService.sendGrantAwaitingDocumentationEmail(returnedCaseDetails3)).thenReturn(buildDocument());

        uk.gov.hmcts.reform.ccd.client.model.CaseDetails caseDetails = Mockito.mock(uk.gov.hmcts.reform.ccd.client.model.CaseDetails.class);
        Map caseData = Mockito.mock(Map.class);
        when(caseData.get("grantDelayedNotificationIdentified")).thenReturn("Yes");
        when(caseData.get("grantAwaitingDocumentatioNotificationSent")).thenReturn("Yes");
        when(caseDetails.getData()).thenReturn(caseData);
        when(ccdClientApi.readForCaseWorker(any(), any(), any())).thenReturn(caseDetails);

        GrantScheduleResponse response = grantNotificationService.handleAwaitingDocumentationNotification(dateString);

        verify(ccdClientApi, times(0)).updateCaseAsCaseworker(any(), any(), any(),
            any(), any());
        
        assertThat(response.getScheduleResponseData().size(), equalTo(3));
        assertThat(response.getScheduleResponseData().contains("<1:Case has already been updated>"), equalTo(true));
        assertThat(response.getScheduleResponseData().contains("<2:Case has already been updated>"), equalTo(true));
        assertThat(response.getScheduleResponseData().contains("<3:Case has already been updated>"), equalTo(true));
    }

    @Test
    public void shouldNotNotifyForGrantAwaitingDocsForNoIdentification() throws NotificationClientException {
        documents.add(sentEmail);

        String dateString = "31-12-2020";
        when(caseQueryService.findCasesForGrantAwaitingDocumentation(dateString)).thenReturn(returnedCases);
        when(notificationService.sendGrantAwaitingDocumentationEmail(returnedCaseDetails1)).thenReturn(buildDocument());
        when(notificationService.sendGrantAwaitingDocumentationEmail(returnedCaseDetails2)).thenReturn(buildDocument());
        when(notificationService.sendGrantAwaitingDocumentationEmail(returnedCaseDetails3)).thenReturn(buildDocument());

        uk.gov.hmcts.reform.ccd.client.model.CaseDetails caseDetails = Mockito.mock(uk.gov.hmcts.reform.ccd.client.model.CaseDetails.class);
        Map caseData = Mockito.mock(Map.class);
        when(caseData.get("grantDelayedNotificationIdentified")).thenReturn("Yes");
        when(caseDetails.getData()).thenReturn(caseData);
        when(ccdClientApi.readForCaseWorker(any(), any(), any())).thenReturn(caseDetails);

        GrantScheduleResponse response = grantNotificationService.handleAwaitingDocumentationNotification(dateString);

        verify(ccdClientApi, times(0)).updateCaseAsCaseworker(any(), any(), any(),
            any(), any());

        assertThat(response.getScheduleResponseData().size(), equalTo(3));
        assertThat(response.getScheduleResponseData().contains("<1:Case has already been updated>"), equalTo(true));
        assertThat(response.getScheduleResponseData().contains("<2:Case has already been updated>"), equalTo(true));
        assertThat(response.getScheduleResponseData().contains("<3:Case has already been updated>"), equalTo(true));
    }

    @Test
    public void shouldNotNotifyForGrantAwaitingDocsForNoNotification() throws NotificationClientException {
        documents.add(sentEmail);

        String dateString = "31-12-2020";
        when(caseQueryService.findCasesForGrantAwaitingDocumentation(dateString)).thenReturn(returnedCases);
        when(notificationService.sendGrantAwaitingDocumentationEmail(returnedCaseDetails1)).thenReturn(buildDocument());
        when(notificationService.sendGrantAwaitingDocumentationEmail(returnedCaseDetails2)).thenReturn(buildDocument());
        when(notificationService.sendGrantAwaitingDocumentationEmail(returnedCaseDetails3)).thenReturn(buildDocument());

        uk.gov.hmcts.reform.ccd.client.model.CaseDetails caseDetails = Mockito.mock(uk.gov.hmcts.reform.ccd.client.model.CaseDetails.class);
        Map caseData = Mockito.mock(Map.class);
        when(caseData.get("grantAwaitingDocumentatioNotificationSent")).thenReturn("Yes");
        when(caseDetails.getData()).thenReturn(caseData);
        when(ccdClientApi.readForCaseWorker(any(), any(), any())).thenReturn(caseDetails);

        GrantScheduleResponse response = grantNotificationService.handleAwaitingDocumentationNotification(dateString);

        verify(ccdClientApi, times(0)).updateCaseAsCaseworker(any(), any(), any(),
            any(), any());

        assertThat(response.getScheduleResponseData().size(), equalTo(3));
        assertThat(response.getScheduleResponseData().contains("<1:Case has already been updated>"), equalTo(true));
        assertThat(response.getScheduleResponseData().contains("<2:Case has already been updated>"), equalTo(true));
        assertThat(response.getScheduleResponseData().contains("<3:Case has already been updated>"), equalTo(true));
    }

    @Test
    public void shouldNotifyForGrantAwaitingDocsWithCCDUpdateException() throws NotificationClientException {
        documents.add(sentEmail);

        String dateString = "31-12-2020";
        when(caseQueryService.findCasesForGrantAwaitingDocumentation(dateString)).thenReturn(returnedCases);
        when(notificationService.sendGrantAwaitingDocumentationEmail(returnedCaseDetails1)).thenReturn(buildDocument());
        when(notificationService.sendGrantAwaitingDocumentationEmail(returnedCaseDetails2)).thenReturn(buildDocument());
        when(notificationService.sendGrantAwaitingDocumentationEmail(returnedCaseDetails3)).thenReturn(buildDocument());

        uk.gov.hmcts.reform.ccd.client.model.CaseDetails caseDetails = Mockito.mock(uk.gov.hmcts.reform.ccd.client.model.CaseDetails.class);
        Map caseData = Mockito.mock(Map.class);
        when(caseDetails.getData()).thenReturn(caseData);
        when(ccdClientApi.readForCaseWorker(any(), any(), any())).thenReturn(caseDetails);

        when(ccdClientApi.updateCaseAsCaseworker(any(), any(), any(), any(), any())).thenThrow(new RuntimeException());
        GrantScheduleResponse response = grantNotificationService.handleAwaitingDocumentationNotification(dateString);

        assertThat(response.getScheduleResponseData().size(), equalTo(3));
        assertThat(response.getScheduleResponseData().contains("<1:null>"), equalTo(true));
        assertThat(response.getScheduleResponseData().contains("<2:null>"), equalTo(true));
        assertThat(response.getScheduleResponseData().contains("<3:null>"), equalTo(true));
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
        assertThat(response.getScheduleResponseData().contains("<1:emailError>"), equalTo(true));
        assertThat(response.getScheduleResponseData().contains("<2:emailError>"), equalTo(true));
        assertThat(response.getScheduleResponseData().contains("<3:emailError>"), equalTo(true));
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