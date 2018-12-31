package uk.gov.hmcts.probate.service.probateman.mapper;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.probateman.Caveat;

import java.time.LocalDate;

public class CaveatMapperTest {
    private static final String DECEASED_FORENAMES = "DeadFN1 DeadFN2";
    private static final String DECEASED_SURNAME = "DeadSN";
    private static final String CAVEATOR_FORENAMES = "CavFN1 CavFN2";
    private static final String CAVEATOR_SURNAME = "CavSN";
    private static final LocalDate DECEASED_DOB = LocalDate.of(1999, 1, 1);
    private static final LocalDate DECEASED_DOD = LocalDate.of(2018, 1, 1);
    private static final String CAVEATOR_ADDRESS = "CavAddLn1 CavAddPC";

    private CaveatMapper caveatMapper;

    @Before
    public void setUp() {
        caveatMapper = Mappers.getMapper(CaveatMapper.class);
    }

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

        CaveatData expectedCaveatData = CaveatData.builder()
                .deceasedForenames(DECEASED_FORENAMES)
                .deceasedSurname(DECEASED_SURNAME)
                .caveatorForenames(CAVEATOR_FORENAMES)
                .caveatorSurname(CAVEATOR_SURNAME)
                .deceasedDateOfBirth(DECEASED_DOB)
                .deceasedDateOfDeath(DECEASED_DOD)
                .build();

        CaveatData caveatData = caveatMapper.toCcdData(caveat);

        Assertions.assertThat(caveatData).isEqualToComparingOnlyGivenFields(expectedCaveatData,
                "deceasedForenames",
                "deceasedSurname",
                "caveatorForenames",
                "caveatorSurname",
                "deceasedDateOfBirth",
                "deceasedDateOfDeath");
    }

}