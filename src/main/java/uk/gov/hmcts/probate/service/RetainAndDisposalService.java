package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.repositories.ElasticSearchRepository;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.ccd.client.model.SearchResult;
import uk.gov.service.notify.NotificationClientException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static uk.gov.hmcts.probate.model.ccd.CcdCaseType.CAVEAT;
import static uk.gov.hmcts.probate.model.ccd.CcdCaseType.GRANT_OF_REPRESENTATION;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableAsync
public class RetainAndDisposalService {
    private static final String DISPOSE_GOP_QUERY = "templates/elasticsearch/caseMatching/"
            + "dispose_gop_date_range_query.json";
    private static final String DISPOSE_CAVEAT_PA_QUERY = "templates/elasticsearch/caseMatching/"
            + "dispose_caveat_pa_date_range_query.json";
    private static final String DISPOSE_CAVEAT_PP_QUERY = "templates/elasticsearch/caseMatching/"
            + "dispose_caveat_pp_date_range_query.json";
    private static final String DISPOSE_GOP_DELETED_QUERY = "templates/elasticsearch/caseMatching/"
            + "dispose_gop_deleted_query.json";

    private final NotificationService notificationService;
    private final DisposalCCDService disposalCCDService;
    private final SecurityUtils securityUtils;
    private final ElasticSearchRepository elasticSearchRepository;

    public void sendEmailForInactiveCase(String switchDate, String runDate, long inactivityNotificationPeriod,
                                         boolean isCaveat) {
        List<Long> failedCases = new ArrayList<>();
        try {
            LocalDate runDateDate = LocalDate.parse(runDate);
            LocalDate fromDate = runDateDate.minusDays(inactivityNotificationPeriod);
            LocalDate toDate = runDateDate.minusDays(inactivityNotificationPeriod);
            if (switchDate.equals(runDate)) {
                log.info("Switch date and run date are same, doubling the period for inactivity notification");
                fromDate = fromDate.minusDays(inactivityNotificationPeriod);
            }

            log.info("Start Disposal reminder query fromDate: {}, toDate: {}", fromDate, toDate);
            SecurityDTO securityDTO = securityUtils.getUserBySchedulerTokenAndServiceSecurityDTO();
            String caseTypeName = GRANT_OF_REPRESENTATION.getName();
            String esQueryString = DISPOSE_GOP_QUERY;
            if (isCaveat) {
                caseTypeName = CAVEAT.getName();
                esQueryString = DISPOSE_CAVEAT_PA_QUERY;
            }

            log.info("Start sending email for inactive {} cases runDate: {}, fromDate: {}, toDate: {}",
                    caseTypeName, runDate, fromDate, toDate);
            SearchResult searchResult = elasticSearchRepository.fetchFirstPage(
                    securityDTO.getAuthorisation(),
                    caseTypeName,
                    esQueryString,
                    fromDate.toString(), toDate.toString());

            log.info("Disposal reminder query executed for date: {}, cases found: {}",
                runDate, searchResult.getTotal());
            if (searchResult.getTotal() == 0) {
                log.info("No cases found for disposal");
                return;
            }

            List<CaseDetails> searchResultCases = searchResult.getCases();
            searchResultCases.forEach(caseDetails -> {
                log.info("Sending email for case id: {}", caseDetails.getId());
                try {
                    notificationService.sendDisposalReminderEmail(caseDetails, isCaveat);
                } catch (NotificationClientException | RuntimeException e) {
                    log.info("Error sending email for case id: {}", caseDetails.getId());
                    failedCases.add(caseDetails.getId());
                }
            });
            String searchAfterValue = searchResultCases.get(searchResultCases.size() - 1).getId().toString();
            log.info("Continuing disposal for searchAfterValue: {}", searchAfterValue);

            boolean keepSearching;
            do {
                SearchResult subsequentSearchResult = elasticSearchRepository
                        .fetchNextPage(securityDTO.getAuthorisation(),
                                caseTypeName,
                                searchAfterValue,
                                esQueryString,
                                fromDate.toString(), toDate.toString());

                log.info("Fetching next page for searchAfterValue: {}", searchAfterValue);

                keepSearching = subsequentSearchResult != null && !subsequentSearchResult.getCases().isEmpty();
                if (keepSearching) {
                    List<CaseDetails> subsequentSearchResultCases = subsequentSearchResult.getCases();
                    subsequentSearchResultCases.forEach(caseDetails -> {
                        log.info("Sending email for case id: {}", caseDetails.getId());
                        try {
                            notificationService.sendDisposalReminderEmail(caseDetails, isCaveat);
                        } catch (NotificationClientException | RuntimeException e) {
                            log.info("Error sending email for case id: {}", caseDetails.getId());
                            failedCases.add(caseDetails.getId());
                        }
                    });
                    searchAfterValue = subsequentSearchResultCases
                            .get(subsequentSearchResultCases.size() - 1).getId().toString();
                }
            } while (keepSearching);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        } finally {
            log.info("sendEmailForInactiveCase completed. Fail to send email with cases: {}", failedCases);
        }
    }

