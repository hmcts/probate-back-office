package uk.gov.hmcts.probate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.hmcts.probate.model.ccd.caveat.response.CaveatCallbackResponse;
import uk.gov.hmcts.probate.model.ccd.caveat.response.ResponseCaveatData;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.hmcts.probate.service.PrepareNocService;
import uk.gov.hmcts.probate.service.PrepareNocCaveatService;
import uk.gov.hmcts.probate.transformer.CaveatCallbackResponseTransformer;
import uk.gov.hmcts.probate.util.TestUtils;
import uk.gov.hmcts.reform.ccd.client.model.CallbackRequest;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.service.notify.NotificationClientException;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.any;
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
class NoticeOfChangeControllerIT {

    private static final String APPLY_DECISION = "/noc/apply-decision";
    private static final String APPLY_DECISION_CAVEAT = "/noc/caveat-apply-decision";
    private static final String NOC_EMAIL = "/noc/caveat-noc-notification";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String AUTH_TOKEN = "Bearer someAuthorizationToken";
    @MockitoBean
    private PrepareNocService prepareNocService;
    @MockitoBean
    private PrepareNocCaveatService prepareNocCaveatService;
    @MockitoBean
    private CaveatCallbackResponseTransformer caveatCallbackResponseTransformer;
    @MockitoBean
    private  NotificationService notificationService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private TestUtils testUtils;
    Map<String, Object> caseDataMap;
    CaseDetails caseDetails;
    CallbackRequest callbackRequest;
    private CaveatCallbackResponse successfulResponse;

    @BeforeEach
    public void setup() throws NotificationClientException {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        caseDataMap = new HashMap<>();
        caseDetails = CaseDetails.builder()
                .id(12345678L)
                .data(caseDataMap)
                .build();
        callbackRequest = CallbackRequest.builder()
                .caseDetails(caseDetails)
                .build();
        Document document = Document.builder().documentType(SENT_EMAIL).build();

        doReturn(document).when(notificationService).sendCaveatNocEmail(any(), any());
        successfulResponse = CaveatCallbackResponse.builder()
                .caveatData(ResponseCaveatData.builder().deceasedForenames("Bob").build()).build();
        when(caveatCallbackResponseTransformer.addNocDocuments(any(), any())).thenReturn(successfulResponse);
    }

    @Test
    void shouldApplyDecision() throws Exception {
        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(APPLY_DECISION).content(json).header("Authorization", AUTH_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldApplyDecisionCaveat() throws Exception {
        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(APPLY_DECISION_CAVEAT).content(json).header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldReturnSuccessfulResponseForNoc() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile("solicitorValidateNocCaveatPayload.json");

        mockMvc.perform(post(NOC_EMAIL)
                        .content(solicitorPayload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")));
    }

    @Test
    void shouldReturnUnSuccessfulResponseForNocEmail() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile("solicitorValidateCaveatPayload.json");

        mockMvc.perform(post(NOC_EMAIL)
                        .content(solicitorPayload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors[0]")
                        .value("There is no email address for this solicitor. Add an email address or "
                                + "contact them by post."))
                .andExpect(jsonPath("$.errors[1]")
                        .value("Nid oes cyfeiriad e-bost ar gyfer y cyfreithiwr hwn. "
                                + "Ychwanegwch gyfeiriad e-bost neu cysylltwch â nhw drwy'r post."))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}

