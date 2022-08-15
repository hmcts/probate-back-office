package uk.gov.hmcts.probate.schedule;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.service.DormantCaseService;
import uk.gov.hmcts.probate.service.dataextract.DataExtractDateValidator;
import uk.gov.hmcts.reform.probate.model.client.ApiClientException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verifyNoInteractions;
import static uk.gov.hmcts.probate.model.Constants.DATE_FORMAT;
import static uk.gov.hmcts.probate.model.Constants.REACTIVATE_DORMANT_FROM_DAY;

@ExtendWith(SpringExtension.class)
class ReactivateDormantCasesTaskTest {

    @Mock
    private DataExtractDateValidator dataExtractDateValidator;

    @Mock
    private DormantCaseService dormantCaseService;

    @InjectMocks
    private ReactivateDormantCasesTask reactivateDormantCasesTask;
    private static final String date = DATE_FORMAT.format(LocalDate.now().minusDays(REACTIVATE_DORMANT_FROM_DAY));

    @Test
    void shouldReactivateDormantCasesDateRange() {
        ResponseEntity<String> responseEntity = ResponseEntity.accepted()
                .body("Perform reactivate dormant finished");
        reactivateDormantCasesTask.run();
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
        assertEquals("Perform reactivate dormant finished", responseEntity.getBody());
        verify(dataExtractDateValidator).dateValidator(date, date);
        verify(dormantCaseService).reactivateDormantCases(date);
    }

    @Test
    void shouldThrowClientExceptionWithBadRequestForReactivateDormantCasesWithIncorrectDateFormat() {
        doThrow(new ApiClientException(HttpStatus.BAD_REQUEST.value(), null)).when(dataExtractDateValidator)
                .dateValidator(date, date);
        reactivateDormantCasesTask.run();
        verify(dataExtractDateValidator).dateValidator(date, date);
        verifyNoInteractions(dormantCaseService);
    }

    @Test
    void shouldThrowExceptionForReactivateDormantCases() {
        doThrow(new NullPointerException()).when(dataExtractDateValidator)
                .dateValidator(date, date);
        reactivateDormantCasesTask.run();
        verify(dataExtractDateValidator).dateValidator(date, date);
        verifyNoInteractions(dormantCaseService);
    }
}
