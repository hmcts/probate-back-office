package uk.gov.hmcts.probate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.model.ccd.raw.CCDDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.model.template.PDFServiceTemplate;
import uk.gov.hmcts.probate.service.StateChangeService;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.hmcts.probate.util.TestUtils;

import java.io.IOException;
import java.util.Optional;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CheckYourAnswersControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TestUtils utils;

    @MockBean
    private CallbackResponseTransformer callbackResponseTransformerMock;
    @MockBean
    private PDFManagementService pdfManagementServiceMock;
    @Mock
    private ObjectMapper objectMapperMock;
    @Mock
    private BindingResult bindingResultMock;
    @Mock
    private CCDDocument ccdDocument;
    @Mock
    private CallbackRequest callbackRequestMock;
    @Mock
    private CallbackResponse callbackResponseMock;
    @Mock
    private CallbackResponse pdfCallbackResponseMock;
    @Mock
    private ResponseCaseData responseCaseDataMock;
    @Mock
    private ResponseCaseData pdfResponseCaseDataMock;
    @Mock
    private StateChangeService stateChangeServiceMock;
    @Mock
    private CaseDetails caseDetailsMock;
    @Mock
    private CaseData caseDataMock;

    @Before
    public void setup() {
        initMocks(this);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
    }

    @Test
    public void shouldContinueWithNoUpdateWhenLegalStatementCheckYourAnswers() throws Exception {
        when(bindingResultMock.hasErrors()).thenReturn(false);
        CallbackResponse response = CallbackResponse.builder().build();
        ResponseCaseData responseCaseData = ResponseCaseData.builder()
            .state("defaultState").build();
        response.setData(responseCaseData);
        when(callbackResponseTransformerMock.transformWithConditionalStateChange(any(CallbackRequest.class), any(Optional.class)))
            .thenReturn(response);

        mockMvc.perform(post("/checkYourAnswers/beforeLegalStatement")
            .content(utils.getJsonFromFile("successForNoStateChangeOnCYABeforeLegalStatement.json"))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

    }

    @Test(expected = BadRequestException.class)
    public void shouldErrorForLegalStatement() throws IOException {
        CheckYourAnswersController underTest = new CheckYourAnswersController(callbackResponseTransformerMock, pdfManagementServiceMock,
            objectMapperMock, stateChangeServiceMock);
        when(bindingResultMock.hasErrors()).thenReturn(true);

        underTest.checkBeforeLegalStatement(callbackRequestMock, bindingResultMock);
    }

    @Test(expected = BadRequestException.class)
    public void shouldErrorForStatementOfTruth() throws IOException {
        CheckYourAnswersController underTest = new CheckYourAnswersController(callbackResponseTransformerMock, pdfManagementServiceMock,
            objectMapperMock, stateChangeServiceMock);
        when(bindingResultMock.hasErrors()).thenReturn(true);

        underTest.checkBeforeStatementOfTruth(callbackRequestMock, bindingResultMock);
    }

    @Test
    public void shouldContinueWithUpdateWhenLegalStatementCheckYourAnswers() throws Exception {
        when(bindingResultMock.hasErrors()).thenReturn(false);

        CallbackResponse response = CallbackResponse.builder().build();
        ResponseCaseData responseCaseData = ResponseCaseData.builder()
            .deceasedForenames("someForename").build();
        response.setData(responseCaseData);
        when(callbackResponseTransformerMock.transformWithConditionalStateChange(any(CallbackRequest.class), any(Optional.class)))
            .thenReturn(response);
        when(callbackResponseMock.getData()).thenReturn(responseCaseDataMock);

        when(pdfManagementServiceMock.generateAndUpload(any(PDFServiceTemplate.class), any(String.class))).thenReturn(ccdDocument);
        when(callbackResponseTransformerMock.transform(any(CallbackRequest.class), any(PDFServiceTemplate.class), any(CCDDocument.class)))
            .thenReturn(response);

        mockMvc.perform(post("/checkYourAnswers/beforeLegalStatement")
            .content(utils.getJsonFromFile("successForStateChangeOnCYABeforeLegalStatement.json"))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.data.deceasedForenames").value("someForename"));

    }

    @Test
    public void shouldContinueWithUpdateWhenStatementOfTruthCheckYourAnswers() throws Exception {
        when(bindingResultMock.hasErrors()).thenReturn(false);

        CallbackResponse response = CallbackResponse.builder().build();
        ResponseCaseData responseCaseData = ResponseCaseData.builder()
            .deceasedForenames("someForename").build();
        response.setData(responseCaseData);
        when(callbackResponseTransformerMock.transformWithConditionalStateChange(any(CallbackRequest.class), any(Optional.class)))
            .thenReturn(response);
        when(callbackResponseMock.getData()).thenReturn(responseCaseDataMock);

        mockMvc.perform(post("/checkYourAnswers/beforeStatementOfTruth")
            .content(utils.getJsonFromFile("successForStateChangeOnCYABeforeStatementOfTruth.json"))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.data.deceasedForenames").value("someForename"));

    }

}
