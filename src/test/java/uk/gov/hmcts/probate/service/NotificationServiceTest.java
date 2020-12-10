package uk.gov.hmcts.probate.service;

import com.google.common.collect.ImmutableList;
import org.assertj.core.api.Assertions;
import org.json.JSONObject;
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
import uk.gov.hmcts.probate.config.properties.registries.Registry;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.exception.InvalidEmailException;
import uk.gov.hmcts.probate.insights.AppInsights;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.CaseType;
import uk.gov.hmcts.probate.model.Constants;
import uk.gov.hmcts.probate.model.ExecutorsApplyingNotification;
import uk.gov.hmcts.probate.model.LanguagePreference;
import uk.gov.hmcts.probate.model.SentEmail;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.ProbateAddress;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.raw.BulkPrint;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.service.client.DocumentStoreClient;
import uk.gov.hmcts.probate.service.template.pdf.LocalDateToWelshStringConverter;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.probate.validator.EmailAddressNotificationValidationRule;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.probate.model.cases.RegistryLocation;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.SendEmailResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
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
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.model.DocumentType.SENT_EMAIL;
import static uk.gov.hmcts.probate.model.State.CASE_STOPPED;
import static uk.gov.hmcts.probate.model.State.CASE_STOPPED_CAVEAT;
import static uk.gov.hmcts.probate.model.State.CASE_STOPPED_REQUEST_INFORMATION;
import static uk.gov.hmcts.probate.model.State.CAVEAT_EXTEND;
import static uk.gov.hmcts.probate.model.State.CAVEAT_RAISED;
import static uk.gov.hmcts.probate.model.State.CAVEAT_RAISED_SOLS;
import static uk.gov.hmcts.probate.model.State.DOCUMENTS_RECEIVED;
import static uk.gov.hmcts.probate.model.State.GENERAL_CAVEAT_MESSAGE;
import static uk.gov.hmcts.probate.model.State.GRANT_ISSUED;
import static uk.gov.hmcts.probate.model.State.GRANT_RAISED;
import static uk.gov.hmcts.probate.model.State.GRANT_REISSUED;
import static uk.gov.hmcts.probate.model.State.REDECLARATION_SOT;

@RunWith(SpringRunner.class)
@SpringBootTest
public class NotificationServiceTest {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private LocalDateToWelshStringConverter localDateToWelshStringConverter;

    @MockBean
    private AppInsights appInsights;

    @MockBean
    private SendEmailResponse sendEmailResponse;

    @MockBean
    private PDFManagementService pdfManagementService;

    @MockBean
    private CaveatQueryService caveatQueryServiceMock;

    @Mock
    private EventValidationService eventValidationService;

    @Mock
    private List<EmailAddressNotificationValidationRule> emailAddressNotificationValidationRules;

    @Mock
    private CallbackResponse callbackResponse;

    @Mock
    private DateFormatterService dateFormatterService;

    @MockBean
    private AuthTokenGenerator tokenGenerator;

    @MockBean
    private DocumentStoreClient documentStoreClient;

    @SpyBean
    private NotificationClient notificationClient;

    private CaseDetails personalCaseDataOxford;
    private CaseDetails solicitorCaseDataOxford;
    private CaseDetails personalCaseDataBirmingham;
    private CaseDetails solicitorCaseDataBirmingham;
    private CaseDetails personalCaseDataManchester;
    private CaseDetails personalCaseDataCtsc;
    private CaseDetails personalCaseDataCtscBilingual;
    private CaseDetails solsCaseDataCtsc;
    private CaseDetails personalCaseDataCtscRequestInformation;
    private CaseDetails personalCaseDataBristol;
    private CaseDetails solsCaseDataCtscRequestInformation;
    private CaseDetails solicitorCaseDataManchester;
    private CaseDetails personalGrantDelayedOxford;
    private CaseDetails personalGrantRaisedOxford;
    private CaseDetails solicitorGrantRaisedOxford;
    private CaseDetails personalGrantRaisedOxfordPaper;
    private CaseDetails solicitorGrantRaisedOxfordPaper;
    private CaseDetails personalGrantRaisedOxfordPaperWelsh;
    private CaseDetails solicitorGrantRaisedOxfordPaperWelsh;

    private ImmutableList.Builder<ReturnedCaseDetails> excelaCaseData = new ImmutableList.Builder<>();
    private ImmutableList.Builder<ReturnedCaseDetails> excelaCaseDataNoWillReference = new ImmutableList.Builder<>();
    private ImmutableList.Builder<ReturnedCaseDetails> excelaCaseDataNoSubtype = new ImmutableList.Builder<>();

    private CaveatDetails personalCaveatDataOxford;
    private CaveatDetails personalCaveatDataBilingualOxford;
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
    private CaveatDetails caveatRaisedCaseDataBilingual;
    private CaveatDetails caveatRaisedCtscCaseData;
    private CaveatDetails caveatRaisedCtscCaseDataBilingual;
    private CaveatDetails solicitorCaveatRaisedCaseData;
    private CaveatData caveatData;
    private CallbackRequest callbackRequest;
    private CaveatDetails caveatStoppedCtscCaseData;


    @Mock
    private RegistriesProperties registriesPropertiesMock;

    private static final Long ID = 1L;
    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private static final Long CASE_ID = 12345678987654321L;
    private static final String SENT_EMAIL_FILE_NAME = "sentEmail.pdf";
    private static final byte[] DOC_BYTES = {(byte) 23};
    private static final String SOLS_CAVEATS_NAME = "Sir/Madam";

    private static final String PERSONALISATION_APPLICANT_NAME = "applicant_name";
    private static final String PERSONALISATION_APPLICANT_FORENAMES = "applicantFN";
    private static final String PERSONALISATION_APPLICANT_SURNAME = "applicantSN";
    private static final String PERSONALISATION_APPLICANT_EMAIL = "primary@probate-test.com";
    private static final String PERSONALISATION_DECEASED_NAME = "deceased_name";
    private static final String PERSONALISATION_DECEASED_FORNAMES = "deceasedFN";
    private static final String PERSONALISATION_DECEASED_SURNAME = "deceasedSN";
    private static final String PERSONALISATION_SOLICITOR_NAME = "solicitor_name";
    private static final String PERSONALISATION_SOLICITOR_REFERENCE = "solicitor_reference";
    private static final String PERSONALISATION_REGISTRY_NAME = "registry_name";
    private static final String PERSONALISATION_REGISTRY_PHONE = "registry_phone";
    private static final String PERSONALISATION_CASE_STOP_DETAILS = "case-stop-details";
    private static final String PERSONALISATION_CAVEAT_CASE_ID = "caveat_case_id";
    private static final String PERSONALISATION_DECEASED_DOD = "deceased_dod";
    private static final String PERSONALISATION_CCD_REFERENCE = "ccd_reference";
    private static final String PERSONALISATION_MESSAGE_CONTENT = "message_content";
    private static final String PERSONALISATION_WELSH_CAVEAT_EXPIRY_DATE = "welsh_caveat_expiry_date";
    private static final String PERSONALISATION_CASE_DATA = "caseData";
    private static final String PERSONALISATION_CAVEAT_EXPIRY_DATE = "caveat_expiry_date";
    private static final String PERSONALISATION_CAVEAT_ENTERED = "date_caveat_entered";
    private static final String PERSONALISATION_CAVEATOR_NAME = "caveator_name";
    private static final String PERSONALISATION_CAVEATOR_ADDRESS = "caveator_address";
    private static final String PERSONALISATION_CASE_STOP_DETAILS_DEC = "boStopDetailsDeclarationParagraph";
    private static final String PERSONALISATION_ADDRESSEE = "addressee";
    private static final String PERSONALISATION_SOT_LINK = "sot_link";
    private static final String PERSONALISATION_WELSH_DECEASED_DATE_OF_DEATH = "welsh_deceased_date_of_death";

