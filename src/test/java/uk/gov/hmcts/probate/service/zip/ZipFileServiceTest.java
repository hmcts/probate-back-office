package uk.gov.hmcts.probate.service.zip;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.ResourceUtils;
import uk.gov.hmcts.probate.blob.component.BlobUpload;
import uk.gov.hmcts.probate.exception.ZipFileException;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.UploadDocument;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;
import uk.gov.hmcts.probate.service.FileSystemResourceService;
import uk.gov.hmcts.probate.service.documentmanagement.DocumentManagementService;
import uk.gov.hmcts.probate.service.notification.SmeeAndFordPersonalisationService;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

@ExtendWith(SpringExtension.class)
class ZipFileServiceTest {

    @Mock
    private DocumentManagementService documentManagementService;

    @Mock
    private SmeeAndFordPersonalisationService smeeAndFordPersonalisationService;

    @Mock
    private FileSystemResourceService fileSystemResourceService;

    @Mock
    private BlobUpload blobUpload;

    private ZipFileService zipFileService;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final List<ByteArrayResource> byteArrayResourceList = new ArrayList<>();
    private final List<ReturnedCaseDetails> returnedCaseDetails = new ArrayList<>();


    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        zipFileService = new ZipFileService(documentManagementService, smeeAndFordPersonalisationService,
                fileSystemResourceService, blobUpload);

        returnedCaseDetails.add(getNewCaseData(1234567812345678L));
        returnedCaseDetails.add(getNewCaseData(1234567812345610L));
        returnedCaseDetails.add(getNewCaseData(1234567812345620L));

        File file1 = ResourceUtils.getFile(this.getClass().getClassLoader().getResource("zip/TestPage1.pdf"));
        File file2 = ResourceUtils.getFile(this.getClass().getClassLoader().getResource("zip/TestPage2.pdf"));
        File file3 = ResourceUtils.getFile(this.getClass().getClassLoader().getResource("zip/TestPage3.pdf"));
        File smeeAndFordDataFile = ResourceUtils.getFile(this.getClass().getClassLoader()
                .getResource("smeeAndFordExpectedData.txt"));
        final ByteArrayResource smeeAndFordDataFileByteArray = new ByteArrayResource(Files
                .readAllBytes(smeeAndFordDataFile.toPath()));
        final ByteArrayResource byteArrayResource1 = new ByteArrayResource(Files.readAllBytes(file1.toPath()));
        final ByteArrayResource byteArrayResource2 = new ByteArrayResource(Files.readAllBytes(file2.toPath()));
        final ByteArrayResource byteArrayResource3 = new ByteArrayResource(Files.readAllBytes(file3.toPath()));
        byteArrayResourceList.add(byteArrayResource1);
        byteArrayResourceList.add(byteArrayResource2);
        byteArrayResourceList.add(byteArrayResource3);

