package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.config.properties.registries.RegistriesProperties;
import uk.gov.hmcts.probate.config.properties.registries.Registry;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;

import static uk.gov.hmcts.probate.model.Constants.CTSC;

@Service
@RequiredArgsConstructor
public class RegistryDetailsService {

    private final RegistriesProperties registriesProperties;

    public CaseDetails getRegistryDetails(CaseDetails caseDetails) {
        Registry registry = registriesProperties.getRegistries().get(
                caseDetails.getData().getRegistryLocation().toLowerCase());
        caseDetails.setRegistryTelephone(registry.getPhone());
        caseDetails.setRegistryAddressLine1(registry.getAddressLine1());
        caseDetails.setRegistryAddressLine2(registry.getAddressLine2());
        caseDetails.setRegistryAddressLine3(registry.getAddressLine3());
        caseDetails.setRegistryAddressLine4(registry.getAddressLine4());
        caseDetails.setRegistryPostcode(registry.getPostcode());
        caseDetails.setRegistryTown(registry.getTown());

        Registry ctscRegistry = registriesProperties.getRegistries().get(CTSC);
        caseDetails.setCtscTelephone(ctscRegistry.getPhone());

        return caseDetails;
    }
}
