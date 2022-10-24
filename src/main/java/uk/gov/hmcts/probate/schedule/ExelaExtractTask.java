package uk.gov.hmcts.probate.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.service.dataextract.DataExtractDateValidator;
import uk.gov.hmcts.probate.service.dataextract.ExelaDataExtractService;
import uk.gov.hmcts.reform.probate.model.client.ApiClientException;

import java.time.LocalDate;

import static uk.gov.hmcts.probate.model.Constants.DATE_FORMAT;

@Component
@Slf4j
@RequiredArgsConstructor
public class ExelaExtractTask implements Runnable {

    private final DataExtractDateValidator dataExtractDateValidator;
    private final ExelaDataExtractService exelaDataExtractService;

    @Value("${exela_extract.minus_days}")
    private int exelaExtractMinusDays;
    @Value("${adhocSchedulerJobDate}")
    public String adHocJobDate;

    @Override
    public void run() {
        log.info("Scheduled task ExelaExtractTask started to extract data for Exela");
        String date = DATE_FORMAT.format(LocalDate.now().minusDays(exelaExtractMinusDays));
        if (StringUtils.isNotEmpty(adHocJobDate)) {
            log.info("Running ExelaExtractTask with Adhoc date {}",date);
            date = adHocJobDate;
        }
        log.info("Calling perform Exela data extract from date, to date {} {}", date, date);
        try {
            dataExtractDateValidator.dateValidator(date, date);
            log.info("Perform Exela data extract from date started");
            exelaDataExtractService.performExelaExtractForDateRange(date, date);
            log.info("Perform Exela data extract from date finished");
        } catch (ApiClientException e) {
            log.error(e.getMessage());
        }  catch (Exception e) {
            log.error("Error on ExelaExtractTask Scheduler {}", e.getMessage());
        }
    }

}
