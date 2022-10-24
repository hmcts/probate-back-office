package uk.gov.hmcts.probate.service.consumer;

import au.com.dius.pact.consumer.dsl.PactDslRootValue;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@PropertySource(value = "classpath:application.yml")
@EnableAutoConfiguration
@ExtendWith(PactConsumerTestExt.class)
@ExtendWith(SpringExtension.class)
@PactTestFor(providerName = "s2s_auth", port = "4502")
@SpringBootTest(classes = ServiceAuthorisationApi.class)
public class ServiceAuthenConsummerLeaseTest {

    private static final String AUTHORISATION_TOKEN = "Bearer someAuthorisationToken";
    public static final String SOME_MICRO_SERVICE_NAME = "someMicroServiceName";
    public static final String SOME_MICRO_SERVICE_TOKEN = "someMicroServiceToken";

    @Autowired
    private ServiceAuthorisationApi serviceAuthorisationApi;

    @Autowired
    ObjectMapper objectMapper;

    Map<String, String> jsonPayload = new HashMap<>();

    @BeforeEach
    public void setUpTest() {
        jsonPayload.put("microservice", "microserviceName");
        jsonPayload.put("oneTimePassword", "784467");
    }

    @Pact(provider = "s2s_auth",consumer = "probate_backOffice")
    public RequestResponsePact executeLease(PactDslWithProvider builder) throws JsonProcessingException {

        return builder.given("microservice with valid credentials")
                .uponReceiving("a request for a token")
                .path("/lease")
                .method(HttpMethod.POST.toString())
                .body(buildJsonPayload())
                .willRespondWith()
                .headers(Map.of(HttpHeaders.CONTENT_TYPE, "text/plain"))
                .status(HttpStatus.OK.value())
                .body(PactDslRootValue.stringType(SOME_MICRO_SERVICE_TOKEN))
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "executeLease")
    void verifyLease() {

        String token = serviceAuthorisationApi.serviceToken(jsonPayload);
        assertThat(token)
                .isEqualTo("someMicroServiceToken");

    }

    private String buildJsonPayload() throws JsonProcessingException {

        return objectMapper.writeValueAsString(jsonPayload);
    }
}