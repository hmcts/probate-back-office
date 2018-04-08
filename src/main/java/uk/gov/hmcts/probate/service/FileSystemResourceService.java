package uk.gov.hmcts.probate.service;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
public class FileSystemResourceService {

    public FileSystemResource getFileSystemResource(String resourcePath) {
        try {
            InputStream in = this.getClass().getResourceAsStream(resourcePath);
            if (in == null) {
                return null;
            }

            File tempFile = File.createTempFile(String.valueOf(in.hashCode()), ".html");
            tempFile.deleteOnExit();
            FileOutputStream out = new FileOutputStream(tempFile);
            IOUtils.copy(in, out);

            return new FileSystemResource(tempFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
