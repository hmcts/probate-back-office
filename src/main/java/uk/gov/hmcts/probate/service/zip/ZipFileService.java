package uk.gov.hmcts.probate.service.zip;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import uk.gov.hmcts.probate.blob.component.BlobUpload;
import uk.gov.hmcts.probate.exception.ZipFileException;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.ccd.raw.UploadDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;
import uk.gov.hmcts.probate.model.zip.ZippedManifestData;
import uk.gov.hmcts.probate.service.FileSystemResourceService;
import uk.gov.hmcts.probate.service.documentmanagement.DocumentManagementService;
import uk.gov.hmcts.probate.service.notification.SmeeAndFordPersonalisationService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static uk.gov.hmcts.probate.model.DocumentType.AD_COLLIGENDA_BONA_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.AD_COLLIGENDA_BONA_GRANT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.ADMON_WILL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.INTESTACY_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_AD_COLLIGENDA_BONA_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_AD_COLLIGENDA_BONA_GRANT_REISSUE;
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

    public static final int BUFFER = 2048;
    private final DocumentManagementService documentManagementService;
    private final SmeeAndFordPersonalisationService smeeAndFordPersonalisationService;
    private final FileSystemResourceService fileSystemResourceService;
    private final BlobUpload blobUpload;
    private Path secureDir = null;
    private static final String PDF = ".pdf";
    private static final String CSV = ".csv";
    private static final String DELIMITER = "|";
    private static final String NEW_LINE = "\n";
    private static final DocumentType[] GRANT_TYPES = {DIGITAL_GRANT, ADMON_WILL_GRANT, INTESTACY_GRANT,
        AD_COLLIGENDA_BONA_GRANT, WELSH_DIGITAL_GRANT, WELSH_ADMON_WILL_GRANT, WELSH_INTESTACY_GRANT,
        WELSH_AD_COLLIGENDA_BONA_GRANT};
    private static final DocumentType[] REISSUE_GRANT_TYPES = {DIGITAL_GRANT_REISSUE, INTESTACY_GRANT_REISSUE,
        ADMON_WILL_GRANT_REISSUE, AD_COLLIGENDA_BONA_GRANT_REISSUE, WELSH_DIGITAL_GRANT_REISSUE,
        WELSH_INTESTACY_GRANT_REISSUE, WELSH_ADMON_WILL_GRANT_REISSUE, WELSH_AD_COLLIGENDA_BONA_GRANT_REISSUE};
    private static final String HEADER_ROW_FILE = "templates/dataExtracts/ManifestFileHeaderRow.csv";

    public void generateZipFile(List<ReturnedCaseDetails> cases, File tempFile, String fromDate) {
        log.info("Smee And Ford generateZipFile for {} cases", cases.size());

        List<ZippedManifestData> manifestDataList = new ArrayList<>();
        try (final FileOutputStream fos = new FileOutputStream(tempFile);
            final ZipOutputStream zipOut = new ZipOutputStream(fos)) {
            for (ReturnedCaseDetails returnedCaseDetails : cases) {
                log.info("Smee And Ford Starting for case {}", returnedCaseDetails.getId());
                getWillDocuments(zipOut, returnedCaseDetails, manifestDataList);
                getGrantDocuments(zipOut, returnedCaseDetails, manifestDataList);
                getReIssueGrantDocuments(zipOut, returnedCaseDetails, manifestDataList);
            }
            getSmeeAndFordCaseData(zipOut, cases, fromDate);
            generateManifestFile(zipOut, manifestDataList);
            zipOut.closeEntry();
            zipOut.close();
            fos.close();
            blobUpload.uploadFile(tempFile);
        } catch (IOException e) {
            log.error("Exception occurred while generating zip file ", e);
            throw new ZipFileException(e.getMessage());
        } catch (Exception e) {
            log.error("Exception occurred while generating zip file ", e);
            throw new ZipFileException(e.getMessage());
        }
    }

    private void getSmeeAndFordCaseData(ZipOutputStream zos,
                                        List<ReturnedCaseDetails> cases,
                                        String fromDate) throws IOException {
        byte[] bytes = smeeAndFordPersonalisationService.getSmeeAndFordByteArray(cases);
        ZippedManifestData zippedManifestData = ZippedManifestData.builder()
                        .caseNumber("all_cases")
                        .docFileType(CSV)
                        .docType("data_" + fromDate)
                        .build();
        zipMultipleDocs(zos, new ByteArrayResource(bytes), zippedManifestData.getDocumentName());
    }

    private void getWillDocuments(final ZipOutputStream zos,
                                  final ReturnedCaseDetails caseDetails,
                                  List<ZippedManifestData> manifestDataList) {
        getScannedDocuments(zos, caseDetails, manifestDataList);
        getUploadedWillDocuments(zos, caseDetails, manifestDataList);
    }

    private void getScannedDocuments(final ZipOutputStream zos,
                                     ReturnedCaseDetails caseDetails,
                                     List<ZippedManifestData> manifestDataList) {
        AtomicInteger scannedDocIndex = new AtomicInteger(1);
        if (caseDetails.getData().getScannedDocuments() != null) {
            caseDetails.getData()
                    .getScannedDocuments().stream()
                    .filter(this::filterScannedDocs)
                    .forEach(doc -> {
                        final String binaryUrl = doc.getValue().getUrl().getDocumentBinaryUrl();
                        final String documentTypeName = "scanned_" + WILL.getTemplateName()
                                + "_" + scannedDocIndex.getAndIncrement();
                        fetchAndUploadDocument(zos, binaryUrl, caseDetails, documentTypeName, PDF,
                                manifestDataList);
                    });
        }
    }

    private void getUploadedWillDocuments(ZipOutputStream zos,
                                          ReturnedCaseDetails caseDetails,
                                          List<ZippedManifestData> manifestDataList) {
        AtomicInteger uploadedDocIndex = new AtomicInteger(1);
        if (caseDetails.getData().getBoDocumentsUploaded() != null) {
            caseDetails.getData()
                    .getBoDocumentsUploaded().stream()
                    .filter(this::filterUploadedDocs)
                    .forEach(doc -> {
                        final String binaryUrl = doc.getValue().getDocumentLink().getDocumentBinaryUrl();
                        final String documentTypeName = "uploaded_" + WILL.getTemplateName()
                                + "_" + uploadedDocIndex.getAndIncrement();
                        fetchAndUploadDocument(zos, binaryUrl, caseDetails, documentTypeName, PDF,
                                manifestDataList);
                    });
        }
    }

    private void getGrantDocuments(ZipOutputStream zos,
                                   ReturnedCaseDetails caseDetails,
                                   List<ZippedManifestData> manifestDataList) {
        caseDetails.getData()
                .getProbateDocumentsGenerated().stream()
                .filter(collectionMember -> Arrays.asList(GRANT_TYPES).contains(collectionMember.getValue()
                        .getDocumentType()))
                .forEach(doc -> {
                    final String binaryUrl = doc.getValue().getDocumentLink().getDocumentBinaryUrl();
                    fetchAndUploadDocument(zos, binaryUrl, caseDetails,
                            doc.getValue().getDocumentType().getTemplateName(), PDF, manifestDataList);
                });
    }

    private void getReIssueGrantDocuments(ZipOutputStream zos,
                                          ReturnedCaseDetails caseDetails,
                                          List<ZippedManifestData> manifestDataList) {
        AtomicInteger reIssueGrantDocIndex = new AtomicInteger(1);
        caseDetails.getData()
                .getProbateDocumentsGenerated().stream()
                .filter(collectionMember -> Arrays.asList(REISSUE_GRANT_TYPES).contains(collectionMember.getValue()
                        .getDocumentType()))
                .forEach(doc -> {
                    final String binaryUrl = doc.getValue().getDocumentLink().getDocumentBinaryUrl();
                    final String documentTypeName = doc.getValue().getDocumentType().getTemplateName()
                            + "_" + reIssueGrantDocIndex.getAndIncrement();
                    fetchAndUploadDocument(zos, binaryUrl, caseDetails, documentTypeName, PDF, manifestDataList);
                });

    }

    private void fetchAndUploadDocument(ZipOutputStream zos,
                                        String binaryUrl,
                                        ReturnedCaseDetails caseDetails,
                                        String documentTypeName,
                                        String docType,
                                        List<ZippedManifestData> manifestDataList) {
        final String documentId = binaryUrl.substring(binaryUrl
                .indexOf("/documents/") + 11, binaryUrl.lastIndexOf("/"));
        String errorDescription = "";
        final ZippedManifestData zippedManifestData = ZippedManifestData.builder()
                .caseNumber(caseDetails.getId().toString())
                .documentId(documentId)
                .docType(documentTypeName)
                .docFileType(docType)
                .subType(caseDetails.getData().getCaseType())
                .errorDescription(errorDescription).build();

        try {
            ByteArrayResource byteArrayResource =
                    new ByteArrayResource(documentManagementService.getDocumentByBinaryUrl(binaryUrl));
            if (byteArrayResource != null) {
                zipMultipleDocs(zos, byteArrayResource, zippedManifestData.getDocumentName());
                log.info("file added: {} for case id {}", zippedManifestData.getDocumentName(),
                        caseDetails.getId().toString());
            }
        } catch (Exception e) {
            errorDescription = "Exception adding file from case id: " + caseDetails.getId().toString()
                    + " document id: " + documentId;
            zippedManifestData.setErrorDescription(errorDescription);
            log.info(errorDescription);
            log.error("Error while adding fie ", e);
        }
        manifestDataList.add(zippedManifestData);
    }

    public File createTempZipFile(String zipName) throws IOException {
        File file = null;
        if (secureDir == null) {
            secureDir = Paths.get("").toAbsolutePath();
            file = ResourceUtils.getFile(secureDir + "/" + zipName + ".zip");
            if (file.exists()) {
                Files.delete(file.toPath());
            }
        }

        Path tempFilePath = Files.createTempFile(secureDir, zipName, ".zip");
        boolean isRenamed = tempFilePath.toFile().renameTo(file);
        if (file != null && isRenamed) {
            boolean isReadable = file.setReadable(true, true);
            boolean isWritable = file.setWritable(true, true);
            log.info("Smee And Ford file: {} and file is isReadable {} and isWritable {}",
                    file.getPath(), isReadable, isWritable);
            Files.deleteIfExists(tempFilePath);
            return file;
        }
        return tempFilePath.toFile();
    }

    private boolean filterScannedDocs(CollectionMember<ScannedDocument> collectionMember) {
        return ((OTHER.getTemplateName().equalsIgnoreCase(collectionMember.getValue().getType())
                && WILL.getTemplateName().equalsIgnoreCase(collectionMember.getValue().getSubtype()))
                || WILL.getTemplateName().equalsIgnoreCase(collectionMember.getValue().getType()));
    }

    private boolean filterUploadedDocs(CollectionMember<UploadDocument> collectionMember) {
        return WILL.getTemplateName().equalsIgnoreCase(collectionMember.getValue().getDocumentType().getTemplateName());
    }

    private void zipMultipleDocs(final ZipOutputStream zos,
                                 final ByteArrayResource byteArrayResource,
                                 final String documentName) throws IOException {

        ZipEntry zipEntry = new ZipEntry(documentName);
        zos.putNextEntry(zipEntry);
        zos.write(byteArrayResource.getByteArray());
        zos.closeEntry();
    }

    private void addHeaderRow(StringBuilder data) {
        String header = fileSystemResourceService.getFileFromResourceAsString(HEADER_ROW_FILE);
        data.append(header);
    }

    private void generateManifestFile(ZipOutputStream zos, List<ZippedManifestData> zippedManifestDataList)
            throws IOException {
        StringBuilder data = new StringBuilder();
        addHeaderRow(data);
        data.append(NEW_LINE);

        for (ZippedManifestData zippedManifestData : zippedManifestDataList) {
            data.append(zippedManifestData.getCaseNumber());
            data.append(DELIMITER);
            data.append(zippedManifestData.getDocumentId());
            data.append(DELIMITER);
            data.append(zippedManifestData.getDocType());
            data.append(DELIMITER);
            data.append(zippedManifestData.getSubType());
            data.append(DELIMITER);
            data.append(zippedManifestData.getCaseType());
            data.append(DELIMITER);
            data.append(zippedManifestData.getDocumentName());
            data.append(DELIMITER);
            data.append(zippedManifestData.getErrorDescription());
            data.append(NEW_LINE);
        }

        ZippedManifestData zippedManifestData = ZippedManifestData.builder()
                .caseNumber("manifest")
                .docType("file")
                .docFileType(CSV)
                .errorDescription("").build();
        ByteArrayResource byteArrayResource = new ByteArrayResource(data.toString().getBytes(StandardCharsets.UTF_8));
        zipMultipleDocs(zos, byteArrayResource, zippedManifestData.getDocumentName());
    }

}
