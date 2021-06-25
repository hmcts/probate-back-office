package uk.gov.hmcts.probate.service.consumer;

import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import au.com.dius.pact.core.model.annotations.PactFolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.StreamUtils;
import uk.gov.hmcts.reform.printletter.api.model.v1.PrintRequest;
import uk.gov.hmcts.reform.printletter.api.proxy.PrintLetterApiProxy;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@ExtendWith(PactConsumerTestExt.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@PactTestFor(providerName = "rpePdfService_PDFGenerationEndpointV2", port = "8486")
@PactFolder("pacts")
@SpringBootTest
@TestPropertySource(locations = {"/application.properties"})
public class PrintLetterServiceConsumerTest {

    private static final String SERVICE_AUTHORIZATION_HEADER = "ServiceAuthorization";
    private static final String SOME_SERVICE_AUTH_TOKEN = "someServiceAuthToken";
    private static final UUID uuid = UUID.fromString("123e4567-e89b-12d3-a456-556642440000");
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PrintLetterApiProxy printLetterApiProxy;

    @Pact(provider = "rpePrintLetterService_PrintLetterController", consumer = "probate_backOffice")
    public RequestResponsePact createPrintLetterServiceFragment(PactDslWithProvider builder)
        throws IOException {
        return builder
            .given("A valid send letter request is received")
            .uponReceiving("a request to send that letter")
            .path("/print-jobs/" + uuid)
            .method("PUT")
            .headers(SERVICE_AUTHORIZATION_HEADER, SOME_SERVICE_AUTH_TOKEN)
            .body(createJsonObject(buildLetter()), "application/json")
            .willRespondWith()
            .matchHeader(HttpHeaders.CONTENT_TYPE, "application/json")
            .body(new PactDslJsonBody()
                .uuid("letter_id", uuid))
            .status(HttpStatus.SC_OK)
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "createPrintLetterServiceFragment")
    public void verifyPrintLetterPact() throws IOException, JSONException {
        printLetterApiProxy.print(SOME_SERVICE_AUTH_TOKEN, uuid, buildLetter());
    }

    private PrintRequest buildLetter() throws IOException {
        var json = StreamUtils.copyToString(
                new ClassPathResource("json/print_job.json").getInputStream(),
                StandardCharsets.UTF_8);
        return objectMapper.readValue(json, PrintRequest.class);
    }

    private String createJsonObject(Object obj) throws JSONException, IOException {
        return objectMapper.writeValueAsString(obj);
    }
}
