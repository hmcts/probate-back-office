package uk.gov.hmcts.probate.service.zip;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import uk.gov.hmcts.probate.exception.ZipFileException;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.ccd.raw.UploadDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;
import uk.gov.hmcts.probate.model.zip.ZippedDocumentFile;
import uk.gov.hmcts.probate.model.zip.ZippedManifestData;
import uk.gov.hmcts.probate.service.FileSystemResourceService;
import uk.gov.hmcts.probate.service.evidencemanagement.upload.EmUploadService;
import uk.gov.hmcts.probate.service.notification.SmeeAndFordPersonalisationService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.ADMON_WILL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.INTESTACY_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_DIGITAL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_ADMON_WILL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_INTESTACY_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.INTESTACY_GRANT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.ADMON_WILL_GRANT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_DIGITAL_GRANT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_INTESTACY_GRANT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_ADMON_WILL_GRANT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.OTHER;
import static uk.gov.hmcts.reform.probate.model.cases.DocumentType.WILL;

@Slf4j
@Service
@RequiredArgsConstructor
public class ZipFileService {
    private final ObjectMapper objectMapper;
    private final EmUploadService emUploadService;
    private final SmeeAndFordPersonalisationService smeeAndFordPersonalisationService;
    private final FileSystemResourceService fileSystemResourceService;
    private Path secureDir = null;
    private static final String PDF = ".pdf";
    private static final String CSV = ".csv";
    private static final String JSON = ".json";
    private static final String DELIMITER = "|";
    private static final String NEW_LINE = "\n";
    private static final DocumentType[] GRANT_TYPES = {DIGITAL_GRANT, ADMON_WILL_GRANT, INTESTACY_GRANT,
        WELSH_DIGITAL_GRANT, WELSH_ADMON_WILL_GRANT, WELSH_INTESTACY_GRANT};
    private static final DocumentType[] REISSUE_GRANT_TYPES = {DIGITAL_GRANT_REISSUE, INTESTACY_GRANT_REISSUE,
        ADMON_WILL_GRANT_REISSUE, WELSH_DIGITAL_GRANT_REISSUE, WELSH_INTESTACY_GRANT_REISSUE,
        WELSH_ADMON_WILL_GRANT_REISSUE};
    private static final String HEADER_ROW_FILE = "templates/dataExtracts/ManifestFileHeaderRow.csv";

    public void generateZipFile(List<ReturnedCaseDetails> cases, File tempFile) {
        log.info("generateZipFile for {} cases", cases.size());
        List<ZippedDocumentFile> filesToZip = new ArrayList<>();

        for (ReturnedCaseDetails returnedCaseDetails : cases) {
            filesToZip.addAll(getWillDocuments(returnedCaseDetails));
            filesToZip.addAll(getGrantDocuments(returnedCaseDetails));
            filesToZip.addAll(getReIssueGrantDocuments(returnedCaseDetails));
        }
        filesToZip.add(getSmeeAndFordCaseData(cases));
        try {
            zipMultipleDocs(filesToZip, tempFile);
        } catch (IOException e) {
            log.info("Exception occurred while generating zip file: {}", e);
            throw new ZipFileException(e.getMessage());
        }
    }

    private ZippedDocumentFile getSmeeAndFordCaseData(List<ReturnedCaseDetails> cases) {
        byte[] bytes = smeeAndFordPersonalisationService.getSmeeAndFordByteArray(cases);
        return ZippedDocumentFile.builder()
                .zippedManifestData(ZippedManifestData.builder()
                        .caseNumber("all")
                        .docFileType(CSV)
                        .docType("csv")
                        .build())
                .byteArrayResource(new ByteArrayResource(bytes))
                .build();
    }

    private Collection<ZippedDocumentFile> getWillDocuments(ReturnedCaseDetails caseDetails) {
        List<ZippedDocumentFile> filesToZip = new ArrayList<>();
        filesToZip.addAll(getScannedDocuments(caseDetails));
        filesToZip.addAll(getUploadedWillDocuments(caseDetails));
        return filesToZip;
    }

