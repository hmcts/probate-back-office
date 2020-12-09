package uk.gov.hmcts.probate.service.pbavalidation;

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
import uk.gov.hmcts.probate.model.pba.PBAOrganisationResponse;
import uk.gov.hmcts.probate.service.IdamApi;
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableFeignClients(basePackageClasses = ServiceAuthorisationApi.class)
public class PBAValidationService {

    private final IdamApi idamService;
    private final RestTemplate restTemplate;
    private final AuthTokenGenerator authTokenGenerator;
    @Value("${pba.validation.url}")
    private String pbaUri;
    @Value("${pba.validation.api}")
    private String pbaApi;

    public List<String> getPBAs(String authToken) {
        ResponseEntity<Map> userResponse = idamService.getUserDetails(authToken);
        Map result = Objects.requireNonNull(userResponse.getBody());
        String emailId = result.get("email").toString().toLowerCase();

        URI uri = buildUri(emailId);
        HttpEntity request = buildRequest(authToken);

        ResponseEntity<PBAOrganisationResponse> responseEntity = restTemplate.exchange(uri, GET,
            request, PBAOrganisationResponse.class);
        PBAOrganisationResponse pbaOrganisationResponse = Objects.requireNonNull(responseEntity.getBody());
        log.info("pbaOrganisationEntityResponse : {}", pbaOrganisationResponse);
        List<String> accounts = pbaOrganisationResponse.getOrganisationEntityResponse().getPaymentAccount();
        return accounts;
    }

    private HttpEntity buildRequest(String authToken) {
        HttpHeaders headers = new HttpHeaders();
        if (!authToken.matches("^Bearer .+")) {
            throw new ClientException(HttpStatus.SC_FORBIDDEN, "Invalid user token");
        }
        headers.add("Authorization", authToken);
        headers.add("Content-Type", "application/json");
        headers.add("ServiceAuthorization", authTokenGenerator.generate());
        return new HttpEntity<>(headers);
    }

    private URI buildUri(String emailId) {
        return fromHttpUrl(pbaUri + pbaApi)
            .queryParam("email", emailId)
            .build().toUri();
    }
}
