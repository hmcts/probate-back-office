package uk.gov.hmcts.probate.schedule;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.service.FetchDraftCaseService;
import uk.gov.hmcts.probate.service.dataextract.DataExtractDateValidator;
import uk.gov.hmcts.reform.probate.model.client.ApiClientException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static uk.gov.hmcts.probate.model.Constants.DATE_FORMAT;

@ExtendWith(SpringExtension.class)
class FetchDraftCasesWithPaymentTaskTest {

    @Mock
    private DataExtractDateValidator dataExtractDateValidator;

    @Mock
    private FetchDraftCaseService fetchDraftCaseService;

    @InjectMocks
    private FetchDraftCasesWithPaymentTask fetchDraftCasesWithPaymentTask;
    private static final String DATE = DATE_FORMAT.format(LocalDate.now().minusDays(180L));
    private String adhocDate = "2022-09-05";
    private String adhocToDate = "2022-09-10";

    @Test
    void shouldPerformDraftCasesExtractDateRange() {
        ResponseEntity<String> responseEntity = ResponseEntity.accepted()
                .body("Perform hmrc data extract from date finished");
        fetchDraftCasesWithPaymentTask.run();
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
        assertEquals("Perform hmrc data extract from date finished", responseEntity.getBody());
        verify(dataExtractDateValidator).dateValidator(DATE, DATE);
        verify(fetchDraftCaseService).fetchGORCases(DATE, DATE);
    }

    @Test
    void shouldPerformGORDraftCasesExtractForAdhocDate() {
        fetchDraftCasesWithPaymentTask.adHocJobFromDate = "2022-09-05";
        ResponseEntity<String> responseEntity = ResponseEntity.accepted()
                .body("Perform hmrc data extract from date finished");
        fetchDraftCasesWithPaymentTask.run();
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
        assertEquals("Perform hmrc data extract from date finished", responseEntity.getBody());
        verify(dataExtractDateValidator).dateValidator(adhocDate, adhocDate);
        verify(fetchDraftCaseService).fetchGORCases(adhocDate, adhocDate);
    }

    @Test
    void shouldPerformCaveatDraftCasesExtractForAdhocDate() {
        fetchDraftCasesWithPaymentTask.adHocJobFromDate = "2022-09-05";
        ResponseEntity<String> responseEntity = ResponseEntity.accepted()
                .body("Perform hmrc data extract from date finished");
        fetchDraftCasesWithPaymentTask.run();
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
        assertEquals("Perform hmrc data extract from date finished", responseEntity.getBody());
        verify(dataExtractDateValidator).dateValidator(adhocDate, adhocDate);
        verify(fetchDraftCaseService).fetchCaveatCases(adhocDate, adhocDate);
    }

    @Test
    void shouldPerformDraftCasesExtractForAdhocDateRange() {
        fetchDraftCasesWithPaymentTask.adHocJobFromDate = "2022-09-05";
        fetchDraftCasesWithPaymentTask.adHocJobToDate = "2022-09-10";
        ResponseEntity<String> responseEntity = ResponseEntity.accepted()
                .body("Perform hmrc data extract from date finished");
        fetchDraftCasesWithPaymentTask.run();
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
        assertEquals("Perform hmrc data extract from date finished", responseEntity.getBody());
        verify(dataExtractDateValidator).dateValidator(adhocDate, adhocToDate);
        verify(fetchDraftCaseService).fetchGORCases(adhocDate, adhocToDate);
    }

    @Test
    void shouldThrowClientExceptionWithBadRequestForDraftCasesExtractWithIncorrectDateFormat() {
        doThrow(new ApiClientException(HttpStatus.BAD_REQUEST.value(), null)).when(dataExtractDateValidator)
                .dateValidator(DATE, DATE);
        fetchDraftCasesWithPaymentTask.run();
        verify(dataExtractDateValidator).dateValidator(DATE, DATE);
        verifyNoInteractions(fetchDraftCaseService);
    }

    @Test
    void shouldThrowExceptionForDraftCasesExtract() {
        doThrow(new NullPointerException()).when(dataExtractDateValidator)
                .dateValidator(DATE, DATE);
        fetchDraftCasesWithPaymentTask.run();
        verify(dataExtractDateValidator).dateValidator(DATE, DATE);
        verifyNoInteractions(fetchDraftCaseService);
    }

}