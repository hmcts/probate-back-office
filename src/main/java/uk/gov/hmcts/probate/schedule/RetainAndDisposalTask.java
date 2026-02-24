package uk.gov.hmcts.probate.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.service.RetainAndDisposalService;
import uk.gov.hmcts.probate.service.dataextract.DataExtractDateValidator;

import java.time.LocalDate;

import static uk.gov.hmcts.probate.model.Constants.DATE_FORMAT;

@Component
@Slf4j
@RequiredArgsConstructor
public class RetainAndDisposalTask implements Runnable {
    private final RetainAndDisposalService retainAndDisposalService;
    private final DataExtractDateValidator dataExtractDateValidator;

    @Value("${disposal.adhocSchedulerJobDate}")
    public String adHocJobDate;

    @Value("${disposal.inactivityNotificationPeriod}")
    public String inactivityNotificationPeriod;

    @Value("${disposal.disposalGracePeriod}")
    public String disposalGracePeriod;

    @Value("${disposal.switchDate}")
    public String switchDate;

    @Value("${disposal.startDate}")
    public String startDate;

    @Override
    public void run() {
        log.info("Scheduled task RetainAndDisposalTask started");
        String runDate = DATE_FORMAT.format(LocalDate.now());
        if (StringUtils.isNotEmpty(adHocJobDate)) {
            runDate = adHocJobDate;
            log.info("Running RetainAndDisposalTask with Adhoc dates {}", runDate);
        }
        dataExtractDateValidator.dateValidator(switchDate, runDate);

        log.info("Calling send email for inactive case for date {}, with inactive period {}",
                runDate, inactivityNotificationPeriod);
        try {
            log.info("Perform send email for inactive caveat case started");
            retainAndDisposalService
                    .sendEmailForInactiveCase(switchDate, runDate, Long.parseLong(inactivityNotificationPeriod),
                            true);
            log.info("Perform send email for inactive gop case started");
            retainAndDisposalService
                    .sendEmailForInactiveCase(switchDate, runDate, Long.parseLong(inactivityNotificationPeriod),
                            false);
            log.info("Perform send email for inactive case finished");
        } catch (Exception e) {
            log.error("Error on RetainAndDisposalTask Scheduler send email task {}", e.getMessage());
        }

        try {
            if (runDate.equals(switchDate)) {
                log.info("Skipping dispose inactive case for date {}", runDate);
                return;
            }
            log.info("Perform dispose inactive case started");
            retainAndDisposalService.disposeInactiveCase(switchDate, runDate, startDate,
                    Long.parseLong(inactivityNotificationPeriod), Long.parseLong(disposalGracePeriod));
            log.info("Perform dispose inactive case finished");
        } catch (Exception e) {
            log.error("Error on RetainAndDisposalTask Scheduler disposal task {}", e.getMessage());
        }
    }
}
