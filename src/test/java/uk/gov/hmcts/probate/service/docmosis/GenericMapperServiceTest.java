package uk.gov.hmcts.probate.service.docmosis;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.config.properties.registries.RegistriesProperties;
import uk.gov.hmcts.probate.config.properties.registries.Registry;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;
import uk.gov.hmcts.probate.service.FileSystemResourceService;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.Constants.CTSC;

public class GenericMapperServiceTest {
    private static final String DECEASED_FORNAME_KEY = "deceasedForenames";
    private static final String DECEASED_FORNAME_VALUE = "Nigel";
    private static final String DECEASED_SURNAME_KEY = "deceasedSurname";
    private static final String DECEASED_SURNAME_VALUE = "Deadsoul";
    private static final String DECEASED_DOD_KEY = "deceasedDateOfDeath";
    private static final String DECEASED_DOB_KEY = "deceasedDateOfBirth";
    private static final String DECEASED_DOD_VALUE = "2015-01-01";
    private static final String DECEASED_DOB_VALUE = "1990-01-01";
    private static final String DECEASED_ADDRESS_KEY = "deceasedAddress";
    private static Map<String, Object> DECEASED_ADDRESS_VALUE = new HashMap<>();
    private static final String DECEASED_TITLE_KEY = "boDeceasedTitle";
    private static final String DECEASED_TITLE_VALUE = "Mr";
    private static final String PRIMARY_APPLICANT_APPLYING_KEY = "primaryApplicantIsApplying";
    private static final String PRIMARY_APPLICANT_APPLYING_VALUE = "Yes";
    private static final String PRIMARY_APPLICANT_FORENAME_KEY = "primaryApplicantForenames";
    private static final String PRIMARY_APPLICANT_FORENAME_VALUE = "Tim";
    private static final String PRIMARY_APPLICANT_SURNAME_KEY = "primaryApplicantSurname";
    private static final String PRIMARY_APPLICANT_SURNAME_VALUE = "Timson";
    private static final String PRIMARY_APPLICANT_ADDRESS_KEY = "primaryApplicantAddress";
    private static Map<String, Object> PRIMARY_APPLICANT_ADDRESS_VALUE = new HashMap<>();
    private static final String SOLICITOR_FIRM_NAME_KEY = "solsSolicitorFirmName";
    private static final String SOLICITOR_FIRM_NAME_VALUE = "Solicitors R us";
    private static final String SOLICITOR_ADDRESS_KEY = "solsSolicitorAddress";
    private static Map<String, Object> SOLICITOR_ADDRESS_VALUE = new HashMap<>();
    private static final String IHT_GROSS_KEY = "ihtGrossValue";
    private static final BigDecimal IHT_GROSS_VALUE = new BigDecimal(8899);
    private static final String IHT_NET_KEY = "ihtNetValue";
    private static final BigDecimal IHT_NET_VALUE = new BigDecimal(7787);
    private static final String CASE_TYPE_KEY = "caseType";
    private static final String CASE_TYPE_VALUE = "gop";
    private static final String REGISTRY_LOCATION_KEY = "registryLocation";
    private static final String REGISTRY_LOCATION_VALUE = "Oxford";
    private static final String GRANT_ISSUED_DATE_KEY = "grantIssuedDate";
    private static final String GRANT_ISSUED_DATE_VALUE = "2019-02-18";
    private static final String SOT_NAME_KEY = "solsSOTName";
    private static final String SOT_NAME_VALUE = "John Thesolicitor";
    private static final String REGISTRY_PHONE = "phone";
    private static final String REGISTRY_ADDRESS_LINE_1 = "addressLine1";
    private static final String REGISTRY_ADDRESS_LINE_2 = "addressLine2";
    private static final String REGISTRY_ADDRESS_LINE_3 = "addressLine3";
    private static final String REGISTRY_ADDRESS_LINE_4 = "addressLine4";
    private static final String REGISTRY_POSTCODE = "postcode";
    private static final String REGISTRY_TOWN = "town";
    private static final String ADDRESS_LINE_1 = "AddressLine1";
    private static final String ADDRESS_LINE_2 = "AddressLine2";
    private static final String ADDRESS_LINE_3 = "AddressLine3";
    private static final String COUNTY = "County";
    private static final String POST_TOWN = "PostTown";
    private static final String POSTCODE = "PostCode";
    private static final String COUNTRY = "Country";
    private static final String ADDRESS_LINE_1_VALUE = "123 Fake street";
    private static final String ADDRESS_LINE_3_VALUE = "The lane";
    private static final String POSTCODE_VALUE = "AB1 2CD";

