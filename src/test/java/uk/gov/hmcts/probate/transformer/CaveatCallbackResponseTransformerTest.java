package uk.gov.hmcts.probate.transformer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.Constants;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.ProbateAddress;
import uk.gov.hmcts.probate.model.ccd.ProbateFullAliasName;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.caveat.response.CaveatCallbackResponse;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.UploadDocument;
import uk.gov.hmcts.probate.model.exceptionrecord.CaseCreationDetails;
import uk.gov.hmcts.reform.probate.model.cases.Address;
import uk.gov.hmcts.reform.probate.model.cases.RegistryLocation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.Constants.NO;

@RunWith(MockitoJUnitRunner.class)
public class CaveatCallbackResponseTransformerTest {

    private static final DateTimeFormatter dateTimeFormatter = CaveatCallbackResponseTransformer.dateTimeFormatter;

    private static final ApplicationType CAV_APPLICATION_TYPE = CaveatCallbackResponseTransformer.DEFAULT_APPLICATION_TYPE;
    private static final ApplicationType CAV_APPLICATION_TYPE_SOLS = ApplicationType.SOLICITOR;
    private static final String CAV_REGISTRY_LOCATION = CaveatCallbackResponseTransformer.DEFAULT_REGISTRY_LOCATION;
    private static final RegistryLocation BULK_SCAN_CAV_REGISTRY_LOCATION
            = CaveatCallbackResponseTransformer.EXCEPTION_RECORD_REGISTRY_LOCATION;

    private static final String CAV_EXCEPTION_RECORD_CASE_TYPE_ID = CaveatCallbackResponseTransformer.EXCEPTION_RECORD_CASE_TYPE_ID;
    private static final String CAV_EXCEPTION_RECORD_EVENT_ID = CaveatCallbackResponseTransformer.EXCEPTION_RECORD_EVENT_ID;

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
    private static final String CAV_CAVEATOR_EMAIL_ADDRESS = "cav@email.com";
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
    private static final String CAV_SOLS_REGISTRY_LOCATION = "ctsc";
    private static final String BULK_SCAN_REFERENCE = "BulkScanRef";

    @InjectMocks
    private CaveatCallbackResponseTransformer underTest;

    @Mock
    private CaveatCallbackRequest caveatCallbackRequestMock;

    @Mock
    private Document document;

    @Mock
    private DocumentLink documentLinkMock;

    @Mock
    private CaveatDetails caveatDetailsMock;

    @Spy
    private DocumentTransformer documentTransformer;

    private CaveatData.CaveatDataBuilder caveatDataBuilder;

    private uk.gov.hmcts.reform.probate.model.cases.caveat.CaveatData bulkScanCaveatData;

    @Before
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
                .pcqId(CAV_SOLICITOR_APP_REFERENCE);

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
                .build();

