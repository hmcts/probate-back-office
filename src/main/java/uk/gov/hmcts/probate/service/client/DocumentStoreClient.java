package uk.gov.hmcts.probate.service.client;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
@Slf4j
@AllArgsConstructor
public class DocumentStoreClient {

    private static final String USER_ROLES = "user-roles";
    private static final String SERVICE_AUTHORIZATION = "ServiceAuthorization";
    private final AuthTokenGenerator authTokenGenerator;

    public byte[] retrieveDocument(Document document, String authHeaderValue) {
        CloseableHttpClient closeableHttpClient = HttpClientBuilder.create().build();
        byte[] bytes = null;
        try {
            HttpGet request = new HttpGet(document.getDocumentLink().getDocumentUrl());
            request.setHeader(SERVICE_AUTHORIZATION, authHeaderValue);
            // request.setHeader(USER_ROLES, CASEWORKER_DIVORCE);
            CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(request);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            closeableHttpResponse.getEntity().writeTo(byteArrayOutputStream);
            bytes = byteArrayOutputStream.toByteArray();

        } catch (IOException e) {

            log.error("Failed to get bytes from document store for document {} in case Id {}",
                    document.getDocumentLink().getDocumentBinaryUrl());
        }
        return bytes;
    }
}
