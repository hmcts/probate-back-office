package uk.gov.hmcts.probate.model.ccd.caveat.request;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import uk.gov.hmcts.probate.model.ccd.ProbateAddress;
import uk.gov.hmcts.probate.service.AddressFormatterService;
import uk.gov.hmcts.probate.service.DateFormatterService;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

public class CaveatDataTest {

    private DateFormatterService dateFormatterService = new DateFormatterService();
    private AddressFormatterService addressFormatterService = new AddressFormatterService();

    private static final String CAV_DECEASED_FORENAMES = "Deceased_fn";
    private static final String CAV_DECEASED_SURNAME = "Deceased_ln";
    private static final String CAV_CAVEATOR_FORENAMES = "Caveator_fn";
    private static final String CAV_CAVEATOR_SURNAME = "Caveator_ln";
    private static final String CAV_CAVEATOR_EMAIL = "caveator@probate-test.com";
    private static final String CAV_CAVEATOR_ADDRESS_1 = "15 Hanover Lane";
    private static final String CAV_CAVEATOR_ADDRESS_2 = "Catford";
    private static final String CAV_CAVEATOR_ADDRESS_3 = "";
    private static final String CAV_CAVEATOR_TOWN = "London";
    private static final String CAV_CAVEATOR_COUNTY = "";
    private static final String CAV_CAVEATOR_POSTCODE = "1AB 1CD";
    private static final String CAV_CAVEATOR_COUNTRY = "";
    private static final LocalDate LOCAL_DATE = LocalDate.of(2000, 01, 01);

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
    public void shouldReturnApplicantFullNameForSolsCaveat() {
        final CaveatData caveatData = CaveatData.builder()
                .caveatorForenames(CAV_CAVEATOR_FORENAMES)
                .caveatorSurname(CAV_CAVEATOR_SURNAME)
                .build();
        assertEquals(CAV_CAVEATOR_FORENAMES + " " + CAV_CAVEATOR_SURNAME,
                caveatData.getCaveatorFullName());
    }


    @Test
    public void shouldReturnDeceasedFullNameForSolsCaveat() {
        final CaveatData caveatData = CaveatData.builder()
                .deceasedForenames(CAV_CAVEATOR_FORENAMES)
                .deceasedSurname(CAV_CAVEATOR_SURNAME)
                .build();

        assertEquals(CAV_CAVEATOR_FORENAMES + " " + CAV_CAVEATOR_SURNAME,
                caveatData.getDeceasedFullName());
    }

    @Test
    public void shouldReturnDefaultEmailNotificationCaveat() {
        final CaveatData caveatData = CaveatData.builder()
                .caveatorEmailAddress(CAV_CAVEATOR_EMAIL)
                .build();

        assertEquals("Yes", caveatData.getDefaultValueForEmailNotifications());
    }

    @Test
    public void shouldReturnSubmissionDateFormattedWithST() {
        final CaveatData caveatData = CaveatData.builder()
                .applicationSubmittedDate(LOCAL_DATE)
                .build();

        assertEquals("1st January 2000", dateFormatterService.formatDate(caveatData.getApplicationSubmittedDate()));
    }

    @Test
    public void shouldReturnSubmissionDateFormattedWithND() {
        final CaveatData caveatData = CaveatData.builder()
                .applicationSubmittedDate(LocalDate.of(2000, 01, 02))
                .build();

        assertEquals("2nd January 2000", dateFormatterService.formatDate(caveatData.getApplicationSubmittedDate()));
    }

    @Test
    public void shouldReturnSubmissionDateFormattedWithRD() {
        final CaveatData caveatData = CaveatData.builder()
                .applicationSubmittedDate(LocalDate.of(2000, 01, 03))
                .build();

        assertEquals("3rd January 2000", dateFormatterService.formatDate(caveatData.getApplicationSubmittedDate()));
    }

