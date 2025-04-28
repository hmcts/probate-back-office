package uk.gov.hmcts.probate.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ScheduleDates;
import uk.gov.hmcts.probate.service.dataextract.DataExtractDateValidator;
import uk.gov.hmcts.probate.service.dataextract.IronMountainDataExtractService;

@Component
@Slf4j
public class IronMountainExtractTask implements Runnable {

    private final DataExtractDateValidator dataExtractionDateValidator;
    private final IronMountainDataExtractService ironMountainDataExtractService;
    private final ScheduleDates scheduleDates;

    public IronMountainExtractTask(
            final DataExtractDateValidator dataExtractionDateValidator,
            final IronMountainDataExtractService ironMountainDataExtractService,
            final ScheduleDates extractScheduleDates) {
        this.dataExtractionDateValidator = dataExtractionDateValidator;
        this.ironMountainDataExtractService = ironMountainDataExtractService;
        this.scheduleDates = extractScheduleDates;
    }

    /// Note that the log messages here are used for alerting/monitoring
    @Override
    public void run() {
        log.info("Scheduled task IronMountainExtractTask");

        final String descr;
        final String fromDate;
        if (scheduleDates.hasValue()) {
            descr = "ad hoc date";
            fromDate = scheduleDates.getFromDate();
        } else {
            fromDate = scheduleDates.getYesterday();
            descr = "default dates (yesterday)";
        }
        log.info("Running IronMountainExtractTask with {}: {}", descr, fromDate);

        try {
            dataExtractionDateValidator.dateValidator(fromDate);

            log.info("Starting IronMountainExtractTask with {}: {}", descr, fromDate);
            ironMountainDataExtractService.performIronMountainExtractForDate(fromDate);
            log.info("Finished IronMountainExtractTask with {}: {}", descr, fromDate);
        } catch (RuntimeException e) {
            final String errMsg = String.format("Exception in IronMountainExtractTask with %s: %s", descr, fromDate);
            log.error(errMsg, e);
        }
    }
}
