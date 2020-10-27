package uk.gov.hmcts.probate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData.CaseDataBuilder;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.probate.controller.CaseDataTestBuilder.ID;
import static uk.gov.hmcts.probate.controller.CaseDataTestBuilder.LAST_MODIFIED;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class NextStepsControllerTest {
  
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String NEXTSTEPS_CONFIRMATION_URL = "/nextsteps/confirmation";
    private static final String APPLICATION_GROUNDS = "Application grounds";

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mockMvc;

    private CaseDataBuilder  caseDataBuilder = CaseDataTestBuilder.withDefaults();

    @MockBean
    AppInsights appInsights;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
    }

    @Test
    public void shouldConfirmNextStepsWithNoErrors() throws Exception {
        caseDataBuilder.applicationType(ApplicationType.SOLICITOR).build();
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, "", ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(NEXTSTEPS_CONFIRMATION_URL).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
    }

    @Test
    public void shouldConfirmNextStepsWithSolicitorFirmIsNullError() throws Exception {
        caseDataBuilder.solsSolicitorFirmName(null);
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, "", ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(NEXTSTEPS_CONFIRMATION_URL).content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("caseDetails.data.solsSolicitorFirmName"))
                .andExpect(jsonPath("$.fieldErrors[0].code").value("NotBlank"))
                .andExpect(jsonPath("$.fieldErrors[0].message").value("Solicitor firm name cannot be empty"));
    }

    @Test
    public void shouldConfirmNextStepsWithSolsSolicitorFirmPostcodeIsNullError() throws Exception {
        caseDataBuilder.solsSolicitorAddress(SolsAddress.builder().addressLine1(CaseDataTestBuilder.SOLICITOR_FIRM_LINE1).build());
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, "", ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(NEXTSTEPS_CONFIRMATION_URL).content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("caseDetails.data.solsSolicitorAddress.postCode"))
                .andExpect(jsonPath("$.fieldErrors[0].code").value("NotNull"))
                .andExpect(jsonPath("$.fieldErrors[0].message").value("The deceased postcode cannot be empty"));
    }

    @Test
    public void shouldConfirmNextStepsWithSolsSOTForenamesIsNullError() throws Exception {
        caseDataBuilder.solsSOTForenames(null);
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, "", ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(NEXTSTEPS_CONFIRMATION_URL).content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("caseDetails.data.solsSOTForenames"))
                .andExpect(jsonPath("$.fieldErrors[0].code").value("NotBlank"))
                .andExpect(jsonPath("$.fieldErrors[0].message").value("Solicitor SOT forenames cannot be empty"));
    }

    @Test
    public void shouldConfirmNextStepsWithSolsSOTSurnameIsNullError() throws Exception {
        caseDataBuilder.solsSOTSurname(null);
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, "", ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(NEXTSTEPS_CONFIRMATION_URL).content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("caseDetails.data.solsSOTSurname"))
                .andExpect(jsonPath("$.fieldErrors[0].code").value("NotBlank"))
                .andExpect(jsonPath("$.fieldErrors[0].message").value("Solicitor SOT surname cannot be empty"));
    }

    @Test
    public void shouldConfirmNextStepsWithSolsSOTJobTitleIsNullError() throws Exception {
        caseDataBuilder.solsSOTJobTitle(null);
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, "", ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(NEXTSTEPS_CONFIRMATION_URL).content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("caseDetails.data.solsSOTJobTitle"))
                .andExpect(jsonPath("$.fieldErrors[0].code").value("NotBlank"))
                .andExpect(jsonPath("$.fieldErrors[0].message").value("Solicitor SOT job title cannot be empty"));
    }

    @Test
    public void shouldConfirmNextStepsWithPaymentMethodIsNullError() throws Exception {
        caseDataBuilder.solsPaymentMethods(null);
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, "", ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(NEXTSTEPS_CONFIRMATION_URL).content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("caseDetails.data.solsPaymentMethods"))
                .andExpect(jsonPath("$.fieldErrors[0].code").value("NotNull"))
                .andExpect(jsonPath("$.fieldErrors[0].message")
                        .value("Payment method cannot be empty. It must be one of fee account or cheque"));
    }

    @Test
    public void shouldConfirmNextStepsWithNullApplicationFeeError() throws Exception {
        caseDataBuilder.applicationFee(null);

        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, "", ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(NEXTSTEPS_CONFIRMATION_URL).content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("caseDetails.data.applicationFee"))
                .andExpect(jsonPath("$.fieldErrors[0].code").value("NotNull"))
                .andExpect(jsonPath("$.fieldErrors[0].message")
                        .value("Application Fee cannot be null"));
    }

    @Test
    public void shouldConfirmNextStepsWithNullTotalFeeError() throws Exception {
        caseDataBuilder.totalFee(null);

        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, "", ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(NEXTSTEPS_CONFIRMATION_URL).content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("caseDetails.data.totalFee"))
                .andExpect(jsonPath("$.fieldErrors[0].code").value("NotNull"))
                .andExpect(jsonPath("$.fieldErrors[0].message")
                        .value("Fee payment amount cannot be null"));
    }

    @Test
    public void shouldConfirmNextStepsWithNullUKFeeError() throws Exception {
        caseDataBuilder.feeForUkCopies(null);

        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, "", ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(NEXTSTEPS_CONFIRMATION_URL).content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("caseDetails.data.feeForUkCopies"))
                .andExpect(jsonPath("$.fieldErrors[0].code").value("NotNull"))
                .andExpect(jsonPath("$.fieldErrors[0].message")
                        .value("Fee for UK Copies cannot be null"));
    }

    @Test
    public void shouldConfirmNextStepsWithNullNonUKFeeError() throws Exception {
        caseDataBuilder.feeForNonUkCopies(null);

        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, "", ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(NEXTSTEPS_CONFIRMATION_URL).content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("caseDetails.data.feeForNonUkCopies"))
                .andExpect(jsonPath("$.fieldErrors[0].code").value("NotNull"))
                .andExpect(jsonPath("$.fieldErrors[0].message")
                        .value("Fee for non UK Copies cannot be null"));
    }

    @Test
    public void shouldProduceEmptyConfirmNextStepsWithNoErrorsForReviewStateChange() throws Exception {
        caseDataBuilder.solsSOTNeedToUpdate("Yes");
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, "", ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(NEXTSTEPS_CONFIRMATION_URL).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"confirmation_header\":null,\"confirmation_body\":null}"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
    }
}
