package uk.gov.hmcts.probate.service.organisations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.probate.exception.ClientException;
import uk.gov.hmcts.probate.model.payments.pba.OrganisationEntityResponse;
import uk.gov.hmcts.probate.model.payments.pba.Organisations;
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

import java.net.URI;
import java.util.Base64;
import java.util.Objects;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableFeignClients(basePackageClasses = ServiceAuthorisationApi.class)
public class OrganisationsRetrievalService {

    private final RestTemplate restTemplate;
    private final AuthTokenGenerator authTokenGenerator;
    @Value("${prd.organisations.url}")
    protected String orgUri;
    @Value("${prd.organisations.api}")
    protected String orgApi;

    public OrganisationEntityResponse getOrganisationEntity(String authToken) {
        URI uri = buildUri();
        HttpEntity<HttpHeaders> request = buildRequest(authToken);

        try {
            ResponseEntity<Organisations> responseEntity = restTemplate.exchange(uri, GET,
                request, Organisations.class);

            Organisations organisationsResponse = Objects.requireNonNull(responseEntity.getBody());

            if (organisationsResponse != null && organisationsResponse.getOrganisations().size() == 1) {
                return organisationsResponse.getOrganisations().get(0);
            }
        } catch (Exception e) {
            log.error("Exception when looking up orgId for authToken={} for exception {}",
                new String(Base64.getEncoder().encode(authToken.getBytes())), e.getMessage());
        }
        return null;
    }

    private HttpEntity<HttpHeaders> buildRequest(String authToken) {
        HttpHeaders headers = new HttpHeaders();
        if (!authToken.matches("^Bearer .+")) {
            throw new ClientException(HttpStatus.SC_FORBIDDEN, "Invalid user token");
        }
        String s2s = authTokenGenerator.generate();
        headers.add("Authorization", authToken);
        headers.add("Content-Type", "application/json");
        headers.add("ServiceAuthorization", s2s);
        return new HttpEntity<>(headers);
    }

    private URI buildUri() {
        return fromHttpUrl(orgUri + orgApi)
            .build().toUri();
    }

}
