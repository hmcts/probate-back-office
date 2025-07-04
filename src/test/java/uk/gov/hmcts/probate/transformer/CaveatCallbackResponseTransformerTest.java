package uk.gov.hmcts.probate.transformer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.Constants;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.caseaccess.Organisation;
import uk.gov.hmcts.probate.model.caseaccess.OrganisationPolicy;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.ProbateAddress;
import uk.gov.hmcts.probate.model.ccd.ProbateFullAliasName;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.caveat.response.CaveatCallbackResponse;
import uk.gov.hmcts.probate.model.ccd.caveat.response.ResponseCaveatData;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicList;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicListItem;
import uk.gov.hmcts.probate.model.ccd.raw.OriginalDocuments;
import uk.gov.hmcts.probate.model.ccd.raw.RegistrarDirection;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.ccd.raw.UploadDocument;
import uk.gov.hmcts.probate.model.ccd.raw.response.AuditEvent;
import uk.gov.hmcts.probate.model.exceptionrecord.CaseCreationDetails;
import uk.gov.hmcts.probate.model.payments.PaymentResponse;
import uk.gov.hmcts.probate.model.payments.pba.OrganisationEntityResponse;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.ccd.AuditEventService;
import uk.gov.hmcts.probate.service.organisations.OrganisationsRetrievalService;
import uk.gov.hmcts.reform.probate.model.BulkScanEnvelope;
import uk.gov.hmcts.reform.probate.model.cases.Address;
import uk.gov.hmcts.reform.probate.model.cases.RegistryLocation;
import uk.gov.hmcts.reform.probate.model.cases.caveat.ProbateFee;
import uk.gov.hmcts.reform.probate.model.cases.caveat.ProbateFeeNotIncludedReason;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.ApplicationType.SOLICITOR;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.reform.probate.model.cases.ApplicationType.SOLICITORS;

@ContextConfiguration(classes = {CaveatCallbackResponseTransformer.class})
@ExtendWith(MockitoExtension.class)
class CaveatCallbackResponseTransformerTest {

    public static final String ORGANISATION_NAME = "OrganisationName";
    public static final String ORG_ID = "OrgID";
    private static final String REPRESENTATIVE_NAME = "Representative Name";
    private static final String DX_NUMBER = "1234567890";
    private static final String POLICY_ROLE_APPLICANT_SOLICITOR = "[APPLICANTSOLICITOR]";
    private List<CaseMatch> caseMatches = new ArrayList<>();

    @Mock
    private OrganisationsRetrievalService organisationsRetrievalService;

    private static final DateTimeFormatter dateTimeFormatter = CaveatCallbackResponseTransformer.dateTimeFormatter;

    private static final ApplicationType CAV_APPLICATION_TYPE =
        CaveatCallbackResponseTransformer.DEFAULT_APPLICATION_TYPE;
    private static final ApplicationType CAV_APPLICATION_TYPE_SOLS = SOLICITOR;
    private static final String CAV_REGISTRY_LOCATION = CaveatCallbackResponseTransformer.DEFAULT_REGISTRY_LOCATION;
    private static final RegistryLocation BULK_SCAN_CAV_REGISTRY_LOCATION
        = CaveatCallbackResponseTransformer.EXCEPTION_RECORD_REGISTRY_LOCATION;

    private static final String CAV_EXCEPTION_RECORD_CASE_TYPE_ID =
        CaveatCallbackResponseTransformer.EXCEPTION_RECORD_CASE_TYPE_ID;
    private static final String CAV_EXCEPTION_RECORD_EVENT_ID =
        CaveatCallbackResponseTransformer.EXCEPTION_RECORD_EVENT_ID;

    private static final String YES = "Yes";

    private static final String CAV_DECEASED_FORENAMES = "Deceased_fn";
    private static final String CAV_DECEASED_SURNAME = "Deceased_ln";
    private static final LocalDate CAV_DECEASED_DOD = LocalDate.parse("2017-12-31", dateTimeFormatter);
    private static final LocalDate CAV_DECEASED_DOB = LocalDate.parse("2016-12-31", dateTimeFormatter);
    private static final String DATE_SUBMITTED = dateTimeFormatter.format(LocalDate.now());
    private static final String CAV_DECEASED_HAS_ALIAS = YES;
    private static final String CAV_DECEASED_FULL_ALIAS_NAME = "AliasFN AliasSN";
    private static final List<CollectionMember<ProbateFullAliasName>> CAV_DECEASED_FULL_ALIAS_NAME_LIST = emptyList();
    private static final ProbateAddress CAV_DECEASED_ADDRESS = Mockito.mock(ProbateAddress.class);
    private static final Address CAV_BSP_DECEASED_ADDRESS = Mockito.mock(Address.class);

    private static final String CAV_CAVEATOR_FORENAMES = "Caveator_fn";
    private static final String CAV_CAVEATOR_SURNAME = "Caveator_ln";
    private static final String CAV_CAVEATOR_EMAIL_ADDRESS = "caveator@probate-test.com";
    private static final ProbateAddress CAV_CAVEATOR_ADDRESS = Mockito.mock(ProbateAddress.class);
    private static final Address CAV_BSP_CAVEATOR_ADDRESS = Mockito.mock(Address.class);

    private static final String CAV_SOLICITOR_FIRMNAME = "The Firm";
    private static final String CAV_SOLICITOR_PHONENUMBER = "07070707077";
    private static final String CAV_SOLICITOR_APP_REFERENCE = "REF";

    private static final LocalDate CAV_SUBMISSION_DATE = LocalDate.now();
    private static final String CAV_FORMATTED_SUBMISSION_DATE = dateTimeFormatter.format(CAV_SUBMISSION_DATE);
    private static final String CAV_AUTO_EXPIRED = "Yes";

