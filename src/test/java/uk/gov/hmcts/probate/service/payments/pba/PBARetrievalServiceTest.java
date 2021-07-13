package uk.gov.hmcts.probate.service.payments.pba;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.probate.exception.ClientException;
import uk.gov.hmcts.probate.model.payments.pba.OrganisationEntityResponse;
import uk.gov.hmcts.probate.model.payments.pba.PBAOrganisationResponse;
import uk.gov.hmcts.probate.service.IdamAuthenticateUserService;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class PBARetrievalServiceTest {

    @InjectMocks
    private PBARetrievalService pbaRetrievalService;

    @Mock
    private IdamAuthenticateUserService idamAuthenticateUserService;
    @Mock(name = "restTemplate")
    private RestTemplate restTemplate;
    @Mock
    private AuthTokenGenerator authTokenGenerator;

    private static final String AUTH_TOKEN = "Bearer .AUTH";

    @Mock
    private PBAOrganisationResponse pbaOrganisationResponse;

    @Mock
    private OrganisationEntityResponse organisationEntityResponse;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        pbaRetrievalService.pbaApi = "/pbaUri";
        pbaRetrievalService.pbaUri = "http://pbaApi";
    }

    @Test
    public void shouldReturnPBAs() {
        when(idamAuthenticateUserService.getEmail(AUTH_TOKEN)).thenReturn("solicitor@probate-test.com");

        ResponseEntity<PBAOrganisationResponse> pbaOrganisationResponseResponseEntity =
            ResponseEntity.of(Optional.of(pbaOrganisationResponse));
        when(restTemplate.exchange(any(URI.class), any(HttpMethod.class),
            any(HttpEntity.class), any(Class.class))).thenReturn(pbaOrganisationResponseResponseEntity);

        when(pbaOrganisationResponse.getOrganisationEntityResponse()).thenReturn(organisationEntityResponse);
        List<String> pbas = Arrays.asList("PBA1111", "PBA2222");
        when(organisationEntityResponse.getPaymentAccount()).thenReturn(pbas);

        List<String> returnedPBAs = pbaRetrievalService.getPBAs(AUTH_TOKEN);

        assertEquals(2, returnedPBAs.size());
    }

    @Test
    public void shouldReturnNoPBAsForLookupForbidden() {
        when(idamAuthenticateUserService.getEmail(AUTH_TOKEN)).thenReturn("solicitor@probate-test.com");

        ResponseEntity<PBAOrganisationResponse> pbaOrganisationResponseResponseEntity =
            ResponseEntity.of(Optional.of(pbaOrganisationResponse));
        when(restTemplate.exchange(any(URI.class), any(HttpMethod.class),
            any(HttpEntity.class), any(Class.class))).thenThrow(new HttpClientErrorException(HttpStatus.FORBIDDEN));

        List<String> returnedPBAs = pbaRetrievalService.getPBAs(AUTH_TOKEN);

        assertEquals(0, returnedPBAs.size());
    }

    @Test(expected = NullPointerException.class)
    public void shouldErrorOnGetIdamUserDetails() {
        when(idamAuthenticateUserService.getEmail(AUTH_TOKEN)).thenReturn(null);

        pbaRetrievalService.getPBAs(AUTH_TOKEN);
    }

    @Test(expected = ClientException.class)
    public void shouldFailOnAuthTokenMatch() {
        when(idamAuthenticateUserService.getEmail(AUTH_TOKEN)).thenReturn("solicitor@probate-test.com");

        pbaRetrievalService.getPBAs("ForbiddenToken");
    }
}