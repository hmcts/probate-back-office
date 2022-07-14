package uk.gov.hmcts.probate.schedule;

import feign.FeignException;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.service.dataextract.DataExtractDateValidator;
import uk.gov.hmcts.probate.service.dataextract.SmeeAndFordDataExtractService;
import uk.gov.hmcts.reform.probate.model.client.ApiClientException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.doThrow;

@ExtendWith(SpringExtension.class)
public class SmeeAndFordExtractTaskTest {

    @Mock
    private DataExtractDateValidator dataExtractDateValidator;

    @Mock
    private SmeeAndFordDataExtractService smeeAndFordDataExtractService;

    @InjectMocks
    private SmeeAndFordExtractTask smeeAndFordExtractTask;

    private static DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Test
    public void shouldInitiateSmeeAndFordExtractDateRange() {

        ResponseEntity<String> responseEntity = ResponseEntity.accepted()
                .body("Perform Smee And Ford data extract finished");
        smeeAndFordExtractTask.run();
        String date = DATE_FORMAT.format(LocalDate.now().minusDays(1L));
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
        assertEquals("Perform Smee And Ford data extract finished", responseEntity.getBody());
        verify(dataExtractDateValidator).dateValidator(date,date);
        verify(smeeAndFordDataExtractService).performSmeeAndFordExtractForDateRange(date,date);
    }

    @Test
    public void shouldThrowClientExceptionWithBadRequestForSmeeAndFordExtractWithIncorrectDateFormat() {
        String date = DATE_FORMAT.format(LocalDate.now().minusDays(1L));
        doThrow(new ApiClientException(HttpStatus.BAD_REQUEST.value(), null)).when(dataExtractDateValidator)
                .dateValidator(date, date);
        smeeAndFordExtractTask.run();
        verify(dataExtractDateValidator).dateValidator(date,date);
        verifyNoInteractions(smeeAndFordDataExtractService);
    }

    @Test
    void shouldThrowFeignExceptionForSmeeAndFordExtract() {
        String date = DATE_FORMAT.format(LocalDate.now().minusDays(1L));
        when(smeeAndFordDataExtractService.performSmeeAndFordExtractForDateRange(date, date)).thenThrow(FeignException
                .errorStatus("performSmeeAndFordExtractForDateRange", Response.builder()
                        .status(404)
                        .reason("message error")
                        .request(Request.create(
                                Request.HttpMethod.POST,
                                "/data-extract/smee-and-ford",
                                new HashMap<>(),
                                null,
                                null,
                                null))
                        .body(new byte[0])
                        .build()));
        smeeAndFordExtractTask.run();
        verify(dataExtractDateValidator).dateValidator(date,date);
        verify(smeeAndFordDataExtractService).performSmeeAndFordExtractForDateRange(date, date);
    }

    @Test
    void shouldThrowExceptionForSmeeAndFordExtract() {
        String date = DATE_FORMAT.format(LocalDate.now().minusDays(1L));
        doThrow(new NullPointerException()).when(dataExtractDateValidator)
                .dateValidator(date, date);
        smeeAndFordExtractTask.run();
        verify(dataExtractDateValidator).dateValidator(date,date);
        verifyNoInteractions(smeeAndFordDataExtractService);
    }
}
