package uk.gov.hmcts.probate.blob.component;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import static java.util.Objects.nonNull;

@Slf4j
@Component
@Configuration
public class BlobUpload {

    @Value("${storage.connection}")
    public String storageConnectionString;

    private static final Logger LOGGER = Logger.getLogger(BlobUpload.class.getName());

    //Create a unique name for the container
    String containerName = "smee-and-ford-document-feed";

    public void uploadFile(File blobFile) throws IOException {

        // Create a BlobServiceClient object which will be used to create a container client
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
            .connectionString(storageConnectionString).buildClient();

        // Get a reference to a blob
        BlobClient blobClient = getContainerClient(blobServiceClient, containerName).getBlobClient(blobFile.getName());

        LOGGER.info("Uploading to Blob storage as blob:" + blobClient.getBlobUrl());

        // Upload the blob
        blobClient.uploadFromFile(blobFile.getPath());

    }


    // Create the container and return a container client object, this logic works fine for existing containers
    // but fails if the container is to be newly created. Needs fixing but not urgent
    private BlobContainerClient getContainerClient(BlobServiceClient blobServiceClient, String containerName) {
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);

        if (nonNull(containerClient)) {
            return  containerClient;
        } else {
            return blobServiceClient.createBlobContainer(containerName);
        }
    }
}
