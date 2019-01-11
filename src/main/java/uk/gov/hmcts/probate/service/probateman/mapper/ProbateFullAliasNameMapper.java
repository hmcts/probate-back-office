package uk.gov.hmcts.probate.service.probateman.mapper;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.ProbateFullAliasName;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.service.probateman.mapper.qualifiers.ToFullAliasNameMember;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ProbateFullAliasNameMapper {

    @ToFullAliasNameMember
    public List<CollectionMember<ProbateFullAliasName>> toFullAliasNameMember(String aliasNames) {
        log.info("Adding probateFullAliasNames to collection for legacy case mapping");
        if (aliasNames == null) {
            return null;
        }
        List<CollectionMember<ProbateFullAliasName>> collectionMemberArrayList = new ArrayList<CollectionMember<ProbateFullAliasName>>();
        String[] names = StringUtils.split(aliasNames, ",");
        for (String name : names) {
            collectionMemberArrayList.add(buildAliasName(name, collectionMemberArrayList));
        }

        return collectionMemberArrayList;
    }

    private CollectionMember buildAliasName(String aliasNames, List<CollectionMember<ProbateFullAliasName>> collectionMemberArrayList) {
        ProbateFullAliasName aliasName = ProbateFullAliasName.builder()
                .fullAliasName(aliasNames)
                .build();
        return new CollectionMember(null, aliasName);
    }

}
