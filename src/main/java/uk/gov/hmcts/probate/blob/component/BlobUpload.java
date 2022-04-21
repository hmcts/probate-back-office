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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

    public void uploadFile(String[] files) throws IOException {

        // Create a BlobServiceClient object which will be used to create a container client
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
            .connectionString(storageConnectionString).buildClient();

        File blobFile = new File(getZippedFile(files));
        // Get a reference to a blob
        BlobClient blobClient = getContainerClient(blobServiceClient, containerName).getBlobClient(blobFile.getName());

        LOGGER.info("Uploading to Blob storage as blob:" + blobClient.getBlobUrl());

        // Upload the blob
        blobClient.uploadFromFile(blobFile.getPath());

    }

    private String getZippedFile(String[] filePaths) throws IOException {
        Path secureDir = Files.createTempDirectory("blobProcessing");
        String zipFileName = secureDir + "/Probate_Docs_" + LocalDate.now() + ".zip";
        FileOutputStream fos = new FileOutputStream(zipFileName);
        ZipOutputStream zos = new ZipOutputStream(fos);

        List<String> filesToBeZipped = Arrays.asList(filePaths);
        FileInputStream fis;

        //Zipping each file
        for (String filepath : filesToBeZipped) {
            File fileToBeZipped = new File(filepath);
            fis = new FileInputStream(fileToBeZipped);

            ZipEntry zipEntry = new ZipEntry(fileToBeZipped.getName());
            zos.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            while (fis.read(bytes) > 0) {
                zos.write(bytes, 0, bytes.length);
            }
            zos.closeEntry();
            fis.close();
        }
        //close resources
        zos.close();
        fos.close();
        secureDir.toFile().deleteOnExit(); // this line of code is not able to delete the directory for some reason
        return zipFileName;

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
