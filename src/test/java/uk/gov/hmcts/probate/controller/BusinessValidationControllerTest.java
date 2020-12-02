package uk.gov.hmcts.probate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.hmcts.probate.insights.AppInsights;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.State;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.EstateItem;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData.CaseDataBuilder;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.CaseStoppedService;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.probate.util.TestUtils;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.REDEC_NOTIFICATION_SENT_STATE;
import static uk.gov.hmcts.probate.model.Constants.YES;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BusinessValidationControllerTest {

    private static final LocalDate DOB = LocalDate.of(1990, 4, 4);
    private static final LocalDate DOD = LocalDate.of(2017, 4, 4);
    private static final Long ID = 1L;
    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private static final String FORENAME = "Andy";
    private static final String SURNAME = "Michael";
    private static final String MARITAL_STATUS = "Never married";
    private static final String SOLICITOR_APP_REFERENCE = "Reference";
    private static final String SOLICITOR_FIRM_NAME = "Legal Service Ltd";
    private static final String SOLICITOR_FIRM_LINE1 = "Aols Add Line1";
    private static final String SOLICITOR_FIRM_POSTCODE = "SW1E 6EA";
    private static final String IHT_FORM = "IHT207";
    private static final String SOLICITOR_FORENAMES = "Peter";
    private static final String SOLICITOR_SURNAME = "Crouch";
    private static final String SOLICITOR_JOB_TITLE = "Lawyer";
    private static final String PAYMENT_METHOD = "fee account";
    private static final String WILL_HAS_CODICILS = "Yes";
    private static final String NUMBER_OF_CODICILS = "1";
    private static final BigDecimal APPLICATION_FEE = BigDecimal.TEN;
    private static final BigDecimal FEE_FOR_UK_COPIES = BigDecimal.TEN;
    private static final BigDecimal FEE_FOR_NON_UK_COPIES = BigDecimal.TEN;
    private static final BigDecimal TOTAL_FEE = BigDecimal.TEN;
    private static final BigDecimal NET = new BigDecimal("77777777");
    private static final BigDecimal GROSS = new BigDecimal("999999999");
    private static final Long EXTRA_UK = 1L;
    private static final Long EXTRA_OUTSIDE_UK = 2L;
    private static final String DEC_ADD_LINE1 = "DecLine1";
    private static final String DEC_ADD_PC = "DecPC";
    private static final SolsAddress DECEASED_ADDRESS = SolsAddress.builder().addressLine1(DEC_ADD_LINE1).postCode(DEC_ADD_PC).build();
    private static final String EX_ADD_LINE1 = "ExLine1";
    private static final String EX_ADD_PC = "ExPC";
    private static final SolsAddress PRIMARY_ADDRESS = SolsAddress.builder().addressLine1(EX_ADD_LINE1).postCode(EX_ADD_PC).build();
    private static final String PRIMARY_APPLICANT_APPLYING = "Yes";
    private static final String PRIMARY_APPLICANT_HAS_ALIAS = "No";
    private static final String PRIMARY_APPLICANT_EMAIL = "test@test.com";
    private static final String OTHER_EXEC_EXISTS = "No";
    private static final String WILL_EXISTS = "Yes";
    private static final String WILL_TYPE_PROBATE = "WillLeft";
    private static final String WILL_TYPE_INTESTACY = "NoWill";
    private static final String WILL_TYPE_ADMON = "WillLeftAnnexed";
    private static final String WILL_ACCESS_ORIGINAL = "Yes";
    private static final String PRIMARY_FORENAMES = "ExFN";
    private static final String PRIMARY_SURNAME = "ExSN";
    private static final String DECEASED_OTHER_NAMES = "No";
    private static final String DECEASED_DOM_UK = "Yes";
    private static final String RELATIONSHIP_TO_DECEASED = "Child";
    private static final String MINORITY_INTEREST = "No";
    private static final String APPLICANT_SIBLINGS = "No";
    private static final String ENTITLED_MINORITY = "No";
    private static final String DIED_OR_NOT_APPLYING = "Yes";
    private static final String RESIDUARY = "Yes";
    private static final String RESIDUARY_TYPE = "Legatee";
    private static final String LIFE_INTEREST = "No";
    private static final String ANSWER_NO = "No";
    private static final String SOLS_NOT_APPLYING_REASON = "Power reserved";
    private static final String APPLICATION_GROUNDS = "Application grounds";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String SOLS_VALIDATE_URL = "/case/sols-validate";
    private static final String SOLS_VALIDATE_PROBATE_URL = "/case/sols-validate-probate";
    private static final String SOLS_VALIDATE_INTESTACY_URL = "/case/sols-validate-intestacy";
    private static final String SOLS_VALIDATE_ADMON_URL = "/case/sols-validate-admon";
    private static final String CASE_VALIDATE_CASE_DETAILS_URL = "/case/validateCaseDetails";
    private static final String SOLS_APPLY_AS_EXEC = "/sols-apply-as-exec";
    private static final String CASE_PRINTED = "/case/casePrinted";
    private static final String CASE_CHCEKLIST_URL = "/case/validateCheckListDetails";
    private static final String PAPER_FORM_URL = "/case/paperForm";
    private static final String RESOLVE_STOP_URL = "/case/resolveStop";
    private static final String CASE_STOPPED_URL = "/case/case-stopped";
    private static final String REDEC_COMPLETE = "/case/redeclarationComplete";
    private static final String REDECE_SOT = "/case/redeclarationSot";

    private static final DocumentLink SCANNED_DOCUMENT_URL = DocumentLink.builder()
            .documentBinaryUrl("http://somedoc")
            .documentFilename("somedoc.pdf")
            .documentUrl("http://somedoc/location")
            .build();

    private static final LocalDateTime scannedDate = LocalDateTime.parse("2018-01-01T12:34:56.123");
    private static final List<CollectionMember<ScannedDocument>> SCANNED_DOCUMENTS_LIST = Arrays.asList(
            new CollectionMember("id",
                    ScannedDocument.builder()
                            .fileName("scanneddocument.pdf")
                            .controlNumber("1234")
                            .scannedDate(scannedDate)
                            .type("other")
                            .subtype("will")
                            .url(SCANNED_DOCUMENT_URL)
                            .build()));

    private static final List<CollectionMember<EstateItem>> UK_ESTATE = Arrays.asList(
            new CollectionMember<>(null,
                    EstateItem.builder()
                            .item("Item")
                            .value("999.99")
                            .build()));

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mockMvc;

    private CaseDataBuilder caseDataBuilder;

    private final TestUtils testUtils = new TestUtils();

    @MockBean
    private AppInsights appInsights;


    @MockBean
    private PDFManagementService pdfManagementService;

    @MockBean
    private CoreCaseDataApi coreCaseDataApi;

    @MockBean
    private CaseStoppedService caseStoppedService;
    
    @MockBean
    private NotificationService notificationService;


    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        SolsAddress solsAddress = SolsAddress.builder()
                .addressLine1(SOLICITOR_FIRM_LINE1)
                .postCode(SOLICITOR_FIRM_POSTCODE)
                .build();

        caseDataBuilder = CaseData.builder()
                .deceasedDateOfBirth(DOB)
                .deceasedDateOfDeath(DOD)
                .deceasedForenames(FORENAME)
                .deceasedSurname(SURNAME)
                .deceasedAddress(DECEASED_ADDRESS)
                .deceasedAnyOtherNames(DECEASED_OTHER_NAMES)
                .deceasedDomicileInEngWales(DECEASED_DOM_UK)
                .primaryApplicantForenames(PRIMARY_FORENAMES)
                .primaryApplicantSurname(PRIMARY_SURNAME)
                .primaryApplicantAddress(PRIMARY_ADDRESS)
                .primaryApplicantIsApplying(PRIMARY_APPLICANT_APPLYING)
                .primaryApplicantHasAlias(PRIMARY_APPLICANT_HAS_ALIAS)
                .otherExecutorExists(OTHER_EXEC_EXISTS)
                .solsWillType(WILL_TYPE_PROBATE)
                .willExists(WILL_EXISTS)
                .willAccessOriginal(WILL_ACCESS_ORIGINAL)
                .ihtNetValue(NET)
                .ihtGrossValue(GROSS)
                .solsSolicitorAppReference(SOLICITOR_APP_REFERENCE)
                .willHasCodicils(WILL_HAS_CODICILS)
                .willNumberOfCodicils(NUMBER_OF_CODICILS)
                .solsSolicitorFirmName(SOLICITOR_FIRM_NAME)
                .solsSolicitorAddress(solsAddress)
                .ukEstate(UK_ESTATE)
                .applicationGrounds(APPLICATION_GROUNDS)
                .willDispose(YES)
                .englishWill(NO)
                .appointExec(YES)
                .ihtFormId(IHT_FORM)
                .solsSOTForenames(SOLICITOR_FORENAMES)
                .solsSOTSurname(SOLICITOR_SURNAME)
                .solsSolicitorIsExec(YES)
                .solsSolicitorIsMainApplicant(YES)
                .solsSolicitorIsApplying(YES)
                .solsSolicitorNotApplyingReason(SOLS_NOT_APPLYING_REASON)
                .solsSOTJobTitle(SOLICITOR_JOB_TITLE)
                .solsPaymentMethods(PAYMENT_METHOD)
                .applicationFee(APPLICATION_FEE)
                .feeForUkCopies(FEE_FOR_UK_COPIES)
                .feeForNonUkCopies(FEE_FOR_NON_UK_COPIES)
                .extraCopiesOfGrant(EXTRA_UK)
                .outsideUKGrantCopies(EXTRA_OUTSIDE_UK)
                .totalFee(TOTAL_FEE)
                .scannedDocuments(SCANNED_DOCUMENTS_LIST);
    }

    @Test
    public void shouldValidateWithDodIsNullError() throws Exception {
        validateDodIsNullError(SOLS_VALIDATE_URL);
    }

    @Test
    public void shouldValidateDobIsNullError() throws Exception {
        validateDobIsNullError(SOLS_VALIDATE_URL);
    }

    @Test
    public void shouldValidateWithForenameIsNullError() throws Exception {
        validateForenameIsNullError(SOLS_VALIDATE_URL);
    }

    @Test
    public void shouldValidateWithSurnameIsNullError() throws Exception {
        validateSurnameIsNullError(SOLS_VALIDATE_URL);
    }

    @Test
    public void shouldValidateWithPrimaryApplicantAddressIsNullErrorIntestacy() throws Exception {
        caseDataBuilder.solsWillType(WILL_TYPE_INTESTACY);
        caseDataBuilder.primaryApplicantEmailAddress(PRIMARY_APPLICANT_EMAIL);
        caseDataBuilder.deceasedMaritalStatus(MARITAL_STATUS);
        caseDataBuilder.solsApplicantRelationshipToDeceased(RELATIONSHIP_TO_DECEASED);
        caseDataBuilder.solsMinorityInterest(MINORITY_INTEREST);
        caseDataBuilder.solsApplicantSiblings(APPLICANT_SIBLINGS);
        validateAddressIsNullError(SOLS_VALIDATE_INTESTACY_URL);
    }

    @Test
    public void shouldValidateWithPrimaryApplicantAddressIsNullErrorAdmonWill() throws Exception {
        caseDataBuilder.solsWillType(WILL_TYPE_ADMON);
        caseDataBuilder.solsEntitledMinority(ENTITLED_MINORITY);
        caseDataBuilder.solsDiedOrNotApplying(DIED_OR_NOT_APPLYING);
        caseDataBuilder.solsResiduary(RESIDUARY);
        caseDataBuilder.solsResiduaryType(RESIDUARY_TYPE);
        caseDataBuilder.solsLifeInterest(LIFE_INTEREST);
        caseDataBuilder.primaryApplicantEmailAddress(PRIMARY_APPLICANT_EMAIL);
        validateAddressIsNullError(SOLS_VALIDATE_ADMON_URL);
    }

    @Test
    public void shouldValidateWithSolicitorIHTFormIsNullError() throws Exception {
        caseDataBuilder.ihtFormId(null);
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        
        mockMvc.perform(post(SOLS_VALIDATE_URL).content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("caseDetails.data.ihtFormId"))
                .andExpect(jsonPath("$.fieldErrors[0].code").value("NotBlank"))
                .andExpect(jsonPath("$.fieldErrors[0].message").value("Solicitor IHT Form cannot be empty"));
    }


    @Test
    public void shouldSuccesfullyGenerateProbateDeclaration() throws Exception {
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);
        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);

        Document probateDocument = Document.builder().documentType(DocumentType.LEGAL_STATEMENT_PROBATE)
                .documentLink(DocumentLink.builder().documentFilename("legalStatementProbate.pdf").build())
                .build();
        when(pdfManagementService.generateAndUpload(any(CallbackRequest.class), any(DocumentType.class)))
                .thenReturn(probateDocument);
        mockMvc.perform(post(SOLS_VALIDATE_PROBATE_URL).content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.data.solsLegalStatementDocument.document_filename").value("legalStatementProbate.pdf"));
    }

    @Test
    public void shouldSuccesfullyGenerateIntestacyDeclaration() throws Exception {
        caseDataBuilder.solsWillType(WILL_TYPE_INTESTACY);
        caseDataBuilder.primaryApplicantEmailAddress(PRIMARY_APPLICANT_EMAIL);
        caseDataBuilder.deceasedMaritalStatus(MARITAL_STATUS);
        caseDataBuilder.solsApplicantRelationshipToDeceased(RELATIONSHIP_TO_DECEASED);
        caseDataBuilder.solsMinorityInterest(MINORITY_INTEREST);
        caseDataBuilder.solsApplicantSiblings(APPLICANT_SIBLINGS);
        caseDataBuilder.solsSolicitorIsExec(NO);
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);
        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);

        Document probateDocument = Document.builder().documentType(DocumentType.LEGAL_STATEMENT_INTESTACY)
                .documentLink(DocumentLink.builder().documentFilename("legalStatementIntestacy.pdf").build())
                .build();
        when(pdfManagementService.generateAndUpload(any(CallbackRequest.class), any(DocumentType.class)))
                .thenReturn(probateDocument);
        mockMvc.perform(post(SOLS_VALIDATE_INTESTACY_URL).content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.data.solsLegalStatementDocument.document_filename").value("legalStatementIntestacy.pdf"));
    }

    @Test
    public void shouldSuccesfullyGenerateAdmonWillDeclaration() throws Exception {
        caseDataBuilder.solsWillType(WILL_TYPE_ADMON);
        caseDataBuilder.solsEntitledMinority(ENTITLED_MINORITY);
        caseDataBuilder.solsDiedOrNotApplying(DIED_OR_NOT_APPLYING);
        caseDataBuilder.solsResiduary(RESIDUARY);
        caseDataBuilder.solsResiduaryType(RESIDUARY_TYPE);
        caseDataBuilder.solsLifeInterest(LIFE_INTEREST);
        caseDataBuilder.primaryApplicantEmailAddress(PRIMARY_APPLICANT_EMAIL);
        caseDataBuilder.solsSolicitorIsExec(NO);
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);
        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);

        Document probateDocument = Document.builder().documentType(DocumentType.LEGAL_STATEMENT_ADMON)
                .documentLink(DocumentLink.builder().documentFilename("legalStatementAdmon.pdf").build())
                .build();
        when(pdfManagementService.generateAndUpload(any(CallbackRequest.class), any(DocumentType.class)))
                .thenReturn(probateDocument);
        mockMvc.perform(post(SOLS_VALIDATE_ADMON_URL).content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.data.solsLegalStatementDocument.document_filename").value("legalStatementAdmon.pdf"));
    }

    @Test
    public void shouldValidateWithSolIsExecutorIsNullError() throws Exception {
        caseDataBuilder.solsSolicitorIsExec(null);
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);

        mockMvc.perform(post(SOLS_VALIDATE_URL).content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("caseDetails.data.solsSolicitorIsExec"))
                .andExpect(jsonPath("$.fieldErrors[0].code").value("NotBlank"))
                .andExpect(jsonPath("$.fieldErrors[0].message").value("Solicitor named as an exec must be chosen"));
    }

    @Test
    public void shouldValidateWithDodIsNullErrorForCaseDetails() throws Exception {
        validateDodIsNullError(CASE_VALIDATE_CASE_DETAILS_URL);
    }

    @Test
    public void shouldValidateDobIsNullErrorForCaseDetails() throws Exception {
        validateDobIsNullError(CASE_VALIDATE_CASE_DETAILS_URL);
    }

    @Test
    public void shouldValidateWithDeceasedForenameIsNullErrorForCaseDetails() throws Exception {
        validateForenameIsNullError(CASE_VALIDATE_CASE_DETAILS_URL);
    }

    @Test
    public void shouldValidateWithDeceasedSurnameIsNullErrorForCaseDetails() throws Exception {
        validateSurnameIsNullError(CASE_VALIDATE_CASE_DETAILS_URL);
    }

    private void validateDodIsNullError(String url) throws Exception {
        caseDataBuilder.deceasedDateOfDeath(null);
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(url).content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("caseDetails.data.deceasedDateOfDeath"))
                .andExpect(jsonPath("$.fieldErrors[0].code").value("NotNull"))
                .andExpect(jsonPath("$.fieldErrors[0].message").value("Date of death cannot be empty"));
    }

    private void validateDobIsNullError(String url) throws Exception {
        caseDataBuilder.deceasedDateOfBirth(null);
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(url).content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("caseDetails.data.deceasedDateOfBirth"))
                .andExpect(jsonPath("$.fieldErrors[0].code").value("NotNull"))
                .andExpect(jsonPath("$.fieldErrors[0].message").value("Date of birth cannot be empty"));
    }

    private void validateForenameIsNullError(String url) throws Exception {
        caseDataBuilder.deceasedForenames(null);
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(url).content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("caseDetails.data.deceasedForenames"))
                .andExpect(jsonPath("$.fieldErrors[0].code").value("NotBlank"))
                .andExpect(jsonPath("$.fieldErrors[0].message").value("Deceased forename cannot be empty"));
    }

    private void validateSurnameIsNullError(String url) throws Exception {
        caseDataBuilder.deceasedSurname(null);
        caseDataBuilder.ukEstate(UK_ESTATE);
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(url).content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("caseDetails.data.deceasedSurname"))
                .andExpect(jsonPath("$.fieldErrors[0].code").value("NotBlank"))
                .andExpect(jsonPath("$.fieldErrors[0].message").value("Deceased surname cannot be empty"));
    }

    private void validateAddressIsNullError(String url) throws Exception {
        caseDataBuilder.primaryApplicantAddress(null);
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(url).content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("caseDetails.data.primaryApplicantAddress"))
                .andExpect(jsonPath("$.fieldErrors[0].code").value("NotNull"))
                .andExpect(jsonPath("$.fieldErrors[0].message").value("The executor address cannot be empty"));
    }

    @Test
    public void shouldReturnAliasNameTransformed() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadAliasNames.json");

        mockMvc.perform(post(CASE_PRINTED).content(solicitorPayload).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
    }

    @Test
    public void shouldReturnAdditionalExecutorsTransformed() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile("solicitorAdditionalExecutors.json");

        mockMvc.perform(post(CASE_PRINTED).content(solicitorPayload).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
    }

    @Test
    public void shouldReturnCheckListValidateSuccessfulQAState() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile("solicitorAdditionalExecutors.json");

        mockMvc.perform(post(CASE_CHCEKLIST_URL).content(solicitorPayload).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.state").value("BOCaseQA"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
    }

    @Test
    public void shouldReturnCheckListValidateUnSuccessfulQ1IsNo() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadAliasNames.json");

        mockMvc.perform(post(CASE_CHCEKLIST_URL).content(solicitorPayload).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors[0]").value("Ensure all checks have been completed, cancel to return to the examining state"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));

    }

    @Test
    public void shouldReturnCheckListValidateUnSuccessfulQ2IsNo() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadChecklist.json");

        mockMvc.perform(post(CASE_CHCEKLIST_URL).content(solicitorPayload).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors[0]").value("Ensure all checks have been completed, cancel to return to the examining state"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));

    }

    @Test
    public void shouldReturnCheckListValidateSuccessful() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile("solicitorAdditionalExecutorsReadyToIssue.json");

        mockMvc.perform(post(CASE_CHCEKLIST_URL).content(solicitorPayload).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
    }

    @Test
    public void shouldReturnSolicitorPaperFormSuccess() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadAliasNames.json");
        Document emailDocument = Document.builder().documentType(DocumentType.EMAIL)
            .documentLink(DocumentLink.builder().documentFilename("email.pdf").build())
            .build();

        when(notificationService.sendEmail(any(State.class), any(CaseDetails.class), any(Optional.class))).thenReturn(emailDocument);

        mockMvc.perform(post(PAPER_FORM_URL).content(solicitorPayload).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
    }

    @Test
    public void shouldReturnPaperFormWithoutEmail() throws Exception {
        String caseCreatorJson = testUtils.getStringFromFile("paperForm.json");

        when(notificationService.sendEmail(any(State.class), any(CaseDetails.class), any(Optional.class))).thenReturn(null);
        mockMvc.perform(post(PAPER_FORM_URL).content(caseCreatorJson).contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
    }

    @Test
    public void shouldReturnPaperFormWithEmail() throws Exception {
        String caseCreatorJson = testUtils.getStringFromFile("paperFormWithPrimaryApplicantEmail.json");

        Document document = Document.builder().documentType(DocumentType.DIGITAL_GRANT).build();
        when(notificationService.sendEmail(any(State.class), any(CaseDetails.class), any(Optional.class))).thenReturn(document);

        mockMvc.perform(post(PAPER_FORM_URL).content(caseCreatorJson).contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));

        when(notificationService.sendEmail(any(State.class), any(CaseDetails.class), any(Optional.class))).thenReturn(document);
        mockMvc.perform(post(PAPER_FORM_URL).content(caseCreatorJson).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
    }

    @Test
    public void shouldReturnScannedDocumentsAndStartAwaitingDocumentationPeriod() throws Exception {
        String scannedDocumentsJson = testUtils.getStringFromFile("scannedDocuments.json");

        mockMvc.perform(post(CASE_PRINTED).content(scannedDocumentsJson).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().string(containsString("controlNumber\":\"1234")))
                .andExpect(content().string(containsString("fileName\":\"scanneddocument.pdf")));

        verify(notificationService).startAwaitingDocumentationNotificationPeriod(any(CaseDetails.class));
    }

    @Test
    public void shouldStopCase() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile("solicitorAdditionalExecutors.json");

        mockMvc.perform(post(CASE_STOPPED_URL).content(solicitorPayload).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));

        verify(caseStoppedService).caseStopped(any(CaseDetails.class));
    }

    @Test
    public void shouldSetStateToCaseCreatedAfterResolveStateChoice() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadResolveStopForCaseCreated.json");

        mockMvc.perform(post(RESOLVE_STOP_URL).content(solicitorPayload).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.state").value("CaseCreated"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));

        verify(caseStoppedService).caseResolved(any(CaseDetails.class));
    }

    @Test
    public void shouldSetStateToCasePrintedAfterResolveStateChoice() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadResolveStopCasePrinted.json");

        mockMvc.perform(post(RESOLVE_STOP_URL).content(solicitorPayload).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.state").value("CasePrinted"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));

        verify(caseStoppedService).caseResolved(any(CaseDetails.class));
    }

    @Test
    public void shouldSetStateToReaddyForExaminationAfterResolveStateChoice() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadResolveStopReadyForExamination.json");

        mockMvc.perform(post(RESOLVE_STOP_URL).content(solicitorPayload).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.state").value("BOReadyForExamination"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));

        verify(caseStoppedService).caseResolved(any(CaseDetails.class));
    }

    @Test
    public void shouldSetStateToExaminingAfterResolveStateChoice() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadResolveStopForExamining.json");

        mockMvc.perform(post(RESOLVE_STOP_URL).content(solicitorPayload).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.state").value("BOExamining"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));

        verify(caseStoppedService).caseResolved(any(CaseDetails.class));
    }

    @Test
    public void shouldSetStateForRedeclarationCompleteToRedec() throws Exception {
        String payload = testUtils.getStringFromFile("payloadWithResponseRecorded.json");

        mockMvc.perform(post(REDEC_COMPLETE).content(payload).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.state").value(REDEC_NOTIFICATION_SENT_STATE))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
    }

    @Test
    public void shouldValidateWithPaperCase() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile("paperForm.json");

        Document document = Document.builder().documentType(DocumentType.DIGITAL_GRANT).build();
        when(notificationService.sendEmail(any(State.class), any(CaseDetails.class), any(Optional.class))).thenReturn(document);
        mockMvc.perform(post(REDECE_SOT).content(solicitorPayload).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors[0]").value("You can only use this event for digital cases."))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
    }

    @Test
    public void shouldValidateWithDigitalCase() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile("digitalCase.json");

        mockMvc.perform(post(REDECE_SOT).content(solicitorPayload).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
    }

    @Test
    public void shouldDefaultLegalStatementAmendOptionsForProbateCase() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile("solicitorWillTypeProbate.json");

        mockMvc.perform(post("/case/default-sols-next-steps")
            .content(solicitorPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.solsAmendLegalStatmentSelect.list_items[0].code", is("SolAppCreated")))
            .andExpect(jsonPath("$.data.solsAmendLegalStatmentSelect.list_items[0].label", is("Deceased Details")))
            .andExpect(jsonPath("$.data.solsAmendLegalStatmentSelect.list_items[1].code", is("WillLeft")))
            .andExpect(jsonPath("$.data.solsAmendLegalStatmentSelect.list_items[1].label", is("Grant of probate where the deceased left a will")))
            .andReturn();
    }

    @Test
    public void shouldDefaultLegalStatementAmendOptionsForIntestacyCase() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile("solicitorWillTypeIntestacy.json");

        mockMvc.perform(post("/case/default-sols-next-steps")
            .content(solicitorPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.solsAmendLegalStatmentSelect.list_items[0].code", is("SolAppCreated")))
            .andExpect(jsonPath("$.data.solsAmendLegalStatmentSelect.list_items[0].label", is("Deceased Details")))
            .andExpect(jsonPath("$.data.solsAmendLegalStatmentSelect.list_items[1].code", is("NoWill")))
            .andExpect(jsonPath("$.data.solsAmendLegalStatmentSelect.list_items[1].label", is("Letters of administration where the deceased left no will")))
            .andReturn();
    }

    @Test
    public void shouldDefaultLegalStatementAmendOptionsForAdmonCase() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile("solicitorWillTypeAdmon.json");

        mockMvc.perform(post("/case/default-sols-next-steps")
            .content(solicitorPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.solsAmendLegalStatmentSelect.list_items[0].code", is("SolAppCreated")))
            .andExpect(jsonPath("$.data.solsAmendLegalStatmentSelect.list_items[0].label", is("Deceased Details")))
            .andExpect(jsonPath("$.data.solsAmendLegalStatmentSelect.list_items[1].code", is("WillLeftAnnexed")))
            .andExpect(jsonPath("$.data.solsAmendLegalStatmentSelect.list_items[1].label", is("Letters of administration with will annexed where the deceased left a will but none of the executors can apply")))
            .andReturn();
    }

}

