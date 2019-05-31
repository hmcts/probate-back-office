package uk.gov.hmcts.probate.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.insights.AppInsights;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.probate.util.TestUtils;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;
import uk.gov.service.notify.NotificationClientException;

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

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CaveatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestUtils testUtils;

    @MockBean
    private AppInsights appInsights;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private PDFManagementService pdfManagementService;

    @MockBean
    private CoreCaseDataApi coreCaseDataApi;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() throws NotificationClientException, BadRequestException {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        Document document = Document.builder().documentType(SENT_EMAIL).build();

        doReturn(document).when(notificationService).sendCaveatEmail(any(), any());

        when(pdfManagementService.generateAndUpload(any(CallbackRequest.class), eq(SENT_EMAIL)))
                .thenReturn(Document.builder().documentType(SENT_EMAIL).build());
    }

    @Test
    public void caveatRaisedShouldReturnDataPayloadOkResponseCode() throws Exception {

        String caveatPayload = testUtils.getStringFromFile("caveatPayloadNotifications.json");

        mockMvc.perform(post("/caveat/raise")
                .content(caveatPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")));
    }

    @Test
    public void personalCaveatRaisedNoEmailShouldReturnDataPayloadOkResponseCode() throws Exception {
        String personalPayload = testUtils.getStringFromFile("caveatPayloadNotificationsNoEmail.json");

        mockMvc.perform(post("/caveat/raise")
                .content(personalPayload)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors[0]")
                        .value("There is no email address for this caveator. Add an email address or contact them by post."))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));

    }

    @Test
    public void personalCaveatRaisedNoValidResponseFromBulkPrintReturnDataPayloadOkResponseCode() throws Exception {
        String personalPayload = testUtils.getStringFromFile("caveatPayloadNotificationsBulkPrint.json");

        mockMvc.perform(post("/caveat/raise")
                .content(personalPayload)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors[0]")
                        .value("Bulk Print is currently unavailable please contact support desk."))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));

    }

    @Test
    public void personalGeneralCaveatMessageShouldReturnDataPayloadOkResponseCode() throws Exception {

        String caveatPayload = testUtils.getStringFromFile("caveatPayloadNotifications.json");

        mockMvc.perform(post("/caveat/general-message")
                .content(caveatPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")));
    }

    @Test
    public void personalGeneralCaveatMessageNoEmailShouldReturnDataPayloadOkResponseCode() throws Exception {
        String personalPayload = testUtils.getStringFromFile("caveatPayloadNotificationsNoEmail.json");

        mockMvc.perform(post("/caveat/general-message")
                .content(personalPayload)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors[0]")
                        .value("There is no email address for this caveator. Add an email address or contact them by post."))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));

    }

    @Test
    public void caveatDefaultValuesShouldReturnDataPayloadOkResponseCode() throws Exception {

        String caveatPayload = testUtils.getStringFromFile("caveatPayloadNotifications.json");

        mockMvc.perform(post("/caveat/defaultValues")
                .content(caveatPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")));
    }
}
