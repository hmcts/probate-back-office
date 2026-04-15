package uk.gov.hmcts.probate.service.caveat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.exception.ConcurrentDataUpdateException;
import uk.gov.hmcts.probate.exception.NotFoundException;
import uk.gov.hmcts.probate.model.ccd.caveat.request.ReturnedCaveatDetails;
import uk.gov.hmcts.probate.model.ccd.EventId;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.CaveatExpiryService;
import uk.gov.hmcts.probate.service.CaveatQueryService;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;
import uk.gov.hmcts.reform.ccd.client.model.CaseDataContent;
import uk.gov.hmcts.reform.ccd.client.model.Event;
import uk.gov.hmcts.reform.ccd.client.model.StartEventResponse;
import uk.gov.hmcts.reform.probate.model.cases.CaseState;

import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static uk.gov.hmcts.probate.model.ccd.EventId.CAVEAT_EXPIRED_FOR_AWAITING_RESOLUTION;
import static uk.gov.hmcts.probate.model.ccd.EventId.CAVEAT_EXPIRED_FOR_CAVEAT_NOT_MATCHED;
import static uk.gov.hmcts.probate.model.ccd.EventId.CAVEAT_EXPIRED_FOR_WARNNG_VALIDATION;
import static uk.gov.hmcts.probate.model.ccd.EventId.CAVEAT_EXPIRED_FOR_AWAITING_WARNING_RESPONSE;
import static uk.gov.hmcts.reform.probate.model.cases.JurisdictionId.PROBATE;

@Slf4j
@Component
@RequiredArgsConstructor
public class CaveatExpiryServiceImpl implements CaveatExpiryService {

    private static final String EVENT_DESCRIPTOR = "Caveat Auto Expired";

    private final CaveatQueryService caveatQueryService;
    private final CoreCaseDataApi coreCaseDataApi;
    private final SecurityUtils securityUtils;
    private final Clock clock;
    @Value("${data-extract.pagination.size}")
    protected int dataExtractPaginationSize;
    private static final Set<String> ALLOWED_EXPIRY_STATES = Set.of(
            CAVEAT_EXPIRED_FOR_AWAITING_RESOLUTION.getName(),
            CAVEAT_EXPIRED_FOR_CAVEAT_NOT_MATCHED.getName(),
            CAVEAT_EXPIRED_FOR_WARNNG_VALIDATION.getName(),
            CAVEAT_EXPIRED_FOR_AWAITING_WARNING_RESPONSE.getName()
    );

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
        EventId eventId = getEventIdForCaveatToExpireGivenPreconditionState(caveat.getState());
        StartEventResponse startEventResponse = coreCaseDataApi.startEventForCaseWorker(
                securityDTO.getAuthorisation(),
                securityDTO.getServiceAuthorisation(),
                securityDTO.getUserId(),
                PROBATE.name(),
                "Caveat",
                caveat.getId().toString(),
                eventId.getName()
        );

        try {
            validateStateForExpiry(startEventResponse, caveat.getId());
            checkExpiryDate(startEventResponse, caveat.getId());

            startEventResponse.getCaseDetails().getData().put("autoClosedExpiry", true);
            CaseDataContent caseDataContent = CaseDataContent.builder()
                    .event(Event.builder()
                            .id(startEventResponse.getEventId())
                            .summary(EVENT_DESCRIPTOR)
                            .description(EVENT_DESCRIPTOR)
                            .build())
                    .eventToken(startEventResponse.getToken())
                    .data(startEventResponse.getCaseDetails().getData())
                    .build();
            coreCaseDataApi.submitEventForCaseWorker(
                securityDTO.getAuthorisation(),
                securityDTO.getServiceAuthorisation(),
                securityDTO.getUserId(),
                PROBATE.name(),
                "Caveat",
                caveat.getId().toString(),
                false,
                caseDataContent
            );
            log.info("Caveat autoExpired: {}", caveat.getId());
        } catch (RuntimeException e) {
            log.info("Caveat autoExpire failure for case: {}, due to {}", caveat.getId(), e.getMessage());
            failedCases.add(String.valueOf(caveat.getId()));
        }
    }

    private void validateStateForExpiry(StartEventResponse startEventResponse, Long caveatId) {
        final String state = startEventResponse.getCaseDetails().getState();
        if (!ALLOWED_EXPIRY_STATES.contains(state)) {
            throw new ConcurrentDataUpdateException(
                    String.format(
                            "caveatId: %s not updated due to different eventId. actualEventId: %s, lastModified: %s",
                            caveatId,
                            startEventResponse.getEventId(),
                            startEventResponse.getCaseDetails().getLastModified()
                    )
            );
        }
    }

    private void checkExpiryDate(StartEventResponse startEventResponse, Long caveatId) {
        final boolean hasExpiry = startEventResponse.getCaseDetails().getData().containsKey("expiryDate");
        if (!hasExpiry) {
            throw new NotFoundException(
                    String.format("Caveat ID: %s has no expiryDate", caveatId)
            );
        }

        final Object expiryDateObj = startEventResponse.getCaseDetails().getData().get("expiryDate");
        if (!(expiryDateObj instanceof String)) {
            throw new IllegalArgumentException(
                    String.format("Expiry date object: %s is not a string", expiryDateObj)
            );
        }

        final String expiryDateStr = (String) expiryDateObj;
        final LocalDate expiryDate;
        try {
            expiryDate = LocalDate.parse(expiryDateStr);
        } catch (DateTimeParseException e) {
            throw new BusinessValidationException(
                    String.format("Unable to parse expiry date: %s (expecting yyyy-MM-dd format)", expiryDateStr),
                    e.getMessage()
            );
        }

        if (expiryDate.isAfter(LocalDate.now(clock))) {
            throw new IllegalArgumentException(
                    String.format("Caveat expiryDate: %s cannot be in the future", expiryDate)
            );
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