    @Before
    public void setUp() throws NotificationClientException, IOException {
        when(sendEmailResponse.getFromEmail()).thenReturn(Optional.of("emailResponseFrom@probate-test.com"));
        when(sendEmailResponse.getBody()).thenReturn("test-body");
        when(documentStoreClient.retrieveDocument(any(), any())).thenReturn(DOC_BYTES);

        when(tokenGenerator.generate()).thenReturn("123");

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
            .primaryApplicantEmailAddress("primary@probate-test.com")
            .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
            .build(), LAST_MODIFIED, ID);

        personalGrantDelayedOxford = new CaseDetails(CaseData.builder()
            .applicationType(PERSONAL)
            .primaryApplicantForenames(PERSONALISATION_APPLICANT_FORENAMES)
            .primaryApplicantSurname(PERSONALISATION_APPLICANT_SURNAME)
            .deceasedForenames(PERSONALISATION_DECEASED_FORNAMES)
            .deceasedSurname(PERSONALISATION_DECEASED_SURNAME)
            .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
            .registryLocation("Oxford")
            .primaryApplicantEmailAddress("primary@probate-test.com")
            .languagePreferenceWelsh("No")
            .build(), LAST_MODIFIED, ID);

        solicitorCaseDataOxford = new CaseDetails(CaseData.builder()
            .applicationType(SOLICITOR)
            .registryLocation("Oxford")
            .solsSolicitorEmail("solicitor@probate-test.com")
            .solsSolicitorAppReference("1234-5678-9012")
            .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
            .build(), LAST_MODIFIED, ID);

        personalCaseDataBirmingham = new CaseDetails(CaseData.builder()
            .applicationType(PERSONAL)
            .registryLocation("Birmingham")
            .primaryApplicantEmailAddress("primary@probate-test.com")
            .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
            .build(), LAST_MODIFIED, ID);

        solicitorCaseDataBirmingham = new CaseDetails(CaseData.builder()
            .applicationType(SOLICITOR)
            .registryLocation("Birmingham")
            .solsSolicitorEmail("solicitor@probate-test.com")
            .solsSolicitorAppReference("1234-5678-9012")
            .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
            .build(), LAST_MODIFIED, ID);

        personalCaseDataManchester = new CaseDetails(CaseData.builder()
            .applicationType(PERSONAL)
            .registryLocation("Manchester")
            .primaryApplicantEmailAddress("primary@probate-test.com")
            .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
            .build(), LAST_MODIFIED, ID);

        personalCaseDataCtsc = new CaseDetails(CaseData.builder()
            .applicationType(PERSONAL)
            .registryLocation("ctsc")
            .primaryApplicantEmailAddress("primary@probate-test.com")
            .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
            .build(), LAST_MODIFIED, ID);

        personalCaseDataCtscBilingual = new CaseDetails(CaseData.builder()
            .applicationType(PERSONAL)
            .registryLocation("ctsc")
            .primaryApplicantEmailAddress("primary@probate-test.com")
            .languagePreferenceWelsh("Yes")
            .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
            .build(), LAST_MODIFIED, ID);

        solsCaseDataCtsc = new CaseDetails(CaseData.builder()
            .applicationType(SOLICITOR)
            .solsSOTName("fred smith")
            .registryLocation("ctsc")
            .solsSolicitorEmail("solicitor@probate-test.com")
            .primaryApplicantEmailAddress("primary@probate-test.com")
            .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
            .build(), LAST_MODIFIED, ID);

        personalCaseDataCtscRequestInformation = new CaseDetails(CaseData.builder()
            .applicationType(PERSONAL)
            .boStopDetailsDeclarationParagraph("Yes")
            .deceasedDateOfDeath(LocalDate.now())
            .primaryApplicantForenames("Fred Smith")
            .registryLocation("ctsc")
            .primaryApplicantEmailAddress("primary@probate-test.com")
            .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
            .build(), LAST_MODIFIED, ID);

        solsCaseDataCtscRequestInformation = new CaseDetails(CaseData.builder()
            .applicationType(SOLICITOR)
            .solsSOTName("fred smith")
            .boStopDetailsDeclarationParagraph("Yes")
            .deceasedDateOfDeath(LocalDate.now())
            .registryLocation("ctsc")
            .solsSolicitorEmail("solicitor@probate-test.com")
            .primaryApplicantEmailAddress("primary@probate-test.com")
            .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
            .build(), LAST_MODIFIED, ID);

        personalCaseDataBristol = new CaseDetails(CaseData.builder()
            .applicationType(PERSONAL)
            .registryLocation("Bristol")
            .primaryApplicantEmailAddress("primary@probate-test.com")
            .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
            .build(), LAST_MODIFIED, ID);


        solicitorCaseDataManchester = new CaseDetails(CaseData.builder()
            .applicationType(SOLICITOR)
            .registryLocation("Manchester")
            .solsSolicitorEmail("solicitor@probate-test.com")
            .solsSolicitorAppReference("1234-5678-9012")
            .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
            .build(), LAST_MODIFIED, ID);

        personalGrantRaisedOxford = new CaseDetails(CaseData.builder()
            .applicationType(PERSONAL)
            .registryLocation("Oxford")
            .primaryApplicantEmailAddress("primary@probate-test.com")
            .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
            .build(), LAST_MODIFIED, ID);

        solicitorGrantRaisedOxford = new CaseDetails(CaseData.builder()
            .applicationType(SOLICITOR)
            .registryLocation("Oxford")
            .solsSolicitorEmail("solicitor@probate-test.com")
            .solsSolicitorAppReference("1234-5678-9012")
            .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
            .build(), LAST_MODIFIED, ID);

        personalGrantRaisedOxfordPaper = new CaseDetails(CaseData.builder()
            .paperForm(YES)
            .applicationType(PERSONAL)
            .registryLocation("Oxford")
            .primaryApplicantEmailAddress("primary@probate-test.com")
            .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
            .build(), LAST_MODIFIED, ID);

        personalGrantRaisedOxfordPaperWelsh = new CaseDetails(CaseData.builder()
            .paperForm(YES)
            .applicationType(PERSONAL)
            .registryLocation("Oxford")
            .primaryApplicantEmailAddress("primary@probate-test.com")
            .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
            .languagePreferenceWelsh("Yes")
            .build(), LAST_MODIFIED, ID);

        solicitorGrantRaisedOxfordPaper = new CaseDetails(CaseData.builder()
            .paperForm(YES)
            .applicationType(SOLICITOR)
            .registryLocation("Oxford")
            .solsSolicitorEmail("solicitor@probate-test.com")
            .solsSolicitorAppReference("1234-5678-9012")
            .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
            .build(), LAST_MODIFIED, ID);

        solicitorGrantRaisedOxfordPaperWelsh = new CaseDetails(CaseData.builder()
            .paperForm(YES)
            .applicationType(SOLICITOR)
            .registryLocation("Oxford")
            .solsSolicitorEmail("solicitor@probate-test.com")
            .solsSolicitorAppReference("1234-5678-9012")
            .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
            .languagePreferenceWelsh("Yes")
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
            .caveatorEmailAddress("caveator@probate-test.com")
            .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
            .expiryDate(LocalDate.of(2019, 01, 01))
            .build(), LAST_MODIFIED, ID);

        caveatRaisedCaseDataBilingual = new CaveatDetails(CaveatData.builder()
            .applicationType(PERSONAL)
            .registryLocation("Oxford")
            .caveatorEmailAddress("caveator@probate-test.com")
            .languagePreferenceWelsh("Yes")
            .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
            .expiryDate(LocalDate.of(2019, 01, 01))
            .build(), LAST_MODIFIED, ID);

        caveatRaisedCtscCaseData = new CaveatDetails(CaveatData.builder()
            .applicationType(PERSONAL)
            .registryLocation("ctsc")
            .caveatorEmailAddress("caveator@probate-test.com")
            .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
            .expiryDate(LocalDate.of(2019, 01, 01))
            .build(), LAST_MODIFIED, ID);