    private static final String CAV_FORMATTED_EXPIRY_DATE = "2020-02-01";
    private static final LocalDate CAV_EXPIRY_DATE = LocalDate.of(2020, 2, 1);

    private static final String CAV_MESSAGE_CONTENT = "";
    private static final String CAV_REOPEN_REASON = "";

    private static final String CAV_RECORD_ID = "12345";
    private static final String CAV_LEGACY_CASE_URL = "someUrl";
    private static final String CAV_LEGACY_CASE_TYPE = "someCaseType";

    private static final String SOLS_PAYMENT_METHOD = "fee account";
    private static final String SOLS_FEE_ACC = "1234";
    private static final String SOLS_SELECTED_PBA = "PBA1234";
    private static final String SOLS_PBA_PAY_REF = "PBA1234-AAA";
    private static final String CAV_SOLS_REGISTRY_LOCATION = "ctsc";
    private static final String BULK_SCAN_REFERENCE = "BulkScanRef";
    private static final List<uk.gov.hmcts.reform.probate.model.cases.CollectionMember<BulkScanEnvelope>>
        BULK_SCAN_ENVELOPES = new ArrayList<>();
    private static final String CAV_CAVEATOR_PHONENUMBER = "123456789";
    private static final String CAV_PROBATE_FEE = "probateFeeNotIncluded";
    private static final String CAV_PROBATE_FEE_NOT_INCLUDED_REASON = "helpWithFeesApplied";
    private static final String CAV_HELP_WITH_FEES_REFERENCE = "Free Text for fees reference";
    private static final String CAV_NOT_INCLUDED_EXPLANATION = "helpWithFeesApplying";
    private static final String CAV_FEE_ACCOUNT_NUMBER = "Free Text for fee account number";
    private static final String CAV_FEE_ACCOUNT_REFERENCE = "Free Text for account reference";
    private static final String CAV_PAYMENT_TAKEN = "Yes";

    @InjectMocks
    private CaveatCallbackResponseTransformer underTest;

    @Mock
    private CaveatCallbackRequest caveatCallbackRequestMock;

    @Mock
    private PaymentResponse paymentResponseMock;

    @Mock
    private Document document;

    @Mock
    private DocumentLink documentLinkMock;

    @Mock
    private CaveatDetails caveatDetailsMock;

    @Mock
    private SolicitorPaymentReferenceDefaulter solicitorPBADefaulterMock;

    @Mock
    private SecurityUtils securityUtils;

    @Mock
    private SecurityDTO securityDTO;

    @Mock
    private AuditEventService auditEventService;

    @Spy
    private DocumentTransformer documentTransformer;

    private CaveatData.CaveatDataBuilder caveatDataBuilder;

    private uk.gov.hmcts.reform.probate.model.cases.caveat.CaveatData bulkScanCaveatData;


    @BeforeEach
    public void setup() {
        caveatDataBuilder = CaveatData.builder()
            .deceasedForenames(CAV_DECEASED_FORENAMES)
            .deceasedSurname(CAV_DECEASED_SURNAME)
            .deceasedDateOfDeath(CAV_DECEASED_DOD)
            .deceasedDateOfBirth(CAV_DECEASED_DOB)
            .deceasedAnyOtherNames(CAV_DECEASED_HAS_ALIAS)
            .deceasedFullAliasNameList(CAV_DECEASED_FULL_ALIAS_NAME_LIST)
            .deceasedAddress(CAV_DECEASED_ADDRESS)
            .caveatorForenames(CAV_CAVEATOR_FORENAMES)
            .caveatorSurname(CAV_CAVEATOR_SURNAME)
            .caveatorEmailAddress(CAV_CAVEATOR_EMAIL_ADDRESS)
            .caveatorAddress(CAV_CAVEATOR_ADDRESS)
            .solsSolicitorFirmName(CAV_SOLICITOR_FIRMNAME)
            .solsSolicitorPhoneNumber(CAV_SOLICITOR_PHONENUMBER)
            .solsSolicitorAppReference(CAV_SOLICITOR_APP_REFERENCE)
            .expiryDate(CAV_EXPIRY_DATE)
            .messageContent(CAV_MESSAGE_CONTENT)
            .caveatReopenReason(CAV_REOPEN_REASON)
            .recordId(CAV_RECORD_ID)
            .legacyCaseViewUrl(CAV_LEGACY_CASE_URL)
            .applicationSubmittedDate(CAV_SUBMISSION_DATE)
            .autoClosedExpiry(CAV_AUTO_EXPIRED)
            .paperForm(YES)
            .legacyType(CAV_LEGACY_CASE_TYPE)
            .solsPaymentMethods(SOLS_PAYMENT_METHOD)
            .solsFeeAccountNumber(SOLS_FEE_ACC)
            .solsPBANumber(DynamicList.builder()
                .value(DynamicListItem.builder().code(SOLS_SELECTED_PBA).label(SOLS_SELECTED_PBA).build())
                .build())
            .solsPBAPaymentReference(SOLS_PBA_PAY_REF)
            .pcqId(CAV_SOLICITOR_APP_REFERENCE)
            .caveatorPhoneNumber(CAV_CAVEATOR_PHONENUMBER)
            .probateFee(CAV_PROBATE_FEE)
            .helpWithFeesReference(CAV_HELP_WITH_FEES_REFERENCE)
            .probateFeeNotIncludedReason(CAV_PROBATE_FEE_NOT_INCLUDED_REASON)
            .probateFeeNotIncludedExplanation(CAV_NOT_INCLUDED_EXPLANATION)
            .probateFeeAccountNumber(CAV_FEE_ACCOUNT_NUMBER)
            .probateFeeAccountReference(CAV_FEE_ACCOUNT_REFERENCE)
            .paymentTaken(CAV_PAYMENT_TAKEN);


        bulkScanCaveatData = uk.gov.hmcts.reform.probate.model.cases.caveat.CaveatData.builder()
            .registryLocation(BULK_SCAN_CAV_REGISTRY_LOCATION)
            .deceasedForenames(CAV_DECEASED_FORENAMES)
            .deceasedSurname(CAV_DECEASED_SURNAME)
            .deceasedDateOfDeath(CAV_DECEASED_DOD)
            .deceasedDateOfBirth(CAV_DECEASED_DOB)
            .deceasedAnyOtherNames(false)
            .deceasedAddress(CAV_BSP_DECEASED_ADDRESS)
            .caveatorForenames(CAV_CAVEATOR_FORENAMES)
            .caveatorSurname(CAV_CAVEATOR_SURNAME)
            .caveatorEmailAddress(CAV_CAVEATOR_EMAIL_ADDRESS)
            .caveatorAddress(CAV_BSP_CAVEATOR_ADDRESS)
            .applicationSubmittedDate(CAV_SUBMISSION_DATE)
            .bulkScanCaseReference(BULK_SCAN_REFERENCE)
            .bulkScanEnvelopes(BULK_SCAN_ENVELOPES)
            .caveatorPhoneNumber(CAV_CAVEATOR_PHONENUMBER)
            .probateFee(ProbateFee.PROBATE_FEE_NOT_INCLUDED)
            .helpWithFeesReference(CAV_HELP_WITH_FEES_REFERENCE)
            .probateFeeNotIncludedReason(ProbateFeeNotIncludedReason.HELP_WITH_FEES_APPLIED)
            .probateFeeNotIncludedExplanation(CAV_NOT_INCLUDED_EXPLANATION)
            .probateFeeAccountNumber(CAV_FEE_ACCOUNT_NUMBER)
            .probateFeeAccountReference(CAV_FEE_ACCOUNT_REFERENCE)
            .solsSolicitorRepresentativeName(REPRESENTATIVE_NAME)
            .dxNumber(DX_NUMBER)
            .practitionerAcceptsServiceByEmail(true)
            .build();
    }

