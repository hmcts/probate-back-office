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
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.hmcts.probate.insights.AppInsights;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.LifeEventCCDService;
import uk.gov.hmcts.probate.service.LifeEventCallbackResponseService;
import uk.gov.hmcts.probate.util.TestUtils;
import uk.gov.hmcts.probate.validator.LifeEventValidationRule;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class LifeEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestUtils testUtils;

    @MockBean
    private AppInsights appInsights;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private LifeEventCCDService lifeEventCCDService;

    @MockBean
    private LifeEventCallbackResponseService lifeEventCallbackResponseService;

    @MockBean
    private SecurityUtils securityUtils;

    @MockBean
    private LifeEventValidationRule lifeEventValidationRule;

    @Captor
    private ArgumentCaptor<CaseDetails> caseDetailsArgumentCaptor;

    @Captor
    private ArgumentCaptor<CallbackRequest> callbackRequestArgumentCaptor;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void lifeEventUpdateShouldReturnDataPayloadOkResponseCode() throws Exception {

        String payload = testUtils.getStringFromFile("lifeEventPayload.json");

        mockMvc.perform(post("/lifeevent/update")
                .content(payload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")));

        verify(lifeEventCCDService).verifyDeathRecord(caseDetailsArgumentCaptor.capture(), any());
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
            .content(payload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(lifeEventValidationRule).validate(any());
    }

    @Test
    void shouldValidateSelectFromMultipleRecords() throws Exception {
        String payload = testUtils.getStringFromFile("lifeEventSelectFromMultipleRecords.json");

        mockMvc.perform(post("/lifeevent/selectFromMultipleRecords")
            .content(payload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(lifeEventValidationRule).validate(any());
    }

}
