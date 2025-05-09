package uk.gov.hmcts.probate.service.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.repositories.ElasticSearchRepository;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.ccd.client.model.SearchResult;
import uk.gov.service.notify.NotificationClientException;

import java.util.ArrayList;
import java.util.List;

import static uk.gov.hmcts.probate.model.ccd.CcdCaseType.GRANT_OF_REPRESENTATION;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableAsync
public class AutomatedNotificationService {
    private final NotificationService notificationService;
    private final SecurityUtils securityUtils;
    private final ElasticSearchRepository elasticSearchRepository;
    private final AutomatedNotificationCCDService automatedNotificationCCDService;

    private static final String FIRST_STOP_REMINDER_QUERY = "templates/elasticsearch/caseMatching/"
            + "first_stop_reminder_query.json";
    private static final String SECOND_STOP_REMINDER_QUERY = "templates/elasticsearch/caseMatching/"
            + "second_stop_reminder_query.json";

    public void sendStopReminder(String date, boolean isFirstStopReminder) {
        List<Long> failedCases = new ArrayList<>();
        securityUtils.setSecurityContextUserAsScheduler();
        try {
            log.info("sendStopReminder for date {} isFirstStop {} ", date, isFirstStopReminder);
            SecurityDTO securityDTO = securityUtils.getUserBySchedulerTokenAndServiceSecurityDTO();
            SearchResult searchResult = elasticSearchRepository.fetchFirstPage(
                    securityDTO.getAuthorisation(),
                    GRANT_OF_REPRESENTATION.getName(),
                    isFirstStopReminder ? FIRST_STOP_REMINDER_QUERY : SECOND_STOP_REMINDER_QUERY,
                    date, date);
            log.info("sendStopReminder query executed for date: {}, cases found: {}",
                    date, searchResult.getTotal());
            if (searchResult.getTotal() == 0) {
                log.info("No cases found for sendStopReminder");
                return;
            }
            List<CaseDetails> searchResultCases = searchResult.getCases();
            searchResultCases.forEach(caseDetails -> {
                try {
                    Document sentEmail = notificationService.sendStopReminderEmail(caseDetails, isFirstStopReminder);
                    automatedNotificationCCDService.saveNotification(caseDetails,
                            caseDetails.getId().toString(), securityDTO, sentEmail, isFirstStopReminder);
                } catch (NotificationClientException | RuntimeException e) {
                    log.info("Error sending email for case id: {}", caseDetails.getId());
                    failedCases.add(caseDetails.getId());
                }
            });
            String searchAfterValue = searchResultCases.get(searchResultCases.size() - 1).getId().toString();
            log.info("Continuing sendStopReminder for searchAfterValue: {}", searchAfterValue);

            boolean keepSearching;
            do {
                SearchResult subsequentSearchResult = elasticSearchRepository
                        .fetchNextPage(securityDTO.getAuthorisation(),
                                GRANT_OF_REPRESENTATION.getName(),
                                searchAfterValue,
                                isFirstStopReminder ? FIRST_STOP_REMINDER_QUERY : SECOND_STOP_REMINDER_QUERY,
                                date, date);

                log.info("Fetching next page for searchAfterValue: {}", searchAfterValue);

                keepSearching = subsequentSearchResult != null && !subsequentSearchResult.getCases().isEmpty();
                if (keepSearching) {
                    List<CaseDetails> subsequentSearchResultCases = subsequentSearchResult.getCases();
                    subsequentSearchResultCases.forEach(caseDetails -> {
                        log.info("Sending email for case id: {}", caseDetails.getId());
                        try {
                            Document sentEmail = notificationService.sendStopReminderEmail(caseDetails,
                                    isFirstStopReminder);
                            automatedNotificationCCDService.saveNotification(caseDetails,
                                    caseDetails.getId().toString(), securityDTO, sentEmail, isFirstStopReminder);
                        } catch (NotificationClientException | RuntimeException e) {
                            log.info("Error sending email for case id: {}", caseDetails.getId());
                            failedCases.add(caseDetails.getId());
                        }
                    });
                    searchAfterValue = subsequentSearchResultCases
                            .getLast().getId().toString();
                }
            } while (keepSearching);
            log.info("Perform sendStopReminder finished");
        } catch (Exception e) {
            log.error("Error on SendNotificationsTask Scheduler sendFirstStopReminder task {}", e.getMessage());
        } finally {
            log.info("Fail to sendStopReminder with cases: {}", failedCases);
        }
    }
}
