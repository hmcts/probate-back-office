package uk.gov.hmcts.probate.service.organisations;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.model.payments.pba.OrganisationEntityResponse;
import uk.gov.hmcts.probate.model.payments.pba.Organisations;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

import java.net.URI;
import java.util.Collections;
import java.util.Optional;

public class OrganisationsRetrievalServiceTest {

    @InjectMocks
    private OrganisationsRetrievalService organisationsRetrievalService;
    private static final String AUTH_TOKEN = "Bearer someAuthorizationToken";
    public static final String ORGANISATION_NAME = "OrganisationName";
    public static final String ORG_ID = "OrgID";
    @Mock
    RestTemplate restTemplate;

    Organisations organisations;
    @Test
    public void testOrganisationEntityGetsReturnedOk() {
        MockitoAnnotations.openMocks(this);

        OrganisationEntityResponse organisationEntityResponse = new OrganisationEntityResponse();
        organisationEntityResponse.setOrganisationIdentifier(ORG_ID);
        organisationEntityResponse.setName(ORGANISATION_NAME);
        organisations = new Organisations(Collections.singletonList(organisationEntityResponse));

        ResponseEntity<Organisations> organisationsResponseEntity =
            ResponseEntity.of(Optional.of(organisations));

        when(restTemplate.exchange(any(URI.class), any(HttpMethod.class),
            any(HttpEntity.class), any(Class.class))).thenReturn(organisationsResponseEntity);

        organisationsRetrievalService.orgApi = "/test_api";
        organisationsRetrievalService.orgUri = "http://localhost:8080/test";
        OrganisationEntityResponse organisationEntity = organisationsRetrievalService.getOrganisationEntity(AUTH_TOKEN);
        assertEquals(organisationEntityResponse, organisationEntity);
    }

    @Test
    public void testOrganisationEntityReturnsNullWhenException() {
        MockitoAnnotations.openMocks(this);

        when(restTemplate.exchange(any(URI.class), any(HttpMethod.class),
            any(HttpEntity.class), any(Class.class))).thenThrow(new BadRequestException("Some exception"));

        organisationsRetrievalService.orgApi = "/test_api";
        organisationsRetrievalService.orgUri = "http://localhost:8080/test";
        OrganisationEntityResponse organisationEntity = organisationsRetrievalService.getOrganisationEntity(AUTH_TOKEN);
        assertEquals(null, organisationEntity);
    }
}

