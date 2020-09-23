package uk.gov.hmcts.probate.service.evidencemanagement.upload;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import uk.gov.hmcts.probate.config.EvidenceManagementRestTemplate;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.evidencemanagement.EvidenceManagementFile;
import uk.gov.hmcts.probate.model.evidencemanagement.EvidenceManagementFileUpload;
import uk.gov.hmcts.probate.model.evidencemanagement.EvidenceManagementTTL;
import uk.gov.hmcts.probate.service.evidencemanagement.builder.DocumentManagementURIBuilder;
import uk.gov.hmcts.probate.service.evidencemanagement.header.HttpHeadersFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmUploadService implements UploadService {

    private final HttpHeadersFactory headers;
    private final EvidenceManagementRestTemplate evidenceManagementRestTemplate;
    private final DocumentManagementURIBuilder documentManagementURIBuilder;
    private final ObjectMapper objectMapper;

    @Override
    public EvidenceManagementFile store(EvidenceManagementFileUpload file) throws IOException {
        MultiValueMap<String, Object> parameters = UploadRequestBuilder.prepareRequest(file);

        @Nullable HashMap response = evidenceManagementRestTemplate.postForObject(
                documentManagementURIBuilder.buildUrl(),
                new HttpEntity<MultiValueMap>(parameters, headers.getMultiPartHttpHeader()),
                HashMap.class);

        ObjectMapper originalObjectMapper = new ObjectMapper();
        Map embedded = (Map) response.get("_embedded");
        List documents = (List) embedded.get("documents");

        return originalObjectMapper.readValue(originalObjectMapper.writeValueAsString(documents.get(0)), EvidenceManagementFile.class);
    }

    @Override
    public void expire(Document document) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(new EvidenceManagementTTL(ZonedDateTime.now()));

        evidenceManagementRestTemplate.patchForObject(
                document.getDocumentLink().getDocumentUrl(),
                new HttpEntity<>(json, headers.getApplicationJsonHttpHeader()),
                HashMap.class);
    }
}