package uk.gov.hmcts.probate.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.LifeEventService;
import uk.gov.hmcts.probate.util.TestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class LifeEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestUtils testUtils;

    @MockBean
    private AppInsights appInsights;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private LifeEventService lifeEventService;

    @MockBean
    private SecurityUtils securityUtils;

    @Captor
    private ArgumentCaptor<CaseDetails> caseDetailsArgumentCaptor;
    
    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void lifeEventUpdateShouldReturnDataPayloadOkResponseCode() throws Exception {

        String payload = testUtils.getStringFromFile("lifeEventPayload.json");

        mockMvc.perform(post("/lifeevent/update")
                .content(payload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")));
        
        verify(lifeEventService).verifyDeathRecord(caseDetailsArgumentCaptor.capture(), any());
        final CaseDetails caseDetailsArgumentCaptorValue = caseDetailsArgumentCaptor.getValue();
        assertThat(caseDetailsArgumentCaptorValue.getId()).isEqualTo(1621002468661478L);
        final CaseData data = caseDetailsArgumentCaptorValue.getData();
        assertThat(data.getDeceasedForenames()).isEqualTo("John");
        assertThat(data.getDeceasedSurname()).isEqualTo("Cook");
        assertThat(data.getDeceasedDateOfDeath().toString()).isEqualTo("2006-11-16");
        verify(securityUtils).getSecurityDTO();
    }
}
