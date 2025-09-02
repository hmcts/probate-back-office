package uk.gov.hmcts.probate.service.dataextract;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.blob.component.BlobUpload;
import uk.gov.hmcts.probate.model.DataExtractType;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static uk.gov.hmcts.probate.model.DataExtractType.SMEE_AND_FORD;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmeeAndFOrdDataExtractStrategy implements  DataExtractStrategy {
    private final BlobUpload blobUpload;

    @Value(value = "${blobstorage.connection.smeeandford}")
    private String smeeAndFordStorageConnectionString;

    private static final String SMEE_AND_FORD_CONTAINER_NAME = "smee-and-ford-document-feed";

    @Override
    public boolean matchesType(DataExtractType type) {
        return SMEE_AND_FORD.equals(type);
    }

    @Override
    public File generateZipFile(List<ReturnedCaseDetails> cases, String date) {
        //not implemented
        return null;
    }

    @Override
    public void uploadToBlobStorage(File file) throws IOException {
        blobUpload.uploadFile(file, SMEE_AND_FORD_CONTAINER_NAME, smeeAndFordStorageConnectionString);
        log.info("File uploaded to blob storage: {}", file.getName());
        Files.delete(file.toPath());
        log.info("Temp File deleted: {}", !file.exists());
    }

    @Override
    public DataExtractType getType() {
        return SMEE_AND_FORD;
    }
}
