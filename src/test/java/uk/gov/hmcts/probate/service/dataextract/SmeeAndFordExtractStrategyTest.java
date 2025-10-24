package uk.gov.hmcts.probate.service.dataextract;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.probate.blob.component.BlobUpload;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static uk.gov.hmcts.probate.model.DataExtractType.SMEE_AND_FORD;

@ExtendWith(MockitoExtension.class)
class SmeeAndFordExtractStrategyTest {
    @Mock
    private BlobUpload blobUpload;

    @TempDir
    Path tempDir;

    private SmeeAndFordDataExtractStrategy strategy;

    private static final String CONNECTION_STRING = "UseDevelopmentStorage=true";

    @BeforeEach
    void setUp() {
        strategy = new SmeeAndFordDataExtractStrategy(blobUpload, CONNECTION_STRING);
    }

    @Test
    void matchesTypeAndGetType() {
        assertAll(
                () -> assertFalse(strategy.matchesType(null)),
                () -> assertTrue(strategy.matchesType(SMEE_AND_FORD)),
                () -> assertEquals(SMEE_AND_FORD, strategy.getType())
        );
    }

    @Test
    void getQueryPath() {
        assertEquals("templates/elasticsearch/caseMatching/grants_issued_date_range_query_smeeford.json",
                strategy.getQueryPath());
    }

    @Test
    void uploadToBlobStorageShouldUploadAndDeleteFile() throws Exception {
        File tempFile = Files.createFile(tempDir.resolve("test-upload.txt")).toFile();

        strategy.uploadToBlobStorage(tempFile);

        verify(blobUpload, times(1)).uploadFile(
                tempFile,
                "smee-and-ford-document-feed",
                "UseDevelopmentStorage=true"
        );

        assertFalse(tempFile.exists(), "Temp file should not exist after deletion");
    }
}
