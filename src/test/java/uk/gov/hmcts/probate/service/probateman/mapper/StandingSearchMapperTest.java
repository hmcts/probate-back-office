package uk.gov.hmcts.probate.service.probateman.mapper;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.hmcts.probate.model.ccd.standingsearch.request.StandingSearchData;
import uk.gov.hmcts.probate.model.probateman.StandingSearch;

import java.time.LocalDate;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class StandingSearchMapperTest {
    private static final String DECEASED_FORENAMES = "DeadFN1 DeadFN2";
    private static final String DECEASED_SURNAME = "DeadSN";
    private static final LocalDate DATE_OF_BIRTH = LocalDate.of(1999, 1, 1);
    private static final LocalDate DATE_OF_DEATH = LocalDate.of(2018, 1, 1);

    private StandingSearchMapper standingSearchMapper;

    @Before
    public void setUp() {
        standingSearchMapper = Mappers.getMapper(StandingSearchMapper.class);
    }

    @Test
    public void shouldMapToCcdData() {
        StandingSearch standingSearch = new StandingSearch();
        standingSearch.setDeceasedForenames(DECEASED_FORENAMES);
        standingSearch.setDeceasedSurname(DECEASED_SURNAME);
        standingSearch.setDateOfBirth(DATE_OF_BIRTH);
        standingSearch.setDateOfDeath1(DATE_OF_DEATH);

        StandingSearchData expectedStandingSearchData = StandingSearchData.builder()
                .deceasedForenames(DECEASED_FORENAMES)
                .deceasedSurname(DECEASED_SURNAME)
                .deceasedDateOfBirth(DATE_OF_BIRTH)
                .deceasedDateOfDeath(DATE_OF_DEATH)
                .build();

        StandingSearchData standingSearchData = standingSearchMapper.toCcdData(standingSearch);

        Assertions.assertThat(standingSearchData).isEqualToComparingFieldByFieldRecursively(expectedStandingSearchData);
    }

}