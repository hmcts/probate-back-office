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
import uk.gov.service.notify.NotificationClientException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.probate.model.DocumentType.SENT_EMAIL;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CaveatControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

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
                .with(csrf())
                .content(caveatPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")));
    }

    @Test
    public void personalGeneralCaveatMessageShouldReturnDataPayloadOkResponseCode() throws Exception {

        String caveatPayload = testUtils.getStringFromFile("caveatPayloadNotifications.json");

        mockMvc.perform(post("/caveat/general-message")
                .with(csrf())
                .content(caveatPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")));
    }
}
