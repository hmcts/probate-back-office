package uk.gov.hmcts.probate.service.documentmanagement;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.hmcts.probate.exception.ClientException;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.evidencemanagement.EvidenceManagementFileUpload;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.reform.ccd.document.am.feign.CaseDocumentClient;
import uk.gov.hmcts.reform.ccd.document.am.model.DocumentUploadRequest;
import uk.gov.hmcts.reform.ccd.document.am.model.UploadResponse;

import jakarta.annotation.Nullable;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static uk.gov.hmcts.reform.ccd.document.am.model.Classification.PRIVATE;

@Slf4j
@Service
@RequiredArgsConstructor
@ComponentScan("uk.gov.hmcts.reform.ccd.document.am.feign")
public class DocumentManagementServiceImpl implements DocumentManagementService {

    private static final int DOC_UUID_LENGTH = 36;
    private static final boolean DELETE_PERMANENT = true;
    private final SecurityUtils securityUtils;
    private final CaseDocumentClient caseDocumentClient;
    private final DocumentManagementRequestBuilder documentManagementRequestBuilder;

    @Override
    @Nullable
    public UploadResponse upload(EvidenceManagementFileUpload file, DocumentType documentType) {
        DocumentUploadRequest documentUploadRequest =
            documentManagementRequestBuilder.perpareDocumentUploadRequest(file,
            documentType);

        SecurityDTO securityDTO = securityUtils.getSecurityDTO();
        String auth = securityUtils.getBearerToken(securityDTO.getAuthorisation());

        return caseDocumentClient.uploadDocuments(auth, securityDTO.getServiceAuthorisation(),
                documentUploadRequest.getCaseTypeId(), documentUploadRequest.getJurisdictionId(),
                documentUploadRequest.getFiles(), PRIVATE);
    }

    @Override
    @Nullable
    public UploadResponse uploadForCitizen(List<MultipartFile> multipartFileList, String authorizationToken,
                                           DocumentType documentType) {
        SecurityDTO securityDTO = securityUtils.getSecurityDTO();
        String serviceAuthorisation = securityDTO.getServiceAuthorisation();
        String auth  = securityUtils.getBearerToken(authorizationToken);

        DocumentUploadRequest documentUploadRequest =
            documentManagementRequestBuilder.perpareDocumentUploadRequestForCitizen(multipartFileList,
                documentType);

        return caseDocumentClient.uploadDocuments(auth, serviceAuthorisation,
            documentUploadRequest.getCaseTypeId(), documentUploadRequest.getJurisdictionId(),
            documentUploadRequest.getFiles(), PRIVATE);
    }

    @Override
    public void delete(Document document) throws JsonProcessingException {
        SecurityDTO securityDTO = securityUtils.getSecurityDTO();
        String auth = securityDTO.getAuthorisation();
        String s2s = securityDTO.getServiceAuthorisation();
        String selfHref = document.getDocumentLink().getDocumentUrl();
        UUID docId = UUID.fromString(selfHref.substring(selfHref.length() - DOC_UUID_LENGTH));
        log.info("Deleting document wth id:{}", docId.toString());
        caseDocumentClient.deleteDocument(auth, s2s, docId, DELETE_PERMANENT);
    }

    @Override
    public byte[] getDocument(Document document) throws IOException {
        String binaryUrl = document.getDocumentLink().getDocumentBinaryUrl();
        return getDocumentByBinaryUrl(binaryUrl);
    }

    @Override
    public byte[] getDocumentByBinaryUrl(String binaryUrl) throws IOException {
        String auth = securityUtils.getCaseworkerToken();
        String s2s = securityUtils.generateServiceToken();
        ResponseEntity<Resource> response = caseDocumentClient.getDocumentBinary(auth, s2s, binaryUrl);
        Resource body = response.getBody();
        if (body != null) {
            return IOUtils.toByteArray(body.getInputStream());
        } else {
            throw new ClientException(500, "No body retrieved for document resource: " + binaryUrl);
        }
    }

    @Override
    public uk.gov.hmcts.reform.ccd.document.am.model.Document getMetadataByUrl(final String docUrl) {
        final SecurityDTO securityDTO = securityUtils.getSecurityDTO();
        final String auth = securityDTO.getAuthorisation();
        final String s2s = securityDTO.getServiceAuthorisation();

        return caseDocumentClient.getMetadataForDocument(auth, s2s, docUrl);
    }
}
