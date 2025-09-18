package uk.gov.hmcts.probate.service.dataextract;

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
public class SmeeAndFordDataExtractStrategy implements  DataExtractStrategy {
    private final BlobUpload blobUpload;

    private final String smeeAndFordStorageConnectionString;

    private static final String SMEE_AND_FORD_CONTAINER_NAME = "smee-and-ford-document-feed";

    public SmeeAndFordDataExtractStrategy(
            BlobUpload blobUpload,
            @Value("${blobstorage.connection.smeeandford}") String smeeAndFordStorageConnectionString) {
        this.blobUpload = blobUpload;
        this.smeeAndFordStorageConnectionString = smeeAndFordStorageConnectionString;
    }

    @Override
    public boolean matchesType(DataExtractType type) {
        return SMEE_AND_FORD.equals(type);
    }

    @Override
    public File generateZipFile(List<ReturnedCaseDetails> cases, String date) {
        throw new UnsupportedOperationException("Not implemented yet.");
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
