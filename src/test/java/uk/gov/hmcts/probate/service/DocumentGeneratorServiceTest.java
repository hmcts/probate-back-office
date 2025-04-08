package uk.gov.hmcts.probate.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.config.properties.registries.Registry;
import uk.gov.hmcts.probate.exception.NotFoundException;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.Constants;
import static uk.gov.hmcts.probate.model.Constants.CTSC;
import uk.gov.hmcts.probate.model.DocumentCaseType;
import uk.gov.hmcts.probate.model.DocumentIssueType;
import uk.gov.hmcts.probate.model.DocumentStatus;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.LanguagePreference;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.OriginalDocuments;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.UploadDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.standingsearch.request.StandingSearchCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.standingsearch.request.StandingSearchData;
import uk.gov.hmcts.probate.model.ccd.standingsearch.request.StandingSearchDetails;
import uk.gov.hmcts.probate.model.ccd.willlodgement.request.WillLodgementCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.willlodgement.request.WillLodgementData;
import uk.gov.hmcts.probate.model.ccd.willlodgement.request.WillLodgementDetails;
import uk.gov.hmcts.probate.service.docmosis.DocumentTemplateService;
import uk.gov.hmcts.probate.service.docmosis.GenericMapperService;
import uk.gov.hmcts.probate.service.docmosis.PreviewLetterService;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.probate.service.template.pdf.PlaceholderDecorator;
import uk.gov.hmcts.probate.transformer.CaseDataTransformer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;

class DocumentGeneratorServiceTest {

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
    private static final String DIGITAL_GRANT_FILE_NAME = "digitalGrantDraft.pdf";
    private static final String WELSH_DIGITAL_GRANT_DRAFT_REISSUE_FILE_NAME = "welshDigitalGrantDraftReissue.pdf";
    private static final String WELSH_INTESTACY_DRAFT_REISSUE_FILE_NAME = "welshIntestacyGrantDraftReissue.pdf";
    private static final String WELSH_ADMON_WILL_DRAFT_REISSUE_FILE_NAME = "welshAdmonWillGrantDraftReissue.pdf";
    private static final String WELSH_DIGITAL_GRANT_FINAL_REISSUE_FILE_NAME = "welshDigitalGrantFinalReissue.pdf";
    private static final String WELSH_INTESTACY_FINAL_REISSUE_FILE_NAME = "welshIntestacyGrantFinalReissue.pdf";
    private static final String WELSH_ADMON_WILL_FINAL_REISSUE_FILE_NAME = "welshAdmonWillGrantFinalReissue.pdf";
    private static final String BLANK = "blank";
    private static final String TEMPLATE = "template";


    private CallbackRequest callbackRequest;
    private CallbackRequest callbackRequestSolsGop;
    private CallbackRequest callbackRequestTrustCorpSolsGop;
    private CallbackRequest callbackRequestSolsAdmon;
    private CallbackRequest callbackRequestSolsIntestacy;
    private Map<String, Object> expectedMap;

    @InjectMocks
    private DocumentGeneratorService documentGeneratorService;

    @Mock
    private PDFManagementService pdfManagementService;

    @Mock
    private GenericMapperService genericMapperService;

    @Mock
    private RegistryDetailsService registryDetailsService;

    @Mock
    private PreviewLetterService previewLetterService;

    @Mock
    private DocumentService documentService;

    @Mock
    private DocumentTemplateService documentTemplateService;

    @Mock
    private CaseDataTransformer caseDataTransformer;

    @Mock
    private PlaceholderDecorator placeholderDecorator;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

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

        CaseDetails caseDetailsSolsTrustCorpGop = new CaseDetails(CaseData.builder()
                .caseType("gop")
                .schemaVersion("2.0.0")
                .registryLocation("Bristol")
                .applicationType(ApplicationType.SOLICITOR)
                .solsSolicitorAddress(SolsAddress.builder().build())
                .solsSolicitorFirmName("firmName").build(),
                LAST_MODIFIED, CASE_ID);
        callbackRequestTrustCorpSolsGop = new CallbackRequest(caseDetailsSolsTrustCorpGop);


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

        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        expectedMap =
            mapper.convertValue(CaseData.builder().caseType("gop").letterType(TEMPLATE)
                .registryLocation("Bristol").build(), Map.class);

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
    void testGenerateReissueDraftProducesCorrectDocumentForGOP() {
        when(pdfManagementService.generateDocmosisDocumentAndUpload(expectedMap,
            DocumentType.DIGITAL_GRANT_REISSUE_DRAFT))
            .thenReturn(Document.builder().documentFileName(DIGITAL_GRANT_DRAFT_REISSUE_FILE_NAME).build());

        when(documentTemplateService
            .getTemplateId(LanguagePreference.ENGLISH, DocumentStatus.PREVIEW, DocumentIssueType.REISSUE,
                DocumentCaseType.GOP))
            .thenReturn(DocumentType.DIGITAL_GRANT_REISSUE_DRAFT);

        assertEquals(DIGITAL_GRANT_DRAFT_REISSUE_FILE_NAME,
            documentGeneratorService
                .generateGrantReissue(callbackRequest, DocumentStatus.PREVIEW, Optional.of(DocumentIssueType.REISSUE))
                .getDocumentFileName());
    }

