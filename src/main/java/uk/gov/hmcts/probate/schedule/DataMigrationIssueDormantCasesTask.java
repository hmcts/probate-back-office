package uk.gov.hmcts.probate.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.service.MigrationIssueDormantCaseService;
import uk.gov.hmcts.reform.probate.model.client.ApiClientException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataMigrationIssueDormantCasesTask implements Runnable {

    private final MigrationIssueDormantCaseService migrationIssueDormantCaseService;
    @Value("${adhocSchedulerJobCaseReference}")
    private String caseReferences;
    @Value("${dormancy.period_months}")
    private int dormancyPeriodMonths;

    @Override
    public void run() {
        log.info("Scheduled task DataMigrationIssueDormantCasesTask started to make dormant cases");
        if (StringUtils.isNotEmpty(caseReferences)) {
            log.info("Running DataMigrationIssueDormantCasesTask with Adhoc date");
            try {
                final LocalDateTime dormancyPeriod = LocalDateTime.now().minusMonths(dormancyPeriodMonths);
                log.info("Calling perform dormant Data Migration issue cases for cases {}", caseReferences);
                List<String> caseReferenceList = Arrays.asList(caseReferences.split(",", -1));
                log.info("Perform Data Migration issue cases to dormant for Adhoc date started");
                migrationIssueDormantCaseService.makeCaseReferenceDormant(caseReferenceList, dormancyPeriod);
                log.info("Perform Data Migration issue cases to dormant for Adhoc date finished");
            } catch (ApiClientException e) {
                log.error(e.getMessage());
            } catch (Exception e) {
                log.error("Error on DataMigrationIssueDormantCasesTask Scheduler {}", e.getMessage());
            }
        } else {
            log.error("Case references are not provided for DataMigrationIssueDormantCasesTask Scheduler");
        }
    }

}
