package uk.gov.hmcts.probate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.FieldError;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.probateman.CaveatCreator;
import uk.gov.hmcts.probate.model.probateman.GrantApplicationCreator;
import uk.gov.hmcts.probate.model.probateman.ProbateManModel;
import uk.gov.hmcts.probate.model.probateman.ProbateManType;
import uk.gov.hmcts.probate.model.probateman.StandingSearchCreator;
import uk.gov.hmcts.probate.model.probateman.WillLodgementCreator;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;
import uk.gov.hmcts.probate.service.LegacyImportService;
import uk.gov.hmcts.probate.service.LegacySearchService;
import uk.gov.hmcts.probate.service.ProbateManService;
import uk.gov.hmcts.probate.util.FileUtils;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = ProbateManController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ProbateManControllerTest {

    private static final String ID = "1234567";

    private static final String GRANT_APPLICATION_URL = "/probateManTypes/GRANT_APPLICATION/cases/";

    private static final String WILL_LODGEMENT_URL = "/probateManTypes/WILL_LODGEMENT/cases/";

    private static final String CAVEAT_URL = "/probateManTypes/CAVEAT/cases/";

    private static final String STANDING_SEARCH_URL = "/probateManTypes/STANDING_SEARCH/cases/";

    private static final String LEGACY_SEARCH_URL = "/legacy/search/";

    private static final String LEGACY_IMPORT_URL = "/legacy/doImport/";

    private static final String LEGACY_RESET_SEARCH_URL = "/legacy/resetSearch/";

    @MockBean
    private ProbateManService probateManService;

    @MockBean
    private LegacySearchService legacySearchService;

    @MockBean
    private LegacyImportService legacyImportService;

    @MockBean
    private BusinessValidationMessageService businessValidationMessageService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldGetGrantApplication() throws Exception {
        ProbateManModel grantApplication = GrantApplicationCreator.create();
        when(probateManService.getProbateManModel(1234567L, ProbateManType.GRANT_APPLICATION))
                .thenReturn(grantApplication);

        mockMvc.perform(get(GRANT_APPLICATION_URL + ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(FileUtils.getStringFromFile("probateman/grantApplication.json")));

        verify(probateManService, times(1)).getProbateManModel(1234567L, ProbateManType.GRANT_APPLICATION);
    }

    @Test
    public void shouldGetWillLodgement() throws Exception {
        ProbateManModel willLodgement = WillLodgementCreator.create();
        when(probateManService.getProbateManModel(1234567L, ProbateManType.WILL_LODGEMENT))
                .thenReturn(willLodgement);
        mockMvc.perform(get(WILL_LODGEMENT_URL + ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(FileUtils.getStringFromFile("probateman/willLodgement.json")));

        verify(probateManService, times(1)).getProbateManModel(1234567L, ProbateManType.WILL_LODGEMENT);
    }

    @Test
    public void shouldGetCaveat() throws Exception {
        ProbateManModel caveat = CaveatCreator.create();
        when(probateManService.getProbateManModel(1234567L, ProbateManType.CAVEAT))
                .thenReturn(caveat);

        mockMvc.perform(get(CAVEAT_URL + ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(FileUtils.getStringFromFile("probateman/caveat.json")));

        verify(probateManService, times(1)).getProbateManModel(1234567L, ProbateManType.CAVEAT);
    }

    @Test
    public void shouldGetStandingSearch() throws Exception {
        ProbateManModel standingSearch = StandingSearchCreator.create();
        when(probateManService.getProbateManModel(1234567L, ProbateManType.STANDING_SEARCH))
                .thenReturn(standingSearch);

        mockMvc.perform(get(STANDING_SEARCH_URL + ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(FileUtils.getStringFromFile("probateman/standingSearch.json")));

        verify(probateManService, times(1)).getProbateManModel(1234567L, ProbateManType.STANDING_SEARCH);
    }

    @Test
    public void shouldPostLegacySearch() throws Exception {
        CaseData.CaseDataBuilder caseDataBuilder = CaseData.builder();
        CaseData caseData = caseDataBuilder.build();
        CaseDetails caseDetails = new CaseDetails(caseData, null, null);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String json = objectMapper.writeValueAsString(callbackRequest);

        List<CollectionMember<CaseMatch>> caseMatchesList = new ArrayList<>();

        when(legacySearchService.findLegacyCaseMatches(caseDetails)).thenReturn(caseMatchesList);

        mockMvc.perform(post(LEGACY_SEARCH_URL)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldPostDoImport() throws Exception {
        CaseData.CaseDataBuilder caseDataBuilder = CaseData.builder();
        CaseData caseData = caseDataBuilder.build();
        CaseDetails caseDetails = new CaseDetails(caseData, null, null);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String json = objectMapper.writeValueAsString(callbackRequest);

        List<CaseMatch> caseMatchesList = new ArrayList<>();

        when(legacyImportService.importLegacyRows(caseData.getLegacySearchResultRows())).thenReturn(caseMatchesList);

        mockMvc.perform(post(LEGACY_IMPORT_URL)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldResetSearch() throws Exception {
        CaseData.CaseDataBuilder caseDataBuilder = CaseData.builder();
        CaseData caseData = caseDataBuilder.build();
        CaseDetails caseDetails = new CaseDetails(caseData, null, null);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String json = objectMapper.writeValueAsString(callbackRequest);

        List<CaseMatch> caseMatchesList = new ArrayList<>();

        FieldErrorResponse errorResponse = FieldErrorResponse.builder().message("someMessage").build();
        when(legacyImportService.importLegacyRows(caseData.getLegacySearchResultRows())).thenReturn(caseMatchesList);
        when(businessValidationMessageService
                .generateError(anyString(), anyString())).thenReturn(errorResponse);

        mockMvc.perform(post(LEGACY_RESET_SEARCH_URL)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk());
    }
}
