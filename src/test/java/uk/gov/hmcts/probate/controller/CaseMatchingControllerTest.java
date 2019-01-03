package uk.gov.hmcts.probate.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.hmcts.probate.insights.AppInsights;
import uk.gov.hmcts.probate.model.criterion.CaseMatchingCriteria;
import uk.gov.hmcts.probate.service.CaseMatchingService;
import uk.gov.hmcts.probate.util.TestUtils;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.probate.model.CaseType.CAVEAT;
import static uk.gov.hmcts.probate.model.CaseType.GRANT_OF_REPRESENTATION;
import static uk.gov.hmcts.probate.model.CaseType.LEGACY;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CaseMatchingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestUtils testUtils;

    @SpyBean
    private CaseMatchingService caseMatchingService;

    @MockBean
    private AppInsights appInsights;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() {
        doReturn(new ArrayList<>()).when(caseMatchingService).findMatches(eq(GRANT_OF_REPRESENTATION), any(CaseMatchingCriteria.class));
        doReturn(new ArrayList<>()).when(caseMatchingService).findMatches(eq(CAVEAT), any(CaseMatchingCriteria.class));
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void caseMatchingSearchFromGrantFlow() throws Exception {

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotifications.json");

        mockMvc.perform(post("/case-matching/search")
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")));

        verify(caseMatchingService).findMatches(eq(GRANT_OF_REPRESENTATION), any(CaseMatchingCriteria.class));
        verify(caseMatchingService).findMatches(eq(CAVEAT), any(CaseMatchingCriteria.class));
        verify(caseMatchingService).findMatches(eq(LEGACY), any(CaseMatchingCriteria.class));
    }

    @Test
    public void caseMatchingSearchFromCaveatFlow() throws Exception {

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotifications.json");

        mockMvc.perform(post("/case-matching/search-from-caveat-flow")
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")));

        verify(caseMatchingService).findMatches(eq(GRANT_OF_REPRESENTATION), any(CaseMatchingCriteria.class));
        verify(caseMatchingService).findMatches(eq(CAVEAT), any(CaseMatchingCriteria.class));
        verify(caseMatchingService).findMatches(eq(LEGACY), any(CaseMatchingCriteria.class));
    }
}