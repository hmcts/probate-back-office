package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.CcdCaseType;
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

    public void fetchDraftCases(String startDate, String endDate, CcdCaseType ccdCaseType) {
        try {
            SecurityDTO securityDTO = securityUtils.getUserBySchedulerTokenAndServiceSecurityDTO();
            String caseTypeName = ccdCaseType.getName().equals(CAVEAT.getName())
                    ? CAVEAT.getName() : GRANT_OF_REPRESENTATION.getName();
            log.info("Fetch {} draft cases from date {} to {}", caseTypeName, startDate, endDate);
            List<CaseDetails> successfulPaymentDraftCases = fetchAndProcessDraftCases(securityDTO,
                    caseTypeName, startDate, endDate);

            if (!successfulPaymentDraftCases.isEmpty()) {
                sendDraftSuccessfulPaymentNotification(successfulPaymentDraftCases, startDate, endDate, ccdCaseType);
            }

        } catch (Exception e) {
            log.error("fetchDraftCases method error {}", e.getMessage(), e);
        }
    }

    private List<CaseDetails> fetchAndProcessDraftCases(SecurityDTO securityDTO,
                                                        String caseTypeName,
                                                        String startDate,
                                                        String endDate) {
        List<CaseDetails> successfulPaymentCases = new ArrayList<>();

        SearchResult searchResult = elasticSearchRepository.fetchFirstPage(securityDTO.getAuthorisation(),
                caseTypeName, DRAFT_CASES_QUERY, startDate, endDate);

        log.info("Found {} {} cases with draft state from {} to {}",
                searchResult.getTotal(), caseTypeName, startDate, endDate);

        if (searchResult.getTotal() == 0) {
            log.info("No {} draft cases found between {} and {}", caseTypeName, startDate, endDate);
            return successfulPaymentCases;
        }

        processCases(searchResult.getCases(), successfulPaymentCases);

        String searchAfterValue = getLastId(searchResult);
        while (true) {
            SearchResult nextResult = elasticSearchRepository.fetchNextPage(securityDTO.getAuthorisation(),
                    caseTypeName, searchAfterValue, DRAFT_CASES_QUERY, startDate, endDate);

            if (nextResult == null || nextResult.getCases().isEmpty()) {
                break;
            }

            processCases(nextResult.getCases(), successfulPaymentCases);
            searchAfterValue = getLastId(nextResult);
        }

        return successfulPaymentCases;
    }

    private void processCases(List<CaseDetails> cases, List<CaseDetails> successfulPaymentCases) {
        for (CaseDetails caseDetails : cases) {
            log.info("Draft state case id: {}", caseDetails.getId());
            boolean isPaymentSuccessful = processPayment(caseDetails.getId().toString());

            if (isPaymentSuccessful) {
                log.info("Payment status is Success for case id: {}", caseDetails.getId());
                successfulPaymentCases.add(caseDetails);
            } else {
                log.info("Payment status is not Success for case id: {}", caseDetails.getId());
            }
        }
    }

    private String getLastId(SearchResult searchResult) {
        return searchResult.getCases().getLast().getId().toString();
    }

    private void sendDraftSuccessfulPaymentNotification(List<CaseDetails> successfulPaymentCases,
                                                      String startDate, String endDate, CcdCaseType ccdCaseType) {
        try {
            notificationService.sendEmailForDraftSuccessfulPayment(successfulPaymentCases,
                    startDate, endDate, ccdCaseType);
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