    private static final String APPEND_NAME = "Bob Smith";
    private static final SolsAddress APPEND_ADDRESS =
            SolsAddress.builder().addressLine1("678").addressLine2("the street").addressLine3("Lane").country(
                    "England").county("County").postTown("town").postCode("AB1").build();

    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private static final String REGISTRY_LOCATION = "oxford";
    private static final Long CASE_ID = 12345678987654321L;
    private static final String CREST_IMAGE = "GrantOfProbateCrest";
    private static final String SEAL_IMAGE = "GrantOfProbateSeal";
    private static final String CREST_FILE_PATH = "crestImage.txt";
    private static final String SEAL_FILE_PATH = "sealImage.txt";
    private static final String WATERMARK = "draftbackground";
    private static final String WATERMARK_FILE_PATH = "watermarkImage.txt";
    private CallbackRequest callbackRequest;
    private Map<String, Object> images = new HashMap<>();
    private Registry registry = new Registry();
    private ReturnedCaseDetails returnedCaseDetails;

    @Mock
    private RegistriesProperties registriesProperties;

    @Mock
    private FileSystemResourceService fileSystemResourceService;

    @InjectMocks
    private GenericMapperService genericMapperService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        registry.setPhone("01010101010101");
        registry.setAddressLine1("registry address 1");
        registry.setAddressLine2("registry address 2");
        registry.setAddressLine3("registry address 3");
        registry.setAddressLine4("registry address 4");
        registry.setPostcode("registry postcode");
        registry.setTown("registry town");

        Map<String, Registry> registryMap = new HashMap<>();
        registryMap.put(REGISTRY_LOCATION, registry);
        registryMap.put(CTSC, registry);

        when(registriesProperties.getRegistries()).thenReturn(registryMap);

        CaseData caseData = CaseData.builder()
                .deceasedForenames("Nigel")
                .deceasedSurname("Deadsoul")
                .deceasedDateOfDeath(LocalDate.of(2015, 1, 1))
                .deceasedDateOfBirth(LocalDate.of(1990, 1, 1))
                .deceasedAddress(SolsAddress.builder().addressLine1(ADDRESS_LINE_1_VALUE).addressLine3(ADDRESS_LINE_3_VALUE)
                        .postCode(POSTCODE_VALUE).build())
                .boDeceasedTitle("Mr")
                .primaryApplicantIsApplying("Yes")
                .primaryApplicantForenames("Tim")
                .primaryApplicantSurname("Timson")
                .primaryApplicantAddress(SolsAddress.builder().addressLine1(ADDRESS_LINE_1_VALUE).postCode(POSTCODE_VALUE)
                        .build())
                .solsSolicitorFirmName("Solicitors R us")
                .solsSolicitorAddress(SolsAddress.builder().addressLine1(ADDRESS_LINE_1_VALUE).build())
                .ihtGrossValue(new BigDecimal(new BigInteger("8899"), 0))
                .ihtNetValue(new BigDecimal(new BigInteger("7787"), 0))
                .caseType("gop")
                .registryLocation("Oxford")
                .grantIssuedDate("2019-02-18")
                .solsSOTName("John Thesolicitor")
                .applicationType(ApplicationType.PERSONAL)
                .build();
        CaseDetails caseDetails = new CaseDetails(caseData, LAST_MODIFIED, CASE_ID);
        callbackRequest = new CallbackRequest(caseDetails);

        images.put(CREST_IMAGE, CREST_FILE_PATH);
        images.put(SEAL_IMAGE, SEAL_FILE_PATH);
        images.put(WATERMARK, WATERMARK_FILE_PATH);

        when(fileSystemResourceService.getFileFromResourceAsString(CREST_FILE_PATH)).thenReturn("Crest");
        when(fileSystemResourceService.getFileFromResourceAsString(SEAL_FILE_PATH)).thenReturn("Seal");
        when(fileSystemResourceService.getFileFromResourceAsString(WATERMARK_FILE_PATH)).thenReturn("Watermark");