    @Test
    void testGenerateReissueDraftProducesCorrectDocumentForIntestacy() {
        CaseDetails caseDetails =
            new CaseDetails(CaseData.builder().caseType("intestacy").registryLocation("Bristol").build(),
                LAST_MODIFIED, CASE_ID);
        callbackRequest = new CallbackRequest(caseDetails);
        when(pdfManagementService.generateDocmosisDocumentAndUpload(expectedMap,
            DocumentType.INTESTACY_GRANT_REISSUE_DRAFT))
            .thenReturn(Document.builder().documentFileName(INTESTACY_DRAFT_REISSUE_FILE_NAME).build());

        when(documentTemplateService
            .getTemplateId(LanguagePreference.ENGLISH, DocumentStatus.PREVIEW, DocumentIssueType.REISSUE,
                DocumentCaseType.INTESTACY)).thenReturn(DocumentType.INTESTACY_GRANT_REISSUE_DRAFT);

        assertEquals(INTESTACY_DRAFT_REISSUE_FILE_NAME,
            documentGeneratorService
                .generateGrantReissue(callbackRequest, DocumentStatus.PREVIEW, Optional.of(DocumentIssueType.REISSUE))
                .getDocumentFileName());
    }

    @Test
    void testGenerateReissueDraftProducesCorrectDocumentForAdmonWill() {
        CaseDetails caseDetails =
            new CaseDetails(CaseData.builder().caseType("admonWill").registryLocation("Bristol").build(),
                LAST_MODIFIED, CASE_ID);
        callbackRequest = new CallbackRequest(caseDetails);
        when(pdfManagementService.generateDocmosisDocumentAndUpload(expectedMap,
            DocumentType.ADMON_WILL_GRANT_REISSUE_DRAFT)).thenReturn(Document.builder()
            .documentFileName(ADMON_WILL_DRAFT_REISSUE_FILE_NAME).build());

        when(documentTemplateService
            .getTemplateId(LanguagePreference.ENGLISH, DocumentStatus.PREVIEW, DocumentIssueType.REISSUE,
                DocumentCaseType.ADMON_WILL)).thenReturn(DocumentType.ADMON_WILL_GRANT_REISSUE_DRAFT);

        assertEquals(ADMON_WILL_DRAFT_REISSUE_FILE_NAME,
            documentGeneratorService
                .generateGrantReissue(callbackRequest, DocumentStatus.PREVIEW, Optional.of(DocumentIssueType.REISSUE))
                .getDocumentFileName());
    }

    @Test
    void testGenerateReissueProducesFinalVersionForGOP() {
        when(pdfManagementService.generateDocmosisDocumentAndUpload(expectedMap, DocumentType.DIGITAL_GRANT_REISSUE))
            .thenReturn(Document.builder().documentFileName(DIGITAL_GRANT_REISSUE_FILE_NAME).build());

        when(documentTemplateService
            .getTemplateId(LanguagePreference.ENGLISH, DocumentStatus.FINAL, DocumentIssueType.REISSUE,
                DocumentCaseType.GOP)).thenReturn(DocumentType.DIGITAL_GRANT_REISSUE);

        assertEquals(DIGITAL_GRANT_REISSUE_FILE_NAME, documentGeneratorService.generateGrantReissue(callbackRequest,
            DocumentStatus.FINAL, Optional.of(DocumentIssueType.REISSUE)).getDocumentFileName());
    }

    @Test
    void testGenerateReissueProducesFinalVersionForIntestacy() {
        CaseDetails caseDetails =
            new CaseDetails(CaseData.builder().caseType("intestacy").registryLocation("Bristol").build(),
                LAST_MODIFIED, CASE_ID);
        callbackRequest = new CallbackRequest(caseDetails);
        when(pdfManagementService.generateDocmosisDocumentAndUpload(expectedMap,
            DocumentType.INTESTACY_GRANT_REISSUE))
            .thenReturn(Document.builder().documentFileName(INTESTACY_REISSUE_FILE_NAME).build());

        when(documentTemplateService
            .getTemplateId(LanguagePreference.ENGLISH, DocumentStatus.FINAL, DocumentIssueType.REISSUE,
                DocumentCaseType.INTESTACY)).thenReturn(DocumentType.INTESTACY_GRANT_REISSUE);

        assertEquals(INTESTACY_REISSUE_FILE_NAME,
            documentGeneratorService
                .generateGrantReissue(callbackRequest, DocumentStatus.FINAL, Optional.of(DocumentIssueType.REISSUE))
                .getDocumentFileName());
    }

    @Test
    void testGenerateReissueProducesFinalVersionForAdmonWill() {
        CaseDetails caseDetails =
            new CaseDetails(CaseData.builder().caseType("admonWill").registryLocation("Bristol").build(),
                LAST_MODIFIED, CASE_ID);
        callbackRequest = new CallbackRequest(caseDetails);
        when(pdfManagementService.generateDocmosisDocumentAndUpload(expectedMap,
            DocumentType.ADMON_WILL_GRANT_REISSUE)).thenReturn(Document.builder()
            .documentFileName(ADMON_WILL_REISSUE_FILE_NAME).build());

        when(documentTemplateService
            .getTemplateId(LanguagePreference.ENGLISH, DocumentStatus.FINAL, DocumentIssueType.REISSUE,
                DocumentCaseType.ADMON_WILL)).thenReturn(DocumentType.ADMON_WILL_GRANT_REISSUE);

        assertEquals(ADMON_WILL_REISSUE_FILE_NAME,
            documentGeneratorService
                .generateGrantReissue(callbackRequest, DocumentStatus.FINAL, Optional.of(DocumentIssueType.REISSUE))
                .getDocumentFileName());
    }

