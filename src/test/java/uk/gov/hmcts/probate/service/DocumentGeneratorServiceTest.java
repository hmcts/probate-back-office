package uk.gov.hmcts.probate.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.config.properties.registries.Registry;
import uk.gov.hmcts.probate.model.*;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.service.docmosis.DocumentTemplateService;
import uk.gov.hmcts.probate.service.docmosis.GenericMapperService;
import uk.gov.hmcts.probate.service.docmosis.PreviewLetterService;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.probate.validator.EmailAddressNotificationValidationRule;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.Constants.CTSC;

public class DocumentGeneratorServiceTest {

    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private static final Long CASE_ID = 12345678987654321L;
    private static final String REGISTRY_LOCATION = "bristol";
    private static final String DIGITAL_GRANT_DRAFT_REISSUE_FILE_NAME = "digitalGrantDraftReissue.pdf";
    private static final String INTESTACY_DRAFT_REISSUE_FILE_NAME = "intestacyGrantDraftReissue.pdf";
    private static final String ADMON_WILL_DRAFT_REISSUE_FILE_NAME = "admonWillGrantDraftReissue.pdf";
    private static final String DIGITAL_GRANT_REISSUE_FILE_NAME = "digitalGrantReissue.pdf";
    private static final String INTESTACY_REISSUE_FILE_NAME = "intestacyGrantReissue.pdf";
    private static final String ADMON_WILL_REISSUE_FILE_NAME = "admonWillGrantReissue.pdf";
    private static final String ADMON_WILL_FINAL_FILE_NAME = "welshAdmonWillGrantFinal.pdf";
    private static final String ADMON_WILL_DRAFT_FILE_NAME = "welshAdmonWillGrantDraft.pdf";
    private static final String DIGITAL_GRANT_DRAFT_FILE_NAME = "welshDigitalGrantDraft.pdf";
    private static final String DIGITAL_GRANT_FINAL_FILE_NAME = "welshDigitalGrantFinal.pdf";
    private static final String WELSH_INTESTACY_GRANT_DRAFT_FILE_NAME = "welshIntestacyGrantDraft.pdf";
    private static final String WELSH_INTESTACY_GRANT_FINAL_FILE_NAME = "welshIntestacyGrantFinal.pdf";


    private CallbackRequest callbackRequest;
    private CallbackRequest callbackRequestSolsGop;
    private CallbackRequest callbackRequestSolsAdmon;
    private CallbackRequest callbackRequestSolsIntestacy;
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
    private PreviewLetterService previewLetterService;

    @Mock
    private CallbackResponse callbackResponse;

    @Mock
    private EventValidationService eventValidationService;

    @Mock
    private List<EmailAddressNotificationValidationRule> emailAddressNotificationValidationRules;

    @Mock
    private DocumentService documentService;

    @Mock
    private DocumentTemplateService documentTemplateService;

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

        CaseDetails caseDetails = new CaseDetails(CaseData.builder()
                .caseType("gop")
                .registryLocation("Bristol")
                .applicationType(ApplicationType.PERSONAL).build(),
                LAST_MODIFIED, CASE_ID);
        callbackRequest = new CallbackRequest(caseDetails);

        CaseDetails caseDetailsSolsGop = new CaseDetails(CaseData.builder()
                .caseType("gop")
                .registryLocation("Bristol")
                .applicationType(ApplicationType.SOLICITOR)
                .solsSolicitorAddress(SolsAddress.builder().build())
                .solsSolicitorFirmName("firmName").build(),
                LAST_MODIFIED, CASE_ID);
        callbackRequestSolsGop = new CallbackRequest(caseDetailsSolsGop);

        CaseDetails caseDetailsSolsAdmon = new CaseDetails(CaseData.builder()
                .caseType("admonWill")
                .registryLocation("Bristol")
                .applicationType(ApplicationType.SOLICITOR).build(),
                LAST_MODIFIED, CASE_ID);
        callbackRequestSolsAdmon = new CallbackRequest(caseDetailsSolsAdmon);

