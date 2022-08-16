package uk.gov.hmcts.probate.schedule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.probate.service.DormantCaseService;
import uk.gov.hmcts.probate.service.dataextract.DataExtractDateValidator;
import uk.gov.hmcts.reform.probate.model.client.ApiClientException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verifyNoInteractions;
import static uk.gov.hmcts.probate.model.Constants.DATE_FORMAT;

@ExtendWith(SpringExtension.class)
 class MakeDormantCasesTaskTest {

    @Mock
    private DataExtractDateValidator dataExtractDateValidator;

    @Mock
    private DormantCaseService dormantCaseService;

    @InjectMocks
    private MakeDormantCasesTask makeDormantCasesTask;
    private static final String date = DATE_FORMAT.format(LocalDate.now().minusMonths(6L));

    @BeforeEach
    public void setUp() throws Exception {
        ReflectionTestUtils.setField(makeDormantCasesTask, "dormancyPeriodMonths", 6);
    }

    @Test
     void shouldMakeDormantCasesDateRange() {
        ResponseEntity<String> responseEntity = ResponseEntity.accepted()
                .body("Perform make dormant finished");
        makeDormantCasesTask.run();
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
        assertEquals("Perform make dormant finished", responseEntity.getBody());
        verify(dataExtractDateValidator).dateValidator(date,date);
        verify(dormantCaseService).makeCasesDormant(date);
    }

    @Test
     void shouldThrowClientExceptionWithBadRequestForMakeDormantCasesWithIncorrectDateFormat() {
        doThrow(new ApiClientException(HttpStatus.BAD_REQUEST.value(), null)).when(dataExtractDateValidator)
                .dateValidator(date, date);
        makeDormantCasesTask.run();
        verify(dataExtractDateValidator).dateValidator(date,date);
        verifyNoInteractions(dormantCaseService);
    }

    @Test
    void shouldThrowNullPointerExceptionForMakeDormantCases() {
        doThrow(new NullPointerException()).when(dataExtractDateValidator)
                .dateValidator(date, date);
        makeDormantCasesTask.run();
        verify(dataExtractDateValidator).dateValidator(date,date);
        verifyNoInteractions(dormantCaseService);
    }

}
