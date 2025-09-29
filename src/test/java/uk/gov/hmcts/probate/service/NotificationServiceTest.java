package uk.gov.hmcts.probate.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.config.notifications.EmailAddresses;
import uk.gov.hmcts.probate.config.notifications.NotificationTemplates;
import uk.gov.hmcts.probate.config.properties.registries.RegistriesProperties;
import uk.gov.hmcts.probate.exception.RequestInformationParameterException;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.LanguagePreference;
import uk.gov.hmcts.probate.model.SentEmail;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.NotificationService.CommonNotificationResult;
import uk.gov.hmcts.probate.service.documentmanagement.DocumentManagementService;
import uk.gov.hmcts.probate.service.notification.AutomatedNotificationPersonalisationService;
import uk.gov.hmcts.probate.service.notification.CaveatPersonalisationService;
import uk.gov.hmcts.probate.service.notification.GrantOfRepresentationPersonalisationService;
import uk.gov.hmcts.probate.service.notification.SentEmailPersonalisationService;
import uk.gov.hmcts.probate.service.notification.SmeeAndFordPersonalisationService;
import uk.gov.hmcts.probate.service.notification.TemplateService;
import uk.gov.hmcts.probate.service.template.pdf.LocalDateToWelshStringConverter;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.probate.service.NotificationService.RegistrarEscalationException;
import uk.gov.hmcts.probate.service.user.UserInfoService;
import uk.gov.hmcts.probate.validator.EmailAddressNotifyValidationRule;
import uk.gov.hmcts.probate.validator.PersonalisationValidationRule;
import uk.gov.hmcts.probate.validator.PersonalisationValidationRule.PersonalisationValidationResult;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.sendletter.api.SendLetterResponse;
import uk.gov.hmcts.reform.probate.model.idam.UserInfo;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.SendEmailResponse;
import uk.gov.service.notify.TemplatePreview;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class NotificationServiceTest {

    @Mock
    private EmailAddresses emailAddressesMock;
    @Mock
    private NotificationTemplates notificationTemplatesMock;
    @Mock
    private RegistriesProperties registriesPropertiesMock;
    @Mock
    private NotificationClient notificationClientMock;
    @Mock
    private TemplatePreview templatePreviewMock;
    @Mock
    private MarkdownTransformationService markdownTransformationServiceMock;
    @Mock
    private PDFManagementService pdfManagementServiceMock;

    @Mock
    private AutomatedNotificationPersonalisationService automatedNotificationPersonalisationServiceMock;

    @Mock
    private BulkPrintService bulkPrintServiceMock;
    @Mock
    private EventValidationService eventValidationServiceMock;
    @Mock
    private List<EmailAddressNotifyValidationRule> emailAddressNotifyValidationRulesMock;
    @Mock
    private GrantOfRepresentationPersonalisationService grantOfRepresentationPersonalisationServiceMock;
    @Mock
    private SmeeAndFordPersonalisationService smeeAndFordPersonalisationServiceMock;
    @Mock
    private CaveatPersonalisationService caveatPersonalisationServiceMock;
    @Mock
    private SentEmailPersonalisationService sentEmailPersonalisationServiceMock;
    @Mock
    private TemplateService templateServiceMock;
    @Mock
    private AuthTokenGenerator serviceAuthTokenGeneratorMock;
    @Mock
    private NotificationClientService notificationClientServiceMock;
    @Mock
    private DocumentManagementService documentManagementServiceMock;
    @Mock
    private PersonalisationValidationRule personalisationValidationRuleMock;
    @Mock
    private SendEmailResponse sendEmailResponseMock;

    @Mock
    private DocumentGeneratorService documentGeneratorServiceMock;
    @Mock
    private BusinessValidationMessageService businessValidationMessageServiceMock;
    @Mock
    private UserInfoService userInfoServiceMock;
    @Mock
    private ObjectMapper objectMapperMock;
    @Mock
    private EmailValidationService emailValidationServiceMock;
    @Mock
    private LocalDateToWelshStringConverter localDateToWelshStringConverterMock;

    private NotificationService notificationService;

    private AutoCloseable closeableMocks;
    private static final String SENT_EMAIL_FILE_NAME = "sentEmail.pdf";

    @BeforeEach
    void setUp() {
        closeableMocks = MockitoAnnotations.openMocks(this);

        notificationService = new NotificationService(
                emailAddressesMock,
                notificationTemplatesMock,
                registriesPropertiesMock,
                notificationClientMock,
                markdownTransformationServiceMock,
                pdfManagementServiceMock,
                eventValidationServiceMock,
                documentGeneratorServiceMock,
                bulkPrintServiceMock,
                emailAddressNotifyValidationRulesMock,
                grantOfRepresentationPersonalisationServiceMock,
                smeeAndFordPersonalisationServiceMock,
                caveatPersonalisationServiceMock,
                sentEmailPersonalisationServiceMock,
                templateServiceMock,
                serviceAuthTokenGeneratorMock,
                notificationClientServiceMock,
                documentManagementServiceMock,
                personalisationValidationRuleMock,
                businessValidationMessageServiceMock,
                automatedNotificationPersonalisationServiceMock,
                userInfoServiceMock,
                objectMapperMock,
                emailValidationServiceMock,
                localDateToWelshStringConverterMock);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeableMocks.close();
    }

    @Test
    void givenPersonalisationWithMarkdown_whenCommonValidation_thenThrows() {
        final Map<String, ?> dummyPersonalisation = Collections.emptyMap();
        final Long dummyCaseId = 1L;

        final PersonalisationValidationResult mockResult = new PersonalisationValidationResult(
                Map.of("fieldName", "Identified"),
                List.of());

        when(personalisationValidationRuleMock.validatePersonalisation(dummyPersonalisation))
                .thenReturn(mockResult);

        assertThrows(RequestInformationParameterException.class, () ->
                notificationService.doCommonNotificationServiceHandling(dummyPersonalisation, dummyCaseId));
    }

    @Test
    void givenPersonalisationWithHtml_whenCommonValidation_thenReturnsHtmlFound()
            throws RequestInformationParameterException {
        final Map<String, ?> dummyPersonalisation = Collections.emptyMap();
        final Long dummyCaseId = 1L;

        final PersonalisationValidationResult mockResult = new PersonalisationValidationResult(
                Map.of(),
                List.of("fieldName"));

        when(personalisationValidationRuleMock.validatePersonalisation(dummyPersonalisation))
                .thenReturn(mockResult);

        final var result = notificationService.doCommonNotificationServiceHandling(dummyPersonalisation, dummyCaseId);

        assertEquals(CommonNotificationResult.FOUND_HTML, result);
    }

    @Test
    void givenPersonalisationWithNoIssue_whenCommonValidation_thenReturnsAllOk()
            throws RequestInformationParameterException {
        final Map<String, ?> dummyPersonalisation = Collections.emptyMap();
        final Long dummyCaseId = 1L;

        final PersonalisationValidationResult mockResult = new PersonalisationValidationResult(
                Map.of(),
                List.of());

        when(personalisationValidationRuleMock.validatePersonalisation(dummyPersonalisation))
                .thenReturn(mockResult);

        final var result = notificationService.doCommonNotificationServiceHandling(dummyPersonalisation, dummyCaseId);

        assertEquals(CommonNotificationResult.ALL_OK, result);
    }

    @Test
    void shouldUpdatePersonalisationForSolicitor() throws NotificationClientException {
        CaseData caseData = mock(CaseData.class);
        CaseDetails caseDetails = mock(CaseDetails.class);
        when(caseDetails.getData()).thenReturn(caseData);
        when(caseData.getRegistryLocation()).thenReturn("oxford");
        when(caseData.getApplicationType()).thenReturn(ApplicationType.SOLICITOR);
        when(caseData.getLanguagePreference()).thenReturn(LanguagePreference.ENGLISH);
        when(caseData.getChannelChoice()).thenReturn("Digital");
        when(caseData.getSolsSolicitorEmail()).thenReturn("abc@gmail.com");
        when(caseData.getSolsSOTName()).thenReturn("OtherName");

        HashMap<String, Object> personalisation = new HashMap<>();
        personalisation.put("applicant_name", "FirstName");
        when(grantOfRepresentationPersonalisationServiceMock.getPersonalisation((CaseDetails) any(), any()))
                .thenReturn(personalisation);
        final PersonalisationValidationResult mockResult = new PersonalisationValidationResult(
                Map.of(),
                List.of());

        when(personalisationValidationRuleMock.validatePersonalisation(personalisation))
                .thenReturn(mockResult);
        when(notificationClientServiceMock.emailPreview(any(), any(), any())).thenReturn(templatePreviewMock);
        when(pdfManagementServiceMock.generateAndUpload(any(SentEmail.class), any())).thenReturn(Document.builder()
                .documentFileName(SENT_EMAIL_FILE_NAME).build());
        String expectedHtml = "<html><body>Test</body></html>";
        String expectedXhtml = "<xhtml><body>Test</body></xhtml>";
        when(templatePreviewMock.getHtml()).thenReturn(Optional.of(expectedHtml));
        when(pdfManagementServiceMock.rerenderAsXhtml(expectedHtml)).thenReturn(expectedXhtml);

        notificationService.emailPreview(caseDetails);

        verify(notificationClientServiceMock).emailPreview(any(), any(), any());
        assertEquals("OtherName", personalisation.get("applicant_name"));
    }

    @Test
    void shouldUpdatePersonalisationWithSolicitorName() {
        CaseData caseData = mock(CaseData.class);
        CaseDetails caseDetails = mock(CaseDetails.class);
        when(caseDetails.getData()).thenReturn(caseData);
        when(caseData.getApplicationType()).thenReturn(ApplicationType.SOLICITOR);
        when(caseData.getSolsSolicitorEmail()).thenReturn("abc@gmail.com");
        when(caseData.getSolsSOTName()).thenReturn("John Doe");

        Map<String, Object> personalisation = new HashMap<>();
        personalisation.put("applicant_name", "Old Name");

        notificationService.updatePersonalisationForSolicitor(caseData, personalisation);

        assertEquals("John Doe", personalisation.get("applicant_name"));
    }

    @Test
    void shouldUpdatePersonalisationWithSolicitorForenamesAndSurname() {
        CaseData caseData = mock(CaseData.class);
        CaseDetails caseDetails = mock(CaseDetails.class);
        when(caseDetails.getData()).thenReturn(caseData);
        when(caseData.getApplicationType()).thenReturn(ApplicationType.SOLICITOR);
        when(caseData.getSolsSolicitorEmail()).thenReturn("abc@gmail.com");
        when(caseData.getSolsSOTForenames()).thenReturn("John");
        when(caseData.getSolsSOTSurname()).thenReturn("Doe");

        Map<String, Object> personalisation = new HashMap<>();
        personalisation.put("applicant_name", "Old Name");

        notificationService.updatePersonalisationForSolicitor(caseData, personalisation);

        assertEquals("John Doe", personalisation.get("applicant_name"));
    }

    @Test
    void shouldNotUpdatePersonalisationWhenApplicationTypeIsNotSolicitor() {
        CaseData caseData = mock(CaseData.class);
        CaseDetails caseDetails = mock(CaseDetails.class);
        when(caseDetails.getData()).thenReturn(caseData);
        when(caseData.getApplicationType()).thenReturn(ApplicationType.PERSONAL);
        when(caseData.getSolsSolicitorEmail()).thenReturn("abc@gmail.com");
        when(caseData.getSolsSOTForenames()).thenReturn("John");
        when(caseData.getSolsSOTSurname()).thenReturn("Doe");

        Map<String, Object> personalisation = new HashMap<>();
        personalisation.put("applicant_name", "Old Name");

        notificationService.updatePersonalisationForSolicitor(caseData, personalisation);

        assertEquals("Old Name", personalisation.get("applicant_name"));
    }

    @Test
    void returnsNullWhenCaseDataIsNull() {
        uk.gov.hmcts.reform.ccd.client.model.CaseDetails caseDetails =
                mock(uk.gov.hmcts.reform.ccd.client.model.CaseDetails.class);
        when(caseDetails.getData()).thenReturn(null);

        Document result = notificationService.sendDormantReminder(caseDetails);

        assertNull(result);
        verifyNoInteractions(pdfManagementServiceMock);
        verifyNoInteractions(bulkPrintServiceMock);
    }

    @Test
    void generatesDormantReminderSuccessfullyForEnglishLanguage() {
        uk.gov.hmcts.reform.ccd.client.model.CaseDetails caseDetails =
                mock(uk.gov.hmcts.reform.ccd.client.model.CaseDetails.class);
        Map<String, Object> data = new HashMap<>();
        data.put("applicationType", ApplicationType.PERSONAL);
        data.put("languagePreference", LanguagePreference.ENGLISH);
        when(caseDetails.getData()).thenReturn(data);
        when(pdfManagementServiceMock.generateDocmosisDocumentAndUpload(any(), eq(DocumentType.DORMANT_REMINDER)))
                .thenReturn(mock(Document.class));
        when(bulkPrintServiceMock.sendToBulkPrintForGrant(any(), any(), any()))
                .thenReturn(new SendLetterResponse(UUID.randomUUID()));

        Document result = notificationService.sendDormantReminder(caseDetails);

        assertNotNull(result);
        assertTrue(data.containsKey("bulkPrint"));
        verify(pdfManagementServiceMock).generateDocmosisDocumentAndUpload(any(), eq(DocumentType.DORMANT_REMINDER));
        verify(bulkPrintServiceMock).sendToBulkPrintForGrant(any(), any(), any());
    }


    @Test
    void generatesDormantReminderSuccessfullyForWelshLanguage() {
        uk.gov.hmcts.reform.ccd.client.model.CaseDetails caseDetails =
                mock(uk.gov.hmcts.reform.ccd.client.model.CaseDetails.class);
        Map<String, Object> data = new HashMap<>();
        data.put("applicationType", ApplicationType.PERSONAL);
        data.put("languagePreferenceWelsh", "Yes");
        when(caseDetails.getData()).thenReturn(data);

        when(pdfManagementServiceMock.generateDocmosisDocumentAndUpload(any(Map.class),
                eq(DocumentType.WELSH_DORMANT_REMINDER))).thenReturn(Document.builder()
                .documentFileName(SENT_EMAIL_FILE_NAME).documentType(DocumentType.WELSH_DORMANT_REMINDER).build());
        when(bulkPrintServiceMock.sendToBulkPrintForGrant(any(), any(), any()))
                .thenReturn(new SendLetterResponse(UUID.randomUUID()));

        Document result = notificationService.sendDormantReminder(caseDetails);

        assertNotNull(result);
        assertTrue(data.containsKey("bulkPrint"));
        verify(pdfManagementServiceMock).generateDocmosisDocumentAndUpload(any(),
                eq(DocumentType.WELSH_DORMANT_REMINDER));
        verify(bulkPrintServiceMock).sendToBulkPrintForGrant(any(), any(), any());
    }

    @Test
    void sendStopResponseReceivedEmailSuccessfully() throws NotificationClientException {
        CaseDetails caseDetails = mock(CaseDetails.class);
        CaseData caseData = mock(CaseData.class);
        when(caseDetails.getId()).thenReturn(12345L);
        when(caseDetails.getData()).thenReturn(caseData);
        when(caseData.getApplicationType()).thenReturn(ApplicationType.SOLICITOR);
        when(caseData.getLanguagePreference()).thenReturn(LanguagePreference.ENGLISH);
        when(caseData.getSolsSOTName()).thenReturn("Solicitor Name");
        when(caseData.getPrimaryApplicantFullName()).thenReturn("Applicant Name");
        when(caseData.getSolsSolicitorEmail()).thenReturn("test@example.com");
        when(pdfManagementServiceMock.generateAndUpload(any(SentEmail.class), any())).thenReturn(Document.builder()
                .documentFileName(SENT_EMAIL_FILE_NAME).build());
        when(templateServiceMock.getStopResponseReceivedTemplateId(ApplicationType.SOLICITOR,
                LanguagePreference.ENGLISH))
                .thenReturn("template-id");
        when(grantOfRepresentationPersonalisationServiceMock.getStopResponseReceivedPersonalisation(
                12345L, "Solicitor Name"))
                .thenReturn(Map.of("key", "value"));
        when(notificationClientServiceMock.sendEmail("template-id",
                "test@example.com", Map.of("key", "value"), "12345"))
                .thenReturn(sendEmailResponseMock);
        when(sendEmailResponseMock.getReference()).thenReturn(Optional.of("email-reference"));

        Document result = notificationService.sendStopResponseReceivedEmail(caseDetails);

        assertNotNull(result);
        verify(notificationClientServiceMock).sendEmail("template-id",
                "test@example.com", Map.of("key", "value"), "12345");
    }

    @Test
    void sendStopResponseReceivedEmailThrowsExceptionWhenCaseDataIsNull() {
        CaseDetails caseDetails = mock(CaseDetails.class);
        CaseData caseData = mock(CaseData.class);
        when(caseDetails.getId()).thenReturn(12345L);
        when(caseDetails.getData()).thenReturn(caseData);
        when(caseData.getApplicationType()).thenReturn(ApplicationType.SOLICITOR);
        when(caseData.getLanguagePreference()).thenReturn(LanguagePreference.ENGLISH);
        when(caseData.getSolsSOTName()).thenReturn("Solicitor Name");

        NotificationClientException exception = assertThrows(NotificationClientException.class, () ->
                notificationService.sendStopResponseReceivedEmail(caseDetails)
        );

        assertEquals("Email address not found for StopResponseReceivedEmail case ID: 12345",
                exception.getMessage());
    }

    @Test
    void sendStopResponseReceivedEmailThrowsExceptionWhenEmailIsNull() throws NotificationClientException {
        CaseDetails caseDetails = mock(CaseDetails.class);
        CaseData caseData = mock(CaseData.class);
        when(caseDetails.getId()).thenReturn(12345L);
        when(caseDetails.getData()).thenReturn(caseData);
        when(caseData.getApplicationType()).thenReturn(ApplicationType.PERSONAL);
        when(caseData.getLanguagePreference()).thenReturn(LanguagePreference.ENGLISH);
        when(caseData.getSolsSOTName()).thenReturn("Solicitor Name");
        when(templateServiceMock.getStopResponseReceivedTemplateId(
                ApplicationType.PERSONAL, LanguagePreference.ENGLISH)).thenReturn("template-id");
        when(grantOfRepresentationPersonalisationServiceMock.getStopResponseReceivedPersonalisation(
                12345L, "Solicitor Name"))
                .thenReturn(Map.of("key", "value"));
        when(notificationClientServiceMock.sendEmail("template-id", null,
                Map.of("key", "value"), "12345"))
                .thenThrow(new NotificationClientException("Email address not found"));

        NotificationClientException exception = assertThrows(NotificationClientException.class, () ->
                notificationService.sendStopResponseReceivedEmail(caseDetails)
        );

        assertEquals("Email address not found for StopResponseReceivedEmail case ID: 12345",
                exception.getMessage());
    }

    @Test
    void shouldSendRegistrarEscalatedForPP() throws NotificationClientException, RegistrarEscalationException {
        final String applEmail = "abc@gmail.com";
        final String tmplId = "tmplId";
        final String decName = "decName";
        final String applName = "applName";
        final LocalDate decdDod = LocalDate.of(2024, 8, 5);
        final String decdDodFrm = "decdDodFrm";
        final String decdDodCy = "decdDodCy";

        final CaseData caseData = mock(CaseData.class);
        final CaseDetails caseDetails = mock(CaseDetails.class);

        final SendEmailResponse sendEmailResponseMock = mock(SendEmailResponse.class);

        final Document documentMock = mock(Document.class);

        when(caseDetails.getData()).thenReturn(caseData);
        when(caseDetails.getId()).thenReturn(1L);

        when(caseData.getApplicationType())
                .thenReturn(ApplicationType.SOLICITOR);
        when(caseData.getSolsSolicitorEmail())
                .thenReturn(applEmail);
        when(caseData.getSolsSOTName())
                .thenReturn(applName);
        when(caseData.getDeceasedFullName())
                .thenReturn(decName);
        when(caseData.getDeceasedDateOfDeath())
                .thenReturn(decdDod);
        when(caseData.getDeceasedDateOfDeathFormatted())
                .thenReturn(decdDodFrm);

        when(localDateToWelshStringConverterMock.convert(any()))
                .thenReturn(decdDodCy);

        when(templateServiceMock.getRegistrarEscalationNotification(any(), any()))
                .thenReturn(tmplId);

        when(notificationClientServiceMock.sendEmail(eq(tmplId), eq(applEmail), any(), any()))
                .thenReturn(sendEmailResponseMock);

        when(pdfManagementServiceMock.generateAndUpload(any(SentEmail.class), any()))
                .thenReturn(documentMock);

        final Document result = notificationService.sendRegistrarEscalationNotification(caseDetails);

        @SuppressWarnings("unchecked")
        final ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        verify(notificationClientServiceMock).sendEmail(eq(tmplId), eq(applEmail), captor.capture(), any());
        final Map<String, Object> captured = captor.getValue();
        assertAll(
                () -> assertThat(captured.size(), greaterThanOrEqualTo(5)),
                () -> assertThat(captured, hasKey("ccd_reference")),
                () -> assertThat(captured, hasKey("deceased_name")),
                () -> assertThat(captured, hasKey("deceased_dod")),
                () -> assertThat(captured, hasKey("deceased_dod_cy")),
                () -> assertThat(captured, hasKey("applicant_name")),
                () -> assertThat(result, sameInstance(documentMock)));
    }

    @Test
    void shouldSendRegistrarEscalatedForPA() throws NotificationClientException, RegistrarEscalationException {
        final String applEmail = "abc@gmail.com";
        final String tmplId = "tmplId";
        final String decName = "decName";
        final String applName = "applName";
        final LocalDate decdDod = LocalDate.of(2024, 8, 5);
        final String decdDodFrm = "decdDodFrm";
        final String decdDodCy = "decdDodCy";

        final CaseData caseData = mock(CaseData.class);
        final CaseDetails caseDetails = mock(CaseDetails.class);

        final SendEmailResponse sendEmailResponseMock = mock(SendEmailResponse.class);

        final Document documentMock = mock(Document.class);

        when(caseDetails.getData()).thenReturn(caseData);
        when(caseDetails.getId()).thenReturn(1L);

        when(caseData.getApplicationType())
                .thenReturn(ApplicationType.PERSONAL);
        when(caseData.getPrimaryApplicantEmailAddress())
                .thenReturn(applEmail);
        when(caseData.getPrimaryApplicantFullName())
                .thenReturn(applName);
        when(caseData.getDeceasedFullName())
                .thenReturn(decName);
        when(caseData.getDeceasedDateOfDeath())
                .thenReturn(decdDod);
        when(caseData.getDeceasedDateOfDeathFormatted())
                .thenReturn(decdDodFrm);

        when(localDateToWelshStringConverterMock.convert(any()))
                .thenReturn(decdDodCy);

        when(templateServiceMock.getRegistrarEscalationNotification(any(), any()))
                .thenReturn(tmplId);

        when(notificationClientServiceMock.sendEmail(eq(tmplId), eq(applEmail), any(), any()))
                .thenReturn(sendEmailResponseMock);

        when(pdfManagementServiceMock.generateAndUpload(any(SentEmail.class), any()))
                .thenReturn(documentMock);

        final Document result = notificationService.sendRegistrarEscalationNotification(caseDetails);

        @SuppressWarnings("unchecked")
        final ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        verify(notificationClientServiceMock).sendEmail(eq(tmplId), eq(applEmail), captor.capture(), any());
        final Map<String, Object> captured = captor.getValue();
        assertAll(
                () -> assertThat(captured.size(), greaterThanOrEqualTo(5)),
                () -> assertThat(captured, hasKey("ccd_reference")),
                () -> assertThat(captured, hasKey("deceased_name")),
                () -> assertThat(captured, hasKey("deceased_dod")),
                () -> assertThat(captured, hasKey("deceased_dod_cy")),
                () -> assertThat(captured, hasKey("applicant_name")),
                () -> assertThat(result, sameInstance(documentMock)));
    }

    @Test
    void shouldReturnNullIfRegistrarEscalatedDocumentGenThrows()
            throws NotificationClientException, RegistrarEscalationException {
        final String applEmail = "abc@gmail.com";
        final String tmplId = "tmplId";
        final String decName = "decName";
        final String applName = "applName";
        final LocalDate decdDod = LocalDate.of(2024, 8, 5);
        final String decdDodFrm = "decdDodFrm";
        final String decdDodCy = "decdDodCy";

        final CaseData caseData = mock(CaseData.class);
        final CaseDetails caseDetails = mock(CaseDetails.class);

        final SendEmailResponse sendEmailResponseMock = mock(SendEmailResponse.class);

        when(caseDetails.getData()).thenReturn(caseData);
        when(caseDetails.getId()).thenReturn(1L);

        when(caseData.getApplicationType())
                .thenReturn(ApplicationType.PERSONAL);
        when(caseData.getPrimaryApplicantEmailAddress())
                .thenReturn(applEmail);
        when(caseData.getPrimaryApplicantFullName())
                .thenReturn(applName);
        when(caseData.getDeceasedFullName())
                .thenReturn(decName);
        when(caseData.getDeceasedDateOfDeath())
                .thenReturn(decdDod);
        when(caseData.getDeceasedDateOfDeathFormatted())
                .thenReturn(decdDodFrm);

        when(localDateToWelshStringConverterMock.convert(any()))
                .thenReturn(decdDodCy);

        when(templateServiceMock.getRegistrarEscalationNotification(any(), any()))
                .thenReturn(tmplId);

        when(notificationClientServiceMock.sendEmail(eq(tmplId), eq(applEmail), any(), any()))
                .thenReturn(sendEmailResponseMock);

        when(pdfManagementServiceMock.generateAndUpload(any(SentEmail.class), any()))
                .thenThrow(mock(RuntimeException.class));

        final Document result = notificationService.sendRegistrarEscalationNotification(caseDetails);

        @SuppressWarnings("unchecked")
        final ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        verify(notificationClientServiceMock).sendEmail(eq(tmplId), eq(applEmail), captor.capture(), any());
        final Map<String, Object> captured = captor.getValue();
        assertAll(
                () -> assertThat(captured.size(), greaterThanOrEqualTo(5)),
                () -> assertThat(captured, hasKey("ccd_reference")),
                () -> assertThat(captured, hasKey("deceased_name")),
                () -> assertThat(captured, hasKey("deceased_dod")),
                () -> assertThat(captured, hasKey("deceased_dod_cy")),
                () -> assertThat(captured, hasKey("applicant_name")),
                () -> assertThat(result, nullValue()));
    }

    @Test
    void shouldThrowIfSendRegistrarEscalatedFailed() throws NotificationClientException {
        final String applEmail = "abc@gmail.com";
        final String tmplId = "tmplId";
        final String decName = "decName";
        final String applName = "applName";
        final LocalDate decdDod = LocalDate.of(2024, 8, 5);
        final String decdDodFrm = "decdDodFrm";
        final String decdDodCy = "decdDodCy";

        final CaseData caseData = mock(CaseData.class);
        final CaseDetails caseDetails = mock(CaseDetails.class);

        final NotificationClientException notificationClientExceptionMock = mock(NotificationClientException.class);

        when(caseDetails.getData()).thenReturn(caseData);
        when(caseDetails.getId()).thenReturn(1L);

        when(caseData.getApplicationType())
                .thenReturn(ApplicationType.SOLICITOR);
        when(caseData.getSolsSolicitorEmail())
                .thenReturn(applEmail);
        when(caseData.getSolsSOTName())
                .thenReturn(applName);
        when(caseData.getDeceasedFullName())
                .thenReturn(decName);
        when(caseData.getDeceasedDateOfDeath())
                .thenReturn(decdDod);
        when(caseData.getDeceasedDateOfDeathFormatted())
                .thenReturn(decdDodFrm);

        when(localDateToWelshStringConverterMock.convert(any()))
                .thenReturn(decdDodCy);

        when(templateServiceMock.getRegistrarEscalationNotification(any(), any()))
                .thenReturn(tmplId);

        when(notificationClientServiceMock.sendEmail(eq(tmplId), eq(applEmail), any(), any()))
                .thenThrow(notificationClientExceptionMock);

        final RegistrarEscalationException registrarEscalationException = assertThrows(
                RegistrarEscalationException.class,
                () -> notificationService.sendRegistrarEscalationNotification(caseDetails));

        @SuppressWarnings("unchecked")
        final ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        verify(notificationClientServiceMock).sendEmail(eq(tmplId), eq(applEmail), captor.capture(), any());
        final Map<String, Object> captured = captor.getValue();
        assertAll(
                () -> assertThat(captured.size(), greaterThanOrEqualTo(5)),
                () -> assertThat(captured, hasKey("ccd_reference")),
                () -> assertThat(captured, hasKey("deceased_name")),
                () -> assertThat(captured, hasKey("deceased_dod")),
                () -> assertThat(captured, hasKey("deceased_dod_cy")),
                () -> assertThat(captured, hasKey("applicant_name")),
                () -> assertThat(
                        registrarEscalationException.getCause(),
                        sameInstance(notificationClientExceptionMock)));

        verify(pdfManagementServiceMock, never()).generateAndUpload(any(SentEmail.class), any());
    }

    @Test
    void shouldSendRegistrarEscalatedFailed() throws NotificationClientException {
        final String cwEmail = "abc@gmail.com";
        final String cwName = "cwName";
        final String tmplId = "tmplId";
        final String decName = "decName";

        final CaseData caseData = mock(CaseData.class);
        final CaseDetails caseDetails = mock(CaseDetails.class);

        final SendEmailResponse sendEmailResponseMock = mock(SendEmailResponse.class);

        final Document documentMock = mock(Document.class);

        final UserInfo userInfoMock = mock(UserInfo.class);
        when(userInfoMock.getSub())
                .thenReturn(cwEmail);
        when(userInfoMock.getName())
                .thenReturn(cwName);
        final Optional<UserInfo> caseworkerInfo = Optional.of(userInfoMock);

        when(caseDetails.getData()).thenReturn(caseData);
        when(caseDetails.getId()).thenReturn(1L);

        when(caseData.getApplicationType())
                .thenReturn(ApplicationType.SOLICITOR);
        when(caseData.getDeceasedFullName())
                .thenReturn(decName);

        when(templateServiceMock.getRegistrarEscalationNotificationFailed(any(), any()))
                .thenReturn(tmplId);

        when(notificationClientServiceMock.sendEmail(eq(tmplId), eq(cwEmail), any(), any()))
                .thenReturn(sendEmailResponseMock);

        when(pdfManagementServiceMock.generateAndUpload(any(SentEmail.class), any()))
                .thenReturn(documentMock);

        final Document result = notificationService.sendRegistrarEscalationNotificationFailed(
                caseDetails,
                caseworkerInfo);

        @SuppressWarnings("unchecked")
        final ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        verify(notificationClientServiceMock).sendEmail(eq(tmplId), eq(cwEmail), captor.capture(), any());
        final Map<String, Object> captured = captor.getValue();
        assertAll(
                () -> assertThat(captured.size(), greaterThanOrEqualTo(3)),
                () -> assertThat(captured, hasKey("ccd_reference")),
                () -> assertThat(captured, hasKey("deceased_name")),
                () -> assertThat(captured, hasKey("caseworker_name")),
                () -> assertThat(result, sameInstance(documentMock)));
    }

    @Test
    void shouldNotSendRegistrarEscalatedFailedIfNoCaseworker() {
        final String decName = "decName";

        final CaseData caseData = mock(CaseData.class);
        final CaseDetails caseDetails = mock(CaseDetails.class);

        final Optional<UserInfo> caseworkerInfo = Optional.empty();

        when(caseDetails.getData()).thenReturn(caseData);
        when(caseDetails.getId()).thenReturn(1L);

        when(caseData.getDeceasedFullName())
                .thenReturn(decName);

        final Document result = notificationService.sendRegistrarEscalationNotificationFailed(
                caseDetails,
                caseworkerInfo);

        verify(templateServiceMock, never()).getRegistrarEscalationNotificationFailed(any(), any());
        assertThat(result, nullValue());
    }

    @Test
    void shouldReturnNullIfSendRegistrarEscalatedFailedFails() throws NotificationClientException {
        final String cwEmail = "abc@gmail.com";
        final String cwName = "cwName";
        final String tmplId = "tmplId";
        final String decName = "decName";

        final CaseData caseData = mock(CaseData.class);
        final CaseDetails caseDetails = mock(CaseDetails.class);

        final NotificationClientException notificationClientExceptionMock = mock(NotificationClientException.class);

        final UserInfo userInfoMock = mock(UserInfo.class);
        when(userInfoMock.getSub())
                .thenReturn(cwEmail);
        when(userInfoMock.getName())
                .thenReturn(cwName);
        final Optional<UserInfo> caseworkerInfo = Optional.of(userInfoMock);

        when(caseDetails.getData()).thenReturn(caseData);
        when(caseDetails.getId()).thenReturn(1L);

        when(caseData.getApplicationType())
                .thenReturn(ApplicationType.SOLICITOR);
        when(caseData.getDeceasedFullName())
                .thenReturn(decName);

        when(templateServiceMock.getRegistrarEscalationNotificationFailed(any(), any()))
                .thenReturn(tmplId);

        when(notificationClientServiceMock.sendEmail(eq(tmplId), eq(cwEmail), any(), any()))
                .thenThrow(notificationClientExceptionMock);

        final Document result = notificationService.sendRegistrarEscalationNotificationFailed(
                caseDetails,
                caseworkerInfo);

        @SuppressWarnings("unchecked")
        final ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        verify(notificationClientServiceMock).sendEmail(eq(tmplId), eq(cwEmail), captor.capture(), any());
        final Map<String, Object> captured = captor.getValue();
        assertAll(
                () -> assertThat(captured.size(), greaterThanOrEqualTo(3)),
                () -> assertThat(captured, hasKey("ccd_reference")),
                () -> assertThat(captured, hasKey("deceased_name")),
                () -> assertThat(captured, hasKey("caseworker_name")),
                () -> assertThat(result, nullValue()));

        verify(pdfManagementServiceMock, never())
                .generateAndUpload(any(SentEmail.class), any());
    }

    @Test
    void shouldReturnNullIfSendRegistrarEscalatedDocGenFails() throws NotificationClientException {
        final String cwEmail = "abc@gmail.com";
        final String cwName = "cwName";
        final String tmplId = "tmplId";
        final String decName = "decName";

        final CaseData caseData = mock(CaseData.class);
        final CaseDetails caseDetails = mock(CaseDetails.class);

        final SendEmailResponse sendEmailResponse = mock(SendEmailResponse.class);

        final RuntimeException runtimeExceptionMock = mock(RuntimeException.class);

        final UserInfo userInfoMock = mock(UserInfo.class);
        when(userInfoMock.getSub())
                .thenReturn(cwEmail);
        when(userInfoMock.getName())
                .thenReturn(cwName);
        final Optional<UserInfo> caseworkerInfo = Optional.of(userInfoMock);

        when(caseDetails.getData()).thenReturn(caseData);
        when(caseDetails.getId()).thenReturn(1L);

        when(caseData.getApplicationType())
                .thenReturn(ApplicationType.SOLICITOR);
        when(caseData.getDeceasedFullName())
                .thenReturn(decName);

        when(templateServiceMock.getRegistrarEscalationNotificationFailed(any(), any()))
                .thenReturn(tmplId);

        when(notificationClientServiceMock.sendEmail(eq(tmplId), eq(cwEmail), any(), any()))
                .thenReturn(sendEmailResponse);

        when(pdfManagementServiceMock.generateAndUpload(any(SentEmail.class), any()))
                .thenThrow(runtimeExceptionMock);

        final Document result = notificationService.sendRegistrarEscalationNotificationFailed(
                caseDetails,
                caseworkerInfo);

        @SuppressWarnings("unchecked")
        final ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        verify(notificationClientServiceMock).sendEmail(eq(tmplId), eq(cwEmail), captor.capture(), any());
        final Map<String, Object> captured = captor.getValue();
        assertAll(
                () -> assertThat(captured.size(), greaterThanOrEqualTo(3)),
                () -> assertThat(captured, hasKey("ccd_reference")),
                () -> assertThat(captured, hasKey("deceased_name")),
                () -> assertThat(captured, hasKey("caseworker_name")),
                () -> assertThat(result, nullValue()));

        verify(pdfManagementServiceMock, times(1))
                .generateAndUpload(any(SentEmail.class), any());
    }
}
