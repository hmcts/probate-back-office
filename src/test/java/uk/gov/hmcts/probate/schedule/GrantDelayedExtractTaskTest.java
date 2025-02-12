package uk.gov.hmcts.probate.schedule;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.service.GrantNotificationService;
import uk.gov.hmcts.probate.service.dataextract.DataExtractDateValidator;
import uk.gov.hmcts.reform.probate.model.client.ApiClientException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static uk.gov.hmcts.probate.model.Constants.DATE_FORMAT;

@ExtendWith(SpringExtension.class)
class GrantDelayedExtractTaskTest {

    @Mock
    private DataExtractDateValidator dataExtractDateValidator;

    @Mock
    private GrantNotificationService grantNotificationService;

    @InjectMocks
    private GrantDelayedExtractTask grantDelayedExtractTask;
    private static final String DATE = DATE_FORMAT.format(LocalDate.now().minusDays(1L));
    private String adhocDate = "2022-09-05";

    @Test
    void shouldPerformGrantDelayedExtractYesterdayDate() {
        ResponseEntity<String> responseEntity = ResponseEntity.accepted()
                .body("Perform grant delayed data extract from date finished");
        grantDelayedExtractTask.run();
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
        assertEquals("Perform grant delayed data extract from date finished", responseEntity.getBody());
        verify(dataExtractDateValidator).dateValidator(DATE);
        verify(grantNotificationService).handleGrantDelayedNotification(DATE);
    }

    @Test
    void shouldPerformGrantDelayedExtractForAdhocDate() {
        grantDelayedExtractTask.adHocJobFromDate = "2022-09-05";
        ResponseEntity<String> responseEntity = ResponseEntity.accepted()
                .body("Perform grant delayed data extract from date finished");
        grantDelayedExtractTask.run();
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
        assertEquals("Perform grant delayed data extract from date finished", responseEntity.getBody());
        verify(dataExtractDateValidator).dateValidator(adhocDate);
        verify(grantNotificationService).handleGrantDelayedNotification(adhocDate);
    }

    @Test
    void shouldThrowClientExceptionWithBadRequestForGrantDelayedExtractWithIncorrectDateFormat() {
        doThrow(new ApiClientException(HttpStatus.BAD_REQUEST.value(), null)).when(dataExtractDateValidator)
                .dateValidator(DATE);
        grantDelayedExtractTask.run();
        verify(dataExtractDateValidator).dateValidator(DATE);
        verifyNoInteractions(grantNotificationService);
    }

    @Test
    void shouldThrowExceptionForGrantDelayedExtract() {
        doThrow(new NullPointerException()).when(dataExtractDateValidator)
                .dateValidator(DATE);
        grantDelayedExtractTask.run();
        verify(dataExtractDateValidator).dateValidator(DATE);
        verifyNoInteractions(grantNotificationService);
    }

}
