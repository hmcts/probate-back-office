package uk.gov.hmcts.probate.service.probateman.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.model.probateman.Caveat;
import uk.gov.hmcts.probate.model.probateman.ProbateManType;
import uk.gov.hmcts.reform.probate.model.cases.Address;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.FullAliasName;
import uk.gov.hmcts.reform.probate.model.cases.caveat.CaveatData;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class CaveatMapperIT {

    @Value("${ccd.gateway.host}")
    private String printServiceHost;

    @Value("${printservice.legacyPath}")
    private String printServiceLegacyPath;

    private static final String ID = "12345";
    private static final String LEGACY_TYPE = "Legacy CAVEAT";
    private static final String DECEASED_FORENAMES = "DeadFN1 DeadFN2";
    private static final String DECEASED_SURNAME = "DeadSN";
    private static final String DECEASED_ALIAS_NAMES = "DeadAN1 DeadAN2";
    private static final String CAVEATOR_FORENAMES = "CavFN1 CavFN2";
    private static final String CAVEATOR_SURNAME = "CavSN";
    private static final LocalDate DECEASED_DOB = LocalDate.of(1999, 1, 1);
    private static final LocalDate DECEASED_DOD = LocalDate.of(2018, 1, 1);
    private static final LocalDate DATE_OF_ENTRY = LocalDate.of(2018, 1, 2);
    private static final String CAVEATOR_ADDRESS = "CavAddLn1 CavAddPC";
    private static final LocalDate CAVEATOR_EXPIRY_DATE = LocalDate.of(2020, 1, 1);

    @Autowired
    private CaveatMapper caveatMapper;

    @Test
    void shouldMapToCcdData() {
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
        caveat.setId(Long.valueOf(ID));
        caveat.setCaveatDateOfEntry(DATE_OF_ENTRY);

        Address deceasedAddress = Address.builder()
                .addressLine1(CAVEATOR_ADDRESS)
                .build();
        String legacyCaseViewUrl = String.format(printServiceHost + printServiceLegacyPath, ProbateManType.CAVEAT, ID);
        CaveatData expectedCaveatData = CaveatData.builder()
                .deceasedForenames(DECEASED_FORENAMES)
                .deceasedSurname(DECEASED_SURNAME)
                .caveatorForenames(CAVEATOR_FORENAMES)
                .caveatorSurname(CAVEATOR_SURNAME)
                .deceasedDateOfBirth(DECEASED_DOB)
                .deceasedDateOfDeath(DECEASED_DOD)
                .caveatorAddress(deceasedAddress)
                .expiryDate(CAVEATOR_EXPIRY_DATE)
                .deceasedAnyOtherNames(true)
                .deceasedFullAliasNameList(buildFullAliasNames())
                .legacyId(ID)
                .legacyType(LEGACY_TYPE)
                .legacyCaseViewUrl(legacyCaseViewUrl)
                .applicationSubmittedDate(DATE_OF_ENTRY)
                .build();

        CaveatData caveatData = caveatMapper.toCcdData(caveat);

        assertThat(caveatData).usingRecursiveComparison().isEqualTo(expectedCaveatData).comparingOnlyFields(
                "deceasedForenames",
                "deceasedSurname",
                "deceasedFullAliasNameList",
                "caveatorForenames",
                "caveatorSurname",
                "caveatorAddress",
                "expiryDate",
                "deceasedDateOfBirth",
                "deceasedDateOfDeath",
                "legacyId",
                "legacyType",
                "legacyCaseViewUrl"
        );
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