    private void setupMocks() {
        when(caveatCallbackRequestMock.getCaseDetails()).thenReturn(caveatDetailsMock);
        when(caveatDetailsMock.getData()).thenReturn(caveatDataBuilder.build());

    }

    @Test
    void shouldTransformSolsCaveatCallbackRequestToCaveatCallbackResponse() {
        setupMocks();
        CaveatCallbackResponse caveatCallbackResponse = underTest.transformForSolicitor(caveatCallbackRequestMock);
        assertCommonSolsCaveats(caveatCallbackResponse);
    }

    @Test
    void shouldTransformSolsCaveatCallbackRequestToCaveatCallbackResponseWithAuth() {
        setupMocks();
        CaveatCallbackResponse caveatCallbackResponse =
            underTest.transformForSolicitor(caveatCallbackRequestMock, "FAKE_TOKEN");
        assertCommonSolsCaveats(caveatCallbackResponse);
    }

    @Test
    void shouldConvertRequestToDataBeanWithCaveatEntryDateChangeWithServiceRequest() {
        setupMocks();
        List<Document> documents = new ArrayList<>();
        Document document = Document.builder()
            .documentLink(documentLinkMock)
            .documentType(DocumentType.CAVEAT_RAISED)
            .build();
        documents.add(0, document);
        caveatDataBuilder.applicationType(SOLICITOR);
        when(caveatDetailsMock.getData()).thenReturn(caveatDataBuilder.build());
        String letterId = "123-456";

        CaveatCallbackResponse caveatCallbackResponse =
            underTest.caveatRaised(caveatCallbackRequestMock, documents, letterId);

        assertCommonDetails(caveatCallbackResponse);
        assertApplicationType(caveatCallbackResponse, CAV_APPLICATION_TYPE_SOLS);
        assertPaperForm(caveatCallbackResponse, YES);

        assertEquals(CAV_PAYMENT_TAKEN, caveatCallbackResponse.getCaveatData().getPaymentTaken());
    }

    @Test
    void shouldConvertRequestToDataBeanWithCaveatEntryDateChange() {
        setupMocks();
        List<Document> documents = new ArrayList<>();
        Document document = Document.builder()
            .documentLink(documentLinkMock)
            .documentType(DocumentType.CAVEAT_RAISED)
            .build();
        documents.add(0, document);
        String letterId = "123-456";
        CaveatCallbackResponse caveatCallbackResponse =
            underTest.caveatRaised(caveatCallbackRequestMock, documents, letterId);

        assertCommon(caveatCallbackResponse);

        assertEquals(CAV_FORMATTED_SUBMISSION_DATE,
            caveatCallbackResponse.getCaveatData().getApplicationSubmittedDate());
    }

    @Test
    void shouldConvertRequestToDataBeanWithCaveatExpiryDateChange() {
        setupMocks();
        List<Document> documents = new ArrayList<>();
        Document document = Document.builder()
            .documentLink(documentLinkMock)
            .documentType(DocumentType.CAVEAT_RAISED)
            .build();
        documents.add(0, document);
        caveatDataBuilder.applicationSubmittedDate(null);
        when(caveatDetailsMock.getData()).thenReturn(caveatDataBuilder.build());
        String letterId = null;
        CaveatCallbackResponse caveatCallbackResponse =
            underTest.caveatRaised(caveatCallbackRequestMock, documents, letterId);

        assertCommon(caveatCallbackResponse);

        assertEquals(CAV_FORMATTED_SUBMISSION_DATE,
            caveatCallbackResponse.getCaveatData().getApplicationSubmittedDate());
        assertEquals(CAV_FORMATTED_EXPIRY_DATE, caveatCallbackResponse.getCaveatData().getExpiryDate());
    }

