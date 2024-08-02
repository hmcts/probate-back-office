package uk.gov.hmcts.probate.transformer;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang3.BooleanUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ExecutorsApplyingNotification;
import uk.gov.hmcts.probate.model.caseaccess.Organisation;
import uk.gov.hmcts.probate.model.caseaccess.OrganisationPolicy;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.Reissue;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutor;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorApplying;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplying;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorTrustCorps;
import uk.gov.hmcts.probate.model.ccd.raw.AdoptedRelative;
import uk.gov.hmcts.probate.model.ccd.raw.AliasName;
import uk.gov.hmcts.probate.model.ccd.raw.AttorneyApplyingOnBehalfOf;
import uk.gov.hmcts.probate.model.ccd.raw.CodicilAddedDate;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicList;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicListItem;
import uk.gov.hmcts.probate.model.ccd.raw.EstateItem;
import uk.gov.hmcts.probate.model.ccd.raw.HandoffReason;
import uk.gov.hmcts.probate.model.ccd.raw.Payment;
import uk.gov.hmcts.probate.model.ccd.raw.ProbateAliasName;
import uk.gov.hmcts.probate.model.ccd.raw.RegistrarDirection;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.StopReason;
import uk.gov.hmcts.probate.model.ccd.raw.UploadDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.model.exceptionrecord.CaseCreationDetails;
import uk.gov.hmcts.probate.model.fee.FeeResponse;
import uk.gov.hmcts.probate.model.fee.FeesResponse;
import uk.gov.hmcts.probate.model.payments.pba.OrganisationEntityResponse;
import uk.gov.hmcts.probate.service.ExceptedEstateDateOfDeathChecker;
import uk.gov.hmcts.probate.service.ExecutorsApplyingNotificationService;
import uk.gov.hmcts.probate.service.StateChangeService;
import uk.gov.hmcts.probate.service.organisations.OrganisationsRetrievalService;
import uk.gov.hmcts.probate.service.solicitorexecutor.ExecutorListMapperService;
import uk.gov.hmcts.probate.service.tasklist.TaskListUpdateService;
import uk.gov.hmcts.probate.transformer.assembly.AssembleLetterTransformer;
import uk.gov.hmcts.probate.transformer.reset.ResetResponseCaseDataTransformer;
import uk.gov.hmcts.probate.transformer.solicitorexecutors.ExecutorsTransformer;
import uk.gov.hmcts.reform.probate.model.BulkScanEnvelope;
import uk.gov.hmcts.reform.probate.model.IhtFormType;
import uk.gov.hmcts.reform.probate.model.ProbateDocumentLink;
import uk.gov.hmcts.reform.probate.model.Relationship;
import uk.gov.hmcts.reform.probate.model.cases.Address;
import uk.gov.hmcts.reform.probate.model.cases.CombinedName;
import uk.gov.hmcts.reform.probate.model.cases.MaritalStatus;
import uk.gov.hmcts.reform.probate.model.cases.RegistryLocation;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.Damage;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantType;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.ApplicationType.PERSONAL;
import static uk.gov.hmcts.probate.model.ApplicationType.SOLICITOR;
import static uk.gov.hmcts.probate.model.Constants.CTSC;
import static uk.gov.hmcts.probate.model.DocumentType.ADMON_WILL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.ADMON_WILL_GRANT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.CAVEAT_STOPPED;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.EDGE_CASE;
import static uk.gov.hmcts.probate.model.DocumentType.INTESTACY_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.INTESTACY_GRANT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT_PROBATE;
import static uk.gov.hmcts.probate.model.DocumentType.OTHER;
import static uk.gov.hmcts.probate.model.DocumentType.SENT_EMAIL;
import static uk.gov.hmcts.probate.model.DocumentType.SOT_INFORMATION_REQUEST;
import static uk.gov.hmcts.probate.model.DocumentType.STATEMENT_OF_TRUTH;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_ADMON_WILL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_ADMON_WILL_GRANT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_DIGITAL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_DIGITAL_GRANT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_INTESTACY_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_INTESTACY_GRANT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_STATEMENT_OF_TRUTH;
import static uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantType.Constants.GRANT_OF_PROBATE_NAME;

@ExtendWith(SpringExtension.class)
class CallbackResponseTransformerTest {

    public static final String DECEASED_DEATH_CERTIFICATE = "deathCertificate";
    private static final String[] LAST_MODIFIED_STR = {"2018", "1", "2", "0", "0", "0", "0"};
    private static final String YES = "Yes";
    public static final String DECEASED_DIED_ENG_OR_WALES = YES;
    private static final String NO = "No";
    private static final String WILL_MESSAGE = "Will message";
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String CASE_TYPE_GRANT_OF_PROBATE = "gop";
    private static final String CASE_TYPE_INTESTACY = "intestacy";
    private static final String WILL_TYPE_PROBATE = "WillLeft";
    private static final String WILL_TYPE_INTESTACY = "NoWill";
    private static final String WILL_TYPE_ADMON = "WillLeftAnnexed";
    private static final String APPLICANT_SIBLINGS = "No";
    private static final String DIED_OR_NOT_APPLYING = "Yes";
    private static final String ENTITLED_MINORITY = "No";
    private static final String LIFE_INTEREST = "No";
    private static final String RESIDUARY = "Yes";
    private static final String RESIDUARY_TYPE = "Legatee";
    private static final String DOMICILITY_COUNTRY = "OtherCountry";

    private static final String APPLICATION_GROUNDS = "Application grounds";
    private static final ApplicationType APPLICATION_TYPE = SOLICITOR;
    private static final String REGISTRY_LOCATION = CTSC;
    private static final RegistryLocation BULK_SCAN_REGISTRY_LOCATION
        = CallbackResponseTransformer.EXCEPTION_RECORD_REGISTRY_LOCATION;
    private static final String GOR_EXCEPTION_RECORD_CASE_TYPE_ID =
        CallbackResponseTransformer.EXCEPTION_RECORD_CASE_TYPE_ID;
    private static final String GOR_EXCEPTION_RECORD_EVENT_ID = CallbackResponseTransformer.EXCEPTION_RECORD_EVENT_ID;
    private static final String SOLICITOR_FIRM_NAME = "Sol Firm Name";
    private static final String SOLICITOR_FIRM_LINE1 = "Sols Add Line 1";
    private static final String SOLICITOR_FIRM_POSTCODE = "SW13 6EA";
    private static final String SOLICITOR_FIRM_EMAIL = "solicitor@probate-test.com";
    private static final String SOLICITOR_FIRM_PHONE = "0123456789";
    private static final String SOLICITOR_SOT_FORENAME = "Andy Middlename";
    private static final String SOLICITOR_SOT_SURNAME = "Test";
    private static final String SOLICITOR_SOT_NAME = "Andy Middlename Test";
    private static final String SOLICITOR_SOT_JOB_TITLE = "Lawyer";
    private static final String SOLICITOR_SOT_NOT_APPLYING_REASON = "Power reserved";
    private static final String SOLICITOR_SOT_ID = "solicitor";
    private static final String DECEASED_FIRSTNAME = "Firstname";
    private static final String DECEASED_LASTNAME = "Lastname";
    private static final String DECEASED_DATE_OF_DEATH_TYPE = "diedOnOrSince";
    private static final LocalDate DOB = LocalDate.parse("2016-12-31", dateTimeFormatter);
    private static final LocalDate DOD = LocalDate.parse("2017-12-31", dateTimeFormatter);
    private static final LocalDate GRANT_DELAYED_DATE = LocalDate.parse("2019-12-31", dateTimeFormatter);
    private static final LocalDate GRANT_STOPPED_DATE = LocalDate.parse("2020-08-31", dateTimeFormatter);
    private static final LocalDate GRANT_AWAITING_DOCS_DATE = LocalDate.parse("2020-09-31", dateTimeFormatter);
    private static final String GRANT_DELAYED_NOTIFICATION_SENT = YES;
    private static final String NUM_CODICILS = "9";
    private static final String IHT_FORM_ID = "IHT205";
    private static final String IHT_FORM_ESTATE = "IHT400421";
    private static final BigDecimal IHT_GROSS = BigDecimal.valueOf(10000f);
    private static final BigDecimal IHT_NET = BigDecimal.valueOf(9000f);
    private static final BigDecimal IHT_ESTATE_GROSS = BigDecimal.valueOf(10000f);
    private static final String IHT_ESTATE_GROSS_FIELD = "10000";
    private static final BigDecimal IHT_ESTATE_NET = BigDecimal.valueOf(9000f);
    private static final String IHT_ESTATE_NET_FIELD = "9000";
    private static final BigDecimal IHT_ESTATE_NET_QUALIFYING = BigDecimal.valueOf(9000f);
    private static final String IHT_ESTATE_NET_QUALIFYING_FIELD = "9000";
    private static final String SOL_PAY_METHODS_FEE = "fee account";
    private static final String SOL_PAY_METHODS_CHEQUE = "cheque";
    private static final String FEE_ACCT_NUMBER = "1234";

    private static final FeeResponse feeForNonUkCopies = FeeResponse.builder().feeAmount(new BigDecimal(11)).build();
    private static final FeeResponse feeForUkCopies = FeeResponse.builder().feeAmount(new BigDecimal(22)).build();
    private static final FeeResponse applicationFee = FeeResponse.builder().feeAmount(new BigDecimal(33)).build();
    private static final BigDecimal totalFee = new BigDecimal(66);
    private static final String DOC_BINARY_URL = "docBinaryUrl";
    private static final String DOC_URL = "docUrl";
    private static final String DOC_NAME = "docName";
    private static final String APPLICANT_FORENAME = "applicant forename";
    private static final String APPLICANT_SURNAME = "applicant surname";
    private static final String APPLICANT_EMAIL_ADDRESS = "primary@probate-test.com";
    private static final String PRIMARY_EXEC_APPLYING = YES;
    private static final String APPLICANT_HAS_ALIAS = YES;
    private static final String OTHER_EXECS_EXIST = NO;
    private static final String PRIMARY_EXEC_ALIAS_NAMES = "Alias names";
    private static final List<CollectionMember<AdditionalExecutor>> ADDITIONAL_EXEC_LIST = emptyList();
    private static final List<CollectionMember<AdditionalExecutorApplying>> ADDITIONAL_EXEC_LIST_APP = emptyList();
    private static final List<CollectionMember<AdditionalExecutorNotApplying>> ADDITIONAL_EXEC_LIST_NOT_APP =
        emptyList();
    private static final List<uk.gov.hmcts.reform.probate.model.cases.CollectionMember
        <uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.ExecutorApplying>>
        BSP_ADDITIONAL_EXEC_LIST_APP = emptyList();
    private static final List<uk.gov.hmcts.reform.probate.model.cases.CollectionMember
        <uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.ExecutorNotApplying>>
        BSP_ADDITIONAL_EXEC_LIST_NOT_APP = emptyList();
    private static final List<CollectionMember<AliasName>> DECEASED_ALIAS_NAMES_LIST = emptyList();
    private static final SolsAddress DECEASED_ADDRESS = mock(SolsAddress.class);
    private static final SolsAddress EXEC_ADDRESS = mock(SolsAddress.class);
    private static final Address BSP_APPLICANT_ADDRESS = mock(Address.class);
    private static final Address BSP_DECEASED_ADDRESS = mock(Address.class);
    private static final List<CollectionMember<AliasName>> ALIAS_NAMES = emptyList();
    private static final String APP_REF = "app ref";
    private static final String ADDITIONAL_INFO = "additional info";
    private static final String IHT_REFERENCE = "123456789abcde";
    private static final String IHT_ONLINE = "Yes";
    private static final String EXEC_FIRST_NAME = "ExFName";
    private static final String EXEC_NAME = "ExName";
    private static final String EXEC_NAME_DIFF = "Ex name difference comment";
    private static final String EXEC_WILL_NAME = "Ex will name";
    private static final String EXEC_SURNAME = "EXSName";
    private static final String EXEC_OTHER_NAMES = EXEC_WILL_NAME;
    private static final String EXEC_PHONE = "010101010101";
    private static final String EXEC_EMAIL = "executor1@probate-test.com";
    private static final String EXEC_NOTIFIED = YES;
    private static final String BO_BULK_PRINT = YES;
    private static final String BO_EMAIL_GRANT_ISSUED = YES;
    private static final String BO_DOCS_RECEIVED = YES;
    private static final String CASE_PRINT = YES;
    private static final String CAVEAT_STOP_NOTIFICATION = YES;
    private static final String CASE_STOP_CAVEAT_ID = "1234567812345678";
    private static final String CAVEAT_STOP_EMAIL_NOTIFICATION = YES;
    private static final String CAVEAT_STOP_SEND_TO_BULK_PRINT = YES;
    private static final List<CollectionMember<StopReason>> STOP_REASONS_LIST = emptyList();
    private static final String STOP_REASON = "Some reason";
    private static final String ALIAS_FORENAME = "AliasFN";
    private static final String ALIAS_SURNAME = "AliasSN";
    private static final String SOLS_ALIAS_NAME = "AliasFN AliasSN";
    private static final String STOP_DETAILS = "";
    private static final Optional<String> ORIGINAL_STATE = Optional.empty();
    private static final Optional<String> CHANGED_STATE = Optional.of("Changed");
    private static final String DECEASED_TITLE = "Deceased Title";
    private static final String DECEASED_HONOURS = "Deceased Honours";
    private static final String LIMITATION_TEXT = "Limitation Text";
    private static final String EXECUTOR_LIMITATION = "Executor Limitation";
    private static final String ADMIN_CLAUSE_LIMITATION = "Admin Clause Limitation";
    private static final String TOTAL_FEE = "6600";
    private static final String RECORD_ID = "12345";
    private static final String LEGACY_CASE_URL = "someUrl";
    private static final String LEGACY_CASE_TYPE = "someCaseType";
    private static final String ORDER_NEEDED = YES;
    private static final List<CollectionMember<Reissue>> REISSUE_REASON = emptyList();
    private static final String REISSUE_DATE = "2019-01-01";
    private static final String REISSUE_NOTATION = "duplicate";
    private static final String DECEASED_DIVORCED_IN_ENGLAND_OR_WALES = YES;
    private static final String PRIMARY_APPLICANT_ADOPTION_IN_ENGLAND_OR_WALES = NO;
    private static final String PRIMARY_APPLICANT_RELATIONSHIP_TO_DECEASED = "partner";
    private static final String DECEASED_SPOUSE_NOT_APPLYING_REASON = "notApplyingReason";
    private static final String DECEASED_OTHER_CHILDREN = YES;
    private static final String ALL_DECEASED_CHILDREN_OVER_EIGHTEEN = YES;
    private static final String ANY_DECEASED_CHILDREN_DIE_BEFORE_DECEASED = NO;
    private static final String ANY_DECEASED_GRANDCHILDREN_UNDER_EIGHTEEN = YES;
    private static final String DECEASED_ANY_CHILDREN = NO;
    private static final String DECEASED_HAS_ASSETS_OUTSIDE_UK = YES;
    private static final DocumentLink SOT = DocumentLink.builder().documentFilename("SOT.pdf").build();
    public static final String QA_CASE_STATE = "BOCaseQA";
    private static final String CASE_PRINTED = "CasePrinted";
    private static final String READY_FOR_ISSUE = "BOReadyToIssue";
    private static final String CASE_MATCHING_ISSUE_GRANT = "BOCaseMatchingIssueGrant";
    private static final String BULK_SCAN_REFERENCE = "BulkScanRef";
    private static final LocalDate VALID_CODICIL_DATE = LocalDate.now().minusDays(1);
    private static final LocalDate VALID_ORIGINAL_WILL_SIGNED_DATE = LocalDate.now().minusDays(1);
    private static final List<CollectionMember<CodicilAddedDate>> VALID_ADDED_CODICIL_DATES =
        Arrays.asList(new CollectionMember<>(CodicilAddedDate.builder().dateCodicilAdded(VALID_CODICIL_DATE).build()));
    private static final String NO_ACCESS_WILL_REASON = "I lost it";
    private static final List<uk.gov.hmcts.reform.probate.model.cases.CollectionMember<BulkScanEnvelope>>
        BULK_SCAN_ENVELOPES = new ArrayList<>();

    private static final Document SOT_DOC = Document.builder().documentType(STATEMENT_OF_TRUTH).build();
    private static final Document SENTEMAIL = Document.builder()
            .documentType(SENT_EMAIL)
            .documentFileName(SENT_EMAIL.getTemplateName())
            .build();
    private static final String ORGANISATION_NAME = "OrganisationName";
    private static final String ORG_ID = "OrgID";
    private static final String NOT_APPLICABLE = "NotApplicable";
    private static final String USER_ID = "User-ID";
    private static final String uniqueCode = "CTS 0405231104 3tpp s8e9";
    @Mock
    private ExceptedEstateDateOfDeathChecker exceptedEstateDateOfDeathChecker;

    private static final List<CollectionMember<EstateItem>> UK_ESTATE = Arrays.asList(
        new CollectionMember<>(null,
            EstateItem.builder()
                .item("Item")
                .value("999.99")
                .build()));

    private static final LocalDateTime scannedDate = LocalDateTime.parse("2018-01-01T12:34:56.123");
    private static final List<CollectionMember<Payment>> PAYMENTS_LIST = Arrays.asList(
        new CollectionMember<Payment>("id",
            Payment.builder()
                .amount("100")
                .date("20/09/2018")
                .method("online")
                .reference("Reference-123")
                .status("Success")
                .siteId("SiteId-123")
                .transactionId("TransactionId-123")
                .build()));

    private static final DocumentLink SCANNED_DOCUMENT_URL = DocumentLink.builder()
        .documentBinaryUrl("http://somedoc")
        .documentFilename("somedoc.pdf")
        .documentUrl("http://somedoc/location")
        .build();

    private static final ProbateDocumentLink BSP_SCANNED_DOCUMENT_URL = ProbateDocumentLink.builder()
        .documentBinaryUrl("http://somedoc")
        .documentFilename("somedoc.pdf")
        .documentUrl("http://somedoc/location")
        .build();

    private static final List<CollectionMember<ScannedDocument>> SCANNED_DOCUMENTS_LIST = Arrays.asList(
        new CollectionMember<ScannedDocument>("id",
            ScannedDocument.builder()
                .fileName("scanneddocument.pdf")
                .controlNumber("1234")
                .scannedDate(scannedDate)
                .type("other")
                .subtype("will")
                .url(SCANNED_DOCUMENT_URL)
                .build()));

    private static final List<uk.gov.hmcts.reform.probate.model.cases.CollectionMember
        <uk.gov.hmcts.reform.probate.model.ScannedDocument>> BSP_SCANNED_DOCUMENTS_LIST = Arrays.asList(
            new uk.gov.hmcts.reform.probate.model.cases
                .CollectionMember<uk.gov.hmcts.reform.probate.model.ScannedDocument>(
                "id",
                uk.gov.hmcts.reform.probate.model.ScannedDocument.builder()
                    .fileName("scanneddocument.pdf")
                    .controlNumber("1234")
                    .scannedDate(scannedDate)
                    .type("other")
                    .subtype("will")
                    .url(BSP_SCANNED_DOCUMENT_URL)
                    .build()));

    private static final List<CollectionMember<ExecutorsApplyingNotification>> EXECEUTORS_APPLYING_NOTIFICATION =
        Arrays.asList(
            new CollectionMember<ExecutorsApplyingNotification>("id",
                ExecutorsApplyingNotification.builder()
                    .name(EXEC_FIRST_NAME)
                    .address(EXEC_ADDRESS)
                    .email(EXEC_EMAIL)
                    .notification(YES)
                    .build()));

    private static final AdditionalExecutorApplying ADDITIONAL_EXECUTOR_APPLYING = AdditionalExecutorApplying.builder()
        .applyingExecutorName(SOLICITOR_SOT_NAME)
        .build();

    private static final AdditionalExecutorApplying ADDITIONAL_EXECUTOR_APPLYING_SECOND =
        AdditionalExecutorApplying.builder().applyingExecutorName("James Smith").build();

    private static final AdditionalExecutorNotApplying ADDITIONAL_EXECUTOR_NOT_APPLYING =
        AdditionalExecutorNotApplying.builder()
            .notApplyingExecutorName(SOLICITOR_SOT_NAME)
            .notApplyingExecutorReason(SOLICITOR_SOT_NOT_APPLYING_REASON)
            .build();

    private static final AdditionalExecutor SOL_ADDITIONAL_EXECUTOR_APPLYING = AdditionalExecutor.builder()
        .additionalExecForenames(SOLICITOR_SOT_FORENAME)
        .additionalExecLastname(SOLICITOR_SOT_SURNAME)
        .additionalExecNameOnWill(NO)
        .additionalApplying(YES)
        .additionalExecAddress(SolsAddress.builder().addressLine1(SOLICITOR_FIRM_LINE1)
            .postCode(SOLICITOR_FIRM_POSTCODE).build())
        .build();

    private static final AdditionalExecutor SOL_ADDITIONAL_EXECUTOR_NOT_APPLYING = AdditionalExecutor.builder()
        .additionalExecForenames(SOLICITOR_SOT_FORENAME)
        .additionalExecLastname(SOLICITOR_SOT_SURNAME)
        .additionalExecNameOnWill(NO)
        .additionalApplying(NO)
        .additionalExecReasonNotApplying(SOLICITOR_SOT_NOT_APPLYING_REASON)
        .build();
    public static final String DAMAGE_TYPE_1 = "Type1";
    public static final String DAMAGE_TYPE_2 = "Type2";
    public static final String DAMAGE_TYPE_OTHER = "Other";
    public static final String DAMAGE_DESC = "Damage Desc";
    public static final String DAMAGE_REASON_DESC = "Damage reason";
    public static final String DAMAGE_CULPRIT_FN = "Damage Culprit FN";
    public static final String DAMAGE_CULPRIT_LN = "Damage Culprit LN";
    public static final String DAMAGE_DATE = "9/2021";
    private static final String SERVICE_REQUEST_REFEREMCE = "Service Request Ref";

    @InjectMocks
    private CallbackResponseTransformer underTest;

    @Mock
    private StateChangeService stateChangeServiceMock;

    @Mock
    private ExecutorListMapperService executorListMapperService;

    @Mock
    private TaskListUpdateService taskListUpdateService;

    @Mock
    private CallbackRequest callbackRequestMock;

    @Mock
    private ExecutorsApplyingNotificationService executorsApplyingNotificationService;

    @Mock
    private CaseDetails caseDetailsMock;

    @Mock
    private CaseData caseDataMock;

    @Mock
    private List<CollectionMember<AdditionalExecutorApplying>> additionalExecutorsApplyingMock;

    @Mock
    private List<CollectionMember<AdditionalExecutorNotApplying>> additionalExecutorsNotApplyingMock;

    @Mock
    private List<CollectionMember<AdditionalExecutor>> solAdditionalExecutorsApplyingMock;

    @Mock
    private List<CollectionMember<AdditionalExecutor>> solAdditionalExecutorsNotApplyingMock;

    @Mock
    private AssembleLetterTransformer assembleLetterTransformer;

    private CaseData.CaseDataBuilder caseDataBuilder;

    private GrantOfRepresentationData bulkScanGrantOfRepresentationData;

    private GrantOfRepresentationData bulkScanGrantOfRepresentationDataSols;

    @Mock
    private FeesResponse feesResponse;

    @Mock
    private DocumentLink documentLinkMock;

    @Mock
    private DocumentLink legalStatementUploadMock;

    @Spy
    private DocumentTransformer documentTransformer;

    @Mock
    private ReprintTransformer reprintTransformer;

    @Mock
    private SolicitorLegalStatementNextStepsTransformer solicitorLegalStatementNextStepsTransformer;
    @Mock
    private SolicitorPaymentReferenceDefaulter solicitorPBADefaulter;
    @Mock
    private OrganisationsRetrievalService organisationsRetrievalService;

    @Mock
    private ResetResponseCaseDataTransformer resetResponseCaseDataTransformer;

    @Mock
    private ExecutorsTransformer solicitorExecutorTransformerMock;

    @Mock
    private CaseDataTransformer caseDataTransformerMock;
    @Mock
    private IhtEstateDefaulter ihtEstateDefaulter;
    @Mock
    private Iht400421Defaulter iht400421Defaulter;
    @Mock
    Document coversheetMock;

