package uk.gov.hmcts.probate.service.probateman.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.service.probateman.mapper.qualifiers.ToSolsAliasNameMember;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.SolsAliasName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class SolsAliasNameMapper {

    @ToSolsAliasNameMember
    public List<CollectionMember<SolsAliasName>> toCollectionMember(String aliasNames) {
        log.info("Adding aliasNames to collection for legacy case mapping");
        if (aliasNames == null) {
            Collections.emptyList();
        }
        List<CollectionMember<SolsAliasName>> collectionMemberArrayList = new ArrayList<>();
        SolsAliasName aliasName = SolsAliasName.builder()
                .solsAliasname(aliasNames)
                .build();
        CollectionMember<SolsAliasName> collectionMember = new CollectionMember(null, aliasName);
        collectionMemberArrayList.add(collectionMember);
        return collectionMemberArrayList;
    }

}
