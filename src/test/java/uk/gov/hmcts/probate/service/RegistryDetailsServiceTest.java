package uk.gov.hmcts.probate.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.config.properties.registries.RegistriesProperties;
import uk.gov.hmcts.probate.config.properties.registries.Registry;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static uk.gov.hmcts.probate.model.Constants.CTSC;

public class RegistryDetailsServiceTest {

    private static final String REGISTRY_LOCATION = "bristol";
    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private static final Long CASE_ID = 12345678987654321L;

    private Registry registry;
    private CaseDetails caseDetails;
    private Map<String, Registry> registryMap;

    private RegistriesProperties registriesProperties = new RegistriesProperties();

    @InjectMocks
    private final RegistryDetailsService registryDetailsService = new RegistryDetailsService(registriesProperties);

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        registry = new Registry();
        registry.setPhone("01010101010101");
        registry.setAddressLine1("addressLine1");
        registry.setAddressLine2("addressLine2");
        registry.setAddressLine3("addressLine3");
        registry.setAddressLine4("addressLine4");
        registry.setPostcode("postcode");
        registry.setTown("town");

        registryMap = new HashMap<>();
        registryMap.put(REGISTRY_LOCATION, registry);
        registryMap.put(CTSC, registry);

        registriesProperties.setRegistries(registryMap);

        caseDetails = new CaseDetails(CaseData.builder().registryLocation("bristol").build(), LAST_MODIFIED, CASE_ID);
    }

    @Test
    public void testCaseDetailsHaveRegistryMappedCorrectly() {
        assertThat(registryDetailsService.getRegistryDetails(caseDetails).getRegistryTelephone(), is(registry.getPhone()));
        assertThat(registryDetailsService.getRegistryDetails(caseDetails).getRegistryAddressLine1(), is(registry.getAddressLine1()));
        assertThat(registryDetailsService.getRegistryDetails(caseDetails).getRegistryAddressLine2(), is(registry.getAddressLine2()));
        assertThat(registryDetailsService.getRegistryDetails(caseDetails).getRegistryAddressLine3(), is(registry.getAddressLine3()));
        assertThat(registryDetailsService.getRegistryDetails(caseDetails).getRegistryAddressLine4(), is(registry.getAddressLine4()));
        assertThat(registryDetailsService.getRegistryDetails(caseDetails).getRegistryAddressLine4(), is(registry.getAddressLine4()));
        assertThat(registryDetailsService.getRegistryDetails(caseDetails).getRegistryPostcode(), is(registry.getPostcode()));
        assertThat(registryDetailsService.getRegistryDetails(caseDetails).getRegistryTown(), is(registry.getTown()));
        assertThat(registryDetailsService.getRegistryDetails(caseDetails).getCtscTelephone(), is(registryMap.get(CTSC).getPhone()));
    }
}
