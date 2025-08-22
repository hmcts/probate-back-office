package uk.gov.hmcts.probate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.LifeEventCCDService;
import uk.gov.hmcts.probate.service.LifeEventCallbackResponseService;
import uk.gov.hmcts.probate.service.user.UserInfoService;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.hmcts.probate.util.TestUtils;
import uk.gov.hmcts.probate.validator.LifeEventValidationRule;
import uk.gov.hmcts.reform.probate.model.idam.UserInfo;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.reform.probate.model.cases.HandoffReasonId.AD_COLLIGENDA_BONA;
import static uk.gov.hmcts.reform.probate.model.cases.HandoffReasonId.FOREIGN_WILL;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class LifeEventControllerIT {
    private static final String AUTH_HEADER = "Authorization";
    private static final String AUTH_TOKEN = "Bearer someAuthorizationToken";
    private static final Optional<UserInfo> CASEWORKER_USERINFO = Optional.ofNullable(UserInfo.builder()
            .familyName("familyName")
            .givenName("givenname")
            .roles(Arrays.asList("caseworker-probate"))
            .build());

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private LifeEventCCDService lifeEventCCDService;

    @MockitoBean
    private LifeEventCallbackResponseService lifeEventCallbackResponseService;

    @MockitoBean
    private SecurityUtils securityUtils;

    @MockitoBean
    private UserInfoService userInfoService;

    @MockitoBean
    private LifeEventValidationRule lifeEventValidationRule;

    @MockitoBean
    private CallbackResponseTransformer callbackResponseTransformer;

    @Captor
    private ArgumentCaptor<CaseDetails> caseDetailsArgumentCaptor;

    @Captor
    private ArgumentCaptor<CallbackRequest> callbackRequestArgumentCaptor;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        doReturn(CASEWORKER_USERINFO).when(userInfoService).getCaseworkerInfo();
    }

    @Test
    void lifeEventUpdateShouldReturnDataPayloadOkResponseCode() throws Exception {

        String payload = testUtils.getStringFromFile("lifeEventPayload.json");
        SecurityDTO securityDTO = SecurityDTO.builder()
                .authorisation("AUTH_TOKEN")
                .serviceAuthorisation("serviceAuth")
                .build();
        when(securityUtils.getSecurityDTO()).thenReturn(securityDTO);
        when(securityUtils.getRoles(anyString())).thenReturn(List.of("citizen"));

        mockMvc.perform(post("/lifeevent/update")
                .content(payload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")));

        verify(lifeEventCCDService).verifyDeathRecord(caseDetailsArgumentCaptor.capture(), any(), anyBoolean());
        final CaseDetails caseDetailsArgumentCaptorValue = caseDetailsArgumentCaptor.getValue();
        assertEquals(caseDetailsArgumentCaptorValue.getId().longValue(), 1621002468661478L);
        final CaseData data = caseDetailsArgumentCaptorValue.getData();
        assertEquals("John", data.getDeceasedForenames());
        assertEquals("Cook", data.getDeceasedSurname());
        assertEquals("2006-11-16", data.getDeceasedDateOfDeath().toString());
        verify(securityUtils).getSecurityDTO();
    }

    @Test
    void lifeEventUpdateShouldReturnDataPayloadOkResponseCodeForCaseworkerUser() throws Exception {

        String payload = testUtils.getStringFromFile("lifeEventPayload.json");
        SecurityDTO securityDTO = SecurityDTO.builder()
                .authorisation("AUTH_TOKEN")
                .serviceAuthorisation("serviceAuth")
                .build();
        when(securityUtils.getSecurityDTO()).thenReturn(securityDTO);
        when(securityUtils.getRoles(anyString())).thenReturn(List.of("caseworker-probate"));

        mockMvc.perform(post("/lifeevent/update")
                        .content(payload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")));

        verify(lifeEventCCDService).verifyDeathRecord(caseDetailsArgumentCaptor.capture(), any(), anyBoolean());
        final CaseDetails caseDetailsArgumentCaptorValue = caseDetailsArgumentCaptor.getValue();
        assertEquals(caseDetailsArgumentCaptorValue.getId().longValue(), 1621002468661478L);
        final CaseData data = caseDetailsArgumentCaptorValue.getData();
        assertEquals("John", data.getDeceasedForenames());
        assertEquals("Cook", data.getDeceasedSurname());
        assertEquals("2006-11-16", data.getDeceasedDateOfDeath().toString());
        verify(securityUtils).getSecurityDTO();
    }

    @Test
    void shouldCountDeathRecords() throws Exception {
        String payload = testUtils.getStringFromFile("lifeEventSelectFromMultipleRecordsAboutToStart.json");

        mockMvc.perform(post("/lifeevent/selectFromMultipleRecordsAboutToStart")
                .header(AUTH_HEADER, AUTH_TOKEN)
                .content(payload)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(lifeEventCallbackResponseService).setNumberOfDeathRecords(callbackRequestArgumentCaptor.capture());
        final CallbackRequest callbackRequest = callbackRequestArgumentCaptor.getValue();
        assertEquals(1621002468661478L, callbackRequest.getCaseDetails().getId());
    }

    @Test
    void shouldValidate() throws Exception {
        String payload = testUtils.getStringFromFile("lifeEventSelectFromMultipleRecords.json");

        mockMvc.perform(post("/lifeevent/selectFromMultipleRecords")
            .header(AUTH_HEADER, AUTH_TOKEN)
            .content(payload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(lifeEventValidationRule).validate(any());
    }

    @Test
    void shouldLookupDeathRecordByNameAndDate() throws Exception {
        String payload = testUtils.getStringFromFile("lifeEventPayload.json");

        mockMvc.perform(post("/lifeevent/manualUpdateAboutToStart")
            .content(payload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(lifeEventCallbackResponseService).getDeathRecordsByNamesAndDate(callbackRequestArgumentCaptor.capture());
        final CallbackRequest callbackRequest = callbackRequestArgumentCaptor.getValue();
        assertEquals(1621002468661478L, callbackRequest.getCaseDetails().getId());
    }

    @Test
    void shouldValidateManualUpdate() throws Exception {
        String payload = testUtils.getStringFromFile("lifeEventSelectFromMultipleRecords.json");

        mockMvc.perform(post("/lifeevent/manualUpdate")
            .header(AUTH_HEADER, AUTH_TOKEN)
            .content(payload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(lifeEventValidationRule).validate(any());
    }

    @Test
    void shouldValidateSelectFromMultipleRecords() throws Exception {
        String payload = testUtils.getStringFromFile("lifeEventSelectFromMultipleRecords.json");

        mockMvc.perform(post("/lifeevent/selectFromMultipleRecords")
            .header(AUTH_HEADER, AUTH_TOKEN)
            .content(payload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(lifeEventValidationRule).validate(any());
    }

    @Test
    void shouldPreserveHandoffReasons() throws Exception {
        String payload = testUtils.getStringFromFile("existingHandoffReason.json");

        mockMvc.perform(post("/lifeevent/handOffToLegacySite")
                        .header(AUTH_HEADER, AUTH_TOKEN)
                        .content(payload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(callbackResponseTransformer).updateTaskList(callbackRequestArgumentCaptor.capture(), any());
        final CallbackRequest callbackRequest = callbackRequestArgumentCaptor.getValue();
        CaseDetails caseDetails = callbackRequest.getCaseDetails();
        assertEquals(1621002468661478L, caseDetails.getId().longValue());
        final CaseData caseData = caseDetails.getData();
        assertEquals(YES, caseData.getCaseHandedOffToLegacySite());
        assertEquals(FOREIGN_WILL, caseData.getBoHandoffReasonList()
                .getFirst().getValue().getCaseHandoffReason());
        assertEquals(AD_COLLIGENDA_BONA, caseData.getBoHandoffReasonList()
                .getLast().getValue().getCaseHandoffReason());
    }
}
