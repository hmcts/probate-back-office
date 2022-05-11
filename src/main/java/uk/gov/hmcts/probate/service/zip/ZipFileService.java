package uk.gov.hmcts.probate.service.zip;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.ccd.raw.UploadDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;
import uk.gov.hmcts.probate.model.zip.ZippedDocumentFile;
import uk.gov.hmcts.probate.service.evidencemanagement.upload.EmUploadService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static uk.gov.hmcts.probate.model.DocumentType.*;
import static uk.gov.hmcts.reform.probate.model.cases.DocumentType.WILL;

@Slf4j
@Service
@RequiredArgsConstructor
public class ZipFileService {
    private final EmUploadService emUploadService;
    private Path secureDir = null;
    private static final String PDF = ".pdf";
    private static final String CSV = ".csv";

    public void generateZipFile(List<ReturnedCaseDetails> cases, File tempFile) {
        log.info("zipIssuedGrants for {} cases", cases.size());
        List<ZippedDocumentFile> filesToZip = new ArrayList<>();
        for (ReturnedCaseDetails returnedCaseDetails : cases) {
            filesToZip.addAll(getWillDocuments(returnedCaseDetails));
            filesToZip.addAll(getGrantDocuments(returnedCaseDetails));
            filesToZip.addAll(getReIssueGrantDocuments(returnedCaseDetails));
        }
        filesToZip.add(generateManifestFile());
        try {
            zipMultipleDocs(filesToZip, tempFile);
        } catch (IOException e) {
            log.info("error: {}", e);
            throw new RuntimeException(e);
        }
    }

    private ZippedDocumentFile generateManifestFile() {
        return null;
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
                .filter(collectionMember -> collectionMember.getValue().getDocumentType().equals(DIGITAL_GRANT))
                .collect(Collectors.toList());

        log.info("{} grant docs for case {}", collect.size(), caseDetails.getId());
        for (CollectionMember<Document> doc : collect) {
            String url = doc.getValue().getDocumentLink().getDocumentBinaryUrl();
            addZippedDocument(filesToZip, caseDetails, url, DIGITAL_GRANT.getTemplateName(), PDF);
        }

        return filesToZip;
    }

    public Collection<ZippedDocumentFile> getReIssueGrantDocuments(ReturnedCaseDetails caseDetails) {
        List<ZippedDocumentFile> filesToZip = new ArrayList<>();
        List<CollectionMember<Document>> collect = caseDetails.getData()
                .getProbateDocumentsGenerated().stream()
                .filter(collectionMember -> collectionMember.getValue().getDocumentType().equals(DIGITAL_GRANT_REISSUE))
                .collect(Collectors.toList());

        log.info("{} grant docs for case {}", collect.size(), caseDetails.getId());
        for (CollectionMember<Document> doc : collect) {
            String url = doc.getValue().getDocumentLink().getDocumentBinaryUrl();
            addZippedDocument(filesToZip, caseDetails, url, DIGITAL_GRANT_REISSUE.getTemplateName(), PDF);
        }

        return filesToZip;
    }

    /*private ZippedDocumentFile getSmeeAndFordCaseData(List<ReturnedCaseDetails> cases) {
        byte[] bytes = smeeAndFordPersonalisationService.getSmeeAndFordByteArray(cases);
        ZippedDocumentFile zippedDocumentFile = ZippedDocumentFile.builder()
                .caseNumber("all")
                .byteArrayResource(new ByteArrayResource(bytes))
                .docFileType(CSV)
                .docType("csv")
                .build();

        return zippedDocumentFile;
    }*/

    private boolean filterScannedDocs(CollectionMember<ScannedDocument> collectionMember) {
        return collectionMember.getValue().getType().equalsIgnoreCase(OTHER.getTemplateName())
                && collectionMember.getValue().getSubtype().equalsIgnoreCase(WILL.getTemplateName());
    }

    private boolean filterUploadedDocs(CollectionMember<UploadDocument> collectionMember) {
        return collectionMember.getValue().getDocumentType().getTemplateName().equalsIgnoreCase(WILL.getTemplateName());
    }

    private void addZippedDocument(List<ZippedDocumentFile> filesToZip, ReturnedCaseDetails returnedCaseDetails,
                                   String url, String documentTypeName, String docFileType) {
        String id = url.substring(url.indexOf("/documents/") + 11, url.lastIndexOf("/"));
        log.info("doc id:" + id);
        try {
            ByteArrayResource byteArrayResource = emUploadService.getDocument(id);
            ZippedDocumentFile zippedDocumentFile = ZippedDocumentFile.builder()
                    .caseNumber(returnedCaseDetails.getId().toString())
                    .byteArrayResource(byteArrayResource)
                    .docType(documentTypeName)
                    .docFileType(docFileType)
                    .subType(returnedCaseDetails.getData().getCaseType())
                    .build();
            log.info("file added:" + zippedDocumentFile.getDocumentName());
            filesToZip.add(zippedDocumentFile);
        } catch (Exception e) {
            log.info("exception adding file from case {}, docId {}:", returnedCaseDetails.getId().toString(), id);
        }
    }

    public File createTempZipFile(String zipName) throws IOException {
        if (secureDir == null) {
            secureDir = Files.createTempDirectory("zip");
            secureDir.toFile().deleteOnExit();
        }

        log.info("secureDir:" + secureDir.toAbsolutePath().toString());
        Path tempFile = Files.createFile(
                Paths.get(secureDir.toAbsolutePath().toString() + "/" + zipName + ".zip"));
        log.info("tempFile:" + tempFile.toAbsolutePath().toString());
        return tempFile.toFile();
    }

    private void zipMultipleDocs(List<ZippedDocumentFile> files, File tempFile) throws IOException {
        FileOutputStream fos = new FileOutputStream(tempFile);

        ZipOutputStream zipOut = new ZipOutputStream(fos);
        for (ZippedDocumentFile file : files) {
            log.info("Zipping file {}", file.getDocumentName());
            ZipEntry zipEntry = new ZipEntry(file.getDocumentName());
            zipOut.putNextEntry(zipEntry);
            zipOut.write(file.getByteArrayResource().getByteArray());
        }
        zipOut.close();
        fos.close();
    }

}
