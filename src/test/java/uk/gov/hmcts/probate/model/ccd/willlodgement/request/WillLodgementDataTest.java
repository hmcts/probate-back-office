package uk.gov.hmcts.probate.model.ccd.willlodgement.request;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;

import static org.junit.Assert.assertEquals;

public class WillLodgementDataTest {

    private static final String WL_DECEASED_FORENAMES = "Forenames";
    private static final String WL_DECEASED_SURNAME = "Surname";
    private static final String WL_EXECUTOR_FORENAMES = "fName";
    private static final String WL_EXECUTOR_SURNAME = "sName";

    @InjectMocks
    private WillLodgementData underTest;

    @Before
    public void setup() {

        underTest = WillLodgementData.builder()
                .deceasedForenames(WL_DECEASED_FORENAMES)
                .deceasedSurname(WL_DECEASED_SURNAME)
                .executorForenames(WL_EXECUTOR_FORENAMES)
                .executorSurname(WL_EXECUTOR_SURNAME)
                .build();
    }

    @Test
    public void shouldReturnDeceasedFullNameForWillLodgement() {
        final WillLodgementData willLodgementData = WillLodgementData.builder()
                .deceasedForenames(WL_DECEASED_FORENAMES)
                .deceasedSurname(WL_DECEASED_SURNAME)
                .build();

        assertEquals(WL_DECEASED_FORENAMES + " " + WL_DECEASED_SURNAME, willLodgementData.getDeceasedFullName());
    }

    @Test
    public void shouldReturnCaveatorFullNameForWillLodgement() {
        final WillLodgementData willLodgementData = WillLodgementData.builder()
                .executorForenames(WL_EXECUTOR_FORENAMES)
                .executorSurname(WL_EXECUTOR_SURNAME)
                .build();

        assertEquals(WL_EXECUTOR_FORENAMES + " " + WL_EXECUTOR_SURNAME, willLodgementData.getExecutorFullName());
    }
}
