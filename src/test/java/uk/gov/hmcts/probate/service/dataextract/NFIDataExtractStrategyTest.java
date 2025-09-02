package uk.gov.hmcts.probate.service.dataextract;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.probate.blob.component.BlobUpload;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;
import uk.gov.hmcts.probate.service.zip.ZipFileService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.DataExtractType.NATIONAL_FRAUD_INITIATIVE;

@ExtendWith(MockitoExtension.class)
class NFIDataExtractStrategyTest {

    @Mock
    private ZipFileService zipFileService;

    @Mock
    private BlobUpload blobUpload;

    private NFIDataExtractStrategy strategy;

    private static final String CONNECTION_STRING = "UseDevelopmentStorage=true";

    @BeforeEach
    void setUp() {
        strategy = new NFIDataExtractStrategy(zipFileService, blobUpload, CONNECTION_STRING);
    }

    @Test
    void matchesTypeAndGetType() {
        assertAll(
            () -> assertFalse(strategy.matchesType(null)),
            () -> assertTrue(strategy.matchesType(NATIONAL_FRAUD_INITIATIVE)),
            () -> assertEquals(NATIONAL_FRAUD_INITIATIVE, strategy.getType())
        );
    }

    @Test
    void generateZipFileDelegatesAndReturnsFile() throws Exception {
        String date = "2025-08-20";
        List<ReturnedCaseDetails> cases = Collections.emptyList();

        File temp = Files.createTempFile("tmp_nfi_", ".zip").toFile();
        File finalZip = new File("Probate_NFI_Docs_" + date + ".zip");

        when(zipFileService.createTempZipFile("Probate_NFI_Docs_" + date)).thenReturn(temp);
        when(zipFileService.generateZipFile(cases, temp, date, NATIONAL_FRAUD_INITIATIVE)).thenReturn(finalZip);

        File result = strategy.generateZipFile(cases, date);

        assertSame(finalZip, result);
        verify(zipFileService).createTempZipFile("Probate_NFI_Docs_" + date);
        verify(zipFileService).generateZipFile(cases, temp, date, NATIONAL_FRAUD_INITIATIVE);

        Files.deleteIfExists(temp.toPath());
    }

    @Test
    void generateZipFilePropagatesIOException() throws Exception {
        String date = "2025-08-20";

        when(zipFileService.createTempZipFile("Probate_NFI_Docs_" + date))
                .thenThrow(new IOException("cannot create temp zip"));

        assertThrows(IOException.class, () -> strategy.generateZipFile(Collections.emptyList(), date));
        verify(zipFileService).createTempZipFile("Probate_NFI_Docs_" + date);
        verify(zipFileService, never()).generateZipFile(anyList(), any(File.class), anyString(), any());
    }

    @Test
    void uploadToBlobStorageUploadsToNfiContainerAndDeletesFile() throws Exception {
        File file = Files.createTempFile("nfi_upload_test_", ".zip").toFile();

        strategy.uploadToBlobStorage(file);

        verify(blobUpload).uploadFile(file, "nfi-document-feed", CONNECTION_STRING);
        assertFalse(file.exists(), "File should be deleted after successful upload");
        Files.deleteIfExists(file.toPath());
    }
}