package uk.gov.hmcts.probate.service.consumer;

import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import au.com.dius.pact.core.model.annotations.PactFolder;
import com.google.common.collect.Maps;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.service.client.DocumentStoreClient;

import java.io.IOException;
import java.util.Map;

@ExtendWith(PactConsumerTestExt.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@PactTestFor(providerName = "em_dm_store", port = "8892")
@PactFolder("pacts")
@SpringBootTest
@TestPropertySource(locations = {"/application.properties"})
public class DocumentStoreClientConsumerTest {

    @Autowired
    DocumentStoreClient documentStoreClient;

    private static final String SERVICE_AUTHORIZATION = "ServiceAuthorization";

    public static final String SOME_SERVICE_AUTHORIZATION_TOKEN = "ServiceToken";
    private static final String USER_ID = "id1";
    private static final String DOCUMENT_ID = "5c3c3906-2b51-468e-8cbb-a4002eded075";

    @Pact(consumer = "probate_backOffice")
    public RequestResponsePact createFragment(PactDslWithProvider builder) throws IOException {
        Map<String, String> headers = Maps.newHashMap();
        headers.put("ServiceAuthorization", SOME_SERVICE_AUTHORIZATION_TOKEN);
        headers.put("user-id", USER_ID);

        return builder
            .given("I have existing document")
            .uponReceiving("a request for download the document")
            .path("/documents/" + DOCUMENT_ID + "/binary")
            .method("GET")
            .headers(headers)
            .willRespondWith()
            .status(200)
            .toPact();
    }


    @Test
    @PactTestFor(pactMethod = "createFragment")
    public void verifyDownloadDocumentPact() throws IOException, JSONException {

        byte[] bytes = documentStoreClient.retrieveDocument(Document.builder().documentGeneratedBy(USER_ID)
                .documentLink(DocumentLink.builder().documentBinaryUrl("http://localhost:8892/documents/" + DOCUMENT_ID + "/binary").build()).build(),
            SOME_SERVICE_AUTHORIZATION_TOKEN);

    }


}
