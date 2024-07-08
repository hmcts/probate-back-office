package uk.gov.hmcts.probate.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.service.MigrationIssueDormantCaseService;
import uk.gov.hmcts.probate.service.dataextract.DataExtractDateValidator;
import uk.gov.hmcts.reform.probate.model.client.ApiClientException;

import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataMigrationIssueDormantCasesTask implements Runnable {

    private final DataExtractDateValidator dataExtractDateValidator;
    private final MigrationIssueDormantCaseService migrationIssueDormantCaseService;
    @Value("${adhocSchedulerJobCaseReference}")
    private String caseReferences;
    @Value("${adhocSchedulerJobDate}")
    public String adHocJobDate;

    @Override
    public void run() {
        log.info("Scheduled task DataMigrationIssueDormantCasesTask started to make dormant cases");
        if (StringUtils.isNotEmpty(adHocJobDate) && StringUtils.isNotEmpty(caseReferences)) {
            log.info("Running DataMigrationIssueDormantCasesTask with Adhoc date");
            try {
                log.info("Calling perform dormant Data Migration issue cases for date {}", adHocJobDate);
                dataExtractDateValidator.dateValidator(adHocJobDate);
                log.info("Perform Data Migration issue cases to dormant for Adhoc date started");
                List<String> caseReferenceList = Arrays.asList(caseReferences.split(",", -1));
                migrationIssueDormantCaseService.makeCaseReferenceDormant(caseReferenceList);
                log.info("Perform Data Migration issue cases to dormant for Adhoc date finished");
            } catch (ApiClientException e) {
                log.error(e.getMessage());
            } catch (Exception e) {
                log.error("Error on DataMigrationIssueDormantCasesTask Scheduler {}", e.getMessage());
            }
        } else {
            log.error("Adhoc date or case references are not set");
        }
    }

}
