package uk.gov.hmcts.probate.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.service.DormantCaseService;
import uk.gov.hmcts.probate.service.dataextract.DataExtractDateValidator;
import uk.gov.hmcts.reform.probate.model.client.ApiClientException;

import java.time.LocalDate;

import static uk.gov.hmcts.probate.model.Constants.DATE_FORMAT;
import static uk.gov.hmcts.probate.model.Constants.REACTIVATE_DORMANT_FROM_DAY;


@Component
@Slf4j
@RequiredArgsConstructor
public class ReactivateDormantCasesTask implements Runnable {

    private final DataExtractDateValidator dataExtractDateValidator;
    private final DormantCaseService dormantCaseService;

    @Override
    public void run() {
        log.info("Scheduled task ReactivateDormantCasesTask started to reactivate dormant cases");
        final String date = DATE_FORMAT.format(LocalDate.now().minusDays(REACTIVATE_DORMANT_FROM_DAY));
        log.info("Calling perform reactivate dormant from date, to date {} {}", date, date);
        try {
            dataExtractDateValidator.dateValidator(date, date);
            log.info("Perform reactivate dormant from date started");
            dormantCaseService.reactivateDormantCases(date);
            log.info("Perform reactivate dormant from date finished");
        } catch (ApiClientException e) {
            log.error(e.getMessage());
        } catch (Exception e) {
            log.error("Error on ReactivateDormantCasesTask Scheduler {}", e.getMessage());
        }
    }

}
