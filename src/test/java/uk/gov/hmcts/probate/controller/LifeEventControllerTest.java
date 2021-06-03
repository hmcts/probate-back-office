package uk.gov.hmcts.probate.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.hmcts.probate.insights.AppInsights;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.DeathRecord;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.LifeEventService;
import uk.gov.hmcts.probate.util.TestUtils;
import uk.gov.hmcts.probate.validator.LifeEventValidationRule;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class LifeEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestUtils testUtils;

    @MockBean
    private AppInsights appInsights;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private LifeEventService lifeEventService;

    @MockBean
    private SecurityUtils securityUtils;
    
    @MockBean
    private LifeEventValidationRule lifeEventValidationRule;

    @Captor
    private ArgumentCaptor<CaseDetails> caseDetailsArgumentCaptor;
    
    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void lifeEventUpdateShouldReturnDataPayloadOkResponseCode() throws Exception {

        String payload = testUtils.getStringFromFile("lifeEventPayload.json");

        mockMvc.perform(post("/lifeevent/update")
                .content(payload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data")));
        
        verify(lifeEventService).verifyDeathRecord(caseDetailsArgumentCaptor.capture(), any());
        final CaseDetails caseDetailsArgumentCaptorValue = caseDetailsArgumentCaptor.getValue();
        assertThat(caseDetailsArgumentCaptorValue.getId()).isEqualTo(1621002468661478L);
        final CaseData data = caseDetailsArgumentCaptorValue.getData();
        assertThat(data.getDeceasedForenames()).isEqualTo("John");
        assertThat(data.getDeceasedSurname()).isEqualTo("Cook");
        assertThat(data.getDeceasedDateOfDeath().toString()).isEqualTo("2006-11-16");
        verify(securityUtils).getSecurityDTO();
    }

    @Test
    public void shouldLookupDeathRecord() throws Exception {
        String payload = testUtils.getStringFromFile("lifeEventUpdateWithSystemNumberPayload.json");
        DeathRecord deathRecord = DeathRecord.builder().systemNumber(500035096).build();
        when(lifeEventService.getDeathRecordById(eq(500035096))).thenReturn(deathRecord);

        mockMvc.perform(post("/lifeevent/updateWithSystemNumber")
            .content(payload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.deathRecord", notNullValue()));
        
        verify(lifeEventService).getDeathRecordById(eq(500035096));
    }

    @Test
    public void shouldLookupDeathRecordByNameAndDate() throws Exception {
        String payload = testUtils.getStringFromFile("lifeEventPayload.json");
        final LocalDate dateOfDeath = LocalDate.of(2006, 11, 16);
        DeathRecord deathRecord = DeathRecord.builder().name("John Cook").dateOfDeath(dateOfDeath).build();
        CollectionMember collectionMember = new CollectionMember(null, deathRecord);
        List results = List.of(collectionMember);
        when(lifeEventService.getDeathRecordsByNamesAndDate(any())).thenReturn(results);

        mockMvc.perform(post("/lifeevent/manualUpdateAboutToStart")
            .content(payload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.deathRecords", notNullValue()))
            .andExpect(jsonPath("$.data.deathRecords", hasSize(1)))
            .andExpect(jsonPath("$.data.deathRecords[0].value.name").value("John Cook"));
    }

    @Test
    public void shouldValidateManualUpdate() throws Exception {
        String payload = testUtils.getStringFromFile("lifeEventSelectFromMultipleRecords.json");

        mockMvc.perform(post("/lifeevent/manualUpdate")
            .content(payload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(lifeEventValidationRule).validate(any());
    }
    
    @Test
    public void shouldCountDeathRecords() throws Exception {
        String payload = testUtils.getStringFromFile("lifeEventSelectFromMultipleRecordsAboutToStart.json");

        mockMvc.perform(post("/lifeevent/selectFromMultipleRecordsAboutToStart")
            .content(payload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.numberOfDeathRecords", is(2)))
            .andExpect(jsonPath("$.errors", nullValue()));
    }
    
    @Test
    public void shouldValidateSelectFromMultipleRecords() throws Exception {
        String payload = testUtils.getStringFromFile("lifeEventSelectFromMultipleRecords.json");

        mockMvc.perform(post("/lifeevent/selectFromMultipleRecords")
            .content(payload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        
        verify(lifeEventValidationRule).validate(any());
    }

}
