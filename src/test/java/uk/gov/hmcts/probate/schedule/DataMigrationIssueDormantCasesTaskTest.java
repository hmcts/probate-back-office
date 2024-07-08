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
import uk.gov.hmcts.probate.service.MigrationIssueDormantCaseService;
import uk.gov.hmcts.probate.service.dataextract.DataExtractDateValidator;
import uk.gov.hmcts.reform.probate.model.client.ApiClientException;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(SpringExtension.class)
 class DataMigrationIssueDormantCasesTaskTest {

    @Mock
    private DataExtractDateValidator dataExtractDateValidator;

    @Mock
    private MigrationIssueDormantCaseService migrationIssueDormantCaseService;

    @InjectMocks
    private DataMigrationIssueDormantCasesTask dataMigrationIssueDormantCasesTask;
    private final String date = "2024-07-05";
    private final List<String> references = Arrays.asList("1234567890123456");

    @BeforeEach
    public void setUp() throws Exception {
        ReflectionTestUtils.setField(dataMigrationIssueDormantCasesTask, "caseReferences",
                "1234567890123456");
        ReflectionTestUtils.setField(dataMigrationIssueDormantCasesTask, "adHocJobDate", "2024-07-05");
    }

    @Test
     void shouldMakeDormantCasesOnAdhocDate() {
        ResponseEntity<String> responseEntity = ResponseEntity.accepted()
                .body("Perform make dormant finished");
        dataMigrationIssueDormantCasesTask.run();
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
        assertEquals("Perform make dormant finished", responseEntity.getBody());
        verify(dataExtractDateValidator).dateValidator(date);
        verify(migrationIssueDormantCaseService).makeCaseReferenceDormant(references);
    }

    @Test
    void shouldNotMakeDormantCasesIfNoAdhocDate() {
        ReflectionTestUtils.setField(dataMigrationIssueDormantCasesTask, "adHocJobDate", null);
        dataMigrationIssueDormantCasesTask.run();
        verifyNoInteractions(dataExtractDateValidator);
        verifyNoInteractions(migrationIssueDormantCaseService);
    }

    @Test
     void shouldThrowClientExceptionWithBadRequestForMakeDormantCasesWithIncorrectDateFormat() {
        doThrow(new ApiClientException(HttpStatus.BAD_REQUEST.value(), null)).when(dataExtractDateValidator)
                .dateValidator(date);
        dataMigrationIssueDormantCasesTask.run();
        verify(dataExtractDateValidator).dateValidator(date);
        verifyNoInteractions(migrationIssueDormantCaseService);
    }

    @Test
    void shouldThrowNullPointerExceptionForDormantCases() {
        doThrow(new NullPointerException()).when(dataExtractDateValidator)
                .dateValidator(date);
        dataMigrationIssueDormantCasesTask.run();
        verify(dataExtractDateValidator).dateValidator(date);
        verifyNoInteractions(migrationIssueDormantCaseService);
    }

}
