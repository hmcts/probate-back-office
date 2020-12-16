package uk.gov.hmcts.probate.service.payments.pba;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.probate.exception.ClientException;
import uk.gov.hmcts.probate.exception.OCRMappingException;
import uk.gov.hmcts.probate.model.payments.pba.OrganisationEntityResponse;
import uk.gov.hmcts.probate.model.payments.pba.PBAOrganisationResponse;
import uk.gov.hmcts.probate.service.IdamApi;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PBAValidationServiceTest {

    @MockBean
    private IdamApi idamService;
    @MockBean(name = "restTemplate")
    private RestTemplate restTemplate;
    @MockBean
    private AuthTokenGenerator authTokenGenerator;

    @Autowired
    private PBAValidationService pbaValidationService;

    private static final String AUTH_TOKEN = "Bearer .AUTH";

    @Mock
    private PBAOrganisationResponse pbaOrganisationResponse;

    @Mock
    private OrganisationEntityResponse organisationEntityResponse;

    @Before
    public void setup() {
    }

    @Test
    public void shouldReturnPBAs() {
        HashMap map = new HashMap<>();
        map.put("email", "solicitor@probate-test.com");
        ResponseEntity<Map> userResponse = ResponseEntity.of(Optional.of(map));
        when(idamService.getUserDetails(AUTH_TOKEN)).thenReturn(userResponse);

        ResponseEntity<PBAOrganisationResponse> pbaOrganisationResponseResponseEntity =
            ResponseEntity.of(Optional.of(pbaOrganisationResponse));
        when(restTemplate.exchange(any(URI.class), any(HttpMethod.class),
            any(HttpEntity.class), any(Class.class))).thenReturn(pbaOrganisationResponseResponseEntity);

        when(pbaOrganisationResponse.getOrganisationEntityResponse()).thenReturn(organisationEntityResponse);
        List<String> pbas = Arrays.asList("PBA1111", "PBA2222");
        when(organisationEntityResponse.getPaymentAccount()).thenReturn(pbas);

        List<String> returnedPBAs = pbaValidationService.getPBAs(AUTH_TOKEN);

        assertEquals(2, returnedPBAs.size());
    }

    @Test(expected = NullPointerException.class)
    public void shouldErrorOnGetIdamUserDetails() {
        ResponseEntity<Map> userResponse = ResponseEntity.of(Optional.empty());
        when(idamService.getUserDetails(AUTH_TOKEN)).thenReturn(userResponse);

        pbaValidationService.getPBAs(AUTH_TOKEN);
    }

    @Test(expected = NullPointerException.class)
    public void shouldErrorOnGetPBAOrganisation() {
        HashMap map = new HashMap<>();
        map.put("email", "solicitor@probate-test.com");
        ResponseEntity<Map> userResponse = ResponseEntity.of(Optional.of(map));
        when(idamService.getUserDetails(AUTH_TOKEN)).thenReturn(userResponse);

        ResponseEntity<PBAOrganisationResponse> pbaOrganisationResponseResponseEntity =
            ResponseEntity.of(Optional.empty());
        when(restTemplate.exchange(any(URI.class), any(HttpMethod.class),
            any(HttpEntity.class), any(Class.class))).thenReturn(pbaOrganisationResponseResponseEntity);

        pbaValidationService.getPBAs(AUTH_TOKEN);
    }

    @Test(expected = ClientException.class)
    public void shouldFailOnAuthTokenMatch() {
        HashMap map = new HashMap<>();
        map.put("email", "solicitor@probate-test.com");
        ResponseEntity<Map> userResponse = ResponseEntity.of(Optional.of(map));
        when(idamService.getUserDetails("ForbiddenToken")).thenReturn(userResponse);

        pbaValidationService.getPBAs("ForbiddenToken");
    }
}