package uk.gov.hmcts.probate.service.notification;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.probate.config.properties.registries.RegistriesProperties;
import uk.gov.hmcts.probate.config.properties.registries.Registry;
import uk.gov.hmcts.probate.insights.AppInsights;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.ApplicationType.PERSONAL;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GrantOfRepresentationPersonalisationServiceTest {

    @InjectMocks
    private GrantOfRepresentationPersonalisationService grantOfRepresentationPersonalisationService;

    @MockBean
    private PDFManagementService pdfManagementService;

    @MockBean
    private CoreCaseDataApi coreCaseDataApi;

    @MockBean
    private CaveatQueryService caveatQueryServiceMock;

    @MockBean
    private AppInsights appInsights;

    @MockBean
    private SendEmailResponse sendEmailResponse;

    @Mock
    private RegistriesProperties registriesPropertiesMock;

    @Mock
    private LocalDateToWelshStringConverter localDateToWelshStringConverter;

    private CaseDetails caseDetails;
    private ReturnedCaseDetails returnedCaseDetails;

    private List<ReturnedCaseDetails> excelaCaseData = new ArrayList<>();
    private List<ReturnedCaseDetails> excelaCaseDataNoWillReference = new ArrayList<>();
    private List<ReturnedCaseDetails> excelaCaseDataNoSubtype = new ArrayList<>();

    private static final Long ID = 1L;
    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private static final DateTimeFormatter EXCELA_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");

    private static final String PERSONALISATION_APPLICANT_NAME = "applicant_name";
    private static final String PERSONALISATION_DECEASED_NAME = "deceased_name";
    private static final String PERSONALISATION_SOLICITOR_NAME = "solicitor_name";
    private static final String PERSONALISATION_SOLICITOR_REFERENCE = "solicitor_reference";
    private static final String PERSONALISATION_REGISTRY_NAME = "registry_name";
    private static final String PERSONALISATION_REGISTRY_PHONE = "registry_phone";
    private static final String PERSONALISATION_CASE_STOP_DETAILS = "case-stop-details";
    private static final String PERSONALISATION_CASE_STOP_DETAILS_DEC = "boStopDetailsDeclarationParagraph";
    private static final String PERSONALISATION_CAVEAT_CASE_ID = "caveat_case_id";
    private static final String PERSONALISATION_DECEASED_DOD = "deceased_dod";
    private static final String PERSONALISATION_CCD_REFERENCE = "ccd_reference";
    private static final String PERSONALISATION_EXCELA_NAME = "excelaName";
    private static final String PERSONALISATION_CASE_DATA = "caseData";
    private static final String PERSONALISATION_ADDRESSEE = "addressee";
    private static final String PERSONALISATION_WELSH_DECEASED_DATE_OF_DEATH = "welsh_deceased_date_of_death";

    Registry registry = new Registry();

    @Before
    public void setUp() {

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


        registry.setPhone("1234567890");
        registry.setName("CTSC");

        CaseData caseData = CaseData.builder()
                .primaryApplicantForenames("first name")
                .primaryApplicantSurname("surname")
                .deceasedSurname("deceased surname")
                .deceasedForenames("deceased forenames")
                .solsSOTName("sols sot name")
                .solsSolicitorAppReference("app reference")
                .boStopDetails("stop details")
                .boCaseStopCaveatId("123456789012345678")
                .deceasedDateOfDeath(LocalDate.now())
                .boStopDetailsDeclarationParagraph("Yes")
                .build();

        caseDetails = new CaseDetails(caseData, LAST_MODIFIED, ID);
        returnedCaseDetails = new ReturnedCaseDetails(caseData, LAST_MODIFIED, ID);

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

    }

    @Test
    public void getPersonalisationContentIsOk() {
        String welshDeceaseDateOfDeath = "27 Mai 2019";
        when(localDateToWelshStringConverter.convert(isA(LocalDate.class))).thenReturn(welshDeceaseDateOfDeath);
        Map<String, Object> response = grantOfRepresentationPersonalisationService.getPersonalisation(caseDetails,
                registry);

        assertEquals("first name surname", response.get(PERSONALISATION_APPLICANT_NAME));
        assertEquals("deceased forenames deceased surname", response.get(PERSONALISATION_DECEASED_NAME));
        assertEquals("app reference", response.get(PERSONALISATION_SOLICITOR_REFERENCE));
        assertEquals("sols sot name", response.get(PERSONALISATION_SOLICITOR_NAME));
        assertEquals("stop details", response.get(PERSONALISATION_CASE_STOP_DETAILS));
        assertEquals("123456789012345678", response.get(PERSONALISATION_CAVEAT_CASE_ID));
        assertEquals(caseDetails.getData().getDeceasedDateOfDeathFormatted(), response.get(PERSONALISATION_DECEASED_DOD));
        assertEquals("Yes", response.get(PERSONALISATION_CASE_STOP_DETAILS_DEC));
        assertEquals("CTSC", response.get(PERSONALISATION_REGISTRY_NAME));
        assertEquals("1234567890", response.get(PERSONALISATION_REGISTRY_PHONE));
        assertEquals(caseDetails.getId().toString(), response.get(PERSONALISATION_CCD_REFERENCE));
        assertEquals(welshDeceaseDateOfDeath, response.get(PERSONALISATION_WELSH_DECEASED_DATE_OF_DEATH));
    }

    @Test
    public void getPersonalisationContentIsOkFromCaseData() {
        String welshDeceaseDateOfDeath = "27 Mai 2019";
        when(localDateToWelshStringConverter.convert(isA(LocalDate.class))).thenReturn(welshDeceaseDateOfDeath);
        Map<String, Object> response = grantOfRepresentationPersonalisationService.getPersonalisation(returnedCaseDetails,
            registry);

        assertEquals("first name surname", response.get(PERSONALISATION_APPLICANT_NAME));
        assertEquals("deceased forenames deceased surname", response.get(PERSONALISATION_DECEASED_NAME));
        assertEquals("app reference", response.get(PERSONALISATION_SOLICITOR_REFERENCE));
        assertEquals("sols sot name", response.get(PERSONALISATION_SOLICITOR_NAME));
        assertEquals("stop details", response.get(PERSONALISATION_CASE_STOP_DETAILS));
        assertEquals("123456789012345678", response.get(PERSONALISATION_CAVEAT_CASE_ID));
        assertEquals(caseDetails.getData().getDeceasedDateOfDeathFormatted(), response.get(PERSONALISATION_DECEASED_DOD));
        assertEquals("Yes", response.get(PERSONALISATION_CASE_STOP_DETAILS_DEC));
        assertEquals("CTSC", response.get(PERSONALISATION_REGISTRY_NAME));
        assertEquals("1234567890", response.get(PERSONALISATION_REGISTRY_PHONE));
        assertEquals(caseDetails.getId().toString(), response.get(PERSONALISATION_CCD_REFERENCE));
        assertEquals(welshDeceaseDateOfDeath, response.get(PERSONALISATION_WELSH_DECEASED_DATE_OF_DEATH));
    }

    @Test
    public void getExcelaPersonalisationContentIsOk() {
        Map<String, String> response =
                grantOfRepresentationPersonalisationService.getExcelaPersonalisation(excelaCaseData);

        assertEquals(LocalDateTime.now().format(EXCELA_DATE) + "will", response.get(PERSONALISATION_EXCELA_NAME));
        assertEquals("123456, Jack, Michelson, 01/01/2019, 01/05/2019, 1\n", response.get(PERSONALISATION_CASE_DATA));
    }

    @Test
    public void getExcelaPersonalisationContentIsOkNoWillReference() {
        Map<String, String> response =
                grantOfRepresentationPersonalisationService.getExcelaPersonalisation(excelaCaseDataNoWillReference);

        assertEquals(LocalDateTime.now().format(EXCELA_DATE) + "will", response.get(PERSONALISATION_EXCELA_NAME));
        assertEquals(", Jack, Michelson, 01/01/2019, 01/05/2019, 1\n", response.get(PERSONALISATION_CASE_DATA));
    }


    @Test
    public void getExcelaPersonalisationContentIsOkNoSubType() {
        Map<String, String> response =
                grantOfRepresentationPersonalisationService.getExcelaPersonalisation(excelaCaseDataNoSubtype);

        assertEquals(LocalDateTime.now().format(EXCELA_DATE) + "will", response.get(PERSONALISATION_EXCELA_NAME));
        assertEquals(", Jack, Michelson, 01/01/2019, 01/05/2019, 1\n", response.get(PERSONALISATION_CASE_DATA));
    }

    @Test
    public void getAddSingleAddressee() {
        Map<String, Object> currentMap = new HashMap<>();
        String addressee = "addressee name";

        Map<String, Object> response =
                grantOfRepresentationPersonalisationService.addSingleAddressee(currentMap, addressee);

        assertEquals(addressee, response.get(PERSONALISATION_ADDRESSEE));
    }


}
