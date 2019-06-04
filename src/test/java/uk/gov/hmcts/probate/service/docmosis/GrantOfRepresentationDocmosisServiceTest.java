package uk.gov.hmcts.probate.service.docmosis;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.config.PDFServiceConfiguration;
import uk.gov.hmcts.probate.config.properties.registries.RegistriesProperties;
import uk.gov.hmcts.probate.model.CaseType;
import uk.gov.hmcts.probate.model.ccd.ProbateAddress;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.CaveatQueryService;
import uk.gov.hmcts.probate.service.FileSystemResourceService;
import uk.gov.hmcts.probate.service.ccd.CcdReferenceFormatterService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class GrantOfRepresentationDocmosisServiceTest {

    @InjectMocks
    private GrantOfRepresentationDocmosisService grantOfRepresentationDocmosisService;

    @Mock
    private RegistriesProperties registriesPropertiesMock;

    @Mock
    private PDFServiceConfiguration pdfServiceConfigurationMock;

    @Mock
    private FileSystemResourceService fileSystemResourceServiceMock;

    @Mock
    private CaveatQueryService caveatQueryServiceMock;

    @Mock
    private CcdReferenceFormatterService ccdReferenceFormatterServiceMock;

    private static final String DATE_INPUT_FORMAT = "ddMMyyyy";
    private static final long ID = 1234567891234567L;
    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private static final String PERSONALISATION_DATE_CAVEAT_ENTERED = "dateCaveatEntered";
    private static final String PERSONALISATION_CAVEATOR_NAME = "caveatorName";
    private static final String PERSONALISATION_CAVEATOR_ADDRESS = "caveatorAddress";
    private static final String PERSONALISATION_CASE_REFERENCE = "caseReference";
    private static final String PERSONALISATION_GENERATED_DATE = "generatedDate";
    private static final String PERSONALISATION_REGISTRY = "registry";
    private static final String PERSONALISATION_PA8AURL = "PA8AURL";
    private static final String PERSONALISATION_PA8BURL = "PA8AURL";
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateDataAsPlaceholders() {
        DateFormat generatedDateFormat = new SimpleDateFormat(DATE_INPUT_FORMAT);

        CaseData caseData = CaseData.builder()
                .registryLocation("leeds")
                .build();
        CaseDetails caseDetails = new CaseDetails(caseData, LAST_MODIFIED, ID);


        CaveatData caveatData = CaveatData.builder()
                .applicationSubmittedDate(LocalDate.now())
                .caveatorForenames("fred")
                .caveatorSurname("jones")
                .caveatorAddress(ProbateAddress.builder().proAddressLine1("addressLine1").build())
                .build();

        when(caveatQueryServiceMock.findCaveatById(CaseType.CAVEAT, "")).thenReturn(caveatData);
        Map<String, Object> placeholders = grantOfRepresentationDocmosisService.caseDataAsPlaceholders(caseDetails);

        assertEquals(placeholders.get(PERSONALISATION_GENERATED_DATE), generatedDateFormat.format(new Date()));
        assertEquals(placeholders.get(PERSONALISATION_REGISTRY), "leeds");
        assertEquals(placeholders.get(PERSONALISATION_PA8AURL), "www.citizensadvice.org.uk|https://www.citizensadvice.org.uk/");
        assertEquals(placeholders.get(PERSONALISATION_PA8BURL), "www.citizensadvice.org.uk|https://www.citizensadvice.org.uk/");
        assertEquals(placeholders.get(PERSONALISATION_CASE_REFERENCE),
                ccdReferenceFormatterServiceMock.getFormattedCaseReference("1234567891234567"));
        assertEquals(placeholders.get(PERSONALISATION_DATE_CAVEAT_ENTERED), LocalDate.now());
        assertEquals(placeholders.get(PERSONALISATION_CAVEATOR_NAME), "fred jones");
        assertEquals(placeholders.get(PERSONALISATION_CAVEATOR_ADDRESS), "addressLine1");

    }


}
