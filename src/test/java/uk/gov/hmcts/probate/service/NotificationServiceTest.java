package uk.gov.hmcts.probate.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.config.notifications.EmailAddresses;
import uk.gov.hmcts.probate.config.notifications.NotificationTemplates;
import uk.gov.hmcts.probate.config.properties.registries.RegistriesProperties;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.LanguagePreference;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.exception.RequestInformationParameterException;
import uk.gov.hmcts.probate.service.documentmanagement.DocumentManagementService;
import uk.gov.hmcts.probate.service.notification.CaveatPersonalisationService;
import uk.gov.hmcts.probate.service.notification.GrantOfRepresentationPersonalisationService;
import uk.gov.hmcts.probate.service.notification.SentEmailPersonalisationService;
import uk.gov.hmcts.probate.service.notification.SmeeAndFordPersonalisationService;
import uk.gov.hmcts.probate.service.notification.TemplateService;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.probate.service.NotificationService.CommonNotificationResult;
import uk.gov.hmcts.probate.validator.EmailAddressNotifyValidationRule;
import uk.gov.hmcts.probate.validator.PersonalisationValidationRule;
import uk.gov.hmcts.probate.validator.PersonalisationValidationRule.PersonalisationValidationResult;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.TemplatePreview;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
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
    private MarkdownTransformationService markdownTransformationServiceMock;
    @Mock
    private PDFManagementService pdfManagementServiceMock;
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
    private BusinessValidationMessageService businessValidationMessageService;

    @InjectMocks
    private NotificationService notificationService;

    private AutoCloseable closeableMocks;

    @BeforeEach
    void setUp() {
        closeableMocks = MockitoAnnotations.openMocks(this);
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
        when(notificationClientServiceMock.emailPreview(any(), any(), any())).thenReturn(mock(TemplatePreview.class));

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
}
