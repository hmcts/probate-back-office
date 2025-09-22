package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.payments.PaymentsResponse;
import uk.gov.hmcts.probate.repositories.ElasticSearchRepository;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.payments.ServiceRequestClient;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.ccd.client.model.SearchResult;
import uk.gov.service.notify.NotificationClientException;

import java.util.ArrayList;
import java.util.List;

import static uk.gov.hmcts.probate.model.ccd.CcdCaseType.CAVEAT;
import static uk.gov.hmcts.probate.model.ccd.CcdCaseType.GRANT_OF_REPRESENTATION;

@Service
@RequiredArgsConstructor
@Slf4j
public class FetchDraftCaseService {

    public static final String SERVICE_NAME = "Probate";
    private final CaseQueryService caseQueryService;
    private final SecurityUtils securityUtils;
    private final ServiceRequestClient serviceRequestClient;
    private final NotificationService notificationService;
    private final CaveatQueryService caveatQueryService;
    private final ElasticSearchRepository elasticSearchRepository;
    private static final String DRAFT_CASES_QUERY = "templates/elasticsearch/caseMatching/"
            + "draft_cases_date_range_query.json";

    public void fetchDraftCases(String startDate, String endDate,boolean isCaveat) {
        try {
            log.info("Fetch GOR cases upto date: from {} to {} isCaveat: {} ", startDate, endDate, isCaveat);
            SecurityDTO securityDTO = securityUtils.getUserBySchedulerTokenAndServiceSecurityDTO();
            String caseTypeName = GRANT_OF_REPRESENTATION.getName();
            if (isCaveat) {
                caseTypeName = CAVEAT.getName();
            }
            System.out.println("DRAFT_CASES_QUERY = " + DRAFT_CASES_QUERY);
            SearchResult searchResult = elasticSearchRepository.fetchFirstPage(securityDTO.getAuthorisation(),
                    caseTypeName, DRAFT_CASES_QUERY, startDate, endDate);
            log.info("Found {} {} cases with draft state from {} to date: {}", searchResult.getTotal(),
                    caseTypeName, startDate, endDate);
            if (searchResult.getTotal() == 0) {
                log.info("No {} cases found cases with draft state: from {} to date: {}", caseTypeName,
                        startDate, endDate);
                return;
            }
            List<CaseDetails> successfulPaymentCases = new ArrayList<>();
            List<CaseDetails> searchResultCases = searchResult.getCases();

            searchResultCases.forEach(caseDetails -> {
                log.info("draft state case id: {}", caseDetails.getId());
                boolean isPaymentSuccessful = processPayment(caseDetails.getId().toString());
                if (isPaymentSuccessful) {
                    log.info("Payment status is Success for case id: {}", caseDetails.getId());
                    successfulPaymentCases.add(caseDetails);
                } else {
                    log.info("Payment status is not Success for case id: {}", caseDetails.getId());
                }
            });

            String searchAfterValue = getLastId(searchResult);
            log.info("Fetching draft state next page for searchAfterValue: {}", searchAfterValue);
            boolean keepSearching;
            do {
                SearchResult nextSearchResult = elasticSearchRepository
                        .fetchNextPage(securityDTO.getAuthorisation(), caseTypeName, searchAfterValue,
                                DRAFT_CASES_QUERY, startDate, endDate);

                log.info("Fetching draft state next page for searchAfterValue: {}", searchAfterValue);

                keepSearching = nextSearchResult != null && !nextSearchResult.getCases().isEmpty();
                if (keepSearching) {
                    List<CaseDetails> subsequentSearchResultCases = nextSearchResult.getCases();
                    subsequentSearchResultCases.forEach(caseDetails -> {
                        log.info("draft state case id: {}", caseDetails.getId());
                        boolean isPaymentSuccessful = processPayment(caseDetails.getId().toString());
                        if (isPaymentSuccessful) {
                            log.info("Payment status is Success for case id: {}", caseDetails.getId());
                            successfulPaymentCases.add(caseDetails);
                        } else {
                            log.info("Payment status is not Success for case id: {}", caseDetails.getId());
                        }
                    });
                    searchAfterValue = getLastId(nextSearchResult);
                }
            } while (keepSearching);

            if (!successfulPaymentCases.isEmpty()) {
                sendDraftSuccessfulPaymentNotification(successfulPaymentCases, startDate, endDate, isCaveat);
            }

        } catch (Exception e) {
            log.error("FetchGORCases method error {}", e.getMessage(), e);
        }
    }

    private String getLastId(SearchResult searchResult) {
        return searchResult.getCases().getLast().getId().toString();
    }

    private void sendDraftSuccessfulPaymentNotification(List<CaseDetails> successfulPaymentCases,
                                                      String startDate, String endDate, boolean isCaveat) {
        try {
            notificationService.sendEmailForDraftSuccessfulPayment(successfulPaymentCases,
                    startDate, endDate, isCaveat);
        } catch (NotificationClientException e) {
            log.error("NotificationClientException for GOR report: {}", e.getMessage());
        }
    }

    private boolean processPayment(String caseId) {
        SecurityDTO securityDTO = securityUtils.getUserByCaseworkerTokenAndServiceSecurityDTO();
        PaymentsResponse response = serviceRequestClient.retrievePayments(securityDTO.getAuthorisation(),
                securityDTO.getServiceAuthorisation(), SERVICE_NAME, caseId);

        boolean isPaymentSuccessful = response.getPayments().stream()
                .anyMatch(payment -> "success".equalsIgnoreCase(payment.getStatus()));
        log.info("PaymentSuccessful: {}", isPaymentSuccessful);
        return isPaymentSuccessful;
    }
}