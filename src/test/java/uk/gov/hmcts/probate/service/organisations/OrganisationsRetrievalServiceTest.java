package uk.gov.hmcts.probate.service.organisations;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.exception.ClientException;
import uk.gov.hmcts.probate.model.payments.pba.OrganisationEntityResponse;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

import java.net.URI;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class OrganisationsRetrievalServiceTest {

    @InjectMocks
    private OrganisationsRetrievalService organisationsRetrievalService;

    private static final String AUTH_TOKEN = "Bearer someAuthorizationToken";
    public static final String ORGANISATION_NAME = "OrganisationName";
    public static final String ORG_ID = "OrgID";

    @Mock(name = "restTemplate")
    private RestTemplate restTemplate;
    OrganisationEntityResponse organisationEntityResponse;

    @Mock
    private AuthTokenGenerator authTokenGenerator;

    @Test
    void testOrganisationEntityGetsReturnedOk() {
        MockitoAnnotations.openMocks(this);

        organisationEntityResponse = new OrganisationEntityResponse();
        organisationEntityResponse.setOrganisationIdentifier(ORG_ID);
        organisationEntityResponse.setName(ORGANISATION_NAME);

        ResponseEntity<OrganisationEntityResponse> organisationsResponseEntity =
            ResponseEntity.of(Optional.of(organisationEntityResponse));

        when(restTemplate.exchange(any(URI.class), any(HttpMethod.class),
            any(HttpEntity.class), any(Class.class))).thenReturn(organisationsResponseEntity);

        organisationsRetrievalService.orgApi = "/test_api";
        organisationsRetrievalService.orgUri = "http://localhost:8080/test";
        OrganisationEntityResponse organisationEntity = organisationsRetrievalService.getOrganisationEntity(AUTH_TOKEN);
        assertEquals(organisationEntityResponse, organisationEntity);

        when(authTokenGenerator.generate()).thenReturn("S2S_DUMMY");
    }

    @Test
    void testOrganisationEntityReturnsNullWhenException() {
        MockitoAnnotations.openMocks(this);

        when(restTemplate.exchange(any(URI.class), any(HttpMethod.class),
            any(HttpEntity.class), any(Class.class))).thenThrow(new BadRequestException("Some exception"));

        organisationsRetrievalService.orgApi = "/test_api";
        organisationsRetrievalService.orgUri = "http://localhost:8080/test";
        OrganisationEntityResponse organisationEntity = organisationsRetrievalService.getOrganisationEntity(AUTH_TOKEN);
        assertEquals(null, organisationEntity);
    }

    @Test
    void testOrganisationEntityExceptionWithNoBearer() {
        assertThrows(ClientException.class, () -> {
            MockitoAnnotations.openMocks(this);

            organisationsRetrievalService.orgApi = "/test_api";
            organisationsRetrievalService.orgUri = "http://localhost:8080/test";
            organisationsRetrievalService.getOrganisationEntity("something else");
        });
    }
}

