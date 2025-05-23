package uk.gov.hmcts.probate.service.dataextract;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import uk.gov.hmcts.probate.exception.ClientException;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;
import uk.gov.hmcts.probate.service.CaseQueryService;
import uk.gov.hmcts.probate.service.FileTransferService;
import uk.gov.hmcts.probate.service.filebuilder.FileExtractDateFormatter;
import uk.gov.hmcts.probate.service.filebuilder.IronMountainFileService;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class IronMountainDataExtractServiceTest {
    @Mock
    private CaseQueryService caseQueryService;
    @Mock
    private IronMountainFileService ironMountainFileService;
    @Mock
    private FileTransferService fileTransferService;
    @Mock
    private FileExtractDateFormatter fileExtractDateFormatter;

    @InjectMocks
    private IronMountainDataExtractService ironMountainDataExtractService;

    private static final LocalDateTime LAST_MODIFIED = LocalDateTime.now(ZoneOffset.UTC).minusYears(2);

    private CaseData caseData;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        CollectionMember<ScannedDocument> scannedDocument = new CollectionMember<>(new ScannedDocument("1",
            "test", "other", "will", LocalDateTime.now(), DocumentLink.builder().build(),
            "test", LocalDateTime.now()));
        CollectionMember<ScannedDocument> scannedDocumentNullSubType = new CollectionMember<>(new ScannedDocument("1",
            "test", "other", null, LocalDateTime.now(), DocumentLink.builder().build(),
            "test", LocalDateTime.now()));
        List<CollectionMember<ScannedDocument>> scannedDocuments = new ArrayList<>();
        scannedDocuments.add(scannedDocument);
        scannedDocuments.add(scannedDocumentNullSubType);

        caseData = CaseData.builder()
            .deceasedSurname("smith")
            .scannedDocuments(scannedDocuments)
            .build();

        when(fileTransferService.uploadFile(any())).thenReturn(HttpStatus.CREATED.value());
    }

    @Test
    void shouldExtractFoundCases() {
        List<ReturnedCaseDetails> returnedCases = new ImmutableList.Builder<ReturnedCaseDetails>().add(new
            ReturnedCaseDetails(caseData, LAST_MODIFIED, 1L)).build();
        when(caseQueryService.findGrantIssuedCasesWithGrantIssuedDate(any(), any())).thenReturn(returnedCases);

        ironMountainDataExtractService.performIronMountainExtractForDate("2000-12-31");

        verify(fileTransferService).uploadFile(any());
        verify(fileExtractDateFormatter).getIronMountainFormattedFileDate(any());
        verify(ironMountainFileService).createIronMountainFile(any(), anyString());
    }

    @Test
    void shouldExtractWhenNoCasesFound() {
        List<ReturnedCaseDetails> returnedCases = new ImmutableList.Builder<ReturnedCaseDetails>()
            .build();
        when(caseQueryService.findGrantIssuedCasesWithGrantIssuedDate(any(), any())).thenReturn(returnedCases);

        ironMountainDataExtractService.performIronMountainExtractForDate("2000-12-31");

        verify(fileTransferService, times(1)).uploadFile(any());
        verify(fileExtractDateFormatter, times(1)).getIronMountainFormattedFileDate(any());
        verify(ironMountainFileService, times(1)).createIronMountainFile(any(), anyString());
    }

    @Test
    void shouldThrowClientExceptionWhenFindingCases() {
        assertThrows(ClientException.class, () -> {
            List<ReturnedCaseDetails> returnedCases = new ImmutableList.Builder<ReturnedCaseDetails>().add(new
                    ReturnedCaseDetails(caseData, LAST_MODIFIED, 1L)).build();
            when(caseQueryService.findGrantIssuedCasesWithGrantIssuedDate(any(), any())).thenReturn(returnedCases);
            when(fileTransferService.uploadFile(any())).thenReturn(HttpStatus.SERVICE_UNAVAILABLE.value());

            ironMountainDataExtractService.performIronMountainExtractForDate("2000-12-31");

            verify(fileTransferService).uploadFile(any());
            verify(fileExtractDateFormatter).getIronMountainFormattedFileDate(any());
            verify(ironMountainFileService, times(0)).createIronMountainFile(any(), anyString());
        });
    }

}
