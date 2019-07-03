package uk.gov.hmcts.probate.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.config.properties.registries.Registry;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.State;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.service.docmosis.GenericMapperService;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.probate.validator.EmailAddressNotificationValidationRule;
import uk.gov.service.notify.NotificationClientException;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.Constants.CTSC;

public class DocumentGeneratorServiceTest {

    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private static final Long CASE_ID = 12345678987654321L;
    private static final String REGISTRY_LOCATION = "bristol";
    private static final String DIGITAL_GRANT_REISSUE_FILE_NAME = "digitalGrantDraftReissue.pdf";
    private static final String INTESTACY_REISSUE_FILE_NAME = "intestacyGrantDraftReissue.pdf";
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

        when(genericMapperService.caseDataWithImages(any(), any())).thenReturn(expectedMap);

        when(pdfManagementService
                .generateDocmosisDocumentAndUpload(eq(expectedMap), any(), anyBoolean())).thenReturn(Document.builder().build());

      //  when(pdfManagementService.generateAndUpload(any(SentEmail.class), SENT_EMAIL).getDocumentFileName());

        doNothing().when(documentService).expire(any(CallbackRequest.class), any());
    }

    @Test
    public void testGenerateReissueDraftProducesCorrectDocumentForGOP() {
        when(pdfManagementService.generateDocmosisDocumentAndUpload(expectedMap,
                DocumentType.DIGITAL_GRANT_DRAFT_REISSUE, true))
                .thenReturn(Document.builder().documentFileName(DIGITAL_GRANT_REISSUE_FILE_NAME).build());
        assertEquals(DIGITAL_GRANT_REISSUE_FILE_NAME,
                documentGeneratorService.generateGrantReissueDraft(callbackRequest).getDocumentFileName());
    }

    @Test
    public void testGenerateReissueDraftProducesCorrectDocumentForIntestacy() {
        CaseDetails caseDetails =
                new CaseDetails(CaseData.builder().caseType("intestacy").registryLocation("Bristol").build(),
                LAST_MODIFIED, CASE_ID);
        callbackRequest = new CallbackRequest(caseDetails);
        when(pdfManagementService.generateDocmosisDocumentAndUpload(expectedMap,
                DocumentType.INTESTACY_GRANT_DRAFT_REISSUE, true))
                .thenReturn(Document.builder().documentFileName(INTESTACY_REISSUE_FILE_NAME).build());
        assertEquals(INTESTACY_REISSUE_FILE_NAME,
                documentGeneratorService.generateGrantReissueDraft(callbackRequest).getDocumentFileName());
    }

    @Test
    public void testGenerateReissueGrantProducesEmailCorrectly() throws NotificationClientException {
        CaseDetails caseDetails =
                new CaseDetails(CaseData.builder()
                        .caseType("gop")
                        .applicationType(ApplicationType.PERSONAL)
                        .primaryApplicantEmailAddress("test@test.com")
                        .registryLocation("Bristol")
                        .boEmailGrantReIssuedNotificationRequested("Yes")
                        .build(),
                        LAST_MODIFIED, CASE_ID);
        callbackRequest = new CallbackRequest(caseDetails);

        when(eventValidationService.validateEmailRequest(callbackRequest, emailAddressNotificationValidationRules)).thenReturn(callbackResponse);
        when(notificationService.sendEmail(State.GRANT_REISSUED, caseDetails)).thenReturn(Document.builder().documentFileName(SENT_EMAIL_FILE_NAME).build());
        assertEquals(SENT_EMAIL_FILE_NAME, documentGeneratorService.generateGrantReissue(callbackRequest).get(0).getDocumentFileName());
    }
}
