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
import uk.gov.hmcts.reform.auth.checker.core.SubjectResolver;
import uk.gov.hmcts.reform.auth.checker.core.service.Service;
import uk.gov.hmcts.reform.auth.checker.core.user.User;
import uk.gov.hmcts.reform.auth.checker.core.user.UserRequestAuthorizer;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;

import javax.servlet.http.HttpServletRequest;

import java.util.HashSet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ContextConfiguration
@SpringBootTest(properties = {"s2s.enabled=true"})
public class SecurityConfigurationTest {

    private static final String PRINCIPAL = "ccd-datamgmt-api";

    private static final String SERVICE_AUTHORIZATION = "ServiceAuthorization";

    private static final String AUTHORIZATION = "Authorization";

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @MockBean
    private SubjectResolver<Service> serviceResolver;

    @MockBean
    private UserRequestAuthorizer<User> userRequestAuthorizer;

    @MockBean
    private AppInsights appInsights;

    @MockBean
    private CoreCaseDataApi coreCaseDataApi;

    private Service service;

    @Before
    public void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
            .apply(springSecurity())
            .defaultRequest(get("/").accept(MediaType.TEXT_HTML))
            .build();

        service = new Service(PRINCIPAL);
        when(serviceResolver.getTokenDetails(anyString())).thenReturn(service);

        User user = new User("123", new HashSet<>());
        when(userRequestAuthorizer.authorise(any(HttpServletRequest.class))).thenReturn(user);
    }

    @Test
    public void shouldGetSwaggerUiWithStatusCodeOkAuthenticated() throws Exception {
        mvc.perform(get("/swagger-ui.html")).andExpect(status().isOk()).andExpect(unauthenticated());
    }

    @Test
    public void shouldGet404ForFormLogin() throws Exception {
        mvc.perform(formLogin().user("user").password("password")).andExpect(status().isNotFound());
    }

    @Test
    public void shouldGet404ForLogout() throws Exception {
        mvc.perform(logout()).andExpect(status().isNotFound());
    }

    @Test
    public void shouldAuthenticateForEndpointWithServiceAuthorizationHeader() throws Exception {
        mvc.perform(post("/case/sols-validate").header(SERVICE_AUTHORIZATION, "Bearer xxxxx.yyyyy.zzzzz"))
            .andExpect(authenticated());
    }

    @Test
    public void shouldAuthenticateForEndpointWithServiceAndUserAuthorizationHeader() throws Exception {
        mvc.perform(post("/notify/grant-delayed-scheduled").header(SERVICE_AUTHORIZATION, "Bearer xxxxx.yyyyy.zzzzz")
            .header(AUTHORIZATION, "Bearer jddslfjsdlfj"))
            .andExpect(authenticated());
    }

    @Test
    public void shouldNotAuthenticateForEndpointWithServiceAndUserAuthorizationHeader() throws Exception {
        mvc.perform(post("/notify/grant-delayed-scheduled").header(SERVICE_AUTHORIZATION, "Bearer xxxxx.yyyyy.zzzzz"))
            .andExpect(unauthenticated());
    }

    @Test
    public void shouldAuthenticateForAwaitDocsEndpointWithServiceAndUserAuthorizationHeader() throws Exception {
        mvc.perform(post("/notify/grant-awaiting-documents-scheduled").header(SERVICE_AUTHORIZATION, "Bearer xxxxx.yyyyy.zzzzz")
            .header(AUTHORIZATION, "Bearer jddslfjsdlfj"))
            .andExpect(authenticated());
    }

    @Test
    public void shouldNotAuthenticateForAwaitDocEndpointWithServiceAndUserAuthorizationHeader() throws Exception {
        mvc.perform(post("/notify/grant-awaiting-documents-scheduled").header(SERVICE_AUTHORIZATION, "Bearer xxxxx.yyyyy.zzzzz"))
            .andExpect(unauthenticated());
    }

    @TestConfiguration
    @EnableWebSecurity
    @ComponentScan("uk.gov.hmcts.probate")
    public class Configuration {

    }
}
