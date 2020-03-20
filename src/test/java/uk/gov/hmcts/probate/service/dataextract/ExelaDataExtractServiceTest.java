package uk.gov.hmcts.probate.service.dataextract;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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
import uk.gov.hmcts.probate.service.ExcelaCriteriaService;
import uk.gov.hmcts.probate.service.FileTransferService;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.service.notify.NotificationClientException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ExelaDataExtractServiceTest {
    @InjectMocks
    private ExelaDataExtractService exelaDataExtractService;
    @Mock
    private CaseQueryService caseQueryService;
    @Mock
    private NotificationService notificationService;
    @Mock
    private ExcelaCriteriaService excelaCriteriaService;
    @Mock
    private FileTransferService fileTransferService;

    @InjectMocks
    private HmrcDataExtractService hmrcDataExtractService;

    @Captor
    private ArgumentCaptor<List<ReturnedCaseDetails>> filteredCasesCaptor;

    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private CaseData caseData1;
    private CaseData caseData2;

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


        when(caseQueryService.findCasesWithDatedDocument(any())).thenReturn(returnedCases);
        when(caseQueryService.findCaseStateWithinTimeFrame(any(), any())).thenReturn(returnedCases);
        when(fileTransferService.uploadFile(any())).thenReturn(HttpStatus.CREATED.value());
    }

    @Test
    public void shouldExtractForDateForFilteredCases() throws NotificationClientException {
        List<ReturnedCaseDetails> filteredCases = new ImmutableList.Builder<ReturnedCaseDetails>()
            .add(new ReturnedCaseDetails(caseData1, LAST_MODIFIED, 1L))
            .build();
        when(excelaCriteriaService.getFilteredCases(any())).thenReturn(filteredCases);

        exelaDataExtractService.performExelaExtractForDate("2000-12-31");

        verify(notificationService).sendExcelaEmail(filteredCasesCaptor.capture());
        assertEquals(1, filteredCasesCaptor.getValue().size());
        ReturnedCaseDetails filtered = filteredCasesCaptor.getValue().get(0);
        assertEquals("smith", filtered.getData().getDeceasedSurname());
    }

    @Test
    public void shouldNotExtractForDateForNoFilteredCases() throws NotificationClientException {
        List<ReturnedCaseDetails> filteredCases = new ImmutableList.Builder<ReturnedCaseDetails>()
            .build();
        when(excelaCriteriaService.getFilteredCases(any())).thenReturn(filteredCases);

        exelaDataExtractService.performExelaExtractForDate("2000-12-31");

        verify(notificationService, times(0)).sendExcelaEmail(any());
    }

    @Test(expected = ClientException.class)
    public void shouldThrowClientException() throws NotificationClientException {
        List<ReturnedCaseDetails> filteredCases = new ImmutableList.Builder<ReturnedCaseDetails>()
            .add(new ReturnedCaseDetails(caseData1, LAST_MODIFIED, 1L))
            .build();
        when(excelaCriteriaService.getFilteredCases(any())).thenReturn(filteredCases);
        when(notificationService.sendExcelaEmail(any())).thenThrow(NotificationClientException.class);

        exelaDataExtractService.performExelaExtractForDate("2000-12-31");
    }

}