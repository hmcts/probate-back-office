package uk.gov.hmcts.probate.service.notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.config.properties.registries.RegistriesProperties;
import uk.gov.hmcts.probate.config.properties.registries.Registry;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.ReturnedCaveatDetails;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;
import uk.gov.hmcts.probate.service.CaveatQueryService;
import uk.gov.hmcts.probate.service.template.pdf.LocalDateToWelshStringConverter;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;
import uk.gov.service.notify.SendEmailResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.ApplicationType.PERSONAL;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class GrantOfRepresentationPersonalisationServiceIT {

    private static final Long ID = 1L;
    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private static final LocalDateTime LAST_DATE_MODIFIED = LocalDateTime.now(ZoneOffset.UTC).minusYears(2);
    private static final DateTimeFormatter EXELA_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final String PERSONALISATION_APPLICANT_NAME = "applicant_name";
    private static final String PERSONALISATION_DECEASED_NAME = "deceased_name";
    private static final String PERSONALISATION_SOLICITOR_NAME = "solicitor_name";
    private static final String PERSONALISATION_SOLICITOR_SOT_FORENAMES = "solicitor_sot_forenames";
    private static final String PERSONALISATION_SOLICITOR_SOT_SURNAME = "solicitor_sot_surname";
    private static final String PERSONALISATION_SOLICITOR_REFERENCE = "solicitor_reference";
    private static final String PERSONALISATION_REGISTRY_NAME = "registry_name";
    private static final String PERSONALISATION_REGISTRY_PHONE = "registry_phone";
    private static final String PERSONALISATION_CASE_STOP_DETAILS = "case-stop-details";
    private static final String PERSONALISATION_CASE_STOP_DETAILS_DEC = "boStopDetailsDeclarationParagraph";
    private static final String PERSONALISATION_CAVEAT_CASE_ID = "caveat_case_id";
    private static final String PERSONALISATION_DECEASED_DOD = "deceased_dod";
    private static final String PERSONALISATION_CCD_REFERENCE = "ccd_reference";
    private static final String PERSONALISATION_EXELA_NAME = "exelaName";
    private static final String PERSONALISATION_CASE_DATA = "caseData";
    private static final String PERSONALISATION_ADDRESSEE = "addressee";
    private static final String PERSONALISATION_WELSH_DECEASED_DATE_OF_DEATH = "welsh_deceased_date_of_death";
    private static final String PERSONALISATION_NOC_SUBMITTED_DATE = "noc_date";
    private static final String PERSONALISATION_DRAFT_NAME = "draftName";
    private static final String PERSONALISATION_CASE_TYPE = "caseType";

    private static final DateTimeFormatter NOC_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    Registry registry = new Registry();
    @InjectMocks
    private GrantOfRepresentationPersonalisationService grantOfRepresentationPersonalisationService;
    @MockBean
    private PDFManagementService pdfManagementService;
    @MockBean
    private CoreCaseDataApi coreCaseDataApi;
    @MockBean
    private CaveatQueryService caveatQueryServiceMock;
    @MockBean
    private SendEmailResponse sendEmailResponse;
    @Mock
    private RegistriesProperties registriesPropertiesMock;
    @Mock
    private LocalDateToWelshStringConverter localDateToWelshStringConverter;
    private CaseDetails caseDetails;
    private ReturnedCaseDetails returnedCaseDetails;
    private List<ReturnedCaseDetails> exelaCaseData = new ArrayList<>();
    private List<ReturnedCaseDetails> exelaCaseDataWithCommas = new ArrayList<>();
    private List<ReturnedCaseDetails> exelaCaseDataTypeWill = new ArrayList<>();
    private List<ReturnedCaseDetails> exelaCaseDataNoWillReference = new ArrayList<>();
    private List<ReturnedCaseDetails> exelaCaseDataNoSubtype = new ArrayList<>();
    private List<ReturnedCaseDetails> exelaCaseDataNoDOB = new ArrayList<>();

    @BeforeEach
    public void setUp() {

        CollectionMember<ScannedDocument> scannedDocument = new CollectionMember<>(ScannedDocument
            .builder().type("other").subtype("will").controlNumber("123456").build());
        List<CollectionMember<ScannedDocument>> scannedDocuments = new ArrayList<>(1);
        scannedDocuments.add(scannedDocument);

        CollectionMember<ScannedDocument> scannedDocumentTypeWill = new CollectionMember<>(ScannedDocument
                .builder().type("will").subtype("Original Will").controlNumber("123456").build());
        List<CollectionMember<ScannedDocument>> scannedDocumentsTypeWill = new ArrayList<>(1);
        scannedDocumentsTypeWill.add(scannedDocumentTypeWill);

        CollectionMember<ScannedDocument> scannedDocumentsNoWillReference = new CollectionMember<>(ScannedDocument
            .builder().type("other").subtype("subtype").build());
        List<CollectionMember<ScannedDocument>> scannedDocumentsNoWill = new ArrayList<>(1);
        scannedDocumentsNoWill.add(scannedDocumentsNoWillReference);

        CollectionMember<ScannedDocument> scannedDocumensNoSubtype = new CollectionMember<>(ScannedDocument
            .builder().type("other").subtype(null).build());
        List<CollectionMember<ScannedDocument>> scannedDocumentsNoSubtype = new ArrayList<>(1);
        scannedDocumentsNoSubtype.add(scannedDocumensNoSubtype);


        registry.setPhone("1234567890");
        registry.setName("CTSC");

        CaseData caseData = CaseData.builder()
            .primaryApplicantForenames("first name")
            .primaryApplicantSurname("surname")
            .deceasedSurname("deceased surname")
            .deceasedForenames("deceased forenames")
            .solsSOTName("sols sot name")
            .solsSOTForenames("sols sot forenames")
            .solsSOTSurname("sols sot surname")
            .solsSolicitorAppReference("app reference")
            .boStopDetails("stop details")
            .boCaseStopCaveatId("123456789012345678")
            .deceasedDateOfDeath(LocalDate.now())
            .boStopDetailsDeclarationParagraph("Yes")
            .build();

        caseDetails = new CaseDetails(caseData, LAST_MODIFIED, ID);
        returnedCaseDetails = new ReturnedCaseDetails(caseData, LAST_DATE_MODIFIED, ID);

        exelaCaseData.add(new ReturnedCaseDetails(CaseData.builder()
            .applicationType(PERSONAL)
            .deceasedForenames("Jack")
            .deceasedSurname("Michelson")
            .grantIssuedDate("2019-05-01")
            .deceasedDateOfBirth(LocalDate.of(2019, 1, 1))
            .scannedDocuments(scannedDocuments)
            .registryLocation("Cardiff")
            .build(), LAST_DATE_MODIFIED, ID));

        exelaCaseDataWithCommas.add(new ReturnedCaseDetails(CaseData.builder()
            .applicationType(PERSONAL)
            .deceasedForenames("Jack,Henry")
            .deceasedSurname("Michelson, Howard")
            .grantIssuedDate("2019-05-01")
            .deceasedDateOfBirth(LocalDate.of(2019, 1, 1))
            .scannedDocuments(scannedDocuments)
            .registryLocation("Cardiff")
            .build(), LAST_DATE_MODIFIED, ID));

        exelaCaseDataTypeWill.add(new ReturnedCaseDetails(CaseData.builder()
            .applicationType(PERSONAL)
            .deceasedForenames("Jack")
            .deceasedSurname("Michelson")
            .grantIssuedDate("2019-05-01")
            .deceasedDateOfBirth(LocalDate.of(2019, 1, 1))
            .scannedDocuments(scannedDocumentsTypeWill)
            .registryLocation("Cardiff")
            .build(), LAST_DATE_MODIFIED, ID));

        exelaCaseDataNoWillReference.add(new ReturnedCaseDetails(CaseData.builder()
            .applicationType(PERSONAL)
            .deceasedForenames("Jack")
            .deceasedSurname("Michelson")
            .grantIssuedDate("2019-05-01")
            .deceasedDateOfBirth(LocalDate.of(2019, 1, 1))
            .scannedDocuments(scannedDocumentsNoWill)
            .registryLocation("Cardiff")
            .build(), LAST_DATE_MODIFIED, ID));

        exelaCaseDataNoSubtype.add(new ReturnedCaseDetails(CaseData.builder()
            .applicationType(PERSONAL)
            .deceasedForenames("Jack")
            .deceasedSurname("Michelson")
            .grantIssuedDate("2019-05-01")
            .deceasedDateOfBirth(LocalDate.of(2019, 1, 1))
            .scannedDocuments(scannedDocumentsNoSubtype)
            .registryLocation("Cardiff")
            .build(), LAST_DATE_MODIFIED, ID));

        exelaCaseDataNoDOB.add(new ReturnedCaseDetails(CaseData.builder()
            .applicationType(PERSONAL)
            .deceasedForenames("Jack")
            .deceasedSurname("Michelson")
            .grantIssuedDate("2019-05-01")
            .scannedDocuments(scannedDocumentsNoSubtype)
            .registryLocation("Cardiff")
            .build(), LAST_DATE_MODIFIED, ID));

    }

    @Test
    void getPersonalisationContentIsOk() {
        String welshDeceaseDateOfDeath = "27 Mai 2019";
        when(localDateToWelshStringConverter.convert(isA(LocalDate.class))).thenReturn(welshDeceaseDateOfDeath);
        Map<String, Object> response = grantOfRepresentationPersonalisationService.getPersonalisation(caseDetails,
            registry);

        assertEquals("first name surname", response.get(PERSONALISATION_APPLICANT_NAME));
        assertEquals("deceased forenames deceased surname", response.get(PERSONALISATION_DECEASED_NAME));
        assertEquals("app reference", response.get(PERSONALISATION_SOLICITOR_REFERENCE));
        assertEquals("sols sot name", response.get(PERSONALISATION_SOLICITOR_NAME));
        assertEquals("sols sot forenames", response.get(PERSONALISATION_SOLICITOR_SOT_FORENAMES));
        assertEquals("sols sot surname", response.get(PERSONALISATION_SOLICITOR_SOT_SURNAME));
        assertEquals("stop details", response.get(PERSONALISATION_CASE_STOP_DETAILS));
        assertEquals("123456789012345678", response.get(PERSONALISATION_CAVEAT_CASE_ID));
        assertEquals(caseDetails.getData().getDeceasedDateOfDeathFormatted(),
            response.get(PERSONALISATION_DECEASED_DOD));
        assertEquals("Yes", response.get(PERSONALISATION_CASE_STOP_DETAILS_DEC));
        assertEquals("CTSC", response.get(PERSONALISATION_REGISTRY_NAME));
        assertEquals("1234567890", response.get(PERSONALISATION_REGISTRY_PHONE));
        assertEquals(caseDetails.getId().toString(), response.get(PERSONALISATION_CCD_REFERENCE));
        assertEquals(welshDeceaseDateOfDeath, response.get(PERSONALISATION_WELSH_DECEASED_DATE_OF_DEATH));
    }

    @Test
    void getPersonalisationContentIsOkFromCaseData() {
        String welshDeceaseDateOfDeath = "27 Mai 2019";
        when(localDateToWelshStringConverter.convert(isA(LocalDate.class))).thenReturn(welshDeceaseDateOfDeath);
        Map<String, Object> response =
            grantOfRepresentationPersonalisationService.getPersonalisation(returnedCaseDetails,
                registry);

        assertEquals("first name surname", response.get(PERSONALISATION_APPLICANT_NAME));
        assertEquals("deceased forenames deceased surname", response.get(PERSONALISATION_DECEASED_NAME));
        assertEquals("app reference", response.get(PERSONALISATION_SOLICITOR_REFERENCE));
        assertEquals("sols sot name", response.get(PERSONALISATION_SOLICITOR_NAME));
        assertEquals("sols sot forenames", response.get(PERSONALISATION_SOLICITOR_SOT_FORENAMES));
        assertEquals("sols sot surname", response.get(PERSONALISATION_SOLICITOR_SOT_SURNAME));
        assertEquals("stop details", response.get(PERSONALISATION_CASE_STOP_DETAILS));
        assertEquals("123456789012345678", response.get(PERSONALISATION_CAVEAT_CASE_ID));
        assertEquals(caseDetails.getData().getDeceasedDateOfDeathFormatted(),
            response.get(PERSONALISATION_DECEASED_DOD));
        assertEquals("Yes", response.get(PERSONALISATION_CASE_STOP_DETAILS_DEC));
        assertEquals("CTSC", response.get(PERSONALISATION_REGISTRY_NAME));
        assertEquals("1234567890", response.get(PERSONALISATION_REGISTRY_PHONE));
        assertEquals(caseDetails.getId().toString(), response.get(PERSONALISATION_CCD_REFERENCE));
        assertEquals(welshDeceaseDateOfDeath, response.get(PERSONALISATION_WELSH_DECEASED_DATE_OF_DEATH));
    }

    @Test
    void getExelaPersonalisationContentIsOk() {
        Map<String, String> response =
            grantOfRepresentationPersonalisationService.getExelaPersonalisation(exelaCaseData);

        assertEquals(LocalDateTime.now().format(EXELA_DATE) + "will", response.get(PERSONALISATION_EXELA_NAME));
        assertEquals("123456, Jack, Michelson, 01/01/2019, 01/05/2019, 1, Cardiff\n",
            response.get(PERSONALISATION_CASE_DATA));
    }

    @Test
    void getExelaPersonalisationContentIsOkWithCommas() {
        Map<String, String> response =
            grantOfRepresentationPersonalisationService.getExelaPersonalisation(exelaCaseDataWithCommas);

        assertEquals(LocalDateTime.now().format(EXELA_DATE) + "will", response.get(PERSONALISATION_EXELA_NAME));
        assertEquals("123456, Jack Henry, Michelson  Howard, 01/01/2019, 01/05/2019, 1, Cardiff\n",
            response.get(PERSONALISATION_CASE_DATA));
    }

    @Test
    void getExelaPersonalisationContentIsOkWithTypeWill() {
        Map<String, String> response =
                grantOfRepresentationPersonalisationService.getExelaPersonalisation(exelaCaseDataTypeWill);

        assertEquals(LocalDateTime.now().format(EXELA_DATE) + "will", response.get(PERSONALISATION_EXELA_NAME));
        assertEquals("123456, Jack, Michelson, 01/01/2019, 01/05/2019, 1, Cardiff\n",
                response.get(PERSONALISATION_CASE_DATA));
    }

    @Test
    void getExelaPersonalisationContentIsOkNoWillReference() {
        Map<String, String> response =
            grantOfRepresentationPersonalisationService.getExelaPersonalisation(exelaCaseDataNoWillReference);

        assertEquals(LocalDateTime.now().format(EXELA_DATE) + "will", response.get(PERSONALISATION_EXELA_NAME));
        assertEquals(", Jack, Michelson, 01/01/2019, 01/05/2019, 1, Cardiff\n",
            response.get(PERSONALISATION_CASE_DATA));
    }

    @Test
    void getNocPersonalisationContentIsOkNoWillReference() {
        Map<String, Object> response =
                grantOfRepresentationPersonalisationService.getNocPersonalisation(ID, "First Last",
                        "Deceased DeceasedL");

        assertEquals(LocalDateTime.now().format(NOC_DATE), response.get(PERSONALISATION_NOC_SUBMITTED_DATE));
        assertEquals(ID.toString(), response.get(PERSONALISATION_CCD_REFERENCE));
    }

    @Test
    void getExelaPersonalisationContentIsOkNoSubType() {
        Map<String, String> response =
            grantOfRepresentationPersonalisationService.getExelaPersonalisation(exelaCaseDataNoSubtype);

        assertEquals(LocalDateTime.now().format(EXELA_DATE) + "will", response.get(PERSONALISATION_EXELA_NAME));
        assertEquals(", Jack, Michelson, 01/01/2019, 01/05/2019, 1, Cardiff\n",
            response.get(PERSONALISATION_CASE_DATA));
    }

    @Test
    void getExelaPersonalisationContentWithExceptionInData() {
        Map<String, String> response =
            grantOfRepresentationPersonalisationService.getExelaPersonalisation(exelaCaseDataNoDOB);

        assertEquals(LocalDateTime.now().format(EXELA_DATE) + "will", response.get(PERSONALISATION_EXELA_NAME));
        assertEquals(", Jack, Michelson, 1, java.lang.NullPointerException: temporal\n",
            response.get(PERSONALISATION_CASE_DATA));
    }

    @Test
    void getAddSingleAddressee() {
        Map<String, Object> currentMap = new HashMap<>();
        String addressee = "addressee name";

        Map<String, Object> response =
            grantOfRepresentationPersonalisationService.addSingleAddressee(currentMap, addressee);

        assertEquals(addressee, response.get(PERSONALISATION_ADDRESSEE));
    }

    @Test
    void getGORDraftCasePersonalisationContentIsOk() {
        List<ReturnedCaseDetails> cases = List.of(new ReturnedCaseDetails(CaseData.builder()
                .applicationType(PERSONAL)
                .deceasedForenames("Jack")
                .deceasedSurname("Michelson")
                .build(), LAST_DATE_MODIFIED, ID));
        Map<String, Object> response =
                grantOfRepresentationPersonalisationService.getGORDraftCaseWithPaymentPersonalisation(cases,
                        "01/01/2025", "01/05/2025");

        assertEquals("Draft cases with payment success extract from 01/01/2025 to 01/05/2025",
                response.get(PERSONALISATION_DRAFT_NAME));
        assertEquals("1, Jack, Michelson\n",
                response.get(PERSONALISATION_CASE_DATA));
        assertEquals("Grant of Representation",
                response.get(PERSONALISATION_CASE_TYPE));
    }

    @Test
    void getCaveatDraftCasePersonalisationContentIsOk() {
        List<ReturnedCaveatDetails> cases = List.of(new ReturnedCaveatDetails(CaveatData.builder()
                .applicationType(PERSONAL)
                .deceasedForenames("Jack")
                .deceasedSurname("Michelson")
                .build(), null, ID));
        Map<String, Object> response =
                grantOfRepresentationPersonalisationService.getCaveatDraftCaseWithPaymentPersonalisation(cases,
                        "01/01/2025", "01/05/2025");

        assertEquals("Draft cases with payment success extract from 01/01/2025 to 01/05/2025",
                response.get(PERSONALISATION_DRAFT_NAME));
        assertEquals("1, Jack, Michelson\n",
                response.get(PERSONALISATION_CASE_DATA));
        assertEquals("Caveat",
                response.get(PERSONALISATION_CASE_TYPE));
    }
}
