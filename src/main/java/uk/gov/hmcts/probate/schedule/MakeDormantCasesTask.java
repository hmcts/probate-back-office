package uk.gov.hmcts.probate.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.service.DormantCaseService;
import uk.gov.hmcts.probate.service.dataextract.DataExtractDateValidator;
import uk.gov.hmcts.reform.probate.model.client.ApiClientException;

import java.time.LocalDate;

import static uk.gov.hmcts.probate.model.Constants.DATE_FORMAT;
import static uk.gov.hmcts.probate.model.Constants.MAKE_DORMANT_FROM_MONTH;

@Component
@Slf4j
@RequiredArgsConstructor
public class MakeDormantCasesTask implements Runnable {

    private final DataExtractDateValidator dataExtractDateValidator;
    private final DormantCaseService dormantCaseService;

    @Override
    public void run() {
        log.info("Scheduled task MakeDormantCasesTask started to make dormant cases");
        final String date = DATE_FORMAT.format(LocalDate.now().minusMonths(MAKE_DORMANT_FROM_MONTH));
        log.info("Calling perform make dormant from date, to date {} {}", date, date);
        try {
            dataExtractDateValidator.dateValidator(date, date);
            log.info("Perform make dormant from date started");
            dormantCaseService.makeCasesDormant(date);
            log.info("Perform make dormant from date finished");
        } catch (ApiClientException e) {
            log.error(e.getMessage());
        } catch (Exception e) {
            log.error("Error on MakeDormantCasesTask Scheduler {}", e.getMessage());
        }
    }

}
