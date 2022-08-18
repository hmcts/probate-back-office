package uk.gov.hmcts.probate.service.zip;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
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

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");
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
        WELSH_DIGITAL_GRANT, WELSH_ADMON_WILL_GRANT, WELSH_INTESTACY_GRANT};
    private static final DocumentType[] REISSUE_GRANT_TYPES = {DIGITAL_GRANT_REISSUE, INTESTACY_GRANT_REISSUE,
        ADMON_WILL_GRANT_REISSUE, WELSH_DIGITAL_GRANT_REISSUE, WELSH_INTESTACY_GRANT_REISSUE,
        WELSH_ADMON_WILL_GRANT_REISSUE};
    private static final String HEADER_ROW_FILE = "templates/dataExtracts/ManifestFileHeaderRow.csv";

    public void generateZipFile(List<ReturnedCaseDetails> cases) {
        log.info("generateZipFile for {} cases", cases.size());
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final ZipOutputStream zos = new ZipOutputStream(out);
        List<ZippedManifestData> manifestDataList = new ArrayList<>();
        try {
            for (ReturnedCaseDetails returnedCaseDetails : cases) {
                getWillDocuments(zos, out, returnedCaseDetails, manifestDataList);
                getGrantDocuments(zos, out, returnedCaseDetails, manifestDataList);
                getReIssueGrantDocuments(zos, out, returnedCaseDetails, manifestDataList);
            }
            getSmeeAndFordCaseData(zos, out, cases);
            generateManifestFile(zos, out, manifestDataList);
            zos.close();
            out.close();
            final ByteArrayInputStream input = new ByteArrayInputStream(out.toByteArray());
            blobUpload.upload(input, out.size());
        } catch (IOException e) {
            log.error("Exception occurred while generating zip file ", e);
            throw new ZipFileException(e.getMessage());
        } catch (Exception e) {
            log.error("Exception occurred while generating zip file ", e);
            throw new ZipFileException(e.getMessage());
        }
    }

    private void getSmeeAndFordCaseData(ZipOutputStream zos,
                                        ByteArrayOutputStream out,
                                        List<ReturnedCaseDetails> cases) throws IOException {
        byte[] bytes = smeeAndFordPersonalisationService.getSmeeAndFordByteArray(cases);
        String todaysDate = DATE_FORMAT.format(LocalDate.now());
        ZippedManifestData zippedManifestData = ZippedManifestData.builder()
                        .caseNumber("all_cases")
                        .docFileType(CSV)
                        .docType("data_" + todaysDate)
                        .build();
        zipMultipleDocs(zos, out, new ByteArrayResource(bytes), zippedManifestData.getDocumentName());
    }

    private void getWillDocuments(final ZipOutputStream zos,
                                  final ByteArrayOutputStream out,
                                  final ReturnedCaseDetails caseDetails,
                                  List<ZippedManifestData> manifestDataList) {
        getScannedDocuments(zos, out, caseDetails, manifestDataList);
        getUploadedWillDocuments(zos, out, caseDetails, manifestDataList);
    }

    private void getScannedDocuments(final ZipOutputStream zos,
                                     final ByteArrayOutputStream out,
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
                        fetchAndUploadDocument(zos, out, binaryUrl, caseDetails, documentTypeName, PDF,
                                manifestDataList);
                    });
        }
    }

    private void getUploadedWillDocuments(ZipOutputStream zos,
                                          ByteArrayOutputStream out,
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
                        fetchAndUploadDocument(zos, out, binaryUrl, caseDetails, documentTypeName, PDF,
                                manifestDataList);
                    });
        }
    }

    private void getGrantDocuments(ZipOutputStream zos,
                                   ByteArrayOutputStream out,
                                   ReturnedCaseDetails caseDetails,
                                   List<ZippedManifestData> manifestDataList) {
        caseDetails.getData()
                .getProbateDocumentsGenerated().stream()
                .filter(collectionMember -> Arrays.asList(GRANT_TYPES).contains(collectionMember.getValue()
                        .getDocumentType()))
                .forEach(doc -> {
                    final String binaryUrl = doc.getValue().getDocumentLink().getDocumentBinaryUrl();
                    fetchAndUploadDocument(zos, out, binaryUrl, caseDetails,
                            doc.getValue().getDocumentType().getTemplateName(), PDF, manifestDataList);
                });
    }

    private void getReIssueGrantDocuments(ZipOutputStream zos,
                                          ByteArrayOutputStream out,
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
                    fetchAndUploadDocument(zos, out, binaryUrl, caseDetails, documentTypeName, PDF, manifestDataList);
                });

    }

    private void fetchAndUploadDocument(ZipOutputStream zos,
                                        ByteArrayOutputStream out,
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
                zipMultipleDocs(zos, out, byteArrayResource, zippedManifestData.getDocumentName());
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

    private boolean filterScannedDocs(CollectionMember<ScannedDocument> collectionMember) {
        return collectionMember.getValue().getType().equalsIgnoreCase(OTHER.getTemplateName())
                && collectionMember.getValue().getSubtype().equalsIgnoreCase(WILL.getTemplateName());
    }

    private boolean filterUploadedDocs(CollectionMember<UploadDocument> collectionMember) {
        return WILL.getTemplateName().equalsIgnoreCase(collectionMember.getValue().getDocumentType().getTemplateName());
    }

    private void zipMultipleDocs(final ZipOutputStream zos,
                                 final ByteArrayOutputStream out,
                                 final ByteArrayResource byteArrayResource,
                                 final String documentName) throws IOException {

        ZipEntry zipEntry = new ZipEntry(documentName);
        zos.putNextEntry(zipEntry);
        final byte[] buffer = new byte[BUFFER];
        int length;
        final BufferedInputStream entryStream = new BufferedInputStream(byteArrayResource.getInputStream(), BUFFER);
        while ((length = entryStream.read(buffer)) >= 0) {
            zos.write(buffer, 0, length);
        }
        entryStream.close();
        zos.closeEntry();
    }

    private void addHeaderRow(StringBuilder data) {
        String header = fileSystemResourceService.getFileFromResourceAsString(HEADER_ROW_FILE);
        data.append(header);
    }

    private void generateManifestFile(ZipOutputStream zos,
                                                    ByteArrayOutputStream out,
                                                    List<ZippedManifestData> zippedManifestDataList)
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
        zipMultipleDocs(zos, out, byteArrayResource, zippedManifestData.getDocumentName());
    }

}
