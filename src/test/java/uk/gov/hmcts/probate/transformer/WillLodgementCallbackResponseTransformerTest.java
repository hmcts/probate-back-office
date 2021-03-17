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
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.ProbateAddress;
import uk.gov.hmcts.probate.model.ccd.ProbateFullAliasName;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.UploadDocument;
import uk.gov.hmcts.probate.model.ccd.willlodgement.request.WillLodgementCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.willlodgement.request.WillLodgementData;
import uk.gov.hmcts.probate.model.ccd.willlodgement.request.WillLodgementDetails;
import uk.gov.hmcts.probate.model.ccd.willlodgement.response.WillLodgementCallbackResponse;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.ApplicationType.PERSONAL;

@RunWith(MockitoJUnitRunner.class)
public class WillLodgementCallbackResponseTransformerTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final String YES = "Yes";
    private static final String NO = "No";

    private static final ApplicationType WL_APPLICATION_TYPE = PERSONAL;
    private static final String WL_REGISTRY_LOCATION = "Leeds";
    private static final String WL_LODGEMENT_TYPE = "safeCustody";
    private static final LocalDate WL_LODGED_DATE = LocalDate.parse("2018-12-31", dateTimeFormatter);
    private static final LocalDate WL_WILL_DATE = LocalDate.parse("2014-12-31", dateTimeFormatter);
    private static final LocalDate WL_CODICIL_DATE = LocalDate.parse("2016-12-31", dateTimeFormatter);
    private static final String WL_JOINT_WILL = NO;

    private static final String WL_DECEASED_FORENAMES = "Forenames";
    private static final String WL_DECEASED_SURNAME = "Surname";
    private static final String WL_DECEASDED_GENDER = "male";
    private static final LocalDate WL_DECEASED_DOB = LocalDate.parse("2016-12-31", dateTimeFormatter);
    private static final LocalDate WL_DECEASED_DOD = LocalDate.parse("2017-12-31", dateTimeFormatter);
    private static final String WL_DECEASED_TYPE_OF_DEATH = "diedOnOrSince";
    private static final String WL_DECEASED_HAS_ALIAS = YES;
    private static final String WL_DECEASED_FULL_ALIAS_NAME = "AliasFN AliasSN";
    private static final List<CollectionMember<ProbateFullAliasName>> WL_DECEASED_FULL_ALIAS_NAME_LIST = emptyList();
    private static final ProbateAddress WL_DECEASED_ADDRESS = Mockito.mock(ProbateAddress.class);
    private static final String WL_DECEASED_EMAIL_ADDRESS = "deceased@probate-test.com";

    private static final String WL_EXECUTOR_TITLE = "Mr";
    private static final String WL_EXECUTOR_FORENAMES = "Forenames";
    private static final String WL_EXECUTOR_SURNAME = "Surname";
    private static final ProbateAddress WL_EXECUTOR_ADDRESS = Mockito.mock(ProbateAddress.class);
    private static final String WL_EXECUTOR_EMAIL_ADDRESS = "executor1@probate-test.com";

    private static final String WL_WITHDRAWAL_REASON = "cancelled";
    private static final String WILL_LODGEMENT_RECEIPT = "willLodgementDepositReceipt";

    private static final String WL_RECORD_ID = "12345";
    private static final String WL_LEGACY_CASE_URL = "someUrl";
    private static final String WL_LEGACY_CASE_TYPE = "someCaseType";

    @InjectMocks
    private WillLodgementCallbackResponseTransformer underTest;

    @Mock
    private WillLodgementCallbackRequest willLodgementCallbackRequestMock;

    @Mock
    private WillLodgementDetails willLodgementDetailsMock;

    @Mock
    private DocumentLink documentLinkMock;

    @Spy
    private DocumentTransformer documentTransformer;

    private WillLodgementData.WillLodgementDataBuilder willLodgementDataBuilder;

    @Before
    public void setup() {

        willLodgementDataBuilder = WillLodgementData.builder()
                .lodgementType(WL_LODGEMENT_TYPE)
                .lodgedDate(WL_LODGED_DATE)
                .willDate(WL_WILL_DATE)
                .codicilDate(WL_CODICIL_DATE)
                .jointWill(WL_JOINT_WILL)
                .deceasedForenames(WL_DECEASED_FORENAMES)
                .deceasedSurname(WL_DECEASED_SURNAME)
                .deceasedGender(WL_DECEASDED_GENDER)
                .deceasedDateOfBirth(WL_DECEASED_DOB)
                .deceasedDateOfDeath(WL_DECEASED_DOD)
                .deceasedTypeOfDeath(WL_DECEASED_TYPE_OF_DEATH)
                .deceasedAnyOtherNames(WL_DECEASED_HAS_ALIAS)
                .deceasedFullAliasNameList(WL_DECEASED_FULL_ALIAS_NAME_LIST)
                .deceasedAddress(WL_DECEASED_ADDRESS)
                .deceasedEmailAddress(WL_DECEASED_EMAIL_ADDRESS)
                .executorTitle(WL_EXECUTOR_TITLE)
                .executorForenames(WL_EXECUTOR_FORENAMES)
                .executorSurname(WL_EXECUTOR_SURNAME)
                .executorAddress(WL_EXECUTOR_ADDRESS)
                .executorEmailAddress(WL_EXECUTOR_EMAIL_ADDRESS)
                .withdrawalReason(WL_WITHDRAWAL_REASON)
                .recordId(WL_RECORD_ID)
                .legacyCaseViewUrl(WL_LEGACY_CASE_URL)
                .legacyType(WL_LEGACY_CASE_TYPE);

        when(willLodgementCallbackRequestMock.getCaseDetails()).thenReturn(willLodgementDetailsMock);
        when(willLodgementDetailsMock.getData()).thenReturn(willLodgementDataBuilder.build());
    }

    @Test
    public void shouldTransformWillLodgementCallbackRequestToWillLodgementCallbackResponse() {
        WillLodgementCallbackResponse willLodgementCallbackResponse = underTest.transform(willLodgementCallbackRequestMock);

        assertCommon(willLodgementCallbackResponse);
    }

    @Test
    public void shouldTransformCaseForWillLodgementWithDeceasedAliasNames() {
        List<CollectionMember<ProbateFullAliasName>> deceasedFullAliasNameList = new ArrayList<>();

        ProbateFullAliasName an11 = ProbateFullAliasName.builder().fullAliasName(WL_DECEASED_FULL_ALIAS_NAME).build();
        CollectionMember<ProbateFullAliasName> an1 = new CollectionMember<>("0", an11);
        deceasedFullAliasNameList.add(an1);
        willLodgementDataBuilder.deceasedFullAliasNameList(deceasedFullAliasNameList);

        when(willLodgementCallbackRequestMock.getCaseDetails()).thenReturn(willLodgementDetailsMock);
        when(willLodgementDetailsMock.getData()).thenReturn(willLodgementDataBuilder.build());

        WillLodgementCallbackResponse willLodgementCallbackResponse = underTest.transform(willLodgementCallbackRequestMock);

        assertCommonDetails(willLodgementCallbackResponse);
        assertEquals(1, willLodgementCallbackResponse.getResponseWillLodgementData().getDeceasedFullAliasNameList().size());
    }

    @Test
    public void shouldGetWillLodgementUploadedDocuments() {
        List<CollectionMember<UploadDocument>> documents = new ArrayList<>();
        documents.add(createUploadDocuments("0"));
        willLodgementDataBuilder.documentsUploaded(documents);

        when(willLodgementCallbackRequestMock.getCaseDetails()).thenReturn(willLodgementDetailsMock);
        when(willLodgementDetailsMock.getData()).thenReturn(willLodgementDataBuilder.build());

        WillLodgementCallbackResponse willLodgementCallbackResponse = underTest.transform(willLodgementCallbackRequestMock);

        assertCommonDetails(willLodgementCallbackResponse);
        assertEquals(1, willLodgementCallbackResponse.getResponseWillLodgementData().getDocumentsUploaded().size());
    }

    @Test
    public void shouldAddMatches() {
        List<CaseMatch> matches = new ArrayList<>();
        matches.add(CaseMatch.builder().fullName("Name One").build());
        matches.add(CaseMatch.builder().fullName("Name Two").build());

        WillLodgementCallbackResponse response = underTest.addMatches(willLodgementCallbackRequestMock, matches);

        assertCommonDetails(response);
        assertEquals(2, response.getResponseWillLodgementData().getCaseMatches().size());
    }

    private void assertCommon(WillLodgementCallbackResponse willLodgementCallbackResponse) {
        assertCommonDetails(willLodgementCallbackResponse);
        assertApplicationType(willLodgementCallbackResponse, WL_APPLICATION_TYPE);
    }

    private void assertCommonDetails(WillLodgementCallbackResponse willLodgementCallbackResponse) {
        assertEquals(WL_REGISTRY_LOCATION, willLodgementCallbackResponse.getResponseWillLodgementData().getRegistryLocation());
        assertEquals(WL_LODGEMENT_TYPE, willLodgementCallbackResponse.getResponseWillLodgementData().getLodgementType());
        assertEquals("2018-12-31", willLodgementCallbackResponse.getResponseWillLodgementData().getLodgedDate());
        assertEquals("2014-12-31", willLodgementCallbackResponse.getResponseWillLodgementData().getWillDate());
        assertEquals("2016-12-31", willLodgementCallbackResponse.getResponseWillLodgementData().getCodicilDate());
        assertEquals(WL_JOINT_WILL, willLodgementCallbackResponse.getResponseWillLodgementData().getJointWill());

        assertEquals(WL_DECEASED_FORENAMES, willLodgementCallbackResponse.getResponseWillLodgementData().getDeceasedForenames());
        assertEquals(WL_DECEASED_SURNAME, willLodgementCallbackResponse.getResponseWillLodgementData().getDeceasedSurname());
        assertEquals(WL_DECEASDED_GENDER, willLodgementCallbackResponse.getResponseWillLodgementData().getDeceasedGender());
        assertEquals("2016-12-31", willLodgementCallbackResponse.getResponseWillLodgementData().getDeceasedDateOfBirth());
        assertEquals("2017-12-31", willLodgementCallbackResponse.getResponseWillLodgementData().getDeceasedDateOfDeath());
        assertEquals(WL_DECEASED_TYPE_OF_DEATH, willLodgementCallbackResponse.getResponseWillLodgementData().getDeceasedTypeOfDeath());
        assertEquals(WL_DECEASED_HAS_ALIAS, willLodgementCallbackResponse.getResponseWillLodgementData().getDeceasedAnyOtherNames());
        assertEquals(WL_DECEASED_ADDRESS, willLodgementCallbackResponse.getResponseWillLodgementData().getDeceasedAddress());
        assertEquals(WL_DECEASED_EMAIL_ADDRESS, willLodgementCallbackResponse.getResponseWillLodgementData().getDeceasedEmailAddress());

        assertEquals(WL_EXECUTOR_TITLE, willLodgementCallbackResponse.getResponseWillLodgementData().getExecutorTitle());
        assertEquals(WL_EXECUTOR_FORENAMES, willLodgementCallbackResponse.getResponseWillLodgementData().getExecutorForenames());
        assertEquals(WL_EXECUTOR_SURNAME, willLodgementCallbackResponse.getResponseWillLodgementData().getExecutorSurname());
        assertEquals(WL_EXECUTOR_ADDRESS, willLodgementCallbackResponse.getResponseWillLodgementData().getExecutorAddress());
        assertEquals(WL_EXECUTOR_EMAIL_ADDRESS, willLodgementCallbackResponse.getResponseWillLodgementData().getExecutorEmailAddress());

        assertEquals(WL_WITHDRAWAL_REASON, willLodgementCallbackResponse.getResponseWillLodgementData().getWithdrawalReason());

        assertEquals(WL_RECORD_ID, willLodgementCallbackResponse.getResponseWillLodgementData().getRecordId());
        assertEquals(WL_LEGACY_CASE_TYPE, willLodgementCallbackResponse.getResponseWillLodgementData().getLegacyType());
        assertEquals(WL_LEGACY_CASE_URL, willLodgementCallbackResponse.getResponseWillLodgementData().getLegacyCaseViewUrl());
    }

    private void assertApplicationType(WillLodgementCallbackResponse willLodgementCallbackResponse, ApplicationType wlApplicationType) {
        assertEquals(wlApplicationType, willLodgementCallbackResponse.getResponseWillLodgementData().getApplicationType());
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
                .documentType(DocumentType.OTHER).build();
        return new CollectionMember<>(id, doc);
    }

    @Test
    public void shouldGetWillLodgementGeneratedDocuments() {
        Document document = Document.builder()
                .documentLink(documentLinkMock)
                .documentType(DocumentType.WILL_LODGEMENT_DEPOSIT_RECEIPT)
                .build();

        when(willLodgementCallbackRequestMock.getCaseDetails()).thenReturn(willLodgementDetailsMock);
        when(willLodgementDetailsMock.getData()).thenReturn(willLodgementDataBuilder.build());

        WillLodgementCallbackResponse willLodgementCallbackResponse
                = underTest.addDocuments(willLodgementCallbackRequestMock, Arrays.asList(document));

        assertCommonDetails(willLodgementCallbackResponse);
        assertEquals(1, willLodgementCallbackResponse.getResponseWillLodgementData().getDocumentsGenerated().size());
    }


}
