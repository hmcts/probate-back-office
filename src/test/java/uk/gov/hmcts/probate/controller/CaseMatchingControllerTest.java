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
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.CaseMatchingService;
import uk.gov.hmcts.probate.util.TestUtils;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.probate.model.CaseType.CAVEAT;
import static uk.gov.hmcts.probate.model.CaseType.GRANT_OF_REPRESENTATION;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CaseMatchingControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestUtils testUtils;

    @SpyBean
    private CaseMatchingService caseMatchingService;

    @MockBean
    private AppInsights appInsights;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        doReturn(new ArrayList<>()).when(caseMatchingService).findMatches(eq(GRANT_OF_REPRESENTATION), any(CaseDetails.class));
        doReturn(new ArrayList<>()).when(caseMatchingService).findMatches(eq(CAVEAT), any(CaseDetails.class));
    }

    @Test
    public void caseMatchingSearch() throws Exception {

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotifications.json");

        mockMvc.perform(post("/case-matching/search")
                .with(csrf())
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")));

        verify(caseMatchingService).findMatches(eq(GRANT_OF_REPRESENTATION), any(CaseDetails.class));
        verify(caseMatchingService).findMatches(eq(CAVEAT), any(CaseDetails.class));
    }
}