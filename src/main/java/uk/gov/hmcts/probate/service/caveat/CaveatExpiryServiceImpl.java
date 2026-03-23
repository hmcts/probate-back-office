package uk.gov.hmcts.probate.service.caveat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.CcdCaseType;
import uk.gov.hmcts.probate.model.ccd.EventId;
import uk.gov.hmcts.probate.model.ccd.caveat.request.ReturnedCaveatDetails;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.CaveatExpiryService;
import uk.gov.hmcts.probate.service.CaveatQueryService;
import uk.gov.hmcts.probate.service.ccd.CcdClientApi;
import uk.gov.hmcts.reform.probate.model.cases.CaseState;

import java.util.ArrayList;
import java.util.List;

import static uk.gov.hmcts.probate.model.ccd.EventId.CAVEAT_EXPIRED_FOR_AWAITING_RESOLUTION;
import static uk.gov.hmcts.probate.model.ccd.EventId.CAVEAT_EXPIRED_FOR_AWAITING_WARNING_RESPONSE;
import static uk.gov.hmcts.probate.model.ccd.EventId.CAVEAT_EXPIRED_FOR_CAVEAT_NOT_MATCHED;
import static uk.gov.hmcts.probate.model.ccd.EventId.CAVEAT_EXPIRED_FOR_WARNNG_VALIDATION;

@Slf4j
@Component
@RequiredArgsConstructor
public class CaveatExpiryServiceImpl implements CaveatExpiryService {

    private static final String EVENT_DESCRIPTOR = "Caveat Auto Expired";

    private final CaveatQueryService caveatQueryService;
    private final SecurityUtils securityUtils;
    private final CcdClientApi ccdClientApi;
    private final int dataExtractPaginationSize = 100;

    @Override
    public void expireCaveats(String expiryDate) {
        securityUtils.setSecurityContextUserAsScheduler();
        log.info("Search for expired Caveats for expiryDate: {}", expiryDate);
        List<String> failedCases = new ArrayList<>();
        Long[] searchAfter = null;
        List<ReturnedCaveatDetails> pageResults;
        do {
            pageResults = caveatQueryService.fetchExpiredCaveatsPage(expiryDate, searchAfter);
            log.info("Processing {} caveats in current page", pageResults.size());
            SecurityDTO securityDTO = securityUtils.getSecurityDTO();
            for (ReturnedCaveatDetails caveat : pageResults) {
                expireCaveat(caveat, securityDTO, failedCases);
            }
            searchAfter = getNextSearchAfter(pageResults);
        } while (hasMorePages(pageResults));

        if (!failedCases.isEmpty()) {
            log.error("Caveat autoExpire failed for cases: {}", failedCases);
        }
    }

    private void expireCaveat(ReturnedCaveatDetails caveat, SecurityDTO securityDTO, List<String> failedCases) {
        EventId eventId = getEventId(caveat.getState());
        uk.gov.hmcts.reform.probate.model.cases.caveat.CaveatData caseData =
                uk.gov.hmcts.reform.probate.model.cases.caveat.CaveatData.builder()
                        .autoClosedExpiry(Boolean.TRUE)
                        .build();
        try {
            ccdClientApi.updateCaseAsCaseworker(
                    CcdCaseType.CAVEAT,
                    String.valueOf(caveat.getId()),
                    caveat.getLastModified(),
                    caseData,
                    eventId,
                    securityDTO,
                    EVENT_DESCRIPTOR,
                    EVENT_DESCRIPTOR
            );
            log.info("Caveat autoExpired: {}", caveat.getId());

        } catch (RuntimeException e) {
            log.info("Caveat autoExpire failure for case: {}, due to {}", caveat.getId(), e.getMessage());
            failedCases.add(String.valueOf(caveat.getId()));
        }
    }

    private EventId getEventId(CaseState state) {
        return switch (state) {
            case CAVEAT_NOT_MATCHED -> CAVEAT_EXPIRED_FOR_CAVEAT_NOT_MATCHED;
            case CAVEAT_AWAITING_RESOLUTION -> CAVEAT_EXPIRED_FOR_AWAITING_RESOLUTION;
            case CAVEAT_AWAITING_WARNING_RESPONSE -> CAVEAT_EXPIRED_FOR_AWAITING_WARNING_RESPONSE;
            case CAVEAT_WARNING_VALIDATION -> CAVEAT_EXPIRED_FOR_WARNNG_VALIDATION;
            default -> throw new IllegalStateException("Unexpected state for Caveat Auto Expiry: " + state);
        };
    }

    private Long[] getNextSearchAfter(List<ReturnedCaveatDetails> pageResults) {
        if (pageResults.isEmpty()) {
            return null;
        }
        return new Long[]{pageResults.get(pageResults.size() - 1).getId()};
    }

    private boolean hasMorePages(List<ReturnedCaveatDetails> pageResults) {
        return pageResults.size() == dataExtractPaginationSize;
    }
}