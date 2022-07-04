package uk.gov.hmcts.probate.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Component
public class DocumentValidation {
    @Value("${document_management.fileupload.extensions}")
    private String allowedFileExtensions;

    @Value("${document_management.fileupload.mimetypes}")
    private String allowedMimeTypes;

    public List<String> validateFiles(List<MultipartFile> files) {
        List<String> result = new ArrayList<>();
        if (files == null || files.isEmpty()) {
            log.error("Zero files received by the API endpoint.");
            result.add("Error: no files passed");
            return result;
        }

        if (files.size() > 10) {
            log.error("Too many files passed to the API endpoint");
            result.add("Error: too many files");
            return result;
        }
        
        List<String> invalidFiles = files.stream()
            .filter(f -> !isValid(f))
            .map(f -> "Error: invalid file type: " + f.getName())
            .collect(Collectors.toList());

        if (!invalidFiles.isEmpty()) {
            return invalidFiles;
        }
    
        return result;
    }

    public boolean isValid(MultipartFile file) {
        return validFileType(file.getOriginalFilename())
                && validMimeType(file.getContentType())
                && validFileSize(file);
    }

    public boolean validMimeType(final String mimeType) {
        return StringUtils.containsIgnoreCase(allowedMimeTypes, mimeType);
    }

    public boolean validFileType(final String filename) {
        return StringUtils.containsIgnoreCase(allowedFileExtensions, FilenameUtils.getExtension(filename));
    }

    public boolean validFileSize(final MultipartFile file) {
        return file.getSize() < (1024 * 1024 * 10);
    }

}
