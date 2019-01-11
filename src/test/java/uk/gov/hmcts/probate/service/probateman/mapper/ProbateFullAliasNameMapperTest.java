package uk.gov.hmcts.probate.service.probateman.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.probate.model.ccd.ProbateFullAliasName;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProbateFullAliasNameMapperTest {

    private static final String DECEASED_ALIAS_NAME1 = "DeadANF1";
    private static final String DECEASED_ALIAS_NAME2 = "DeadANF2";
    private static final String DECEASED_ALIAS_NAMES = DECEASED_ALIAS_NAME1 + "," + DECEASED_ALIAS_NAME2;

    @Autowired
    private ProbateFullAliasNameMapper aliasNameMapper;

    @Test
    public void shouldMapToCollection() {

        List<CollectionMember<ProbateFullAliasName>> aliasCollection = aliasNameMapper.toFullAliasNameMember(DECEASED_ALIAS_NAMES);
        List<CollectionMember<ProbateFullAliasName>> expectedAliasNames = buildAliasNames();

        aliasCollection.forEach(alias -> assertThat(expectedAliasNames).contains(alias));

    }

    private List<CollectionMember<ProbateFullAliasName>> buildAliasNames() {
        ProbateFullAliasName aliasName1 = ProbateFullAliasName.builder()
                .fullAliasName(DECEASED_ALIAS_NAME1)
                .build();
        ProbateFullAliasName aliasName2 = ProbateFullAliasName.builder()
                .fullAliasName(DECEASED_ALIAS_NAME2)
                .build();
        List<CollectionMember<ProbateFullAliasName>> aliasNamesCollections = new ArrayList<CollectionMember<ProbateFullAliasName>>();
        CollectionMember<ProbateFullAliasName> aliasNamesCollection1 = new CollectionMember(null, aliasName1);
        aliasNamesCollections.add(aliasNamesCollection1);
        CollectionMember<ProbateFullAliasName> aliasNamesCollection2 = new CollectionMember(null, aliasName2);
        aliasNamesCollections.add(aliasNamesCollection2);
        return aliasNamesCollections;
    }
}