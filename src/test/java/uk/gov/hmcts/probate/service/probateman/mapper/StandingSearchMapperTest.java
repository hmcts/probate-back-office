package uk.gov.hmcts.probate.service.probateman.mapper;

import org.junit.Before;
import org.junit.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.hmcts.probate.model.ccd.standingsearch.request.StandingSearchData;
import uk.gov.hmcts.probate.model.probateman.StandingSearch;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class StandingSearchMapperTest {

    private StandingSearchMapper standingSearchMapper;

    @Before
    public void setUp() {
        standingSearchMapper = Mappers.getMapper(StandingSearchMapper.class);
    }

    @Test
    public void shouldMapToCcdData() {
        StandingSearch standingSearch = new StandingSearch();

        StandingSearchData standingSearchData = standingSearchMapper.toCcdData(standingSearch);

        assertThat(standingSearchData, is(notNullValue()));
    }

}