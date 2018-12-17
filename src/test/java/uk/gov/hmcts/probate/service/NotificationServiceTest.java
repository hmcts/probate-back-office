package uk.gov.hmcts.probate.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.probate.config.properties.registries.RegistriesProperties;
import uk.gov.hmcts.probate.config.properties.registries.Registry;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.insights.AppInsights;
import uk.gov.hmcts.probate.model.SentEmail;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.SendEmailResponse;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.ApplicationType.PERSONAL;
import static uk.gov.hmcts.probate.model.ApplicationType.SOLICITOR;
import static uk.gov.hmcts.probate.model.DocumentType.SENT_EMAIL;
import static uk.gov.hmcts.probate.model.State.CASE_STOPPED;
import static uk.gov.hmcts.probate.model.State.DOCUMENTS_RECEIVED;
import static uk.gov.hmcts.probate.model.State.GENERAL_CAVEAT_MESSAGE;
import static uk.gov.hmcts.probate.model.State.GRANT_ISSUED;

@RunWith(SpringRunner.class)
@SpringBootTest
public class NotificationServiceTest {

    @Autowired
    private NotificationService notificationService;

    @MockBean
    private AppInsights appInsights;

    @MockBean
    private SendEmailResponse sendEmailResponse;

    @MockBean
    private PDFManagementService pdfManagementService;

    @MockBean
    private RegistriesProperties registriesProperties;

    @SpyBean
    private NotificationClient notificationClient;

    private Registry registry;
    private CaseDetails personalCaseDataOxford;
    private CaseDetails solicitorCaseDataOxford;
    private CaseDetails personalCaseDataBirmingham;
    private CaseDetails solicitorCaseDataBirmingham;
    private CaseDetails personalCaseDataManchester;
    private CaseDetails solicitorCaseDataManchester;

    private CaveatDetails personalCaveatDataOxford;
    private CaveatDetails personalCaveatDataBirmingham;
    private CaveatDetails personalCaveatDataManchester;
    private CaveatDetails personalCaveatDataLeeds;

