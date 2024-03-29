package uk.gov.hmcts.probate.model.ccd.willlodgement.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class WillLodgementDataTest {

    private static final String WL_DECEASED_FORENAMES = "Forenames";
    private static final String WL_DECEASED_SURNAME = "Surname";
    private static final String WL_EXECUTOR_FORENAMES = "fName";
    private static final String WL_EXECUTOR_SURNAME = "sName";
    private static final LocalDate LOCAL_DATE = LocalDate.of(2000,01,01);

    @InjectMocks
    private WillLodgementData underTest;

    @BeforeEach
    public void setup() {

        underTest = WillLodgementData.builder()
                .deceasedForenames(WL_DECEASED_FORENAMES)
                .deceasedSurname(WL_DECEASED_SURNAME)
                .executorForenames(WL_EXECUTOR_FORENAMES)
                .executorSurname(WL_EXECUTOR_SURNAME)
                .build();
    }

    @Test
    void shouldReturnDeceasedFullNameForWillLodgement() {
        final WillLodgementData willLodgementData = WillLodgementData.builder()
                .deceasedForenames(WL_DECEASED_FORENAMES)
                .deceasedSurname(WL_DECEASED_SURNAME)
                .build();

        assertEquals(WL_DECEASED_FORENAMES + " " + WL_DECEASED_SURNAME, willLodgementData.getDeceasedFullName());
    }

    @Test
    void shouldReturnCaveatorFullNameForWillLodgement() {
        final WillLodgementData willLodgementData = WillLodgementData.builder()
                .executorForenames(WL_EXECUTOR_FORENAMES)
                .executorSurname(WL_EXECUTOR_SURNAME)
                .build();

        assertEquals(WL_EXECUTOR_FORENAMES + " " + WL_EXECUTOR_SURNAME, willLodgementData.getExecutorFullName());
    }

    @Test
    void shouldReturnNullFromInputData() {
        final WillLodgementData caseData = WillLodgementData.builder()
                .willDate(null)
                .build();

        assertEquals(null, caseData.getWillDateFormatted());
    }

    @Test
    void shouldReturnDODFormattedWithST() {
        final WillLodgementData caseData = WillLodgementData.builder()
                .willDate(LOCAL_DATE)
                .build();

        assertEquals("1st January 2000", caseData.getWillDateFormatted());
    }

    @Test
    void shouldReturnDODFormattedWithND() {
        final WillLodgementData caseData = WillLodgementData.builder()
                .willDate(LocalDate.of(2000,01,02))
                .build();

        assertEquals("2nd January 2000", caseData.getWillDateFormatted());
    }

    @Test
    void shouldReturnDODFormattedWithRD() {
        final WillLodgementData caseData = WillLodgementData.builder()
                .willDate(LocalDate.of(2000,01,03))
                .build();

        assertEquals("3rd January 2000", caseData.getWillDateFormatted());
    }

    @Test
    void shouldReturnDODFormattedWithTH() {
        final WillLodgementData caseData = WillLodgementData.builder()
                .willDate(LocalDate.of(2000,01,04))
                .build();

        assertEquals("4th January 2000", caseData.getWillDateFormatted());
    }

    @Test
    void shouldThrowParseException() {
        final WillLodgementData caseData = WillLodgementData.builder()
                .willDate(LocalDate.of(300000,01,04))
                .build();

        assertEquals(null, caseData.getWillDateFormatted());
    }

    @Test
    void shouldNotReturnNullDocumentsGenerated() {
        final WillLodgementData caseData = WillLodgementData.builder()
                .build();

        assertNotNull(caseData.getDocumentsGenerated());
    }
}
