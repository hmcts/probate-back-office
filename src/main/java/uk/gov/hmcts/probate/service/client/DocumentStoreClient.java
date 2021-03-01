package uk.gov.hmcts.probate.service.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.security.SecurityUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class DocumentStoreClient {

    protected CloseableHttpClient closeableHttpClient = HttpClientBuilder.create().build();
    private static final String SERVICE_AUTHORIZATION = "ServiceAuthorization";
    private static final String USER_ID = "user-id";
    private final SecurityUtils securityUtils;

    public byte[] retrieveDocument(Document document, String authHeaderValue) throws IOException {

        byte[] bytes = null;
        try {
            String userId = document.getDocumentGeneratedBy();
            if (userId == null) {
                userId = securityUtils.getSecurityDTO().getUserId();
            }

            HttpGet request = new HttpGet(document.getDocumentLink().getDocumentBinaryUrl());
            request.setHeader(SERVICE_AUTHORIZATION, authHeaderValue);
            request.setHeader(USER_ID, userId);
            log.info("About to retrieve " + document + " from dm-store with binary url: "
                    + document.getDocumentLink().getDocumentBinaryUrl());
            CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(request);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            closeableHttpResponse.getEntity().writeTo(byteArrayOutputStream);
            bytes = byteArrayOutputStream.toByteArray();
            log.info("Successfully retrieved " + document + " from dm-store with binary url: "
                    + document.getDocumentLink().getDocumentBinaryUrl());


        } catch (IOException e) {
            log.error("Failed to get bytes from document store for document {} in case Id {}",
                    document.getDocumentLink().getDocumentBinaryUrl());
            throw new IOException("Failed to get bytes from document store for "
                    + document.getDocumentFileName() + "with url "
                    + document.getDocumentLink().getDocumentBinaryUrl());
        }
        return bytes;
    }
}
