package uk.gov.hmcts.probate.service;

import com.github.hmcts.lifeevents.client.model.V1Death;
import com.github.hmcts.lifeevents.client.service.DeathService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.raw.DeathRecord;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableAsync
public class LifeEventService {
    
    private DeathService deathService;
    private DeathRecordCCDService deathRecordCCDService;

    @Autowired
    public LifeEventService(final DeathService deathService, final DeathRecordCCDService deathRecordCCDService) {
        this.deathService = deathService;
        this.deathRecordCCDService = deathRecordCCDService;
    }

    public DeathRecord getDeathRecordById(final Integer systemNumber) {
        log.info("Trying LEV call");
        V1Death record;
        try {
            record = deathService.getDeathRecordById(systemNumber);
        } catch (Exception e) {
            log.error("Error during LEV call", e);
            throw e;
        }

        if (null == record) {
            String message = String.format("No death record found with system number %s", systemNumber);
            throw new BusinessValidationException(message, message);
        }

        return deathRecordCCDService.mapDeathRecord(record);
    }
}
