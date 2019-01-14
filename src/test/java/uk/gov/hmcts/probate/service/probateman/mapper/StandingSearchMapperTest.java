package uk.gov.hmcts.probate.service.probateman.mapper;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.probate.insights.AppInsights;
import uk.gov.hmcts.probate.model.probateman.StandingSearch;
import uk.gov.hmcts.reform.probate.model.cases.Address;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.FullAliasName;
import uk.gov.hmcts.reform.probate.model.cases.standingsearch.StandingSearchData;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StandingSearchMapperTest {
    private static final String DECEASED_FORENAMES = "DeadFN1 DeadFN2";
    private static final String DECEASED_SURNAME = "DeadSN";
    private static final LocalDate DATE_OF_BIRTH = LocalDate.of(1999, 1, 1);
    private static final LocalDate DATE_OF_DEATH = LocalDate.of(2018, 1, 1);
    private static final String DECEASED_ALIAS_NAMES = "DeadAN1 DeadAN2";
    private static final String DECEASED_ADRESS = "DeadAddLN1 DeadAddPC";
    private static final String APPLICANT_FORENAMES = "AppFN1 AppFN2";
    private static final String APPLICANT_SURNAME = "AppSN";
    private static final String APPLICANT_ADRESS = "AppAddLN1 AppAddPC";
    private static final LocalDate DATE_OF_EXPIRY = LocalDate.of(2020, 1, 1);

    @Autowired
    private StandingSearchMapper standingSearchMapper;

    @MockBean
    AppInsights appInsights;

    @Test
    public void shouldMapToCcdData() {
        StandingSearch standingSearch = new StandingSearch();
        standingSearch.setDeceasedForenames(DECEASED_FORENAMES);
        standingSearch.setDeceasedSurname(DECEASED_SURNAME);
        standingSearch.setDateOfBirth(DATE_OF_BIRTH);
        standingSearch.setDateOfDeath1(DATE_OF_DEATH);
        standingSearch.setAliasNames(DECEASED_ALIAS_NAMES);
        standingSearch.setDeceasedAddress(DECEASED_ADRESS);
        standingSearch.setSsApplicantForename(APPLICANT_FORENAMES);
        standingSearch.setSsApplicantSurname(APPLICANT_SURNAME);
        standingSearch.setApplicantAddress(APPLICANT_ADRESS);
        standingSearch.setSsDateOfExpiry(DATE_OF_EXPIRY);

        StandingSearchData expectedStandingSearchData = StandingSearchData.builder()
                .deceasedForenames(DECEASED_FORENAMES)
                .deceasedSurname(DECEASED_SURNAME)
                .deceasedDateOfBirth(DATE_OF_BIRTH)
                .deceasedDateOfDeath(DATE_OF_DEATH)
                .deceasedFullAliasNameList(buildFullAliasNames())
                .deceasedAddress(Address.builder().addressLine1(DECEASED_ADRESS).build())
                .applicantForenames(APPLICANT_FORENAMES)
                .applicantSurname(APPLICANT_SURNAME)
                .applicantAddress(Address.builder().addressLine1(APPLICANT_ADRESS).build())
                .expiryDate(DATE_OF_EXPIRY)
                .build();

        StandingSearchData standingSearchData = standingSearchMapper.toCcdData(standingSearch);

        Assertions.assertThat(standingSearchData).isEqualToComparingFieldByFieldRecursively(expectedStandingSearchData);
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