    public void disposeInactiveCase(String switchDate, String runDate, String startDate,
                                    long inactivityNotificationPeriod, long disposalGracePeriod) {
        List<Long> failedCases = new ArrayList<>();
        try {
            LocalDate disposalStartDate = LocalDate.parse(startDate);
            LocalDate disposalEndDate = LocalDate.parse(runDate)
                    .minusDays(inactivityNotificationPeriod + disposalGracePeriod);

            log.info("Start Dispose Gop Deleted case initiated for date: {}, fromDate: {}, toDate: {}",
                    runDate, disposalStartDate, disposalEndDate);
            disposeDeletedCase(disposalStartDate.toString(), disposalEndDate.toString(), failedCases, true);

            log.info("Start Dispose Caveat Deleted case initiated for date: {}, fromDate: {}, toDate: {}",
                    runDate, disposalStartDate, disposalEndDate);
            disposeDeletedCase(disposalStartDate.toString(), disposalEndDate.toString(), failedCases, false);

            log.info("Start disposing inactive PA Caveat cases. runDate: {}, fromDate: {}, toDate: {}",
                    runDate, disposalStartDate, disposalEndDate);
            disposeCaveatCase(disposalStartDate.toString(), disposalEndDate.toString(), failedCases, false);

            if (shouldSkipGOPDraftDisposal(runDate, switchDate, disposalGracePeriod)) {
                log.info("Skipping draft disposal for runDate: {} ", runDate);
                return;
            }
            log.info("Start disposing inactive GOP cases. runDate: {}, fromDate: {}, toDate: {}",
                    runDate, disposalStartDate, disposalEndDate);
            disposeGOPDraftCase(disposalStartDate.toString(), disposalEndDate.toString(), failedCases);
            log.info("Start disposing inactive PP caveat cases. runDate: {}, fromDate: {}, toDate: {}",
                    runDate, disposalStartDate, disposalEndDate);
            disposeCaveatCase(disposalStartDate.toString(), disposalEndDate.toString(), failedCases, true);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        } finally {
            log.info("disposeInactiveCase completed. Fail to dispose cases: {}", failedCases);
        }
    }

    private boolean shouldSkipGOPDraftDisposal(String runDate, String switchDate, long disposalInactivePeriod) {
        return LocalDate.parse(runDate).isBefore(LocalDate.parse(switchDate).plusDays(disposalInactivePeriod));
    }

    private void disposeGOPDraftCase(String disposalStartDate, String disposalEndDate, List<Long> failedCases) {
        try {
            SecurityDTO securityDTO = securityUtils.getUserBySchedulerTokenAndServiceSecurityDTO();
            SearchResult searchResult = elasticSearchRepository.fetchFirstPage(securityDTO.getAuthorisation(),
                    GRANT_OF_REPRESENTATION.getName(), DISPOSE_GOP_QUERY, disposalStartDate, disposalEndDate);
            log.info("disposeGOPDraftCase query executed cases found: {}",
                    searchResult.getTotal());
            processCasesForDisposal(searchResult, failedCases, securityDTO, disposalStartDate, disposalEndDate,
                    DISPOSE_GOP_QUERY, true);
        } catch (Exception e) {
            log.error("Error on disposeGOPDraftCase: {}", e.getMessage(), e);
        }
    }