        caveatRaisedCtscCaseDataBilingual = new CaveatDetails(CaveatData.builder()
            .applicationType(PERSONAL)
            .registryLocation("ctsc")
            .caveatorEmailAddress("caveator@probate-test.com")
            .languagePreferenceWelsh("Yes")
            .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
            .expiryDate(LocalDate.of(2019, 01, 01))
            .build(), LAST_MODIFIED, ID);

        solicitorCaveatRaisedCaseData = new CaveatDetails(CaveatData.builder()
            .applicationType(SOLICITOR)
            .registryLocation("ctsc")
            .caveatorEmailAddress("solicitor@probate-test.com")
            .solsSolicitorAppReference("SOLSREF")
            .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
            .expiryDate(LocalDate.of(2019, 01, 01))
            .build(), LAST_MODIFIED, ID);

        caveatStoppedCtscCaseData = new CaveatDetails(CaveatData.builder()
            .applicationSubmittedDate(LocalDate.of(2019, 01, 01))
            .applicationType(PERSONAL)
            .registryLocation("ctsc")
            .caveatorEmailAddress("caveator@probate-test.com")
            .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
            .expiryDate(LocalDate.of(2019, 01, 01))
            .build(), LAST_MODIFIED, ID);

        personalCaveatDataOxford = new CaveatDetails(CaveatData.builder()
            .applicationType(PERSONAL)
            .registryLocation("Oxford")
            .caveatorEmailAddress("caveator@probate-test.com")
            .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
            .build(), LAST_MODIFIED, ID);

        personalCaveatDataBilingualOxford = new CaveatDetails(CaveatData.builder()
            .applicationType(PERSONAL)
            .registryLocation("Oxford")
            .caveatorEmailAddress("caveator@probate-test.com")
            .languagePreferenceWelsh("Yes")
            .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
            .build(), LAST_MODIFIED, ID);

        personalCaveatDataBirmingham = new CaveatDetails(CaveatData.builder()
            .applicationType(PERSONAL)
            .registryLocation("Birmingham")
            .caveatorEmailAddress("caveator@probate-test.com")
            .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
            .build(), LAST_MODIFIED, ID);

        personalCaveatDataManchester = new CaveatDetails(CaveatData.builder()
            .applicationType(PERSONAL)
            .registryLocation("Manchester")
            .caveatorEmailAddress("caveator@probate-test.com")
            .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
            .build(), LAST_MODIFIED, ID);

        personalCaveatDataLeeds = new CaveatDetails(CaveatData.builder()
            .applicationType(PERSONAL)
            .registryLocation("Leeds")
            .caveatorEmailAddress("caveator@probate-test.com")
            .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
            .build(), LAST_MODIFIED, ID);

        personalCaveatDataLiverpool = new CaveatDetails(CaveatData.builder()
            .applicationType(PERSONAL)
            .registryLocation("Liverpool")
            .caveatorEmailAddress("caveator@probate-test.com")
            .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
            .build(), LAST_MODIFIED, ID);

        personalCaveatDataBrighton = new CaveatDetails(CaveatData.builder()
            .applicationType(PERSONAL)
            .registryLocation("Brighton")
            .caveatorEmailAddress("caveator@probate-test.com")
            .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
            .build(), LAST_MODIFIED, ID);

        personalCaveatDataLondon = new CaveatDetails(CaveatData.builder()
            .applicationType(PERSONAL)
            .registryLocation("London")
            .caveatorEmailAddress("caveator@probate-test.com")
            .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
            .build(), LAST_MODIFIED, ID);

        personalCaveatDataCardiff = new CaveatDetails(CaveatData.builder()
            .applicationType(PERSONAL)
            .registryLocation("Cardiff")
            .caveatorEmailAddress("caveator@probate-test.com")
            .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
            .build(), LAST_MODIFIED, ID);

        personalCaveatDataNewcastle = new CaveatDetails(CaveatData.builder()
            .applicationType(PERSONAL)
            .registryLocation("Newcastle")
            .caveatorEmailAddress("caveator@probate-test.com")
            .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
            .build(), LAST_MODIFIED, ID);

        personalCaveatDataWinchester = new CaveatDetails(CaveatData.builder()
            .applicationType(PERSONAL)
            .registryLocation("Winchester")
            .caveatorEmailAddress("caveator@probate-test.com")
            .deceasedDateOfDeath(LocalDate.of(2000, 12, 12))
            .build(), LAST_MODIFIED, ID);

        personalCaveatDataBristol = new CaveatDetails(CaveatData.builder()
            .applicationType(PERSONAL)
            .registryLocation("Bristol")
            .caveatorEmailAddress("caveator@probate-test.com")
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
            eq("primary@probate-test.com"),
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
            eq("solicitor@probate-test.com"),
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
            eq("primary@probate-test.com"),
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
            eq("solicitor@probate-test.com"),
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
            eq("primary@probate-test.com"),
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
            eq("solicitor@probate-test.com"),
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
            eq("primary@probate-test.com"),
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
            eq("solicitor@probate-test.com"),
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
            eq("primary@probate-test.com"),
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
            eq("solicitor@probate-test.com"),
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
            eq("primary@probate-test.com"),
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
            eq("solicitor@probate-test.com"),
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
            eq("primary@probate-test.com"),
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
            eq("primary@probate-test.com"),
            any(),
            isNull(),
            eq("bristol-emailReplyToId"));

