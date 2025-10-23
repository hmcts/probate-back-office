package uk.gov.hmcts.probate.blob.component;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;

import static java.util.Objects.nonNull;

@Service
@Slf4j
public class BlobUpload {

    public void uploadFile(File blobFile, String container, String connectionString) {
        // Create a BlobServiceClient object which will be used to create a container client
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(connectionString).buildClient();

        // Get a reference to a blob
        BlobContainerClient containerClient = getContainerClient(blobServiceClient, container);
        BlobClient blobClient = containerClient.getBlobClient(blobFile.getName());

        log.info("Uploading to Blob storage as blob: {} container: {}", blobClient.getBlobUrl(), container);

        // Upload the blob
        blobClient.uploadFromFile(blobFile.getPath(), true);
    }

    // Create the container and return a container client object, this logic works fine for existing containers
    // but fails if the container is to be newly created. Needs fixing but not urgent
    private BlobContainerClient getContainerClient(BlobServiceClient blobServiceClient, String containerName) {
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);

        if (nonNull(containerClient)) {
            return containerClient;
        } else {
            return blobServiceClient.createBlobContainer(containerName);
        }
    }
}