    @BeforeEach
    public void setup() {

        caseDataBuilder = CaseData.builder()
            .applicationType(APPLICATION_TYPE)
            .solsSolicitorFirmName(SOLICITOR_FIRM_NAME)
            .solsSolicitorAddress(SolsAddress.builder().addressLine1(SOLICITOR_FIRM_LINE1)
                .postCode(SOLICITOR_FIRM_POSTCODE).build())
            .solsSolicitorEmail(SOLICITOR_FIRM_EMAIL)
            .solsSolicitorPhoneNumber(SOLICITOR_FIRM_PHONE)
            .solsSOTForenames(SOLICITOR_SOT_FORENAME)
            .solsSOTSurname(SOLICITOR_SOT_SURNAME)
            .solsSOTName(SOLICITOR_SOT_NAME)
            .solsSOTJobTitle(SOLICITOR_SOT_JOB_TITLE)
            .deceasedForenames(DECEASED_FIRSTNAME)
            .deceasedSurname(DECEASED_LASTNAME)
            .deceasedDateOfBirth(DOB)
            .deceasedDateOfDeath(DOD)
            .willHasCodicils(YES)
            .willNumberOfCodicils(NUM_CODICILS)
            .originalWillSignedDate(VALID_ORIGINAL_WILL_SIGNED_DATE)
            .codicilAddedDateList(VALID_ADDED_CODICIL_DATES)
            .ihtFormId(IHT_FORM_ID)
            .ihtGrossValue(IHT_GROSS)
            .ihtNetValue(IHT_NET)
            .ihtFormEstateValuesCompleted(YES)
            .ihtFormEstate(IHT_FORM_ESTATE)
            .ihtEstateGrossValue(IHT_ESTATE_GROSS)
            .ihtEstateGrossValueField(IHT_ESTATE_GROSS_FIELD)
            .ihtEstateNetValue(IHT_ESTATE_NET)
            .ihtEstateNetValueField(IHT_ESTATE_NET_FIELD)
            .ihtEstateNetQualifyingValue(IHT_ESTATE_NET_QUALIFYING)
            .ihtEstateNetQualifyingValueField(IHT_ESTATE_NET_QUALIFYING_FIELD)
            .deceasedHadLateSpouseOrCivilPartner(YES)
            .ihtUnusedAllowanceClaimed(YES)
            .primaryApplicantForenames(APPLICANT_FORENAME)
            .primaryApplicantSurname(APPLICANT_SURNAME)
            .primaryApplicantEmailAddress(APPLICANT_EMAIL_ADDRESS)
            .primaryApplicantIsApplying(PRIMARY_EXEC_APPLYING)
            .primaryApplicantHasAlias(APPLICANT_HAS_ALIAS)
            .otherExecutorExists(OTHER_EXECS_EXIST)
            .primaryApplicantAlias(PRIMARY_EXEC_ALIAS_NAMES)
            .solsExecutorAliasNames(PRIMARY_EXEC_ALIAS_NAMES)
            .solsAdditionalExecutorList(ADDITIONAL_EXEC_LIST)
            .deceasedAddress(DECEASED_ADDRESS)
            .deceasedAnyOtherNames(YES)
            .caseType("gop")
            .solsDeceasedAliasNamesList(DECEASED_ALIAS_NAMES_LIST)
            .primaryApplicantAddress(EXEC_ADDRESS)
            .solsDeceasedAliasNamesList(ALIAS_NAMES)
            .solsSolicitorAppReference(APP_REF)
            .solsAdditionalInfo(ADDITIONAL_INFO)
            .boEmailGrantIssuedNotification(BO_EMAIL_GRANT_ISSUED)
            .boEmailGrantIssuedNotificationRequested(BO_EMAIL_GRANT_ISSUED)
            .boEmailGrantReissuedNotification(YES)
            .boEmailDocsReceivedNotification(BO_DOCS_RECEIVED)
            .boEmailDocsReceivedNotificationRequested(BO_DOCS_RECEIVED)
            .boSendToBulkPrintRequested(BO_BULK_PRINT)
            .casePrinted(CASE_PRINT)
            .boCaveatStopEmailNotification(CAVEAT_STOP_NOTIFICATION)
            .boCaveatStopNotificationRequested(CAVEAT_STOP_NOTIFICATION)
            .boCaveatStopNotification(CAVEAT_STOP_NOTIFICATION)
            .boCaseStopCaveatId(CASE_STOP_CAVEAT_ID)
            .solsSolicitorIsExec(YES)
            .solsSolicitorIsApplying(NO)
            .titleAndClearingType("TCTTrustCorpResWithApp")
            .boCaveatStopEmailNotificationRequested(CAVEAT_STOP_EMAIL_NOTIFICATION)
            .boCaveatStopSendToBulkPrintRequested(CAVEAT_STOP_SEND_TO_BULK_PRINT)
            .boCaseStopReasonList(STOP_REASONS_LIST)
            .boStopDetails(STOP_DETAILS)
            .applicationGrounds(APPLICATION_GROUNDS)
            .willDispose(YES)
            .englishWill(NO)
            .appointExec(YES)
            .solsWillType(WILL_TYPE_PROBATE)
            .solsApplicantSiblings(APPLICANT_SIBLINGS)
            .solsDiedOrNotApplying(DIED_OR_NOT_APPLYING)
            .solsEntitledMinority(ENTITLED_MINORITY)
            .solsLifeInterest(LIFE_INTEREST)
            .solsResiduary(RESIDUARY)
            .solsResiduaryType(RESIDUARY_TYPE)
            .willExists(YES)
            .additionalExecutorsApplying(ADDITIONAL_EXEC_LIST_APP)
            .additionalExecutorsNotApplying(ADDITIONAL_EXEC_LIST_NOT_APP)
            .boDeceasedTitle(DECEASED_TITLE)
            .boDeceasedHonours(DECEASED_HONOURS)
            .boWillMessage(WILL_MESSAGE)
            .boExecutorLimitation(EXECUTOR_LIMITATION)
            .boAdminClauseLimitation(ADMIN_CLAUSE_LIMITATION)
            .boLimitationText(LIMITATION_TEXT)
            .ihtReferenceNumber(IHT_REFERENCE)
            .ihtFormCompletedOnline(IHT_ONLINE)
            .payments(PAYMENTS_LIST)
            .boExaminationChecklistQ1(YES)
            .boExaminationChecklistQ2(YES)
            .boExaminationChecklistRequestQA(YES)
            .scannedDocuments(SCANNED_DOCUMENTS_LIST)
            .recordId(RECORD_ID)
            .legacyType(LEGACY_CASE_TYPE)
            .orderNeeded(ORDER_NEEDED)
            .reissueReason(REISSUE_REASON)
            .reissueDate(REISSUE_DATE)
            .reissueReasonNotation(REISSUE_NOTATION)
            .legacyCaseViewUrl(LEGACY_CASE_URL)
            .deceasedDivorcedInEnglandOrWales(DECEASED_DIVORCED_IN_ENGLAND_OR_WALES)
            .primaryApplicantAdoptionInEnglandOrWales(PRIMARY_APPLICANT_ADOPTION_IN_ENGLAND_OR_WALES)
            .deceasedSpouseNotApplyingReason(DECEASED_SPOUSE_NOT_APPLYING_REASON)
            .deceasedOtherChildren(DECEASED_OTHER_CHILDREN)
            .allDeceasedChildrenOverEighteen(ALL_DECEASED_CHILDREN_OVER_EIGHTEEN)
            .anyDeceasedChildrenDieBeforeDeceased(ANY_DECEASED_CHILDREN_DIE_BEFORE_DECEASED)
            .anyDeceasedGrandChildrenUnderEighteen(ANY_DECEASED_GRANDCHILDREN_UNDER_EIGHTEEN)
            .deceasedAnyChildren(DECEASED_ANY_CHILDREN)
            .deceasedHasAssetsOutsideUK(DECEASED_HAS_ASSETS_OUTSIDE_UK)
            .statementOfTruthDocument(SOT)
            .boStopDetailsDeclarationParagraph(YES)
            .boEmailRequestInfoNotification(YES)
            .boEmailRequestInfoNotificationRequested(YES)
            .boAssembleLetterSendToBulkPrintRequested(YES)
            .boRequestInfoSendToBulkPrintRequested(YES)
            .executorsApplyingNotifications(EXECEUTORS_APPLYING_NOTIFICATION)
            .grantDelayedNotificationDate(GRANT_DELAYED_DATE)
            .grantStoppedDate(GRANT_STOPPED_DATE)
            .grantDelayedNotificationSent(YES)
            .grantAwaitingDocumentatioNotificationSent(YES)
            .grantAwaitingDocumentationNotificationDate(GRANT_AWAITING_DOCS_DATE)
            .pcqId(APP_REF)
            .deceasedDiedEngOrWales(DECEASED_DIED_ENG_OR_WALES)
            .deceasedDeathCertificate(DECEASED_DEATH_CERTIFICATE)
            .willHasVisibleDamage(YES)
            .willDamage(Damage.builder()
                .damageTypesList(Arrays.asList(
                    DAMAGE_TYPE_1, DAMAGE_TYPE_2, DAMAGE_TYPE_OTHER))
                .otherDamageDescription(DAMAGE_DESC)
                .build())
            .willDamageReasonKnown(YES)
            .willDamageReasonDescription(DAMAGE_REASON_DESC)
            .willDamageCulpritKnown(YES)
            .willDamageCulpritName(CombinedName.builder()
                .firstName(DAMAGE_CULPRIT_FN)
                .lastName(DAMAGE_CULPRIT_LN)
                .build())
            .willDamageDateKnown(YES)
            .willDamageDate(DAMAGE_DATE)
            .codicilsHasVisibleDamage(YES)
            .codicilsDamage(Damage.builder()
                .damageTypesList(Arrays.asList(DAMAGE_TYPE_1, DAMAGE_TYPE_2, DAMAGE_TYPE_OTHER))
                .otherDamageDescription(DAMAGE_DESC)
                .build())
            .codicilsDamageReasonKnown(YES)
            .codicilsDamageReasonDescription(DAMAGE_REASON_DESC)
            .codicilsDamageCulpritKnown(YES)
            .codicilsDamageCulpritName(CombinedName.builder()
                .firstName(DAMAGE_CULPRIT_FN)
                .lastName(DAMAGE_CULPRIT_LN)
                .build())
            .codicilsDamageDateKnown(YES)
            .codicilsDamageDate(DAMAGE_DATE)
            .deceasedWrittenWishes(YES)
            .documentsReceivedNotificationSent(YES);
        ;

        bulkScanGrantOfRepresentationData = GrantOfRepresentationData.builder()
            .deceasedForenames(DECEASED_FIRSTNAME)
            .deceasedSurname(DECEASED_LASTNAME)
            .deceasedDateOfBirth(DOB)
            .deceasedDateOfDeath(DOD)
            .willHasCodicils(TRUE)
            .willNumberOfCodicils(Long.valueOf(NUM_CODICILS))
            .ihtFormId(IhtFormType.optionIHT205)
            .ihtGrossValue(IHT_GROSS.longValue())
            .ihtNetValue(IHT_NET.longValue())
            .primaryApplicantForenames(APPLICANT_FORENAME)
            .primaryApplicantSurname(APPLICANT_SURNAME)
            .primaryApplicantEmailAddress(APPLICANT_EMAIL_ADDRESS)
            .primaryApplicantIsApplying(TRUE)
            .primaryApplicantHasAlias(TRUE)
            .primaryApplicantAlias(PRIMARY_EXEC_ALIAS_NAMES)
            .deceasedAddress(BSP_DECEASED_ADDRESS)
            .deceasedAnyOtherNames(TRUE)
            .primaryApplicantAddress(BSP_APPLICANT_ADDRESS)
            .boSendToBulkPrintRequested(TRUE)
            .grantType(GrantType.GRANT_OF_PROBATE)
            .willExists(TRUE)
            .executorsApplying(BSP_ADDITIONAL_EXEC_LIST_APP)
            .executorsNotApplying(BSP_ADDITIONAL_EXEC_LIST_NOT_APP)
            .ihtReferenceNumber(IHT_REFERENCE)
            .ihtFormCompletedOnline(TRUE)
            .scannedDocuments(BSP_SCANNED_DOCUMENTS_LIST)
            .deceasedDivorcedInEnglandOrWales(TRUE)
            .primaryApplicantAdoptionInEnglandOrWales(Boolean.FALSE)
            .deceasedOtherChildren(TRUE)
            .deceasedHasAssetsOutsideUK(TRUE)
            .boEmailRequestInfoNotificationRequested(Boolean.FALSE)
            .boSendToBulkPrintRequested(Boolean.FALSE)
            .primaryApplicantSecondPhoneNumber(EXEC_PHONE)
            .primaryApplicantRelationshipToDeceased(Relationship.OTHER)
            .paRelationshipToDeceasedOther("cousin")
            .deceasedMaritalStatus(MaritalStatus.NEVER_MARRIED)
            .dateOfMarriageOrCP(null)
            .dateOfDivorcedCPJudicially(null)
            .willsOutsideOfUK(TRUE)
            .courtOfDecree("Random Court Name")
            .willGiftUnderEighteen(Boolean.FALSE)
            .applyingAsAnAttorney(TRUE)
            .attorneyOnBehalfOfNameAndAddress(null)
            .mentalCapacity(TRUE)
            .courtOfProtection(TRUE)
            .epaOrLpa(Boolean.FALSE)
            .epaRegistered(Boolean.FALSE)
            .domicilityCountry("Spain")
            .adopted(TRUE)
            .adoptiveRelatives(null)
            .domicilityIHTCert(TRUE)
            .foreignAsset(TRUE)
            .foreignAssetEstateValue(Long.valueOf("123"))
            .grantType(GrantType.INTESTACY)
            .childrenSurvived(TRUE)
            .childrenOverEighteenSurvivedText(NUM_CODICILS)
            .childrenUnderEighteenSurvivedText(NUM_CODICILS)
            .childrenDied(TRUE)
            .childrenDiedOverEighteenText(NUM_CODICILS)
            .childrenDiedUnderEighteenText(NUM_CODICILS)
            .grandChildrenSurvived(TRUE)
            .grandChildrenSurvivedOverEighteenText(NUM_CODICILS)
            .grandChildrenSurvivedUnderEighteenText(NUM_CODICILS)
            .parentsExistSurvived(TRUE)
            .parentsExistOverEighteenSurvived(NUM_CODICILS)
            .parentsExistUnderEighteenSurvived(NUM_CODICILS)
            .wholeBloodSiblingsSurvived(TRUE)
            .wholeBloodSiblingsSurvivedOverEighteen(NUM_CODICILS)
            .wholeBloodSiblingsSurvivedUnderEighteen(NUM_CODICILS)
            .wholeBloodSiblingsDied(TRUE)
            .wholeBloodSiblingsDiedOverEighteen(NUM_CODICILS)
            .wholeBloodSiblingsDiedUnderEighteen(NUM_CODICILS)
            .wholeBloodNeicesAndNephews(TRUE)
            .wholeBloodNeicesAndNephewsOverEighteen(NUM_CODICILS)
            .wholeBloodNeicesAndNephewsUnderEighteen(NUM_CODICILS)
            .halfBloodSiblingsSurvived(TRUE)
            .halfBloodSiblingsSurvivedOverEighteen(NUM_CODICILS)
            .halfBloodSiblingsSurvivedUnderEighteen(NUM_CODICILS)
            .halfBloodSiblingsDied(TRUE)
            .halfBloodSiblingsDiedOverEighteen(NUM_CODICILS)
            .halfBloodSiblingsDiedUnderEighteen(NUM_CODICILS)
            .halfBloodNeicesAndNephews(TRUE)
            .halfBloodNeicesAndNephewsOverEighteen(NUM_CODICILS)
            .halfBloodNeicesAndNephewsUnderEighteen(NUM_CODICILS)
            .grandparentsDied(TRUE)
            .grandparentsDiedOverEighteen(NUM_CODICILS)
            .grandparentsDiedUnderEighteen(NUM_CODICILS)
            .wholeBloodUnclesAndAuntsSurvived(TRUE)
            .wholeBloodUnclesAndAuntsSurvivedOverEighteen(NUM_CODICILS)
            .wholeBloodUnclesAndAuntsSurvivedUnderEighteen(NUM_CODICILS)
            .wholeBloodUnclesAndAuntsDied(TRUE)
            .wholeBloodUnclesAndAuntsDiedOverEighteen(NUM_CODICILS)
            .wholeBloodUnclesAndAuntsDiedUnderEighteen(NUM_CODICILS)
            .wholeBloodCousinsSurvived(TRUE)
            .wholeBloodCousinsSurvivedOverEighteen(NUM_CODICILS)
            .wholeBloodCousinsSurvivedUnderEighteen(NUM_CODICILS)
            .halfBloodUnclesAndAuntsSurvived(TRUE)
            .halfBloodUnclesAndAuntsSurvivedOverEighteen(NUM_CODICILS)
            .halfBloodUnclesAndAuntsSurvivedUnderEighteen(NUM_CODICILS)
            .halfBloodUnclesAndAuntsDied(TRUE)
            .halfBloodUnclesAndAuntsDiedOverEighteen(NUM_CODICILS)
            .halfBloodUnclesAndAuntsDiedUnderEighteen(NUM_CODICILS)
            .halfBloodCousinsSurvived(TRUE)
            .halfBloodCousinsSurvivedOverEighteen(NUM_CODICILS)
            .halfBloodCousinsSurvivedUnderEighteen(NUM_CODICILS)
            .applicationFeePaperForm(Long.valueOf("0"))
            .feeForCopiesPaperForm(Long.valueOf("0"))
            .totalFeePaperForm(Long.valueOf("0"))
            .paperPaymentMethod("debitOrCredit")
            .paymentReferenceNumberPaperform(IHT_REFERENCE)
            .paperForm(TRUE)
            .bulkScanCaseReference(BULK_SCAN_REFERENCE)
            .grantDelayedNotificationDate(GRANT_DELAYED_DATE)
            .grantStoppedDate(GRANT_STOPPED_DATE)
            .grantDelayedNotificationSent(TRUE)
            .grantAwaitingDocumentationNotificationDate(GRANT_AWAITING_DOCS_DATE)
            .grantAwaitingDocumentatioNotificationSent(TRUE)
            .bulkScanEnvelopes(BULK_SCAN_ENVELOPES)
            .build();

        bulkScanGrantOfRepresentationDataSols = GrantOfRepresentationData.builder()
            .deceasedForenames(DECEASED_FIRSTNAME)
            .deceasedSurname(DECEASED_LASTNAME)
            .deceasedDateOfBirth(DOB)
            .deceasedDateOfDeath(DOD)
            .willHasCodicils(TRUE)
            .willNumberOfCodicils(Long.valueOf(NUM_CODICILS))
            .ihtFormId(IhtFormType.optionIHT205)
            .ihtGrossValue(IHT_GROSS.longValue())
            .ihtNetValue(IHT_NET.longValue())
            .primaryApplicantForenames(APPLICANT_FORENAME)
            .primaryApplicantSurname(APPLICANT_SURNAME)
            .primaryApplicantEmailAddress(APPLICANT_EMAIL_ADDRESS)
            .primaryApplicantIsApplying(TRUE)
            .primaryApplicantHasAlias(TRUE)
            .primaryApplicantAlias(PRIMARY_EXEC_ALIAS_NAMES)
            .deceasedAddress(BSP_DECEASED_ADDRESS)
            .deceasedAnyOtherNames(TRUE)
            .primaryApplicantAddress(BSP_APPLICANT_ADDRESS)
            .boSendToBulkPrintRequested(TRUE)
            .grantType(GrantType.GRANT_OF_PROBATE)
            .willExists(TRUE)
            .executorsApplying(BSP_ADDITIONAL_EXEC_LIST_APP)
            .executorsNotApplying(BSP_ADDITIONAL_EXEC_LIST_NOT_APP)
            .ihtReferenceNumber(IHT_REFERENCE)
            .ihtFormCompletedOnline(TRUE)
            .scannedDocuments(BSP_SCANNED_DOCUMENTS_LIST)
            .deceasedDivorcedInEnglandOrWales(TRUE)
            .primaryApplicantAdoptionInEnglandOrWales(Boolean.FALSE)
            .deceasedOtherChildren(TRUE)
            .deceasedHasAssetsOutsideUK(TRUE)
            .boEmailRequestInfoNotificationRequested(Boolean.FALSE)
            .boSendToBulkPrintRequested(Boolean.FALSE)
            .primaryApplicantSecondPhoneNumber(EXEC_PHONE)
            .primaryApplicantRelationshipToDeceased(Relationship.OTHER)
            .paRelationshipToDeceasedOther("cousin")
            .deceasedMaritalStatus(MaritalStatus.NEVER_MARRIED)
            .dateOfMarriageOrCP(null)
            .dateOfDivorcedCPJudicially(null)
            .willsOutsideOfUK(TRUE)
            .courtOfDecree("Random Court Name")
            .willGiftUnderEighteen(Boolean.FALSE)
            .applyingAsAnAttorney(TRUE)
            .attorneyOnBehalfOfNameAndAddress(null)
            .mentalCapacity(TRUE)
            .courtOfProtection(TRUE)
            .epaOrLpa(Boolean.FALSE)
            .epaRegistered(Boolean.FALSE)
            .domicilityCountry("Spain")
            .adopted(TRUE)
            .adoptiveRelatives(null)
            .domicilityIHTCert(TRUE)
            .foreignAsset(TRUE)
            .foreignAssetEstateValue(Long.valueOf("123"))
            .grantType(GrantType.INTESTACY)
            .childrenSurvived(TRUE)
            .childrenOverEighteenSurvivedText(NUM_CODICILS)
            .childrenUnderEighteenSurvivedText(NUM_CODICILS)
            .childrenDied(TRUE)
            .childrenDiedOverEighteenText(NUM_CODICILS)
            .childrenDiedUnderEighteenText(NUM_CODICILS)
            .grandChildrenSurvived(TRUE)
            .grandChildrenSurvivedOverEighteenText(NUM_CODICILS)
            .grandChildrenSurvivedUnderEighteenText(NUM_CODICILS)
            .parentsExistSurvived(TRUE)
            .parentsExistOverEighteenSurvived(NUM_CODICILS)
            .parentsExistUnderEighteenSurvived(NUM_CODICILS)
            .wholeBloodSiblingsSurvived(TRUE)
            .wholeBloodSiblingsSurvivedOverEighteen(NUM_CODICILS)
            .wholeBloodSiblingsSurvivedUnderEighteen(NUM_CODICILS)
            .wholeBloodSiblingsDied(TRUE)
            .wholeBloodSiblingsDiedOverEighteen(NUM_CODICILS)
            .wholeBloodSiblingsDiedUnderEighteen(NUM_CODICILS)
            .wholeBloodNeicesAndNephews(TRUE)
            .wholeBloodNeicesAndNephewsOverEighteen(NUM_CODICILS)
            .wholeBloodNeicesAndNephewsUnderEighteen(NUM_CODICILS)
            .halfBloodSiblingsSurvived(TRUE)
            .halfBloodSiblingsSurvivedOverEighteen(NUM_CODICILS)
            .halfBloodSiblingsSurvivedUnderEighteen(NUM_CODICILS)
            .halfBloodSiblingsDied(TRUE)
            .halfBloodSiblingsDiedOverEighteen(NUM_CODICILS)
            .halfBloodSiblingsDiedUnderEighteen(NUM_CODICILS)
            .halfBloodNeicesAndNephews(TRUE)
            .halfBloodNeicesAndNephewsOverEighteen(NUM_CODICILS)
            .halfBloodNeicesAndNephewsUnderEighteen(NUM_CODICILS)
            .grandparentsDied(TRUE)
            .grandparentsDiedOverEighteen(NUM_CODICILS)
            .grandparentsDiedUnderEighteen(NUM_CODICILS)
            .wholeBloodUnclesAndAuntsSurvived(TRUE)
            .wholeBloodUnclesAndAuntsSurvivedOverEighteen(NUM_CODICILS)
            .wholeBloodUnclesAndAuntsSurvivedUnderEighteen(NUM_CODICILS)
            .wholeBloodUnclesAndAuntsDied(TRUE)
            .wholeBloodUnclesAndAuntsDiedOverEighteen(NUM_CODICILS)
            .wholeBloodUnclesAndAuntsDiedUnderEighteen(NUM_CODICILS)
            .wholeBloodCousinsSurvived(TRUE)
            .wholeBloodCousinsSurvivedOverEighteen(NUM_CODICILS)
            .wholeBloodCousinsSurvivedUnderEighteen(NUM_CODICILS)
            .halfBloodUnclesAndAuntsSurvived(TRUE)
            .halfBloodUnclesAndAuntsSurvivedOverEighteen(NUM_CODICILS)
            .halfBloodUnclesAndAuntsSurvivedUnderEighteen(NUM_CODICILS)
            .halfBloodUnclesAndAuntsDied(TRUE)
            .halfBloodUnclesAndAuntsDiedOverEighteen(NUM_CODICILS)
            .halfBloodUnclesAndAuntsDiedUnderEighteen(NUM_CODICILS)
            .halfBloodCousinsSurvived(TRUE)
            .halfBloodCousinsSurvivedOverEighteen(NUM_CODICILS)
            .halfBloodCousinsSurvivedUnderEighteen(NUM_CODICILS)
            .applicationFeePaperForm(Long.valueOf("0"))
            .feeForCopiesPaperForm(Long.valueOf("0"))
            .totalFeePaperForm(Long.valueOf("0"))
            .paperPaymentMethod("debitOrCredit")
            .paymentReferenceNumberPaperform(IHT_REFERENCE)
            .paperForm(TRUE)
            .bulkScanCaseReference(BULK_SCAN_REFERENCE)
            .grantDelayedNotificationDate(GRANT_DELAYED_DATE)
            .grantStoppedDate(GRANT_STOPPED_DATE)
            .grantDelayedNotificationSent(TRUE)
            .grantAwaitingDocumentationNotificationDate(GRANT_AWAITING_DOCS_DATE)
            .grantAwaitingDocumentatioNotificationSent(TRUE)
            .bulkScanEnvelopes(BULK_SCAN_ENVELOPES)
            .languagePreferenceWelsh(FALSE)
            .applicationType(uk.gov.hmcts.reform.probate.model.cases.ApplicationType.SOLICITORS)
            .build();
        additionalExecutorsApplyingMock = new ArrayList<>();
        additionalExecutorsNotApplyingMock = new ArrayList<>();
        solAdditionalExecutorsApplyingMock = new ArrayList<>();
        solAdditionalExecutorsNotApplyingMock = new ArrayList<>();

        additionalExecutorsApplyingMock.add(new CollectionMember<>(SOLICITOR_SOT_ID, ADDITIONAL_EXECUTOR_APPLYING));
        additionalExecutorsNotApplyingMock
            .add(new CollectionMember<>(SOLICITOR_SOT_ID, ADDITIONAL_EXECUTOR_NOT_APPLYING));
        solAdditionalExecutorsApplyingMock
            .add(new CollectionMember<>(SOLICITOR_SOT_ID, SOL_ADDITIONAL_EXECUTOR_APPLYING));
        solAdditionalExecutorsNotApplyingMock
            .add(new CollectionMember<>(SOLICITOR_SOT_ID, SOL_ADDITIONAL_EXECUTOR_NOT_APPLYING));

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        when(caseDetailsMock.getId()).thenReturn(123456789012456L);
        when(feesResponse.getOverseasCopiesFeeResponse()).thenReturn(feeForNonUkCopies);
        when(feesResponse.getUkCopiesFeeResponse()).thenReturn(feeForUkCopies);
        when(feesResponse.getApplicationFeeResponse()).thenReturn(applicationFee);
        when(feesResponse.getTotalAmount()).thenReturn(totalFee);

        when(taskListUpdateService.generateTaskList(any(CaseDetails.class),
            any(ResponseCaseData.ResponseCaseDataBuilder.class)))
            .thenAnswer(invocation -> invocation.getArgument(1));
    }

    @Test
    void shouldConvertRequestToDataBeanForWithStateChange() {
        CallbackResponse callbackResponse =
            underTest.transformWithConditionalStateChange(callbackRequestMock, CHANGED_STATE);

        assertCommon(callbackResponse);
        assertLegacyInfo(callbackResponse);

        assertTrue(CHANGED_STATE.isPresent());
        assertEquals(CHANGED_STATE.get(), callbackResponse.getData().getState());
    }

