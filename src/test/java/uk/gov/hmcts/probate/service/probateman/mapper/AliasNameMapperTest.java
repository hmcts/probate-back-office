package uk.gov.hmcts.probate.service.probateman.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.probate.insights.AppInsights;
import uk.gov.hmcts.reform.probate.model.cases.AliasName;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AliasNameMapperTest {

    private static final String DECEASED_ALIAS_NAMES_FN = "DeadANFN1 DeadANFN2";
    private static final String DECEASED_ALIAS_NAMES_SN = "DeadANSN";
    private static final String DECEASED_ALIAS_NAMES = DECEASED_ALIAS_NAMES_FN + " " + DECEASED_ALIAS_NAMES_SN;

    @Autowired
    private AliasNameMapper aliasNameMapper;

    @MockBean
    AppInsights appInsights;

    @Test
    public void shouldMapToCollection() {

        List<CollectionMember<AliasName>> aliasCollection = aliasNameMapper.toCollectionMember(DECEASED_ALIAS_NAMES);
        List<CollectionMember<AliasName>> expectedAliasNames = buildAliasNames();

        aliasCollection.forEach(alias -> assertThat(expectedAliasNames).contains(alias));

    }

    @Test
    public void shouldMapEmptyNamesToEmptyCollection() {

        List<CollectionMember<AliasName>> expectedAliasNames = Collections.emptyList();
        List<CollectionMember<AliasName>> aliasCollection = aliasNameMapper.toCollectionMember(null);

        assertEquals(expectedAliasNames, aliasCollection);

    }

    @Test
    public void shouldMapToCollectionOneAliasOnly() {

        List<CollectionMember<AliasName>> aliasCollection = aliasNameMapper.toCollectionMember(DECEASED_ALIAS_NAMES);

        assertEquals(DECEASED_ALIAS_NAMES_FN, aliasCollection.get(0).getValue().getForenames());
        assertEquals(DECEASED_ALIAS_NAMES_SN, aliasCollection.get(0).getValue().getLastName());
        assertEquals(1, aliasCollection.size());
    }


    private List<CollectionMember<AliasName>> buildAliasNames() {
        AliasName aliasName = AliasName.builder()
                .forenames(DECEASED_ALIAS_NAMES_FN)
                .lastName(DECEASED_ALIAS_NAMES_SN)
                .build();
        List<CollectionMember<AliasName>> aliasNamesCollections = new ArrayList<CollectionMember<AliasName>>();
        CollectionMember<AliasName> aliasNamesCollection = new CollectionMember(null, aliasName);
        aliasNamesCollections.add(aliasNamesCollection);
        return aliasNamesCollections;
    }

}