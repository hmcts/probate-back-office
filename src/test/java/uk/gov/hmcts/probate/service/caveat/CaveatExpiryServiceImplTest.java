package uk.gov.hmcts.probate.service.caveat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.ReturnedCaveatDetails;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.CaveatQueryService;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.ccd.client.model.StartEventResponse;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.ccd.EventId.CAVEAT_EXPIRED_FOR_AWAITING_RESOLUTION;
import static uk.gov.hmcts.probate.model.ccd.EventId.CAVEAT_EXPIRED_FOR_CAVEAT_NOT_MATCHED;
import static uk.gov.hmcts.probate.model.ccd.EventId.CAVEAT_EXPIRED_FOR_WARNNG_VALIDATION;
import static uk.gov.hmcts.probate.model.ccd.EventId.CAVEAT_EXPIRED_FOR_AWAITING_WARNING_RESPONSE;
import static uk.gov.hmcts.reform.probate.model.cases.CaseState.CAVEAT_AWAITING_RESOLUTION;
import static uk.gov.hmcts.reform.probate.model.cases.CaseState.CAVEAT_AWAITING_WARNING_RESPONSE;
import static uk.gov.hmcts.reform.probate.model.cases.CaseState.CAVEAT_NOT_MATCHED;
import static uk.gov.hmcts.reform.probate.model.cases.CaseState.CAVEAT_WARNING_VALIDATION;

@ExtendWith(SpringExtension.class)
class CaveatExpiryServiceImplTest {

    private static final String EXPIRY_DATE = "2020-12-31";
    private static final String TOKEN = "someToken";
    private static final LocalDateTime LAST_MODIFIED = LocalDateTime.now();

    @Mock
    private CaveatQueryService caveatQueryService;

    @InjectMocks
    private CaveatExpiryServiceImpl caveatExpiryService;

    @Mock
    private CoreCaseDataApi coreCaseDataApi;

    @Mock
    private SecurityUtils securityUtils;

    @Mock
    private SecurityDTO securityDTO;

    @Mock
    private Clock clock;

    @BeforeEach
    void setup() {
        when(clock.instant()).thenReturn(Clock.systemDefaultZone().instant());
        when(clock.getZone()).thenReturn(ZoneId.systemDefault());

        when(securityUtils.getSecurityDTO()).thenReturn(securityDTO);
        when(securityDTO.getAuthorisation()).thenReturn("auth");
        when(securityDTO.getServiceAuthorisation()).thenReturn("serviceAuth");
        when(securityDTO.getUserId()).thenReturn("userId");
        ReflectionTestUtils.setField(caveatExpiryService, "dataExtractPaginationSize", 100);
    }

