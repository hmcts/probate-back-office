package uk.gov.hmcts.probate.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.service.DormantCaseReminderService;
import uk.gov.hmcts.probate.service.dataextract.DataExtractDateValidator;
import uk.gov.hmcts.reform.probate.model.client.ApiClientException;

import java.time.LocalDate;

import static uk.gov.hmcts.probate.model.Constants.DATE_FORMAT;

@Component
@Slf4j
@RequiredArgsConstructor
public class BulkScanDormantCaseReminderTask implements Runnable {

    private final DataExtractDateValidator dataExtractDateValidator;
    private final DormantCaseReminderService dormantCaseReminderService;

    @Override
    public void run() {
        log.info("Scheduled task BulkScanDormantCaseReminderTask started");
        String reminderDate = DATE_FORMAT.format(LocalDate.now().minusDays(0L));
        log.info("Calling perform sending remainder email for paper cases from date, to date {} {}",
                reminderDate, reminderDate);
        try {
            dataExtractDateValidator.dateValidator(reminderDate, reminderDate);
            log.info("Perform sending remainder email for paper cases from date started");
            dormantCaseReminderService.sendReminderLetter(reminderDate, reminderDate);
        } catch (ApiClientException e) {
            log.error(e.getMessage());
        } catch (Exception e) {
            log.error("Error on BulkScanDormantCaseReminderTask Scheduler {}", e.getMessage());
        }
    }

}