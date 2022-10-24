package uk.gov.hmcts.probate.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.service.GrantNotificationService;
import uk.gov.hmcts.probate.service.dataextract.DataExtractDateValidator;
import uk.gov.hmcts.reform.probate.model.client.ApiClientException;

import java.time.LocalDate;

import static uk.gov.hmcts.probate.model.Constants.DATE_FORMAT;

@Component
@Slf4j
@RequiredArgsConstructor
public class GrantDelayedTask implements Runnable {

    private final GrantNotificationService grantNotificationService;

    @Value("${adhocSchedulerJobDate}")
    public String adHocJobDate;

    @Override
    public void run() {
        log.info("Scheduled task GrantDelayedTask started to extract data for GrantDelayed");
        String date = DATE_FORMAT.format(LocalDate.now().minusDays(1L));
        if (StringUtils.isNotEmpty(adHocJobDate)) {
            log.info("Running GrantDelayedTask with Adhoc date {}",date);
            date = adHocJobDate;
        }
        log.info("Calling perform Grants delayed for date {}", date);
        try {
            log.info("Perform Grants delayed data extract from date started");
            grantNotificationService.handleGrantDelayedNotification(date);
            log.info("Perform Grants delayed data extract from date finished");
        } catch (ApiClientException e) {
            log.error(e.getMessage());
        }  catch (Exception e) {
            log.error("Error on GrantDelayedTask Scheduler {}", e.getMessage());
        }
    }

}
