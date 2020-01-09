package uk.gov.hmcts.probate.service.notification;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.probate.config.properties.registries.RegistriesProperties;
import uk.gov.hmcts.probate.config.properties.registries.Registry;
import uk.gov.hmcts.probate.insights.AppInsights;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.CaseType;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.CaveatQueryService;
import uk.gov.hmcts.probate.service.DateFormatterService;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;
import uk.gov.service.notify.SendEmailResponse;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CaveatPersonalisationServiceTest {

    @Autowired
    private CaveatPersonalisationService caveatPersonalisationService;

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
    private DateFormatterService dateFormatterService;

    private CaseDetails caseDetails;

    private CaveatDetails caveatDetails;

    private CaveatDetails solsCaveatDetails;

    Registry registry = new Registry();

    private static final Long ID = 1L;
    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};

    private static final String PERSONALISATION_APPLICANT_NAME = "applicant_name";
    private static final String PERSONALISATION_SOLICITOR_REFERENCE = "solicitor_reference";
    private static final String PERSONALISATION_DECEASED_NAME = "deceased_name";
    private static final String PERSONALISATION_REGISTRY_NAME = "registry_name";
    private static final String PERSONALISATION_REGISTRY_PHONE = "registry_phone";
    private static final String PERSONALISATION_CCD_REFERENCE = "ccd_reference";
    private static final String PERSONALISATION_CAVEAT_EXPIRY_DATE = "caveat_expiry_date";
    private static final String PERSONALISATION_WELSH_CAVEAT_EXPIRY_DATE = "welsh_caveat_expiry_date";
    private static final String PERSONALISATION_MESSAGE_CONTENT = "message_content";
    private static final String PERSONALISATION_DATE_CAVEAT_ENTERED = "date_caveat_entered";
    private static final String PERSONALISATION_CAVEATOR_NAME = "caveator_name";

    HashMap<String, Object> personalisation = new HashMap<>();
    private CaseData caseDataPersonal;
    private CaseData caseDataSolicitor;
    private CaveatData returnedCaveatData;

    @Before
    public void setUp() {
        registry.setPhone("1234567890");
        registry.setName("CTSC");

        caseDataPersonal = CaseData.builder()
                .applicationType(ApplicationType.PERSONAL)
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

        caseDataSolicitor = CaseData.builder()
                .applicationType(ApplicationType.SOLICITOR)
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
                .solsSOTName("SotName")
                .build();

        caseDetails = new CaseDetails(caseDataPersonal, LAST_MODIFIED, ID);

        CaveatData caveatData = CaveatData.builder()
                .caveatorForenames("cav first name")
                .caveatorSurname("cav surname")
                .deceasedForenames("forename")
                .deceasedSurname("surname")
                .messageContent("message content")
                .expiryDate(LocalDate.parse("2000-10-10"))
                .build();

        CaveatData solsCaveatData = CaveatData.builder()
                .applicationType(ApplicationType.SOLICITOR)
                .caveatorForenames("first name")
                .caveatorSurname("surname")
                .deceasedForenames("deceased forenames")
                .deceasedSurname("deceased surname")
                .solsSolicitorAppReference("app reference")
                .deceasedDateOfDeath(LocalDate.now())
                .expiryDate(LocalDate.parse("2000-10-10"))
                .build();

        returnedCaveatData = CaveatData.builder()
                .applicationSubmittedDate(LocalDate.parse("2000-10-10"))
                .caveatorForenames("cav first name")
                .caveatorSurname("cav surname")
                .deceasedForenames("forename")
                .deceasedSurname("surname")
                .messageContent("message content")
                .expiryDate(LocalDate.parse("2000-10-10"))
                .build();

        caveatDetails = new CaveatDetails(caveatData, LAST_MODIFIED, ID);
        solsCaveatDetails = new CaveatDetails(solsCaveatData, LAST_MODIFIED, ID);

        when(dateFormatterService.formatCaveatExpiryDate(any())).thenReturn(PERSONALISATION_CAVEAT_EXPIRY_DATE);

    }

    @Test
    public void getPersonalisationContentIsOk() {
        Map<String, String> response = caveatPersonalisationService.getCaveatPersonalisation(caveatDetails, registry);

        assertEquals("cav first name cav surname", response.get(PERSONALISATION_APPLICANT_NAME));
        assertEquals("forename surname", response.get(PERSONALISATION_DECEASED_NAME));
        assertEquals(caveatDetails.getId().toString(), response.get(PERSONALISATION_CCD_REFERENCE));
        assertEquals("message content", response.get(PERSONALISATION_MESSAGE_CONTENT));
        assertEquals("CTSC", response.get(PERSONALISATION_REGISTRY_NAME));
        assertEquals("1234567890", response.get(PERSONALISATION_REGISTRY_PHONE));
        assertEquals("10th October 2000", response.get(PERSONALISATION_CAVEAT_EXPIRY_DATE));
        assertEquals("10 Hydref 2000", response.get(PERSONALISATION_WELSH_CAVEAT_EXPIRY_DATE));
    }

    @Test
    public void getSolsCaveatsPersonalisationIsOk() {
        Map<String, String> response = caveatPersonalisationService.getSolsCaveatPersonalisation(solsCaveatDetails, registry);

        assertEquals("deceased forenames deceased surname", response.get(PERSONALISATION_DECEASED_NAME));
        assertEquals(caveatDetails.getId().toString(), response.get(PERSONALISATION_CCD_REFERENCE));
        assertEquals("app reference", response.get(PERSONALISATION_SOLICITOR_REFERENCE));
        assertEquals("CTSC", response.get(PERSONALISATION_REGISTRY_NAME));
        assertEquals("1234567890", response.get(PERSONALISATION_REGISTRY_PHONE));
        assertEquals("10th October 2000", response.get(PERSONALISATION_CAVEAT_EXPIRY_DATE));
        assertEquals("10 Hydref 2000", response.get(PERSONALISATION_WELSH_CAVEAT_EXPIRY_DATE));
    }


    @Test
    public void getCaveatStopPersonalisationContentIsOk() {
        when(caveatQueryServiceMock.findCaveatById(CaseType.CAVEAT, caseDataPersonal.getBoCaseStopCaveatId()))
                .thenReturn(returnedCaveatData);

        Map<String, Object> response = caveatPersonalisationService.getCaveatStopPersonalisation(personalisation,
                caseDataPersonal);

        assertEquals("10th October 2000", response.get(PERSONALISATION_DATE_CAVEAT_ENTERED));
        assertEquals("cav first name cav surname", response.get(PERSONALISATION_CAVEATOR_NAME));
        assertEquals("10th October 2000", response.get(PERSONALISATION_CAVEAT_EXPIRY_DATE));
    }

    @Test
    public void getCaveatStopPersonalisationContentSolsIsOk() {
        personalisation.put(PERSONALISATION_APPLICANT_NAME, "name");
        when(caveatQueryServiceMock.findCaveatById(CaseType.CAVEAT, caseDataSolicitor.getBoCaseStopCaveatId()))
                .thenReturn(returnedCaveatData);

        Map<String, Object> response = caveatPersonalisationService.getCaveatStopPersonalisation(personalisation,
                caseDataSolicitor);

        assertEquals("10th October 2000", response.get(PERSONALISATION_DATE_CAVEAT_ENTERED));
        assertEquals("cav first name cav surname", response.get(PERSONALISATION_CAVEATOR_NAME));
        assertEquals("10th October 2000", response.get(PERSONALISATION_CAVEAT_EXPIRY_DATE));
        assertEquals("SotName", response.get(PERSONALISATION_APPLICANT_NAME));
    }


}
