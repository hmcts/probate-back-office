package uk.gov.hmcts.probate.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.service.DormantCaseService;
import uk.gov.hmcts.probate.service.dataextract.DataExtractDateValidator;
import uk.gov.hmcts.reform.probate.model.client.ApiClientException;

import java.time.LocalDate;

import static uk.gov.hmcts.probate.model.Constants.DATE_FORMAT;

@Component
@Slf4j
@RequiredArgsConstructor
public class WithDrawDormantCasesTask implements Runnable {

    private final DataExtractDateValidator dataExtractDateValidator;
    private final DormantCaseService dormantCaseService;
    @Value("${withdrawal_dormant.minus_months}")
    private int withdrawDormancyPeriodMonths;

    @Override
    public void run() {
        log.info("Scheduled task WithDrawDormantCasesTask started to withdraw dormant cases");
        final String date = DATE_FORMAT.format(LocalDate.now().minusMonths(withdrawDormancyPeriodMonths));
        log.info("Calling perform withdraw dormant cases from date, to date {} {}", date, date);

        try {
            dataExtractDateValidator.dateValidator(date, date);
            log.info("Perform dormant withdraw from date started");
            dormantCaseService.withdrawDormantNotificationCases(date);
            log.info("Perform dormant withdraw from date finished");
        } catch (ApiClientException e) {
            log.error(e.getMessage());
        } catch (Exception e) {
            log.error("Error on WithDrawDormantCasesTask Scheduler {}", e.getMessage());
        }
    }

}