    @Test
    public void shouldReturnSubmissionDateFormattedWithTH() {
        final CaveatData caveatData = CaveatData.builder()
                .applicationSubmittedDate(LocalDate.of(2000, 01, 04))
                .build();

        assertEquals("4th January 2000", dateFormatterService.formatDate(caveatData.getApplicationSubmittedDate()));
    }

    @Test
    public void shouldThrowParseException() {
        final CaveatData caveatData = CaveatData.builder()
                .applicationSubmittedDate(LocalDate.of(300000, 01, 04))
                .build();

        assertEquals(null, dateFormatterService.formatDate(caveatData.getApplicationSubmittedDate()));
    }

    @Test
    public void shouldReturnExpiryDateFormattedWithST() {
        final CaveatData caveatData = CaveatData.builder()
                .expiryDate(LOCAL_DATE)
                .build();

        assertEquals("1st January 2000", dateFormatterService.formatDate(caveatData.getExpiryDate()));
    }

    @Test
    public void shouldReturnExpiryDateFormattedWithND() {
        final CaveatData caveatData = CaveatData.builder()
                .expiryDate(LocalDate.of(2000, 01, 02))
                .build();

        assertEquals("2nd January 2000", dateFormatterService.formatDate(caveatData.getExpiryDate()));
    }

    @Test
    public void shouldReturnExpiryDateFormattedWithRD() {
        final CaveatData caveatData = CaveatData.builder()
                .expiryDate(LocalDate.of(2000, 01, 03))
                .build();

        assertEquals("3rd January 2000", dateFormatterService.formatDate(caveatData.getExpiryDate()));
    }

    @Test
    public void shouldReturnExpiryDateFormattedWithTH() {
        final CaveatData caveatData = CaveatData.builder()
                .expiryDate(LocalDate.of(2000, 01, 04))
                .build();

        assertEquals("4th January 2000", dateFormatterService.formatDate(caveatData.getExpiryDate()));
    }

    @Test
    public void shouldReturnCaveatorFullAddressLongForCaveat() {

        ProbateAddress caveatorAddress = new ProbateAddress();

        caveatorAddress.setProAddressLine1(CAV_CAVEATOR_ADDRESS_1);
        caveatorAddress.setProAddressLine2(CAV_CAVEATOR_ADDRESS_2);
        caveatorAddress.setProAddressLine3(CAV_CAVEATOR_ADDRESS_3);
        caveatorAddress.setProPostTown(CAV_CAVEATOR_TOWN);
        caveatorAddress.setProCounty(CAV_CAVEATOR_COUNTY);
        caveatorAddress.setProPostCode(CAV_CAVEATOR_POSTCODE);
        caveatorAddress.setProCountry(CAV_CAVEATOR_COUNTRY);

        final CaveatData caveatData = CaveatData.builder()
                .caveatorAddress(caveatorAddress)
                .build();

        assertEquals(CAV_CAVEATOR_ADDRESS_1 + ", " + CAV_CAVEATOR_ADDRESS_2 + ", "
                        + CAV_CAVEATOR_TOWN + ", " + CAV_CAVEATOR_POSTCODE,
                addressFormatterService.formatAddress(caveatorAddress));
    }

    @Test
    public void shouldReturnCaveatorFullAddressShortForCaveat() {

        ProbateAddress caveatorAddress = new ProbateAddress();

        caveatorAddress.setProAddressLine1(CAV_CAVEATOR_ADDRESS_1);
        caveatorAddress.setProAddressLine2("");
        caveatorAddress.setProAddressLine3("");
        caveatorAddress.setProPostTown("");
        caveatorAddress.setProCounty("");
        caveatorAddress.setProPostCode(CAV_CAVEATOR_POSTCODE);
        caveatorAddress.setProCountry("");

        final CaveatData caveatData = CaveatData.builder()
                .caveatorAddress(caveatorAddress)
                .build();

        assertEquals(CAV_CAVEATOR_ADDRESS_1 + ", " + CAV_CAVEATOR_POSTCODE, addressFormatterService.formatAddress(caveatorAddress));
    }
}
