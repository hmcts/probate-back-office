package uk.gov.hmcts.probate.service.bulkscan;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import uk.gov.hmcts.bulkscan.enums.EnvelopeProcessStatus;
import uk.gov.hmcts.bulkscan.type.IPdfProcessor;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.evidencemanagement.EvidenceManagementFileUpload;
import uk.gov.hmcts.probate.service.documentmanagement.DocumentManagementService;
import uk.gov.hmcts.reform.ccd.document.am.model.UploadResponse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class BulkScanPdfProcessor implements IPdfProcessor {

    private final DocumentManagementService documentManagementService;

    private HashMap<File, UploadResponse> uploadedPdfs;

    @Override
    public EnvelopeProcessStatus processPdfList(List<File> pdfFiles) {

        var status = EnvelopeProcessStatus.SUCCESS;
        uploadedPdfs = new HashMap<File, UploadResponse>();
        for (File p: pdfFiles) {
            log.info("Uploading file {}", p);
            try {
                uploadedPdfs.put(p, documentManagementService.upload(
                        getEmFile(Files.readAllBytes(p.toPath())),
                        DocumentType.OTHER
                ));
            } catch (IOException e) {
                status = EnvelopeProcessStatus.ERRORS;
                e.printStackTrace();
            }
        }
        return status;
    }

    private EvidenceManagementFileUpload getEmFile(byte[] bytes) {
        return new EvidenceManagementFileUpload(MediaType.APPLICATION_PDF, bytes);
    }
}
