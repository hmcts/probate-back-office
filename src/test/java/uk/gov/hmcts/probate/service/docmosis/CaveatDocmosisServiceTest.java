package uk.gov.hmcts.probate.service.docmosis;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.config.PDFServiceConfiguration;
import uk.gov.hmcts.probate.config.properties.registries.RegistriesProperties;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.service.FileSystemResourceService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class CaveatDocmosisServiceTest {

    @InjectMocks
    private CaveatDocmosisService caveatDocmosisService;

    @Mock
    private RegistriesProperties registriesPropertiesMock;

    @Mock
    private PDFServiceConfiguration pdfServiceConfigurationMock;

    @Mock
    private FileSystemResourceService fileSystemResourceServiceMock;

    private static final String DATE_INPUT_FORMAT = "ddMMyyyy";
    private static final long ID = 1234567891234567L;
    private static final String CASE_REFERENCE = "#1234-5678-9123-4567";
    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateDataAsPlaceholders() {
        CaveatData caveatData = CaveatData.builder()
                .registryLocation("leeds")
                .build();
        CaveatDetails caveatDetails = new CaveatDetails(caveatData, LAST_MODIFIED, ID);
        DateFormat generatedDateFormat = new SimpleDateFormat(DATE_INPUT_FORMAT);
        Map<String, Object> placeholders = caveatDocmosisService.caseDataAsPlaceholders(caveatDetails);

        assertEquals(placeholders.get("generatedDate"), generatedDateFormat.format(new Date()));
        assertEquals(placeholders.get("registryLocation"), "leeds");
        assertEquals(placeholders.get("PA8AURL"), "www.citizensadvice.org.uk|https://www.citizensadvice.org.uk/");
        assertEquals(placeholders.get("caseReference"), CASE_REFERENCE);
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
