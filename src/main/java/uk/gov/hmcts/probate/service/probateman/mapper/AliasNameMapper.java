package uk.gov.hmcts.probate.service.probateman.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.service.probateman.mapper.qualifiers.ToAliasNameMember;
import uk.gov.hmcts.reform.probate.model.cases.AliasName;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class AliasNameMapper {

    @SuppressWarnings("squid:S1168")
    @ToAliasNameMember
    public List<CollectionMember<AliasName>> toCollectionMember(String aliasNames) {
        log.info("Adding aliasNames to collection for legacy case mapping");
        if (aliasNames == null) {
            return null;
        }
        List<CollectionMember<AliasName>> collectionMemberArrayList = new ArrayList<>();
        String lastName = aliasNames.substring(aliasNames.lastIndexOf(' ') + 1);
        String forenames = aliasNames.substring(0, aliasNames.lastIndexOf(' '));
        AliasName aliasName = AliasName.builder()
                .forenames(forenames)
                .lastName(lastName)
                .build();
        CollectionMember<AliasName> collectionMember = new CollectionMember(null, aliasName);
        collectionMemberArrayList.add(collectionMember);
        return collectionMemberArrayList;
    }

}
