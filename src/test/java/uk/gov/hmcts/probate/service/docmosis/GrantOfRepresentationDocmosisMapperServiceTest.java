package uk.gov.hmcts.probate.service.docmosis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.config.properties.registries.RegistriesProperties;
import uk.gov.hmcts.probate.config.properties.registries.Registry;
import uk.gov.hmcts.probate.model.CaseType;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.ProbateAddress;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.raw.BulkPrint;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.AddressFormatterService;
import uk.gov.hmcts.probate.service.CaveatQueryService;
import uk.gov.hmcts.probate.service.DateFormatterService;
import uk.gov.hmcts.probate.service.ccd.CcdReferenceFormatterService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;


public class GrantOfRepresentationDocmosisMapperServiceTest {

    @InjectMocks
    private GrantOfRepresentationDocmosisMapperService grantOfRepresentationDocmosisMapperService;

    @Mock
    private RegistriesProperties registriesPropertiesMock;

    @Mock
    private CaveatQueryService caveatQueryServiceMock;

    @Mock
    private CcdReferenceFormatterService ccdReferenceFormatterServiceMock;

    @Mock
    private GenericMapperService genericMapperService;

    @Mock
    private AddressFormatterService addressFormatterService;

    @Mock
    private DateFormatterService dateFormatterService;

    private static final String DATE_INPUT_FORMAT = "ddMMyyyy";
    private static final long ID = 1234567891234567L;
    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private static final LocalDate CAVEAT_EXPIRY_DATE = LocalDate.of(2019, 12, 31);
    private static final String PERSONALISATION_CAVEAT_EXPIRY_DATE = "2019-12-31";
    private static final String PERSONALISATION_CAVEATOR_NAME = "caveatorName";
    private static final String PERSONALISATION_CAVEATOR_ADDRESS = "caveatorAddress";
    private static final String PERSONALISATION_CASE_REFERENCE = "caseReference";
    private static final String PERSONALISATION_GENERATED_DATE = "generatedDate";
    private static final String PERSONALISATION_REGISTRY = "registry";
    private static final String PERSONALISATION_PA8AURL = "PA8AURL";
    private static final String PERSONALISATION_PA8BURL = "PA8BURL";
    private static final String PERSONALISATION_CAVEAT_REFERENCE = "caveatReference";
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private CaveatData caveatData;
    private CaseDetails caseDetails;
    Registry registry = new Registry();
    ObjectMapper mapper = new ObjectMapper();
    Map<String, Registry> registries = new HashMap<>();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        registry.setName("leeds");
        registry.setPhone("123456789");
        registries = mapper.convertValue(registry, Map.class);

        CaseData caseData = CaseData.builder()
                .registryLocation("leeds")
                .build();
        caseDetails = new CaseDetails(caseData, LAST_MODIFIED, ID);
        caseDetails.setRegistryTelephone("123456789");

        CollectionMember<CaseMatch> caseMatchMember = new CollectionMember<>(CaseMatch.builder().build());
        List<CollectionMember<CaseMatch>> caseMatch = new ArrayList<>();
        caseMatch.add(caseMatchMember);

        CollectionMember<Document> documentMember = new CollectionMember<>(Document.builder().build());
        List<CollectionMember<Document>> notificationGenerated = new ArrayList<>();
        notificationGenerated.add(documentMember);

        CollectionMember<BulkPrint> bulkPrintMember = new CollectionMember<>(BulkPrint.builder().build());
        List<CollectionMember<BulkPrint>> bulkPrintId = new ArrayList<>();
        bulkPrintId.add(bulkPrintMember);

        List<CollectionMember<Document>> documentsGenerated = new ArrayList<>();
        documentsGenerated.add(documentMember);

        caveatData = CaveatData.builder()
                .registryLocation("leeds")
                .applicationSubmittedDate(LocalDate.now())
                .caveatorForenames("fred")
                .caveatorSurname("jones")
                .caseMatches(caseMatch)
                .notificationsGenerated(notificationGenerated)
                .bulkPrintId(bulkPrintId)
                .documentsGenerated(documentsGenerated)
                .caveatorAddress(ProbateAddress.builder().proAddressLine1("addressLine1").build())
                .expiryDate(CAVEAT_EXPIRY_DATE)
                .build();

        when(caveatQueryServiceMock.findCaveatById(eq(CaseType.CAVEAT), any())).thenReturn(caveatData);
        when(registriesPropertiesMock.getRegistries()).thenReturn(registries);
        when(addressFormatterService.formatAddress(any())).thenReturn(PERSONALISATION_CAVEATOR_ADDRESS);
        when(dateFormatterService.formatCaveatExpiryDate(any())).thenReturn(PERSONALISATION_CAVEAT_EXPIRY_DATE);
        when(genericMapperService.addCaseDataWithRegistryProperties(caseDetails)).thenReturn(mapper.convertValue(caseDetails, Map.class));

    }

    @Test
    public void testCreateDataAsPlaceholders() {
        DateFormat generatedDateFormat = new SimpleDateFormat(DATE_INPUT_FORMAT);

        Map<String, Object> placeholders = grantOfRepresentationDocmosisMapperService.caseDataForStoppedMatchedCaveat(caseDetails);

        assertEquals(ccdReferenceFormatterServiceMock.getFormattedCaseReference("1234567891234567"),
                placeholders.get(PERSONALISATION_CASE_REFERENCE));
        assertEquals(generatedDateFormat.format(new Date()), placeholders.get(PERSONALISATION_GENERATED_DATE));
        assertEquals(registries.get(
                caseDetails.getData().getRegistryLocation().toLowerCase()),
                placeholders.get(PERSONALISATION_REGISTRY));
        assertEquals("https://www.gov.uk/inherits-someone-dies-without-will|https://www.gov.uk/inherits-someone-dies-without-will",
                placeholders.get(PERSONALISATION_PA8AURL));
        assertEquals("https://www.citizensadvice.org.uk|https://www.citizensadvice.org.uk/",
                placeholders.get(PERSONALISATION_PA8BURL));
        assertEquals("fred jones", placeholders.get(PERSONALISATION_CAVEATOR_NAME));
        assertEquals("caveatorAddress",
                placeholders.get(PERSONALISATION_CAVEATOR_ADDRESS));
        assertEquals(PERSONALISATION_CAVEAT_EXPIRY_DATE,
                placeholders.get("caveatExpiryDate"));
        assertEquals(ccdReferenceFormatterServiceMock.getFormattedCaseReference("1234567891234567"),
                placeholders.get(PERSONALISATION_CAVEAT_REFERENCE));
    }
}

