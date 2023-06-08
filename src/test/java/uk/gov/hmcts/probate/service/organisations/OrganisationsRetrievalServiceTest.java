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
import uk.gov.hmcts.probate.model.caseaccess.OrganisationUser;
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
    OrganisationUser organisationUser;

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
        OrganisationEntityResponse organisationEntity = organisationsRetrievalService.getOrganisationEntity(
                "1234567890123456", AUTH_TOKEN);
        assertEquals(organisationEntityResponse, organisationEntity);

        when(authTokenGenerator.generate()).thenReturn("S2S_DUMMY");
    }

    @Test
    void testOrganisationGetsReturnedOk() {
        MockitoAnnotations.openMocks(this);

        organisationUser = new OrganisationUser();
        organisationUser.setUserIdentifier(ORG_ID);
        organisationUser.setFirstName(ORGANISATION_NAME);

        ResponseEntity<OrganisationUser> organisationsResponseEntity =
                ResponseEntity.of(Optional.of(organisationUser));

        when(restTemplate.exchange(any(URI.class), any(HttpMethod.class),
                any(HttpEntity.class), any(Class.class))).thenReturn(organisationsResponseEntity);

        organisationsRetrievalService.orgApis = "/test_api";
        organisationsRetrievalService.orgUri = "http://localhost:8080/test";
        when(authTokenGenerator.generate()).thenReturn("S2S_DUMMY");
        OrganisationUser organisationEntity = organisationsRetrievalService.findUserByEmail(
                "1234567890123456", "abc@gmail.co", AUTH_TOKEN);
        assertEquals(organisationUser, organisationEntity);
    }

    @Test
    void testOrganisationEntityReturnsNullWhenException() {
        MockitoAnnotations.openMocks(this);

        when(restTemplate.exchange(any(URI.class), any(HttpMethod.class),
            any(HttpEntity.class), any(Class.class))).thenThrow(new BadRequestException("Some exception"));

        organisationsRetrievalService.orgApi = "/test_api";
        organisationsRetrievalService.orgUri = "http://localhost:8080/test";
        OrganisationEntityResponse organisationEntity = organisationsRetrievalService.getOrganisationEntity(
                "1234567890123456", AUTH_TOKEN);
        assertEquals(null, organisationEntity);
    }

    @Test
    void testOrganisationEntityExceptionWithNoBearer() {
        assertThrows(ClientException.class, () -> {
            MockitoAnnotations.openMocks(this);

            organisationsRetrievalService.orgApi = "/test_api";
            organisationsRetrievalService.orgUri = "http://localhost:8080/test";
            organisationsRetrievalService.getOrganisationEntity("1234567890123456", "something else");
        });
    }
}

