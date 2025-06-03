package uk.gov.hmcts.probate.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.service.FetchDraftCaseService;
import uk.gov.hmcts.probate.service.dataextract.DataExtractDateValidator;
import uk.gov.hmcts.reform.probate.model.client.ApiClientException;

import java.time.LocalDate;

import static uk.gov.hmcts.probate.model.Constants.DATE_FORMAT;

@Component
@Slf4j
public class FetchDraftCasesWithPaymentTask implements Runnable {

    private final DataExtractDateValidator dataExtractDateValidator;
    private final FetchDraftCaseService fetchDraftCaseService;
    @Value("${draft_payment.start_date}")
    public String startDate;

    public FetchDraftCasesWithPaymentTask(DataExtractDateValidator dataExtractDateValidator,
                                          FetchDraftCaseService fetchDraftCaseService,
                                          @Value("${draft_payment.start_date}") String startDate) {
        this.dataExtractDateValidator = dataExtractDateValidator;
        this.fetchDraftCaseService = fetchDraftCaseService;
        this.startDate = startDate;
    }

    // Update LocalDate.now() call on L30 with config when DTSPB-4580 is merged.
    // https://github.com/hmcts/probate-back-office/pull/3154/files/396d0f0214ced1ce238cd321229954a265110111#r2115573061
    @Override
    public void run() {
        log.info("Scheduled task FetchDraftCasesWithPaymentTask started");
        final String endDate = DATE_FORMAT.format(LocalDate.now());
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