package uk.gov.hmcts.probate.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Slf4j
@Component
@SuppressWarnings("squid:S5443")
public class FileSystemResourceService {

    public Optional<FileSystemResource> getFileSystemResource(String resourcePath) {

        final InputStream ins = this.getClass().getClassLoader().getResourceAsStream(resourcePath);

        return Optional.ofNullable(ins)
            .map(in -> {
                FileOutputStream out = null;
                try (ins) {
                    Path secureDir = Files.createTempDirectory("");
                    Path tempFile = Files.createTempFile(
                        Paths.get(secureDir.toAbsolutePath().toString()), "", ".html");
                    secureDir.toFile().deleteOnExit();
                    tempFile.toFile().deleteOnExit();
                    out = new FileOutputStream(tempFile.toFile());
                    IOUtils.copy(in, out);
                    return new FileSystemResource(tempFile.toFile());
                } catch (IOException e) {
                    log.error("File system [ {} ] could not be found", resourcePath, e);
                    return null;
                }
            });
    }

    public String getFileFromResourceAsString(String resourcePath) {
        try {
            Optional<FileSystemResource> fileSystemResource = getFileSystemResource(resourcePath);
            if (fileSystemResource.isPresent()) {
                return FileUtils.readFileToString(fileSystemResource.get().getFile(), Charset.defaultCharset());
            }
        } catch (IOException | NullPointerException e) {
            log.error("Cannot read file system resource: {}", resourcePath, e);
        }
        return null;
    }

}