    @Test
    void shouldConvertRequestToDataBeanWithBulkPrintId() {
        setupMocks();
        List<Document> documents = new ArrayList<>();
        Document document = Document.builder()
            .documentLink(documentLinkMock)
            .documentType(DocumentType.CAVEAT_RAISED)
            .build();
        documents.add(0, document);
        String letterId = "123-456";
        CaveatCallbackResponse caveatCallbackResponse =
            underTest.caveatRaised(caveatCallbackRequestMock, documents, letterId);

        assertCommon(caveatCallbackResponse);

        assertEquals("123-456",
            caveatCallbackResponse.getCaveatData().getBulkPrintId().get(0).getValue().getSendLetterId());
    }

    @Test
    void shouldKeepApplicationSubmittedDateWhenNotNullCaveatRaised() {
        setupMocks();
        LocalDate newSubmittedDate = LocalDate.now().minusDays(5);
        caveatDataBuilder.applicationSubmittedDate(newSubmittedDate);
        when(caveatDetailsMock.getData()).thenReturn(caveatDataBuilder.build());
        List<Document> documents = new ArrayList<>();
        Document document = Document.builder()
                .documentLink(documentLinkMock)
                .documentType(DocumentType.CAVEAT_RAISED)
                .build();
        documents.add(0, document);
        String letterId = "123-456";
        CaveatCallbackResponse caveatCallbackResponse =
                underTest.caveatRaised(caveatCallbackRequestMock, documents, letterId);

        assertEquals(newSubmittedDate.toString(),
                caveatCallbackResponse.getCaveatData().getApplicationSubmittedDate());
    }

    @Test
    void shouldConvertRequestToDataBeanWithCaveatMessageContentChange() {
        setupMocks();
        CaveatCallbackResponse caveatCallbackResponse = underTest.generalMessage(caveatCallbackRequestMock, document);

        assertCommon(caveatCallbackResponse);

        assertTrue(caveatCallbackResponse.getCaveatData().getMessageContent().isEmpty());
    }

    @Test
    void shouldDefaultValuesCaveatRaisedEmailNotification() {
        setupMocks();
        CaveatCallbackResponse caveatCallbackResponse = underTest.defaultCaveatValues(caveatCallbackRequestMock);

        assertCommon(caveatCallbackResponse);

        assertEquals(Constants.YES, caveatCallbackResponse.getCaveatData().getCaveatRaisedEmailNotificationRequested());
    }

    @Test
    void shouldDefaultValuesCaveatRaisedEmailNotificationWhenNoEmail() {
        setupMocks();
        CaveatData caseData = caveatDataBuilder.caveatorEmailAddress(null)
            .build();
        when(caveatDetailsMock.getData()).thenReturn(caseData);

        CaveatCallbackResponse caveatCallbackResponse = underTest.defaultCaveatValues(caveatCallbackRequestMock);

        assertEquals(NO, caveatCallbackResponse.getCaveatData().getCaveatRaisedEmailNotificationRequested());
    }

    @Test
    void shouldDefaultValuesBulkPrintlNotification() {
        setupMocks();
        CaveatCallbackResponse caveatCallbackResponse = underTest.defaultCaveatValues(caveatCallbackRequestMock);

        assertCommon(caveatCallbackResponse);

        assertEquals(Constants.YES, caveatCallbackResponse.getCaveatData().getSendToBulkPrintRequested());
    }

    @Test
    void bulkScanCaveatTransform() {
        CaseCreationDetails caveatDetails = underTest.bulkScanCaveatCaseTransform(bulkScanCaveatData);
        assertBulkScanCaseCreationDetails(caveatDetails);
    }

    @Test
    void bulkScanCaveatTransformForOrgPolicy() {
        uk.gov.hmcts.reform.probate.model.cases.OrganisationPolicy orgPolicy =
                uk.gov.hmcts.reform.probate.model.cases.OrganisationPolicy.builder()
                        .organisation(uk.gov.hmcts.reform.probate.model.cases.Organisation.builder()
                                .organisationID(null)
                                .organisationName(null)
                                .build())
                        .orgPolicyReference(null)
                        .orgPolicyCaseAssignedRole(POLICY_ROLE_APPLICANT_SOLICITOR)
                        .build();
        bulkScanCaveatData.setApplicationType(SOLICITORS);
        CaseCreationDetails caveatDetails = underTest.bulkScanCaveatCaseTransform(bulkScanCaveatData);
        uk.gov.hmcts.reform.probate.model.cases.caveat.CaveatData caveatData =
                (uk.gov.hmcts.reform.probate.model.cases.caveat.CaveatData) caveatDetails.getCaseData();
        assertEquals(orgPolicy, caveatData.getApplicantOrganisationPolicy());
    }

    @Test
    void shouldConvertRequestToDataBeanWithCaveatExpiry() {
        setupMocks();
        List<Document> documents = new ArrayList<>();
        Document document = Document.builder()
            .documentLink(documentLinkMock)
            .documentType(DocumentType.CAVEAT_EXTENDED)
            .build();
        documents.add(0, document);
        String letterId = "123-456";
        CaveatCallbackResponse caveatCallbackResponse =
            underTest.caveatExtendExpiry(caveatCallbackRequestMock, documents, letterId);

        assertCommon(caveatCallbackResponse);

        assertEquals(1, caveatCallbackResponse.getCaveatData().getNotificationsGenerated().size());
        assertEquals(1, caveatCallbackResponse.getCaveatData().getBulkPrintId().size());
        assertEquals(letterId,
            caveatCallbackResponse.getCaveatData().getBulkPrintId().get(0).getValue().getSendLetterId());
    }

