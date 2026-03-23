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

import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.wa.WorkAllocationToggleService;
import uk.gov.hmcts.probate.util.TestUtils;
import uk.gov.hmcts.reform.authorisation.generators.ServiceAuthTokenGenerator;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
    private WorkAllocationToggleService workAllocationToggleService;

    @MockitoBean
    private SecurityUtils securityUtils;

    @MockitoBean
    private ServiceAuthTokenGenerator serviceAuthTokenGenerator;
    
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

    @Test
    void standingSearchSupplementaryDataShouldReturnDataPayloadOkResponseCode() throws Exception {
        String ssPayload = testUtils.getStringFromFile("standingSearchPayload.json");
        when(workAllocationToggleService.isProbateGSEnabled()).thenReturn(true);
        SecurityDTO securityDTO = SecurityDTO.builder()
                .serviceAuthorisation("serviceToken")
                .authorisation("userToken")
                .userId("id")
                .build();
        when(securityUtils.getUserByCaseworkerTokenAndServiceSecurityDTO()).thenReturn(securityDTO);

        mockMvc.perform(post("/standing-search/supplementaryData")
                        .content(ssPayload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(coreCaseDataApi).submitSupplementaryData(any(), any(), any(), any());
    }

}