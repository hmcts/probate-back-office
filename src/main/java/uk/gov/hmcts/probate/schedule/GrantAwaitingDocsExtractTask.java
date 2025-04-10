package uk.gov.hmcts.probate.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import uk.gov.hmcts.probate.config.CustomRequestScopeAttr;
import uk.gov.hmcts.probate.model.GrantScheduleResponse;
import uk.gov.hmcts.probate.service.GrantNotificationService;
import uk.gov.hmcts.probate.service.dataextract.DataExtractDateValidator;
import uk.gov.hmcts.reform.probate.model.client.ApiClientException;

import java.time.LocalDate;

import static uk.gov.hmcts.probate.model.Constants.DATE_FORMAT;

@Component
@Slf4j
@RequiredArgsConstructor
public class GrantAwaitingDocsExtractTask implements Runnable {

    private final DataExtractDateValidator dataExtractDateValidator;
    private final GrantNotificationService grantNotificationService;

    @Value("${adhocSchedulerJobDate}")
    public String adHocJobFromDate;

    public void run() {
        log.info("Scheduled task GrantAwaitingDocumentation started to"
                + " extract data for Grant Awaiting Documentation Job");
        RequestContextHolder.setRequestAttributes(new CustomRequestScopeAttr());
        String fromDate = DATE_FORMAT.format(LocalDate.now().minusDays(1L));
        if (StringUtils.isNotEmpty(adHocJobFromDate)) {
            fromDate = adHocJobFromDate;
            log.info("Running GrantAwaitingDocumentationExtractTask with Adhoc dates {}", fromDate);
        }
        log.info("Calling perform grant awaiting documentation data extract from date {}", fromDate);
        try {
            dataExtractDateValidator.dateValidator(fromDate);
            log.info("Perform grant awaiting documentation data extract from date started");
            GrantScheduleResponse grantScheduleResponse =
                    grantNotificationService.handleAwaitingDocumentationNotification(fromDate);
            log.info("Grants awaiting documents attempted for: {} grants, {}",
                    grantScheduleResponse.getScheduleResponseData().size(),
                    StringUtils.joinWith(",", grantScheduleResponse.getScheduleResponseData()));
            log.info("Perform grant awaiting documentation data extract from date finished");
        } catch (ApiClientException e) {
            log.error(e.getMessage());
        } catch (Exception e) {
            log.error("Error on GrantAwaitingDocumentationExtractTask Scheduler {}", e.getMessage());
        }
    }
}
