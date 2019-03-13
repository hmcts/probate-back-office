package uk.gov.hmcts.probate.service.probateman;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.probate.insights.AppInsights;
import uk.gov.hmcts.probate.service.probateman.mapper.SolsAliasNameMapper;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.SolsAliasName;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SolsAliasNameMapperTest {

    private static final String DECEASED_ALIAS_NAME1 = "DeadANF1";
    private static final String DECEASED_ALIAS_NAME2 = "DeadANF2";
    private static final String DECEASED_ALIAS_NAMES = DECEASED_ALIAS_NAME1 + "," + DECEASED_ALIAS_NAME2;

    @Autowired
    private SolsAliasNameMapper aliasNameMapper;

    @MockBean
    AppInsights appInsights;

    @Test
    public void shouldMapToCollection() {

        List<CollectionMember<SolsAliasName>> aliasCollection = aliasNameMapper.toCollectionMember(DECEASED_ALIAS_NAMES);
        List<CollectionMember<SolsAliasName>> expectedAliasNames = buildAliasNames();

        aliasCollection.forEach(alias -> assertThat(expectedAliasNames).contains(alias));

    }

    private List<CollectionMember<SolsAliasName>> buildAliasNames() {
        SolsAliasName aliasName1 = SolsAliasName.builder()
                .solsAliasname(DECEASED_ALIAS_NAMES)
                .build();
        List<CollectionMember<SolsAliasName>> aliasNamesCollections = new ArrayList<CollectionMember<SolsAliasName>>();
        CollectionMember<SolsAliasName> aliasNamesCollection1 = new CollectionMember(null, aliasName1);
        aliasNamesCollections.add(aliasNamesCollection1);
        return aliasNamesCollections;
    }
}