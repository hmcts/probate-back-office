package uk.gov.hmcts.probate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.probate.model.CaseType;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.ProbateFullAliasName;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.caveat.response.CaveatCallbackResponse;
import uk.gov.hmcts.probate.model.ccd.raw.AliasName;
import uk.gov.hmcts.probate.model.ccd.raw.CaseLink;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;
import uk.gov.hmcts.probate.service.CaseMatchingService;
import uk.gov.hmcts.probate.service.LegacyImportService;
import uk.gov.hmcts.probate.service.user.UserInfoService;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.hmcts.probate.transformer.CaveatCallbackResponseTransformer;
import uk.gov.hmcts.probate.transformer.StandingSearchCallbackResponseTransformer;
import uk.gov.hmcts.probate.transformer.WillLodgementCallbackResponseTransformer;

import jakarta.servlet.http.HttpServletRequest;
import uk.gov.hmcts.reform.probate.model.idam.UserInfo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

class CaseMatchingControllerUnitTest {

    @InjectMocks
    private CaseMatchingController underTest;
    @Mock
    private CallbackResponseTransformer callbackResponseTransformer;
    @Mock
    private CaveatCallbackResponseTransformer caveatCallbackResponseTransformer;
    @Mock
    private StandingSearchCallbackResponseTransformer standingSearchCallbackResponseTransformer;
    @Mock
    private WillLodgementCallbackResponseTransformer willLodgementCallbackResponseTransformer;
    @Mock
    private CaseMatchingService caseMatchingService;
    @Mock
    private LegacyImportService legacyImportService;
    @Mock
    private BusinessValidationMessageService businessValidationMessageService;
    @Mock
    private HttpServletRequest httpServletRequestMock;

    @Mock
    private CallbackRequest callbackRequest;
    @Mock
    private CaseDetails caseDetailsMock;
    @Mock
    private CaveatCallbackRequest caveatCallbackRequest;
    @Mock
    private CaveatDetails caveatDetailsMock;
    @Mock
    private CaseData caseDataMock;
    @Mock
    private CaveatData caveatDataMock;
    @Mock
    private List<CollectionMember<CaseMatch>> caseMatchMock;
    @Mock
    private CallbackResponse callbackResponse;
    @Mock
    private CaveatCallbackResponse caveatCallbackResponse;
    @Mock
    private UserInfoService userInfoService;

    private List<CaseMatch> caseMatches = new ArrayList<>();

    private static final Optional<UserInfo> CASEWORKER_USERINFO = Optional.ofNullable(UserInfo.builder()
            .familyName("familyName")
            .givenName("givenname")
            .roles(Arrays.asList("caseworker-probate"))
            .build());

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        underTest = new CaseMatchingController(callbackResponseTransformer, caveatCallbackResponseTransformer,
                standingSearchCallbackResponseTransformer, willLodgementCallbackResponseTransformer,
                caseMatchingService, legacyImportService, businessValidationMessageService, userInfoService);

        CaseMatch ccdCase = CaseMatch.builder()
                .dob("dob")
                .dod("dod")
                .aliases("aliases")
                .caseLink(CaseLink.builder().caseReference("caseReference").build())
                .postcode("postcode")
                .type("type")
                .fullName("fullname")
                .build();

        CaseMatch ccdCaseNoId = CaseMatch.builder()
                .dob("dob")
                .dod("dod")
                .aliases("aliases")
                .postcode("postcode")
                .type("type")
                .fullName("fullname")
                .build();

        CaseMatch legacyCase = CaseMatch.builder()
                .id("id")
                .dob("dob")
                .dod("dod")
                .aliases("aliases")
                .postcode("postcode")
                .type("type")
                .fullName("fullname")
                .recordId("recordId")
                .legacyCaseViewUrl("legacyCaseViewUrl")
                .build();

