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
        String fromDate = DATE_FORMAT.format(LocalDate.now().minusDays(1L));
        String toDate = fromDate;
        if (StringUtils.isNotEmpty(adHocJobFromDate)) {
            fromDate = adHocJobFromDate;
            toDate = StringUtils.isNotEmpty(adHocJobToDate) ? adHocJobToDate : adHocJobFromDate;
            log.info("Running FetchDraftCasesWithPaymentTask with Adhoc dates {} {}", fromDate, toDate);
        }
        log.info("Calling perform fetch draft cases wth payment done from date, to date {} {}", fromDate, toDate);
        try {
            dataExtractDateValidator.dateValidator(fromDate, toDate);
            log.info("Perform send email for GOR draft cases wth payment  from date started");
            fetchDraftCaseService.fetchCases(fromDate, toDate, false);
            log.info("Perform send email for Caveat draft cases wth payment  from date started");
            fetchDraftCaseService.fetchCases(fromDate, toDate, true);
            log.info("Perform fetch draft cases with payment from date finished");
        } catch (ApiClientException e) {
            log.error(e.getMessage());
        } catch (Exception e) {
            log.error("Error on FetchDraftCasesWithPaymentTask Scheduler {}", e.getMessage());
        }
    }

}