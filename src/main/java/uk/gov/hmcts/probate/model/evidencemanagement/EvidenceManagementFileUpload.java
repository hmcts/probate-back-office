package uk.gov.hmcts.probate.model.evidencemanagement;

import lombok.Data;
import org.springframework.http.MediaType;

import java.util.UUID;

@Data
public class EvidenceManagementFileUpload {

    public static final String DEFAULT_FILE_EXTENSION = ".pdf";

    private final String fileName;
    private final MediaType contentType;
    private final byte[] bytes;

    public EvidenceManagementFileUpload(MediaType contentType, byte[] bytes) {
        fileName = UUID.randomUUID().toString() + DEFAULT_FILE_EXTENSION;
        this.contentType = contentType;
        this.bytes = bytes;
    }
}
