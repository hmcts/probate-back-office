package uk.gov.hmcts.probate.schedule;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.service.dataextract.DataExtractDateValidator;
import uk.gov.hmcts.probate.service.dataextract.ExelaDataExtractService;
import uk.gov.hmcts.reform.probate.model.client.ApiClientException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.doThrow;
import static uk.gov.hmcts.probate.model.Constants.DATE_FORMAT;

@ExtendWith(SpringExtension.class)
class ExelaExtractTaskTest {

    @Mock
    private DataExtractDateValidator dataExtractDateValidator;

    @Mock
    private ExelaDataExtractService exelaDataExtractService;

    @InjectMocks
    private ExelaExtractTask exelaExtractTask;
    private static final String DATE = DATE_FORMAT.format(LocalDate.now().minusDays(1L));
    private String adhocDate = "2022-09-05";
    private String adhocToDate = "2022-09-10";

    @Test
    void shouldPerformExelaExtractDateRange() {
        ResponseEntity<String> responseEntity = ResponseEntity.accepted()
                .body("Perform exela data extract from date finished");
        exelaExtractTask.run();
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
        assertEquals("Perform exela data extract from date finished", responseEntity.getBody());
        verify(dataExtractDateValidator).dateValidator(DATE, DATE);
        verify(exelaDataExtractService).performExelaExtractForDateRange(DATE, DATE);
    }

    @Test
    void shouldPerformHmrcExtractForAdhocDate() {
        exelaExtractTask.adHocJobStartDate = "2022-09-05";
        ResponseEntity<String> responseEntity = ResponseEntity.accepted()
                .body("Perform exela data extract from date finished");
        exelaExtractTask.run();
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
        assertEquals("Perform exela data extract from date finished", responseEntity.getBody());
        verify(dataExtractDateValidator).dateValidator(adhocDate, adhocDate);
        verify(exelaDataExtractService).performExelaExtractForDateRange(adhocDate, adhocDate);
    }

    @Test
    void shouldPerformHmrcExtractForAdhocDateRange() {
        exelaExtractTask.adHocJobStartDate = "2022-09-05";
        exelaExtractTask.adHocJobEndDate = "2022-09-10";
        ResponseEntity<String> responseEntity = ResponseEntity.accepted()
                .body("Perform exela data extract from date finished");
        exelaExtractTask.run();
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
        assertEquals("Perform exela data extract from date finished", responseEntity.getBody());
        verify(dataExtractDateValidator).dateValidator(adhocDate, adhocToDate);
        verify(exelaDataExtractService).performExelaExtractForDateRange(adhocDate, adhocToDate);
    }

    @Test
    void shouldThrowClientExceptionWithBadRequestForHmrcExtractWithIncorrectDateFormat() {
        doThrow(new ApiClientException(HttpStatus.BAD_REQUEST.value(), null)).when(dataExtractDateValidator)
                .dateValidator(DATE, DATE);
        exelaExtractTask.run();
        verify(dataExtractDateValidator).dateValidator(DATE, DATE);
        verifyNoInteractions(exelaDataExtractService);
    }

    @Test
    void shouldThrowExceptionForHmrcExtract() {
        doThrow(new NullPointerException()).when(dataExtractDateValidator)
                .dateValidator(DATE, DATE);
        exelaExtractTask.run();
        verify(dataExtractDateValidator).dateValidator(DATE, DATE);
        verifyNoInteractions(exelaDataExtractService);
    }

}
