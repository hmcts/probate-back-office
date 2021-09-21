package uk.gov.hmcts.probate.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Optional;

@Slf4j
@Component
public class FileSystemResourceService {

    public Optional<FileSystemResource> getFileSystemResource(String resourcePath) {

        return Optional.ofNullable(this.getClass().getClassLoader().getResourceAsStream(resourcePath))
                .map(in -> {
                    FileOutputStream out = null;
                    try {
                        File tempFile = File.createTempFile(String.valueOf(in.hashCode()), ".html");
                        tempFile.deleteOnExit();
                        out = new FileOutputStream(tempFile);
                        IOUtils.copy(in, out);
                        return new FileSystemResource(tempFile);
                    } catch (IOException e) {
                        log.warn("File system [ {} ] could not be found", resourcePath, e);
                        return null;
                    } finally {
                        if (out != null) {
                            safeClose(out);
                        }
                        safeClose(out);
                        safeClose(in);
                    }
                });
    }

    public String getFileFromResourceAsString(String resourcePath) {
        try {
            Optional<FileSystemResource> fileSystemResource = getFileSystemResource(resourcePath);
            if (fileSystemResource.isPresent()) {
                return FileUtils.readFileToString(fileSystemResource.get().getFile(), Charset.defaultCharset());
            }
            return null;
        } catch (IOException e) {
            log.error("Cannot read file system resource: " + resourcePath, e);
            return null;
        }
    }

    public static void safeClose(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
                log.error("Cannot close file system resource", e);
            }
        }
    }
}

