package uk.gov.hmcts.probate.controller;

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
import uk.gov.hmcts.probate.util.TestUtils;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;
import uk.gov.hmcts.probate.service.CcdSupplementaryDataService;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class StandingSearchControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestUtils testUtils;


    @MockitoBean
    private CoreCaseDataApi coreCaseDataApi;

    @MockitoBean
    private CcdSupplementaryDataService ccdSupplementaryDataService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void standingSearchCreatedShouldReturnDataPayloadOkResponseCode() throws Exception {

        String standingSearchPayload = testUtils.getStringFromFile("standingSearchPayload.json");

        mockMvc.perform(post("/standing-search/create")
                .content(standingSearchPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")));
    }

    @Test
    void standingSearchSupplementaryDataShouldReturnDataPayloadOkResponseCode() throws Exception {
        String standingSearchPayload = testUtils.getStringFromFile("standingSearchPayload.json");

        mockMvc.perform(post("/standing-search/supplementaryData")
                .content(standingSearchPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void standingSetupForDocRemoval() throws Exception {

        String standingSearchPayload = testUtils.getStringFromFile("standingSearchPayload.json");

        mockMvc.perform(post("/standing-search/setup-for-permanent-removal")
                        .content(standingSearchPayload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void standingDeleteDocuments() throws Exception {

        String standingSearchPayload = testUtils.getStringFromFile("standingSearchDocumentsPayload.json");

        mockMvc.perform(post("/standing-search/permanently-delete-removed")
                        .content(standingSearchPayload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}