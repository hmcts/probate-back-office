package uk.gov.hmcts.probate.service.caveat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.CcdCaseType;
import uk.gov.hmcts.probate.model.ccd.caveat.request.ReturnedCaveatDetails;
import uk.gov.hmcts.probate.model.ccd.EventId;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.CaveatExpiryService;
import uk.gov.hmcts.probate.service.CaveatQueryService;
import uk.gov.hmcts.probate.service.ccd.CcdClientApi;
import uk.gov.hmcts.reform.probate.model.cases.CaseState;
import uk.gov.hmcts.reform.probate.model.cases.caveat.CaveatData;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static uk.gov.hmcts.probate.model.ccd.EventId.CAVEAT_EXPIRED_FOR_AWAITING_RESOLUTION;
import static uk.gov.hmcts.probate.model.ccd.EventId.CAVEAT_EXPIRED_FOR_CAVEAT_NOT_MATCHED;
import static uk.gov.hmcts.probate.model.ccd.EventId.CAVEAT_EXPIRED_FOR_WARNNG_VALIDATION;
import static uk.gov.hmcts.probate.model.ccd.EventId.CAVEAT_EXPIRED_FOR_AWAITING_WARNING_RESPONSE;

@Slf4j
@Component
@RequiredArgsConstructor
public class CaveatExpiryServiceImpl implements CaveatExpiryService {

    private static final String EVENT_DESCRIPTOR_CAVEAT_EXPIRED = "Caveat Auto Expired";
    private final CcdClientApi ccdClientApi;
    private final SecurityUtils securityUtils;
    private final CaveatQueryService caveatQueryService;

    @Override
    public void expireCaveats(String expiryDate) {
        securityUtils.setSecurityContextUserAsScheduler();
        SecurityDTO securityDto = securityUtils.getSecurityDTO();
        log.info("Search for expired Caveats for expiryDate: {}", expiryDate);
        List<ReturnedCaveatDetails> caseDetails = caveatQueryService.findCaveatExpiredCases(expiryDate);
        log.info("Caveats found for expiry: {}", caseDetails.size());

        List<String> failedCases = new ArrayList<>();
        for (ReturnedCaveatDetails expiredCaveat : caseDetails) {
            EventId eventIdToStart =
                    getEventIdForCaveatToExpireGivenPreconditionState(expiredCaveat.getState());
            CaveatData caveatData = CaveatData.builder()
                            .autoClosedExpiry(Boolean.TRUE)
                            .build();
            updateCaseAsCaseworker(String.valueOf(expiredCaveat.getId()),
                    caveatData,
                    expiredCaveat.getLastModified(),
                    eventIdToStart,
                    securityDto,
                    failedCases);
        }
        if (!failedCases.isEmpty()) {
            log.error("Caveat autoExpire failed for cases: {}", failedCases);
        }
    }

    private void updateCaseAsCaseworker(String caseId, Object caseData, LocalDateTime lastModified,
                                        EventId eventIdToStart, SecurityDTO securityDto, List<String> failedCases) {
        try {
            ccdClientApi
                    .updateCaseAsCaseworker(CcdCaseType.CAVEAT, caseId, lastModified, caseData, eventIdToStart,
                            securityDto, EVENT_DESCRIPTOR_CAVEAT_EXPIRED, EVENT_DESCRIPTOR_CAVEAT_EXPIRED);
            log.info("Caveat autoExpired: {}", caseId);
        } catch (RuntimeException e) {
            log.info("Caveat autoExpire failure for case: {}, due to {}", caseId, e.getMessage());
            failedCases.add(caseId);
        }
    }

    private EventId getEventIdForCaveatToExpireGivenPreconditionState(CaseState caveatState) {
        return switch (caveatState) {
            case CAVEAT_NOT_MATCHED -> CAVEAT_EXPIRED_FOR_CAVEAT_NOT_MATCHED;
            case CAVEAT_AWAITING_RESOLUTION -> CAVEAT_EXPIRED_FOR_AWAITING_RESOLUTION;
            case CAVEAT_AWAITING_WARNING_RESPONSE -> CAVEAT_EXPIRED_FOR_AWAITING_WARNING_RESPONSE;
            case CAVEAT_WARNING_VALIDATION -> CAVEAT_EXPIRED_FOR_WARNNG_VALIDATION;
            default -> throw new IllegalStateException("Unexpected state for Caveat Auto Expiry: " + caveatState);
        };
    }

}