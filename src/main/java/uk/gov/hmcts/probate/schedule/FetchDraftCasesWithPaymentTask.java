package uk.gov.hmcts.probate.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.service.FetchDraftCaseService;
import uk.gov.hmcts.probate.service.dataextract.DataExtractDateValidator;
import uk.gov.hmcts.reform.probate.model.client.ApiClientException;

import java.time.LocalDate;

import static uk.gov.hmcts.probate.model.Constants.DATE_FORMAT;

@Component
@Slf4j
@RequiredArgsConstructor
public class FetchDraftCasesWithPaymentTask implements Runnable {

    private final DataExtractDateValidator dataExtractDateValidator;
    private final FetchDraftCaseService fetchDraftCaseService;
    @Value("${adhocSchedulerJobDate}")
    public String adHocJobFromDate;
    @Value("${adhocSchedulerJobToDate}")
    public String adHocJobToDate;

    @Override
    public void run() {
        log.info("Scheduled task FetchDraftCasesWithPaymentTask started");
        String startDate = DATE_FORMAT.format(LocalDate.now().minusDays(180L));
        String endDate = startDate;
        if (StringUtils.isNotEmpty(adHocJobFromDate)) {
            startDate = adHocJobFromDate;
            endDate = StringUtils.isNotEmpty(adHocJobToDate) ? adHocJobToDate : adHocJobFromDate;
            log.info("Running FetchDraftCasesWithPaymentTask with Adhoc dates {} {}", startDate, endDate);
        }
        log.info("Calling perform fetch draft cases wth payment done from date, to date {} {}", startDate, endDate);
        try {
            dataExtractDateValidator.dateValidator(startDate, endDate);
            log.info("Perform send email for GOR draft cases wth payment  from date started");
            fetchDraftCaseService.fetchGORCases(startDate, endDate);
            log.info("Perform send email for Caveat draft cases wth payment  from date started");
            fetchDraftCaseService.fetchCaveatCases(startDate, endDate);
            log.info("Perform fetch draft cases with payment from date finished");
        } catch (ApiClientException e) {
            log.error(e.getMessage());
        } catch (Exception e) {
            log.error("Error on FetchDraftCasesWithPaymentTask Scheduler {}", e.getMessage());
        }
    }

}