    @Test
    void testGenerateProducesWelshAdmonWillVersionFinal() {
        CaseDetails caseDetails =
            new CaseDetails(CaseData.builder().caseType("admonWill").registryLocation("Bristol")
                .languagePreferenceWelsh(Constants.YES).build(),
                LAST_MODIFIED, CASE_ID);

        callbackRequest = new CallbackRequest(caseDetails);
        when(pdfManagementService.generateDocmosisDocumentAndUpload(expectedMap,
            DocumentType.WELSH_ADMON_WILL_GRANT)).thenReturn(Document.builder()
            .documentFileName(ADMON_WILL_FINAL_FILE_NAME).build());

        when(documentTemplateService
            .getTemplateId(LanguagePreference.WELSH, DocumentStatus.FINAL, DocumentIssueType.REISSUE,
                DocumentCaseType.ADMON_WILL)).thenReturn(DocumentType.WELSH_ADMON_WILL_GRANT);


        assertEquals(ADMON_WILL_FINAL_FILE_NAME,
            documentGeneratorService.getDocument(callbackRequest, DocumentStatus.FINAL, DocumentIssueType.REISSUE)
                .getDocumentFileName());
        verify(placeholderDecorator).decorate(expectedMap);
    }

    @Test
    void testGenerateProducesWelshAdmonWillVersionDraft() {
        CaseDetails caseDetails =
            new CaseDetails(CaseData.builder().caseType("admonWill").registryLocation("Bristol")
                .languagePreferenceWelsh(Constants.YES).build(),
                LAST_MODIFIED, CASE_ID);

        callbackRequest = new CallbackRequest(caseDetails);
        when(pdfManagementService.generateDocmosisDocumentAndUpload(expectedMap,
            DocumentType.WELSH_ADMON_WILL_GRANT_DRAFT)).thenReturn(Document.builder()
            .documentFileName(ADMON_WILL_DRAFT_FILE_NAME).build());

        when(documentTemplateService
            .getTemplateId(LanguagePreference.WELSH, DocumentStatus.PREVIEW, DocumentIssueType.REISSUE,
                DocumentCaseType.ADMON_WILL)).thenReturn(DocumentType.WELSH_ADMON_WILL_GRANT_DRAFT);

        assertEquals(ADMON_WILL_DRAFT_FILE_NAME,
            documentGeneratorService.getDocument(callbackRequest, DocumentStatus.PREVIEW, DocumentIssueType.REISSUE)
                .getDocumentFileName());
        verify(placeholderDecorator).decorate(expectedMap);
    }

    @Test
    void testGenerateProducesWelshDigitalGrantFinal() {
        CaseDetails caseDetails =
            new CaseDetails(CaseData.builder().caseType("gop").registryLocation("Bristol")
                .languagePreferenceWelsh(Constants.YES).build(),
                LAST_MODIFIED, CASE_ID);

        callbackRequest = new CallbackRequest(caseDetails);
        when(pdfManagementService.generateDocmosisDocumentAndUpload(expectedMap,
            DocumentType.WELSH_DIGITAL_GRANT)).thenReturn(Document.builder()
            .documentFileName(DIGITAL_GRANT_FINAL_FILE_NAME).build());

        when(documentTemplateService
            .getTemplateId(LanguagePreference.WELSH, DocumentStatus.FINAL, DocumentIssueType.REISSUE,
                DocumentCaseType.GOP)).thenReturn(DocumentType.WELSH_DIGITAL_GRANT);

        assertEquals(DIGITAL_GRANT_FINAL_FILE_NAME,
            documentGeneratorService.getDocument(callbackRequest, DocumentStatus.FINAL, DocumentIssueType.REISSUE)
                .getDocumentFileName());
        verify(placeholderDecorator).decorate(expectedMap);
    }

    @Test
    void testGenerateProducesWelshDigitalGrantDraft() {
        CaseDetails caseDetails =
            new CaseDetails(CaseData.builder().caseType("gop").registryLocation("Bristol")
                .languagePreferenceWelsh(Constants.YES).build(),
                LAST_MODIFIED, CASE_ID);

        callbackRequest = new CallbackRequest(caseDetails);
        when(pdfManagementService.generateDocmosisDocumentAndUpload(expectedMap,
            DocumentType.WELSH_DIGITAL_GRANT_DRAFT)).thenReturn(Document.builder()
            .documentFileName(DIGITAL_GRANT_DRAFT_FILE_NAME).build());

        when(documentTemplateService
            .getTemplateId(LanguagePreference.WELSH, DocumentStatus.PREVIEW, DocumentIssueType.REISSUE,
                DocumentCaseType.GOP)).thenReturn(DocumentType.WELSH_DIGITAL_GRANT_DRAFT);

        assertEquals(DIGITAL_GRANT_DRAFT_FILE_NAME,
            documentGeneratorService.getDocument(callbackRequest, DocumentStatus.PREVIEW, DocumentIssueType.REISSUE)
                .getDocumentFileName());
        verify(placeholderDecorator).decorate(expectedMap);
    }

    @Test
    void testGenerateProducesWelshIntestacyGrantDraft() {
        CaseDetails caseDetails =
            new CaseDetails(CaseData.builder().caseType("intestacy").registryLocation("Bristol")
                .languagePreferenceWelsh(Constants.YES).build(),
                LAST_MODIFIED, CASE_ID);

        callbackRequest = new CallbackRequest(caseDetails);
        when(pdfManagementService.generateDocmosisDocumentAndUpload(expectedMap,
            DocumentType.WELSH_INTESTACY_GRANT_DRAFT)).thenReturn(Document.builder()
            .documentFileName(WELSH_INTESTACY_GRANT_DRAFT_FILE_NAME).build());

        when(documentTemplateService
            .getTemplateId(LanguagePreference.WELSH, DocumentStatus.PREVIEW, DocumentIssueType.REISSUE,
                DocumentCaseType.INTESTACY)).thenReturn(DocumentType.WELSH_INTESTACY_GRANT_DRAFT);

        assertEquals(WELSH_INTESTACY_GRANT_DRAFT_FILE_NAME,
            documentGeneratorService.getDocument(callbackRequest, DocumentStatus.PREVIEW, DocumentIssueType.REISSUE)
                .getDocumentFileName());
        verify(placeholderDecorator).decorate(expectedMap);
    }