    @Test
    void shouldExpireCaveats() {
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
        when(coreCaseDataApi.startEventForCaseWorker(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(StartEventResponse.builder()
                .eventId(CAVEAT_EXPIRED_FOR_CAVEAT_NOT_MATCHED.getName())
                .token(TOKEN)
                .caseDetails(CaseDetails.builder()
                        .id(1L)
                        .state(CAVEAT_NOT_MATCHED.getName())
                        .data(new HashMap<>(Map.of("expiryDate", "2021-11-11")))
                        .lastModified(LocalDateTime.now().minusDays(2))
                        .build())
                .build());

        caveatExpiryService.expireCaveats(EXPIRY_DATE);

        verify(securityUtils).setSecurityContextUserAsScheduler();
        verify(coreCaseDataApi).submitEventForCaseWorker(
                any(), any(), any(), any(), any(), any(), eq(false), any()
        );
    }

    @Test
    void shouldSkipCaveatWhenStateMissing() {

        CaveatData reliantData = CaveatData.builder().deceasedSurname("Reliant").build();
        List<ReturnedCaveatDetails> firstPage = List.of(
                new ReturnedCaveatDetails(reliantData, LAST_MODIFIED, CAVEAT_NOT_MATCHED, 1L));
        when(caveatQueryService.fetchExpiredCaveatsPage(any(), any()))
                .thenReturn(firstPage)
                .thenReturn(List.of());
        when(coreCaseDataApi.startEventForCaseWorker(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(StartEventResponse.builder()
                .eventId(CAVEAT_EXPIRED_FOR_CAVEAT_NOT_MATCHED.getName())
                .token(TOKEN)
                .caseDetails(CaseDetails.builder()
                        .id(1L)
                        .state(null)
                        .lastModified(LocalDateTime.now())
                        .build())
                .build());

        caveatExpiryService.expireCaveats(EXPIRY_DATE);

        verify(securityUtils).setSecurityContextUserAsScheduler();
        verify(coreCaseDataApi, never())
                .submitEventForCaseWorker(any(), any(), any(), any(), any(), any(), anyBoolean(), any());
    }

    @Test
    void shouldSkipWhenNoExpiredCaveats() {
        when(caveatQueryService.fetchExpiredCaveatsPage(any(), any())).thenReturn(List.of());
        caveatExpiryService.expireCaveats(EXPIRY_DATE);
        verify(securityUtils).setSecurityContextUserAsScheduler();
        verify(coreCaseDataApi, never())
                .submitEventForCaseWorker(any(), any(), any(), any(), any(), any(), anyBoolean(), any());
    }

    @Test
    void shouldUseAwaitingResolutionEventIdWhenStateIsAwaitingResolution() {

        CaveatData reliantData = CaveatData.builder().deceasedSurname("Reliant").build();
        List<ReturnedCaveatDetails> firstPage = List.of(
                new ReturnedCaveatDetails(reliantData, LAST_MODIFIED, CAVEAT_AWAITING_RESOLUTION, 1L));
        when(caveatQueryService.fetchExpiredCaveatsPage(any(), any()))
                .thenReturn(firstPage)
                .thenReturn(List.of());

        when(coreCaseDataApi.startEventForCaseWorker(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(StartEventResponse.builder()
                .eventId(CAVEAT_EXPIRED_FOR_AWAITING_RESOLUTION.getName())
                .token(TOKEN)
                .caseDetails(CaseDetails.builder()
                        .id(1L)
                        .state(CAVEAT_AWAITING_RESOLUTION.getName())
                        .data(new HashMap<>(Map.of("expiryDate", "2020-11-11")))
                        .lastModified(LocalDateTime.now().minusDays(2))
                        .build())
                .build());

        caveatExpiryService.expireCaveats(EXPIRY_DATE);

        verify(securityUtils).setSecurityContextUserAsScheduler();
        verify(coreCaseDataApi).submitEventForCaseWorker(
                any(), any(), any(), any(), any(), any(), eq(false), any()
        );
    }

    @Test
    void shouldUseAwaitingWarningResponse() {
        CaveatData reliantData = CaveatData.builder().deceasedSurname("Reliant").build();
        List<ReturnedCaveatDetails> firstPage = List.of(
                new ReturnedCaveatDetails(reliantData, LAST_MODIFIED, CAVEAT_AWAITING_WARNING_RESPONSE, 1L));
        when(caveatQueryService.fetchExpiredCaveatsPage(any(), any()))
                .thenReturn(firstPage)
                .thenReturn(List.of());
        when(coreCaseDataApi.startEventForCaseWorker(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(StartEventResponse.builder()
                .eventId(CAVEAT_EXPIRED_FOR_AWAITING_WARNING_RESPONSE.getName())
                .token(TOKEN)
                .caseDetails(CaseDetails.builder()
                        .id(1L)
                        .state(CAVEAT_AWAITING_WARNING_RESPONSE.getName())
                        .data(new HashMap<>(Map.of(
                                "expiryDate", "2019-01-11"
                        )))
                        .lastModified(LocalDateTime.now().minusDays(2))
                        .build())
                .build());

        caveatExpiryService.expireCaveats(EXPIRY_DATE);

        verify(securityUtils).setSecurityContextUserAsScheduler();
        verify(coreCaseDataApi).submitEventForCaseWorker(
                any(), any(), any(), any(), any(), any(), eq(false), any()
        );
    }

    @Test
    void shouldUseWarningValidation() {
        CaveatData reliantData = CaveatData.builder().deceasedSurname("Reliant").build();
        List<ReturnedCaveatDetails> firstPage = List.of(
                new ReturnedCaveatDetails(reliantData, LAST_MODIFIED, CAVEAT_WARNING_VALIDATION, 1L));
        when(caveatQueryService.fetchExpiredCaveatsPage(any(), any()))
                .thenReturn(firstPage)
                .thenReturn(List.of());
        when(coreCaseDataApi.startEventForCaseWorker(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(StartEventResponse.builder()
                .eventId(CAVEAT_EXPIRED_FOR_WARNNG_VALIDATION.getName())
                .token(TOKEN)
                .caseDetails(CaseDetails.builder()
                        .id(1L)
                        .state(CAVEAT_WARNING_VALIDATION.getName())
                        .data(new HashMap<>(Map.of(
                                "expiryDate", "2019-01-11"
                        )))
                        .lastModified(LocalDateTime.now().minusDays(2))
                        .build())
                .build());

        caveatExpiryService.expireCaveats(EXPIRY_DATE);

        verify(securityUtils).setSecurityContextUserAsScheduler();
        verify(coreCaseDataApi).submitEventForCaseWorker(
                any(), any(), any(), any(), any(), any(), eq(false), any()
        );
    }
}