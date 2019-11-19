package uk.gov.hmcts.probate.service;

import org.junit.*;
import org.mockito.*;
import uk.gov.hmcts.probate.config.properties.thirdParties.*;
import uk.gov.hmcts.probate.model.ccd.raw.request.*;

import java.util.*;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.core.Is.*;
import static uk.gov.hmcts.probate.model.Constants.*;

public class ThirdPartyDetailsServiceTest {

    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private static final Long CASE_ID = 12345678987654321L;

    private ThirdParty thirdParty;
    private CaseDetails caseDetails;
    private Map<String, ThirdParty> thirdPartyMap;

    private ThirdPartiesProperties thirdPartiesProperties = new ThirdPartiesProperties();

    @InjectMocks
    private final ThirdPartyDetailsService thirdPartyDetailsService = new ThirdPartyDetailsService(thirdPartiesProperties);

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        thirdParty = new ThirdParty();
        thirdParty.setAddressLine1("Unit 2");
        thirdParty.setAddressLine2("Wood Lane");
        thirdParty.setAddressLine3("Erdington");
        thirdParty.setPostcode("Birmingham");
        thirdParty.setTown("B24 9QG");

        thirdPartyMap = new HashMap<>();
        thirdPartyMap.put(IRON_MOUNTAIN, thirdParty);

        thirdPartiesProperties.setThirdParty(thirdPartyMap);

        caseDetails = new CaseDetails(CaseData.builder().registryLocation("bristol").build(), LAST_MODIFIED, CASE_ID);
    }

    @Test
    public void testCaseDetailsHaveRegistryMappedCorrectly() {
        assertThat(thirdPartyDetailsService.getThirdPartyDetails(caseDetails, IRON_MOUNTAIN).getThirdPartyAddressLine1(), is(thirdParty.getAddressLine1()));
        assertThat(thirdPartyDetailsService.getThirdPartyDetails(caseDetails, IRON_MOUNTAIN).getThirdPartyAddressLine2(), is(thirdParty.getAddressLine2()));
        assertThat(thirdPartyDetailsService.getThirdPartyDetails(caseDetails, IRON_MOUNTAIN).getThirdPartyAddressLine3(), is(thirdParty.getAddressLine3()));
        assertThat(thirdPartyDetailsService.getThirdPartyDetails(caseDetails, IRON_MOUNTAIN).getThirdPartyPostcode(), is(thirdParty.getPostcode()));
        assertThat(thirdPartyDetailsService.getThirdPartyDetails(caseDetails, IRON_MOUNTAIN).getThirdPartyTown(), is(thirdParty.getTown()));
    }
}
