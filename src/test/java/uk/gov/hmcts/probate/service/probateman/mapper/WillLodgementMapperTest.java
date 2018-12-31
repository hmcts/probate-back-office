package uk.gov.hmcts.probate.service.probateman.mapper;

import org.junit.Before;
import org.junit.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.hmcts.probate.model.ccd.willlodgement.request.WillLodgementData;
import uk.gov.hmcts.probate.model.probateman.WillLodgement;

import static org.assertj.core.api.Assertions.assertThat;

public class WillLodgementMapperTest {

    public static final String RK_NUMBER = "234342";
    private WillLodgementMapper willLodgementMapper;

    @Before
    public void willLodgementMapper() {
        willLodgementMapper = Mappers.getMapper(WillLodgementMapper.class);
    }

    @Test
    public void shouldMapToCcdData() {
        WillLodgement willLodgement = new WillLodgement();
        willLodgement.setRkNumber(RK_NUMBER);

        WillLodgementData expectedWillLodgementData = WillLodgementData.builder()
            .wlApplicantReferenceNumber(RK_NUMBER)
            .build();

        WillLodgementData willLodgementData = willLodgementMapper.toCcdData(willLodgement);

        assertThat(willLodgementData).isEqualToComparingFieldByFieldRecursively(expectedWillLodgementData);
    }
}
