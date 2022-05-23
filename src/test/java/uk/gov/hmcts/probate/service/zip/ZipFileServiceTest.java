package uk.gov.hmcts.probate.service.zip;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.util.ResourceUtils;
import uk.gov.hmcts.probate.exception.ZipFileException;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.UploadDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;
import uk.gov.hmcts.probate.service.FileSystemResourceService;
import uk.gov.hmcts.probate.service.evidencemanagement.upload.EmUploadService;
import uk.gov.hmcts.probate.service.notification.SmeeAndFordPersonalisationService;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public class ZipFileServiceTest {

    @Mock
    private EmUploadService emUploadService;

    @Mock
    private SmeeAndFordPersonalisationService smeeAndFordPersonalisationService;

    @Mock
    private FileSystemResourceService fileSystemResourceService;

    private ObjectMapper objectMapper;

    private ZipFileService zipFileService;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private final List<ByteArrayResource> byteArrayResourceList = new ArrayList<>();
    private final List<ReturnedCaseDetails> returnedCaseDetails = new ArrayList<>();


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        zipFileService = new ZipFileService(new ObjectMapper(),
                emUploadService, smeeAndFordPersonalisationService, fileSystemResourceService);

        returnedCaseDetails.add(getNewCaseData(1L));
        returnedCaseDetails.add(getNewCaseData(2L));
        returnedCaseDetails.add(getNewCaseData(3L));

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

        when(emUploadService.getDocument(anyString())).thenReturn(byteArrayResource1, byteArrayResource2,
                byteArrayResource3);
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
        UploadDocument willDocument = UploadDocument.builder().documentType(DocumentType.WILL)
                .documentLink(link)
                .build();
        List<CollectionMember<Document>> grantDocuments = new ArrayList<>();
        grantDocuments.add(new CollectionMember<>(grantDocument));
        grantDocuments.add(new CollectionMember<>(reIssueGrantDocument));

        List<CollectionMember<UploadDocument>> willDocuments = new ArrayList<>();
        willDocuments.add(new CollectionMember<>(willDocument));

        CaseData data = CaseData.builder().caseType(GrantType.Constants.GRANT_OF_PROBATE_NAME)
                .probateDocumentsGenerated(grantDocuments).boDocumentsUploaded(willDocuments).build();
        ReturnedCaseDetails returnedCaseDetails = new ReturnedCaseDetails(data, null, caseId);

        return returnedCaseDetails;
    }

    @Test
    public void shouldCreateZip() throws IOException {
        File zipFile = new File("Probate_Docs_" + DATE_FORMAT.format(LocalDate.now()) + ".zip");
        zipFileService.generateZipFile(returnedCaseDetails, zipFile);
        Assert.assertTrue(zipFile.getAbsolutePath().contains("Probate_Docs_"));
        verify(emUploadService,times(9)).getDocument(anyString());
        Files.delete(zipFile.toPath());
    }

    @Test(expected = ZipFileException.class)
    public void shouldThrowExceptionAndZipFileShouldNotGenerated() {
        File zipFile = new File("");
        zipFileService.generateZipFile(returnedCaseDetails, zipFile);
        Assert.assertTrue(false);
    }

    @Test
    public void shouldCreateTempZipFile() throws IOException {
        String fileName = "Probate_Docs_" + DATE_FORMAT.format(LocalDate.now());
        File tempFile = zipFileService.createTempZipFile(fileName);
        Assert.assertTrue(tempFile.exists());
        Assert.assertTrue(tempFile.canRead());
        Assert.assertTrue(tempFile.canWrite());
        Assert.assertTrue(tempFile.getName().contains(fileName));
        Files.delete(tempFile.toPath());
    }

}