    @Test
    void testGenerateProducesWelshIntestacyGrantFinal() {
        CaseDetails caseDetails =
            new CaseDetails(CaseData.builder().caseType("intestacy").registryLocation("Bristol")
                .languagePreferenceWelsh(Constants.YES).build(),
                LAST_MODIFIED, CASE_ID);

        callbackRequest = new CallbackRequest(caseDetails);
        when(pdfManagementService.generateDocmosisDocumentAndUpload(expectedMap,
            DocumentType.WELSH_INTESTACY_GRANT)).thenReturn(Document.builder()
            .documentFileName(WELSH_INTESTACY_GRANT_FINAL_FILE_NAME).build());

        when(documentTemplateService
            .getTemplateId(LanguagePreference.WELSH, DocumentStatus.FINAL, DocumentIssueType.REISSUE,
                DocumentCaseType.INTESTACY)).thenReturn(DocumentType.WELSH_INTESTACY_GRANT);

        assertEquals(WELSH_INTESTACY_GRANT_FINAL_FILE_NAME,
            documentGeneratorService.getDocument(callbackRequest, DocumentStatus.FINAL, DocumentIssueType.REISSUE)
                .getDocumentFileName());
        verify(placeholderDecorator).decorate(expectedMap);
    }

    @Test
    void testGenerateProducesWelshDigitalGrantReissueDraft() {
        CaseDetails caseDetails =
            new CaseDetails(CaseData.builder().caseType("gop").registryLocation("Bristol")
                .languagePreferenceWelsh(Constants.YES).build(),
                LAST_MODIFIED, CASE_ID);

        callbackRequest = new CallbackRequest(caseDetails);
        when(pdfManagementService.generateDocmosisDocumentAndUpload(expectedMap,
            DocumentType.WELSH_DIGITAL_GRANT_REISSUE_DRAFT)).thenReturn(Document.builder()
            .documentFileName(WELSH_DIGITAL_GRANT_DRAFT_REISSUE_FILE_NAME).build());

        when(documentTemplateService
            .getTemplateId(LanguagePreference.WELSH, DocumentStatus.PREVIEW, DocumentIssueType.REISSUE,
                DocumentCaseType.GOP)).thenReturn(DocumentType.WELSH_DIGITAL_GRANT_REISSUE_DRAFT);

        assertEquals(WELSH_DIGITAL_GRANT_DRAFT_REISSUE_FILE_NAME,
            documentGeneratorService.getDocument(callbackRequest, DocumentStatus.PREVIEW, DocumentIssueType.REISSUE)
                .getDocumentFileName());
        verify(placeholderDecorator).decorate(expectedMap);
    }

    @Test
    void testGenerateProducesWelshIntestacyGrantReissueDraft() {
        CaseDetails caseDetails =
            new CaseDetails(CaseData.builder().caseType("intestacy").registryLocation("Bristol")
                .languagePreferenceWelsh(Constants.YES).build(),
                LAST_MODIFIED, CASE_ID);

        callbackRequest = new CallbackRequest(caseDetails);
        when(pdfManagementService.generateDocmosisDocumentAndUpload(expectedMap,
            DocumentType.WELSH_INTESTACY_GRANT_REISSUE_DRAFT)).thenReturn(Document.builder()
            .documentFileName(WELSH_INTESTACY_DRAFT_REISSUE_FILE_NAME).build());

        when(documentTemplateService
            .getTemplateId(LanguagePreference.WELSH, DocumentStatus.PREVIEW, DocumentIssueType.REISSUE,
                DocumentCaseType.INTESTACY)).thenReturn(DocumentType.WELSH_INTESTACY_GRANT_REISSUE_DRAFT);

        assertEquals(WELSH_INTESTACY_DRAFT_REISSUE_FILE_NAME,
            documentGeneratorService.getDocument(callbackRequest, DocumentStatus.PREVIEW, DocumentIssueType.REISSUE)
                .getDocumentFileName());
        verify(placeholderDecorator).decorate(expectedMap);
    }

    @Test
    void testGenerateProducesWelshAdmonGrantReissueDraft() {
        CaseDetails caseDetails =
            new CaseDetails(CaseData.builder().caseType("admonWill").registryLocation("Bristol")
                .languagePreferenceWelsh(Constants.YES).build(),
                LAST_MODIFIED, CASE_ID);

        callbackRequest = new CallbackRequest(caseDetails);
        when(pdfManagementService.generateDocmosisDocumentAndUpload(expectedMap,
            DocumentType.WELSH_ADMON_WILL_GRANT_REISSUE_DRAFT)).thenReturn(Document.builder()
            .documentFileName(WELSH_ADMON_WILL_DRAFT_REISSUE_FILE_NAME).build());

        when(documentTemplateService
            .getTemplateId(LanguagePreference.WELSH, DocumentStatus.PREVIEW, DocumentIssueType.REISSUE,
                DocumentCaseType.ADMON_WILL)).thenReturn(DocumentType.WELSH_ADMON_WILL_GRANT_REISSUE_DRAFT);

        assertEquals(WELSH_ADMON_WILL_DRAFT_REISSUE_FILE_NAME,
            documentGeneratorService.getDocument(callbackRequest, DocumentStatus.PREVIEW, DocumentIssueType.REISSUE)
                .getDocumentFileName());
        verify(placeholderDecorator).decorate(expectedMap);
    }