        caseMatches.add(ccdCase);
        caseMatches.add(legacyCase);
        caseMatches.add(ccdCaseNoId);
        when(caseMatchingService.findCrossMatches(eq(CaseType.getAll()), any())).thenReturn(caseMatches);
        doReturn(CASEWORKER_USERINFO).when(userInfoService).getCaseworkerInfo();
    }

    @Test
    void shouldDoSearchFromGrantFlow() {
        CaseData caseData = CaseData.builder()
                .recordId("recordId")
                .deceasedForenames("deceasedForenames")
                .deceasedSurname("deceasedSurname")
                .solsDeceasedAliasNamesList(Arrays.asList(
                        new CollectionMember(null, AliasName.builder()
                                .solsAliasname("PETER PIPER KRENT").build())))
                .deceasedDateOfBirth(LocalDate.of(1950,10,10))
                .deceasedDateOfDeath(LocalDate.of(2000,10,10))
                .build();
        CaseDetails caseDetails = new CaseDetails(caseData, new String[]{"2022", "1", "1", "1"}, 0L);
        when(callbackRequest.getCaseDetails()).thenReturn(caseDetails);
        when(callbackResponseTransformer
                .addMatches(callbackRequest, caseMatches, Optional.empty())).thenReturn(callbackResponse);
        ResponseEntity<CallbackResponse> response = underTest.search(callbackRequest);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(callbackResponse));
    }

    @Test
    void shouldDoSearchFromCaveatFlow() {
        CaveatData caveatData = CaveatData.builder()
                .recordId("recordId")
                .deceasedForenames("deceasedForenames")
                .deceasedSurname("deceasedSurname")
                .deceasedFullAliasNameList(Arrays.asList(
                        new CollectionMember(null, ProbateFullAliasName.builder()
                                .fullAliasName("PETER PIPER KRENT").build())))
                .deceasedDateOfBirth(LocalDate.of(1950,10,10))
                .deceasedDateOfDeath(LocalDate.of(2000,10,10))
                .build();
        CaveatDetails caveatDetails = new CaveatDetails(caveatData, new String[]{"2022", "1", "1", "1"}, 0L);
        when(caveatCallbackRequest.getCaseDetails()).thenReturn(caveatDetails);
        when(caveatCallbackResponseTransformer.addMatches(caveatCallbackRequest, caseMatches))
                .thenReturn(caveatCallbackResponse);
        ResponseEntity<CaveatCallbackResponse> response = underTest.searchFromCaveatFlow(caveatCallbackRequest);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(caveatCallbackResponse));
    }

    @Test
    void shouldDoImportFromGrantFlow() {
        when(callbackRequest.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(caseDataMock.getCaseMatches()).thenReturn(caseMatchMock);
        when(legacyImportService.areLegacyRowsValidToImport(caseMatchMock)).thenReturn(true);
        when(legacyImportService.importLegacyRows(caseMatchMock)).thenReturn(caseMatches);
        when(callbackResponseTransformer.addMatches(callbackRequest, caseMatches, CASEWORKER_USERINFO))
                .thenReturn(callbackResponse);
        ResponseEntity<CallbackResponse> response = underTest
                .doImportFromGrant(callbackRequest, httpServletRequestMock);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(callbackResponse));
    }

    @Test
    void shouldDoImportFromCaveatFlow() {
        when(caveatCallbackRequest.getCaseDetails()).thenReturn(caveatDetailsMock);
        when(caveatDetailsMock.getData()).thenReturn(caveatDataMock);
        when(caveatDataMock.getCaseMatches()).thenReturn(caseMatchMock);
        when(legacyImportService.areLegacyRowsValidToImport(caseMatchMock)).thenReturn(true);
        when(legacyImportService.importLegacyRows(caseMatchMock)).thenReturn(caseMatches);
        when(caveatCallbackResponseTransformer.addMatches(caveatCallbackRequest, caseMatches))
                .thenReturn(caveatCallbackResponse);
        ResponseEntity<CaveatCallbackResponse> response = underTest
                .doImportFromCaveat(caveatCallbackRequest, httpServletRequestMock);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(caveatCallbackResponse));
    }
}
