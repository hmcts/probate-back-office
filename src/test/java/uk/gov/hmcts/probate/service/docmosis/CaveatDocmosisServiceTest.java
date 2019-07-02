package uk.gov.hmcts.probate.service.docmosis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.config.properties.registries.RegistriesProperties;
import uk.gov.hmcts.probate.config.properties.registries.Registry;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.service.DateFormatterService;
import uk.gov.hmcts.probate.service.ccd.CcdReferenceFormatterService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class CaveatDocmosisServiceTest {

    @InjectMocks
    private CaveatDocmosisService caveatDocmosisService;

    @Mock
    private RegistriesProperties registriesPropertiesMock;

    @Mock
    private DateFormatterService dateFormatterService;

    @Mock
    private CcdReferenceFormatterService ccdReferenceFormatterServiceMock;

    private static final String DATE_INPUT_FORMAT = "ddMMyyyy";
    private static final long ID = 1234567891234567L;
    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private static final String CAV_EXPIRY_DATE = "31st December 2019";

    Registry registry = new Registry();
    ObjectMapper mapper = new ObjectMapper();
    Map<String, Registry> registries = new HashMap<>();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        registry.setName("leeds");
        registry.setPhone("123456789");
        registries = mapper.convertValue(registry, Map.class);

        when(registriesPropertiesMock.getRegistries()).thenReturn(registries);
        when(dateFormatterService.formatCaveatExpiryDate(any())).thenReturn(CAV_EXPIRY_DATE);
    }

    @Test
    public void testCreateDataAsPlaceholders() {
        CaveatData caveatData = CaveatData.builder()
                .registryLocation("leeds")
                .expiryDate(LocalDate.of(2019, 12, 31))
                .build();
        CaveatDetails caveatDetails = new CaveatDetails(caveatData, LAST_MODIFIED, ID);
        DateFormat generatedDateFormat = new SimpleDateFormat(DATE_INPUT_FORMAT);
        Map<String, Object> placeholders = caveatDocmosisService.caseDataAsPlaceholders(caveatDetails);

        assertEquals(placeholders.get("generatedDate"), generatedDateFormat.format(new Date()));
        assertEquals(placeholders.get("registry"), registries.get(
                caveatData.getRegistryLocation().toLowerCase()));
        assertEquals(placeholders.get("PA8AURL"), "www.citizensadvice.org.uk|https://www.citizensadvice.org.uk/");
        assertEquals(placeholders.get("caseReference"), ccdReferenceFormatterServiceMock.getFormattedCaseReference("1234567891234567"));
        assertEquals(placeholders.get("caveatExpiryDate"), CAV_EXPIRY_DATE);
    }

    @Test
    public void testCreateDataAsPlaceholdersCoverSheet() {
        CaveatData caveatData = CaveatData.builder()
                .registryLocation("leeds")
                .build();
        CaveatDetails caveatDetails = new CaveatDetails(caveatData, LAST_MODIFIED, ID);
        DateFormat generatedDateFormat = new SimpleDateFormat(DATE_INPUT_FORMAT);
        Map<String, Object> placeholders = caveatDocmosisService.caseDataAsPlaceholders(caveatDetails);

        assertEquals(placeholders.get("generatedDate"), generatedDateFormat.format(new Date()));
    }
}
