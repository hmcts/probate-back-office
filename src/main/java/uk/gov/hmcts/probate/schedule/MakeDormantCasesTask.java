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
public class MakeDormantCasesTask implements Runnable {

    private final DataExtractDateValidator dataExtractDateValidator;
    private final DormantCaseService dormantCaseService;
    @Value("${dormancy.period_months}")
    private int dormancyPeriodMonths;
    @Value("${dormancy.start_date}")
    private String dormancyStartDate;

    @Override
    public void run() {
        log.info("Scheduled task MakeDormantCasesTask started to make dormant cases");
        final String endDate = DATE_FORMAT.format(LocalDate.now().minusDays(0L));
        log.info("Calling perform make dormant from date, to date {} {}", dormancyStartDate, endDate);

        try {
            dataExtractDateValidator.dateValidator(dormancyStartDate, endDate);
            log.info("Perform make dormant from date started");
            dormantCaseService.makeCasesDormant("2025-05-23", endDate);
            log.info("Perform make dormant from date finished");
        } catch (ApiClientException e) {
            log.error(e.getMessage());
        } catch (Exception e) {
            log.error("Error on MakeDormantCasesTask Scheduler {}", e.getMessage());
        }
    }

}
