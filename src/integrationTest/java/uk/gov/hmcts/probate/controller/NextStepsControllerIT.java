package uk.gov.hmcts.probate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData.CaseDataBuilder;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.fee.FeesResponse;
import uk.gov.hmcts.probate.service.fee.FeeService;
import uk.gov.hmcts.probate.service.payments.PaymentsService;
import uk.gov.hmcts.probate.transformer.ServiceRequestTransformer;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.probate.controller.CaseDataTestBuilder.ID;
import static uk.gov.hmcts.probate.controller.CaseDataTestBuilder.LAST_MODIFIED;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class NextStepsControllerIT {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String NEXTSTEPS_CONFIRMATION_URL = "/nextsteps/confirmation";
    private static final String NEXTSTEPS_VALIDATE_URL = "/nextsteps/validate";
    private static final String APPLICATION_GROUNDS = "Application grounds";

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mockMvc;

    private CaseDataBuilder  caseDataBuilder = CaseDataTestBuilder.withDefaults();

    @MockitoBean
    FeeService feeService;
    @MockitoBean
    FeesResponse feesResponseMock;
    @MockitoBean
    private PaymentsService paymentsService;
    @MockitoBean
    private ServiceRequestTransformer serviceRequestTransformer;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        when(feeService.getAllFeesData(any(), any(), any())).thenReturn(feesResponseMock);
    }

    @Test
    void shouldConfirmNextStepsWithNoErrorsWithPaymentMethodNull() throws Exception {
        caseDataBuilder.applicationType(ApplicationType.SOLICITOR)
            .paperPaymentMethod(null)
            .build();
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);
        when(feesResponseMock.getTotalAmount()).thenReturn(BigDecimal.valueOf(0));

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(NEXTSTEPS_CONFIRMATION_URL).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldConfirmNextStepsWithNoErrors() throws Exception {
        caseDataBuilder.applicationType(ApplicationType.SOLICITOR).build();
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);
        when(feesResponseMock.getTotalAmount()).thenReturn(BigDecimal.valueOf(100000));

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(NEXTSTEPS_CONFIRMATION_URL).content(json).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldConfirmNextStepsWithSolicitorFirmIsNullError() throws Exception {
        caseDataBuilder.solsSolicitorFirmName(null);
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(NEXTSTEPS_CONFIRMATION_URL).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("caseDetails.data.solsSolicitorFirmName"))
                .andExpect(jsonPath("$.fieldErrors[0].code").value("NotBlank"))
                .andExpect(jsonPath("$.fieldErrors[0].message").value("Solicitor firm name "
                        + "cannot be empty"));
    }

    @Test
    void shouldConfirmNextStepsWithSolsSolicitorFirmPostcodeIsNullError() throws Exception {
        caseDataBuilder
            .solsSolicitorAddress(SolsAddress.builder().addressLine1(CaseDataTestBuilder.SOLICITOR_FIRM_LINE1).build());
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(NEXTSTEPS_CONFIRMATION_URL).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("caseDetails.data.solsSolicitorAddress.postCode"))
                .andExpect(jsonPath("$.fieldErrors[0].code").value("NotNull"))
                .andExpect(jsonPath("$.fieldErrors[0].message").value("The deceased postcode "
                        + "cannot be empty"));
    }

    @Test
    void shouldConfirmNextStepsWithSolsSOTForenamesIsNullError() throws Exception {
        caseDataBuilder.solsSOTForenames(null);
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(NEXTSTEPS_CONFIRMATION_URL).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("caseDetails.data.solsSOTForenames"))
                .andExpect(jsonPath("$.fieldErrors[0].code").value("NotBlank"))
                .andExpect(jsonPath("$.fieldErrors[0].message").value("Solicitor SOT forenames "
                        + "cannot be empty"));
    }

    @Test
    void shouldConfirmNextStepsWithSolsSOTSurnameIsNullError() throws Exception {
        caseDataBuilder.solsSOTSurname(null);
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(NEXTSTEPS_CONFIRMATION_URL).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("caseDetails.data.solsSOTSurname"))
                .andExpect(jsonPath("$.fieldErrors[0].code").value("NotBlank"))
                .andExpect(jsonPath("$.fieldErrors[0].message").value("Solicitor SOT surname "
                        + "cannot be empty"));
    }


    @Test
    void shouldConfirmNextStepsWithNullApplicationFeeError() throws Exception {
        caseDataBuilder.applicationFee(null);

        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(NEXTSTEPS_CONFIRMATION_URL).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("caseDetails.data.applicationFee"))
                .andExpect(jsonPath("$.fieldErrors[0].code").value("NotNull"))
                .andExpect(jsonPath("$.fieldErrors[0].message")
                        .value("Application Fee cannot be null"));
    }

    @Test
    void shouldConfirmNextStepsWithNullTotalFeeError() throws Exception {
        caseDataBuilder.totalFee(null);

        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(NEXTSTEPS_CONFIRMATION_URL).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("caseDetails.data.totalFee"))
                .andExpect(jsonPath("$.fieldErrors[0].code").value("NotNull"))
                .andExpect(jsonPath("$.fieldErrors[0].message")
                        .value("Fee payment amount cannot be null"));
    }

    @Test
    void shouldConfirmNextStepsWithNullUKFeeError() throws Exception {
        caseDataBuilder.feeForUkCopies(null);

        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(NEXTSTEPS_CONFIRMATION_URL).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("caseDetails.data.feeForUkCopies"))
                .andExpect(jsonPath("$.fieldErrors[0].code").value("NotNull"))
                .andExpect(jsonPath("$.fieldErrors[0].message")
                        .value("Fee for UK Copies cannot be null"));
    }

    @Test
    void shouldConfirmNextStepsWithNullNonUKFeeError() throws Exception {
        caseDataBuilder.feeForNonUkCopies(null);

        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(NEXTSTEPS_CONFIRMATION_URL).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("caseDetails.data.feeForNonUkCopies"))
                .andExpect(jsonPath("$.fieldErrors[0].code").value("NotNull"))
                .andExpect(jsonPath("$.fieldErrors[0].message")
                        .value("Fee for non UK Copies cannot be null"));
    }

    @Test
    void shouldProduceEmptyConfirmNextStepsWithNoErrorsForReviewStateChange() throws Exception {
        caseDataBuilder.solsSOTNeedToUpdate("Yes");
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(NEXTSTEPS_CONFIRMATION_URL).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"confirmation_header\":null,\"confirmation_body\":null}"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