    @Test
    void shouldConvertRequestToDataBeanWithCaveatNoc() {
        setupMocks();
        List<Document> documents = new ArrayList<>();
        Document document = Document.builder()
                .documentLink(documentLinkMock)
                .documentType(DocumentType.SENT_EMAIL)
                .build();
        documents.add(0, document);
        CaveatCallbackResponse caveatCallbackResponse =
                underTest.addNocDocuments(caveatCallbackRequestMock, documents);

        assertCommon(caveatCallbackResponse);
    }

    @Test
    void shouldConvertRequestToDataBeanWithCaveatExpiryWithNoDocuments() {
        setupMocks();
        List<Document> documents = new ArrayList<>();
        CaveatCallbackResponse caveatCallbackResponse =
            underTest.caveatExtendExpiry(caveatCallbackRequestMock, documents, null);

        assertCommon(caveatCallbackResponse);

        assertEquals(0, caveatCallbackResponse.getCaveatData().getNotificationsGenerated().size());
    }

    @Test
    void shouldConvertRequestToDataBeanWithCaveatExpiryWithNoCaveatExtendDocuments() {
        setupMocks();
        List<Document> documents = new ArrayList<>();
        Document document = Document.builder()
            .documentLink(documentLinkMock)
            .documentType(DocumentType.DIGITAL_GRANT)
            .build();
        documents.add(0, document);
        String letterId = "123-456";
        CaveatCallbackResponse caveatCallbackResponse =
            underTest.caveatExtendExpiry(caveatCallbackRequestMock, documents, letterId);

        assertCommon(caveatCallbackResponse);

        assertEquals(0, caveatCallbackResponse.getCaveatData().getNotificationsGenerated().size());
    }

    @Test
    void shouldConvertRequestToDataBeanWithCaveatWithdrawn() {
        setupMocks();
        List<Document> documents = new ArrayList<>();
        Document document = Document.builder()
            .documentLink(documentLinkMock)
            .documentType(DocumentType.CAVEAT_WITHDRAWN)
            .build();
        documents.add(0, document);
        String letterId = "123-456";
        CaveatCallbackResponse caveatCallbackResponse =
            underTest.withdrawn(caveatCallbackRequestMock, documents, letterId);

        assertCommon(caveatCallbackResponse);

        assertEquals(1, caveatCallbackResponse.getCaveatData().getNotificationsGenerated().size());
        assertEquals(1, caveatCallbackResponse.getCaveatData().getBulkPrintId().size());
        assertEquals(letterId,
            caveatCallbackResponse.getCaveatData().getBulkPrintId().get(0).getValue().getSendLetterId());
    }

    @Test
    void shouldConvertRequestToDataBeanWithCaveatWithdrawnWithNoDocuments() {
        setupMocks();
        List<Document> documents = new ArrayList<>();
        CaveatCallbackResponse caveatCallbackResponse = underTest.withdrawn(caveatCallbackRequestMock, documents, null);

        assertCommon(caveatCallbackResponse);

        assertEquals(0, caveatCallbackResponse.getCaveatData().getNotificationsGenerated().size());
        assertEquals(0, caveatCallbackResponse.getCaveatData().getBulkPrintId().size());
    }

    @Test
    void shouldConvertRequestToDataBeanWithCaveatWithdrawnNoCaveatWithdrawnDocuments() {
        setupMocks();
        List<Document> documents = new ArrayList<>();
        Document document = Document.builder()
            .documentLink(documentLinkMock)
            .documentType(DocumentType.DIGITAL_GRANT)
            .build();
        documents.add(0, document);
        String letterId = "123-456";
        CaveatCallbackResponse caveatCallbackResponse =
            underTest.withdrawn(caveatCallbackRequestMock, documents, letterId);

        assertCommon(caveatCallbackResponse);

        assertEquals(0, caveatCallbackResponse.getCaveatData().getNotificationsGenerated().size());
        assertEquals(0, caveatCallbackResponse.getCaveatData().getBulkPrintId().size());
    }

    @Test
    void testBuildOrganisationPolicy() {
        when(organisationsRetrievalService.getOrganisationEntity(anyString(), anyString()))
            .thenReturn(new OrganisationEntityResponse());
        when(caveatDetailsMock.getData()).thenReturn(CaveatData.builder().build());
        assertNull(underTest.buildOrganisationPolicy(caveatDetailsMock, "ABC123"));
        verify(organisationsRetrievalService).getOrganisationEntity(anyString(), anyString());
    }

    @Test
    void testBuildOrganisationPolicyReturnNullForNoAuth() {
        assertNull(underTest.buildOrganisationPolicy(caveatDetailsMock, null));
    }

    @Test
    void testBuildOrganisationPolicyNullWhenRetrievalServiceNull() {
        when(organisationsRetrievalService.getOrganisationEntity(anyString(), anyString())).thenReturn(null);
        assertNull(underTest.buildOrganisationPolicy(caveatDetailsMock, "ABC123"));
        verify(organisationsRetrievalService).getOrganisationEntity(anyString(), anyString());
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

        CaveatData caveatData = CaveatData.builder().applicantOrganisationPolicy(organisationPolicy).build();
        when(caveatDetailsMock.getData()).thenReturn(caveatData);
        OrganisationPolicy actualBuildOrganisationPolicyResult = underTest
            .buildOrganisationPolicy(caveatDetailsMock, "ABC123");
        assertEquals("Org Policy Case Assigned Role",
            actualBuildOrganisationPolicyResult.getOrgPolicyCaseAssignedRole());
        assertEquals("Org Policy Reference", actualBuildOrganisationPolicyResult.getOrgPolicyReference());
        Organisation organisationResult = actualBuildOrganisationPolicyResult.getOrganisation();
        assertEquals(ORGANISATION_NAME, organisationResult.getOrganisationName());
        assertEquals(ORG_ID, organisationResult.getOrganisationID());
        verify(organisationsRetrievalService).getOrganisationEntity(anyString(), anyString());
        verify(organisationPolicy).getOrgPolicyCaseAssignedRole();
        verify(organisationPolicy).getOrgPolicyReference();
    }

