package uk.gov.hmcts.probate.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import uk.gov.hmcts.probate.model.ocr.OCRField;
import uk.gov.hmcts.probate.service.ocr.OCRPopulatedValueMapper;
import uk.gov.hmcts.probate.service.ocr.OCRToCCDMandatoryField;
import uk.gov.hmcts.probate.util.TestUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.EMPTY_LIST;
import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class OCRFormsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private AppInsights appInsights;

    @Autowired
    private TestUtils testUtils;

    @MockBean
    private OCRPopulatedValueMapper ocrPopulatedValueMapper;

    @MockBean
    private OCRToCCDMandatoryField ocrToCCDMandatoryField;

    private String ocrPayload;
    private List<OCRField> ocrFields = new ArrayList<>();
    private List<String> warnings = new ArrayList<>();

    @Before
    public void setUp() throws IOException {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        ocrPayload = testUtils.getStringFromFile("expectedOCRData.json");
        OCRField field1 = OCRField.builder()
                .name("deceasedForenames")
                .value("John")
                .description("Deceased forename").build();
        ocrFields.add(field1);
        warnings.add("test warning");
        when(ocrPopulatedValueMapper.ocrPopulatedValueMapper(any())).thenReturn(ocrFields);
        when(ocrToCCDMandatoryField.ocrToCCDMandatoryFields(eq(ocrFields), any(), any())).thenReturn(EMPTY_LIST);
    }

    @Test
    public void testNoWarningsReturnOkResponseAndSuccessResponseStateForPA1P() throws Exception {
        mockMvc.perform(post("/forms/PA1P/validate-ocr")
                .content(ocrPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("SUCCESS")));
    }

    @Test
    public void testWarningsPopulateListAndReturnOkWithWarningsResponseState() throws Exception {
        when(ocrToCCDMandatoryField.ocrToCCDMandatoryFields(any(), any(), any())).thenReturn(warnings);
        mockMvc.perform(post("/forms/PA1P/validate-ocr")
                .content(ocrPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("WARNINGS")))
                .andExpect(content().string(containsString("test warning")));
    }

    @Test
    public void testNoWarningsReturnOkResponseAndSuccessResponseStateForPA1A() throws Exception {
        mockMvc.perform(post("/forms/PA1A/validate-ocr")
                .content(ocrPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("SUCCESS")));
    }

    @Test
    public void testNoWarningsReturnOkResponseAndSuccessResponseStateForPA8A() throws Exception {
        mockMvc.perform(post("/forms/PA8A/validate-ocr")
                .content(ocrPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("SUCCESS")));
    }

    @Test
    public void testInvalidFormTypeThrowsNotFound() throws Exception {
        mockMvc.perform(post("/forms/test/validate-ocr")
                .content(ocrPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString("Form type 'test' not found")));
    }
}