    @Test
    void testGenerateProducesWelshDigitalGrantReissue() {
        CaseDetails caseDetails =
            new CaseDetails(CaseData.builder().caseType("gop").registryLocation("Bristol")
                .languagePreferenceWelsh(Constants.YES).build(),
                LAST_MODIFIED, CASE_ID);

        callbackRequest = new CallbackRequest(caseDetails);
        when(pdfManagementService.generateDocmosisDocumentAndUpload(expectedMap,
            DocumentType.WELSH_DIGITAL_GRANT_REISSUE)).thenReturn(Document.builder()
            .documentFileName(WELSH_DIGITAL_GRANT_FINAL_REISSUE_FILE_NAME).build());

        when(documentTemplateService
            .getTemplateId(LanguagePreference.WELSH, DocumentStatus.FINAL, DocumentIssueType.REISSUE,
                DocumentCaseType.GOP)).thenReturn(DocumentType.WELSH_DIGITAL_GRANT_REISSUE);

        assertEquals(WELSH_DIGITAL_GRANT_FINAL_REISSUE_FILE_NAME,
            documentGeneratorService.getDocument(callbackRequest, DocumentStatus.FINAL, DocumentIssueType.REISSUE)
                .getDocumentFileName());
        verify(placeholderDecorator).decorate(expectedMap);
    }

    @Test
    void testGenerateProducesWelshIntestacyGrantReissue() {
        CaseDetails caseDetails =
            new CaseDetails(CaseData.builder().caseType("intestacy").registryLocation("Bristol")
                .languagePreferenceWelsh(Constants.YES).build(),
                LAST_MODIFIED, CASE_ID);

        callbackRequest = new CallbackRequest(caseDetails);
        when(pdfManagementService.generateDocmosisDocumentAndUpload(expectedMap,
            DocumentType.WELSH_INTESTACY_GRANT_REISSUE)).thenReturn(Document.builder()
            .documentFileName(WELSH_INTESTACY_FINAL_REISSUE_FILE_NAME).build());

        when(documentTemplateService
            .getTemplateId(LanguagePreference.WELSH, DocumentStatus.FINAL, DocumentIssueType.REISSUE,
                DocumentCaseType.INTESTACY)).thenReturn(DocumentType.WELSH_INTESTACY_GRANT_REISSUE);

        assertEquals(WELSH_INTESTACY_FINAL_REISSUE_FILE_NAME,
            documentGeneratorService.getDocument(callbackRequest, DocumentStatus.FINAL, DocumentIssueType.REISSUE)
                .getDocumentFileName());
        verify(placeholderDecorator).decorate(expectedMap);
    }

    @Test
    void testGenerateProducesWelshAdmonWIllGrantReissue() {
        CaseDetails caseDetails =
            new CaseDetails(CaseData.builder().caseType("admonWill").registryLocation("Bristol")
                .languagePreferenceWelsh(Constants.YES).build(),
                LAST_MODIFIED, CASE_ID);

        callbackRequest = new CallbackRequest(caseDetails);
        when(pdfManagementService.generateDocmosisDocumentAndUpload(expectedMap,
            DocumentType.WELSH_ADMON_WILL_GRANT_REISSUE)).thenReturn(Document.builder()
            .documentFileName(WELSH_ADMON_WILL_FINAL_REISSUE_FILE_NAME).build());

        when(documentTemplateService
            .getTemplateId(LanguagePreference.WELSH, DocumentStatus.FINAL, DocumentIssueType.REISSUE,
                DocumentCaseType.ADMON_WILL)).thenReturn(DocumentType.WELSH_ADMON_WILL_GRANT_REISSUE);

        assertEquals(WELSH_ADMON_WILL_FINAL_REISSUE_FILE_NAME,
            documentGeneratorService.getDocument(callbackRequest, DocumentStatus.FINAL, DocumentIssueType.REISSUE)
                .getDocumentFileName());
        verify(placeholderDecorator).decorate(expectedMap);
    }

    @Test
    void testGenerateReissueProducesNewEdgeCaseDocumentForDraft() {
        CaseDetails caseDetails =
            new CaseDetails(CaseData.builder().caseType("edgeCase").registryLocation("Bristol").build(),
                LAST_MODIFIED, CASE_ID);
        callbackRequest = new CallbackRequest(caseDetails);
        assertEquals(DocumentType.EDGE_CASE,
            documentGeneratorService.generateGrantReissue(callbackRequest, DocumentStatus.PREVIEW, Optional.empty())
                .getDocumentType());
    }

    @Test
    void testGenerateReissueProducesNewEdgeCaseDocumentForFinal() {
        CaseDetails caseDetails =
            new CaseDetails(CaseData.builder().caseType("edgeCase").registryLocation("Bristol").build(),
                LAST_MODIFIED, CASE_ID);
        callbackRequest = new CallbackRequest(caseDetails);
        assertEquals(DocumentType.EDGE_CASE,
            documentGeneratorService.generateGrantReissue(callbackRequest, DocumentStatus.FINAL, Optional.empty())
                .getDocumentType());
    }

    @Test
    void testGenerateCoversheetReturnsCorrectDocumentType() {
        when(pdfManagementService.generateDocmosisDocumentAndUpload(expectedMap, DocumentType.GRANT_COVERSHEET))
            .thenReturn(Document.builder().documentType(DocumentType.GRANT_COVERSHEET).build());
        assertEquals(DocumentType.GRANT_COVERSHEET,
            documentGeneratorService.generateCoversheet(callbackRequest).getDocumentType());
    }

