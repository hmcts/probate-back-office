package uk.gov.hmcts.probate.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ScheduleDates;
import uk.gov.hmcts.probate.service.dataextract.DataExtractDateValidator;
import uk.gov.hmcts.probate.service.dataextract.ExelaDataExtractService;

@Component
@Slf4j
public class ExelaExtractTask implements Runnable {

    private final DataExtractDateValidator dataExtractJobDateValidator;
    private final ExelaDataExtractService exelaDataExtractService;
    private final ScheduleDates scheduleDates;

    public ExelaExtractTask(
            final DataExtractDateValidator dataExtractJobDateValidator,
            final ExelaDataExtractService exelaDataExtractService,
            final ScheduleDates scheduleExtractDates) {
        this.dataExtractJobDateValidator = dataExtractJobDateValidator;
        this.exelaDataExtractService = exelaDataExtractService;
        this.scheduleDates = scheduleExtractDates;
    }

    @Override
    public void run() {
        log.info("Scheduled task ExelaExtractTask started to extract data for Exela");

        final String fromDate;
        final String toDate;
        if (scheduleDates.hasValue()) {
            fromDate = scheduleDates.getFromDate();
            toDate = scheduleDates.getToDate();
            log.info("Running ExelaDataExtractTask with ad hoc dates {} - {}", fromDate, toDate);
        } else {
            fromDate = scheduleDates.getYesterday();
            toDate = fromDate;
            log.info("Running ExelaDataExtractTask with default dates (yesterday): {} - {}", fromDate, toDate);
        }

        try {
            dataExtractJobDateValidator.dateValidator(fromDate, toDate);
            log.info("Perform Exela data extract from date started");
            exelaDataExtractService.performExelaExtractForDateRange(fromDate, toDate);
            log.info("Perform Exela data extract from date finished");
        } catch (RuntimeException e) {
            log.error("Error on ExelaExtractTask Scheduler", e);
        }
    }

}