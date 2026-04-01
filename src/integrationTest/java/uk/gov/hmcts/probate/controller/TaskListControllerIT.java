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
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.user.UserInfoService;
import uk.gov.hmcts.probate.transformer.CaseDataTransformer;
import uk.gov.hmcts.probate.util.TestUtils;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;
import uk.gov.hmcts.reform.probate.model.idam.UserInfo;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.probate.model.Constants.NO;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class TaskListControllerIT {
    private static final String AUTH_HEADER = "Authorization";
    private static final String AUTH_TOKEN = "Bearer someAuthorizationToken";
    private static final Optional<UserInfo> CASEWORKER_USERINFO = Optional.ofNullable(UserInfo.builder()
            .familyName("familyName")
            .givenName("givenname")
            .roles(Arrays.asList("caseworker-probate"))
            .build());

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestUtils testUtils;

    @MockitoBean
    private CoreCaseDataApi coreCaseDataApi;

    @MockitoBean
    private CaseDataTransformer caseDataTransformer;

    @MockitoBean
    private UserInfoService userInfoService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private CaseData.CaseDataBuilder caseDataBuilder;
    private static final Long ID = 1234567890123456L;
    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private static final String CASE_CLOSED_STATE = "BOCaseClosed";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @BeforeEach
    public void setup() {
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        doReturn(CASEWORKER_USERINFO).when(userInfoService).getCaseworkerInfo();
    }

    @Test
    void taskListUpdateShouldReturnDataPayloadOkResponseCode() throws Exception {

        String taskListPayload = testUtils.getStringFromFile("standingSearchPayload.json");

        mockMvc.perform(post("/tasklist/update")
                .header(AUTH_HEADER, AUTH_TOKEN)
                .content(taskListPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")));
    }

    @Test
    void taskListUpdateCasePrintedShouldTransformEvidenceHandled() throws Exception {

        String taskListPayload = testUtils.getStringFromFile("standingSearchPayload.json");

        mockMvc.perform(post("/tasklist/updateCasePrinted")
                        .header(AUTH_HEADER, AUTH_TOKEN)
                        .content(taskListPayload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")));
        verify(caseDataTransformer).transformCaseDataForEvidenceHandled(any());
        verify(caseDataTransformer).transformIhtFormCaseDataByDeceasedDOD(any());
        verify(caseDataTransformer).setApplicationSubmittedDateForPA(any());
    }

    @Test
    void taskListUpdateCaseClosedShouldTransformEvidenceHandledNo() throws Exception {

        caseDataBuilder = CaseData.builder().evidenceHandled(NO);
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        caseDetails.setState(CASE_CLOSED_STATE);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post("/tasklist/updateCaseClosed")
                        .header(AUTH_HEADER, AUTH_TOKEN)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.state").value(CASE_CLOSED_STATE))
                .andExpect(content().string(containsString("data")));
        verify(caseDataTransformer).transformCaseDataForCaseCloseEvidenceHandled(any());
    }
}
