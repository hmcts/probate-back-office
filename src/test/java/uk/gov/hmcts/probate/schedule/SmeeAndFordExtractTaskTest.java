package uk.gov.hmcts.probate.schedule;

import feign.FeignException;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.probate.service.dataextract.DataExtractDateValidator;
import uk.gov.hmcts.probate.service.dataextract.SmeeAndFordDataExtractService;
import uk.gov.hmcts.reform.probate.model.client.ApiClientException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(SpringExtension.class)
class SmeeAndFordExtractTaskTest {

    @Mock
    private DataExtractDateValidator dataExtractDateValidator;

    @Mock
    private SmeeAndFordDataExtractService smeeAndFordDataExtractService;

    @InjectMocks
    private SmeeAndFordExtractTask smeeAndFordExtractTask;

    private static DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @BeforeEach
    public void setUp() throws Exception {
        ReflectionTestUtils.setField(smeeAndFordExtractTask, "schedulerTimerShutdownDelayMinutes", "1");
    }

    @Test
    void shouldInitiateSmeeAndFordExtractDateRange() {

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
    void shouldInitiateSmeeAndFordForAdhocJobDateRange() {
        smeeAndFordExtractTask.adHocJobDate = "2022-09-01";
        ResponseEntity<String> responseEntity = ResponseEntity.accepted()
                .body("Perform Smee And Ford data extract finished");
        smeeAndFordExtractTask.run();
        String date = smeeAndFordExtractTask.adHocJobDate;
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
        assertEquals("Perform Smee And Ford data extract finished", responseEntity.getBody());
        verify(dataExtractDateValidator).dateValidator(date,date);
        verify(smeeAndFordDataExtractService).performSmeeAndFordExtractForDateRange(date,date);
    }

    @Test
    void shouldThrowClientExceptionWithBadRequestForSmeeAndFordExtractWithIncorrectDateFormat() {
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
        doThrow(FeignException
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
                        .build())).when(smeeAndFordDataExtractService).performSmeeAndFordExtractForDateRange(date,
                date);
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
