package uk.gov.hmcts.probate.model.ccd.standingsearch.request;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;

import static org.junit.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;

public class StandingSearchDataTest {

    private static final String SS_DECEASED_FORENAMES = "Forenames";
    private static final String SS_DECEASED_SURNAME = "Surname";
    private static final String SS_APPLICANT_FORENAMES = "fName";
    private static final String SS_APPLICANT_SURNAME = "sName";

    @InjectMocks
    private StandingSearchData underTest;

    @Before
    public void setup() {

        underTest = StandingSearchData.builder()
                .deceasedForenames(SS_DECEASED_FORENAMES)
                .deceasedSurname(SS_DECEASED_SURNAME)
                .applicantForenames(SS_APPLICANT_FORENAMES)
                .applicantSurname(SS_APPLICANT_SURNAME)
                .build();
    }

    @Test
    public void shouldReturnDeceasedFullNameForStandingSearch() {
        final StandingSearchData standingSearchData = StandingSearchData.builder()
                .deceasedForenames(SS_DECEASED_FORENAMES)
                .deceasedSurname(SS_DECEASED_SURNAME)
                .build();

        assertEquals(SS_DECEASED_FORENAMES + " " + SS_DECEASED_SURNAME, standingSearchData.getDeceasedFullName());
    }

    @Test
    public void shouldReturnCaveatorFullNameForStandingSearch() {
        final StandingSearchData standingSearchData = StandingSearchData.builder()
                .applicantForenames(SS_APPLICANT_FORENAMES)
                .applicantSurname(SS_APPLICANT_SURNAME)
                .build();

        assertEquals(SS_APPLICANT_FORENAMES + " " + SS_APPLICANT_SURNAME, standingSearchData.getApplicantFullName());
    }
}