    @Test
    void shouldConvertRequestToDataBeanWithNoStateChange() {
        CallbackResponse callbackResponse =
            underTest.transformWithConditionalStateChange(callbackRequestMock, ORIGINAL_STATE);

        assertCommon(callbackResponse);
        assertLegacyInfo(callbackResponse);
        assertEquals(YES, callbackResponse.getData().getBoEmailRequestInfoNotification());
        assertNull(callbackResponse.getData().getState());
    }

    @Test
    void shouldConvertRequestToDataBeanForPaymentWithExecutorDetails() {

        when(documentLinkMock.getDocumentBinaryUrl()).thenReturn(DOC_BINARY_URL);
        when(documentLinkMock.getDocumentUrl()).thenReturn(DOC_URL);
        when(documentLinkMock.getDocumentFilename()).thenReturn(DOC_NAME);
        Document document = Document.builder()
            .documentLink(documentLinkMock)
            .documentType(LEGAL_STATEMENT_PROBATE)
            .build();

        CallbackResponse callbackResponse = underTest.transform(callbackRequestMock, document, "gop");

        assertCommon(callbackResponse);
        assertLegacyInfo(callbackResponse);

        assertEquals(DOC_BINARY_URL, callbackResponse.getData().getSolsLegalStatementDocument().getDocumentBinaryUrl());
        assertEquals(DOC_URL, callbackResponse.getData().getSolsLegalStatementDocument().getDocumentUrl());
        assertEquals(DOC_NAME, callbackResponse.getData().getSolsLegalStatementDocument().getDocumentFilename());
        assertNull(callbackResponse.getData().getSolsSOTNeedToUpdate());
    }

    @Test
    void shouldConvertRequestToDataBeanForPaymentWithLegalStatementDocNullWhenPdfServiceTemplateIsNull() {
        Document document = Document.builder()
            .documentLink(documentLinkMock)
            .build();
        CallbackResponse callbackResponse = underTest.transform(callbackRequestMock, document, null);

        assertCommon(callbackResponse);
        assertLegacyInfo(callbackResponse);

        assertNull(callbackResponse.getData().getSolsLegalStatementDocument());
    }

    @Test
    void shouldAddDigitalGrantDraftToGeneratedDocuments() {
        Document document = Document.builder()
            .documentLink(documentLinkMock)
            .documentType(DIGITAL_GRANT_DRAFT)
            .build();

        CallbackResponse callbackResponse =
            underTest.addDocuments(callbackRequestMock, Arrays.asList(document), null, null);

        assertCommon(callbackResponse);
        assertLegacyInfo(callbackResponse);

        assertEquals(1, callbackResponse.getData().getProbateDocumentsGenerated().size());
    }

    @Test
    void shouldAddNocToGeneratedDocuments() {
        Document document = Document.builder()
                .documentLink(documentLinkMock)
                .documentType(SENT_EMAIL)
                .build();

        CallbackResponse callbackResponse =
                underTest.addNocDocuments(callbackRequestMock, Arrays.asList(document));

        assertCommon(callbackResponse);
    }

    @Test
    void shouldConvertRequestToDataBeanForPaymentWithFeeAccount() {
        CaseData caseData = caseDataBuilder.solsPaymentMethods(SOL_PAY_METHODS_FEE)
            .solsFeeAccountNumber(FEE_ACCT_NUMBER)
            .payments(null)
            .build();
        when(caseDetailsMock.getData()).thenReturn(caseData);
        CallbackResponse callbackResponse = underTest.transformForSolicitorComplete(callbackRequestMock, feesResponse,
                SENTEMAIL, coversheetMock, USER_ID);

        assertCommonDetails(callbackResponse);
        assertLegacyInfo(callbackResponse);

        assertEquals(TOTAL_FEE, callbackResponse.getData().getTotalFee());
        assertEquals(SOL_PAY_METHODS_FEE, callbackResponse.getData().getSolsPaymentMethods());
        assertEquals(FEE_ACCT_NUMBER, callbackResponse.getData().getSolsFeeAccountNumber());
        assertEquals(0, callbackResponse.getData().getBoDocumentsUploaded().size());
    }

    @Test
    void shouldConvertRequestToDataBeanForPaymentWithFeeAccountAndLegalStatementUpload() {
        CaseData caseData = caseDataBuilder.solsPaymentMethods(SOL_PAY_METHODS_FEE)
            .solsFeeAccountNumber(FEE_ACCT_NUMBER)
            .solsLegalStatementUpload(legalStatementUploadMock)
            .boDocumentsUploaded(new ArrayList<CollectionMember<UploadDocument>>())
            .build();
        when(caseDetailsMock.getData()).thenReturn(caseData);

        CallbackResponse callbackResponse = underTest.transformForSolicitorComplete(callbackRequestMock, feesResponse,
                SENTEMAIL, coversheetMock, USER_ID);

        assertCommonDetails(callbackResponse);
        assertLegacyInfo(callbackResponse);

        assertEquals(TOTAL_FEE, callbackResponse.getData().getTotalFee());
        assertEquals(SOL_PAY_METHODS_FEE, callbackResponse.getData().getSolsPaymentMethods());
        assertEquals(FEE_ACCT_NUMBER, callbackResponse.getData().getSolsFeeAccountNumber());
        assertEquals(1, callbackResponse.getData().getBoDocumentsUploaded().size());
    }

    @Test
    void shouldConvertRequestToDataBeanForPaymentWithServiceRequest() {
        CaseData caseData = caseDataBuilder.solsPaymentMethods(SOL_PAY_METHODS_FEE)
            .build();
        when(caseDetailsMock.getData()).thenReturn(caseData);
        CallbackResponse callbackResponse = underTest.transformForSolicitorComplete(callbackRequestMock, feesResponse,
                SENTEMAIL, coversheetMock, USER_ID);

        assertCommonDetails(callbackResponse);
        assertLegacyInfo(callbackResponse);
        assertCommonAdditionalExecutors(callbackResponse);
        assertApplicationType(callbackResponse, ApplicationType.SOLICITOR);
        assertEquals(APPLICANT_HAS_ALIAS, callbackResponse.getData().getPrimaryApplicantHasAlias());
        assertEquals(OTHER_EXECS_EXIST, callbackResponse.getData().getOtherExecutorExists());

        assertEquals(TOTAL_FEE, callbackResponse.getData().getTotalFee());
        assertEquals(SOL_PAY_METHODS_FEE, callbackResponse.getData().getSolsPaymentMethods());
        verify(caseDataTransformerMock).transformForSolicitorApplicationCompletion(callbackRequestMock,
                BigDecimal.valueOf(66));
    }

    @Test
    void shouldConvertRequestToDataBeanForPaymentWithoutServiceRequest() {
        CaseData caseData = (CaseData) caseDataBuilder
                .payments(null)
                .paymentTaken(NOT_APPLICABLE)
                .build();
        when(caseDetailsMock.getData()).thenReturn(caseData);
        CallbackResponse callbackResponse = underTest.transformForSolicitorComplete(callbackRequestMock, feesResponse,
               SENTEMAIL, coversheetMock, USER_ID);

        assertCommonDetails(callbackResponse);
        assertLegacyInfo(callbackResponse);
        assertCommonAdditionalExecutors(callbackResponse);
        assertApplicationType(callbackResponse, ApplicationType.SOLICITOR);
        assertEquals(APPLICANT_HAS_ALIAS, callbackResponse.getData().getPrimaryApplicantHasAlias());
        assertEquals(OTHER_EXECS_EXIST, callbackResponse.getData().getOtherExecutorExists());

        assertEquals(TOTAL_FEE, callbackResponse.getData().getTotalFee());
        assertNull(callbackResponse.getData().getServiceRequestReference());
        assertEquals(NOT_APPLICABLE, callbackResponse.getData().getPaymentTaken());
    }

    @Test
    void shouldSetSchemaVersionCorrectly() {
        CaseData caseData = caseDataBuilder.deceasedDateOfBirth(null)
            .build();
        when(caseDetailsMock.getData()).thenReturn(caseData);

        CallbackResponse callbackResponse = underTest.transformForSolicitorComplete(callbackRequestMock, feesResponse,
                SENTEMAIL, coversheetMock, USER_ID);

        assertEquals("2.0.0", callbackResponse.getData().getSchemaVersion());
    }

    @Test
    void shouldConvertRequestToDataBeanForPaymentWithFeeAccountNoPayment() {
        CaseData caseData = caseDataBuilder.solsPaymentMethods(SOL_PAY_METHODS_FEE)
            .solsFeeAccountNumber(FEE_ACCT_NUMBER)
            .payments(null)
            .build();
        when(caseDetailsMock.getData()).thenReturn(caseData);
        CallbackResponse callbackResponse = underTest.transformForSolicitorComplete(callbackRequestMock, feesResponse,
                SENTEMAIL, coversheetMock, USER_ID);

        assertCommonDetails(callbackResponse);
        assertLegacyInfo(callbackResponse);
        assertCommonAdditionalExecutors(callbackResponse);
        assertApplicationType(callbackResponse, ApplicationType.SOLICITOR);
        assertEquals(APPLICANT_HAS_ALIAS, callbackResponse.getData().getPrimaryApplicantHasAlias());
        assertEquals(OTHER_EXECS_EXIST, callbackResponse.getData().getOtherExecutorExists());

        assertEquals(TOTAL_FEE, callbackResponse.getData().getTotalFee());
        assertEquals(SOL_PAY_METHODS_FEE, callbackResponse.getData().getSolsPaymentMethods());
        assertEquals(FEE_ACCT_NUMBER, callbackResponse.getData().getSolsFeeAccountNumber());
        assertNull(callbackResponse.getData().getPayments());
    }

    @Test
    void shouldTestForNullDOB() {
        CaseData caseData = caseDataBuilder.deceasedDateOfBirth(null)
            .build();
        when(caseDetailsMock.getData()).thenReturn(caseData);

        CallbackResponse callbackResponse = underTest.transformForSolicitorComplete(callbackRequestMock, feesResponse,
                SENTEMAIL, coversheetMock, USER_ID);

        assertEquals(null, callbackResponse.getData().getDeceasedDateOfBirth());
    }

    @Test
    void shouldTestForNullDOD() throws JsonProcessingException {
        CaseData caseData = caseDataBuilder.deceasedDateOfDeath(null)
            .build();
        when(caseDetailsMock.getData()).thenReturn(caseData);

        CallbackResponse callbackResponse = underTest.transformForSolicitorComplete(callbackRequestMock, feesResponse,
                SENTEMAIL, coversheetMock, USER_ID);

        assertEquals(null, callbackResponse.getData().getDeceasedDateOfDeath());
    }

    @Test
    void shouldConvertRequestToDataBeanForPaymentWithCheque() throws JsonProcessingException {
        CaseData caseData = caseDataBuilder.solsPaymentMethods(SOL_PAY_METHODS_CHEQUE)
            .build();
        when(caseDetailsMock.getData()).thenReturn(caseData);

        CallbackResponse callbackResponse = underTest.transformForSolicitorComplete(callbackRequestMock, feesResponse,
            SENTEMAIL, coversheetMock, USER_ID);

        assertCommonDetails(callbackResponse);
        assertLegacyInfo(callbackResponse);
        assertCommonAdditionalExecutors(callbackResponse);
        assertApplicationType(callbackResponse, ApplicationType.SOLICITOR);
        assertEquals(APPLICANT_HAS_ALIAS, callbackResponse.getData().getPrimaryApplicantHasAlias());
        assertEquals(OTHER_EXECS_EXIST, callbackResponse.getData().getOtherExecutorExists());

        assertEquals(YES, callbackResponse.getData().getBoEmailRequestInfoNotification());
        assertEquals(TOTAL_FEE, callbackResponse.getData().getTotalFee());
        assertEquals(SOL_PAY_METHODS_CHEQUE, callbackResponse.getData().getSolsPaymentMethods());
        assertNull(callbackResponse.getData().getSolsFeeAccountNumber());
    }

    @Test
    void shouldAddDocumentsToProbateDocumentsAndNotificationsGenerated() {
        Document grantDocument = Document.builder().documentType(DIGITAL_GRANT).build();
        Document grantIssuedSentEmail = Document.builder().documentType(SENT_EMAIL).build();

        CallbackResponse callbackResponse = underTest.addDocuments(callbackRequestMock,
            Arrays.asList(grantDocument, grantIssuedSentEmail), null, null);

        assertCommon(callbackResponse);
        assertLegacyInfo(callbackResponse);

        assertEquals(1, callbackResponse.getData().getProbateDocumentsGenerated().size());
        assertEquals(grantDocument, callbackResponse.getData().getProbateDocumentsGenerated().get(0).getValue());

        assertEquals(1, callbackResponse.getData().getProbateNotificationsGenerated().size());
        assertEquals(grantIssuedSentEmail,
            callbackResponse.getData().getProbateNotificationsGenerated().get(0).getValue());
    }

    @Test
    void shouldSetSendLetterIdAndPdfSize() {
        Document grantDocument = Document.builder().documentType(DIGITAL_GRANT).build();
        Document grantIssuedSentEmail = Document.builder().documentType(SENT_EMAIL).build();

        CallbackResponse callbackResponse = underTest.addDocuments(callbackRequestMock,
            Arrays.asList(grantDocument, grantIssuedSentEmail), "abc123", "2");

        assertCommon(callbackResponse);

        assertEquals("2", callbackResponse.getData().getBulkPrintPdfSize());
        assertEquals("abc123", callbackResponse.getData().getBulkPrintSendLetterId());
        assertEquals(1, callbackResponse.getData().getProbateDocumentsGenerated().size());
        assertEquals(grantDocument, callbackResponse.getData().getProbateDocumentsGenerated().get(0).getValue());
        assertEquals(1, callbackResponse.getData().getProbateNotificationsGenerated().size());
        assertEquals(grantIssuedSentEmail,
            callbackResponse.getData().getProbateNotificationsGenerated().get(0).getValue());

    }

    @Test
    void shouldSetSendLetterIdAndPdfSizeInWelshDigitalGrant() {
        Document grantDocument = Document.builder().documentType(WELSH_DIGITAL_GRANT).build();
        Document grantIssuedSentEmail = Document.builder().documentType(SENT_EMAIL).build();

        CallbackResponse callbackResponse = underTest.addDocuments(callbackRequestMock,
            Arrays.asList(grantDocument, grantIssuedSentEmail), "abc123", "2");

        assertCommon(callbackResponse);

        assertEquals("2", callbackResponse.getData().getBulkPrintPdfSize());
        assertEquals("abc123", callbackResponse.getData().getBulkPrintSendLetterId());
        assertEquals(1, callbackResponse.getData().getProbateDocumentsGenerated().size());
        assertEquals(grantDocument, callbackResponse.getData().getProbateDocumentsGenerated().get(0).getValue());
        assertEquals(1, callbackResponse.getData().getProbateNotificationsGenerated().size());
        assertEquals(grantIssuedSentEmail,
            callbackResponse.getData().getProbateNotificationsGenerated().get(0).getValue());
    }

    @Test
    void shouldSetSendLetterIdAndPdfSizeInWelshIntestacyGrant() {
        Document grantDocument = Document.builder().documentType(WELSH_INTESTACY_GRANT).build();
        Document grantIssuedSentEmail = Document.builder().documentType(SENT_EMAIL).build();

        CallbackResponse callbackResponse = underTest.addDocuments(callbackRequestMock,
            Arrays.asList(grantDocument, grantIssuedSentEmail), "abc123", "2");

        assertCommon(callbackResponse);

        assertEquals("2", callbackResponse.getData().getBulkPrintPdfSize());
        assertEquals("abc123", callbackResponse.getData().getBulkPrintSendLetterId());
        assertEquals(1, callbackResponse.getData().getProbateDocumentsGenerated().size());
        assertEquals(grantDocument, callbackResponse.getData().getProbateDocumentsGenerated().get(0).getValue());
        assertEquals(1, callbackResponse.getData().getProbateNotificationsGenerated().size());
        assertEquals(grantIssuedSentEmail,
            callbackResponse.getData().getProbateNotificationsGenerated().get(0).getValue());
    }

    @Test
    void shouldSetSendLetterIdAndPdfSizeInWelshAdmonWillGrant() {
        Document grantDocument = Document.builder().documentType(WELSH_ADMON_WILL_GRANT).build();
        Document grantIssuedSentEmail = Document.builder().documentType(SENT_EMAIL).build();

        CallbackResponse callbackResponse = underTest.addDocuments(callbackRequestMock,
            Arrays.asList(grantDocument, grantIssuedSentEmail), "abc123", "2");

        assertCommon(callbackResponse);

        assertEquals("2", callbackResponse.getData().getBulkPrintPdfSize());
        assertEquals("abc123", callbackResponse.getData().getBulkPrintSendLetterId());
        assertEquals(1, callbackResponse.getData().getProbateDocumentsGenerated().size());
        assertEquals(grantDocument, callbackResponse.getData().getProbateDocumentsGenerated().get(0).getValue());
        assertEquals(1, callbackResponse.getData().getProbateNotificationsGenerated().size());
        assertEquals(grantIssuedSentEmail,
            callbackResponse.getData().getProbateNotificationsGenerated().get(0).getValue());
    }

    @Test
    void shouldSetSendLetterIdAndPdfSizeGrantReissue() {
        Document grantDocument = Document.builder().documentType(DIGITAL_GRANT_REISSUE).build();
        Document grantIssuedSentEmail = Document.builder().documentType(SENT_EMAIL).build();

        CallbackResponse callbackResponse = underTest.addDocuments(callbackRequestMock,
            Arrays.asList(grantDocument, grantIssuedSentEmail), "abc123", "2");

        assertCommon(callbackResponse);

        assertEquals("abc123", callbackResponse.getData().getBulkPrintId().get(0).getValue().getSendLetterId());
        assertEquals(DIGITAL_GRANT_REISSUE.getTemplateName(),
            callbackResponse.getData().getBulkPrintId().get(0).getValue().getTemplateName());
        assertEquals(1, callbackResponse.getData().getProbateDocumentsGenerated().size());
        assertEquals(grantDocument, callbackResponse.getData().getProbateDocumentsGenerated().get(0).getValue());
        assertEquals(1, callbackResponse.getData().getProbateNotificationsGenerated().size());
        assertEquals(grantIssuedSentEmail,
            callbackResponse.getData().getProbateNotificationsGenerated().get(0).getValue());
        assertEquals(YES, callbackResponse.getData().getBoEmailGrantReissuedNotificationRequested());
        assertEquals(YES, callbackResponse.getData().getBoGrantReissueSendToBulkPrintRequested());

    }

    @Test
    void shouldSetSendLetterIdAndPdfSizeAdmonWillGrantReissue() {
        Document grantDocument = Document.builder().documentType(ADMON_WILL_GRANT_REISSUE).build();
        Document grantIssuedSentEmail = Document.builder().documentType(SENT_EMAIL).build();

        CallbackResponse callbackResponse = underTest.addDocuments(callbackRequestMock,
            Arrays.asList(grantDocument, grantIssuedSentEmail), "abc123", "2");

        assertCommon(callbackResponse);
        assertEquals(YES, callbackResponse.getData().getBoEmailRequestInfoNotification());
        assertEquals("abc123", callbackResponse.getData()
            .getBulkPrintId().get(0).getValue().getSendLetterId());
        assertEquals(ADMON_WILL_GRANT_REISSUE.getTemplateName(),
            callbackResponse.getData().getBulkPrintId().get(0).getValue().getTemplateName());
        assertEquals(1, callbackResponse.getData().getProbateDocumentsGenerated().size());
        assertEquals(grantDocument, callbackResponse.getData().getProbateDocumentsGenerated().get(0).getValue());
        assertEquals(1, callbackResponse.getData().getProbateNotificationsGenerated().size());
        assertEquals(grantIssuedSentEmail,
            callbackResponse.getData().getProbateNotificationsGenerated().get(0).getValue());
        assertEquals(YES, callbackResponse.getData().getBoEmailGrantReissuedNotificationRequested());
        assertEquals(YES, callbackResponse.getData().getBoGrantReissueSendToBulkPrintRequested());

    }

    @Test
    void shouldSetSendLetterIdAndPdfSizeIntestacyGrantReissue() {
        Document grantDocument = Document.builder().documentType(INTESTACY_GRANT_REISSUE).build();
        Document grantIssuedSentEmail = Document.builder().documentType(SENT_EMAIL).build();

        CallbackResponse callbackResponse = underTest.addDocuments(callbackRequestMock,
            Arrays.asList(grantDocument, grantIssuedSentEmail), "abc123", "2");

        assertCommon(callbackResponse);

        assertEquals("abc123", callbackResponse.getData()
            .getBulkPrintId().get(0).getValue().getSendLetterId());
        assertEquals(INTESTACY_GRANT_REISSUE.getTemplateName(),
            callbackResponse.getData().getBulkPrintId().get(0).getValue().getTemplateName());
        assertEquals(1, callbackResponse.getData().getProbateDocumentsGenerated().size());
        assertEquals(grantDocument, callbackResponse.getData().getProbateDocumentsGenerated().get(0).getValue());
        assertEquals(1, callbackResponse.getData().getProbateNotificationsGenerated().size());
        assertEquals(grantIssuedSentEmail,
            callbackResponse.getData().getProbateNotificationsGenerated().get(0).getValue());
        assertEquals(YES, callbackResponse.getData().getBoEmailGrantReissuedNotificationRequested());
        assertEquals(YES, callbackResponse.getData().getBoGrantReissueSendToBulkPrintRequested());

    }

    @Test
    void shouldSetSendLetterIdAndPdfSizeWelshGrantReissue() {
        Document grantDocument = Document.builder().documentType(WELSH_DIGITAL_GRANT_REISSUE).build();
        Document grantIssuedSentEmail = Document.builder().documentType(SENT_EMAIL).build();

        CallbackResponse callbackResponse = underTest.addDocuments(callbackRequestMock,
            Arrays.asList(grantDocument, grantIssuedSentEmail), "abc123", "2");

        assertCommon(callbackResponse);

        assertEquals("abc123", callbackResponse.getData()
            .getBulkPrintId().get(0).getValue().getSendLetterId());
        assertEquals(WELSH_DIGITAL_GRANT_REISSUE.getTemplateName(),
            callbackResponse.getData().getBulkPrintId().get(0).getValue().getTemplateName());
        assertEquals(1, callbackResponse.getData().getProbateDocumentsGenerated().size());
        assertEquals(grantDocument, callbackResponse.getData().getProbateDocumentsGenerated().get(0).getValue());
        assertEquals(1, callbackResponse.getData().getProbateNotificationsGenerated().size());
        assertEquals(grantIssuedSentEmail,
            callbackResponse.getData().getProbateNotificationsGenerated().get(0).getValue());
        assertEquals(YES, callbackResponse.getData().getBoEmailGrantReissuedNotificationRequested());
        assertEquals(YES, callbackResponse.getData().getBoGrantReissueSendToBulkPrintRequested());
    }

    @Test
    void shouldSetSendLetterIdAndPdfSizeWelshIntestacyReissue() {
        Document grantDocument = Document.builder().documentType(WELSH_INTESTACY_GRANT_REISSUE).build();
        Document grantIssuedSentEmail = Document.builder().documentType(SENT_EMAIL).build();

        CallbackResponse callbackResponse = underTest.addDocuments(callbackRequestMock,
            Arrays.asList(grantDocument, grantIssuedSentEmail), "abc123", "2");

        assertCommon(callbackResponse);

        assertEquals("abc123", callbackResponse.getData()
            .getBulkPrintId().get(0).getValue().getSendLetterId());
        assertEquals(WELSH_INTESTACY_GRANT_REISSUE.getTemplateName(),
            callbackResponse.getData().getBulkPrintId().get(0).getValue().getTemplateName());
        assertEquals(1, callbackResponse.getData().getProbateDocumentsGenerated().size());
        assertEquals(grantDocument, callbackResponse.getData().getProbateDocumentsGenerated().get(0).getValue());
        assertEquals(1, callbackResponse.getData().getProbateNotificationsGenerated().size());
        assertEquals(grantIssuedSentEmail,
            callbackResponse.getData().getProbateNotificationsGenerated().get(0).getValue());
        assertEquals(YES, callbackResponse.getData().getBoEmailGrantReissuedNotificationRequested());
        assertEquals(YES, callbackResponse.getData().getBoGrantReissueSendToBulkPrintRequested());
    }

    @Test
    void shouldSetSendLetterIdAndPdfSizeWelshAdmonWillReissue() {
        Document grantDocument = Document.builder().documentType(WELSH_ADMON_WILL_GRANT_REISSUE).build();
        Document grantIssuedSentEmail = Document.builder().documentType(SENT_EMAIL).build();

        CallbackResponse callbackResponse = underTest.addDocuments(callbackRequestMock,
            Arrays.asList(grantDocument, grantIssuedSentEmail), "abc123", "2");

        assertCommon(callbackResponse);

        assertEquals("abc123", callbackResponse.getData()
            .getBulkPrintId().get(0).getValue().getSendLetterId());
        assertEquals(WELSH_ADMON_WILL_GRANT_REISSUE.getTemplateName(),
            callbackResponse.getData().getBulkPrintId().get(0).getValue().getTemplateName());
        assertEquals(1, callbackResponse.getData().getProbateDocumentsGenerated().size());
        assertEquals(grantDocument, callbackResponse.getData().getProbateDocumentsGenerated().get(0).getValue());
        assertEquals(1, callbackResponse.getData().getProbateNotificationsGenerated().size());
        assertEquals(grantIssuedSentEmail,
            callbackResponse.getData().getProbateNotificationsGenerated().get(0).getValue());
        assertEquals(YES, callbackResponse.getData().getBoEmailGrantReissuedNotificationRequested());
        assertEquals(YES, callbackResponse.getData().getBoGrantReissueSendToBulkPrintRequested());
    }

    @Test
    void shouldSetGrantIssuedDateForEdgeCase() {
        Document document = Document.builder()
            .documentLink(documentLinkMock)
            .documentType(EDGE_CASE)
            .build();

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        DateFormat targetFormat = new SimpleDateFormat(DATE_FORMAT);
        String grantIssuedDate = targetFormat.format(new Date());
        CallbackResponse callbackResponse = underTest.addDocuments(callbackRequestMock,
            Arrays.asList(document), null, null);
        assertEquals(grantIssuedDate, callbackResponse.getData().getGrantIssuedDate());
    }

