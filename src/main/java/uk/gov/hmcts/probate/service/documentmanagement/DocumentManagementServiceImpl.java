package uk.gov.hmcts.probate.service.documentmanagement;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.evidencemanagement.EvidenceManagementFileUpload;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.reform.ccd.document.am.feign.CaseDocumentClient;
import uk.gov.hmcts.reform.ccd.document.am.model.DocumentUploadRequest;
import uk.gov.hmcts.reform.ccd.document.am.model.UploadResponse;

import javax.annotation.Nullable;
import java.io.IOException;
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
    public UploadResponse store(EvidenceManagementFileUpload file, DocumentType documentType) throws IOException {
        DocumentUploadRequest documentUploadRequest =
            documentManagementRequestBuilder.perpareDocumentUploadRequest(file,
            documentType);

        SecurityDTO securityDTO = securityUtils.getSecurityDTO();
        String auth = securityDTO.getAuthorisation();
        String s2s = securityDTO.getServiceAuthorisation();

        return caseDocumentClient.uploadDocuments(auth, s2s, documentUploadRequest.getCaseTypeId(),
            documentUploadRequest.getJurisdictionId(), documentUploadRequest.getFiles(), PRIVATE);
    }

    @Override
    public void expire(Document document) throws JsonProcessingException {
        SecurityDTO securityDTO = securityUtils.getSecurityDTO();
        String auth = securityDTO.getAuthorisation();
        String s2s = securityDTO.getServiceAuthorisation();
        String selfHref = document.getDocumentLink().getDocumentUrl();
        UUID docId = UUID.fromString(selfHref.substring(selfHref.length() - DOC_UUID_LENGTH));
        caseDocumentClient.deleteDocument(auth, s2s, docId, DELETE_PERMANENT);
    }
}