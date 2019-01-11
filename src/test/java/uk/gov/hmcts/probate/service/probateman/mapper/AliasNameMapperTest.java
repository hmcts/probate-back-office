package uk.gov.hmcts.probate.service.probateman.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.probate.model.cases.AliasName;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AliasNameMapperTest {

    private static final String DECEASED_ALIAS_NAMES_FN = "DeadANFN1 DeadANFN2";
    private static final String DECEASED_ALIAS_NAMES_SN = "DeadANSN";
    private static final String DECEASED_ALIAS_NAMES = DECEASED_ALIAS_NAMES_FN + " " + DECEASED_ALIAS_NAMES_SN;

    @Autowired
    private AliasNameMapper aliasNameMapper;

    @Test
    public void shouldMapToCollection() {

        List<CollectionMember<AliasName>> aliasCollection = aliasNameMapper.toCollectionMember(DECEASED_ALIAS_NAMES);
        List<CollectionMember<AliasName>> expectedAliasNames = buildAliasNames();

        aliasCollection.forEach(alias -> assertThat(expectedAliasNames).contains(alias));

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