    @Test
    void testGenerateCoversheetReturnsCorrectDocumentTypeForSpecificExec() {
        when(pdfManagementService.generateDocmosisDocumentAndUpload(expectedMap, DocumentType.GRANT_COVERSHEET))
            .thenReturn(Document.builder().documentType(DocumentType.GRANT_COVERSHEET).build());
        assertEquals(DocumentType.GRANT_COVERSHEET,
            documentGeneratorService.generateCoversheet(callbackRequest, "Bob Smith", SolsAddress.builder().build())
                .getDocumentType());
    }

    @Test
    void testStatementOfTruthReturnedSuccessfullyForPersonalCase() {
        when(pdfManagementService.generateDocmosisDocumentAndUpload(expectedMap, DocumentType.STATEMENT_OF_TRUTH))
            .thenReturn(Document.builder().documentType(DocumentType.STATEMENT_OF_TRUTH).build());
        assertEquals(Document.builder().documentType(DocumentType.STATEMENT_OF_TRUTH).build(),
            documentGeneratorService.generateSoT(callbackRequest));
    }

    @Test
    void testStatementOfTruthReturnedSuccessfullyForSolsGopCase() {
        when(pdfManagementService.generateAndUpload(callbackRequestSolsGop, DocumentType.LEGAL_STATEMENT_PROBATE))
            .thenReturn(Document.builder().documentType(DocumentType.LEGAL_STATEMENT_PROBATE).build());
        assertEquals(Document.builder().documentType(DocumentType.LEGAL_STATEMENT_PROBATE).build(),
            documentGeneratorService.generateSoT(callbackRequestSolsGop));
    }

    @Test
    void testStatementOfTruthReturnedSuccessfullyForSolsIntestacyCase() {
        when(pdfManagementService
            .generateAndUpload(callbackRequestSolsIntestacy, DocumentType.LEGAL_STATEMENT_INTESTACY))
            .thenReturn(Document.builder().documentType(DocumentType.LEGAL_STATEMENT_INTESTACY).build());
        assertEquals(Document.builder().documentType(DocumentType.LEGAL_STATEMENT_INTESTACY).build(),
            documentGeneratorService.generateSoT(callbackRequestSolsIntestacy));
    }

    @Test
    void testStatementOfTruthReturnedSuccessfullyForSolsAdmonWillCase() {
        when(pdfManagementService.generateAndUpload(callbackRequestSolsAdmon, DocumentType.LEGAL_STATEMENT_ADMON))
            .thenReturn(Document.builder().documentType(DocumentType.LEGAL_STATEMENT_ADMON).build());
        assertEquals(Document.builder().documentType(DocumentType.LEGAL_STATEMENT_ADMON).build(),
            documentGeneratorService.generateSoT(callbackRequestSolsAdmon));
    }

    @Test
    void redeclarationOfSOTWelsh() {
        expectedMap.put("deceasedDateOfBirth", String.valueOf(LocalDate.of(2018, 10, 19)));
        expectedMap.put("deceasedDateOfDeath", String.valueOf(LocalDate.of(2018, 10, 19)));

        CaseDetails caseDetails =
            new CaseDetails(CaseData.builder().caseType("gop").registryLocation("Bristol")
                .languagePreferenceWelsh(Constants.YES)
                .deceasedDateOfBirth(LocalDate.of(2018, 10, 19))
                .deceasedDateOfDeath(LocalDate.of(2018, 10, 19))
                .applicationType(ApplicationType.PERSONAL)
                .build(),
                LAST_MODIFIED, CASE_ID);
        callbackRequest = new CallbackRequest(caseDetails);
        when(genericMapperService.addCaseDataWithRegistryProperties(caseDetails)).thenReturn(expectedMap);
        when(pdfManagementService.generateDocmosisDocumentAndUpload(expectedMap, DocumentType.WELSH_STATEMENT_OF_TRUTH))
            .thenReturn(Document.builder().documentType(DocumentType.WELSH_STATEMENT_OF_TRUTH).build());
        assertEquals(Document.builder().documentType(DocumentType.WELSH_STATEMENT_OF_TRUTH).build(),
            documentGeneratorService.generateSoT(callbackRequest));
    }

    @Test
    void testGenerateLetter() {
        when(pdfManagementService.generateDocmosisDocumentAndUpload(expectedMap, DocumentType.ASSEMBLED_LETTER))
            .thenReturn(Document.builder().documentType(DocumentType.ASSEMBLED_LETTER).build());
        assertEquals(Document.builder().documentType(DocumentType.ASSEMBLED_LETTER).build(),
            documentGeneratorService.generateLetter(callbackRequest, true));
    }

    @Test
    void testGenerateBlankLetter() {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        expectedMap =
            mapper.convertValue(CaseData.builder().letterType(BLANK).build(), Map.class);
        when(previewLetterService.addLetterData(any())).thenReturn(expectedMap);
        when(pdfManagementService.generateDocmosisDocumentAndUpload(expectedMap, DocumentType.BLANK_LETTER))
            .thenReturn(Document.builder().documentType(DocumentType.BLANK_LETTER).build());
        assertEquals(Document.builder().documentType(DocumentType.BLANK_LETTER).build(),
            documentGeneratorService.generateLetter(callbackRequest, true));
    }

    @Test
    void unknownLetterTypeShouldThrowException() {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        expectedMap =
            mapper.convertValue(CaseData.builder().build(), Map.class);
        when(previewLetterService.addLetterData(any())).thenReturn(expectedMap);
        when(pdfManagementService.generateDocmosisDocumentAndUpload(expectedMap, DocumentType.BLANK_LETTER))
            .thenReturn(Document.builder().documentType(DocumentType.BLANK_LETTER).build());

        assertThrows(NotFoundException.class, () -> {
            documentGeneratorService.generateLetter(callbackRequest, true);
        });
    }

