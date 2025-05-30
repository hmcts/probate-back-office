package uk.gov.hmcts.probate.schedule;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.ScheduleDates;
import uk.gov.hmcts.probate.service.dataextract.DataExtractDateValidator;
import uk.gov.hmcts.probate.service.dataextract.IronMountainDataExtractService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

class IronMountainExtractTaskTest {

    @Mock
    private DataExtractDateValidator dataExtractDateValidatorMock;
    @Mock
    private IronMountainDataExtractService ironMountainDataExtractServiceDataMock;
    @Mock
    private ScheduleDates scheduleDatesMock;

    private IronMountainExtractTask ironMountainExtractTask;

    private AutoCloseable closeableMocks;

    private static final String AD_HOC_JOB_START_DATE = "2025-04-20";
    private static final String YESTERDAY = "2025-04-22";

    @BeforeEach
    void setUp() {
        closeableMocks = MockitoAnnotations.openMocks(this);

        when(scheduleDatesMock.hasValue())
                .thenReturn(false);
        when(scheduleDatesMock.getYesterday())
                .thenReturn(YESTERDAY);

        ironMountainExtractTask = new IronMountainExtractTask(
                dataExtractDateValidatorMock,
                ironMountainDataExtractServiceDataMock,
                scheduleDatesMock);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeableMocks.close();
    }

    @Test
    void shouldPerformIronMountainExtractYesterdayDate() {
        ironMountainExtractTask.run();

        verify(scheduleDatesMock).hasValue();
        verify(scheduleDatesMock).getYesterday();

        verify(dataExtractDateValidatorMock).dateValidator(YESTERDAY);
        verify(ironMountainDataExtractServiceDataMock).performIronMountainExtractForDate(YESTERDAY);
    }

    @Test
    void shouldPerformIronMountainExtractForAdhocDate() {
        when(scheduleDatesMock.hasValue())
                .thenReturn(true);
        when(scheduleDatesMock.getFromDate())
                .thenReturn(AD_HOC_JOB_START_DATE);

        ironMountainExtractTask.run();

        verify(scheduleDatesMock).hasValue();
        verify(scheduleDatesMock).getFromDate();

        verify(dataExtractDateValidatorMock).dateValidator(AD_HOC_JOB_START_DATE);
        verify(ironMountainDataExtractServiceDataMock).performIronMountainExtractForDate(AD_HOC_JOB_START_DATE);
    }

    @Test
    void shouldNotInteractWithIronMountainExtractIfDateValidationFails() {
        doThrow(mock(RuntimeException.class))
                .when(dataExtractDateValidatorMock)
                .dateValidator(any());

        ironMountainExtractTask.run();

        verify(dataExtractDateValidatorMock).dateValidator(any());
        verifyNoInteractions(ironMountainDataExtractServiceDataMock);
    }
}