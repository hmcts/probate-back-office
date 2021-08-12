package uk.gov.hmcts.probate.service.documentmanagement;

import com.fasterxml.jackson.core.JsonProcessingException;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.evidencemanagement.EvidenceManagementFileUpload;
import uk.gov.hmcts.reform.ccd.document.am.model.UploadResponse;

import java.io.IOException;

public interface DocumentManagementService {
    UploadResponse store(EvidenceManagementFileUpload file, DocumentType documentType) throws IOException;

    void expire(Document document) throws JsonProcessingException;
}
