package uk.gov.hmcts.probate.service.consumer;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactHttpsProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;
import com.google.common.collect.Maps;
import org.json.JSONException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.service.client.DocumentStoreClient;

import java.io.IOException;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DocumentStoreClientConsumerTest {


    @Rule
    public PactHttpsProviderRuleMk2 provider = new PactHttpsProviderRuleMk2("em_dm_store", "localhost", 8892, this);

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
    @PactVerification(fragment = "createFragment")
    public void verifyDownloadDocumentPact() throws IOException, JSONException {

        byte[] bytes = documentStoreClient.retrieveDocument(Document.builder().documentGeneratedBy(USER_ID)
                .documentLink(DocumentLink.builder().documentBinaryUrl("http://localhost:8892/documents/" + DOCUMENT_ID + "/binary").build()).build(),
            SOME_SERVICE_AUTHORIZATION_TOKEN);

    }


}