    @Test
    void testGenerateLetterWithWaterMark() {
        when(pdfManagementService.generateDocmosisDocumentAndUpload(expectedMap, DocumentType.ASSEMBLED_LETTER))
            .thenReturn(Document.builder().documentType(DocumentType.ASSEMBLED_LETTER).build());
        assertEquals(Document.builder().documentType(DocumentType.ASSEMBLED_LETTER).build(),
            documentGeneratorService.generateLetter(callbackRequest, false));
    }

    @Test
    void testGeneratePDFDocument() {
        CaseDetails caseDetails =
            new CaseDetails(CaseData.builder().caseType("digitalGrant").registryLocation("Bristol")
                .build(),
                LAST_MODIFIED, CASE_ID);

        callbackRequest = new CallbackRequest(caseDetails);

        when(pdfManagementService.generateAndUpload(callbackRequest, DocumentType.DIGITAL_GRANT))
            .thenReturn(
                Document.builder().documentType(DocumentType.DIGITAL_GRANT).documentFileName(DIGITAL_GRANT_FILE_NAME)
                    .build());

        when(documentTemplateService
            .getTemplateId(LanguagePreference.ENGLISH, DocumentStatus.FINAL, DocumentIssueType.GRANT,
                DocumentCaseType.GOP)).thenReturn(DocumentType.DIGITAL_GRANT);

        assertEquals(DIGITAL_GRANT_FILE_NAME,
            documentGeneratorService.getDocument(callbackRequest, DocumentStatus.FINAL, DocumentIssueType.GRANT)
                .getDocumentFileName());
    }

    @Test
    void testGenerateEdgeCasePDFDocument() {
        CaseDetails caseDetails =
            new CaseDetails(CaseData.builder().caseType("edgeCase").registryLocation("Bristol")
                .build(),
                LAST_MODIFIED, CASE_ID);

        callbackRequest = new CallbackRequest(caseDetails);
        assertEquals(DocumentType.EDGE_CASE,
            documentGeneratorService.getDocument(callbackRequest, DocumentStatus.FINAL, DocumentIssueType.GRANT)
                .getDocumentType());
    }

    @Test
    void testGenerateTrustCorpsProbateLegalStatementForSchema2() {
        when(pdfManagementService.generateAndUpload(callbackRequestTrustCorpSolsGop,
                DocumentType.LEGAL_STATEMENT_PROBATE_TRUST_CORPS))
                .thenReturn(Document.builder().documentType(DocumentType.LEGAL_STATEMENT_PROBATE_TRUST_CORPS).build());
        assertEquals(Document.builder().documentType(DocumentType.LEGAL_STATEMENT_PROBATE_TRUST_CORPS).build(),
                documentGeneratorService.generateSoT(callbackRequestTrustCorpSolsGop));
    }

    @Test
    void shouldRemoveDocumentsForGrant() {
        Document doc1 = Document.builder().build();
        Document doc2 = Document.builder().build();
        List<CollectionMember<Document>> generatedList = new ArrayList<>();
        generatedList.add(new CollectionMember<Document>("1", doc1));
        List<CollectionMember<Document>> originalGeneratedList = new ArrayList<>();
        originalGeneratedList.add(new CollectionMember<Document>("1", doc1));
        originalGeneratedList.add(new CollectionMember<Document>("2", doc2));

        UploadDocument uploadDocument1 = UploadDocument.builder().build();
        UploadDocument uploadDocument2 = UploadDocument.builder().build();
        List<CollectionMember<UploadDocument>> uploadedList = new ArrayList<>();
        uploadedList.add(new CollectionMember<UploadDocument>("3", uploadDocument1));
        List<CollectionMember<UploadDocument>> originalUploadedList = new ArrayList<>();
        originalUploadedList.add(new CollectionMember<UploadDocument>("3", uploadDocument1));
        originalUploadedList.add(new CollectionMember<UploadDocument>("4", uploadDocument2));

        ScannedDocument scannedDocument1 = ScannedDocument.builder().type("WILL").build();
        ScannedDocument scannedDocument2 = ScannedDocument.builder().type("CHERISHED").build();
        List<CollectionMember<ScannedDocument>> scannedList = new ArrayList<>();
        scannedList.add(new CollectionMember<ScannedDocument>("5", scannedDocument1));
        List<CollectionMember<ScannedDocument>> originalScannedList = new ArrayList<>();
        originalScannedList.add(new CollectionMember<ScannedDocument>("5", scannedDocument1));
        originalScannedList.add(new CollectionMember<ScannedDocument>("6", scannedDocument2));

        OriginalDocuments originalDocuments = OriginalDocuments.builder()
                .originalDocsGenerated(originalGeneratedList)
                .originalDocsUploaded(originalUploadedList)
                .originalDocsScanned(originalScannedList)
                .build();
        CaseDetails caseDetails =
                new CaseDetails(CaseData.builder()
                        .probateDocumentsGenerated(generatedList)
                        .boDocumentsUploaded(uploadedList)
                        .scannedDocuments(scannedList)
                        .originalDocuments(originalDocuments)
                        .build(),
                        LAST_MODIFIED, CASE_ID);
        callbackRequest = new CallbackRequest(caseDetails);

        documentGeneratorService.permanentlyDeleteRemovedDocumentsForGrant(callbackRequest);
        verify(documentService, times(3)).delete(any(Document.class), anyString());
    }

