package uk.gov.hmcts.probate.service.evidencemanagement.upload;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import uk.gov.hmcts.probate.config.EvidenceManagementRestTemplate;
import uk.gov.hmcts.probate.exception.ClientDataException;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.evidencemanagement.EvidenceManagementFile;
import uk.gov.hmcts.probate.model.evidencemanagement.EvidenceManagementFileUpload;
import uk.gov.hmcts.probate.model.evidencemanagement.EvidenceManagementTTL;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.evidencemanagement.builder.DocumentManagementURIBuilder;
import uk.gov.hmcts.probate.service.evidencemanagement.header.HttpHeadersFactory;
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;
import uk.gov.hmcts.reform.ccd.document.am.feign.CaseDocumentClient;
import uk.gov.hmcts.reform.ccd.document.am.model.DocumentUploadRequest;
import uk.gov.hmcts.reform.ccd.document.am.model.UploadResponse;

import javax.annotation.Nullable;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@ComponentScan("uk.gov.hmcts.reform.ccd.document.am.feign")
public class EmUploadService implements UploadService {

    private final HttpHeadersFactory headers;
    private final EvidenceManagementRestTemplate evidenceManagementRestTemplate;
    private final DocumentManagementURIBuilder documentManagementURIBuilder;
    private final ObjectMapper objectMapper;
    private final SecurityUtils securityUtils;
    private final CaseDocumentClient caseDocumentClient;

    private static <T> T nonNull(@Nullable T result) {
        try {
            Assert.state(result != null, "Entity should be non null in EmUploadService");
        } catch (IllegalStateException e) {
            throw new ClientDataException(e.getMessage());
        }
        return result;
    }

    @Override
    @Nullable
    public EvidenceManagementFile store(EvidenceManagementFileUpload file, DocumentType documentType) throws IOException {
        DocumentUploadRequest documentUploadRequest = UploadRequestBuilder.perpareDocumentUploadRequest(file,
            documentType);

        SecurityDTO securityDTO = securityUtils.getSecurityDTO();
        String auth = securityDTO.getAuthorisation();
        String s2s = securityDTO.getServiceAuthorisation();

        log.info("auth:" + auth);
        log.info("s2s:" + s2s);
        log.info("documentUploadRequest.getCaseTypeId:" + documentUploadRequest.getCaseTypeId());
        log.info("documentUploadRequest.getClassification:" + documentUploadRequest.getClassification());
        log.info("documentUploadRequest.getJurisdictionId:" + documentUploadRequest.getJurisdictionId());
        log.info("documentUploadRequest.getFiles:" + documentUploadRequest.getFiles().size());
        UploadResponse uploadResponse =
            caseDocumentClient.uploadDocuments(auth, s2s, documentUploadRequest.getCaseTypeId(),
                documentUploadRequest.getJurisdictionId(), documentUploadRequest.getFiles());
                //uploadDocuments(auth, s2s, documentUploadRequest);

        ObjectMapper originalObjectMapper = new ObjectMapper();
        return originalObjectMapper
            .readValue(originalObjectMapper.writeValueAsString(uploadResponse.getDocuments().get(0)),
                EvidenceManagementFile.class);
    }

    @Override
    @Nullable
    public EvidenceManagementFile store(EvidenceManagementFileUpload file) throws IOException {
        MultiValueMap<String, Object> parameters = UploadRequestBuilder.prepareRequest(file);

        HashMap<String, HashMap<String, EvidenceManagementFile>> response =
            nonNull(evidenceManagementRestTemplate.postForObject(
                documentManagementURIBuilder.buildUrl(),
                new HttpEntity<MultiValueMap>(parameters, headers.getMultiPartHttpHeader()),
                HashMap.class));

        ObjectMapper originalObjectMapper = new ObjectMapper();
        Map embedded = response.get("_embedded");
        List documents = (List) embedded.get("documents");

        return originalObjectMapper
            .readValue(originalObjectMapper.writeValueAsString(documents.get(0)), EvidenceManagementFile.class);
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