package uk.gov.hmcts.probate.service.zip;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.ccd.raw.UploadDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;
import uk.gov.hmcts.probate.model.zip.ZippedDocumentFile;
import uk.gov.hmcts.probate.model.zip.ZippedManifestData;
import uk.gov.hmcts.probate.service.evidencemanagement.upload.EmUploadService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
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
    private Path secureDir = null;
    private static final String PDF = ".pdf";
    private static final String JSON = ".json";
    private static final DocumentType[] GRANT_TYPES = {DIGITAL_GRANT, ADMON_WILL_GRANT, INTESTACY_GRANT,
        WELSH_DIGITAL_GRANT, WELSH_ADMON_WILL_GRANT, WELSH_INTESTACY_GRANT};
    private static final DocumentType[] REISSUE_GRANT_TYPES = {DIGITAL_GRANT_REISSUE, INTESTACY_GRANT_REISSUE,
        ADMON_WILL_GRANT_REISSUE, WELSH_DIGITAL_GRANT_REISSUE, WELSH_INTESTACY_GRANT_REISSUE,
        WELSH_ADMON_WILL_GRANT_REISSUE};

    public void generateZipFile(List<ReturnedCaseDetails> cases, File tempFile) {
        log.info("generateZipFile for {} cases", cases.size());
        List<ZippedDocumentFile> filesToZip = new ArrayList<>();

        for (ReturnedCaseDetails returnedCaseDetails : cases) {
            filesToZip.addAll(getWillDocuments(returnedCaseDetails));
            filesToZip.addAll(getGrantDocuments(returnedCaseDetails));
            filesToZip.addAll(getReIssueGrantDocuments(returnedCaseDetails));
        }
        try {
            zipMultipleDocs(filesToZip, tempFile);
        } catch (IOException e) {
            log.info("Exception occurred while generating zip file: {}", e);
            throw new RuntimeException(e);
        }
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
                    .filter(collectionMember -> filterScannedDocs(collectionMember)
                    )
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
                    .filter(collectionMember -> filterUploadedDocs(collectionMember))
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

    public Collection<ZippedDocumentFile> getReIssueGrantDocuments(ReturnedCaseDetails caseDetails) {
        List<ZippedDocumentFile> filesToZip = new ArrayList<>();
        List<CollectionMember<Document>> collect = caseDetails.getData()
                .getProbateDocumentsGenerated().stream()
                .filter(collectionMember -> Arrays.asList(REISSUE_GRANT_TYPES).contains(collectionMember.getValue()
                        .getDocumentType()))
                .collect(Collectors.toList());

        log.info("{} grant docs for case {}", collect.size(), caseDetails.getId());
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
        return collectionMember.getValue().getDocumentType().getTemplateName().equalsIgnoreCase(WILL.getTemplateName());
    }

    private void addZippedDocument(List<ZippedDocumentFile> filesToZip, ReturnedCaseDetails returnedCaseDetails,
                                   String url, String documentTypeName, String docFileType) {
        String documentId = url.substring(url.indexOf("/documents/") + 11, url.lastIndexOf("/"));
        ByteArrayResource byteArrayResource;
        String errorDescription = "";
        try {
            byteArrayResource = emUploadService.getDocument(documentId);
        } catch (Exception e) {
            byteArrayResource = null;
            errorDescription = "Exception adding file from case id: " + returnedCaseDetails.getId().toString()
                    + " document id: " + documentId;
            log.info(errorDescription);
        }
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
        log.info("file added:" + zippedDocumentFile.getZippedManifestData().getDocumentName());
        filesToZip.add(zippedDocumentFile);
    }

    public File createTempZipFile(String zipName) throws IOException {
        if (secureDir == null) {
            secureDir = Files.createTempDirectory("zip");
            secureDir.toFile().deleteOnExit();
        }

        log.info("secureDir:" + secureDir.toAbsolutePath());
        Path tempFile = Files.createFile(
                Paths.get(secureDir.toAbsolutePath() + "/" + zipName + ".zip"));
        log.info("tempFile:" + tempFile.toAbsolutePath());
        return tempFile.toFile();
    }

    private void zipMultipleDocs(List<ZippedDocumentFile> files, File tempFile) throws IOException {
        FileOutputStream fos = new FileOutputStream(tempFile);

        ZipOutputStream zipOut = new ZipOutputStream(fos);
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
        zipOut.close();
        fos.close();
    }

    private ZippedDocumentFile generateManifestFile(List<ZippedDocumentFile> files) throws IOException {
        JSONArray jsonArray = new JSONArray();
        for (ZippedDocumentFile file : files) {
            jsonArray.put(objectMapper.writeValueAsString(file.getZippedManifestData()));
        }
        ZippedDocumentFile zippedDocumentFile = ZippedDocumentFile.builder()
                .byteArrayResource(new ByteArrayResource(jsonArray.toString().getBytes(StandardCharsets.UTF_8)))
                .zippedManifestData(ZippedManifestData.builder()
                        .caseNumber("All")
                        .docType("cases")
                        .docFileType(JSON)
                        .errorDescription("").build())
                .build();
        return zippedDocumentFile;
    }

}
