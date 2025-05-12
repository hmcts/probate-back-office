package uk.gov.hmcts.probate.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.service.CaveatExpiryService;
import uk.gov.hmcts.probate.service.dataextract.DataExtractDateValidator;
import uk.gov.hmcts.reform.probate.model.client.ApiClientException;

import java.time.LocalDate;

import static uk.gov.hmcts.probate.model.Constants.DATE_FORMAT;

@Component
@Slf4j
@RequiredArgsConstructor
public class CaveatExpiryTask implements Runnable {

    private final DataExtractDateValidator dataExtractDateValidator;
    private final CaveatExpiryService caveatExpiryService;

    @Value("${adhocSchedulerJobDate}")
    public String adHocJobDate;

    @Override
    public void run() {
        log.info("Scheduled task CaveatExpiryTask started to expire caveat");
        String expiryDate = DATE_FORMAT.format(LocalDate.now().minusDays(1L));
        if (StringUtils.isNotEmpty(adHocJobDate)) {
            log.info("Running CaveatExpiryTask with Adhoc date");
            expiryDate = adHocJobDate;
        }

        log.info("Calling perform caveat expiry for expiryDate: {}", expiryDate);
        try {
            dataExtractDateValidator.dateValidator(expiryDate);
            log.info("Perform caveat expiry started");
            caveatExpiryService.expireCaveats(expiryDate);
            log.info("Perform caveat expiry finished");
        } catch (ApiClientException e) {
            log.error(e.getMessage());
        } catch (Exception e) {
            log.error("Error on CaveatExpiryTask Scheduler {}", e.getMessage());
        }
    }

}
