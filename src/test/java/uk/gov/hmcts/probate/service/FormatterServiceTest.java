package uk.gov.hmcts.probate.service;

import org.apache.logging.log4j.util.Strings;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.probate.model.ccd.ProbateAddress;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class FormatterServiceTest {

    @InjectMocks
    private FormatterService formatterService;

    @Test
    public void shouldReturnDateFormattedWithStSuffix() {
        assertEquals("1st January 2000", formatterService.formatDate(LocalDate.of(2000, 01, 01)));
    }

    @Test
    public void shouldReturnDateFormattedWithNdSuffix() {
        assertEquals("2nd January 2000", formatterService.formatDate(LocalDate.of(2000, 01, 02)));
    }

    @Test
    public void shouldReturnDateFormattedWithRdSuffix() {
        assertEquals("3rd January 2000", formatterService.formatDate(LocalDate.of(2000, 01, 03)));
    }

    @Test
    public void shouldReturnDateFormattedWithThSuffix() {
        assertEquals("4th January 2000", formatterService.formatDate(LocalDate.of(2000, 01, 04)));
    }

    @Test
    public void shouldThrowParseException() {
        assertEquals(null, formatterService.formatDate(LocalDate.of(30000, 01, 01)));
    }

    @Test
    public void shouldReturnNullFormatDate() {
        assertEquals(null, formatterService.formatDate(null));
    }

    @Test
    public void shouldReturnFormattedAddress() {
        ProbateAddress probateAddress = ProbateAddress.builder()
                .proAddressLine1("addressLine1")
                .proAddressLine2("addressLine2")
                .proAddressLine3("addressLine3")
                .proPostCode("postcode")
                .proPostTown("posttown")
                .proCounty("county")
                .proCountry("country")
                .build();
        assertEquals("addressLine1, addressLine2, addressLine3, posttown, county, postcode, country",
                formatterService.formatAddress(probateAddress));
    }

    @Test
    public void shouldReturnFormattedAddressWillNulls() {
        ProbateAddress probateAddress = ProbateAddress.builder()
                .proAddressLine1("addressLine1")
                .proPostCode("postcode")
                .build();
        assertEquals("addressLine1, postcode",
                formatterService.formatAddress(probateAddress));
    }

    @Test
    public void shouldReturnBlankStringFormattedAddress() {
        assertEquals(Strings.EMPTY, formatterService.formatAddress(null));
    }

    @Test
    public void shouldReturnCavetExpiryDateFormatted() {
        assertEquals("1st January 2019", formatterService.formatCaveatExpiryDate(LocalDate.of(2019, 01, 01)));
        assertEquals("22nd February 2019", formatterService.formatCaveatExpiryDate(LocalDate.of(2019, 02, 22)));
        assertEquals("13th March 2019", formatterService.formatCaveatExpiryDate(LocalDate.of(2019, 03, 13)));
        assertEquals("23rd April 2019", formatterService.formatCaveatExpiryDate(LocalDate.of(2019, 04, 23)));
    }


}
