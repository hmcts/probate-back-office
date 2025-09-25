package uk.gov.hmcts.probate.service.dataextract;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;
import uk.gov.hmcts.probate.service.CaseQueryService;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.DataExtractType.NATIONAL_FRAUD_INITIATIVE;

@ExtendWith(MockitoExtension.class)
class DataExtractServiceTest {

    @Mock
    private CaseQueryService caseQueryService;

    @Mock
    private DataExtractStrategy nfiStrategy;

    @Mock
    private DataExtractStrategy otherStrategy;

    private DataExtractService underTest;

    @BeforeEach
    void setUp() {
        when(nfiStrategy.matchesType(NATIONAL_FRAUD_INITIATIVE)).thenReturn(true);
        when(nfiStrategy.getQueryPath()).thenReturn("some-query-path");
        when(otherStrategy.matchesType(any())).thenReturn(false);
        underTest = new DataExtractService(caseQueryService, List.of(otherStrategy, nfiStrategy));
    }

    @Test
    void performsExtractForEachDayInRangeHappyPath() throws IOException {
        String d1 = "2025-08-01";
        String d2 = "2025-08-02";
        String d3 = "2025-08-03";

        List<ReturnedCaseDetails> day1Cases = Collections.emptyList();
        List<ReturnedCaseDetails> day2Cases = Collections.emptyList();
        List<ReturnedCaseDetails> day3Cases = Collections.emptyList();

        when(caseQueryService.findCaseWithQueryPathAndDate(String.valueOf(NATIONAL_FRAUD_INITIATIVE),
                nfiStrategy.getQueryPath(), d1)).thenReturn(day1Cases);
        when(caseQueryService.findCaseWithQueryPathAndDate(String.valueOf(NATIONAL_FRAUD_INITIATIVE),
                nfiStrategy.getQueryPath(), d2)).thenReturn(day2Cases);
        when(caseQueryService.findCaseWithQueryPathAndDate(String.valueOf(NATIONAL_FRAUD_INITIATIVE),
                nfiStrategy.getQueryPath(), d3)).thenReturn(day3Cases);

        File f1 = new File("nfi-2025-08-01.zip");
        File f2 = new File("nfi-2025-08-02.zip");
        File f3 = new File("nfi-2025-08-03.zip");

        when(nfiStrategy.generateZipFile(day1Cases, d1)).thenReturn(f1);
        when(nfiStrategy.generateZipFile(day2Cases, d2)).thenReturn(f2);
        when(nfiStrategy.generateZipFile(day3Cases, d3)).thenReturn(f3);

        assertDoesNotThrow(() ->
                underTest.performExtractForDateRange(d1, d3, NATIONAL_FRAUD_INITIATIVE));

        verify(caseQueryService).findCaseWithQueryPathAndDate(String.valueOf(NATIONAL_FRAUD_INITIATIVE),
                nfiStrategy.getQueryPath(), d1);
        verify(caseQueryService).findCaseWithQueryPathAndDate(String.valueOf(NATIONAL_FRAUD_INITIATIVE),
                nfiStrategy.getQueryPath(), d2);
        verify(caseQueryService).findCaseWithQueryPathAndDate(String.valueOf(NATIONAL_FRAUD_INITIATIVE),
                nfiStrategy.getQueryPath(), d3);

        InOrder inOrder = inOrder(nfiStrategy);
        inOrder.verify(nfiStrategy).matchesType(NATIONAL_FRAUD_INITIATIVE);
        inOrder.verify(nfiStrategy).generateZipFile(day1Cases, d1);
        inOrder.verify(nfiStrategy).uploadToBlobStorage(f1);

        verify(nfiStrategy).generateZipFile(day2Cases, d2);
        verify(nfiStrategy).uploadToBlobStorage(f2);
        verify(nfiStrategy).generateZipFile(day3Cases, d3);
        verify(nfiStrategy).uploadToBlobStorage(f3);

        verify(otherStrategy, atLeastOnce()).matchesType(any());
        verifyNoMoreInteractions(otherStrategy);
    }

    @Test
    void perDayFailureInGenerateZipFileIsCaughtAndSubsequentDaysProceed() throws IOException {
        String d1 = "2025-08-01";
        String d2 = "2025-08-02";
        String d3 = "2025-08-03";

        when(caseQueryService.findCaseWithQueryPathAndDate(anyString(), anyString(), eq(d1)))
                .thenReturn(Collections.emptyList());
        when(caseQueryService.findCaseWithQueryPathAndDate(anyString(), anyString(), eq(d2)))
                .thenReturn(Collections.emptyList());
        when(caseQueryService.findCaseWithQueryPathAndDate(anyString(), anyString(), eq(d3)))
                .thenReturn(Collections.emptyList());

        when(nfiStrategy.generateZipFile(anyList(), eq(d1))).thenReturn(new File("ok1.zip"));
        when(nfiStrategy.generateZipFile(anyList(), eq(d2))).thenThrow(new RuntimeException("boom on day 2"));
        when(nfiStrategy.generateZipFile(anyList(), eq(d3))).thenReturn(new File("ok3.zip"));

        assertDoesNotThrow(() ->
                underTest.performExtractForDateRange(d1, d3, NATIONAL_FRAUD_INITIATIVE));

        // Day 1 uploaded
        verify(nfiStrategy).uploadToBlobStorage(new File("ok1.zip"));
        // Day 2 failed -> no upload call for day 2
        verify(nfiStrategy, never()).uploadToBlobStorage(argThat(f -> "ok2.zip".equals(f.getName())));
        // Day 3 uploaded
        verify(nfiStrategy).uploadToBlobStorage(new File("ok3.zip"));
    }

    @Test
    void failureInCaseQueryServiceIsCaughtAndNoStrategyCallsForThatDay() throws IOException {
        String d1 = "2025-08-01";
        String d2 = "2025-08-02";

        when(caseQueryService.findCaseWithQueryPathAndDate(anyString(),anyString(),  eq(d1)))
                .thenThrow(new RuntimeException("ES down"));
        when(caseQueryService.findCaseWithQueryPathAndDate(anyString(), anyString(), eq(d2)))
                .thenReturn(Collections.emptyList());
        when(nfiStrategy.generateZipFile(anyList(), eq(d2))).thenReturn(new File("ok.zip"));

        assertDoesNotThrow(() ->
                underTest.performExtractForDateRange(d1, d2, NATIONAL_FRAUD_INITIATIVE));

        verify(nfiStrategy, never()).generateZipFile(anyList(), eq(d1));
        verify(nfiStrategy, never()).uploadToBlobStorage(argThat(f -> "d1.zip".equals(f.getName())));

        verify(nfiStrategy).generateZipFile(Collections.emptyList(), d2);
        verify(nfiStrategy).uploadToBlobStorage(new File("ok.zip"));
    }
}
