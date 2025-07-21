package uk.gov.hmcts.probate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.State;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorTrustCorps;
import uk.gov.hmcts.probate.model.ccd.raw.CodicilAddedDate;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicList;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicListItem;
import uk.gov.hmcts.probate.model.ccd.raw.EstateItem;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.StopReason;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData.CaseDataBuilder;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.AuditEvent;
import uk.gov.hmcts.probate.model.payments.pba.OrganisationEntityResponse;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.CaseStoppedService;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.hmcts.probate.service.PrepareNocService;
import uk.gov.hmcts.probate.service.caseaccess.CcdDataStoreService;
import uk.gov.hmcts.probate.service.RegistrarDirectionService;
import uk.gov.hmcts.probate.service.ccd.AuditEventService;
import uk.gov.hmcts.probate.service.organisations.OrganisationsRetrievalService;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.probate.service.user.UserInfoService;
import uk.gov.hmcts.probate.transformer.CaseDataTransformer;
import uk.gov.hmcts.probate.util.TestUtils;
import uk.gov.hmcts.reform.authorisation.generators.ServiceAuthTokenGenerator;
import uk.gov.hmcts.reform.probate.model.idam.UserInfo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.REDEC_NOTIFICATION_SENT_STATE;
import static uk.gov.hmcts.probate.model.Constants.YES;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class BusinessValidationControllerIT {

    private static final LocalDate DOB = LocalDate.of(1990, 4, 4);
    private static final LocalDate DOD = LocalDate.of(2017, 4, 4);
    private static final Long ID = 1234567890123456L;
    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};

    private static final String APPLICATION_SUBMITTED_DATE = "2023-02-02";
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
    private static final SolsAddress DECEASED_ADDRESS =
        SolsAddress.builder().addressLine1(DEC_ADD_LINE1).postCode(DEC_ADD_PC).build();
    private static final String EX_ADD_LINE1 = "ExLine1";
    private static final String EX_ADD_PC = "ExPC";
    private static final SolsAddress PRIMARY_ADDRESS =
        SolsAddress.builder().addressLine1(EX_ADD_LINE1).postCode(EX_ADD_PC).build();
    private static final String PRIMARY_APPLICANT_APPLYING = "Yes";
    private static final String PRIMARY_APPLICANT_HAS_ALIAS = "No";
    private static final String PRIMARY_APPLICANT_EMAIL = "primary@probate-test.com";
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
    private static final String SOLS_DEFAULT_IHT_ESTATE_URL = "/case/default-iht-estate";
    private static final String SOLS_CREATE_VALIDATE_URL = "/case/sols-create-validate";
    private static final String SOLS_VALIDATE_IHT_ESTATE_URL = "/case/validate-iht-estate";
    private static final String SOLS_VALIDATE_URL = "/case/sols-validate";
    private static final String SOLS_VALIDATE_PROBATE_URL = "/case/sols-validate-probate";
    private static final String SOLS_VALIDATE_EXEC_URL = "/case/sols-validate-executors";
    private static final String SOLS_VALIDATE_WILL_AND_CODICIL_DATES_URL = "/case/sols-validate-will-and-codicil-dates";
    private static final String SOLS_VALIDATE_INTESTACY_URL = "/case/sols-validate-intestacy";
    private static final String SOLS_VALIDATE_ADMON_URL = "/case/sols-validate-admon";
    private static final String CASE_VALIDATE_CASE_DETAILS_URL = "/case/validateCaseDetails";
    private static final String CASE_PRINTED = "/case/casePrinted";
    private static final String PAPER_FORM_URL = "/case/paperForm";
    private static final String RESOLVE_STOP_URL = "/case/resolveStop";
    private static final String CHANGE_CASE_STATE_URL = "/case/changeCaseState";
    private static final String RESOLVE_CAVEAT_STOP_URL = "/case/resolveCaveatStopState";
    private static final String CASE_STOPPED_URL = "/case/case-stopped";
    private static final String REDEC_COMPLETE = "/case/redeclarationComplete";
    private static final String REDECE_SOT = "/case/redeclarationSot";
    private static final String DEFAULT_SOLS_NEXT_STEPS = "/case/default-sols-next-steps";
    private static final String DEFAULT_SOLS_PBA = "/case/default-sols-pba";
    private static final String REACTIVATE_CASE = "/case/reactivate-case";
    private static final String PA_CREATE_URL = "/case/pa-create";
    private static final String DEFAULT_REGISTRARS_DECISION = "/case/default-registrars-decision";
    private static final String REGISTRARS_DECISION = "/case/registrars-decision";
    private static final String AUTH_HEADER = "Authorization";
    private static final String AUTH_TOKEN = "Bearer someAuthorizationToken";
    private static final String SOLS_VALIDATE_FURTHER_EVIDENCE_URL = "/case/validate-further-evidence";
    private static final String CASE_WORKER_ESCALATED = "/case/case-worker-escalated";
    private static final String CASE_WORKER_RESOLVED_ESCALATED = "/case/resolve-case-worker-escalated";
    private static final String PREPARE_FOR_NOC = "/case/prepare-case-for-noc";
    private static final String UNIQUE_CODE = "/case/validate-unique-code";
    private static final String ROLLBACK = "/case/rollback";
    private static final String uniqueCode = "CTS 0405231104 3tpp s8e9";
    private static final String FURTHER_EVIDENCE = "Some Further Evidence";
    private static final String VALUES_PAGE = "/case/validate-values-page";
    private static final String CHANGE_DOB = "/case/changeDob";
    private static final String LAST_MODIFIED_DATE = "/case/setLastModifiedDate";
    private static final String INVALID_EVENT = "/case/invalidEvent";
    private static final String CAVEAT_EVENT = "/case/use-caveat-notification-event";
    private static final String ASSEMBLE_LETTER_EVENT = "/case/use-assemble-letter-event";
    private static final String SUPER_USER_MAKE_DORMANT = "/case/superUserMakeDormantCase";
    private static final String VALIDATE_STOP_REASON = "/case/validate-stop-reason";

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
    private static final Optional<UserInfo> CASEWORKER_USERINFO = Optional.ofNullable(UserInfo.builder()
            .familyName("familyName")
            .givenName("givenname")
            .roles(Arrays.asList("caseworker-probate"))
            .build());

    private final TestUtils testUtils = new TestUtils();
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private MockMvc mockMvc;
    private CaseDataBuilder caseDataBuilder;

    @MockitoBean
    private PDFManagementService pdfManagementService;

    @MockitoBean
    private CaseStoppedService caseStoppedService;

    @MockitoBean
    private NotificationService notificationService;
    @MockitoBean
    private CaseDataTransformer caseDataTransformer;
    @MockitoBean
    private CcdDataStoreService ccdDataStoreService;
    @MockitoBean
    private RegistrarDirectionService registrarDirectionService;
    @MockitoBean
    private PrepareNocService prepareNocService;
    @MockitoBean
    private UserInfoService userInfoService;
    @MockitoBean
    private SecurityUtils securityUtils;
    @MockitoBean
    private AuditEventService auditEventService;
    @MockitoBean
    private ServiceAuthTokenGenerator serviceAuthTokenGenerator;



    @MockitoSpyBean
    OrganisationsRetrievalService organisationsRetrievalService;

    @BeforeEach
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
            // .applicationGrounds(APPLICATION_GROUNDS) - commented for dtsb-904 as likely to be reinstated
            .willDispose(YES)
            .englishWill(NO)
            .appointExec(YES)
            .ihtFormId(IHT_FORM)
            .solsSOTForenames(SOLICITOR_FORENAMES)
            .solsSOTSurname(SOLICITOR_SURNAME)
            .solsSolicitorIsExec(YES)
            .solsSolicitorIsApplying(YES)
            .solsSolicitorNotApplyingReason(SOLS_NOT_APPLYING_REASON)
            .solsSOTJobTitle(SOLICITOR_JOB_TITLE)
            .solsPaymentMethods(PAYMENT_METHOD)
            .solsPBANumber(DynamicList.builder().value(DynamicListItem.builder().code("PBA1234").build()).build())
            .applicationFee(APPLICATION_FEE)
            .feeForUkCopies(FEE_FOR_UK_COPIES)
            .feeForNonUkCopies(FEE_FOR_NON_UK_COPIES)
            .extraCopiesOfGrant(EXTRA_UK)
            .outsideUKGrantCopies(EXTRA_OUTSIDE_UK)
            .totalFee(TOTAL_FEE)
            .scannedDocuments(SCANNED_DOCUMENTS_LIST);

        OrganisationEntityResponse organisationEntityResponse = new OrganisationEntityResponse();
        organisationEntityResponse.setOrganisationIdentifier("ORG_ID");
        organisationEntityResponse.setName("ORGANISATION_NAME");
        doReturn(organisationEntityResponse).when(organisationsRetrievalService).getOrganisationEntity(ID.toString(),
                AUTH_TOKEN);

        doReturn(CASEWORKER_USERINFO).when(userInfoService).getCaseworkerInfo();
    }

    @Test
    void shouldSetupIHTEstate() throws Exception {
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(SOLS_DEFAULT_IHT_ESTATE_URL).content(json).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void shouldValidateIHTEstate() throws Exception {
        LocalDateTime dod = LocalDateTime.parse("2021-07-01T00:00:00.000");
        caseDataBuilder.deceasedDateOfDeath(dod.toLocalDate());
        caseDataBuilder.applicationSubmittedDate(APPLICATION_SUBMITTED_DATE);
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(SOLS_VALIDATE_IHT_ESTATE_URL).content(json).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void shouldValidateWithDodIsNullError() throws Exception {
        validateDodIsNullError(SOLS_VALIDATE_URL);
    }

    @Test
    void shouldValidateDobIsNullError() throws Exception {
        validateDobIsNullError(SOLS_VALIDATE_URL);
    }

    @Test
    void shouldValidateWithForenameIsNullError() throws Exception {
        validateForenameIsNullError(SOLS_VALIDATE_URL);
    }

    @Test
    void shouldValidateWithSurnameIsNullError() throws Exception {
        validateSurnameIsNullError(SOLS_VALIDATE_URL);
    }

    @Test
    void shouldValidateWithPrimaryApplicantAddressIsNullErrorIntestacy() throws Exception {
        caseDataBuilder.solsWillType(WILL_TYPE_INTESTACY);
        caseDataBuilder.primaryApplicantEmailAddress(PRIMARY_APPLICANT_EMAIL);
        caseDataBuilder.deceasedMaritalStatus(MARITAL_STATUS);
        caseDataBuilder.solsApplicantRelationshipToDeceased(RELATIONSHIP_TO_DECEASED);
        caseDataBuilder.solsMinorityInterest(MINORITY_INTEREST);
        caseDataBuilder.solsApplicantSiblings(APPLICANT_SIBLINGS);
        validateAddressIsNullError(SOLS_VALIDATE_INTESTACY_URL);
    }

    @Test
    void shouldValidateWithPrimaryApplicantAddressIsNullErrorAdmonWill() throws Exception {
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
    void shouldValidateWithSolicitorIHTFormIsNullWithNoError() throws Exception {
        caseDataBuilder.ihtFormId(null);
        caseDataBuilder.applicationSubmittedDate(APPLICATION_SUBMITTED_DATE);
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);

        mockMvc.perform(post(SOLS_VALIDATE_URL).content(json).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void shouldValidateWithCorrectNumberOfExecutors() throws Exception {
        CollectionMember<AdditionalExecutorTrustCorps> additionalExecutorTrustCorp = new CollectionMember<>(
                new AdditionalExecutorTrustCorps(
                        "Executor forename",
                        "Executor surname",
                        "Solicitor"
                ));
        List<CollectionMember<AdditionalExecutorTrustCorps>> additionalExecutorsTrustCorpList = new ArrayList<>();
        additionalExecutorsTrustCorpList.add(additionalExecutorTrustCorp);
        additionalExecutorsTrustCorpList.add(additionalExecutorTrustCorp);

        caseDataBuilder.additionalExecutorsTrustCorpList(additionalExecutorsTrustCorpList);

        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);

        mockMvc.perform(post(SOLS_VALIDATE_EXEC_URL).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldValidateWithCorrectWillAndCodicilDates() throws Exception {
        final List<CollectionMember<CodicilAddedDate>> codicilDates =
                Arrays.asList(new CollectionMember<>(CodicilAddedDate.builder()
                        .dateCodicilAdded(LocalDate.now().minusDays(1)).build()));

        caseDataBuilder.codicilAddedDateList(codicilDates);
        caseDataBuilder.originalWillSignedDate(LocalDate.now().minusDays(3));
        caseDataBuilder.deceasedDateOfDeath(LocalDate.now().minusDays(2));

        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);

        mockMvc.perform(post(SOLS_VALIDATE_WILL_AND_CODICIL_DATES_URL).content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldNotValidateWithInvalidWillDate() throws Exception {
        final List<CollectionMember<CodicilAddedDate>> codicilDates =
                Arrays.asList(new CollectionMember<>(CodicilAddedDate.builder()
                        .dateCodicilAdded(LocalDate.now().minusDays(1)).build()));

        caseDataBuilder.codicilAddedDateList(codicilDates);
        caseDataBuilder.originalWillSignedDate(LocalDate.now());

        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);

        mockMvc.perform(post(SOLS_VALIDATE_WILL_AND_CODICIL_DATES_URL).content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors[0]")
                        .value("A codicil cannot be made before the will was signed"))
                .andExpect(jsonPath("$.errors[1]")
                        .value("Ni ellir gwneud codisil cyn llofnodi'r ewyllys"))
                .andExpect(jsonPath("$.errors[2]")
                        .value("Original will signed date must be in the past"))
                .andExpect(jsonPath("$.errors[3]")
                        .value("Rhaid i'r dyddiad gwreiddiol a lofnodwyd yr ewyllys fod yn y gorffennol"))
                .andExpect(jsonPath("$.errors[4]")
                        .value("The will must be signed and dated before the date of death"))
                .andExpect(jsonPath("$.errors[5]")
                        .value("Rhaid bod yr ewyllys wedi?i llofnodi a'i dyddio cyn dyddiad y "
                                + "farwolaeth"));
    }

    @Test
    void shouldNotValidateWithInvalidCodicilDate() throws Exception {
        final List<CollectionMember<CodicilAddedDate>> codicilDates =
                Arrays.asList(new CollectionMember<>(CodicilAddedDate.builder()
                        .dateCodicilAdded(LocalDate.now()).build()));

        caseDataBuilder.codicilAddedDateList(codicilDates);
        caseDataBuilder.originalWillSignedDate(LocalDate.now().minusDays(1));

        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);

        mockMvc.perform(post(SOLS_VALIDATE_WILL_AND_CODICIL_DATES_URL).content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors[0]")
                        .value("Codicil date must be in the past"))
                .andExpect(jsonPath("$.errors[1]")
                        .value("Rhaid i ddyddiad y codisil fod yn y gorffennol"));
    }

    @Test
    void shouldNotValidateWithInvalidWillAndCodicilDates() throws Exception {
        final List<CollectionMember<CodicilAddedDate>> codicilDates =
                Arrays.asList(new CollectionMember<>(CodicilAddedDate.builder()
                        .dateCodicilAdded(LocalDate.now()).build()));

        caseDataBuilder.codicilAddedDateList(codicilDates);
        caseDataBuilder.originalWillSignedDate(LocalDate.now());

        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);

        mockMvc.perform(post(SOLS_VALIDATE_WILL_AND_CODICIL_DATES_URL).content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors[0]")
                        .value("Codicil date must be in the past"))
                .andExpect(jsonPath("$.errors[1]")
                        .value("Rhaid i ddyddiad y codisil fod yn y gorffennol"))
                .andExpect(jsonPath("$.errors[2]")
                        .value("Original will signed date must be in the past"))
                .andExpect(jsonPath("$.errors[3]")
                        .value("Rhaid i'r dyddiad gwreiddiol a lofnodwyd yr ewyllys fod yn y gorffennol"))
                .andExpect(jsonPath("$.errors[4]")
                        .value("The will must be signed and dated before the date of death"))
                .andExpect(jsonPath("$.errors[5]")
                        .value("Rhaid bod yr ewyllys wedi?i llofnodi a'i dyddio cyn dyddiad y "
                                + "farwolaeth"));
    }

    @Test
    void shouldNotValidateWithWillDateAfterDateOfDeath() throws Exception {
        caseDataBuilder.originalWillSignedDate(LocalDate.now().minusDays(1));
        caseDataBuilder.deceasedDateOfDeath(LocalDate.now().minusDays(2));

        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);

        mockMvc.perform(post(SOLS_VALIDATE_WILL_AND_CODICIL_DATES_URL).content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors[0]")
                        .value("The will must be signed and dated before the date of death"))
                .andExpect(jsonPath("$.errors[1]")
                        .value("Rhaid bod yr ewyllys wedi?i llofnodi a'i dyddio cyn dyddiad y "
                                + "farwolaeth"));
    }

    @Test
    void shouldNotValidateWithCodicilDateBeforeWillDate() throws Exception {
        final List<CollectionMember<CodicilAddedDate>> codicilDates =
                Arrays.asList(new CollectionMember<>(CodicilAddedDate.builder()
                        .dateCodicilAdded(LocalDate.now().minusDays(2)).build()));

        caseDataBuilder.codicilAddedDateList(codicilDates);
        caseDataBuilder.originalWillSignedDate(LocalDate.now().minusDays(1));

        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);

        mockMvc.perform(post(SOLS_VALIDATE_WILL_AND_CODICIL_DATES_URL).content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors[0]")
                        .value("A codicil cannot be made before the will was signed"))
                .andExpect(jsonPath("$.errors[1]")
                        .value("Ni ellir gwneud codisil cyn llofnodi'r ewyllys"));
    }

    @Test
    void shouldSuccesfullyGenerateProbateDeclaration() throws Exception {
        caseDataBuilder.applicationSubmittedDate(APPLICATION_SUBMITTED_DATE);
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);
        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);

        Document probateDocument = Document.builder().documentType(DocumentType.LEGAL_STATEMENT_PROBATE)
            .documentLink(DocumentLink.builder().documentFilename("legalStatementProbate.pdf").build())
            .build();
        when(pdfManagementService.generateAndUpload(any(CallbackRequest.class), any(DocumentType.class)))
            .thenReturn(probateDocument);
        mockMvc.perform(post(SOLS_VALIDATE_PROBATE_URL).content(json).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(
                jsonPath("$.data.solsLegalStatementDocument.document_filename")
                        .value("legalStatementProbate.pdf"));
    }

    @Test
    void shouldSuccesfullyGenerateTrustCorpsProbateDeclaration() throws Exception {
        caseDataBuilder.applicationSubmittedDate(APPLICATION_SUBMITTED_DATE);
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);
        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);

        Document probateDocument = Document.builder().documentType(DocumentType.LEGAL_STATEMENT_PROBATE_TRUST_CORPS)
                .documentLink(DocumentLink.builder().documentFilename("legalStatementGrantOfProbate.pdf").build())
                .build();
        when(pdfManagementService.generateAndUpload(any(CallbackRequest.class), any(DocumentType.class)))
                .thenReturn(probateDocument);
        mockMvc.perform(post(SOLS_VALIDATE_PROBATE_URL).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                        jsonPath("$.data.solsLegalStatementDocument.document_filename")
                                .value("legalStatementGrantOfProbate.pdf"));
    }

    @Test
    void shouldSuccesfullyGenerateIntestacyDeclaration() throws Exception {
        caseDataBuilder.solsWillType(WILL_TYPE_INTESTACY);
        caseDataBuilder.primaryApplicantEmailAddress(PRIMARY_APPLICANT_EMAIL);
        caseDataBuilder.deceasedMaritalStatus(MARITAL_STATUS);
        caseDataBuilder.solsApplicantRelationshipToDeceased(RELATIONSHIP_TO_DECEASED);
        caseDataBuilder.solsMinorityInterest(MINORITY_INTEREST);
        caseDataBuilder.solsApplicantSiblings(APPLICANT_SIBLINGS);
        caseDataBuilder.solsSolicitorIsExec(NO);
        caseDataBuilder.solsSolicitorIsApplying(NO);
        caseDataBuilder.applicationSubmittedDate(APPLICATION_SUBMITTED_DATE);
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);
        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);

        Document probateDocument = Document.builder().documentType(DocumentType.LEGAL_STATEMENT_INTESTACY)
            .documentLink(DocumentLink.builder().documentFilename("legalStatementIntestacy.pdf").build())
            .build();
        when(pdfManagementService.generateAndUpload(any(CallbackRequest.class), any(DocumentType.class)))
            .thenReturn(probateDocument);
        mockMvc.perform(post(SOLS_VALIDATE_INTESTACY_URL).content(json).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(
                jsonPath("$.data.solsLegalStatementDocument.document_filename")
                        .value("legalStatementIntestacy.pdf"));
    }

    @Test
    void shouldSuccesfullyGenerateAdmonWillDeclaration() throws Exception {
        caseDataBuilder.solsWillType(WILL_TYPE_ADMON);
        caseDataBuilder.solsEntitledMinority(ENTITLED_MINORITY);
        caseDataBuilder.solsDiedOrNotApplying(DIED_OR_NOT_APPLYING);
        caseDataBuilder.solsResiduary(RESIDUARY);
        caseDataBuilder.solsResiduaryType(RESIDUARY_TYPE);
        caseDataBuilder.solsLifeInterest(LIFE_INTEREST);
        caseDataBuilder.primaryApplicantEmailAddress(PRIMARY_APPLICANT_EMAIL);
        caseDataBuilder.solsSolicitorIsExec(NO);
        caseDataBuilder.solsSolicitorIsApplying(NO);
        caseDataBuilder.furtherEvidenceForApplication(FURTHER_EVIDENCE);
        caseDataBuilder.applicationSubmittedDate(APPLICATION_SUBMITTED_DATE);
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);
        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);

        Document probateDocument = Document.builder().documentType(DocumentType.LEGAL_STATEMENT_ADMON)
            .documentLink(DocumentLink.builder().documentFilename("legalStatementAdmon.pdf").build())
            .build();
        when(pdfManagementService.generateAndUpload(any(CallbackRequest.class), any(DocumentType.class)))
            .thenReturn(probateDocument);
        mockMvc.perform(post(SOLS_VALIDATE_ADMON_URL).content(json).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(
                jsonPath("$.data.solsLegalStatementDocument.document_filename")
                        .value("legalStatementAdmon.pdf"));
    }

    @Test
    void shouldValidateWithSolIsExecutorIsNullError() throws Exception {
        caseDataBuilder.solsSolicitorIsExec(null);
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);

        mockMvc.perform(post(SOLS_VALIDATE_URL).content(json).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
            .andExpect(jsonPath("$.fieldErrors[0].field")
                    .value("caseDetails.data.solsSolicitorIsExec"))
            .andExpect(jsonPath("$.fieldErrors[0].code").value("NotBlank"))
            .andExpect(jsonPath("$.fieldErrors[0].message")
                    .value("Solicitor named as an exec must be chosen"));
    }

    @Test
    void shouldValidateWithDodIsNullErrorForCaseDetails() throws Exception {
        validateDodIsNullError(CASE_VALIDATE_CASE_DETAILS_URL);
    }

    @Test
    void shouldValidateDobIsNullErrorForCaseDetails() throws Exception {
        validateDobIsNullError(CASE_VALIDATE_CASE_DETAILS_URL);
    }

    @Test
    void shouldValidateWithDeceasedForenameIsNullErrorForCaseDetails() throws Exception {
        validateForenameIsNullError(CASE_VALIDATE_CASE_DETAILS_URL);
    }

    @Test
    void shouldValidateWithDeceasedSurnameIsNullErrorForCaseDetails() throws Exception {
        validateSurnameIsNullError(CASE_VALIDATE_CASE_DETAILS_URL);
    }

    private void validateDodIsNullError(String url) throws Exception {
        caseDataBuilder.deceasedDateOfDeath(null);
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(url).header(AUTH_HEADER, AUTH_TOKEN)
                        .content(json).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
            .andExpect(jsonPath("$.fieldErrors[0].field")
                    .value("caseDetails.data.deceasedDateOfDeath"))
            .andExpect(jsonPath("$.fieldErrors[0].code").value("NotNull"))
            .andExpect(jsonPath("$.fieldErrors[0].message")
                    .value("Date of death cannot be empty"));
    }

    private void validateDobIsNullError(String url) throws Exception {
        caseDataBuilder.deceasedDateOfBirth(null);
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(url).header(AUTH_HEADER, AUTH_TOKEN)
                        .content(json).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
            .andExpect(jsonPath("$.fieldErrors[0].field")
                    .value("caseDetails.data.deceasedDateOfBirth"))
            .andExpect(jsonPath("$.fieldErrors[0].code").value("NotNull"))
            .andExpect(jsonPath("$.fieldErrors[0].message")
                    .value("Date of birth cannot be empty"));
    }

    private void validateForenameIsNullError(String url) throws Exception {
        caseDataBuilder.deceasedForenames(null);
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(url).header(AUTH_HEADER, AUTH_TOKEN)
                        .content(json).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
            .andExpect(jsonPath("$.fieldErrors[0].field")
                    .value("caseDetails.data.deceasedForenames"))
            .andExpect(jsonPath("$.fieldErrors[0].code").value("NotBlank"))
            .andExpect(jsonPath("$.fieldErrors[0].message")
                    .value("Deceased forename cannot be empty"));
    }

    private void validateSurnameIsNullError(String url) throws Exception {
        caseDataBuilder.deceasedSurname(null);
        caseDataBuilder.ukEstate(UK_ESTATE);
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(url).header(AUTH_HEADER, AUTH_TOKEN)
                        .content(json).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
            .andExpect(jsonPath("$.fieldErrors[0].field")
                    .value("caseDetails.data.deceasedSurname"))
            .andExpect(jsonPath("$.fieldErrors[0].code").value("NotBlank"))
            .andExpect(jsonPath("$.fieldErrors[0].message")
                    .value("Deceased surname cannot be empty"));
    }

    private void validateAddressIsNullError(String url) throws Exception {
        caseDataBuilder.primaryApplicantAddress(null);
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(url).content(json).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
            .andExpect(jsonPath("$.fieldErrors[0].field")
                    .value("caseDetails.data.primaryApplicantAddress"))
            .andExpect(jsonPath("$.fieldErrors[0].code").value("NotNull"))
            .andExpect(jsonPath("$.fieldErrors[0].message")
                    .value("The executor address cannot be empty"));
    }

    @Test
    void shouldReturnAliasNameTransformed() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadAliasNames.json");

        mockMvc.perform(post(CASE_PRINTED).header(AUTH_HEADER, AUTH_TOKEN)
                        .content(solicitorPayload).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldReturnAdditionalExecutorsTransformed() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile("solicitorAdditionalExecutors.json");

        mockMvc.perform(post(CASE_PRINTED).header(AUTH_HEADER, AUTH_TOKEN)
                        .content(solicitorPayload).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldReturnSolicitorPaperFormSuccess() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadAliasNames.json");
        Document emailDocument = Document.builder().documentType(DocumentType.EMAIL)
            .documentLink(DocumentLink.builder().documentFilename("email.pdf").build())
            .build();

        when(notificationService.sendEmail(any(State.class), any(CaseDetails.class), any(Optional.class)))
            .thenReturn(emailDocument);

        mockMvc.perform(post(PAPER_FORM_URL).header(AUTH_HEADER, AUTH_TOKEN)
                        .content(solicitorPayload).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.schemaVersion", is("2.0.0")))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldReturnPersonalPaperFormSuccess() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadAliasNames.json");

        solicitorPayload =  solicitorPayload.replaceFirst("\"applicationType\": \"Solicitor\"",
                "\"applicationType\": \"Personal\"");

        Document emailDocument = Document.builder().documentType(DocumentType.EMAIL)
                .documentLink(DocumentLink.builder().documentFilename("email.pdf").build())
                .build();

        when(notificationService.sendEmail(any(State.class), any(CaseDetails.class), any(Optional.class)))
                .thenReturn(emailDocument);

        mockMvc.perform(post(PAPER_FORM_URL).header(AUTH_HEADER, AUTH_TOKEN)
                        .content(solicitorPayload).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.schemaVersion").doesNotExist())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldReturnPaperFormWithoutEmail() throws Exception {
        String caseCreatorJson = testUtils.getStringFromFile("paperForm.json");

        when(notificationService.sendEmail(any(State.class), any(CaseDetails.class), any(Optional.class)))
            .thenReturn(null);
        mockMvc.perform(post(PAPER_FORM_URL).header(AUTH_HEADER, AUTH_TOKEN)
                        .content(caseCreatorJson).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldReturnPaperFormWithEmail() throws Exception {
        String caseCreatorJson = testUtils.getStringFromFile("paperFormWithPrimaryApplicantEmail.json");

        Document document = Document.builder().documentType(DocumentType.DIGITAL_GRANT).build();
        when(notificationService.sendEmail(any(State.class), any(CaseDetails.class), any(Optional.class)))
            .thenReturn(document);

        mockMvc.perform(post(PAPER_FORM_URL).header(AUTH_HEADER, AUTH_TOKEN)
                        .content(caseCreatorJson).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldSubmitTrustCorpsSolicitorGoPCaseForCaseworker() throws Exception {
        String caseCreatorJson = testUtils.getStringFromFile("solicitorWillTypeProbate.json");

        when(notificationService.sendEmail(any(State.class), any(CaseDetails.class), any(Optional.class)))
                .thenReturn(null);
        mockMvc.perform(post(PAPER_FORM_URL).header(AUTH_HEADER, AUTH_TOKEN)
                        .content(caseCreatorJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldReturnScannedDocumentsAndStartAwaitingDocumentationPeriod() throws Exception {
        String scannedDocumentsJson = testUtils.getStringFromFile("scannedDocuments.json");

        mockMvc.perform(post(CASE_PRINTED).header(AUTH_HEADER, AUTH_TOKEN)
                        .content(scannedDocumentsJson).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(containsString("controlNumber\":\"1234")))
            .andExpect(content().string(containsString("fileName\":\"scanneddocument.pdf")));

        verify(notificationService).startAwaitingDocumentationNotificationPeriod(any(CaseDetails.class));
    }

    @Test
    void shouldStopCase() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile("solicitorAdditionalExecutors.json");

        mockMvc.perform(post(CASE_STOPPED_URL).header(AUTH_HEADER, AUTH_TOKEN)
                        .content(solicitorPayload).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(caseStoppedService).caseStopped(any(CaseDetails.class));
    }

    @Test
    void shouldSetStateToBOCaseQAAfterResolveStateChoice() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadResolveStopForBOCaseQA.json");

        mockMvc.perform(post(RESOLVE_STOP_URL).header(AUTH_HEADER, AUTH_TOKEN)
                        .content(solicitorPayload).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.state").value("BOCaseQA"))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(caseStoppedService).caseResolved(any(CaseDetails.class));
    }

    @Test
    void shouldSetStateToCasePrintedAfterResolveStateChoice() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadResolveStopCasePrinted.json");

        mockMvc.perform(post(RESOLVE_STOP_URL).header(AUTH_HEADER, AUTH_TOKEN)
                        .content(solicitorPayload).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.state").value("CasePrinted"))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(caseStoppedService).caseResolved(any(CaseDetails.class));
    }

    @Test
    void shouldSetStateToReaddyForExaminationAfterResolveStateChoice() throws Exception {
        String solicitorPayload =
                testUtils.getStringFromFile("solicitorPayloadResolveStopReadyForReadyToIssue.json");

        mockMvc.perform(post(RESOLVE_STOP_URL).header(AUTH_HEADER, AUTH_TOKEN)
                        .content(solicitorPayload).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.state").value("BOReadyToIssue"))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(caseStoppedService).caseResolved(any(CaseDetails.class));
    }

    @Test
    void shouldSetStateToExaminingAfterResolveStateChoice() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile(
                "solicitorPayloadResolveStopForCaseMatchingIssueGrant.json");

        mockMvc.perform(post(RESOLVE_STOP_URL).header(AUTH_HEADER, AUTH_TOKEN)
                        .content(solicitorPayload).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.state").value("BOCaseMatchingIssueGrant"))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(caseStoppedService).caseResolved(any(CaseDetails.class));
    }

    @Test
    void shouldSetStateToBOCaseWorkerEscalationAfterCaseworkerEscalated() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile(
                "solicitorPayloadCaseWorkerEscalation.json");

        mockMvc.perform(post(CASE_WORKER_RESOLVED_ESCALATED).header(AUTH_HEADER, AUTH_TOKEN)
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldSetStateToBOCaseMatchingIssueGrantAfterResolveCaseworkerEscalated() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile(
                "solicitorPayloadCaseWorkerResolveEscalation.json");

        mockMvc.perform(post(CASE_WORKER_RESOLVED_ESCALATED).header(AUTH_HEADER, AUTH_TOKEN)
                        .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.state").value("BOCaseQA"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldSetStateToBOCaseMatchingIssueGrantAfterChangeCaseState() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile(
                "solicitorPayloadChangeCaseStateForCaseMatchingIssueGrant.json");

        mockMvc.perform(post(CHANGE_CASE_STATE_URL).header(AUTH_HEADER, AUTH_TOKEN)
                        .content(solicitorPayload).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.state").value("BOCaseMatchingIssueGrant"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldSetStateToBOCaseMatchingIssueGrantAfterResolveCaveatStopState() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile(
                "solicitorPayloadResolveCaveatStopStateForCaseMatchingIssueGrant.json");

        mockMvc.perform(post(RESOLVE_CAVEAT_STOP_URL).content(solicitorPayload).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.state").value("BOCaseMatchingIssueGrant"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldSetStateForRedeclarationCompleteToRedec() throws Exception {
        String payload = testUtils.getStringFromFile("payloadWithResponseRecorded.json");

        mockMvc.perform(post(REDEC_COMPLETE).header(AUTH_HEADER, AUTH_TOKEN)
                        .content(payload).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.state").value(REDEC_NOTIFICATION_SENT_STATE))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldValidateWithPaperCase() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile("paperForm.json");

        Document document = Document.builder().documentType(DocumentType.DIGITAL_GRANT).build();
        when(notificationService.sendEmail(any(State.class), any(CaseDetails.class), any(Optional.class)))
            .thenReturn(document);
        mockMvc.perform(post(REDECE_SOT).header(AUTH_HEADER, AUTH_TOKEN)
                        .content(solicitorPayload).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors[0]")
                        .value("You can only use this event for digital cases."))
                .andExpect(jsonPath("$.errors[1]")
                        .value("Dim ond ar gyfer achosion digidol y gallwch ddefnyddio'r adnodd hwn."))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldValidateIhtNetGreaterThanGrossProbateValue() throws Exception {

        String caseCreatorJson = testUtils.getStringFromFile("paperForm.json");

        caseCreatorJson = caseCreatorJson.replaceFirst("\"ihtNetValue\": \"200000\"",
            "\"ihtNetValue\": \"400000\"");

        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        mockMvc.perform(post(SOLS_VALIDATE_IHT_ESTATE_URL).content(caseCreatorJson)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.errors[1]")
                .value("Ni all gwerth gros y cais am brofiant fod yn llai na gwerth net y cais am "
                        + "brofiant"));
    }

    @Test
    void shouldValidateIhtNetGreaterThanGrossIhtValue() throws Exception {

        String caseCreatorJson = testUtils.getStringFromFile("paperForm.json");

        caseCreatorJson = caseCreatorJson.replaceFirst("\"ihtGrossValue\": \"300000\"",
            "\"ihtGrossValue\": \"300000\",\n\"ihtEstateNetValue\": \"300000\",\n"
                + "\"ihtEstateGrossValue\": \"200000\"");

        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        mockMvc.perform(post(SOLS_VALIDATE_IHT_ESTATE_URL).content(caseCreatorJson)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.errors[0]")
                .value("The gross IHT value cannot be less than the net IHT value"));
    }

    @Test
    void shouldValidateWithDigitalCase() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile("digitalCase.json");

        mockMvc.perform(post(REDECE_SOT).header(AUTH_HEADER, AUTH_TOKEN)
                        .content(solicitorPayload).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldDefaultLegalStatementAmendOptionsForProbateCase() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile("solicitorWillTypeProbate.json");

        mockMvc.perform(post(DEFAULT_SOLS_NEXT_STEPS)
            .header(AUTH_HEADER, AUTH_TOKEN)
            .content(solicitorPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.solsAmendLegalStatmentSelect.list_items[0].code",
                    is("SolAppCreatedSolicitorDtls")))
            .andExpect(jsonPath("$.data.solsAmendLegalStatmentSelect.list_items[0].label",
                    is("Probate practitioner details")))
            .andExpect(jsonPath("$.data.solsAmendLegalStatmentSelect.list_items[1].code",
                    is("SolAppCreatedDeceasedDtls")))
            .andExpect(jsonPath("$.data.solsAmendLegalStatmentSelect.list_items[1].label",
                    is("Deceased details")))
            .andExpect(jsonPath("$.data.solsAmendLegalStatmentSelect.list_items[2].code",
                    is("WillLeft")))
            .andExpect(jsonPath("$.data.solsAmendLegalStatmentSelect.list_items[2].label",
                is("Probate details")))
            .andReturn();
    }

    @Test
    void shouldDefaultLegalStatementAmendOptionsForIntestacyCase() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile("solicitorWillTypeIntestacy.json");

        mockMvc.perform(post(DEFAULT_SOLS_NEXT_STEPS)
            .header(AUTH_HEADER, AUTH_TOKEN)
            .content(solicitorPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.solsAmendLegalStatmentSelect.list_items[0].code",
                    is("SolAppCreatedSolicitorDtls")))
            .andExpect(jsonPath("$.data.solsAmendLegalStatmentSelect.list_items[0].label",
                    is("Probate practitioner details")))
            .andExpect(jsonPath("$.data.solsAmendLegalStatmentSelect.list_items[1].code",
                    is("SolAppCreatedDeceasedDtls")))
            .andExpect(jsonPath("$.data.solsAmendLegalStatmentSelect.list_items[1].label",
                    is("Deceased details")))
            .andExpect(jsonPath("$.data.solsAmendLegalStatmentSelect.list_items[2].code",
                    is("NoWill")))
            .andExpect(jsonPath("$.data.solsAmendLegalStatmentSelect.list_items[2].label",
                is("Letters of administration details")))
            .andReturn();
    }

    @Test
    void shouldDefaultLegalStatementAmendOptionsForAdmonCase() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile("solicitorWillTypeAdmon.json");

        mockMvc.perform(post(DEFAULT_SOLS_NEXT_STEPS)
            .header(AUTH_HEADER, AUTH_TOKEN)
            .content(solicitorPayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.solsAmendLegalStatmentSelect.list_items[0].code",
                    is("SolAppCreatedSolicitorDtls")))
            .andExpect(jsonPath("$.data.solsAmendLegalStatmentSelect.list_items[0].label",
                    is("Probate practitioner details")))
            .andExpect(jsonPath("$.data.solsAmendLegalStatmentSelect.list_items[1].code",
                    is("SolAppCreatedDeceasedDtls")))
            .andExpect(jsonPath("$.data.solsAmendLegalStatmentSelect.list_items[1].label",
                    is("Deceased details")))
            .andExpect(jsonPath("$.data.solsAmendLegalStatmentSelect.list_items[2].code",
                    is("WillLeftAnnexed")))
            .andExpect(jsonPath("$.data.solsAmendLegalStatmentSelect.list_items[2].label",
                is("Letters of administration with will annexed details")))
            .andReturn();
    }

    @Test
    void shouldNotErrorOnSolCreateValidate() throws Exception {
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(SOLS_CREATE_VALIDATE_URL).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void solsCaseCreated_ShouldReturnDataPayload_OkResponseCode() throws Exception {
        String caseDetails = testUtils.getStringFromFile("caseDetailWithOrgPolicy.json");

        mockMvc.perform(post("/case/sols-created")
                .header(AUTH_HEADER, AUTH_TOKEN)
                .content(caseDetails)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(CoreMatchers.containsString("data")));
    }

    @Test
    void shouldOkValidateStopReason() throws Exception {
        List<CollectionMember<StopReason>> stopReasonList = Arrays.asList(
                new CollectionMember<>(null,
                        StopReason.builder()
                                .caseStopReason("Item")
                                .build()));
        LocalDateTime dod = LocalDateTime.parse("2021-07-01T00:00:00.000");
        caseDataBuilder.boCaseStopReasonList(stopReasonList);
        caseDataBuilder.applicationSubmittedDate(APPLICATION_SUBMITTED_DATE);
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(VALIDATE_STOP_REASON).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldErrorValidateStopReason() throws Exception {
        List<CollectionMember<StopReason>> stopReasonList = Arrays.asList(
                new CollectionMember<>(null,
                        StopReason.builder()
                                .caseStopReason("Other")
                                .build()));
        LocalDateTime dod = LocalDateTime.parse("2021-07-01T00:00:00.000");
        caseDataBuilder.boCaseStopReasonList(stopReasonList);
        caseDataBuilder.applicationSubmittedDate(APPLICATION_SUBMITTED_DATE);
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(VALIDATE_STOP_REASON).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors[0]")
                        .value("You cannot use stop reason **NOT TO BE USED (Other)**. "
                                + "You must select a specific stop reason from the case stop reason list"));

    }

    @Test
    void shouldNotSendEmailForErrorCase() throws Exception {
        String caseCreatorJson = testUtils.getStringFromFile("paperFormWithoutExecutorAddress.json");

        mockMvc.perform(post(PAPER_FORM_URL).header(AUTH_HEADER, AUTH_TOKEN)
                        .content(caseCreatorJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors[0]")
                        .value("The executor address line 1 cannot be empty"));
        verify(notificationService, never()).sendEmail(any(State.class), any(CaseDetails.class), any(Optional.class));
    }

    @Test
    void shouldTransformCaseDataForEvidenceHandledPACreateCaseOK() throws Exception {
        String caseDetails = testUtils.getStringFromFile("personalPayloadNotifications.json");

        mockMvc.perform(post(PA_CREATE_URL)
                        .header(AUTH_HEADER, AUTH_TOKEN)
                        .content(caseDetails)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(caseDataTransformer).transformCaseDataForEvidenceHandled(any(CallbackRequest.class));
        verify(caseDataTransformer).transformIhtFormCaseDataByDeceasedDOD(any(CallbackRequest.class));
    }

    @Test
    void shouldValidateFurtherEvidence() throws Exception {
        caseDataBuilder.furtherEvidenceForApplication(FURTHER_EVIDENCE);
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(SOLS_VALIDATE_FURTHER_EVIDENCE_URL).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReactivateCase() throws Exception {
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(REACTIVATE_CASE).header(AUTH_HEADER, AUTH_TOKEN)
                        .content(json).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void shouldDefaultRegistrarsDecision() throws Exception {
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(DEFAULT_REGISTRARS_DECISION).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRegistrarsDecision() throws Exception {
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(REGISTRARS_DECISION).header(AUTH_HEADER, AUTH_TOKEN)
                        .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldValidateUniqueCode() throws Exception {
        caseDataBuilder.uniqueProbateCodeId(uniqueCode);
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(UNIQUE_CODE).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldValidateValuesPage() throws Exception {
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(VALUES_PAGE).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldValidateRollback() throws Exception {
        SecurityDTO securityDTO = SecurityDTO.builder()
                .serviceAuthorisation("serviceToken")
                .authorisation("userToken")
                .userId("id")
                .build();
        when(securityUtils.getSecurityDTO()).thenReturn(securityDTO);
        when(auditEventService.getLatestAuditEventByName(any(), any(), any(), any()))
                .thenReturn(Optional.ofNullable(AuditEvent.builder()
                        .stateId("SolsAppUpdated")
                        .createdDate(LocalDateTime.now())
                        .build()));
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(ROLLBACK).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldOverrideDOB() throws Exception {
        caseDataBuilder.deceasedDateOfDeath(LocalDate.of(1900,1,1));
        caseDataBuilder.deceasedDob("1800-12-31");
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(CHANGE_DOB).header(AUTH_HEADER, AUTH_TOKEN)
                        .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(CoreMatchers.containsString("1800-12-31")));
    }

    @Test
    void shouldSetLastModifiedDate() throws Exception {
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(LAST_MODIFIED_DATE).header(AUTH_HEADER, AUTH_TOKEN)
                        .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldErrorOnInvalidEvent() throws Exception {
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(INVALID_EVENT).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(CoreMatchers.containsString(
                        "You must select the 'PA1P/PA1A/Solicitors Manual' event")));
    }

    @Test
    void shouldSetDormantDateTime() throws Exception {
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(SUPER_USER_MAKE_DORMANT).header(AUTH_HEADER, AUTH_TOKEN)
                        .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldThrowErrorToUseCaveatNotificationForNoInformationNeeded() throws Exception {

        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(CAVEAT_EVENT).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(CoreMatchers.containsString(
                        "you must use the 'Caveat notification' event")));
    }

    @Test
    void shouldThrowErrorToUseAssembleLetterForNoEmail() throws Exception {
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(ASSEMBLE_LETTER_EVENT).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(CoreMatchers.containsString(
                        "you must use the 'Assemble a letter' event to request information instead.")));
    }
}

