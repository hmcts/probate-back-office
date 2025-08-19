package uk.gov.hmcts.probate.service;

import com.github.hmcts.lifeevents.client.model.Deceased;
import com.github.hmcts.lifeevents.client.model.V1Death;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.DeathRecord;

import jakarta.validation.constraints.NotNull;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.Alias;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
public class DeathRecordCCDService {

    public List<CollectionMember<DeathRecord>> mapDeathRecords(List<V1Death> deathRecords) {
        return Optional.ofNullable(deathRecords)
            .map(Collection::stream)
            .orElseGet(Stream::empty)
            .filter(Objects::nonNull)
            .map(this::mapCollectionMember)
            .flatMap(Optional::stream)
            .collect(Collectors.toList());
    }

    private Optional<CollectionMember<DeathRecord>> mapCollectionMember(@NotNull V1Death v1Death) {
        return Optional.of(v1Death)
            .map(this::mapDeathRecord)
            .map(d -> new CollectionMember<>(null, d));
    }

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
                .dateOfDeath(deceased.getDateOfDeath())
                .aliases(getAliases(deceased));
        }

        return builder.build();
    }

    @NotNull
    static List<Alias> getAliases(Deceased deceased) {
        final List<com.github.hmcts.lifeevents.client.model.Alias> aliases = deceased.getAliases();
        if (aliases == null || aliases.isEmpty()) {
            return Collections.emptyList();
        }

        return aliases.stream()
                .filter(Objects::nonNull)
                .map(alias -> Alias.builder()
                        .prefix(alias.getPrefix())
                        .forenames(alias.getForenames())
                        .lastName(alias.getSurname())
                        .type(alias.getType())
                        .suffix(alias.getSuffix())
                        .build())
                .collect(Collectors.toList());
    }
}