        when(caveatCallbackRequestMock.getCaseDetails()).thenReturn(caveatDetailsMock);
        when(caveatDetailsMock.getData()).thenReturn(caveatDataBuilder.build());
    }

    @Test
    public void shouldTransformSolsCaveatCallbackRequestToCaveatCallbackResponse() {
        CaveatCallbackResponse caveatCallbackResponse = underTest.transformForSolicitor(caveatCallbackRequestMock);
        assertCommonSolsCaveats(caveatCallbackResponse);
    }

    @Test
    public void shouldConvertRequestToDataBeanWithCaveatEntryDateChange() {
        List<Document> documents = new ArrayList<>();
        Document document = Document.builder()
                .documentLink(documentLinkMock)
                .documentType(DocumentType.CAVEAT_RAISED)
                .build();
        documents.add(0, document);
        String letterId = "123-456";
        CaveatCallbackResponse caveatCallbackResponse = underTest.caveatRaised(caveatCallbackRequestMock, documents, letterId);

        assertCommon(caveatCallbackResponse);

        assertEquals(CAV_FORMATTED_SUBMISSION_DATE, caveatCallbackResponse.getCaveatData().getApplicationSubmittedDate());
    }

    @Test
    public void shouldConvertRequestToDataBeanWithCaveatExpiryDateChange() {
        List<Document> documents = new ArrayList<>();
        Document document = Document.builder()
                .documentLink(documentLinkMock)
                .documentType(DocumentType.CAVEAT_RAISED)
                .build();
        documents.add(0, document);
        String letterId = null;
        CaveatCallbackResponse caveatCallbackResponse = underTest.caveatRaised(caveatCallbackRequestMock, documents, letterId);

        assertCommon(caveatCallbackResponse);

        assertEquals(CAV_FORMATTED_SUBMISSION_DATE, caveatCallbackResponse.getCaveatData().getApplicationSubmittedDate());
        assertEquals(CAV_FORMATTED_EXPIRY_DATE, caveatCallbackResponse.getCaveatData().getExpiryDate());
    }

    @Test
    public void shouldConvertRequestToDataBeanWithBulkPrintId() {
        List<Document> documents = new ArrayList<>();
        Document document = Document.builder()
                .documentLink(documentLinkMock)
                .documentType(DocumentType.CAVEAT_RAISED)
                .build();
        documents.add(0, document);
        String letterId = "123-456";
        CaveatCallbackResponse caveatCallbackResponse = underTest.caveatRaised(caveatCallbackRequestMock, documents, letterId);

        assertCommon(caveatCallbackResponse);

        assertEquals("123-456", caveatCallbackResponse.getCaveatData().getBulkPrintId().get(0).getValue().getSendLetterId());
    }

    @Test
    public void shouldConvertRequestToDataBeanWithCaveatMessageContentChange() {
        CaveatCallbackResponse caveatCallbackResponse = underTest.generalMessage(caveatCallbackRequestMock, document);

        assertCommon(caveatCallbackResponse);

        assertTrue(caveatCallbackResponse.getCaveatData().getMessageContent().isEmpty());
    }

    @Test
    public void shouldDefaultValuesCaveatRaisedEmailNotification() {
        CaveatCallbackResponse caveatCallbackResponse = underTest.defaultCaveatValues(caveatCallbackRequestMock);

        assertCommon(caveatCallbackResponse);

        assertEquals(Constants.YES, caveatCallbackResponse.getCaveatData().getCaveatRaisedEmailNotificationRequested());
    }

    @Test
    public void shouldDefaultValuesCaveatRaisedEmailNotificationWhenNoEmail() {
        CaveatData caseData = caveatDataBuilder.caveatorEmailAddress(null)
                .build();
        when(caveatDetailsMock.getData()).thenReturn(caseData);

        CaveatCallbackResponse caveatCallbackResponse = underTest.defaultCaveatValues(caveatCallbackRequestMock);

        assertEquals(NO, caveatCallbackResponse.getCaveatData().getCaveatRaisedEmailNotificationRequested());
    }

    @Test
    public void shouldDefaultValuesBulkPrintlNotification() {
        CaveatCallbackResponse caveatCallbackResponse = underTest.defaultCaveatValues(caveatCallbackRequestMock);

        assertCommon(caveatCallbackResponse);

        assertEquals(Constants.YES, caveatCallbackResponse.getCaveatData().getSendToBulkPrintRequested());
    }

    @Test
    public void bulkScanCaveatTransform() {
        CaseCreationDetails caveatDetails = underTest.bulkScanCaveatCaseTransform(bulkScanCaveatData);
        assertBulkScanCaseCreationDetails(caveatDetails);
    }

    @Test
    public void shouldConvertRequestToDataBeanWithCaveatExpiry() {
        List<Document> documents = new ArrayList<>();
        Document document = Document.builder()
            .documentLink(documentLinkMock)
            .documentType(DocumentType.CAVEAT_EXTENDED)
            .build();
        documents.add(0, document);
        String letterId = "123-456";
        CaveatCallbackResponse caveatCallbackResponse = underTest.caveatExtendExpiry(caveatCallbackRequestMock, documents, letterId);

        assertCommon(caveatCallbackResponse);

        assertEquals(1, caveatCallbackResponse.getCaveatData().getNotificationsGenerated().size());
        assertEquals(1, caveatCallbackResponse.getCaveatData().getBulkPrintId().size());
        assertEquals(letterId, caveatCallbackResponse.getCaveatData().getBulkPrintId().get(0).getValue().getSendLetterId());
    }

    @Test
    public void shouldConvertRequestToDataBeanWithCaveatExpiryWithNoDocuments() {
        List<Document> documents = new ArrayList<>();
        CaveatCallbackResponse caveatCallbackResponse = underTest.caveatExtendExpiry(caveatCallbackRequestMock, documents, null);

        assertCommon(caveatCallbackResponse);

        assertEquals(0, caveatCallbackResponse.getCaveatData().getNotificationsGenerated().size());
    }

    @Test
    public void shouldConvertRequestToDataBeanWithCaveatExpiryWithNoCaveatExtendDocuments() {
        List<Document> documents = new ArrayList<>();
        Document document = Document.builder()
            .documentLink(documentLinkMock)
            .documentType(DocumentType.DIGITAL_GRANT)
            .build();
        documents.add(0, document);
        String letterId = "123-456";
        CaveatCallbackResponse caveatCallbackResponse = underTest.caveatExtendExpiry(caveatCallbackRequestMock, documents, letterId);

        assertCommon(caveatCallbackResponse);

        assertEquals(0, caveatCallbackResponse.getCaveatData().getNotificationsGenerated().size());
    }

    @Test
    public void shouldConvertRequestToDataBeanWithCaveatWithdrawn() {
        List<Document> documents = new ArrayList<>();
        Document document = Document.builder()
                .documentLink(documentLinkMock)
                .documentType(DocumentType.CAVEAT_WITHDRAWN)
                .build();
        documents.add(0, document);
        String letterId = "123-456";
        CaveatCallbackResponse caveatCallbackResponse = underTest.withdrawn(caveatCallbackRequestMock, documents, letterId);

        assertCommon(caveatCallbackResponse);

        assertEquals(1, caveatCallbackResponse.getCaveatData().getNotificationsGenerated().size());
        assertEquals(1, caveatCallbackResponse.getCaveatData().getBulkPrintId().size());
        assertEquals(letterId, caveatCallbackResponse.getCaveatData().getBulkPrintId().get(0).getValue().getSendLetterId());
    }

    @Test
    public void shouldConvertRequestToDataBeanWithCaveatWithdrawnWithNoDocuments() {
        List<Document> documents = new ArrayList<>();
        CaveatCallbackResponse caveatCallbackResponse = underTest.withdrawn(caveatCallbackRequestMock, documents, null);

        assertCommon(caveatCallbackResponse);

        assertEquals(0, caveatCallbackResponse.getCaveatData().getNotificationsGenerated().size());
        assertEquals(0, caveatCallbackResponse.getCaveatData().getBulkPrintId().size());
    }

    @Test
    public void shouldConvertRequestToDataBeanWithCaveatWithdrawnNoCaveatWithdrawnDocuments() {
        List<Document> documents = new ArrayList<>();
        Document document = Document.builder()
            .documentLink(documentLinkMock)
            .documentType(DocumentType.DIGITAL_GRANT)
            .build();
        documents.add(0, document);
        String letterId = "123-456";
        CaveatCallbackResponse caveatCallbackResponse = underTest.withdrawn(caveatCallbackRequestMock, documents, letterId);

        assertCommon(caveatCallbackResponse);

        assertEquals(0, caveatCallbackResponse.getCaveatData().getNotificationsGenerated().size());
        assertEquals(0, caveatCallbackResponse.getCaveatData().getBulkPrintId().size());
    }

    @Test
    public void shouldExtendCaveatExpiry() {
        CaveatCallbackResponse caveatCallbackResponse = underTest.transformResponseWithExtendedExpiry(caveatCallbackRequestMock);

        String extendedDate = "2020-08-01";
        assertEquals(extendedDate, caveatCallbackResponse.getCaveatData().getExpiryDate());
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

        assertFalse(caveatData.getDeceasedAnyOtherNames());
        assertTrue(caveatData.getCaveatRaisedEmailNotificationRequested());
        assertFalse(caveatData.getSendToBulkPrintRequested());
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
        assertEquals(CAV_SOLICITOR_APP_REFERENCE, caveatCallbackResponse.getCaveatData().getSolsSolicitorAppReference());

        assertEquals(CAV_FORMATTED_EXPIRY_DATE, caveatCallbackResponse.getCaveatData().getExpiryDate());
        assertEquals(CAV_MESSAGE_CONTENT, caveatCallbackResponse.getCaveatData().getMessageContent());
        assertEquals(CAV_REOPEN_REASON, caveatCallbackResponse.getCaveatData().getCaveatReopenReason());

        assertEquals(CAV_RECORD_ID, caveatCallbackResponse.getCaveatData().getRecordId());
        assertEquals(CAV_LEGACY_CASE_TYPE, caveatCallbackResponse.getCaveatData().getLegacyType());
        assertEquals(CAV_LEGACY_CASE_URL, caveatCallbackResponse.getCaveatData().getLegacyCaseViewUrl());

        assertEquals(SOLS_PAYMENT_METHOD, caveatCallbackResponse.getCaveatData().getSolsPaymentMethods());
        assertEquals(SOLS_FEE_ACC, caveatCallbackResponse.getCaveatData().getSolsFeeAccountNumber());

        assertEquals(YES, caveatCallbackResponse.getCaveatData().getAutoClosedExpiry());
        assertEquals(CAV_SOLICITOR_APP_REFERENCE, caveatCallbackResponse.getCaveatData().getPcqId());
    }

    private void assertApplicationType(CaveatCallbackResponse caveatCallbackResponse, ApplicationType cavApplicationType) {
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
