package uk.gov.hmcts.probate.service;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Component
public class DocumentValidation {
    @Value("${document_management.fileupload.extensions}")
    private String allowedFileExtensions;

    @Value("${document_management.fileupload.mimetypes}")
    private String allowedMimeTypes;

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
