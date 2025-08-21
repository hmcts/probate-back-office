package uk.gov.hmcts.probate.schedule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.probate.service.FeatureToggleService;
import uk.gov.hmcts.probate.service.dataextract.DataExtractDateValidator;
import uk.gov.hmcts.probate.service.dataextract.DataExtractService;
import uk.gov.hmcts.reform.probate.model.client.ApiClientException;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.Constants.DATE_FORMAT;
import static uk.gov.hmcts.probate.model.DataExtractType.NATIONAL_FRAUD_INITIATIVE;


@ExtendWith(MockitoExtension.class)
class DataExtractTaskTest {

    @Mock
    private DataExtractDateValidator dateValidator;

    @Mock
    private FeatureToggleService featureToggleService;

    @Mock
    private DataExtractService dataExtractService;

    @Mock
    private Clock fixedClock;

    private DataExtractTask underTest;

    private static final String AD_HOC_DATE = "2025-01-31";

    @BeforeEach
    void setup() {
        fixedClock = Clock.fixed(Instant.parse("2025-08-21T10:00:00Z"), ZoneId.of("Europe/London"));

        underTest = new DataExtractTask(
                fixedClock,
                dateValidator,
                featureToggleService,
                dataExtractService,
                "");
    }

    @Test
    void shouldSkipWhenFeatureToggleOff() {
        when(featureToggleService.isNfiDataExtractFeatureToggleOn()).thenReturn(false);

        underTest.run();

        verify(featureToggleService).isNfiDataExtractFeatureToggleOn();
        verifyNoInteractions(dateValidator);
        verifyNoInteractions(dataExtractService);
    }

    @Test
    void shouldUseYesterdayWhenNoAdhocDateAndFeatureOn() {
        when(featureToggleService.isNfiDataExtractFeatureToggleOn()).thenReturn(true);

        String expectedDate = DATE_FORMAT.format(java.time.LocalDate.of(2025, 8, 20));

        underTest.run();

        InOrder inOrder = inOrder(featureToggleService, dateValidator, dataExtractService);
        inOrder.verify(featureToggleService).isNfiDataExtractFeatureToggleOn();
        inOrder.verify(dateValidator).dateValidator(expectedDate, expectedDate);
        inOrder.verify(dataExtractService)
                .performExtractForDateRange(expectedDate, expectedDate, NATIONAL_FRAUD_INITIATIVE);
        verifyNoMoreInteractions(dataExtractService);
    }

    @Test
    void shouldUseAdhocDateWhenProvided() {
        when(featureToggleService.isNfiDataExtractFeatureToggleOn()).thenReturn(true);
        ReflectionTestUtils.setField(underTest, "adHocJobDate", "2025-01-31");

        underTest.run();

        verify(dateValidator).dateValidator(AD_HOC_DATE, AD_HOC_DATE);
        verify(dataExtractService)
                .performExtractForDateRange(AD_HOC_DATE, AD_HOC_DATE, NATIONAL_FRAUD_INITIATIVE);
    }

    @Test
    void shouldCatchApiClientExceptionAndNotPropagate() {
        when(featureToggleService.isNfiDataExtractFeatureToggleOn()).thenReturn(true);
        String expectedDate = DATE_FORMAT.format(java.time.LocalDate.of(2025, 8, 20));
        doThrow(new ApiClientException(HttpStatus.BAD_REQUEST.value(), null))
                .when(dataExtractService)
                .performExtractForDateRange(expectedDate, expectedDate, NATIONAL_FRAUD_INITIATIVE);

        underTest.run();

        verify(dateValidator).dateValidator(expectedDate, expectedDate);
        verify(dataExtractService)
                .performExtractForDateRange(expectedDate, expectedDate, NATIONAL_FRAUD_INITIATIVE);
    }

    @Test
    void shouldCatchGenericExceptionAndNotPropagate() {
        when(featureToggleService.isNfiDataExtractFeatureToggleOn()).thenReturn(true);
        String expectedDate = DATE_FORMAT.format(java.time.LocalDate.of(2025, 8, 20));
        doThrow(new RuntimeException("boom"))
                .when(dataExtractService)
                .performExtractForDateRange(expectedDate, expectedDate, NATIONAL_FRAUD_INITIATIVE);

        underTest.run();

        verify(dateValidator).dateValidator(expectedDate, expectedDate);
        verify(dataExtractService)
                .performExtractForDateRange(expectedDate, expectedDate, NATIONAL_FRAUD_INITIATIVE);
    }
}