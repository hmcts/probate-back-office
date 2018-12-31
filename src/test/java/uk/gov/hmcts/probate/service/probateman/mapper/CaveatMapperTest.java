package uk.gov.hmcts.probate.service.probateman.mapper;

import org.junit.Before;
import org.junit.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.probateman.Caveat;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class CaveatMapperTest {

    private CaveatMapper caveatMapper;

    @Before
    public void setUp() {
        caveatMapper = Mappers.getMapper(CaveatMapper.class);
    }

    @Test
    public void shouldMapToCcdData() {
        Caveat caveat = new Caveat();

        CaveatData caveatData = caveatMapper.toCcdData(caveat);

        assertThat(caveatData, is(notNullValue()));
    }

}