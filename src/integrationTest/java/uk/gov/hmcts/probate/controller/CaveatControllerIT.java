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
import uk.gov.hmcts.probate.model.payments.pba.OrganisationEntityResponse;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.hmcts.probate.service.organisations.OrganisationsRetrievalService;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.probate.transformer.CaveatCallbackResponseTransformer;
import uk.gov.hmcts.probate.util.TestUtils;
import uk.gov.service.notify.NotificationClientException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestUtils testUtils;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private PDFManagementService pdfManagementService;

    private CaveatCallbackResponseTransformer caveatCallbackResponseTransformer;

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
                .value("There is no email address for this caveator. Add an email address or contact them by post."))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

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
                .value("There is no email address for this caveator. Add an email address or contact them by post."))
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
}