    @Test
    void shouldRemoveDocumentsForCaveat() {
        Document doc1 = Document.builder().build();
        Document doc2 = Document.builder().build();
        List<CollectionMember<Document>> generatedList = new ArrayList<>();
        generatedList.add(new CollectionMember<Document>("1", doc1));
        List<CollectionMember<Document>> originalGeneratedList = new ArrayList<>();
        originalGeneratedList.add(new CollectionMember<Document>("1", doc1));
        originalGeneratedList.add(new CollectionMember<Document>("2", doc2));

        UploadDocument uploadDocument1 = UploadDocument.builder().build();
        UploadDocument uploadDocument2 = UploadDocument.builder().build();
        List<CollectionMember<UploadDocument>> uploadedList = new ArrayList<>();
        uploadedList.add(new CollectionMember<UploadDocument>("3", uploadDocument1));
        List<CollectionMember<UploadDocument>> originalUploadedList = new ArrayList<>();
        originalUploadedList.add(new CollectionMember<UploadDocument>("3", uploadDocument1));
        originalUploadedList.add(new CollectionMember<UploadDocument>("4", uploadDocument2));

        ScannedDocument scannedDocument1 = ScannedDocument.builder().type("WILL").build();
        ScannedDocument scannedDocument2 = ScannedDocument.builder().type("WILL").build();
        List<CollectionMember<ScannedDocument>> scannedList = new ArrayList<>();
        scannedList.add(new CollectionMember<ScannedDocument>("5", scannedDocument1));
        List<CollectionMember<ScannedDocument>> originalScannedList = new ArrayList<>();
        originalScannedList.add(new CollectionMember<ScannedDocument>("5", scannedDocument1));
        originalScannedList.add(new CollectionMember<ScannedDocument>("6", scannedDocument2));

        OriginalDocuments originalDocuments = OriginalDocuments.builder()
                .originalDocsGenerated(originalGeneratedList)
                .originalDocsUploaded(originalUploadedList)
                .originalDocsScanned(originalScannedList)
                .build();
        CaveatDetails caseDetails =
                new CaveatDetails(CaveatData.builder()
                        .documentsGenerated(generatedList)
                        .documentsUploaded(uploadedList)
                        .scannedDocuments(scannedList)
                        .originalDocuments(originalDocuments)
                        .build(),
                        LAST_MODIFIED, CASE_ID);
        CaveatCallbackRequest caveatCallbackRequest = new CaveatCallbackRequest(caseDetails);

        documentGeneratorService.permanentlyDeleteRemovedDocumentsForCaveat(caveatCallbackRequest);
        verify(documentService, times(3)).delete(any(Document.class), anyString());
    }

    @Test
    void shouldRemoveDocumentsForWillLodgement() {
        Document doc1 = Document.builder().build();
        Document doc2 = Document.builder().build();
        List<CollectionMember<Document>> generatedList = new ArrayList<>();
        generatedList.add(new CollectionMember<Document>("1", doc1));
        List<CollectionMember<Document>> originalGeneratedList = new ArrayList<>();
        originalGeneratedList.add(new CollectionMember<Document>("1", doc1));
        originalGeneratedList.add(new CollectionMember<Document>("2", doc2));

        UploadDocument uploadDocument1 = UploadDocument.builder().build();
        UploadDocument uploadDocument2 = UploadDocument.builder().build();
        List<CollectionMember<UploadDocument>> uploadedList = new ArrayList<>();
        uploadedList.add(new CollectionMember<UploadDocument>("3", uploadDocument1));
        List<CollectionMember<UploadDocument>> originalUploadedList = new ArrayList<>();
        originalUploadedList.add(new CollectionMember<UploadDocument>("3", uploadDocument1));
        originalUploadedList.add(new CollectionMember<UploadDocument>("4", uploadDocument2));

        OriginalDocuments originalDocuments = OriginalDocuments.builder()
                .originalDocsGenerated(originalGeneratedList)
                .originalDocsUploaded(originalUploadedList)
                .build();
        WillLodgementDetails caseDetails =
                new WillLodgementDetails(WillLodgementData.builder()
                        .documentsGenerated(generatedList)
                        .documentsUploaded(uploadedList)
                        .originalDocuments(originalDocuments)
                        .build(),
                        LAST_MODIFIED, CASE_ID);
        WillLodgementCallbackRequest willLodgementCallbackRequest = new WillLodgementCallbackRequest(caseDetails);

        documentGeneratorService.permanentlyDeleteRemovedDocumentsForWillLodgement(willLodgementCallbackRequest);
        verify(documentService, times(2)).delete(any(Document.class), anyString());
    }

    @Test
    void shouldRemoveDocumentsForStandingSearch() {
        UploadDocument uploadDocument1 = UploadDocument.builder().build();
        UploadDocument uploadDocument2 = UploadDocument.builder().build();
        List<CollectionMember<UploadDocument>> uploadedList = new ArrayList<>();
        uploadedList.add(new CollectionMember<UploadDocument>("3", uploadDocument1));
        List<CollectionMember<UploadDocument>> originalUploadedList = new ArrayList<>();
        originalUploadedList.add(new CollectionMember<UploadDocument>("3", uploadDocument1));
        originalUploadedList.add(new CollectionMember<UploadDocument>("4", uploadDocument2));

        OriginalDocuments originalDocuments = OriginalDocuments.builder()
                .originalDocsUploaded(originalUploadedList)
               .build();
        StandingSearchDetails caseDetails =
                new StandingSearchDetails(StandingSearchData.builder()
                        .documentsUploaded(uploadedList)
                        .originalDocuments(originalDocuments)
                        .build(),
                        LAST_MODIFIED, CASE_ID);
        StandingSearchCallbackRequest standingSearchCallbackRequest = new StandingSearchCallbackRequest(caseDetails);

        documentGeneratorService.permanentlyDeleteRemovedDocumentsForStandingSearch(standingSearchCallbackRequest);
        verify(documentService, times(1)).delete(any(Document.class), anyString());
    }
}
