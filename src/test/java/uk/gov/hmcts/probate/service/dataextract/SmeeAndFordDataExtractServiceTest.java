package uk.gov.hmcts.probate.service.dataextract;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import uk.gov.hmcts.probate.blob.component.BlobUpload;
import uk.gov.hmcts.probate.exception.ClientException;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;
import uk.gov.hmcts.probate.service.CaseQueryService;
import uk.gov.hmcts.probate.service.FileTransferService;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.hmcts.probate.service.zip.ZipFileService;
import uk.gov.service.notify.NotificationClientException;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SmeeAndFordDataExtractServiceTest {

    @InjectMocks
    private SmeeAndFordDataExtractService smeeAndFordDataExtractService;
    @Mock
    private CaseQueryService caseQueryService;
    @Mock
    private NotificationService notificationService;
    @Mock
    private FileTransferService fileTransferService;
    @Mock
    private ZipFileService zipFileService;
    @Mock
    private BlobUpload blobUpload;
    @Mock
    private SmeeAndFOrdDataExtractStrategy smeeAndFOrdDataExtractStrategy;

    private static final LocalDateTime LAST_MODIFIED = LocalDateTime.now(ZoneOffset.UTC).minusYears(2);

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private CaseData caseData1;
    private CaseData caseData2;
    private List<ReturnedCaseDetails> returnedCases;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        smeeAndFordDataExtractService.featureBlobStorageSmeeAndFord = false;
        CollectionMember<ScannedDocument> scannedDocument = new CollectionMember<>(new ScannedDocument("1",
            "test", "other", "will", LocalDateTime.now(), DocumentLink.builder().build(),
            "test", LocalDateTime.now()));
        CollectionMember<ScannedDocument> scannedDocumentNullSubType = new CollectionMember<>(new ScannedDocument("1",
            "test", "other", null, LocalDateTime.now(), DocumentLink.builder().build(),
            "test", LocalDateTime.now()));
        List<CollectionMember<ScannedDocument>> scannedDocuments = new ArrayList<>();
        scannedDocuments.add(scannedDocument);
        scannedDocuments.add(scannedDocumentNullSubType);

        caseData1 = CaseData.builder()
            .deceasedSurname("smith")
            .scannedDocuments(scannedDocuments)
            .build();
        caseData2 = CaseData.builder()
            .deceasedSurname("jones")
            .scannedDocuments(scannedDocuments)
            .build();
        List<ReturnedCaseDetails> returnedCases = new ImmutableList.Builder<ReturnedCaseDetails>()
            .add(new ReturnedCaseDetails(caseData1, LAST_MODIFIED, 1L))
            .add(new ReturnedCaseDetails(caseData2, LAST_MODIFIED, 2L))
            .build();


        when(caseQueryService.findAllCasesWithGrantIssuedDate(any(), any())).thenReturn(returnedCases);
        when(caseQueryService.findCaseStateWithinDateRangeSmeeAndFord(any(), any())).thenReturn(returnedCases);
        when(fileTransferService.uploadFile(any())).thenReturn(HttpStatus.CREATED.value());
    }

    @Test
    void shouldExtractForDate() throws NotificationClientException {
        smeeAndFordDataExtractService.performSmeeAndFordExtractForDateRange("2000-12-30", "2000-12-30");

        verify(notificationService, times(1)).sendSmeeAndFordEmail(any(), eq("2000-12-30"), eq("2000-12-30"));
    }

    @Test
    void shouldExtractForDateRange() throws NotificationClientException {
        smeeAndFordDataExtractService.performSmeeAndFordExtractForDateRange("2000-12-30", "2000-12-31");

        verify(notificationService, times(1)).sendSmeeAndFordEmail(any(), eq("2000-12-30"), eq("2000-12-31"));
    }

    @Test
    void shouldExtractForDateForNoCasesFound() throws NotificationClientException {
        List<ReturnedCaseDetails> returnedCases = new ImmutableList.Builder<ReturnedCaseDetails>()
            .build();

        when(caseQueryService.findAllCasesWithGrantIssuedDate(any(), any())).thenReturn(returnedCases);
        when(caseQueryService.findCaseStateWithinDateRangeSmeeAndFord(any(), any())).thenReturn(returnedCases);

        smeeAndFordDataExtractService.performSmeeAndFordExtractForDateRange("2000-12-30", "2000-12-30");

        verify(notificationService, times(0)).sendSmeeAndFordEmail(any(), eq("2000-12-30"), eq("2000-12-30"));
    }

    @Test
    void shouldThrowClientExceptionForDateRange() throws NotificationClientException {
        assertThrows(ClientException.class, () -> {
            when(notificationService.sendSmeeAndFordEmail(any(), any(), any()))
                    .thenThrow(NotificationClientException.class);

            smeeAndFordDataExtractService.performSmeeAndFordExtractForDateRange("2000-12-30", "2000-12-31");
        });
    }

    @Test
    void shouldExtractDataForDateRangeAndGenerateZipFileThenOnUploadThrowException() {
        assertThrows(ClientException.class, () -> {
            File zipFile = new File("Probate_Docs_" + DATE_FORMAT.format(LocalDate.now()) + ".zip");
            smeeAndFordDataExtractService.featureBlobStorageSmeeAndFord = true;
            when(zipFileService.createTempZipFile(anyString())).thenReturn(zipFile);
            doNothing().when(blobUpload).uploadFile(any(),anyString(),anyString());
            smeeAndFordDataExtractService.performSmeeAndFordExtractForDateRange("2000-12-30", "2000-12-31");

            verify(notificationService, times(1)).sendSmeeAndFordEmail(any(), eq("2000-12-30"), eq("2000-12-31"));
            verify(zipFileService, times(1)).createTempZipFile(anyString());
            verify(zipFileService, times(1))
                    .generateAndUploadZipFile(returnedCases, zipFile, "2000-12-30", smeeAndFOrdDataExtractStrategy);
            verify(blobUpload, times(1)).uploadFile(zipFile, anyString(), anyString());
        });
    }
}
