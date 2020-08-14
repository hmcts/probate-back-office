package uk.gov.hmcts.probate.controller;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
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
import uk.gov.hmcts.probate.model.exceptionrecord.JourneyClassification;
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

    private String exceptionRecordPayloadCitizenPA8A;
    private String exceptionRecordPayloadSolicitorPA8A;
    private String exceptionRecordPayloadCitizenSingleExecutorPA1P;
    private String exceptionRecordPayloadSolicitorSingleExecutorPA1P;
    private String exceptionRecordPayloadCitizenMultipleExecutorPA1P;
    private String exceptionRecordPayloadCitizenPA1A;
    private String exceptionRecordInvalidJsonPayloadPA1P;
    private String exceptionRecordInvalidJsonPayloadPA8A;
    private String updateCasePayload;
    private List<OCRField> ocrFields = new ArrayList<>();
    private List<String> warnings = new ArrayList<>();

    @Before
    public void setUp() throws IOException {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        exceptionRecordPayloadCitizenPA8A = testUtils.getStringFromFile("expectedExceptionRecordDataCitizenPA8A.json");
        exceptionRecordPayloadSolicitorPA8A = testUtils.getStringFromFile("expectedExceptionRecordDataSolicitorPA8A.json");
        exceptionRecordPayloadCitizenSingleExecutorPA1P = testUtils.getStringFromFile("expectedExceptionRecordDataCitizenSingleExecutorPA1P.json");
        exceptionRecordPayloadSolicitorSingleExecutorPA1P = testUtils.getStringFromFile("expectedExceptionRecordDataSolicitorSingleExecutorPA1P.json");
        exceptionRecordPayloadCitizenPA1A = testUtils.getStringFromFile("expectedExceptionRecordDataCitizenPA1A.json");
        exceptionRecordPayloadCitizenMultipleExecutorPA1P = testUtils.getStringFromFile("expectedExceptionRecordDataCitizenMultipleExecutorPA1P.json");
        exceptionRecordInvalidJsonPayloadPA1P = testUtils.getStringFromFile("invalidExceptionRecordDataPA1P.json");
        exceptionRecordInvalidJsonPayloadPA8A = testUtils.getStringFromFile("invalidExceptionRecordDataPA8A.json");
        updateCasePayload = testUtils.getStringFromFile("updateExceptionRecordDataPA8A.json");
        warnings.add("test warning");
        when(ocrPopulatedValueMapper.ocrPopulatedValueMapper(any())).thenReturn(ocrFields);
        when(ocrToCCDMandatoryField.ocrToCCDMandatoryFields(eq(ocrFields), any())).thenReturn(EMPTY_LIST);
    }

    @Test
    public void testWarningsPopulateListAndReturnOkWithWarningsResponseState() throws Exception {
        when(ocrToCCDMandatoryField.ocrToCCDMandatoryFields(any(), any())).thenReturn(warnings);
        mockMvc.perform(post("/transform-scanned-data")
                .content(exceptionRecordPayloadCitizenPA8A)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(containsString("{\"warnings\":[\"test warning\"],\"errors\":[\"OCR fields could not be mapped to a case\"]}")));
    }

    @Test
    public void testNoWarningsReturnOkResponseAndSuccessResponseStateForPA8A() throws Exception {
        mockMvc.perform(post("/transform-scanned-data")
                .content(exceptionRecordPayloadCitizenPA8A)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"bulkScanCaseReference\":\"1001\"")))
                .andExpect(content().string(containsString("\"exception_record_case_type_id\":\"Caveat\"")))
                .andExpect(content().string(containsString("\"applicationType\":\"Personal\"")))
                .andExpect(content().string(containsString("\"deceasedSurname\":\"Smith\"")))
                .andExpect(content().string(containsString("\"warnings\":[]")));
    }

    @Test
    public void testNoWarningsReturnOkResponseAndSuccessResponseStateForSolicitorPA8A() throws Exception {
        mockMvc.perform(post("/transform-scanned-data")
                .content(exceptionRecordPayloadSolicitorPA8A)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"bulkScanCaseReference\":\"1001\"")))
                .andExpect(content().string(containsString("\"exception_record_case_type_id\":\"Caveat\"")))
                .andExpect(content().string(containsString("\"applicationType\":\"Solicitor\"")))
                .andExpect(content().string(containsString("\"deceasedSurname\":\"Smith\"")))
                .andExpect(content().string(containsString("\"warnings\":[]")));
    }

    @Test
    public void testNoWarningsReturnOkResponseAndSuccessResponseStateForCitizenSingleExecutorPA1P() throws Exception {
        mockMvc.perform(post("/transform-scanned-data")
                .content(exceptionRecordPayloadCitizenSingleExecutorPA1P)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"bulkScanCaseReference\":\"1002\"")))
                .andExpect(content().string(containsString("\"exception_record_case_type_id\":\"GrantOfRepresentation\"")))
                .andExpect(content().string(containsString("\"applicationType\":\"Personal\"")))
                .andExpect(content().string(containsString("\"caseType\":\"gop\"")))
                .andExpect(content().string(containsString("\"deceasedSurname\":\"Smith\"")))
                .andExpect(content().string(containsString("\"warnings\":[]")));
    }

    @Test
    public void testNoWarningsReturnOkResponseAndSuccessResponseStateForCitizenMultipleExecutorPA1P() throws Exception {
        mockMvc.perform(post("/transform-scanned-data")
                .content(exceptionRecordPayloadCitizenMultipleExecutorPA1P)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"bulkScanCaseReference\":\"1002\"")))
                .andExpect(content().string(containsString("\"exception_record_case_type_id\":\"GrantOfRepresentation\"")))
                .andExpect(content().string(containsString("\"applicationType\":\"Personal\"")))
                .andExpect(content().string(containsString("\"caseType\":\"gop\"")))
                .andExpect(content().string(containsString("\"deceasedSurname\":\"Smith\"")))
                .andExpect(content().string(containsString("\"warnings\":[]")));
    }

    @Test
    public void testNoWarningsReturnOkResponseAndSuccessResponseStateForSolicitorSingleExecutorPA1P() throws Exception {
        mockMvc.perform(post("/transform-scanned-data")
                .content(exceptionRecordPayloadSolicitorSingleExecutorPA1P)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"bulkScanCaseReference\":\"1002\"")))
                .andExpect(content().string(containsString("\"exception_record_case_type_id\":\"GrantOfRepresentation\"")))
                .andExpect(content().string(containsString("\"applicationType\":\"Solicitor\"")))
                .andExpect(content().string(containsString("\"caseType\":\"gop\"")))
                .andExpect(content().string(containsString("\"deceasedSurname\":\"Smith\"")))
                .andExpect(content().string(containsString("\"warnings\":[]")));
    }

    @Test
    public void testNoWarningsReturnOkResponseAndSuccessResponseStateForPA1A() throws Exception {
        mockMvc.perform(post("/transform-scanned-data")
                .content(exceptionRecordPayloadCitizenPA1A)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"bulkScanCaseReference\":\"1003\"")))
                .andExpect(content().string(containsString("\"exception_record_case_type_id\":\"GrantOfRepresentation\"")))
                .andExpect(content().string(containsString("\"applicationType\":\"Personal\"")))
                .andExpect(content().string(containsString("\"caseType\":\"intestacy\"")))
                .andExpect(content().string(containsString("\"deceasedSurname\":\"Smith\"")))
                .andExpect(content().string(containsString("\"warnings\":[]")));
    }

    @Test
    public void testMissingFormType() throws Exception {
        JSONObject modifiedExceptionRecordPayload  = new JSONObject(exceptionRecordPayloadCitizenPA8A);
        modifiedExceptionRecordPayload.remove("form_type");
        mockMvc.perform(post("/transform-scanned-data")
                .content(modifiedExceptionRecordPayload.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString("Form type 'null' not found")));
    }

    @Test
    public void testExceptionRecordErrorHandler() throws Exception {
        String deceasedDateOfDeath = "\"name\": \"deceasedDateOfDeath\", \"value\": \"02022019\"";
        String badDeceasedDateOfDeath = "\"name\": \"deceasedDateOfDeath\", \"value\": \"02022\"";
        mockMvc.perform(post("/transform-scanned-data")
                .content(exceptionRecordPayloadCitizenPA8A.replace(deceasedDateOfDeath, badDeceasedDateOfDeath))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(containsString(
                        "\"warnings\":[\"OCR Data Mapping Error: Date field '02022' not in expected format ddMMyyyy\"]")))
                .andExpect(content().string(containsString("\"errors\":[\"OCR fields could not be mapped to a case\"]")));
    }

    @Test
    public void testErrorReturnedForIncorrectClassification() throws Exception {
        mockMvc.perform(post("/transform-scanned-data")
                .content(exceptionRecordPayloadCitizenPA8A.replace("NEW_APPLICATION", "SUPPLEMENTARY_EVIDENCE"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(containsString(
                        "\"warnings\":[\"OCR Data Mapping Error: This Exception Record can not be created as a case: 1001\"]")));
    }

    @Ignore
    public void testErrorReturnedForUnimplementedFormType() throws Exception {
        mockMvc.perform(post("/transform-scanned-data")
                .content(exceptionRecordPayloadCitizenPA8A.replace("PA8A", "PPPP"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(containsString(
                        "\"warnings\":[\"OCR Data Mapping Error: This Exception Record form currently has no case mapping\"]")));
    }

    @Test
    public void testInvalidExceptionRecordGoPTransformJsonResponse() throws Exception {
        mockMvc.perform(post("/transform-scanned-data")
                .content(exceptionRecordInvalidJsonPayloadPA1P)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testInvalidExceptionRecordCaveatTransformJsonResponse() throws Exception {
        mockMvc.perform(post("/transform-scanned-data")
                .content(exceptionRecordInvalidJsonPayloadPA8A)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }


    @Test
    public void testInvalidExceptionRecordGoPUpdateJsonResponse() throws Exception {
        mockMvc.perform(post("/update-case")
                .content(exceptionRecordInvalidJsonPayloadPA1P)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testInvalidExceptionRecordCaveatUpdateJsonResponse() throws Exception {
        mockMvc.perform(post("/update-case")
                .content(exceptionRecordInvalidJsonPayloadPA8A)
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
            .andExpect(content().string(containsString("{\"warnings\":[\"OCR Data Mapping Error: This Exception Record can not be created as a case update for case:1542274092932452\"],\"errors\":[\"OCR fields could not be mapped to a case\"]}")));
    }

    @Test
    public void shouldNotUpdateCaseForIncorrectFormType() throws Exception {

        mockMvc.perform(post("/update-case")
            .content(updateCasePayload.replace("\"form_type\": \"PA8A\"", "\"form_type\": \"PA1A\""))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(content().string(containsString("{\"warnings\":[\"OCR Data Mapping Error: This Exception Record form currently has no case mapping for case: 1542274092932452\"],\"errors\":[\"OCR fields could not be mapped to a case\"]}")));
    }
}