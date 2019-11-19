package uk.gov.hmcts.probate.service;

import lombok.*;
import org.springframework.stereotype.*;
import uk.gov.hmcts.probate.config.properties.thirdParties.*;
import uk.gov.hmcts.probate.model.ccd.raw.request.*;

@Service
@RequiredArgsConstructor
public class ThirdPartyDetailsService {

    private final ThirdPartiesProperties thirdPartiesProperties;


    public CaseDetails getThirdPartyDetails(CaseDetails caseDetails, String companyName) {
        ThirdParty thirdParty = thirdPartiesProperties.getThirdParty().get(companyName);
        caseDetails.setThirdPartyAddressLine1(thirdParty.getAddressLine1());
        caseDetails.setThirdPartyAddressLine2(thirdParty.getAddressLine2());
        caseDetails.setThirdPartyAddressLine3(thirdParty.getAddressLine3());
        caseDetails.setThirdPartyPostcode(thirdParty.getPostcode());
        caseDetails.setThirdPartyTown(thirdParty.getTown());

        return caseDetails;
    }
}
