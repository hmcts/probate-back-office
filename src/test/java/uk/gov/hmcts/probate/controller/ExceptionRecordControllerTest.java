package uk.gov.hmcts.probate.controller;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.hmcts.probate.exception.OCRMappingException;
import uk.gov.hmcts.probate.insights.AppInsights;
import uk.gov.hmcts.probate.model.exceptionrecord.JourneyClassification;
import uk.gov.hmcts.probate.model.exceptionrecord.SuccessfulCaveatUpdateResponse;
import uk.gov.hmcts.probate.model.ocr.OCRField;
import uk.gov.hmcts.probate.service.ocr.OCRPopulatedValueMapper;
import uk.gov.hmcts.probate.service.ocr.OCRToCCDMandatoryField;
import uk.gov.hmcts.probate.util.TestUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
public class ExceptionRecordControllerTest {

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
    
    private String exceptionRecordPayloadPA8A;
    private String exceptionRecordPayloadPA1P;
    private String exceptionRecordPayloadPA1A;
    private String exceptionRecordInvalidJsonPayload;
    private String updateCasePayload;
    private List<OCRField> ocrFields = new ArrayList<>();
    private List<String> warnings = new ArrayList<>();

    @Before
    public void setUp() throws IOException {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        exceptionRecordPayloadPA8A = testUtils.getStringFromFile("expectedExceptionRecordDataPA8A.json");
        exceptionRecordPayloadPA1P = testUtils.getStringFromFile("expectedExceptionRecordDataPA1P.json");
        exceptionRecordPayloadPA1A = testUtils.getStringFromFile("expectedExceptionRecordDataPA1A.json");
        exceptionRecordInvalidJsonPayload = testUtils.getStringFromFile("invalidExceptionRecordDataJson.json");
        updateCasePayload = testUtils.getStringFromFile("updateExceptionRecordDataPA8A.json");
        warnings.add("test warning");
        when(ocrPopulatedValueMapper.ocrPopulatedValueMapper(any())).thenReturn(ocrFields);
        when(ocrToCCDMandatoryField.ocrToCCDMandatoryFields(eq(ocrFields), any(), any())).thenReturn(EMPTY_LIST);
    }

    @Test
    public void testWarningsPopulateListAndReturnOkWithWarningsResponseState() throws Exception {
        when(ocrToCCDMandatoryField.ocrToCCDMandatoryFields(any(), any(), any())).thenReturn(warnings);
        mockMvc.perform(post("/transform-exception-record")
                .content(exceptionRecordPayloadPA8A)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(containsString("Please resolve all warnings before creating this case")));
    }

