package uk.gov.hmcts.probate.service.lifeevents;

import com.github.hmcts.lifeevents.client.model.Deceased;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.Alias;

import jakarta.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
public class AliasMapper {

    @NotNull
    public List<uk.gov.hmcts.reform.probate.model.cases.CollectionMember<Alias>> map(Deceased deceased) {
        final List<com.github.hmcts.lifeevents.client.model.Alias> aliases = deceased.getAliases();
        if (aliases == null || aliases.isEmpty()) {
            return Collections.emptyList();
        }

        return aliases.stream()
                .filter(Objects::nonNull)
                .filter(this::isNotEmpty)
                .map(alias -> Alias.builder()
                        .prefix(alias.getPrefix())
                        .forenames(alias.getForenames())
                        .lastName(alias.getSurname())
                        .type(alias.getType())
                        .suffix(alias.getSuffix())
                        .build())
                .map(a -> new uk.gov.hmcts.reform.probate.model.cases.CollectionMember<>(null, a))
                .toList();
    }

    private boolean isNotEmpty(com.github.hmcts.lifeevents.client.model.Alias alias) {
        return StringUtils.isNotEmpty(alias.getForenames()) || StringUtils.isNotEmpty(alias.getSurname());
    }
}