        returnedCaseDetails = new ReturnedCaseDetails(caseData, null, Long.valueOf(1));
    }

    @Test
    public void testCaseDataIsMappedSuccessfullyWithFormattedDOD() {
        Map<String, Object> returnedMap = genericMapperService.addCaseData(callbackRequest.getCaseDetails().getData());
        expectedMappedCaseData().keySet().stream()
                .forEach((key) -> {
                    assertEquals(expectedMappedCaseData().get(key), returnedMap.get(key));
                });
    }

    @Test
    public void addCaseDataWithRegistryProperties() {
        Map<String, Object> returnedMap =
                genericMapperService.addCaseDataWithRegistryProperties(callbackRequest.getCaseDetails());
        expectedMappedRegistries().keySet().stream()
                .forEach((key) -> {
                    assertEquals(expectedMappedRegistries().get(key), ((Map) returnedMap.get("registry")).get(key));
                });
    }

    @Test
    public void testRegistryMappedSuccessfully() {
        Map<String, Object> returnedMap =
                genericMapperService.addCaseDataWithRegistryProperties(returnedCaseDetails);
        expectedMappedRegistries().keySet().stream()
                .forEach((key) -> {
                    assertEquals(expectedMappedRegistries().get(key), ((Map) returnedMap.get("registry")).get(key));
                });
        assertEquals("1", returnedMap.get("gorCaseReference").toString());
    }

    @Test
    public void testImagesMappedSuccessfully() {
        Map<String, Object> returnedMap = genericMapperService.addCaseDataWithImages(images,
                callbackRequest.getCaseDetails());
        expectedImages().keySet().stream()
                .forEach((key) -> {
                    assertEquals(expectedImages().get(key), returnedMap.get(key));
                });
    }

    @Test
    public void testAllFieldsAreAppendedToExistingMap() {
        assertEquals(expectedMappedCaseData().size() + 8,
                genericMapperService.appendExecutorDetails(expectedMappedCaseData(), APPEND_NAME, APPEND_ADDRESS).size());
    }

    @Test
    public void testAppendedValuesMatchExpected() {
        Map<String, Object> resultMap = genericMapperService.appendExecutorDetails(expectedMappedCaseData(),
                APPEND_NAME, APPEND_ADDRESS);

        assertEquals(APPEND_NAME, resultMap.get("name"));
        assertEquals(APPEND_ADDRESS.getAddressLine1(), resultMap.get("addressLine1"));
        assertEquals(APPEND_ADDRESS.getAddressLine2(), resultMap.get("addressLine2"));
        assertEquals(APPEND_ADDRESS.getAddressLine3(), resultMap.get("addressLine3"));
        assertEquals(APPEND_ADDRESS.getCounty(), resultMap.get("county"));
        assertEquals(APPEND_ADDRESS.getPostTown(), resultMap.get("postTown"));
        assertEquals(APPEND_ADDRESS.getPostCode(), resultMap.get("postCode"));
        assertEquals(APPEND_ADDRESS.getCountry(), resultMap.get("country"));
    }

    @Test
    public void testPartPopulatedAddressIsAppendedWithExistingValues() {
        SolsAddress address = SolsAddress.builder().addressLine1("321 street").postCode("AB").build();

        assertEquals("321 street", genericMapperService.appendExecutorDetails(expectedMappedCaseData(), APPEND_NAME,
                address).get("addressLine1"));
        assertEquals(null,
                genericMapperService.appendExecutorDetails(expectedMappedCaseData(), APPEND_NAME, address).get(
                        "addressLine2"));
    }

    private Map<String, Object> expectedImages() {
        Map<String, Object> expectedMap = new HashMap<>();
        expectedMap.put(SEAL_IMAGE, "image:base64:Seal");
        expectedMap.put(CREST_IMAGE, "image:base64:Crest");
        expectedMap.put(WATERMARK, "image:base64:Watermark");

        return expectedMap;
    }

    private Map<String, Object> expectedMappedRegistries() {
        Map<String, Object> expectedMap = new HashMap<>();

        expectedMap.put(REGISTRY_PHONE, registry.getPhone());
        expectedMap.put(REGISTRY_ADDRESS_LINE_1, registry.getAddressLine1());
        expectedMap.put(REGISTRY_ADDRESS_LINE_2, registry.getAddressLine2());
        expectedMap.put(REGISTRY_ADDRESS_LINE_3, registry.getAddressLine3());
        expectedMap.put(REGISTRY_ADDRESS_LINE_4, registry.getAddressLine4());
        expectedMap.put(REGISTRY_POSTCODE, registry.getPostcode());
        expectedMap.put(REGISTRY_TOWN, registry.getTown());

        return expectedMap;
    }

    private Map<String, Object> expectedMappedCaseData() {
        DECEASED_ADDRESS_VALUE.put(ADDRESS_LINE_1, ADDRESS_LINE_1_VALUE);
        DECEASED_ADDRESS_VALUE.put(ADDRESS_LINE_2, null);
        DECEASED_ADDRESS_VALUE.put(ADDRESS_LINE_3, ADDRESS_LINE_3_VALUE);
        DECEASED_ADDRESS_VALUE.put(COUNTY, null);
        DECEASED_ADDRESS_VALUE.put(POSTCODE, POSTCODE_VALUE);
        DECEASED_ADDRESS_VALUE.put(POST_TOWN, null);
        DECEASED_ADDRESS_VALUE.put(COUNTRY, null);

        PRIMARY_APPLICANT_ADDRESS_VALUE.put(ADDRESS_LINE_1, ADDRESS_LINE_1_VALUE);
        PRIMARY_APPLICANT_ADDRESS_VALUE.put(ADDRESS_LINE_2, null);
        PRIMARY_APPLICANT_ADDRESS_VALUE.put(ADDRESS_LINE_3, null);
        PRIMARY_APPLICANT_ADDRESS_VALUE.put(COUNTY, null);
        PRIMARY_APPLICANT_ADDRESS_VALUE.put(POSTCODE, POSTCODE_VALUE);
        PRIMARY_APPLICANT_ADDRESS_VALUE.put(POST_TOWN, null);
        PRIMARY_APPLICANT_ADDRESS_VALUE.put(COUNTRY, null);

        SOLICITOR_ADDRESS_VALUE.put(ADDRESS_LINE_1, ADDRESS_LINE_1_VALUE);
        SOLICITOR_ADDRESS_VALUE.put(ADDRESS_LINE_2, null);
        SOLICITOR_ADDRESS_VALUE.put(ADDRESS_LINE_3, null);
        SOLICITOR_ADDRESS_VALUE.put(COUNTY, null);
        SOLICITOR_ADDRESS_VALUE.put(POSTCODE, null);
        SOLICITOR_ADDRESS_VALUE.put(POST_TOWN, null);
        SOLICITOR_ADDRESS_VALUE.put(COUNTRY, null);

        Map<String, Object> expectedMap = new HashMap<>();

        expectedMap.put(DECEASED_FORNAME_KEY, DECEASED_FORNAME_VALUE);
        expectedMap.put(DECEASED_SURNAME_KEY, DECEASED_SURNAME_VALUE);
        expectedMap.put(DECEASED_DOD_KEY, DECEASED_DOD_VALUE);
        expectedMap.put(DECEASED_DOB_KEY, DECEASED_DOB_VALUE);
        expectedMap.put(DECEASED_ADDRESS_KEY, DECEASED_ADDRESS_VALUE);
        expectedMap.put(DECEASED_TITLE_KEY, DECEASED_TITLE_VALUE);
        expectedMap.put(PRIMARY_APPLICANT_APPLYING_KEY, PRIMARY_APPLICANT_APPLYING_VALUE);
        expectedMap.put(PRIMARY_APPLICANT_FORENAME_KEY, PRIMARY_APPLICANT_FORENAME_VALUE);
        expectedMap.put(PRIMARY_APPLICANT_SURNAME_KEY, PRIMARY_APPLICANT_SURNAME_VALUE);
        expectedMap.put(PRIMARY_APPLICANT_ADDRESS_KEY, PRIMARY_APPLICANT_ADDRESS_VALUE);
        expectedMap.put(SOLICITOR_FIRM_NAME_KEY, SOLICITOR_FIRM_NAME_VALUE);
        expectedMap.put(SOLICITOR_ADDRESS_KEY, SOLICITOR_ADDRESS_VALUE);
        expectedMap.put(IHT_GROSS_KEY, IHT_GROSS_VALUE);
        expectedMap.put(IHT_NET_KEY, IHT_NET_VALUE);
        expectedMap.put(CASE_TYPE_KEY, CASE_TYPE_VALUE);
        expectedMap.put(REGISTRY_LOCATION_KEY, REGISTRY_LOCATION_VALUE);
        expectedMap.put(GRANT_ISSUED_DATE_KEY, GRANT_ISSUED_DATE_VALUE);
        expectedMap.put(SOT_NAME_KEY, SOT_NAME_VALUE);

        return expectedMap;
    }
}