        CaseDetails caseDetailsSolsIntestacy = new CaseDetails(CaseData.builder()
                .caseType("intestacy")
                .registryLocation("Bristol")
                .applicationType(ApplicationType.SOLICITOR).build(),
                LAST_MODIFIED, CASE_ID);
        callbackRequestSolsIntestacy = new CallbackRequest(caseDetailsSolsIntestacy);


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
                .generateDocmosisDocumentAndUpload(any(), any())).thenReturn(Document.builder().build());

        when(pdfManagementService.getDecodedSignature()).thenReturn("decodedSignature");

        doNothing().when(documentService).expire(any(CallbackRequest.class), any());

        when(genericMapperService.addCaseData(caseDetails.getData())).thenReturn(expectedMap);
        when(genericMapperService.addCaseDataWithRegistryProperties(caseDetails)).thenReturn(expectedMap);

        when(previewLetterService.addLetterData(caseDetails)).thenReturn(expectedMap);

    }

    @Test
    public void testGenerateReissueDraftProducesCorrectDocumentForGOP() {
        when(pdfManagementService.generateDocmosisDocumentAndUpload(expectedMap,
                DocumentType.DIGITAL_GRANT_REISSUE_DRAFT))
                .thenReturn(Document.builder().documentFileName(DIGITAL_GRANT_DRAFT_REISSUE_FILE_NAME).build());

        when(documentTemplateService.getTemplateId(LanguagePreference.ENGLISH, DocumentStatus.PREVIEW, DocumentIssueType.REISSUE, DocumentCaseType.GOP )).thenReturn(DocumentType.DIGITAL_GRANT_REISSUE_DRAFT);

        assertEquals(DIGITAL_GRANT_DRAFT_REISSUE_FILE_NAME,
                documentGeneratorService.generateGrantReissue(callbackRequest, DocumentStatus.PREVIEW, Optional.of(DocumentIssueType.REISSUE)).getDocumentFileName());
    }

    @Test
    public void testGenerateReissueDraftProducesCorrectDocumentForIntestacy() {
        CaseDetails caseDetails =
                new CaseDetails(CaseData.builder().caseType("intestacy").registryLocation("Bristol").build(),
                        LAST_MODIFIED, CASE_ID);
        callbackRequest = new CallbackRequest(caseDetails);
        when(pdfManagementService.generateDocmosisDocumentAndUpload(expectedMap,
                DocumentType.INTESTACY_GRANT_REISSUE_DRAFT))
                .thenReturn(Document.builder().documentFileName(INTESTACY_DRAFT_REISSUE_FILE_NAME).build());

        when(documentTemplateService.getTemplateId(LanguagePreference.ENGLISH, DocumentStatus.PREVIEW, DocumentIssueType.REISSUE, DocumentCaseType.INTESTACY )).thenReturn(DocumentType.INTESTACY_GRANT_REISSUE_DRAFT);

        assertEquals(INTESTACY_DRAFT_REISSUE_FILE_NAME,
                documentGeneratorService.generateGrantReissue(callbackRequest, DocumentStatus.PREVIEW, Optional.of(DocumentIssueType.REISSUE)).getDocumentFileName());
    }

    @Test
    public void testGenerateReissueDraftProducesCorrectDocumentForAdmonWill() {
        CaseDetails caseDetails =
                new CaseDetails(CaseData.builder().caseType("admonWill").registryLocation("Bristol").build(),
                        LAST_MODIFIED, CASE_ID);
        callbackRequest = new CallbackRequest(caseDetails);
        when(pdfManagementService.generateDocmosisDocumentAndUpload(expectedMap,
                DocumentType.ADMON_WILL_GRANT_REISSUE_DRAFT)).thenReturn(Document.builder()
                .documentFileName(ADMON_WILL_DRAFT_REISSUE_FILE_NAME).build());

        when(documentTemplateService.getTemplateId(LanguagePreference.ENGLISH, DocumentStatus.PREVIEW, DocumentIssueType.REISSUE, DocumentCaseType.ADMON_WILL )).thenReturn(DocumentType.ADMON_WILL_GRANT_REISSUE_DRAFT);

        assertEquals(ADMON_WILL_DRAFT_REISSUE_FILE_NAME,
                documentGeneratorService.generateGrantReissue(callbackRequest, DocumentStatus.PREVIEW, Optional.of(DocumentIssueType.REISSUE)).getDocumentFileName());
    }

    @Test
    public void testGenerateReissueProducesFinalVersionForGOP() {
        when(pdfManagementService.generateDocmosisDocumentAndUpload(expectedMap, DocumentType.DIGITAL_GRANT_REISSUE))
                .thenReturn(Document.builder().documentFileName(DIGITAL_GRANT_REISSUE_FILE_NAME).build());

        when(documentTemplateService.getTemplateId(LanguagePreference.ENGLISH, DocumentStatus.FINAL, DocumentIssueType.REISSUE, DocumentCaseType.GOP )).thenReturn(DocumentType.DIGITAL_GRANT_REISSUE);

        assertEquals(DIGITAL_GRANT_REISSUE_FILE_NAME, documentGeneratorService.generateGrantReissue(callbackRequest,
                DocumentStatus.FINAL, Optional.of(DocumentIssueType.REISSUE)).getDocumentFileName());
    }

    @Test
    public void testGenerateReissueProducesFinalVersionForIntestacy() {
        CaseDetails caseDetails =
                new CaseDetails(CaseData.builder().caseType("intestacy").registryLocation("Bristol").build(),
                        LAST_MODIFIED, CASE_ID);
        callbackRequest = new CallbackRequest(caseDetails);
        when(pdfManagementService.generateDocmosisDocumentAndUpload(expectedMap,
                DocumentType.INTESTACY_GRANT_REISSUE))
                .thenReturn(Document.builder().documentFileName(INTESTACY_REISSUE_FILE_NAME).build());

        when(documentTemplateService.getTemplateId(LanguagePreference.ENGLISH, DocumentStatus.FINAL, DocumentIssueType.REISSUE, DocumentCaseType.INTESTACY )).thenReturn(DocumentType.INTESTACY_GRANT_REISSUE);

        assertEquals(INTESTACY_REISSUE_FILE_NAME,
                documentGeneratorService.generateGrantReissue(callbackRequest, DocumentStatus.FINAL, Optional.of(DocumentIssueType.REISSUE)).getDocumentFileName());
    }

    @Test
    public void testGenerateReissueProducesFinalVersionForAdmonWill() {
        CaseDetails caseDetails =
                new CaseDetails(CaseData.builder().caseType("admonWill").registryLocation("Bristol").build(),
                        LAST_MODIFIED, CASE_ID);
        callbackRequest = new CallbackRequest(caseDetails);
        when(pdfManagementService.generateDocmosisDocumentAndUpload(expectedMap,
                DocumentType.ADMON_WILL_GRANT_REISSUE)).thenReturn(Document.builder()
                .documentFileName(ADMON_WILL_REISSUE_FILE_NAME).build());

        when(documentTemplateService.getTemplateId(LanguagePreference.ENGLISH, DocumentStatus.FINAL, DocumentIssueType.REISSUE, DocumentCaseType.ADMON_WILL )).thenReturn(DocumentType.ADMON_WILL_GRANT_REISSUE);

        assertEquals(ADMON_WILL_REISSUE_FILE_NAME,
                documentGeneratorService.generateGrantReissue(callbackRequest, DocumentStatus.FINAL, Optional.of(DocumentIssueType.REISSUE)).getDocumentFileName());
    }

    @Test
    public void testGenerateProducesWelshAdmonWillVersionFinal() {
        CaseDetails caseDetails =
                new CaseDetails(CaseData.builder().caseType("admonWill").registryLocation("Bristol").
                        languagePreferenceWelsh(Constants.YES).build(),
                        LAST_MODIFIED, CASE_ID);

        callbackRequest = new CallbackRequest(caseDetails);
        when(pdfManagementService.generateDocmosisDocumentAndUpload(expectedMap,
                DocumentType.WELSH_ADMON_WILL_GRANT)).thenReturn(Document.builder()
                .documentFileName(ADMON_WILL_FINAL_FILE_NAME).build());

        when(documentTemplateService.getTemplateId(LanguagePreference.WELSH, DocumentStatus.FINAL, DocumentIssueType.REISSUE, DocumentCaseType.ADMON_WILL )).thenReturn(DocumentType.WELSH_ADMON_WILL_GRANT);


        assertEquals(ADMON_WILL_FINAL_FILE_NAME,
                documentGeneratorService.generateGrant(callbackRequest, DocumentStatus.FINAL,DocumentIssueType.REISSUE).getDocumentFileName());
    }

    @Test
    public void testGenerateProducesWelshAdmonWillVersionDraft() {
        CaseDetails caseDetails =
                new CaseDetails(CaseData.builder().caseType("admonWill").registryLocation("Bristol").
                        languagePreferenceWelsh(Constants.YES).build(),
                        LAST_MODIFIED, CASE_ID);

        callbackRequest = new CallbackRequest(caseDetails);
        when(pdfManagementService.generateDocmosisDocumentAndUpload(expectedMap,
                DocumentType.WELSH_ADMON_WILL_GRANT_DRAFT)).thenReturn(Document.builder()
                .documentFileName(ADMON_WILL_DRAFT_FILE_NAME).build());

        when(documentTemplateService.getTemplateId(LanguagePreference.WELSH, DocumentStatus.PREVIEW, DocumentIssueType.REISSUE, DocumentCaseType.ADMON_WILL )).thenReturn(DocumentType.WELSH_ADMON_WILL_GRANT_DRAFT);

        assertEquals(ADMON_WILL_DRAFT_FILE_NAME,
                documentGeneratorService.generateGrant(callbackRequest, DocumentStatus.PREVIEW,DocumentIssueType.REISSUE).getDocumentFileName());
    }

    @Test
    public void testGenerateProducesWelshDigitalGrantFinal() {
        CaseDetails caseDetails =
                new CaseDetails(CaseData.builder().caseType("gop").registryLocation("Bristol").
                        languagePreferenceWelsh(Constants.YES).build(),
                        LAST_MODIFIED, CASE_ID);

        callbackRequest = new CallbackRequest(caseDetails);
        when(pdfManagementService.generateDocmosisDocumentAndUpload(expectedMap,
                DocumentType.WELSH_DIGITAL_GRANT)).thenReturn(Document.builder()
                .documentFileName(DIGITAL_GRANT_FINAL_FILE_NAME).build());

        when(documentTemplateService.getTemplateId(LanguagePreference.WELSH, DocumentStatus.FINAL, DocumentIssueType.REISSUE,DocumentCaseType.GOP )).thenReturn(DocumentType.WELSH_DIGITAL_GRANT);

        assertEquals(DIGITAL_GRANT_FINAL_FILE_NAME,
                documentGeneratorService.generateGrant(callbackRequest, DocumentStatus.FINAL, DocumentIssueType.REISSUE).getDocumentFileName());
    }

    @Test
    public void testGenerateProducesWelshDigitalGrantDraft() {
        CaseDetails caseDetails =
                new CaseDetails(CaseData.builder().caseType("gop").registryLocation("Bristol").
                        languagePreferenceWelsh(Constants.YES).build(),
                        LAST_MODIFIED, CASE_ID);

        callbackRequest = new CallbackRequest(caseDetails);
        when(pdfManagementService.generateDocmosisDocumentAndUpload(expectedMap,
                DocumentType.WELSH_DIGITAL_GRANT_DRAFT)).thenReturn(Document.builder()
                .documentFileName(DIGITAL_GRANT_DRAFT_FILE_NAME).build());

        when(documentTemplateService.getTemplateId(LanguagePreference.WELSH, DocumentStatus.PREVIEW, DocumentIssueType.REISSUE,DocumentCaseType.GOP )).thenReturn(DocumentType.WELSH_DIGITAL_GRANT_DRAFT);

        assertEquals(DIGITAL_GRANT_DRAFT_FILE_NAME,
                documentGeneratorService.generateGrant(callbackRequest, DocumentStatus.PREVIEW, DocumentIssueType.REISSUE).getDocumentFileName());
    }

    @Test
    public void testGenerateProducesWelshIntestacyGrantDraft() {
        CaseDetails caseDetails =
                new CaseDetails(CaseData.builder().caseType("intestacy").registryLocation("Bristol").
                        languagePreferenceWelsh(Constants.YES).build(),
                        LAST_MODIFIED, CASE_ID);

        callbackRequest = new CallbackRequest(caseDetails);
        when(pdfManagementService.generateDocmosisDocumentAndUpload(expectedMap,
                DocumentType.WELSH_INTESTACY_GRANT_DRAFT)).thenReturn(Document.builder()
                .documentFileName(WELSH_INTESTACY_GRANT_DRAFT_FILE_NAME).build());

        when(documentTemplateService.getTemplateId(LanguagePreference.WELSH, DocumentStatus.PREVIEW, DocumentIssueType.REISSUE,DocumentCaseType.INTESTACY )).thenReturn(DocumentType.WELSH_INTESTACY_GRANT_DRAFT);

        assertEquals(WELSH_INTESTACY_GRANT_DRAFT_FILE_NAME,
                documentGeneratorService.generateGrant(callbackRequest, DocumentStatus.PREVIEW, DocumentIssueType.REISSUE).getDocumentFileName());
    }

    @Test
    public void testGenerateProducesWelshIntestacyGrantFinal() {
        CaseDetails caseDetails =
                new CaseDetails(CaseData.builder().caseType("intestacy").registryLocation("Bristol").
                        languagePreferenceWelsh(Constants.YES).build(),
                        LAST_MODIFIED, CASE_ID);

        callbackRequest = new CallbackRequest(caseDetails);
        when(pdfManagementService.generateDocmosisDocumentAndUpload(expectedMap,
                DocumentType.WELSH_INTESTACY_GRANT)).thenReturn(Document.builder()
                .documentFileName(WELSH_INTESTACY_GRANT_FINAL_FILE_NAME).build());

        when(documentTemplateService.getTemplateId(LanguagePreference.WELSH, DocumentStatus.FINAL, DocumentIssueType.REISSUE, DocumentCaseType.INTESTACY )).thenReturn(DocumentType.WELSH_INTESTACY_GRANT);

        assertEquals(WELSH_INTESTACY_GRANT_FINAL_FILE_NAME,
                documentGeneratorService.generateGrant(callbackRequest, DocumentStatus.FINAL, DocumentIssueType.REISSUE).getDocumentFileName());
    }

