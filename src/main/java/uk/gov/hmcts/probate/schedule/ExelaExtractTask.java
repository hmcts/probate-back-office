package uk.gov.hmcts.probate.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ScheduleDates;
import uk.gov.hmcts.probate.service.FeatureToggleService;
import uk.gov.hmcts.probate.service.dataextract.DataExtractDateValidator;
import uk.gov.hmcts.probate.service.dataextract.ExelaDataExtractService;

@Component
@Slf4j
public class ExelaExtractTask implements Runnable {

    private final DataExtractDateValidator dataExtractJobDateValidator;
    private final ExelaDataExtractService exelaDataExtractService;
    private final ScheduleDates scheduleDates;
    private final FeatureToggleService featureToggleService;

    public ExelaExtractTask(
            final DataExtractDateValidator dataExtractJobDateValidator,
            final ExelaDataExtractService exelaDataExtractService,
            final ScheduleDates scheduleExtractDates,
            final FeatureToggleService featureToggleService) {
        this.dataExtractJobDateValidator = dataExtractJobDateValidator;
        this.exelaDataExtractService = exelaDataExtractService;
        this.scheduleDates = scheduleExtractDates;
        this.featureToggleService = featureToggleService;
    }

    /// Note that the log messages here are used for alerting/monitoring
    @Override
    public void run() {
        log.info("Scheduled task ExelaExtractTask");

        final String descr;
        final String fromDate;
        final String toDate;
        if (scheduleDates.hasValue()) {
            descr = "ad hoc dates";
            fromDate = scheduleDates.getFromDate();
            toDate = scheduleDates.getToDate();
        } else {
            descr = "default dates (yesterday)";
            fromDate = scheduleDates.getYesterday();
            toDate = fromDate;
        }
        log.info("Running ExelaDataExtractTask with {}: {} - {}", descr, fromDate, toDate);

        try {
            dataExtractJobDateValidator.dateValidator(fromDate, toDate);

            log.info("Starting ExelaExtractTask with {}: {} - {}", descr, fromDate, toDate);
            exelaDataExtractService.performExelaExtractForDateRange(fromDate, toDate);
            log.info("Finished ExelaExtractTask with {}: {} - {}", descr, fromDate, toDate);
        } catch (RuntimeException e) {
            final String errMsg = String.format(
                    "Exception in ExelaExtractTask with %s: %s - %s",
                    descr,
                    fromDate,
                    toDate);
            log.error(errMsg, e);
        }
    }
}
