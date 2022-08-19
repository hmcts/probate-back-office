package uk.gov.hmcts.probate.schedule;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.service.dataextract.DataExtractDateValidator;
import uk.gov.hmcts.probate.service.dataextract.SmeeAndFordDataExtractService;
import uk.gov.hmcts.reform.probate.model.client.ApiClientException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class SmeeAndFordExtractTask implements Runnable {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final DataExtractDateValidator dataExtractDateValidator;
    private final SmeeAndFordDataExtractService smeeAndFordDataExtractService;

    @Override
    public void run() {
        log.info("Scheduled task SmeeAndFordExtractTask started to extract data for Smee and Ford");
        final String date = DATE_FORMAT.format(LocalDate.now().minusDays(1L));
        log.info("Calling perform Smee and Ford data extract from date, to date {} {}", date, date);
        try {
            dataExtractDateValidator.dateValidator(date, date);
            log.info("Perform Smee And Ford data extract from date started");
            smeeAndFordDataExtractService.performSmeeAndFordExtractForDateRange(date, date);
            log.info("Perform Smee And Ford data extract from date finished");
            TimeUnit.MINUTES.sleep(10);
        } catch (ApiClientException e) {
            log.error(e.getMessage());
        } catch (FeignException e) {
            log.error("Error on calling BackOfficeAPI {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error on SmeeAndFordExtractTask Scheduler {}", e.getMessage());
        }
    }

}
