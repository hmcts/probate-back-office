package uk.gov.hmcts.probate.service.probateman.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.SolsAliasName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class SolsAliasNameMapperIT {

    private static final String DECEASED_ALIAS_NAME1 = "DeadANF1";
    private static final String DECEASED_ALIAS_NAME2 = "DeadANF2";
    private static final String DECEASED_ALIAS_NAMES = DECEASED_ALIAS_NAME1 + "|" + DECEASED_ALIAS_NAME2;
    @Autowired
    private SolsAliasNameMapper aliasNameMapper;

    @Test
    void shouldMapToCollection() {

        List<CollectionMember<SolsAliasName>> aliasCollection =
            aliasNameMapper.toCollectionMember(DECEASED_ALIAS_NAMES);
        List<CollectionMember<SolsAliasName>> expectedAliasNames = buildAliasNames();

        aliasCollection.forEach(alias -> assertThat(expectedAliasNames).contains(alias));

    }

    @Test
    void shouldMapEmptyNamesToEmptyCollection() {

        List<CollectionMember<SolsAliasName>> expectedAliasNames = Collections.emptyList();
        List<CollectionMember<SolsAliasName>> aliasCollection = aliasNameMapper.toCollectionMember(null);

        assertEquals(expectedAliasNames, aliasCollection);

    }

    @Test
    void shouldMapToCollectionOneAliasOnly() {

        List<CollectionMember<SolsAliasName>> aliasCollection =
            aliasNameMapper.toCollectionMember(DECEASED_ALIAS_NAME1);

        assertEquals(DECEASED_ALIAS_NAME1, aliasCollection.get(0).getValue().getSolsAliasname());
        assertEquals(1, aliasCollection.size());
    }

    private List<CollectionMember<SolsAliasName>> buildAliasNames() {
        SolsAliasName aliasName1 = SolsAliasName.builder()
            .solsAliasname(DECEASED_ALIAS_NAME1)
            .build();
        SolsAliasName aliasName2 = SolsAliasName.builder()
            .solsAliasname(DECEASED_ALIAS_NAME2)
            .build();
        List<CollectionMember<SolsAliasName>> aliasNamesCollections = new ArrayList<CollectionMember<SolsAliasName>>();
        CollectionMember<SolsAliasName> aliasNamesCollection1 = new CollectionMember(null, aliasName1);
        aliasNamesCollections.add(aliasNamesCollection1);
        CollectionMember<SolsAliasName> aliasNamesCollection2 = new CollectionMember(null, aliasName2);
        aliasNamesCollections.add(aliasNamesCollection2);
        return aliasNamesCollections;
    }
}
