package uk.gov.hmcts.probate.service;

import com.github.hmcts.lifeevents.client.model.Alias;
import com.github.hmcts.lifeevents.client.model.V1Death;
import com.github.hmcts.lifeevents.client.service.DeathService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;

import java.time.LocalDate;
import java.util.List;

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

    public List<uk.gov.hmcts.probate.model.ccd.raw.CollectionMember<uk.gov.hmcts.probate.model.ccd.raw.DeathRecord>> 
        getDeathRecordsByNamesAndDate(final CaseDetails caseDetails) {
        final CaseData caseData = caseDetails.getData();
        final String deceasedForenames = caseData.getDeceasedForenames();
        final String deceasedSurname = caseData.getDeceasedSurname();
        final LocalDate deceasedDateOfDeath = caseData.getDeceasedDateOfDeath();
        log.info("Trying LEV call");
        List<V1Death> records;
        try {
            records = deathService.searchForDeathRecordsByNamesAndDate(deceasedForenames, deceasedSurname,
                deceasedDateOfDeath);
            for (V1Death record : records) {
                log.info("LifeEventService.getDeathRecordsByNamesAndDate record: {}", record);
                if (record.getDeceased() != null) {
                    log.info("Aliases size: {}", record.getDeceased().getAliases().size());
                    log.info("Date of death: {}", record.getDeceased().getDateOfDeath());
                    if (!record.getDeceased().getAliases().isEmpty()) {
                        for (Alias alias : record.getDeceased().getAliases()) {
                            log.info("Alias forenames: {}", alias.getForenames());
                            log.info("Alias surname: {}", alias.getSurname());
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error during LEV call", e);
            throw e;
        }

        if (records.isEmpty()) {
            String message = "No death records found";
            throw new BusinessValidationException(message, message);
        }

        return deathRecordCCDService.mapDeathRecords(records);
    }
}