    @Test
    void shouldCovertSolsPBANumbers() {
        setupMocks();
        CaveatCallbackResponse caveatCallbackResponse =
            underTest.transformCaseForSolicitorPayment(caveatCallbackRequestMock);

        assertCommon(caveatCallbackResponse);
        verify(solicitorPBADefaulterMock).defaultCaveatSolicitorReference(any(), any());
    }

    @Test
    void shouldExtendCaveatExpiry() {
        setupMocks();
        CaveatCallbackResponse caveatCallbackResponse =
            underTest.transformResponseWithExtendedExpiry(caveatCallbackRequestMock);

        String extendedDate = "2020-08-01";
        assertEquals(extendedDate, caveatCallbackResponse.getCaveatData().getExpiryDate());
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

        caveatDataBuilder.registrarDirections(directions);

        when(caveatCallbackRequestMock.getCaseDetails()).thenReturn(caveatDetailsMock);
        when(caveatDetailsMock.getData()).thenReturn(caveatDataBuilder.build());

        CaveatCallbackResponse callbackResponse =
                underTest.transformCaseWithRegistrarDirection(caveatCallbackRequestMock);

        ResponseCaveatData responseCaveatData = callbackResponse.getCaveatData();
        assertEquals(2, responseCaveatData.getRegistrarDirections().size());
        assertEquals("2023-01-01T23:45:45.890Z", format(formatter, responseCaveatData, 0));
        assertEquals("Decision 1", responseCaveatData.getRegistrarDirections().get(0).getValue().getDecision());
        assertEquals("Further information 1", responseCaveatData.getRegistrarDirections().get(0).getValue()
                .getFurtherInformation());
        assertEquals("2023-01-02T23:45:45.890Z", format(formatter, responseCaveatData, 1));
        assertEquals("Decision 2", responseCaveatData.getRegistrarDirections().get(1).getValue().getDecision());
        assertNull(responseCaveatData.getRegistrarDirections().get(1).getValue().getFurtherInformation());

        assertNotNull(responseCaveatData.getRegistrarDirectionToAdd());
        assertNull(responseCaveatData.getRegistrarDirectionToAdd().getAddedDateTime());
        assertNull(responseCaveatData.getRegistrarDirectionToAdd().getDecision());
        assertNull(responseCaveatData.getRegistrarDirectionToAdd().getFurtherInformation());
    }

    private String format(DateTimeFormatter formatter, ResponseCaveatData caseData, int ind) {
        return formatter.format(caseData.getRegistrarDirections().get(ind).getValue().getAddedDateTime());
    }

    @Test
    void shouldTransformResponseWithNoChanges() {
        caveatDataBuilder.applicationType(SOLICITOR);
        caveatDataBuilder.paperForm("No");
        caveatDataBuilder.registryLocation("ctsc");
        setupMocks();
        CaveatCallbackResponse caveatCallbackResponse = underTest
                .transformResponseWithNoChanges(caveatCallbackRequestMock);

        assertCommonSolsCaveats(caveatCallbackResponse);
    }

    @Test
    void shouldNotAddMatches() {
        caveatDataBuilder.applicationType(SOLICITOR);
        caveatDataBuilder.paperForm("No");
        caveatDataBuilder.registryLocation("ctsc");
        setupMocks();
        CaveatCallbackResponse caveatCallbackResponse =
                underTest.addMatches(caveatCallbackRequestMock, caseMatches);

        assertEquals(caveatCallbackResponse.getCaveatData().getMatches(), "No matches found");
    }

    @Test
    void shouldAddMatches() {
        List<CollectionMember<CaseMatch>> caseMatch = new ArrayList<>();
        CollectionMember<CaseMatch> match =
                new CollectionMember<>(null, CaseMatch
                        .builder()
                        .id("123")
                        .build());
        caseMatch.add(match);
        caveatDataBuilder.applicationType(SOLICITOR);
        caveatDataBuilder.paperForm("No");
        caveatDataBuilder.registryLocation("ctsc");
        caveatDataBuilder.caseMatches(caseMatch);
        setupMocks();
        CaveatCallbackResponse caveatCallbackResponse =
                underTest.addMatches(caveatCallbackRequestMock, caseMatches);

        assertEquals(caveatCallbackResponse.getCaveatData().getMatches(), "Possible case matches");
    }

    @Test
    void shouldTransformResponse() {
        caveatDataBuilder.applicationType(SOLICITOR);
        caveatDataBuilder.paperForm("No");
        caveatDataBuilder.registryLocation("ctsc");
        setupMocks();
        CaveatCallbackResponse caveatCallbackResponse =
                underTest.transformResponseWithServiceRequest(caveatCallbackRequestMock, "User-id");

        assertCommonSolsCaveats(caveatCallbackResponse);
        assertEquals(caveatCallbackResponse.getCaveatData().getApplicationSubmittedBy(), "User-id");
    }