/////////////////////////////////////////
    @Test
    public void testGenerateReissueProducesNewEdgeCaseDocumentForDraft() {
        CaseDetails caseDetails =
                new CaseDetails(CaseData.builder().caseType("edgeCase").registryLocation("Bristol").build(),
                        LAST_MODIFIED, CASE_ID);
        callbackRequest = new CallbackRequest(caseDetails);
        assertEquals(DocumentType.EDGE_CASE,
                documentGeneratorService.generateGrantReissue(callbackRequest, DocumentStatus.PREVIEW, Optional.empty()).getDocumentType());
    }

    @Test
    public void testGenerateReissueProducesNewEdgeCaseDocumentForFinal() {
        CaseDetails caseDetails =
                new CaseDetails(CaseData.builder().caseType("edgeCase").registryLocation("Bristol").build(),
                        LAST_MODIFIED, CASE_ID);
        callbackRequest = new CallbackRequest(caseDetails);
        assertEquals(DocumentType.EDGE_CASE,
                documentGeneratorService.generateGrantReissue(callbackRequest, DocumentStatus.FINAL, Optional.empty()).getDocumentType());
    }

    @Test
    public void testGenerateCoversheetReturnsCorrectDocumentType() {
        when(pdfManagementService.generateDocmosisDocumentAndUpload(expectedMap, DocumentType.GRANT_COVERSHEET))
                .thenReturn(Document.builder().documentType(DocumentType.GRANT_COVERSHEET).build());
        assertEquals(DocumentType.GRANT_COVERSHEET, documentGeneratorService.generateCoversheet(callbackRequest).getDocumentType());
    }

    @Test
    public void testGenerateCoversheetReturnsCorrectDocumentTypeForSpecificExec() {
        when(pdfManagementService.generateDocmosisDocumentAndUpload(expectedMap, DocumentType.GRANT_COVERSHEET))
                .thenReturn(Document.builder().documentType(DocumentType.GRANT_COVERSHEET).build());
        assertEquals(DocumentType.GRANT_COVERSHEET,
                documentGeneratorService.generateCoversheet(callbackRequest, "Bob Smith", SolsAddress.builder().build()).getDocumentType());
    }

    @Test
    public void testGenerateRequestInformationReturnsCorrectDocumentType() {
        ExecutorsApplyingNotification executor = ExecutorsApplyingNotification.builder().name("Bob").build();
        when(pdfManagementService.generateDocmosisDocumentAndUpload(expectedMap,DocumentType.SOT_INFORMATION_REQUEST))
                .thenReturn(Document.builder().documentType(DocumentType.SOT_INFORMATION_REQUEST).build());
        assertEquals(DocumentType.SOT_INFORMATION_REQUEST,
                documentGeneratorService.generateRequestForInformation(callbackRequest.getCaseDetails(), executor).getDocumentType());
    }

    @Test
    public void testGenerateRequestInformationReturnsCorrectDocumentTypeForSolicitors() {
        expectedMap.clear();
        expectedMap.put("applicantName", "Bob Sot");
        expectedMap.put("fullRedec", "No");
        CaseDetails caseDetails = new CaseDetails(CaseData.builder().solsSOTName("Bob Sot")
                .applicationType(ApplicationType.SOLICITOR)
                .caseType("gop")
                .registryLocation("Bristol")
                .build(), LAST_MODIFIED, CASE_ID);
        ExecutorsApplyingNotification executor = ExecutorsApplyingNotification.builder().name("boB").build();
        when(pdfManagementService.generateDocmosisDocumentAndUpload(expectedMap, DocumentType.SOT_INFORMATION_REQUEST))
                .thenReturn(Document.builder().documentType(DocumentType.SOT_INFORMATION_REQUEST).build());
        assertEquals(DocumentType.SOT_INFORMATION_REQUEST,
                documentGeneratorService.generateRequestForInformation(caseDetails, executor).getDocumentType());
    }

    @Test
    public void testStatementOfTruthReturnedSuccessfullyForPersonalCase() {
        when(pdfManagementService.generateDocmosisDocumentAndUpload(expectedMap, DocumentType.STATEMENT_OF_TRUTH))
                .thenReturn(Document.builder().documentType(DocumentType.STATEMENT_OF_TRUTH).build());
        assertEquals(Document.builder().documentType(DocumentType.STATEMENT_OF_TRUTH).build(),
                documentGeneratorService.generateSoT(callbackRequest));
    }

    @Test
    public void testStatementOfTruthReturnedSuccessfullyForSolsGopCase() {
        when(pdfManagementService.generateAndUpload(callbackRequestSolsGop, DocumentType.LEGAL_STATEMENT_PROBATE))
                .thenReturn(Document.builder().documentType(DocumentType.LEGAL_STATEMENT_PROBATE).build());
        assertEquals(Document.builder().documentType(DocumentType.LEGAL_STATEMENT_PROBATE).build(),
                documentGeneratorService.generateSoT(callbackRequestSolsGop));
    }

    @Test
    public void testStatementOfTruthReturnedSuccessfullyForSolsIntestacyCase() {
        when(pdfManagementService.generateAndUpload(callbackRequestSolsIntestacy, DocumentType.LEGAL_STATEMENT_INTESTACY))
                .thenReturn(Document.builder().documentType(DocumentType.LEGAL_STATEMENT_INTESTACY).build());
        assertEquals(Document.builder().documentType(DocumentType.LEGAL_STATEMENT_INTESTACY).build(),
                documentGeneratorService.generateSoT(callbackRequestSolsIntestacy));
    }

    @Test
    public void testStatementOfTruthReturnedSuccessfullyForSolsAdmonWillCase() {
        when(pdfManagementService.generateAndUpload(callbackRequestSolsAdmon, DocumentType.LEGAL_STATEMENT_ADMON))
                .thenReturn(Document.builder().documentType(DocumentType.LEGAL_STATEMENT_ADMON).build());
        assertEquals(Document.builder().documentType(DocumentType.LEGAL_STATEMENT_ADMON).build(),
                documentGeneratorService.generateSoT(callbackRequestSolsAdmon));
    }

    @Test
    public void testGenerateLetter() {
        when(pdfManagementService.generateDocmosisDocumentAndUpload(expectedMap, DocumentType.ASSEMBLED_LETTER))
                .thenReturn(Document.builder().documentType(DocumentType.ASSEMBLED_LETTER).build());
        assertEquals(Document.builder().documentType(DocumentType.ASSEMBLED_LETTER).build(),
                documentGeneratorService.generateLetter(callbackRequest, true));
    }

    @Test
    public void testGenerateLetterWithWaterMark() {
        when(pdfManagementService.generateDocmosisDocumentAndUpload(expectedMap, DocumentType.ASSEMBLED_LETTER))
                .thenReturn(Document.builder().documentType(DocumentType.ASSEMBLED_LETTER).build());
        assertEquals(Document.builder().documentType(DocumentType.ASSEMBLED_LETTER).build(),
                documentGeneratorService.generateLetter(callbackRequest, false));
    }
}
