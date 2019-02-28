package uk.gov.hmcts.probate.service.probateman.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.probate.insights.AppInsights;
import uk.gov.hmcts.probate.model.probateman.ProbateManType;
import uk.gov.hmcts.probate.model.probateman.WillLodgement;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.FullAliasName;
import uk.gov.hmcts.reform.probate.model.cases.willlodgement.WillLodgementData;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WillLodgementMapperTest {

    @Value("${ccd.gateway.host}")
    private String printServiceHost;

    @Value("${printservice.legacyPath}")
    private String printServiceLegacyPath;

    private static final String ID = "12345";
    private static final String LEGACY_TYPE = "Legacy WILL";
    private static final String DECEASED_ALIAS_NAMES = "DeadAN1 DeadAN2";
    private static final String FORENAMES = "WLFN1 WLFN2";
    private static final String SURNAME = "WLSN";
    private static final LocalDate DATE_OF_BIRTH = LocalDate.of(1999, 1, 1);
    private static final LocalDate DATE_OF_DEATH = LocalDate.of(2018, 1, 1);

    @Autowired
    private WillLodgementMapper willLodgementMapper;

    @MockBean
    AppInsights appInsights;

    @Test
    public void shouldMapToCcdData() {
        WillLodgement willLodgement = new WillLodgement();
        willLodgement.setAliasNames(DECEASED_ALIAS_NAMES);
        willLodgement.setDeceasedForenames(FORENAMES);
        willLodgement.setDeceasedSurname(SURNAME);
        willLodgement.setDateOfBirth(DATE_OF_BIRTH);
        willLodgement.setDateOfDeath1(DATE_OF_DEATH);
        willLodgement.setId(Long.valueOf(ID));

        String legacyCaseViewUrl = String.format(printServiceHost + printServiceLegacyPath, ProbateManType.WILL_LODGEMENT, ID);
        WillLodgementData expectedWillLodgementData = WillLodgementData.builder()
                .deceasedFullAliasNameList(buildFullAliasNames())
                .deceasedForenames(FORENAMES)
                .deceasedSurname(SURNAME)
                .deceasedDateOfBirth(DATE_OF_BIRTH)
                .deceasedDateOfDeath(DATE_OF_DEATH)
                .legacyId(ID)
                .legacyType(LEGACY_TYPE)
                .legacyCaseViewUrl(legacyCaseViewUrl)
                .build();

        WillLodgementData willLodgementData = willLodgementMapper.toCcdData(willLodgement);

        assertThat(willLodgementData).isEqualToComparingFieldByFieldRecursively(expectedWillLodgementData);
    }

    private List<CollectionMember<FullAliasName>> buildFullAliasNames() {
        FullAliasName aliasName = FullAliasName.builder()
                .fullAliasName(DECEASED_ALIAS_NAMES)
                .build();
        List<CollectionMember<FullAliasName>> aliasNamesCollections = new ArrayList<CollectionMember<FullAliasName>>();
        CollectionMember<FullAliasName> aliasNamesCollection = new CollectionMember(null, aliasName);
        aliasNamesCollections.add(aliasNamesCollection);

        return aliasNamesCollections;
    }

}