    @Test
    void shouldAddDocumentToProbateNotificationsGenerated() {
        Document documentsReceivedSentEmail = Document.builder().documentType(SENT_EMAIL).build();

        CallbackResponse callbackResponse = underTest.addDocuments(callbackRequestMock,
            Arrays.asList(documentsReceivedSentEmail), null, null);

        assertCommon(callbackResponse);
        assertLegacyInfo(callbackResponse);

        assertEquals(1, callbackResponse.getData().getProbateNotificationsGenerated().size());
        assertEquals(documentsReceivedSentEmail,
            callbackResponse.getData().getProbateNotificationsGenerated().get(0).getValue());
    }

    @Test
    void shouldAddNoDocumentButSetNotificationRequested() {
        List<Document> documents = new ArrayList<>();

        CaseData caseData = caseDataBuilder
            .solsSolicitorEmail(null)
            .primaryApplicantEmailAddress(null)
            .boEmailDocsReceivedNotification(NO)
            .boEmailDocsReceivedNotificationRequested(NO)
            .build();
        when(caseDetailsMock.getData()).thenReturn(caseData);

        CallbackResponse callbackResponse = underTest
            .addDocuments(callbackRequestMock, documents, null, null);

        assertEquals("No", callbackResponse.getData().getBoEmailDocsReceivedNotification());
    }

    @Test
    void shouldAddMatches() {
        CaseMatch caseMatch = CaseMatch.builder().build();

        CallbackResponse response = underTest.addMatches(callbackRequestMock, Collections.singletonList(caseMatch));

        assertCommon(response);
        assertLegacyInfo(response);

        assertEquals(1, response.getData().getCaseMatches().size());
        assertEquals(caseMatch, response.getData().getCaseMatches().get(0).getValue());
    }

    @Test
    void shouldSelectForQA() {
        CallbackResponse response = underTest.selectForQA(callbackRequestMock);

        assertCommon(response);
        assertLegacyInfo(response);

        assertEquals(CallbackResponseTransformer.QA_CASE_STATE, response.getData().getState());
    }

    @Test
    void shouldNotSelectForQA() {
        caseDataBuilder.boExaminationChecklistRequestQA(null);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        when(caseDetailsMock.getState()).thenReturn("CurrentStateId");

        CallbackResponse response = underTest.selectForQA(callbackRequestMock);

        assertEquals("CurrentStateId", response.getData().getState());
    }

    @Test
    void shouldConvertRequestToDataBeanWithStopDetailsChange() {
        List<Document> documents = new ArrayList<>();
        documents.add(Document.builder().documentType(CAVEAT_STOPPED).build());

        CallbackResponse callbackResponse = underTest.caseStopped(callbackRequestMock, documents, "123");

        assertCommon(callbackResponse);
        assertLegacyInfo(callbackResponse);

        assertTrue(callbackResponse.getData().getBoStopDetails().isEmpty());
    }

    @Test
    void shouldNotIncludeBulkPrintIdWithOtherDocType() {
        List<Document> documents = new ArrayList<>();
        documents.add(Document.builder().documentType(DIGITAL_GRANT).build());

        CallbackResponse callbackResponse = underTest.caseStopped(callbackRequestMock, documents, "123");

        assertThat(callbackResponse.getData().getBulkPrintId(), is(EMPTY_LIST));
    }

    @Test
    void shouldTransformCallbackRequestToCallbackResponse() {
        CallbackResponse callbackResponse = underTest.transform(callbackRequestMock);

        assertCommon(callbackResponse);
    }

    @Test
    void verifyTrustCorpFieldsAreReset() {
        underTest.transformCase(callbackRequestMock);

        verify(resetResponseCaseDataTransformer, times(1))
            .resetTitleAndClearingFields(any(), any());
    }

    @Test
    void verifyExecutorListsAreSet() {
        caseDataBuilder
            .additionalExecutorsApplying(additionalExecutorsApplyingMock)
            .additionalExecutorsNotApplying(additionalExecutorsNotApplyingMock);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertEquals(additionalExecutorsApplyingMock, callbackResponse.getData().getAdditionalExecutorsApplying());
        assertEquals(additionalExecutorsNotApplyingMock,
            callbackResponse.getData().getAdditionalExecutorsNotApplying());
    }