    private Collection<ZippedDocumentFile> getScannedDocuments(ReturnedCaseDetails caseDetails) {
        List<ZippedDocumentFile> filesToZip = new ArrayList<>();
        if (caseDetails.getData().getScannedDocuments() != null) {
            List<CollectionMember<ScannedDocument>> collect = caseDetails.getData()
                    .getScannedDocuments().stream()
                    .filter(this::filterScannedDocs)
                    .collect(Collectors.toList());

            log.info("scanned will {} docs for case {}", collect.size(), caseDetails.getId());
            int scannedDocIndex = 1;
            for (CollectionMember<ScannedDocument> doc : collect) {
                String url = doc.getValue().getUrl().getDocumentBinaryUrl();
                addZippedDocument(filesToZip, caseDetails, url, "scanned_"
                        + WILL.getTemplateName() + "_" + scannedDocIndex, PDF);
                scannedDocIndex++;
            }
        }
        return filesToZip;
    }

    private Collection<ZippedDocumentFile> getUploadedWillDocuments(ReturnedCaseDetails caseDetails) {
        List<ZippedDocumentFile> filesToZip = new ArrayList<>();
        if (caseDetails.getData().getBoDocumentsUploaded() != null) {
            List<CollectionMember<UploadDocument>> collect = caseDetails.getData()
                    .getBoDocumentsUploaded().stream()
                    .filter(this::filterUploadedDocs)
                    .collect(Collectors.toList());

            log.info("uploaded will {} docs for case {}", collect.size(), caseDetails.getId());
            int uploadedDocIndex = 1;
            for (CollectionMember<UploadDocument> doc : collect) {
                String url = doc.getValue().getDocumentLink().getDocumentBinaryUrl();
                addZippedDocument(filesToZip, caseDetails, url,
                        "uploaded_" + WILL.getTemplateName() + "_" + uploadedDocIndex, PDF);
                uploadedDocIndex++;
            }
        }
        return filesToZip;
    }

    private Collection<ZippedDocumentFile> getGrantDocuments(ReturnedCaseDetails caseDetails) {
        List<ZippedDocumentFile> filesToZip = new ArrayList<>();
        List<CollectionMember<Document>> collect = caseDetails.getData()
                .getProbateDocumentsGenerated().stream()
                .filter(collectionMember -> Arrays.asList(GRANT_TYPES).contains(collectionMember.getValue()
                        .getDocumentType()))
                .collect(Collectors.toList());

        log.info("{} grant docs for case {}", collect.size(), caseDetails.getId());
        for (CollectionMember<Document> doc : collect) {
            String url = doc.getValue().getDocumentLink().getDocumentBinaryUrl();
            addZippedDocument(filesToZip, caseDetails, url, doc.getValue().getDocumentType().getTemplateName(), PDF);
        }

        return filesToZip;
    }

    private Collection<ZippedDocumentFile> getReIssueGrantDocuments(ReturnedCaseDetails caseDetails) {
        List<ZippedDocumentFile> filesToZip = new ArrayList<>();
        List<CollectionMember<Document>> collect = caseDetails.getData()
                .getProbateDocumentsGenerated().stream()
                .filter(collectionMember -> Arrays.asList(REISSUE_GRANT_TYPES).contains(collectionMember.getValue()
                        .getDocumentType()))
                .collect(Collectors.toList());

        log.info("{} re-issue grant docs for case {}", collect.size(), caseDetails.getId());
        for (CollectionMember<Document> doc : collect) {
            String url = doc.getValue().getDocumentLink().getDocumentBinaryUrl();
            addZippedDocument(filesToZip, caseDetails, url, doc.getValue().getDocumentType().getTemplateName(), PDF);
        }

        return filesToZip;
    }

    private boolean filterScannedDocs(CollectionMember<ScannedDocument> collectionMember) {
        return collectionMember.getValue().getType().equalsIgnoreCase(OTHER.getTemplateName())
                && collectionMember.getValue().getSubtype().equalsIgnoreCase(WILL.getTemplateName());
    }

    private boolean filterUploadedDocs(CollectionMember<UploadDocument> collectionMember) {
        return WILL.getTemplateName().equalsIgnoreCase(collectionMember.getValue().getDocumentType().getTemplateName());
    }

