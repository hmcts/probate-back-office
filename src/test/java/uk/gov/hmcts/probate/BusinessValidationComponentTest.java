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
import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = "s2s.enabled=true")
public class BusinessValidationComponentTest extends ComponentTestBase {

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
        MediaType.APPLICATION_JSON.getSubtype(),
        Charset.forName("utf8"));

    private static final String VALIDATE_SOLICITOR_CREATE_SERVICE_URL = "/validate/addDeceasedDetails";
    private static final String VALIDATE_SOL_ADD_DECEASED_ESTATE_DETAILS_SERVICE_URL = "/validate/solAddDeceasedEstateDetails";
    private static final String VALIDATE_SOL_EXECUTOR_DETAILS = "/validate/solExecutorDetails";

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
    public void setup() {
        mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void shouldReturnBadRequestForEmptyContent() throws Exception {
        mockMvc.perform(post(VALIDATE_SOLICITOR_CREATE_SERVICE_URL)
            .contentType(contentType))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldSuccessfullyValidateDobBeforeDod() throws Exception {
        validatePostSuccessSolicitorCreate("success.json");
    }

    @Test
    public void shouldReturnValidationErrorForDobNull() throws Exception {
        validatePostFailureSolicitorCreate("failure.dobIsNull.json",
            Collections.singletonList("dobIsNull"), 1);
    }

    @Test
    public void shouldReturnValidationErrorForDodNull() throws Exception {
        validatePostFailureSolicitorCreate("failure.dodIsNull.json",
            Collections.singletonList("dodIsNull"), 1);
    }

    @Test
    public void shouldReturnValidationErrorForDodAfterDob() throws Exception {
        validatePostFailureSolicitorCreate("failure.dobIsAfterDod.json",
            Collections.singletonList("dodIsBeforeDob"), 1);
    }

    @Test
    public void shouldReturnValidationErrorForDodOnDob() throws Exception {
        validatePostFailureSolicitorCreate("failure.dodIsSameAsDob.json",
            Collections.singletonList("dodIsSameAsDob"), 1);
    }

    @Test
    public void shouldReturnValidationErrorForDobIsInTheFuture() throws Exception {
        validatePostFailureSolicitorCreate("failure.dobIsInTheFuture.json",
            Collections.singletonList("dobIsInTheFuture"), 2);
    }

    @Test
    public void shouldReturnValidationErrorForDodIsInTheFuture() throws Exception {
        validatePostFailureSolicitorCreate("failure.dodIsInTheFuture.json",
            Collections.singletonList("dodIsInTheFuture"), 1);
    }

    @Test
    public void shouldReturnValidationErrorForIhtEmptyGrossValue() throws Exception {
        validatePostFailureSolAddDeceasedEstateDetails("failure.ihtGrossIsEmpty.json",
            Collections.singletonList("ihtGrossIsNull"), 1);
    }

    @Test
    public void shouldReturnValidationErrorForIhtEmptyNetValue() throws Exception {
        validatePostFailureSolAddDeceasedEstateDetails("failure.ihtNetIsEmpty.json",
            Collections.singletonList("ihtNetIsNull"), 1);
    }

    @Test
    public void shouldReturnValidationErrorForIhtNetNegativeValue() throws Exception {
        validatePostFailureSolAddDeceasedEstateDetails("failure.ihtNetIsNegative.json",
            Collections.singletonList("ihtNetNegative"), 1);
    }

    @Test
    public void shouldReturnValidationErrorForIhtGrossNegativeValue() throws Exception {
        validatePostFailureSolAddDeceasedEstateDetails("failure.ihtGrossIsNegative.json",
            Collections.singletonList("ihtGrossNegative"), 1);
    }

    @Test
    public void shouldReturnValidationErrorForIhtWhenNetValueIsGreaterThanGrossValue() throws Exception {
        validatePostFailureSolAddDeceasedEstateDetails("failure.ihtNetIsGreaterThanGross.json",
            Collections.singletonList("ihtNetGreaterThanGross"), 1);
    }

    @Test
    public void shouldReturnValidationErrorMissingAddress() throws Exception {
        validatePostFailureSolAddDeceasedEstateDetails("failure.missingDeceasedAddress.json",
            Collections.singletonList("deceasedAddressIsNull"), 1);
    }

    @Test
    public void shouldReturnValidationErrorMissingPostcode() throws Exception {
        validatePostFailureSolAddDeceasedEstateDetails("failure.missingDeceasedPostcode.json",
            Collections.singletonList("deceasedPostcodeIsNull"), 1);
    }

    @Test
    public void shouldReturnValidationErrorSolExecutorMissingAddress() throws Exception {
        validatePostFailureSolExecutorDetails("failure.missingExecutorAddress.json",
            Collections.singletonList("executorAddressIsNull"), 1);
    }

    @Test
    public void shouldReturnValidationErrorSolExecutorMissingPostcode() throws Exception {
        validatePostFailureSolExecutorDetails("failure.missingExecutorPostcode.json",
            Collections.singletonList("executorPostcodeIsNull"), 1);
    }

    @Test
    public void shouldReturnValidationErrorForDeceasedDomicileInEngWalesMissing() throws Exception {
        validatePostFailureSolAddDeceasedEstateDetails("failure.deceasedDomicileInEngWalesMissing.json",
            Collections.singletonList("deceasedDomicileInEngWalesIsNull"), 1);
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

    private void validatePostSuccessSolicitorCreate(String jsonFile) throws Exception {
        validatePostSuccess(jsonFile, VALIDATE_SOLICITOR_CREATE_SERVICE_URL);
    }

    private void validatePostFailureSolicitorCreate(String jsonFile, List<String> errorMessageCode, int errorSize) throws Exception {
        validatePostFailure(jsonFile, VALIDATE_SOLICITOR_CREATE_SERVICE_URL, errorMessageCode, errorSize);
    }

    private void validatePostFailureSolAddDeceasedEstateDetails(String jsonFile,
                                                                List<String> errorMessageCode,
                                                                int errorSize) throws Exception {
        validatePostFailure(jsonFile, VALIDATE_SOL_ADD_DECEASED_ESTATE_DETAILS_SERVICE_URL, errorMessageCode, errorSize);
    }

    private void validatePostFailureSolExecutorDetails(String jsonFile,
                                                       List<String> errorMessageCode,
                                                       int errorSize) throws Exception {
        validatePostFailure(jsonFile, VALIDATE_SOL_EXECUTOR_DETAILS, errorMessageCode, errorSize);
    }

    private ResultActions validatePostSuccess(String headerName, String header, String jsonFile) throws Exception {
        return mockMvc.perform(post(VALIDATE_SOLICITOR_CREATE_SERVICE_URL)
            .header(headerName, header)
            .content(utils.getJsonFromFile(jsonFile))
            .contentType(contentType))
            .andExpect(status().isOk())
            .andExpect(matchErrorsSize(0));
    }

    private void validatePostSuccess(String jsonFile, String url) throws Exception {
        mockMvc.perform(post(url)
            .content(utils.getJsonFromFile(jsonFile))
            .contentType(contentType))
            .andExpect(status().isOk())
            .andExpect(matchErrorsSize(0));
    }

    private void validatePostFailure(String jsonFile, String url, List<String> errorMessageCode, int errorSize) throws Exception {
        mockMvc.perform(post(url)
            .content(utils.getJsonFromFile(jsonFile))
            .contentType(contentType))
            .andExpect(status().isOk())
            .andExpect(matchErrorsSize(errorSize))
            .andExpect(matchFirstErrorMessageCode(errorMessageCode));
    }
}
