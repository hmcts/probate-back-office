package uk.gov.hmcts.probate.service.probateman.mapper;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.service.probateman.mapper.qualifiers.ToFullAliasNameMember;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.FullAliasName;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class FullAliasNameMapper {

    @ToFullAliasNameMember
    public List<CollectionMember<FullAliasName>> toFullAliasNameMember(String aliasNames) {
        log.info("Adding FullAliasNames to collection for legacy case mapping");
        if (aliasNames == null) {
            return null;
        }
        List<CollectionMember<FullAliasName>> collectionMemberArrayList = new ArrayList<CollectionMember<FullAliasName>>();
        String[] names = StringUtils.split(aliasNames, ",");
        for (String name : names) {
            collectionMemberArrayList.add(buildAliasName(name, collectionMemberArrayList));
        }

        return collectionMemberArrayList;
    }

    private CollectionMember buildAliasName(String aliasNames, List<CollectionMember<FullAliasName>> collectionMemberArrayList) {
        FullAliasName aliasName = FullAliasName.builder()
                .fullAliasName(aliasNames)
                .build();
        return new CollectionMember(null, aliasName);
    }

}
