package uk.gov.hmcts.probate.service.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.exception.CcdUpdateNotificationException;
import uk.gov.hmcts.probate.model.NotificationType;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.repositories.ElasticSearchRepository;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.ccd.client.model.SearchResult;
import uk.gov.hmcts.reform.ccd.client.model.StartEventResponse;
import uk.gov.service.notify.NotificationClientException;

import java.util.ArrayList;
import java.util.List;

import static uk.gov.hmcts.probate.model.ccd.CcdCaseType.GRANT_OF_REPRESENTATION;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableAsync
public class AutomatedNotificationService {
    private final List<NotificationStrategy> strategies;
    private final SecurityUtils securityUtils;
    private final ElasticSearchRepository elasticSearchRepository;
    private final AutomatedNotificationCCDService automatedNotificationCCDService;

    public void sendNotification(String date, NotificationType type) {
        NotificationStrategy strategy = strategies.stream()
                .filter(s -> s.matchesType(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No strategy for type " + type));

        List<Long> failedCases = new ArrayList<>();
        securityUtils.setSecurityContextUserAsScheduler();
        SecurityDTO securityDTO = securityUtils.getUserBySchedulerTokenAndServiceSecurityDTO();

        try {
            log.info("Fetch and process automated notification for type: {} date: {}", type, date);
            fetchAndProcessPages(strategy, date, securityDTO, failedCases);
        } catch (Exception e) {
            final String errorMsg = String.format("Error sending notifications for type: %s for date: %s", type, date);
            log.error(errorMsg, e);
        } finally {
            log.info("Failed cases for {} on {} : {}", type, date, failedCases);
        }
    }

    private void fetchAndProcessPages(NotificationStrategy strategy, String date,
                                      SecurityDTO securityDTO, List<Long> failedCases) {
        String query = strategy.getQueryTemplate();

        SearchResult searchResult = elasticSearchRepository.fetchFirstPage(
                securityDTO.getAuthorisation(), GRANT_OF_REPRESENTATION.getName(),
                query, date, date);
        log.info("sendStopReminder query executed for date: {}, cases found: {}",
                date, searchResult.getTotal());
        if (searchResult.getTotal() == 0) {
            log.info("No cases found for query: {} for date: {}", query, date);
            return;
        }

        processCases(searchResult.getCases(), strategy, securityDTO, failedCases);

        String searchAfterValue = getLastId(searchResult);
        boolean keepSearching;
        do {
            SearchResult nextPage = elasticSearchRepository.fetchNextPage(
                    securityDTO.getAuthorisation(), GRANT_OF_REPRESENTATION.getName(),
                    searchAfterValue, query, date, date);

            keepSearching = nextPage != null && !nextPage.getCases().isEmpty();
            if (keepSearching) {
                processCases(nextPage.getCases(), strategy, securityDTO, failedCases);
                searchAfterValue = getLastId(nextPage);
            }
        } while (keepSearching);
    }

    private void processCases(List<CaseDetails> cases, NotificationStrategy strategy,
                              SecurityDTO securityDTO, List<Long> failedCases) {
        for (CaseDetails caseDetails : cases) {
            StartEventResponse startEventResponse = null;
            Document sentEmail = null;
            boolean emailSucceeded = false;
            String caseId = caseDetails.getId().toString();
            try {
                startEventResponse = automatedNotificationCCDService
                        .startEvent(caseId, securityDTO, strategy);
                if (!strategy.accepts().test(startEventResponse.getCaseDetails())) {
                    log.info("Skipping case {} as it doesn’t meet criteria", caseDetails.getId());
                    failedCases.add(caseDetails.getId());
                    continue;
                }
                sentEmail = strategy.sendEmail(startEventResponse.getCaseDetails());
                emailSucceeded = true;
            } catch (CcdUpdateNotificationException e) {
                log.error(getErrorMessage("StartEvent failed for case: %s", caseId), e);
            } catch (NotificationClientException e) {
                log.error(getErrorMessage("Email send failed for case: %s", caseId), e);
            } catch (RuntimeException e) {
                log.error(getErrorMessage("Unexpected error for case: %s", caseId), e);

            }

            try {
                if (emailSucceeded && sentEmail != null) {
                    automatedNotificationCCDService.saveNotification(
                            startEventResponse.getCaseDetails(), caseDetails.getId().toString(),
                            securityDTO, sentEmail, strategy, startEventResponse
                    );
                } else {
                    automatedNotificationCCDService.saveFailedNotification(
                            caseDetails.getId().toString(), securityDTO, strategy, startEventResponse
                    );
                    failedCases.add(Long.valueOf(caseId));
                }
            } catch (CcdUpdateNotificationException e) {
                log.error(getErrorMessage("Failed to persist notification for case: %s", caseId), e);
                failedCases.add(caseDetails.getId());
            }
        }
    }

    private String getLastId(SearchResult searchResult) {
        return searchResult.getCases().getLast().getId().toString();
    }

    private String getErrorMessage(String msg, String caseId) {
        return String.format(msg, caseId);
    }
}