    @Test
    void shouldTransformPersonalCaseForDeceasedAliasNamesExist() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL);
        List<CollectionMember<ProbateAliasName>> deceasedAliasNamesList = new ArrayList<>();
        deceasedAliasNamesList.add(createdDeceasedAliasName("0", ALIAS_FORENAME, ALIAS_SURNAME, YES));

        caseDataBuilder.deceasedAliasNameList(deceasedAliasNamesList);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertCommonDetails(callbackResponse);
        assertLegacyInfo(callbackResponse);
        assertEquals(YES, callbackResponse.getData().getBoEmailRequestInfoNotification());
        assertApplicationType(callbackResponse, ApplicationType.PERSONAL);
        assertEquals(YES, callbackResponse.getData().getPrimaryApplicantHasAlias());
        assertEquals(1, callbackResponse.getData().getSolsDeceasedAliasNamesList().size());
    }

    @Test
    void shouldTransformPersonalCaseForNoPaymentDeceasedAliasNames() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL);
        List<CollectionMember<ProbateAliasName>> deceasedAliasNamesList = new ArrayList<>();
        deceasedAliasNamesList.add(createdDeceasedAliasName("0", ALIAS_FORENAME, ALIAS_SURNAME, YES));

        caseDataBuilder.deceasedAliasNameList(deceasedAliasNamesList);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.updateTaskList(callbackRequestMock);

        assertCommonDetails(callbackResponse);
        assertLegacyInfo(callbackResponse);
        assertApplicationType(callbackResponse, ApplicationType.PERSONAL);
        assertEquals(YES, callbackResponse.getData().getDeceasedAnyOtherNames());
        assertEquals(1, callbackResponse.getData().getSolsDeceasedAliasNamesList().size());
    }

    @Test
    void shouldTransformPersonalCaseForEmptyDeceasedNames() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertCommonDetails(callbackResponse);
        assertLegacyInfo(callbackResponse);
        assertEquals(YES, callbackResponse.getData().getBoEmailRequestInfoNotification());
        assertApplicationType(callbackResponse, ApplicationType.PERSONAL);
        assertEquals(YES, callbackResponse.getData().getPrimaryApplicantHasAlias());
        assertEquals(0, callbackResponse.getData().getSolsDeceasedAliasNamesList().size());
    }

    @Test
    void shouldTransformPersonalCaseForNoPaymentEmptyDeceasedNames() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL);
        List<CollectionMember<ProbateAliasName>> deceasedAliasNamesList = new ArrayList<>();

        caseDataBuilder.deceasedAliasNameList(deceasedAliasNamesList);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.updateTaskList(callbackRequestMock);

        assertCommonDetails(callbackResponse);
        assertLegacyInfo(callbackResponse);
        assertApplicationType(callbackResponse, ApplicationType.PERSONAL);
        assertEquals(0, callbackResponse.getData().getSolsDeceasedAliasNamesList().size());
    }

    @Test
    void shouldTransformCaseForSolicitorWithDeceasedAliasNames() {
        caseDataBuilder.applicationType(ApplicationType.SOLICITOR);

        List<CollectionMember<AliasName>> deceasedAliasNamesList = new ArrayList<>();
        AliasName an11 = AliasName.builder().solsAliasname(SOLS_ALIAS_NAME).build();
        CollectionMember<AliasName> an1 = new CollectionMember<>("0", an11);
        deceasedAliasNamesList.add(an1);
        caseDataBuilder.solsDeceasedAliasNamesList(deceasedAliasNamesList);
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertCommonDetails(callbackResponse);
        assertLegacyInfo(callbackResponse);
        assertEquals(1, callbackResponse.getData().getSolsDeceasedAliasNamesList().size());
        assertSolsDetails(callbackResponse);
    }


    @Test
    void shouldTransformCaseForSolicitorWithProbate() {
        caseDataBuilder.applicationType(ApplicationType.SOLICITOR);
        caseDataBuilder.solsWillType(WILL_TYPE_PROBATE);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertCommonDetails(callbackResponse);
        assertLegacyInfo(callbackResponse);
        assertEquals(YES, callbackResponse.getData().getWillExists());
        assertSolsDetails(callbackResponse);
    }

    @Test
    void shouldTransformCaseForSolicitorWithIntestacy() {
        caseDataBuilder.applicationType(ApplicationType.SOLICITOR);
        caseDataBuilder.solsWillType(WILL_TYPE_INTESTACY);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertCommonDetails(callbackResponse);
        assertLegacyInfo(callbackResponse);
        assertEquals(NO, callbackResponse.getData().getWillExists());
        assertSolsDetails(callbackResponse);
    }

    @Test
    void shouldTransformCaseForSolicitorWithAdmon() {
        caseDataBuilder.applicationType(ApplicationType.SOLICITOR);
        caseDataBuilder.solsWillType(WILL_TYPE_ADMON);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertCommonDetails(callbackResponse);
        assertLegacyInfo(callbackResponse);
        assertEquals(YES, callbackResponse.getData().getWillExists());
        assertSolsDetails(callbackResponse);
    }

    @Test
    void shouldTransformCaseForPAWithIHTOnlineYes() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL);
        caseDataBuilder.ihtFormCompletedOnline(YES);
        caseDataBuilder.ihtReferenceNumber(IHT_REFERENCE);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertEquals(IHT_REFERENCE, callbackResponse.getData().getIhtReferenceNumber());
    }


    @Test
    void shouldTransformCaseForSolsEmailEmpty() {
        caseDataBuilder.solsSolicitorEmail("");
        caseDataBuilder.applicationType(SOLICITOR);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertEquals(NO, callbackResponse.getData().getBoEmailGrantIssuedNotification());
        assertEquals(NO, callbackResponse.getData().getBoEmailDocsReceivedNotification());
        assertEquals(NO, callbackResponse.getData().getBoEmailRequestInfoNotification());
        assertEquals(NO, callbackResponse.getData().getBoEmailGrantReissuedNotification());
    }

    @Test
    void shouldTransformCaseForSolsEmailIsSet() {
        caseDataBuilder.solsSolicitorEmail("");
        caseDataBuilder.applicationType(SOLICITOR);
        caseDataBuilder.solsSolicitorEmail(SOLICITOR_FIRM_EMAIL);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertEquals(YES, callbackResponse.getData().getBoEmailGrantIssuedNotification());
        assertEquals(YES, callbackResponse.getData().getBoEmailDocsReceivedNotification());
        assertEquals(YES, callbackResponse.getData().getBoEmailRequestInfoNotification());
        assertEquals(YES, callbackResponse.getData().getBoEmailGrantReissuedNotification());
    }

    @Test
    void shouldTransformCaseForPAEmailEmpty() {
        caseDataBuilder.primaryApplicantEmailAddress("");
        caseDataBuilder.applicationType(PERSONAL);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertEquals(NO, callbackResponse.getData().getBoEmailGrantIssuedNotification());
        assertEquals(NO, callbackResponse.getData().getBoEmailDocsReceivedNotification());
        assertEquals(NO, callbackResponse.getData().getBoEmailRequestInfoNotification());
        assertEquals(NO, callbackResponse.getData().getBoEmailGrantReissuedNotification());
    }

    @Test
    void shouldTransformCaseForPAEmailIsNotEmpty() {
        caseDataBuilder.primaryApplicantEmailAddress("primary@probate-test.com");
        caseDataBuilder.applicationType(PERSONAL);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertEquals(YES, callbackResponse.getData().getBoEmailGrantIssuedNotification());
        assertEquals(YES, callbackResponse.getData().getBoEmailDocsReceivedNotification());
        assertEquals(YES, callbackResponse.getData().getBoEmailRequestInfoNotification());
        assertEquals(YES, callbackResponse.getData().getBoEmailGrantReissuedNotification());
    }

    @Test
    void shouldTransformCaseForPAWithPrimaryApplicantAlias() {
        caseDataBuilder.primaryApplicantAlias(PRIMARY_EXEC_ALIAS_NAMES);
        caseDataBuilder.primaryApplicantSameWillName(YES);
        caseDataBuilder.primaryApplicantAliasReason("Other");
        caseDataBuilder.primaryApplicantOtherReason("Married");

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertEquals(YES, callbackResponse.getData().getPrimaryApplicantSameWillName());
        assertEquals(PRIMARY_EXEC_ALIAS_NAMES, callbackResponse.getData().getPrimaryApplicantAlias());
        assertEquals("Other", callbackResponse.getData().getPrimaryApplicantAliasReason());
        assertEquals("Married", callbackResponse.getData().getPrimaryApplicantOtherReason());
    }

    @Test
    void shouldTransformCaseForPAWithPrimaryApplicantAliasOtherToBeNull() {
        caseDataBuilder.primaryApplicantAlias(PRIMARY_EXEC_ALIAS_NAMES);
        caseDataBuilder.primaryApplicantSameWillName(YES);
        caseDataBuilder.primaryApplicantAliasReason("Marriage");
        caseDataBuilder.primaryApplicantOtherReason("Married");

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertEquals(YES, callbackResponse.getData().getPrimaryApplicantSameWillName());
        assertEquals(PRIMARY_EXEC_ALIAS_NAMES, callbackResponse.getData().getPrimaryApplicantAlias());
        assertEquals("Marriage", callbackResponse.getData().getPrimaryApplicantAliasReason());
        assertNull(callbackResponse.getData().getPrimaryApplicantOtherReason());
    }

    @Test
    void shouldTransformCaseForPAWithIHTOnlineNo() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL);
        caseDataBuilder.ihtFormCompletedOnline(NO);
        caseDataBuilder.ihtReferenceNumber(IHT_REFERENCE);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertNull(callbackResponse.getData().getIhtReferenceNumber());
    }

    @Test
    void shouldTransformForSolDeceasedDomicileEngAndWales() {
        caseDataBuilder
            .applicationType(SOLICITOR)
            .recordId(null)
            .paperForm(NO)
            .deceasedDomicileInEngWales(YES)
            .ukEstate(UK_ESTATE)
            .domicilityCountry(DOMICILITY_COUNTRY);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertNull(callbackResponse.getData().getDomicilityCountry());
        assertEquals("Item", callbackResponse.getData().getUkEstate().get(0).getValue().getItem());
        assertEquals("999.99", callbackResponse.getData().getUkEstate().get(0).getValue().getValue());
    }

    @Test
    void shouldTransformForSolDeceasedForeignDomicile() {
        caseDataBuilder
            .applicationType(SOLICITOR)
            .recordId(null)
            .paperForm(NO)
            .deceasedDomicileInEngWales(NO)
            .ukEstate(UK_ESTATE)
            .domicilityCountry(DOMICILITY_COUNTRY);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertEquals("OtherCountry", callbackResponse.getData().getDomicilityCountry());
    }

    @Test
    void shouldTransformCaseForSolsExecAliasIsNull() {
        caseDataBuilder.applicationType(SOLICITOR);
        caseDataBuilder.recordId(null);
        caseDataBuilder.paperForm(NO);
        caseDataBuilder.solsExecutorAliasNames(null);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertEquals("Alias names", callbackResponse.getData().getPrimaryApplicantAlias());
        assertEquals(null, callbackResponse.getData().getSolsExecutorAliasNames());
    }


    @Test
    void shouldTransformCaseForWhenPaperFormIsNO() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL);
        caseDataBuilder.ihtFormCompletedOnline(NO);
        caseDataBuilder.ihtReferenceNumber(IHT_REFERENCE);
        caseDataBuilder.paperForm(NO);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertEquals(null, callbackResponse.getData().getIhtReferenceNumber());
    }

    @Test
    void shouldTransformCaseForWhenPaperFormIsNull() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL);
        caseDataBuilder.ihtFormCompletedOnline(NO);
        caseDataBuilder.ihtReferenceNumber(IHT_REFERENCE);
        caseDataBuilder.paperForm(null);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertEquals(null, callbackResponse.getData().getIhtReferenceNumber());
    }

    @Test
    void shouldTransformCaseForWhenCaseTypeIsGOP() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL);
        caseDataBuilder.ihtFormCompletedOnline(NO);
        caseDataBuilder.ihtReferenceNumber(IHT_REFERENCE);
        caseDataBuilder.caseType(CASE_TYPE_GRANT_OF_PROBATE);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertEquals(null, callbackResponse.getData().getIhtReferenceNumber());
        assertEquals(CASE_TYPE_GRANT_OF_PROBATE, callbackResponse.getData().getCaseType());
    }

    @Test
    void shouldPreserveDeathRecordList() {

        final List mockList = mock(List.class);

        caseDataBuilder.deathRecords(mockList);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertSame(mockList, callbackResponse.getData().getDeathRecords());
    }

    @Test
    void shouldTransformCaseForWhenCaseTypeIsNull() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL);
        caseDataBuilder.ihtFormCompletedOnline(NO);
        caseDataBuilder.ihtReferenceNumber(IHT_REFERENCE);
        caseDataBuilder.caseType(null);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertEquals(null, callbackResponse.getData().getIhtReferenceNumber());
    }

    @Test
    void shouldGetUploadedDocuments() {
        caseDataBuilder.applicationType(ApplicationType.SOLICITOR);
        List<CollectionMember<UploadDocument>> documents = new ArrayList<>();
        documents.add(createUploadDocuments("0"));
        caseDataBuilder.boDocumentsUploaded(documents);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertCommonDetails(callbackResponse);
        assertApplicationType(callbackResponse, ApplicationType.SOLICITOR);
        assertLegacyInfo(callbackResponse);
        assertEquals(1, callbackResponse.getData().getBoDocumentsUploaded().size());
        assertSolsDetails(callbackResponse);
    }

    @Test
    void shouldGetPaperIntestacyApplication() {
        caseDataBuilder.applicationType(ApplicationType.SOLICITOR);
        List<CollectionMember<EstateItem>> estate = new ArrayList<>();
        estate.add(createEstateItems("0"));
        List<CollectionMember<AttorneyApplyingOnBehalfOf>> attorneyList = new ArrayList<>();
        attorneyList.add(createAttorneyApplyingList("0"));
        List<CollectionMember<AdoptedRelative>> adoptedRelatives = new ArrayList<>();
        adoptedRelatives.add(createAdoptiveRelativeList("0"));
        caseDataBuilder
            .primaryApplicantSecondPhoneNumber(EXEC_PHONE)
            .primaryApplicantRelationshipToDeceased("other")
            .paRelationshipToDeceasedOther("cousin")
            .deceasedMaritalStatus("neverMarried")
            .willDatedBeforeApril(YES)
            .deceasedEnterMarriageOrCP(NO)
            .dateOfMarriageOrCP(null)
            .dateOfDivorcedCPJudicially(null)
            .willsOutsideOfUK(YES)
            .courtOfDecree("Random Court Name")
            .willGiftUnderEighteen(NO)
            .applyingAsAnAttorney(YES)
            .attorneyOnBehalfOfNameAndAddress(null)
            .mentalCapacity(YES)
            .courtOfProtection(YES)
            .epaOrLpa(NO)
            .epaRegistered(NO)
            .domicilityCountry("Spain")
            .ukEstate(estate)
            .attorneyOnBehalfOfNameAndAddress(attorneyList)
            .adopted(YES)
            .adoptiveRelatives(adoptedRelatives)
            .domicilityIHTCert(YES)
            .entitledToApply(YES)
            .entitledToApplyOther(YES)
            .notifiedApplicants(YES)
            .foreignAsset(YES)
            .foreignAssetEstateValue("123")
            .caseType(CASE_TYPE_INTESTACY)
            .spouseOrPartner(NO)
            .childrenSurvived(YES)
            .childrenOverEighteenSurvived(NUM_CODICILS)
            .childrenUnderEighteenSurvived(NUM_CODICILS)
            .childrenDied(YES)
            .childrenDiedOverEighteen(NUM_CODICILS)
            .childrenDiedUnderEighteen(NUM_CODICILS)
            .grandChildrenSurvived(YES)
            .grandChildrenSurvivedOverEighteen(NUM_CODICILS)
            .grandChildrenSurvivedUnderEighteen(NUM_CODICILS)
            .parentsExistSurvived(YES)
            .parentsExistOverEighteenSurvived(NUM_CODICILS)
            .parentsExistUnderEighteenSurvived(NUM_CODICILS)
            .wholeBloodSiblingsSurvived(YES)
            .wholeBloodSiblingsSurvivedOverEighteen(NUM_CODICILS)
            .wholeBloodSiblingsSurvivedUnderEighteen(NUM_CODICILS)
            .wholeBloodSiblingsDied(YES)
            .wholeBloodSiblingsDiedOverEighteen(NUM_CODICILS)
            .wholeBloodSiblingsDiedUnderEighteen(NUM_CODICILS)
            .wholeBloodNeicesAndNephews(YES)
            .wholeBloodNeicesAndNephewsOverEighteen(NUM_CODICILS)
            .wholeBloodNeicesAndNephewsUnderEighteen(NUM_CODICILS)
            .halfBloodSiblingsSurvived(YES)
            .halfBloodSiblingsSurvivedOverEighteen(NUM_CODICILS)
            .halfBloodSiblingsSurvivedUnderEighteen(NUM_CODICILS)
            .halfBloodSiblingsDied(YES)
            .halfBloodSiblingsDiedOverEighteen(NUM_CODICILS)
            .halfBloodSiblingsDiedUnderEighteen(NUM_CODICILS)
            .halfBloodNeicesAndNephews(YES)
            .halfBloodNeicesAndNephewsOverEighteen(NUM_CODICILS)
            .halfBloodNeicesAndNephewsUnderEighteen(NUM_CODICILS)
            .grandparentsDied(YES)
            .grandparentsDiedOverEighteen(NUM_CODICILS)
            .grandparentsDiedUnderEighteen(NUM_CODICILS)
            .wholeBloodUnclesAndAuntsSurvived(YES)
            .wholeBloodUnclesAndAuntsSurvivedOverEighteen(NUM_CODICILS)
            .wholeBloodUnclesAndAuntsSurvivedUnderEighteen(NUM_CODICILS)
            .wholeBloodUnclesAndAuntsDied(YES)
            .wholeBloodUnclesAndAuntsDiedOverEighteen(NUM_CODICILS)
            .wholeBloodUnclesAndAuntsDiedUnderEighteen(NUM_CODICILS)
            .wholeBloodCousinsSurvived(YES)
            .wholeBloodCousinsSurvivedOverEighteen(NUM_CODICILS)
            .wholeBloodCousinsSurvivedUnderEighteen(NUM_CODICILS)
            .halfBloodUnclesAndAuntsSurvived(YES)
            .halfBloodUnclesAndAuntsSurvivedOverEighteen(NUM_CODICILS)
            .halfBloodUnclesAndAuntsSurvivedUnderEighteen(NUM_CODICILS)
            .halfBloodUnclesAndAuntsDied(YES)
            .halfBloodUnclesAndAuntsDiedOverEighteen(NUM_CODICILS)
            .halfBloodUnclesAndAuntsDiedUnderEighteen(NUM_CODICILS)
            .halfBloodCousinsSurvived(YES)
            .halfBloodCousinsSurvivedOverEighteen(NUM_CODICILS)
            .halfBloodCousinsSurvivedUnderEighteen(NUM_CODICILS)
            .applicationFeePaperForm("0")
            .feeForCopiesPaperForm("0")
            .totalFeePaperForm("0")
            .scannedDocuments(SCANNED_DOCUMENTS_LIST)
            .paperPaymentMethod("debitOrCredit")
            .paymentReferenceNumberPaperform(IHT_REFERENCE)
            .paperForm(YES)
            .dateOfDeathType(DECEASED_DATE_OF_DEATH_TYPE);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        Document document = Document.builder().documentType(DIGITAL_GRANT).build();
        CallbackResponse callbackResponse = underTest.paperForm(callbackRequestMock, document);
        assertEquals(1, callbackResponse.getData().getUkEstate().size());
        assertEquals(1, callbackResponse.getData().getAttorneyOnBehalfOfNameAndAddress().size());
        assertEquals(1, callbackResponse.getData().getScannedDocuments().size());
        assertEquals(1, callbackResponse.getData().getAdoptiveRelatives().size());
        assertEquals(CASE_TYPE_INTESTACY, callbackResponse.getData().getCaseType());
        assertEquals(NO, callbackResponse.getData().getWillExists());

        assertCommonDetails(callbackResponse);
        assertEquals(YES, callbackResponse.getData().getBoEmailRequestInfoNotification());
        assertLegacyInfo(callbackResponse);
        assertCommonPaperForm(callbackResponse);
        assertSolsDetails(callbackResponse);
    }

    @Test
    void shouldGetPaperGOPApplication() {
        caseDataBuilder.applicationType(ApplicationType.SOLICITOR);
        List<CollectionMember<EstateItem>> estate = new ArrayList<>();
        estate.add(createEstateItems("0"));
        List<CollectionMember<AttorneyApplyingOnBehalfOf>> attorneyList = new ArrayList<>();
        attorneyList.add(createAttorneyApplyingList("0"));
        List<CollectionMember<AdoptedRelative>> adoptedRelatives = new ArrayList<>();
        adoptedRelatives.add(createAdoptiveRelativeList("0"));
        caseDataBuilder
            .primaryApplicantSecondPhoneNumber(EXEC_PHONE)
            .primaryApplicantRelationshipToDeceased("other")
            .paRelationshipToDeceasedOther("cousin")
            .deceasedMaritalStatus("neverMarried")
            .willDatedBeforeApril(YES)
            .deceasedEnterMarriageOrCP(NO)
            .dateOfMarriageOrCP(null)
            .dateOfDivorcedCPJudicially(null)
            .willsOutsideOfUK(YES)
            .courtOfDecree("Random Court Name")
            .willGiftUnderEighteen(NO)
            .applyingAsAnAttorney(YES)
            .attorneyOnBehalfOfNameAndAddress(null)
            .mentalCapacity(YES)
            .courtOfProtection(YES)
            .epaOrLpa(NO)
            .epaRegistered(NO)
            .domicilityCountry("Spain")
            .ukEstate(estate)
            .attorneyOnBehalfOfNameAndAddress(attorneyList)
            .adopted(YES)
            .adoptiveRelatives(adoptedRelatives)
            .domicilityIHTCert(YES)
            .entitledToApply(YES)
            .entitledToApplyOther(YES)
            .notifiedApplicants(YES)
            .foreignAsset(YES)
            .foreignAssetEstateValue("123")
            .caseType(CASE_TYPE_GRANT_OF_PROBATE)
            .spouseOrPartner(NO)
            .childrenSurvived(YES)
            .childrenOverEighteenSurvived(NUM_CODICILS)
            .childrenUnderEighteenSurvived(NUM_CODICILS)
            .childrenDied(YES)
            .childrenDiedOverEighteen(NUM_CODICILS)
            .childrenDiedUnderEighteen(NUM_CODICILS)
            .grandChildrenSurvived(YES)
            .grandChildrenSurvivedOverEighteen(NUM_CODICILS)
            .grandChildrenSurvivedUnderEighteen(NUM_CODICILS)
            .parentsExistSurvived(YES)
            .parentsExistOverEighteenSurvived(NUM_CODICILS)
            .parentsExistUnderEighteenSurvived(NUM_CODICILS)
            .wholeBloodSiblingsSurvived(YES)
            .wholeBloodSiblingsSurvivedOverEighteen(NUM_CODICILS)
            .wholeBloodSiblingsSurvivedUnderEighteen(NUM_CODICILS)
            .wholeBloodSiblingsDied(YES)
            .wholeBloodSiblingsDiedOverEighteen(NUM_CODICILS)
            .wholeBloodSiblingsDiedUnderEighteen(NUM_CODICILS)
            .wholeBloodNeicesAndNephews(YES)
            .wholeBloodNeicesAndNephewsOverEighteen(NUM_CODICILS)
            .wholeBloodNeicesAndNephewsUnderEighteen(NUM_CODICILS)
            .halfBloodSiblingsSurvived(YES)
            .halfBloodSiblingsSurvivedOverEighteen(NUM_CODICILS)
            .halfBloodSiblingsSurvivedUnderEighteen(NUM_CODICILS)
            .halfBloodSiblingsDied(YES)
            .halfBloodSiblingsDiedOverEighteen(NUM_CODICILS)
            .halfBloodSiblingsDiedUnderEighteen(NUM_CODICILS)
            .halfBloodNeicesAndNephews(YES)
            .halfBloodNeicesAndNephewsOverEighteen(NUM_CODICILS)
            .halfBloodNeicesAndNephewsUnderEighteen(NUM_CODICILS)
            .grandparentsDied(YES)
            .grandparentsDiedOverEighteen(NUM_CODICILS)
            .grandparentsDiedUnderEighteen(NUM_CODICILS)
            .wholeBloodUnclesAndAuntsSurvived(YES)
            .wholeBloodUnclesAndAuntsSurvivedOverEighteen(NUM_CODICILS)
            .wholeBloodUnclesAndAuntsSurvivedUnderEighteen(NUM_CODICILS)
            .wholeBloodUnclesAndAuntsDied(YES)
            .wholeBloodUnclesAndAuntsDiedOverEighteen(NUM_CODICILS)
            .wholeBloodUnclesAndAuntsDiedUnderEighteen(NUM_CODICILS)
            .wholeBloodCousinsSurvived(YES)
            .wholeBloodCousinsSurvivedOverEighteen(NUM_CODICILS)
            .wholeBloodCousinsSurvivedUnderEighteen(NUM_CODICILS)
            .halfBloodUnclesAndAuntsSurvived(YES)
            .halfBloodUnclesAndAuntsSurvivedOverEighteen(NUM_CODICILS)
            .halfBloodUnclesAndAuntsSurvivedUnderEighteen(NUM_CODICILS)
            .halfBloodUnclesAndAuntsDied(YES)
            .halfBloodUnclesAndAuntsDiedOverEighteen(NUM_CODICILS)
            .halfBloodUnclesAndAuntsDiedUnderEighteen(NUM_CODICILS)
            .halfBloodCousinsSurvived(YES)
            .halfBloodCousinsSurvivedOverEighteen(NUM_CODICILS)
            .halfBloodCousinsSurvivedUnderEighteen(NUM_CODICILS)
            .applicationFeePaperForm("0")
            .feeForCopiesPaperForm("0")
            .totalFeePaperForm("0")
            .scannedDocuments(SCANNED_DOCUMENTS_LIST)
            .paperPaymentMethod("debitOrCredit")
            .paymentReferenceNumberPaperform(IHT_REFERENCE)
            .paperForm(YES)
            .dateOfDeathType(DECEASED_DATE_OF_DEATH_TYPE);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        Document document = Document.builder().documentType(DIGITAL_GRANT).build();
        CallbackResponse callbackResponse = underTest.paperForm(callbackRequestMock, document);
        assertEquals(1, callbackResponse.getData().getUkEstate().size());
        assertEquals(1, callbackResponse.getData().getAttorneyOnBehalfOfNameAndAddress().size());
        assertEquals(1, callbackResponse.getData().getScannedDocuments().size());
        assertEquals(1, callbackResponse.getData().getAdoptiveRelatives().size());
        assertEquals(CASE_TYPE_GRANT_OF_PROBATE, callbackResponse.getData().getCaseType());
        assertEquals(YES, callbackResponse.getData().getWillExists());

        assertCommonDetails(callbackResponse);
        assertLegacyInfo(callbackResponse);
        assertCommonPaperForm(callbackResponse);
        assertSolsDetails(callbackResponse);
    }

    @Test
    void shouldGetPaperGOPApplicationWithDocument() {
        caseDataBuilder.applicationType(ApplicationType.SOLICITOR);
        caseDataBuilder.paperForm(YES);
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        Document document = Document.builder().documentType(DIGITAL_GRANT).build();

        CallbackResponse callbackResponse = underTest.paperForm(callbackRequestMock, document);
        assertEquals(1, callbackResponse.getData().getProbateDocumentsGenerated().size());
        assertEquals(CASE_TYPE_GRANT_OF_PROBATE, callbackResponse.getData().getCaseType());

        assertCommonDetails(callbackResponse);
        assertLegacyInfo(callbackResponse);
    }

    @Test
    void shouldTransformCaseWithScannedDocuments() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL);
        caseDataBuilder.scannedDocuments(SCANNED_DOCUMENTS_LIST);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);
        assertEquals(1, callbackResponse.getData().getScannedDocuments().size());
        assertEquals(SCANNED_DOCUMENTS_LIST, callbackResponse.getData().getScannedDocuments());
    }

    @Test
    void shouldDefaultYesToBulkPrint() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        Document document = Document.builder().documentType(DIGITAL_GRANT).build();
        CallbackResponse callbackResponse = underTest.paperForm(callbackRequestMock, document);
        assertEquals("Yes", callbackResponse.getData().getBoSendToBulkPrintRequested());
        assertEquals("Yes", callbackResponse.getData().getBoSendToBulkPrint());
    }

    @Test
    void shouldDefaultSolicitorsInfoToNull() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        Document document = Document.builder().documentType(DIGITAL_GRANT).build();

        CallbackResponse callbackResponse = underTest.paperForm(callbackRequestMock, document);
        assertEquals(null, callbackResponse.getData().getSolsSolicitorAppReference());
        assertEquals(null, callbackResponse.getData().getSolsSolicitorEmail());
        assertEquals(null, callbackResponse.getData().getSolsSOTJobTitle());
        assertEquals(null, callbackResponse.getData().getSolsSOTName());
        assertEquals(null, callbackResponse.getData().getSolsSolicitorAddress());
        assertEquals(null, callbackResponse.getData().getSolsSolicitorFirmName());
        assertEquals(null, callbackResponse.getData().getSolsSolicitorPhoneNumber());
    }

    @Test
    void shouldSetSolicitorsInfoWhenApplicationTypeIsNull() {
        caseDataBuilder.applicationType(null);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        Document document = Document.builder().documentType(DIGITAL_GRANT).build();
        when(caseDataMock.getApplicationType()).thenReturn(SOLICITOR);
        CallbackResponse callbackResponse = underTest.paperForm(callbackRequestMock, document);
        assertSolsDetails(callbackResponse);
    }

    @Test
    void shouldSetSolicitorsInfoWhenApplicationTypeIht() {
        caseDataBuilder.ihtReferenceNumber("123456");

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        Document document = Document.builder().documentType(DIGITAL_GRANT).build();
        CallbackResponse callbackResponse = underTest.paperForm(callbackRequestMock, document);
        assertEquals(IHT_FORM_ID, callbackResponse.getData().getIhtFormId());
        assertSolsDetails(callbackResponse);
    }

    @Test
    void shouldSetSolicitorsInfoWhenApplicationTypeIhtIsNotChosen() {
        caseDataBuilder.ihtReferenceNumber("123456");
        caseDataBuilder.ihtFormId(null);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        Document document = Document.builder().documentType(DIGITAL_GRANT).build();
        CallbackResponse callbackResponse = underTest.paperForm(callbackRequestMock, document);
        assertNull(callbackResponse.getData().getIhtFormId());
        assertSolsDetails(callbackResponse);
    }

    @Test
    void shouldSetSolicitorsInfoWhenApplicationTypeIhtIsNull() {
        CaseData.CaseDataBuilder caseDataBuilder2;
        caseDataBuilder2 = CaseData.builder().ihtReferenceNumber(null);

        CaseDetails caseDetails = new CaseDetails(caseDataBuilder2.build(), LAST_MODIFIED_STR, 1L);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);
        Document document = Document.builder().documentType(DIGITAL_GRANT).build();
        when(caseDataMock.getApplicationType()).thenReturn(SOLICITOR);
        CallbackResponse callbackResponse = underTest.paperForm(callbackRequest, document);
        assertNull(callbackResponse.getData().getIhtFormId());
    }

    @Test
    void shouldSetSolicitorsInfoWhenApplicationTypeIhtIsEmpty() {
        CaseData.CaseDataBuilder caseDataBuilder2;
        caseDataBuilder2 = CaseData.builder().ihtReferenceNumber("").applicationType(APPLICATION_TYPE.SOLICITOR);

        CaseDetails caseDetails = new CaseDetails(caseDataBuilder2.build(), LAST_MODIFIED_STR, 1L);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        Document document = Document.builder().documentType(DIGITAL_GRANT).build();
        CallbackResponse callbackResponse = underTest.paperForm(callbackRequest, document);
        assertNull(callbackResponse.getData().getIhtFormId());
    }

    @Test
    void shouldSetGrantIssuedDate() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL);
        Document document = Document.builder()
            .documentLink(documentLinkMock)
            .documentType(DIGITAL_GRANT)
            .build();

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        DateFormat targetFormat = new SimpleDateFormat(DATE_FORMAT);
        String grantIssuedDate = targetFormat.format(new Date());
        CallbackResponse callbackResponse = underTest.addDocuments(callbackRequestMock,
            Arrays.asList(document), null, null);
        assertEquals(grantIssuedDate, callbackResponse.getData().getGrantIssuedDate());
    }

    @Test
    void shouldSetGrantReissuedDateAtReissue() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL);
        Document document = Document.builder()
            .documentLink(documentLinkMock)
            .documentType(DIGITAL_GRANT_REISSUE)
            .build();

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        DateFormat targetFormat = new SimpleDateFormat(DATE_FORMAT);
        String latestReissueDate = targetFormat.format(new Date());
        CallbackResponse callbackResponse = underTest.addDocuments(callbackRequestMock,
            Arrays.asList(document), null, null);
        assertEquals(latestReissueDate, callbackResponse.getData().getLatestGrantReissueDate());
    }

    @Test
    void shouldSetDateOfDeathType() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        Document document = Document.builder().documentType(DIGITAL_GRANT).build();

        CallbackResponse callbackResponse = underTest.paperForm(callbackRequestMock, document);
        assertEquals("diedOn", callbackResponse.getData().getDateOfDeathType());
    }

    @Test
    void shouldSetCodicilsNumberNullWhenWillHasCodicilsNo() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL);
        caseDataBuilder.willHasCodicils(NO);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertEquals(null, callbackResponse.getData().getWillNumberOfCodicils());
    }

    @Test
    void shouldSetDeceasedDeathCertificateNull() {
        caseDataBuilder.deceasedDeathCertificate(DECEASED_DEATH_CERTIFICATE);
        caseDataBuilder.deceasedDiedEngOrWales(NO);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertEquals(null, callbackResponse.getData().getDeceasedDeathCertificate());
    }

    @Test
    void shouldSetDeceasedForeignDeathCertTranslationNull() {
        caseDataBuilder.deceasedForeignDeathCertTranslation(YES);
        caseDataBuilder.deceasedForeignDeathCertInEnglish(YES);
        caseDataBuilder.deceasedDiedEngOrWales(NO);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertEquals(null, callbackResponse.getData().getDeceasedForeignDeathCertTranslation());
    }

    @Test
    void shouldSetDeceasedEnglishForeignDeathCertANDForeignDeathCertTranslationNull() {
        caseDataBuilder.deceasedForeignDeathCertInEnglish(NO);
        caseDataBuilder.deceasedForeignDeathCertTranslation(YES);
        caseDataBuilder.deceasedDiedEngOrWales(YES);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertEquals(null, callbackResponse.getData().getDeceasedForeignDeathCertInEnglish());
        assertEquals(null, callbackResponse.getData().getDeceasedForeignDeathCertTranslation());
    }

    @Test
    void shouldSetSOT() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        Document document = Document.builder().documentType(DIGITAL_GRANT).build();

        CallbackResponse callbackResponse = underTest.paperForm(callbackRequestMock, document);
        assertEquals("SOT.pdf", callbackResponse
            .getData().getStatementOfTruthDocument().getDocumentFilename());
    }

    @Test
    void shouldDefaultRequestInformationValues() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        CallbackResponse callbackResponse = underTest.defaultRequestInformationValues(callbackRequestMock);
        assertEquals("Yes", callbackResponse.getData().getBoEmailRequestInfoNotification());
        assertEquals("Yes", callbackResponse.getData().getBoRequestInfoSendToBulkPrint());
    }

    @Test
    void shouldAddInformationRequestDocumentsSentEmail() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL);

        Document document = Document.builder()
            .documentLink(documentLinkMock)
            .documentType(SENT_EMAIL)
            .documentFileName(SENT_EMAIL.getTemplateName())
            .build();

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        CallbackResponse callbackResponse = underTest.addInformationRequestDocuments(callbackRequestMock,
            Arrays.asList(document), Arrays.asList("123"));
        assertEquals(SENT_EMAIL.getTemplateName(),
            callbackResponse.getData().getProbateNotificationsGenerated().get(0).getValue().getDocumentFileName());
        assertEquals("Yes", callbackResponse.getData().getBoEmailRequestInfoNotificationRequested());
    }

    @Test
    void shouldAddInformationRequestDocumentsSOT() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL);

        Document document = Document.builder()
            .documentLink(documentLinkMock)
            .documentType(SOT_INFORMATION_REQUEST)
            .documentFileName(SOT_INFORMATION_REQUEST.getTemplateName())
            .build();

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        CallbackResponse callbackResponse = underTest.addInformationRequestDocuments(callbackRequestMock,
            Arrays.asList(document), Arrays.asList("123"));
        assertEquals(SOT_INFORMATION_REQUEST.getTemplateName(),
            callbackResponse.getData().getProbateDocumentsGenerated().get(0).getValue().getDocumentFileName());
        assertEquals("Yes", callbackResponse.getData().getBoEmailRequestInfoNotificationRequested());
    }

    @Test
    void shouldResolveStopCaseQA() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL)
            .resolveStopState(QA_CASE_STATE);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        CallbackResponse callbackResponse = underTest.resolveStop(callbackRequestMock);
        assertEquals(QA_CASE_STATE, callbackResponse.getData().getState());
    }

    @Test
    void shouldResolveStopCasePrinted() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL)
            .resolveStopState(CASE_PRINTED);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        CallbackResponse callbackResponse = underTest.resolveStop(callbackRequestMock);
        assertEquals(CASE_PRINTED, callbackResponse.getData().getState());
    }

    @Test
    void shouldResolveStopCaseReadyToIssue() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL)
            .resolveStopState(READY_FOR_ISSUE);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        CallbackResponse callbackResponse = underTest.resolveStop(callbackRequestMock);
        assertEquals(READY_FOR_ISSUE, callbackResponse.getData().getState());
    }

    @Test
    void shouldResolveStopCaseCaseMatchingIssueGrant() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL)
            .resolveStopState(CASE_MATCHING_ISSUE_GRANT);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        CallbackResponse callbackResponse = underTest.resolveStop(callbackRequestMock);
        assertEquals(CASE_MATCHING_ISSUE_GRANT, callbackResponse.getData().getState());
    }

    @Test
    void shouldResolveCaseWorkerEscalationStateBOCaseQA() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL)
                .resolveCaseWorkerEscalationState(QA_CASE_STATE);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        CallbackResponse callbackResponse = underTest.resolveCaseWorkerEscalationState(callbackRequestMock);
        assertEquals(QA_CASE_STATE, callbackResponse.getData().getState());
    }


    @Test
    void shouldChangeCaseStateBOCaseQA() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL)
                .transferToState(QA_CASE_STATE);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        CallbackResponse callbackResponse = underTest.transferToState(callbackRequestMock);
        assertEquals(QA_CASE_STATE, callbackResponse.getData().getState());
    }

    @Test
    void shouldChangeCaseStateCaseMatchingIssueGrant() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL)
                .transferToState(CASE_MATCHING_ISSUE_GRANT);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        CallbackResponse callbackResponse = underTest.transferToState(callbackRequestMock);
        assertEquals(CASE_MATCHING_ISSUE_GRANT, callbackResponse.getData().getState());
    }

    @Test
    void shouldTransformUniqueCode() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL)
                .uniqueProbateCodeId(uniqueCode);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(false);
        CallbackResponse callbackResponse = underTest.transformUniqueProbateCode(callbackRequestMock);
        assertEquals("CTS04052311043tpps8e9", callbackResponse.getData().getUniqueProbateCodeId());
    }

    @Test
    void shouldTransformDOB() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL)
                .deceasedDob("1889-03-31");;

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        CallbackResponse callbackResponse = underTest.changeDob(callbackRequestMock);
        assertEquals("1889-03-31", callbackResponse.getData().getDeceasedDateOfBirth());
    }

    @Test
    void shouldTransformValuesPageForFormId400() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL)
                .ihtFormId("IHT400")
                .hmrcLetterId(YES);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(false);
        CallbackResponse callbackResponse = underTest.transformValuesPage(callbackRequestMock);
        assertEquals(YES, callbackResponse.getData().getIht400Switch());
        assertEquals(NO, callbackResponse.getData().getIhtNetValueSwitch());
    }

    @Test
    void shouldTransformNetValueForFormEstate400() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL)
                .ihtFormEstateValuesCompleted(YES)
                .ihtFormEstate("IHT400")
                .hmrcLetterId(YES);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(true);
        CallbackResponse callbackResponse = underTest.transformValuesPage(callbackRequestMock);
        assertEquals(YES, callbackResponse.getData().getIht400Switch());
        assertEquals(NO, callbackResponse.getData().getIhtNetValueSwitch());
    }

    @Test
    void shouldTransformSwitchToNo() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL)
                .ihtFormEstateValuesCompleted(YES)
                .ihtFormEstate("IHT400")
                .hmrcLetterId(NO);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(true);
        CallbackResponse callbackResponse = underTest.transformValuesPage(callbackRequestMock);
        assertEquals(NO, callbackResponse.getData().getIht400Switch());
    }

    @Test
    void shouldTransformNetValueForEE() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL)
                .ihtFormEstateValuesCompleted(NO)
                .deceasedHadLateSpouseOrCivilPartner(YES);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(true);
        CallbackResponse callbackResponse = underTest.transformValuesPage(callbackRequestMock);
        assertEquals(YES, callbackResponse.getData().getIht400Switch());
        assertEquals(YES, callbackResponse.getData().getIhtNetValueSwitch());
    }

    @Test
    void shouldTransformNetValueForEEDeceasedSpouseNo() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL)
                .ihtFormEstateValuesCompleted(NO)
                .deceasedHadLateSpouseOrCivilPartner(NO);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(true);
        CallbackResponse callbackResponse = underTest.transformValuesPage(callbackRequestMock);
        assertEquals(YES, callbackResponse.getData().getIht400Switch());
    }

    @ParameterizedTest
    @MethodSource("invalidValue")
    void shouldTransformNetValueForOtherFormId(final String formId) {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL)
                .ihtFormId(formId);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(false);
        CallbackResponse callbackResponse = underTest.transformValuesPage(callbackRequestMock);
        assertEquals(YES, callbackResponse.getData().getIht400Switch());
        assertEquals(YES, callbackResponse.getData().getIhtNetValueSwitch());
    }

    @ParameterizedTest
    @MethodSource("invalidValue")
    void shouldTransformNetValueForOtherFormEstate(final String form) {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL)
                .ihtFormEstate(form);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(true);
        CallbackResponse callbackResponse = underTest.transformValuesPage(callbackRequestMock);
        assertEquals(YES, callbackResponse.getData().getIht400Switch());
        assertEquals(YES, callbackResponse.getData().getIhtNetValueSwitch());
    }

    private static Stream<String> invalidValue() {
        return Stream.of("IHT400421", "IHT205", "IHT207", "NA");
    }

    @Test
    void shouldTransformHandoffReason() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL)
                .lastModifiedDateForDormant(LocalDateTime.of(2024, 1, 1, 1,
                        1, 1, 1));

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        CallbackResponse callbackResponse = underTest.rollback(callbackRequestMock);
        assertNull(callbackResponse.getData().getLastModifiedDateForDormant());
    }

    @Test
    void shouldTransformCaseForLetter() {

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        CallbackResponse callbackResponse = underTest.transformCaseForLetter(callbackRequestMock);

        assertCommon(callbackResponse);
    }

    @Test
    void shouldTransformCaseForLetterWithDocument() {
        Document letter = Document.builder().documentType(DocumentType.ASSEMBLED_LETTER).build();

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        CallbackResponse callbackResponse =
            underTest.transformCaseForLetter(callbackRequestMock, Arrays.asList(letter), null);

        assertCommon(callbackResponse);
        assertEquals(EMPTY_LIST, callbackResponse.getData().getParagraphDetails());
        assertEquals(null, callbackResponse.getData().getPreviewLink());
        assertEquals(1, callbackResponse.getData().getProbateDocumentsGenerated().size());
        assertEquals(DocumentType.ASSEMBLED_LETTER, callbackResponse.getData().getProbateDocumentsGenerated()
            .get(0).getValue().getDocumentType());
    }

    @Test
    void shouldTransformCaseForLetterPreview() {
        Document letter = Document.builder().documentType(DocumentType.ASSEMBLED_LETTER).build();

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        CallbackResponse callbackResponse = underTest.transformCaseForLetterPreview(callbackRequestMock, letter);

        assertCommon(callbackResponse);
        assertEquals(YES, callbackResponse.getData().getBoEmailRequestInfoNotification());
    }

    @Test
    void shouldAddSOTToGeneratedDocuments() {
        Document document = Document.builder()
            .documentLink(documentLinkMock)
            .documentType(STATEMENT_OF_TRUTH)
            .build();

        CallbackResponse callbackResponse =
            underTest.addDocuments(callbackRequestMock, Arrays.asList(document), null, null);

        assertCommon(callbackResponse);
        assertLegacyInfo(callbackResponse);

        assertEquals(1, callbackResponse.getData().getProbateSotDocumentsGenerated().size());
    }

    @Test
    void testAddSotDocumentReturnsTransformedCaseWithDocAdded() {
        doAnswer(invoke -> {
            callbackRequestMock.getCaseDetails().getData().getProbateSotDocumentsGenerated()
                .add(new CollectionMember<>(SOT_DOC));
            assertEquals(SOT_DOC,
                callbackRequestMock.getCaseDetails().getData().getProbateSotDocumentsGenerated().get(0).getValue());
            return null;
        }).when(documentTransformer).addDocument(callbackRequestMock, SOT_DOC, false);
        underTest.addSOTDocument(callbackRequestMock, SOT_DOC);
    }

    @Test
    void shouldUpdateParentBuilderAttributes() {
        DynamicList reprintDocument =
            DynamicList.builder().value(DynamicListItem.builder().code("reprintDocument").build()).build();
        DynamicList solsAmendLegalStatmentSelect =
            DynamicList.builder().value(DynamicListItem.builder()
                .code("solsAmendLegalStatmentSelect").build()).build();

        caseDataBuilder
            .primaryApplicantForenames("PAFN")
            .reprintDocument(reprintDocument).reprintNumberOfCopies("1")
            .solsAmendLegalStatmentSelect(solsAmendLegalStatmentSelect)
            .ihtGrossValueField("1000").ihtNetValueField("900")
            .numberOfExecutors(1L).numberOfApplicants(2L)
            .legalDeclarationJson("legalDeclarationJson").checkAnswersSummaryJson("checkAnswersSummaryJson")
            .registryAddress("registryAddress").registryEmailAddress("registryEmailAddress")
            .registrySequenceNumber("registrySequenceNumber").iht217("Yes");

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);
        assertEquals("PAFN", callbackResponse.getData().getPrimaryApplicantForenames());
        assertEquals("reprintDocument", callbackResponse.getData().getReprintDocument().getValue().getCode());
        assertEquals("1", callbackResponse.getData().getReprintNumberOfCopies());
        assertEquals("solsAmendLegalStatmentSelect",
            callbackResponse.getData().getSolsAmendLegalStatmentSelect().getValue().getCode());
        assertEquals("1000", callbackResponse.getData().getIhtGrossValueField());
        assertEquals("900", callbackResponse.getData().getIhtNetValueField());
        assertEquals(Long.valueOf(1), callbackResponse.getData().getNumberOfExecutors());
        assertEquals(Long.valueOf(2), callbackResponse.getData().getNumberOfApplicants());
        assertEquals("legalDeclarationJson", callbackResponse.getData().getLegalDeclarationJson());
        assertEquals("checkAnswersSummaryJson", callbackResponse.getData().getCheckAnswersSummaryJson());
        assertEquals("registryAddress", callbackResponse.getData().getRegistryAddress());
        assertEquals("registryEmailAddress", callbackResponse.getData().getRegistryEmailAddress());
        assertEquals("registrySequenceNumber", callbackResponse.getData().getRegistrySequenceNumber());
        assertEquals("Yes", callbackResponse.getData().getIht217());
    }

    @Test
    void shouldApplySolicitorInfoAttributes() {
        caseDataBuilder
            .solsForenames("Solicitor Forename")
            .solsSurname("Solicitor Surname")
            .solsSolicitorWillSignSOT("Yes")
            .build();

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertEquals("Solicitor Forename", callbackResponse.getData().getSolsForenames());
        assertEquals("Solicitor Surname", callbackResponse.getData().getSolsSurname());
        assertEquals("Yes", callbackResponse.getData().getSolsSolicitorWillSignSOT());

    }

    @Test
    void shouldApplyTrustCorpAttributes() {
        CollectionMember<AdditionalExecutorTrustCorps> additionalExecutorTrustCorp = new CollectionMember<>(
            new AdditionalExecutorTrustCorps(
                "Executor forename",
                "Executor surname",
                "Solicitor"
            ));
        List<CollectionMember<AdditionalExecutorTrustCorps>> additionalExecutorsTrustCorpList = new ArrayList<>();
        additionalExecutorsTrustCorpList.add(additionalExecutorTrustCorp);

        SolsAddress trustCorpAddress = new SolsAddress(
            "Address Line 1",
            "",
            "",
            "",
            "",
            "POSTCODE",
            "");

        SolsAddress addressOfSucceededFirm = new SolsAddress(
            "Address Line 1",
            "",
            "",
            "",
            "",
            "POSTCODE",
            "");

        SolsAddress addressOfFirmNamedInWill = new SolsAddress(
            "Address Line 1",
            "",
            "",
            "",
            "",
            "POSTCODE",
            "");

        caseDataBuilder
            .dispenseWithNotice(YES)
            .titleAndClearingType("TCTTrustCorpResWithApp")
            .trustCorpName("Trust corp name")
            .trustCorpAddress(trustCorpAddress)
            .addressOfSucceededFirm(addressOfSucceededFirm)
            .addressOfFirmNamedInWill(addressOfFirmNamedInWill)
            .furtherEvidenceForApplication("Further evidence")
            .additionalExecutorsTrustCorpList(additionalExecutorsTrustCorpList)
            .lodgementAddress("London")
            .isSolThePrimaryApplicant("Yes")
            .lodgementDate(LocalDate.parse("2020-01-01", dateTimeFormatter));

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertEquals("Yes", callbackResponse.getData().getDispenseWithNotice());
        assertEquals("TCTTrustCorpResWithApp", callbackResponse.getData().getTitleAndClearingType());
        assertEquals("Yes", callbackResponse.getData().getDispenseWithNotice());
        assertEquals("Yes", callbackResponse.getData().getIsSolThePrimaryApplicant());
        assertEquals("Trust corp name", callbackResponse.getData().getTrustCorpName());
        assertEquals(trustCorpAddress, callbackResponse.getData().getTrustCorpAddress());
        assertEquals(addressOfSucceededFirm, callbackResponse.getData().getAddressOfSucceededFirm());
        assertEquals(addressOfFirmNamedInWill, callbackResponse.getData().getAddressOfFirmNamedInWill());
        assertEquals("Further evidence", callbackResponse.getData().getFurtherEvidenceForApplication());
        assertEquals(additionalExecutorsTrustCorpList, callbackResponse.getData()
            .getAdditionalExecutorsTrustCorpList());
        assertEquals("London", callbackResponse.getData().getLodgementAddress());
        assertEquals("2020-01-01", callbackResponse.getData().getLodgementDate());

    }

    @Test
    void shouldCallSolLSAmendTransformerGoP() throws JsonProcessingException {
        underTest.transformCaseForSolicitorLegalStatementRegeneration(callbackRequestMock);
        verify(solicitorLegalStatementNextStepsTransformer).transformLegalStatmentAmendStates(any(CaseDetails.class),
            any(ResponseCaseData.ResponseCaseDataBuilder.class));
    }

    @Test
    void checkSolsReviewCheckBoxesTextSingleExecSolApplying() {

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.solsSolicitorIsApplying(YES).build());

        List<CollectionMember<AdditionalExecutorApplying>> listOfApplyingExecs =
            solicitorExecutorTransformerMock.createCaseworkerApplyingList(caseDetailsMock.getData());

        String professionalName = caseDetailsMock.getData().getSolsSOTName();

        String executorNames = underTest.setExecutorNames(caseDetailsMock.getData(), listOfApplyingExecs,
            professionalName);

        assertEquals("The executor Andy Middlename Test: ", executorNames);
    }

    @Test
    void checkSolsReviewCheckBoxesTextSingleExecSolNotApplying() {

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        List<CollectionMember<AdditionalExecutorApplying>> listOfApplyingExecs =
            solicitorExecutorTransformerMock.createCaseworkerApplyingList(caseDetailsMock.getData());

        String professionalName = caseDetailsMock.getData().getSolsSOTName();

        String executorNames = underTest.setExecutorNames(caseDetailsMock.getData(), listOfApplyingExecs,
            professionalName);

        assertEquals("The executor applicant forename applicant surname: ", executorNames);
    }

    @Test
    void checkSolsReviewCheckBoxesTextMultiExecsSolApplying() {
        List<CollectionMember<AdditionalExecutorApplying>> additionalExecs = new ArrayList<>();
        AdditionalExecutorApplying additionalExecutorApplying = AdditionalExecutorApplying.builder()
            .applyingExecutorName(SOLICITOR_SOT_NAME).build();
        AdditionalExecutorApplying additionalExecutorApplyingSecond = AdditionalExecutorApplying.builder()
            .applyingExecutorName("James smith").build();

        additionalExecs.add(new CollectionMember<>(additionalExecutorApplying));
        additionalExecs.add(new CollectionMember<>(additionalExecutorApplyingSecond));
        caseDataBuilder.additionalExecutorsApplying(additionalExecs).build();

        CaseData caseData = caseDataBuilder.solsSolicitorIsApplying(YES).build();

        String professionalName = caseData.getSolsSOTName();

        String executorNames = underTest.setExecutorNames(caseData, additionalExecs, professionalName);

        assertEquals("The executors Andy Middlename Test, James Smith: ",
            executorNames);
    }

    @Test
    void checkSolsReviewCheckBoxesTextMultiExecsSolNotApplying() {
        List<CollectionMember<AdditionalExecutorApplying>> additionalExecs = new ArrayList<>();
        AdditionalExecutorApplying additionalExecutorApplyingSecond = AdditionalExecutorApplying.builder()
            .applyingExecutorName("James smith").build();

        additionalExecs.add(new CollectionMember<>(additionalExecutorApplyingSecond));
        caseDataBuilder.additionalExecutorsApplying(additionalExecs).build();

        CaseData caseData = caseDataBuilder.build();

        String professionalName = caseData.getSolsSOTName();

        String executorNames = underTest.setExecutorNames(caseData, additionalExecs, professionalName);

        assertEquals("The executors applicant forename applicant surname, James Smith: ",
            executorNames);
    }

    @Test
    void checkSolsReviewCheckBoxesTextMultiExecsSolNotApplyingPrimaryApplicantNotApplying() {
        List<CollectionMember<AdditionalExecutorApplying>> additionalExecs = new ArrayList<>();
        AdditionalExecutorApplying additionalExecutorApplyingSecond = AdditionalExecutorApplying.builder()
            .applyingExecutorName("James smith").build();

        additionalExecs.add(new CollectionMember<>(additionalExecutorApplyingSecond));
        caseDataBuilder.additionalExecutorsApplying(additionalExecs).build();

        CaseData caseData = caseDataBuilder.primaryApplicantIsApplying(NO).build();

        String professionalName = caseData.getSolsSOTName();

        String executorNames = underTest.setExecutorNames(caseData, additionalExecs, professionalName);

        assertEquals("The executor James Smith: ", executorNames);
    }

    @Test
    void checkSolsReviewCheckBoxesTextAdmonWill() {
        CaseData caseData = caseDataBuilder.solsWillType(WILL_TYPE_ADMON).build();
        String professionalName = caseData.getSolsSOTName();
        List<CollectionMember<AdditionalExecutorApplying>> listOfApplyingExecs =
            solicitorExecutorTransformerMock.createCaseworkerApplyingList(caseDetailsMock.getData());
        String applicantName = underTest.setExecutorNames(caseData, listOfApplyingExecs, professionalName);
        assertEquals("The applicant applicant forename applicant surname: ", applicantName);
    }

    @Test
    void checkSolsReviewCheckBoxesTextIntestacy() {
        CaseData caseData = caseDataBuilder.solsWillType(WILL_TYPE_INTESTACY).build();
        String professionalName = caseData.getSolsSOTName();
        List<CollectionMember<AdditionalExecutorApplying>> listOfApplyingExecs =
            solicitorExecutorTransformerMock.createCaseworkerApplyingList(caseDetailsMock.getData());
        String applicantName = underTest.setExecutorNames(caseData, listOfApplyingExecs, professionalName);
        assertEquals("The applicant applicant forename applicant surname: ", applicantName);
    }

    @Test
    void shouldCallSolLSAmendTransformerAdmon() throws JsonProcessingException {
        caseDataBuilder.solsWillType("WillLeftAnnexed");
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        underTest.transformCaseForSolicitorLegalStatementRegeneration(callbackRequestMock);
        verify(solicitorLegalStatementNextStepsTransformer).transformLegalStatmentAmendStates(any(CaseDetails.class),
            any(ResponseCaseData.ResponseCaseDataBuilder.class));
    }

    @Test
    void shouldCallReprintTransformer() {
        underTest.transformCaseForReprint(callbackRequestMock);
        verify(reprintTransformer)
            .transformReprintDocuments(any(CaseDetails.class), any(ResponseCaseData.ResponseCaseDataBuilder.class));
    }

    @Test
    void shouldAddBPInformationForGrantReprint() {
        Document document = Document.builder()
            .documentType(DIGITAL_GRANT)
            .build();
        String letterId = "letterId";
        String pdfSize = "10";
        CallbackResponse callbackResponse =
            underTest.addBulkPrintInformationForReprint(callbackRequestMock, document, letterId, pdfSize);

        assertThat(callbackResponse.getData().getBulkPrintSendLetterId(), is(letterId));
        assertThat(callbackResponse.getData().getBulkPrintPdfSize(), is(pdfSize));
    }

    @Test
    void shouldAddBPInformationForAdmonWillReprint() {
        Document document = Document.builder()
            .documentType(ADMON_WILL_GRANT)
            .build();
        String letterId = "letterId";
        String pdfSize = "10";
        CallbackResponse callbackResponse =
            underTest.addBulkPrintInformationForReprint(callbackRequestMock, document, letterId, pdfSize);

        assertThat(callbackResponse.getData().getBulkPrintSendLetterId(), is(letterId));
        assertThat(callbackResponse.getData().getBulkPrintPdfSize(), is(pdfSize));
    }

    @Test
    void shouldAddBPInformationForIntestacyReprint() {
        Document document = Document.builder()
            .documentType(INTESTACY_GRANT)
            .build();
        String letterId = "letterId";
        String pdfSize = "10";
        CallbackResponse callbackResponse =
            underTest.addBulkPrintInformationForReprint(callbackRequestMock, document, letterId, pdfSize);

        assertThat(callbackResponse.getData().getBulkPrintSendLetterId(), is(letterId));
        assertThat(callbackResponse.getData().getBulkPrintPdfSize(), is(pdfSize));
    }

    @Test
    void shouldAddBPInformationForWelshGrantReprint() {
        Document document = Document.builder()
            .documentType(WELSH_DIGITAL_GRANT)
            .build();
        String letterId = "letterId";
        String pdfSize = "10";
        CallbackResponse callbackResponse =
            underTest.addBulkPrintInformationForReprint(callbackRequestMock, document, letterId, pdfSize);

        assertThat(callbackResponse.getData().getBulkPrintSendLetterId(), is(letterId));
        assertThat(callbackResponse.getData().getBulkPrintPdfSize(), is(pdfSize));
    }

    @Test
    void shouldAddBPInformationForWelshAdmonWillReprint() {
        Document document = Document.builder()
            .documentType(WELSH_ADMON_WILL_GRANT)
            .build();
        String letterId = "letterId";
        String pdfSize = "10";
        CallbackResponse callbackResponse =
            underTest.addBulkPrintInformationForReprint(callbackRequestMock, document, letterId, pdfSize);

        assertThat(callbackResponse.getData().getBulkPrintSendLetterId(), is(letterId));
        assertThat(callbackResponse.getData().getBulkPrintPdfSize(), is(pdfSize));
    }

    @Test
    void shouldAddBPInformationForWelshIntestacyReprint() {
        Document document = Document.builder()
            .documentType(WELSH_INTESTACY_GRANT)
            .build();
        String letterId = "letterId";
        String pdfSize = "10";
        CallbackResponse callbackResponse =
            underTest.addBulkPrintInformationForReprint(callbackRequestMock, document, letterId, pdfSize);

        assertThat(callbackResponse.getData().getBulkPrintSendLetterId(), is(letterId));
        assertThat(callbackResponse.getData().getBulkPrintPdfSize(), is(pdfSize));
    }

    @Test
    void shouldAddBPInformationForGrantReissueReprint() {
        Document document = Document.builder()
            .documentType(DIGITAL_GRANT_REISSUE)
            .build();
        String letterId = "letterId";
        String pdfSize = "10";
        CallbackResponse callbackResponse =
            underTest.addBulkPrintInformationForReprint(callbackRequestMock, document, letterId, pdfSize);

        assertThat(callbackResponse.getData().getBulkPrintId().get(0).getValue().getSendLetterId(), is(letterId));
        assertThat(callbackResponse.getData().getBulkPrintId().get(0).getValue().getTemplateName(),
            is(DIGITAL_GRANT_REISSUE.getTemplateName()));
        assertEquals(null, callbackResponse.getData().getBulkPrintPdfSize());
    }

    @Test
    void shouldAddBPInformationForWelshGrantReissueReprint() {
        Document document = Document.builder()
            .documentType(WELSH_DIGITAL_GRANT_REISSUE)
            .build();
        String letterId = "letterId";
        String pdfSize = "10";
        doAnswer(invoke -> {
            return true;
        }).when(documentTransformer).hasDocumentWithType(any(List.class), any(DocumentType.class));
        CallbackResponse callbackResponse =
            underTest.addBulkPrintInformationForReprint(callbackRequestMock, document, letterId, pdfSize);

        assertThat(callbackResponse.getData().getBulkPrintId().get(0).getValue().getSendLetterId(), is(letterId));
        assertThat(callbackResponse.getData().getBulkPrintId().get(0).getValue().getTemplateName(),
            is(WELSH_DIGITAL_GRANT_REISSUE.getTemplateName()));
        assertThat(callbackResponse.getData().getBulkPrintPdfSize(), is(pdfSize));
    }

    @Test
    void shouldAddBPInformationForWelshAdmonWillReissueReprint() {
        Document document = Document.builder()
            .documentType(WELSH_ADMON_WILL_GRANT_REISSUE)
            .build();
        String letterId = "letterId";
        String pdfSize = "10";
        doAnswer(invoke -> {
            return true;
        }).when(documentTransformer).hasDocumentWithType(any(List.class), any(DocumentType.class));

        CallbackResponse callbackResponse =
            underTest.addBulkPrintInformationForReprint(callbackRequestMock, document, letterId, pdfSize);

        assertThat(callbackResponse.getData().getBulkPrintId().get(0).getValue().getSendLetterId(), is(letterId));
        assertThat(callbackResponse.getData().getBulkPrintId().get(0).getValue().getTemplateName(),
            is(WELSH_ADMON_WILL_GRANT_REISSUE.getTemplateName()));
        assertThat(callbackResponse.getData().getBulkPrintPdfSize(), is(pdfSize));
    }


    @Test
    void shouldAddBPInformationForWelshIntestacyReissueReprint() {
        Document document = Document.builder()
            .documentType(WELSH_INTESTACY_GRANT_REISSUE)
            .build();
        String letterId = "letterId";
        String pdfSize = "10";
        doAnswer(invoke -> {
            return true;
        }).when(documentTransformer).hasDocumentWithType(any(List.class), any(DocumentType.class));

        CallbackResponse callbackResponse =
            underTest.addBulkPrintInformationForReprint(callbackRequestMock, document, letterId, pdfSize);

        assertThat(callbackResponse.getData().getBulkPrintId().get(0).getValue().getSendLetterId(), is(letterId));
        assertThat(callbackResponse.getData().getBulkPrintId().get(0).getValue().getTemplateName(),
            is(WELSH_INTESTACY_GRANT_REISSUE.getTemplateName()));
        assertThat(callbackResponse.getData().getBulkPrintPdfSize(), is(pdfSize));
    }

    @Test
    void shouldNotAddBPInformationForWelshGrantReissueNoLetterIdReprint() {
        Document document = Document.builder()
            .documentType(WELSH_DIGITAL_GRANT_REISSUE)
            .build();
        List<Document> documents = Arrays.asList(document);
        doAnswer(invoke -> {
            return true;
        }).when(documentTransformer).hasDocumentWithType(documents, WELSH_DIGITAL_GRANT_REISSUE);
        CallbackResponse callbackResponse =
            underTest.addBulkPrintInformationForReprint(callbackRequestMock, document, null, "0");

        assertThat(callbackResponse.getData().getBulkPrintId().size(), is(0));
        assertNull(callbackResponse.getData().getBulkPrintPdfSize());
    }

    @Test
    void shouldNotAddBPInformationForWelshAdmonWillReissueNoLetterIdReprint() {
        Document document = Document.builder()
            .documentType(WELSH_ADMON_WILL_GRANT_REISSUE)
            .build();
        List<Document> documents = Arrays.asList(document);
        doAnswer(invoke -> {
            return true;
        }).when(documentTransformer).hasDocumentWithType(documents, WELSH_ADMON_WILL_GRANT_REISSUE);
        CallbackResponse callbackResponse =
            underTest.addBulkPrintInformationForReprint(callbackRequestMock, document, null, "0");

        assertThat(callbackResponse.getData().getBulkPrintId().size(), is(0));
        assertNull(callbackResponse.getData().getBulkPrintPdfSize());
    }

    @Test
    void shouldNotAddBPInformationForWelshIntestacyReissueNoLetterIdReprint() {
        Document document = Document.builder()
            .documentType(WELSH_INTESTACY_GRANT_REISSUE)
            .build();
        List<Document> documents = Arrays.asList(document);
        doAnswer(invoke -> {
            return true;
        }).when(documentTransformer).hasDocumentWithType(documents, WELSH_INTESTACY_GRANT_REISSUE);
        CallbackResponse callbackResponse =
            underTest.addBulkPrintInformationForReprint(callbackRequestMock, document, null, "0");

        assertThat(callbackResponse.getData().getBulkPrintId().size(), is(0));
        assertNull(callbackResponse.getData().getBulkPrintPdfSize());
    }

    @Test
    void shouldAddBPInformationForAdmonWillReissueReprint() {
        Document document = Document.builder()
            .documentType(ADMON_WILL_GRANT_REISSUE)
            .build();
        String letterId = "letterId";
        String pdfSize = "10";
        CallbackResponse callbackResponse =
            underTest.addBulkPrintInformationForReprint(callbackRequestMock, document, letterId, pdfSize);

        assertThat(callbackResponse.getData().getBulkPrintId().get(0).getValue().getSendLetterId(), is(letterId));
        assertThat(callbackResponse.getData().getBulkPrintId().get(0).getValue().getTemplateName(),
            is(ADMON_WILL_GRANT_REISSUE.getTemplateName()));
        assertEquals(null, callbackResponse.getData().getBulkPrintPdfSize());
    }

    @Test
    void shouldAddBPInformationForIntestacyReissueReprint() {
        Document document = Document.builder()
            .documentType(INTESTACY_GRANT_REISSUE)
            .build();
        String letterId = "letterId";
        String pdfSize = "10";
        CallbackResponse callbackResponse =
            underTest.addBulkPrintInformationForReprint(callbackRequestMock, document, letterId, pdfSize);

        assertThat(callbackResponse.getData().getBulkPrintId().get(0).getValue().getSendLetterId(), is(letterId));
        assertThat(callbackResponse.getData().getBulkPrintId().get(0).getValue().getTemplateName(),
            is(INTESTACY_GRANT_REISSUE.getTemplateName()));
        assertEquals(null, callbackResponse.getData().getBulkPrintPdfSize());
    }

    @Test
    void shouldAddBPInformationForWelshSOTReprint() {
        Document document = Document.builder()
            .documentType(WELSH_STATEMENT_OF_TRUTH)
            .build();
        String letterId = "letterId";
        String pdfSize = "10";
        CallbackResponse callbackResponse =
            underTest.addBulkPrintInformationForReprint(callbackRequestMock, document, letterId, pdfSize);

        assertThat(callbackResponse.getData().getBulkPrintId().get(0).getValue().getSendLetterId(), is(letterId));
        assertThat(callbackResponse.getData().getBulkPrintId().get(0).getValue().getTemplateName(),
            is(WELSH_STATEMENT_OF_TRUTH.getTemplateName()));
        assertEquals(null, callbackResponse.getData().getBulkPrintPdfSize());
    }

    @Test
    void shouldAddBPInformationForSOTReprint() {
        Document document = Document.builder()
            .documentType(STATEMENT_OF_TRUTH)
            .build();
        String letterId = "letterId";
        String pdfSize = "10";
        CallbackResponse callbackResponse =
            underTest.addBulkPrintInformationForReprint(callbackRequestMock, document, letterId, pdfSize);

        assertThat(callbackResponse.getData().getBulkPrintId().get(0).getValue().getSendLetterId(), is(letterId));
        assertThat(callbackResponse.getData().getBulkPrintId().get(0).getValue().getTemplateName(),
            is(STATEMENT_OF_TRUTH.getTemplateName()));
        assertEquals(null, callbackResponse.getData().getBulkPrintPdfSize());
    }

    @Test
    void shouldAddBPInformationForWillReprint() {
        Document document = Document.builder()
            .documentType(OTHER)
            .build();
        String letterId = "letterId";
        String pdfSize = "10";
        CallbackResponse callbackResponse =
            underTest.addBulkPrintInformationForReprint(callbackRequestMock, document, letterId, pdfSize);

        assertThat(callbackResponse.getData().getBulkPrintId().get(0).getValue().getSendLetterId(), is(letterId));
        assertThat(callbackResponse.getData().getBulkPrintId().get(0).getValue().getTemplateName(),
            is(OTHER.getTemplateName()));
        assertEquals(null, callbackResponse.getData().getBulkPrintPdfSize());
    }

    @Test
    void shouldSetIhtEstateValues() {
        underTest.defaultIhtEstateFromDateOfDeath(callbackRequestMock);
        verify(ihtEstateDefaulter).defaultPageFlowIhtSwitchDate(any(), any());
    }

    @Test
    void shouldSetIht400421PageFlow() {
        underTest.defaultIht400421DatePageFlow(callbackRequestMock);
        verify(iht400421Defaulter).defaultPageFlowForIht400421(any(), any());
    }

    private CollectionMember<ProbateAliasName> createdDeceasedAliasName(String id, String forename, String lastname,
                                                                        String onGrant) {
        ProbateAliasName pan = ProbateAliasName.builder()
            .appearOnGrant(onGrant)
            .forenames(forename)
            .lastName(lastname)
            .build();
        return new CollectionMember<>(id, pan);
    }

    private CollectionMember<UploadDocument> createUploadDocuments(String id) {
        DocumentLink docLink = DocumentLink.builder()
            .documentBinaryUrl("")
            .documentFilename("")
            .documentUrl("")
            .build();

        UploadDocument doc = UploadDocument.builder()
            .comment("comment")
            .documentLink(docLink)
            .documentType(DocumentType.IHT).build();
        return new CollectionMember<>(id, doc);
    }

    private CollectionMember<EstateItem> createEstateItems(String id) {
        EstateItem items = EstateItem.builder()
            .item("")
            .value("")
            .build();

        return new CollectionMember<>(id, items);
    }

    private CollectionMember<AdoptedRelative> createAdoptiveRelativeList(String id) {
        AdoptedRelative relatives = AdoptedRelative.builder()
            .adoptedInOrOut("IN")
            .name("Jane Doe")
            .relationship("Sister")
            .build();
        return new CollectionMember<>(id, relatives);
    }

    private CollectionMember<AttorneyApplyingOnBehalfOf> createAttorneyApplyingList(String id) {
        SolsAddress address = SolsAddress.builder()
            .addressLine1("")
            .addressLine2("")
            .addressLine3("")
            .postTown("")
            .postCode("")
            .county("")
            .country("")
            .build();

        AttorneyApplyingOnBehalfOf list = AttorneyApplyingOnBehalfOf.builder()
            .address(address)
            .name("")
            .build();
        return new CollectionMember<>(id, list);
    }

    private CollectionMember<AdditionalExecutor> createSolsAdditionalExecutor(String id, String applying,
                                                                              String reason) {
        AdditionalExecutor add1na = AdditionalExecutor.builder()
            .additionalApplying(applying)
            .additionalExecAddress(EXEC_ADDRESS)
            .additionalExecForenames(EXEC_FIRST_NAME)
            .additionalExecLastname(EXEC_SURNAME)
            .additionalExecReasonNotApplying(reason)
            .additionalExecAliasNameOnWill(ALIAS_FORENAME + " " + ALIAS_SURNAME)
            .build();
        return new CollectionMember<>(id, add1na);
    }

    private CollectionMember<AdditionalExecutorApplying> createAdditionalExecutorApplying(String id) {
        AdditionalExecutorApplying add1na = AdditionalExecutorApplying.builder()
            .applyingExecutorAddress(EXEC_ADDRESS)
            .applyingExecutorEmail(EXEC_EMAIL)
            .applyingExecutorName(EXEC_FIRST_NAME + " " + EXEC_SURNAME)
            .applyingExecutorOtherNames(ALIAS_FORENAME + " " + ALIAS_SURNAME)
            .applyingExecutorPhoneNumber(EXEC_PHONE)
            .applyingExecutorOtherNamesReason("Other")
            .applyingExecutorOtherReason("Married")
            .build();
        return new CollectionMember<>(id, add1na);
    }

    private void assertCommon(CallbackResponse callbackResponse) {
        assertCommonDetails(callbackResponse);
        assertCommonPayments(callbackResponse);
        assertLegacyInfo(callbackResponse);
        assertApplicationType(callbackResponse, ApplicationType.SOLICITOR);
        assertEquals(APPLICANT_HAS_ALIAS, callbackResponse.getData().getPrimaryApplicantHasAlias());
        assertEquals(OTHER_EXECS_EXIST, callbackResponse.getData().getOtherExecutorExists());
    }

    private void assertSolsDetails(CallbackResponse callbackResponse) {
        assertEquals(SOLICITOR_FIRM_NAME, callbackResponse.getData().getSolsSolicitorFirmName());
        assertEquals(SOLICITOR_FIRM_LINE1, callbackResponse.getData().getSolsSolicitorAddress().getAddressLine1());
        assertEquals(SOLICITOR_FIRM_POSTCODE, callbackResponse.getData().getSolsSolicitorAddress().getPostCode());
        assertEquals(SOLICITOR_FIRM_EMAIL, callbackResponse.getData().getSolsSolicitorEmail());
        assertEquals(SOLICITOR_FIRM_PHONE, callbackResponse.getData().getSolsSolicitorPhoneNumber());
        assertEquals(SOLICITOR_SOT_FORENAME + " " + SOLICITOR_SOT_SURNAME,
            callbackResponse.getData().getSolsSOTName());
        assertEquals(SOLICITOR_SOT_JOB_TITLE, callbackResponse.getData().getSolsSOTJobTitle());
        assertEquals(APP_REF, callbackResponse.getData().getSolsSolicitorAppReference());

    }

    private void assertCommonDetails(CallbackResponse callbackResponse) {
        assertEquals(REGISTRY_LOCATION, callbackResponse.getData().getRegistryLocation());

        assertEquals(DECEASED_FIRSTNAME, callbackResponse.getData().getDeceasedForenames());
        assertEquals(DECEASED_LASTNAME, callbackResponse.getData().getDeceasedSurname());
        assertEquals("2016-12-31", callbackResponse.getData().getDeceasedDateOfBirth());
        assertEquals("2017-12-31", callbackResponse.getData().getDeceasedDateOfDeath());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getWillNumberOfCodicils());

        assertEquals(IHT_FORM_ID, callbackResponse.getData().getIhtFormId());
        assertThat(new BigDecimal("10000"), comparesEqualTo(callbackResponse.getData().getIhtGrossValue()));
        assertThat(new BigDecimal("9000"), comparesEqualTo(callbackResponse.getData().getIhtNetValue()));
        assertThat(new BigDecimal("10000"),
            comparesEqualTo(callbackResponse.getData().getIhtEstateGrossValue()));
        assertThat(new BigDecimal("9000"), comparesEqualTo(callbackResponse.getData().getIhtEstateNetValue()));
        assertThat(new BigDecimal("9000"),
            comparesEqualTo(callbackResponse.getData().getIhtEstateNetQualifyingValue()));
        assertEquals("10000", callbackResponse.getData().getIhtEstateGrossValueField());
        assertEquals("9000", callbackResponse.getData().getIhtEstateNetValueField());
        assertEquals("9000", callbackResponse.getData().getIhtEstateNetQualifyingValueField());

        assertEquals(APPLICANT_FORENAME, callbackResponse.getData().getPrimaryApplicantForenames());
        assertEquals(APPLICANT_SURNAME, callbackResponse.getData().getPrimaryApplicantSurname());
        assertEquals(APPLICANT_EMAIL_ADDRESS, callbackResponse.getData().getPrimaryApplicantEmailAddress());
        assertEquals(PRIMARY_EXEC_APPLYING, callbackResponse.getData().getPrimaryApplicantIsApplying());
        assertEquals(PRIMARY_EXEC_ALIAS_NAMES, callbackResponse.getData().getPrimaryApplicantAlias());
        assertEquals(DECEASED_ADDRESS, callbackResponse.getData().getDeceasedAddress());
        assertEquals(EXEC_ADDRESS, callbackResponse.getData().getPrimaryApplicantAddress());
        assertEquals(ADDITIONAL_INFO, callbackResponse.getData().getSolsAdditionalInfo());

        assertEquals(BO_DOCS_RECEIVED, callbackResponse.getData().getBoEmailDocsReceivedNotificationRequested());
        assertEquals(BO_EMAIL_GRANT_ISSUED, callbackResponse.getData().getBoEmailGrantIssuedNotificationRequested());
        assertEquals(CASE_PRINT, callbackResponse.getData().getCasePrinted());
        assertEquals(CAVEAT_STOP_NOTIFICATION, callbackResponse.getData().getBoCaveatStopNotificationRequested());
        assertEquals(CAVEAT_STOP_NOTIFICATION, callbackResponse.getData().getBoCaveatStopEmailNotification());
        assertEquals(CASE_STOP_CAVEAT_ID, callbackResponse.getData().getBoCaseStopCaveatId());
        assertEquals(CAVEAT_STOP_EMAIL_NOTIFICATION,
            callbackResponse.getData().getBoCaveatStopEmailNotificationRequested());
        assertEquals(CAVEAT_STOP_SEND_TO_BULK_PRINT,
            callbackResponse.getData().getBoCaveatStopSendToBulkPrintRequested());
        assertEquals(STOP_REASONS_LIST, callbackResponse.getData().getBoCaseStopReasonList());
        assertEquals(STOP_DETAILS, callbackResponse.getData().getBoStopDetails());

        assertEquals(DECEASED_TITLE, callbackResponse.getData().getBoDeceasedTitle());
        assertEquals(DECEASED_HONOURS, callbackResponse.getData().getBoDeceasedHonours());

        assertEquals(WILL_MESSAGE, callbackResponse.getData().getBoWillMessage());
        assertEquals(EXECUTOR_LIMITATION, callbackResponse.getData().getBoExecutorLimitation());
        assertEquals(ADMIN_CLAUSE_LIMITATION, callbackResponse.getData().getBoAdminClauseLimitation());
        assertEquals(LIMITATION_TEXT, callbackResponse.getData().getBoLimitationText());

        assertEquals(IHT_REFERENCE, callbackResponse.getData().getIhtReferenceNumber());
        assertEquals(IHT_ONLINE, callbackResponse.getData().getIhtFormCompletedOnline());

        assertEquals(YES, callbackResponse.getData().getBoExaminationChecklistQ1());
        assertEquals(YES, callbackResponse.getData().getBoExaminationChecklistQ2());
        assertEquals(YES, callbackResponse.getData().getBoExaminationChecklistRequestQA());
        assertEquals(ORDER_NEEDED, callbackResponse.getData().getOrderNeeded());
        assertEquals(REISSUE_REASON, callbackResponse.getData().getReissueReason());
        assertEquals(REISSUE_DATE, callbackResponse.getData().getReissueDate());
        assertEquals(REISSUE_NOTATION, callbackResponse.getData().getReissueReasonNotation());

        assertEquals(SCANNED_DOCUMENTS_LIST, callbackResponse.getData().getScannedDocuments());
        assertEquals(YES, callbackResponse.getData().getBoStopDetailsDeclarationParagraph());
        assertEquals(YES, callbackResponse.getData().getBoEmailRequestInfoNotificationRequested());
        assertEquals(YES, callbackResponse.getData().getBoAssembleLetterSendToBulkPrintRequested());
        assertEquals(YES, callbackResponse.getData().getBoRequestInfoSendToBulkPrint());
        assertEquals(YES, callbackResponse.getData().getBoRequestInfoSendToBulkPrintRequested());
        assertEquals(EXECEUTORS_APPLYING_NOTIFICATION, callbackResponse
            .getData().getExecutorsApplyingNotifications());
        assertEquals(APPLICANT_SIBLINGS, callbackResponse.getData().getSolsApplicantSiblings());
        assertEquals(DIED_OR_NOT_APPLYING, callbackResponse.getData().getSolsDiedOrNotApplying());
        assertEquals(ENTITLED_MINORITY, callbackResponse.getData().getSolsEntitledMinority());
        assertEquals(LIFE_INTEREST, callbackResponse.getData().getSolsLifeInterest());
        assertEquals(RESIDUARY, callbackResponse.getData().getSolsResiduary());
        assertEquals(RESIDUARY_TYPE, callbackResponse.getData().getSolsResiduaryType());
        assertEquals(APP_REF, callbackResponse.getData().getPcqId());

        assertEquals(YES, callbackResponse.getData().getWillHasVisibleDamage());
        assertEquals(DAMAGE_TYPE_1, callbackResponse.getData().getWillDamage().getDamageTypesList().get(0));
        assertEquals(DAMAGE_TYPE_2, callbackResponse.getData().getWillDamage().getDamageTypesList().get(1));
        assertEquals(DAMAGE_TYPE_OTHER, callbackResponse.getData().getWillDamage().getDamageTypesList().get(2));
        assertEquals(DAMAGE_DESC, callbackResponse.getData().getWillDamage().getOtherDamageDescription());
        assertEquals(YES, callbackResponse.getData().getWillDamageReasonKnown());
        assertEquals(DAMAGE_REASON_DESC, callbackResponse.getData().getWillDamageReasonDescription());
        assertEquals(YES, callbackResponse.getData().getWillDamageCulpritKnown());
        assertEquals(DAMAGE_CULPRIT_FN, callbackResponse.getData().getWillDamageCulpritName().getFirstName());
        assertEquals(DAMAGE_CULPRIT_LN, callbackResponse.getData().getWillDamageCulpritName().getLastName());
        assertEquals(YES, callbackResponse.getData().getWillDamageDateKnown());
        assertEquals(DAMAGE_DATE, callbackResponse.getData().getWillDamageDate());

        assertEquals(YES, callbackResponse.getData().getCodicilsHasVisibleDamage());
        assertEquals(DAMAGE_TYPE_1, callbackResponse.getData().getCodicilsDamage().getDamageTypesList().get(0));
        assertEquals(DAMAGE_TYPE_2, callbackResponse.getData().getCodicilsDamage().getDamageTypesList().get(1));
        assertEquals(DAMAGE_TYPE_OTHER, callbackResponse.getData().getCodicilsDamage().getDamageTypesList().get(2));
        assertEquals(DAMAGE_DESC, callbackResponse.getData().getCodicilsDamage().getOtherDamageDescription());
        assertEquals(YES, callbackResponse.getData().getCodicilsDamageReasonKnown());
        assertEquals(DAMAGE_REASON_DESC, callbackResponse.getData().getCodicilsDamageReasonDescription());
        assertEquals(YES, callbackResponse.getData().getCodicilsDamageCulpritKnown());
        assertEquals(DAMAGE_CULPRIT_FN, callbackResponse.getData().getCodicilsDamageCulpritName().getFirstName());
        assertEquals(DAMAGE_CULPRIT_LN, callbackResponse.getData().getCodicilsDamageCulpritName().getLastName());
        assertEquals(YES, callbackResponse.getData().getCodicilsDamageDateKnown());
        assertEquals(DAMAGE_DATE, callbackResponse.getData().getCodicilsDamageDate());
        assertEquals(YES, callbackResponse.getData().getDeceasedWrittenWishes());
    }

    private void assertCommonPayments(CallbackResponse callbackResponse) {
        assertEquals(PAYMENTS_LIST, callbackResponse.getData().getPayments());
    }

    private void assertCommonAdditionalExecutors(CallbackResponse callbackResponse) {
        assertEquals(emptyList(), callbackResponse.getData().getSolsAdditionalExecutorList());
        assertEquals(emptyList(), callbackResponse.getData().getAdditionalExecutorsApplying());
        assertEquals(emptyList(), callbackResponse.getData().getAdditionalExecutorsNotApplying());
    }

    private void assertLegacyInfo(CallbackResponse callbackResponse) {
        assertEquals(RECORD_ID, callbackResponse.getData().getRecordId());
        assertEquals(LEGACY_CASE_TYPE, callbackResponse.getData().getLegacyType());
        assertEquals(LEGACY_CASE_URL, callbackResponse.getData().getLegacyCaseViewUrl());
    }

    private void assertApplicationType(CallbackResponse callbackResponse, ApplicationType applicationType) {
        assertEquals(applicationType, callbackResponse.getData().getApplicationType());
    }

    private void assertCommonPaperForm(CallbackResponse callbackResponse) {
        assertEquals(EXEC_PHONE, callbackResponse.getData().getPrimaryApplicantSecondPhoneNumber());
        assertEquals("other", callbackResponse.getData().getPrimaryApplicantRelationshipToDeceased());
        assertEquals("cousin", callbackResponse.getData().getPaRelationshipToDeceasedOther());
        assertEquals("neverMarried", callbackResponse.getData().getDeceasedMaritalStatus());

        assertEquals(YES, callbackResponse.getData().getWillDatedBeforeApril());
        assertEquals(NO, callbackResponse.getData().getDeceasedEnterMarriageOrCP());
        assertEquals(null, callbackResponse.getData().getDateOfMarriageOrCP());
        assertEquals(null, callbackResponse.getData().getDateOfDivorcedCPJudicially());
        assertEquals(YES, callbackResponse.getData().getWillsOutsideOfUK());
        assertEquals("Random Court Name", callbackResponse.getData().getCourtOfDecree());
        assertEquals(NO, callbackResponse.getData().getWillGiftUnderEighteen());
        assertEquals(YES, callbackResponse.getData().getApplyingAsAnAttorney());
        assertEquals(YES, callbackResponse.getData().getMentalCapacity());
        assertEquals(YES, callbackResponse.getData().getCourtOfProtection());
        assertEquals(NO, callbackResponse.getData().getEpaOrLpa());

        assertEquals(NO, callbackResponse.getData().getEpaRegistered());
        assertEquals("Spain", callbackResponse.getData().getDomicilityCountry());
        assertEquals(NO, callbackResponse.getData().getSpouseOrPartner());

        assertEquals(YES, callbackResponse.getData().getChildrenSurvived());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getChildrenOverEighteenSurvived());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getChildrenUnderEighteenSurvived());
        assertEquals(YES, callbackResponse.getData().getChildrenDied());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getChildrenDiedOverEighteen());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getChildrenDiedUnderEighteen());
        assertEquals(YES, callbackResponse.getData().getGrandChildrenSurvived());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getGrandChildrenSurvivedOverEighteen());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getGrandChildrenSurvivedUnderEighteen());
        assertEquals(YES, callbackResponse.getData().getParentsExistSurvived());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getParentsExistOverEighteenSurvived());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getParentsExistUnderEighteenSurvived());
        assertEquals(YES, callbackResponse.getData().getWholeBloodSiblingsSurvived());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getWholeBloodSiblingsSurvivedOverEighteen());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getWholeBloodSiblingsSurvivedUnderEighteen());
        assertEquals(YES, callbackResponse.getData().getWholeBloodSiblingsDied());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getWholeBloodSiblingsDiedOverEighteen());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getWholeBloodSiblingsDiedUnderEighteen());
        assertEquals(YES, callbackResponse.getData().getWholeBloodNeicesAndNephews());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getWholeBloodNeicesAndNephewsOverEighteen());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getWholeBloodNeicesAndNephewsUnderEighteen());
        assertEquals(YES, callbackResponse.getData().getHalfBloodSiblingsSurvived());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getHalfBloodSiblingsSurvivedOverEighteen());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getHalfBloodSiblingsSurvivedUnderEighteen());
        assertEquals(YES, callbackResponse.getData().getHalfBloodSiblingsDied());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getHalfBloodSiblingsDiedUnderEighteen());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getHalfBloodSiblingsDiedOverEighteen());
        assertEquals(YES, callbackResponse.getData().getHalfBloodNeicesAndNephews());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getHalfBloodNeicesAndNephewsUnderEighteen());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getHalfBloodNeicesAndNephewsOverEighteen());
        assertEquals(YES, callbackResponse.getData().getGrandparentsDied());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getGrandparentsDiedOverEighteen());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getGrandparentsDiedUnderEighteen());
        assertEquals(YES, callbackResponse.getData().getWholeBloodUnclesAndAuntsSurvived());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getWholeBloodUnclesAndAuntsSurvivedOverEighteen());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getWholeBloodUnclesAndAuntsSurvivedUnderEighteen());
        assertEquals(YES, callbackResponse.getData().getWholeBloodUnclesAndAuntsDied());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getWholeBloodUnclesAndAuntsDiedOverEighteen());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getWholeBloodUnclesAndAuntsDiedUnderEighteen());
        assertEquals(YES, callbackResponse.getData().getWholeBloodCousinsSurvived());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getWholeBloodCousinsSurvivedOverEighteen());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getWholeBloodCousinsSurvivedUnderEighteen());
        assertEquals(YES, callbackResponse.getData().getHalfBloodUnclesAndAuntsSurvived());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getHalfBloodUnclesAndAuntsSurvivedOverEighteen());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getHalfBloodUnclesAndAuntsSurvivedUnderEighteen());
        assertEquals(YES, callbackResponse.getData().getHalfBloodUnclesAndAuntsDied());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getHalfBloodUnclesAndAuntsSurvivedUnderEighteen());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getHalfBloodUnclesAndAuntsSurvivedOverEighteen());
        assertEquals(YES, callbackResponse.getData().getHalfBloodCousinsSurvived());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getHalfBloodCousinsSurvivedOverEighteen());
        assertEquals(NUM_CODICILS, callbackResponse.getData().getHalfBloodCousinsSurvivedUnderEighteen());

        assertEquals(YES, callbackResponse.getData().getPaperForm());
        assertEquals(IHT_REFERENCE, callbackResponse.getData().getPaymentReferenceNumberPaperform());
        assertEquals("debitOrCredit", callbackResponse.getData().getPaperPaymentMethod());
        assertEquals("0", callbackResponse.getData().getApplicationFeePaperForm());
        assertEquals("0", callbackResponse.getData().getFeeForCopiesPaperForm());
        assertEquals("0", callbackResponse.getData().getTotalFeePaperForm());
        assertEquals(YES, callbackResponse.getData().getAdopted());
        assertEquals(YES, callbackResponse.getData().getDomicilityIHTCert());
        assertEquals(YES, callbackResponse.getData().getEntitledToApply());
        assertEquals(YES, callbackResponse.getData().getEntitledToApplyOther());
        assertEquals(YES, callbackResponse.getData().getNotifiedApplicants());
        assertEquals(YES, callbackResponse.getData().getForeignAsset());
        assertEquals("123", callbackResponse.getData().getForeignAssetEstateValue());
        assertEquals(DECEASED_DATE_OF_DEATH_TYPE, callbackResponse.getData().getDateOfDeathType());

        assertEquals(DECEASED_DIVORCED_IN_ENGLAND_OR_WALES,
            callbackResponse.getData().getDeceasedDivorcedInEnglandOrWales());
        assertEquals(PRIMARY_APPLICANT_ADOPTION_IN_ENGLAND_OR_WALES,
            callbackResponse.getData().getPrimaryApplicantAdoptionInEnglandOrWales());
        assertEquals(DECEASED_SPOUSE_NOT_APPLYING_REASON,
            callbackResponse.getData().getDeceasedSpouseNotApplyingReason());
        assertEquals(DECEASED_OTHER_CHILDREN, callbackResponse.getData().getDeceasedOtherChildren());
        assertEquals(ALL_DECEASED_CHILDREN_OVER_EIGHTEEN,
            callbackResponse.getData().getAllDeceasedChildrenOverEighteen());
        assertEquals(ANY_DECEASED_GRANDCHILDREN_UNDER_EIGHTEEN,
            callbackResponse.getData().getAnyDeceasedGrandChildrenUnderEighteen());
        assertEquals(ANY_DECEASED_CHILDREN_DIE_BEFORE_DECEASED,
            callbackResponse.getData().getAnyDeceasedChildrenDieBeforeDeceased());
        assertEquals(DECEASED_ANY_CHILDREN, callbackResponse.getData().getDeceasedAnyChildren());
        assertEquals(DECEASED_HAS_ASSETS_OUTSIDE_UK, callbackResponse.getData().getDeceasedHasAssetsOutsideUK());
        assertEquals(GRANT_DELAYED_NOTIFICATION_SENT, callbackResponse.getData().getGrantDelayedNotificationSent());
        assertEquals(CallbackResponseTransformer.dateTimeFormatter.format(GRANT_DELAYED_DATE),
            callbackResponse.getData().getGrantDelayedNotificationDate());
        assertEquals(CallbackResponseTransformer.dateTimeFormatter.format(GRANT_STOPPED_DATE),
            callbackResponse.getData().getGrantStoppedDate());
    }

    @Test
    void bulkScanGrantOfRepresentationTransform() {
        CaseCreationDetails grantOfRepresentationDetails
            = underTest.bulkScanGrantOfRepresentationCaseTransform(bulkScanGrantOfRepresentationData);
        assertBulkScanCaseCreationDetails(grantOfRepresentationDetails);
    }



    @Test
    void bulkScanGrantOfRepresentationTransformSolsCaseWelsh() {
        bulkScanGrantOfRepresentationDataSols.setLanguagePreferenceWelsh(TRUE);
        CaseCreationDetails grantOfRepresentationDetails
            = underTest.bulkScanGrantOfRepresentationCaseTransform(bulkScanGrantOfRepresentationDataSols);
        GrantOfRepresentationData grantOfRepresentationData =
            (GrantOfRepresentationData) grantOfRepresentationDetails.getCaseData();
        assertEquals(RegistryLocation.CARDIFF,grantOfRepresentationData.getRegistryLocation());
        bulkScanGrantOfRepresentationDataSols.setLanguagePreferenceWelsh(FALSE);
    }

    @Test
    void shouldSetCorrectPrintIdForBulkScanGrantRaise() {
        List<Document> documents = new ArrayList<>();
        Document document = Document.builder()
            .documentLink(documentLinkMock)
            .documentType(DocumentType.GRANT_RAISED)
            .build();
        documents.add(0, document);
        String letterId = "123-456";
        CallbackResponse callbackResponse = underTest.grantRaised(callbackRequestMock, documents, letterId);

        assertCommon(callbackResponse);

        assertEquals("123-456", callbackResponse
            .getData().getBulkPrintId().get(0).getValue().getSendLetterId());
    }

    private void assertBulkScanCaseCreationDetails(CaseCreationDetails gorCreationDetails) {
        uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData
            grantOfRepresentationData =
            (uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData)
                gorCreationDetails
                    .getCaseData();
        assertEquals(GOR_EXCEPTION_RECORD_EVENT_ID, gorCreationDetails.getEventId());
        assertEquals(GOR_EXCEPTION_RECORD_CASE_TYPE_ID, gorCreationDetails.getCaseTypeId());
        assertEquals(BULK_SCAN_REGISTRY_LOCATION.name(), grantOfRepresentationData.getRegistryLocation().name());
        assertEquals(ApplicationType.PERSONAL.name(),
            grantOfRepresentationData.getApplicationType().getName().toUpperCase());

        assertEquals(TRUE, grantOfRepresentationData.getPaperForm());
        assertEquals(GrantType.INTESTACY, grantOfRepresentationData.getGrantType());

        assertEquals(DECEASED_FIRSTNAME, grantOfRepresentationData.getDeceasedForenames());
        assertEquals(DECEASED_LASTNAME, grantOfRepresentationData.getDeceasedSurname());
        assertEquals("2016-12-31", grantOfRepresentationData.getDeceasedDateOfBirth().toString());
        assertEquals("2017-12-31", grantOfRepresentationData.getDeceasedDateOfDeath().toString());
        assertEquals(Long.valueOf(NUM_CODICILS), grantOfRepresentationData.getWillNumberOfCodicils());

        assertEquals(IHT_FORM_ID, grantOfRepresentationData.getIhtFormId().getDescription());
        assertThat(Long.valueOf("10000"), comparesEqualTo(grantOfRepresentationData.getIhtGrossValue()));
        assertThat(Long.valueOf("9000"), comparesEqualTo(grantOfRepresentationData.getIhtNetValue()));

        assertEquals(APPLICANT_FORENAME, grantOfRepresentationData.getPrimaryApplicantForenames());
        assertEquals(APPLICANT_SURNAME, grantOfRepresentationData.getPrimaryApplicantSurname());
        assertEquals(APPLICANT_EMAIL_ADDRESS, grantOfRepresentationData.getPrimaryApplicantEmailAddress());
        assertEquals(BooleanUtils.toBoolean(PRIMARY_EXEC_APPLYING),
            grantOfRepresentationData.getPrimaryApplicantIsApplying());
        assertEquals(PRIMARY_EXEC_ALIAS_NAMES, grantOfRepresentationData.getPrimaryApplicantAlias());
        assertEquals(BSP_DECEASED_ADDRESS, grantOfRepresentationData.getDeceasedAddress());
        assertEquals(BSP_APPLICANT_ADDRESS, grantOfRepresentationData.getPrimaryApplicantAddress());

        assertEquals(IHT_REFERENCE, grantOfRepresentationData.getIhtReferenceNumber());
        assertEquals(BooleanUtils.toBoolean(IHT_ONLINE), grantOfRepresentationData.getIhtFormCompletedOnline());

        assertEquals(BSP_SCANNED_DOCUMENTS_LIST, grantOfRepresentationData.getScannedDocuments());
        assertEquals(Boolean.FALSE, grantOfRepresentationData.getBoEmailRequestInfoNotificationRequested());

        assertEquals(EXEC_PHONE, grantOfRepresentationData.getPrimaryApplicantSecondPhoneNumber());
        assertEquals(Relationship.OTHER, grantOfRepresentationData.getPrimaryApplicantRelationshipToDeceased());
        assertEquals("cousin", grantOfRepresentationData.getPaRelationshipToDeceasedOther());
        assertEquals(MaritalStatus.NEVER_MARRIED, grantOfRepresentationData.getDeceasedMaritalStatus());

        assertEquals(null, grantOfRepresentationData.getDateOfMarriageOrCP());
        assertEquals(null, grantOfRepresentationData.getDateOfDivorcedCPJudicially());
        assertEquals(TRUE, grantOfRepresentationData.getWillsOutsideOfUK());
        assertEquals("Random Court Name", grantOfRepresentationData.getCourtOfDecree());
        assertEquals(Boolean.FALSE, grantOfRepresentationData.getWillGiftUnderEighteen());
        assertEquals(TRUE, grantOfRepresentationData.getApplyingAsAnAttorney());
        assertEquals(TRUE, grantOfRepresentationData.getMentalCapacity());
        assertEquals(TRUE, grantOfRepresentationData.getCourtOfProtection());
        assertEquals(Boolean.FALSE, grantOfRepresentationData.getEpaOrLpa());

        assertEquals(Boolean.FALSE, grantOfRepresentationData.getEpaRegistered());
        assertEquals("Spain", grantOfRepresentationData.getDomicilityCountry());

        assertEquals(EXEC_PHONE, grantOfRepresentationData.getPrimaryApplicantSecondPhoneNumber());
        assertEquals(Relationship.OTHER, grantOfRepresentationData.getPrimaryApplicantRelationshipToDeceased());
        assertEquals("cousin", grantOfRepresentationData.getPaRelationshipToDeceasedOther());
        assertEquals(MaritalStatus.NEVER_MARRIED, grantOfRepresentationData.getDeceasedMaritalStatus());
        assertEquals(null, grantOfRepresentationData.getDateOfMarriageOrCP());
        assertEquals(null, grantOfRepresentationData.getDateOfDivorcedCPJudicially());
        assertEquals(TRUE, grantOfRepresentationData.getWillsOutsideOfUK());
        assertEquals("Random Court Name", grantOfRepresentationData.getCourtOfDecree());
        assertEquals(Boolean.FALSE, grantOfRepresentationData.getWillGiftUnderEighteen());
        assertEquals(TRUE, grantOfRepresentationData.getApplyingAsAnAttorney());
        assertEquals(null, grantOfRepresentationData.getAttorneyOnBehalfOfNameAndAddress());
        assertEquals(TRUE, grantOfRepresentationData.getMentalCapacity());
        assertEquals(TRUE, grantOfRepresentationData.getCourtOfProtection());
        assertEquals(Boolean.FALSE, grantOfRepresentationData.getEpaOrLpa());
        assertEquals(Boolean.FALSE, grantOfRepresentationData.getEpaRegistered());
        assertEquals("Spain", grantOfRepresentationData.getDomicilityCountry());
        assertEquals(TRUE, grantOfRepresentationData.getAdopted());
        assertEquals(null, grantOfRepresentationData.getAdoptiveRelatives());
        assertEquals(TRUE, grantOfRepresentationData.getDomicilityIHTCert());
        assertEquals(TRUE, grantOfRepresentationData.getForeignAsset());
        assertEquals(Long.valueOf("123"), grantOfRepresentationData.getForeignAssetEstateValue());

        assertEquals(TRUE, grantOfRepresentationData.getChildrenSurvived());
        assertEquals(NUM_CODICILS, grantOfRepresentationData.getChildrenOverEighteenSurvivedText());
        assertEquals(NUM_CODICILS, grantOfRepresentationData.getChildrenUnderEighteenSurvivedText());
        assertEquals(TRUE, grantOfRepresentationData.getChildrenDied());
        assertEquals(NUM_CODICILS, grantOfRepresentationData.getChildrenDiedOverEighteenText());
        assertEquals(NUM_CODICILS, grantOfRepresentationData.getChildrenDiedUnderEighteenText());
        assertEquals(TRUE, grantOfRepresentationData.getGrandChildrenSurvived());
        assertEquals(NUM_CODICILS, grantOfRepresentationData.getGrandChildrenSurvivedOverEighteenText());
        assertEquals(NUM_CODICILS, grantOfRepresentationData.getGrandChildrenSurvivedUnderEighteenText());
        assertEquals(TRUE, grantOfRepresentationData.getParentsExistSurvived());
        assertEquals(NUM_CODICILS, grantOfRepresentationData.getParentsExistOverEighteenSurvived());
        assertEquals(NUM_CODICILS, grantOfRepresentationData.getParentsExistUnderEighteenSurvived());
        assertEquals(TRUE, grantOfRepresentationData.getWholeBloodSiblingsSurvived());
        assertEquals(NUM_CODICILS, grantOfRepresentationData.getWholeBloodSiblingsSurvivedOverEighteen());
        assertEquals(NUM_CODICILS, grantOfRepresentationData.getWholeBloodSiblingsSurvivedUnderEighteen());
        assertEquals(TRUE, grantOfRepresentationData.getWholeBloodSiblingsDied());
        assertEquals(NUM_CODICILS, grantOfRepresentationData.getWholeBloodSiblingsDiedOverEighteen());
        assertEquals(NUM_CODICILS, grantOfRepresentationData.getWholeBloodSiblingsDiedUnderEighteen());
        assertEquals(TRUE, grantOfRepresentationData.getWholeBloodNeicesAndNephews());
        assertEquals(NUM_CODICILS, grantOfRepresentationData.getWholeBloodNeicesAndNephewsOverEighteen());
        assertEquals(NUM_CODICILS, grantOfRepresentationData.getWholeBloodNeicesAndNephewsUnderEighteen());
        assertEquals(TRUE, grantOfRepresentationData.getHalfBloodSiblingsSurvived());
        assertEquals(NUM_CODICILS, grantOfRepresentationData.getHalfBloodSiblingsSurvivedOverEighteen());
        assertEquals(NUM_CODICILS, grantOfRepresentationData.getHalfBloodSiblingsSurvivedUnderEighteen());
        assertEquals(TRUE, grantOfRepresentationData.getHalfBloodSiblingsDied());
        assertEquals(NUM_CODICILS, grantOfRepresentationData.getHalfBloodSiblingsDiedUnderEighteen());
        assertEquals(NUM_CODICILS, grantOfRepresentationData.getHalfBloodSiblingsDiedOverEighteen());
        assertEquals(TRUE, grantOfRepresentationData.getHalfBloodNeicesAndNephews());
        assertEquals(NUM_CODICILS, grantOfRepresentationData.getHalfBloodNeicesAndNephewsUnderEighteen());
        assertEquals(NUM_CODICILS, grantOfRepresentationData.getHalfBloodNeicesAndNephewsOverEighteen());
        assertEquals(TRUE, grantOfRepresentationData.getGrandparentsDied());
        assertEquals(NUM_CODICILS, grantOfRepresentationData.getGrandparentsDiedOverEighteen());
        assertEquals(NUM_CODICILS, grantOfRepresentationData.getGrandparentsDiedUnderEighteen());
        assertEquals(TRUE, grantOfRepresentationData.getWholeBloodUnclesAndAuntsSurvived());
        assertEquals(NUM_CODICILS, grantOfRepresentationData.getWholeBloodUnclesAndAuntsSurvivedOverEighteen());
        assertEquals(NUM_CODICILS, grantOfRepresentationData.getWholeBloodUnclesAndAuntsSurvivedUnderEighteen());
        assertEquals(TRUE, grantOfRepresentationData.getWholeBloodUnclesAndAuntsDied());
        assertEquals(NUM_CODICILS, grantOfRepresentationData.getWholeBloodUnclesAndAuntsDiedOverEighteen());
        assertEquals(NUM_CODICILS, grantOfRepresentationData.getWholeBloodUnclesAndAuntsDiedUnderEighteen());
        assertEquals(TRUE, grantOfRepresentationData.getWholeBloodCousinsSurvived());
        assertEquals(NUM_CODICILS, grantOfRepresentationData.getWholeBloodCousinsSurvivedOverEighteen());
        assertEquals(NUM_CODICILS, grantOfRepresentationData.getWholeBloodCousinsSurvivedUnderEighteen());
        assertEquals(TRUE, grantOfRepresentationData.getHalfBloodUnclesAndAuntsSurvived());
        assertEquals(NUM_CODICILS, grantOfRepresentationData.getHalfBloodUnclesAndAuntsSurvivedOverEighteen());
        assertEquals(NUM_CODICILS, grantOfRepresentationData.getHalfBloodUnclesAndAuntsSurvivedUnderEighteen());
        assertEquals(TRUE, grantOfRepresentationData.getHalfBloodUnclesAndAuntsDied());
        assertEquals(NUM_CODICILS, grantOfRepresentationData.getHalfBloodUnclesAndAuntsSurvivedUnderEighteen());
        assertEquals(NUM_CODICILS, grantOfRepresentationData.getHalfBloodUnclesAndAuntsSurvivedOverEighteen());
        assertEquals(TRUE, grantOfRepresentationData.getHalfBloodCousinsSurvived());
        assertEquals(NUM_CODICILS, grantOfRepresentationData.getHalfBloodCousinsSurvivedOverEighteen());
        assertEquals(NUM_CODICILS, grantOfRepresentationData.getHalfBloodCousinsSurvivedUnderEighteen());

        assertEquals(IHT_REFERENCE, grantOfRepresentationData.getPaymentReferenceNumberPaperform());
        assertEquals("debitOrCredit", grantOfRepresentationData.getPaperPaymentMethod());
        assertEquals(Long.valueOf("0"), grantOfRepresentationData.getApplicationFeePaperForm());
        assertEquals(Long.valueOf("0"), grantOfRepresentationData.getFeeForCopiesPaperForm());
        assertEquals(Long.valueOf("0"), grantOfRepresentationData.getTotalFeePaperForm());

        assertEquals(BULK_SCAN_REFERENCE, grantOfRepresentationData.getBulkScanCaseReference());
        assertEquals(BULK_SCAN_ENVELOPES, grantOfRepresentationData.getBulkScanEnvelopes());

        assertEquals(TRUE, grantOfRepresentationData.getGrantDelayedNotificationSent());
        assertEquals(GRANT_DELAYED_DATE, grantOfRepresentationData.getGrantDelayedNotificationDate());
        assertEquals(GRANT_STOPPED_DATE, grantOfRepresentationData.getGrantStoppedDate());

        assertEquals(Boolean.FALSE, grantOfRepresentationData.getEvidenceHandled());
    }

    @Test
    void shouldInvokeGenerateTaskList() {
        CaseData.CaseDataBuilder caseDataBuilder = CaseData.builder();

        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED_STR, 1L);
        caseDetails.setState("CaseCreated");

        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        underTest.transformWithConditionalStateChange(callbackRequest, Optional.of("Examining"));
        verify(taskListUpdateService, times(1)).generateTaskList(any(), any());

    }

    @Test
    void shouldTransformCaseForSolicitorWithProbateAndSetWillAndCodicilDates() {
        caseDataBuilder.applicationType(ApplicationType.SOLICITOR);
        caseDataBuilder.solsWillType(WILL_TYPE_PROBATE);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertEquals(VALID_ORIGINAL_WILL_SIGNED_DATE, callbackResponse.getData().getOriginalWillSignedDate());
        assertEquals(VALID_CODICIL_DATE, callbackResponse.getData().getCodicilAddedDateList()
            .get(0).getValue().getDateCodicilAdded());
    }

    @Test
    void shouldTransformCaseForSolicitorWithProbateNoWillAndWillReason() {
        caseDataBuilder.applicationType(ApplicationType.SOLICITOR);
        caseDataBuilder.solsWillType(WILL_TYPE_PROBATE);
        caseDataBuilder.willAccessOriginal(NO);
        caseDataBuilder.noOriginalWillAccessReason(NO_ACCESS_WILL_REASON);
        caseDataBuilder.willHasCodicils(NO);
        caseDataBuilder.willAccessNotarial(YES);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertEquals(NO, callbackResponse.getData().getWillAccessOriginal());
        assertEquals(NO_ACCESS_WILL_REASON, callbackResponse.getData().getNoOriginalWillAccessReason());
        assertEquals(NO, callbackResponse.getData().getWillHasCodicils());
        assertEquals(YES, callbackResponse.getData().getWillAccessNotarial());
    }

    @Test
    void shouldGetSolicitorPaperGOPApplicationWithDocumentPaperFormNo() {
        caseDataBuilder.applicationType(ApplicationType.SOLICITOR);
        caseDataBuilder.caseType(GRANT_OF_PROBATE_NAME);
        caseDataBuilder.solsWillType(WILL_TYPE_PROBATE);
        caseDataBuilder.solsWillType(WILL_TYPE_PROBATE);
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        Document document = Document.builder().documentType(DIGITAL_GRANT).build();

        CallbackResponse callbackResponse = underTest.paperForm(callbackRequestMock, document);
        assertEquals(1, callbackResponse.getData().getProbateDocumentsGenerated().size());
        assertEquals(CASE_TYPE_GRANT_OF_PROBATE, callbackResponse.getData().getCaseType());

        assertCommonDetails(callbackResponse);
        assertLegacyInfo(callbackResponse);
        verify(caseDataTransformerMock, times(1))
            .transformForSolicitorApplicationCompletion(callbackRequestMock);
    }

    @Test
    void shouldTransformForDeceasedDetails() {
        caseDataBuilder.applicationType(SOLICITOR)
            .caseType(GRANT_OF_PROBATE_NAME)
            .solsWillType(WILL_TYPE_PROBATE)
            .solsSOTForenames("Fred")
            .solsSOTSurname("Bassett")
            .solsSOTName("Fred Bassett")
            .solsSolicitorIsExec("Yes")
            .solsSolicitorIsApplying("Yes");
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse =
            underTest.transformForDeceasedDetails(callbackRequestMock, CHANGED_STATE);

        assertCommon(callbackResponse);
        assertLegacyInfo(callbackResponse);

        assertTrue(CHANGED_STATE.isPresent());
        assertEquals(CHANGED_STATE.get(), callbackResponse.getData().getState());
        verify(solicitorExecutorTransformerMock, times(1))
            .mapSolicitorExecutorFieldsToExecutorNamesLists(any(), any());
    }

    @Test
    void testBuildOrganisationPolicyValues() {

        OrganisationEntityResponse organisationEntityResponse = new OrganisationEntityResponse();
        organisationEntityResponse.setOrganisationIdentifier(ORG_ID);
        organisationEntityResponse.setName(ORGANISATION_NAME);

        when(organisationsRetrievalService.getOrganisationEntity(anyString(), anyString()))
            .thenReturn(organisationEntityResponse);
        OrganisationPolicy organisationPolicy = mock(OrganisationPolicy.class);
        when(organisationPolicy.getOrgPolicyCaseAssignedRole()).thenReturn("Org Policy Case Assigned Role");
        when(organisationPolicy.getOrgPolicyReference()).thenReturn("Org Policy Reference");

        CaseData caseData = CaseData.builder().applicantOrganisationPolicy(organisationPolicy)
                .applicationType(APPLICATION_TYPE.SOLICITOR)
            .build();
        when(caseDetailsMock.getData()).thenReturn(caseData);
        OrganisationPolicy actualBuildOrganisationPolicyResult = this.underTest
            .buildOrganisationPolicy(caseDetailsMock, "ABC123");
        assertEquals("Org Policy Case Assigned Role",
            actualBuildOrganisationPolicyResult.getOrgPolicyCaseAssignedRole());
        assertEquals("Org Policy Reference", actualBuildOrganisationPolicyResult.getOrgPolicyReference());
        Organisation organisationResult = actualBuildOrganisationPolicyResult.getOrganisation();
        assertEquals(ORGANISATION_NAME, organisationResult.getOrganisationName());
        assertEquals(ORG_ID, organisationResult.getOrganisationID());
        verify(this.organisationsRetrievalService).getOrganisationEntity(anyString(), anyString());
        verify(organisationPolicy).getOrgPolicyCaseAssignedRole();
        verify(organisationPolicy).getOrgPolicyReference();
    }

    @Test
    void shouldWipeCodicilAddedDateForNoCodicil() {
        caseDataBuilder.willHasCodicils(NO);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertNull(callbackResponse.getData().getCodicilAddedDateList());
    }

    @Test
    void shouldWipeCodicilAddedDateForNoCodicilDoTransform() {
        caseDataBuilder.willHasCodicils(NO);
        caseDataBuilder.recordId(null);
        caseDataBuilder.paperForm(NO);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCase(callbackRequestMock);

        assertNull(callbackResponse.getData().getCodicilAddedDateList());
    }

    @Test
    void shouldBeNullSafeForSentEmailAndCoversheet() {
        CallbackResponse callbackResponse = underTest.transformForSolicitorComplete(callbackRequestMock,
            feesResponse, null, null, USER_ID);

        assertThat(callbackResponse.getData().getProbateNotificationsGenerated(), is(empty()));
        assertNull(callbackResponse.getData().getSolsCoversheetDocument());
    }

    @Test
    void shouldTransformCaseForSentEmailAndCoversheet() {
        when(coversheetMock.getDocumentLink()).thenReturn(documentLinkMock);
        CallbackResponse callbackResponse = underTest.transformForSolicitorComplete(callbackRequestMock,
                feesResponse, SENTEMAIL, coversheetMock, USER_ID);

        assertEquals(1, callbackResponse.getData().getProbateNotificationsGenerated().size());
        assertEquals(SENTEMAIL,
                callbackResponse.getData().getProbateNotificationsGenerated().get(0).getValue());
        assertEquals(SENT_EMAIL.getTemplateName(),
                callbackResponse.getData().getProbateNotificationsGenerated().get(0).getValue().getDocumentFileName());
        assertEquals(documentLinkMock, callbackResponse.getData().getSolsCoversheetDocument());
    }

    @Test
    void shouldTransformCaseForAttachScannedDocsWithSentEmail() {

        caseDataBuilder.applicationType(ApplicationType.PERSONAL);
        caseDataBuilder.scannedDocuments(SCANNED_DOCUMENTS_LIST);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        Document sentEmail = Document.builder()
            .documentLink(documentLinkMock)
            .documentType(SENT_EMAIL)
            .documentFileName(SENT_EMAIL.getTemplateName())
            .build();

        CallbackResponse callbackResponse = underTest.transformCaseForAttachScannedDocs(callbackRequestMock, sentEmail);
        assertEquals(1, callbackResponse.getData().getScannedDocuments().size());
        assertEquals(SCANNED_DOCUMENTS_LIST, callbackResponse.getData().getScannedDocuments());
        assertEquals(1, callbackResponse.getData().getProbateNotificationsGenerated().size());
        assertEquals(sentEmail,
            callbackResponse.getData().getProbateNotificationsGenerated().get(0).getValue());
        assertEquals(SENT_EMAIL.getTemplateName(),
            callbackResponse.getData().getProbateNotificationsGenerated().get(0).getValue().getDocumentFileName());
    }

    @Test
    void shouldTransformCaseForAttachScannedDocsWithoutSentEmail() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL);
        caseDataBuilder.scannedDocuments(SCANNED_DOCUMENTS_LIST);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCaseForAttachScannedDocs(callbackRequestMock, null);
        assertEquals(1, callbackResponse.getData().getScannedDocuments().size());
        assertEquals(SCANNED_DOCUMENTS_LIST, callbackResponse.getData().getScannedDocuments());
        assertEquals(0, callbackResponse.getData().getProbateNotificationsGenerated().size());
    }

    @Test
    void shouldTransformCaseWithRegistrarDirections() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        List<CollectionMember<RegistrarDirection>> directions = new ArrayList<>();
        CollectionMember<RegistrarDirection> registrarDirectionCollectionMember1 = new CollectionMember<>(null,
                RegistrarDirection.builder()
                        .addedDateTime(LocalDateTime.parse("2023-01-01T23:45:45.890Z", formatter))
                        .decision("Decision 1")
                        .furtherInformation("Further information 1")
                        .build());
        CollectionMember<RegistrarDirection> registrarDirectionCollectionMember2 = new CollectionMember<>(null,
                RegistrarDirection.builder()
                        .addedDateTime(LocalDateTime.parse("2023-01-02T23:45:45.890Z", formatter))
                        .decision("Decision 2")
                        .build());

        directions.add(registrarDirectionCollectionMember1);
        directions.add(registrarDirectionCollectionMember2);

        caseDataBuilder.registrarDirections(directions);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transformCaseWithRegistrarDirection(callbackRequestMock);
        assertEquals(2, callbackResponse.getData().getRegistrarDirections().size());
        assertEquals("2023-01-01T23:45:45.890Z", format(formatter, callbackResponse.getData(), 0));
        assertEquals("Decision 1", callbackResponse.getData().getRegistrarDirections().get(0).getValue().getDecision());
        assertEquals("Further information 1", callbackResponse.getData().getRegistrarDirections().get(0).getValue()
                .getFurtherInformation());
        assertEquals("2023-01-02T23:45:45.890Z", format(formatter, callbackResponse.getData(), 1));
        assertEquals("Decision 2", callbackResponse.getData().getRegistrarDirections().get(1).getValue().getDecision());
        assertNull(callbackResponse.getData().getRegistrarDirections().get(1).getValue().getFurtherInformation());

        assertNotNull(callbackResponse.getData().getRegistrarDirectionToAdd());
        assertNull(callbackResponse.getData().getRegistrarDirectionToAdd().getAddedDateTime());
        assertNull(callbackResponse.getData().getRegistrarDirectionToAdd().getDecision());
        assertNull(callbackResponse.getData().getRegistrarDirectionToAdd().getFurtherInformation());
    }

    @Test
    void shouldSetupDocumentsForRemoval() {

        List<CollectionMember<Document>> generated = Arrays.asList(new CollectionMember("1",
                Document.builder().build()));
        List<CollectionMember<ScannedDocument>> scanned = Arrays.asList(new CollectionMember("2",
                ScannedDocument.builder().build()));
        List<CollectionMember<UploadDocument>> uploaded = Arrays.asList(new CollectionMember("3",
                UploadDocument.builder().build()));

        caseDataBuilder.probateDocumentsGenerated(generated);
        caseDataBuilder.scannedDocuments(scanned);
        caseDataBuilder.boDocumentsUploaded(uploaded);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse response = underTest.setupOriginalDocumentsForRemoval(callbackRequestMock);
        assertEquals("1", response.getData().getOriginalDocuments().getOriginalDocsGenerated().get(0).getId());
        assertEquals("2", response.getData().getOriginalDocuments().getOriginalDocsScanned().get(0).getId());
        assertEquals("3", response.getData().getOriginalDocuments().getOriginalDocsUploaded().get(0).getId());
    }

    @Test
    void shouldTransformPersonalCaseForUpdateTaskList() {
        CaseData caseData = caseDataBuilder
                .applicationType(PERSONAL)
                .build();
        when(caseDetailsMock.getData()).thenReturn(caseData);
        CallbackResponse callbackResponse = underTest.updateTaskList(callbackRequestMock);
        assertEquals("Yes", callbackResponse.getData().getBoEmailDocsReceivedNotification());

        caseData = caseDataBuilder
                .primaryApplicantEmailAddress(null)
                .build();
        when(caseDetailsMock.getData()).thenReturn(caseData);
        callbackResponse = underTest.updateTaskList(callbackRequestMock);
        assertEquals("No", callbackResponse.getData().getBoEmailDocsReceivedNotification());
    }

    @Test
    void shouldTransformSolicitorCaseForUpdateTaskList() {
        CaseData caseData = caseDataBuilder
                .build();
        when(caseDetailsMock.getData()).thenReturn(caseData);
        CallbackResponse callbackResponse = underTest.updateTaskList(callbackRequestMock);
        assertEquals("Yes", callbackResponse.getData().getBoEmailDocsReceivedNotification());

        caseData = caseDataBuilder
                .solsSolicitorEmail(null)
                .build();
        when(caseDetailsMock.getData()).thenReturn(caseData);
        callbackResponse = underTest.updateTaskList(callbackRequestMock);
        assertEquals("No", callbackResponse.getData().getBoEmailDocsReceivedNotification());
    }

    @Test
    void shouldTransformForFormNetValue() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL)
                .ihtFormNetValue(IHT_NET)
                .ihtFormId("IHT400")
                .ihtNetValue(null);
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(false);

        CallbackResponse callbackResponse = underTest.transform(callbackRequestMock);
        assertEquals(IHT_NET, callbackResponse.getData().getIhtNetValue());
    }

    @Test
    void shouldTransformNoForFormNetValue() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL)
                .ihtFormId("IHT400")
                .ihtNetValue(IHT_NET)
                .ihtFormNetValue(null);
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(false);

        CallbackResponse callbackResponse = underTest.transform(callbackRequestMock);
        assertEquals(IHT_NET, callbackResponse.getData().getIhtNetValue());
    }

    @Test
    void shouldTransformForFormEstateAndNoFormNetValue() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL)
                .ihtFormEstate("IHT400")
                .ihtNetValue(IHT_NET)
                .ihtFormNetValue(null);
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(true);

        CallbackResponse callbackResponse = underTest.transform(callbackRequestMock);
        assertEquals(IHT_NET, callbackResponse.getData().getIhtNetValue());
    }

    @Test
    void shouldTransformNoFormNetValueDifferentFormId() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL)
                .ihtFormId("IHT205")
                .ihtFormEstate("IHT400")
                .ihtNetValue(IHT_NET)
                .ihtFormNetValue(null);
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(false);

        CallbackResponse callbackResponse = underTest.transform(callbackRequestMock);
        assertEquals(IHT_NET, callbackResponse.getData().getIhtNetValue());
    }

    @SuppressWarnings("unchecked")
    @Test
    void shouldTransformWithHandOffReason() {
        List<CollectionMember<HandoffReason>> reason = new ArrayList();
        reason.add(new CollectionMember<>(null, HandoffReason.builder().caseHandoffReason("Reason").build()));
        caseDataBuilder.applicationType(ApplicationType.PERSONAL)
                .boHandoffReasonList(reason)
                .caseHandedOffToLegacySite("Yes");
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transform(callbackRequestMock);
        assertEquals(reason, callbackResponse.getData().getBoHandoffReasonList());
    }

    @SuppressWarnings("unchecked")
    @Test
    void shouldReturnNullForEmptyHandOffReason() {
        List<CollectionMember<HandoffReason>> reason = new ArrayList();
        caseDataBuilder.applicationType(ApplicationType.PERSONAL)
                .boHandoffReasonList(reason);
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transform(callbackRequestMock);
        assertThat(callbackResponse.getData().getBoHandoffReasonList(), empty());
    }

    @Test
    void shouldReturnNullWhenHandOffSiteIsNo() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL)
                .caseHandedOffToLegacySite("No");
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transform(callbackRequestMock);
        assertThat(callbackResponse.getData().getBoHandoffReasonList(), empty());
    }

    @Test
    void shouldReturnDateWhenEventIsNotMatched() {
        caseDataBuilder.applicationType(ApplicationType.PERSONAL);
        when(callbackRequestMock.getEventId()).thenReturn("eventId");
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        CallbackResponse callbackResponse = underTest.transform(callbackRequestMock);
        assertNotNull(callbackResponse.getData().getLastModifiedDateForDormant());
    }

    private String format(DateTimeFormatter formatter, ResponseCaseData caseData, int ind) {
        return formatter.format(caseData.getRegistrarDirections().get(ind).getValue().getAddedDateTime());
    }
}
