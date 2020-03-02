package uk.gov.hmcts.probate.service.dataextract;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
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
import uk.gov.hmcts.probate.service.filebuilder.HmrcFileService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HmrcDataExtractServiceTest {
    @Mock
    private CaseQueryService caseQueryService;
    @Mock
    private FileTransferService fileTransferService;
    @Mock
    private HmrcFileService hmrcFileService;
    @Mock
    private FileExtractDateFormatter fileExtractDateFormatter;

    @InjectMocks
    private HmrcDataExtractService hmrcDataExtractService;

    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private CaseData caseData;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

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
    public void shouldExtractFoundCases() {
        List<ReturnedCaseDetails> returnedCases = new ImmutableList.Builder<ReturnedCaseDetails>().add(new
            ReturnedCaseDetails(caseData, LAST_MODIFIED, 1L)).build();
        when(caseQueryService.findCasesWithDatedDocument(any())).thenReturn(returnedCases);
        
        hmrcDataExtractService.performHmrcExtractFromDate("2000-12-31", "2000-12-31");

        verify(fileTransferService).uploadFile(any());
        verify(fileExtractDateFormatter).getFormattedFileDate("2000-12-31");
        verify(hmrcFileService).createHmrcFile(any(), anyString());
    }

    @Test
    public void shouldExtractWhenNoCasesFound() {
        List<ReturnedCaseDetails> returnedCases = new ImmutableList.Builder<ReturnedCaseDetails>()
            .build();
        when(caseQueryService.findCasesWithDatedDocument(any())).thenReturn(returnedCases);
        
        hmrcDataExtractService.performHmrcExtractFromDate("2000-12-31", "2000-12-31");

        verify(fileTransferService, times(1)).uploadFile(any());
        verify(fileExtractDateFormatter, times(1)).getFormattedFileDate("2000-12-31");
        verify(fileExtractDateFormatter, times(1)).getFormattedFileDate("2000-12-31");
        verify(hmrcFileService, times(1)).createHmrcFile(any(), anyString());
    }

    @Test
    public void shouldExtractFoundCasesToFrom() {
        List<ReturnedCaseDetails> returnedCases = new ImmutableList.Builder<ReturnedCaseDetails>().add(new
            ReturnedCaseDetails(caseData, LAST_MODIFIED, 1L)).build();
        when(caseQueryService.findCaseStateWithinTimeFrame(any(), any())).thenReturn(returnedCases);
        
        hmrcDataExtractService.performHmrcExtractFromDate("2000-10-30", "2000-12-31");

        verify(fileTransferService).uploadFile(any());
        verify(fileExtractDateFormatter).getFormattedFileDate("2000-12-31");
        verify(hmrcFileService).createHmrcFile(any(), anyString());
    }

    @Test
    public void shouldExtractForNoCasesFoundToFrom() {
        List<ReturnedCaseDetails> returnedCases = new ImmutableList.Builder<ReturnedCaseDetails>()
            .build();
        when(caseQueryService.findCasesWithDatedDocument(any())).thenReturn(returnedCases);

        hmrcDataExtractService.performHmrcExtractFromDate("2000-10-30", "2000-12-31");

        verify(fileTransferService, times(1)).uploadFile(any());
        verify(fileExtractDateFormatter, times(1)).getFormattedFileDate("2000-12-31");
        verify(hmrcFileService, times(1)).createHmrcFile(any(), anyString());
    }

    @Test(expected = ClientException.class)
    public void shouldThrowClientExceptionWhenFindingCases() {
        List<ReturnedCaseDetails> returnedCases = new ImmutableList.Builder<ReturnedCaseDetails>().add(new
            ReturnedCaseDetails(caseData, LAST_MODIFIED, 1L)).build();
        when(caseQueryService.findCasesWithDatedDocument(any())).thenReturn(returnedCases);
        when(fileTransferService.uploadFile(any())).thenReturn(HttpStatus.SERVICE_UNAVAILABLE.value());

        hmrcDataExtractService.performHmrcExtractFromDate("2000-12-31", "2000-12-31");

        verify(fileTransferService).uploadFile(any());
        verify(fileExtractDateFormatter).getFormattedFileDate("2000-12-31");
        verify(hmrcFileService, times(0)).createHmrcFile(any(), anyString());
    }

    @Test(expected = ClientException.class)
    public void shouldThrowClientExceptionWhenFindingCasesFromTo() {
        List<ReturnedCaseDetails> returnedCases = new ImmutableList.Builder<ReturnedCaseDetails>().add(new
            ReturnedCaseDetails(caseData, LAST_MODIFIED, 1L)).build();
        when(caseQueryService.findCasesWithDatedDocument(any())).thenReturn(returnedCases);
        when(fileTransferService.uploadFile(any())).thenReturn(HttpStatus.SERVICE_UNAVAILABLE.value());
        when(caseQueryService.findCaseStateWithinTimeFrame(any(), any())).thenReturn(returnedCases);

        hmrcDataExtractService.performHmrcExtractFromDate("2000-12-31", "2001-01-01");

        verify(fileTransferService).uploadFile(any());
        verify(fileExtractDateFormatter).getFormattedFileDate("2000-12-31");
        verify(fileExtractDateFormatter).getFormattedFileDate("2001-01-01");
        
        verify(hmrcFileService, times(0)).createHmrcFile(any(), anyString());
    }
}