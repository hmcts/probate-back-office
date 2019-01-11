package uk.gov.hmcts.probate.service.probateman.mapper;

import org.junit.Before;
import org.junit.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.hmcts.probate.model.ccd.willlodgement.request.WillLodgementData;
import uk.gov.hmcts.probate.model.probateman.WillLodgement;

import static org.assertj.core.api.Assertions.assertThat;

public class WillLodgementMapperTest {

    public static final String RK_NUMBER = "234342";
    public static final String FORENAMES = "WLFN1 WLFN2";
    public static final String SURNAME = "WLSN";
    private WillLodgementMapper willLodgementMapper;

    @Before
    public void willLodgementMapper() {
        willLodgementMapper = Mappers.getMapper(WillLodgementMapper.class);
    }

    @Test
    public void shouldMapToCcdData() {
        WillLodgement willLodgement = new WillLodgement();
        willLodgement.setRkNumber(RK_NUMBER);
        willLodgement.setDeceasedForenames(FORENAMES);
        willLodgement.setDeceasedSurname(SURNAME);

        WillLodgementData expectedWillLodgementData = WillLodgementData.builder()
                .wlApplicantReferenceNumber(RK_NUMBER)
                .wlApplicantForenames(FORENAMES)
                .wlApplicantSurname(SURNAME)
                .build();

        WillLodgementData willLodgementData = willLodgementMapper.toCcdData(willLodgement);

        assertThat(willLodgementData).isEqualToComparingFieldByFieldRecursively(expectedWillLodgementData);
    }
}
