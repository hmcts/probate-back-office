package uk.gov.hmcts.probate.service;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.probate.config.properties.registries.RegistriesProperties;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.insights.AppInsights;
import uk.gov.hmcts.probate.model.SentEmail;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.SendEmailResponse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.ApplicationType.PERSONAL;
import static uk.gov.hmcts.probate.model.ApplicationType.SOLICITOR;
import static uk.gov.hmcts.probate.model.DocumentType.SENT_EMAIL;
import static uk.gov.hmcts.probate.model.State.CASE_STOPPED;
import static uk.gov.hmcts.probate.model.State.CAVEAT_RAISED;
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
    private CoreCaseDataApi coreCaseDataApi;

    @SpyBean
    private NotificationClient notificationClient;

    private CaseDetails personalCaseDataOxford;
    private CaseDetails solicitorCaseDataOxford;
    private CaseDetails personalCaseDataBirmingham;
    private CaseDetails solicitorCaseDataBirmingham;
    private CaseDetails personalCaseDataManchester;
    private CaseDetails personalCaseDataCtsc;
    private CaseDetails personalCaseDataBristol;
    private CaseDetails solicitorCaseDataManchester;
    private ImmutableList.Builder<ReturnedCaseDetails> excelaCaseData = new ImmutableList.Builder<>();

    private CaveatDetails personalCaveatDataOxford;
    private CaveatDetails personalCaveatDataBirmingham;
    private CaveatDetails personalCaveatDataManchester;
    private CaveatDetails personalCaveatDataLeeds;
    private CaveatDetails personalCaveatDataLiverpool;
    private CaveatDetails personalCaveatDataBrighton;
    private CaveatDetails personalCaveatDataLondon;
    private CaveatDetails personalCaveatDataCardiff;
    private CaveatDetails personalCaveatDataNewcastle;
    private CaveatDetails personalCaveatDataWinchester;
    private CaveatDetails personalCaveatDataBristol;
    private CaveatDetails caveatRaisedCaseData;

    @Mock
    private RegistriesProperties registriesPropertiesMock;

    private static final Long ID = 1L;
    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};

    private static final String PERSONALISATION_APPLICANT_NAME = "applicant_name";
    private static final String PERSONALISATION_DECEASED_NAME = "deceased_name";
    private static final String PERSONALISATION_SOLICITOR_NAME = "solicitor_name";
    private static final String PERSONALISATION_SOLICITOR_REFERENCE = "solicitor_reference";
    private static final String PERSONALISATION_REGISTRY_NAME = "registry_name";
    private static final String PERSONALISATION_REGISTRY_PHONE = "registry_phone";
    private static final String PERSONALISATION_CASE_STOP_DETAILS = "case-stop-details";
    private static final String PERSONALISATION_DECEASED_DOD = "deceased_dod";
    private static final String PERSONALISATION_CCD_REFERENCE = "ccd_reference";
    private static final String PERSONALISATION_MESSAGE_CONTENT = "message_content";
    private static final String PERSONALISATION_EXCELA_NAME = "excelaName";
    private static final String PERSONALISATION_CASE_DATA = "caseData";

    @Before
    public void setUp() throws NotificationClientException {
        when(sendEmailResponse.getFromEmail()).thenReturn(Optional.of("test@test.com"));
        when(sendEmailResponse.getBody()).thenReturn("test-body");

        doReturn(sendEmailResponse).when(notificationClient).sendEmail(anyString(), anyString(), any(), isNull());
        doReturn(sendEmailResponse).when(notificationClient).sendEmail(any(), any(), any(), any(), any());

        CollectionMember<ScannedDocument> scannedDocument = new CollectionMember<>(ScannedDocument
                .builder().subtype("will").controlNumber("123456").build());
        List<CollectionMember<ScannedDocument>> scannedDocuments = new ArrayList<>(1);
        scannedDocuments.add(scannedDocument);

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

        personalCaseDataCtsc = new CaseDetails(CaseData.builder()
                .applicationType(PERSONAL)
                .registryLocation("ctsc")
                .primaryApplicantEmailAddress("personal@test.com")
                .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
                .build(), LAST_MODIFIED, ID);

        personalCaseDataBristol = new CaseDetails(CaseData.builder()
                .applicationType(PERSONAL)
                .registryLocation("Bristol")
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

        excelaCaseData.add(
                new ReturnedCaseDetails(CaseData.builder()
                .applicationType(PERSONAL)
                .deceasedSurname("Michelson")
                .scannedDocuments(scannedDocuments)
                .build(), LAST_MODIFIED, ID));

        caveatRaisedCaseData = new CaveatDetails(CaveatData.builder()
                .applicationType(PERSONAL)
                .registryLocation("Oxford")
                .caveatorEmailAddress("personal@test.com")
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

        personalCaveatDataLiverpool = new CaveatDetails(CaveatData.builder()
                .applicationType(PERSONAL)
                .registryLocation("Liverpool")
                .caveatorEmailAddress("personal@test.com")
                .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
                .build(), LAST_MODIFIED, ID);

        personalCaveatDataBrighton = new CaveatDetails(CaveatData.builder()
                .applicationType(PERSONAL)
                .registryLocation("Brighton")
                .caveatorEmailAddress("personal@test.com")
                .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
                .build(), LAST_MODIFIED, ID);

        personalCaveatDataLondon = new CaveatDetails(CaveatData.builder()
                .applicationType(PERSONAL)
                .registryLocation("London")
                .caveatorEmailAddress("personal@test.com")
                .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
                .build(), LAST_MODIFIED, ID);

        personalCaveatDataCardiff = new CaveatDetails(CaveatData.builder()
                .applicationType(PERSONAL)
                .registryLocation("Cardiff")
                .caveatorEmailAddress("personal@test.com")
                .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
                .build(), LAST_MODIFIED, ID);

        personalCaveatDataNewcastle = new CaveatDetails(CaveatData.builder()
                .applicationType(PERSONAL)
                .registryLocation("Newcastle")
                .caveatorEmailAddress("personal@test.com")
                .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
                .build(), LAST_MODIFIED, ID);

        personalCaveatDataWinchester = new CaveatDetails(CaveatData.builder()
                .applicationType(PERSONAL)
                .registryLocation("Winchester")
                .caveatorEmailAddress("personal@test.com")
                .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
                .build(), LAST_MODIFIED, ID);

        personalCaveatDataBristol = new CaveatDetails(CaveatData.builder()
                .applicationType(PERSONAL)
                .registryLocation("Bristol")
                .caveatorEmailAddress("personal@test.com")
                .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
                .build(), LAST_MODIFIED, ID);

    }

    @Test
    public void sendDocumentsReceivedEmailToPersonalApplicantFromBirmingham()
            throws NotificationClientException, BadRequestException {

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
    public void sendCaseStoppedEmailToPersonalApplicantFromCtsc()
            throws NotificationClientException, BadRequestException {

        notificationService.sendEmail(CASE_STOPPED, personalCaseDataCtsc);

        verify(notificationClient).sendEmail(
                eq("pa-case-stopped"),
                eq("personal@test.com"),
                any(),
                isNull(),
                eq("ctsc-emailReplyToId"));

        verify(pdfManagementService).generateAndUpload(any(SentEmail.class), eq(SENT_EMAIL));
    }

    @Test
    public void sendCaseStoppedEmailToPersonalApplicantFromBristol()
            throws NotificationClientException, BadRequestException {

        notificationService.sendEmail(CASE_STOPPED, personalCaseDataBristol);

        verify(notificationClient).sendEmail(
                eq("pa-case-stopped"),
                eq("personal@test.com"),
                any(),
                isNull(),
                eq("bristol-emailReplyToId"));

        verify(pdfManagementService).generateAndUpload(any(SentEmail.class), eq(SENT_EMAIL));
    }

    @Test
    public void sendGeneralCaveatEmailToPersonalApplicantFromOxford()
            throws NotificationClientException, BadRequestException {

        notificationService.sendCaveatEmail(GENERAL_CAVEAT_MESSAGE, personalCaveatDataOxford);

        verify(notificationClient).sendEmail(
                eq("pa-general-caveat-message"),
                eq("personal@test.com"),
                any(),
                anyString());

        verify(pdfManagementService).generateAndUpload(any(SentEmail.class), eq(SENT_EMAIL));
    }

    @Test
    public void sendGeneralCaveatEmailToPersonalApplicantFromBirmingham()
            throws NotificationClientException, BadRequestException {

        notificationService.sendCaveatEmail(GENERAL_CAVEAT_MESSAGE, personalCaveatDataBirmingham);

        verify(notificationClient).sendEmail(
                eq("pa-general-caveat-message"),
                eq("personal@test.com"),
                any(),
                anyString());

        verify(pdfManagementService).generateAndUpload(any(SentEmail.class), eq(SENT_EMAIL));
    }

    @Test
    public void sendGeneralCaveatEmailToPersonalApplicantFromManchester()
            throws NotificationClientException, BadRequestException {

        notificationService.sendCaveatEmail(GENERAL_CAVEAT_MESSAGE, personalCaveatDataManchester);

        verify(notificationClient).sendEmail(
                eq("pa-general-caveat-message"),
                eq("personal@test.com"),
                any(),
                anyString());

        verify(pdfManagementService).generateAndUpload(any(SentEmail.class), eq(SENT_EMAIL));
    }

    @Test
    public void sendGeneralCaveatEmailToPersonalApplicantFromLeeds()
            throws NotificationClientException, BadRequestException {

        notificationService.sendCaveatEmail(GENERAL_CAVEAT_MESSAGE, personalCaveatDataLeeds);

        verify(notificationClient).sendEmail(
                eq("pa-general-caveat-message"),
                eq("personal@test.com"),
                any(),
                anyString());

        verify(pdfManagementService).generateAndUpload(any(SentEmail.class), eq(SENT_EMAIL));
    }

    @Test
    public void sendGeneralCaveatEmailToPersonalApplicantFromLiverpool()
            throws NotificationClientException, BadRequestException {

        notificationService.sendCaveatEmail(GENERAL_CAVEAT_MESSAGE, personalCaveatDataLiverpool);

        verify(notificationClient).sendEmail(
                eq("pa-general-caveat-message"),
                eq("personal@test.com"),
                any(),
                anyString());

        verify(pdfManagementService).generateAndUpload(any(SentEmail.class), eq(SENT_EMAIL));
    }

    @Test
    public void sendGeneralCaveatEmailToPersonalApplicantFromBrighton()
            throws NotificationClientException, BadRequestException {

        notificationService.sendCaveatEmail(GENERAL_CAVEAT_MESSAGE, personalCaveatDataBrighton);

        verify(notificationClient).sendEmail(
                eq("pa-general-caveat-message"),
                eq("personal@test.com"),
                any(),
                anyString());

        verify(pdfManagementService).generateAndUpload(any(SentEmail.class), eq(SENT_EMAIL));
    }

    @Test
    public void sendGeneralCaveatEmailToPersonalApplicantFromBristol()
            throws NotificationClientException, BadRequestException {

        notificationService.sendCaveatEmail(GENERAL_CAVEAT_MESSAGE, personalCaveatDataBristol);

        verify(notificationClient).sendEmail(
                eq("pa-general-caveat-message"),
                eq("personal@test.com"),
                any(),
                anyString());

        verify(pdfManagementService).generateAndUpload(any(SentEmail.class), eq(SENT_EMAIL));
    }

    @Test
    public void sendGeneralCaveatEmailToPersonalApplicantFromLondon()
            throws NotificationClientException, BadRequestException {

        notificationService.sendCaveatEmail(GENERAL_CAVEAT_MESSAGE, personalCaveatDataLondon);

        verify(notificationClient).sendEmail(
                eq("pa-general-caveat-message"),
                eq("personal@test.com"),
                any(),
                anyString());

        verify(pdfManagementService).generateAndUpload(any(SentEmail.class), eq(SENT_EMAIL));
    }

    @Test
    public void sendGeneralCaveatEmailToPersonalApplicantFromCardiff()
            throws NotificationClientException, BadRequestException {

        notificationService.sendCaveatEmail(GENERAL_CAVEAT_MESSAGE, personalCaveatDataCardiff);

        verify(notificationClient).sendEmail(
                eq("pa-general-caveat-message"),
                eq("personal@test.com"),
                any(),
                anyString());

        verify(pdfManagementService).generateAndUpload(any(SentEmail.class), eq(SENT_EMAIL));
    }

    @Test
    public void sendGeneralCaveatEmailToPersonalApplicantFromNewcastle()
            throws NotificationClientException, BadRequestException {

        notificationService.sendCaveatEmail(GENERAL_CAVEAT_MESSAGE, personalCaveatDataNewcastle);

        verify(notificationClient).sendEmail(
                eq("pa-general-caveat-message"),
                eq("personal@test.com"),
                any(),
                anyString());

        verify(pdfManagementService).generateAndUpload(any(SentEmail.class), eq(SENT_EMAIL));
    }

    @Test
    public void sendGeneralCaveatEmailToPersonalApplicantFromWinchester()
            throws NotificationClientException, BadRequestException {

        notificationService.sendCaveatEmail(GENERAL_CAVEAT_MESSAGE, personalCaveatDataWinchester);

        verify(notificationClient).sendEmail(
                eq("pa-general-caveat-message"),
                eq("personal@test.com"),
                any(),
                anyString());

        verify(pdfManagementService).generateAndUpload(any(SentEmail.class), eq(SENT_EMAIL));
    }

    @Test
    public void sendGeneralCaveatRaisedEmail()
            throws NotificationClientException, BadRequestException {

        HashMap<String, String> personalisation = new HashMap<>();

        personalisation.put(PERSONALISATION_APPLICANT_NAME, caveatRaisedCaseData.getData().getCaveatorFullName());
        personalisation.put(PERSONALISATION_DECEASED_NAME, caveatRaisedCaseData.getData().getDeceasedFullName());
        personalisation.put(PERSONALISATION_CCD_REFERENCE, caveatRaisedCaseData.getId().toString());
        personalisation.put(PERSONALISATION_MESSAGE_CONTENT, caveatRaisedCaseData.getData().getMessageContent());
        personalisation.put(PERSONALISATION_REGISTRY_NAME, "Oxford Probate Registry");
        personalisation.put(PERSONALISATION_REGISTRY_PHONE, "0186 579 3055");

        notificationService.sendCaveatEmail(CAVEAT_RAISED, caveatRaisedCaseData);

        verify(notificationClient).sendEmail(
                eq("pa-caveat-raised"),
                eq("personal@test.com"),
                eq(personalisation),
                eq("1"));

        verify(pdfManagementService).generateAndUpload(any(SentEmail.class), eq(SENT_EMAIL));
    }

    @Test
    public void sendExcelaEmail() throws NotificationClientException {
        notificationService.sendExcelaEmail(excelaCaseData.build());

        verify(notificationClient).sendEmail(
                eq("pa-excela-data"),
                eq("probatetest@gmail.com"),
                any(),
                anyString());

        verify(pdfManagementService).generateAndUpload(any(SentEmail.class), eq(SENT_EMAIL));
    }
}
