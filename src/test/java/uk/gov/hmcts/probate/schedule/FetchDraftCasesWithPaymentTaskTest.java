package uk.gov.hmcts.probate.schedule;

import org.junit.jupiter.api.BeforeEach;
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

import java.time.Clock;
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

    @Mock
    private Clock clock;

    @InjectMocks
    private FetchDraftCasesWithPaymentTask fetchDraftCasesWithPaymentTask;
    private static final String DATE_TODAY = DATE_FORMAT.format(LocalDate.now());
    private final String adhocDate = "2022-09-05";

    @BeforeEach
    void setUp() {
        clock = Clock.systemDefaultZone();

        fetchDraftCasesWithPaymentTask = new FetchDraftCasesWithPaymentTask(
                dataExtractDateValidator,
                fetchDraftCaseService,
                adhocDate,
                clock
        );
    }

    @Test
    void shouldPerformDraftCasesExtractDateRange() {
        ResponseEntity<String> responseEntity = ResponseEntity.accepted()
                .body("Perform hmrc data extract from date finished");
        fetchDraftCasesWithPaymentTask.run();
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
        assertEquals("Perform hmrc data extract from date finished", responseEntity.getBody());
        verify(dataExtractDateValidator).dateValidator(adhocDate, DATE_TODAY);
        verify(fetchDraftCaseService).fetchGORCases(adhocDate, DATE_TODAY);
    }

    @Test
    void shouldPerformGORDraftCasesExtractForAdhocDate() {
        ResponseEntity<String> responseEntity = ResponseEntity.accepted()
                .body("Perform hmrc data extract from date finished");
        fetchDraftCasesWithPaymentTask.run();
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
        assertEquals("Perform hmrc data extract from date finished", responseEntity.getBody());
        verify(dataExtractDateValidator).dateValidator(adhocDate, DATE_TODAY);
        verify(fetchDraftCaseService).fetchGORCases(adhocDate, DATE_TODAY);
    }

    @Test
    void shouldPerformDraftCasesExtractForAdhocDateRange() {
        ResponseEntity<String> responseEntity = ResponseEntity.accepted()
                .body("Perform hmrc data extract from date finished");
        fetchDraftCasesWithPaymentTask.run();
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
        assertEquals("Perform hmrc data extract from date finished", responseEntity.getBody());
        verify(dataExtractDateValidator).dateValidator(adhocDate, DATE_TODAY);
        verify(fetchDraftCaseService).fetchGORCases(adhocDate, DATE_TODAY);
    }

    @Test
    void shouldThrowClientExceptionWithBadRequestForDraftCasesExtractWithIncorrectDateFormat() {
        doThrow(new ApiClientException(HttpStatus.BAD_REQUEST.value(), null)).when(dataExtractDateValidator)
                .dateValidator(adhocDate, DATE_TODAY);
        fetchDraftCasesWithPaymentTask.run();
        verify(dataExtractDateValidator).dateValidator(adhocDate, DATE_TODAY);
        verifyNoInteractions(fetchDraftCaseService);
    }

    @Test
    void shouldThrowExceptionForDraftCasesExtract() {
        doThrow(new NullPointerException()).when(dataExtractDateValidator)
                .dateValidator(adhocDate, DATE_TODAY);
        fetchDraftCasesWithPaymentTask.run();
        verify(dataExtractDateValidator).dateValidator(adhocDate, DATE_TODAY);
        verifyNoInteractions(fetchDraftCaseService);
    }

}