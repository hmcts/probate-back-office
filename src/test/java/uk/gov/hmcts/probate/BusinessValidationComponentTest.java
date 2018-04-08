package uk.gov.hmcts.probate;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.hmcts.auth.checker.service.Service;
import uk.gov.hmcts.auth.checker.service.ServiceRequestAuthorizer;
import uk.gov.hmcts.probate.util.TestUtils;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BusinessValidationComponentTest extends ComponentTestBase {

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
        MediaType.APPLICATION_JSON.getSubtype(),
        Charset.forName("utf8"));

    protected static final String VALIDATE_SERVICE_URL = "/validate";

    @Autowired
    private WebApplicationContext webApplicationContext;

    @SpyBean
    private ServiceRequestAuthorizer serviceRequestAuthorizer;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    protected TestUtils utils;

    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {

        mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void shouldReturnBadRequestForEmptyContent() throws Exception {

        mockMvc.perform(post(VALIDATE_SERVICE_URL)
            .contentType(contentType))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldSuccessfullyValidateDobBeforeDod() throws Exception {

        validatePostSuccess("success.json");
    }

    @Test
    public void shouldReturnValidationErrorforDobNull() throws Exception {

        validatePostFailure("failure.dobIsNull.json", Arrays.asList("dobIsNull"), 1);
    }

    @Test
    public void shouldReturnValidationErrorforDodNull() throws Exception {

        validatePostFailure("failure.dodIsNull.json", Arrays.asList("dodIsNull"), 1);
    }

    @Test
    public void shouldReturnValidationErrorforDodAfterDob() throws Exception {

        validatePostFailure("failure.dobIsAfterDod.json", Arrays.asList("dodIsBeforeDob"), 1);
    }

    @Test
    public void shouldReturnValidationErrorforDodOnDob() throws Exception {

        validatePostFailure("failure.dodIsSameAsDob.json", Arrays.asList("dodIsSameAsDob"), 1);
    }

    @Test
    public void shouldReturnValidationErrorforDobIsInTheFuture() throws Exception {

        validatePostFailure("failure.dobIsInTheFuture.json", Arrays.asList("dobIsInTheFuture"), 2);
    }

    @Test
    public void shouldReturnValidationErrorforDodIsInTheFuture() throws Exception {

        validatePostFailure("failure.dodIsInTheFuture.json", Arrays.asList("dodIsInTheFuture"), 1);
    }

    @Test
    public void shouldReturnValidationErrorForIhtEmptyGrossValue() throws Exception {

        validatePostFailure("failure.ihtGrossIsEmpty.json", Arrays.asList("ihtGrossIsNull"), 1);
    }

    @Test
    public void shouldReturnValidationErrorForIhtEmptyNetValue() throws Exception {

        validatePostFailure("failure.ihtNetIsEmpty.json", Arrays.asList("ihtNetIsNull"), 1);
    }

    @Test
    public void shouldReturnValidationErrorForIhtNetNegativeValue() throws Exception {

        validatePostFailure("failure.ihtNetIsNegative.json", Arrays.asList("ihtNetNegative"), 1);
    }

    @Test
    public void shouldReturnValidationErrorForIhtGrossNegativeValue() throws Exception {

        validatePostFailure("failure.ihtGrossIsNegative.json", Arrays.asList("ihtGrossNegative"), 1);
    }


    @Test
    public void shouldReturnValidationErrorForIhtWhenNetValueIsGreaterThanGrossValue() throws Exception {

        validatePostFailure("failure.ihtNetIsGreaterThanGross.json", Arrays.asList("ihtNetGreaterThanGross"), 1);
    }

    @Test
    public void testS2SAuthorisation() throws Exception {
        doReturn(new Service("probate_backend")).when(serviceRequestAuthorizer).authorise(any());
        mockMvc = webAppContextSetup(webApplicationContext)
            .addFilters(springSecurityFilterChain)
            .build();

        String header = "ServiceAuthorisation";
        String token = "probate_backend_dummy_token";
        MvcResult result = validatePostSuccess(header, token, "success.json").andReturn();

        Matcher<HttpServletRequest> requestHeaderMatcher =
            utils.requestHeaderMatcher(result.getRequest(), header, token);

        verify(serviceRequestAuthorizer).authorise(argThat(requestHeaderMatcher));
    }

    private ResultActions validatePostSuccess(String headerName, String header, String jsonFile) throws Exception {
        return mockMvc.perform(post(VALIDATE_SERVICE_URL)
            .header(headerName, header)
            .content(utils.getJsonFromFile(jsonFile))
            .contentType(contentType))
            .andExpect(status().isOk())
            .andExpect(matchSuccesslidationVaStatus())
            .andExpect(matchErrorsSize(0));

    }

    private void validatePostSuccess(String jsonFile) throws Exception {
        mockMvc.perform(post(VALIDATE_SERVICE_URL)
            .content(utils.getJsonFromFile(jsonFile))
            .contentType(contentType))
            .andExpect(status().isOk())
            .andExpect(matchSuccesslidationVaStatus())
            .andExpect(matchErrorsSize(0));
    }

    private void validatePostFailure(String jsonFile, List<String> errorMessageCode, int errorSize) throws Exception {
        mockMvc.perform(post(VALIDATE_SERVICE_URL)
            .content(utils.getJsonFromFile(jsonFile))
            .contentType(contentType))
            .andExpect(status().isOk())
            .andExpect(matchFailureValidationStatus())
            .andExpect(matchErrorsSize(errorSize))
            .andExpect(matchFirstErrorMessageCode(errorMessageCode));
    }


}
