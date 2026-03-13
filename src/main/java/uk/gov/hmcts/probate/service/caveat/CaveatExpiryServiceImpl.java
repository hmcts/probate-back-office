package uk.gov.hmcts.probate.service.caveat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.service.CaveatExpiryService;
import uk.gov.hmcts.probate.service.CaveatQueryService;

@Slf4j
@Component
@RequiredArgsConstructor
public class CaveatExpiryServiceImpl implements CaveatExpiryService {

    private final CaveatQueryService caveatQueryService;

    @Override
    public void expireCaveats(String expiryDate) {
        log.info("Search for expired Caveats for expiryDate: {}", expiryDate);
        caveatQueryService.findAndExpireCaveatExpiredCases(expiryDate);
    }
}