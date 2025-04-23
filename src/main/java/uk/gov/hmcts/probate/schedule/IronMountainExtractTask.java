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

    @Override
    public void run() {
        log.info("Scheduled task IronMountainExtractTask started to extract data for IronMountain");

        final String fromDate;
        if (scheduleDates.hasValue()) {
            fromDate = scheduleDates.getFromDate();
            log.info("IronMountainExtractTask has ad hoc date: {}", fromDate);
        } else {
            fromDate = scheduleDates.getYesterday();
            log.info("IronMountainExtractTask has no ad hoc date so default to yesterday: {}", fromDate);
        }

        try {
            dataExtractionDateValidator.dateValidator(fromDate);
            log.info("Perform iron mountain data extract from date started");
            ironMountainDataExtractService.performIronMountainExtractForDate(fromDate);
            log.info("Perform iron mountain data extract from date finished");
        } catch (RuntimeException e) {
            log.error("Exception within on IronMountainExtractTask Scheduler", e);
        }
    }

}