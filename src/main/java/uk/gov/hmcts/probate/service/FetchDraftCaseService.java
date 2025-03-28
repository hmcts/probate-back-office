package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.CaseType;
import uk.gov.hmcts.probate.model.ccd.caveat.request.ReturnedCaveatDetails;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;
import uk.gov.hmcts.probate.model.payments.PaymentsResponse;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.payments.ServiceRequestClient;
import uk.gov.service.notify.NotificationClientException;

import java.util.ArrayList;
import java.util.List;

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

    public void fetchGORCases(String startDate, String endDate) {
        try {
            log.info("Fetch GOR cases upto date: {}", endDate);
            List<ReturnedCaseDetails> cases = caseQueryService.findDraftCases(startDate, endDate);
            log.info("Found {} cases with draft state", cases.size());

            List<ReturnedCaseDetails> successfulPaymentCases = new ArrayList<>();

            for (ReturnedCaseDetails returnedCaseDetails : cases) {
                boolean isPaymentSuccessful = processPayment(returnedCaseDetails.getId().toString());

                if (isPaymentSuccessful) {
                    log.info("Payment status is Success for case id: {}", returnedCaseDetails.getId());
                    successfulPaymentCases.add(returnedCaseDetails);
                } else {
                    log.info("Payment status is not Success for case id: {}", returnedCaseDetails.getId());
                }
            }

            if (!successfulPaymentCases.isEmpty()) {
                sendGORSuccessfulPaymentNotification(successfulPaymentCases, startDate, endDate);
            }
        } catch (Exception e) {
            log.error("FetchGORCases method error {}", e.getMessage());
        }
    }

    public void fetchCaveatCases(String startDate, String endDate) {
        try {
            log.info("Fetch Caveat cases upto date: {}", endDate);
            List<ReturnedCaveatDetails> caveatCases = caveatQueryService.findCaveatDraftCases(startDate, endDate,
                    CaseType.CAVEAT);
            log.info("Found {} Caveat cases with draft state", caveatCases.size());

            List<ReturnedCaveatDetails> successfulPaymentCases = new ArrayList<>();

            for (ReturnedCaveatDetails returnedCaseDetails : caveatCases) {

                boolean isPaymentSuccessful = processPayment(returnedCaseDetails.getId().toString());

                if (isPaymentSuccessful) {
                    log.info("Payment status is Success for case id: {}", returnedCaseDetails.getId());
                    successfulPaymentCases.add(returnedCaseDetails);
                } else {
                    log.info("Payment status is not Success for case id: {}", returnedCaseDetails.getId());
                }
            }

            if (!successfulPaymentCases.isEmpty()) {
                sendCaveatSuccessfulPaymentNotification(successfulPaymentCases, startDate, endDate);
            }
        } catch (Exception e) {
            log.error("FetchDraftCase method error {}", e.getMessage());
        }
    }

    private void sendCaveatSuccessfulPaymentNotification(List<ReturnedCaveatDetails> successfulPaymentCases,
                                                         String startDate, String endDate) {
        try {
            notificationService.sendEmailForCaveatSuccessfulPayment(successfulPaymentCases, startDate, endDate);
        } catch (NotificationClientException e) {
            log.error("NotificationClientException: {}", e.getMessage());
        }
    }

    private void sendGORSuccessfulPaymentNotification(List<ReturnedCaseDetails> successfulPaymentCases,
                                                      String startDate, String endDate) {
        try {
            notificationService.sendEmailForGORSuccessfulPayment(successfulPaymentCases, startDate, endDate);
        } catch (NotificationClientException e) {
            log.error("NotificationClientException: {}", e.getMessage());
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