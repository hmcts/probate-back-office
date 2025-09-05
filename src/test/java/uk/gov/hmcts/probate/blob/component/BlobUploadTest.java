package uk.gov.hmcts.probate.blob.component;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedConstruction;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BlobUploadTest {

    @TempDir
    Path tempDir;

    @Test
    void uploadFileUsesExistingContainerClientAndUploads() throws Exception {
        File tempFile = Files.createFile(tempDir.resolve("test.txt")).toFile();

        BlobServiceClient blobServiceClient = mock(BlobServiceClient.class);
        BlobContainerClient existingContainerClient = mock(BlobContainerClient.class);
        BlobClient blobClient = mock(BlobClient.class);

        when(blobServiceClient.getBlobContainerClient("my-container")).thenReturn(existingContainerClient);
        when(existingContainerClient.getBlobClient("test.txt")).thenReturn(blobClient);
        when(blobClient.getBlobUrl()).thenReturn("https://example.blob.core.windows.net/my-container/test.txt");

        try (MockedConstruction<BlobServiceClientBuilder> mocked =
                     mockConstruction(BlobServiceClientBuilder.class, (builderMock, ctx) -> {
                         when(builderMock.connectionString(anyString())).thenReturn(builderMock);
                         when(builderMock.buildClient()).thenReturn(blobServiceClient);
                     })) {

            BlobUpload sut = new BlobUpload();

            sut.uploadFile(tempFile, "my-container", "UseDevelopmentStorage=true");

            verify(blobServiceClient, times(1)).getBlobContainerClient("my-container");
            verify(blobServiceClient, never()).createBlobContainer(anyString());
            verify(existingContainerClient, times(1)).getBlobClient("test.txt");
            verify(blobClient, times(1)).uploadFromFile(tempFile.getPath(), true);
        }
    }

    @Test
    void uploadFileCreatesContainerWhenGetReturnsNullAndUploads() throws Exception {
        File tempFile = Files.createFile(tempDir.resolve("newfile.bin")).toFile();

        BlobServiceClient blobServiceClient = mock(BlobServiceClient.class);
        BlobContainerClient nullReturn = null;
        BlobContainerClient createdContainerClient = mock(BlobContainerClient.class);
        BlobClient blobClient = mock(BlobClient.class);

        when(blobServiceClient.getBlobContainerClient("new-container")).thenReturn(nullReturn);
        when(blobServiceClient.createBlobContainer("new-container")).thenReturn(createdContainerClient);
        when(createdContainerClient.getBlobClient("newfile.bin")).thenReturn(blobClient);
        when(blobClient.getBlobUrl()).thenReturn("https://example.blob.core.windows.net/new-container/newfile.bin");

        try (MockedConstruction<BlobServiceClientBuilder> mocked =
                     mockConstruction(BlobServiceClientBuilder.class, (builderMock, ctx) -> {
                         when(builderMock.connectionString(anyString())).thenReturn(builderMock);
                         when(builderMock.buildClient()).thenReturn(blobServiceClient);
                     })) {

            BlobUpload sut = new BlobUpload();

            sut.uploadFile(tempFile, "new-container", "UseDevelopmentStorage=true");

            verify(blobServiceClient, times(1)).getBlobContainerClient("new-container");
            verify(blobServiceClient, times(1)).createBlobContainer("new-container");
            verify(createdContainerClient, times(1)).getBlobClient("newfile.bin");
            verify(blobClient, times(1)).uploadFromFile(tempFile.getPath(), true);
        }
    }
}