    private void assertBulkScanCaseCreationDetails(CaseCreationDetails caveatCreationDetails) {
        uk.gov.hmcts.reform.probate.model.cases.caveat.CaveatData caveatData =
            (uk.gov.hmcts.reform.probate.model.cases.caveat.CaveatData) caveatCreationDetails.getCaseData();
        assertEquals(CAV_EXCEPTION_RECORD_EVENT_ID, caveatCreationDetails.getEventId());
        assertEquals(CAV_EXCEPTION_RECORD_CASE_TYPE_ID, caveatCreationDetails.getCaseTypeId());
        assertEquals(BULK_SCAN_CAV_REGISTRY_LOCATION.name(), caveatData.getRegistryLocation().name());
        assertEquals(CAV_APPLICATION_TYPE.name(), caveatData.getApplicationType().getName().toUpperCase());
        assertEquals(DATE_SUBMITTED.toString(), caveatData.getApplicationSubmittedDate().toString());

        assertEquals(true, caveatData.getPaperForm());
        assertEquals(CAV_DECEASED_FORENAMES, caveatData.getDeceasedForenames());
        assertEquals(CAV_DECEASED_SURNAME, caveatData.getDeceasedSurname());
        assertEquals(CAV_BSP_DECEASED_ADDRESS, caveatData.getDeceasedAddress());
        assertEquals(CAV_DECEASED_DOD, caveatData.getDeceasedDateOfDeath());
        assertEquals(CAV_DECEASED_DOB, caveatData.getDeceasedDateOfBirth());

        assertEquals(CAV_BSP_CAVEATOR_ADDRESS, caveatData.getCaveatorAddress());
        assertEquals(CAV_CAVEATOR_EMAIL_ADDRESS, caveatData.getCaveatorEmailAddress());
        assertEquals(CAV_CAVEATOR_FORENAMES, caveatData.getCaveatorForenames());
        assertEquals(CAV_CAVEATOR_SURNAME, caveatData.getCaveatorSurname());
        assertEquals(BULK_SCAN_REFERENCE, caveatData.getBulkScanCaseReference());
        assertEquals(BULK_SCAN_ENVELOPES, caveatData.getBulkScanEnvelopes());

        assertFalse(caveatData.getDeceasedAnyOtherNames());
        assertTrue(caveatData.getCaveatRaisedEmailNotificationRequested());
        assertFalse(caveatData.getSendToBulkPrintRequested());

        assertEquals(CAV_CAVEATOR_PHONENUMBER, caveatData.getCaveatorPhoneNumber());
        assertEquals(CAV_PROBATE_FEE, caveatData.getProbateFee().getDescription());
        assertEquals(CAV_HELP_WITH_FEES_REFERENCE, caveatData.getHelpWithFeesReference());
        assertEquals(CAV_PROBATE_FEE_NOT_INCLUDED_REASON, caveatData.getProbateFeeNotIncludedReason().getDescription());
        assertEquals(CAV_NOT_INCLUDED_EXPLANATION, caveatData.getProbateFeeNotIncludedExplanation());
        assertEquals(CAV_FEE_ACCOUNT_NUMBER, caveatData.getProbateFeeAccountNumber());
        assertEquals(CAV_FEE_ACCOUNT_REFERENCE, caveatData.getProbateFeeAccountReference());
        assertEquals(REPRESENTATIVE_NAME, caveatData.getSolsSolicitorRepresentativeName());
        assertEquals(DX_NUMBER, caveatData.getDxNumber());
        assertTrue(caveatData.getPractitionerAcceptsServiceByEmail());
    }

    @Test
    void shouldSetupDocumentsForRemoval() {

        List<CollectionMember<Document>> generated = Arrays.asList(new CollectionMember("1",
                Document.builder().build()));
        List<CollectionMember<ScannedDocument>> scanned = Arrays.asList(new CollectionMember("2",
                ScannedDocument.builder().build()));
        List<CollectionMember<UploadDocument>> uploaded = Arrays.asList(new CollectionMember("3",
                UploadDocument.builder().build()));

        caveatDataBuilder.documentsGenerated(generated);
        caveatDataBuilder.scannedDocuments(scanned);
        caveatDataBuilder.documentsUploaded(uploaded);

        when(caveatCallbackRequestMock.getCaseDetails()).thenReturn(caveatDetailsMock);
        when(caveatDetailsMock.getData()).thenReturn(caveatDataBuilder.build());

        CaveatCallbackResponse response = underTest.setupOriginalDocumentsForRemoval(caveatCallbackRequestMock);
        OriginalDocuments originalDocuments = response.getCaveatData().getOriginalDocuments();
        assertEquals("1", originalDocuments.getOriginalDocsGenerated().get(0).getId());
        assertEquals("2", originalDocuments.getOriginalDocsScanned().get(0).getId());
        assertEquals("3", originalDocuments.getOriginalDocsUploaded().get(0).getId());
    }

    @Test
    void shouldTransformRollbackState() {
        when(securityUtils.getSecurityDTO()).thenReturn(securityDTO);
        when(caveatCallbackRequestMock.getCaseDetails()).thenReturn(caveatDetailsMock);
        caveatDataBuilder.applicationType(SOLICITOR)
                .autoClosedExpiry(YES);
        when(caveatDetailsMock.getData()).thenReturn(caveatDataBuilder.build());
        when(auditEventService.getLatestAuditEventByState(any(), any(), any(), any()))
                .thenReturn(Optional.ofNullable(AuditEvent.builder()
                        .stateId("CaveatNotMatched")
                        .createdDate(LocalDateTime.now())
                        .build()));
        CaveatCallbackResponse callbackResponse = underTest.rollback(caveatCallbackRequestMock);
        assertAll(
                () -> assertEquals("CaveatNotMatched", callbackResponse.getCaveatData().getState()),
                () -> assertNull(callbackResponse.getCaveatData().getAutoClosedExpiry())
        );
    }

    private void assertCommon(CaveatCallbackResponse caveatCallbackResponse) {
        assertCommonDetails(caveatCallbackResponse);
        assertApplicationType(caveatCallbackResponse, CAV_APPLICATION_TYPE);
        assertPaperForm(caveatCallbackResponse, YES);
        assertRegistryLocation(caveatCallbackResponse, CAV_REGISTRY_LOCATION);
    }

    private void assertCommonSolsCaveats(CaveatCallbackResponse caveatCallbackResponse) {
        assertCommonDetails(caveatCallbackResponse);
        assertApplicationType(caveatCallbackResponse, CAV_APPLICATION_TYPE_SOLS);
        assertPaperForm(caveatCallbackResponse, NO);
        assertRegistryLocation(caveatCallbackResponse, CAV_SOLS_REGISTRY_LOCATION);
    }

