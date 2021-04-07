package uk.gov.hmcts.probate.service;

import uk.gov.hmcts.lifeevents.client.api.DeathApiClient;
import uk.gov.hmcts.lifeevents.client.model.V1Death;
import uk.gov.hmcts.lifeevents.client.service.DeathService;

import java.time.LocalDate;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LifeEventService {

    private DeathService deathService;

    @Autowired
    public LifeEventService(final DeathService deathService) {
        this.deathService = deathService;
    }

    public List<V1Death> findDeathRecords(final String deceasedForenames, final String deceasedSurname, final String deceasedDateOfDeath) {
        final LocalDate localDate = LocalDate.parse(deceasedDateOfDeath);
        return deathService.searchForDeathRecordsByNamesAndDate(deceasedForenames, deceasedSurname, localDate);
    }
}
