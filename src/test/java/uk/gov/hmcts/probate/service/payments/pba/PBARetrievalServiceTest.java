package uk.gov.hmcts.probate.service.payments.pba;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class PBARetrievalServiceTest {

    @InjectMocks
    private PBARetrievalService pbaRetrievalService;

    @Mock
    private SecurityUtils securityUtils;
    @Mock(name = "restTemplate")
    private RestTemplate restTemplate;
    @Mock
    private AuthTokenGenerator authTokenGenerator;

    private static final String AUTH_TOKEN = "Bearer .AUTH";

    @Mock
    private PBAOrganisationResponse pbaOrganisationResponse;

    @Mock
    private OrganisationEntityResponse organisationEntityResponse;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        pbaRetrievalService.pbaApi = "/pbaUri";
        pbaRetrievalService.pbaUri = "http://pbaApi";
    }

    @Test
    void shouldReturnPBAs() {
        when(securityUtils.getEmail(AUTH_TOKEN)).thenReturn("solicitor@probate-test.com");

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
    void shouldReturnNoPBAsForLookupForbidden() {
        when(securityUtils.getEmail(AUTH_TOKEN)).thenReturn("solicitor@probate-test.com");

        ResponseEntity<PBAOrganisationResponse> pbaOrganisationResponseResponseEntity =
            ResponseEntity.of(Optional.of(pbaOrganisationResponse));
        when(restTemplate.exchange(any(URI.class), any(HttpMethod.class),
            any(HttpEntity.class), any(Class.class))).thenThrow(new HttpClientErrorException(HttpStatus.FORBIDDEN));

        List<String> returnedPBAs = pbaRetrievalService.getPBAs(AUTH_TOKEN);

        assertEquals(0, returnedPBAs.size());
    }

    @Test
    void shouldErrorOnGetIdamUserDetails() {
        assertThrows(NullPointerException.class, () -> {
            when(securityUtils.getEmail(AUTH_TOKEN)).thenReturn(null);

            pbaRetrievalService.getPBAs(AUTH_TOKEN);
        });
    }

    @Test
    void shouldFailOnAuthTokenMatch() {
        assertThrows(ClientException.class, () -> {
            when(securityUtils.getEmail(AUTH_TOKEN)).thenReturn("solicitor@probate-test.com");

            pbaRetrievalService.getPBAs("ForbiddenToken");
        });
    }
}