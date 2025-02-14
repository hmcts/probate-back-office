package uk.gov.hmcts.probate.service.probateman.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.FullAliasName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class FullAliasNameMapperIT {

    private static final String DECEASED_ALIAS_NAME1 = "DeadANF1";
    private static final String DECEASED_ALIAS_NAME2 = "DeadANF2";
    private static final String DECEASED_ALIAS_NAMES = DECEASED_ALIAS_NAME1 + "|" + DECEASED_ALIAS_NAME2;
    @Autowired
    private FullAliasNameMapper aliasNameMapper;

    @Test
    void shouldMapToCollection() {

        List<CollectionMember<FullAliasName>> aliasCollection =
            aliasNameMapper.toFullAliasNameMember(DECEASED_ALIAS_NAMES);
        List<CollectionMember<FullAliasName>> expectedAliasNames = buildAliasNames();

        aliasCollection.forEach(alias -> assertThat(expectedAliasNames).contains(alias));

    }

    @Test
    void shouldMapEmptyNamesToEmptyCollection() {

        List<CollectionMember<FullAliasName>> expectedAliasNames = Collections.emptyList();
        List<CollectionMember<FullAliasName>> aliasCollection = aliasNameMapper.toFullAliasNameMember(null);

        assertEquals(expectedAliasNames, aliasCollection);

    }

    @Test
    void shouldMapToCollectionOneAliasOnly() {

        List<CollectionMember<FullAliasName>> aliasCollection =
            aliasNameMapper.toFullAliasNameMember(DECEASED_ALIAS_NAME1);

        assertEquals(DECEASED_ALIAS_NAME1, aliasCollection.get(0).getValue().getFullAliasName());
        assertEquals(1, aliasCollection.size());
    }

    private List<CollectionMember<FullAliasName>> buildAliasNames() {
        FullAliasName aliasName1 = FullAliasName.builder()
            .fullAliasName(DECEASED_ALIAS_NAME1)
            .build();
        FullAliasName aliasName2 = FullAliasName.builder()
            .fullAliasName(DECEASED_ALIAS_NAME2)
            .build();
        List<CollectionMember<FullAliasName>> aliasNamesCollections = new ArrayList<CollectionMember<FullAliasName>>();
        CollectionMember<FullAliasName> aliasNamesCollection1 = new CollectionMember(null, aliasName1);
        aliasNamesCollections.add(aliasNamesCollection1);
        CollectionMember<FullAliasName> aliasNamesCollection2 = new CollectionMember(null, aliasName2);
        aliasNamesCollections.add(aliasNamesCollection2);
        return aliasNamesCollections;
    }
}
