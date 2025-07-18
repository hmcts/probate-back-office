package uk.gov.hmcts.probate.service.notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.config.properties.registries.RegistriesProperties;
import uk.gov.hmcts.probate.config.properties.registries.Registry;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class CaveatPersonalisationServiceIT {

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
    private static final String PERSONALISATION_WELSH_DATE_OF_DEATH = "deceased_date_of_death_welsh";
    private static final String PERSONALISATION_DATE_OF_DEATH = "deceased_date_of_death";

    Registry registry = new Registry();
    HashMap<String, Object> personalisation = new HashMap<>();
    @Autowired
    private CaveatPersonalisationService caveatPersonalisationService;
    @MockitoBean
    private PDFManagementService pdfManagementService;
    @MockitoBean
    private CoreCaseDataApi coreCaseDataApi;
    @MockitoBean
    private CaveatQueryService caveatQueryServiceMock;
    @MockitoBean
    private SendEmailResponse sendEmailResponse;
    @Mock
    private RegistriesProperties registriesPropertiesMock;
    @Mock
    private DateFormatterService dateFormatterService;
    private CaseDetails caseDetails;
    private CaveatDetails caveatDetails;
    private CaveatDetails caveatDetailsDobNull;
    private CaveatDetails solsCaveatDetailsDobNull;
    private CaveatDetails solsCaveatDetails;
    private CaseData caseDataPersonal;
    private CaseData caseDataSolicitor;
    private CaveatData returnedCaveatData;

    @BeforeEach
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
                .deceasedDateOfDeath(LocalDate.parse("2000-10-10"))
                .deceasedDateOfBirth(LocalDate.parse("1900-10-10"))
                .build();

        CaveatData caveatDataDobNull = CaveatData.builder()
                .caveatorForenames("cav first name")
                .caveatorSurname("cav surname")
                .deceasedForenames("forename")
                .deceasedSurname("surname")
                .messageContent("message content")
                .expiryDate(LocalDate.parse("2000-10-10"))
                .deceasedDateOfDeath(LocalDate.parse("2000-10-10"))
                .deceasedDateOfBirth(null)
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
        caveatDetailsDobNull = new CaveatDetails(caveatDataDobNull, LAST_MODIFIED, ID);

        CaveatData solsCaveatData = CaveatData.builder()
            .applicationType(ApplicationType.SOLICITOR)
            .caveatorForenames("first name")
            .caveatorSurname("surname")
            .deceasedForenames("deceased forenames")
            .deceasedSurname("deceased surname")
            .solsSolicitorAppReference("app reference")
            .deceasedDateOfDeath(LocalDate.now())
            .expiryDate(LocalDate.parse("2000-10-10"))
            .deceasedDateOfDeath(LocalDate.parse("2000-10-10"))
            .deceasedDateOfBirth(LocalDate.parse("1900-10-10"))
            .build();
        solsCaveatDetails = new CaveatDetails(solsCaveatData, LAST_MODIFIED, ID);

        when(dateFormatterService.formatCaveatExpiryDate(any())).thenReturn(PERSONALISATION_CAVEAT_EXPIRY_DATE);

    }

    @Test
    void getPersonalisationContentIsOk() {
        Map<String, String> response = caveatPersonalisationService.getCaveatPersonalisation(caveatDetails, registry);

        assertEquals("cav first name cav surname", response.get(PERSONALISATION_APPLICANT_NAME));
        assertEquals("forename surname", response.get(PERSONALISATION_DECEASED_NAME));
        assertEquals(caveatDetails.getId().toString(), response.get(PERSONALISATION_CCD_REFERENCE));
        assertEquals("message content", response.get(PERSONALISATION_MESSAGE_CONTENT));
        assertEquals("CTSC", response.get(PERSONALISATION_REGISTRY_NAME));
        assertEquals("1234567890", response.get(PERSONALISATION_REGISTRY_PHONE));
        assertEquals("10th October 2000", response.get(PERSONALISATION_CAVEAT_EXPIRY_DATE));
        assertEquals("10 Hydref 2000", response.get(PERSONALISATION_WELSH_CAVEAT_EXPIRY_DATE));
        assertEquals("10th October 2000", response.get(PERSONALISATION_DATE_OF_DEATH));
    }

    @Test
    void getPersonalisationContentIsOkDOBNull() {
        Map<String, String> response = caveatPersonalisationService
            .getCaveatPersonalisation(caveatDetailsDobNull, registry);

        assertEquals("cav first name cav surname", response.get(PERSONALISATION_APPLICANT_NAME));
        assertEquals("forename surname", response.get(PERSONALISATION_DECEASED_NAME));
        assertEquals(caveatDetails.getId().toString(), response.get(PERSONALISATION_CCD_REFERENCE));
        assertEquals("message content", response.get(PERSONALISATION_MESSAGE_CONTENT));
        assertEquals("CTSC", response.get(PERSONALISATION_REGISTRY_NAME));
        assertEquals("1234567890", response.get(PERSONALISATION_REGISTRY_PHONE));
        assertEquals("10th October 2000", response.get(PERSONALISATION_CAVEAT_EXPIRY_DATE));
        assertEquals("10 Hydref 2000", response.get(PERSONALISATION_WELSH_CAVEAT_EXPIRY_DATE));
        assertEquals("10 Hydref 2000", response.get(PERSONALISATION_WELSH_DATE_OF_DEATH));
        assertEquals("10th October 2000", response.get(PERSONALISATION_DATE_OF_DEATH));
    }

    @Test
    void getSolsCaveatsPersonalisationIsOk() {
        Map<String, String> response =
            caveatPersonalisationService.getSolsCaveatPersonalisation(solsCaveatDetails, registry);

        assertEquals("deceased forenames deceased surname", response.get(PERSONALISATION_DECEASED_NAME));
        assertEquals(caveatDetails.getId().toString(), response.get(PERSONALISATION_CCD_REFERENCE));
        assertEquals("app reference", response.get(PERSONALISATION_SOLICITOR_REFERENCE));
        assertEquals("CTSC", response.get(PERSONALISATION_REGISTRY_NAME));
        assertEquals("1234567890", response.get(PERSONALISATION_REGISTRY_PHONE));
        assertEquals("10th October 2000", response.get(PERSONALISATION_CAVEAT_EXPIRY_DATE));
        assertEquals("10 Hydref 2000", response.get(PERSONALISATION_WELSH_CAVEAT_EXPIRY_DATE));
    }


    @Test
    void getCaveatStopPersonalisationContentIsOk() {
        when(caveatQueryServiceMock.findCaveatById(CaseType.CAVEAT, caseDataPersonal.getBoCaseStopCaveatId()))
            .thenReturn(returnedCaveatData);

        Map<String, Object> response = caveatPersonalisationService.getCaveatStopPersonalisation(personalisation,
            caseDataPersonal);

        assertEquals("10th October 2000", response.get(PERSONALISATION_DATE_CAVEAT_ENTERED));
        assertEquals("cav first name cav surname", response.get(PERSONALISATION_CAVEATOR_NAME));
        assertEquals("10th October 2000", response.get(PERSONALISATION_CAVEAT_EXPIRY_DATE));
    }

    @Test
    void getCaveatStopPersonalisationContentSolsIsOk() {
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
