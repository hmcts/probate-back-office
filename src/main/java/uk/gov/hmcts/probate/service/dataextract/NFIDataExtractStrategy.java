package uk.gov.hmcts.probate.service.dataextract;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.blob.component.BlobUpload;
import uk.gov.hmcts.probate.model.DataExtractType;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;
import uk.gov.hmcts.probate.service.zip.ZipFileService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static uk.gov.hmcts.probate.model.DataExtractType.NATIONAL_FRAUD_INITIATIVE;

@Slf4j
@Service
@RequiredArgsConstructor
public class NFIDataExtractStrategy implements DataExtractStrategy {
    private final ZipFileService zipFileService;
    private final BlobUpload blobUpload;

    @Value(value = "${blobstorage.connection.nfi}")
    private String nfiStorageConnectionString;

    private static final String NFI_CONTAINER_NAME = "nfi-document-feed";

    @Override
    public boolean matchesType(DataExtractType type) {
        return NATIONAL_FRAUD_INITIATIVE.equals(type);
    }

    @Override
    public File generateZipFile(List<ReturnedCaseDetails> cases, String date) throws IOException {
        File tempFile = zipFileService.createTempZipFile("Probate_NFI_Docs_" + date);
        return zipFileService.generateZipFile(cases, tempFile, date, NATIONAL_FRAUD_INITIATIVE);
    }

    @Override
    public void uploadToBlobStorage(File file) throws IOException {
        blobUpload.uploadFile(file, NFI_CONTAINER_NAME, nfiStorageConnectionString);
        log.info("File uploaded to blob storage: {}", file.getName());
        Files.delete(file.toPath());
        log.info("Temp File deleted: {}", !file.exists());
    }

    @Override
    public DataExtractType getType() {
        return NATIONAL_FRAUD_INITIATIVE;
    }
}