        verify(pdfManagementService).generateAndUpload(any(SentEmail.class), eq(SENT_EMAIL));
    }

    @Test
    public void sendGrantRaisedEmailToPersonalApplicantFromOxford()
        throws NotificationClientException, BadRequestException {

        notificationService.sendEmail(GRANT_RAISED, personalGrantRaisedOxford);

        verify(notificationClient).sendEmail(
            eq("pa-grant-raised"),
            eq("primary@probate-test.com"),
            any(),
            isNull());

        verify(pdfManagementService).generateAndUpload(any(SentEmail.class), eq(SENT_EMAIL));
    }

    @Test
    public void sendGrantRaisedEmailToSolicitorApplicantFromOxford()
        throws NotificationClientException, BadRequestException {

        notificationService.sendEmail(GRANT_RAISED, solicitorGrantRaisedOxford);

        verify(notificationClient).sendEmail(
            eq("sol-grant-raised"),
            eq("solicitor@probate-test.com"),
            any(),
            eq("1234-5678-9012"));

        verify(pdfManagementService).generateAndUpload(any(SentEmail.class), eq(SENT_EMAIL));
    }

    @Test
    public void sendGrantRaisedEmailToPersonalApplicantFromOxfordPaperForm()
        throws NotificationClientException, BadRequestException {

        notificationService.sendEmail(GRANT_RAISED, personalGrantRaisedOxfordPaper);

        verify(notificationClient).sendEmail(
            eq("pa-grant-raised-paper-bulk-scan"),
            eq("primary@probate-test.com"),
            any(),
            isNull());

        verify(pdfManagementService).generateAndUpload(any(SentEmail.class), eq(SENT_EMAIL));
    }

    @Test
    public void sendGrantRaisedEmailToSolicitorApplicantFromOxfordPaperForm()
        throws NotificationClientException, BadRequestException {

        notificationService.sendEmail(GRANT_RAISED, solicitorGrantRaisedOxfordPaper);

        verify(notificationClient).sendEmail(
            eq("sol-grant-raised-paper-bulk-scan"),
            eq("solicitor@probate-test.com"),
            any(),
            eq("1234-5678-9012"));

        verify(pdfManagementService).generateAndUpload(any(SentEmail.class), eq(SENT_EMAIL));
    }

    @Test
    public void sendGrantRaisedEmailToPersonalApplicantFromOxfordPaperFormWelsh()
        throws NotificationClientException, BadRequestException {

        notificationService.sendEmail(GRANT_RAISED, personalGrantRaisedOxfordPaperWelsh);

        verify(notificationClient).sendEmail(
            eq("pa-grant-raised-paper-bulk-scan-welsh"),
            eq("primary@probate-test.com"),
            any(),
            isNull());

        verify(pdfManagementService).generateAndUpload(any(SentEmail.class), eq(SENT_EMAIL));
    }

    @Test
    public void sendGrantRaisedEmailToSolicitorApplicantFromOxfordPaperFormWelsh()
        throws NotificationClientException, BadRequestException {

        notificationService.sendEmail(GRANT_RAISED, solicitorGrantRaisedOxfordPaperWelsh);

        verify(notificationClient).sendEmail(
            eq("sol-grant-raised-paper-bulk-scan-welsh"),
            eq("solicitor@probate-test.com"),
            any(),
            eq("1234-5678-9012"));

        verify(pdfManagementService).generateAndUpload(any(SentEmail.class), eq(SENT_EMAIL));
    }

    @Test
    public void sendGeneralCaveatEmailToPersonalApplicantFromOxford()
        throws NotificationClientException, BadRequestException {

        notificationService.sendCaveatEmail(GENERAL_CAVEAT_MESSAGE, personalCaveatDataOxford);

        verify(notificationClient).sendEmail(
            eq("pa-general-caveat-message"),
            eq("caveator@probate-test.com"),
            any(),
            anyString());

        verify(pdfManagementService).generateAndUpload(any(SentEmail.class), eq(SENT_EMAIL));
    }


    @Test
    public void sendGeneralCaveatBilingualEmailToPersonalApplicantFromOxford()
        throws NotificationClientException, BadRequestException {

        notificationService.sendCaveatEmail(GENERAL_CAVEAT_MESSAGE, personalCaveatDataBilingualOxford);

        verify(notificationClient).sendEmail(
            eq("pa-general-caveat-message-welsh"),
            eq("caveator@probate-test.com"),
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
            eq("caveator@probate-test.com"),
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
            eq("caveator@probate-test.com"),
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
            eq("caveator@probate-test.com"),
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
            eq("caveator@probate-test.com"),
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
            eq("caveator@probate-test.com"),
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
            eq("caveator@probate-test.com"),
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
            eq("caveator@probate-test.com"),
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
            eq("caveator@probate-test.com"),
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
            eq("caveator@probate-test.com"),
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
            eq("caveator@probate-test.com"),
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
        personalisation.put(PERSONALISATION_WELSH_CAVEAT_EXPIRY_DATE, "1 Ionawr 2019");
        notificationService.sendCaveatEmail(CAVEAT_RAISED, caveatRaisedCaseData);

        verify(notificationClient).sendEmail(
            eq("pa-caveat-raised"),
            eq("caveator@probate-test.com"),
            eq(personalisation),
            eq("1"));

        verify(pdfManagementService).generateAndUpload(any(SentEmail.class), eq(SENT_EMAIL));
    }


    @Test
    public void sendGeneralCaveatRaisedBilingualEmail()
        throws NotificationClientException, BadRequestException {

        HashMap<String, String> personalisation = new HashMap<>();

        personalisation.put(PERSONALISATION_APPLICANT_NAME, caveatRaisedCaseDataBilingual.getData().getCaveatorFullName());
        personalisation.put(PERSONALISATION_DECEASED_NAME, caveatRaisedCaseDataBilingual.getData().getDeceasedFullName());
        personalisation.put(PERSONALISATION_CCD_REFERENCE, caveatRaisedCaseDataBilingual.getId().toString());
        personalisation.put(PERSONALISATION_MESSAGE_CONTENT, caveatRaisedCaseDataBilingual.getData().getMessageContent());
        personalisation.put(PERSONALISATION_REGISTRY_NAME, "Oxford Probate Registry");
        personalisation.put(PERSONALISATION_REGISTRY_PHONE, "0186 579 3055");
        personalisation.put(PERSONALISATION_CAVEAT_EXPIRY_DATE, "1st January 2019");
        personalisation.put(PERSONALISATION_WELSH_CAVEAT_EXPIRY_DATE, "1 Ionawr 2019");
        notificationService.sendCaveatEmail(CAVEAT_RAISED, caveatRaisedCaseDataBilingual);

        verify(notificationClient).sendEmail(
            eq("pa-caveat-raised-welsh"),
            eq("caveator@probate-test.com"),
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
        personalisation.put(PERSONALISATION_WELSH_CAVEAT_EXPIRY_DATE, "1 Ionawr 2019");


        notificationService.sendCaveatEmail(CAVEAT_RAISED, caveatRaisedCtscCaseData);

        verify(notificationClient).sendEmail(
            eq("pa-ctsc-caveat-raised"),
            eq("caveator@probate-test.com"),
            eq(personalisation),
            eq("1"));

        verify(pdfManagementService).generateAndUpload(any(SentEmail.class), eq(SENT_EMAIL));
    }

    @Test
    public void sendGeneralCaveatRaisedCtscBilingualEmail()
        throws NotificationClientException, BadRequestException {

        HashMap<String, String> personalisation = new HashMap<>();

        personalisation.put(PERSONALISATION_APPLICANT_NAME, caveatRaisedCtscCaseDataBilingual.getData().getCaveatorFullName());
        personalisation.put(PERSONALISATION_DECEASED_NAME, caveatRaisedCtscCaseDataBilingual.getData().getDeceasedFullName());
        personalisation.put(PERSONALISATION_CCD_REFERENCE, caveatRaisedCtscCaseDataBilingual.getId().toString());
        personalisation.put(PERSONALISATION_MESSAGE_CONTENT, caveatRaisedCtscCaseDataBilingual.getData().getMessageContent());
        personalisation.put(PERSONALISATION_REGISTRY_NAME, "CTSC");
        personalisation.put(PERSONALISATION_REGISTRY_PHONE, "0300 303 0648");
        personalisation.put(PERSONALISATION_CAVEAT_EXPIRY_DATE, "1st January 2019");
        personalisation.put(PERSONALISATION_WELSH_CAVEAT_EXPIRY_DATE, "1 Ionawr 2019");

        notificationService.sendCaveatEmail(CAVEAT_RAISED, caveatRaisedCtscCaseDataBilingual);

        verify(notificationClient).sendEmail(
            eq("pa-ctsc-caveat-raised-welsh"),
            eq("caveator@probate-test.com"),
            eq(personalisation),
            eq("1"));

        verify(pdfManagementService).generateAndUpload(any(SentEmail.class), eq(SENT_EMAIL));
    }

    @Test
    public void sendSolsCaveatRaisedCtscEmail()
        throws NotificationClientException, BadRequestException {

        HashMap<String, String> personalisation = new HashMap<>();

        personalisation.put(PERSONALISATION_APPLICANT_NAME, SOLS_CAVEATS_NAME);
        personalisation.put(PERSONALISATION_DECEASED_NAME, solicitorCaveatRaisedCaseData.getData().getDeceasedFullName());
        personalisation.put(PERSONALISATION_CCD_REFERENCE, solicitorCaveatRaisedCaseData.getId().toString());
        personalisation.put(PERSONALISATION_MESSAGE_CONTENT, caveatRaisedCtscCaseData.getData().getMessageContent());
        personalisation.put(PERSONALISATION_REGISTRY_NAME, "CTSC");
        personalisation.put(PERSONALISATION_REGISTRY_PHONE, "0300 303 0648");
        personalisation.put(PERSONALISATION_SOLICITOR_REFERENCE, solicitorCaveatRaisedCaseData.getData().getSolsSolicitorAppReference());
        personalisation.put(PERSONALISATION_CAVEAT_EXPIRY_DATE, "1st January 2019");
        personalisation.put(PERSONALISATION_WELSH_CAVEAT_EXPIRY_DATE, "1 Ionawr 2019");
        notificationService.sendCaveatEmail(CAVEAT_RAISED_SOLS, solicitorCaveatRaisedCaseData);

        verify(notificationClient).sendEmail(
            eq("solicitor-caveat-raised"),
            eq("solicitor@probate-test.com"),
            eq(personalisation),
            eq("1"));

        verify(pdfManagementService).generateAndUpload(any(SentEmail.class), eq(SENT_EMAIL));
    }

    @Test
    public void sendGeneralCaveatRaisedCtscEmailThrowsUnsupportedState()
        throws BadRequestException {

        Assertions.assertThatThrownBy(() -> {
            notificationService.sendCaveatEmail(DOCUMENTS_RECEIVED, caveatRaisedCtscCaseData);
        })
            .isInstanceOf(BadRequestException.class)
            .hasMessage("Unsupported State");
    }

    @Test
    public void shouldSendEmailForCaveatStoppedOnCtsBilingual()
        throws NotificationClientException, BadRequestException {

        HashMap<String, String> personalisation = new HashMap<>();

        personalisation.put(PERSONALISATION_APPLICANT_NAME, personalCaseDataCtsc.getData().getPrimaryApplicantFullName());
        personalisation.put(PERSONALISATION_DECEASED_NAME, personalCaseDataCtsc.getData().getDeceasedFullName());
        personalisation.put(PERSONALISATION_SOLICITOR_NAME, personalCaseDataCtsc.getData().getSolsSOTName());
        personalisation.put(PERSONALISATION_SOLICITOR_REFERENCE, personalCaseDataCtsc.getData().getSolsSolicitorAppReference());
        personalisation.put(PERSONALISATION_REGISTRY_NAME, "CTSC");
        personalisation.put(PERSONALISATION_REGISTRY_PHONE, "0300 303 0648");
        personalisation.put(PERSONALISATION_CASE_STOP_DETAILS_DEC,
            personalCaseDataCtsc.getData().getBoStopDetailsDeclarationParagraph());
        personalisation.put(PERSONALISATION_CASE_STOP_DETAILS, personalCaseDataCtsc.getData().getBoStopDetails());
        personalisation.put(PERSONALISATION_CAVEAT_CASE_ID, personalCaseDataCtsc.getData().getBoCaseStopCaveatId());
        personalisation.put(PERSONALISATION_DECEASED_DOD, personalCaseDataCtsc.getData().getDeceasedDateOfDeathFormatted());
        personalisation.put(PERSONALISATION_CCD_REFERENCE, personalCaseDataCtsc.getId().toString());

        personalisation.put(PERSONALISATION_CAVEATOR_NAME, caveatStoppedCtscCaseData.getData().getCaveatorFullName());
        personalisation.put(PERSONALISATION_CAVEAT_ENTERED, "1st January 2019");
        personalisation.put(PERSONALISATION_CAVEATOR_ADDRESS, "");
        personalisation.put(PERSONALISATION_CAVEAT_EXPIRY_DATE, "1st January 2019");
        personalisation.put(PERSONALISATION_WELSH_DECEASED_DATE_OF_DEATH, localDateToWelshStringConverter.convert(personalCaseDataCtsc.getData().getDeceasedDateOfDeath()));


        when(caveatQueryServiceMock.findCaveatById(CaseType.CAVEAT, null)).thenReturn(caveatStoppedCtscCaseData.getData());
        when(notificationClient.sendEmail(anyString(), anyString(), any(), any(), any())).thenReturn(sendEmailResponse);

        notificationService.sendEmail(CASE_STOPPED_CAVEAT, personalCaseDataCtsc);

        verify(notificationClient).sendEmail(
            eq("pa-case-stopped-caveat"),
            eq("primary@probate-test.com"),
            eq(personalisation),
            eq(null),
            eq("ctsc-emailReplyToId"));

        verify(pdfManagementService).generateAndUpload(any(SentEmail.class), eq(SENT_EMAIL));
    }

    @Test
    public void shouldSendEmailForCaveatStoppedOnCtsc()
        throws NotificationClientException, BadRequestException {

        HashMap<String, String> personalisation = new HashMap<>();

        personalisation.put(PERSONALISATION_APPLICANT_NAME, personalCaseDataCtscBilingual.getData().getPrimaryApplicantFullName());
        personalisation.put(PERSONALISATION_DECEASED_NAME, personalCaseDataCtscBilingual.getData().getDeceasedFullName());
        personalisation.put(PERSONALISATION_SOLICITOR_NAME, personalCaseDataCtscBilingual.getData().getSolsSOTName());
        personalisation.put(PERSONALISATION_SOLICITOR_REFERENCE, personalCaseDataCtscBilingual.getData().getSolsSolicitorAppReference());
        personalisation.put(PERSONALISATION_REGISTRY_NAME, "CTSC");
        personalisation.put(PERSONALISATION_REGISTRY_PHONE, "0300 303 0648");
        personalisation.put(PERSONALISATION_CASE_STOP_DETAILS_DEC,
            personalCaseDataCtscBilingual.getData().getBoStopDetailsDeclarationParagraph());
        personalisation.put(PERSONALISATION_CASE_STOP_DETAILS, personalCaseDataCtscBilingual.getData().getBoStopDetails());
        personalisation.put(PERSONALISATION_CAVEAT_CASE_ID, personalCaseDataCtscBilingual.getData().getBoCaseStopCaveatId());
        personalisation.put(PERSONALISATION_DECEASED_DOD, personalCaseDataCtscBilingual.getData().getDeceasedDateOfDeathFormatted());
        personalisation.put(PERSONALISATION_CCD_REFERENCE, personalCaseDataCtscBilingual.getId().toString());

        personalisation.put(PERSONALISATION_CAVEATOR_NAME, caveatStoppedCtscCaseData.getData().getCaveatorFullName());
        personalisation.put(PERSONALISATION_CAVEAT_ENTERED, "1st January 2019");
        personalisation.put(PERSONALISATION_CAVEATOR_ADDRESS, "");
        personalisation.put(PERSONALISATION_CAVEAT_EXPIRY_DATE, "1st January 2019");
        personalisation.put(PERSONALISATION_WELSH_DECEASED_DATE_OF_DEATH, localDateToWelshStringConverter.convert(personalCaseDataCtscBilingual.getData().getDeceasedDateOfDeath()));

        when(caveatQueryServiceMock.findCaveatById(CaseType.CAVEAT, null)).thenReturn(caveatStoppedCtscCaseData.getData());
        when(notificationClient.sendEmail(anyString(), anyString(), any(), any(), any())).thenReturn(sendEmailResponse);

        notificationService.sendEmail(CASE_STOPPED_CAVEAT, personalCaseDataCtscBilingual);

        verify(notificationClient).sendEmail(
            eq("pa-case-stopped-caveat-welsh"),
            eq("primary@probate-test.com"),
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
        personalisation.put(PERSONALISATION_CASE_STOP_DETAILS_DEC,
            solsCaseDataCtsc.getData().getBoStopDetailsDeclarationParagraph());
        personalisation.put(PERSONALISATION_CASE_STOP_DETAILS, solsCaseDataCtsc.getData().getBoStopDetails());
        personalisation.put(PERSONALISATION_CAVEAT_CASE_ID, solsCaseDataCtsc.getData().getBoCaseStopCaveatId());
        personalisation.put(PERSONALISATION_DECEASED_DOD, solsCaseDataCtsc.getData().getDeceasedDateOfDeathFormatted());
        personalisation.put(PERSONALISATION_CCD_REFERENCE, solsCaseDataCtsc.getId().toString());

        personalisation.put(PERSONALISATION_CAVEATOR_NAME, caveatStoppedCtscCaseData.getData().getCaveatorFullName());
        personalisation.put(PERSONALISATION_CAVEAT_ENTERED, "1st January 2019");
        personalisation.put(PERSONALISATION_CAVEATOR_ADDRESS, "");
        personalisation.put(PERSONALISATION_CAVEAT_EXPIRY_DATE, "1st January 2019");
        personalisation.put(PERSONALISATION_WELSH_DECEASED_DATE_OF_DEATH, localDateToWelshStringConverter.convert(solsCaseDataCtsc.getData().getDeceasedDateOfDeath()));

        when(caveatQueryServiceMock.findCaveatById(CaseType.CAVEAT, null)).thenReturn(caveatStoppedCtscCaseData.getData());
        when(notificationClient.sendEmail(anyString(), anyString(), any(), any(), any())).thenReturn(sendEmailResponse);

        notificationService.sendEmail(CASE_STOPPED_CAVEAT, solsCaseDataCtsc);

        verify(notificationClient).sendEmail(
            eq("sol-case-stopped-caveat"),
            eq("solicitor@probate-test.com"),
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
            eq("excela@probate-test.com"),
            any(),
            anyString());

        verify(pdfManagementService).generateAndUpload(any(SentEmail.class), eq(SENT_EMAIL));
    }

    @Test
    public void sendExcelaEmailScannedDocumentWithNoWillReference() throws NotificationClientException {
        notificationService.sendExcelaEmail(excelaCaseDataNoWillReference.build());

        verify(notificationClient).sendEmail(
            eq("pa-excela-data"),
            eq("excela@probate-test.com"),
            any(),
            anyString());

        verify(pdfManagementService).generateAndUpload(any(SentEmail.class), eq(SENT_EMAIL));
    }

    @Test
    public void sendExcelaEmailScannedDocumentWithNoSubtype() throws NotificationClientException {
        notificationService.sendExcelaEmail(excelaCaseDataNoSubtype.build());

        verify(notificationClient).sendEmail(
            eq("pa-excela-data"),
            eq("excela@probate-test.com"),
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
                .primaryApplicantEmailAddress("primary@probate-test.com")
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

        personalisation.put(PERSONALISATION_APPLICANT_NAME,
            personalCaseDataCtscRequestInformation.getData().getPrimaryApplicantFullName());
        personalisation.put(PERSONALISATION_DECEASED_NAME,
            personalCaseDataCtscRequestInformation.getData().getDeceasedFullName());
        personalisation.put(PERSONALISATION_SOLICITOR_NAME, personalCaseDataCtscRequestInformation.getData().getSolsSOTName());
        personalisation.put(PERSONALISATION_SOLICITOR_REFERENCE,
            personalCaseDataCtscRequestInformation.getData().getSolsSolicitorAppReference());
        personalisation.put(PERSONALISATION_REGISTRY_NAME, "CTSC");
        personalisation.put(PERSONALISATION_REGISTRY_PHONE, "0300 303 0648");
        personalisation.put(PERSONALISATION_CASE_STOP_DETAILS_DEC,
            personalCaseDataCtscRequestInformation.getData().getBoStopDetailsDeclarationParagraph());
        personalisation.put(PERSONALISATION_CASE_STOP_DETAILS,
            personalCaseDataCtscRequestInformation.getData().getBoStopDetails());
        personalisation.put(PERSONALISATION_CAVEAT_CASE_ID,
            personalCaseDataCtscRequestInformation.getData().getBoCaseStopCaveatId());
        personalisation.put(PERSONALISATION_DECEASED_DOD,
            personalCaseDataCtscRequestInformation.getData().getDeceasedDateOfDeathFormatted());
        personalisation.put(PERSONALISATION_CCD_REFERENCE, personalCaseDataCtscRequestInformation.getId().toString());
        personalisation.put(PERSONALISATION_WELSH_DECEASED_DATE_OF_DEATH, localDateToWelshStringConverter.convert(personalCaseDataCtscRequestInformation.getData().getDeceasedDateOfDeath()));

        when(notificationClient.sendEmail(anyString(), anyString(), any(), any(), any())).thenReturn(sendEmailResponse);
        ExecutorsApplyingNotification executorsApplyingNotification = ExecutorsApplyingNotification.builder()
            .name(personalCaseDataCtscRequestInformation.getData().getPrimaryApplicantFullName())
            .address(SolsAddress.builder()
                .addressLine1("Addressline1")
                .postCode("postcode")
                .postTown("posttown")
                .build())
            .email("primary@probate-test.com")
            .notification("Yes").build();
        notificationService.sendEmail(CASE_STOPPED_REQUEST_INFORMATION, personalCaseDataCtscRequestInformation,
            executorsApplyingNotification);
        verify(notificationClient).sendEmail(
            eq("pa-request-information"),
            eq("primary@probate-test.com"),
            eq(personalisation),
            eq(null));

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
        personalisation.put(PERSONALISATION_SOLICITOR_REFERENCE,
            solsCaseDataCtscRequestInformation.getData().getSolsSolicitorAppReference());
        personalisation.put(PERSONALISATION_REGISTRY_NAME, "CTSC");
        personalisation.put(PERSONALISATION_REGISTRY_PHONE, "0300 303 0648");
        personalisation.put(PERSONALISATION_CASE_STOP_DETAILS_DEC,
            solsCaseDataCtscRequestInformation.getData().getBoStopDetailsDeclarationParagraph());
        personalisation.put(PERSONALISATION_CASE_STOP_DETAILS, solsCaseDataCtscRequestInformation.getData().getBoStopDetails());
        personalisation.put(PERSONALISATION_CAVEAT_CASE_ID,
            solsCaseDataCtscRequestInformation.getData().getBoCaseStopCaveatId());
        personalisation.put(PERSONALISATION_DECEASED_DOD,
            solsCaseDataCtscRequestInformation.getData().getDeceasedDateOfDeathFormatted());
        personalisation.put(PERSONALISATION_CCD_REFERENCE, solsCaseDataCtscRequestInformation.getId().toString());
        personalisation.put(PERSONALISATION_WELSH_DECEASED_DATE_OF_DEATH, localDateToWelshStringConverter.convert(solsCaseDataCtscRequestInformation.getData().getDeceasedDateOfDeath()));

        when(notificationClient.sendEmail(anyString(), anyString(), any(), any(), any())).thenReturn(sendEmailResponse);

        ExecutorsApplyingNotification executorsApplyingNotification = ExecutorsApplyingNotification.builder()
            .name(solsCaseDataCtscRequestInformation.getData().getSolsSOTName())
            .address(SolsAddress.builder()
                .addressLine1("Addressline1")
                .postCode("postcode")
                .postTown("posttown")
                .build())
            .email("solicitor@probate-test.com")
            .notification("Yes").build();
        notificationService.sendEmail(CASE_STOPPED_REQUEST_INFORMATION, solsCaseDataCtscRequestInformation,
            executorsApplyingNotification);

        verify(notificationClient).sendEmail(
            eq("sols-request-information"),
            eq("solicitor@probate-test.com"),
            eq(personalisation),
            eq(null));

        when(pdfManagementService.generateDocmosisDocumentAndUpload(any(Map.class), any())).thenReturn(Document.builder()
            .documentFileName(SENT_EMAIL_FILE_NAME).build());
    }

    @Test
    public void shouldSendEmailWithDocumentAttachedRedeclaration() throws IOException, NotificationClientException {
        Map<String, Object> personalisation = new HashMap<>();
        CollectionMember<Document> doc = new CollectionMember<>(Document.builder().build());

        personalCaseDataCtsc.getData().getProbateSotDocumentsGenerated().add(doc);
        Map<String, String> sotValue = new HashMap<>();
        sotValue.put("file", "Fw==");

        personalisation.put(PERSONALISATION_CAVEAT_CASE_ID, personalCaseDataCtsc.getData().getBoCaseStopCaveatId());
        personalisation.put(PERSONALISATION_ADDRESSEE, personalCaseDataCtsc.getData().getPrimaryApplicantForenames());
        personalisation.put(PERSONALISATION_SOT_LINK, new JSONObject(sotValue));
        personalisation.put(PERSONALISATION_CASE_STOP_DETAILS, personalCaseDataCtsc.getData().getBoStopDetails());
        personalisation.put(PERSONALISATION_CCD_REFERENCE, personalCaseDataCtsc.getId().toString());
        personalisation.put(PERSONALISATION_DECEASED_DOD, personalCaseDataCtsc.getData().getDeceasedDateOfDeathFormatted());
        personalisation.put(PERSONALISATION_REGISTRY_PHONE, "0300 303 0648");
        personalisation.put(PERSONALISATION_SOLICITOR_NAME, personalCaseDataCtsc.getData().getSolsSolicitorFirmName());
        personalisation.put(PERSONALISATION_REGISTRY_NAME, "CTSC");
        personalisation.put(PERSONALISATION_CASE_STOP_DETAILS_DEC, personalCaseDataCtsc.getData().getBoStopDetailsDeclarationParagraph());
        personalisation.put(PERSONALISATION_APPLICANT_NAME, "null null");
        personalisation.put(PERSONALISATION_SOLICITOR_REFERENCE, personalCaseDataCtsc.getData().getSolsSolicitorAppReference());
        personalisation.put(PERSONALISATION_DECEASED_NAME, "null null");

        ExecutorsApplyingNotification executorsApplyingNotification = ExecutorsApplyingNotification.builder()
            .name(personalCaseDataCtsc.getData().getSolsSOTName())
            .address(SolsAddress.builder()
                .addressLine1("Addressline1")
                .postCode("postcode")
                .postTown("posttown")
                .build())
            .email("primary@probate-test.com")
            .notification("Yes").build();
        notificationService.sendEmailWithDocumentAttached(personalCaseDataCtsc, executorsApplyingNotification, REDECLARATION_SOT);

        verify(notificationClient).sendEmail(
            eq("pa-redeclaration-sot"),
            eq("primary@probate-test.com"),
            any(),
            eq(null));

    }

    @Test
    public void shouldSendEmailWithDocumentAttachedCaseStoppedRequestInfo() throws IOException, NotificationClientException {
        Map<String, Object> personalisation = new HashMap<>();
        CollectionMember<Document> doc = new CollectionMember<>(Document.builder().build());

        personalCaseDataCtsc.getData().getProbateSotDocumentsGenerated().add(doc);
        Map<String, String> sotValue = new HashMap<>();
        sotValue.put("file", "Fw==");

        personalisation.put(PERSONALISATION_CAVEAT_CASE_ID, personalCaseDataCtsc.getData().getBoCaseStopCaveatId());
        personalisation.put(PERSONALISATION_ADDRESSEE, personalCaseDataCtsc.getData().getPrimaryApplicantForenames());
        personalisation.put(PERSONALISATION_SOT_LINK, new JSONObject(sotValue));
        personalisation.put(PERSONALISATION_CASE_STOP_DETAILS, personalCaseDataCtsc.getData().getBoStopDetails());
        personalisation.put(PERSONALISATION_CCD_REFERENCE, personalCaseDataCtsc.getId().toString());
        personalisation.put(PERSONALISATION_DECEASED_DOD, personalCaseDataCtsc.getData().getDeceasedDateOfDeathFormatted());
        personalisation.put(PERSONALISATION_REGISTRY_PHONE, "0300 303 0648");
        personalisation.put(PERSONALISATION_SOLICITOR_NAME, personalCaseDataCtsc.getData().getSolsSolicitorFirmName());
        personalisation.put(PERSONALISATION_REGISTRY_NAME, "CTSC");
        personalisation.put(PERSONALISATION_CASE_STOP_DETAILS_DEC, personalCaseDataCtsc.getData().getBoStopDetailsDeclarationParagraph());
        personalisation.put(PERSONALISATION_APPLICANT_NAME, "null null");
        personalisation.put(PERSONALISATION_SOLICITOR_REFERENCE, personalCaseDataCtsc.getData().getSolsSolicitorAppReference());
        personalisation.put(PERSONALISATION_DECEASED_NAME, "null null");

        ExecutorsApplyingNotification executorsApplyingNotification = ExecutorsApplyingNotification.builder()
            .name(personalCaseDataCtsc.getData().getSolsSOTName())
            .address(SolsAddress.builder()
                .addressLine1("Addressline1")
                .postCode("postcode")
                .postTown("posttown")
                .build())
            .email("primary@probate-test.com")
            .notification("Yes").build();
        notificationService.sendEmailWithDocumentAttached(personalCaseDataCtsc,
            executorsApplyingNotification, CASE_STOPPED_REQUEST_INFORMATION);

        verify(notificationClient).sendEmail(
            eq("pa-request-information"),
            eq("primary@probate-test.com"),
            any(),
            eq(null));

    }

    @Test
    public void sendGeneralCaveatExtendEmail()
        throws NotificationClientException, BadRequestException {

        HashMap<String, String> personalisation = new HashMap<>();

        personalisation.put(PERSONALISATION_APPLICANT_NAME, caveatRaisedCaseData.getData().getCaveatorFullName());
        personalisation.put(PERSONALISATION_DECEASED_NAME, caveatRaisedCaseData.getData().getDeceasedFullName());
        personalisation.put(PERSONALISATION_CCD_REFERENCE, caveatRaisedCaseData.getId().toString());
        personalisation.put(PERSONALISATION_MESSAGE_CONTENT, caveatRaisedCaseData.getData().getMessageContent());
        personalisation.put(PERSONALISATION_REGISTRY_NAME, "Oxford Probate Registry");
        personalisation.put(PERSONALISATION_REGISTRY_PHONE, "0186 579 3055");
        personalisation.put(PERSONALISATION_CAVEAT_EXPIRY_DATE, "1st January 2019");
        personalisation.put(PERSONALISATION_WELSH_CAVEAT_EXPIRY_DATE, "1 Ionawr 2019");

        notificationService.sendCaveatEmail(CAVEAT_EXTEND, caveatRaisedCaseData);

        verify(notificationClient).sendEmail(
            eq("pa-ctsc-caveat-extend"),
            eq("caveator@probate-test.com"),
            eq(personalisation),
            eq("1"));

        verify(pdfManagementService).generateAndUpload(any(SentEmail.class), eq(SENT_EMAIL));
    }

    @Test
    public void sendGrantDelayedEmail()
        throws NotificationClientException, BadRequestException {

        HashMap<String, String> personalisation = new HashMap<>();

        personalisation.put(PERSONALISATION_CASE_STOP_DETAILS, personalGrantDelayedOxford.getData().getBoStopDetails());
        personalisation.put(PERSONALISATION_CCD_REFERENCE, personalGrantDelayedOxford.getId().toString());
        personalisation.put(PERSONALISATION_CAVEAT_CASE_ID, null);
        personalisation.put(PERSONALISATION_DECEASED_DOD, "12th December 2000");
        personalisation.put(PERSONALISATION_SOLICITOR_REFERENCE, null);
        personalisation.put(PERSONALISATION_REGISTRY_PHONE, "0186 579 3055");
        personalisation.put(PERSONALISATION_SOLICITOR_NAME, null);
        personalisation.put(PERSONALISATION_DECEASED_NAME, personalGrantDelayedOxford.getData().getDeceasedFullName());
        personalisation.put(PERSONALISATION_REGISTRY_NAME, "Oxford Probate Registry");
        personalisation.put(PERSONALISATION_WELSH_DECEASED_DATE_OF_DEATH, "12 Rhagfyr 2000");
        personalisation.put(PERSONALISATION_CASE_STOP_DETAILS_DEC, null);
        personalisation.put(PERSONALISATION_APPLICANT_NAME, personalGrantDelayedOxford.getData().getPrimaryApplicantFullName());

        ReturnedCaseDetails returnedCaseDetails = new ReturnedCaseDetails(personalGrantDelayedOxford.getData(), null, ID);

        when(pdfManagementService.generateAndUpload(any(SentEmail.class), any())).thenReturn(Document.builder()
            .documentFileName(SENT_EMAIL_FILE_NAME).build());

        Document document = notificationService.sendGrantDelayedEmail(returnedCaseDetails);

        assertEquals(SENT_EMAIL_FILE_NAME, document.getDocumentFileName());

        verify(notificationClient).sendEmail(
            eq("pa-grantDelayed"),
            eq("primary@probate-test.com"),
            eq(personalisation),
            eq(null));

        verify(pdfManagementService).generateAndUpload(any(SentEmail.class), eq(SENT_EMAIL));
    }

    @Test
    public void sendGrantDelayedAwaitingDocumentationEmail()
        throws NotificationClientException, BadRequestException {

        HashMap<String, String> personalisation = new HashMap<>();

        personalisation.put(PERSONALISATION_CASE_STOP_DETAILS, personalGrantDelayedOxford.getData().getBoStopDetails());
        personalisation.put(PERSONALISATION_CCD_REFERENCE, personalGrantDelayedOxford.getId().toString());
        personalisation.put(PERSONALISATION_CAVEAT_CASE_ID, null);
        personalisation.put(PERSONALISATION_DECEASED_DOD, "12th December 2000");
        personalisation.put(PERSONALISATION_SOLICITOR_REFERENCE, null);
        personalisation.put(PERSONALISATION_REGISTRY_PHONE, "0186 579 3055");
        personalisation.put(PERSONALISATION_SOLICITOR_NAME, null);
        personalisation.put(PERSONALISATION_DECEASED_NAME, personalGrantDelayedOxford.getData().getDeceasedFullName());
        personalisation.put(PERSONALISATION_REGISTRY_NAME, "Oxford Probate Registry");
        personalisation.put(PERSONALISATION_WELSH_DECEASED_DATE_OF_DEATH, "12 Rhagfyr 2000");
        personalisation.put(PERSONALISATION_CASE_STOP_DETAILS_DEC, null);
        personalisation.put(PERSONALISATION_APPLICANT_NAME, personalGrantDelayedOxford.getData().getPrimaryApplicantFullName());

        ReturnedCaseDetails returnedCaseDetails = new ReturnedCaseDetails(personalGrantDelayedOxford.getData(), null, ID);

        when(pdfManagementService.generateAndUpload(any(SentEmail.class), any())).thenReturn(Document.builder()
            .documentFileName(SENT_EMAIL_FILE_NAME).build());

        Document document = notificationService.sendGrantAwaitingDocumentationEmail(returnedCaseDetails);

        assertEquals(SENT_EMAIL_FILE_NAME, document.getDocumentFileName());

        verify(notificationClient).sendEmail(
            eq("pa-grantAwaitingDoc"),
            eq("primary@probate-test.com"),
            eq(personalisation),
            eq(null));

        verify(pdfManagementService).generateAndUpload(any(SentEmail.class), eq(SENT_EMAIL));
    }

    @Test
    public void shouldSetScheduledStartGrantDelayNotificationPeriod() {
        CaseDetails caseDetails =
            new CaseDetails(CaseData.builder()
                .caseType("gop")
                .applicationType(SOLICITOR)
                .primaryApplicantEmailAddress("")
                .registryLocation("Bristol")
                .evidenceHandled(Constants.NO)
                .build(),
                LAST_MODIFIED, CASE_ID);

        notificationService.startGrantDelayNotificationPeriod(caseDetails);
        assertEquals(LocalDate.now().plusDays(1), caseDetails.getData().getGrantDelayedNotificationDate());

    }

    @Test
    public void shouldNotSetScheduledStartGrantDelayNotificationPeriodWithNoEvidenceHandled() {
        CaseDetails caseDetails =
            new CaseDetails(CaseData.builder()
                .caseType("gop")
                .applicationType(SOLICITOR)
                .primaryApplicantEmailAddress("")
                .registryLocation("Bristol")
                .build(),
                LAST_MODIFIED, CASE_ID);

        notificationService.startGrantDelayNotificationPeriod(caseDetails);
        assertEquals(null, caseDetails.getData().getGrantDelayedNotificationDate());

    }

    @Test
    public void shouldNotSetScheduledStartGrantDelayNotificationPeriodWithEvidenceHandled() {
        CaseDetails caseDetails =
            new CaseDetails(CaseData.builder()
                .caseType("gop")
                .applicationType(SOLICITOR)
                .primaryApplicantEmailAddress("")
                .registryLocation("Bristol")
                .evidenceHandled(YES)
                .build(),
                LAST_MODIFIED, CASE_ID);

        notificationService.startGrantDelayNotificationPeriod(caseDetails);
        assertEquals(null, caseDetails.getData().getGrantDelayedNotificationDate());

    }

    @Test
    public void shouldNotSetScheduledStartGrantDelayNotificationPeriodWhenAlreadySet() {
        CaseDetails caseDetails =
            new CaseDetails(CaseData.builder()
                .caseType("gop")
                .applicationType(SOLICITOR)
                .primaryApplicantEmailAddress("")
                .registryLocation("Bristol")
                .evidenceHandled(Constants.NO)
                .grantDelayedNotificationDate(LocalDate.of(2020, 12, 31))
                .build(),
                LAST_MODIFIED, CASE_ID);

        notificationService.startGrantDelayNotificationPeriod(caseDetails);
        assertEquals(LocalDate.of(2020, 12, 31), caseDetails.getData().getGrantDelayedNotificationDate());
    }

    @Test
    public void shouldSetScheduledStartGrantAwaitingDocsNotificationPeriod() {
        CaseDetails caseDetails =
            new CaseDetails(CaseData.builder()
                .caseType("gop")
                .applicationType(SOLICITOR)
                .primaryApplicantEmailAddress("")
                .registryLocation("Bristol")
                .evidenceHandled(Constants.NO)
                .build(),
                LAST_MODIFIED, CASE_ID);

        notificationService.startAwaitingDocumentationNotificationPeriod(caseDetails);
        assertEquals(LocalDate.now().plusDays(1), caseDetails.getData().getGrantAwaitingDocumentationNotificationDate());

    }

    @Test
    public void shouldSetScheduledStartGrantAwaitingDocsNotificationPeriodForEmptyScannedDocs() {
        CaseDetails caseDetails =
            new CaseDetails(CaseData.builder()
                .caseType("gop")
                .applicationType(SOLICITOR)
                .primaryApplicantEmailAddress("")
                .registryLocation("Bristol")
                .evidenceHandled(Constants.NO)
                .scannedDocuments(new ArrayList())
                .build(),
                LAST_MODIFIED, CASE_ID);

        notificationService.startAwaitingDocumentationNotificationPeriod(caseDetails);
        assertEquals(LocalDate.now().plusDays(1), caseDetails.getData().getGrantAwaitingDocumentationNotificationDate());

    }

    @Test
    public void shouldNotSetScheduledStartGrantAwaitingDocsNotificationPeriodForCaseWithScannedDocs() {
        CollectionMember<ScannedDocument> collectionMember = new CollectionMember<>(null, ScannedDocument.builder().build());
        CaseDetails caseDetails =
            new CaseDetails(CaseData.builder()
                .caseType("gop")
                .applicationType(SOLICITOR)
                .primaryApplicantEmailAddress("")
                .registryLocation("Bristol")
                .evidenceHandled(Constants.NO)
                .scannedDocuments(Arrays.asList(collectionMember))
                .build(),
                LAST_MODIFIED, CASE_ID);

        notificationService.startAwaitingDocumentationNotificationPeriod(caseDetails);
        assertEquals(null, caseDetails.getData().getGrantAwaitingDocumentationNotificationDate());

    }

    @Test
    public void shouldResetScheduledStartGrantAwaitingDocsNotificationPeriod() {
        CaseDetails caseDetails =
            new CaseDetails(CaseData.builder()
                .caseType("gop")
                .applicationType(SOLICITOR)
                .primaryApplicantEmailAddress("")
                .registryLocation("Bristol")
                .evidenceHandled(Constants.NO)
                .grantAwaitingDocumentationNotificationDate(LocalDate.now())
                .build(),
                LAST_MODIFIED, CASE_ID);

        notificationService.resetAwaitingDocumentationNotificationDate(caseDetails);
        assertEquals(null, caseDetails.getData().getGrantAwaitingDocumentationNotificationDate());

    }

    @Test
    public void shouldDefaultRegistryLocationIfNotSet() {

        Registry registry = notificationService.getRegistry(null, LanguagePreference.ENGLISH);
        assertEquals("CTSC", registry.getName());

        Registry registryWelsh = notificationService.getRegistry(null, LanguagePreference.WELSH);
        assertEquals( "Probate Registry of Wales", registryWelsh.getName());

        Registry registryPassedIn = notificationService.getRegistry(RegistryLocation.MANCHESTER.getName(),
            LanguagePreference.WELSH);
        assertEquals("Manchester Probate Registry", registryPassedIn.getName());

    }
}
