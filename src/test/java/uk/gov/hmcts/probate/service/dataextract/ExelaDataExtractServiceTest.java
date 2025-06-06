package uk.gov.hmcts.probate.service.dataextract;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import uk.gov.hmcts.probate.service.ExelaCriteriaService;
import uk.gov.hmcts.probate.service.FileTransferService;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.service.notify.NotificationClientException;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExelaDataExtractServiceTest {
    @InjectMocks
    private ExelaDataExtractService exelaDataExtractService;
    @Mock
    private CaseQueryService caseQueryService;
    @Mock
    private NotificationService notificationService;
    @Mock
    private ExelaCriteriaService exelaCriteriaService;
    @Mock
    private FileTransferService fileTransferService;

    @InjectMocks
    private HmrcDataExtractService hmrcDataExtractService;

    @Captor
    private ArgumentCaptor<List<ReturnedCaseDetails>> filteredCasesCaptor;

    private static final LocalDateTime LAST_MODIFIED = LocalDateTime.now(ZoneOffset.UTC).minusYears(2);

    private CaseData caseData1;
    private CaseData caseData2;

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


        when(caseQueryService.findGrantIssuedCasesWithGrantIssuedDate(any(), any())).thenReturn(returnedCases);
        when(caseQueryService.findCaseStateWithinDateRangeExela(any(), any())).thenReturn(returnedCases);
        when(fileTransferService.uploadFile(any())).thenReturn(HttpStatus.CREATED.value());
    }

    @Test
    void shouldExtractForDateForFilteredCases() throws NotificationClientException {
        List<ReturnedCaseDetails> filteredCases = new ImmutableList.Builder<ReturnedCaseDetails>()
            .add(new ReturnedCaseDetails(caseData1, LAST_MODIFIED, 1L))
            .build();
        when(exelaCriteriaService.getFilteredCases(any())).thenReturn(filteredCases);

        exelaDataExtractService.performExelaExtractForDate("2000-12-31");

        verify(notificationService).sendExelaEmail(filteredCasesCaptor.capture());
        assertEquals(1, filteredCasesCaptor.getValue().size());
        ReturnedCaseDetails filtered = filteredCasesCaptor.getValue().get(0);
        assertEquals("smith", filtered.getData().getDeceasedSurname());
    }

    @Test
    void shouldExtractForDateRangeForFilteredCases() throws NotificationClientException {
        List<ReturnedCaseDetails> filteredCases = new ImmutableList.Builder<ReturnedCaseDetails>()
                .add(new ReturnedCaseDetails(caseData1, LAST_MODIFIED, 1L))
                .add(new ReturnedCaseDetails(caseData2, LAST_MODIFIED, 2L))
                .build();
        when(exelaCriteriaService.getFilteredCases(any())).thenReturn(filteredCases);

        exelaDataExtractService.performExelaExtractForDateRange("2000-12-30", "2000-12-31");

        verify(notificationService).sendExelaEmail(filteredCasesCaptor.capture());
        assertEquals(2, filteredCasesCaptor.getValue().size());
        ReturnedCaseDetails filtered = filteredCasesCaptor.getValue().get(0);
        assertEquals("smith", filtered.getData().getDeceasedSurname());
        ReturnedCaseDetails filtered2 = filteredCasesCaptor.getValue().get(1);
        assertEquals("jones", filtered2.getData().getDeceasedSurname());
    }

    @Test
    void shouldNotExtractForDateForNoFilteredCases() throws NotificationClientException {
        List<ReturnedCaseDetails> filteredCases = new ImmutableList.Builder<ReturnedCaseDetails>()
            .build();
        when(exelaCriteriaService.getFilteredCases(any())).thenReturn(filteredCases);

        exelaDataExtractService.performExelaExtractForDate("2000-12-31");

        verify(notificationService, times(0)).sendExelaEmail(any());
    }

    @Test
    void shouldNotExtractForDateRangeForNoFilteredCases() throws NotificationClientException {
        List<ReturnedCaseDetails> filteredCases = new ImmutableList.Builder<ReturnedCaseDetails>()
                .build();
        when(exelaCriteriaService.getFilteredCases(any())).thenReturn(filteredCases);

        exelaDataExtractService.performExelaExtractForDateRange("2000-12-30", "2000-12-31");

        verify(notificationService, times(0)).sendExelaEmail(any());
    }

    @Test
    void shouldThrowClientException() throws NotificationClientException {
        assertThrows(ClientException.class, () -> {
            List<ReturnedCaseDetails> filteredCases = new ImmutableList.Builder<ReturnedCaseDetails>()
                    .add(new ReturnedCaseDetails(caseData1, LAST_MODIFIED, 1L))
                    .build();
            when(exelaCriteriaService.getFilteredCases(any())).thenReturn(filteredCases);
            doThrow(NotificationClientException.class)
                    .when(notificationService).sendExelaEmail(any());

            exelaDataExtractService.performExelaExtractForDate("2000-12-31");
        });
    }

    @Test
    void shouldThrowClientExceptionForDateRange() throws NotificationClientException {
        assertThrows(ClientException.class, () -> {
            List<ReturnedCaseDetails> filteredCases = new ImmutableList.Builder<ReturnedCaseDetails>()
                    .add(new ReturnedCaseDetails(caseData1, LAST_MODIFIED, 1L))
                    .build();
            when(exelaCriteriaService.getFilteredCases(any())).thenReturn(filteredCases);
            doThrow(NotificationClientException.class)
                    .when(notificationService).sendExelaEmail(any());

            exelaDataExtractService.performExelaExtractForDateRange("2000-12-30", "2000-12-31");
        });
    }

}
