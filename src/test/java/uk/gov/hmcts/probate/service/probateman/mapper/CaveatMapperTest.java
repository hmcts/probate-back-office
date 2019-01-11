package uk.gov.hmcts.probate.service.probateman.mapper;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.probate.model.ccd.ProbateAddress;
import uk.gov.hmcts.probate.model.ccd.ProbateFullAliasName;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.probateman.Caveat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CaveatMapperTest {
    private static final String DECEASED_FORENAMES = "DeadFN1 DeadFN2";
    private static final String DECEASED_SURNAME = "DeadSN";
    private static final String DECEASED_ALIAS_NAMES = "DeadAN1 DeadAN2";
    private static final String CAVEATOR_FORENAMES = "CavFN1 CavFN2";
    private static final String CAVEATOR_SURNAME = "CavSN";
    private static final LocalDate DECEASED_DOB = LocalDate.of(1999, 1, 1);
    private static final LocalDate DECEASED_DOD = LocalDate.of(2018, 1, 1);
    private static final String CAVEATOR_ADDRESS = "CavAddLn1 CavAddPC";
    private static final LocalDate CAVEATOR_EXPIRY_DATE = LocalDate.of(2020, 1, 1);

    @Autowired
    private CaveatMapper caveatMapper;

    @Test
    public void shouldMapToCcdData() {
        Caveat caveat = new Caveat();
        caveat.setDeceasedForenames(DECEASED_FORENAMES);
        caveat.setDeceasedSurname(DECEASED_SURNAME);
        caveat.setCaveatorForenames(CAVEATOR_FORENAMES);
        caveat.setCaveatorSurname(CAVEATOR_SURNAME);
        caveat.setDateOfBirth(DECEASED_DOB);
        caveat.setDateOfDeath(DECEASED_DOD);
        caveat.setCavServiceAddress(CAVEATOR_ADDRESS);
        caveat.setAliasNames(DECEASED_ALIAS_NAMES);
        caveat.setCavExpiryDate(CAVEATOR_EXPIRY_DATE);

        ProbateAddress deceasedAddress = ProbateAddress.builder()
                .proAddressLine1(CAVEATOR_ADDRESS)
                .build();
        CaveatData expectedCaveatData = CaveatData.builder()
                .deceasedForenames(DECEASED_FORENAMES)
                .deceasedSurname(DECEASED_SURNAME)
                .caveatorForenames(CAVEATOR_FORENAMES)
                .caveatorSurname(CAVEATOR_SURNAME)
                .deceasedDateOfBirth(DECEASED_DOB)
                .deceasedDateOfDeath(DECEASED_DOD)
                .deceasedAddress(deceasedAddress)
                .expiryDate(CAVEATOR_EXPIRY_DATE)
                .deceasedFullAliasNameList(buildFullAliasNames())
                .build();

        CaveatData caveatData = caveatMapper.toCcdData(caveat);

        Assertions.assertThat(caveatData).isEqualToComparingOnlyGivenFields(expectedCaveatData,
                "deceasedForenames",
                "deceasedSurname",
                "deceasedFullAliasNameList",
                "caveatorForenames",
                "caveatorSurname",
                "deceasedAddress",
                "expiryDate",
                "deceasedDateOfBirth",
                "deceasedDateOfDeath");
    }

    private List<CollectionMember<ProbateFullAliasName>> buildFullAliasNames() {
        ProbateFullAliasName aliasName = ProbateFullAliasName.builder()
                .fullAliasName(DECEASED_ALIAS_NAMES)
                .build();
        List<CollectionMember<ProbateFullAliasName>> aliasNamesCollections = new ArrayList<CollectionMember<ProbateFullAliasName>>();
        CollectionMember<ProbateFullAliasName> aliasNamesCollection = new CollectionMember(null, aliasName);
        aliasNamesCollections.add(aliasNamesCollection);

        return aliasNamesCollections;
    }

}