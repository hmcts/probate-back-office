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
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;
import uk.gov.hmcts.reform.sendletter.api.model.v3.Document;
import uk.gov.hmcts.reform.sendletter.api.model.v3.LetterV3;
import uk.gov.hmcts.reform.sendletter.api.proxy.SendLetterApiProxy;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@ExtendWith(PactConsumerTestExt.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@PactTestFor(providerName = "rpePdfService_PDFGenerationEndpointV2", port = "8486")
@PactFolder("pacts")
@SpringBootTest
@TestPropertySource(locations = {"/application.properties"})
public class SendLetterServiceConsumerTest {

    private static final String XEROX_TYPE_PARAMETER = "PRO001";
    private static final String ADDITIONAL_DATA_CASE_REFERENCE = "caseReference";
    private static final String SERVICE_AUTHORIZATION_HEADER = "ServiceAuthorization";
    private static final String SOME_SERVICE_AUTH_TOKEN = "someServiceAuthToken";

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private SendLetterApiProxy sendLetterApiProxy;

    @Pact(provider = "rpeSendLetterService_SendLetterController", consumer = "probate_backOffice")
    public RequestResponsePact createSendLetterServiceFragment(PactDslWithProvider builder)
            throws IOException, URISyntaxException {
        return builder
                .given("A valid send letter request is received")
                .uponReceiving("a request to send that letter")
                .path("/letters")
                .query("isAsync=false")
                .method("POST")
                .headers(SERVICE_AUTHORIZATION_HEADER, SOME_SERVICE_AUTH_TOKEN)
                .body(createJsonObject(buildLetter()), "application/vnd.uk.gov.hmcts.letter-service.in.letter.v3+json")
                .willRespondWith()
                .matchHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .body(new PactDslJsonBody()
                        .uuid("letter_id", "123e4567-e89b-12d3-a456-556642440000"))
                .status(HttpStatus.SC_OK)
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "createSendLetterServiceFragment")
    public void verifySendLetterPact() throws IOException, JSONException, URISyntaxException {
        sendLetterApiProxy.sendLetter(SOME_SERVICE_AUTH_TOKEN, "false", buildLetter());
    }

    private LetterV3 buildLetter() throws IOException, URISyntaxException {
        var pdfPath = Paths.get(ClassLoader.getSystemResource("files/response.pdf").toURI());
        byte[] pdf = Files.readAllBytes(pdfPath);
        var response = Base64.getEncoder().encodeToString(pdf);
        Map<String, Object> additionalData = new HashMap<>();
        additionalData.put(ADDITIONAL_DATA_CASE_REFERENCE, "123421323");
        return new LetterV3(XEROX_TYPE_PARAMETER,
                Collections.singletonList(new Document(response, 2)),
                additionalData);
    }

    protected String createJsonObject(Object obj) throws JSONException, IOException {
        return objectMapper.writeValueAsString(obj);
    }
}