    private void addZippedDocument(List<ZippedDocumentFile> filesToZip, ReturnedCaseDetails returnedCaseDetails,
                                   String url, String documentTypeName, String docFileType) {
        String documentId = url.substring(url.indexOf("/documents/") + 11, url.lastIndexOf("/"));
        ByteArrayResource byteArrayResource = null;
        String errorDescription = "";
        try {
            byteArrayResource = emUploadService.getDocumentByteArrayById(documentId);
        } catch (Exception e) {
            errorDescription = "Exception adding file from case id: " + returnedCaseDetails.getId().toString()
                    + " document id: " + documentId;
            log.info(errorDescription);
        } finally {
            ZippedDocumentFile zippedDocumentFile = ZippedDocumentFile.builder()
                    .byteArrayResource(byteArrayResource)
                    .zippedManifestData(ZippedManifestData.builder()
                            .caseNumber(returnedCaseDetails.getId().toString())
                            .documentId(documentId)
                            .docType(documentTypeName)
                            .docFileType(docFileType)
                            .subType(returnedCaseDetails.getData().getCaseType())
                            .errorDescription(errorDescription).build())
                    .build();
            log.info("file added: {} for case id {}", zippedDocumentFile.getZippedManifestData().getDocumentName(),
                    returnedCaseDetails.getId().toString());
            filesToZip.add(zippedDocumentFile);
        }
    }

    public File createTempZipFile(String zipName) throws IOException {
        if (secureDir == null) {
            secureDir = Paths.get("").toAbsolutePath();
            File file = ResourceUtils.getFile(secureDir.toString() + "/" + zipName + ".zip");
            if (file.exists()) {
                Files.delete(file.toPath());
            }
        }

        log.info("secureDir:" + secureDir);
        Path tempFilePath = Files.createTempFile(secureDir, zipName, ".zip");
        boolean isReadable = tempFilePath.toFile().setReadable(true, true);
        boolean isWritable = tempFilePath.toFile().setWritable(true, true);
        log.info("tempFile: {} and file is isReadable {} and isWritable {}",
                tempFilePath.toAbsolutePath(), isReadable, isWritable);
        return tempFilePath.toFile();
    }

    private void zipMultipleDocs(List<ZippedDocumentFile> files, File tempFile) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(tempFile); ZipOutputStream zipOut = new ZipOutputStream(fos)) {
            List<ZippedDocumentFile> filteredDocumentFiles = files.stream()
                    .filter(file -> file.getByteArrayResource() != null)
                    .collect(Collectors.toList());
            for (ZippedDocumentFile file : filteredDocumentFiles) {
                log.info("Zipping file {}", file.getZippedManifestData().getDocumentName());
                ZipEntry zipEntry = new ZipEntry(file.getZippedManifestData().getDocumentName());
                zipOut.putNextEntry(zipEntry);
                zipOut.write(file.getByteArrayResource().getByteArray());
            }
            ZippedDocumentFile manifestFile = generateManifestFile(files);
            ZipEntry zipEntry = new ZipEntry(manifestFile.getZippedManifestData().getDocumentName());
            zipOut.putNextEntry(zipEntry);
            zipOut.write(manifestFile.getByteArrayResource().getByteArray());
        }
    }

    private void addHeaderRow(StringBuilder data) {
        String header = fileSystemResourceService.getFileFromResourceAsString(HEADER_ROW_FILE);
        data.append(header);
    }

    private ZippedDocumentFile generateManifestFile(List<ZippedDocumentFile> files) {
        StringBuilder data = new StringBuilder();
        addHeaderRow(data);
        data.append(NEW_LINE);

        for (ZippedDocumentFile file : files) {
            data.append(file.getZippedManifestData().getCaseNumber());
            data.append(DELIMITER);
            data.append(file.getZippedManifestData().getDocumentId());
            data.append(DELIMITER);
            data.append(file.getZippedManifestData().getDocType());
            data.append(DELIMITER);
            data.append(file.getZippedManifestData().getSubType());
            data.append(DELIMITER);
            data.append(file.getZippedManifestData().getCaseType());
            data.append(DELIMITER);
            data.append(file.getZippedManifestData().getDocumentName());
            data.append(DELIMITER);
            data.append(file.getZippedManifestData().getErrorDescription());
            data.append(NEW_LINE);
        }

        return ZippedDocumentFile.builder()
                .byteArrayResource(new ByteArrayResource(data.toString().getBytes(StandardCharsets.UTF_8)))
                .zippedManifestData(ZippedManifestData.builder()
                        .caseNumber("manifest")
                        .docType("cases")
                        .docFileType(JSON)
                        .errorDescription("").build())
                .build();
    }

}
