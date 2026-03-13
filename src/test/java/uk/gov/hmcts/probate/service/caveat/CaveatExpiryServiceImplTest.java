package uk.gov.hmcts.probate.service.caveat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.service.CaveatQueryService;

import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
class CaveatExpiryServiceImplTest {

    private static final String EXPIRY_DATE = "2020-12-31";

    @Mock
    private CaveatQueryService caveatQueryService;

    @InjectMocks
    private CaveatExpiryServiceImpl caveatExpiryService;

    @Test
    void shouldCallQueryServiceToExpireCaveats() {
        caveatExpiryService.expireCaveats(EXPIRY_DATE);
        verify(caveatQueryService).findAndExpireCaveatExpiredCases(EXPIRY_DATE);
    }
}