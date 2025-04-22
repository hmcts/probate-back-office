package uk.gov.hmcts.probate.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.hmcts.probate.model.ScheduleDates;

import java.time.Clock;

@Configuration
@Slf4j
public class ScheduleConfiguration {
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

    @Bean
    public ScheduleDates extractScheduleDates(
            final Clock clock,
            @Value("${adhocSchedulerJobDate}") final String adHocJobStartDate,
            @Value("${adhocSchedulerJobToDate}") final String adHocJobEndDate) {
        log.info("Setting up extract dates with values: [{}], [{}]", adHocJobStartDate, adHocJobEndDate);
        return new ScheduleDates(clock, adHocJobStartDate, adHocJobEndDate);
    }
}
