package uk.gov.hmcts.probate.service.probateman.mapper;

import org.junit.Before;
import org.junit.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.hmcts.probate.model.ccd.willlodgement.request.WillLodgementData;
import uk.gov.hmcts.probate.model.probateman.WillLodgement;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class WillLodgementMapperTest {

    private WillLodgementMapper willLodgementMapper;

    @Before
    public void willLodgementMapper() {
        willLodgementMapper = Mappers.getMapper(WillLodgementMapper.class);
    }

    @Test
    public void shouldMapToCcdData() {
        WillLodgement willLodgement = new WillLodgement();

        WillLodgementData willLodgementData = willLodgementMapper.toCcdData(willLodgement);

        assertThat(willLodgementData, is(notNullValue()));
    }

}