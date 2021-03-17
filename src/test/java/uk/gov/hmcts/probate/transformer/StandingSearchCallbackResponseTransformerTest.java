package uk.gov.hmcts.probate.transformer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.ProbateAddress;
import uk.gov.hmcts.probate.model.ccd.ProbateFullAliasName;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.UploadDocument;
import uk.gov.hmcts.probate.model.ccd.standingsearch.request.StandingSearchCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.standingsearch.request.StandingSearchData;
import uk.gov.hmcts.probate.model.ccd.standingsearch.request.StandingSearchDetails;
import uk.gov.hmcts.probate.model.ccd.standingsearch.response.StandingSearchCallbackResponse;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.ApplicationType.PERSONAL;
import static uk.gov.hmcts.probate.model.Constants.STANDING_SEARCH_LIFESPAN;

@RunWith(MockitoJUnitRunner.class)
public class StandingSearchCallbackResponseTransformerTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final ApplicationType SS_APPLICATION_TYPE = PERSONAL;
    private static final String SS_REGISTRY_LOCATION = "Leeds";

    private static final String YES = "Yes";

    private static final String SS_DECEASED_FORENAMES = "Forenames";
    private static final String SS_DECEASED_SURNAME = "Surname";
    private static final LocalDate SS_DECEASED_DOD = LocalDate.parse("2017-12-31", dateTimeFormatter);
    private static final LocalDate SS_DECEASED_DOB = LocalDate.parse("2016-12-31", dateTimeFormatter);
    private static final String DATE_SUBMITTED = "2019-01-01";
    private static final String SS_DECEASED_HAS_ALIAS = YES;
    private static final String SS_DECEASED_FULL_ALIAS_NAME = "AliasFN AliasSN";
    private static final List<CollectionMember<ProbateFullAliasName>> SS_DECEASED_FULL_ALIAS_NAME_LIST = emptyList();
    private static final ProbateAddress SS_DECEASED_ADDRESS = Mockito.mock(ProbateAddress.class);

    private static final String SS_APPLICANT_FORENAMES = "Forenames";
    private static final String SS_APPLICANT_SURNAME = "Surname";
    private static final String SS_APPLICANT_EMAIL_ADDRESS = "primary@probate-test.com";
    private static final ProbateAddress SS_APPLICANT_ADDRESS = Mockito.mock(ProbateAddress.class);

    private static final LocalDate SS_EXPIRY_DATE = LocalDate.now().plusMonths(STANDING_SEARCH_LIFESPAN);
    private static final String SS_FORMATTED_EXPIRY_DATE = dateTimeFormatter.format(SS_EXPIRY_DATE);

    private static final String SS_RECORD_ID = "12345";
    private static final String SS_LEGACY_CASE_URL = "someUrl";
    private static final String SS_LEGACY_CASE_TYPE = "someCaseType";

    @InjectMocks
    private StandingSearchCallbackResponseTransformer underTest;

    @Mock
    private StandingSearchCallbackRequest standingSearchCallbackRequestMock;

    @Mock
    private StandingSearchDetails standingSearchDetailsMock;

    private StandingSearchData.StandingSearchDataBuilder standingSearchDataBuilder;

    @Before
    public void setup() {

        standingSearchDataBuilder = StandingSearchData.builder()
                .deceasedForenames(SS_DECEASED_FORENAMES)
                .deceasedSurname(SS_DECEASED_SURNAME)
                .deceasedDateOfDeath(SS_DECEASED_DOD)
                .deceasedDateOfBirth(SS_DECEASED_DOB)
                .deceasedAnyOtherNames(SS_DECEASED_HAS_ALIAS)
                .deceasedFullAliasNameList(SS_DECEASED_FULL_ALIAS_NAME_LIST)
                .deceasedAddress(SS_DECEASED_ADDRESS)
                .applicantForenames(SS_APPLICANT_FORENAMES)
                .applicantSurname(SS_APPLICANT_SURNAME)
                .applicantEmailAddress(SS_APPLICANT_EMAIL_ADDRESS)
                .applicantAddress(SS_APPLICANT_ADDRESS)
                .expiryDate(SS_EXPIRY_DATE)
                .recordId(SS_RECORD_ID)
                .legacyCaseViewUrl(SS_LEGACY_CASE_URL)
                .applicationSubmittedDate(DATE_SUBMITTED)
                .legacyType(SS_LEGACY_CASE_TYPE);
        ;

        when(standingSearchCallbackRequestMock.getCaseDetails()).thenReturn(standingSearchDetailsMock);
        when(standingSearchDetailsMock.getData()).thenReturn(standingSearchDataBuilder.build());
    }

    @Test
    public void shouldTransformStandingSearchCallbackRequestToStandingSearchCallbackResponse() {
        StandingSearchCallbackResponse standingSearchCallbackResponse = underTest.transform(standingSearchCallbackRequestMock);

        assertCommon(standingSearchCallbackResponse);
    }

    @Test
    public void shouldTransformCaseForStandingSearchWithDeceasedAliasNames() {
        List<CollectionMember<ProbateFullAliasName>> deceasedFullAliasNameList = new ArrayList<>();

        ProbateFullAliasName an11 = ProbateFullAliasName.builder().fullAliasName(SS_DECEASED_FULL_ALIAS_NAME).build();
        CollectionMember<ProbateFullAliasName> an1 = new CollectionMember<>("0", an11);
        deceasedFullAliasNameList.add(an1);
        standingSearchDataBuilder.deceasedFullAliasNameList(deceasedFullAliasNameList);

        when(standingSearchCallbackRequestMock.getCaseDetails()).thenReturn(standingSearchDetailsMock);
        when(standingSearchDetailsMock.getData()).thenReturn(standingSearchDataBuilder.build());

        StandingSearchCallbackResponse standingSearchCallbackResponse = underTest.transform(standingSearchCallbackRequestMock);

        assertCommonDetails(standingSearchCallbackResponse);
        assertEquals(1, standingSearchCallbackResponse.getResponseStandingSearchData().getDeceasedFullAliasNameList().size());
    }

    @Test
    public void shouldGetStandingSearchUploadedDocuments() {
        List<CollectionMember<UploadDocument>> documents = new ArrayList<>();
        documents.add(createUploadDocuments("0"));
        standingSearchDataBuilder.documentsUploaded(documents);

        when(standingSearchCallbackRequestMock.getCaseDetails()).thenReturn(standingSearchDetailsMock);
        when(standingSearchDetailsMock.getData()).thenReturn(standingSearchDataBuilder.build());

        StandingSearchCallbackResponse standingSearchCallbackResponse = underTest.transform(standingSearchCallbackRequestMock);

        assertCommonDetails(standingSearchCallbackResponse);
        assertEquals(1, standingSearchCallbackResponse.getResponseStandingSearchData().getDocumentsUploaded().size());
    }

    @Test
    public void shouldConvertRequestToDataBeanWithStandingSearchExpiryDateChange() {
        StandingSearchCallbackResponse standingSearchCallbackResponse = underTest.standingSearchCreated(standingSearchCallbackRequestMock);

        assertCommon(standingSearchCallbackResponse);

        assertEquals(SS_FORMATTED_EXPIRY_DATE, standingSearchCallbackResponse.getResponseStandingSearchData().getExpiryDate());
    }

    private void assertCommon(StandingSearchCallbackResponse standingSearchCallbackResponse) {
        assertCommonDetails(standingSearchCallbackResponse);
        assertApplicationType(standingSearchCallbackResponse, SS_APPLICATION_TYPE);
    }

    private void assertCommonDetails(StandingSearchCallbackResponse standingSearchCallbackResponse) {
        assertEquals(SS_REGISTRY_LOCATION, standingSearchCallbackResponse.getResponseStandingSearchData().getRegistryLocation());

        assertEquals(SS_DECEASED_FORENAMES, standingSearchCallbackResponse.getResponseStandingSearchData().getDeceasedForenames());
        assertEquals(SS_DECEASED_SURNAME, standingSearchCallbackResponse.getResponseStandingSearchData().getDeceasedSurname());
        assertEquals("2017-12-31", standingSearchCallbackResponse.getResponseStandingSearchData().getDeceasedDateOfDeath());
        assertEquals("2016-12-31", standingSearchCallbackResponse.getResponseStandingSearchData().getDeceasedDateOfBirth());
        assertEquals(SS_DECEASED_HAS_ALIAS, standingSearchCallbackResponse.getResponseStandingSearchData().getDeceasedAnyOtherNames());
        assertEquals(SS_DECEASED_ADDRESS, standingSearchCallbackResponse.getResponseStandingSearchData().getDeceasedAddress());

        assertEquals(SS_APPLICANT_FORENAMES, standingSearchCallbackResponse.getResponseStandingSearchData().getApplicantForenames());
        assertEquals(SS_APPLICANT_SURNAME, standingSearchCallbackResponse.getResponseStandingSearchData().getApplicantSurname());
        assertEquals(SS_APPLICANT_EMAIL_ADDRESS, standingSearchCallbackResponse.getResponseStandingSearchData().getApplicantEmailAddress());
        assertEquals(SS_APPLICANT_ADDRESS, standingSearchCallbackResponse.getResponseStandingSearchData().getApplicantAddress());

        assertEquals(SS_FORMATTED_EXPIRY_DATE, standingSearchCallbackResponse.getResponseStandingSearchData().getExpiryDate());
        assertEquals(DATE_SUBMITTED, standingSearchCallbackResponse.getResponseStandingSearchData().getApplicationSubmittedDate());

        assertEquals(SS_RECORD_ID, standingSearchCallbackResponse.getResponseStandingSearchData().getRecordId());
        assertEquals(SS_LEGACY_CASE_TYPE, standingSearchCallbackResponse.getResponseStandingSearchData().getLegacyType());
        assertEquals(SS_LEGACY_CASE_URL, standingSearchCallbackResponse.getResponseStandingSearchData().getLegacyCaseViewUrl());

    }

    private void assertApplicationType(StandingSearchCallbackResponse standingSearchCallbackResponse, ApplicationType ssApplicationType) {
        assertEquals(ssApplicationType, standingSearchCallbackResponse.getResponseStandingSearchData().getApplicationType());
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

}