    private void assertCommonDetails(CaveatCallbackResponse caveatCallbackResponse) {

        assertEquals(CAV_DECEASED_FORENAMES, caveatCallbackResponse.getCaveatData().getDeceasedForenames());
        assertEquals(CAV_DECEASED_SURNAME, caveatCallbackResponse.getCaveatData().getDeceasedSurname());
        assertEquals("2017-12-31", caveatCallbackResponse.getCaveatData().getDeceasedDateOfDeath());
        assertEquals("2016-12-31", caveatCallbackResponse.getCaveatData().getDeceasedDateOfBirth());
        assertEquals(CAV_DECEASED_HAS_ALIAS, caveatCallbackResponse.getCaveatData().getDeceasedAnyOtherNames());
        assertEquals(CAV_DECEASED_ADDRESS, caveatCallbackResponse.getCaveatData().getDeceasedAddress());

        assertEquals(CAV_CAVEATOR_FORENAMES, caveatCallbackResponse.getCaveatData().getCaveatorForenames());
        assertEquals(CAV_CAVEATOR_SURNAME, caveatCallbackResponse.getCaveatData().getCaveatorSurname());
        assertEquals(CAV_CAVEATOR_EMAIL_ADDRESS, caveatCallbackResponse.getCaveatData().getCaveatorEmailAddress());
        assertEquals(CAV_CAVEATOR_ADDRESS, caveatCallbackResponse.getCaveatData().getCaveatorAddress());
        assertEquals(DATE_SUBMITTED.toString(), caveatCallbackResponse.getCaveatData().getApplicationSubmittedDate());

        assertEquals(CAV_SOLICITOR_FIRMNAME, caveatCallbackResponse.getCaveatData().getSolsSolicitorFirmName());
        assertEquals(CAV_SOLICITOR_PHONENUMBER, caveatCallbackResponse.getCaveatData().getSolsSolicitorPhoneNumber());
        assertEquals(CAV_SOLICITOR_APP_REFERENCE,
            caveatCallbackResponse.getCaveatData().getSolsSolicitorAppReference());

        assertEquals(CAV_FORMATTED_EXPIRY_DATE, caveatCallbackResponse.getCaveatData().getExpiryDate());
        assertEquals(CAV_MESSAGE_CONTENT, caveatCallbackResponse.getCaveatData().getMessageContent());
        assertEquals(CAV_REOPEN_REASON, caveatCallbackResponse.getCaveatData().getCaveatReopenReason());

        assertEquals(CAV_RECORD_ID, caveatCallbackResponse.getCaveatData().getRecordId());
        assertEquals(CAV_LEGACY_CASE_TYPE, caveatCallbackResponse.getCaveatData().getLegacyType());
        assertEquals(CAV_LEGACY_CASE_URL, caveatCallbackResponse.getCaveatData().getLegacyCaseViewUrl());

        assertEquals(SOLS_PAYMENT_METHOD, caveatCallbackResponse.getCaveatData().getSolsPaymentMethods());
        assertEquals(SOLS_FEE_ACC, caveatCallbackResponse.getCaveatData().getSolsFeeAccountNumber());
        assertEquals(SOLS_SELECTED_PBA, caveatCallbackResponse.getCaveatData().getSolsPBANumber().getValue().getCode());

        assertEquals(YES, caveatCallbackResponse.getCaveatData().getAutoClosedExpiry());
        assertEquals(CAV_SOLICITOR_APP_REFERENCE, caveatCallbackResponse.getCaveatData().getPcqId());
        assertEquals(CAV_CAVEATOR_PHONENUMBER, caveatCallbackResponse.getCaveatData().getCaveatorPhoneNumber());
        assertEquals(CAV_PROBATE_FEE, caveatCallbackResponse.getCaveatData().getProbateFee());
        assertEquals(CAV_HELP_WITH_FEES_REFERENCE, caveatCallbackResponse.getCaveatData().getHelpWithFeesReference());
        assertEquals(CAV_PROBATE_FEE_NOT_INCLUDED_REASON,
            caveatCallbackResponse.getCaveatData().getProbateFeeNotIncludedReason());
        assertEquals(CAV_NOT_INCLUDED_EXPLANATION,
            caveatCallbackResponse.getCaveatData().getProbateFeeNotIncludedExplanation());
        assertEquals(CAV_FEE_ACCOUNT_NUMBER, caveatCallbackResponse.getCaveatData().getProbateFeeAccountNumber());
        assertEquals(CAV_FEE_ACCOUNT_REFERENCE, caveatCallbackResponse.getCaveatData().getProbateFeeAccountReference());
    }

    private void assertCaveatPayment(CaveatCallbackResponse caveatCallbackResponse) {
        assertEquals("pba",
            caveatCallbackResponse.getCaveatData().getPayments().get(0).getValue().getMethod());

    }

    private void assertApplicationType(CaveatCallbackResponse caveatCallbackResponse,
                                       ApplicationType cavApplicationType) {
        assertEquals(cavApplicationType, caveatCallbackResponse.getCaveatData().getApplicationType());
    }

    private void assertPaperForm(CaveatCallbackResponse caveatCallbackResponse, String paperForm) {
        assertEquals(paperForm, caveatCallbackResponse.getCaveatData().getPaperForm());
    }

    private void assertRegistryLocation(CaveatCallbackResponse caveatCallbackResponse, String registry) {
        assertEquals(registry, caveatCallbackResponse.getCaveatData().getRegistryLocation());
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
            .documentType(DocumentType.CORRESPONDENCE).build();
        return new CollectionMember<>(id, doc);
    }
}
