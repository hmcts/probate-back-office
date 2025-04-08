package uk.gov.hmcts.probate.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.service.documentmanagement.DocumentManagementService;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Component
public class DocumentValidation {
    @Value("${document_management.fileupload.extensions}")
    private String allowedFileExtensions;

    @Value("${document_management.fileupload.mimetypes}")
    private String allowedMimeTypes;

    private final DocumentManagementService documentManagementService;

    @Autowired
    public DocumentValidation(final DocumentManagementService documentManagementService) {
        this.documentManagementService = documentManagementService;
    }

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

    /**
     * Checks that the referenced document has the expected Media Type. This check only compares the base and subtype
     * for equality - for example 'text/plain;charset=UTF-8' and 'text/plain;charset=UTF-16' are considered matching.
     *
     * @param caseId The case for which this check is being performed. Used for logging.
     * @param uploadedDocumentLink The document to validate the Media Type for
     * @param expectedType The expected Media Type
     * @return An empty Optional if document is of expected type, or a descriptive error String if not.
     */
    public Optional<String> validateUploadedDocumentIsType(
            final Long caseId,
            final DocumentLink uploadedDocumentLink,
            final MediaType expectedType) {

        final String docUrl = uploadedDocumentLink.getDocumentUrl();
        final int lastFSlash = docUrl.lastIndexOf('/');
        final String docId = docUrl.substring(lastFSlash + 1);

        final uk.gov.hmcts.reform.ccd.document.am.model.Document uploadedDocument = documentManagementService
                .getMetadataByUrl(docUrl);

        log.info("case {} got uploadedDocument with id[0..5]: [{}] mimetype: [{}] size: [{}]",
                caseId,
                docId.substring(0,6),
                uploadedDocument.mimeType,
                uploadedDocument.size);

        final MediaType actualType = MediaType.parseMediaType(uploadedDocument.mimeType);

        if (!actualType.equalsTypeAndSubtype(expectedType)) {
            log.info("case {} uploadedDocument has MediaType: [{}] when expecting [{}] so rejecting update",
                    caseId,
                    actualType,
                    expectedType);

            final String errMsg = MessageFormat.format(
                    "Uploaded file [{0}] has file type [{1}] which does not match [{2}]",
                    uploadedDocument.originalDocumentName,
                    actualType,
                    expectedType);
            return Optional.of(errMsg);
        }
        return Optional.empty();
    }

}