    @Test
    public void testNoWarningsReturnOkResponseAndSuccessResponseStateForPA8A() throws Exception {
        mockMvc.perform(post("/transform-exception-record")
                .content(exceptionRecordPayloadPA8A)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"bulkScanCaseReference\":\"1001\"")))
                .andExpect(content().string(containsString("\"case_type_id\":\"Caveat\"")))
                .andExpect(content().string(containsString("\"applicationType\":\"Personal\"")))
                .andExpect(content().string(containsString("\"deceasedSurname\":\"Smith\"")))
                .andExpect(content().string(containsString("\"warnings\":[]")));
    }

    @Test
    public void testNoWarningsReturnOkResponseAndSuccessResponseStateForPA1P() throws Exception {
        mockMvc.perform(post("/transform-exception-record")
                .content(exceptionRecordPayloadPA1P)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"bulkScanCaseReference\":\"1002\"")))
                .andExpect(content().string(containsString("\"case_type_id\":\"GrantOfRepresentation\"")))
                .andExpect(content().string(containsString("\"applicationType\":\"Personal\"")))
                .andExpect(content().string(containsString("\"caseType\":\"gop\"")))
                .andExpect(content().string(containsString("\"deceasedSurname\":\"Smith\"")))
                .andExpect(content().string(containsString("\"warnings\":[]")));
    }

    @Test
    public void testNoWarningsReturnOkResponseAndSuccessResponseStateForPA1A() throws Exception {
        mockMvc.perform(post("/transform-exception-record")
                .content(exceptionRecordPayloadPA1A)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"bulkScanCaseReference\":\"1003\"")))
                .andExpect(content().string(containsString("\"case_type_id\":\"GrantOfRepresentation\"")))
                .andExpect(content().string(containsString("\"applicationType\":\"Personal\"")))
                .andExpect(content().string(containsString("\"caseType\":\"intestacy\"")))
                .andExpect(content().string(containsString("\"deceasedSurname\":\"Smith\"")))
                .andExpect(content().string(containsString("\"warnings\":[]")));
    }

    @Test
    public void testMissingFormType() throws Exception {
        JSONObject modifiedExceptionRecordPayload  = new JSONObject(exceptionRecordPayloadPA8A);
        modifiedExceptionRecordPayload.remove("form_type");
        mockMvc.perform(post("/transform-exception-record")
                .content(modifiedExceptionRecordPayload.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString("Form type 'null' not found")));
    }

    @Test
    public void testExceptionRecordErrorHandler() throws Exception {
        String deceasedDateOfDeath = "\"name\": \"deceasedDateOfDeath\", \"value\": \"02022019\"";
        String badDeceasedDateOfDeath = "\"name\": \"deceasedDateOfDeath\", \"value\": \"02022\"";
        mockMvc.perform(post("/transform-exception-record")
                .content(exceptionRecordPayloadPA8A.replace(deceasedDateOfDeath, badDeceasedDateOfDeath))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(containsString(
                        "\"warnings\":[\"OCR Data Mapping Error: Date field '02022' not in expected format ddMMyyyy\"]")))
                .andExpect(content().string(containsString("\"errors\":[\"OCR fields could not be mapped to a case\"]")));
    }

    @Test
    public void testErrorReturnedForIncorrectClassification() throws Exception {
        mockMvc.perform(post("/transform-exception-record")
                .content(exceptionRecordPayloadPA8A.replace("NEW_APPLICATION", "SUPPLEMENTARY_EVIDENCE"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(containsString(
                        "\"warnings\":[\"OCR Data Mapping Error: This Exception Record can not be created as a case\"]")));
    }

    @Ignore
    public void testErrorReturnedForUnimplementedFormType() throws Exception {
        mockMvc.perform(post("/transform-exception-record")
                .content(exceptionRecordPayloadPA8A.replace("PA8A", "PPPP"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(containsString(
                        "\"warnings\":[\"OCR Data Mapping Error: This Exception Record form currently has no case mapping\"]")));
    }

    @Test
    public void testInvalidExceptionRecordJsonResponse() throws Exception {
        mockMvc.perform(post("/transform-exception-record")
                .content(exceptionRecordInvalidJsonPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldNotUpdateCaseForCaveatAlreadyExpired() throws Exception {

        mockMvc.perform(post("/update-case")
            .content(updateCasePayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(content().string(containsString("\"warnings\":[\"OCR Data Mapping Error: Cannot extend an already expired caveat.\"]")));
    }

    @Test
    public void shouldNotUpdateCaseForIncorrectJourneyClassification() throws Exception {

        mockMvc.perform(post("/update-case")
            .content(updateCasePayload.replace("SUPPLEMENTARY_EVIDENCE_WITH_OCR", JourneyClassification.SUPPLEMENTARY_EVIDENCE.name()))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(content().string(containsString("{\"warnings\":[\"OCR Data Mapping Error: This Exception Record can not be created as a case update\"],\"errors\":[\"OCR fields could not be mapped to a case\"]}")));
    }

    @Test
    public void shouldNotUpdateCaseForIncorrectFormType() throws Exception {

        mockMvc.perform(post("/update-case")
            .content(updateCasePayload.replace("\"form_type\": \"PA8A\"", "\"form_type\": \"PA1A\""))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(content().string(containsString("{\"warnings\":[\"OCR Data Mapping Error: This Exception Record form currently has no case mapping\"],\"errors\":[\"OCR fields could not be mapped to a case\"]}")));
    }
}