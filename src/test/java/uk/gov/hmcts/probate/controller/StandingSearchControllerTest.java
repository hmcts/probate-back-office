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
import uk.gov.hmcts.probate.insights.AppInsights;
import uk.gov.hmcts.probate.model.ccd.standingsearch.request.StandingSearchCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.standingsearch.response.StandingSearchCallbackResponse;
import uk.gov.hmcts.probate.transformer.StandingSearchCallbackResponseTransformer;
import uk.gov.hmcts.probate.util.TestUtils;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class StandingSearchControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StandingSearchCallbackResponseTransformer standingSearchCallbackResponseTransformerMock;

    @Autowired
    private TestUtils testUtils;

    @MockBean
    private AppInsights appInsights;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void standingSearchCreatedShouldReturnDataPayloadOkResponseCode() throws Exception {
        StandingSearchCallbackResponse searchCallbackResponse = StandingSearchCallbackResponse.builder().build();

        when(standingSearchCallbackResponseTransformerMock
                .standingSearchCreated(any(StandingSearchCallbackRequest.class)))
                .thenReturn(searchCallbackResponse);
        String standingSearchPayload = testUtils.getStringFromFile("standingSearchPayload.json");

        this.mockMvc.perform(post("/standing-search/create")
                .with(csrf())
                .content(standingSearchPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")));
    }

}
