package uk.gov.hmcts.probate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.hmcts.probate.service.PrepareNocService;
import uk.gov.hmcts.reform.ccd.client.model.CallbackRequest;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class NoticeOfChangeControllerIT {

    private static final String APPLY_DECISION = "/noc/apply-decision";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String AUTH_TOKEN = "Bearer someAuthorizationToken";
    @MockBean
    private PrepareNocService prepareNocService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    Map<String, Object> caseDataMap;
    CaseDetails caseDetails;
    CallbackRequest callbackRequest;

    @BeforeEach
    public void setup() {
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
    }

    @Test
    void shouldPrepareCaseForNoc() throws Exception {
        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(APPLY_DECISION).content(json).header("Authorization", AUTH_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON));
    }
}

