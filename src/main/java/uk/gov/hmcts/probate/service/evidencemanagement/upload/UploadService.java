package uk.gov.hmcts.probate.service.evidencemanagement.upload;

import com.fasterxml.jackson.core.JsonProcessingException;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.evidencemanagement.EvidenceManagementFile;
import uk.gov.hmcts.probate.model.evidencemanagement.EvidenceManagementFileUpload;

import java.io.IOException;

public interface UploadService {
    EvidenceManagementFile store(EvidenceManagementFileUpload file) throws IOException;

    void expire(Document document) throws JsonProcessingException;
}
