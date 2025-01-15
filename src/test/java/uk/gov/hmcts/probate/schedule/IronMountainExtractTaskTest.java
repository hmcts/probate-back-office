package uk.gov.hmcts.probate.schedule;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.service.dataextract.DataExtractDateValidator;
import uk.gov.hmcts.probate.service.dataextract.IronMountainDataExtractService;
import uk.gov.hmcts.reform.probate.model.client.ApiClientException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.doThrow;
import static uk.gov.hmcts.probate.model.Constants.DATE_FORMAT;

@ExtendWith(SpringExtension.class)
class IronMountainExtractTaskTest {

    @Mock
    private DataExtractDateValidator dataExtractDateValidator;

    @Mock
    private IronMountainDataExtractService ironMountainDataExtractServiceData;

    @InjectMocks
    private IronMountainExtractTask ironMountainExtractTask;
    private static final String date = DATE_FORMAT.format(LocalDate.now().minusDays(1L));
    private String adhocDate = "2022-09-05";

    @Test
    void shouldPerformIronMountainExtractYesterdayDate() {
        ResponseEntity<String> responseEntity = ResponseEntity.accepted()
                .body("Perform iron mountain data extract from date finished");
        ironMountainExtractTask.run();
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
        assertEquals("Perform iron mountain data extract from date finished", responseEntity.getBody());
        verify(dataExtractDateValidator).dateValidator(date);
        verify(ironMountainDataExtractServiceData).performIronMountainExtractForDate(date);
    }

    @Test
    void shouldPerformIronMountainExtractForAdhocDate() {
        ironMountainExtractTask.adHocJobFromDate = "2022-09-05";
        ResponseEntity<String> responseEntity = ResponseEntity.accepted()
                .body("Perform iron mountain data extract from date finished");
        ironMountainExtractTask.run();
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
        assertEquals("Perform iron mountain data extract from date finished", responseEntity.getBody());
        verify(dataExtractDateValidator).dateValidator(adhocDate);
        verify(ironMountainDataExtractServiceData).performIronMountainExtractForDate(adhocDate);
    }

    @Test
    void shouldThrowClientExceptionWithBadRequestForIronMountainExtractWithIncorrectDateFormat() {
        doThrow(new ApiClientException(HttpStatus.BAD_REQUEST.value(), null)).when(dataExtractDateValidator)
                .dateValidator(date);
        ironMountainExtractTask.run();
        verify(dataExtractDateValidator).dateValidator(date);
        verifyNoInteractions(ironMountainDataExtractServiceData);
    }

    @Test
    void shouldThrowExceptionForIronMountainExtract() {
        doThrow(new NullPointerException()).when(dataExtractDateValidator)
                .dateValidator(date);
        ironMountainExtractTask.run();
        verify(dataExtractDateValidator).dateValidator(date);
        verifyNoInteractions(ironMountainDataExtractServiceData);
    }

}
