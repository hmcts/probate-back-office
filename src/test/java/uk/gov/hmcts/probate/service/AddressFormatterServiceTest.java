package uk.gov.hmcts.probate.service;

import org.apache.logging.log4j.util.Strings;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.probate.model.ccd.ProbateAddress;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class AddressFormatterServiceTest {

    @InjectMocks
    private AddressFormatterService addressFormatterService;

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
                addressFormatterService.formatAddress(probateAddress));
    }

    @Test
    public void shouldReturnFormattedAddressWillNulls() {
        ProbateAddress probateAddress = ProbateAddress.builder()
                .proAddressLine1("addressLine1")
                .proPostCode("postcode")
                .build();
        assertEquals("addressLine1, postcode",
                addressFormatterService.formatAddress(probateAddress));
    }

    @Test
    public void shouldReturnBlankStringFormattedAddress() {
        assertEquals(Strings.EMPTY, addressFormatterService.formatAddress(null));
    }

}
