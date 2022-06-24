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
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

import java.net.URI;
import java.util.Base64;
import java.util.Map;
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
    @Value("${prd.url}")
    protected String orgUri;
    @Value("${prd.organisations.api}")
    protected String orgApi;
    @Value("${prd.organisations.account}")
    protected String accountApi;

    public OrganisationEntityResponse getOrganisationEntity(String caseId, String authToken) {
        URI uri = buildUri();
        HttpEntity<HttpHeaders> request = buildRequest(authToken);

        try {
            log.info("SAC: get OrganisationEntityResponse for caseId {}", caseId);
            ResponseEntity<OrganisationEntityResponse> responseEntity = restTemplate.exchange(uri, GET,
                    request, OrganisationEntityResponse.class);

            log.info("SAC: found OrganisationEntityResponse for caseId {}, OrganisationEntityResponse {}", caseId,
                    responseEntity.toString());
            return Objects.requireNonNull(responseEntity.getBody());
        } catch (Exception e) {
            log.error("SAC: Exception when looking up org for case {} authToken {} for exception {}",
                    caseId, new String(Base64.getEncoder().encode(authToken.getBytes())), e.getMessage());
        }
        log.info("SAC: no OrganisationEntityResponse for caseId {}", caseId);
        return null;
    }

    public String getUserAccountStatus(String emailAddress, String authToken, String caseId) {
        URI uri = buildAccountUri(emailAddress);
        HttpEntity<HttpHeaders> request = buildRequest(authToken, emailAddress);

        try {
            log.info("SAC: get OrganisationEntityResponse for caseId {} with uri {}", caseId, uri.toString());
            ResponseEntity<Map> responseEntity = restTemplate.exchange(uri, GET,
                    request, Map.class);

            Map<String, Object> body = Objects.requireNonNull(responseEntity.getBody());
            Object idamstatus = Objects.requireNonNull(body.get("idamStatus"));
            log.info("SAC: found OrganisationEntityResponse for caseId {}, OrganisationEntityResponse {}", caseId,
                    responseEntity.toString());
            return idamstatus.toString();
        } catch (Exception e) {
            log.error("SAC: Exception when looking up org for case {} authToken {} for exception {}",
                    caseId, authToken, e.getMessage());
        }
        log.info("SAC: no OrganisationEntityResponse for caseId {}", caseId);
        return null;
    }

    private HttpEntity<HttpHeaders> buildRequest(String authToken) {
        return buildRequest(authToken, null);
    }

    private HttpEntity<HttpHeaders> buildRequest(String authToken, String emailAddress) {
        HttpHeaders headers = new HttpHeaders();
        if (!authToken.matches("^Bearer .+")) {
            throw new ClientException(HttpStatus.SC_FORBIDDEN, "Invalid user token");
        }
        String s2s = authTokenGenerator.generate();
        headers.add("Authorization", authToken);
        headers.add("Content-Type", "application/json");
        headers.add("ServiceAuthorization", s2s);
        if (emailAddress != null) {
            headers.add("UserEmail", emailAddress);
        }
        return new HttpEntity<>(headers);
    }

    private URI buildUri() {
        return fromHttpUrl(orgUri + orgApi)
                .build().toUri();
    }

    private URI buildAccountUri(String userEmail) {
        return fromHttpUrl(orgUri + accountApi + "?email=" + userEmail)
                .build().toUri();
    }

}
