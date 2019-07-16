package uk.gov.hmcts.probate.controller;

import lombok.extern.slf4j.Slf4j;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.hmcts.probate.insights.AppInsights;
import uk.gov.hmcts.probate.util.TestUtils;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BulkScanningControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private AppInsights appInsights;

    @Autowired
    private TestUtils testUtils;

    private static final String EXPECTED_ATTACH_SCAN_DOC_FROM_UI_ERROR =
            "You can't attach a document to a case using this event. Use Upload Documents instead.";

    private static final String BASIC_CASE_PAYLOAD = "{\"case_details\": {\"id\": 1528365719153338} }";

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void displayAttachScanDocErrorIfUsedFromUI() throws Exception {
        mockMvc.perform(post("/error/attach-scanned-docs")
                .content(BASIC_CASE_PAYLOAD)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(EXPECTED_ATTACH_SCAN_DOC_FROM_UI_ERROR)));

    }
}