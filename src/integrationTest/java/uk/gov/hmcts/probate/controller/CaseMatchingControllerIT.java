package uk.gov.hmcts.probate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.criterion.CaseMatchingCriteria;
import uk.gov.hmcts.probate.service.CaseMatchingService;
import uk.gov.hmcts.probate.service.LegacyImportService;
import uk.gov.hmcts.probate.service.user.UserInfoService;
import uk.gov.hmcts.probate.util.TestUtils;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;
import uk.gov.hmcts.reform.probate.model.idam.UserInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.probate.model.CaseType.CAVEAT;
import static uk.gov.hmcts.probate.model.CaseType.GRANT_OF_REPRESENTATION;
import static uk.gov.hmcts.probate.model.CaseType.LEGACY;
import static uk.gov.hmcts.probate.model.CaseType.STANDING_SEARCH;
import static uk.gov.hmcts.probate.model.CaseType.WILL_LODGEMENT;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class CaseMatchingControllerIT {
    private static final Optional<UserInfo> CASEWORKER_USERINFO = Optional.ofNullable(UserInfo.builder()
            .familyName("familyName")
            .givenName("givenname")
            .roles(Arrays.asList("caseworker-probate"))
            .build());

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestUtils testUtils;

    @SpyBean(name = "caseMatchingService")
    private CaseMatchingService caseMatchingService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private CoreCaseDataApi coreCaseDataApi;

    @MockBean
    private LegacyImportService legacyImportService;

    @MockBean
    private UserInfoService userInfoService;

    @Captor
    private ArgumentCaptor<List<CollectionMember<CaseMatch>>> caseMatchListCaptor;

    @BeforeEach
    public void setUp() {
        doReturn(new ArrayList<>()).when(caseMatchingService).findMatches(any(), any(CaseMatchingCriteria.class));
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        doReturn(CASEWORKER_USERINFO).when(userInfoService).getCaseworkerInfo();
    }

    @Test
    void caseMatchingSearchFromGrantFlow() throws Exception {

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotifications.json");

        mockMvc.perform(post("/case-matching/search-from-grant-flow")
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")));

        verify(caseMatchingService).findMatches(eq(GRANT_OF_REPRESENTATION), any(CaseMatchingCriteria.class));
        verify(caseMatchingService).findMatches(eq(CAVEAT), any(CaseMatchingCriteria.class));
        verify(caseMatchingService).findMatches(eq(LEGACY), any(CaseMatchingCriteria.class));
        verify(caseMatchingService).findMatches(eq(STANDING_SEARCH), any(CaseMatchingCriteria.class));
        verify(caseMatchingService).findMatches(eq(WILL_LODGEMENT), any(CaseMatchingCriteria.class));
    }

    @Test
    void caseMatchingSearchFromCaveatFlow() throws Exception {

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotifications.json");

        mockMvc.perform(post("/case-matching/search-from-caveat-flow")
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")));

        verify(caseMatchingService).findMatches(eq(GRANT_OF_REPRESENTATION), any(CaseMatchingCriteria.class));
        verify(caseMatchingService).findMatches(eq(CAVEAT), any(CaseMatchingCriteria.class));
        verify(caseMatchingService).findMatches(eq(LEGACY), any(CaseMatchingCriteria.class));
        verify(caseMatchingService).findMatches(eq(STANDING_SEARCH), any(CaseMatchingCriteria.class));
        verify(caseMatchingService).findMatches(eq(WILL_LODGEMENT), any(CaseMatchingCriteria.class));
    }

    @Test
    void caseMatchingSearchFromStandingSearchFlow() throws Exception {

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotifications.json");

        mockMvc.perform(post("/case-matching/search-from-standing-search-flow")
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")));

        verify(caseMatchingService).findMatches(eq(GRANT_OF_REPRESENTATION), any(CaseMatchingCriteria.class));
        verify(caseMatchingService).findMatches(eq(CAVEAT), any(CaseMatchingCriteria.class));
        verify(caseMatchingService).findMatches(eq(LEGACY), any(CaseMatchingCriteria.class));
        verify(caseMatchingService).findMatches(eq(STANDING_SEARCH), any(CaseMatchingCriteria.class));
        verify(caseMatchingService).findMatches(eq(WILL_LODGEMENT), any(CaseMatchingCriteria.class));
    }

    @Test
    void caseMatchingSearchFromWillLodgementFlow() throws Exception {

        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadNotifications.json");

        mockMvc.perform(post("/case-matching/search-from-will-lodgement-flow")
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")));

        verify(caseMatchingService).findMatches(eq(GRANT_OF_REPRESENTATION), any(CaseMatchingCriteria.class));
        verify(caseMatchingService).findMatches(eq(CAVEAT), any(CaseMatchingCriteria.class));
        verify(caseMatchingService).findMatches(eq(LEGACY), any(CaseMatchingCriteria.class));
        verify(caseMatchingService).findMatches(eq(STANDING_SEARCH), any(CaseMatchingCriteria.class));
        verify(caseMatchingService).findMatches(eq(WILL_LODGEMENT), any(CaseMatchingCriteria.class));
    }

    @Test
    void caseMatchingImportFromGrantFlow() throws Exception {

        String solicitorPayload = testUtils.getStringFromFile("payloadWithCaseMatches.json");
        when(legacyImportService.areLegacyRowsValidToImport(any(List.class))).thenReturn(true);

        mockMvc.perform(post("/case-matching/import-legacy-from-grant-flow")
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")));


        verifyAllForImport();
    }

    @Test
    void caseMatchingImportFromGrantFlowWithInvalidRowSelection() throws Exception {

        String solicitorPayload = testUtils.getStringFromFile("payloadWithCaseMatches.json");
        when(legacyImportService.areLegacyRowsValidToImport(any(List.class))).thenReturn(false);

        mockMvc.perform(post("/case-matching/import-legacy-from-grant-flow")
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("errors")));
    }

    @Test
    void caseMatchingImportFromCaveatFlow() throws Exception {

        String solicitorPayload = testUtils.getStringFromFile("payloadWithCaseMatches.json");
        when(legacyImportService.areLegacyRowsValidToImport(any(List.class))).thenReturn(true);

        mockMvc.perform(post("/case-matching/import-legacy-from-caveat-flow")
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")));


        verifyAllForImport();
    }

    @Test
    void caseMatchingImportFromCaveatFlowWithInvalidRowSelection() throws Exception {

        String solicitorPayload = testUtils.getStringFromFile("payloadWithCaseMatches.json");
        when(legacyImportService.areLegacyRowsValidToImport(any(List.class))).thenReturn(false);

        mockMvc.perform(post("/case-matching/import-legacy-from-caveat-flow")
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")));
    }

    @Test
    void caseMatchingImportFromStandingSearchFlow() throws Exception {

        String solicitorPayload = testUtils.getStringFromFile("payloadWithCaseMatches.json");
        when(legacyImportService.areLegacyRowsValidToImport(any(List.class))).thenReturn(true);

        mockMvc.perform(post("/case-matching/import-legacy-from-standing-search-flow")
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")));


        verifyAllForImport();
    }

    @Test
    void caseMatchingImportFromStandingSearchFlowWithInvalidRowSelection() throws Exception {

        String solicitorPayload = testUtils.getStringFromFile("payloadWithCaseMatches.json");

        when(legacyImportService.areLegacyRowsValidToImport(any(List.class))).thenReturn(false);
        mockMvc.perform(post("/case-matching/import-legacy-from-standing-search-flow")
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("errors")));
    }

    @Test
    void caseMatchingImportFromWillLodgementFlow() throws Exception {

        String solicitorPayload = testUtils.getStringFromFile("payloadWithCaseMatches.json");
        when(legacyImportService.areLegacyRowsValidToImport(any(List.class))).thenReturn(true);

        mockMvc.perform(post("/case-matching/import-legacy-from-will-lodgement-flow")
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")));


        verifyAllForImport();
    }

    @Test
    void caseMatchingImportFromWillLodgementFlowWithInvalidRowSelection() throws Exception {

        String solicitorPayload = testUtils.getStringFromFile("payloadWithCaseMatches.json");

        when(legacyImportService.areLegacyRowsValidToImport(any(List.class))).thenReturn(false);
        mockMvc.perform(post("/case-matching/import-legacy-from-will-lodgement-flow")
                .content(solicitorPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("error")));
    }

    private void verifyAllForImport() {
        verify(legacyImportService).importLegacyRows(caseMatchListCaptor.capture());
        assertEquals(1, caseMatchListCaptor.getValue().size());
        CaseMatch caseMatchFound = caseMatchListCaptor.getValue().get(0).getValue();
        assertEquals("1", caseMatchFound.getId());
        assertEquals("DecAN1 DecAN2", caseMatchFound.getAliases());
        assertEquals("1111222233334444", caseMatchFound.getCaseLink().getCaseReference());
        assertEquals("Some comment", caseMatchFound.getComment());
        assertEquals("1999-01-01", caseMatchFound.getDob());
        assertEquals("2018-01-01", caseMatchFound.getDod());
        assertEquals("Y", caseMatchFound.getDoImport());
        assertEquals("DecFN DecSN", caseMatchFound.getFullName());
        assertEquals("http://localhost/print/probateManType/Grant/cases/1", caseMatchFound.getLegacyCaseViewUrl());
        assertEquals("HP5 2PN", caseMatchFound.getPostcode());
        assertEquals("Legacy Grant", caseMatchFound.getType());
        assertEquals("N", caseMatchFound.getValid());
    }
}
