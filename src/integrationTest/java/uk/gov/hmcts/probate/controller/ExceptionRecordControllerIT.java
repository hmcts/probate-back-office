package uk.gov.hmcts.probate.controller;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class ExceptionRecordControllerIT {

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

    private String exceptionRecordPayloadCitizenPA8A;
    private String exceptionRecordPayloadSolicitorPA8A;
    private String exceptionRecordPayloadCitizenSingleExecutorPA1P;

    private String exceptionRecordPayloadSolicitorSingleExecutorPA1P;

    private String exceptionRecordPayloadSolicitorSingleExecutorAdmonWillPA1P;
    private String exceptionRecordPayloadCitizenMultipleExecutorPA1P;
    private String exceptionRecordPayloadSolicitorMultipleExecutorPA1P;
    private String exceptionRecordPayloadSolicitorPA1A;
    private String exceptionRecordPayloadCitizenPA1A;
    private String exceptionRecordInvalidJsonPayloadPA1P;
    private String exceptionRecordInvalidJsonPayloadPA8A;
    private String updateCasePayload;
    private List<OCRField> ocrFields = new ArrayList<>();
    private List<String> warnings = new ArrayList<>();

    @BeforeEach
    public void setUp() throws IOException {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        exceptionRecordPayloadCitizenPA8A =
                testUtils.getStringFromFile("expectedExceptionRecordDataCitizenPA8A.json");
        exceptionRecordPayloadSolicitorPA8A =
                testUtils.getStringFromFile("expectedExceptionRecordDataSolicitorPA8A.json");
        exceptionRecordPayloadCitizenSingleExecutorPA1P =
                testUtils.getStringFromFile("expectedExceptionRecordDataCitizenSingleExecutorPA1P.json");
        exceptionRecordPayloadSolicitorSingleExecutorPA1P =
                testUtils.getStringFromFile("expectedExceptionRecordDataSolicitorSingleExecutorPA1P.json");
        exceptionRecordPayloadSolicitorSingleExecutorAdmonWillPA1P =
                testUtils.getStringFromFile("expectedExceptionRecordDataSolicitorSingleExecutorAdmonWillPA1P.json");
        exceptionRecordPayloadCitizenPA1A =
                testUtils.getStringFromFile("expectedExceptionRecordDataCitizenPA1A.json");
        exceptionRecordPayloadCitizenMultipleExecutorPA1P =
                testUtils.getStringFromFile("expectedExceptionRecordDataCitizenMultipleExecutorPA1P.json");
        exceptionRecordPayloadSolicitorMultipleExecutorPA1P =
                testUtils.getStringFromFile("expectedExceptionRecordDataSolicitorMultipleExecutorPA1P.json");
        exceptionRecordPayloadSolicitorPA1A =
                testUtils.getStringFromFile("exceptionRecordPayloadSolicitorPA1A.json");
        exceptionRecordInvalidJsonPayloadPA1P =
                testUtils.getStringFromFile("invalidExceptionRecordDataPA1P.json");
        exceptionRecordInvalidJsonPayloadPA8A =
                testUtils.getStringFromFile("invalidExceptionRecordDataPA8A.json");
        updateCasePayload = testUtils.getStringFromFile("updateExceptionRecordDataPA8A.json");
        warnings.add("test warning");
        when(ocrPopulatedValueMapper.ocrPopulatedValueMapper(any())).thenReturn(ocrFields);
        when(ocrToCCDMandatoryField.ocrToCCDMandatoryFields(eq(ocrFields), any())).thenReturn(EMPTY_LIST);
    }

    @Test
    void testWarningsPopulateListAndReturnOkWithWarningsResponseState() throws Exception {
        when(ocrToCCDMandatoryField.ocrToCCDMandatoryFields(any(), any())).thenReturn(warnings);
        mockMvc.perform(post("/transform-scanned-data")
                .content(exceptionRecordPayloadCitizenPA8A)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(containsString(
                    "{\"warnings\":[\"test warning\"],\"errors\":[\"OCR fields could not be mapped to a case\"]}")));
    }

    @Test
    void testNoWarningsReturnOkResponseAndSuccessResponseStateForCitizenPA8A() throws Exception {
        mockMvc.perform(post("/transform-scanned-data")
                .content(exceptionRecordPayloadCitizenPA8A)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"bulkScanCaseReference\":\"1001\"")))
                .andExpect(content().string(containsString("\"case_type_id\":\"Caveat\"")))
                .andExpect(content().string(containsString("\"applicationType\":\"Personal\"")))
                .andExpect(content().string(containsString("\"deceasedSurname\":\"Smith\"")))
                .andExpect(content().string(containsString("\"warnings\":[]")));
    }

    @Test
    void testNoWarningsReturnOkResponseAndSuccessResponseStateForSolicitorPA8A() throws Exception {
        mockMvc.perform(post("/transform-scanned-data")
                .content(exceptionRecordPayloadSolicitorPA8A)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"bulkScanCaseReference\":\"1001\"")))
                .andExpect(content().string(containsString("\"case_type_id\":\"Caveat\"")))
                .andExpect(content().string(containsString("\"applicationType\":\"Solicitor\"")))
                .andExpect(content().string(containsString("\"deceasedSurname\":\"Smith\"")))
                .andExpect(content().string(containsString("\"warnings\":[]")));
    }

    @Test
    void testNoWarningsReturnOkResponseAndSuccessResponseStateForCitizenSingleExecutorPA1P() throws Exception {
        mockMvc.perform(post("/transform-scanned-data")
                .content(exceptionRecordPayloadCitizenSingleExecutorPA1P)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"bulkScanCaseReference\":\"1002\"")))
                .andExpect(content().string(containsString("\"case_type_id\":\"GrantOfRepresentation\"")))
                .andExpect(content().string(containsString("\"applicationType\":\"Personal\"")))
                .andExpect(content().string(containsString("\"caseType\":\"gop\"")))
                .andExpect(content().string(containsString("\"deceasedSurname\":\"Smith\"")))
                .andExpect(content().string(containsString("\"warnings\":[]")))
                .andExpect(content().string(containsString("\"evidenceHandled\":\"No\"")));;
    }

    @Test
    void testNoWarningsReturnOkResponseAndSuccessResponseStateForCitizenMultipleExecutorPA1P() throws Exception {
        mockMvc.perform(post("/transform-scanned-data")
                .content(exceptionRecordPayloadCitizenMultipleExecutorPA1P)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"bulkScanCaseReference\":\"1002\"")))
                .andExpect(content().string(containsString("\"case_type_id\":\"GrantOfRepresentation\"")))
                .andExpect(content().string(containsString("\"applicationType\":\"Personal\"")))
                .andExpect(content().string(containsString("\"caseType\":\"gop\"")))
                .andExpect(content().string(containsString("\"deceasedSurname\":\"Smith\"")))
                .andExpect(content().string(containsString("\"warnings\":[]")))
                .andExpect(content().string(containsString("\"evidenceHandled\":\"No\"")));;
    }

    @Test
    void testNoWarningsReturnOkResponseAndSuccessResponseStateForSolicitorMultipleExecutorPA1P()
            throws Exception {
        mockMvc.perform(post("/transform-scanned-data")
                .content(exceptionRecordPayloadSolicitorMultipleExecutorPA1P)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"bulkScanCaseReference\":\"1002\"")))
                .andExpect(content().string(containsString("\"case_type_id\":\"GrantOfRepresentation\"")))
                .andExpect(content().string(containsString("\"applicationType\":\"Solicitor\"")))
                .andExpect(content().string(containsString("\"caseType\":\"gop\"")))
                .andExpect(content().string(containsString("\"deceasedSurname\":\"Smith\"")))
                .andExpect(content().string(containsString("\"warnings\":[]")))
                .andExpect(content().string(containsString("\"evidenceHandled\":\"No\"")));;
    }

    @Test
    void testNoWarningsReturnOkResponseAndSuccessResponseStateForSolicitorPA1A() throws Exception {
        mockMvc.perform(post("/transform-scanned-data")
                .content(exceptionRecordPayloadSolicitorPA1A)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"bulkScanCaseReference\":\"1002\"")))
                .andExpect(content().string(containsString("\"case_type_id\":\"GrantOfRepresentation\"")))
                .andExpect(content().string(containsString("\"applicationType\":\"Solicitor\"")))
                .andExpect(content().string(containsString("\"caseType\":\"intestacy\"")))
                .andExpect(content().string(containsString("\"deceasedSurname\":\"Smith\"")))
                .andExpect(content().string(containsString("\"warnings\":[]")))
                .andExpect(content().string(containsString("\"evidenceHandled\":\"No\"")));;
    }

    @Test
    void testNoWarningsReturnOkResponseAndSuccessResponseStateForSolicitorSingleExecutorPA1P() throws Exception {
        mockMvc.perform(post("/transform-scanned-data")
                .content(exceptionRecordPayloadSolicitorSingleExecutorPA1P)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"bulkScanCaseReference\":\"1002\"")))
                .andExpect(content().string(containsString("\"case_type_id\":\"GrantOfRepresentation\"")))
                .andExpect(content().string(containsString("\"applicationType\":\"Solicitor\"")))
                .andExpect(content().string(containsString("\"caseType\":\"gop\"")))
                .andExpect(content().string(containsString("\"deceasedSurname\":\"Smith\"")))
                .andExpect(content().string(containsString("\"warnings\":[]")))
                .andExpect(content().string(containsString("\"caseHandedOffToLegacySite\":\"Yes\"")))
                .andExpect(content().string(containsString("\"evidenceHandled\":\"No\"")));
    }

    @Test
    void testNoWarningsReturnOkAndSuccessResponseStateForSolicitorSingleExecutorAdmonWillPA1P() throws Exception {
        mockMvc.perform(post("/transform-scanned-data")
                        .content(exceptionRecordPayloadSolicitorSingleExecutorAdmonWillPA1P)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"bulkScanCaseReference\":\"1002\"")))
                .andExpect(content().string(containsString("\"case_type_id\":\"GrantOfRepresentation\"")))
                .andExpect(content().string(containsString("\"applicationType\":\"Solicitor\"")))
                .andExpect(content().string(containsString("\"caseType\":\"admonWill\"")))
                .andExpect(content().string(containsString("\"deceasedSurname\":\"Smith\"")))
                .andExpect(content().string(containsString("\"warnings\":[]")))
                .andExpect(content().string(containsString("\"caseHandedOffToLegacySite\":\"Yes\"")))
                .andExpect(content().string(containsString("\"evidenceHandled\":\"No\"")));

    }

    @Test
    void testNoWarningsReturnOkResponseAndSuccessResponseStateForPA1A() throws Exception {
        mockMvc.perform(post("/transform-scanned-data")
                .content(exceptionRecordPayloadCitizenPA1A)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"bulkScanCaseReference\":\"1003\"")))
                .andExpect(content().string(containsString("\"case_type_id\":\"GrantOfRepresentation\"")))
                .andExpect(content().string(containsString("\"applicationType\":\"Personal\"")))
                .andExpect(content().string(containsString("\"caseType\":\"intestacy\"")))
                .andExpect(content().string(containsString("\"deceasedSurname\":\"Smith\"")))
                .andExpect(content().string(containsString("\"warnings\":[]")))
                .andExpect(content().string(containsString("\"evidenceHandled\":\"No\"")));;
    }


    @Test
    void testMissingFormType() throws Exception {
        JSONObject modifiedExceptionRecordPayload  = new JSONObject(exceptionRecordPayloadCitizenPA8A);
        modifiedExceptionRecordPayload.remove("form_type");
        mockMvc.perform(post("/transform-scanned-data")
                .content(modifiedExceptionRecordPayload.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString("Form type 'null' not found")));
    }


    @Test
    void testWarningsPopulateListAndReturnOkWithWarningsResponseStatePreviousEndPoint() throws Exception {
        when(ocrToCCDMandatoryField.ocrToCCDMandatoryFields(any(), any())).thenReturn(warnings);
        mockMvc.perform(post("/transform-exception-record")
                .content(exceptionRecordPayloadCitizenPA8A)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(containsString(
                    "{\"warnings\":[\"test warning\"],\"errors\":[\"OCR fields could not be mapped to a case\"]}")));
    }

    @Test
    void testNoWarningsReturnOkResponseAndSuccessResponseStateForCitizenPA8APreviousEndPoint() throws Exception {
        mockMvc.perform(post("/transform-exception-record")
                .content(exceptionRecordPayloadCitizenPA8A)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"bulkScanCaseReference\":\"1001\"")))
                .andExpect(content().string(containsString("\"case_type_id\":\"Caveat\"")))
                .andExpect(content().string(containsString("\"applicationType\":\"Personal\"")))
                .andExpect(content().string(containsString("\"deceasedSurname\":\"Smith\"")))
                .andExpect(content().string(containsString("\"warnings\":[]")));
    }

    @Test
    void testNoWarningsReturnOkResponseAndSuccessResponseStateForSolicitorPA8APreviousEndPoint()
            throws Exception {
        mockMvc.perform(post("/transform-exception-record")
                .content(exceptionRecordPayloadSolicitorPA8A)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"bulkScanCaseReference\":\"1001\"")))
                .andExpect(content().string(containsString("\"case_type_id\":\"Caveat\"")))
                .andExpect(content().string(containsString("\"applicationType\":\"Solicitor\"")))
                .andExpect(content().string(containsString("\"deceasedSurname\":\"Smith\"")))
                .andExpect(content().string(containsString("\"warnings\":[]")));
    }

    @Test
    void testNoWarningsReturnOkResponseAndSuccessResponseStateForCitizenSingleExecutorPA1PPreviousEndPoint()
            throws Exception {
        mockMvc.perform(post("/transform-exception-record")
                .content(exceptionRecordPayloadCitizenSingleExecutorPA1P)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"bulkScanCaseReference\":\"1002\"")))
                .andExpect(content().string(containsString("\"case_type_id\":\"GrantOfRepresentation\"")))
                .andExpect(content().string(containsString("\"applicationType\":\"Personal\"")))
                .andExpect(content().string(containsString("\"caseType\":\"gop\"")))
                .andExpect(content().string(containsString("\"deceasedSurname\":\"Smith\"")))
                .andExpect(content().string(containsString("\"warnings\":[]")))
                .andExpect(content().string(containsString("\"evidenceHandled\":\"No\"")));;
    }

    @Test
    void testNoWarningsReturnOkResponseAndSuccessResponseStateForCitizenMultipleExecutorPA1PPreviousEndPoint()
            throws Exception {
        mockMvc.perform(post("/transform-exception-record")
                .content(exceptionRecordPayloadCitizenMultipleExecutorPA1P)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"bulkScanCaseReference\":\"1002\"")))
                .andExpect(content().string(containsString("\"case_type_id\":\"GrantOfRepresentation\"")))
                .andExpect(content().string(containsString("\"applicationType\":\"Personal\"")))
                .andExpect(content().string(containsString("\"caseType\":\"gop\"")))
                .andExpect(content().string(containsString("\"deceasedSurname\":\"Smith\"")))
                .andExpect(content().string(containsString("\"warnings\":[]")))
                .andExpect(content().string(containsString("\"evidenceHandled\":\"No\"")));;
    }

    @Test
    void testNoWarningsReturnOkResponseAndSuccessResponseStateForSolicitorMultipleExecutorPA1PPreviousEndPoint()
            throws Exception {
        mockMvc.perform(post("/transform-exception-record")
                .content(exceptionRecordPayloadSolicitorMultipleExecutorPA1P)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"bulkScanCaseReference\":\"1002\"")))
                .andExpect(content().string(containsString("\"case_type_id\":\"GrantOfRepresentation\"")))
                .andExpect(content().string(containsString("\"applicationType\":\"Solicitor\"")))
                .andExpect(content().string(containsString("\"caseType\":\"gop\"")))
                .andExpect(content().string(containsString("\"deceasedSurname\":\"Smith\"")))
                .andExpect(content().string(containsString("\"warnings\":[]")))
                .andExpect(content().string(containsString("\"evidenceHandled\":\"No\"")));;
    }

    @Test
    void testNoWarningsReturnOkResponseAndSuccessResponseStateForSolicitorPA1APreviousEndPoint()
            throws Exception {
        mockMvc.perform(post("/transform-exception-record")
                .content(exceptionRecordPayloadSolicitorPA1A)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"bulkScanCaseReference\":\"1002\"")))
                .andExpect(content().string(containsString("\"case_type_id\":\"GrantOfRepresentation\"")))
                .andExpect(content().string(containsString("\"applicationType\":\"Solicitor\"")))
                .andExpect(content().string(containsString("\"caseType\":\"intestacy\"")))
                .andExpect(content().string(containsString("\"deceasedSurname\":\"Smith\"")))
                .andExpect(content().string(containsString("\"warnings\":[]")))
                .andExpect(content().string(containsString("\"evidenceHandled\":\"No\"")));;
    }

    @Test
    void testNoWarningsReturnOkResponseAndSuccessResponseStateForSolicitorSingleExecutorPA1PPreviousEndPoint()
            throws Exception {
        mockMvc.perform(post("/transform-exception-record")
                .content(exceptionRecordPayloadSolicitorSingleExecutorPA1P)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"bulkScanCaseReference\":\"1002\"")))
                .andExpect(content().string(containsString("\"case_type_id\":\"GrantOfRepresentation\"")))
                .andExpect(content().string(containsString("\"applicationType\":\"Solicitor\"")))
                .andExpect(content().string(containsString("\"caseType\":\"gop\"")))
                .andExpect(content().string(containsString("\"deceasedSurname\":\"Smith\"")))
                .andExpect(content().string(containsString("\"warnings\":[]")))
                .andExpect(content().string(containsString("\"evidenceHandled\":\"No\"")));;
    }

    @Test
    void testNoWarningsReturnOkResponseAndSuccessResponseStateForPA1APreviousEndPoint() throws Exception {
        mockMvc.perform(post("/transform-exception-record")
                .content(exceptionRecordPayloadCitizenPA1A)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"bulkScanCaseReference\":\"1003\"")))
                .andExpect(content().string(containsString("\"case_type_id\":\"GrantOfRepresentation\"")))
                .andExpect(content().string(containsString("\"applicationType\":\"Personal\"")))
                .andExpect(content().string(containsString("\"caseType\":\"intestacy\"")))
                .andExpect(content().string(containsString("\"deceasedSurname\":\"Smith\"")))
                .andExpect(content().string(containsString("\"warnings\":[]")))
                .andExpect(content().string(containsString("\"evidenceHandled\":\"No\"")));;
    }

    @Test
    void testMissingFormTypePreviousEndPoint() throws Exception {
        JSONObject modifiedExceptionRecordPayload  = new JSONObject(exceptionRecordPayloadCitizenPA8A);
        modifiedExceptionRecordPayload.remove("form_type");
        mockMvc.perform(post("/transform-exception-record")
            .content(modifiedExceptionRecordPayload.toString())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().is4xxClientError())
            .andExpect(content().string(containsString("Form type 'null' not found")));
    }

    @Test
    void testExceptionRecordErrorHandlerCitizenPA8A() throws Exception {
        String deceasedDateOfDeath = "\"name\": \"deceasedDateOfDeath\", \"value\": \"02022019\"";
        String badDeceasedDateOfDeath = "\"name\": \"deceasedDateOfDeath\", \"value\": \"02022\"";
        mockMvc.perform(post("/transform-scanned-data")
                .content(exceptionRecordPayloadCitizenPA8A.replace(deceasedDateOfDeath, badDeceasedDateOfDeath))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(containsString(
                    "\"warnings\":[\"OCR Data Mapping Error: "
                            + "deceasedDateOfDeath: Date field '02022' not in expected format ddMMyyyy\"]")))
                .andExpect(content().string(containsString(
                    "\"errors\":[\"OCR fields could not be mapped to a case\"]")));
    }

    @Test
    void testExceptionRecordErrorHandlerSolicitorPA8A() throws Exception {
        String deceasedDateOfDeath = "\"name\": \"deceasedDateOfDeath\", \"value\": \"02022019\"";
        String badDeceasedDateOfDeath = "\"name\": \"deceasedDateOfDeath\", \"value\": \"02022\"";
        mockMvc.perform(post("/transform-scanned-data")
                .content(exceptionRecordPayloadSolicitorPA8A.replace(deceasedDateOfDeath, badDeceasedDateOfDeath))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(containsString(
                    "\"warnings\":[\"OCR Data Mapping Error: "
                            + "deceasedDateOfDeath: Date field '02022' not in expected format ddMMyyyy\"]")))
                .andExpect(content().string(containsString(
                    "\"errors\":[\"OCR fields could not be mapped to a case\"]")));
    }

    @Test
    void testExceptionRecordErrorHandlerPA1P() throws Exception {
        String deceasedDateOfDeath = "\"name\": \"deceasedDateOfDeath\", \"value\": \"02022019\"";
        String badDeceasedDateOfDeath = "\"name\": \"deceasedDateOfDeath\", \"value\": \"02022\"";
        mockMvc.perform(post("/transform-scanned-data")
                .content(exceptionRecordPayloadCitizenSingleExecutorPA1P.replace(
                    deceasedDateOfDeath, badDeceasedDateOfDeath))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(containsString(
                    "\"warnings\":[\"OCR Data Mapping Error: "
                            + "deceasedDateOfDeath: Date field '02022' not in expected format ddMMyyyy\"]")))
                .andExpect(content().string(containsString(
                    "\"errors\":[\"OCR fields could not be mapped to a case\"]")));
    }

    @Test
    void testErrorReturnedForIncorrectClassification() throws Exception {
        mockMvc.perform(post("/transform-scanned-data")
                .content(exceptionRecordPayloadCitizenPA8A.replace("NEW_APPLICATION", "SUPPLEMENTARY_EVIDENCE"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(containsString(
                    "\"warnings\":[\"OCR Data Mapping Error: "
                        + "This Exception Record can not be created as a case: 1001\"]")));
    }

    @Disabled
    public void testErrorReturnedForUnimplementedFormType() throws Exception {
        mockMvc.perform(post("/transform-scanned-data")
                .content(exceptionRecordPayloadCitizenPA8A.replace("PA8A", "PPPP"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(containsString(
                    "\"warnings\":[\"OCR Data Mapping Error: "
                        + "This Exception Record form currently has no case mapping\"]")));
    }

    @Test
    void testInvalidExceptionRecordGoPTransformJsonResponse() throws Exception {
        mockMvc.perform(post("/transform-scanned-data")
                .content(exceptionRecordInvalidJsonPayloadPA1P)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testInvalidExceptionRecordCaveatTransformJsonResponse() throws Exception {
        mockMvc.perform(post("/transform-scanned-data")
                .content(exceptionRecordInvalidJsonPayloadPA8A)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testInvalidExceptionRecordCaveatUpdateJsonResponse() throws Exception {
        mockMvc.perform(post("/update-case")
                .content(exceptionRecordInvalidJsonPayloadPA8A)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void shouldNotUpdateCaseForCaveatAlreadyExpired() throws Exception {

        mockMvc.perform(post("/update-case")
            .content(updateCasePayload)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(content().string(
                containsString("\"warnings\":[\"OCR Data Mapping Error: Cannot extend an already expired "
                        + "caveat.\"]")));
    }

    @Test
    void shouldNotUpdateCaseForIncorrectJourneyClassification() throws Exception {

        mockMvc.perform(post("/update-case")
            .content(updateCasePayload
                .replace("SUPPLEMENTARY_EVIDENCE_WITH_OCR", JourneyClassification.SUPPLEMENTARY_EVIDENCE.name()))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(content().string(containsString(
                "{\"warnings\":[\"OCR Data Mapping Error: This Exception Record can not be created as a case update "
                    + "for case:1542274092932452\"],\"errors\":[\"OCR fields could not be mapped to a case\"]}")));
    }

    @Test
    void shouldNotUpdateCaseForIncorrectFormType() throws Exception {

        mockMvc.perform(post("/update-case")
            .content(updateCasePayload.replace("\"form_type\": \"PA8A\"", "\"form_type\": \"PA1A\""))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(content().string(containsString(
                "{\"warnings\":[\"OCR Data Mapping Error: This Exception Record form currently has no case mapping "
                    + "for case: 1542274092932452\"],\"errors\":[\"OCR fields could not be mapped to a case\"]}")));
    }
}
