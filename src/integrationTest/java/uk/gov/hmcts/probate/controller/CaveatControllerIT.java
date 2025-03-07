package uk.gov.hmcts.probate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.AuditEvent;
import uk.gov.hmcts.probate.model.fee.FeesResponse;
import uk.gov.hmcts.probate.model.payments.pba.OrganisationEntityResponse;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.hmcts.probate.service.RegistrarDirectionService;
import uk.gov.hmcts.probate.service.ccd.AuditEventService;
import uk.gov.hmcts.probate.service.fee.FeeService;
import uk.gov.hmcts.probate.service.organisations.OrganisationsRetrievalService;
import uk.gov.hmcts.probate.service.payments.PaymentsService;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.probate.transformer.ServiceRequestTransformer;
import uk.gov.hmcts.probate.util.TestUtils;
import uk.gov.service.notify.NotificationClientException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.probate.model.DocumentType.SENT_EMAIL;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class CaveatControllerIT {
    private static final String AUTH_TOKEN = "Bearer someAuthorizationToken";
    private static final String DEFAULT_REGISTRARS_DECISION = "/caveat/default-registrars-decision";
    private static final String REGISTRARS_DECISION = "/caveat/registrars-decision";
    private static final String SETUP_FOR_REMOVAL = "/caveat/setup-for-permanent-removal";
    private static final String DELETE_REMOVED = "/caveat/permanently-delete-removed";
    private static final String ROLLBACK = "/caveat/rollback";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestUtils testUtils;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private PDFManagementService pdfManagementService;

    @MockBean
    private RegistrarDirectionService registrarDirectionService;
    @MockBean
    private FeeService feeService;
    @MockBean
    private FeesResponse feesResponseMock;
    @MockBean
    private PaymentsService paymentsService;
    @MockBean
    private ServiceRequestTransformer serviceRequestTransformer;
    @MockBean
    private SecurityUtils securityUtils;
    @MockBean
    private AuditEventService auditEventService;

    @Autowired
    private WebApplicationContext webApplicationContext;


    @SpyBean
    OrganisationsRetrievalService organisationsRetrievalService;

    @BeforeEach
    public void setUp() throws NotificationClientException, BadRequestException {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        Document document = Document.builder().documentType(SENT_EMAIL).build();

        doReturn(document).when(notificationService).sendCaveatEmail(any(), any());

        when(pdfManagementService.generateAndUpload(any(CallbackRequest.class), eq(SENT_EMAIL)))
            .thenReturn(Document.builder().documentType(SENT_EMAIL).build());
        OrganisationEntityResponse organisationEntityResponse = new OrganisationEntityResponse();
        organisationEntityResponse.setOrganisationIdentifier("ORG_ID");
        organisationEntityResponse.setName("ORGANISATION_NAME");
        doReturn(organisationEntityResponse).when(organisationsRetrievalService).getOrganisationEntity(
                "1234567890123456", AUTH_TOKEN);
        when(feeService.getAllFeesData(any(), any(), any())).thenReturn(feesResponseMock);

    }

    @Test
    void solsCaveatCreated_ShouldReturnDataPayload_OkResponseCode() throws Exception {

        String caveatPayload = testUtils.getStringFromFile("solicitorCreateCaveatPayloadWithOrgPolicy.json");

        mockMvc.perform(post("/caveat/solsCreate")
            .header("Authorization", AUTH_TOKEN)
            .content(caveatPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("data")));
    }

    @Test
    void solsCaveatCreatedForOrganisation_ShouldReturnDataPayload_OkResponseCode() throws Exception {

        String caveatPayload = testUtils.getStringFromFile("solicitorCreateCaveatPayloadWithOrgPolicy.json");

        mockMvc.perform(post("/caveat/sols-created")
                        .header("Authorization", AUTH_TOKEN)
                        .content(caveatPayload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")));
    }

    @Test
    void solsCaveatCreated_ShouldReturnError_400ResponseCode() throws Exception {
        String personalPayload = testUtils.getStringFromFile("solsCaveatPayloadNoEmail.json");

        mockMvc.perform(post("/caveat/solsCreate")
            .content(personalPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().is4xxClientError());
    }

    @Test
    void solsCaveatUpdated_ShouldReturnDataPayload_OkResponseCode() throws Exception {

        String caveatPayload = testUtils.getStringFromFile("solicitorUpdateCaveatPayload.json");

        mockMvc.perform(post("/caveat/solsUpdate")
            .header("Authorization", AUTH_TOKEN)
            .content(caveatPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("data")));
    }

    @Test
    void solsCaveatConfirmation_ShouldReturnDataPayload_OkResponseCode() throws Exception {

        String caveatPayload = testUtils.getStringFromFile("solicitorValidateCaveatPayload.json");

        mockMvc.perform(post("/caveat/confirmation")
            .content(caveatPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void caveatRaisedShouldReturnDataPayloadOkResponseCode() throws Exception {

        String caveatPayload = testUtils.getStringFromFile("caveatPayloadNotifications.json");

        mockMvc.perform(post("/caveat/raise")
            .content(caveatPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("data")));
    }

    @Test
    void personalCaveatRaisedNoEmailShouldReturnDataPayloadOkResponseCode() throws Exception {
        String personalPayload = testUtils.getStringFromFile("caveatPayloadNotificationsNoEmail.json");

        mockMvc.perform(post("/caveat/raise")
            .content(personalPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.errors[0]")
                .value("There is no email address for this caveator. Add an email address or contact them "
                        + "by post."))
            .andExpect(jsonPath("$.errors[1]")
                .value("Nid oes cyfeiriad e-bost ar gyfer yr cafeatydd hwn. Ychwanegwch gyfeiriad e-bost "
                                + "neu cysylltwch â nhw drwy'r post."))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    }

    @Test
    void raiseCaveatValidateShouldReturnDataPayloadOkResponseCode() throws Exception {

        String caveatPayload = testUtils.getStringFromFile("caveatPayloadNotifications.json");

        mockMvc.perform(post("/caveat/raise-caveat-validate")
                        .content(caveatPayload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")));
    }

    @Test
    void raiseCaveatValidateShouldThrowException() throws Exception {

        String caveatPayload = testUtils.getStringFromFile("caveatPayloadNotifications.json");
        DateTimeFormatter caveatDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate futureDoD = LocalDate.now().plusDays(1);
        caveatPayload = caveatPayload.replace("2017-12-31", caveatDateFormatter.format(futureDoD));

        mockMvc.perform(post("/caveat/raise-caveat-validate").content(caveatPayload)
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.errors[0]")
                        .value("Date of death cannot be in the future"));
    }

    @Test
    void personalCaveatRaisedNoValidResponseFromBulkPrintReturnDataPayloadOkResponseCode() throws Exception {

        String personalPayload = testUtils.getStringFromFile("caveatPayloadNotificationsBulkPrint.json");

        mockMvc.perform(post("/caveat/raise")
            .content(personalPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.errors[0]")
                .value("Bulk Print is currently unavailable please contact support desk."))
            .andExpect(jsonPath("$.errors[1]")
                        .value("Nid yw Argraffu Swmp ar gael ar hyn o bryd, cysylltwch â'r ddesg "
                                + "gymorth."))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    }

    @Test
    void personalGeneralCaveatMessageShouldReturnDataPayloadOkResponseCode() throws Exception {

        String caveatPayload = testUtils.getStringFromFile("caveatPayloadNotifications.json");

        mockMvc.perform(post("/caveat/general-message")
            .content(caveatPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("data")));
    }

    @Test
    void personalGeneralCaveatMessageNoEmailShouldReturnDataPayloadOkResponseCode() throws Exception {
        String personalPayload = testUtils.getStringFromFile("caveatPayloadNotificationsNoEmail.json");

        mockMvc.perform(post("/caveat/general-message")
            .content(personalPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.errors[0]")
                    .value("There is no email address for this caveator. Add an email address or contact them "
                        + "by post."))
            .andExpect(jsonPath("$.errors[1]")
                    .value("Nid oes cyfeiriad e-bost ar gyfer yr cafeatydd hwn. Ychwanegwch gyfeiriad e-bost "
                                + "neu cysylltwch â nhw drwy'r post."))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    }

    @Test
    void caveatDefaultValuesShouldReturnDataPayloadOkResponseCode() throws Exception {

        String caveatPayload = testUtils.getStringFromFile("caveatPayloadNotifications.json");

        mockMvc.perform(post("/caveat/defaultValues")
            .content(caveatPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("data")));
    }

    @Test
    void shouldCaveatExpiryValidateExtend() throws Exception {

        String caveatPayload = testUtils.getStringFromFile("caveatPayloadNotifications.json");
        DateTimeFormatter caveatExpiryDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate newExpired = LocalDate.now().plusDays(1);
        caveatPayload = caveatPayload.replace("2019-05-15", caveatExpiryDateFormatter.format(newExpired));

        mockMvc.perform(post("/caveat/validate-extend")
            .content(caveatPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("data")));
    }

    @Test
    void shouldCaveatExpiryValidateExtendErrorsAlreadyExpired() throws Exception {

        String caveatPayload = testUtils.getStringFromFile("caveatPayloadNotifications.json");
        DateTimeFormatter caveatExpiryDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate newExpired = LocalDate.now().minusDays(1);
        caveatPayload = caveatPayload.replace("2019-05-15", caveatExpiryDateFormatter.format(newExpired));

        mockMvc.perform(post("/caveat/validate-extend")
            .content(caveatPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.errors[0]")
                .value("Cannot extend an already expired caveat."))
                .andExpect(jsonPath("$.errors[1]")
                        .value("Ni ellir ymestyn cafeat sydd eisoes wedi dod i ben."))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldCaveatExpiryValidateExtendErrorsMoreThan1MonthRemaining() throws Exception {

        String caveatPayload = testUtils.getStringFromFile("caveatPayloadNotifications.json");
        DateTimeFormatter caveatExpiryDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate newExpired = LocalDate.now().plusMonths(1).plusDays(1);
        caveatPayload = caveatPayload.replace("2019-05-15", caveatExpiryDateFormatter.format(newExpired));

        mockMvc.perform(post("/caveat/validate-extend")
            .content(caveatPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.errors[0]")
                .value("Cannot extend a caveat that is more than 1 month from expiry."))
                .andExpect(jsonPath("$.errors[1]")
                        .value("Ni ellir ymestyn cafeat sy'n fwy na 1 mis o?r dyddiad dod i ben."))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldCaveatExpiryExtend() throws Exception {

        String caveatPayload = testUtils.getStringFromFile("caveatPayloadNotifications.json");

        mockMvc.perform(post("/caveat/extend")
            .content(caveatPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void shouldCaveatWithdraw() throws Exception {

        String caveatPayload = testUtils.getStringFromFile("caveatPayloadNotifications.json");

        mockMvc.perform(post("/caveat/withdraw")
            .content(caveatPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void shouldDefaultRegistrarsDecision() throws Exception {
        String caveatPayload = testUtils.getStringFromFile("caveatPayloadNotifications.json");
        mockMvc.perform(post(DEFAULT_REGISTRARS_DECISION).content(caveatPayload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRegistrarsDecision() throws Exception {
        String caveatPayload = testUtils.getStringFromFile("caveatPayloadNotifications.json");
        mockMvc.perform(post(REGISTRARS_DECISION).content(caveatPayload).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldSetupForPermanentRemovalCaveat() throws Exception {
        String caveatPayload = testUtils.getStringFromFile("caveatPayloadNotifications.json");
        mockMvc.perform(post(SETUP_FOR_REMOVAL).content(caveatPayload).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteRemovedDocuments() throws Exception {
        String caveatPayload = testUtils.getStringFromFile("caveatDocumentsPayloadNotifications.json");
        mockMvc.perform(post(DELETE_REMOVED).content(caveatPayload).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRollback() throws Exception {
        SecurityDTO securityDTO = SecurityDTO.builder()
                .serviceAuthorisation("serviceToken")
                .authorisation("userToken")
                .userId("id")
                .build();
        when(securityUtils.getSecurityDTO()).thenReturn(securityDTO);
        when(auditEventService.getLatestAuditEventByName(any(), any(), any(), any()))
                .thenReturn(Optional.ofNullable(AuditEvent.builder()
                        .stateId("SolsAppUpdated")
                        .createdDate(LocalDateTime.now())
                        .build()));
        String caveatPayload = testUtils.getStringFromFile("caveatPayloadNotificationsOrgPolicy.json");
        mockMvc.perform(post(ROLLBACK).content(caveatPayload).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
