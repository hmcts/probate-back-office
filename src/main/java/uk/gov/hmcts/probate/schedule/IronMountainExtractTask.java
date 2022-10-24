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

    private final DataExtractDateValidator dataExtractDateValidator;
    private final IronMountainDataExtractService ironMountainDataExtractService;

    @Value("${ironMountain_extract.minus_days}")
    private int ironMountainExtractMinusDays;
    @Value("${adhocSchedulerJobDate}")
    public String adHocJobDate;

    @Override
    public void run() {
        log.info("Scheduled task IronMountainExtractTask started to extract data for IronMountain");
        String date = DATE_FORMAT.format(LocalDate.now().minusDays(ironMountainExtractMinusDays));
        if (StringUtils.isNotEmpty(adHocJobDate)) {
            log.info("Running IronMountainExtractTask with Adhoc date {}",date);
            date = adHocJobDate;
        }
        log.info("Calling perform Iron Mountain data extract from date {}", date);
        try {
            dataExtractDateValidator.dateValidator(date);
            log.info("Perform IronMountain data extract from date started");
            ironMountainDataExtractService.performIronMountainExtractForDate(date);
            log.info("Perform IronMountain data extract from date finished");
        } catch (ApiClientException e) {
            log.error(e.getMessage());
        }  catch (Exception e) {
            log.error("Error on IronMountainExtractTask Scheduler {}", e.getMessage());
        }
    }

}
