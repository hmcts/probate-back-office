package uk.gov.hmcts.probate.schedule;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.service.FeatureToggleService;
import uk.gov.hmcts.probate.service.dataextract.DataExtractDateValidator;
import uk.gov.hmcts.probate.service.dataextract.DataExtractService;
import uk.gov.hmcts.reform.probate.model.client.ApiClientException;

import java.time.Clock;
import java.time.LocalDate;

import static uk.gov.hmcts.probate.model.Constants.DATE_FORMAT;
import static uk.gov.hmcts.probate.model.DataExtractType.NATIONAL_FRAUD_INITIATIVE;

@Component
@Slf4j
public class DataExtractTask implements Runnable {

    private final Clock clock;
    private final DataExtractDateValidator dataExtractDateValidator;
    private final FeatureToggleService featureToggleService;
    private final DataExtractService dataExtractService;
    public final String adHocJobFromDate;
    public final String adHocJobToDate;

    public DataExtractTask(Clock clock,
                           DataExtractDateValidator dataExtractDateValidator,
                           FeatureToggleService featureToggleService,
                           DataExtractService dataExtractService,
                           @Value("${adhocSchedulerJobDate}") String adHocJobFromDate,
                           @Value("${adhocSchedulerJobToDate}") String adHocJobToDate) {
        this.clock = clock;
        this.dataExtractDateValidator = dataExtractDateValidator;
        this.featureToggleService = featureToggleService;
        this.dataExtractService = dataExtractService;
        this.adHocJobFromDate = adHocJobFromDate;
        this.adHocJobToDate = adHocJobToDate;
    }

    @Override
    public void run() {
        log.info("Scheduled task DataExtractTask started");
        String fromDate = DATE_FORMAT.format(LocalDate.now(clock).minusDays(1L));
        String toDate = fromDate;
        if (StringUtils.isNotEmpty(adHocJobFromDate)) {
            fromDate = adHocJobFromDate;
            toDate = StringUtils.isNotEmpty(adHocJobToDate) ? adHocJobToDate : adHocJobFromDate;
            log.info("Running DataExtractTask with with Adhoc dates {} {}", fromDate, toDate);
        }
        log.info("Calling perform data extract from date, to date {} {}", fromDate, toDate);
        try {
            if (!featureToggleService.isNfiDataExtractFeatureToggleOn()) {
                log.info("NFI data extract feature is disabled, skipping task");
            } else {
                dataExtractDateValidator.dateValidator(fromDate, toDate);
                log.info("Perform NFI data extract from date started");
                dataExtractService.performExtractForDateRange(fromDate, toDate, NATIONAL_FRAUD_INITIATIVE);
                log.info("Perform NFI data extract from date finished");
            }
        } catch (ApiClientException e) {
            log.error("API client exception during NFI data extract", e);
        } catch (Exception e) {
            log.error("Error on DataExtractTask Scheduler NFI data extract", e);
        }
    }
}
