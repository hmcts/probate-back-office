package uk.gov.hmcts.probate.service;

import com.google.common.collect.ImmutableList;
import org.assertj.core.api.Assertions;
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
import uk.gov.hmcts.probate.exception.InvalidEmailException;
import uk.gov.hmcts.probate.insights.AppInsights;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.CaseType;
import uk.gov.hmcts.probate.model.SentEmail;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.ProbateAddress;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.raw.BulkPrint;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.probate.validator.EmailAddressNotificationValidationRule;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.SendEmailResponse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
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
import static uk.gov.hmcts.probate.model.State.CASE_STOPPED_CAVEAT;
import static uk.gov.hmcts.probate.model.State.CASE_STOPPED_REQUEST_INFORMATION;
import static uk.gov.hmcts.probate.model.State.CAVEAT_RAISED;
import static uk.gov.hmcts.probate.model.State.DOCUMENTS_RECEIVED;
import static uk.gov.hmcts.probate.model.State.GENERAL_CAVEAT_MESSAGE;
import static uk.gov.hmcts.probate.model.State.GRANT_ISSUED;
import static uk.gov.hmcts.probate.model.State.GRANT_REISSUED;

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

    @MockBean
    private CaveatQueryService caveatQueryServiceMock;

    @Mock
    private EventValidationService eventValidationService;

    @Mock
    private List<EmailAddressNotificationValidationRule> emailAddressNotificationValidationRules;

    @Mock
    CallbackResponse callbackResponse;

    @Mock
    private DateFormatterService dateFormatterService;

    @SpyBean
    private NotificationClient notificationClient;

    private CaseDetails personalCaseDataOxford;
    private CaseDetails personalCaseDataOxfordInvalidAppType;
    private CaseDetails solicitorCaseDataOxford;
    private CaseDetails personalCaseDataBirmingham;
    private CaseDetails solicitorCaseDataBirmingham;
    private CaseDetails personalCaseDataManchester;
    private CaseDetails personalCaseDataCtsc;
    private CaseDetails solsCaseDataCtsc;
    private CaseDetails personalCaseDataCtscRequestInformation;
    private CaseDetails personalCaseDataBristol;
    private CaseDetails solsCaseDataCtscRequestInformation;
    private CaseDetails solicitorCaseDataManchester;
    private ImmutableList.Builder<ReturnedCaseDetails> excelaCaseData = new ImmutableList.Builder<>();
    private ImmutableList.Builder<ReturnedCaseDetails> excelaCaseDataNoWillReference = new ImmutableList.Builder<>();
    private ImmutableList.Builder<ReturnedCaseDetails> excelaCaseDataNoSubtype = new ImmutableList.Builder<>();

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
    private CaveatDetails caveatRaisedCtscCaseData;
    private CaveatData caveatData;
    private CallbackRequest callbackRequest;
    private CaveatDetails caveatStoppedCtscCaseData;

    @Mock
    private RegistriesProperties registriesPropertiesMock;

    private static final Long ID = 1L;
    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private static final Long CASE_ID = 12345678987654321L;
    private static final String SENT_EMAIL_FILE_NAME = "sentEmail.pdf";

    private static final String PERSONALISATION_APPLICANT_NAME = "applicant_name";
    private static final String PERSONALISATION_DECEASED_NAME = "deceased_name";
    private static final String PERSONALISATION_SOLICITOR_NAME = "solicitor_name";
    private static final String PERSONALISATION_SOLICITOR_REFERENCE = "solicitor_reference";
    private static final String PERSONALISATION_REGISTRY_NAME = "registry_name";
    private static final String PERSONALISATION_REGISTRY_PHONE = "registry_phone";
    private static final String PERSONALISATION_CASE_STOP_DETAILS = "case-stop-details";
    private static final String PERSONALISATION_CAVEAT_CASE_ID = "caveat_case_id";
    private static final String PERSONALISATION_DECEASED_DOD = "deceased_dod";
    private static final String PERSONALISATION_CCD_REFERENCE = "ccd_reference";
    private static final String PERSONALISATION_MESSAGE_CONTENT = "message_content";
    private static final String PERSONALISATION_EXCELA_NAME = "excelaName";
    private static final String PERSONALISATION_CASE_DATA = "caseData";
    private static final String PERSONALISATION_CAVEAT_EXPIRY_DATE = "caveat_expiry_date";
    private static final String PERSONALISATION_CAVEAT_ENTERED = "date_caveat_entered";
    private static final String PERSONALISATION_CAVEATOR_NAME = "caveator_name";
    private static final String PERSONALISATION_CAVEATOR_ADDRESS = "caveator_address";
    private static final String PERSONALISATION_CASE_STOP_DETAILS_DEC = "boStopDetailsDeclarationParagraph";


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

        CollectionMember<ScannedDocument> scannedDocumentsNoWillReference = new CollectionMember<>(ScannedDocument
                .builder().subtype("subtype").build());
        List<CollectionMember<ScannedDocument>> scannedDocumentsNoWill = new ArrayList<>(1);
        scannedDocumentsNoWill.add(scannedDocumentsNoWillReference);

        CollectionMember<ScannedDocument> scannedDocumensNoSubtype = new CollectionMember<>(ScannedDocument
                .builder().subtype(null).build());
        List<CollectionMember<ScannedDocument>> scannedDocumentsNoSubtype = new ArrayList<>(1);
        scannedDocumentsNoSubtype.add(scannedDocumensNoSubtype);

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

        solsCaseDataCtsc = new CaseDetails(CaseData.builder()
                .applicationType(SOLICITOR)
                .solsSOTName("fred smith")
                .registryLocation("ctsc")
                .solsSolicitorEmail("sols@test.com")
                .primaryApplicantEmailAddress("personal@test.com")
                .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
                .build(), LAST_MODIFIED, ID);

        personalCaseDataCtscRequestInformation = new CaseDetails(CaseData.builder()
                .applicationType(PERSONAL)
                .boStopDetailsDeclarationParagraph("Yes")
                .deceasedDateOfDeath(LocalDate.now())
                .registryLocation("ctsc")
                .primaryApplicantEmailAddress("personal@test.com")
                .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
                .build(), LAST_MODIFIED, ID);

        solsCaseDataCtscRequestInformation = new CaseDetails(CaseData.builder()
                .applicationType(SOLICITOR)
                .solsSOTName("fred smith")
                .boStopDetailsDeclarationParagraph("Yes")
                .deceasedDateOfDeath(LocalDate.now())
                .registryLocation("ctsc")
                .solsSolicitorEmail("sols@test.com")
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

        excelaCaseData.add(new ReturnedCaseDetails(CaseData.builder()
                .applicationType(PERSONAL)
                .deceasedForenames("Jack")
                .deceasedSurname("Michelson")
                .grantIssuedDate("2019-05-01")
                .deceasedDateOfBirth(LocalDate.of(2019, 1, 1))
                .scannedDocuments(scannedDocuments)
                .build(), LAST_MODIFIED, ID));

        excelaCaseDataNoWillReference.add(new ReturnedCaseDetails(CaseData.builder()
                .applicationType(PERSONAL)
                .deceasedForenames("Jack")
                .deceasedSurname("Michelson")
                .grantIssuedDate("2019-05-01")
                .deceasedDateOfBirth(LocalDate.of(2019, 1, 1))
                .scannedDocuments(scannedDocumentsNoWill)
                .build(), LAST_MODIFIED, ID));

        excelaCaseDataNoSubtype.add(new ReturnedCaseDetails(CaseData.builder()
                .applicationType(PERSONAL)
                .deceasedForenames("Jack")
                .deceasedSurname("Michelson")
                .grantIssuedDate("2019-05-01")
                .deceasedDateOfBirth(LocalDate.of(2019, 1, 1))
                .scannedDocuments(scannedDocumentsNoSubtype)
                .build(), LAST_MODIFIED, ID));

        caveatRaisedCaseData = new CaveatDetails(CaveatData.builder()
                .applicationType(PERSONAL)
                .registryLocation("Oxford")
                .caveatorEmailAddress("personal@test.com")
                .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
                .expiryDate(LocalDate.of(2019, 01, 01))
                .build(), LAST_MODIFIED, ID);

        caveatRaisedCtscCaseData = new CaveatDetails(CaveatData.builder()
                .applicationType(PERSONAL)
                .registryLocation("ctsc")
                .caveatorEmailAddress("personal@test.com")
                .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
                .expiryDate(LocalDate.of(2019, 01, 01))
                .build(), LAST_MODIFIED, ID);

        caveatStoppedCtscCaseData = new CaveatDetails(CaveatData.builder()
                .applicationSubmittedDate(LocalDate.of(2019, 01, 01))
                .applicationType(PERSONAL)
                .registryLocation("ctsc")
                .caveatorEmailAddress("personal@test.com")
                .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
                .expiryDate(LocalDate.of(2019, 01, 01))
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

        CollectionMember<CaseMatch> caseMatchMember = new CollectionMember<>(CaseMatch.builder().build());
        List<CollectionMember<CaseMatch>> caseMatch = new ArrayList<>();
        caseMatch.add(caseMatchMember);

        CollectionMember<Document> documentMember = new CollectionMember<>(Document.builder().build());
        List<CollectionMember<Document>> notificationGenerated = new ArrayList<>();
        notificationGenerated.add(documentMember);

        CollectionMember<BulkPrint> bulkPrintMember = new CollectionMember<>(BulkPrint.builder().build());
        List<CollectionMember<BulkPrint>> bulkPrintId = new ArrayList<>();
        bulkPrintId.add(bulkPrintMember);

        List<CollectionMember<Document>> documentsGenerated = new ArrayList<>();
        documentsGenerated.add(documentMember);

        caveatData = CaveatData.builder()
                .registryLocation("leeds")
                .applicationSubmittedDate(LocalDate.now())
                .caveatorForenames("fred")
                .caveatorSurname("jones")
                .caseMatches(caseMatch)
                .notificationsGenerated(notificationGenerated)
                .bulkPrintId(bulkPrintId)
                .documentsGenerated(documentsGenerated)
                .caveatorAddress(ProbateAddress.builder().proAddressLine1("addressLine1").build())
                .build();

        when(caveatQueryServiceMock.findCaveatById(eq(CaseType.CAVEAT), any())).thenReturn(caveatData);

        when(dateFormatterService.formatCaveatExpiryDate(any())).thenReturn("1st January 2019");
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
    public void sendGrantReissuedEmailToPersonalApplicantFromBirmingham()
            throws NotificationClientException, BadRequestException {

        notificationService.sendEmail(GRANT_REISSUED, personalCaseDataBirmingham);

        verify(notificationClient).sendEmail(
                eq("pa-grant-reissued"),
                eq("personal@test.com"),
                any(),
                isNull());

        verify(pdfManagementService).generateAndUpload(any(SentEmail.class), eq(SENT_EMAIL));
    }

    @Test
    public void sendGrantReissuedEmailToSolicitorFromBirmingham()
            throws NotificationClientException, BadRequestException {

        notificationService.sendEmail(GRANT_REISSUED, solicitorCaseDataBirmingham);

        verify(notificationClient).sendEmail(
                eq("sol-grant-reissued"),
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
        personalisation.put(PERSONALISATION_CAVEAT_EXPIRY_DATE, "1st January 2019");

        notificationService.sendCaveatEmail(CAVEAT_RAISED, caveatRaisedCaseData);

        verify(notificationClient).sendEmail(
                eq("pa-caveat-raised"),
                eq("personal@test.com"),
                eq(personalisation),
                eq("1"));

        verify(pdfManagementService).generateAndUpload(any(SentEmail.class), eq(SENT_EMAIL));
    }

    @Test
    public void sendGeneralCaveatRaisedCtscEmail()
            throws NotificationClientException, BadRequestException {

        HashMap<String, String> personalisation = new HashMap<>();

        personalisation.put(PERSONALISATION_APPLICANT_NAME, caveatRaisedCtscCaseData.getData().getCaveatorFullName());
        personalisation.put(PERSONALISATION_DECEASED_NAME, caveatRaisedCtscCaseData.getData().getDeceasedFullName());
        personalisation.put(PERSONALISATION_CCD_REFERENCE, caveatRaisedCtscCaseData.getId().toString());
        personalisation.put(PERSONALISATION_MESSAGE_CONTENT, caveatRaisedCtscCaseData.getData().getMessageContent());
        personalisation.put(PERSONALISATION_REGISTRY_NAME, "CTSC");
        personalisation.put(PERSONALISATION_REGISTRY_PHONE, "0300 303 0648");
        personalisation.put(PERSONALISATION_CAVEAT_EXPIRY_DATE, "1st January 2019");

        notificationService.sendCaveatEmail(CAVEAT_RAISED, caveatRaisedCtscCaseData);

        verify(notificationClient).sendEmail(
                eq("pa-ctsc-caveat-raised"),
                eq("personal@test.com"),
                eq(personalisation),
                eq("1"));

        verify(pdfManagementService).generateAndUpload(any(SentEmail.class), eq(SENT_EMAIL));
    }

    @Test
    public void sendGeneralCaveatRaisedCtscEmailThrowsUnsupportedState()
            throws BadRequestException {

        Assertions.assertThatThrownBy(() -> {notificationService.sendCaveatEmail(DOCUMENTS_RECEIVED, caveatRaisedCtscCaseData);})
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Unsupported State");
    }

    @Test
    public void shouldSendEmailForCaveatStoppedOnCtsc()
            throws NotificationClientException, BadRequestException {

        HashMap<String, String> personalisation = new HashMap<>();

        personalisation.put(PERSONALISATION_APPLICANT_NAME, personalCaseDataCtsc.getData().getPrimaryApplicantFullName());
        personalisation.put(PERSONALISATION_DECEASED_NAME, personalCaseDataCtsc.getData().getDeceasedFullName());
        personalisation.put(PERSONALISATION_SOLICITOR_NAME, personalCaseDataCtsc.getData().getSolsSOTName());
        personalisation.put(PERSONALISATION_SOLICITOR_REFERENCE, personalCaseDataCtsc.getData().getSolsSolicitorAppReference());
        personalisation.put(PERSONALISATION_REGISTRY_NAME, "CTSC");
        personalisation.put(PERSONALISATION_REGISTRY_PHONE, "0300 303 0648");
        personalisation.put(PERSONALISATION_CASE_STOP_DETAILS_DEC, personalCaseDataCtsc.getData().getBoStopDetailsDeclarationParagraph());
        personalisation.put(PERSONALISATION_CASE_STOP_DETAILS, personalCaseDataCtsc.getData().getBoStopDetails());
        personalisation.put(PERSONALISATION_CAVEAT_CASE_ID, personalCaseDataCtsc.getData().getBoCaseStopCaveatId());
        personalisation.put(PERSONALISATION_DECEASED_DOD, personalCaseDataCtsc.getData().getDeceasedDateOfDeathFormatted());
        personalisation.put(PERSONALISATION_CCD_REFERENCE, personalCaseDataCtsc.getId().toString());

        personalisation.put(PERSONALISATION_CAVEATOR_NAME, caveatStoppedCtscCaseData.getData().getCaveatorFullName());
        personalisation.put(PERSONALISATION_CAVEAT_ENTERED, "1st January 2019");
        personalisation.put(PERSONALISATION_CAVEATOR_ADDRESS, "");
        personalisation.put(PERSONALISATION_CAVEAT_EXPIRY_DATE, "1st January 2019");

        when(caveatQueryServiceMock.findCaveatById(CaseType.CAVEAT, null)).thenReturn(caveatStoppedCtscCaseData.getData());
        when(notificationClient.sendEmail(anyString(), anyString(), any(), any(), any())).thenReturn(sendEmailResponse);

        notificationService.sendEmail(CASE_STOPPED_CAVEAT, personalCaseDataCtsc);

        verify(notificationClient).sendEmail(
                eq("pa-case-stopped-caveat"),
                eq("personal@test.com"),
                eq(personalisation),
                eq(null),
                eq("ctsc-emailReplyToId"));

        verify(pdfManagementService).generateAndUpload(any(SentEmail.class), eq(SENT_EMAIL));
    }

    @Test
    public void shouldSendEmailForCaveatStoppedOnSolsCtsc()
            throws NotificationClientException, BadRequestException {

        HashMap<String, String> personalisation = new HashMap<>();

        personalisation.put(PERSONALISATION_APPLICANT_NAME, solsCaseDataCtsc.getData().getSolsSOTName());
        personalisation.put(PERSONALISATION_DECEASED_NAME, solsCaseDataCtsc.getData().getDeceasedFullName());
        personalisation.put(PERSONALISATION_SOLICITOR_NAME, solsCaseDataCtsc.getData().getSolsSOTName());
        personalisation.put(PERSONALISATION_SOLICITOR_REFERENCE, solsCaseDataCtsc.getData().getSolsSolicitorAppReference());
        personalisation.put(PERSONALISATION_REGISTRY_NAME, "CTSC");
        personalisation.put(PERSONALISATION_REGISTRY_PHONE, "0300 303 0648");
        personalisation.put(PERSONALISATION_CASE_STOP_DETAILS_DEC, solsCaseDataCtsc.getData().getBoStopDetailsDeclarationParagraph());
        personalisation.put(PERSONALISATION_CASE_STOP_DETAILS, solsCaseDataCtsc.getData().getBoStopDetails());
        personalisation.put(PERSONALISATION_CAVEAT_CASE_ID, solsCaseDataCtsc.getData().getBoCaseStopCaveatId());
        personalisation.put(PERSONALISATION_DECEASED_DOD, solsCaseDataCtsc.getData().getDeceasedDateOfDeathFormatted());
        personalisation.put(PERSONALISATION_CCD_REFERENCE, solsCaseDataCtsc.getId().toString());

        personalisation.put(PERSONALISATION_CAVEATOR_NAME, caveatStoppedCtscCaseData.getData().getCaveatorFullName());
        personalisation.put(PERSONALISATION_CAVEAT_ENTERED, "1st January 2019");
        personalisation.put(PERSONALISATION_CAVEATOR_ADDRESS, "");
        personalisation.put(PERSONALISATION_CAVEAT_EXPIRY_DATE, "1st January 2019");

        when(caveatQueryServiceMock.findCaveatById(CaseType.CAVEAT, null)).thenReturn(caveatStoppedCtscCaseData.getData());
        when(notificationClient.sendEmail(anyString(), anyString(), any(), any(), any())).thenReturn(sendEmailResponse);

        notificationService.sendEmail(CASE_STOPPED_CAVEAT, solsCaseDataCtsc);

        verify(notificationClient).sendEmail(
                eq("sol-case-stopped-caveat"),
                eq("sols@test.com"),
                eq(personalisation),
                eq(null),
                eq("ctsc-emailReplyToId"));

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

    @Test
    public void sendExcelaEmailScannedDocumentWithNoWillReference() throws NotificationClientException {
        notificationService.sendExcelaEmail(excelaCaseDataNoWillReference.build());

        verify(notificationClient).sendEmail(
                eq("pa-excela-data"),
                eq("probatetest@gmail.com"),
                any(),
                anyString());

        verify(pdfManagementService).generateAndUpload(any(SentEmail.class), eq(SENT_EMAIL));
    }

    @Test
    public void sendExcelaEmailScannedDocumentWithNoSubtype() throws NotificationClientException {
        notificationService.sendExcelaEmail(excelaCaseDataNoSubtype.build());

        verify(notificationClient).sendEmail(
                eq("pa-excela-data"),
                eq("probatetest@gmail.com"),
                any(),
                anyString());

        verify(pdfManagementService).generateAndUpload(any(SentEmail.class), eq(SENT_EMAIL));
    }

    @Test
    public void testGenerateReissueGrantProducesEmailCorrectly() throws NotificationClientException {
        CaseDetails caseDetails =
                new CaseDetails(CaseData.builder()
                        .caseType("gop")
                        .applicationType(ApplicationType.PERSONAL)
                        .primaryApplicantEmailAddress("test@test.com")
                        .registryLocation("Bristol")
                        .build(),
                        LAST_MODIFIED, CASE_ID);
        callbackRequest = new CallbackRequest(caseDetails);

        when(eventValidationService.validateEmailRequest(callbackRequest, emailAddressNotificationValidationRules))
                .thenReturn(callbackResponse);
        when(pdfManagementService.generateAndUpload(any(SentEmail.class), any())).thenReturn(Document.builder()
                .documentFileName(SENT_EMAIL_FILE_NAME).build());
        assertEquals(SENT_EMAIL_FILE_NAME, notificationService.generateGrantReissue(callbackRequest).getDocumentFileName());
    }

    @Test
    public void testInvalidEmailExceptionThrownWhenNoEmailPresentForPersonalApplication() {
        CaseDetails caseDetails =
                new CaseDetails(CaseData.builder()
                        .caseType("gop")
                        .applicationType(ApplicationType.PERSONAL)
                        .primaryApplicantEmailAddress("")
                        .registryLocation("Bristol")
                        .build(),
                        LAST_MODIFIED, CASE_ID);
        callbackRequest = new CallbackRequest(caseDetails);
        List<String> errors = new ArrayList<>();
        errors.add("test error");

        when(eventValidationService.validateEmailRequest(callbackRequest,
                emailAddressNotificationValidationRules)).thenReturn(CallbackResponse.builder().errors(errors).build());

        assertThatThrownBy(() -> {
            notificationService.generateGrantReissue(callbackRequest);
        }).isInstanceOf(InvalidEmailException.class)
                .hasMessage("Invalid email exception: No email address provided for application type PA: " + CASE_ID);
    }

    @Test
    public void testInvalidEmailExceptionThrownWhenNoEmailPresentForSolicitorApplication() {
        CaseDetails caseDetails =
                new CaseDetails(CaseData.builder()
                        .caseType("gop")
                        .applicationType(SOLICITOR)
                        .primaryApplicantEmailAddress("")
                        .registryLocation("Bristol")
                        .build(),
                        LAST_MODIFIED, CASE_ID);
        callbackRequest = new CallbackRequest(caseDetails);
        List<String> errors = new ArrayList<>();
        errors.add("test error");

        when(eventValidationService.validateEmailRequest(callbackRequest,
                emailAddressNotificationValidationRules)).thenReturn(CallbackResponse.builder().errors(errors).build());

        assertThatThrownBy(() -> {
            notificationService.generateGrantReissue(callbackRequest);
        }).isInstanceOf(InvalidEmailException.class)
                .hasMessage("Invalid email exception: No email address provided for application type SOLS: " + CASE_ID);
    }

    @Test
    public void shouldSendEmailForRequestInformationPACtsc()
            throws NotificationClientException, BadRequestException {

        HashMap<String, String> personalisation = new HashMap<>();

        personalisation.put(PERSONALISATION_APPLICANT_NAME, personalCaseDataCtscRequestInformation.getData().getPrimaryApplicantFullName());
        personalisation.put(PERSONALISATION_DECEASED_NAME, personalCaseDataCtscRequestInformation.getData().getDeceasedFullName());
        personalisation.put(PERSONALISATION_SOLICITOR_NAME, personalCaseDataCtscRequestInformation.getData().getSolsSOTName());
        personalisation.put(PERSONALISATION_SOLICITOR_REFERENCE, personalCaseDataCtscRequestInformation.getData().getSolsSolicitorAppReference());
        personalisation.put(PERSONALISATION_REGISTRY_NAME, "CTSC");
        personalisation.put(PERSONALISATION_REGISTRY_PHONE, "0300 303 0648");
        personalisation.put(PERSONALISATION_CASE_STOP_DETAILS_DEC, personalCaseDataCtscRequestInformation.getData().getBoStopDetailsDeclarationParagraph());
        personalisation.put(PERSONALISATION_CASE_STOP_DETAILS, personalCaseDataCtscRequestInformation.getData().getBoStopDetails());
        personalisation.put(PERSONALISATION_CAVEAT_CASE_ID, personalCaseDataCtscRequestInformation.getData().getBoCaseStopCaveatId());
        personalisation.put(PERSONALISATION_DECEASED_DOD, personalCaseDataCtscRequestInformation.getData().getDeceasedDateOfDeathFormatted());
        personalisation.put(PERSONALISATION_CCD_REFERENCE, personalCaseDataCtscRequestInformation.getId().toString());

        when(notificationClient.sendEmail(anyString(), anyString(), any(), any(), any())).thenReturn(sendEmailResponse);

        notificationService.sendEmail(CASE_STOPPED_REQUEST_INFORMATION, personalCaseDataCtscRequestInformation);

        verify(notificationClient).sendEmail(
                eq("pa-request-information"),
                eq("personal@test.com"),
                eq(personalisation),
                eq(null),
                eq("ctsc-emailReplyToId"));

        when(pdfManagementService.generateDocmosisDocumentAndUpload(any(Map.class), any())).thenReturn(Document.builder()
                .documentFileName(SENT_EMAIL_FILE_NAME).build());
    }

    @Test
    public void shouldSendEmailForRequestInformationSolsCtsc()
            throws NotificationClientException, BadRequestException {

        HashMap<String, String> personalisation = new HashMap<>();

        personalisation.put(PERSONALISATION_APPLICANT_NAME, solsCaseDataCtscRequestInformation.getData().getSolsSOTName());
        personalisation.put(PERSONALISATION_DECEASED_NAME, solsCaseDataCtscRequestInformation.getData().getDeceasedFullName());
        personalisation.put(PERSONALISATION_SOLICITOR_NAME, solsCaseDataCtscRequestInformation.getData().getSolsSOTName());
        personalisation.put(PERSONALISATION_SOLICITOR_REFERENCE, solsCaseDataCtscRequestInformation.getData().getSolsSolicitorAppReference());
        personalisation.put(PERSONALISATION_REGISTRY_NAME, "CTSC");
        personalisation.put(PERSONALISATION_REGISTRY_PHONE, "0300 303 0648");
        personalisation.put(PERSONALISATION_CASE_STOP_DETAILS_DEC, solsCaseDataCtscRequestInformation.getData().getBoStopDetailsDeclarationParagraph());
        personalisation.put(PERSONALISATION_CASE_STOP_DETAILS, solsCaseDataCtscRequestInformation.getData().getBoStopDetails());
        personalisation.put(PERSONALISATION_CAVEAT_CASE_ID, solsCaseDataCtscRequestInformation.getData().getBoCaseStopCaveatId());
        personalisation.put(PERSONALISATION_DECEASED_DOD, solsCaseDataCtscRequestInformation.getData().getDeceasedDateOfDeathFormatted());
        personalisation.put(PERSONALISATION_CCD_REFERENCE, solsCaseDataCtscRequestInformation.getId().toString());

        when(notificationClient.sendEmail(anyString(), anyString(), any(), any(), any())).thenReturn(sendEmailResponse);

        notificationService.sendEmail(CASE_STOPPED_REQUEST_INFORMATION, solsCaseDataCtscRequestInformation);

        verify(notificationClient).sendEmail(
                eq("sols-request-information"),
                eq("sols@test.com"),
                eq(personalisation),
                eq(null),
                eq("ctsc-emailReplyToId"));

        when(pdfManagementService.generateDocmosisDocumentAndUpload(any(Map.class), any())).thenReturn(Document.builder()
                .documentFileName(SENT_EMAIL_FILE_NAME).build());
    }

}
