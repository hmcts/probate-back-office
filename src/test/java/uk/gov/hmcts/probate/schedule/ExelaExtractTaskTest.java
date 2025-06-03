package uk.gov.hmcts.probate.schedule;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.ScheduleDates;
import uk.gov.hmcts.probate.service.FeatureToggleService;
import uk.gov.hmcts.probate.service.dataextract.DataExtractDateValidator;
import uk.gov.hmcts.probate.service.dataextract.ExelaDataExtractService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

class ExelaExtractTaskTest {

    @Mock
    private DataExtractDateValidator dataExtractDateValidatorMock;
    @Mock
    private ExelaDataExtractService exelaDataExtractServiceMock;
    @Mock
    private ScheduleDates scheduleDatesMock;
    @Mock
    private FeatureToggleService featureToggleServiceMock;

    private ExelaExtractTask exelaExtractTask;

    private AutoCloseable closeableMocks;

    private static final String AD_HOC_JOB_START_DATE = "2025-04-20";
    private static final String AD_HOC_JOB_END_DATE = "2025-04-21";
    private static final String YESTERDAY = "2025-04-22";

    @BeforeEach
    void setUp() {
        closeableMocks = MockitoAnnotations.openMocks(this);

        when(scheduleDatesMock.hasValue())
                .thenReturn(false);
        when(scheduleDatesMock.getYesterday())
                .thenReturn(YESTERDAY);

        exelaExtractTask = new ExelaExtractTask(
                dataExtractDateValidatorMock,
                exelaDataExtractServiceMock,
                scheduleDatesMock,
                featureToggleServiceMock);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeableMocks.close();
    }

    @Test
    void shouldPerformExelaExtractDateRange() {
        exelaExtractTask.run();

        verify(scheduleDatesMock).hasValue();
        verify(scheduleDatesMock).getYesterday();

        verify(dataExtractDateValidatorMock).dateValidator(YESTERDAY, YESTERDAY);
        verify(exelaDataExtractServiceMock).performExelaExtractForDateRange(YESTERDAY, YESTERDAY);
    }

    @Test
    void shouldPerformHmrcExtractForAdhocDate() {
        when(scheduleDatesMock.hasValue())
                .thenReturn(true);
        when(scheduleDatesMock.getFromDate())
                .thenReturn(AD_HOC_JOB_START_DATE);
        when(scheduleDatesMock.getToDate())
                .thenReturn(AD_HOC_JOB_START_DATE);

        exelaExtractTask.run();

        verify(scheduleDatesMock).hasValue();
        verify(scheduleDatesMock).getFromDate();
        verify(scheduleDatesMock).getToDate();

        verify(dataExtractDateValidatorMock).dateValidator(AD_HOC_JOB_START_DATE, AD_HOC_JOB_START_DATE);
        verify(exelaDataExtractServiceMock)
                .performExelaExtractForDateRange(AD_HOC_JOB_START_DATE, AD_HOC_JOB_START_DATE);
    }

    @Test
    void shouldPerformHmrcExtractForAdhocDateRange() {
        when(scheduleDatesMock.hasValue())
                .thenReturn(true);
        when(scheduleDatesMock.getFromDate())
                .thenReturn(AD_HOC_JOB_START_DATE);
        when(scheduleDatesMock.getToDate())
                .thenReturn(AD_HOC_JOB_END_DATE);

        exelaExtractTask.run();

        verify(dataExtractDateValidatorMock).dateValidator(AD_HOC_JOB_START_DATE, AD_HOC_JOB_END_DATE);
        verify(exelaDataExtractServiceMock).performExelaExtractForDateRange(AD_HOC_JOB_START_DATE, AD_HOC_JOB_END_DATE);
    }

    @Test
    void shouldNotInteractWithExelaExtractIfDateValidationFails() {
        doThrow(mock(RuntimeException.class))
                .when(dataExtractDateValidatorMock)
                .dateValidator(any(), any());

        exelaExtractTask.run();

        verify(dataExtractDateValidatorMock).dateValidator(any(), any());
        verifyNoInteractions(exelaDataExtractServiceMock);
    }
}
