package uk.gov.hmcts.probate.service;

import com.github.hmcts.lifeevents.client.model.Deceased;
import com.github.hmcts.lifeevents.client.model.V1Death;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.DeathRecord;

import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
public class DeathRecordCCDService {
    @SuppressWarnings("squid:S2583")
    public DeathRecord mapDeathRecord(V1Death v1Death) {
        if (null == v1Death) {
            return null;
        }
        final DeathRecord.DeathRecordBuilder builder = DeathRecord.builder().systemNumber(v1Death.getId());
        final Deceased deceased = v1Death.getDeceased();

        if (nonNull(deceased)) {
            builder.name(String.format("%s %s", deceased.getForenames(), deceased.getSurname()))
                    .dateOfBirth(deceased.getDateOfBirth())
                    .sex(null == deceased.getSex() ? null : deceased.getSex().getValue())
                    .address(deceased.getAddress())
                    .dateOfDeath(deceased.getDateOfDeath());
        }

        return builder.build();
    }
}
