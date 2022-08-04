package uk.gov.hmcts.probate.blob.component;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static java.util.Objects.nonNull;

@Service
@Slf4j
public class BlobUpload {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Value(value = "${blobstorage.connection}")
    private String storageConnectionString;

    //Create a unique name for the container
    String containerName = "smee-and-ford-document-feed";

    public void upload(BinaryData data) {
        String blobZipFileName = "Probate_Docs_" + DATE_FORMAT.format(LocalDate.now()) + ".zip";
        log.info("Blob connection : " + storageConnectionString);
        // Create a BlobServiceClient object which will be used to create a container client
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(storageConnectionString).buildClient();

        // Get a reference to a blob
        BlobClient blobClient = getContainerClient(blobServiceClient, containerName).getBlobClient(blobZipFileName);

        log.info("Uploading to Blob storage as blob:" + blobClient.getBlobUrl());

        // Upload the blob
        try {
            blobClient.upload(data, true);
        } catch (Exception e) {
            log.error("Azure blob upload failed {}", e);

        }

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
