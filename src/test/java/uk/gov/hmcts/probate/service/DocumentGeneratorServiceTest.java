package uk.gov.hmcts.probate.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.config.properties.registries.Registry;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.service.docmosis.GenericMapperService;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.probate.validator.EmailAddressNotificationValidationRule;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.Constants.CTSC;

public class DocumentGeneratorServiceTest {

    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private static final Long CASE_ID = 12345678987654321L;
    private static final String REGISTRY_LOCATION = "bristol";
    private static final String DIGITAL_GRANT_DRAFT_REISSUE_FILE_NAME = "digitalGrantDraftReissue.pdf";
    private static final String INTESTACY_REISSUE_FILE_NAME = "intestacyGrantDraftReissue.pdf";
    private static final String ADMON_WILL_REISSUE_FILE_NAME = "admonWillGrantDraftReissue.pdf";
    private static final String DIGITAL_GRANT_REISSUE_FILE_NAME = "digitalGrantReissue.pdf";
    private static final String DRAFT = "preview";
    private static final String FINAL = "final";
    private static final String SENT_EMAIL_FILE_NAME = "sentEmail.pdf";
    private CallbackRequest callbackRequest;
    private Map<String, Object> expectedMap;
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM Y HH:mm");


    @InjectMocks
    private DocumentGeneratorService documentGeneratorService;

    @Mock
    private PDFManagementService pdfManagementService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private GenericMapperService genericMapperService;

    @Mock
    private RegistryDetailsService registryDetailsService;

    @Mock
    private CallbackResponse callbackResponse;

    @Mock
    private EventValidationService eventValidationService;

    @Mock
    private List<EmailAddressNotificationValidationRule> emailAddressNotificationValidationRules;

    @Mock
    private DocumentService documentService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        Registry registry = new Registry();
        registry.setPhone("01010101010101");
        registry.setAddressLine1("addressLine1");
        registry.setAddressLine2("addressLine2");
        registry.setAddressLine3("addressLine3");
        registry.setAddressLine4("addressLine4");
        registry.setPostcode("postcode");
        registry.setTown("town");

        Map<String, Registry> registryMap = new HashMap<>();
        registryMap.put(REGISTRY_LOCATION, registry);
        registryMap.put(CTSC, registry);

        CaseDetails caseDetails = new CaseDetails(CaseData.builder().caseType("gop").registryLocation("Bristol").build(),
                LAST_MODIFIED, CASE_ID);
        callbackRequest = new CallbackRequest(caseDetails);

        CaseDetails returnedCaseDetails = caseDetails;
        returnedCaseDetails.setRegistryTelephone("01010101010101");
        returnedCaseDetails.setRegistryAddressLine1("addressLine1");
        returnedCaseDetails.setRegistryAddressLine2("addressLine2");
        returnedCaseDetails.setRegistryAddressLine3("addressLine3");
        returnedCaseDetails.setRegistryAddressLine4("addressLine4");
        returnedCaseDetails.setRegistryPostcode("postcode");
        returnedCaseDetails.setRegistryTown("town");
        returnedCaseDetails.setCtscTelephone("01010101010101");

        ObjectMapper mapper = new ObjectMapper();
        expectedMap = mapper.convertValue(CaseData.builder().caseType("gop").registryLocation("Bristol").build(), Map.class);

        when(registryDetailsService.getRegistryDetails(caseDetails)).thenReturn(returnedCaseDetails);

        when(genericMapperService.addCaseDataWithImages(any(), any())).thenReturn(expectedMap);

        when(pdfManagementService
                .generateDocmosisDocumentAndUpload(eq(expectedMap), any())).thenReturn(Document.builder().build());

        when(pdfManagementService.getDecodedSignature()).thenReturn("decodedSignature");

      //  when(pdfManagementService.generateAndUpload(any(SentEmail.class), SENT_EMAIL).getDocumentFileName());

        doNothing().when(documentService).expire(any(CallbackRequest.class), any());
    }

    @Test
    public void testGenerateReissueDraftProducesCorrectDocumentForGOP() {
        when(pdfManagementService.generateDocmosisDocumentAndUpload(expectedMap,
                DocumentType.DIGITAL_GRANT_DRAFT_REISSUE))
                .thenReturn(Document.builder().documentFileName(DIGITAL_GRANT_DRAFT_REISSUE_FILE_NAME).build());
        assertEquals(DIGITAL_GRANT_DRAFT_REISSUE_FILE_NAME,
                documentGeneratorService.generateGrantReissue(callbackRequest, DRAFT).getDocumentFileName());
    }

    @Test
    public void testGenerateReissueDraftProducesCorrectDocumentForIntestacy() {
        CaseDetails caseDetails =
                new CaseDetails(CaseData.builder().caseType("intestacy").registryLocation("Bristol").build(),
                        LAST_MODIFIED, CASE_ID);
        callbackRequest = new CallbackRequest(caseDetails);
        when(pdfManagementService.generateDocmosisDocumentAndUpload(expectedMap,
                DocumentType.INTESTACY_GRANT_DRAFT_REISSUE))
                .thenReturn(Document.builder().documentFileName(INTESTACY_REISSUE_FILE_NAME).build());
        assertEquals(INTESTACY_REISSUE_FILE_NAME,
                documentGeneratorService.generateGrantReissue(callbackRequest, DRAFT).getDocumentFileName());
    }

    @Test
    public void testGenerateReissueDraftProducesCorrectDocumentForAdmonWill() {
        CaseDetails caseDetails =
                new CaseDetails(CaseData.builder().caseType("admonWill").registryLocation("Bristol").build(),
                        LAST_MODIFIED, CASE_ID);
        callbackRequest = new CallbackRequest(caseDetails);
        when(pdfManagementService.generateDocmosisDocumentAndUpload(expectedMap,
                DocumentType.ADMON_WILL_GRANT_DRAFT_REISSUE)).thenReturn(Document.builder()
                .documentFileName(ADMON_WILL_REISSUE_FILE_NAME).build());
        assertEquals(ADMON_WILL_REISSUE_FILE_NAME,
                documentGeneratorService.generateGrantReissue(callbackRequest, DRAFT).getDocumentFileName());
    }

    @Test
    public void testGenerateReissueProducesFinalVersionForGOP() {
        when(pdfManagementService.generateDocmosisDocumentAndUpload(expectedMap, DocumentType.DIGITAL_GRANT_REISSUE))
                .thenReturn(Document.builder().documentFileName(DIGITAL_GRANT_REISSUE_FILE_NAME).build());
        assertEquals(DIGITAL_GRANT_REISSUE_FILE_NAME, documentGeneratorService.generateGrantReissue(callbackRequest,
                FINAL).getDocumentFileName());
    }

    @Test
    public void testInvalidVersionDefaultsToDraftVersion() {
        when(pdfManagementService.generateDocmosisDocumentAndUpload(expectedMap,
                DocumentType.DIGITAL_GRANT_DRAFT_REISSUE))
                .thenReturn(Document.builder().documentFileName(DIGITAL_GRANT_DRAFT_REISSUE_FILE_NAME).build());
        assertEquals(DIGITAL_GRANT_DRAFT_REISSUE_FILE_NAME,
                documentGeneratorService.generateGrantReissue(callbackRequest, "INVALID").getDocumentFileName());
    }
}
