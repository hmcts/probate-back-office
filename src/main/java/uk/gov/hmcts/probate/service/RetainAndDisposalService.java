package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.service.notify.NotificationClientException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableAsync
public class RetainAndDisposalService {
    private final CaseQueryService caseQueryService;
    private final NotificationService notificationService;
    private final DisposalCCDService disposalCCDService;
    private final SecurityUtils securityUtils;

    public void sendEmailForInactiveCase(String switchDate, String runDate, long inactivityNotificationPeriod) {
        List<Long> failedCases = new ArrayList<>();
        try {
            LocalDate switchLocalDate = LocalDate.parse(switchDate);
            LocalDate fromDate = switchLocalDate.minusDays(inactivityNotificationPeriod);
            if (switchDate.equals(runDate)) {
                fromDate = fromDate.minusDays(inactivityNotificationPeriod);
            }
            LocalDate toDate = switchLocalDate.minusDays(inactivityNotificationPeriod);
            log.info("Start Disposal reminder query fromDate: {}, toDate: {}", fromDate, toDate);
            List<ReturnedCaseDetails> casesFound =
                    caseQueryService.findInactiveCaseForDisposalReminder(fromDate.toString(), toDate.toString());
            log.info("Disposal reminder query executed for date: {}, cases found: {}", runDate, casesFound.size());
            casesFound.forEach(caseDetails -> {
                log.info("Sending email for case id: {}", caseDetails.getId());
                try {
                    notificationService.sendDisposalReminderEmail(caseDetails);
                } catch (NotificationClientException | RuntimeException e) {
                    log.info("Error sending email for case id: {}", caseDetails.getId());
                    failedCases.add(caseDetails.getId());
                }
            });
            log.info("Fail to send email with cases: {}", failedCases);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    public void disposeInactiveCase(String switchDate, String runDate, String startDate,
                                    long inactivityNotificationPeriod, long disposalGracePeriod) {
        List<Long> failedCases = new ArrayList<>();
        try {
            log.info("Start Dispose Deleted case initiated for date: {}, fromDate: {}, toDate: {}",
                    runDate, startDate, runDate);
            disposeGOPDeletedCase(startDate, runDate, failedCases);
            if (shouldSkipDraftDisposal(runDate, switchDate, disposalGracePeriod)) {
                log.info("Skipping draft disposal for runDate: {} ", runDate);
                return;
            }
            LocalDate switchLocalDate = LocalDate.parse(switchDate);
            LocalDate runLocalDate = LocalDate.parse(runDate);

            LocalDate disposalStartDate = LocalDate.parse(startDate);
            LocalDate disposalEndDate = runLocalDate.equals(switchLocalDate)
                    ? runLocalDate.minusDays(inactivityNotificationPeriod + disposalGracePeriod + 1) :
                    runLocalDate.minusDays(inactivityNotificationPeriod + disposalGracePeriod);
            log.info("Start Dispose inactive GOP case initiated for date: {}, fromDate: {}, toDate: {}",
                    runDate, disposalStartDate, disposalEndDate);
            disposeGOPDraftCase(disposalStartDate.toString(), disposalEndDate.toString(), failedCases);
            log.info("Start Dispose inactive Caveat case initiated for date: {}, fromDate: {}, toDate: {}",
                    runDate, disposalStartDate, disposalEndDate);
            disposeCaveatCase(disposalStartDate.toString(), disposalEndDate.toString(), failedCases);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        } finally {
            log.info("disposeInactiveCase completed. Fail to dispose cases: {}", failedCases);
        }
    }

    private boolean shouldSkipDraftDisposal(String runDate, String switchDate, long disposalInactivePeriod) {
        LocalDate switchLocalDate = LocalDate.parse(switchDate);
        LocalDate runLocalDate = LocalDate.parse(runDate);

        return runLocalDate.isBefore(switchLocalDate.plusDays(disposalInactivePeriod));
    }

    private void disposeGOPDraftCase(String disposalStartDate, String disposalEndDate, List<Long> failedCases) {
        List<ReturnedCaseDetails> casesFound = caseQueryService
                .findInactiveGOPCaseForDisposal(disposalStartDate, disposalEndDate);
        log.info("GOP Cases found for Dispose inactive case initiated for date: {}, cases found: {}",
                disposalStartDate, casesFound.size());

        casesFound.forEach(caseDetails -> {
            log.info("Disposing GOP draft case for case id: {}", caseDetails.getId());
            try {
                SecurityDTO securityDTO = securityUtils.getUserBySchedulerTokenAndServiceSecurityDTO();
                disposalCCDService.disposeGOPCase(caseDetails, caseDetails.getId().toString(), securityDTO);
            } catch (Exception e) {
                failedCases.add(caseDetails.getId());
                log.error("Error disposing GOP case for case id: {}", caseDetails.getId(), e);
            }
        });
    }

    private void disposeGOPDeletedCase(String disposalStartDate, String disposalEndDate, List<Long> failedCases) {
        List<ReturnedCaseDetails> casesFound = caseQueryService
                .findDeletedGOPCaseForDisposal(disposalStartDate, disposalEndDate);
        log.info("GOP Cases found for Dispose deleted case initiated for date: {}, cases found: {}",
                disposalStartDate, casesFound.size());

        casesFound.forEach(caseDetails -> {
            log.info("Disposing GOP deleted case for case id: {}", caseDetails.getId());
            try {
                SecurityDTO securityDTO = securityUtils.getUserBySchedulerTokenAndServiceSecurityDTO();
                disposalCCDService.disposeGOPCase(caseDetails, caseDetails.getId().toString(), securityDTO);
            } catch (Exception e) {
                failedCases.add(caseDetails.getId());
                log.error("Error disposing GOP case for case id: {}", caseDetails.getId(), e);
            }
        });
    }

    private void disposeCaveatCase(String disposalStartDate, String disposalEndDate, List<Long> failedCases) {
        List<ReturnedCaseDetails> casesFound = caseQueryService
                .findInactiveCaveatCaseForDisposal(disposalStartDate, disposalEndDate);
        log.info("Caveat Cases found for Dispose inactive case initiated for date: {}, cases found: {}",
                disposalStartDate, casesFound.size());
        casesFound.forEach(caseDetails -> {
            log.info("Disposing GOP Caveat for case id: {}", caseDetails.getId());
            try {
                SecurityDTO securityDTO = securityUtils.getUserBySchedulerTokenAndServiceSecurityDTO();
                disposalCCDService.disposeCaveatCase(caseDetails, caseDetails.getId().toString(), securityDTO);
            } catch (Exception e) {
                failedCases.add(caseDetails.getId());
                log.error("Error disposing Caveat case for case id: {}", caseDetails.getId(), e);
            }
        });
    }
}
