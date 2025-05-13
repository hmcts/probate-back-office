package uk.gov.hmcts.probate.service.caveat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.model.ccd.CcdCaseType;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.ReturnedCaveatDetails;
import uk.gov.hmcts.probate.model.ccd.EventId;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.CaveatQueryService;
import uk.gov.hmcts.probate.service.ccd.CcdClientApi;
import uk.gov.hmcts.reform.probate.model.cases.CaseState;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class CaveatExpiryServiceImplTest {

    private static final String EXPIRY_DATE = "2020-12-31";
    private static final Long CASE_ID = 1234567890L;

    @Mock
    private CcdClientApi ccdClientApi;

    @Mock
    private SecurityUtils securityUtils;

    @Mock
    private CaveatQueryService caveatQueryService;

    @InjectMocks
    private CaveatExpiryServiceImpl caveatExpiryService;

    private SecurityDTO securityDTO;

    @BeforeEach
    void setUp() {
        securityDTO = SecurityDTO.builder().build();
        when(securityUtils.getSecurityDTO()).thenReturn(securityDTO);
    }

    @ParameterizedTest
    @MethodSource("validCaveatStates")
    void shouldExpireCaveatsBasedOnState(CaseState caseState, EventId expectedEventId) {
        CaveatData caveatData = CaveatData.builder().build();
        ReturnedCaveatDetails returnedDetails = new ReturnedCaveatDetails(caveatData,
                LocalDateTime.now(), caseState, CASE_ID);

        when(caveatQueryService.findCaveatExpiredCases(EXPIRY_DATE))
                .thenReturn(List.of(returnedDetails));

        caveatExpiryService.expireCaveats(EXPIRY_DATE);
        uk.gov.hmcts.reform.probate.model.cases.caveat.CaveatData updatedCaveat =
                uk.gov.hmcts.reform.probate.model.cases.caveat.CaveatData.builder()
                    .autoClosedExpiry(Boolean.TRUE)
                    .build();

        verify(ccdClientApi, times(1)).updateCaseAsCaseworker(
                CcdCaseType.CAVEAT,
                CASE_ID.toString(),
                returnedDetails.getLastModified(),
                updatedCaveat,
                expectedEventId,
                securityDTO,
                "Caveat Auto Expired",
                "Caveat Auto Expired"
        );

        assertEquals(Boolean.TRUE, updatedCaveat.getAutoClosedExpiry());
    }

    private static Stream<Arguments> validCaveatStates() {
        return Stream.of(
                Arguments.of(CaseState.CAVEAT_NOT_MATCHED, EventId.CAVEAT_EXPIRED_FOR_CAVEAT_NOT_MATCHED),
                Arguments.of(CaseState.CAVEAT_AWAITING_RESOLUTION, EventId.CAVEAT_EXPIRED_FOR_AWAITING_RESOLUTION),
                Arguments.of(CaseState.CAVEAT_AWAITING_WARNING_RESPONSE,
                        EventId.CAVEAT_EXPIRED_FOR_AWAITING_WARNING_RESPONSE),
                Arguments.of(CaseState.CAVEAT_WARNING_VALIDATION, EventId.CAVEAT_EXPIRED_FOR_WARNNG_VALIDATION)
        );
    }

    @Test
    void shouldSkipWhenNoExpiredCaveats() {
        when(caveatQueryService.findCaveatExpiredCases(EXPIRY_DATE))
                .thenReturn(Collections.emptyList());

        caveatExpiryService.expireCaveats(EXPIRY_DATE);

        verify(ccdClientApi, never()).updateCaseAsCaseworker(any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void shouldCatchExceptionDuringCaseUpdate() {
        ReturnedCaveatDetails returnedDetails = new ReturnedCaveatDetails(CaveatData.builder().build(),
                LocalDateTime.now(), CaseState.CAVEAT_NOT_MATCHED, 1L);

        when(caveatQueryService.findCaveatExpiredCases(EXPIRY_DATE))
                .thenReturn(List.of(returnedDetails));

        doThrow(new RuntimeException("Update failed")).when(ccdClientApi).updateCaseAsCaseworker(
                any(), any(), any(), any(), any(), any(), any(), any());

        assertDoesNotThrow(() -> caveatExpiryService.expireCaveats(EXPIRY_DATE));
    }

    @Test
    void shouldThrowExceptionWhenInvalidState() {
        ReturnedCaveatDetails invalidStateDetails = new ReturnedCaveatDetails(CaveatData.builder().build(),
                LocalDateTime.now(), CaseState.CAVEAT_RAISED, 1L);

        when(caveatQueryService.findCaveatExpiredCases(EXPIRY_DATE))
                .thenReturn(List.of(invalidStateDetails));

        assertThrows(IllegalStateException.class, () -> caveatExpiryService.expireCaveats(EXPIRY_DATE));
    }
}