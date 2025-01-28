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
class GrantAwaitingDocumentationExtractTaskTest {

    @Mock
    private DataExtractDateValidator dataExtractDateValidator;

    @Mock
    private GrantNotificationService grantNotificationService;

    @InjectMocks
    private GrantAwaitingDocumentationExtractTask grantAwaitingDocumentationExtractTask;
    private static final String DATE = DATE_FORMAT.format(LocalDate.now().minusDays(1L));
    private String adhocDate = "2022-09-05";

    @Test
    void shouldPerformGrantAwaitingDocumentationExtractYesterdayDate() {
        ResponseEntity<String> responseEntity = ResponseEntity.accepted()
                .body("Perform grant awaiting documentation data extract from date finished");
        grantAwaitingDocumentationExtractTask.run();
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
        assertEquals("Perform grant awaiting documentation data extract from date finished",
                responseEntity.getBody());
        verify(dataExtractDateValidator).dateValidator(DATE);
        verify(grantNotificationService).handleAwaitingDocumentationNotification(DATE);
    }

    @Test
    void shouldPerformGrantAwaitingDocumentationExtractForAdhocDate() {
        grantAwaitingDocumentationExtractTask.adHocJobFromDate = "2022-09-05";
        ResponseEntity<String> responseEntity = ResponseEntity.accepted()
                .body("Perform grant awaiting documentation data extract from date finished");
        grantAwaitingDocumentationExtractTask.run();
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
        assertEquals("Perform grant awaiting documentation data extract from date finished",
                responseEntity.getBody());
        verify(dataExtractDateValidator).dateValidator(adhocDate);
        verify(grantNotificationService).handleAwaitingDocumentationNotification(adhocDate);
    }

    @Test
    void shouldThrowClientExceptionWithBadRequestForGrantAwaitingDocumentationExtractWithIncorrectDateFormat() {
        doThrow(new ApiClientException(HttpStatus.BAD_REQUEST.value(), null)).when(dataExtractDateValidator)
                .dateValidator(DATE);
        grantAwaitingDocumentationExtractTask.run();
        verify(dataExtractDateValidator).dateValidator(DATE);
        verifyNoInteractions(grantNotificationService);
    }

    @Test
    void shouldThrowExceptionForGrantAwaitingDocumentationExtract() {
        doThrow(new NullPointerException()).when(dataExtractDateValidator)
                .dateValidator(DATE);
        grantAwaitingDocumentationExtractTask.run();
        verify(dataExtractDateValidator).dateValidator(DATE);
        verifyNoInteractions(grantNotificationService);
    }

}
