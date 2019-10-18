package uk.gov.hmcts.probate.service.docmosis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.config.properties.registries.RegistriesProperties;
import uk.gov.hmcts.probate.config.properties.registries.Registry;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicList;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicListItem;
import uk.gov.hmcts.probate.model.ccd.raw.ParagraphDetail;
import uk.gov.hmcts.probate.model.ccd.raw.ParagraphDetailEnablementType;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.AddressFormatterService;
import uk.gov.hmcts.probate.service.DateFormatterService;
import uk.gov.hmcts.probate.service.ccd.CcdReferenceFormatterService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;


public class PreviewLetterServiceTest {

    @InjectMocks
    private PreviewLetterService previewLetterService;

    @Mock
    private RegistriesProperties registriesPropertiesMock;

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
    private static final String PERSONALISATION_CASE_REFERENCE = "caseReference";
    private static final String PERSONALISATION_GENERATED_DATE = "generatedDate";
    private static final String PERSONALISATION_REGISTRY = "registry";
    private static final String PERSONALISATION_PA8AURL = "PA8AURL";
    private static final String PERSONALISATION_PA8BURL = "PA8BURL";
    private static final String PERSONALISATION_CAVEAT_REFERENCE = "caveatReference";
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private CaseDetails caseDetails;
    Registry registry = new Registry();
    ObjectMapper mapper = new ObjectMapper();
    Map<String, Registry> registries = new HashMap<>();

    private static final String YES = "Yes";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        DynamicListItem dynamicListItem = DynamicListItem.builder().code("will").label("WILL").build();
        DynamicListItem dynamicListItem2 = DynamicListItem.builder().code("codicil").label("CODICIL").build();

        List<DynamicListItem> dynamicList = new ArrayList<DynamicListItem>();
        dynamicList.add(dynamicListItem);
        dynamicList.add(dynamicListItem2);

        List<List<DynamicListItem>> listItems = new ArrayList<List<DynamicListItem>>();
        listItems.add(dynamicList);

        registry.setName("leeds");
        registry.setPhone("123456789");
        registries = mapper.convertValue(registry, Map.class);

        DynamicList dynamicList1 = DynamicList.builder().listItems(listItems.get(0)).value(DynamicListItem.builder().build()).build();

        List<CollectionMember<ParagraphDetail>> paragraphDetails = Arrays.asList(
            new CollectionMember<ParagraphDetail>("id",
                ParagraphDetail.builder()
                    .code("IHT421Await")
                    .enableType(ParagraphDetailEnablementType.Text)
                    .label("Awaiting IHT421")
                    .textValue("primary fn primary sn")
                    .templateName("template1.docx")
                    .build()),
            new CollectionMember<ParagraphDetail>("id",
                ParagraphDetail.builder()
                    .code("FreeText")
                    .enableType(ParagraphDetailEnablementType.TextArea)
                    .textAreaValue("textArea")
                    .templateName("template1.docx")
                    .build()),
            new CollectionMember<ParagraphDetail>("id",
                ParagraphDetail.builder()
                    .code("WillAnyOther")
                    .enableType(ParagraphDetailEnablementType.List)
                    .label("label")
                    .dynamicList(dynamicList1)
                    .templateName("template2.docx")
                    .build()),
            new CollectionMember<ParagraphDetail>("id",
                ParagraphDetail.builder()
                    .code("Caseworker")
                    .enableType(ParagraphDetailEnablementType.Text)
                    .label("Caseworker")
                    .textValue("primary fn primary sn")
                    .templateName("template3.docx")
                    .build()));

        CaseData caseData = CaseData.builder()
            .registryLocation("leeds")
            .paragraphDetails(paragraphDetails)
            .build();
        caseDetails = new CaseDetails(caseData, LAST_MODIFIED, ID);
        caseDetails.setRegistryTelephone("123456789");

        when(registriesPropertiesMock.getRegistries()).thenReturn(registries);
        when(genericMapperService.addCaseDataWithRegistryProperties(caseDetails)).thenReturn(mapper.convertValue(caseDetails, Map.class));


    }

    @Test
    public void testAddLetterDataNoMatchedDetailOptional() {
        DateFormat generatedDateFormat = new SimpleDateFormat(DATE_INPUT_FORMAT);

        Map<String, Object> placeholders = previewLetterService.addLetterData(caseDetails);

        assertEquals(3, ((List) placeholders.get("templateList")).size());
        assertEquals(ccdReferenceFormatterServiceMock.getFormattedCaseReference("1234567891234567"),
            placeholders.get(PERSONALISATION_CASE_REFERENCE));
        assertEquals(registries.get(
            caseDetails.getData().getRegistryLocation().toLowerCase()),
            placeholders.get(PERSONALISATION_REGISTRY));
    }
}

