package uk.gov.hmcts.probate.model.ccd.caveat.request;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;

import static org.junit.Assert.assertEquals;

public class CaveatDataTest {

    private static final String CAV_DECEASED_FORENAMES = "Forenames";
    private static final String CAV_DECEASED_SURNAME = "Surname";
    private static final String CAV_CAVEATOR_FORENAMES = "fName";
    private static final String CAV_CAVEATOR_SURNAME = "sName";
    private static final String CAV_CAVEATOR_EMAIL = "caveator@test.com";

    @InjectMocks
    private CaveatData underTest;

    @Before
    public void setup() {

        underTest = CaveatData.builder()
                .deceasedForenames(CAV_DECEASED_FORENAMES)
                .deceasedSurname(CAV_DECEASED_SURNAME)
                .caveatorForenames(CAV_CAVEATOR_FORENAMES)
                .caveatorSurname(CAV_CAVEATOR_SURNAME)
                .build();
    }

    @Test
    public void shouldReturnDeceasedFullNameForCaveat() {
        final CaveatData caveatData = CaveatData.builder()
                .deceasedForenames(CAV_DECEASED_FORENAMES)
                .deceasedSurname(CAV_DECEASED_SURNAME)
                .build();

        assertEquals(CAV_DECEASED_FORENAMES + " " + CAV_DECEASED_SURNAME, caveatData.getDeceasedFullName());
    }

    @Test
    public void shouldReturnCaveatorFullNameForCaveat() {
        final CaveatData caveatData = CaveatData.builder()
                .caveatorForenames(CAV_CAVEATOR_FORENAMES)
                .caveatorSurname(CAV_CAVEATOR_SURNAME)
                .build();

        assertEquals(CAV_CAVEATOR_FORENAMES + " " + CAV_CAVEATOR_SURNAME, caveatData.getCaveatorFullName());
    }

    @Test
    public void shouldReturnDefaultEmailNotificationCaveat() {
        final CaveatData caveatData = CaveatData.builder()
                .caveatorEmailAddress(CAV_CAVEATOR_EMAIL)
                .build();

        assertEquals("Yes", caveatData.getDefaultValueForEmailNotifications());
    }
}