    private void disposeDeletedCase(String disposalStartDate, String disposalEndDate, List<Long> failedCases,
                                    boolean isGOP) {
        try {
            SecurityDTO securityDTO = securityUtils.getUserBySchedulerTokenAndServiceSecurityDTO();
            SearchResult searchResult = elasticSearchRepository.fetchFirstPage(securityDTO.getAuthorisation(),
                    isGOP ? GRANT_OF_REPRESENTATION.getName() : CAVEAT.getName(), DISPOSE_GOP_DELETED_QUERY,
                    disposalStartDate, disposalEndDate);
            log.info("disposeDeletedCase query executed cases found: {} is GOP: {}",
                    searchResult.getTotal(), isGOP);
            processCasesForDisposal(searchResult, failedCases, securityDTO, disposalStartDate, disposalEndDate,
                    DISPOSE_GOP_DELETED_QUERY, isGOP);
        } catch (Exception e) {
            log.error("Error on disposeDeletedCase: {}", e.getMessage(), e);
        }
    }

    private void disposeCaveatCase(String disposalStartDate, String disposalEndDate, List<Long> failedCases,
                                   boolean isPP) {
        try {
            SecurityDTO securityDTO = securityUtils.getUserBySchedulerTokenAndServiceSecurityDTO();
            SearchResult searchResult = elasticSearchRepository.fetchFirstPage(securityDTO.getAuthorisation(),
                    CAVEAT.getName(), isPP ? DISPOSE_CAVEAT_PP_QUERY : DISPOSE_CAVEAT_PA_QUERY,
                    disposalStartDate, disposalEndDate);
            log.info("disposeCaveatCase query executed cases found: {}",
                    searchResult.getTotal());
            processCasesForDisposal(searchResult, failedCases, securityDTO, disposalStartDate, disposalEndDate,
                    isPP ? DISPOSE_CAVEAT_PP_QUERY : DISPOSE_CAVEAT_PA_QUERY, false);
        } catch (Exception e) {
            log.error("Error on disposeCaveatCase: {}", e.getMessage(), e);
        }
    }

    private void processCasesForDisposal(SearchResult searchResult, List<Long> failedCases, SecurityDTO securityDTO,
                                      String disposalStartDate, String disposalEndDate, String query, boolean isGOP) {
        if (searchResult == null || searchResult.getTotal() == 0) {
            log.info("No cases found for disposal: query={}", query);
            return;
        }

        List<CaseDetails> searchResultCases = searchResult.getCases();
        executeCaseDisposal(searchResultCases, failedCases, securityDTO, isGOP);

        String searchAfterValue = searchResultCases.get(searchResultCases.size() - 1).getId().toString();
        log.info("Continuing disposal for searchAfterValue: {}", searchAfterValue);

        boolean keepSearching;
        do {
            SearchResult subsequentSearchResult = elasticSearchRepository
                    .fetchNextPage(securityDTO.getAuthorisation(), isGOP
                            ? GRANT_OF_REPRESENTATION.getName()
                            : CAVEAT.getName(), searchAfterValue, query, disposalStartDate, disposalEndDate);
            log.info("Fetching next page for searchAfterValue: {}", searchAfterValue);

            keepSearching = subsequentSearchResult != null && !subsequentSearchResult.getCases().isEmpty();
            if (keepSearching) {
                List<CaseDetails> subsequentSearchResultCases = subsequentSearchResult.getCases();
                executeCaseDisposal(subsequentSearchResultCases, failedCases, securityDTO, isGOP);
                searchAfterValue = subsequentSearchResultCases
                        .get(subsequentSearchResultCases.size() - 1).getId().toString();
            }
        } while (keepSearching);
    }

    private void executeCaseDisposal(List<CaseDetails> caseDetailsList, List<Long> failedCases,
                                     SecurityDTO securityDTO, boolean isGOP) {
        caseDetailsList.forEach(caseDetails -> {
            try {
                if (isGOP) {
                    disposalCCDService.disposeGOPCase(caseDetails, caseDetails.getId().toString(), securityDTO);
                } else {
                    disposalCCDService.disposeCaveatCase(caseDetails, caseDetails.getId().toString(), securityDTO);
                }
            } catch (Exception e) {
                failedCases.add(caseDetails.getId());
                log.error("Error disposing case with ID: {}", caseDetails.getId(), e);
            }
        });
    }
}
