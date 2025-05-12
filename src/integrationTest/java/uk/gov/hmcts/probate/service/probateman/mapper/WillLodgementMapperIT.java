package uk.gov.hmcts.probate.service.probateman.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.model.probateman.ProbateManType;
import uk.gov.hmcts.probate.model.probateman.WillLodgement;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.FullAliasName;
import uk.gov.hmcts.reform.probate.model.cases.willlodgement.WillLodgementData;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class WillLodgementMapperIT {

    private static final String ID = "12345";
    private static final String LEGACY_TYPE = "Legacy WILL";
    private static final String DECEASED_ALIAS_NAMES = "DeadAN1 DeadAN2";
    private static final String FORENAMES = "WLFN1 WLFN2";
    private static final String SURNAME = "WLSN";
    private static final LocalDate DATE_OF_BIRTH = LocalDate.of(1999, 1, 1);
    private static final LocalDate DATE_OF_DEATH = LocalDate.of(2018, 1, 1);
    @Value("${ccd.gateway.host}")
    private String printServiceHost;
    @Value("${printservice.legacyPath}")
    private String printServiceLegacyPath;
    @Autowired
    private WillLodgementMapper willLodgementMapper;

    @Test
    void shouldMapToCcdData() {
        WillLodgement willLodgement = new WillLodgement();
        willLodgement.setAliasNames(DECEASED_ALIAS_NAMES);
        willLodgement.setDeceasedForenames(FORENAMES);
        willLodgement.setDeceasedSurname(SURNAME);
        willLodgement.setDateOfBirth(DATE_OF_BIRTH);
        willLodgement.setDateOfDeath1(DATE_OF_DEATH);
        willLodgement.setId(Long.valueOf(ID));

        String legacyCaseViewUrl =
            String.format(printServiceHost + printServiceLegacyPath, ProbateManType.WILL_LODGEMENT, ID);
        WillLodgementData expectedWillLodgementData = WillLodgementData.builder()
            .deceasedFullAliasNameList(buildFullAliasNames())
            .deceasedAnyOtherNames(true)
            .deceasedForenames(FORENAMES)
            .deceasedSurname(SURNAME)
            .deceasedDateOfBirth(DATE_OF_BIRTH)
            .deceasedDateOfDeath(DATE_OF_DEATH)
            .legacyId(ID)
            .legacyType(LEGACY_TYPE)
            .legacyCaseViewUrl(legacyCaseViewUrl)
            .build();

        WillLodgementData willLodgementData = willLodgementMapper.toCcdData(willLodgement);

        assertThat(willLodgementData).usingRecursiveComparison().isEqualTo(expectedWillLodgementData);
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
