package uk.gov.hmcts.probate.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = ProbateManController.class, secure = false)
public class ProbateManControllerTest {

    private static final String ID = "1234567";

    private static final String GRANT_APPLICATION_URL = "/probateManTypes/grantApplication/";

    private static final String ADMON_WILL_URL = "/probateManTypes/admonWill/";

    private static final String CAVEAT_URL = "/probateManTypes/caveat/";

    private static final String STANDING_SEARCH_URL = "/probateManTypes/standingSearch/";


    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldSaveGrantApplicationToCcd() throws Exception {
        mockMvc.perform(post(GRANT_APPLICATION_URL + ID)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    public void shouldSaveAdmonWillToCcd() throws Exception {
        mockMvc.perform(post(ADMON_WILL_URL + ID)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    public void shouldSaveCaveatToCcd() throws Exception {
        mockMvc.perform(post(CAVEAT_URL + ID)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    public void shouldSaveStandingSearchToCcd() throws Exception {
        mockMvc.perform(post(STANDING_SEARCH_URL + ID)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }
}
