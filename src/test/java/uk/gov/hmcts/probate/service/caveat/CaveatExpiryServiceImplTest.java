package uk.gov.hmcts.probate.service.caveat;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.ReturnedCaveatDetails;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.CaveatQueryService;
import uk.gov.hmcts.probate.service.ccd.CcdClientApi;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.probate.model.cases.CaseState.CAVEAT_NOT_MATCHED;

@ExtendWith(SpringExtension.class)
class CaveatExpiryServiceImplTest {

    private static final String EXPIRY_DATE = "2020-12-31";
    private static final LocalDateTime LAST_MODIFIED = LocalDateTime.now();

    @Mock
    private CaveatQueryService caveatQueryService;

    @InjectMocks
    private CaveatExpiryServiceImpl caveatExpiryService;

    @Mock
    private CcdClientApi ccdClientApi;

    @Mock
    private SecurityUtils securityUtils;

    @Test
    void shouldExpireCaveats() {
        CaveatData data = CaveatData.builder().deceasedSurname("Test").build();
        List<ReturnedCaveatDetails> page = List.of(new ReturnedCaveatDetails(
                data,
                LocalDateTime.now(),
                CAVEAT_NOT_MATCHED,
                1L));
        when(caveatQueryService.fetchExpiredCaveatsPage(any(), any()))
                .thenReturn(page)
                .thenReturn(List.of());
        caveatExpiryService.expireCaveats(EXPIRY_DATE);
        verify(securityUtils).setSecurityContextUserAsScheduler();
        verify(ccdClientApi).updateCaseAsCaseworker(
                any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void findCaveatWithExpiryDate() {
        CaveatData reliantData = CaveatData.builder().deceasedSurname("Reliant").build();
        List<ReturnedCaveatDetails> firstPage = List.of(
                new ReturnedCaveatDetails(reliantData, LAST_MODIFIED, CAVEAT_NOT_MATCHED, 1L));
        CaveatData robinData = CaveatData.builder().deceasedSurname("Robin").build();
        List<ReturnedCaveatDetails> secondPage = List.of(
                new ReturnedCaveatDetails(robinData, LAST_MODIFIED, CAVEAT_NOT_MATCHED, 2L));
        when(caveatQueryService.fetchExpiredCaveatsPage(any(), any()))
                .thenReturn(firstPage)
                .thenReturn(secondPage)
                .thenReturn(List.of());
        caveatExpiryService.expireCaveats(EXPIRY_DATE);
        verify(ccdClientApi).updateCaseAsCaseworker(
                any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void shouldSkipWhenNoExpiredCaveats() {
        when(caveatQueryService.fetchExpiredCaveatsPage(any(), any()))
                 .thenReturn(List.of());
        caveatExpiryService.expireCaveats(EXPIRY_DATE);
        verify(ccdClientApi, never()).updateCaseAsCaseworker(any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void shouldUseAwaitingResolutionEventIdWhenStateIsAwaitingResolution() {
        CaveatData caveatData = CaveatData.builder().deceasedSurname("Test").build();
        List<ReturnedCaveatDetails> page = ImmutableList.of(
                new ReturnedCaveatDetails(
                        caveatData,
                        LAST_MODIFIED,
                        uk.gov.hmcts.reform.probate.model.cases.CaseState.CAVEAT_AWAITING_RESOLUTION,
                        1L
                )
        );
        when(caveatQueryService.fetchExpiredCaveatsPage(any(), any()))
                .thenReturn(page)
                .thenReturn(List.of());
        caveatExpiryService.expireCaveats(EXPIRY_DATE);
        verify(ccdClientApi).updateCaseAsCaseworker(any(), any(), any(), any(),
                eq(uk.gov.hmcts.probate.model.ccd.EventId.CAVEAT_EXPIRED_FOR_AWAITING_RESOLUTION),
                any(), any(), any()
        );
    }

    @Test
    void shouldUseExpiredForAwaitingResponseEventIdWhenStateIsAwaitingWarningResponse() {
        CaveatData caveatData = CaveatData.builder().deceasedSurname("Test").build();
        List<ReturnedCaveatDetails> returned = ImmutableList.of(
                new ReturnedCaveatDetails(
                        caveatData,
                        LAST_MODIFIED,
                        uk.gov.hmcts.reform.probate.model.cases.CaseState.CAVEAT_AWAITING_WARNING_RESPONSE,
                        1L
                )
        );
        when(caveatQueryService.fetchExpiredCaveatsPage(any(), any()))
                .thenReturn(returned)
                .thenReturn(List.of());
        caveatExpiryService.expireCaveats(EXPIRY_DATE);
        verify(ccdClientApi).updateCaseAsCaseworker(any(), any(), any(), any(),
                eq(uk.gov.hmcts.probate.model.ccd.EventId.CAVEAT_EXPIRED_FOR_AWAITING_WARNING_RESPONSE),
                any(), any(), any()
        );
    }

    @Test
    void shouldUseExpiredForWarningValidationEventIdWhenStateIsWarningResponse() {
        CaveatData caveatData = CaveatData.builder().deceasedSurname("Test").build();
        List<ReturnedCaveatDetails> page = ImmutableList.of(
                new ReturnedCaveatDetails(
                        caveatData,
                        LAST_MODIFIED,
                        uk.gov.hmcts.reform.probate.model.cases.CaseState.CAVEAT_WARNING_VALIDATION,
                        1L
                )
        );
        when(caveatQueryService.fetchExpiredCaveatsPage(any(), any()))
                .thenReturn(page)
                .thenReturn(List.of());
        caveatExpiryService.expireCaveats(EXPIRY_DATE);
        verify(ccdClientApi).updateCaseAsCaseworker(any(), any(), any(), any(),
                eq(uk.gov.hmcts.probate.model.ccd.EventId.CAVEAT_EXPIRED_FOR_WARNNG_VALIDATION),
                any(), any(), any()
        );
    }

    @Test
    void shouldHandleRuntimeExceptionDuringCaseUpdate() {
        CaveatData caveatData = CaveatData.builder().deceasedSurname("Test").build();
        List<ReturnedCaveatDetails> page = ImmutableList.of(
                new ReturnedCaveatDetails(
                        caveatData,
                        LAST_MODIFIED,
                        uk.gov.hmcts.reform.probate.model.cases.CaseState.CAVEAT_WARNING_VALIDATION,
                        1L
                )
        );
        when(caveatQueryService.fetchExpiredCaveatsPage(any(), any()))
                .thenReturn(page)
                .thenReturn(List.of());

        doThrow(new RuntimeException("Caveat autoExpire failure"))
                .when(ccdClientApi).updateCaseAsCaseworker(any(), any(), any(), any(), any(), any(), any(), any());

        caveatExpiryService.expireCaveats(EXPIRY_DATE);
        verify(ccdClientApi).updateCaseAsCaseworker(any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void shouldThrowIllegalStateExceptionForUnknownCaseState() {
        CaveatData caveatData = CaveatData.builder().deceasedSurname("Test").build();
        List<ReturnedCaveatDetails> page = ImmutableList.of(
                new ReturnedCaveatDetails(
                        caveatData,
                        LAST_MODIFIED,
                        uk.gov.hmcts.reform.probate.model.cases.CaseState.DRAFT,
                        1L
                )
        );
        when(caveatQueryService.fetchExpiredCaveatsPage(any(), any()))
                .thenReturn(page)
                .thenReturn(List.of());

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> caveatExpiryService.expireCaveats(EXPIRY_DATE)
        );
        assertEquals("Unexpected state for Caveat Auto Expiry: DRAFT", ex.getMessage());
    }
}
