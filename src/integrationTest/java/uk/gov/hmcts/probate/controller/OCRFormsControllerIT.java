package uk.gov.hmcts.probate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.hmcts.probate.model.ocr.OCRField;
import uk.gov.hmcts.probate.service.ocr.NonMandatoryFieldsValidator;
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

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class OCRFormsControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private TestUtils testUtils;

    @MockitoBean
    private OCRPopulatedValueMapper ocrPopulatedValueMapper;

    @MockitoBean
    private OCRToCCDMandatoryField ocrToCCDMandatoryField;

    @MockitoBean
    private NonMandatoryFieldsValidator nonMandatoryFieldsValidator;

    private String ocrPayload;
    private List<OCRField> ocrFields = new ArrayList<>();
    private List<String> warnings = new ArrayList<>();

    @BeforeEach
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
        when(ocrToCCDMandatoryField.ocrToCCDMandatoryFields(eq(ocrFields), any())).thenReturn(EMPTY_LIST);
    }

    @Test
    void testNoWarningsReturnOkResponseAndSuccessResponseStateForPA1P() throws Exception {
        mockMvc.perform(post("/forms/PA1P/validate-ocr")
                .content(ocrPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("SUCCESS")));
    }

    @Test
    void testWarningsPopulateListAndReturnOkWithWarningsResponseState() throws Exception {
        when(nonMandatoryFieldsValidator.ocrToCCDNonMandatoryWarnings(any(), any())).thenReturn(warnings);
        mockMvc.perform(post("/forms/PA1P/validate-ocr")
                .content(ocrPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("WARNINGS")))
                .andExpect(content().string(containsString("test warning")));
    }

    @Test
    void testNoWarningsReturnOkResponseAndSuccessResponseStateForPA1A() throws Exception {
        mockMvc.perform(post("/forms/PA1A/validate-ocr")
                .content(ocrPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("SUCCESS")));
    }

    @Test
    void testNoWarningsReturnOkResponseAndSuccessResponseStateForPA8A() throws Exception {
        mockMvc.perform(post("/forms/PA8A/validate-ocr")
                .content(ocrPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("SUCCESS")));
    }

    @Test
    void testInvalidFormTypeThrowsNotFound() throws Exception {
        mockMvc.perform(post("/forms/test/validate-ocr")
                .content(ocrPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString("Form type 'test' not found")));
    }
}
