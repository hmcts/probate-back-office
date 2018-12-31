package uk.gov.hmcts.probate.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.probate.model.probateman.ProbateManType;
import uk.gov.hmcts.probate.service.ProbateManService;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = ProbateManController.class, secure = false)
public class ProbateManControllerTest {

    private static final String ID = "1234567";

    private static final String GRANT_APPLICATION_URL = "/probateManTypes/GRANT_APPLICATION/cases/";

    private static final String WILL_LODGEMENT_URL = "/probateManTypes/WILL_LODGEMENT/cases/";

    private static final String CAVEAT_URL = "/probateManTypes/CAVEAT/cases/";

    private static final String STANDING_SEARCH_URL = "/probateManTypes/STANDING_SEARCH/cases/";

    @MockBean
    private ProbateManService probateManService;

    @Autowired
    private MockMvc mockMvc;

    private CaseDetails caseDetails;

    @Before
    public void setUp() {
        caseDetails = CaseDetails.builder().build();
    }

    @Test
    public void shouldSaveGrantApplicationToCcd() throws Exception {
        when(probateManService.saveToCcd(1234567L, ProbateManType.GRANT_APPLICATION))
            .thenReturn(caseDetails);

        mockMvc.perform(post(GRANT_APPLICATION_URL + ID)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(probateManService, times(1)).saveToCcd(1234567L, ProbateManType.GRANT_APPLICATION);
    }

    @Test
    public void shouldSaveWillLodgementToCcd() throws Exception {
        when(probateManService.saveToCcd(1234567L, ProbateManType.WILL_LODGEMENT))
            .thenReturn(caseDetails);

        mockMvc.perform(post(WILL_LODGEMENT_URL + ID)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(probateManService, times(1)).saveToCcd(1234567L, ProbateManType.WILL_LODGEMENT);
    }

    @Test
    public void shouldSaveCaveatToCcd() throws Exception {
        when(probateManService.saveToCcd(1234567L, ProbateManType.CAVEAT))
            .thenReturn(caseDetails);

        mockMvc.perform(post(CAVEAT_URL + ID)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(probateManService, times(1)).saveToCcd(1234567L, ProbateManType.CAVEAT);
    }

    @Test
    public void shouldSaveStandingSearchToCcd() throws Exception {
        when(probateManService.saveToCcd(1234567L, ProbateManType.STANDING_SEARCH))
            .thenReturn(caseDetails);

        mockMvc.perform(post(STANDING_SEARCH_URL + ID)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(probateManService, times(1)).saveToCcd(1234567L, ProbateManType.STANDING_SEARCH);
    }
}
