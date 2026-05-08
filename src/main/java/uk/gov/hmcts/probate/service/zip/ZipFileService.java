package uk.gov.hmcts.probate.service.zip;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import uk.gov.hmcts.probate.blob.component.BlobUpload;
import uk.gov.hmcts.probate.exception.ZipFileException;
import uk.gov.hmcts.probate.model.DataExtractType;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.ccd.raw.UploadDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;
import uk.gov.hmcts.probate.model.zip.SmeeAndFordCommentMode;
import uk.gov.hmcts.probate.model.zip.ZippedManifestData;
import uk.gov.hmcts.probate.service.FeatureToggleService;
import uk.gov.hmcts.probate.service.FileSystemResourceService;
import uk.gov.hmcts.probate.service.dataextract.DataExtractStrategy;
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
    private final FeatureToggleService featureToggleService;
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
    private static final String ERROR_MESSAGE = "Exception occurred while generating zip file ";

    public File generateZipFile(List<ReturnedCaseDetails> cases, File tempFile, String date, DataExtractType type) {
        log.info("{} generateZipFile for {} cases", type, cases.size());

        try (final FileOutputStream fos = new FileOutputStream(tempFile);
             final ZipOutputStream zipOut = new ZipOutputStream(fos)) {
            getSmeeAndFordCaseData(zipOut, cases, date);
            zipOut.closeEntry();
            return tempFile;
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e);
            throw new ZipFileException(e.getMessage());
        }
    }

    public void generateAndUploadZipFile(List<ReturnedCaseDetails> cases,
                                         File tempFile,
                                         String fromDate,
                                         DataExtractStrategy strategy,
                                         SmeeAndFordCommentMode smeeAndFordCommentMode) {
        log.info("Smee And Ford generateZipFile for {} cases", cases.size());
        List<ZippedManifestData> manifestDataList = new ArrayList<>();
        try (final FileOutputStream fos = new FileOutputStream(tempFile);
            final ZipOutputStream zipOut = new ZipOutputStream(fos)) {
            for (ReturnedCaseDetails returnedCaseDetails : cases) {
                log.info("Smee And Ford Starting for case {}", returnedCaseDetails.getId());
                getWillDocuments(zipOut, returnedCaseDetails, manifestDataList, smeeAndFordCommentMode);
                getGrantDocuments(zipOut, returnedCaseDetails, manifestDataList);
                getReIssueGrantDocuments(zipOut, returnedCaseDetails, manifestDataList);
            }
            getSmeeAndFordCaseData(zipOut, cases, fromDate);
            generateManifestFile(zipOut, manifestDataList, smeeAndFordCommentMode);
            zipOut.closeEntry();
            zipOut.close();
            fos.close();
            strategy.uploadToBlobStorage(tempFile);
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e);
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
                                  List<ZippedManifestData> manifestDataList,
                                  SmeeAndFordCommentMode smeeAndFordCommentMode) {
        getScannedDocuments(zos, caseDetails, manifestDataList);
        getUploadedWillDocuments(zos, caseDetails, manifestDataList, smeeAndFordCommentMode);
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
                        final String documentSubType = doc.getValue().getSubtype();
                        fetchAndUploadDocument(zos, binaryUrl, caseDetails, documentTypeName, PDF, documentSubType,
                                manifestDataList, null);
                    });
        }
    }

    private void getUploadedWillDocuments(ZipOutputStream zos,
                                          ReturnedCaseDetails caseDetails,
                                          List<ZippedManifestData> manifestDataList,
                                          SmeeAndFordCommentMode smeeAndFordCommentMode) {
        AtomicInteger uploadedDocIndex = new AtomicInteger(1);
        if (caseDetails.getData().getBoDocumentsUploaded() != null) {
            caseDetails.getData()
                    .getBoDocumentsUploaded().stream()
                    .filter(this::filterUploadedDocs)
                    .forEach(doc -> {
                        final String binaryUrl = doc.getValue().getDocumentLink().getDocumentBinaryUrl();
                        final String documentTypeName = "uploaded_" + WILL.getTemplateName()
                                + "_" + uploadedDocIndex.getAndIncrement();

                        String documentComment = switch (smeeAndFordCommentMode) {
                            case INCLUDE_COMMENT -> {
                                if (featureToggleService.isSmeeAndFordCommentFieldFeatureToggleOn()) {
                                    yield doc.getValue().getComment();
                                }
                                yield null;
                            }
                            case EXCLUDE_COMMENT -> null;
                        };
                        fetchAndUploadDocument(zos, binaryUrl, caseDetails, documentTypeName, PDF, null,
                                manifestDataList, documentComment);
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
                            doc.getValue().getDocumentType().getTemplateName(), PDF, null, manifestDataList, null);
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
                    fetchAndUploadDocument(zos, binaryUrl, caseDetails, documentTypeName, PDF, null,
                            manifestDataList, null);
                });

    }

    private void fetchAndUploadDocument(ZipOutputStream zos,
                                        String binaryUrl,
                                        ReturnedCaseDetails caseDetails,
                                        String documentTypeName,
                                        String docType,
                                        String documentSubType,
                                        List<ZippedManifestData> manifestDataList,
                                        String documentComment) {
        final String documentId = binaryUrl.substring(binaryUrl
                .indexOf("/documents/") + 11, binaryUrl.lastIndexOf("/"));
        String errorDescription = "";
        final ZippedManifestData zippedManifestData = ZippedManifestData.builder()
                .caseNumber(caseDetails.getId().toString())
                .documentId(documentId)
                .docType(documentTypeName)
                .docFileType(docType)
                .subType(documentSubType)
                .caseType(caseDetails.getData().getCaseType())
                .errorDescription(errorDescription)
                .comment(documentComment)
                .build();

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
            log.error("Error while adding file ", e);
        }
        manifestDataList.add(zippedManifestData);
    }

    public File createTempZipFile(String zipName) throws IOException {
        if (secureDir == null) {
            secureDir = Paths.get("").toAbsolutePath();
        }
        File file = ResourceUtils.getFile(secureDir + "/" + zipName + ".zip");
        if (file.exists()) {
            Files.delete(file.toPath());
        }

        Path tempFilePath = Files.createTempFile(secureDir, zipName, ".zip");
        boolean isRenamed = tempFilePath.toFile().renameTo(file);
        if (file != null && isRenamed) {
            boolean isReadable = file.setReadable(true, true);
            boolean isWritable = file.setWritable(true, true);
            log.info("File: {} and file is isReadable {} and isWritable {}",
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

    private void generateManifestFile(ZipOutputStream zos, List<ZippedManifestData> zippedManifestDataList,
                                      SmeeAndFordCommentMode smeeAndFordCommentMode)
            throws IOException {

        boolean isUpdatedSmeeAndFord = switch (smeeAndFordCommentMode) {
            case INCLUDE_COMMENT -> featureToggleService.isSmeeAndFordCommentFieldFeatureToggleOn();
            case EXCLUDE_COMMENT -> false;
        };

        StringBuilder data = new StringBuilder();
        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setDelimiter(DELIMITER)
                .setRecordSeparator(NEW_LINE)
                .setQuoteMode(QuoteMode.MINIMAL)
                .build();

        try (final CSVPrinter csvWriter = new CSVPrinter(data, format)) {
            csvWriter.print("Case reference number");
            csvWriter.print("Document id");
            csvWriter.print("Document type");
            if (isUpdatedSmeeAndFord) {
                csvWriter.print("Case type");
                csvWriter.print("Document sub type");
            } else {
                csvWriter.print("Document sub type");
                csvWriter.print("Case type");
            }
            csvWriter.print("Document file name");
            csvWriter.print("Error description");
            if (isUpdatedSmeeAndFord) {
                csvWriter.print("Comment");
            }
            csvWriter.println();
            csvWriter.println();

            for (ZippedManifestData zippedManifestData : zippedManifestDataList) {
                csvWriter.print(zippedManifestData.getCaseNumber());
                csvWriter.print(zippedManifestData.getDocumentId());
                csvWriter.print(zippedManifestData.getDocType());
                csvWriter.print(zippedManifestData.getCaseType());
                csvWriter.print(getSafeDocumentSubType(zippedManifestData.getSubType(), isUpdatedSmeeAndFord));
                csvWriter.print(zippedManifestData.getDocumentName());
                csvWriter.print(zippedManifestData.getErrorDescription());
                if (isUpdatedSmeeAndFord) {
                    csvWriter.print(sanitiseComment(zippedManifestData.getComment()));
                }
                csvWriter.println();
            }
        }

        ZippedManifestData zippedManifestData = ZippedManifestData.builder()
                .caseNumber("manifest")
                .docType("file")
                .docFileType(CSV)
                .errorDescription("").build();
        ByteArrayResource byteArrayResource = new ByteArrayResource(data.toString().getBytes(StandardCharsets.UTF_8));
        zipMultipleDocs(zos, byteArrayResource, zippedManifestData.getDocumentName());
    }

    //Sending only the subType to S&F because NFI receive 'original' pre DTSPB-5156 changes - avoid ruining their ingest
    private String getSafeDocumentSubType(String subType, boolean isUpdatedSmeeAndFord) {
        if (isUpdatedSmeeAndFord) {
            return (subType == null || subType.isEmpty()) ? "null" : subType;
        }
        return "null";
    }

    private String sanitiseComment(String comment) {
        if (comment == null) {
            return "null";
        }
        return comment.replaceAll("[\n\r,|;\"']", " ").trim();
    }
}
