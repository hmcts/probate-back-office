package uk.gov.hmcts.probate.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.service.dataextract.DataExtractDateValidator;
import uk.gov.hmcts.probate.service.dataextract.IronMountainDataExtractService;
import uk.gov.hmcts.reform.probate.model.client.ApiClientException;

import java.time.LocalDate;

import static uk.gov.hmcts.probate.model.Constants.DATE_FORMAT;

@Component
@Slf4j
@RequiredArgsConstructor
public class IronMountainExtractTask implements Runnable {

    private final DataExtractDateValidator dataExtractionDateValidator;
    private final IronMountainDataExtractService ironMountainDataExtractService;

    @Value("${adhocSchedulerJobDate}")
    public String adHocJobFromDate;

    @Override
    public void run() {
        log.info("Scheduled task IronMountainExtractTask started to extract data for IronMountain");
        String fromDate = DATE_FORMAT.format(LocalDate.now().minusDays(1L));
        if (StringUtils.isNotEmpty(adHocJobFromDate)) {
            fromDate = adHocJobFromDate;
            log.info("Running IronMountainDataExtractTask with Adhoc dates {}", fromDate);
        }
        log.info("Calling perform iron mountain data extract from date {}", fromDate);
        try {
            dataExtractionDateValidator.dateValidator(fromDate);
            log.info("Perform iron mountain data extract from date started");
            ironMountainDataExtractService.performIronMountainExtractForDate(fromDate);
            log.info("Perform iron mountain data extract from date finished");
        } catch (ApiClientException e) {
            log.error(e.getMessage());
        } catch (Exception e) {
            log.error("Error on IronMountainExtractTask Scheduler {}", e.getMessage());
        }
    }

}
