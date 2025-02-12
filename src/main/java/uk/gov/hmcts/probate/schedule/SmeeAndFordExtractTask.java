package uk.gov.hmcts.probate.schedule;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.service.dataextract.DataExtractDateValidator;
import uk.gov.hmcts.probate.service.dataextract.SmeeAndFordDataExtractService;
import uk.gov.hmcts.reform.probate.model.client.ApiClientException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RequiredArgsConstructor
public class SmeeAndFordExtractTask implements Runnable {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final DataExtractDateValidator dataExtractDateValidator;
    private final SmeeAndFordDataExtractService smeeAndFordDataExtractService;

    @Value("${adhocSchedulerJobDate}")
    public String adHocJobDate;

    @Value("${schedulerTimerShutdownDelayMinutes}")
    public String schedulerTimerShutdownDelayMinutes;

    @Override
    public void run() {
        log.info("Scheduled task SmeeAndFordExtractTask started to extract data for Smee and Ford");
        String date = DATE_FORMAT.format(LocalDate.now().minusDays(1L));
        if (StringUtils.isNotEmpty(adHocJobDate)) {
            log.info("Ad hoc scheduler job date is given");
            date = adHocJobDate;
        }
        log.info("Calling perform Smee and Ford data extract from date, to date {} {}", date, date);
        try {
            dataExtractDateValidator.dateValidator(date, date);
            log.info("Perform Smee And Ford data extract from date started");
            smeeAndFordDataExtractService.performSmeeAndFordExtractForDateRange(date, date);
            log.info("Perform Smee And Ford data extract from date finished");

        } catch (ApiClientException e) {
            log.error(e.getMessage());
        } catch (FeignException e) {
            log.error("Error on calling BackOfficeAPI:{}", e.getMessage());
        } catch (Exception e) {
            log.error("Error on SmeeAndFordExtractTask Scheduler:{}", e.getMessage());
        } finally {
            ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
            executorService.schedule(new TimerTask() {
                @Override
                public void run() {
                    log.info("TimerTask for Smee And Ford data extract completed");
                }
            }, Integer.parseInt(schedulerTimerShutdownDelayMinutes), TimeUnit.MINUTES);
            executorService.shutdown();
        }
    }

}
