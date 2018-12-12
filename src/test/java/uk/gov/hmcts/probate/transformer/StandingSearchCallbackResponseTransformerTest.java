package uk.gov.hmcts.probate.transformer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.ProbateAddress;
import uk.gov.hmcts.probate.model.ccd.ProbateFullAliasName;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
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
    private static final String SS_DECEASED_HAS_ALIAS = YES;
    private static final String SS_DECEASED_FULL_ALIAS_NAME = "AliasFN AliasSN";
    private static final List<CollectionMember<ProbateFullAliasName>> SS_DECEASED_FULL_ALIAS_NAME_LIST = emptyList();
    private static final ProbateAddress SS_DECEASED_ADDRESS = Mockito.mock(ProbateAddress.class);

    private static final String SS_APPLICANT_FORENAMES = "Forenames";
    private static final String SS_APPLICANT_SURNAME = "Surname";
    private static final String SS_APPLICANT_EMAIL_ADDRESS = "applicant@email.com";
    private static final ProbateAddress SS_APPLICANT_ADDRESS = Mockito.mock(ProbateAddress.class);

    private static final LocalDate SS_EXPIRY_DATE = LocalDate.now().plusMonths(STANDING_SEARCH_LIFESPAN);
    private static final String SS_FORMATTED_EXPIRY_DATE = dateTimeFormatter.format(SS_EXPIRY_DATE);

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
                .expiryDate(SS_EXPIRY_DATE);

        when(standingSearchCallbackRequestMock.getStandingSearchDetails()).thenReturn(standingSearchDetailsMock);
        when(standingSearchDetailsMock.getStandingSearchData()).thenReturn(standingSearchDataBuilder.build());
    }

    @Test
    public void shouldTransformStandingSearchCallbackRequestToStandingSearchCallbackResponse() {
        StandingSearchCallbackResponse standingSearchCallbackResponse = underTest.transform(standingSearchCallbackRequestMock);

        assertCommon(standingSearchCallbackResponse);
    }

    @Test
    public void shouldTransformCaseForCaveatWithDeceasedAliasNames() {
        List<CollectionMember<ProbateFullAliasName>> deceasedFullAliasNameList = new ArrayList<>();

        ProbateFullAliasName an11 = ProbateFullAliasName.builder().fullAliasName(SS_DECEASED_FULL_ALIAS_NAME).build();
        CollectionMember<ProbateFullAliasName> an1 = new CollectionMember<>("0", an11);
        deceasedFullAliasNameList.add(an1);
        standingSearchDataBuilder.deceasedFullAliasNameList(deceasedFullAliasNameList);

        when(standingSearchCallbackRequestMock.getStandingSearchDetails()).thenReturn(standingSearchDetailsMock);
        when(standingSearchDetailsMock.getStandingSearchData()).thenReturn(standingSearchDataBuilder.build());

        StandingSearchCallbackResponse standingSearchCallbackResponse = underTest.transform(standingSearchCallbackRequestMock);

        assertCommonDetails(standingSearchCallbackResponse);
        assertEquals(1, standingSearchCallbackResponse.getResponseStandingSearchData().getDeceasedFullAliasNameList().size());
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
    }

    private void assertApplicationType(StandingSearchCallbackResponse standingSearchCallbackResponse, ApplicationType ssApplicationType) {
        assertEquals(ssApplicationType, standingSearchCallbackResponse.getResponseStandingSearchData().getApplicationType());
    }

}
