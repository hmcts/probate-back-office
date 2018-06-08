package uk.gov.hmcts.probate.config;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.hmcts.probate.insights.AppInsights;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ContextConfiguration
@SpringBootTest(properties = {"s2s.enabled=true"})
public class SecurityConfigurationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @MockBean
    AppInsights appInsights;

    @Before
    public void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .defaultRequest(get("/").accept(MediaType.TEXT_HTML))
                .build();
    }

    @Test
    public void shouldGetSwaggerUiWithStatusCodeOkAuthenticated() throws Exception {
        mvc.perform(get("/swagger-ui.html")).andExpect(status().isOk()).andExpect(unauthenticated());
    }

    @Test
    public void shouldGet404ForFormLogin() throws Exception {
        mvc.perform(formLogin().user("user").password("password")).andExpect(status().isForbidden());
    }

    @Test
    public void shouldGet404ForLogout() throws Exception {
        mvc.perform(logout()).andExpect(status().isForbidden());
    }

    @TestConfiguration
    @EnableWebSecurity
    @ComponentScan("uk.gov.hmcts.probate")
    public class Configuration {

    }
}