    private static final Long ID = 1L;
    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};

    @Before
    public void setUp() throws NotificationClientException {
        registry = new Registry();
        registry.setName("test");
        registry.setPhone("123456");

        when(sendEmailResponse.getFromEmail()).thenReturn(Optional.of("test@test.com"));
        when(sendEmailResponse.getBody()).thenReturn("test-body");

        doReturn(sendEmailResponse).when(notificationClient).sendEmail(anyString(), anyString(), any(), isNull());
        doReturn(sendEmailResponse).when(notificationClient).sendEmail(any(), any(), any(), any(), any());



        personalCaseDataOxford = new CaseDetails(CaseData.builder()
                .applicationType(PERSONAL)
                .registryLocation("Oxford")
                .primaryApplicantEmailAddress("personal@test.com")
                .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
                .build(), LAST_MODIFIED, ID);

        solicitorCaseDataOxford = new CaseDetails(CaseData.builder()
                .applicationType(SOLICITOR)
                .registryLocation("Oxford")
                .solsSolicitorEmail("solicitor@test.com")
                .solsSolicitorAppReference("1234-5678-9012")
                .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
                .build(), LAST_MODIFIED, ID);

        personalCaseDataBirmingham = new CaseDetails(CaseData.builder()
                .applicationType(PERSONAL)
                .registryLocation("Birmingham")
                .primaryApplicantEmailAddress("personal@test.com")
                .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
                .build(), LAST_MODIFIED, ID);

        solicitorCaseDataBirmingham = new CaseDetails(CaseData.builder()
                .applicationType(SOLICITOR)
                .registryLocation("Birmingham")
                .solsSolicitorEmail("solicitor@test.com")
                .solsSolicitorAppReference("1234-5678-9012")
                .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
                .build(), LAST_MODIFIED, ID);

        personalCaseDataManchester = new CaseDetails(CaseData.builder()
                .applicationType(PERSONAL)
                .registryLocation("Manchester")
                .primaryApplicantEmailAddress("personal@test.com")
                .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
                .build(), LAST_MODIFIED, ID);

        solicitorCaseDataManchester = new CaseDetails(CaseData.builder()
                .applicationType(SOLICITOR)
                .registryLocation("Manchester")
                .solsSolicitorEmail("solicitor@test.com")
                .solsSolicitorAppReference("1234-5678-9012")
                .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
                .build(), LAST_MODIFIED, ID);

        personalCaveatDataOxford = new CaveatDetails(CaveatData.builder()
                .applicationType(PERSONAL)
                .registryLocation("Oxford")
                .caveatorEmailAddress("personal@test.com")
                .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
                .build(), LAST_MODIFIED, ID);

        personalCaveatDataBirmingham = new CaveatDetails(CaveatData.builder()
                .applicationType(PERSONAL)
                .registryLocation("Birmingham")
                .caveatorEmailAddress("personal@test.com")
                .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
                .build(), LAST_MODIFIED, ID);

        personalCaveatDataManchester = new CaveatDetails(CaveatData.builder()
                .applicationType(PERSONAL)
                .registryLocation("Manchester")
                .caveatorEmailAddress("personal@test.com")
                .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
                .build(), LAST_MODIFIED, ID);

        personalCaveatDataLeeds = new CaveatDetails(CaveatData.builder()
                .applicationType(PERSONAL)
                .registryLocation("Leeds")
                .caveatorEmailAddress("personal@test.com")
                .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
                .build(), LAST_MODIFIED, ID);
    }

    @Test
    public void sendDocumentsReceivedEmailToPersonalApplicantFromBirmingham()
            throws NotificationClientException, BadRequestException {

        registry.setEmailReplyToId("oxford-emailReplyToId");
        HashMap<String, Registry> map = new HashMap<>();
        HashMap<String, Registry> spyMap = spy(map);
        spyMap.put("leads", registry);
        when(registriesProperties.getRegistries()).thenReturn(spyMap);
        when(spyMap.get(anyString())).thenReturn(registry);

        notificationService.sendEmail(DOCUMENTS_RECEIVED, personalCaseDataBirmingham);

        verify(notificationClient).sendEmail(
                eq("pa-document-received"),
                eq("personal@test.com"),
                any(),
                isNull());

        verify(pdfManagementService).generateAndUpload(any(SentEmail.class), eq(SENT_EMAIL));
    }

    @Test
    public void sendDocumentsReceivedEmailToSolicitorFromBirmingham()
            throws NotificationClientException, BadRequestException {

        registry.setEmailReplyToId("oxford-emailReplyToId");
        HashMap<String, Registry> map = new HashMap<>();
        HashMap<String, Registry> spyMap = spy(map);
        spyMap.put("leads", registry);
        when(registriesProperties.getRegistries()).thenReturn(spyMap);
        when(spyMap.get(anyString())).thenReturn(registry);

        notificationService.sendEmail(DOCUMENTS_RECEIVED, solicitorCaseDataBirmingham);

        verify(notificationClient).sendEmail(
                eq("sol-document-received"),
                eq("solicitor@test.com"),
                any(),
                eq("1234-5678-9012"));

        verify(pdfManagementService).generateAndUpload(any(SentEmail.class), eq(SENT_EMAIL));
    }

    @Test
    public void sendGrantIssuedEmailToPersonalApplicantFromBirmingham()
            throws NotificationClientException, BadRequestException {

        registry.setEmailReplyToId("oxford-emailReplyToId");
        HashMap<String, Registry> map = new HashMap<>();
        HashMap<String, Registry> spyMap = spy(map);
        spyMap.put("leads", registry);
        when(registriesProperties.getRegistries()).thenReturn(spyMap);
        when(spyMap.get(anyString())).thenReturn(registry);

        notificationService.sendEmail(GRANT_ISSUED, personalCaseDataBirmingham);

        verify(notificationClient).sendEmail(
                eq("pa-grant-issued"),
                eq("personal@test.com"),
                any(),
                isNull());

        verify(pdfManagementService).generateAndUpload(any(SentEmail.class), eq(SENT_EMAIL));
    }

    @Test
    public void sendGrantIssuedEmailToSolicitorFromBirmingham()
            throws NotificationClientException, BadRequestException {

        registry.setEmailReplyToId("oxford-emailReplyToId");
        HashMap<String, Registry> map = new HashMap<>();
        HashMap<String, Registry> spyMap = spy(map);
        spyMap.put("leads", registry);
        when(registriesProperties.getRegistries()).thenReturn(spyMap);
        when(spyMap.get(anyString())).thenReturn(registry);

        notificationService.sendEmail(GRANT_ISSUED, solicitorCaseDataBirmingham);

        verify(notificationClient).sendEmail(
                eq("sol-grant-issued"),
                eq("solicitor@test.com"),
                any(),
                eq("1234-5678-9012"));

        verify(pdfManagementService).generateAndUpload(any(SentEmail.class), eq(SENT_EMAIL));
    }

    @Test
    public void sendCaseStoppedEmailToPersonalApplicantFromBirmingham()
            throws NotificationClientException, BadRequestException {

        registry.setEmailReplyToId("birmingham-emailReplyToId");
        HashMap<String, Registry> map = new HashMap<>();
        HashMap<String, Registry> spyMap = spy(map);
        spyMap.put("birmingham", registry);
        when(registriesProperties.getRegistries()).thenReturn(spyMap);
        when(spyMap.get(anyString())).thenReturn(registry);

        notificationService.sendEmail(CASE_STOPPED, personalCaseDataBirmingham);

        verify(notificationClient).sendEmail(
                eq("pa-case-stopped"),
                eq("personal@test.com"),
                any(),
                isNull(),
                eq("birmingham-emailReplyToId"));

        verify(pdfManagementService).generateAndUpload(any(SentEmail.class), eq(SENT_EMAIL));
    }

    @Test
    public void sendCaseStoppedEmailToSolicitorFromBirmingham()
            throws NotificationClientException, BadRequestException {

        registry.setEmailReplyToId("birmingham-emailReplyToId");
        HashMap<String, Registry> map = new HashMap<>();
        HashMap<String, Registry> spyMap = spy(map);
        spyMap.put("birmingham", registry);
        when(registriesProperties.getRegistries()).thenReturn(spyMap);
        when(spyMap.get(anyString())).thenReturn(registry);

        notificationService.sendEmail(CASE_STOPPED, solicitorCaseDataBirmingham);

        verify(notificationClient).sendEmail(
                eq("sol-case-stopped"),
                eq("solicitor@test.com"),
                any(),
                eq("1234-5678-9012"),
                eq("birmingham-emailReplyToId"));

        verify(pdfManagementService).generateAndUpload(any(SentEmail.class), eq(SENT_EMAIL));
    }

    @Test
    public void sendCaseStoppedEmailToPersonalApplicantFromOxford()
            throws NotificationClientException, BadRequestException {

        registry.setEmailReplyToId("oxford-emailReplyToId");
        HashMap<String, Registry> map = new HashMap<>();
        HashMap<String, Registry> spyMap = spy(map);
        spyMap.put("oxford", registry);
        when(registriesProperties.getRegistries()).thenReturn(spyMap);
        when(spyMap.get(anyString())).thenReturn(registry);

        notificationService.sendEmail(CASE_STOPPED, personalCaseDataOxford);

        verify(notificationClient).sendEmail(
                eq("pa-case-stopped"),
                eq("personal@test.com"),
                any(),
                isNull(),
                eq("oxford-emailReplyToId"));

        verify(pdfManagementService).generateAndUpload(any(SentEmail.class), eq(SENT_EMAIL));
    }

    @Test
    public void sendCaseStoppedEmailToSolicitorFromOxford()
            throws NotificationClientException, BadRequestException {

        registry.setEmailReplyToId("oxford-emailReplyToId");
        HashMap<String, Registry> map = new HashMap<>();
        HashMap<String, Registry> spyMap = spy(map);
        spyMap.put("oxford", registry);
        when(registriesProperties.getRegistries()).thenReturn(spyMap);
        when(spyMap.get(anyString())).thenReturn(registry);

        notificationService.sendEmail(CASE_STOPPED, solicitorCaseDataOxford);

        verify(notificationClient).sendEmail(
                eq("sol-case-stopped"),
                eq("solicitor@test.com"),
                any(),
                eq("1234-5678-9012"),
                eq("oxford-emailReplyToId"));

        verify(pdfManagementService).generateAndUpload(any(SentEmail.class), eq(SENT_EMAIL));
    }

    @Test
    public void sendCaseStoppedEmailToPersonalApplicantFromManchester()
            throws NotificationClientException, BadRequestException {

        registry.setEmailReplyToId("manchester-emailReplyToId");
        HashMap<String, Registry> map = new HashMap<>();
        HashMap<String, Registry> spyMap = spy(map);
        spyMap.put("manchester", registry);
        when(registriesProperties.getRegistries()).thenReturn(spyMap);
        when(spyMap.get(anyString())).thenReturn(registry);

        notificationService.sendEmail(CASE_STOPPED, personalCaseDataManchester);

        verify(notificationClient).sendEmail(
                eq("pa-case-stopped"),
                eq("personal@test.com"),
                any(),
                isNull(),
                eq("manchester-emailReplyToId"));

        verify(pdfManagementService).generateAndUpload(any(SentEmail.class), eq(SENT_EMAIL));
    }

    @Test
    public void sendCaseStoppedEmailToSolicitorFromManchester()
            throws NotificationClientException, BadRequestException {
        registry.setEmailReplyToId("manchester-emailReplyToId");
        HashMap<String, Registry> map = new HashMap<>();
        HashMap<String, Registry> spyMap = spy(map);
        spyMap.put("manchester", registry);
        when(registriesProperties.getRegistries()).thenReturn(spyMap);
        when(spyMap.get(anyString())).thenReturn(registry);

        notificationService.sendEmail(CASE_STOPPED, solicitorCaseDataManchester);

        verify(notificationClient).sendEmail(
                eq("sol-case-stopped"),
                eq("solicitor@test.com"),
                any(),
                eq("1234-5678-9012"),
                eq("manchester-emailReplyToId"));

        verify(pdfManagementService).generateAndUpload(any(SentEmail.class), eq(SENT_EMAIL));
    }

    @Test
    public void sendGeneralCaveatEmailToPersonalApplicantFromOxford()
            throws NotificationClientException, BadRequestException {

        registry.setEmailReplyToId("oxford-emailReplyToId");
        HashMap<String, Registry> map = new HashMap<>();
        HashMap<String, Registry> spyMap = spy(map);
        spyMap.put("oxford", registry);
        when(registriesProperties.getRegistries()).thenReturn(spyMap);
        when(spyMap.get(anyString())).thenReturn(registry);

        notificationService.sendCaveatEmail(GENERAL_CAVEAT_MESSAGE, personalCaveatDataOxford);

        verify(notificationClient).sendEmail(
                eq("pa-general-caveat-message"),
                eq("personal@test.com"),
                any(),
                anyString(),
                eq("oxford-emailReplyToId"));

        verify(pdfManagementService).generateAndUpload(any(SentEmail.class), eq(SENT_EMAIL));
    }

    @Test
    public void sendGeneralCaveatEmailToPersonalApplicantFromBirmingham()
            throws NotificationClientException, BadRequestException {

        registry.setEmailReplyToId("birmingham-emailReplyToId");
        HashMap<String, Registry> map = new HashMap<>();
        HashMap<String, Registry> spyMap = spy(map);
        spyMap.put("birmingham", registry);
        when(registriesProperties.getRegistries()).thenReturn(spyMap);
        when(spyMap.get(anyString())).thenReturn(registry);

        notificationService.sendCaveatEmail(GENERAL_CAVEAT_MESSAGE, personalCaveatDataBirmingham);

        verify(notificationClient).sendEmail(
                eq("pa-general-caveat-message"),
                eq("personal@test.com"),
                any(),
                anyString(),
                eq("birmingham-emailReplyToId"));

        verify(pdfManagementService).generateAndUpload(any(SentEmail.class), eq(SENT_EMAIL));
    }

    @Test
    public void sendGeneralCaveatEmailToPersonalApplicantFromManchester()
            throws NotificationClientException, BadRequestException {

        registry.setEmailReplyToId("manchester-emailReplyToId");
        HashMap<String, Registry> map = new HashMap<>();
        HashMap<String, Registry> spyMap = spy(map);
        spyMap.put("manchester", registry);
        when(registriesProperties.getRegistries()).thenReturn(spyMap);
        when(spyMap.get(anyString())).thenReturn(registry);

        notificationService.sendCaveatEmail(GENERAL_CAVEAT_MESSAGE, personalCaveatDataManchester);

        verify(notificationClient).sendEmail(
                eq("pa-general-caveat-message"),
                eq("personal@test.com"),
                any(),
                anyString(),
                eq("manchester-emailReplyToId"));

        verify(pdfManagementService).generateAndUpload(any(SentEmail.class), eq(SENT_EMAIL));
    }

    @Test
    public void sendGeneralCaveatEmailToPersonalApplicantFromLeeds()
            throws NotificationClientException, BadRequestException {

        registry.setEmailReplyToId("leeds-emailReplyToId");
        HashMap<String, Registry> map = new HashMap<>();
        HashMap<String, Registry> spyMap = spy(map);
        spyMap.put("leads", registry);
        when(registriesProperties.getRegistries()).thenReturn(spyMap);
        when(spyMap.get(anyString())).thenReturn(registry);

        notificationService.sendCaveatEmail(GENERAL_CAVEAT_MESSAGE, personalCaveatDataLeeds);

        verify(notificationClient).sendEmail(
                eq("pa-general-caveat-message"),
                eq("personal@test.com"),
                any(),
                anyString(),
                eq("leeds-emailReplyToId"));

        verify(pdfManagementService).generateAndUpload(any(SentEmail.class), eq(SENT_EMAIL));
    }
}
