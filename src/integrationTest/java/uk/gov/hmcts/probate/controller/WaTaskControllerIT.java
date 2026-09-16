package uk.gov.hmcts.probate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.wa.WaMapper;
import uk.gov.hmcts.probate.service.wa.WorkAllocationToggleService;
import uk.gov.hmcts.probate.util.TestUtils;
import uk.gov.hmcts.probate.utils.TaskUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.probate.model.Constants.CLIENT_CONTEXT_HEADER_PARAMETER;

@AutoConfigureMockMvc
@SpringBootTest
public class WaTaskControllerIT {
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoSpyBean
    private TaskUtils taskUtils;

    @MockitoBean
    private WorkAllocationToggleService workAllocationToggleService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestUtils testUtils;

    private static final String CLIENT_CONTEXT = """
        {
          "client_context": {
            "user_task": {
              "complete_task" : true
            }
          }
        }
        """;

    @Test
    void shouldUpdateClientContextWhenProbateWaIsEnabled() throws Exception {
        String payload = testUtils.getStringFromFile("waTaskCaseType.json");
        when(workAllocationToggleService.isProbateWAEnabled())
                .thenReturn(true);

        WaMapper waMapper = objectMapper.readValue(CLIENT_CONTEXT, WaMapper.class);
        Optional<String> encodedString = taskUtils.base64Encode(waMapper);
        assertThat(encodedString).isNotEmpty();

        mockMvc.perform(post("/waTaskContoller/case-type/updateClientContext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(
                                CLIENT_CONTEXT_HEADER_PARAMETER,
                                encodedString.get())
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(header().string(
                        CLIENT_CONTEXT_HEADER_PARAMETER,
                        encodedString.get()));
    }

    @Test
    void shouldNotUpdateClientContextWhenProbateWaIsNotEnabled() throws Exception {
        String payload = testUtils.getStringFromFile("waTaskCaseType.json");
        when(workAllocationToggleService.isProbateWAEnabled())
                .thenReturn(false);

        WaMapper waMapper = objectMapper.readValue(CLIENT_CONTEXT, WaMapper.class);
        Optional<String> encodedString = taskUtils.base64Encode(waMapper);
        assertThat(encodedString).isNotEmpty();

        mockMvc.perform(post("/waTaskContoller/case-type/updateClientContext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(
                                CLIENT_CONTEXT_HEADER_PARAMETER,
                                encodedString.get())
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(header().doesNotExist(
                        CLIENT_CONTEXT_HEADER_PARAMETER));

        verify(taskUtils, never())
                .setTaskCompletion(isA(String.class), isA(CallbackRequest.class), any());
    }

    @Test
    void shouldReturnBadRequestForInvalidPayload() throws Exception {

        String invalidRequest = """
                {
                  "case_details": {
                    "jurisdiction": "PROBATE",
                    "case_data": {
                    }
                   }
                }
            """;

        when(workAllocationToggleService.isProbateWAEnabled())
                .thenReturn(true);

        mockMvc.perform(post("/waTaskContoller/case-type/updateClientContext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());
    }
}
