package uk.gov.hmcts.probate.service.documentmanagement;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.evidencemanagement.EvidenceManagementFileUpload;
import uk.gov.hmcts.reform.ccd.document.am.model.UploadResponse;

import java.io.IOException;
import java.util.List;

public interface DocumentManagementService {
    UploadResponse upload(EvidenceManagementFileUpload file, DocumentType documentType);

    UploadResponse uploadForCitizen(List<MultipartFile> multipartFileList, String authorizationToken,
                                    DocumentType documentType);

    void delete(Document document) throws JsonProcessingException;

    byte[] getDocument(Document document) throws IOException;

    byte[] getDocumentByBinaryUrl(String binaryUrl) throws IOException;

    uk.gov.hmcts.reform.ccd.document.am.model.Document getMetadataByUrl(String documentUrl);
}