        when(documentManagementService.getDocumentByBinaryUrl(anyString())).thenReturn(
                byteArrayResource1.getByteArray());
        when(fileSystemResourceService.getFileFromResourceAsString("templates/dataExtracts/ManifestFileHeaderRow.csv"))
                .thenReturn("Case reference number|Document id|Document type|"
                        + "Document sub type|Case type|Document file name|Error description");
        when(smeeAndFordPersonalisationService.getSmeeAndFordByteArray(anyList()))
                .thenReturn(smeeAndFordDataFileByteArray.getByteArray());
    }

    private ReturnedCaseDetails getNewCaseData(Long caseId) {
        DocumentLink link = DocumentLink.builder().documentBinaryUrl("/documents/12345/binary").build();

        Document grantDocument = Document.builder().documentType(DocumentType.DIGITAL_GRANT)
                .documentLink(link)
                .build();
        Document reIssueGrantDocument = Document.builder().documentType(DocumentType.DIGITAL_GRANT_REISSUE)
                .documentLink(link)
                .build();
        List<CollectionMember<Document>> grantDocuments = new ArrayList<>();
        grantDocuments.add(new CollectionMember<>(grantDocument));
        grantDocuments.add(new CollectionMember<>(reIssueGrantDocument));

        UploadDocument willDocument = UploadDocument.builder().documentType(DocumentType.WILL)
                .documentLink(link)
                .build();
        List<CollectionMember<UploadDocument>> willDocuments = new ArrayList<>();
        willDocuments.add(new CollectionMember<>(willDocument));

        ScannedDocument scannedSubtypeWillDocument = ScannedDocument.builder()
                .type(DocumentType.OTHER.getTemplateName())
                .subtype("will")
                .url(link)
                .build();
        ScannedDocument scannedTypeWillDocument = ScannedDocument.builder().type(DocumentType.WILL.getTemplateName())
                .subtype("Original Will")
                .url(link)
                .build();
        ScannedDocument scannedOtherDocumentWithoutSubType = ScannedDocument.builder()
                .type(DocumentType.OTHER.getTemplateName())
                .url(link)
                .build();
        List<CollectionMember<ScannedDocument>> scannedWillDocuments = new ArrayList<>();
        scannedWillDocuments.add(new CollectionMember<>(scannedSubtypeWillDocument));
        scannedWillDocuments.add(new CollectionMember<>(scannedTypeWillDocument));
        scannedWillDocuments.add(new CollectionMember<>(scannedOtherDocumentWithoutSubType));

        CaseData data = CaseData.builder().caseType(GrantType.Constants.GRANT_OF_PROBATE_NAME)
                .probateDocumentsGenerated(grantDocuments)
                .boDocumentsUploaded(willDocuments)
                .scannedDocuments(scannedWillDocuments)
                .build();

        return new ReturnedCaseDetails(data, null, caseId);
    }

    @Test
    void shouldCreateZip() throws IOException {
        String todayDate = DATE_FORMAT.format(LocalDate.now());
        File zipFile = new File("Probate_Docs_" + todayDate + ".zip");
        zipFileService.generateZipFile(returnedCaseDetails, zipFile, todayDate);
        Assertions.assertTrue(zipFile.getAbsolutePath().contains("Probate_Docs_"));
        ZipFile zip = new ZipFile(zipFile);
        Assertions.assertTrue(zip.stream().map(ZipEntry::getName)
                .anyMatch(name -> name.equalsIgnoreCase("all_cases_data_"
                        + DATE_FORMAT.format(LocalDate.now()) + ".csv")));
        Assertions.assertTrue(zip.stream().map(ZipEntry::getName)
                .anyMatch(name -> name.equalsIgnoreCase("manifest_file.csv")));
        Assertions.assertTrue(zip.stream().map(ZipEntry::getName)
                .anyMatch(name -> name.contains("scanned_will")));
        Assertions.assertTrue(zip.stream().map(ZipEntry::getName)
                .anyMatch(name -> name.contains("uploaded_will")));
        Assertions.assertTrue(zip.stream().map(ZipEntry::getName)
                .anyMatch(name -> name.contains("digitalGrant")));
        Assertions.assertTrue(zip.stream().map(ZipEntry::getName)
                .anyMatch(name -> name.contains("digitalGrantReissue")));
        verify(documentManagementService,times(15)).getDocumentByBinaryUrl(anyString());
        Files.delete(zipFile.toPath());
    }

    @Test
    void shouldThrowExceptionAndZipFileShouldNotGenerated() {
        String todayDate = DATE_FORMAT.format(LocalDate.now());
        File zipFile = new File("");
        Assertions.assertThrows(ZipFileException.class, () ->
                zipFileService.generateZipFile(returnedCaseDetails, zipFile, todayDate));
    }

    @Test
    void shouldCreateTempZipFile() throws IOException {
        String fileName = "Probate_Docs_" + DATE_FORMAT.format(LocalDate.now());
        File tempFile = zipFileService.createTempZipFile(fileName);
        Assertions.assertTrue(tempFile.exists());
        Assertions.assertTrue(tempFile.canRead());
        Assertions.assertTrue(tempFile.canWrite());
        Assertions.assertTrue(tempFile.getName().contains(fileName));
        Files.delete(tempFile.toPath());
    }

}
