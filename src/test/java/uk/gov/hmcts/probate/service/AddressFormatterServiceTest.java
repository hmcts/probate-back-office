package uk.gov.hmcts.probate.service;

import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.model.ccd.ProbateAddress;

import static org.junit.Assert.assertEquals;

@ExtendWith(SpringExtension.class)
class AddressFormatterServiceTest {

    @InjectMocks
    private AddressFormatterService addressFormatterService;

    @Test
    void shouldReturnFormattedAddress() {
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
    void shouldReturnFormattedAddressWillNulls() {
        ProbateAddress probateAddress = ProbateAddress.builder()
                .proAddressLine1("addressLine1")
                .proPostCode("postcode")
                .build();
        assertEquals("addressLine1, postcode",
                addressFormatterService.formatAddress(probateAddress));
    }

    @Test
    void shouldReturnBlankStringFormattedAddress() {
        assertEquals(Strings.EMPTY, addressFormatterService.formatAddress(null));
    }

}
