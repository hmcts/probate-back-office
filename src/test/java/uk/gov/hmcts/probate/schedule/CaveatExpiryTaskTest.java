package uk.gov.hmcts.probate.schedule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.probate.service.CaveatExpiryService;
import uk.gov.hmcts.probate.service.dataextract.DataExtractDateValidator;
import uk.gov.hmcts.reform.probate.model.client.ApiClientException;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.Constants.DATE_FORMAT;


@ExtendWith(MockitoExtension.class)
class CaveatExpiryTaskTest {

    @Mock
    private DataExtractDateValidator dataExtractDateValidator;

    @Mock
    private CaveatExpiryService caveatExpiryService;

    @Mock
    private Clock clock;

    @InjectMocks
    private CaveatExpiryTask caveatExpiryTaskMock;

    private String yesterday;

    @BeforeEach
    void init() {
        LocalDate fixedDate = LocalDate.of(2025, 5, 19);
        Instant fixedInstant = fixedDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        when(clock.instant()).thenReturn(fixedInstant);
        when(clock.getZone()).thenReturn(ZoneId.systemDefault());
        yesterday = LocalDate.now(clock).minusDays(1).format(DATE_FORMAT);

        caveatExpiryTaskMock.adHocJobDate = null;
        ReflectionTestUtils.setField(caveatExpiryTaskMock, "clock", clock);
    }

    @Test
    void shouldExpireCaveatCasesForYesterday() {
        caveatExpiryTaskMock.run();

        verify(dataExtractDateValidator).dateValidator(yesterday);
        verify(caveatExpiryService).expireCaveats(yesterday);
    }

    @Test
    void shouldExpireCaveatCasesForAdhocDate() {
        caveatExpiryTaskMock.adHocJobDate = "2022-09-05";

        caveatExpiryTaskMock.run();

        verify(dataExtractDateValidator).dateValidator("2022-09-05");
        verify(caveatExpiryService).expireCaveats("2022-09-05");
    }

    @Test
    void shouldThrowClientExceptionWithBadRequestForExpireCaveatCasesWithIncorrectDateFormat() {
        doThrow(new ApiClientException(HttpStatus.BAD_REQUEST.value(), null)).when(dataExtractDateValidator)
                .dateValidator(yesterday);

        caveatExpiryTaskMock.run();

        verify(dataExtractDateValidator).dateValidator(yesterday);
        verifyNoInteractions(caveatExpiryService);
    }

    @Test
    void shouldThrowExceptionForExpireCaveatCases() {
        doThrow(new NullPointerException()).when(dataExtractDateValidator)
                .dateValidator(yesterday);

        caveatExpiryTaskMock.run();

        verify(dataExtractDateValidator).dateValidator(yesterday);
        verifyNoInteractions(caveatExpiryService);
    }
}
